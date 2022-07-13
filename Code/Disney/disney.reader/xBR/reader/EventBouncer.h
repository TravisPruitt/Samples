/**
    @file   EventBouncer.h
    @author Greg Strange
    @date   May 2012

    Copyright (c) 2012, synapse.com
*/


#ifndef __EVENT_BOUNCER_H
#define __EVENT_BOUNCER_H

#include "json/json.h"
#include "Thread.h"
#include "ticks.h"
#include "Mutex.h"
#include "Event.h"
#include "ThreadEvent.h"
#include <queue>

namespace Reader
{

class EventBouncer: public Thread
{
public:
    static EventBouncer* instance();

    int addEvents(Json::Value& events);
    void clear();

private:  // methods
    // singleton
    EventBouncer();
    ~EventBouncer();

    // No copies please
    EventBouncer(const EventBouncer&);
    const EventBouncer& operator=(const EventBouncer&);

    void run();
    bool addJsonEvent(Json::Value& json);

private:  // data
    struct EventEntry {
        MILLISECONDS delay;
        Event* event;
    };

    std::queue<EventEntry> _eventQ;
    MILLISECONDS _lastEvent;

    Mutex _mutex;
    ThreadEvent _threadEvent;
};


}

#endif
