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


using namespace RFID;

    
static char formatNibble(uint8_t n)
{
    if (n <= 9)
        return n + '0';
    else
        return n - 10 + 'A';
}



UltralightC::UltralightC(UID& uid) : RFIDTag(uid)
{
}


/**
    Read public and optionally secure IDs from a tag

    @param    reader  Reader to use
    @param    readSecureId  true if we should read  the secure ID in addition to publid IC.  
                            false if we should only read the public ID

    @return   true on success, false on failure

    @throw    RFIDException on problems with reader or SAM.
              Problems with the tag are filtered and not thrown.
*/
bool UltralightC::readIds(IReader& reader, bool readSecureId)
{
    try
    {
        // If we fail, we keep trying for as long as we can select the tag
        while (reader.select(_uid))
        {
            try
            {
                if (readPublicData(reader)
                    && (!readSecureId || readSecureData(reader)) )
                {
                    return true;
                }
            }
            catch (TagException e)
            {
                LOG_INFO(e.what());
            }
        }
    }
    catch (TagException e)
    {
        LOG_INFO(e.what());
    }

    return false;
}



/**
    Read and parse the public data on a tag.

    @return true on success, false if public data is not formatted correctly

    @throw  TagException on error commnicating with tag
    @throw  ReaderException on error communicating with reader
*/
bool UltralightC::readPublicData(IReader& reader)
{
    ByteArray reply;
    reader.readBlocks(16, 4, reply);
    return parsePublicData(reply);
}




/**
    Read and parse the secure data on a tag.

    @return true on success, false if secure data is not formatted correctly

    @throw  TagException on error commnicating with tag
    @throw  ReaderException on error communicating with reader
    @throw  SAMException on error communicating with SAM
*/
bool UltralightC::readSecureData(IReader& reader)
{
    ByteArray reply;

    if (_cryptoFormat != 0)
    {
        LOG_INFO("Tag uses unknown crypto format");
        return false;
    }

    // authenticate with key pre-loaded into reader
    reader.authenticateUltralightC();

    // read first part of the secure data with crypto format and key information
    reader.readBlocks(23, 5, reply);

    // parse the first part of the secure data
    SecureData sd;
    if (!parseSecureData(reply, sd))
        return false;

    // read the encrypted secure ID
    reader.readBlocks(28, sd.cryptoLength / 4, reply);
    LOG_DEBUG("Read secure IDs");

    // decrypt the secure ID
    ByteArray decryptedData;
    reader.decrypt(reply, sd.cryptoKeyNumber, sd.cryptoKeyVersion, sd.initVector, 28, decryptedData);

    // parse secure ID
    if (!parseSecureId(decryptedData))
        return false;

    return true;
}
