/**
    @file SAM.cpp
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#include "SAM.h"
#include "log.h"
#include "RFIDExceptions.h"
#include "ResponseAPDU.h"
#include "DesKey.h"
#include "AES128Key.h"
#include "SAMKey.h"
#include "SAMMasterKey.h"

#include <stdlib.h>
#include <string>
#include <sstream>


using namespace RFID;


// This is the number of keys that we use on the SAM.  The SAM can store many more
// keys than this.
static const unsigned NUM_KEYS = 10;


namespace Command { enum Enum {
    GetVersion = 0x60,
    RC_Init = 0xe5,
    RC_RFControl = 0xcf,

    ISO14443_3_ActivateIdle = 0x26,
    ISO14443_3_Exchange = 0x7e,

    ISO14443_4_PresenceCheck = 0x4c,
    ISO14443_4_RATS_PPS = 0xe0,
    ISO14443_4_Exchange = 0xec,
    ISO14443_4_Deselect = 0xd4,

    SAM_LockUnlock = 0x10,
    SAM_AuthenticateHost = 0xa4,
    SAM_KillAuthentication = 0xca,
    SAM_GetKeyEntry = 0x64,
    SAM_ChangeKeyEntry = 0xc1,
    SAM_SelectApplication = 0x5a,
    SAM_LoadInitVector = 0x71,
    SAM_Decipher_Data = 0xdd,

    MF_READ = 0x30,

    DESFire_AuthenticatePICC = 0xda,
    ULC_AUthenticatePICC = 0x2c,

    RC_ReadRegister = 0xee,
    RC_WriteRegister = 0x1e
}; }




SAM::SAM(ISAMTalker* comLink) : _comLink(comLink), _sessionKey(NULL), _haveRadio(false), _haveInfo(false)
{
}

SAM::~SAM()
{
    // turn off the radio
    try
    {
        if (_haveRadio)
        {
            LOG_TRAFFIC("turn off radio");
            ResponseAPDU reply;

            uint8_t rfcontrol[] = {0x00, 0x00};    // delays 1ms before turning on power
            sendCommand(Command::RC_RFControl, 0, 0, rfcontrol, sizeof(rfcontrol), -1);
            readReply(reply);
        }
    }
    catch (RFIDException e)
    {
        LOG_WARN("Caught exception %s while turning off radio", e.what());
    }

    if (_sessionKey)
        delete _sessionKey;
}



/**************************************************************************************************

                                            SAM Methods

**************************************************************************************************/

/**
    Iniitialize the SAM and read its keys

    @throw  RFIDException on error
*/
void SAM::init()
{
    readVersionInfo();
}

void SAM::initRadio()
{
    ResponseAPDU reply;

    // p1 bits:
    //      Use default register set (bits 0-2)
    //      Connected to 663 host interface (bit 6, 0x40)
    sendCommand(Command::RC_Init, 0x40, 0, NULL, 0, -1);
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Failed to init radio, returned status: %02xh %02xh", reply.sw1(), reply.sw2());

    startRadio();
    _haveRadio = true;
}

void SAM::startRadio()
{
    ResponseAPDU reply;

    LOG_TRAFFIC("Start radio");

    // turn on RF
    uint8_t rfcontrol[] = {0x00, 0x01};    // delays 1ms before turning on power
    sendCommand(Command::RC_RFControl, 0, 0, rfcontrol, sizeof(rfcontrol), -1);
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Failed to turn on radio, returned status: %02xh %02xh", reply.sw1(), reply.sw2());
}

void SAM::stopRadio()
{
    ResponseAPDU reply;

    LOG_TRAFFIC("Stop radio");

    // turn on RF
    uint8_t rfcontrol[] = {0x00, 0x00};
    sendCommand(Command::RC_RFControl, 0, 0, rfcontrol, sizeof(rfcontrol), -1);
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Failed to turn off radio, returned status: %02xh %02xh", reply.sw1(), reply.sw2());
}



void fillWithRandomBytes(ByteArray& bytes)
{
    for (size_t i = 0; i < bytes.size(); ++i)
        bytes[i] = (uint8_t)rand();
}


