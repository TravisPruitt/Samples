/**
    @file   Serializer.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef SERIALIZER_H
#define SERIALIZER_H

#include "standard.h"
#include "stdint.h"


namespace RFID
{

class Serializer
{    
public:
#if 0
    static void SerializeInt64(uint8_t* bytes, long value, int offset)
    {
        for (int idx = offset; idx < offset + 8; idx++)
        {
            bytes[idx] = (uint8_t)value;
            value >>= 8;
        }
    }

    static long DeserializeInt64(const uint8_t* bytes, int offset)
    {
        unsigned long value = 0;
        for (int idx = offset + 7; idx >= offset; idx--)
        {
            value <<= 8;
            value += bytes[idx];
        }

        return (long)value;
    }
#endif

    static void SerializeInt32(uint8_t* bytes, int32_t value, int offset)
    {
        for (int idx = offset; idx < offset + 4; idx++)
        {
            bytes[idx] = (uint8_t)value;
            value >>= 8;
        }
    }

    static int32_t DeserializeInt32(const uint8_t* bytes, int offset)
    {
        uint32_t value = 0;
        for (int idx = offset + 3; idx >= offset; idx--)
        {                
            value <<= 8;
            value += bytes[idx];
        }

        return (int32_t)value;
    }

    static void SerializeInt24(uint8_t* bytes, int32_t value, int offset)
    {
        for (int idx = offset; idx < offset + 3; idx++)
        {
            bytes[idx] = (uint8_t)value;
            value >>= 8;
        }
    }

    static int DeserializeInt24(const uint8_t* bytes, int offset)
    {
        int32_t value = 0;
        for (int idx = offset + 2; idx >= offset; idx--)
        {
            value <<= 8;
            value += bytes[idx];
        }

        return value;
    }

    static void SerializeInt16(uint8_t* bytes, int16_t value, int offset)
    {
        for (int idx = offset; idx < offset + 2; idx++)
        {
            bytes[idx] = (uint8_t)value;
            value >>= 8;
        }
    }

    static int16_t DeserializeInt16(const uint8_t* bytes, int offset)
    {
        uint16_t value = 0;
        for (int idx = offset + 1; idx >= offset; idx--)
        {
            value <<= 8;
            value += bytes[idx];
        }

        return (int16_t)value;
    }
};

}

#endif // SERIALIZER_H
