/**
    @file   DesKey.cpp
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#include "DesKey.h"
#include "log.h"
#include "RFIDExceptions.h"
#include <openssl/des.h>


using namespace RFID;
using namespace Reader;



DesKey::DesKey(const uint8_t* key, unsigned keyLength)
{
    if (keyLength != 8)
        throw BugException("DES key must be 8 bytes long, have %d", keyLength);

    _key.clear();
    _key.append(key, keyLength);
}



DesKey::~DesKey()
{
}


ByteArray DesKey::encrypt(const uint8_t* input, unsigned length)
{
    DES_cblock key; // key
    DES_cblock iv;  // init vector
    DES_key_schedule schedule;
    ByteArray output;

    output.resize(length);
 
    // Prep the key
    memcpy(key, _key.data(), _key.size());
    DES_set_key_unchecked(&key, &schedule );

    // Set the init vector to all 0's
    memset(iv, 0, 8);

    // do it
    DES_ncbc_encrypt(input, &output[0], length, &schedule, &iv, DES_ENCRYPT);
    return output;
}




ByteArray DesKey::decrypt(const uint8_t* input, unsigned length)
{
    DES_cblock key;     // key
    DES_cblock iv;      // init vector
    DES_key_schedule schedule;
    ByteArray output;
 
    output.resize(length);
 
    // Prep the key
    memcpy(key, _key.data(), _key.size());
    DES_set_key_unchecked(&key, &schedule );

    // set the init vector to 0's
    memset(iv, 0, 8);

    // decrypt
    DES_ncbc_encrypt(input, &output[0], length, &schedule, &iv, DES_DECRYPT);
    return output;
}
