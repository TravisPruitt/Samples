/**
 *  @file   BiometricReader.cpp
 *  @date   August 1, 2011
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  This class contains the high level interface to the the Lumidigm Biometric Reader.
 */


#include "BiometricReader.h"
#include "V100_shared_types.h"
#include "ticks.h"
#include "FileSystem.h"
#include "EventLogger.h"
#include "DapReader.h"
#include "DapConfig.h"
#include "RFIDReader.h"
#include "LightEffects.h"
#include "log.h"
#include "base64.h"
#include "Sound.h"

#include <string.h>
#include <stdlib.h>
#include <memory>


#define DIAGNOSTICS_UPDATE_INTERVAL  (1 * 1000)
#define DIAGNOSTICS_EVENT_INTERVAL   (5 * 60 * 1000)

#define DEFAULT_SCAN_TIMEOUT  8
#define TIMEOUT_LIMIT         3
#define RETRY_LIMIT           3


using namespace Reader;
using namespace RFID;


BiometricReader::BiometricReader() : 
    _inTestMode(false),
    _enrollRequested(false), 
    _matchRequested(false), 
    _cancelRequested(false), 
    _fwUpgradeRequested(false),
    _imageLength(0)
{
    _lastDiagnosticsEvent = getMilliseconds();
    _inTestMode = DapConfig::instance()->getValue("xbio image capture", false);
    _haveXbio = DapConfig::instance()->getValue("xbio", true);
}


BiometricReader::~BiometricReader()
{
}


BiometricReader* BiometricReader::instance()
{
    static BiometricReader _instance;
    return &_instance;
}


bool BiometricReader::isPresent()
{
    return _haveXbio && _device.isOpened();
}


void BiometricReader::setTestMode(bool which)
{
    _inTestMode = which;
}


unsigned short BiometricReader::getSerialNumber()
{
    return _deviceConfig.Device_Serial_Number;
}


unsigned short BiometricReader::getFirmwareVersion()
{
    return _deviceConfig.Firmware_Rev;
}


unsigned short BiometricReader::getLightRingFWVersion()
{
    return _deviceDiagnostics.lightring_FW_ID;
}


unsigned short BiometricReader::getHardwareVersion()
{
    return _deviceConfig.Hardware_Rev;
}


void BiometricReader::getImage(const uint8_t** dataPtr, unsigned int* length)
{
    *dataPtr = _imageBuffer;
    *length = _imageLength;
}


/**
    Is the biometric reader idle?
*/
bool BiometricReader::isIdle()
{
    return (_xBioState == XBIO_IDLE);
}


void BiometricReader::startEnroll()
{
    Lock lock(_mutex);

    if (!_device.isOpened())
        return;

//    LightEffects::instance()->show("entry_start_scan", NO_EFFECT_TIMEOUT);
    _enrollRequested = true;
    _imageLength = 0;
    _tries = 0;
    _timeouts = 0;
    _event.signal();
}


void BiometricReader::startMatch(std::vector<std::vector<uint8_t> >* templates)
{
    Lock lock(_mutex);

    if (!_device.isOpened())
        return;

    if (_xBioState != XBIO_IDLE && templates)
    {
        LOG_WARN("xBIO is busy, cannot start new match\n");
        return;
    }

    if (templates)
    {
        _templates = *templates;
        _haveTemplates = true;
    }
    else
    {
        _haveTemplates = false;
    }
    _matchRequested = true;
    _imageLength = 0;
    _tries = 0;
    _timeouts = 0;
    _event.signal();
}


void BiometricReader::cancelRead()
{
    Lock lock(_mutex);

    if (!_device.isOpened())
        return;

    if (_xBioState == XBIO_IDLE)
    {
        lightOff();
    }
    else
    {
        _enrollRequested = _matchRequested = false;
        _cancelRequested = true;
        _event.signal();
    }
}


void BiometricReader::startFirmwareUpdate(const uint8_t* image, size_t length)
{
    Lock lock(_mutex);

    if (_fwUpgradeRequested)
    {
        LOG_WARN("xBIO firmware upgrade already in progress");
        return;
    }

    _firmwareImage.resize(length);
    memcpy(&_firmwareImage.front(), image, length);
    _fwUpgradeRequested = true;
    _event.signal();
}


