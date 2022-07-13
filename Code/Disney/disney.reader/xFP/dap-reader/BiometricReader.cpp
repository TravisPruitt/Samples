/**
    BiometricReader.cpp - Lumidigm Biometric reader
    August 1, 2011
    Greg Strange

    Copyright (c) 2011-2012, synapse.com

    This class encapsolates the Lumidigm Biometric Reader interface.  The bulk of the
    Lumidigm interface code exists in a dynamically linked library.  This just wraps
    the functions we care about from that library to represent a single Lumidigm
    Biometric reader to the rest of the software.
*/


#include "BiometricReader.h"
#include "V100_shared_types.h"
#include "VCOMCore.h"
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
#include <stdarg.h>
#include <stdlib.h>


#define DIAGNOSTICS_INTERVAL    (5 * 60 * 1000)


using namespace Reader;
using namespace RFID;


BiometricReader::BiometricReader() : 
    _isOpen(false), 
    _isBusy(false),
    _inTestMode(false),
    _enrollRequested(false), 
    _matchRequested(false), 
    _cancelRequested(false), 
    _fwUpgradeRequested(false),
    _imageLength(0)
{
    _lastDiagnosticsEvent = getMilliseconds();
    _inTestMode = DapConfig::instance()->getValue("xbio image capture", false);
    _haveXbio = DapConfig::instance()->getValue("xbio", false);
}



BiometricReader::~BiometricReader()
{
}


