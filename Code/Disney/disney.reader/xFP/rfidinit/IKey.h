/**
    @file   IKey.h - Interface to SAM
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/



#ifndef __IKEY_H
#define __IKEY_H

#include "standard.h"
#include "ByteArray.h"

using namespace Reader;


namespace RFID
{
    namespace KeyType { enum Enum {
        TDEA_DESFIRE = 0,
        TDEA_CRC16,
        MIFARE,
        TDEA_3,
        AES128,
        AES192,
        TDEA_CRC32,
        NUM_KEY_TYPES
    }; };

    class IKey
    {
    public:
        virtual ~IKey() {};

        virtual ByteArray decrypt(const uint8_t* input, unsigned length) = 0;
        virtual ByteArray encrypt(const uint8_t* input, unsigned length) = 0;
        virtual KeyType::Enum getType() = 0;
    };
};


#endif

