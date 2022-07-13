/**
    @file   CRC32.h
    @author Greg Strange
    @date   May 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef CRC_32_H
#define CRC_32_H

#include "standard.h"

namespace RFID
{

class CRC32
{
public:
    static uint32_t ComputeCRC32_IEEE(const uint8_t* bytes, unsigned int length, bool invert);
};

}

#endif // CRC_16_H

