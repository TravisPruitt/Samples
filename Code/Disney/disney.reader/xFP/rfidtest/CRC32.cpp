/**
    @file   CRC32.cpp
    @author Roy Sprowl
    @date   May 2012

    Copyright (c) 2012, synapse.com
*/

#include "standard.h"
#include "CRC32.h"

using namespace RFID;


static bool tableInitialized = false;
static uint32_t table[256];


enum Polynomial 
{
    IEEE = 0xEDB88320,
    Castagnoli = 0x82F63B78,
    Koopman = 0xEB31D82E
};


void initializeTable()
{            
    if (tableInitialized)
        return;

    for (unsigned idx = 0; idx < 256; ++idx)
    {
        uint32_t crc = idx;
        for (uint32_t bit = 0; bit < 8; bit++)
        {
            if ((crc & 0x01) == 0x01)
                crc = (uint32_t)((crc >> 1) ^ ::IEEE);
            else
                crc >>= 1;
        }

        table[idx] = crc;
    }

    tableInitialized = true;
}

uint32_t CRC32::ComputeCRC32_IEEE(const uint8_t* bytes, unsigned int length, bool invert) 
{   
    initializeTable();
    uint32_t crc = 0xFFFFFFFF;

    for(unsigned idx = 0; idx < length; idx++) 
    {
        uint8_t lookup = (uint8_t)(((crc) & 0xFF) ^ bytes[idx]);
        crc = (uint32_t)((crc >> 8) ^ table[lookup]);
    }

    return (invert) ? ~crc : crc;
}