BiometricReader* BiometricReader::instance()
{
    static BiometricReader _instance;
    return &_instance;
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

unsigned short BiometricReader::getHardwareVersion()
{
    return _deviceConfig.Hardware_Rev;
}

void BiometricReader::getImage(const uint8_t** dataPtr, unsigned int* length)
{
    *dataPtr = _imageBuffer;
    *length = _imageLength;
}


void BiometricReader::startEnroll()
{
    if (!_isOpen)
        return;

    _enrollRequested = true;
    _imageLength = 0;
    _tries = 0;
    _timeouts = 0;
    _threadEvent.signal();
}


void BiometricReader::startMatch(std::vector<std::vector<uint8_t> >* templates)
{
    if (!_isOpen)
        return;

    // TODO - do we want to do something more sophisticated so that we can interrupt a
    // match and start a new one?  E.g. mutex.

    // TODO - do we need to return an error code to xBRC?
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
    _threadEvent.signal();
}

void BiometricReader::cancelRead()
{
    if (!_isOpen)
        return;

    _enrollRequested = _matchRequested = false;
    _cancelRequested = true;
    _threadEvent.signal();
}


void BiometricReader::startFirmwareUpdate(const uint8_t* image, size_t length)
{
    if (_fwUpgradeRequested)
    {
        LOG_WARN("xBIO firmware upgrade already in progress\n");
        return;
    }

    _firmwareImage.resize(length);
    memcpy(&_firmwareImage.front(), image, length);
    _fwUpgradeRequested = true;
    _threadEvent.signal();
}


void BiometricReader::setLED(LRING_Mode_Type value)
{
    if (_isOpen && !_isBusy)
    {
        _readerMutex.lock();

        // firmware upgrade will set the busy flag and then unlock the mutex, so we must check the busy flag
        // again after getting the mutex
        if (_isOpen && !_isBusy)
        {
            V100_ERROR_CODE rc = V100_Set_LED(&_device, (_V100_LED_CONTROL)value);
            if (rc != GEN_OK)
            {
                LOG_ERROR("xBIO V100_Set_LED error %d - %s\n", rc, getReturnCodeAsString(rc));
                setStatus(IStatus::Red, "xBIO command failed");
            }
        }

        _readerMutex.unlock();
    }
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
    setLED(LRING_OFF);
}


void BiometricReader::setLightGain(int gain)
{
    if (_isOpen && !_isBusy)
    {
        _readerMutex.lock();

        // firmware upgrade will set the busy flag and then unlock the mutex, so we must check the busy flag
        // again after getting the mutex
        if (_isOpen && !_isBusy)
        {
            uchar g = (uchar)gain;
            V100_ERROR_CODE rc = V100_Set_Option(&_device, (_V100_OPTION_TYPE)170, &g, 1);
            if (rc != GEN_OK)
            {
                LOG_ERROR("xBIO V100_Set_Option error %d - %s\n", rc, getReturnCodeAsString(rc));
                setStatus(IStatus::Red, "xBIO command failed");
            }
            LOG_TRAFFIC("XBIO: Set_Option(), gain %d\n", g);
        }

        _readerMutex.unlock();
    }
}


void BiometricReader::postScanError(const char* reason)
{
    _event.clear();
    _event["type"] = "bio-scan-error";
    _event["uid"] = RFIDReader::instance()->getLastUID();
    _event["reason"] = reason;
    EventLogger::instance()->postEvent(_event);
}


void BiometricReader::run()
{
    V100_ERROR_CODE rc;

    // If we can't open the reader, then just exit
    if (!initReader())
        return;

    _xBioState = XBIO_IDLE;

    checkForFirmwareUpdate();

    while (!_quit)
    {
        switch (_xBioState)
        {
        case XBIO_IDLE:
            // If already idle, clear cancel request so we don't cancel a future request
            _cancelRequested = false;

            if (_enrollRequested || _matchRequested)
            {
                startCapture(_enrollRequested);
                _enrollRequested = false;
                _matchRequested = false;
                _event.clear();
            }
            else if (_fwUpgradeRequested)
            {
                doFirmwareUpgrade();
            }
            // TODO - need way of checking if xBRC is alive
            else if ( (getMilliseconds() - _lastDiagnosticsEvent) > DIAGNOSTICS_INTERVAL)
            {
                _V500_DIAGNOSTIC_PACKET diagnostics;
                uint length;
                _readerMutex.lock();
                rc = V100_Get_Image(&_device, (_V100_IMAGE_TYPE)5010, (uint8_t*)&diagnostics, length);
                LOG_TRAFFIC("XBIO: Get_Image\n");
                _readerMutex.unlock();
                _lastDiagnosticsEvent = getMilliseconds();
                if (rc == GEN_OK)
                {
                    _event.clear();
                    _event["xbio-data"] = base64_encode((uint8_t*)&diagnostics, 10); 
                    _event["type"] = "xbio-diagnostics";
                    EventLogger::instance()->postEvent(_event);
                    clearStatus();
                }
                else
                {
                    LOG_ERROR("xBIO Error acquiring diagnostic info: %d - %s\n", rc, getReturnCodeAsString(rc));
                    setStatus(IStatus::Red, "xBIO command failed");
                }
            }
            break;

        case XBIO_ENROLL_SCAN:
        case XBIO_MATCH_SCAN:
            if (_cancelRequested)
            {
                LOG_DEBUG("cancelling enroll\n");
                _cancelRequested = false;
                _readerMutex.lock();

                int tries = 0;
                do
                {
                    rc = V100_Cancel_Operation(&_device);
                    LOG_TRAFFIC("XBIO: Cancel_Operation, rc = %d\n");
                }
                while (rc == GEN_ERROR_APP_BUSY && ++tries < 10);

                _readerMutex.unlock();
                _xBioState = XBIO_IDLE;
            }
            else
            {
                checkCapture();
            }
            break;

        case XBIO_RETRY_DELAY:
            if ( (getMilliseconds() - _retryDelayStart) >= _retryDelay)
                startCapture(_xBioState == XBIO_ENROLL_SCAN);
            break;

        default:
            break;
        }

        _threadEvent.wait(100);
    }

    close();
}


bool BiometricReader::initReader()
{
    V100_ERROR_CODE rc;

    if (_isOpen)
        return true;

    // Open the device
    // V100_Open() crashes if there is junk in the uninitialized device structure, so we zero it here
    memset((void*)&_device, 0, sizeof(_device));
    _device.DeviceNumber = 0;
	rc = V100_Open(&_device);
    LOG_TRAFFIC("XBIO: Open\n");
    if (rc != GEN_OK)
    {
        if (_haveXbio)
        {
            LOG_ERROR("Unable to open biometric reader: %d - %s\n", rc, getReturnCodeAsString(rc));
            setStatus(IStatus::Red, "No xBIO detected");
        }
        else
            LOG_INFO("No xBio detected");
        return false;
    }

    // Get device configuration info
    rc = V100_Get_Config(&_device, &_deviceConfig);
    LOG_TRAFFIC("XBIO: Get_Config\n");
    if (rc != GEN_OK)
    {
		LOG_ERROR("xBIO Get_Config() failed: %d - %s\n", rc, getReturnCodeAsString(rc));
        setStatus(IStatus::Red, "xBIO initialization failed");
        V100_Close(&_device);
		return false;
    }


	// Get Command Structure
	_V100_INTERFACE_COMMAND_TYPE cmd;
	rc = V100_Get_Cmd(&_device, &cmd);
    LOG_TRAFFIC("XBIO: Get_Cmd\n");
	if (rc != GEN_OK)
	{
		LOG_ERROR("xBIO Get_Cmd() failed: %d - %s\n", rc, getReturnCodeAsString(rc));
        setStatus(IStatus::Red, "xBIO initialization failed");
        V100_Close(&_device);
		return false;
	}

	// Set preprocessing, extraction, matching
	// Turn on all on board processing
	cmd.Select_PreProc    = 1;
	cmd.Select_Matcher    = 1;
	cmd.Select_Extractor  = 1;
	cmd.Override_Trigger  = 0;

    // Set the scan timeout value (0 for no timeout)
    cmd.TimeOut = DapConfig::instance()->getValue("scan timeout", 8);

	// Set the Command structure with all on board processing turned on
	rc = V100_Set_Cmd(&_device, &cmd);
    LOG_TRAFFIC("XBIO: Set_Cmd\n");
    if (rc != GEN_OK)
	{
		LOG_ERROR("xBIO Set_Cmd() failed: %d - %s\n", rc, getReturnCodeAsString(rc));
        setStatus(IStatus::Red, "xBIO initialization failed");
        V100_Close(&_device);
		return false;
	}


    _isOpen = true;
	return true;
}



// Reboot device if necessary
void BiometricReader::close()
{
    if (_isOpen)
    {
	    V100_Close(&_device);
        LOG_TRAFFIC("XBIO: close\n");
        _isOpen = false;
    }
}


void BiometricReader::startCapture(bool enroll)
{
    _readerMutex.lock();

    // Get Command Structure
    _V100_INTERFACE_COMMAND_TYPE cmd;

    V100_ERROR_CODE rc = V100_Get_Cmd(&_device, &cmd);
    LOG_TRAFFIC("xBIO: Get_Cmd()\n");
    if (rc != GEN_OK)
    {
        LOG_ERROR("xBIO Get_Cmd() failed: %d - %s\n", rc, getReturnCodeAsString(rc));
        setStatus(IStatus::Red, "xBIO command failed");
    }
    else
    {
        cmd.PAD_0 = enroll ? 1 : 0;

        // Set the Command structure with all on board processing turned on
        rc = V100_Set_Cmd(&_device, &cmd);
        LOG_TRAFFIC("XBIO: Set_Cmd\n");
        if (rc != GEN_OK)
        {
            LOG_ERROR("xBIO Set_Cmd() failed: %d - %s\n", rc, getReturnCodeAsString(rc));
            setStatus(IStatus::Red, "xBIO command failed");
        }
    }

    rc = V100_Arm_Trigger(&_device,TRIGGER_ON);
    LOG_TRAFFIC("XBIO: Arm_Trigger\n");
    if (rc != GEN_OK)
    {
        LOG_ERROR("xBIO acquire error %d - %s\n", rc, getReturnCodeAsString(rc));
        setStatus(IStatus::Red, "xBIO command failed");
        _xBioState = XBIO_IDLE;
    }
    else if (enroll)
        _xBioState = XBIO_ENROLL_SCAN;
    else
        _xBioState = XBIO_MATCH_SCAN;

    _readerMutex.unlock();
}


void BiometricReader::checkCapture()
{
    V100_ERROR_CODE rc;
    _V100_ACQ_STATUS_TYPE AcqStatus;

    _readerMutex.lock();
    rc = V100_Get_Acq_Status(&_device, &AcqStatus);
    _readerMutex.unlock();
    if (rc != GEN_OK)
    {
        LOG_ERROR("xBIO acquire error %d - %s\n", rc, getReturnCodeAsString(rc));
        setStatus(IStatus::Red, "xBIO command failed");
    }
    else
    {
        bool readerError = false;

        // The xbio reader will always go through these states in the order shown
        //    ACQ_BUSY, ACQ_FINGER_PRESENT, ACQ_PROCESSING, ACQ_DONE
        // with the exception of ACQ_TIMEOUT which may show up at any time
        switch (AcqStatus)
        {
        case ACQ_FINGER_PRESENT:
            if (!_event.isMember("finger down"))
                _event["finger down"] = DapReader::instance()->getTimeAsString();
            break;

        case ACQ_TIMEOUT:
            // The first timeout causes a retry
            // The second timeout sends an event to the xBRC and returns us to the idle state.
            postScanError("xBIO timeout");
            ++_timeouts;
            ++_tries;
            if (_tries < 3 && _timeouts < 2)
            {
                LightEffects::instance()->show("entry_start_scan", 0);
                _retryDelayStart = getMilliseconds();
                _retryDelay = 600;
               _xBioState = XBIO_RETRY_DELAY;
            }
            else
            {
                lightOff();

                // post event - without template
                _event.clear();
                _event["type"] = _xBioState == XBIO_ENROLL_SCAN ? "bio-enroll" : "bio-match";
                _event["uid"] = RFIDReader::instance()->getLastUID();
                EventLogger::instance()->postEvent(_event);
                _xBioState = XBIO_IDLE;
            }
            break;

        case ACQ_DONE:
            _readerMutex.lock();
            _V500_TRANSACTION_DATA transData;
            uint length;
            rc = V100_Get_Image(&_device, (_V100_IMAGE_TYPE)5011, (uint8_t*)&transData, length);
            LOG_TRAFFIC("XBIO: Get_Image\n");
            _readerMutex.unlock();

            if (rc != GEN_OK)
            {
                LOG_ERROR("xBIO Get Image of transaction data failed: %d - %s\n", rc, getReturnCodeAsString(rc));
                setStatus(IStatus::Red, "xBIO command failed");
                readerError = true;
            }

            if ((rc == GEN_OK) && (transData.lift_off || transData.movement))
            {
                // Post a scan error event
                postScanError(transData.lift_off ? "lift off" : "movement");

                _xBioState = XBIO_IDLE;

                // Retry up to 3 tries
                if (++_tries < 3)
                {
                    LightEffects::instance()->show("entry_retry", 0);
                    _retryDelayStart = getMilliseconds();
                    _retryDelay = 1400;
                    _xBioState = XBIO_RETRY_DELAY;
                }
                else
                {
                    lightOff();

                    // post event - without template
                    _event.clear();
                    _event["type"] = _xBioState == XBIO_ENROLL_SCAN ? "bio-enroll" : "bio-match";
                    _event["uid"] = RFIDReader::instance()->getLastUID();
                    EventLogger::instance()->postEvent(_event);

                    _xBioState = XBIO_IDLE;
                }
            }
            else
            {
                _readerMutex.lock();

                // Get Template
                uint templateLength;
                rc = V100_Get_Template(&_device, _templatesBuffer, &templateLength);
                LOG_TRAFFIC("XBIO: Get_Template\n");
                if (rc != GEN_OK)
                {
                    LOG_ERROR("xBIO get template failed: %d - %s\n", rc, getReturnCodeAsString(rc));
                    setStatus(IStatus::Red, "xBIO command failed");
                    templateLength = 0;
                    readerError = true;
                }

                // If in test mode, get image stack
                if (_inTestMode)
                {
                    rc = V100_Get_Image(&_device, (_V100_IMAGE_TYPE)5000, _imageBuffer, _imageLength);
                    LOG_TRAFFIC("XBIO: Get_Image\n");
                    if (rc != GEN_OK)
                    {
                        LOG_ERROR("xBIO get image failed: %d - %s\n", rc, getReturnCodeAsString(rc));
                        setStatus(IStatus::Red, "xBIO command failed");
                        _imageLength = 0;
                        readerError = true;
                    }
                }
                _readerMutex.unlock();

                // post event
                _event["xbio-template"] = base64_encode(_templatesBuffer, templateLength);
                _event["type"] = _xBioState == XBIO_ENROLL_SCAN ? "bio-enroll" : "bio-match";
                _event["uid"] = RFIDReader::instance()->getLastUID();
                EventLogger::instance()->postEvent(_event);
                Sound::instance()->play("bioprocess.wav");
                _xBioState = XBIO_IDLE;

                if (!readerError)
                    clearStatus();
            }
            break;

        default:
            break;
        }
    }
}


#if 0
bool BiometricReader::capture()
{
    if (!_isOpen)
    {
        LOG_ERROR("xBIO is not open\n");
        return false;
    }

	// Initialize variables	
	_V100_INTERFACE_CONFIGURATION_TYPE config;
	V100_ERROR_CODE rc;
	uchar bioTemplate[2048];
	uint width;
	uint height;
	uint templateSize;
	int nSpoof;

	// Get Config Structure
	rc = V100_Get_Config(&_device, &config);
	if (rc != GEN_OK)
	{
        LOG_ERROR("xBIO V100_Get_Config() failed: %d - %s\n", rc, getReturnCodeAsString(rc));
        return false;
	}

	// Initialize width and height of Composite image
	width = config.Composite_Image_Size_X;
	height = config.Composite_Image_Size_Y;
	
	uchar* compositeImage = new uchar[width*height];
    rc = V100_Capture(&_device, compositeImage, width, height, bioTemplate, templateSize, nSpoof, 1, 1);
    delete compositeImage;

    if (rc == GEN_OK)
    {
        Json::Value json;
        json["type"] = "bio-enroll";
        EventLogger::instance()->postEvent(json);
    }
    else
    {
        LOG_WARN("Captured failed: %d - %s\n", rc, getReturnCodeAsString(rc));
        return false;
    }

    return true;
}
#endif


const char* BiometricReader::getReturnCodeAsString(V100_ERROR_CODE rc)
{
	switch(rc)
	{
		case GEN_OK: return "GEN_OK";
		case GEN_ENCRYPTION_FAIL: return "GEN_ENCRYPTION_FAIL";
		case GEN_DECRYPTION_FAIL: return "GEN_DECRYPTION_FAIL";
		case GET_PD_INIT_FAIL: return "GET_PD_INIT_FAIL";
		case PD_HISTOGRAM_FAIL: return "PD_HISTOGRAM_FAIL";
		case INVALID_ACQ_MODE: return "INVALID_ACQ_MODE";
		case BURNIN_THREAD_FAIL: return "BURNIN_THREAD_FAIL";
		case UPDATER_THREAD_FAIL: return "UPDATER_THREAD_FAIL";
		case GEN_ERROR_START: return "GEN_ERROR_START";
		case GEN_ERROR_PROCESSING: return "GEN_ERROR_PROCESSING,";
		case GEN_ERROR_VERIFY: return "GEN_ERROR_VERIFY,";
		case GEN_ERROR_MATCH: return "GEN_ERROR_MATCH";
		case GEN_ERROR_INTERNAL: return "GEN_ERROR_INTERNAL";
		case GEN_ERROR_INVALID_CMD: return "GEN_ERROR_INVALID_CMD";
		case GEN_ERROR_PARAMETER: return "GEN_ERROR_PARAMETER";
		case GEN_NOT_SUPPORTED: return "GEN_NOT_SUPPORTED";
		case GEN_INVALID_ARGUEMENT: return "GEN_INVALID_ARGUEMENT";
		case GEN_ERROR_TIMEOUT: return "GEN_ERROR_TIMEOUT";
		case GEN_ERROR_LICENSE: return "GEN_ERROR_LICENSE";
		case GEN_ERROR_COMM_TIMEOUT: return "GEN_ERROR_COMM_TIMEOUT";
		case GEN_FS_ERR_CD: return "GEN_FS_ERR_CD";
		case GEN_FS_ERR_DELETE: return "GEN_FS_ERR_DELETE";
		case GEN_FS_ERR_FIND: return "GEN_FS_ERR_FIND";
		case GEN_FS_ERR_WRITE: return "GEN_FS_ERR_WRITE";
		case GEN_FS_ERR_READ: return "GEN_FS_ERR_READ";
		case GEN_FS_ERR_FORMAT: return "GEN_FS_ERR_FORMAT";
		case GEN_ERROR_MEMORY: return "GEN_ERROR_MEMORY";
		case GEN_ERROR_RECORD_NOT_FOUND: return "GEN_ERROR_RECORD_NOT_FOUND";
		case GEN_VER_INVALID_RECORD_FORMAT: return "GEN_VER_INVALID_RECORD_FORMAT";
		case GEN_ERROR_DB_FULL: return "GEN_ERROR_DB_FULL";
		case GEN_ERROR_INVALID_SIZE: return "GEN_ERROR_INVALID_SIZE";
		case GEN_ERROR_TAG_NOT_FOUND: return "GEN_ERROR_TAG_NOT_FOUND";
		case GEN_ERROR_APP_BUSY: return "GEN_ERROR_APP_BUSY";
		case GEN_ERROR_DEVICE_UNCONFIGURED: return "GEN_ERROR_DEVICE_UNCONFIGURED";
		case GEN_ERROR_TIMEOUT_LATENT: return "GEN_ERROR_TIMEOUT_LATENT";
		case GEN_ERROR_DB_NOT_LOADED: return "GEN_ERROR_DB_NOT_LOADED";
		case GEN_ERROR_DB_DOESNOT_EXIST: return "GEN_ERROR_DB_DOESNOT_EXIST";
		case GEN_ERROR_ENROLLMENTS_DO_NOT_MATCH: return "GEN_ERROR_ENROLLMENTS_DO_NOT_MATCH";
		case GEN_ERROR_USER_NOT_FOUND: return "GEN_ERROR_USER_NOT_FOUND";
		case GEN_ERROR_DB_USER_FINGERS_FULL: return "GEN_ERROR_DB_USER_FINGERS_FULL";
		case GEN_ERROR_DB_USERS_FULL: return "GEN_ERROR_DB_USERS_FULL";
		case GEN_ERROR_USER_EXISTS: return "GEN_ERROR_USER_EXISTS";
		case GEN_ERROR_DEVICE_NOT_FOUND: return "GEN_ERROR_DEVICE_NOT_FOUND";
		case GEN_ERROR_DEVICE_NOT_READY: return "GEN_ERROR_DEVICE_NOT_READY";
		case GEN_ERROR_PIPE_READ: return "GEN_ERROR_PIPE_READ";
		case GEN_ERROR_PIPE_WRITE: return "GEN_ERROR_PIPE_WRITE";
		case GEN_LAST: return "GEN_LAST";
		default: return "UNKOWN ERROR";
	}
}


// Print error code
const char* BiometricReader::getOpErrorAsString(_V100_OP_ERROR rc)
{
	switch(rc)
	{
		case STATUS_OK: return "STATUS_OK";
		case ERROR_UID_EXISTS: return "ERROR_UID_EXISTS";
		case ERROR_ENROLLMENT_QUALIFICATION: return "ERROR_ENROLLMENT_QUALIFICATION";
		case ERROR_UID_DOES_NOT_EXIST: return "ERROR_UID_DOES_NOT_EXIST";
		case ERROR_DB_FULL: return "ERROR_DB_FULL";
		case ERROR_QUALIFICATION: return "ERROR_QUALIFICATION";
		case ERROR_DEV_TIMEOUT: return "ERROR_DEV_TIMEOUT";
		case ERROR_USER_CANCELLED: return "ERROR_USER_CANCELLED";
		case ERROR_SPOOF_DETECTED: return "ERROR_SPOOF_DETECTED";
		case ERROR_DB_EXISTS: return "ERROR_DB_EXISTS,";
		case ERROR_DB_DOES_NOT_EXIST: return "ERROR_DB_DOES_NOT_EXIST,";
		case ERROR_ID_DB_TOO_LARGE: return "ERROR_ID_DB_TOO_LARGE";
		case ERROR_ID_DB_EXISTS: return "ERROR_ID_DB_EXISTS";
		case ERROR_ID_USER_EXISTS: return "ERROR_ID_DB_USER_EXISTS";
		case ERROR_ID_USER_NOT_FOUND: return "ERROR_ID_DB_USER_NOT_FOUND";
		case STATUS_ID_MATCH: return "STATUS_ID_MATCH";
		case STATUS_ID_NO_MATCH: return "STATUS_ID_NO_MATCH";
		case ERROR_ID_PARAMETER: return "ERROR_ID_PARAMETER";
		case ERROR_ID_GENERAL: return "ERROR_ID_GENERAL";
		case ERROR_ID_FILE: return "ERROR_ID_FILE";
		case ERROR_ID_NOT_INITIALIZED: return "ERROR_ID_NOT_INITIALIZED";
		case ERROR_ID_DB_FULL: return "ERROR_ID_DB_FULL";
		case ERROR_ID_DB_DOESNT_EXIST: return "ERROR_ID_DB_DOESNT_EXIST";
		case ERROR_ID_DB_NOT_LOADED: return "ERROR_ID_DB_NOT_LOADED";
		case ERROR_ID_RECORD_NOT_FOUND: return "ERROR_ID_RECORD_NOT_FOUND";
		case ERROR_ID_FS: return "ERROR_ID_FS";
		case ERROR_ID_CREATE_FAIL: return "ERROR_ID_CREATE_FAIL";
		case ERROR_ID_INTERNAL: return "ERROR_ID_INTERNAL";
		case ERROR_ID_MEM: return "ERROR_ID_MEM";
		case STATUS_ID_USER_FOUND: return "STATUS_ID_USER_FOUND";
		case STATUS_ID_USER_NOT_FOUND: return "STATUS_ID_USER_NOT_FOUND";
		case ERROR_ID_USER_FINGERS_FULL: return "ERROR_ID_USER_FINGERS_FULL";
		case ERROR_ID_USER_MULTI_FINGERS_NOT_FOUND: return "ERROR_ID_USER_MULTI_FINGERS_NOT_FOUND";
		case ERROR_ID_USERS_FULL: return "ERROR_ID_USERS_FULL";
		case ERROR_ID_OPERATION_NOT_SUPPORTED: return "ERROR_ID_OPERATION_NOT_SUPPORTED";
		case ERROR_ID_NOT_ENOUGH_SPACE: return "ERROR_ID_NOT_ENOUGH_SPACE";
		case ERROR_CAPTURE_TIMEOUT: return "ERROR_CAPTURE_TIMEOUT";
		case ERROR_CAPTURE_LATENT: return "ERROR_CAPTURE_LATENT";
		case ERROR_CAPTURE_CANCELLED: return "ERROR_CAPTURE_CANCELLED";
		case ERROR_CAPTURE_INTERNAL: return "ERROR_CAPTURE_INTERNAL";
		case STATUS_NO_OP: return "STATUS_NO_OP";
        default: return "UNKNOWN OP ERROR";
	}

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
    V100_ERROR_CODE rc;
    _V100_OP_ERROR oprc;

    _readerMutex.lock();
    _isBusy = true;
    _readerMutex.unlock();

    std::string status = "success";

    if (_firmwareImage.size() <= 0)
    {
        LOG_WARN("xBIO empty firmware image\n");
        status = "failed: empty firmware image";
        goto post_event;
    }

    rc = V100_Update_Firmware(&_device, &_firmwareImage[0], _firmwareImage.size());
    if (rc != GEN_OK)
    {
        LOG_WARN("xBIO firmware ugprade failed: %d - %s\n", rc, getReturnCodeAsString(rc));
        setStatus(IStatus::Yellow, "xBIO failed to upgrade");
        status = "failed: ";
        status += getReturnCodeAsString(rc);
        goto post_event;
    }

	oprc = WaitForOperationCompletion(&_device);
	if(oprc !=0)
	{
        LOG_ERROR("xBIO wait for firmware upgrade failed: %d - %s\n", oprc, getOpErrorAsString(oprc));
        setStatus(IStatus::Red, "xBIO command failed");
        status = "failed: ";
        status += getOpErrorAsString(oprc);
        goto post_event;
	}

    
	rc = V100_Reset(&_device);
    close();

	// Give it a few seconds to reboot
	sleepMilliseconds(2000);

    // Reopen Device handle
    initReader();

post_event:
    Json::Value event;
    event["type"] = "xbio-fw-upgrade";
    event["status"] = status;
    event["verstion"] = this->getFirmwareVersion();
    EventLogger::instance()->postEvent(event);

    _fwUpgradeRequested = false;
    _isBusy = false;
}


// Wait for completion
_V100_OP_ERROR BiometricReader::WaitForOperationCompletion(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	int nLastInst = -1;
	_V100_OP_STATUS opStatus;
	
	while(1)
	{
		V100_ERROR_CODE rc = V100_Get_OP_Status(pDev, opStatus);
		if(GEN_OK != rc)
		{
            LOG_ERROR("xBIO V100_Get_OP_Status failed: %d - %s", rc, getReturnCodeAsString(rc));
            setStatus(IStatus::Red, "xBIO command failed");
		    return ERROR_CAPTURE_INTERNAL;
		}

		switch(opStatus.nMode)
		{
		case OP_IN_PROGRESS:
			if(opStatus.eMacroCommand == CMD_ID_ENROLL_USER_RECORD)
			{
				if(opStatus.eAcqStatus != ACQ_NOOP) // Ignore the NO OP where the system is deciding if it can enroll the user
				{
//					if(nLastInst != opStatus.nParameter)
//						fprintf(stdout, "\nWaiting for insertion %d ", opStatus.nParameter);
				}
				nLastInst = opStatus.nParameter;
			}
			if(opStatus.eAcqStatus == ACQ_FINGER_PRESENT)
			{
//				fprintf(stdout, "\nPlease lift your finger");
			}
			break;
		case OP_ERROR:
			if((_V100_OP_ERROR)opStatus.nParameter != ERROR_CAPTURE_LATENT)
			{
                LOG_ERROR("xBIO opStatus.nParameter: %d\n", opStatus.nParameter);
                setStatus(IStatus::Red, "xBIO command failed");
			}
			return (_V100_OP_ERROR)opStatus.nParameter;
			break;
		case OP_COMPLETE:
			return STATUS_OK;
        default:
            break;
		}

        sleepMilliseconds(100);
	}
}

