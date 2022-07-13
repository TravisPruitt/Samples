/**
 *  @file Thread.cpp
 *  @Author Greg Strange
 *  
 *  To create a new thread, simply sub-class this class and provide a run()
 *  function.  This class will handle starting and stopping and the platform
 *  dependencies.
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include "Thread.h"
#include "log.h"

#ifdef _WIN32
#include <process.h>
#else
#include <string.h>
#include <signal.h>
#endif



/**
    Constructor
*/
Thread::Thread() : _quit(false), _running(false)
{
#ifdef _WIN32
    _threadHandle = NULL;
#endif
}

/**
    Destructor
*/
Thread::~Thread(void)
{
}




/**
    The thread function when running on Windows
*/
#ifdef _WIN32
unsigned __stdcall winThreadFunc(void* param)
{
    Thread* p = (Thread*)param;
    p->_running = true;
    p->run();
    p->_running = false;
    return 0;
}
#else
/**
    The thread function when running on Linux or OS X
*/
void* threadProc(void* param)
{
    Thread* p = (Thread*)param;
    p->_running = true;
    p->run();
    p->_running = false;
    return NULL;
}
#endif



/**
    Start the thread
*/
void Thread::start()
{
    _quit = false;

    // start the thread
#ifdef _WIN32

    _threadHandle = (HANDLE)_beginthreadex(
        NULL,                // default security attributes 
        0,                   // use default stack size  
        winThreadFunc ,      // thread function 
        this,                // argument to thread function 
        0,                   // use default creation flags 
        &_threadID);        // returns the thread identifier

    // Check the return value for success. 
    if (!_threadHandle) 
    {
        LOG_ERROR("Unable to create thread\n");
    }
#else

    sigset_t sig, sigSave;
    sigfillset(&sig);

    // block signals in the new thread so that it does not steal them from
    // the main thread
    pthread_sigmask(SIG_BLOCK, &sig, &sigSave);

    int result = pthread_create(&_thread, NULL, threadProc, this);
    _started = (result == 0);
    if (!_started)
    {
        LOG_ERROR("pthread_create failed: %d\n", result);
    }

    // restore the signals as they were for the current thread
    pthread_sigmask(SIG_SETMASK, &sigSave, NULL);

#endif
    return;
}


/**
    Stop the thread
    
    @param  timeout     Max time to wait for thread to stop (milliseconds)
    
    Note that the timeout only works on Windows.  On Linux there is no
    timeout and the function will block until the thread exits, if ever.
    If there is any doubt as to whether the thread will exit nicely,
    it is best to use cancel().
*/
void Thread::stop(uint32_t timeout)
{
    _quit = true;
    _event.signal();

#ifdef WIN32
    if (_threadHandle != NULL)
    {
        DWORD result = WaitForSingleObject(_threadHandle, timeout);
        if (result == WAIT_TIMEOUT)
        {
            LOG_DEBUG("Terminating rogue thread\n");    
            if (!TerminateThread(_threadHandle, _threadID))
            {			
                LOG_DEBUG("Couldn't terminate the bugger either, error: %d\n", GetLastError());
            }
        }
        CloseHandle(_threadHandle);
        _threadHandle = NULL;
    }
#else
    timeout = 0;
    if (_started)
    {
        int ret = pthread_join(_thread, NULL);
        if (ret < 0)
            LOG_WARN("pthread_join(): %s", strerror(ret));
        _started = false;
    }
#endif
}


/**
    Cancel the thread immediately.
*/
void Thread::cancel()
{
    _quit = true;
    
#ifdef WIN32
    // Note that there is no way to cancel a thread started with _beginthreadex.  We may
    // want to add signaling to the thread or some such thing.
    CloseHandle(_threadHandle);
    _threadHandle = NULL;
#else
    if (_started)
    {
        pthread_cancel(_thread);
        _started = false;
    }
#endif
}
