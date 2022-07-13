/**
    @file   SAMKey.cpp
    @author Greg Strange
    @date   Feb 2012

    Copyright (c) 2012, synapse.com
*/


#include "SAMKey.h"
#include "log.h"

using namespace RFID;


// Settings masks
const static uint16_t MASK_ALLOW_DUMP_SESSKEY = 0x0001;
const static uint16_t MASK_ALLOW_CRYPTO = 0x0002;
const static uint16_t MASK_KEEP_IV = 0x0004;
const static uint16_t MASK_KEYTYPE = 0x0038;
const static uint16_t MASK_USE_CMAC = 0x0100;
const static uint16_t MASK_DISABLE_ENTRY = 0x0200;
const static uint16_t MASK_REQUIRE_HOST_AUTH = 0x0400;
const static uint16_t MASK_DISABLE_PICC_CHANGEKEY = 0x0800;
const static uint16_t MASK_DISABLE_DECRYPT = 0x1000;
const static uint16_t MASK_DISABLE_ENCRYPT = 0x2000;
const static uint16_t MASK_DISABLE_VERIFY_MAC = 0x4000;
const static uint16_t MASK_DISABLE_GEN_MAC = 0x8000;


SAMKey::SAMKey() : settings(0)
{
}


void SAMKey::deserialize(const uint8_t* data, unsigned length)
{
    if (length != 11 && length != 12)
        throw SAMException("Incorrect SAM key data length");

    int i = 0;
    keyAVersion = data[i++];
    keyBVersion = data[i++];
    if (length == 12)
        keyCVersion = data[i++];
    desfireAID = data[i] + (data[i+1] << 8) + (data[i+2] << 16);
    i += 3;
    desfireKey = data[i++];
    changeKey = data[i++];
    changeKeyVersion = data[i++];
    usageCounter = data[i++];
    settings = data[i] + (data[i+1] << 8);
}


void SAMKey::dump()
{
    LOG_DEBUG("  Key Type: %02X  ***\n", getKeyType());
    LOG_DEBUG("  AppID: %d\n", desfireAID);
    LOG_DEBUG("  DFKey#: %d\n", desfireKey);
    LOG_DEBUG("  ChangeKey#: %d\n", changeKey);
    LOG_DEBUG("  KeyAVersion: %d\n", keyAVersion);
    LOG_DEBUG("  ChangeVer: %d\n", changeKeyVersion);
    LOG_DEBUG("  KeySettings: %08X\n", (unsigned short)settings);
}




/// If set, allows session key or MIFARE key to be dumped
bool SAMKey::CanDumpSessionKey()
{
    return Bitmask::IsSet(MASK_ALLOW_DUMP_SESSKEY, settings);
}
void SAMKey::CanDumpSessionKey(bool value)
{
    Bitmask::SetBit(value, MASK_ALLOW_DUMP_SESSKEY, settings);
}

/// If set, enables the secret key to be used directly for all cryptographic operations
bool SAMKey::CanUseForCrypto()
{
    return Bitmask::IsSet(MASK_ALLOW_CRYPTO, settings);
}
void SAMKey::CanUseForCrypto(bool value)
{
    Bitmask::SetBit(value, MASK_ALLOW_CRYPTO, settings);
}

/// If set, the initialization vector will be maintained across subsequent cryptographic operations
bool SAMKey::KeepInitVector()
{
    return Bitmask::IsSet(MASK_KEEP_IV, settings);
}
void SAMKey::KeepInitVector(bool value)
{
    Bitmask::SetBit(value, MASK_KEEP_IV, settings);
}

/// Gets/sets the type of key (cryptographic algorithm) stored in this key entry
KeyType::Enum SAMKey::getKeyType()
{
    return (KeyType::Enum)((Bitmask::GetMaskedValue(MASK_KEYTYPE, settings) >> 3));
}
void SAMKey::setKeyType(KeyType::Enum value)
{
    uint16_t type = (uint16_t)((uint8_t)value << 3);
    Bitmask::SetMaskedValue(MASK_KEYTYPE, type, settings);
}

/// If set, enables higher security by employing CMAC encryption on all commands.  
/// Only applicable for KeyNo 0x00 - RFU for all other keys
bool SAMKey::IsCMACEnabled()
{
    return Bitmask::IsSet(MASK_USE_CMAC, settings);
}
void SAMKey::IsCMACEnabled(bool value)
{
    Bitmask::SetBit(value, MASK_USE_CMAC, settings);
}

/// If not set, the key entry is disabled and cannot be used until reactivated
bool SAMKey::IsEnabled()
{
    return !Bitmask::IsSet(MASK_DISABLE_ENTRY, settings);
}
void SAMKey::IsEnabled(bool value)
{
    Bitmask::SetBit(!value, MASK_DISABLE_ENTRY, settings);
}

/// If set, host authentication is required after reset to enable most other commands.
/// Only applicable for KeyNo 0x00 - RFU for all other keys
bool SAMKey::RequiresHostAuthentication()
{
    return Bitmask::IsSet(MASK_REQUIRE_HOST_AUTH, settings);
}
void SAMKey::RequiresHostAuthentication(bool value)
{
    Bitmask::SetBit(value, MASK_REQUIRE_HOST_AUTH, settings);
}

/// If set, the key can be downloaded to a DESFire PICC using a ChangeKey command
bool SAMKey::CanChangeDESFireKey()
{
    return !Bitmask::IsSet(MASK_DISABLE_PICC_CHANGEKEY, settings);
}
void SAMKey::CanChangeDESFireKey(bool value)
{
    Bitmask::SetBit(!value, MASK_DISABLE_PICC_CHANGEKEY, settings);
}

/// If set, this key can be used to decrypt data from a PICC or host
bool SAMKey::CanDecrypt()
{
    return !Bitmask::IsSet(MASK_DISABLE_DECRYPT, settings);
}
void SAMKey::CanDecrypt(bool value)
{
    Bitmask::SetBit(!value, MASK_DISABLE_DECRYPT, settings);
}

/// If set, this key can be used to encrypt data to be sent to a PICC or host
bool SAMKey::CanEncrypt()
{
    return !Bitmask::IsSet(MASK_DISABLE_ENCRYPT, settings);
}
void SAMKey::CanEncrypt(bool value)
{
    Bitmask::SetBit(!value, MASK_DISABLE_ENCRYPT, settings);
}

/// If set, this key can be used to verify MACs sent by a PICC or host
bool SAMKey::CanVerifyMAC()
{
    return !Bitmask::IsSet(MASK_DISABLE_VERIFY_MAC, settings);
}
void SAMKey::CanVerifyMAC(bool value)
{
    Bitmask::SetBit(!value, MASK_DISABLE_VERIFY_MAC, settings);
}

/// If set, this key can be used to generate MACs to be sent to a PICC or host
bool SAMKey::CanGenerateMAC()
{
    return !Bitmask::IsSet(MASK_DISABLE_GEN_MAC, settings);
}
void SAMKey::CanGenerateMAC(bool value)
{
    Bitmask::SetBit(!value, MASK_DISABLE_GEN_MAC, settings);
}

