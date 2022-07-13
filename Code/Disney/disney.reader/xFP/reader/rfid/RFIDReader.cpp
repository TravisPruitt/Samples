/**
    @file       RFIDReader.cpp
    @author     Greg Strange
    @date       Sept 2011

    Copyright (c) 2011, synapse.com
*/


#include "RFIDReader.h"
#include "EventLogger.h"
#include "ticks.h"
#include "log.h"
#include "json/json.h"
#include "DapConfig.h"
#include "Sound.h"
#include "LightEffects.h"
#include "Desfire.h"
#include "UltralightC.h"
#include "RFIDExceptions.h"
#include "DapReader.h"


using namespace RFID;
using namespace Reader;


RFIDReader::RFIDReader() : _inTestMode(false), _restartCount(0)
{
    _debounceTime = DapConfig::instance()->getValue("rfid debounce", 500);
    _desiredMode = DapConfig::instance()->getValue("rfid test loop", false);
    _testModeInterval = DapConfig::instance()->getValue("rfid test interval", 6500);
    _tapLight = DapConfig::instance()->getValue("tap light", "thinking");
    _tapLightTimeout = DapConfig::instance()->getValue("tap light timeout", 10000);
    _tapSound = DapConfig::instance()->getValue("tap sound", "");
    _publicIdEnabled = DapConfig::instance()->getValue("public id", true);
    _secureIdEnabled = DapConfig::instance()->getValue("secure id", true);
}


RFIDReader::~RFIDReader()
{
}


RFIDReader* RFIDReader::instance()
{
    static RFIDReader _instance;
    return &_instance;
}


void RFIDReader::getInfo(Json::Value& json)
{
    _reader.getInfo(json);
}


std::string RFIDReader::getLastUID()
{
    std::string id;

    _lastTagMutex.lock();
    if (_lastTag)
        std::string id = _lastTag->getUID();
    _lastTagMutex.unlock();
    return id;
}


void RFIDReader::postEvent(const RFIDTag* tag)
{
    // post the event
    Json::Value event;
    event["type"] = "RFID";
    event["uid"] = tag->getUID();

    if (_publicIdEnabled)
        event["pid"] = tag->getPublicId();

    if (_secureIdEnabled && tag->hasSecureId())
    {
        event["sid"] = tag->getSecureId();
        event["iin"] = tag->getIIN();
    }
    EventLogger::instance()->postEvent(event);
}


void RFIDReader::setTestMode(bool which)
{
    _desiredMode = which;
}


void RFIDReader::enablePublicId(bool which)
{
    _publicIdEnabled = which;
    DapConfig::instance()->setValue("public id", which);
    DapConfig::instance()->save();
}


void RFIDReader::enableSecureId(bool which)
{
    _secureIdEnabled = which;
    DapConfig::instance()->setValue("secure id", which);
    DapConfig::instance()->save();
}


void RFIDReader::setTapLight(std::string light)
{
    _tapLight = light;
}


void RFIDReader::setTapLightTimeout(MILLISECONDS timeout)
{
    _tapLightTimeout = timeout;
}


void RFIDReader::setTapSound(const char* sound)
{
    _tapSound = sound;
}


/**
    Get the status of the RFID reader
*/
IStatus::Status RFIDReader::getStatus(std::string& msg)
{
    IStatus::Status status = StatusKeeper::getStatus(msg);
#if 0
    if (status != IStatus::Red && _secureIdEnabled && !_reader.hasSam())
    {
        status = IStatus::Red;
        msg = "No SAM installed";
    }
#endif
    return status;
}


