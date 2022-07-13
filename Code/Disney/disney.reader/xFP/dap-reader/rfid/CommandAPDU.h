/**
    @file   CommandAPDU.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef COMMAND_APDU_H
#define COMMAND_APDU_H

#include "standard.h"
#include "ByteArray.h"
#include "RFIDExceptions.h"
#include "log.h"

using namespace Reader;

namespace RFID
{

/// Application Protocol Data Unit (APDU) command structure as specified by ISO 7816-4
class CommandAPDU
{

private:
    ByteArray _bytes;
    const unsigned int _length;


    /**
        APDU are formatted as follows
            byte 0 = cla = class byte = 0x80 + channel number (0-3)
            byte 1 = ins = instruction
            byte 2 = p1 = parameter byte 1
            byte 3 = p2 = parameter byte 2
            byte 4 = data length (if data)
            byte 5.. = data bytes
    */
    unsigned int Initialize(uint8_t cla, uint8_t ins, uint8_t p1, uint8_t p2, const uint8_t* data, unsigned int dataLen, uint8_t le)
    {
        // Construct the APDU header and parameter section
        _bytes.clear();
        _bytes.append(cla);
        _bytes.append(ins);
        _bytes.append(p1);
        _bytes.append(p2);

        // If data was provided, include Lc and the data
        if (dataLen > 0)
        {
            _bytes.append((uint8_t)dataLen);
            _bytes.append(data, dataLen);
        }

        // If and expected response length Le was provided, include it as well
        if (le != 0xFF) 
            _bytes.append(le);

        return _bytes.size();
    }

    unsigned int Initialize(uint8_t cla, uint8_t ins, uint8_t p1, uint8_t p2, ByteArray& data, uint8_t le)
    {
        // Construct the APDU header and parameter section
        _bytes.append(cla);
        _bytes.append(ins);
        _bytes.append(p1);
        _bytes.append(p2);

        // If data was provided, include Lc and the data
        if (data.size() > 0xFF)
        {
            LOG_WARN("Too much data in CommandAPDU\n");
            throw SAMException();
        }
        uint8_t dataBytes[256];
        uint8_t dataLen = data.size();
        data.copy(dataBytes);
        if (dataLen > 0)
        {
            _bytes.append((uint8_t)dataLen);
            _bytes.append(dataBytes, dataLen);
        }

        // If and expected response length Le was provided, include it as well
        if (le != 0xFF) 
            _bytes.append(le);

        return _bytes.size();
    }

public:
    CommandAPDU(uint8_t cla, uint8_t ins, uint8_t p1, uint8_t p2)
        : _length(Initialize(cla, ins, p1, p2, 0, 0, 0xFF))
    {
    }

    CommandAPDU(uint8_t cla, uint8_t ins, uint8_t p1, uint8_t p2, const uint8_t* data, unsigned int dataLen)
        : _length(Initialize(cla, ins, p1, p2, data, dataLen, 0xFF))
    {
    }

    CommandAPDU(uint8_t cla, uint8_t ins, uint8_t p1, uint8_t p2, const uint8_t* data, unsigned int dataLen, uint8_t le)
        : _length(Initialize(cla, ins, p1, p2, data, dataLen, le))
    {
    }
    CommandAPDU(uint8_t cla, uint8_t ins, uint8_t p1, uint8_t p2, ByteArray& bytes, uint8_t le)
        : _length(Initialize(cla, ins, p1, p2, bytes, le))
    {
    }

    // Copy constructor to allow return by value. Steals the original data. (Roy)
//    CommandAPDU(const CommandAPDU& rhs) :_length(rhs._length)
//    {
//        _bytes = rhs._bytes;
//        rhs._bytes = 0;
//  }

    ~CommandAPDU()
    {
    }

    const uint8_t* Bytes() const { return &_bytes[0]; }

    unsigned int Length() const { return _bytes.size(); }
};

}

#endif // COMMAND_APDU_H
