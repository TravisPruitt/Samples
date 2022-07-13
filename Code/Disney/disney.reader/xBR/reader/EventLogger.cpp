/**
    EventLogger.cpp
    Greg Strange
    August 5, 2011

    Copyright (c) 2011, synapse.com
*/


#include "standard.h"
#include "EventLogger.h"
#include "log.h"
#include "config.h"
#include "HttpRequest.h"
#include "ticks.h"
#include "HelloThread.h"
#include "ReaderInfo.h"

#include <stdlib.h>
#include <string.h>
#include <sstream>
#include <iostream>
#include <iomanip>



using namespace Reader;
using namespace std;


EventLogger::EventLogger() : 
    _wrapped(false),
    _nextEventSlot(0),
    _nextEventNumber(1),
    _interval(1000), 
    _max(-1), 
    _bandId(0),
    _minss(-127),
    _nextPush(1),
    _nextPull(1)
{
    for (unsigned i = 0; i < ArrayLength(_events); ++i)
        _events[i].event = NULL;

    setReaderName(Config::instance()->getValue("name", "").c_str());
}


EventLogger::~EventLogger()
{
    // Free up memory used by events
    for (unsigned i = 0; i < ArrayLength(_events); ++i)
    {
        if (_events[i].event)
            delete _events[i].event;
    }
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

    @param  url         URL to push events to. Unchanged if blank. Unchanged if NULL.
    @param  interval    interval between PUT commands (in milliseconds).  Unchanged if < 0.
    @param  max         Maximum number of events to put in each PUT.  Unchanged if <= 0.
    @param  after       Push events starting with event number after + 1.
    @param  useAfter    True if we should use the after paramter, ignore it otherwise
    @param  id          Band ID (0 = all)
    @param  minss       Minimum signal strength

    This method is provided to set all of the push related properties at once so it can
    be an atomic operation.  All of these parameters will take affect between PUT commands
    to the push server.
*/
void EventLogger::setPushParams(const char* url, int interval, int max, uint32_t after, bool useAfter, BandId id, int minss)
{
    // lock mutex so that the changes are atomic
    Lock lock(_mutex);

    if (url)
        _pushUrl = url;

    if (interval > 0)
        _interval = interval;
    else if (interval == 0)
        _interval = 1;
    
    if (max > 0)
        _max = max;

    _bandId = id;
    _minss = minss;

    if (useAfter)
        _nextPush = (uint32_t)(after+1);
}


void EventLogger::setReaderName(const char* name)
{
    Lock lock(_mutex);

    _readerName = "  \"reader name\": \"";
    _readerName += name;
    _readerName += "\",\n";
}

/**
    Post an event to the event logger.

    NOTE: After calling this function, the event is owned by EventLogger and can be deleted
    and the memory freed at any time.
*/
void EventLogger::postEvent(Event* ev)
{
    // Print the event to the console, for debugging purposes
    if (logIsLevelVisible(LOG_LEVEL_TRAFFIC))
    {
        LOG_TRAFFIC("event: %d %s", _nextEventNumber, ev->formatCompressed().c_str());
    }

    // Add the event to the queue
    _mutex.lock();
    Event* oldEvent = _events[_nextEventSlot].event;
    _events[_nextEventSlot].event = ev;
    _events[_nextEventSlot].eventNumber = _nextEventNumber++;
    clock_gettime(CLOCK_REALTIME, &_events[_nextEventSlot].timeStamp);

    // bump index
    _nextEventSlot = bumpIndex(_nextEventSlot);
    if (_nextEventSlot == 0)
        _wrapped = true;

    _mutex.unlock();

    // Delete the event that lost its place in the queue
    if (oldEvent)
        delete oldEvent;
}



/**
    Get events as a Json formatted string.

    @param  str     String to append to with events formatted as Json
    @param  max     Maximum number of events to get. Set to <= 0 to pull all events.
    @param  after   Start retrieving with events after event number 'after'
    @param  useAfter    True if we should use the after value, otherwise ignore it
    @param  tail    True if we should pull 'max' newest events, else pull oldest events.
                    NOTE: This flag only has affect if max is not <= 0
    @param  id      Band id to filter for
    @param  minSignalStrength  Filter for events with signal strength >= this value.

    This class keeps two indexes into the events.  One for events being pushed and one
    for events pulled from the event array using this function.  This second event index
    is updated by this function.
*/
void EventLogger::getEvents(std::string& str, int max, uint32_t after, bool useAfter, bool tail, BandId id, int minSignalStrength)
{
    LOG_DEBUG("Get events after %d, useAfter %d\n", after, useAfter);

    if (useAfter)
        _nextPull = (uint32_t)after + 1;

    if (max <= 0)
    {
        max = ArrayLength(_events);
        tail = false;
    }

    _nextPull = gatherEvents(str, _nextPull, max, tail, id, minSignalStrength);
}


/**
    Thread for pushing events
*/
void EventLogger::run()
{
    // don't put this on the stack 'cause they can get pretty large
    static std::string payload;

    while (!_quit)
    {
        _mutex.lock();
        std::string pushUrl = _pushUrl;
        _mutex.unlock();

        if (pushUrl.size() > 0 && getNumEventsSince(_nextPush))
        {
            payload.clear();
            uint32_t nextPush = gatherEvents(payload, _nextPush, _max, false, _bandId, _minss);

            HttpRequest* req = new HttpRequest(&_connection, "put", pushUrl.c_str());
            if (Config::instance()->getValue("ssl verify host", false))
            {
                req->enableSSLHostVerification();
            }
            req->setPayload(payload.c_str(), "application/json");

            if (req->send())
            {
                // If successful, move the next push event number forward
                _nextPush = nextPush;
            }
            else
            {
                // If not successful, go back to saying hello until we re-establish communications
                ReaderInfo::instance()->setStatus(IStatus::Red, "Message to xBRC failed");
                setPushUrl(NULL);
                HelloThread::instance()->sayHello();
            }
            delete req;
        }
        sleepMilliseconds(_interval);
    }
}



/**
    Gather some events into a Json array

    @param  str     String to append the events to
    @param  start   Event number to start with
    @param  max     Maximum number of events to append. -1 = no limit.
    @param  tail    True to pull events from newest events, false to pull from oldest.
    @param  id      Band ID to filter on (0 = all bands)
    @param  minss   Minimum signal strength to filter on

    @return Next event number not returned
*/
uint32_t EventLogger::gatherEvents(std::string& str, uint32_t start, int max, bool tail, BandId id, int minss)
{
    uint32_t nextEno = start;
    std::stringstream ss;
    int count = 0;

    Lock lock(_mutex);

    ss << "{\n";
    ss << _readerName;
    ss << "  \"events\": [\n";

    if ((start + getNumStoredEvents()) < _nextEventNumber)
        start = _nextEventNumber - getNumStoredEvents();

    int index;
    if (tail)
        index = searchBack(max, id, minss);
    else
        index = getEventIndex(start);

    if (index >= 0)
    {
        int numEvents = getNumEventsSince(start);
        if (max <= 0)    
            max = numEvents;

        for (int i = 0; i < numEvents && count < max; ++i)
        {
            if (_events[index].event->matchesFilter(id, minss))
            {
                // closing bracket for previous event
                if (count)
                    ss << ",\n";

                ++count;

                // opening bracket
//                ss << "  {\n";

                // event number
//                ss << "    \"eno\": ";
//                ss << _events[index].eventNumber;
//                ss << ",\n";

                // time stamp
                struct tm * timeinfo = gmtime(&(_events[index].timeStamp.tv_sec));
                char timeText[100];
                int length = strftime(timeText, sizeof(timeText), "%Y-%m-%dT%H:%M:%S.", timeinfo);
                sprintf(timeText + length, "%03u", _events[index].timeStamp.tv_nsec / (1000 * 1000) );
//                ss << "    \"time\": \"";
//                ss << buf;
//                ss << setw(3) << setfill('0') << _events[index].timeStamp.tv_nsec / (1000 * 1000);
//                ss << "\",\n";

                // rest of event contents
                _events[index].event->writeAsJson(ss, _events[index].eventNumber, timeText);
            }
            index = bumpIndex(index);
            ++nextEno;
        }
    }
    else
    {
        // something went wonky, reset index
        nextEno = _nextEventNumber;
    }

    // closing bracket for last event
//    if (count)
//        ss << "  }\n";

    ss << "  ]\n";
    ss << "}";
    str += ss.str();

    return nextEno;
}


/**
    Search back for the most recent 'max' events that match the filter (id, minss)

    @param  max     Number of events to match
    @param  id      Band id to filter on (0 to accept all events)
    @param  minss   Minimum signal strength to filter on (-127 to accept all events)

    @return Index of first event to use.


    This function gets called when the user asked to see 'max' newest events.
    The function searches backwards through the queue until it has found 'max'
    events that match the filter and then returns the index of the first event
    that the normal gatherEvents() method should start with to get the events.
    gatherEvents() then goes through the events normally, allowing the output
    to be in the correct order.
*/
int EventLogger::searchBack(int max, BandId id, int minss)
{
    if (!_wrapped && _nextEventSlot == 0)
        return -1;

    int firstEntry;
    if (!_wrapped)
        firstEntry = 0;
    else
        firstEntry = _nextEventSlot;

    int lastEntry;
    if (_nextEventSlot != 0)
        lastEntry = _nextEventSlot - 1;
    else
        lastEntry = ArrayLength(_events)-1;

    int i = lastEntry;
    int count = 0;
    while (count < max)
    {
        if (_events[i].event->matchesFilter(id, minss))
            ++count;

        if (i == firstEntry)
            break;

        i = decrementIndex(i);
    }

    return i;
}





/**
    Bump an index into the events array, dealing with wrap
*/
int EventLogger::bumpIndex(int index)
{
    return ((size_t)index >= (ArrayLength(_events)-1) ? 0 : index+1);
}

/**
    Decrement an events array index by one slot.
*/
int EventLogger::decrementIndex(int index)
{
    return (index > 0) ? index-1 : ArrayLength(_events)-1;
}

/**
    Get the index of an event number in the event array

    @return Index of event with the given event number, or -1 if not in array
*/
int EventLogger::getEventIndex(uint32_t eventNumber)
{
    // calculate the offset from _nextEventSlot
    uint32_t offset = _nextEventNumber - eventNumber;

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
unsigned EventLogger::getNumEventsSince(uint32_t eno)
{
    uint32_t i = _nextEventNumber - eno;
    unsigned numStoredEvents = getNumStoredEvents();
    return (i > numStoredEvents ? numStoredEvents : i);
}

uint32_t EventLogger::getNextEventNumber() 
{ 
    return _nextEventNumber; 
}

uint32_t EventLogger::getEventQueueSize() 
{ 
    return ArrayLength(_events); 
}


uint32_t EventLogger::getNumStored()
{
    if (_wrapped)
        return getEventQueueSize();
    else
        return _nextEventSlot;
}


uint32_t EventLogger::getNumQueuedForPush()
{
    uint32_t n = _nextEventNumber - _nextPush;
    if (n > getEventQueueSize())
        n = getEventQueueSize();
    return n;
}

uint32_t EventLogger::getNumQueuedForGet()
{
    uint32_t n = _nextEventNumber - _nextPull;
    if (n > getEventQueueSize())
        n = getEventQueueSize();
    return n;
}