void BiometricReader::updateScriptSet()
{
    if (!_device.isOpened())
        return;

    // First look for script file in data directory
    std::string path = FileSystem::getMediaPath();
    path += "/xbio/scripts.xml";
    if (FileSystem::fileExists(path.c_str()))
    {
        doUpdateScriptSet(path.c_str());
    }
    else
    {
        // Next fall back to default location
        path = FileSystem::getAppPath();
        path += "/xbio/scripts.xml";
        if (FileSystem::fileExists(path.c_str()))
        {
            doUpdateScriptSet(path.c_str());
        }
        else
            LOG_DEBUG("No xbio script set file found");
    }
}


unsigned BiometricReader::readScriptSet(char* buffer)
{
    size_t length = 0;
    _device.readScriptSet(buffer, &length);
    return length;
}


bool BiometricReader::writeScriptSet(char* buffer, unsigned length)
{
    return _device.writeScriptSet(buffer, length);
}


void BiometricReader::restart()
{
    _mutex.lock();
    if (_device.isOpened())
        _restart();
    _mutex.unlock();
}


void BiometricReader::setLED(LRING_Mode_Type value)
{
    if (!_device.setLed((_V100_LED_CONTROL)value))
        setStatus(IStatus::Red, "xBIO command failed");
}



void BiometricReader::lightGreen()
{
    setLED(LRING_GREEN);
}


void BiometricReader::lightBlue()
{
    setLED(LRING_BLUE);
}


void BiometricReader::lightWhite()
{
    setLED(LRING_WHITE);
}


void BiometricReader::lightOff()
{
    playScript("");
}


void BiometricReader::setLightGain(int gain)
{
    uchar g = (uchar)gain;
    if (!_device.setLightGain(&g))
    {
        setStatus(IStatus::Red, "xBIO command failed");
    }
}


bool BiometricReader::playScript(const char *name, LED_BRIGHTNESS brightness)
{
    LOG_DEBUG("xbio play script '%s'", name);
    return _device.playScript(name, brightness);
}


void BiometricReader::run()
{
    _xBioState = XBIO_IDLE;

    // If xBio is disabled in config or we can't open the reader then
    // just exit the thread.
    if (!_haveXbio || !initReader())
        return;

    // Turn off xbio lights just in case
    lightOff();

    // Update the firmware if necessary
    checkForFirmwareUpdate();
    if (_fwUpgradeRequested)
    {
        Lock lock(_mutex);
        doFirmwareUpgrade();
    }

    // update the xbio script set if necessary
    updateScriptSet();

    while (!_quit)
    {
        doDiagnostics();

        _mutex.lock();

        switch (_xBioState)
        {
        case XBIO_IDLE:
            doIdleProcessing();
            break;

        case XBIO_ENROLL_SCAN:
        case XBIO_MATCH_SCAN:
            doScanProcessing();
            break;

        case XBIO_RETRY_DELAY:
            if ( (getMilliseconds() - _retryDelayStart) >= _retryDelay)
                startCapture(_xBioState == XBIO_ENROLL_SCAN);
            break;

        default:
            break;
        }

        _mutex.unlock();

        _event.wait(100);
    }

    lightOff();

    _device.close();
}



/**
 *  Initialize the reader
*/
bool BiometricReader::initReader()
{
    if (_device.isOpened())
        return true;

    // Open the device
    if (!_device.open())
    {
        LOG_INFO("No xBio detected");
        return false;
    }

    // Get device configuration info
    if (!_device.getConfigurationInfo(&_deviceConfig))
    {
        setStatus(IStatus::Red, "xBIO initialization failed");
        _device.close();
		return false;
    }

    _device.getDiagnosticsInfo(&_deviceDiagnostics);

	// Get Command Structure
	_V100_INTERFACE_COMMAND_TYPE cmd;
	if (!_device.getCmd(&cmd))
	{
        setStatus(IStatus::Red, "xBIO initialization failed");
        _device.close();
		return false;
	}

	// Set preprocessing, extraction, matching
	// Turn on all on board processing
	cmd.Select_PreProc    = 1;
	cmd.Select_Matcher    = 1;
	cmd.Select_Extractor  = 1;
	cmd.Override_Trigger  = 0;

    // Set the scan timeout value (0 for no timeout)
    cmd.TimeOut = DapConfig::instance()->getValue("xbio scan timeout", DEFAULT_SCAN_TIMEOUT);

	// Set the Command structure with all on board processing turned on
    if (!_device.setCmd(&cmd))
	{
        setStatus(IStatus::Red, "xBIO initialization failed");
        _device.close();
		return false;
	}

	return true;
}


