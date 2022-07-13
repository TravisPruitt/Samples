/**
    @file   Event.cpp
    @author Greg Strange
    @date   Oct, 2011
*/

#include "Event.h"

#ifndef _WIN32
#include <pthread.h>
#include <sys/time.h>
#endif


#ifdef _WIN32

/**
    Constructor
*/
Event::Event(void)
{
    _event = CreateEvent(NULL, FALSE, FALSE, NULL);
}

/**
    Desctructor
*/
Event::~Event(void)
{
    CloseHandle(_event);
}


/**
    Signal the event
*/
void Event::signal()
{
    SetEvent(_event);
}


/**
    Wait up to a specified amount of time for an event.
    
    @param  timeoutMs   Timeout in milliseconds.
*/
bool Event::wait(uint32_t timeoutMs)
{
    return (WaitForSingleObject(_event, timeoutMs) != WAIT_TIMEOUT);
}


/**
    Wait as long as it takes for an event.
*/
void Event::wait()
{
    WaitForSingleObject(_event, INFINITE);
}

#else



Event::Event(void) : _event(false)
{
    pthread_mutex_init(&_mutex, NULL);
    pthread_cond_init(&_cond, NULL);
}

Event::~Event(void)
{
    pthread_mutex_destroy(&_mutex);
    pthread_cond_destroy(&_cond);
}

void Event::signal()
{
    pthread_mutex_lock(&_mutex);
    _event = true;
    pthread_cond_signal(&_cond);
    pthread_mutex_unlock(&_mutex);
}


/**
    Linux's (and Mac) condition variable is a bit odd to use.  It must have
    a mutex associated with it that you must lock before calling the wait
    function.  The wait function can exit even when the condition has not
    been signalled, so you must also have a separate predicate variable that
    can be used to indicate whether the condition has been signalled.  There
    is plenty of sample code on the net that shows exactly how to do this,
    and it looks pretty much exactly like the code below.
*/
void Event::wait()
{
    pthread_mutex_lock(&_mutex);

    int result = 0;
    while (!_event && result == 0)
    {
        pthread_cond_wait(&_cond, &_mutex);
    }

    _event = false;

    pthread_mutex_unlock(&_mutex);
}


/**
    On Linux (and Mac), the timed wait function takes an absolute time in the 
    form of a timespec structure.  The timespec structure stores the time as 
    seconds since Jan 1, 1970 plus a second nanoseconds value.  We must 
    construct a timespec by getting the current time which is stored as 
    seconds and microseconds and adding the timeout to it and converting it 
    to a timespec structure.

    After that, the wait portion is nearly identical to the simpler wait()
    without a timeout function.
*/
bool Event::wait(uint32_t timeoutMs)
{
    struct timeval tv;
    struct timespec ts;

    if (gettimeofday(&tv, NULL) < 0)
    {
        return false;
    }

    // Add the tmieout value to the current time
    uint32_t seconds = tv.tv_sec + timeoutMs / 1000;
    uint32_t useconds = tv.tv_usec + (timeoutMs % 1000) * 1000;

    // Take care of carry from micro-seconds to seconds
    seconds += useconds / (1000 * 1000);
    useconds = useconds % (1000 * 1000);

    // Convert to timespec which is seconds and nano-seconds
    ts.tv_sec = seconds;
    ts.tv_nsec = useconds * 1000;

    // Now wait for the event
    pthread_mutex_lock(&_mutex);

    int result = 0;
    while (!_event && result == 0)
    {
        result = pthread_cond_timedwait(&_cond, &_mutex, &ts);
    }

    _event = false;

    pthread_mutex_unlock(&_mutex);

    return (result == 0);
}


#endif
