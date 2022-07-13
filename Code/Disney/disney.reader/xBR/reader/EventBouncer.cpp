/**
    @file   EventBouncer.cpp
    @author Greg Strange
    @date   May 2012

    Copyright (c) 2012, synapse.com
*/


#include "standard.h"
#include "EventBouncer.h"
#include "JsonParser.h"
#include "StringLib.h"
#include "PingEvent.h"
#include "EventLogger.h"
#include "log.h"




using namespace Reader;


EventBouncer::EventBouncer()
{
}


EventBouncer::~EventBouncer()
{
}


EventBouncer* EventBouncer::instance()
{
    static EventBouncer _instance;
    return &_instance;
}

void EventBouncer::clear()
{
    Lock lock(_mutex);

    while (!_eventQ.empty())
    {
        EventEntry e = _eventQ.front();
        _eventQ.pop();
        delete e.event;
    }
}

int EventBouncer::addEvents(Json::Value& json)
{
    int count = 0;

    // If the queue is empty, start any delay from now,
    // not from whenever the last event was
    _mutex.lock();
    if (_eventQ.empty())
        _lastEvent = getMilliseconds();
    _mutex.unlock();

    // Array of events can be preceeded by "events"
    if (json.isObject() && json.isMember("events"))
        json = json["events"];

    // Add the events
    if (!json.isArray())
    {
        if (addJsonEvent(json))
            ++count;
    }
    else
    {
        for (Json::ArrayIndex i = 0; i < json.size(); i++)
            if (addJsonEvent(json[i]))
                ++count;
    }

    // wake up the thread in case event queue was empty
    _threadEvent.signal();

    return count;
}

bool EventBouncer::addJsonEvent(Json::Value& json)
{
    // XLRID is the only required field.
    if (!json.isMember("XLRID"))
        return false;

    std::string bandString = JsonParser::getString(json, "XLRID", "");
    uint64_t bandId;
    if (!StringLib::hex2bandId(bandString.c_str(), &bandId))
        return false;

    EventEntry e;
    e.delay = JsonParser::getInt(json, "delay", 0);
    e.event = new PingEvent(json);

    _mutex.lock();
    _eventQ.push(e);
    _mutex.unlock();
}



/**
    Thread for pushing events
*/
void EventBouncer::run()
{
    while (!_quit)
    {
        MILLISECONDS now = getMilliseconds();
        MILLISECONDS waitTime = 5000;

        _mutex.lock();
        if (!_eventQ.empty())
        {
            EventEntry e = _eventQ.front();
            if ( (e.delay == 0) || (now - _lastEvent) >= e.delay)
            {
                _eventQ.pop();
                EventLogger::instance()->postEvent(e.event);
                _lastEvent = now;
            }

            if (_eventQ.empty())
                waitTime = 5000;
            else
                waitTime = _eventQ.front().delay;
        }
        _mutex.unlock();

        if (waitTime > 0)
            _threadEvent.wait(waitTime);
    }
}

