/**
    EventLogger.h
    Greg Strange
    August 5, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __EVENT_LOGGER_H
#define __EVENT_LOGGER_H


#include <stdint.h>
#include <json/json.h>
#include <queue>
#include "Thread.h"
#include "Mutex.h"
#include "Event.h"
#include <string>
#include "HttpConnection.h"


namespace Reader
{

class EventLogger: public Thread
{
public:
    static EventLogger* instance();

    typedef uint32_t EventNumber;

    void setPushUrl(const char* url);
    std::string getPushUrl();

    void setPushParams(const char* url, int interval, int max, EventNumber after);

    void postEvent(Json::Value& event);
    void getEvents(Json::Value& events, int max, long after);

    uint32_t getNextEventNumberToPush() { return _nextPush; };
    uint32_t getNextEventNumber() { return _nextEventNumber; };
    int getPushInterval() { return _interval; };
    int getMaxEventsToPush() { return _max; };

private:
    // singleton
    EventLogger();
    ~EventLogger();

    // No copies please
    EventLogger(const EventLogger&);
    const EventLogger& operator=(const EventLogger&);

    void run();
    int bumpIndex(int index);
    int getEventIndex(EventNumber eventNumber);
    unsigned getNumStoredEvents();
    unsigned getNumEventsSince(EventNumber eno);
    void gatherEvents(Json::Value& events, EventNumber& start, int max);

    HttpConnection _connection;

    // circular buffer of events
    Json::Value _events[10];
    bool _wrapped;
    int _nextEventSlot;
    EventNumber _nextEventNumber;

    // push variables
    std::string _pushUrl;
    int _interval;
    int _max;
    EventNumber _nextPush;

    // pull variables
    EventNumber _nextPull;

    // Thread synchronization stuff
    Mutex _mutex;
};


}

#endif
