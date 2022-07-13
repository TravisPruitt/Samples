/**
    @file   FeigReder.cpp
    @author Greg Strange
    @date   Sept 2011


    NOTE: These functions are not thread safe.  It is up to the caller to insure that
    two Feig functions are not called at the same time.
*/

#include "standard.h"
#include "FeigConstants.h"
#include "FeigReader.h"
#include "log.h"
#include "ticks.h"
#include "FeigConstants.h"
#include "DesfireConstants.h"
#include "RFIDExceptions.h"

#include <fcntl.h>
#include <string>
#include <sstream>
#include <stdlib.h>

using namespace Reader;
using namespace RFID;


#ifdef _WIN32
#define PORT_NAME    "\\\\.\\COM24"
#else
#define PORT_NAME    "/dev/ttyO0"
#endif



// inter-byte tmieout when receiving messages from Feig
#define INTER_BYTE_TIMEOUT    200

#define SAM_SLOTS       4
#define SAM_BAUD_RATE   SAMBaudRate::baud307200
#define SAM_TIMEOUT     500     // timeout on SAM commands in milliseconds


#define ULTRALIGHT_KEY_INDEX    0       // index of key used to authenticate ultralights


namespace ErrorSource { enum Enum {Reader, Tag, SAM}; }

struct StatusCode
{
    uint8_t code;
    ErrorSource::Enum source;
    const char* description;
};

static const StatusCode statusCodes[] = {
    // Transponder status
    {FeigStatus::NoTransponder, ErrorSource::Tag, "No tag"},
    {FeigStatus::DataFalse, ErrorSource::Tag, "Tag transmission error"},
    {FeigStatus::WriteError, ErrorSource::Tag, "Tag write error"},
    {FeigStatus::AddressError, ErrorSource::Tag, "Tag address error"},
    {FeigStatus::WrongTransponderType, ErrorSource::Tag, "Wrong tag type"},
    {FeigStatus::AuthentError, ErrorSource::Tag, "Tag authentification error"},
    {FeigStatus::GeneralError, ErrorSource::Tag, "Tag reply error"},
    {FeigStatus::RFCommError, ErrorSource::Tag, "Tag RF comm error"},
    {FeigStatus::DataBufferOverlow, ErrorSource::Tag, "Too many tags"},
    {FeigStatus::MoreData, ErrorSource::Tag, "Too many tags"},
    {FeigStatus::ISO15693Error, ErrorSource::Tag, "Tag ISO 15693 error"},
    {FeigStatus::ISO14443Error, ErrorSource::Tag, "Tag ISO 14443 error"},
    {FeigStatus::CryptoProcessingError, ErrorSource::Tag, "Tag crypto error"},

    // Parameter status
    {FeigStatus::EEPROMFailure, ErrorSource::Reader, "Reader EEPROM failure"},
    {FeigStatus::ParameterRangeError, ErrorSource::Reader, "Reader param range error"},

    // Interface status
    {FeigStatus::UnknownCommand, ErrorSource::Reader, "Reader unknown command"},
    {FeigStatus::LengthError, ErrorSource::Reader, "Reader length error"},
    {FeigStatus::CommandUnavailable, ErrorSource::Reader, "Reader command unavailable"},

    // Reader status
    {FeigStatus::HardwareWarning, ErrorSource::Reader, "Reader FW incompatible with HW"},

    // SAM status
    {FeigStatus::NoSAM, ErrorSource::SAM, "No SAM"},
    {FeigStatus::SAMNotActivated, ErrorSource::SAM, "SAM not activated"},
    {FeigStatus::SAMAlreadyActivated, ErrorSource::SAM, "SAM already activated"},
    {FeigStatus::ProtocolNotSupported, ErrorSource::SAM, "SAM does not support protocol"},
    {FeigStatus::SAMCommError, ErrorSource::SAM, "SAM comm error"},
    {FeigStatus::Timeout, ErrorSource::SAM, "SAM timeout"},
    {FeigStatus::UnsupportedSAMBaudRate, ErrorSource::SAM, "SAM does not support baud rate"}
};



/**
    Constructor
*/
FeigReader::FeigReader() : 
        _haveVersionInfo(false),
        _sam(NULL)
{
    _port = new SerialPort(PORT_NAME);
}

/**
    Destructor
*/
FeigReader::~FeigReader()
{
    if (_sam)    delete _sam;
    if (_port)   delete _port;
}


/**
    Get the Feig firmware version
*/
const char* FeigReader::getFirmwareVersion()
{
    return _haveVersionInfo ? _firmwareVersion.c_str() : "unknown";
}

