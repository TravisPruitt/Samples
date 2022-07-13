/**
    DapReader.cpp
    Greg Strange
    Sept 13, 2011

    Copyright (c) 2011, synapse.com
*/



#include "standard.h"
#include "DapReader.h"
#include "DapConfig.h"
#include "version.h"
#include "WebServer.h"
#include "EventLogger.h"
#include "BiometricReader.h"
#include "RFIDReader.h"
#include "Sensors.h"
#include "Mutex.h"
#include "log.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <string>
#include <sstream>

#ifndef _WIN32
//#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>
#endif


#define DEFAULT_STATS_PERIOD (3600)


using namespace Reader;
using namespace RFID;


DapReader::DapReader()
{
    initMacAddress();
    resetTapStats();
}

DapReader::~DapReader()
{
}


DapReader* DapReader::instance()
{
    static DapReader _instance;
    return &_instance;
}


std::string DapReader::getName()
{
    return DapConfig::instance()->getValue("name", "");
}


const char* DapReader::getMacAddress()
{
    return _macAddress.c_str();
}


void DapReader::initMacAddress()
{
#ifndef _WIN32
    int s;
	struct ifreq buffer;
	s = socket(PF_INET, SOCK_DGRAM, 0);

	memset(&buffer, 0x00, sizeof(buffer));
	strcpy(buffer.ifr_name, "eth0");

	ioctl(s, SIOCGIFHWADDR, &buffer);
	close(s);

	static char output[20];
	char *p = output;
	for( s = 0; s < 6; s++ )
	{
		p += sprintf(p, "%.2X:", (unsigned char)buffer.ifr_hwaddr.sa_data[s]);
	}
	*(--p) = '\0'; // erase the last ':'.
	_macAddress = output;
#else
    _macAddress = "C8:2A:14:04:C2:F8";
#endif
}


void DapReader::resetTapStats()
{
    _tapStatCount = 0;
    _minTapStatTime = 0;
    _maxTapStatTime = 0;
    _totalTapStatTime = 0;
    _tapStatStartTime = getMilliseconds();
}


void DapReader::reportTapStats(Json::Value& json)
{
    Lock lock(_statsMutex);

    MILLISECONDS sTime = getMilliseconds() - _tapStatStartTime;
    MILLISECONDS sPeriod = DapConfig::instance()->getValue("stats period", DEFAULT_STATS_PERIOD);

    Json::Value& stats = json["stats"];
    stats["taps"] = _tapStatCount;
    stats["min tap time"] = _minTapStatTime;
    stats["max tap time"] = _maxTapStatTime;
    stats["mean tap time"] = _tapStatCount ? _totalTapStatTime / _tapStatCount : 0;
    stats["period"] = sPeriod;
    stats["time"] = sTime / 1000;
    
    if (sTime >= (sPeriod * 1000))
        resetTapStats();
}


const char* DapReader::getLinuxVersion()
{
#ifndef _WIN32
    FILE *fIn = popen("uname -a", "r");
	if (!fIn) { return ""; }
	static char buffer[256];
	fgets(buffer, sizeof(buffer), fIn);
	pclose(fIn);
	return buffer;
#else
    return "N/A - running Windows";
#endif
}


std::string DapReader::getBoardType()
{
    FILE* fp;

    fp = fopen("/proc/board_type", "r");
    if (fp)
    {
        char buf[200];
        char* result = fgets(buf, sizeof(buf), fp);
        fclose(fp);
        if (result)
            return result;
    }

    return "unknown";
}


void DapReader::getInfo(Json::Value& value)
{
    value["mac"] = getMacAddress();
    value["port"] = WebServer::instance()->getListeningPort();
    value["reader name"] = getName();
    value["reader type"] = BiometricReader::instance()->isPresent() ? "xFP+xBIO" : "xFP";
    value["reader version"] = FULL_VERSION;
    value["linux version"] = getLinuxVersion();
    value["min xbrc version"] = MIN_XBRC_VERSION;
    value["next eno"] = EventLogger::instance()->getNextEventNumber();
    value["HW type"] = getBoardType();
// num events
// oldest event
// push url
    value["xbio serial no"] = BiometricReader::instance()->getSerialNumber();
    value["xbio fw version"] = BiometricReader::instance()->getFirmwareVersion();
    value["xbio hw version"] = BiometricReader::instance()->getHardwareVersion();

    value["RFID fw version"] = RFIDReader::instance()->getFirmwareVersion();
    value["RFID description"] = RFIDReader::instance()->getDescription();
}