/**
    Authenticate with the SAM master key
*/
bool SAM::authenticate()
{
    SAMMasterKey key;
    return authenticate(&key);
}



/**
    Authenciate with a given key.

    Works for AES128 and DES_CRC16. Probably need to add more cases for others if they are needed.
*/
bool SAM::authenticate(IKey* key)
{
    ByteArray rndA;
    unsigned rndLength = 8;
    if (key->getType() == KeyType::AES128 || key->getType() == KeyType::AES192)
        rndLength = 16;
    rndA.resize(rndLength);
    fillWithRandomBytes(rndA);

    ResponseAPDU reply;

    LOG_TRAFFIC("Authenticating with SAM");

    // Generate challenge token
    uint8_t payload1[] = {0, 0};
    sendCommand(Command::SAM_AuthenticateHost, 0, 0, payload1, sizeof(payload1), 0);
    readReply(reply);
    if (!reply.isContinue())
    {
        LOG_INFO("Authenticate step 1 failed, response %02xh %02xh", reply.sw1(), reply.sw2());
        return false;
    }

    // decrypt the challenge token
    ByteArray rndB = key->decrypt(reply.data(), reply.size());

    // Create the decrypted counter challenge
    ByteArray concat;
    concat.append(rndA);
    rndB.rotateLeft();
    concat.append(rndB);
    rndB.rotateRight();

    // Encrypt the counter challenge
    ByteArray counter = key->encrypt(concat.data(), concat.size());

    sendCommand(Command::SAM_AuthenticateHost, 0, 0, counter.data(), counter.size(), 0);
    readReply(reply);
    if (!reply.isSuccess())
    {
        LOG_INFO("Authenticate step 2 failed, response %02xh %02xh", reply.sw1(), reply.sw2());
        return false;
    }
    if (reply.size() != rndLength)
    {
        LOG_INFO("Authenicate step 2 failed, counter challenge size wrong, expected %d, got %d", counter.size(), reply.size());
        return false;
    }

    ByteArray compare = key->decrypt(reply.data(), rndB.size());
    compare.rotateRight();
    if (compare != rndA)
    {
        LOG_INFO("Counter challenge response != original random number");
        return false;
    }

    if (_sessionKey)
    {
        delete _sessionKey;
        _sessionKey = NULL;
    }
    if (key->getType() == KeyType::AES128)
        _sessionKey = generateAes128SessionKey(rndA, rndB);
    else
        _sessionKey = generateDesSessionKey(rndA, rndB);

    return true;
}





void SAM::changeKey(Json::Value& json)
{
    if (!json.isMember("KeyNumber"))
    {
        LOG_WARN("No 'KeyNumber' member in key definition");
        return;
    }
    Json::Value value = json["KeyNumber"];
    int keyNumber;
    if (value.isInt())
        keyNumber = value.asInt();
    else if (value.isString())
    {
        std::string s = value.asString();
        if (!isdigit(s[0]))
        {
            LOG_WARN("'KeyNumber' member is not a number");
            return;
        }
        keyNumber = atoi(s.c_str());
    }

    if (keyNumber < 0 || keyNumber >= (int)NUM_KEYS)
    {
        LOG_WARN("Invalid 'KeyNumber'");
        return;
    }
    LOG_DEBUG("Key number %d:", keyNumber);

    SAMKey key;
    key.parse(json);
    key.dump();

    ResponseAPDU reply;
    ByteArray encrypted = key.encode(*_sessionKey);
    sendCommand(Command::SAM_ChangeKeyEntry, keyNumber, 0xff, encrypted.data(), encrypted.size(), -1);
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Unable to change key, response %02xh, %02xh", reply.sw1(), reply.sw2());
}