/**
    Restart and re-initialize the reader

    This code does NOT lock the mutex and should be called when no one else is allowed
    to be talking to the xbio.
*/
void BiometricReader::_restart()
{
	_device.reset();
    _device.close();

	// Give it a few seconds to reboot
	sleepMilliseconds(2000);

    // Reopen Device handle
    initReader();
}


void BiometricReader::doIdleProcessing()
{
    // If already idle, clear cancel request so we don't cancel a future request
    _cancelRequested = false;

    if (_enrollRequested || _matchRequested)
    {
        startCapture(_enrollRequested);
        _enrollRequested = false;
        _matchRequested = false;
        _bioEvent.clear();
    }
    else if (_fwUpgradeRequested)
        doFirmwareUpgrade();
}


void BiometricReader::doScanProcessing()
{
    if (_cancelRequested)
    {
        LOG_DEBUG("cancelling scan\n");
        _cancelRequested = false;
        
        int tries = 0;
        do
        {
            _device.cancelOperation();
        }
        while (_device.getLastCode() == GEN_ERROR_APP_BUSY && ++tries < 10);
        
        _xBioState = XBIO_IDLE;
    }
    else
    {
        checkCapture();
    }
}


/*
 *  Checks the xBio diagnostics
 *
 *  This function will check the diagnostics on regular intervals to verify
 *  that the device is connected, recover a broken connection, an to send
 *  regular diagnotic events.
 */
void BiometricReader::doDiagnostics()
{
    static MILLISECONDS lastUpdate = 0;

    MILLISECONDS currentTime = getMilliseconds();

    // Check the diagnostics once per second
    if ((currentTime - lastUpdate) < DIAGNOSTICS_UPDATE_INTERVAL)
        return;

    lastUpdate = currentTime;

    if (!_device.getDiagnosticsInfo(&_deviceDiagnostics))
    {
        setStatus(IStatus::Red, "xBIO command failed");

        // Try to recover connection
        LOG_WARN("Unable to communicate with xBio, trying to recover");
        _device.close();
        sleepMilliseconds(100);
        _device.open();
        return;
    }

    if ((currentTime - _lastDiagnosticsEvent) > DIAGNOSTICS_EVENT_INTERVAL)
    {
        _lastDiagnosticsEvent = getMilliseconds();
    
        _bioEvent.clear();
        _bioEvent["temperature_platen"] = _deviceDiagnostics.temperature_platen_local;
        _bioEvent["temperature_light_ring"] = _deviceDiagnostics.temperature_light_ring_local;
        _bioEvent["ambient_light_1"] = _deviceDiagnostics.ambient_light_1;
        _bioEvent["ambient_light_2"] = _deviceDiagnostics.ambient_light_2;
        _bioEvent["type"] = "xbio-diagnostics";
        EventLogger::instance()->postEvent(_bioEvent);
        clearStatus();
    }
}


void BiometricReader::startCapture(bool enroll)
{
    LOG_DEBUG("xbio start capture");

    // Get Command Structure
    _V100_INTERFACE_COMMAND_TYPE cmd;
    if (!_device.getCmd(&cmd))
    {
        setStatus(IStatus::Red, "xBIO command failed");
    }
    else
    {
        cmd.PAD_0 = enroll ? 1 : 0;

        // Set the Command structure with all on board processing turned on
        if (!_device.setCmd(&cmd))
            setStatus(IStatus::Red, "xBIO command failed");
    }

    if (!_device.armTrigger())
    {
        setStatus(IStatus::Red, "xBIO command failed");
        _xBioState = XBIO_IDLE;
    }
    else if (enroll)
        _xBioState = XBIO_ENROLL_SCAN;
    else
        _xBioState = XBIO_MATCH_SCAN;
}


