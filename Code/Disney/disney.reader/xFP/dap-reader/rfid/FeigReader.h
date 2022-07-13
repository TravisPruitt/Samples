/**
    @file   FeigReader.h
    @author Greg Strange
    @date   Sept 19, 2011
*/



#ifndef __FEIG_READER_H
#define __FEIG_READER_H

#include <string.h>
#include <stdint.h>
#include <string>
#include "SerialPort.h"
#include "StatusKeeper.h"
#include "SAM.h"
#include "IReader.h"
#include "RFIDTag.h"

namespace RFID
{


using namespace Reader;


class FeigReply;

// Knows how to do an Inventory command on the Feig reader and report results.
class FeigReader : public IReader
{
public:
    FeigReader();
    ~FeigReader();

    // Primary interface for initializing, reading tags and tag contents
    bool init(bool samRequired, std::string& errorMsg);
    bool readTag(UID& uid, bool& isDesfire, unsigned timeoutMS);
    bool select(UID& uid);

    // Feig firmware version and description
    const char* getFirmwareVersion();
    const char* getDescription();
    bool hasSam()   { return _sam != NULL; }

    // Configuration option
    void readRepeats(bool);

    // Methods used by sams and desfire classes
//    bool readDesfireAppKeys(DesfireApplication& app);
    void sendSAMT1Frame(uint8_t slot, unsigned timeoutMs, const uint8_t* payload, unsigned length, ByteArray& response);

    void sendISO144434TCL(const uint8_t* msg, unsigned length, ByteArray& reply);
    void authenticateTag(UID& uid, unsigned aid, uint8_t keyNo, bool useDiversity);
    void generateMAC(const uint8_t* message, unsigned messageLen);
    void decryptData(const uint8_t* encryptedData, unsigned encryptedDataLen, int plainTextLength, const uint8_t* crcData, unsigned crcDataLen, ByteArray& decryptedData);


    // ISO Standard Host commands (e.g. Ultralight C)
    void readBlocks(uint8_t blockNum, uint8_t numBlocks, ByteArray& buf);
    void authenticateUltralightC();
    void decrypt(ByteArray& encryptedData, uint8_t keyNo, uint8_t keyVersion, ByteArray& initVector, unsigned decryptedLength, ByteArray& decryptedData);


private:    // private methods
    void sendSAMCommand(uint8_t slot, unsigned timeoutMs, const uint8_t* payload, unsigned length);

    // Low level protocol functions
    void sendAPFrame(uint8_t cmd, const uint8_t* payload, unsigned payloadLength);
    void sendSPFrame(const uint8_t* payload, unsigned length);
    FeigReply readReply(uint8_t cmd, unsigned timeoutMs);

    // Helper functions
    void readFile(uint8_t fileNo, unsigned offset, unsigned length, ByteArray& bytes);

    bool readVersionInfo();
    void writeKeys();

    void tuneAntenna();
    bool initSams();
    bool deactivateSam(uint8_t slot);
    bool activateSam(uint8_t slot);

    void throwStatusException(uint8_t status);
    const char* getStatusDescription(uint8_t status);

private:    // data
    bool _haveVersionInfo;
    SerialPort* _port;
    std::string _firmwareVersion;
    std::string _description;
    SAM* _sam;
};


class FeigReply : public Reader::ByteArray
{
public:
    FeigReply(uint8_t replyStatus) : status(replyStatus) {}

    uint8_t status;
};


}

#endif
