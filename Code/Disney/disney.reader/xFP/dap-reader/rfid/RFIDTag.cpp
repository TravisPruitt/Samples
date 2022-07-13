/**
    @file   RFIDTag.cpp
    @author Greg Strange
    @date   Jan 2012
*/

#include "standard.h"
#include "RFIDTag.h"
#include "RFIDExceptions.h"
#include "log.h"
#include <string.h>
#include <string>


using namespace RFID;





static char formatNibble(uint8_t n)
{
    if (n <= 9)
        return n + '0';
    else
        return n - 10 + 'A';
}


static void bytesToString(const ByteArray& bytes, std::string& s)
{
    s.clear();

    for (unsigned i = 0; i < bytes.size(); ++i)
    {
        s += formatNibble(bytes[i] >> 4);
        s += formatNibble(bytes[i] & 0xf);
    }
}


RFIDTag::RFIDTag()
{
}


RFIDTag::RFIDTag(UID& uid) : _uid(uid)
{
}



const char* RFIDTag::getUID() const
{
    static std::string s;
    bytesToString(_uid, s);
    return s.c_str();
}


void RFIDTag::setPublicId(ByteArray& id)
{
    bytesToString(id, _publicId);
}




bool RFIDTag::parsePublicData(ByteArray& bytes)
{
    if (bytes.size() < 16)
    {
        LOG_INFO("Tag read length incorrect");
        return false;
    }

    uint8_t pidLength = bytes[8] & 0xf;
    _domain = (bytes[8] >> 4) & 0xf;
    uint8_t check012 = bytes[9];
    uint8_t check345 = bytes[10];
    uint8_t check678 = bytes[11];
    _cryptoFormat = bytes[12];
    _useDiversity = ((bytes[13] & 0x80) != 0);
    _authKeyNumber = bytes[13] & 0x7f;
    _authKeyVersion = bytes[14];
    uint8_t keyCheck = bytes[15];

    if ( (check012 != (bytes[0] ^ bytes[1] ^ bytes[2])) ||
            (check345 != (bytes[3] ^ bytes[4] ^ bytes[5])) ||
            (check678 != (bytes[6] ^ bytes[7] ^ bytes[8])) ||
            (keyCheck != (bytes[12] ^ bytes[13] ^ bytes[14])) )
    {
        LOG_INFO("Tag checksum failed");
        return false;
    }

    if (pidLength != 5)
    {
        LOG_INFO("Unexpected public ID length");
        return false;
    }

    _publicId.clear();
    for (int i = pidLength-1; i >= 0; --i)
    {
        _publicId += formatNibble(bytes[i] >> 4);
        _publicId += formatNibble(bytes[i] & 0xf);
    }

    return true;
}




bool RFIDTag::parseSecureData(ByteArray& bytes, SecureData& sd)
{
    if (bytes.size() < 20)
    {
        LOG_INFO("Tag read length incorrect");
        return false;
    }

    if (bytes[3] != (bytes[0] ^ bytes[1] ^ bytes[2]) )
    {
        LOG_INFO("Tag secure checksum failed");
        return false;
    }

    sd.cryptoLength = bytes[0];
    sd.cryptoKeyNumber = bytes[1];
    sd.cryptoKeyVersion = bytes[2];

    if (sd.cryptoLength < 4 || sd.cryptoLength > 48)
    {
        LOG_INFO("Bad crypto length on tag");
        return false;
    }

    sd.initVector.append(&bytes[4], 16);
    return true;
}



/**
    Parse the secure ID portion of a tag

    The secure ID is separated into two portions

    bytes 1-4 = IIN.  Stored as a 32 bit unsigned value, low byte first.
    bytes 5-12 = Account Number (commonly called the secure ID).  Stored as a
                 64 bit unsigned value, low byte first.

    
*/
bool RFIDTag::parseSecureId(ByteArray& bytes)
{
    char buf[30];


    if (bytes.size() != 28)
    {
        LOG_INFO("Decrypted data length incorrect");
        return false;
    }

    // parse out the UID
    UID sn;
    sn.append(bytes.data() + 1, 7);
    if (sn != _uid)
        throw TagException("Invalid secure ID, Serial number != UID");
        
    // parse out the IIN
    uint32_t iin = 0;
    for (int i = 11; i >= 8; --i)
        iin = (iin << 8) + bytes[i];
    sprintf(buf, "%05u", iin);
    if (strlen(buf) > 6)
    {
        LOG_INFO("Invalid IIN %s", buf);
        return false;
    }
    _iin = buf;

    // parse out the secure ID
    uint64_t sid = 0;
    for (int i = 23; i >= 12; --i)
        sid = (sid << 8) + bytes[i];
    sprintf(buf, "%016llu", sid);
    if (strlen(buf) > 16)
        throw TagException("Invalid secure ID %s", buf);
    _secureId = buf; 

    for (int column = 0; column < 4; ++column)
    {
        uint8_t checksum = 0;
        for (int row = 0; row < 6; ++row)
            checksum ^= bytes[row*4 + column];
        if (checksum != bytes[24 + column])
        {
            LOG_INFO("Checksum error on secure data from tag");
            return false;
        }
    }

    return true;
}

