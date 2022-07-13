/**
    @file   Desfire.cpp
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/


#include "standard.h"
#include "Desfire.h"
#include "IReader.h"
#include "log.h"
#include "DesfireConstants.h"
#include "RFIDExceptions.h"


using namespace RFID;



static const unsigned AppId = 0x78e127;
static const unsigned PublicFile = 1;
static const unsigned SecureFile = 2;




Desfire::Desfire(UID& uid) : RFIDTag(uid)
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
bool Desfire::readIds(IReader& reader, bool readSecureId)
{
    try
    {
        // If we fail, we keep trying for as long as we can select the tag
        while (reader.select(_uid))
        {
            try
            {
                selectApp(reader);

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
bool Desfire::readPublicData(IReader& reader)
{
    ByteArray buf;
    if (readFile(reader, PublicFile, 0, 16, buf))
        return parsePublicData(buf);
    else
        return false;
}


/**
    Read and parse the secure data on a tag.

    @return true on success, false if secure data is not formatted correctly

    @throw  TagException on error commnicating with tag
    @throw  ReaderException on error communicating with reader
    @throw  SAMException on error communicating with SAM
*/
bool Desfire::readSecureData(IReader& reader)
{
    ByteArray buf;
    
    LOG_DEBUG("authenticate the tag");
    reader.authenticateTag(_uid, AppId, _authKeyNumber, _useDiversity);
    
    if (!readFile(reader, SecureFile, 4, 20, buf))
        return false;

    SecureData sd;
    if (!parseSecureData(buf, sd))
        return false;

    // read the encrypted secure ID
    if (!readFile(reader, SecureFile, 24, sd.cryptoLength, buf))
        return false;

    // decrypt the secure ID
    ByteArray decryptedData;
    reader.decrypt(buf, sd.cryptoKeyNumber, sd.cryptoKeyVersion, sd.initVector, 28, decryptedData);

    // parse secure ID
    if (!parseSecureId(decryptedData))
        return false;

    return true;
}



/**
    Select the application on the desfire tag
*/
void Desfire::selectApp(IReader& reader)
{
    LOG_DEBUG("select the app on the card\n");
    ByteArray reply;
    ByteArray msg;
    msg.append(DesfireCommand::SelectAID);
    msg.append((uint8_t)AppId);
    msg.append((uint8_t)(AppId >> 8));
    msg.append((uint8_t)(AppId >> 16));

    reader.sendISO144434TCL(&msg[0], msg.size(), reply);
    if (reply.size() <= 0 || reply[0] != DesfireStatus::OK)
    {
        throw TagException("Unable to select application on desfire tag\n");
    }
}




/**
    @return true on success.  False if unable to read the desired file.  That is the hardware
            appeared to work properly, but the file is not on the tag, or the keys don't match.

    @throw  BugException    for software bugs detected
    @throw  TagException    for communication errors with Tag
    @throw  SAMException    for errors communicating with SAM
    @throw  ReaderException for Feig reader errors
*/
bool Desfire::readFile(IReader& reader, unsigned fileNo, unsigned offset, unsigned length, ByteArray& buf)
{
    ByteArray reply;

    // read the file
    LOG_DEBUG("read the file contents\n");
    uint8_t data[8];
    unsigned i = 0;
    data[i++] = DesfireCommand::ReadData;
    data[i++] = fileNo;
    data[i++] = offset & 0xff;
    data[i++] = (offset >> 8) & 0xff;
    data[i++] = (offset >> 16) & 0xff;
    data[i++] = length & 0xff;
    data[i++] = (length >> 8) & 0xff;
    data[i++] = (length >> 16) & 0xff;
    reader.sendISO144434TCL(data, 8, reply);
    if (reply.size() <= 0 || reply[0] != DesfireStatus::OK)
    {
        LOG_INFO("unable to read file from tag\n");
        return false;
    }

    buf.clear();
    if (reply.size() <= 1)
        return false;

    if (length > (reply.size() - 1) )
        length = reply.size() - 1;

    buf.append(&reply[1], length);

    return true;
}





