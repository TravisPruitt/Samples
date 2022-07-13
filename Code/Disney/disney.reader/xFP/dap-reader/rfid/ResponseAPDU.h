/**
    @file   ResponseAPDU.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef RESPONSE_APDU_H
#define RESPONSE_APDU_H

#include "standard.h"
#include "ByteArray.h"
#include <vector>

using namespace Reader;

namespace RFID
{

/**
    Application Protocol Data Unit (APDU) response structure as specified by ISO 7816-4

    A response ADPU is composed of a number of data bytes followed by two status bytes named SW1 and SW2
*/
class ResponseAPDU
{
private:
    ByteArray _bytes;

public:
    //static readonly ResponseAPDU OK = new ResponseAPDU(0x90, 0x00);

    ResponseAPDU(const uint8_t* bytes, unsigned int length)
    {
        _bytes.append(bytes, length);
    }

    ResponseAPDU(ByteArray& data)
    {
        _bytes = data;
    }

    ResponseAPDU(uint8_t sw1, uint8_t sw2)
    {
        _bytes.append(sw1);
        _bytes.append(sw2);
    }

    // Copy constructor for returning by value steals the byte array
    ResponseAPDU(const ResponseAPDU& rhs)
    {
        _bytes = rhs._bytes;
    }

    ~ResponseAPDU()
    {
    }

    bool IsSuccess()
    {
        uint8_t cps = this->CommandProcessingStatus();
        uint8_t cpq = this->CommandProcessingQualifier();
        return _bytes.size() >= 2
            && cps == 0x90
            && (cpq == 0x00 || cpq == 0xAF);                    
    }

    /// Indicates if the parsed frame constitutes a valid ResponseAPDU
    bool IsValid()
    {
        return _bytes.size() >= 2;
    }

    /// Response data
    const uint8_t* Data()
    {
        if (_bytes.size() > 2)
            return &_bytes[0];
        else 
            return 0;
    }

    /// The length of the response APDU data
    unsigned int DataLength() 
    { 
        return _bytes.size() - 2;
    }


private:
    /// Command processing status
    uint8_t CommandProcessingStatus()
    {
        return _bytes.size() >= 2 ? _bytes[_bytes.size() - 2] : 0;
    }

    /// Command processing qualifier
    uint8_t CommandProcessingQualifier()
    {
        return _bytes.size() >= 2 ? _bytes[_bytes.size() - 1] : 0;
    }
};

}

#endif // RESPONSE_APDU_H
