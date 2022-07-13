/**
    ReaderInfo.cpp
    Greg Strange
    August 2012

    Copyright (c) 2012, synapse.com
*/



#include "standard.h"
#include "ReaderInfo.h"
#include "version.h"
#include "EventLogger.h"
#include "Mutex.h"
#include "config.h"
#include "Radios.h"
#include "webServ.h"
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



using namespace Reader;


ReaderInfo::ReaderInfo()
{
    initMacAddress();
}

ReaderInfo::~ReaderInfo()
{
}


ReaderInfo* ReaderInfo::instance()
{
    static ReaderInfo _instance;
    return &_instance;
}


std::string ReaderInfo::getName()
{
    return Config::instance()->getValue("name", "");
}


const char* ReaderInfo::getMacAddress()
{
    return _macAddress.c_str();
}


void ReaderInfo::initMacAddress()
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


const char* ReaderInfo::getLinuxVersion()
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


std::string ReaderInfo::getBoardType()
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


void ReaderInfo::getInfo(Json::Value &json, bool detailed)
{
    json["mac"] = getMacAddress();
    json["reader name"] = Config::instance()->getValue("name", "");
    json["reader type"] = READER_TYPE;
    json["reader version"] = FULL_VERSION;
    json["linux version"] = getLinuxVersion();
    json["radio driver version"] = Radios::getDriverVersion().c_str();
    json["port"] = webServ::getListeningPort();
    json["next eno"] = EventLogger::instance()->getNextEventNumber();
    json["HW type"] = getBoardType();
    Json::Value events;
    events["capable"] = EventLogger::instance()->getEventQueueSize();
    events["stored"]  = EventLogger::instance()->getNumStored();
    events["queued (push)"] = EventLogger::instance()->getNumQueuedForPush();
    events["queued (get)"]  = EventLogger::instance()->getNumQueuedForGet();
    json["events"] = events;
    json["update stream"] = EventLogger::instance()->getPushUrl().c_str();
    json["time"] = getTimeAsString().c_str();

    if(detailed)
    {	
        // get radios' version info
        unsigned numRadios = Radios::totalNumberOfRadios();
        for (unsigned i = 0; i < numRadios; ++i)
        {
            char name[100];
            sprintf(name, "Radio%d version", i);
            json[name] = Radios::radio(i).getVersion();

            sprintf(name, "Radio%d calibration", i);
            json[name] = Radios::radio(i).getOffset();
        }

        // hello server / stream server address
        json["xbrc_url"] = Config::instance()->getXbrcUrl();
    }
}


std::string ReaderInfo::getTimeAsString()
{
    static char buf[200];
#ifndef WIN32
    struct timespec ts;
    struct tm tm;

    clock_gettime(CLOCK_REALTIME, &ts);
    localtime_r(&ts.tv_sec, &tm);
    int length = strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S", &tm);
    sprintf(buf+length, ".%03li", ts.tv_nsec / (1000 * 1000));
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
void ReaderInfo::setStatus(IStatus::Status newState, const char* msg)
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



// error and warning messages will automatically clear after MSG_TIMEOUT duration
// AND they have been seen MSG_MIN_SEE times.
#define MSG_MIN_SEE     2
#define MSG_TIMEOUT     10000

/**
    Pull the diagnostics information on the reader
*/
void ReaderInfo::getDiagnostics(Json::Value& json)
{
    LOG_DEBUG("ReaderInfo::getDiagnostics");

    json["type"] = "xbr-diagnostics";

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
    
    // Get the individual radio statuses
    unsigned numRadios = Radios::totalNumberOfRadios();
    for (unsigned i = 0; i < numRadios; ++i)
    {
        char name[100];
        std::string radioMsg;

        IStatus::Status radioStatus = Radios::radio(i).getStatus(radioMsg);

        sprintf(name, "Radio%d status", i);
        json[name] = IStatus::statusToString(radioStatus);

        sprintf(name, "Radio%d status msg", i);
        json[name] = radioMsg;

        // If one of the radio statuses is higher alert than our general status,
        // then replace the general status with the radio status.
        if (radioStatus > status)
        {
            status = radioStatus;
            statusMsg = radioMsg;
        }
    }

    // set over all status
    json["status"] = IStatus::statusToString(status);
    json["status msg"] = statusMsg;
}