/**
    RFID thread watches for RFID tag to appear and generates an event
    when one does.  The RFID reader is configured to only read "new"
    tags, so keeping a tag next to the reader will not generate multiple
    events.
*/
void RFIDReader::run()
{
    RFIDTag* tag = NULL;

    std::string errorMsg;
    if (!_reader.init(errorMsg))
    {
        // It is not uncommon for the first attempt at initializing the 8029 radio chip to fail
        // after a power up.  The problem does not seem to be time related.  I.e. waiting longer
        // before attempting to initialize it does not fix the problem.  Also, there seems to be
        // no need to wait before a retry, and the retry seems to always work.
        LOG_WARN(errorMsg.c_str());
        if (!_reader.init(errorMsg))
        {
            LOG_WARN(errorMsg.c_str());
            setStatus(IStatus::Red, "Unable to initialize RFID radio");
            return;
        }
    }


    bool restartRadio = false;
    while (!_quit)
    {
        try
        {
            if (_desiredMode != _inTestMode)
            {
                _inTestMode = _desiredMode;
//                _reader.readRepeats(_inTestMode);
//                sleepMilliseconds(5);
            }

            if (restartRadio)
            {
                _reader.getSAM()->stopRadio();
                _reader.getSAM()->startRadio();
                restartRadio = false;
            }
            else if (_inTestMode)
            {
                // Test mode is designed to allow a card to be taped to the reader and read
                // periodically.  Desfire cards however cannot be read a second time unless
                // they leave and re-enter the radio field. Cycling the power on the radio
                // takes care of this.
                _reader.getSAM()->stopRadio();
                sleepMilliseconds(_testModeInterval);
                _reader.getSAM()->startRadio();
            }

            ByteArray uid;
            bool isDesfire;
            if (_reader.readTag(uid, isDesfire, 1000))
            {
                MILLISECONDS readStart = getMilliseconds();
                if (tag)    
                {
                    delete tag;
                    tag = NULL;
                }

                if (isDesfire)
                {
                    tag = new Desfire(uid);
                    LOG_INFO("tap of Desfire %s\n", tag->getUID());
                }
                else
                {
                    tag = new UltralightC(uid);
                    LOG_INFO("tap of Ultralight %s\n", tag->getUID());
                }

                // Ignore (debounce) repeated taps from same RFID
                if ( ((getMilliseconds() - _lastRFIDTime) < _debounceTime) && (strcmp(_lastTag->getUID(), tag->getUID()) == 0) )
                {
                    _lastRFIDTime = getMilliseconds();
                    LOG_INFO("Tap ignored by debouncer");
                }
                else
                {
                    if (_publicIdEnabled || _secureIdEnabled)
                    {
                        try
                        {
                            tag->readIds(_reader.getSAM(), _secureIdEnabled);
                        }
                        catch (TagException e)
                        {
                            // If the desfire rails to read, it typically will fail subsequent reads unless it is
                            // removed and re-inserted in the radio field. We can do this ourselves by turning
                            // the radio off and back on.
                            if (isDesfire)
                                restartRadio = true;
                            throw;
                        }
                    }

                    if (_tapSound.size() > 0)
                        Sound::instance()->play(_tapSound.c_str());

                    if (!_tapLight.empty())
                        LightEffects::instance()->tap(_tapLight.c_str(), _tapLightTimeout);

                    postEvent(tag);

                    // save time stamp of when tag was read
                    _lastRFIDTime = getMilliseconds();

                    // update reader tap stats
                    DapReader::instance()->recordTapReadTime(_lastRFIDTime - readStart);

                    // save the tag as the last tag read
                    _lastTagMutex.lock();
                    if (_lastTag)    delete _lastTag;
                    _lastTag = tag;
                    tag = NULL;
                    _lastTagMutex.unlock();

                    clearStatus();
                }
            }
        }
        catch (TagException e)
        {
            // Tag exceptions are logged, but do not change our status from 'green'
            LOG_INFO(e.what());

            // Delay to prevent a recurring error from hogging the processor and flooding the log
            sleepMilliseconds(500);
        }
        catch (RFIDException e)
        {
            // All other exceptions change our status to 'red'
            LOG_ERROR(e.what());
            setStatus(IStatus::Red, "RFID radio error");

            // We have encountered occasions where the NXP will stop responding and a complete
            // re-initialization of the radio software often fixes the problem.
            LOG_INFO("re-initializing radio");
            _reader.shutdown();

            sleepMilliseconds(2000);

            if (!_reader.init(errorMsg))
            {
                // If we can't re-init the radio, then we're really out of luck
                LOG_WARN(errorMsg.c_str());
                setStatus(IStatus::Red, "Unable to initialize RFID radio");
                return;
            }
            ++_restartCount;
        }
    }

    try
    {
        _reader.getSAM()->stopRadio();
    }
    catch (RFIDException e)
    {
        LOG_ERROR("error stopping RFID reader - e.what()");
    }
}




