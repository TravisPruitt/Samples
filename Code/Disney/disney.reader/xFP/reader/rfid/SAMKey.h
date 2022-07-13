/**
    @file   SAMKey.h - SAM key entry
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/



#ifndef __SAM_KEY_H
#define __SAM_KEY_H

#include "standard.h"
#include "ByteArray.h"
#include "IKey.h"
#include "json/json.h"

using namespace Reader;



namespace RFID
{
    class SAMKey
    {
    public:
        SAMKey();
        ~SAMKey() {};

        ByteArray pack();
        void unpack(ByteArray& bytes);
        ByteArray encode(IKey& encryptingKey);
        void parse(Json::Value&);

        void dump();

    private:
        uint8_t _keyA[16];
        uint8_t _keyB[16];
        uint8_t _keyC[16];
        uint8_t _keyVersionA;
        uint8_t _keyVersionB;
        uint8_t _keyVersionC;
        uint32_t _desfireAppId;
        uint8_t _desfireKeyNumber;
        uint8_t _changeKeyNumber;
        uint8_t _changeKeyVersion;
        uint8_t _usageCounter;
        bool _keepInitVector;
        bool _canUseForCrypto;
        KeyType::Enum _keyType;
    };
};


#endif