/**
    Check if there is a tag within range.

    @param  uid         Set to UID of the tag if a tag is detected and read
    @param  isDesfire   True if the tag is a Desfire, false otherwise
    @param  timeoutMs   Timeout in milliseconds

    @return true if a tag is detected and read, false otherwise
*/
bool SAM::readTag(ByteArray& uid, bool& isDesfire, unsigned timeoutMs)
{
//    LOG_DEBUG("ISO14443 Activate Idle command");
    ResponseAPDU reply;

    // activate idle
    // NOTE: doc says timeout is in milliseconds, but it seems significantly slower than that
    ByteArray payload;
    payload.append(timeoutMs & 0xff);
    payload.append(timeoutMs >> 8);
    sendCommand(Command::ISO14443_3_ActivateIdle, 1, 0, payload.data(), payload.size(), 0);
    readReply(reply, timeoutMs + 10000);
    if (reply.isNoCard())
    {
        return false;
    }

    if (!reply.isOk())
        throw SAMException("Activate idle command failed, returned status: %02xh %02xh", reply.sw1(), reply.sw2());

    if (reply.size() < 4 || (reply.size() < (unsigned)(reply[3] + 4) ) )
        throw SAMException("Activate idle command response too short");

    // ATQA = Answer to Request.  There are some proprietory bits in the ATQA that NXP uses to
    // differentiate between desfire and Ultralight.  Specifically
    //    Desfire ATQA = 0x0344
    //    Ultralight C ATQA = 0x0044
    // Therefore, we could have used these bytes to differentiate between desfire and ultralight
    // but we use the ISO14443-4 compliant bit in the SAK instead.  Perhaps this makes our software 
    // more likely to work with new future chips supplied by NXP.
//    uint16_t atqa = (reply[1] << 8) + reply[0];

    // SAK = Select Acknowlege.  Only a couple of bits in this have any meaning.  See ISO14443-3 document
    // for details.  The bit we care about is 0x20 which tells whether the tag is ISO14443-4 compliant.
    // To us, that tells us whether we should treat it as a Desfire or an Ultralight C.
    uint8_t sak = reply[2];

    isDesfire = ((sak & 0x20) != 0);
    
    // grab the UID
    uid.clear();
    for (unsigned i = 4; i < reply.size(); ++i)
        uid.append(reply[i]);

    return true;
}



void SAM::decrypt(ByteArray& encryptedData, uint8_t keyNo, uint8_t keyVersion, ByteArray& initVector, unsigned decryptedLength, ByteArray& decryptedData)
{
    authenticateHost(keyNo, keyVersion);
    loadInitVector(initVector);
    decryptData(encryptedData.data(), encryptedData.size(), decryptedLength, NULL, 0, decryptedData);
}



void SAM::killAuthentication()
{
    LOG_TRAFFIC("SAM_KillAuthentication");

    sendCommand(Command::SAM_KillAuthentication, 0, 0, NULL, 0, -1);
    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Unable to kill authentication, response %02xh %02xh", reply.sw1(), reply.sw2());
}


/******************************************************************************
                    ISO 14443-3 interface methods
******************************************************************************/

void SAM::iso3_sendCommand(const uint8_t* payload, unsigned length)
{
    sendCommand(Command::ISO14443_3_Exchange, 0, 0, payload, length, 0);
}


void SAM::iso3_readReply(Reader::ByteArray& reply, unsigned timeoutMs)
{
    ResponseAPDU apdu;
    readReply(apdu, timeoutMs);
    if (!apdu.isOk())
        throw TagException("Unable to complete command with tag, response %02xh %02xh", apdu.sw1(), apdu.sw2());

    reply.clear();
    reply.append(apdu.data(), apdu.size());
}


/******************************************************************************
                        Ultralight C Methods
******************************************************************************/


void SAM::authenticateULC(uint8_t keyNumber, uint8_t keyVersion)
{
    LOG_TRAFFIC("Authenticate ULC");

    ByteArray payload;
    payload.append(keyNumber);
    payload.append(keyVersion);
    sendCommand(Command::ULC_AUthenticatePICC, 0, 0, payload.data(), payload.size(), -1);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw TagException("Unable to authenticate tag, response %02xh %02xh", reply.sw1(), reply.sw2());
}

void SAM::readULC(ByteArray& addresses, ByteArray& data)
{
    LOG_TRAFFIC("Read ULC");

    sendCommand(Command::MF_READ, 0, 0, addresses.data(), addresses.size(), 0);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw TagException("Unable to read from tag, response %02xh %02xh", reply.sw1(), reply.sw2());

    data.clear();
    data.append(reply.data(), reply.size());
}


