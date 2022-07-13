/**
    @file   ThreadEvent.h 
    @author Greg Strange
    @date   Oct, 2011
*/

#ifndef _THREAD_EVENT_H
#define _THREAD_EVENT_H


#include "standard.h"
#include <stdint.h>


#ifdef _WIN32
#include <Windows.h>
#else
#include <pthread.h>
#endif



class ThreadEvent
{
public:
    ThreadEvent();
    ~ThreadEvent();
    
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

