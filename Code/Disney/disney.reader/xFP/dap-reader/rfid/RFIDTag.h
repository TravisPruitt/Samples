/**
    @file   RFIDTag.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef __RFID_TAG_H
#define __RFID_TAG_H

#include "ByteArray.h"
#include <vector>
#include <string>


using namespace Reader;
typedef ByteArray UID;


namespace RFID
{

class IReader;


class RFIDTag
{
public:
    RFIDTag();
    RFIDTag(UID& uid);

    virtual bool readIds(IReader& reader, bool readSecureId) = 0;

    const char* getUID() const;

    const char* getPublicId() const { return _publicId.c_str(); }

    // The "accunt number" portion of secure ID
    const char* getSecureId() const { return _secureId.c_str(); }   

    // The Issuers Identification Number (IIN) portion of secure ID
    const char* getIIN() const { return _iin.c_str(); }

    bool hasSecureId() const { return _secureId.size() > 0; }

protected:  // types
    struct SecureData
    {
        uint8_t cryptoLength;
        uint8_t cryptoKeyNumber;
        uint8_t cryptoKeyVersion;
        ByteArray initVector;
    };

protected:  // data
    // All of the ID information on the tag
    UID _uid;
    std::string _publicId;
    std::string _iin;
    std::string _secureId;
    uint8_t _domain;

    // Information from the public data section about the secure data section
    bool _useDiversity;
    uint8_t _cryptoFormat;
    uint8_t _authKeyNumber;
    uint8_t _authKeyVersion;

protected:  // methods
    void setPublicId(ByteArray& id);
    bool parsePublicData(ByteArray& bytes);
    bool parseSecureData(ByteArray& bytes, SecureData& sd);
    bool parseSecureId(ByteArray& id);
};

}


#endif
