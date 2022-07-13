/**
    @file   AES128.h - 128 bit AES key
    @author Greg Strange
    @date   May 2012

    Copyright (c) 2012, synapse.com
*/



#ifndef __AES128_KEY_H
#define __AES128_KEY_H

#include "standard.h"
#include "ByteArray.h"
#include "IKey.h"

using namespace Reader;

namespace RFID
{
    class AES128Key : public IKey
    {
    public:
        AES128Key(const uint8_t* key, unsigned keyLength);
        virtual ~AES128Key();

        virtual ByteArray decrypt(const uint8_t* input, unsigned length);
        virtual ByteArray encrypt(const uint8_t* input, unsigned length);
        virtual KeyType::Enum getType() { return KeyType::AES128; };

    private:
        ByteArray _key;
    };
};


#endif

