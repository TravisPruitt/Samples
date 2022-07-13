/**
    @file   UltralightC.cpp
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/


#include "standard.h"
#include "UltralightC.h"
#include "log.h"
#include "RFIDExceptions.h"
#include "CRC16.h"


using namespace RFID;


namespace Command { enum Enum {
    READ = 0x30
}; }


    

UltralightC::UltralightC(ByteArray& uid) : RFIDTag(uid)
{
}


/**
    Read the public and optionally the secure ID from a tag
*/
void UltralightC::readIds(SAM* sam, bool readSecureId)
{
    readPublicData(sam);
    if (readSecureId)
        readSecureData(sam);
}



/**
    Read and parse the public data on a tag.

    @return true on success, false if public data is not formatted correctly

    @throw  TagException on error commnicating with tag
    @throw  ReaderException on error communicating with reader
*/
void UltralightC::readPublicData(SAM* sam)
{
    LOG_TRAFFIC("read public data");

    ByteArray addresses;
    ByteArray reply;
    addresses.append(16);
    sam->readULC(addresses, reply);

    if (!parsePublicData(reply))
        throw TagException("Unable to parase public data");
}




/**
    Read and parse the secure data on a tag.

    @return true on success, false if secure data is not formatted correctly

    @throw  TagException on error commnicating with tag
    @throw  ReaderException on error communicating with reader
    @throw  SAMException on error communicating with SAM
*/
void UltralightC::readSecureData(SAM* sam)
{
    ByteArray reply;

    if (_cryptoFormat != 0)
        throw TagException("Tag uses unknown crypto format");

    sam->authenticateULC(6, 0);

    LOG_TRAFFIC("read secure data");
    ByteArray addresses;
    addresses.append(23);
    addresses.append(27);
    sam->readULC(addresses, reply);

    // parse the first part of the secure data
    SecureData sd;
    if (!parseSecureData(reply, sd))
        throw TagException("Unable to parse secure data");

    // read the encrypted secure ID
    LOG_TRAFFIC("Read secure IDs");
    addresses.clear();
    for (uint8_t block = 28; block < 28 + (sd.cryptoLength / 4); block += 4)
        addresses.append(block);
    sam->readULC(addresses, reply);

    // decrypt the secure ID
    ByteArray decryptedData;
    sam->decrypt(reply, sd.cryptoKeyNumber, sd.cryptoKeyVersion, sd.initVector, 28, decryptedData);

    // parse secure ID
    if (!parseSecureId(decryptedData))
        throw TagException("Unable to parse secure ID");
}
