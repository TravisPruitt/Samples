/**
    @file   SAMKey.h
    @author Greg Strange
    @date   Feb 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef SAM_KEY_H
#define SAM_KEY_H

#include "standard.h"
#include "RFIDExceptions.h"
#include "Serializer.h"
#include "BitMask.h"

namespace RFID
{

namespace KeyType { enum Enum 
{
    /// TDES DESFire4 Native implementation
    TDES_DESFire4 = 0x00,

    /// ISO 10116 TDES using 16 bit CRCs and 4 byte MACs
    TDES_16bitCRC = 0x01,

    /// MIFARE Crypto1
    Crypto1 = 0x02,

    /// ISO 10116 3 Key TDES
    TDES3Key = 0x03,

    /// AES 128
    AES = 0x04,

    /// AES 192
    AES192 = 0x05,

    /// ISO 10116 TDES using 32 bit CRCs and 8 byte MACs
    TDES_32bitCRC = 0x06,

    /// No key type has been specified
    Unspecified = 0xFF
};}

struct SAMKey
{
    SAMKey();
    void deserialize(const uint8_t* data, unsigned length);
    void dump();

    bool CanDumpSessionKey();
    void CanDumpSessionKey(bool value);
    bool CanUseForCrypto();
    void CanUseForCrypto(bool value);
    bool KeepInitVector();
    void KeepInitVector(bool value);
    KeyType::Enum getKeyType();
    void setKeyType(KeyType::Enum value);
    bool IsCMACEnabled();
    void IsCMACEnabled(bool value);
    bool IsEnabled();
    void IsEnabled(bool value);
    bool RequiresHostAuthentication();
    void RequiresHostAuthentication(bool value);
    bool CanChangeDESFireKey();
    void CanChangeDESFireKey(bool value);
    bool CanDecrypt();
    void CanDecrypt(bool value);
    bool CanEncrypt();
    void CanEncrypt(bool value);
    bool CanVerifyMAC();
    void CanVerifyMAC(bool value);
    bool CanGenerateMAC();
    void CanGenerateMAC(bool value);
    
    uint32_t desfireAID;
    uint8_t desfireKey;
    uint8_t keyAVersion;
    uint8_t keyBVersion;
    uint8_t keyCVersion;
    uint8_t changeKey;
    uint8_t changeKeyVersion;
    uint8_t usageCounter;
    uint16_t settings;
};


}

#endif