void BiometricReader::checkCapture()
{
    _V100_ACQ_STATUS_TYPE AcqStatus;
    if (!_device.getAcqStatus(&AcqStatus))
    {
        setStatus(IStatus::Red, "xBIO command failed");
        return;
    }

    // The xbio reader will always go through these states in the order shown
    // ACQ_BUSY, ACQ_FINGER_PRESENT, ACQ_PROCESSING, ACQ_DONE
    // with the exception of ACQ_TIMEOUT which may show up at any time
    switch (AcqStatus)
    {
    case ACQ_FINGER_PRESENT:
        processCaptureFingerPresent();
        break;

    case ACQ_TIMEOUT:
        processCaptureTimeout();
        break;

    case ACQ_DONE:
        processCaptureDone();
        break;

    default:
        break;
    }
}


void BiometricReader::processCaptureFingerPresent()
{
    LOG_TRAFFIC("Capture: ACQ_FINGER_PRESENT");
        
    if (!_bioEvent.isMember("finger down"))
    {
        _bioEvent["finger down"] = DapReader::instance()->getTimeAsString();

        // Only play the xBio thinking sequence on first finger down event
        LightEffects::instance()->show("entry_xbio_thinking");
    }
}


void BiometricReader::processCaptureTimeout()
{
    LOG_TRAFFIC("Capture: ACQ_TIMEOUT");
        
    postScanError("xBIO timeout");

    if (++_timeouts < TIMEOUT_LIMIT)
    {
        LightEffects::instance()->show("entry_timeout", NO_EFFECT_TIMEOUT);
        _retryDelayStart = getMilliseconds();
        _retryDelay = 600;
        _xBioState = XBIO_RETRY_DELAY;
    }
    else
    {
        lightOff();
            
        // post event - without template
        _bioEvent.clear();
        _bioEvent["type"] = _xBioState == XBIO_ENROLL_SCAN ? "bio-enroll" : "bio-match";
        _bioEvent["uid"] = RFIDReader::instance()->getLastUID();
        EventLogger::instance()->postEvent(_bioEvent);
        _xBioState = XBIO_IDLE;
    }
}


void BiometricReader::processCaptureDone()
{
    bool readerError = false;

    LOG_TRAFFIC("Capture: ACQ_DONE");

    _V500_TRANSACTION_DATA transData;
    bool transSuccess = _device.getTransactionData(&transData);
    if (!transSuccess)
    {
        setStatus(IStatus::Red, "xBIO command failed");
        readerError = true;
    }

    if (transSuccess && (transData.lift_off || transData.movement))
    {
        // Post a scan error event
        postScanError(transData.lift_off ? "lift off" : "movement");

        // Retry
        if (++_tries < RETRY_LIMIT)
        {
            LightEffects::instance()->show("entry_retry", NO_EFFECT_TIMEOUT);

            // Reset the timeout counter
            _timeouts = 0;

            _retryDelayStart = getMilliseconds();
            _retryDelay = 1400;
            _xBioState = XBIO_RETRY_DELAY;
        }
        else
        {
            lightOff();

            // post event - without template
            _bioEvent.clear();
            _bioEvent["type"] = _xBioState == XBIO_ENROLL_SCAN ? "bio-enroll" : "bio-match";
            _bioEvent["uid"] = RFIDReader::instance()->getLastUID();
            EventLogger::instance()->postEvent(_bioEvent);

            _xBioState = XBIO_IDLE;
        }
    }
    else
    {
        // Get the template
        uint templateLength;
        if (!_device.getTemplate(_templatesBuffer, &templateLength))
        {
            setStatus(IStatus::Red, "xBIO command failed");
            templateLength = 0;
            readerError = true;
        }

        // If in test mode, get image stack
        if (_inTestMode)
        {
            if (!_device.getImageStack(_imageBuffer, _imageLength))
            {
                setStatus(IStatus::Red, "xBIO command failed");
                _imageLength = 0;
                readerError = true;
            }
        }

        // post event
        _bioEvent["xbio-template"] = base64_encode(_templatesBuffer, templateLength);
        _bioEvent["type"] = _xBioState == XBIO_ENROLL_SCAN ? "bio-enroll" : "bio-match";
        _bioEvent["uid"] = RFIDReader::instance()->getLastUID();
        EventLogger::instance()->postEvent(_bioEvent);

        _xBioState = XBIO_IDLE;

        if (!readerError)
            clearStatus();
    }
}


void BiometricReader::postScanError(const char* reason)
{
    _bioEvent.clear();
    _bioEvent["type"] = "bio-scan-error";
    _bioEvent["uid"] = RFIDReader::instance()->getLastUID();
    _bioEvent["reason"] = reason;
    EventLogger::instance()->postEvent(_bioEvent);
}