/******************************************************************************
                            ISO 14443-4 Methods
******************************************************************************/



bool SAM::iso4_isActiveCardPresent()
{
    ResponseAPDU reply;

    LOG_TRAFFIC("Presence check");

    sendCommand(Command::ISO14443_4_PresenceCheck, 0, 0, NULL, 0, -1);
    readReply(reply);
    if (reply.sw1() == 0x90 && reply.sw2() == 0xe0)
    {
        LOG_TRAFFIC("No active card");
        return false;
    }
    else if (!reply.isOk())
        throw TagException("Presence check failed, response %02xh %02xh", reply.sw1(), reply.sw2());

    return true;
}

void SAM::iso4_connect()
{
    ResponseAPDU reply;

    LOG_TRAFFIC("Connect tag");

    // rats PPS command
    // Second two bytes of data set the radio bit rate.  0,0 sets it to 106kbit which is the
    // slowest choice. Setting speed to the fastest choice of 848kbits shaves 10ms off the time
    // required to read secure data from a desfire, but decreases the read range.
    uint8_t data2[] = {0, 0, 0};
    sendCommand(Command::ISO14443_4_RATS_PPS, 0, 0, data2, sizeof(data2), 0);
    readReply(reply);
    if (!reply.isOk())
        throw TagException("Unable to negotiate connection with tag, response %02xh %02xh", reply.sw1(), reply.sw2());
}


void SAM::iso4_disconnect()
{
    ResponseAPDU reply;

    LOG_TRAFFIC("Disconnect tag");

    sendCommand(Command::ISO14443_4_Deselect, 1, 0, NULL, 0, -1);
    readReply(reply);

    // Return codes for this command are ignored because it is expected this command will be called
    // even with no card selected as a safeguard
}


void SAM::iso4_sendCommand(const uint8_t* payload, unsigned length)
{
    sendCommand(Command::ISO14443_4_Exchange, 0, 0, payload, length, 0);
}


void SAM::iso4_readReply(Reader::ByteArray& reply, unsigned timeoutMs)
{
    ResponseAPDU apdu;
    readReply(apdu, timeoutMs);
    if (!apdu.isOk())
        throw TagException("Unable to complete command with tag, response %02xh %02xh", apdu.sw1(), apdu.sw2());

    reply.clear();
    reply.append(apdu.data(), apdu.size());
}

/******************************************************************************
                            Desfire Methods
******************************************************************************/

void SAM::authenticateDesfire(const ByteArray& uid, uint8_t keyNo, uint8_t keyVersion, uint8_t desfireKeyNo, bool useDiversity)
{
    LOG_TRAFFIC("Authenticating the desfire tag");

    ByteArray payload;
    payload.append(desfireKeyNo);
    payload.append(keyNo);
    payload.append(keyVersion);
    if (useDiversity)
    {
        ByteArray diversity;
        generateDiversity(uid, true, diversity);
        payload.append(diversity);
    }
    sendCommand(Command::DESFire_AuthenticatePICC, useDiversity ? 1 : 0, 0, payload.data(), payload.size(), 0);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw TagException("Authentificatio with tag failed, response %02xh %02xh", reply.sw1(), reply.sw2());
}




/******************************************************************************
                            Radio methods
******************************************************************************/



void SAM::readRadioRegisters(ByteArray& addresses, ByteArray& values)
{
    sendCommand(Command::RC_ReadRegister, 0, 0, addresses.data(), addresses.size(), 0);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Failed to read radio registers, response %02xh %02xh", reply.sw1(), reply.sw2());

    values.append(reply.data(), reply.size());
}



void SAM::writeRadioRegisters(ByteArray& addresses, ByteArray& values)
{
    ByteArray payload;

    unsigned count = addresses.size() <= values.size() ? addresses.size() : values.size();
    for (unsigned i = 0; i < count; ++i)
    {
        payload.append(addresses[i]);
        payload.append(values[i]);
    }
    sendCommand(Command::RC_WriteRegister, 0, 0, payload.data(), payload.size(), -1);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Failed to write radio registers, response %02xh %02xh", reply.sw1(), reply.sw2());
}





