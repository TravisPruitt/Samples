
#include "standard.h"
#include "SynapseReader.h"
#include "log.h"
#include "ByteArray.h"
#include "ResponseAPDU.h"
#include "RFIDExceptions.h"
#include "DesKey.h"
#include "AES128Key.h"
#include "JsonParser.h"
#include "SAMMasterKey.h"
#include <string>

using namespace Reader;
using namespace RFID;


static const unsigned AppId = 0x78e127;



SynapseReader::SynapseReader() : 
    _tda(NULL), 
    _sam(NULL)
{
}


SynapseReader::~SynapseReader()
{
    if (_sam)
    {
        delete _sam;

        try
        {
            _tda->turnOffSAM();
        }
        catch (RFIDException e)
        {
            LOG_WARN("Caught exception %s while turning off SAM", e.what());
        }
    }

    if (_tda)
        delete _tda;
}

bool SynapseReader::init(std::string& errorMsg, bool initRadio)
{
    try
    {
        // Initialize the TDA8029
        _tda = new TDA8029();
        _tda->init();

        // Initialize the SAM
        if (!_tda->haveSAM())
        {
            errorMsg = "No SAM installed";
            return false;
        }
        _tda->turnOnSAM();
        _sam = new SAM(_tda);
        _sam->init();
        if (initRadio)
            _sam->initRadio();
    }
    catch (RFIDException e)
    {
        errorMsg = e.what();
        return false;
    }

    return true;
}


void SynapseReader::getInfo(Json::Value& json)
{
    if (_tda)
        json["TDA8029 FW version"] = _tda->getFirmwareVersion();

    if (_sam)
    {
        json["SAM FW version"] = _sam->getVersion();
        json["SAM sn"] = _sam->getSerialNumber();
        json["SAM mfg date"] = _sam->getProductionDate();
    }
}


void SynapseReader::dumpKeys()
{
    if (_sam)
        _sam->dumpKeys();
    else
        LOG_INFO("No SAM detected");
}



void SynapseReader::provisionSam(Json::Value& value)
{
    if (value.isMember("SAMFormatDefinition"))
        value = value["SAMFormatDefinition"];

    if (value.isMember("SymmetricKeys"))
        value = value["SymmetricKeys"];

    if (!value.isMember("SAMKeyEntry"))
    {
        LOG_DEBUG("No key entries found");
        return;
    }

    value = value["SAMKeyEntry"];

    SAMMasterKey masterKey;
    if (!_sam->authenticate(&masterKey))
    {
        //LOG_INFO("Failed to authenticate SAM, assuming virgin SAM and trying default key");
        //SAMVirginKey virginKey;
        //if (!_sam->authenticate(&virginKey))
        throw SAMException("Unable to authenticate SAM");
    }

    if (value.isArray())
    {
        int numKeys = value.size();
        for (int i = 0; i < numKeys; ++i)
        {
            int keyNumber;
            if (!JsonParser::parseInt(value[i], "KeyNumber", keyNumber))
                LOG_WARN("Missing or invalid 'KeyNumber' member in key definition");
            else if (keyNumber == 0)
                LOG_WARN("Key 0 is the SAM master key and will not be programmed");
            else
                _sam->changeKey(value[i]);
        }
    }
    else
    {
        _sam->changeKey(value);
    }
}


bool SynapseReader::readTag(ByteArray& uid, bool& isDesfire, unsigned timeoutMs)
{
    if (!_sam)
        throw SAMException("No SAM");

    return _sam->readTag(uid, isDesfire, timeoutMs);
}

