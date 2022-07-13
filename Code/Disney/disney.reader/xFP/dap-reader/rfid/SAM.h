/**
    @file   SAM.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef SAM_H
#define SAM_H


#include "standard.h"
#include "ResponseAPDU.h"
#include "CommandAPDU.h"
#include "T1Frame.h"
#include "IReader.h"
#include "SAMKey.h"

namespace RFID
{
//class DesfireApplication;




class SAM
{
public:
    SAM(IReader* reader, unsigned char slot);
    ~SAM();

    // Initialization
    void init();

    bool getKey(SAMKey& key, unsigned keyNo);
    void authenticateHost(uint8_t keyNumber, uint8_t keyVersion);
    void loadInitVector(ByteArray& initVector);

    void externalAuthenticate(const UID& uid, 
                              uint8_t keyNo, 
                              uint8_t keyVersion, 
                              const uint8_t* challenge, 
                              unsigned challengeLen, 
                              ByteArray& counterChallenge,
                              bool useDiversity);
    void internalAuthenticate(const uint8_t* response, unsigned responseLen);
    void generateMAC(const uint8_t* message, unsigned messageLen);
    void decryptData(const uint8_t* encryptedData, unsigned encryptedDataLen, int plainTextLength, const uint8_t* crcData, unsigned crcDataLen, ByteArray& decryptedData);

    // DESFire commands
    void selectApplication(unsigned aid);
    bool getDesfireKey(SAMKey& key, unsigned appId, unsigned keyNo);

private:   // data
    IReader* _reader;
    uint8_t _slot;
    uint8_t _cla;
    uint8_t _pcb;

    // The SAM holds 32 keys, but we only use 10
//    SAMKeyEntry keyEntries[32];
    SAMKey _keys[10];

private: //methods
    void sleep();
    void getVersion();
    void readKeys();
    void generateDiversity(const UID& uid, bool AES, ByteArray& buf);

    ResponseAPDU sendAPDU(const CommandAPDU& capdu);
    void sendT1(const uint8_t* data, unsigned length, ByteArray& response);
    T1Frame sendT1Frame(T1Frame& frame);
};

}

#endif // SAM_H
