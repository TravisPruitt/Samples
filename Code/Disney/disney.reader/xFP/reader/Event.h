/**
    @file   Event.h 
    @author Greg Strange
    @date   Oct, 2011
*/

#ifndef _EVENT_H
#define _EVENT_H


#include "standard.h"
#include <stdint.h>


#ifdef _WIN32
#include <Windows.h>
#else
#include <pthread.h>
#endif



class Event
{
public:
    Event();
    ~Event();
    
    void signal();
    void wait();
    bool wait(uint32_t timeoutMs);
    
private:

#ifdef _WIN32
    HANDLE _event;
#else
    bool _event;
    pthread_mutex_t _mutex;
    pthread_cond_t _cond;
#endif
    
};


#endif

