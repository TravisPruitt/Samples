/**
    @file   SAMKey.cpp
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#include "SAMKey.h"
#include "log.h"
#include "RFIDExceptions.h"
#include "CRC16.h"
#include "CRC32.h"
#include "IKey.h"
#include "StringLib.h"
#include "JsonParser.h"


using namespace RFID;
using namespace Reader;

    //<SAMKeyEntry KeyNumber="9" Comment="NGE DESFire App Secure File Auth Key">
    //  <ChangeKey KeyNumber="0" KeyVersion="0" />
    //  <DESFire AID="0x78E127" DESFireKeyNumber="1" />
    //  <KeySettings KeepInitVector="true" KeyType="AES" />
    //  <Keys>
    //    <AESKey Version="1">5C6F-5DBA-4387-B466-C28D-D094-0C52-C886</AESKey>
    //    <AESKey Version="0">0000-0000-0000-0000-0000-0000-0000-0000</AESKey>
    //    <AESKey Version="0">0000-0000-0000-0000-0000-0000-0000-0000</AESKey>
    //  </Keys>
    //</SAMKeyEntry>
#if 0
static const uint8_t _keyData[] = 
{
    0x5C, 0x6F, 0x5D, 0xBA, 0x43, 0x87, 0xB4, 0x66, 0xC2, 0x8D, 0xD0, 0x94, 0x0C, 0x52, 0xC8, 0x86, // key A
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // key B
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // key C
    0x27, 0xe1, 0x78,   // app ID, LSB first
    1,  // desfire key number
    0, 0,   // change key and version
    0xff,   // key usage counter reference number (0xff = none)
    0x20 + 0x04, // config settings, low byte, 0x20 = AES key type, 0x04 = keep init vector
    0x00,        // config settings, high byte
    1, 0, 0 // key versions
};

static const uint8_t _keyData[] = 
{
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // key A
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // key B
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // key C
    0, 0, 0,   // app ID, LSB first
    0,  // desfire key number
    0, 0,   // change key and version
    0xff,   // key usage counter reference number (0xff = none)
    0, // config settings, low byte, 0x20 = AES key type, 0x04 = keep init vector
    0,        // config settings, high byte
    0, 0, 0 // key versions
};
#endif



SAMKey::SAMKey() :
        _keyVersionA(0),
        _keyVersionB(0),
        _keyVersionC(0),
        _desfireAppId(0),
        _desfireKeyNumber(0),
        _changeKeyNumber(0),
        _changeKeyVersion(0),
        _usageCounter(0xff),
        _keepInitVector(false),
        _canUseForCrypto(false),
        _keyType(KeyType::TDEA_CRC32)

{
    memset(_keyA, 0, sizeof(_keyA));
    memset(_keyB, 0, sizeof(_keyB));
    memset(_keyC, 0, sizeof(_keyC));
}

ByteArray SAMKey::pack()
{
    ByteArray packed;

    packed.append(_keyA, sizeof(_keyA));
    packed.append(_keyB, sizeof(_keyB));
    packed.append(_keyC, sizeof(_keyC));
    packed.append(_desfireAppId & 0xff);
    packed.append((_desfireAppId >> 8) & 0xff);
    packed.append((_desfireAppId >> 16) & 0xff);
    packed.append(_desfireKeyNumber);
    packed.append(_changeKeyNumber);
    packed.append(_changeKeyVersion);
    packed.append(_usageCounter);
    uint16_t settings = 0;
    settings |= (_keyType << 3);
    if (_keepInitVector)            settings |= 0x04;
    if (_canUseForCrypto)           settings |= 0x02;
    packed.append(settings & 0xff);
    packed.append( (settings >> 8) & 0xff);
    packed.append(_keyVersionA);
    packed.append(_keyVersionB);
    packed.append(_keyVersionC);

    LOG_DEBUG("packed key = %s", packed.toString().c_str());

    return packed;

#if 0
    ByteArray result;
    result.append(_keyData, sizeof(_keyData));
    return result;
#endif
}



void SAMKey::unpack(ByteArray& bytes)
{
    if (bytes.size() < 12)
        throw SAMException("Key length too short, need 12 bytes, got %d\n", bytes.size());

    unsigned i = 0;
    _keyVersionA = bytes[i++];
    _keyVersionB = bytes[i++];
    _keyVersionC = bytes[i++];
    _desfireAppId = bytes[i++];
    _desfireAppId += (bytes[i++] << 8);
    _desfireAppId += (bytes[i++] << 16);
    _desfireKeyNumber = bytes[i++];
    _changeKeyNumber = bytes[i++];
    _changeKeyVersion = bytes[i++];
    _usageCounter = bytes[i++];
    uint16_t settings = bytes[i++];
    settings += bytes[i++];

    _keepInitVector = (settings & 0x04) != 0;
    _canUseForCrypto = (settings & 2) != 0;

    _keyType = (KeyType::Enum)((settings & 0x38) >> 3);
}


static const char* getKeyTypeName(int keyType)
{
    switch (keyType)
    {
        case KeyType::TDEA_DESFIRE: return "TDEA_DESFIRE";
        case KeyType::TDEA_CRC16:   return "TDEA_CRC16";
        case KeyType::MIFARE:       return "MIFARE";
        case KeyType::TDEA_3:       return "3DEA";
        case KeyType::AES128:       return "AES128";
        case KeyType::AES192:       return "AES192";
        case KeyType::TDEA_CRC32:   return "TDEA_CRC32";
        default:                    return "UNKNOWN";
    }
}


void SAMKey::dump()
{
    LOG_INFO("    Key versions (A, B, C): %d, %d, %d\n", _keyVersionA, _keyVersionB, _keyVersionC);
    LOG_INFO("    Desfire AppID: %d (%06x)\n", _desfireAppId, _desfireAppId);
    LOG_INFO("    Desfire key number: %d\n", _desfireKeyNumber);
    LOG_INFO("    Change key number, version: %d, %d\n", _changeKeyNumber, _changeKeyVersion);
    LOG_INFO("    Usage counter: %d\n", _usageCounter);
    LOG_INFO("    key type = %s\n", getKeyTypeName(_keyType));
    LOG_INFO("    keep init vector = %s\n", _keepInitVector ? "true" : "false");
    LOG_INFO("    key A: %s", StringLib::formatBytes(_keyA, sizeof(_keyA)).c_str());
    LOG_INFO("    key B: %s", StringLib::formatBytes(_keyB, sizeof(_keyB)).c_str());
    LOG_INFO("    key C: %s", StringLib::formatBytes(_keyC, sizeof(_keyC)).c_str());
}


ByteArray SAMKey::encode(IKey& key)
{
    ByteArray bytes = pack();

    switch (key.getType())
    {
    case KeyType::TDEA_DESFIRE:
    case KeyType::TDEA_CRC16:
        {
            uint16_t crc = CRC16::ComputeCRC16_CCITT(bytes.data(), bytes.size(), false);
            bytes.append(crc & 0xff);
            bytes.append((crc >> 8) & 0xff);
        }
        break;

    default:
        {
            uint32_t crc = CRC32::ComputeCRC32_IEEE(bytes.data(), bytes.size(), false);
            bytes.append(crc & 0xff);
            bytes.append((crc >> 8) & 0xff);
            bytes.append((crc >> 16) & 0xff);
            bytes.append((crc >> 24) & 0xff);
        }
        break;
    }

    for (unsigned i = bytes.size(); i < 64; ++i)
        bytes.append(0);

    ByteArray ciphered = key.encrypt(bytes.data(), bytes.size());
    return ciphered;
}



static bool jgetValue(Json::Value& value, const char* name, Json::Value& newValue)
{
    if (value.isMember(name))
    {
        newValue = value[name];
        return true;
    }
    else
    {
        LOG_WARN("No %s member in key defintion", name);
        return false;
    }
}


static int jgetInt(Json::Value& value, const char* name, int defaultValue)
{
    int result;
    if (JsonParser::parseInt(value, name, result))
        return result;

    LOG_WARN("No '%s' member, or member is not an integer", name);
    return defaultValue;
}


static bool jgetBool(Json::Value& value, const char* name, bool defaultValue)
{
    bool result;
    if (JsonParser::parseBool(value, name, result))
        return result;

    LOG_WARN("No '%s' member, or member is not a boolean", name);
    return defaultValue;
}


static bool ishex(char c)
{
    c = tolower(c);
    return (isdigit(c) || ( (c >= 'a') && (c <= 'f')) );
}

static uint8_t convertNibble(char c)
{
    c = tolower(c);
    if (isdigit(c))
        return c - '0';
    else
        return c - 'a' + 10;
}

static void jgetKey(Json::Value& value, const char* name, uint8_t* key)
{
    // Grab the key string
    std::string str;
    if (name)
    {
        if (!value.isMember(name))
        {
            LOG_WARN("No %s member in key definitions", name);
            return;
        }

        if (!value[name].isString())
        {
            LOG_WARN("Member %s needs to be a string value", name);
            return;
        }

        str = value[name].asString();
    }
    else
    {
        if (!value.isString())
        {
            LOG_WARN("No key bytes provided");
            return;
        }
        str = value.asString();
    }

    // And parse it into bytes
    const char* s = str.c_str();
    for (int i = 0; i < 16; ++i)
    {
        while (*s && !ishex(*s))    ++s;
        if (*s)
        {
            key[i] = convertNibble(*s++);
            if (ishex(*s))
                key[i] = (key[i] << 4) + convertNibble(*s++);
        }
        else
        {
            LOG_WARN("key length too short");
            return;
        }
    }
}


static void parseKey(Json::Value& json, uint8_t& version, uint8_t* bytes)
{
    if (json.isString())
    {
        version = 0;
        jgetKey(json, NULL, bytes);
    }
    else
    {
        version = jgetInt(json, "Version", 0);
        jgetKey(json, "Value", bytes);
    }
}


void SAMKey::parse(Json::Value& json)
{
    _changeKeyNumber = jgetInt(json, "ChangeKeyNumber", _changeKeyNumber);
    _changeKeyVersion = jgetInt(json, "ChangeKeyVersion", _changeKeyVersion);
    _desfireAppId = jgetInt(json, "DESFireAID", _desfireAppId);
    _desfireKeyNumber = jgetInt(json, "DESFireKeyNumber", _desfireKeyNumber);
    _keepInitVector = jgetBool(json, "KeepInitVector", _keepInitVector);
    _canUseForCrypto = jgetBool(json, "CanUseForCrypto", false);
    if (!json.isMember("KeyType") || !json["KeyType"].isString())
        LOG_WARN("No KeyType member");
    else
    {
        std::string s = json["KeyType"].asString();
        for (int i = 0; i < KeyType::NUM_KEY_TYPES; ++i)
        {
            if (strcasecmp(s.c_str(), getKeyTypeName(i)) == 0)
            {
                _keyType = (KeyType::Enum)i;
                break;
            }
        }
    }

    Json::Value keys;
    if (jgetValue(json, "Keys", keys))
    {
        if (!keys.isArray() || keys.size() != 3)
            LOG_WARN("Member 'Keys' needs to be an array with 3 members");

        parseKey(keys[0], _keyVersionA, _keyA);
        parseKey(keys[1], _keyVersionB, _keyB);
        parseKey(keys[2], _keyVersionC, _keyC);
    }
}


