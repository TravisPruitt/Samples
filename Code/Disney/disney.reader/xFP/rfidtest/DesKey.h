/**
    @file   DesKey.h - DES Key
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/



#ifndef __DES_KEY_H
#define __DES_KEY_H

#include "standard.h"
#include "ByteArray.h"
#include "IKey.h"

using namespace Reader;

namespace RFID
{
    class DesKey : public IKey
    {
    public:
        DesKey(const uint8_t* key, unsigned keyLength);
        virtual ~DesKey();

        virtual ByteArray decrypt(const uint8_t* input, unsigned length);
        virtual ByteArray encrypt(const uint8_t* input, unsigned length);
        virtual KeyType::Enum getType() { return KeyType::TDEA_CRC16; };

    private:
        ByteArray _key;
    };
};


#endif