/**
 *  Determines the firmware version from it's filename.
 *
 *  File Format: /path/xbio-<version>.bin
 *
 *  @return  A 16-bit version number or zero if it fails
 *           to parse it.
 */
uint16_t BiometricReader::getFirmwareFileVersion(std::string filename)
{
    // Remove directory & prefix
    size_t pos = filename.find("xbio-");
    if (pos != std::string::npos)
        filename.erase(0, pos+5);

    // remove extension
    pos = filename.rfind(".bin");
    if (pos != std::string::npos)
        filename.erase(pos);

    // Convert and validate version number
    int version = atoi(filename.c_str());
    if (version > USHRT_MAX)
        version = 0;

    return static_cast<uint16_t>(version);
}


/**
 *  Checks for a xbio firmaware file and initiates an update
 *  if it detects a new version.
 *
 *  Note: This routine only checks to see if the version
 *  differs from the current version and can downgrade
 *  firmware as well as upgrade it.
 */
void BiometricReader::checkForFirmwareUpdate()
{
    LOG_INFO("Checking for xbio update");

    std::string filename;
    if (FileSystem::findFirmwareFile("xbio-", filename))
    {
        uint16_t version = getFirmwareFileVersion(filename);
        uint16_t current_version = getFirmwareVersion();

        // Accept any valid firmware version different from the current
        if ( version && (version != current_version) )
        {
            LOG_INFO("Found new xbio firmware update '%s' (%hd -> %hd)",
                     filename.c_str(), current_version, version);

            FILE* file = fopen(filename.c_str(), "rb");
            if (!file)
            {
                LOG_ERROR("Unable to load new xbio firmware file");
                return;
            }

            // Get file length
            fseek(file, 0, SEEK_END);
            int length = ftell(file);
            rewind(file);

            // Copy file contents to memory
            _firmwareImage.resize(length);
            fread(&_firmwareImage.front(), 1, length, file);
            fclose(file);

            _fwUpgradeRequested = true;
        }
    }
}


void BiometricReader::doFirmwareUpgrade()
{
    std::string status = "success";

    if (_firmwareImage.size() <= 0)
    {
        LOG_WARN("xBIO empty firmware image");
        status = "failed: empty firmware image";
        goto post_event;
    }

    if (!_device.updateFirmware(&_firmwareImage[0], _firmwareImage.size()))
    {
        setStatus(IStatus::Yellow, "xBIO failed to upgrade");
        status = "failed: ";
        status += _device.getLastReturnCodeAsString();
        goto post_event;
    }	

    // Restart and re-init the reader
    _restart(); 

post_event:
    Json::Value event;
    event["type"] = "xbio-fw-upgrade";
    event["status"] = status;
    event["verstion"] = this->getFirmwareVersion();
    EventLogger::instance()->postEvent(event);

    _fwUpgradeRequested = false;
}


void BiometricReader::doUpdateScriptSet(const char* filename)
{
    LOG_DEBUG("comparing script set '%s'", filename);

    // Make sure file is a valid size (>0 and <=1MB)
    unsigned fileSize = FileSystem::fileSize(filename);
    if (fileSize == 0 && fileSize > (1024*1024))
    {
        LOG_WARN("xbio script set '%s' is either empty or too large (>1MB)", filename);
        return;
    }

    // Read script into memory
    std::auto_ptr<char> buf(new char[1024*1024]);
    FILE* fp = fopen(filename, "rb");
    fread(buf.get(), 1, fileSize, fp);
    fclose(fp);

    // Get the current on-device script into memory
    std::auto_ptr<char> buf2(new char[1024*1024]);
    unsigned scriptSize;
    if (!_device.readScriptSet(buf2.get(), &scriptSize) || scriptSize == 0)
        return;

    // Skip write if they are the same
    if ( (fileSize == scriptSize) &&
         (memcmp(buf.get(), buf2.get(), fileSize) == 0) )
    {
        LOG_DEBUG("script sets match");
        return;
    }

    LOG_DEBUG("xbio script set change detected: writing to xbio");
    if (!_device.writeScriptSet(buf.get(), fileSize))
    {
        setStatus(IStatus::Red, "xBIO command failed");
        return;
    }

    // Must restart the xbio before it will parse and use the new scripts
    _restart();
}
