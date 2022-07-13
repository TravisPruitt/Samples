/**
    @file   ResponseAPDU.h
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef __RESPONSE_APDU_H
#define __RESPONSE_APDU_H

#include "ByteArray.h"


using namespace Reader;


namespace RFID
{


/**
    Application Protocol Data Unit (APDU) response structure as specified by ISO 7816-4

    A response ADPU is composed of a number of data bytes followed by two status bytes named SW1 and SW2
*/
class ResponseAPDU : public ByteArray
{
public:
    static const uint8_t SW1_OK = 0x90;
    static const uint8_t SW2_OK = 0x00;
    static const uint8_t SW2_CONTINUE = 0xAF;
    static const uint8_t SW2_NO_CARD = 0xe0;

    bool isValid()
    {
        return ByteArray::size() >= 2;
    }

    // True if command completed successfully
    bool isSuccess()
    {
        return (sw1() == SW1_OK && sw2() == SW2_OK);
    }

    // True if command was accepted, and ready for next step in a sequence
    bool isContinue()
    {
        return (sw1() == SW1_OK && sw2() == SW2_CONTINUE);
    }

    // True if there were no errors
    bool isOk()
    {
        return isSuccess() || isContinue();
    }

    // True if the response indicates there is no card in the field
    bool isNoCard()
    {
        return ( (sw1() == SW1_OK) && (sw2() == SW2_NO_CARD) );
    }

    uint8_t sw1()
    {
        return ( (ByteArray::size() >= 2) ? (*this)[ByteArray::size()-2] : 0);
    }

    uint8_t sw2()
    {
        return ( (ByteArray::size() >= 2) ? (*this)[ByteArray::size()-1] : 0);
    }

    size_type size() const
    {
        return ByteArray::size() >= 2 ? ByteArray::size() - 2 : 0;
    }
    
};

}


#endif

