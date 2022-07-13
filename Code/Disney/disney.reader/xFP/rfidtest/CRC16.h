/**
    @file   CRC16.cpp
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef CRC_16_H
#define CRC_16_H

#include "standard.h"

namespace RFID
{

class CRC16
{
public:
    static uint16_t ComputeCRC16_CCITT(const uint8_t* bytes, unsigned int length, bool invert);

    static uint16_t ComputeCRC16_CCITT(const uint8_t* bytes, unsigned int length, int offset, bool invert);
};

}

#endif // CRC_16_H

