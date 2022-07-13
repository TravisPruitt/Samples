/**
    @file   Desfire.cpp
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/


#include "standard.h"
#include "Desfire.h"
#include "log.h"
//#include "DesfireConstants.h"
#include "RFIDExceptions.h"


using namespace RFID;



static const unsigned AppId = 0x78e127;
static const unsigned PublicFile = 1;
static const unsigned SecureFile = 2;



/// DESFire Native Protocol Command Values
namespace Command { enum Enum 
{
    GetChallengeTokenDES_DESFire4 = 0x0A,
    GetChallengeTokenDES = 0x1A,
    GetChallengeTokenAES = 0xAA,

    SelectAID = 0x5A,       

    GetKeySettings = 0x45,
    ChangeKeySettings = 0x54,

    ChangeKey = 0xC4,

    GetKeyVersion = 0x64,

    CreateApplication = 0xCA,
    DeleteApplication = 0xDA,

    GetAIDs = 0x6A,
    GetDFNames = 0x6D,
       
    FormatPICC = 0xFC,

    GetVersion = 0x60,
    FreeMemory = 0x6E,

    SetConfiguration = 0x5C,

    GetUID = 0x51,

    GetFileIDs = 0x6F,
    GetISOFileIDs = 0x61,

    GetFileSettings = 0xF5,
    ChangeFileSettings = 0x5F,

    CreateStdFile = 0xCD,
    CreateBackupFile = 0xCB,
    CreateValueFile = 0xCC,
    CreateLinearLog = 0xC1,
    CreateCyclicLog = 0xC0,

    DeleteFile = 0xDF,

    ReadData = 0xBD,
    WriteData = 0x3D,

    GetValue = 0x6C,
    Credit = 0x0C,
    Debit = 0xDC,
    LimitedCredit = 0x1C,

    WriteRecord = 0x3B,
    ReadRecords = 0xBB,
    ClearRecords = 0xEB,

    CommitTrans = 0xC7,
    AbortTrans = 0xA7
};}



namespace Status { enum Enum 
{
    OK = 0x00,
    NoChanges = 0x0C,
    OutOfEEPROM = 0X0E,
    IllegalCommand = 0X1C,
    IntegrityError = 0X1E,
    NoSuchKey = 0x40,
    LengthError = 0x7e,
    PermissionDenied = 0x9d,
    ParameterError = 0x9e,
    AppNotFound = 0xa0,
    AppIntegrityError = 0xa1,
    AuthenticationError = 0xae,
    AdditionalFrame = 0xaf,
    BoundaryError = 0xbe,
    PICCIntegrityError = 0xc1,
    CommandAborted = 0xca,
    PICCDisabled = 0xcd,
    CountError = 0xce,
    DuplicateError = 0xde,
    EEPROMError = 0xee,
    FileNotFound = 0xf0,
    FileIntegrityError = 0xf1
};}




Desfire::Desfire(ByteArray& uid) : RFIDTag(uid)
{
}


/**
    Read public and optionally secure IDs from a tag
*/
void Desfire::readIds(SAM* sam, bool readSecureId)
{
    try
    {
        sam->iso4_connect();
    }
    catch (RFIDException)
    {
        // If first connect failed, maybe a card got left connected
        sam->iso4_disconnect();
        sam->iso4_connect();
    }

    try
    {
        selectApp(sam);
        readPublicData(sam);
        if (readSecureId)
            readSecureData(sam);
    }
    catch (RFIDException ex)
    {
        sam->iso4_disconnect();
        throw;
    }

    sam->iso4_disconnect();
}



/**
    Read and parse the public data on a tag.

    @throw  TagException on error commnicating with tag, or error parsing data
    @throw  ReaderException on error communicating with reader
*/
void Desfire::readPublicData(SAM* sam)
{
    ByteArray buf;
    readFile(sam, PublicFile, 0, 16, buf);
    if (!parsePublicData(buf))
        throw TagException("Unable to parse public data");
}


/**
    Read and parse the secure data on a tag.

    @throw  TagException on error commnicating with tag
    @throw  ReaderException on error communicating with reader
    @throw  SAMException on error communicating with SAM
*/
void Desfire::readSecureData(SAM* sam)
{
    ByteArray buf;
    
    LOG_TRAFFIC("authenticate the tag");

    sam->authenticateDesfire(_uid, _authKeyNumber, _authKeyVersion, 1, _useDiversity);
    
    readFile(sam, SecureFile, 4, 20, buf);

    SecureData sd;
    if (!parseSecureData(buf, sd))
        throw TagException("Unable to parse secure data");

    // read the encrypted secure ID
    readFile(sam, SecureFile, 24, sd.cryptoLength, buf);

    // decrypt the secure ID
    ByteArray decryptedData;
    sam->decrypt(buf, sd.cryptoKeyNumber, sd.cryptoKeyVersion, sd.initVector, 28, decryptedData);

    // parse secure ID
    if (!parseSecureId(decryptedData))
        throw TagException("Unable to parse secure ID");
}



/**
    Select the application on the desfire tag
*/
void Desfire::selectApp(SAM* sam)
{
    LOG_TRAFFIC("select the app on the tag\n");
    ByteArray reply;
    ByteArray msg;
    msg.append(Command::SelectAID);
    msg.append((uint8_t)AppId);
    msg.append((uint8_t)(AppId >> 8));
    msg.append((uint8_t)(AppId >> 16));

    sam->iso4_sendCommand(msg);
    sam->iso4_readReply(reply);

    if (reply.size() <= 0)
        throw TagException("Reply too short");
    else if (reply[0] == Status::AppNotFound)
        throw TagException("No NGE app on tag");
    else if (reply[0] != Status::OK)
        throw TagException("Unable to select application on desfire tag, response %02xh", reply[0]);
}




/**
    @throw  BugException    for software bugs detected
    @throw  TagException    for communication errors with Tag
    @throw  SAMException    for errors communicating with SAM
    @throw  ReaderException for Feig reader errors
*/
void Desfire::readFile(SAM* sam, unsigned fileNo, unsigned offset, unsigned length, ByteArray& buf)
{
    ByteArray msg;
    ByteArray reply;

    // read the file
    LOG_TRAFFIC("read the file contents\n");

    msg.append(Command::ReadData);
    msg.append(fileNo);
    msg.append(offset & 0xff);
    msg.append((offset >> 8) & 0xff);
    msg.append((offset >> 16) & 0xff);
    msg.append(length & 0xff);
    msg.append((length >> 8) & 0xff);
    msg.append((length >> 16) & 0xff);
    sam->iso4_sendCommand(msg);
    sam->iso4_readReply(reply);

    if (reply.size() <= 1)
    {
        throw TagException("Tag reply too short");
    }
    else if (reply[0] != Status::OK)
    {
        throw TagException("read of public ID failed, response %02xh", reply[0]);
    }

    buf.clear();

    if (length > (reply.size() - 1) )
        length = reply.size() - 1;

    buf.append(&reply[1], length);
}