std::string DapReader::getTimeAsString()
{
    static char buf[200];
#ifndef WIN32
    struct timespec ts;
    struct tm tm;

    clock_gettime(CLOCK_REALTIME, &ts);
    localtime_r(&ts.tv_sec, &tm);
    int length = strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S", &tm);
    sprintf(buf+length, ".%li", ts.tv_nsec / (1000 * 1000));
#else
    tzset();
    time_t now = time(NULL);
    struct tm tm = *gmtime(&now);
    int length = strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S.000", &tm);
#endif
    return buf;
}



/**
    Error occurred.  Save the message for diagnostics events.
*/
void DapReader::setStatus(IStatus::Status newState, const char* msg)
{
    Lock lock(_errorMutex);

    if (newState == IStatus::Red)
    {
        _errorMsg = msg;
        _errorWhen = getMilliseconds();
        _errorSeenCount = 0;
    }
    else
    {
        _warnMsg = msg;
        _warnWhen = getMilliseconds();
        _warnSeenCount = 0;
    }
}



void DapReader::recordTapReadTime(MILLISECONDS time)
{
    Lock lock(_statsMutex);

    ++_tapStatCount;
    _totalTapStatTime += time;

    if (_minTapStatTime == 0 || time < _minTapStatTime)
        _minTapStatTime = time;
    if (time > _maxTapStatTime)
        _maxTapStatTime = time;
}


// error and warning messages will automatically clear after MSG_TIMEOUT duration
// AND they have been seen MSG_MIN_SEE times.
#define MSG_MIN_SEE     2
#define MSG_TIMEOUT     10000

/**
    Pull the diagnostics information on the reader
*/
void DapReader::getDiagnostics(Json::Value& value)
{
    IStatus::Status status = IStatus::Green;
    std::string statusMsg = "";

    // Get any current error or warning message
    // Status is set to Red if there is an error message to display, or Yellow if a warning message
    _errorMutex.lock();
    if (_errorMsg.size() > 0)
    {
        status = IStatus::Red;
        statusMsg = _errorMsg;
        if (++_errorSeenCount > MSG_MIN_SEE && (getMilliseconds() - _errorWhen) > MSG_TIMEOUT)
        {
            _errorMsg.clear();
        }
    }

    if (_warnMsg.size() > 0)
    {
        // Error messages will hide warning messages which are lower priority
        if (status == IStatus::Green)
        {
            status = IStatus::Yellow;
            statusMsg = _warnMsg;
        }
        // But the warning messages still timeout even if hidden by an error message
        if (++_warnSeenCount > MSG_MIN_SEE && (getMilliseconds() - _warnWhen) > MSG_TIMEOUT)
        {
            _warnMsg.clear();
        }
    }
    _errorMutex.unlock();
    
    value["type"] = "xfp-diagnostics";

    // Get ambient light value
    value["amb"] = Sensors::instance()->getLuminance();

    // Get temperature and change status to red if over temperature
    value["temp"] = Sensors::instance()->getTemperature();
    value["max temp"] = Sensors::instance()->getMaxTemperature();
    if (Sensors::instance()->overTemp())
    {
        status = IStatus::Yellow;
        std::stringstream os;
        os << "temperature above " << Sensors::instance()->getTemperatureLimit() << "C";
        statusMsg = os.str();
    }

    // Get Feig reader status
    std::string msg;
    IStatus::Status deviceStatus = RFIDReader::instance()->getStatus(msg);
    value["RFID status"] = IStatus::statusToString(deviceStatus);
    value["RFID msg"] = msg;
    if (deviceStatus > status)
    {
        status = deviceStatus;
        statusMsg = msg;
    }

    // Biometric reader status
    if (BiometricReader::instance()->isPresent())
    {
        deviceStatus = BiometricReader::instance()->getStatus(msg); 
        value["xbio status"] = IStatus::statusToString(deviceStatus); 
        value["xbio msg"] = msg;
        if (deviceStatus > status) 
        {
            status = deviceStatus; statusMsg = msg;
        }
    } 

    // set over all status
    value["status"] = IStatus::statusToString(status);
    value["status msg"] = statusMsg;

    reportTapStats(value);
}

