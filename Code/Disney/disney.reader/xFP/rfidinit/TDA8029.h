/**
    @file   TDA8029.h
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef TDA8029_H
#define TDA8029_H


#include "standard.h"
#include "ResponseAPDU.h"
#include "SerialPort.h"

#include "ISAMTalker.h"
#include <string>


namespace RFID
{


class TDA8029 : public ISAMTalker
{
public:
    TDA8029();
    ~TDA8029();

    // Initialization
    void init();
    bool readTag(ByteArray& uid, bool& isDesfire, unsigned timeoutMs);

    bool haveSAM() { return _haveSAM; };
    void turnOnSAM();
    void turnOffSAM();

    void sendSAMCommand(const  uint8_t* data, unsigned length);
    void readSAMReply(ByteArray& reply, unsigned timeoutMs = 1000);

    std::string getFirmwareVersion() { return _haveVersion ? _firmwareVersion : ""; };

    void idleMode();

private:   // data
    SerialPort* _port;
    bool _haveVersion;
    std::string _firmwareVersion;
    bool _haveSAM;

private:   // methods
    bool init(uint32_t baud);

    void setBaud(uint32_t baud);
    void getVersion();
    void getStatus();
    void setSAMClock();

    void sendCommand(uint8_t command, const uint8_t* data, unsigned length);
    void readReply(uint8_t command, ByteArray& reply, unsigned timeoutMs = 1000);

    uint8_t calcLRC(const uint8_t* data, unsigned length);
};

}

#endif
