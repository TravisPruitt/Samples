/**
    @file   CRC16.cpp
    @author Roy Sprowl
    @date   Feb, 2012

    Copyright (c) 2012, synapse.com
*/

#include "standard.h"
#include "CRC16.h"

using namespace RFID;


enum Polynomial 
{
    CCITT = 0x8408
};

//static Dictionary<Polynomial, uint16_t[]> _tables = new Dictionary<Polynomial, uint16_t[]>();

// There's only ever a single table, so we'll make it static
static short table[256];
bool tableInitialized = false;

void InitializeTable()
{
    if (tableInitialized)
        return;

    for (uint16_t idx = 0; idx < 256; idx++)
    {
        uint16_t crc = idx;
        for (unsigned int bit = 0; bit < 8; bit++)
        {
            if ((crc & 0x01) == 0x01)
               crc = (uint16_t)((crc >> 1) ^ ::CCITT);
            else
                crc >>= 1;
        }

        table[idx] = crc;
    }
    tableInitialized = true;
}

uint16_t CRC16::ComputeCRC16_CCITT(uint8_t* bytes, unsigned int length, bool invert)
{
    return ComputeCRC16_CCITT(bytes, length, 0, invert);
}

uint16_t CRC16::ComputeCRC16_CCITT(uint8_t* bytes, unsigned int length, int offset, bool invert)
{
    InitializeTable();
    uint16_t crc = 0x6363;

    for (unsigned int idx = 0; idx < length; idx++)
    {
        uint8_t lookup = (uint8_t)(((crc) & 0xFF) ^ bytes[offset + idx]);
        crc = (uint16_t)((crc >> 8) ^ table[lookup]);
    }

    return (invert) ? (uint16_t)~crc : crc;
}
