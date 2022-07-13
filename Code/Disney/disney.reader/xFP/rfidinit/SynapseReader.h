


#ifndef __SYNAPSE_READER_H
#define __SYNAPSE_READER_H


#include "SerialPort.h"
#include "ByteArray.h"
#include "ResponseAPDU.h"
#include "SAM.h"
#include "TDA8029.h"
#include "json/json.h"

using namespace Reader;

namespace RFID
{


//using namespace Reader;


// Knows how to do an Inventory command on the Feig reader and report results.
class SynapseReader
{
public:
    SynapseReader();
    ~SynapseReader();

    // Primary interface for initializing, reading tags and tag contents
    bool init(std::string& errorMsg, bool initRadio = true);
    bool readTag(ByteArray& uid, bool& isDesfire, unsigned timeoutMS);

    void getInfo(Json::Value& json);
    void dumpKeys();

    void provisionSam();
    void provisionSam(Json::Value&);

    SAM* getSAM() { return _sam; };
    
private:    // data
    TDA8029* _tda;
    SAM* _sam;
};

}

#endif