/**
    Get the description returned by the Feig get version command
*/
const char* FeigReader::getDescription()
{
    return _haveVersionInfo ? _description.c_str() : "unknown";
}


/**
     Initialize the reader

     @param samRequired true if a SAM is required, false otherwise.

     @return    true on success, false if any errors occurred during initialization.
                If an error occurs, then errorMsg holds an description of the error.

*/
bool FeigReader::init(bool samRequired, std::string& errorMsg)
{
    // The first byte of each row is a configuration parameter index
    // Read the Feig "System Manual CPR44.02-4SUSB.pdf" for a description of everything else
    static uint8_t configParams[7][15] = {
        {0x01, 0x00, 0x00, 0x08, 0x01, 0x00, 0x00, 0x00, 0x14, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
        {0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0x00, 0x00, 0x00, 0x00},
        {0x03, 0x00, 0x10, 0x00, 0x0D, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x9A},
        {0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
        {0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0d, 0x00, 0x00},
        {0x06, 0x02, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x05, 0x04, 0x00},
        {0x07, 0x02, 0x20, 0x2C, 0x01, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}
    };

    bool success = true;

    // Initialize the serial port
    if (!_port->init(38400, 'e'))
    {
        errorMsg = "Unable to initialize RFID reader serial port";
        LOG_ERROR(errorMsg.c_str());
        return false;
    }

    // Verify we can talk to the Feig
    try
    {
        const static uint8_t baudCheckMessage[] = { Command::BaudRateDetection, 0x00};
        sendSPFrame(baudCheckMessage, 2);
        FeigReply reply = readReply(Command::BaudRateDetection, 250);

        // check length, message number, and status
        if ( reply.status != FeigStatus::OK || reply.size() != 0)
        {
            char buf[200];
            sprintf(buf, "RFID reader baud rate check failed (0x%02x) - %s", reply.status, getStatusDescription(reply.status));
            errorMsg = buf;
            LOG_WARN(errorMsg.c_str());
            success = false;
        }
    }
    catch (ReaderException e)
    {
        errorMsg = e.what();
        LOG_ERROR(e.what());
        success = false;
    }

    // This delay is necessary between messages to the Feig
    // TODO - is this still necessary?
    sleepMilliseconds(5);

    // Get the Feig version information
    try
    {
        if (!readVersionInfo())
        {
            errorMsg = "RFID reader - unable to read firmware version";
            LOG_ERROR(errorMsg.c_str());
            success = false;
        }
        _haveVersionInfo = true;
    }
    catch (ReaderException e)
    {
        errorMsg = e.what();
        LOG_ERROR(e.what());
        success = false;
    }

    // TODO - is this still necessary?
    sleepMilliseconds(5);

    // Configure the Feig parameters and initialize the SAM
    // TODO - a single failure should not abort this sequence, should pick up and try next step
    try
    {
        // Configure the Feig parameters
        LOG_DEBUG("Set Feig configuration\n");
        for (unsigned i = 0; i < ArrayLength(configParams); ++i)
        {
            sendAPFrame(Command::WriteConfig, configParams[i], 15);
            readReply(Command::WriteConfig, 2000);
        }

        // Initialize the SAMs
        if (!initSams() && samRequired)
        {
            errorMsg = "No SAM installed";
            LOG_ERROR(errorMsg.c_str());
            success = false;
        }

        // Load  the reader key for the Ultralight C tags
        writeKeys();
    }
    catch (RFIDException e)
    {
        errorMsg = e.what();
        LOG_ERROR(e.what());
        success = false;
    }

    // Tune the antenna
    tuneAntenna();

    return success;
}



void FeigReader::writeKeys()
{
    LOG_DEBUG("Load reader key");
    static const uint8_t key[] = { 0x8A, 0x9E, 0xE2, 0x68, 0x8E, 0xA2, 0xDE, 0xA6, 0xB0, 0x8C, 0xBE, 0x32, 0x3C, 0xAF, 0x0E, 0xFE };
    ByteArray msg;
    msg.append(0);                      // mode = store in RAM
    msg.append(ULTRALIGHT_KEY_INDEX);   // key index
    msg.append(5);                      // key type = AES
    msg.append(sizeof(key));            // length
    msg.append(key, sizeof(key));       // key
    sendAPFrame(Command::WriteUltralightKey, msg.data(), msg.size());
    FeigReply reply = readReply(Command::WriteUltralightKey, 500);
    if (reply.status != FeigStatus::OK)
        throwStatusException(reply.status);
}



/**
    Turn reading of repeats on or off.  If repeats is off, then a tag must leave the RFID
    field and re-enter before it will be read a second time.

    @param  which   true to read repeats, false to not
    @throw  RFIDException on error
*/
void FeigReader::readRepeats(bool which)
{
    const unsigned ConfigLength = 14;

    // Read the existing configuration settings for config address 5
    uint8_t address = 5;
    sendAPFrame(Command::ReadConfig, &address, 1);
    FeigReply reply = readReply(Command::ReadConfig, 2000);
    if (reply.status != FeigStatus::OK)
        throwStatusException(reply.status);

    if (reply.size() != ConfigLength)
        throw ReaderException("Feig configuration returned unexpected length");

    // Set or clear the read once bit
    uint8_t buf[ConfigLength+1];
    buf[0] = address;

    memcpy(buf+1, &reply[0], ConfigLength);
    if (which)
        buf[12] &= 0xfe;
    else
        buf[12] |= 0x01;

    // Send the updated configuration bytes
    sendAPFrame(Command::WriteConfig, buf, 15);
    reply = readReply(Command::WriteConfig, 2000);

    if (reply.status != FeigStatus::OK)
        throwStatusException(reply.status);
}





/**
    Read a tag if one is within range

    @param tag        RFIDTag structure to fill in with information on the tag

    @param timeoutMs  Maximum amount of time to wait.  This timeout is handled by the Feig
                      so our processor is idle while waiting for the reply.  Setting this
                      value to 0 will cause the Feig to return the result immediately.


    @return     true if one tag is found, false if no tags or multiple tags are found.

    @throw      Exception on error

    NOTE: If multiple tags are detected in the same message, neither is returned.  In our
    application, this seems an appropriate response.  The user will have to tap again with
    a single card or band.
*/
bool FeigReader::readTag(UID& uid, bool& isDesfire, unsigned timeoutMs)
{
    // Format and send the command with the proper timeout
    size_t cmdLength;
    uint8_t cmd[5];
    cmd[0] = Command::Inventory;

    if (timeoutMs)
    {
        cmd[1] = InventoryMode::Notification;
        cmd[2] = timeoutMs / 100;
        cmdLength = 3;
    }
    else
    {
        cmd[1] = InventoryMode::Default;
        cmdLength = 2;
    }
    sendAPFrame(Command::ISOCmd, cmd, cmdLength);

    FeigReply reply = readReply(Command::ISOCmd, timeoutMs + 500);

    // no tag
    if (reply.status == FeigStatus::NoTransponder)
    {
        return false;
    }

    // check status byte, 0 = OK
    if (reply.status != FeigStatus::OK)
    {
        throwStatusException(reply.status);
    }

    if (reply.size() < 1)
    {
        throw ReaderException("Feig reply too short");
    }

    // check UID count
    if (reply[0] == 0)
    {
        return false;
    }
    else if (reply[0] > 1)
    {
        LOG_INFO("multiple tags detected\n");
        return false;
    }

    if (reply.size() < 2)
    {
        throw ReaderException("Feig reply too short");
    }

    // check tag type
    if (reply[1] != 4)
    {
        LOG_INFO("unknown tag type %02x\n", reply[1]);
        return false;
    }

    // get MUID length
    unsigned uidLength = (reply[2] & 4) ? 10 : 7;

    if (reply.size() < uidLength + 3)
    {
        throw ReaderException("Feig reply too short");
    }

    // grab the MUID
    for (unsigned i = 0; i < uidLength; ++i)
        uid.push_back(reply[i + 4]);

    // Determine if the tag is part 4 compliant (e.g. Desfire)
    isDesfire = ((reply[2] & 0x20) != 0);

    return true;
}



/**
    Select an RFID tag

    @throw   RFIDException on error
*/
bool FeigReader::select(UID& uid)
{
    uint8_t cmd[100];
    cmd[0] = Command::Select;
    cmd[1] = (SelectMode::Addressed | SelectMode::UIDLengthSpecified);
    cmd[2] = uid.size();
    for (unsigned i = 0; i < uid.size(); ++i)
        cmd[i+3] = uid[i];
    sendAPFrame(Command::ISOCmd, cmd, uid.size() + 3);
    FeigReply reply = readReply(Command::ISOCmd, 500);

    if (reply.status == FeigStatus::OK)
        return true;
    else if (reply.status == FeigStatus::NoTransponder)
        return false;

    throwStatusException(reply.status);
}



/******************************************************************************
                                SAM methods
******************************************************************************/


/**
    Send a message to a SAM and receive the reply

    NOTE: It is left to the caller to check the validity of the T1 frame received.
    This should include checking the length against the number of bytes received
    and checking the CRC.
*/
void FeigReader::sendSAMT1Frame(uint8_t slot, unsigned timeoutMs, const uint8_t* payload, unsigned length, ByteArray& response)
{
    ByteArray msg;
    msg.append(Command::SAM_T1);    // SAM sub-command
    msg.append(0);                  // mode (always 0)
    msg.append(payload, length);
    sendSAMCommand(slot, timeoutMs, msg.data(), msg.size());
    FeigReply reply = readReply(Command::SAMCmd, 500);
    if (reply.status != FeigStatus::OK && reply.status != FeigStatus::SAMAlreadyActivated)
        throwStatusException(reply.status);

    for (unsigned i = 0; i < reply.size(); ++i)
        response.push_back(reply[i]);
}


/**
    Send a message to a SAM
*/
void FeigReader::sendSAMCommand(uint8_t slot, unsigned timeoutMs, const uint8_t* payload, unsigned length)
{
    ByteArray msg;
    msg.append(slot);
    // timeout is in 0.1 seconds
    unsigned int timeout = (unsigned int)((timeoutMs + 51) / 100.0);
    if (timeout > 255)
        timeout = 255;
    if (timeout < 1)
        timeout = 1;
    msg.append((uint8_t)timeout);
    //_bytes[6] = (byte)Math.Max(1, Math.Min(255, Math.Round(timeoutMS / 100.0)));

    msg.append(payload, length);

    // There are two 0 bytes tacked on the end of the data
    // I don't know why (Roy)
    msg.append(0);
    msg.append(0);
    sendAPFrame(Command::SAMCmd, msg.data(), msg.size());
}




/**
    Initialize the SAMs.

    @return    true on success, false if no SAM found
    @throw     SAMException on problems with SAM
    @throw     FeigException on Feig communication errors

    The Feig board has 4 SAM slots.  We cycle power on the slots until we find one
    with a SAM and then use that SAM.  The assumption is that only one SAM will
    be installed and it is the SAM we want.
*/
bool FeigReader::initSams()
{
    unsigned slot;
    
    // turn off all of the SAM slots
    for (slot = 0; slot < SAM_SLOTS; ++slot)
        deactivateSam(slot);

    // activate SAM slots until we find one with a SAM
    for (slot = 0; slot < SAM_SLOTS; ++slot)
        if (activateSam(slot))
            break;

//  slot = SAM_SLOTS;
    if (slot >= SAM_SLOTS)
        return false;

    LOG_DEBUG("Using SAM in slot %d\n", slot);
    _sam = new SAM(this, slot);
    _sam->init();
    return true;
}


/**
    Deactivate a SAM - removes power from the SAM

    @param  slot    Slot to deactivate

    @return true on success, false otherwise

    @throw  RFIDException on communication errors
*/
bool FeigReader::deactivateSam(uint8_t slot)
{
    LOG_DEBUG("Deactivate SAM\n");
    uint8_t bytes[2];
    bytes[0] = Command::SAM_Activate;  // SAM Activation sub-command
    bytes[1] = SAMActivationMode::Deactivate;
    sendSAMCommand(slot, SAM_TIMEOUT, bytes, 2);
    FeigReply reply = readReply(Command::SAMCmd, SAM_TIMEOUT);
    
    if (reply.status == FeigStatus::OK || reply.status == FeigStatus::NoSAM || reply.status == FeigStatus::SAMNotActivated)
        return true;

    LOG_WARN("Deactivate SAM %d failed (0x%02x) - %s", slot, reply.status, getStatusDescription(reply.status));
    return false;
}



/**
    Activate a SAM

    @param  slot    Slot to activate

    @return true on success, false otherwise

    @throw  RFIDException on communication errors
*/
bool FeigReader::activateSam(uint8_t slot)
{
    LOG_DEBUG("Activate SAM\n");
    uint8_t bytes[3];
    bytes[0] = Command::SAM_Activate;  // SAM Activation sub-command
    bytes[1] = SAMActivationMode::ActivateT1 | SAMActivationMode::SpecifyBaud;
    bytes[2] = SAM_BAUD_RATE;
    sendSAMCommand(slot, SAM_TIMEOUT, bytes, 3);
    FeigReply reply = readReply(Command::SAMCmd, SAM_TIMEOUT);

    if (reply.status == FeigStatus::OK || reply.status == FeigStatus::SAMAlreadyActivated)
    {
        return true;
    }
    else if (reply.status == FeigStatus::NoSAM)
    {
        return false;
    }
    else
    {
        LOG_WARN("Activate SAM %d failed (0x%02x) - %s", slot, reply.status, getStatusDescription(reply.status));
        return false;
    }
}



/******************************************************************************
                            Desfire methods
******************************************************************************/



/**
    send an ISO 14443-4 T=CL message to a RFID tag and return the reply.

    On the Feig, ISO 14443-4 T=CL messages have the form:
    Desfire commands have the form:
        B2, BE, 81, payload

    where
        B2 = ISO 14443 Special Host Command
        BE = ISO 14443 T=CL command
        81 = mode bits
        payload = the message

    Thre reply has the form:

    byte 0 = PSTAT.  0x02 indicates that the Desfire reply is in the data bytes
    byte 1, 2 = BLK_CNT.  An incrementing counter that we ignore, but can be used to serialize replies.
    byte 3... = reply message

    See the Feig CPR44.02-4CUSB operations manual, section 6.3.4 for more info.
*/
void FeigReader::sendISO144434TCL(const uint8_t* msg, unsigned length, ByteArray& buf)
{
    ByteArray frame;
    frame.append(Command::ISO1443TCL);
    frame.append(CommandMode::First | CommandMode::INF);
    frame.append(msg, length);
    sendAPFrame(Command::ISO1443SpecialHostCmd, frame.data(), frame.size());
    FeigReply reply = readReply(Command::ISO1443SpecialHostCmd, 500);

    // NOTE: We pass through ISO14443 Errors because because there will be additional error
    // information in the ISO 14443 reply.
    if (reply.status != FeigStatus::OK && reply.status != FeigStatus::ISO14443Error)
    {
        throwStatusException(reply.status);
    }

    if (reply.size() < 3)
    {
        throw TagException("reply from RFID tag too short");
    }

    if (reply[0] != 2)
    {
        throw TagException("unexpected status in reply from RFID tag");
    }

    unsigned replyLength = reply.size() - 3;
    buf.resize(replyLength);
    if (replyLength > 0)
        memcpy(&buf[0], &reply[3], replyLength);
}




void FeigReader::authenticateTag(UID& uid, unsigned aid, uint8_t keyNo, bool useDiversity)
{
    ByteArray msg;

    if (_sam == NULL)
        throw SAMException("No SAM installed");

    SAMKey key;
    if (!_sam->getKey(key, keyNo))
    {
        throw TagException("Requested key not on SAM");
    }

    KeyType::Enum keyType = key.getKeyType();
    uint8_t keyVersion = key.keyAVersion;

    _sam->selectApplication(aid);

    LOG_DEBUG("Get challenge token from tag\n");

    uint8_t cmd;
    switch (keyType)
    {
    case KeyType::TDES_DESFire4:
        cmd = DesfireCommand::GetChallengeTokenDES_DESFire4;
        break;

    case KeyType::TDES_16bitCRC:
    case KeyType::TDES_32bitCRC:
    case KeyType::TDES3Key:
        cmd = DesfireCommand::GetChallengeTokenDES;
        break;

    case KeyType::AES:
        cmd = DesfireCommand::GetChallengeTokenAES;
        break;

    default:
        // key type not supported
        throw TagException("Key type not supported");
    }

    msg.clear();
    msg.append(cmd);
    msg.append(key.desfireKey);

    ByteArray reply;
    sendISO144434TCL(&msg[0], msg.size(), reply);
    if (reply.size() < 2 || reply[0] != DesfireStatus::ADDITIONAL_FRAME)
    {
        throw TagException("Unable to get challenge token from tag");
    }

    // Process challenge token and generate counter challenge
    LOG_DEBUG("Process challenge token and generate counter challenge\n");
    ByteArray counterChallenge;
    _sam->externalAuthenticate(uid, key.desfireKey, keyVersion, &reply[1], reply.size()-1, counterChallenge, useDiversity);

    // Issue counter challenge to PICC
    LOG_DEBUG("Issue counter challenge to PICC\n");

    msg.clear();
    msg.append(DesfireCommand::More);
    msg.append(&counterChallenge[0], counterChallenge.size());
    sendISO144434TCL(&msg[0], msg.size(), reply);
    if (reply.size() <= 1 || reply[0] != DesfireStatus::OK)
    {
        throw TagException("counter challenged failed on tag");
    }
    
    // Validate counter challenge response
    LOG_DEBUG("Validate counter challenge response\n");
    _sam->internalAuthenticate(&reply[1], reply.size()-1);
}


#if 0
void FeigReader::generateMAC(const uint8_t* message, unsigned messageLen)
{
    if (_sam == NULL)
        throw SAMException("No SAM installed");

    _sam->generateMAC(message, messageLen);
}


void FeigReader::decryptData(const uint8_t* encryptedData, unsigned encryptedDataLen, int plainTextLength, const uint8_t* crcData, unsigned crcDataLen, ByteArray& decryptedData)
{
    if (_sam == NULL)
        throw SAMException("No SAM installed");

    _sam->decryptData(encryptedData, encryptedDataLen, plainTextLength, crcData, crcDataLen, decryptedData);
}
#endif


/******************************************************************************
                ISO Standard Host Commands (e.g. Ultralight C)
******************************************************************************/

/**
    The reply from the Feig comes in the form

    byte 0 = number of blocks read
    byte 1 = size of each block
    bytes 3.. = data blocks where each block is

        byte n = status byte
        byte n.. = data bytes (size given in byte 1)
*/
void FeigReader::readBlocks(uint8_t blockNum, uint8_t numBlocks, ByteArray& buf)
{
    ByteArray msg;
    msg.append(Command::ReadMultipleBlocks);
    msg.append(0x02);       // mode = read from selected chip
    msg.append(blockNum);
    msg.append(numBlocks);
    sendAPFrame(Command::ISOCmd,  msg.data(), msg.size());
    FeigReply reply = readReply(Command::ISOCmd, 500);
    if (reply.status != FeigStatus::OK)
        throwStatusException(reply.status);

    if (reply.size() < 2)
        throw TagException("Tag data too short");

    if (reply[0] != numBlocks)
        throw TagException("Invalid read of tag");

    uint8_t blockSize = reply[1];
    if (reply.size() < (unsigned)((numBlocks * (blockSize + 1)) + 2))
        throw TagException("Tag data too short");


    buf.clear();
    unsigned i = blockSize + 2;
    for (unsigned block = 0; block < numBlocks; ++block)
    {
        // The bytes in each block are reversed
        buf.append(reply[i]);
        buf.append(reply[i-1]);
        buf.append(reply[i-2]);
        buf.append(reply[i-3]);
        i += blockSize + 1;     // skip SEC-STATUS byte at front of each block
    }
}


void FeigReader::authenticateUltralightC()
{
    ByteArray msg;
    msg.append(Command::AuthenticateUltralightC);   // authenticate ultralight C command
    msg.append(0x02);                               // mode = use selected tag
    msg.append(ULTRALIGHT_KEY_INDEX);               // key index
    sendAPFrame(Command::ISO1443SpecialHostCmd, msg.data(), msg.size());
    FeigReply reply = readReply(Command::ISO1443SpecialHostCmd, 500);
    if (reply.status != FeigStatus::OK)
        throwStatusException(reply.status);
}



void FeigReader::decrypt(ByteArray& encryptedData, uint8_t keyNo, uint8_t keyVersion, ByteArray& initVector, unsigned decryptedLength, ByteArray& decryptedData)
{
    if (_sam == NULL)
        throw SAMException("No SAM installed");

    _sam->authenticateHost(keyNo, keyVersion);
    _sam->loadInitVector(initVector);
    _sam->decryptData(encryptedData.data(), encryptedData.size(), decryptedLength, NULL, 0, decryptedData);
}




/******************************************************************************
                Low level Feig protocol methods
******************************************************************************/

static char formatNibble(uint8_t n)
{
    if (n <= 9)
        return n + '0';
    else
        return n - 10 + 'A';
}


static std::string formatBytes(const uint8_t* data, int length)
{
    std::string s;
    for (int i = 0; i < length; ++i)
    {
        s += formatNibble((data[i] >> 4) & 0x0f);
        s += formatNibble(data[i] & 0x0f);
        s += " ";
    }

    return s;
}



static uint16_t CalcCrc(uint16_t crc, const uint8_t* data, int length)
{
    for (int i = 0; i < length; ++i)
    {
        crc ^= data[i];
        for (int j = 0; j < 8; ++j)
        {
            if ( (crc & 0x0001) != 0)
                crc = (unsigned short)((crc >> 1) ^ 0x8408);
            else
                crc = (unsigned short)(crc >> 1);
        }
    }

    return crc;
}



/**
    Send an "Advanced Protocol" frame to the Feig

    The Feig accepts messages using either the "Standard Protocol" or "Advanced Protocol".
    Advanced protocol exchanges look like this:

    outgoing: <STX><2-byte-length><address><command><payload><2-byte-crc>
    response: <STX><2-byte-length><address><command><status><payload><2-byte-crc>
*/
void FeigReader::sendAPFrame(uint8_t cmd, const uint8_t* data, unsigned int dataLength)
{
    ByteArray frame;

    // Toss any lingering replies from old messages
    _port->flushInput();

    // start of packet byte
    frame.append(0x02);

    // length
    frame.append((uint8_t)((dataLength + 7) >> 8));
    frame.append((uint8_t)((dataLength + 7) & 0xff));

    // address (0xff is the broadcast address which we always use)
    frame.append(0xff);

    // The command
    frame.append(cmd);

    // payload
    frame.append(data, dataLength);

    // CRC
    uint16_t crc = CalcCrc(0xffff, frame.data(), frame.size());
    frame.append((uint8_t)(crc & 0xff));
    frame.append((uint8_t)((crc >> 8) & 0xff));

    LOG_TRAFFIC("->feig: %s\n", formatBytes(frame.data(), frame.size()).c_str());
    if (!_port->write(frame.data(), frame.size()))
        throw ReaderException("Unable to write to Feig");
}



/**
    Send a Feig "standard protocol" frame.

    The Feig accepts message using either the "standard protocol" or the "advanced protocol".
    The standard protocol looks like this:

    outgoing: <length><address><command><payload><2-byte-crc>
    response: <length><address><command><status><payload><2-byte-crc>
*/
void FeigReader::sendSPFrame(const uint8_t* payload, unsigned payloadLength)
{
    ByteArray frame;

    _port->flushInput();

    // Length
    frame.append(payloadLength + 4);

    // address (0xff is the broadcast address which we always use)
    frame.append(0xff);

    // payload
    frame.append(payload, payloadLength);

    // CRC
    uint16_t crc = CalcCrc(0xffff, frame.data(), frame.size());
    frame.append((uint8_t)(crc & 0xff));
    frame.append((uint8_t)((crc >> 8) & 0xff));

    LOG_TRAFFIC("->feig: %s\n", formatBytes(frame.data(), frame.size()).c_str());
    _port->write(frame.data(), frame.size());
}



/**
    Receive a command response from the Feig.  This function auto-detects whether the response
    is using the standard protocol or advanced protocol by whether the first byte is an STX.
    See sendSPFrame and sendAPFrame for the different response formats.  The primary difference
    between standard protocol and advanced protocol is that the advanced protocol starts with
    a STX, and has a 2 byte length instead of a 1 byte length.
*/
FeigReply FeigReader::readReply(uint8_t cmd, unsigned timeoutMs)
{
    int length = 0;
    uint8_t preamble[10];
    uint8_t crc[2];
    unsigned preambleLength = 0;

    // Read the first byte which will either be an STX (advanced protocol) or length (standard protocol)
    if (!_port->readByte(preamble, timeoutMs))
    {
        throw ReaderException("Feig timeout or read error");
    }
    ++preambleLength;

    if (preamble[0] == 0x02)
    {
        // For standard protcol, read 2 byte length
        if (!_port->readByte(preamble+preambleLength++, INTER_BYTE_TIMEOUT) || !_port->readByte(preamble+preambleLength++, INTER_BYTE_TIMEOUT))
        {
            throw ReaderException("Feig timeout");
        }
        unsigned totalExpected = preamble[1] * 256 + preamble[2];
        length = totalExpected - 8;
    }
    else
    {
        length = preamble[0] - 6;
    }

    // Read the address, command byte and status byte
    for (int i = 0; i < 3; ++i)
    {
        if (!_port->readByte(preamble+preambleLength++, INTER_BYTE_TIMEOUT))
        {
            throw ReaderException("Feig timeout or read error");
        }
    }

    // Construct the reply with the reply status
    FeigReply reply(preamble[preambleLength - 1]);

    // Read the payload bytes
    for (int i = 0; i < length; i++)
    {
        uint8_t b;
        if (!_port->readByte(&b, INTER_BYTE_TIMEOUT))
        {
            throw ReaderException("Feig timeout or read error");
        }
        reply.append(b);
    }

    // Read the CRC bytes
    if (!_port->readByte(crc, INTER_BYTE_TIMEOUT) || !_port->readByte(crc+1, INTER_BYTE_TIMEOUT))
    {
        throw ReaderException("Feig timeout or read error");
    }

    LOG_TRAFFIC("<=feig: %s%s%s\n", 
        formatBytes(preamble, preambleLength).c_str(), 
        reply.size() > 0 ? formatBytes(&reply[0], reply.size()).c_str() : "",
        formatBytes(crc, 2).c_str());

    // Verify correct CRC
    uint16_t calculatedCrc = CalcCrc(0xffff, preamble, preambleLength);
    if (reply.size() > 0)
        calculatedCrc = CalcCrc(calculatedCrc, &reply[0], reply.size());
    if (calculatedCrc != ((crc[1] << 8) | crc[0]))
    {
        throw ReaderException("Feig CRC error, calculated %04x, received %04x", calculatedCrc, ((crc[1] << 8) | crc[0]));
    }

    // Verify that the received command == the sent command
    if (preamble[preambleLength - 2] != cmd)
    {
        throw ReaderException("Feig reply command does not match sent command");
    }

    return reply;
}



/******************************************************************************
                            Everytghing else
******************************************************************************/

bool FeigReader::readVersionInfo()
{
    sendAPFrame(Command::GetSoftwareVersion, NULL, 0);

    // Get the reply
    FeigReply reply = readReply(Command::GetSoftwareVersion, 200);

    if (reply.size() < 7)
    {
        LOG_WARN("Feig unexpected reply length, got %d, expected 9\n", reply.size());
        return false;
    }

    // check status byte, 0 = OK
    if (reply.status != FeigStatus::OK)
    {
        LOG_WARN("Read version info failed (0x%02x) - %s", reply.status, getStatusDescription(reply.status));
        return false;
    }

    char buf[50];

    // software version and rev
    sprintf(buf, "%d - %d", (reply[1] << 8) + reply[0], reply[2]);
    _firmwareVersion = buf;

    // description
    sprintf(buf, "Feig (%02x %02x %02x%02x)", reply[3], reply[4], reply[5], reply[6]);
    _description = buf;

    return true;
}


#define TUNE_PATH         "/sys/class/gpio/gpio70/value"
#define TUNE_DONE_PATH    "/sys/class/gpio/gpio72/value"
void FeigReader::tuneAntenna()
{
#ifndef _WIN32
    // TODO - setup GPIO here

    // toggle the auto-tune start output
    int fd = open(TUNE_PATH, O_WRONLY);
    if (fd < 0)
    {
        LOG_ERROR("unable to open '%s' for auto-tune output\n", TUNE_PATH);
        return;
    }
    write(fd, "0", 1);
    sleepMilliseconds(2);
    write(fd, "1", 1);
    close(fd);

    MILLISECONDS start = getMilliseconds();

    // Wait a bit for auto-tune to start
    sleepMilliseconds(50);

    // Wait for the auto-tune complete input
    int done = 0;
    while ( !done && (getMilliseconds() - start) < 3000)
    {
        char buf[50];

        FILE* file = fopen(TUNE_DONE_PATH, "r");
        if (file == NULL)
        {
            LOG_ERROR("unable to open '%s' for auto-tune complete input\n", TUNE_DONE_PATH);
            return;
        }
        fgets(buf, sizeof(buf)-1, file);
        fclose(file);
        done = atoi(buf);
    }
    if (!done)
        LOG_ERROR("Auto-tune complete never signaled\n");
    else
        LOG_DEBUG("Auto-tune took %d ms\n", getMilliseconds() - start);

#endif
}



/**
    Throw the correct type of an exception based on the status code returned by the Feig.
*/
void FeigReader::throwStatusException(uint8_t status)
{
    unsigned i;

    for (i = 0; i < ArrayLength(statusCodes); ++i)
        if (status == statusCodes[i].code)
            break;

    if (i >= ArrayLength(statusCodes))
    {
        throw ReaderException("Unknown reader status code 0x%02x", status);
    }
    else if (statusCodes[i].source == ErrorSource::Tag)
    {
        throw TagException("Feig status 0x%02x - %s", status, statusCodes[i].description);
    }
    else if (statusCodes[i].source == ErrorSource::SAM)
    {
        throw TagException("Feig status 0x%02x - %s", status, statusCodes[i].description);
    }
    else
    {
        throw ReaderException("Feig status 0x%02x - %s", status, statusCodes[i].description);
    }
}


const char* FeigReader::getStatusDescription(uint8_t status)
{
    unsigned i;

    for (i = 0; i < ArrayLength(statusCodes); ++i)
        if (status == statusCodes[i].code)
            break;

    if (i < ArrayLength(statusCodes))
        return statusCodes[i].description;
    else
        return "Unknown Feig status code";
}

