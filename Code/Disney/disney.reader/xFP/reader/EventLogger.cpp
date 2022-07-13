/**
    EventLogger.cpp
    Greg Strange
    August 5, 2011

    Copyright (c) 2011, synapse.com
*/


#include "standard.h"
#include "EventLogger.h"
#include "log.h"
#include "DapConfig.h"
#include "HttpRequest.h"
#include "ticks.h"
#include "DapReader.h"
#include "jsonDebugWriter.h"
#include "HelloThread.h"

#include <stdlib.h>
#include <string.h>



using namespace Reader;


EventLogger::EventLogger() : 
    _wrapped(false),
    _nextEventSlot(0),
    _nextEventNumber(1),
    _interval(0), 
    _max(-1), 
    _nextPush(1),
    _nextPull(1)
{
}


EventLogger::~EventLogger()
{
}


EventLogger* EventLogger::instance()
{
    static EventLogger _instance;
    return &_instance;
}


/**
    Set the URL to push events to

    Setting this to NULL will turn off pushing of events.
*/
void EventLogger::setPushUrl(const char* url)
{
    Lock lock(_mutex);
    _pushUrl = url ? url : "";
}


/**
    Get the push URL.
*/
std::string EventLogger::getPushUrl()
{
    Lock lock(_mutex);
    return _pushUrl;
}


/**
    Set the push parameters

    @param  url         URL to push events to
    @param  interval    interval between PUT commands (in milliseconds)
    @param  max         Maximum number of events to put in each PUT
    @param  after       Push events starting with event number after + 1

    This method is provided to set all of the push related properties at once so it can
    be an atomic operation.  All of these parameters will take affect between PUT commands
    to the push server.
*/
void EventLogger::setPushParams(const char* url, int interval, int max, EventNumber after)
{
    // lock mutex so that the changes are atomic
    Lock lock(_mutex);

    _pushUrl = url ? url : "";

    if (interval > 0)
        _interval = interval;
    else if (interval == 0)
        _interval = 1;
        
    _max = max;
    _nextPush = (EventNumber)(after+1);
}


/**
    Post an event to the event logger
*/
void EventLogger::postEvent(Json::Value& event)
{
    _mutex.lock();

    // Add event
    event["eno"] = _nextEventNumber++;
    event["time"] = DapReader::instance()->getTimeAsString();
    _events[_nextEventSlot] = event;

    // bump index
    _nextEventSlot = bumpIndex(_nextEventSlot);
    if (_nextEventSlot == 0)
        _wrapped = true;

    _mutex.unlock();
    _event.signal();

    // Print the event to the console, for debugging purposes
    if (logIsLevelVisible(LOG_LEVEL_DEBUG))
    {
        DebugWriter writer;
        std::string json = writer.write(event);
        LOG_DEBUG("event: %s\n", json.c_str());
    }
}



/**
    Get events as a Json formatted string.

    @param  events  Json value to populate with the events
    @param  max     Maximum number of events to get
    @param  after   Start retrieving with events after event number 'after'

    This class keeps two indexes into the events.  One for events being pushed and one
    for events pulled from the event array using this function.  This second event index
    is updated by this function.
*/
void EventLogger::getEvents(Json::Value& events, int max, long after)
{
    if (after >= 0)
        _nextPull = (EventNumber)after + 1;

    if (max <= 0)
        max = ArrayLength(_events);

    events["reader name"] = DapReader::instance()->getName();
    gatherEvents(events, _nextPull, max);
}


/**
    Thread for pushing events
*/
void EventLogger::run()
{
    EventNumber nextPush;

    // don't put these on the stack 'cause they can get pretty large
    static Json::Value events;
    static Json::StyledWriter writer;

    while (!_quit)
    {
        _mutex.lock();
        std::string pushUrl = _pushUrl;
        _mutex.unlock();
        
        if (pushUrl.size() > 0 && getNumEventsSince(_nextPush))
        {
            LOG_DEBUG("next eno %d, next push %d\n", _nextEventNumber, _nextPush);
            LOG_DEBUG("%d events since %d\n", getNumEventsSince(_nextPush), _nextPush);

            events.clear();
            events["reader name"] = DapReader::instance()->getName();
            nextPush = _nextPush;
            gatherEvents(events, _nextPush, _max);

            std::string json = writer.write(events);

            HttpRequest* req = new HttpRequest(&_connection, "put", pushUrl.c_str());
            if (DapConfig::instance()->getValue("ssl verify host", false))
            {
                req->enableSSLHostVerification();
            }
            req->setPayload(json.c_str(), "application/json");

            if (!req->send())
            {
                DapReader::instance()->setStatus(IStatus::Red, "Message to xBRC failed");
                setPushUrl(NULL);
                _nextPush = nextPush;    // restore next push pointer since events did not go out
                HelloThread::instance()->sayHello();
            }
            delete req;
        }
        if (_interval)
            sleepMilliseconds(_interval);
        else
            _event.wait(1000);
    }
}



/**
    Gather some events into a Json array

    @param  events  Json value to append events to
    @param  start   Event number to start with
    @param  max     Maximum number of events to append
*/
void EventLogger::gatherEvents(Json::Value& events, EventNumber& start, int max)
{
    Lock lock(_mutex);

    if ((start + getNumStoredEvents()) < _nextEventNumber)
        start = _nextEventNumber - getNumStoredEvents();

    int index = getEventIndex(start);
    if (index >= 0)
    {
        int numToSend = getNumEventsSince(start);
        if (max > 0 && numToSend > max)
            numToSend = max;
        for (int i = 0; i < numToSend; ++i)
        {
            events["events"].append(_events[index]);
            index = bumpIndex(index);
            ++start;
        }
    }
    else
    {
        // something went wonky, reset index
        start = _nextEventNumber;
    }
}





/**
    Bump an index into the events array, dealing with wrap
*/
int EventLogger::bumpIndex(int index)
{
    return ((size_t)index >= (ArrayLength(_events)-1) ? 0 : index+1);
}

/**
    Get the index of an event number in the event array

    @return Index of event with the given event number, or -1 if not in array
*/
int EventLogger::getEventIndex(EventNumber eventNumber)
{
    // calculate the offset from _nextEventSlot
    EventNumber offset = _nextEventNumber - eventNumber;

    // return -1 if not in the array
    if (offset == 0 || offset > getNumStoredEvents())
        return -1;

    // calculate the index, dealing with wrap if necessary
    int index = _nextEventSlot - offset;
    if (index < 0)
    {
        // if we haven't wrapped, then the event is not in the array
        if (!_wrapped)
            return -1;
        index += ArrayLength(_events);
    }

    return index;
}


/**
    Get the number of events stored in the array
*/
unsigned EventLogger::getNumStoredEvents()
{
    return _wrapped ? ArrayLength(_events) : _nextEventSlot;
}


/**
    Get the number of events in the event array since a given event number
*/
unsigned EventLogger::getNumEventsSince(EventNumber eno)
{
    EventNumber i = _nextEventNumber - eno;
    unsigned numStoredEvents = getNumStoredEvents();
    return (i > numStoredEvents ? numStoredEvents : i);
}
