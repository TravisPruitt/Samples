/**
    @file BiometricReader.h - Lumidigm Biometric reader
    @date August 1, 2011
    @author Greg Strange

    Copyright (c) 2011-2012, synapse.com

    This class encapsolates the Lumidigm Biometric Reader interfaceThe.  The bulk of the
    Lumidigm interface code exists in a dynamically linked library.  This just wraps
    the functions we care about from that library to represent a single Lumidigm
    Biometric reader to the rest of the software.
*/


#ifndef __BIOMETRIC_READER_H
#define __BIOMETRIC_READER_H

#include <string>
#include <vector>

#include "VCOMCore.h"
#include "Thread.h"
#include "json/json.h"
#include "Event.h"
#include "Mutex.h"
#include "ticks.h"
#include "StatusKeeper.h"
#include "BiometricReaderDevice.h"

#define MIN_XBIO_BRIGHTNESS      BRIGHTNESS_LEVEL_1
#define MAX_XBIO_BRIGHTNESS      BRIGHTNESS_LEVEL_8
#define DEFAULT_XBIO_BRIGHTNESS  BRIGHTNESS_LEVEL_2


namespace Reader
{

class BiometricReader : public Thread, public StatusKeeper
{
public:
    static BiometricReader* instance();

    bool isPresent();

    // non-blocking calls
    void startEnroll();
    void startMatch(std::vector<std::vector<uint8_t> >* templates = NULL);
    void cancelRead();
    void startFirmwareUpdate(const uint8_t* image, size_t length);

    void updateScriptSet();
    unsigned readScriptSet(char* buffer);
    bool writeScriptSet(char* buffer, unsigned length);
    void restart();

    void lightBlue();
    void lightGreen();
    void lightWhite();
    void lightOff();
    void setLightGain(int gain);
    bool playScript(const char *name, LED_BRIGHTNESS brightness = DEFAULT_XBIO_BRIGHTNESS);

    unsigned short getSerialNumber();
    unsigned short getFirmwareVersion();
    unsigned short getLightRingFWVersion();
    unsigned short getHardwareVersion();
    void getImage(const uint8_t** dataPtr, unsigned int* length);
    bool isIdle();

    void setTestMode(bool);
    bool getTestMode() { return _inTestMode; };

private: // methods
    // singleton
    BiometricReader();
    ~BiometricReader();

    // There should be only one Biometric Reader, so no copying please
    BiometricReader(const BiometricReader&);
    const BiometricReader& operator=(const BiometricReader&);

    void setLED(LRING_Mode_Type value);

    void run();
    bool initReader();
    void _restart();

    void doIdleProcessing();
    void doScanProcessing();
    void doDiagnostics();

    void startCapture(bool enroll);
    void checkCapture();
    void processCaptureFingerPresent();
    void processCaptureTimeout();
    void processCaptureDone();

    void postScanError(const char* reason);

    uint16_t getFirmwareFileVersion(std::string filename);
    void checkForFirmwareUpdate();
    void doFirmwareUpgrade();

    void doUpdateScriptSet(const char* filename);

private: //data 
    bool _haveXbio;
    bool _inTestMode;

    bool _enrollRequested;
    bool _matchRequested;
    bool _cancelRequested;
    bool _fwUpgradeRequested;

    int _tries;
    int _timeouts;

    MILLISECONDS _lastDiagnosticsEvent;

    std::vector<uint8_t> _firmwareImage;

    bool _haveTemplates;
    std::vector<std::vector<uint8_t> > _templates;

    enum {
        XBIO_IDLE,
        XBIO_ENROLL_SCAN,
        XBIO_MATCH_SCAN,
        XBIO_RETRY_DELAY,
    };
    int _xBioState;

    MILLISECONDS _retryDelay;
    MILLISECONDS _retryDelayStart;

    Json::Value _bioEvent;

    BiometricReaderDevice _device;

    _V100_INTERFACE_CONFIGURATION_TYPE _deviceConfig;
    _V500_DIAGNOSTIC_PACKET _deviceDiagnostics;

    // TODO - defines for Maxs
    uint8_t _imageBuffer[2 * 1024 * 1024];
    unsigned int _imageLength;
    uint8_t _templatesBuffer[2048];

    // status and last error message
    IStatus::Status _errorStatus;
    std::string _errorMsg;

    // thread synchronization stuff
    Mutex _mutex;
};


} // namespace Reader


#endif
