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
#include "FeigReader.h"
#include "DapConfig.h"
#include "Sound.h"
#include "LightEffects.h"
#include "Desfire.h"
#include "UltralightC.h"
#include "DapReader.h"


using namespace RFID;
using namespace Reader;


RFIDReader::RFIDReader() : _inTestMode(false)
{
    _debounceTime = DapConfig::instance()->getValue("rfid debounce", 500);
    _desiredMode = DapConfig::instance()->getValue("rfid test loop", false);
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

    if (status != IStatus::Red && _secureIdEnabled && !_reader.hasSam())
    {
        // If we need but don't have a SAM and status is not red, then override the status
        status = IStatus::Red;
        msg = "No SAM installed";
    }
    // This is a bit of a hack, but we'll clean it up in the next gen xTP code
    else if (!_secureIdEnabled && (strcasecmp(msg.c_str(), "No SAM installed") == 0) )
    {
        // If we don't need a SAM and status is "No SAM installed" then override the status
        status = IStatus::Green;
    }
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
    if (!_reader.init(_secureIdEnabled, errorMsg))
    {
        LOG_ERROR(errorMsg.c_str());
        setStatus(IStatus::Red, "Unable to initialize RFID radio");
        return;
    }

    while (!_quit)
    {
        // The Feig reader drops some messages without this delay
        sleepMilliseconds(5);

        try
        {
            if (_desiredMode != _inTestMode)
            {
                _inTestMode = _desiredMode;
                _reader.readRepeats(_inTestMode);
                sleepMilliseconds(5);
            }

            if (_inTestMode)
            {
                sleepMilliseconds(5000);
            }


            // The timeout for reading a tag is chosen as a compromise between
            // processor load and speed of recovery on a communication error.
            // Making the timeout shorter will use more processor as we poll
            // more often for tags.  Making the timeout longer means it could
            // be longer before we read a tag on the off chance that a message
            // is lost by the Feig, something we saw routinely before adding
            // the above delay.
            UID uid;
            bool isDesfire;
            if (_reader.readTag(uid, isDesfire, 2000))
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
                // TODO - how about a tag compare operator?
                if ( ((getMilliseconds() - _lastRFIDTime) < _debounceTime) && (strcmp(_lastTag->getUID(), tag->getUID()) == 0) )
                {
                    _lastRFIDTime = getMilliseconds();
                    LOG_INFO("Tap ignored by debouncer");
                }
                else if ( (!_publicIdEnabled && !_secureIdEnabled) || tag->readIds(_reader, _secureIdEnabled))
                {
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

            // Delay to prevent a recurring error from hogging the processor and flooding the log
            sleepMilliseconds(2000);
        }
    }
}




