/**
    EventLogger.h
    Greg Strange
    August 5, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __EVENT_LOGGER_H
#define __EVENT_LOGGER_H


#include <stdint.h>
#include "grover.h"
#include "Event.h"
#include "Thread.h"
#include "Mutex.h"
#include <string>
#include "HttpConnection.h"


namespace Reader
{

class EventLogger: public Thread
{
public:
    static EventLogger* instance();

    void setPushUrl(const char* url);
    std::string getPushUrl();

    void setPushParams(const char* url, int interval, int max, uint32_t after, bool useAfter, BandId id, int minss);
    void setReaderName(const char* name);

    void postEvent(Event* event);
    void getEvents(std::string& str, int max, uint32_t after, bool useAfter, bool tail, BandId id = 0, int minSignalStrength = -127);

    uint32_t getNextEventNumber();
    uint32_t getEventQueueSize();
    uint32_t getNumStored();
    uint32_t getNumQueuedForPush();
    uint32_t getNumQueuedForGet();

private:  // methods
    // singleton
    EventLogger();
    ~EventLogger();

    // No copies please
    EventLogger(const EventLogger&);
    const EventLogger& operator=(const EventLogger&);

    void run();
    int bumpIndex(int index);
    int decrementIndex(int index);
    int getEventIndex(uint32_t eventNumber);
    unsigned getNumStoredEvents();
    unsigned getNumEventsSince(uint32_t eno);
    uint32_t gatherEvents(std::string& events, uint32_t start, int max, bool tail, BandId id, int minSignalStrength);
    int searchBack(int max, BandId id, int minSignalStrength);

private:  // data
    struct EventEntry {
        uint32_t eventNumber;
		struct timespec timeStamp;
        Event* event;
    };

    HttpConnection _connection;

    // circular buffer of events
    EventEntry _events[3000];        // TODO - what is the correct number for this?
    bool _wrapped;
    int _nextEventSlot;
    uint32_t _nextEventNumber;

    // push variables
    std::string _pushUrl;
    int _interval;
    int _max;
    BandId _bandId;
    int _minss;
    uint32_t _nextPush;
    std::string _readerName;

    // pull variables
    uint32_t _nextPull;

    // Mutex
    Mutex _mutex;
};


}

#endif
