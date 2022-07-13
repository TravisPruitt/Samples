/**
    @file SAM.cpp
    @author Roy Sprowl/Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#include "SAM.h"
#include "log.h"
#include "CommandAPDU.h"
#include "T1Frame.h"
#include "RFIDExceptions.h"
#include "SAMKey.h"
#include "SAMEnums.h"
#include "ByteArray.h"
#if 0
#include "FrameTypes.h"
#endif


using namespace RFID;

static const uint8_t PCB_SEQ = 0x40;

#define SAM_TIMEOUT 500         // milliseconds



SAM::SAM(IReader* reader, unsigned char slot) :
    _reader(reader),
    _slot(slot)
{
    _cla = 0x80;
    _pcb = 0x00;
}

SAM::~SAM()
{
}


/**
    Iniitialize the SAM and read its keys

    @throw  RFIDException on error
*/
void SAM::init()
{
    sleep();            // TODO - this should not be necessary
    getVersion();
    readKeys();
}
//
//
//KeyType::Enum SAM::getKeyType(unsigned keyNo)
//{
//    if (keyNo > ArrayLength(keys))
//        return KeyType::Unspecified;
//    else
//        return keys[keyNo].KeyType();
//}
//
//
//uint8_t SAM::getKeyVersion(unsigned keyNo)
//{
//    if (keyNo > ArrayLength(keys))
//        return 0;
//    else
//        return keys[keyNo].KeyAVersion();
//}



bool SAM::getKey(SAMKey& key, unsigned keyNo)
{
    if (keyNo >= ArrayLength(_keys))
        return false;

    key = _keys[keyNo];
    return true;
}


bool SAM::getDesfireKey(SAMKey& key, unsigned appId, unsigned keyNo)
{
    for (unsigned i = 0; i < ArrayLength(_keys); ++i)
    {
        unsigned aid = _keys[i].desfireAID;
        if (aid == appId && _keys[i].desfireKey == keyNo)
        {
            key = _keys[i];
            return true;
        }
    }
    return false;
}


/**
    Read the SAM keys

    Get key entry command as documented in section 11.2.1.2 of MIFARE SAM AV2 product sheet.
*/
void SAM::readKeys()
{
    for (unsigned char i = 0; i < ArrayLength(_keys); i++)
    {
        LOG_DEBUG("Get SAM key entry #%d\n", i);
        CommandAPDU capdu(_cla, 0x64, i, 0x00, 0, 0, 0);
        ResponseAPDU rapdu = sendAPDU(capdu);
        if (rapdu.IsSuccess() == false)
        {
            throw SAMException("SAM failed to get key entry");
        }
        _keys[i].deserialize(rapdu.Data(), rapdu.DataLength());
        //if (keys[i].AID() == appDef.AID())
        //{
        //    CryptoKey k(keys[i].KeyType(), keys[i].KeyAVersion());
        //    appDef.AddKey(k);
        //}
        _keys[i].dump();
    }
}


/**
    @throw  SAMException if not successful
*/
void SAM::sleep()
{
    LOG_DEBUG("SAM Sleep\n");
    CommandAPDU cmdApdu(_cla, 0x51, 0x00, 0x00);
    ResponseAPDU rapdu = sendAPDU(cmdApdu);
    if (!rapdu.IsSuccess())
    {
        throw SAMException("Unable to sleep SAM");
    }
}


/**
    TODO - do something with version so we can display it.
           Refer to the NXP SAM doc for description of returned fields.
*/
void SAM::getVersion()
{
    LOG_DEBUG("Get SAM Version\n");
    CommandAPDU capdu(_cla, 0x60, 0x00, 0x00, 0, 0, 0);
    ResponseAPDU rapdu = sendAPDU(capdu);
    if (!rapdu.IsSuccess())
    {
        throw SAMException("SAM version command failed");
    }
}


/**
    Send an APDU (Application Protocol Data Unit) to the SAM

    The APDU gets wrapped in a T1 frame before being sent.
*/
ResponseAPDU SAM::sendAPDU(const CommandAPDU& capdu)
{
    ByteArray response;
    sendT1(capdu.Bytes(), capdu.Length(), response);
    ResponseAPDU rapdu(response);
    return rapdu;
}


/**
    Send a block of data using the T=1 protocol

    The T=1 protocol send the block of data in a T=1 frame.  The response is also received
    in a T=1 frame, but can be broken across multiple frames.  This function takes care
    of acking each frame and piecing the data from the multiple frames into a single
    ByteArray.
*/
void SAM::sendT1(const uint8_t* bytes, unsigned length, ByteArray& reply)
{
    T1Frame request((uint8_t)0x00, _pcb, bytes, length);
    _pcb ^= PCB_SEQ;

    T1Frame response = sendT1Frame(request);

    reply.append(response.INF(), response.LEN());

    while (response.IsChained())
    {
        uint8_t seq = (uint8_t)response.getPCB().SequenceNumber();
        seq ^= (uint8_t)FrameSequenceNumber::Odd;

        T1Frame ack = T1Frame::CreateACK((FrameSequenceNumber::Enum)seq);
        response = sendT1Frame(ack);

        reply.append(response.INF(), response.LEN());
    }
}


/**
    Send a T1 frame to the SAM
*/
T1Frame SAM::sendT1Frame(T1Frame& frame)
{
    ByteArray response;
    _reader->sendSAMT1Frame(_slot, SAM_TIMEOUT, frame.getData(), frame.getLength(), response);
    if (response.size() == 0)
    {
        throw SAMException("No response from SAM");
    }

    T1Frame result(&response[0], response.size());
    return result;
}