/******************************************************************************
                    Methods for communicating with SAM
******************************************************************************/


/**
    The 8029 firmware provides a command for sending commands to the SAM.  We have to package
    the SAM commands up in proper APDUs an the 8029 firmware takes care of packaging the APDU
    into a T=1 frame if necessary (and in our case it is necessary).

    APDU stands for Application Protocol Data Unit and are described in ISO 7816-3 and in the
    SAM documentation.  In short, an outgoing APDU has the following form...

    <cla><ins><p1><p2>[<length><data>][<le>]

    where

    <cla> = class byte.  In our case it will always be 0x80.  See SAM Product Data Sheet section
            7.3 for why)

    <ins> = SAM instruction.  Documented in SAM Product Data Sheet.

    <p1>, <p2> = parameter 1 and parameter 2. Value depends on command.  See SAM documentation.

    <length><data> = If there is data to be passed, it goes here.

    <le> = Expected length of return APDU.  The correct value for this is also documented for each
           command in the SAM documentation.  If le is < 0, then it is not transmitted.
*/
void SAM::sendCommand(uint8_t ins, uint8_t p1, uint8_t p2, const uint8_t* data, unsigned length, int le)
{
    ByteArray msg;

    msg.append(0x80);       // class byte (cla)
    msg.append(ins);        // instruction byte (ins)
    msg.append(p1);         // parameter 1
    msg.append(p2);         // parameter 2
    if (length)
    {
        msg.append(length);
        msg.append(data, length);
    }

    if (le >= 0)
        msg.append(le);

    _comLink->sendSAMCommand(msg.data(), msg.size());
}


/**
    Read the response from the SAM.  SAM responses come in "Response Application Protocol Data Units"
    or RAPDU as documented in ISO 7816-3.  An RAPDU looks like this:

    [<data>]<sw1><sw2>

    <sw1> and <sw2> are two status bytes.  The possible values are documented in the SAM documentation.
*/
void SAM::readReply(ResponseAPDU& reply, unsigned timeoutMs)
{
    _comLink->readSAMReply(reply, timeoutMs);
    if (!reply.isValid())
        throw SAMException("SAM response too short");
}



/**************************************************************************************************
                                        Private Methods
**************************************************************************************************/




/**
    Retrieve and parse versio info from the SAM
*/
void SAM::readVersionInfo()
{
    ResponseAPDU reply;

    sendCommand(Command::GetVersion, 0, 0, NULL, 0, 0);
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Failed to get SAM version, returned status: %02xh %02xh", reply.sw1(), reply.sw2());

    if (reply.size() < 29)
        throw SAMException("SAM response too short");

    std::ostringstream s;
    s << (int)reply[10] << '.' << (int)reply[11];
    _version = s.str();

    _serialNumber = StringLib::formatBytes(reply.data() + 14, 7, "");

    s.str("");
    s << (int)reply[27] << '/' << (int)reply[26] << '/' << (int)reply[28];
    _productionDate = s.str();

    _haveInfo = true;
}


void SAM::dumpKeys()
{
    for (unsigned i = 0; i < NUM_KEYS; ++i)
    {
        LOG_INFO("SAM key #%d\n", i);

        ResponseAPDU reply;
        sendCommand(Command::SAM_GetKeyEntry, i, 0, NULL, 0, 0);
        readReply(reply);
        if (!reply.isOk())
            throw SAMException("Unable to read key %d, response: %02xh %02xh", i, reply.sw1(), reply.sw2());

        SAMKey key;
        key.unpack(reply);
        LOG_DEBUG("key %d:\n", i);
        key.dump();
    }
}


