/**
    @file   SAM.h
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef SAM_H
#define SAM_H


#include "standard.h"
#include "ISAMTalker.h"
#include "ResponseAPDU.h"
#include "IKey.h"
#include "json/json.h"


namespace RFID
{


class SAM
{
public:
    SAM(ISAMTalker* comLink);
    ~SAM();

    // SAM methods (i.e. perform actions on or with SAM, no tag in field required)
    void init();
    void initRadio();
    void startRadio();
    void stopRadio();
    std::string getVersion();
    std::string getSerialNumber();
    std::string getProductionDate();
    void dumpKeys();

    bool authenticate();
    bool authenticate(IKey* key);
    void killAuthentication();
    void av2Mode();
    void changeKey(Json::Value&);
    void selectApp(uint32_t appId);
    void decrypt(ByteArray& encryptedData, uint8_t keyNo, uint8_t keyVersion, ByteArray& initVector, unsigned decryptedLength, ByteArray& decryptedData);

    // Methods used with all tags
    bool readTag(ByteArray& uid, bool& isDesfire, unsigned timeoutMs);

    // Methods used with Desfire
        // ISO 14443-4 interface methods (e.g. Desfire)
        void iso4_connect();
        void iso4_disconnect();
        bool iso4_isActiveCardPresent();
        void iso4_sendCommand(const uint8_t* payload, unsigned length);
        void iso4_sendCommand(Reader::ByteArray& msg) { iso4_sendCommand(msg.data(), msg.size()); }
        void iso4_readReply(Reader::ByteArray& reply, unsigned timeoutMs = 1000);

        // Desfire specific methods
        void authenticateDesfire(const ByteArray& uid, uint8_t keyNo, uint8_t keyVersion, uint8_t desfireKeyNo, bool useDiversity);

    // Methods used with Ultralight C
        // ISO 14443-3 interface methods (e.g. Ultralight C)
        void iso3_sendCommand(const uint8_t* payload, unsigned length);
        void iso3_sendCommand(Reader::ByteArray& msg) { iso3_sendCommand(msg.data(), msg.size()); }
        void iso3_readReply(Reader::ByteArray& reply, unsigned timeoutMs = 1000);

        // Ultralight C specific methods
        void authenticateULC(uint8_t keyNumber, uint8_t keyVersion);
        void readULC(Reader::ByteArray& addresses, Reader::ByteArray& reply);


    // Radio commands
        void readRadioRegisters(ByteArray& addresses, ByteArray& values);
        void writeRadioRegisters(ByteArray& addresses, ByteArray& values);

private:   // methods
    IKey* generateDesSessionKey(const ByteArray& rndA, const ByteArray& rndB);
    IKey* generateAes128SessionKey(const ByteArray& rndA, const ByteArray& rndB);

    void sendCommand(uint8_t ins, uint8_t p1, uint8_t p2, const uint8_t* data, unsigned length, int le);
    void readReply(ResponseAPDU& reply, unsigned timeoutMs = 1000);
    void readVersionInfo();

    void generateDiversity(const ByteArray& uid, bool AES, ByteArray& buf);
    void authenticateHost(uint8_t keyNumber, uint8_t keyVersion);
    void loadInitVector(ByteArray& initVector);
    void decryptData(const uint8_t* cipherData, unsigned cipherDataLen, int plainTextLength, const uint8_t* crcData, unsigned crcDataLen, ByteArray& decryptedData);

private:   // data
    ISAMTalker* _comLink;
    IKey* _sessionKey;
    bool _haveRadio;

    bool _haveInfo;
    std::string _version;
    std::string _serialNumber;
    std::string _productionDate;
};

}

#endif // SAM_H