/**
    Send the SAM_SelectApplication command to the SAM.
*/
void SAM::selectApplication(unsigned aid)
{
    LOG_DEBUG("SAM select application\n");
    ByteArray bytes;
    bytes.append((uint8_t)aid);
    bytes.append((uint8_t)(aid >> 8));
    bytes.append((uint8_t)(aid >> 16));
    CommandAPDU capdu(_cla, 0x5A, 0x00, 0x00, bytes.data(), bytes.size());
    ResponseAPDU response = sendAPDU(capdu);
    if (!response.IsSuccess())
        throw SAMException("SAM failed to select application");
}

/**
    Second step in authentication, sends the SAM_AuthenticatePICC command to the SAM.

    The authentication handshake goes like this
    1) The other side encrypts a random number to create a "challenge token"
    2) This challenge token is sent to the SAM by this function
    3) SAM decrypts the challenge token and returns the decrypted challenge along with its
       own encrypted random number as a challenge token for the other side.
    4) The other side decrypts the challenge token from the SAM
    5) This decrypted challenge token is passed to the InternalAuthenticate() function
       so it can be passed to the SAM and verified.
*/
void SAM::externalAuthenticate(const UID& uid, 
                               uint8_t keyNo, 
                               uint8_t keyVersion, 
                               const uint8_t* challenge, 
                               unsigned int challengeLen, 
                               ByteArray& counterChallenge,
                               bool useDiversity)
{
    // Assemble the command APDU
    ByteArray bytes;
    bytes.append(keyNo);
    bytes.append(keyVersion);
    bytes.append(challenge, challengeLen);
    if (useDiversity)
    {
        ByteArray diversity;
        generateDiversity(uid, true, diversity);
        bytes.append(diversity);
    }

    uint8_t p1 = PICCAuthenticationMode::UseDESFireKeyNo;
    if (useDiversity)
        p1 |= PICCAuthenticationMode::DiversifyKeys;

    CommandAPDU capdu(_cla, 0x0A, p1, 0, bytes, 0);
    ResponseAPDU rapdu = sendAPDU(capdu);
    if (!rapdu.IsSuccess())
        throw SAMException("SAM failed to get counter-challenge");

    counterChallenge.clear();
    counterChallenge.append(rapdu.Data(), rapdu.DataLength());
}


void SAM::generateDiversity(const UID& uid, bool AES, ByteArray& buf)
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


/**
    Last step of the authication handshake.

    Sends the SAM_AuthenticatePICC command to the SAM to verify that the challenge token provided to
    other side was properly decrypted.
*/
void SAM::internalAuthenticate(const uint8_t* response, unsigned responseLen)
{
    CommandAPDU capdu(_cla, 0x0A, 0x00, 0x00, response, responseLen);
    ResponseAPDU rapdu = sendAPDU(capdu);
    if (!rapdu.IsSuccess())
        throw TagException("SAM authentification of RFID tag failed");
}


/**
    Generate a MAC

    MAC = Message Authentication Code
*/
#if 0
void SAM::generateMAC(const uint8_t* message, unsigned messageLen)
{
    uint8_t lfi = 0x00;

    CommandAPDU capdu(_cla, 0x7C, lfi, 0x00, message, messageLen, 0);
    ResponseAPDU rapdu = sendAPDU(capdu);
    if (!rapdu.IsSuccess())
        // TODO - is this a SAM exception or tag exception
        throw SAMException("SAM unable to generate MAC");
}
#endif

void SAM::decryptData(const uint8_t* cipherData, unsigned cipherDataLen, int plainTextLength, const uint8_t* crcData, unsigned crcDataLen, ByteArray& decryptedData)
{
    ByteArray bytes;
    //int lc = 3 + cipherData.Length + ((crcData != null) ? crcData.Length : 0);
    //byte[] data = new byte[lc];

    uint8_t ptl[3];
    //Serializer.SerializeInt24(data, plainTextLength, 0);
    Serializer::SerializeInt24(ptl, plainTextLength, 0);
    bytes.append(ptl, 3);
    bytes.append(cipherData, cipherDataLen);
    if (crcData != NULL)
        bytes.append(crcData, crcDataLen);
    //Buffer.BlockCopy(cipherData, 0, data, 3, cipherData.Length);
    //if (crcData != null) Buffer.BlockCopy(crcData, 0, data, 3 + cipherData.Length, crcData.Length);

    CommandAPDU capdu(_cla, 0xDD, 0x00, 0x00, bytes, 0);
    ResponseAPDU rapdu = sendAPDU(capdu);
    if (!rapdu.IsSuccess())
        throw TagException("SAM unable to decrypt data");

    decryptedData.resize(rapdu.DataLength());
    memcpy(&decryptedData[0], rapdu.Data(), decryptedData.size());
}



void SAM::authenticateHost(uint8_t keyNumber, uint8_t keyVersion)
{
    ByteArray payload;

    payload.append(keyNumber);
    payload.append(keyVersion);
    CommandAPDU capdu(_cla, 0xA4, 0x04, 0, payload, 0);
    ResponseAPDU rapdu = sendAPDU(capdu);
    if (!rapdu.IsSuccess())
        throw TagException("SAM unable to authenticate tag");
}



void SAM::loadInitVector(ByteArray& initVector)
{
    CommandAPDU capdu(_cla, 0x71, 0, 0, initVector, 0xff);
    ResponseAPDU rapdu = sendAPDU(capdu);
    if (!rapdu.IsSuccess())
        throw SAMException("SAM unable to load init vector");
}
