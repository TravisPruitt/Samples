/**
 *  @file   BiometricReaderDevice.cpp
 *  @date   October, 2012
 *  @author Corey Wharton
 *  @author Greg Strange
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  This class contains the low level interface to the the Lumidigm Biometric Reader
 *  and ensures single-threaded access to the Lumidigm library.
 */

#include <string.h>
#include "log.h"
#include "BiometricReaderDevice.h"
#include "ticks.h"


#define XBIO_ERROR(msg)  LOG_ERROR(msg ": %d - %s",                     \
                                   _rc,                                 \
                                   getLastReturnCodeAsString());


#define XBIO_WARN(msg)   LOG_WARN(msg ": %d - %s",                      \
                                  _rc,                                  \
                                  getLastReturnCodeAsString());


#define XBIO_CMD(cmd, ...)                            \
    Lock lock(_mutex);                                \
                                                      \
    if (!_opened)                                     \
        return true;                                  \
                                                      \
    LOG_TRAFFIC("xBIO: " # cmd);                      \
    _rc = cmd(&_device, ##__VA_ARGS__);               \
    if (_rc != GEN_OK)                                \
    {                                                 \
        XBIO_ERROR("xBIO " # cmd " failed");          \
        return false;                                 \
    }                                                 \
                                                      \
    return true;                                      \



namespace Reader
{


BiometricReaderDevice::BiometricReaderDevice() :
    _opened(false)
{
}


BiometricReaderDevice::~BiometricReaderDevice()
{
    if(_opened)
        close();
}


bool BiometricReaderDevice::open()
{
    Lock lock(_mutex);
    if (_opened)
        return false;

    LOG_TRAFFIC("XBIO: Open");

    // V100_Open() crashes if there is junk in the uninitialized device structure
    // so we zero it here
    memset((void*)&_device, 0, sizeof(_device));

	_rc = V100_Open(&_device);
    if (_rc == GEN_OK)
        _opened = true;

    return _opened;
}


bool BiometricReaderDevice::close()
{
    Lock lock(_mutex);

    LOG_TRAFFIC("XBIO: close");
    _rc = V100_Close(&_device);
    if (_rc == GEN_OK)
        _opened = false;

    return !_opened;
}


void BiometricReaderDevice::reset()
{
    Lock lock(_mutex);
    V100_Reset(&_device);
}


bool BiometricReaderDevice::getConfigurationInfo(_V100_INTERFACE_CONFIGURATION_TYPE* config)
{
    XBIO_CMD(V100_Get_Config, config);
}


bool BiometricReaderDevice::getDiagnosticsInfo(_V500_DIAGNOSTIC_PACKET* info)
{
    return getImage((_V100_IMAGE_TYPE)5010,
                    (uint8_t*)info,
                    sizeof(_V500_DIAGNOSTIC_PACKET));
}


bool BiometricReaderDevice::getTransactionData(_V500_TRANSACTION_DATA* data)
{
    return getImage((_V100_IMAGE_TYPE)5011,
                    (uint8_t*)data,
                    sizeof(_V500_TRANSACTION_DATA));
}


bool BiometricReaderDevice::getImageStack(uint8_t* buffer, size_t length)
{
    return getImage((_V100_IMAGE_TYPE)5000, buffer, length);
}


bool BiometricReaderDevice::getAcqStatus(_V100_ACQ_STATUS_TYPE*  status)
{
    XBIO_CMD(V100_Get_Acq_Status, status);
}


bool BiometricReaderDevice::getOperationStatus(_V100_OP_STATUS& status)
{
    XBIO_CMD(V100_Get_OP_Status, status);
}


bool BiometricReaderDevice::cancelOperation()
{
    XBIO_CMD(V100_Cancel_Operation);
}


bool BiometricReaderDevice::setOption(_V100_OPTION_TYPE type, uint8_t* option, size_t length)
{
    XBIO_CMD(V100_Set_Option, type, option, length);
}


bool BiometricReaderDevice::setLightGain(uint8_t* gain)
{
    return setOption((_V100_OPTION_TYPE)170, gain, 1);
}


bool BiometricReaderDevice::setLed(_V100_LED_CONTROL value)
{
    XBIO_CMD(V100_Set_LED, value);
}


bool BiometricReaderDevice::getCmd(_V100_INTERFACE_COMMAND_TYPE* cmd)
{
    XBIO_CMD(V100_Get_Cmd, cmd);
}


bool BiometricReaderDevice::setCmd(_V100_INTERFACE_COMMAND_TYPE* cmd)
{
    XBIO_CMD(V100_Set_Cmd, cmd);
}


bool BiometricReaderDevice::getImage(_V100_IMAGE_TYPE type, uint8_t* buffer, size_t length)
{
    XBIO_CMD(V100_Get_Image, type, buffer, length);
}


bool BiometricReaderDevice::getTemplate(uint8_t* buffer, size_t* length)
{
    XBIO_CMD(V100_Get_Template, buffer, length);
}


bool BiometricReaderDevice::armTrigger()
{
    XBIO_CMD(V100_Arm_Trigger, TRIGGER_ON);
}


bool BiometricReaderDevice::readScriptSet(char* buffer, size_t* length)
{
    XBIO_CMD(V100_Script_Read, buffer, length);
}


bool BiometricReaderDevice::writeScriptSet(char* buffer, size_t length)
{
    XBIO_CMD(V100_Script_Write, buffer, length);
}


bool BiometricReaderDevice::playScript(const char* name, LED_BRIGHTNESS brightness)
{
    XBIO_CMD(V100_Script_Play, (char*)name, brightness);
}


bool BiometricReaderDevice::updateFirmware(uint8_t*  buffer, size_t length)
{
    Lock lock(_mutex);

    _V100_OP_ERROR oprc;

    if (!_opened)
        return true;

    LOG_TRAFFIC("xBIO: V100_Update_Firmware");
    _rc = V100_Update_Firmware(&_device, buffer, length);
    if (_rc != GEN_OK)
    {
        XBIO_ERROR("xBIO V100_Update_Firmware failed");
        return false;
    }

    oprc = WaitForOperationCompletion();
	if(oprc != 0)
        return false;

    return true;
}


_V100_OP_ERROR BiometricReaderDevice::WaitForOperationCompletion()
{
    int nLastInst = -1;
    _V100_OP_STATUS opStatus;
    
    while(1)
    {
        _rc = V100_Get_OP_Status(&_device, opStatus);
        if(_rc != GEN_OK)
            return ERROR_CAPTURE_INTERNAL;

        switch(opStatus.nMode)
        {
        case OP_IN_PROGRESS:
            if(opStatus.eMacroCommand == CMD_ID_ENROLL_USER_RECORD)
            {
                nLastInst = opStatus.nParameter;
            }
            break;
        case OP_ERROR:
            if((_V100_OP_ERROR)opStatus.nParameter != ERROR_CAPTURE_LATENT)
                LOG_ERROR("xBio opStatus.nParameter: %d\n", opStatus.nParameter);
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


const char* BiometricReaderDevice::getLastReturnCodeAsString()
{
	switch(_rc)
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


const char* BiometricReaderDevice::getOpErrorAsString(_V100_OP_ERROR code)
{
	switch(code)
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


}  // namespace Reader
