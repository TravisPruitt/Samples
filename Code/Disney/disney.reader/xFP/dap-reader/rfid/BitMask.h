/**
    @file   BitMask.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef BIT_MASK_H
#define BIT_MASK_H

#include "standard.h"

class Bitmask
{
public:
    static bool IsSet(uint8_t mask, uint8_t value)
    {
        return (value & mask) == mask;
    }

    static void SetBit(bool isSet, uint8_t mask, uint8_t& value)
    {
        if (isSet) value |= mask;
        else value = (uint8_t)(value & ~mask);
    }

    static uint8_t GetMaskedValue(uint8_t mask, uint8_t value)
    {
        return (uint8_t)(value & mask);
    }

    static void SetMaskedValue(uint8_t mask, uint8_t newValue, uint8_t& value)
    {
        value = (uint8_t)((value & ~mask) | (newValue & mask));
    }

    static bool IsSet(uint16_t mask, uint16_t value)
    {
        return (value & mask) == mask;
    }

    static void SetBit(bool isSet, uint16_t mask, uint16_t& value)
    {
        if (isSet) value |= mask;
        else value = (uint16_t)(value & ~mask);
    }

    static uint16_t GetMaskedValue(unsigned short mask, unsigned short value)
    {
        return (uint16_t)(value & mask);
    }

    static void SetMaskedValue(unsigned short mask, unsigned short newValue, uint16_t& value)
    {
        value = (uint16_t)((value & ~mask) | (newValue & mask));
    }

    static bool IsSet(unsigned short mask, unsigned int value)
    {
        return (value & mask) == mask;
    }

    static void SetBit(bool isSet, unsigned int mask, unsigned int& value)
    {
        if (isSet) value |= mask;
        else value = (unsigned int)(value & ~mask);
    }

    static unsigned int GetMaskedValue(unsigned int mask, unsigned int value)
    {
        return (unsigned int)(value & mask);
    }

    static void SetMaskedValue(unsigned int mask, unsigned int newValue, unsigned int& value)
    {
        value = (unsigned int)((value & ~mask) | (newValue & mask));
    }
};

#endif // BIT_MASK_H
