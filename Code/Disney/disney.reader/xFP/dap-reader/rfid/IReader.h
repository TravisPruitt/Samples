/**
    @file   IReader.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef __IREADER_H
#define __IREADER_H

#include "RFIDTag.h"
#include "ByteArray.h"

namespace RFID
{

class RFIDTag;

class IReader
{
public:
    // used by all tags
    virtual bool select(UID& uid) = 0;

    // used by Desfire
    virtual void sendISO144434TCL(const uint8_t* msg, unsigned length, ByteArray& reply) = 0;
    virtual void authenticateTag(UID& uid, unsigned appId, uint8_t keyNo, bool useDiversity) = 0;
//    virtual void generateMAC(const uint8_t* message, unsigned messageLen) = 0;
//    virtual void decryptData(const uint8_t* encryptedData, unsigned encryptedDataLen, int decryptedLength, const uint8_t* crcData, unsigned crcDataLen, ByteArray& decryptedData) = 0;

    // SAM commands
    virtual void sendSAMT1Frame(uint8_t slot, unsigned timeoutMs, const uint8_t* payload, unsigned length, ByteArray& response) = 0;

    // ISO Standard Host commands (e.g. Ultralight C)
    virtual void readBlocks(uint8_t blockNumber, uint8_t numBlocks, ByteArray& buf) = 0;
    virtual void authenticateUltralightC() = 0;
    virtual void decrypt(ByteArray& encryptedData, uint8_t keyNo, uint8_t keyVersion, ByteArray& initVector, unsigned decryptedLength, ByteArray& decryptedData) = 0;
};

}

#endif