IKey* SAM::generateDesSessionKey(const ByteArray& rndA, const ByteArray& rndB)
{
    ByteArray keyBytes;

    if (rndA.size() < 4 || rndB.size() < 4)
        throw BugException("Cannot generate key, random number too short");

    keyBytes.append(rndA.data(), 4);
    keyBytes.append(rndB.data(), 4);
    return new DesKey(keyBytes.data(), keyBytes.size());
}
    
    
IKey* SAM::generateAes128SessionKey(const ByteArray& rndA, const ByteArray& rndB)
{
    ByteArray keyBytes;

    if (rndA.size() < 16 || rndB.size() < 16)
        throw BugException("Cannot generate key, random number too short");


    keyBytes.append(rndA.data(), 4);
    keyBytes.append(rndB.data(), 4);
    keyBytes.append(rndA.data() + 12, 4);
    keyBytes.append(rndB.data() + 12, 4);
    return new AES128Key(keyBytes.data(), keyBytes.size());
}


void SAM::selectApp(uint32_t appId)
{
    LOG_TRAFFIC("Select app on SAM");

    ByteArray payload;
    payload.append(appId & 0xff);
    payload.append((appId >> 8) & 0xff);
    payload.append((appId >> 16) & 0xff);
    sendCommand(Command::SAM_SelectApplication, 0, 0, payload.data(), payload.size(), -1);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw TagException("Select App failed, response %02xh %02xh", reply.sw1(), reply.sw2());
}

void SAM::generateDiversity(const ByteArray& uid, bool AES, ByteArray& buf)
{
    int repeats = AES ? 2 : 1;
    while (repeats--)
    {
        // start with a 0x88
        buf.push_back(0x88);

        // add 7 bytes of the RFID tag ID
        unsigned i;
        unsigned length = uid.size();
        for (i = 0; i < 7 && i < length; ++i)
            buf.push_back(uid[i]);
        while (i++ < 7)
            buf.push_back(0);
    }
}



void SAM::authenticateHost(uint8_t keyNumber, uint8_t keyVersion)
{
    LOG_TRAFFIC("SAM_AuthenticateHost");

    ByteArray payload;
    payload.append(keyNumber);
    payload.append(keyVersion);
    sendCommand(Command::SAM_AuthenticateHost, 4, 0, payload.data(), payload.size(), 0);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Unable to authenticate for decryption, response %02xh %02xh", reply.sw1(), reply.sw2());
}



void SAM::loadInitVector(ByteArray& initVector)
{
    LOG_TRAFFIC("SAM_LoadInitVector");

    sendCommand(Command::SAM_LoadInitVector, 0, 0, initVector.data(), initVector.size(), -1);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Unable to laod init vector, response %02xh %02xh", reply.sw1(), reply.sw2());
}


void SAM::decryptData(const uint8_t* cipherData, unsigned cipherDataLen, int plainTextLength, const uint8_t* crcData, unsigned crcDataLen, ByteArray& decryptedData)
{
    LOG_TRAFFIC("SAM_Decrypt_Data");

    ByteArray payload;
    //int lc = 3 + cipherData.Length + ((crcData != null) ? crcData.Length : 0);
    //byte[] data = new byte[lc];

    payload.append(plainTextLength & 0xff);
    payload.append( (plainTextLength >> 8) & 0xff );
    payload.append( (plainTextLength >> 16) & 0xff );
    payload.append(cipherData, cipherDataLen);
    if (crcData != NULL)
        payload.append(crcData, crcDataLen);
    //Buffer.BlockCopy(cipherData, 0, data, 3, cipherData.Length);
    //if (crcData != null) Buffer.BlockCopy(crcData, 0, data, 3 + cipherData.Length, crcData.Length);

    sendCommand(Command::SAM_Decipher_Data, 0, 0, payload.data(), payload.size(), 0);

    ResponseAPDU reply;
    readReply(reply);
    if (!reply.isOk())
        throw SAMException("Unable to laod decrypt data, response %02xh %02xh", reply.sw1(), reply.sw2());

    decryptedData.clear();
    decryptedData.append(reply.data(), reply.size());
}


std::string SAM::getVersion()
{
    return _haveInfo ? _version : "";
}


std::string SAM::getSerialNumber()
{
    return _haveInfo ? _serialNumber : "";
}

std::string SAM::getProductionDate()
{
    return _haveInfo ? _productionDate : "";
}
