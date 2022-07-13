/**
    @file   AES128Key.cpp
    @author Greg Strange
    @date   May 2012

    Copyright (c) 2012, synapse.com
*/

#include "AES128Key.h"
#include "log.h"
#include "RFIDExceptions.h"
//#include <openssl/aes.h>
#include <openssl/evp.h>


using namespace RFID;
using namespace Reader;



AES128Key::AES128Key(const uint8_t* key, unsigned keyLength)
{
    if (keyLength != 16)
        throw BugException("AES128 key must be 16 bytes long, have %d", keyLength);

    _key.clear();
    _key.append(key, keyLength);
}



AES128Key::~AES128Key()
{
}


#if 0
ByteArray AES128Key::encrypt(const uint8_t* input, unsigned length)
{
    uint8_t keyBytes[16];
    uint8_t initVector[32];
    AES_KEY key;
    ByteArray output;
    output.resize(((length + 15) / 16) * 16);
 
    // Prep the key
    if (_key.size() != sizeof(keyBytes))
        throw BugException("Incorrect key size");
    memcpy(keyBytes, _key.data(), _key.size());
    AES_set_encrypt_key(keyBytes, 128, &key);

    // Set the init vector to all 0's
    memset(initVector, 0, sizeof(initVector));

    // do it
//    int num;
//    AES_cfb128_encrypt(input, &output[0], length, &key, initVector, &num, AES_ENCRYPT);
//    AES_encrypt(input, &output[0], &key);
    AES_cbc_encrypt(input, &output[0], length, &key, initVector, AES_ENCRYPT);

    return output;
}




ByteArray AES128Key::decrypt(const uint8_t* input, unsigned length)
{
    uint8_t keyBytes[16];
    uint8_t initVector[32];
    AES_KEY key;
    ByteArray output;
    output.resize(((length + 15) / 16) * 16);
 
    // Prep the key
    if (_key.size() != sizeof(keyBytes))
        throw BugException("Incorrect key size");
    memcpy(keyBytes, _key.data(), _key.size());
    AES_set_encrypt_key(keyBytes, 128, &key);

    // set the init vector to 0's
    memset(initVector, 0, sizeof(initVector));

    // do it
    int num;
    AES_cbc_encrypt(input, &output[0], length, &key, initVector, AES_DECRYPT);
//    AES_decrypt(input, &output[0], &key);

    return output;
}
#endif

ByteArray AES128Key::encrypt(const uint8_t* input, unsigned length)
{
    ByteArray output;
    output.resize( ((length + 15) / 16) * 16);

    // Set the init vector to all 0's
    uint8_t initVector[32];
    memset(initVector, 0, sizeof(initVector));

    EVP_CIPHER_CTX context;

    EVP_CIPHER_CTX_init(&context);
    EVP_EncryptInit_ex(&context, EVP_aes_128_cbc(), NULL, _key.data(), initVector);

    EVP_EncryptInit_ex(&context, NULL, NULL, NULL, NULL);
    int outLength;
    EVP_EncryptUpdate(&context, &output[0], &outLength, input, length);
//    EVP_EncryptFinal_ex(&context, &output[outLength1], &outLength2);
//    output.resize(outLength1 + outLength2);

    return output;
}




ByteArray AES128Key::decrypt(const uint8_t* input, unsigned length)
{
    ByteArray output;
    output.resize( ((length + 15) / 16) * 16);

    // Set the init vector to all 0's
    uint8_t initVector[32];
    memset(initVector, 0, sizeof(initVector));

    EVP_CIPHER_CTX context;

    EVP_CIPHER_CTX_init(&context);
    EVP_DecryptInit_ex(&context, EVP_aes_128_cbc(), NULL, _key.data(), initVector);

    int outLength;
    EVP_DecryptUpdate(&context, &output[0], &outLength, input, length);
//    EVP_DecryptFinal_ex(&context, &output[outLength1], &outLength2);

    return output;
}
