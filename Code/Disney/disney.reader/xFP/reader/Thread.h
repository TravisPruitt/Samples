/**
 *  @file   Thread.h
 *  @date   Aug 30, 2011
 *  @author Greg Strange
 *
 *  Copyright (c) 2011-2012, synapse.com
*/

#ifndef __THREAD_H
#define __THREAD_H


#ifdef _WIN32
#include <Windows.h>
#else
#include <pthread.h>
#endif
#include <stdint.h>

#include "Event.h"


class Thread
{
public:
    Thread(void);
    virtual ~Thread(void);

    void start();
    
    // Tell thread to stop and wait up to 'timeout' milliseconds for it to happen
    virtual void stop(uint32_t timeout = 0);
    
    // kill the thread
    virtual void cancel();

    // Have we told the thread to stop?
    // Note: This only means we set the quit flag, the thread may still be
    // running because it has not responded to the quit flag.
    bool stopped() { return _quit; };

    // Is the thread running?
    // If this returns false, the thread has really stopped.
    bool running() { return _running; };
    
    
protected:
    virtual void run() = 0;

    bool _quit;
    bool _running;

    Event _event;
    
private:

#ifdef _WIN32    
    unsigned int _threadID;
    HANDLE _threadHandle;
    
    friend unsigned __stdcall winThreadFunc(void* param);
#else
    pthread_t _thread;
    bool _started;

    friend void* threadProc(void* param);
#endif
};


#endif
