/**
 *  @file   BiometricReaderDevice.h
 *  @date   October, 2012
 *  @author Corey Wharton
 *  @author Greg Strange
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  This class contains the low level interface to the the Lumidigm Biometric Reader
 *  and ensures single-threaded access to the Lumidigm library.
 */

#include <stdint.h>

#include "V100_shared_types.h"
#include "VCOMCore.h"
#include "Mutex.h"

namespace Reader
{


class BiometricReaderDevice
{
public:
    BiometricReaderDevice();
    ~BiometricReaderDevice();

    bool open();
    bool close();
    void reset();

    bool getAcqStatus(_V100_ACQ_STATUS_TYPE* status);
    bool getOperationStatus(_V100_OP_STATUS& status);
    bool cancelOperation();

    bool getConfigurationInfo(_V100_INTERFACE_CONFIGURATION_TYPE* config);
    bool getDiagnosticsInfo(_V500_DIAGNOSTIC_PACKET* info);
    bool getTransactionData(_V500_TRANSACTION_DATA* data);
    bool getImageStack(uint8_t* buffer, size_t length);

    bool setOption(_V100_OPTION_TYPE type, uint8_t* option, size_t length);
    bool setLightGain(uint8_t* gain);

    bool setLed(_V100_LED_CONTROL value);
    bool getCmd(_V100_INTERFACE_COMMAND_TYPE* cmd);
    bool setCmd(_V100_INTERFACE_COMMAND_TYPE* cmd);
    bool getImage(_V100_IMAGE_TYPE type, uint8_t* buffer, size_t length);
    bool getTemplate(uint8_t* buffer, size_t* length);
    bool armTrigger();

    bool readScriptSet(char* buffer, size_t* length);
    bool writeScriptSet(char* buffer, size_t length);
    bool playScript(const char* name, LED_BRIGHTNESS brightness);

    bool updateFirmware(uint8_t*  buffer, size_t length);

    V100_ERROR_CODE getLastCode()
    {
        return _rc;
    }

    bool isOpened()
    {
        return _opened;
    }

    const char* getLastReturnCodeAsString();
    const char* getOpErrorAsString(_V100_OP_ERROR code);

private: // methods

    _V100_OP_ERROR WaitForOperationCompletion();

private: //data
    Mutex _mutex;

    V100_DEVICE_TRANSPORT_INFO _device;
    bool _opened;

    V100_ERROR_CODE _rc;
};


} // namespace Reader
