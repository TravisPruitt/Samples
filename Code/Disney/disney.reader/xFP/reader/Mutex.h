/**
    @file   Mutex.h 
    @author Greg Strange
    @date   Sept 14, 2011
*/

#ifndef _MUTEX_H
#define _MUTEX_H


#ifndef _WIN32

#ifndef _GNU_SOURCE
#define _GNU_SOURCE     // for recursive mutexes
#endif

#include <sys/types.h>
#include <pthread.h>

#else

#include <Windows.h>

#endif


/**
    Only two operations on a Mutex: lock and unlock.
*/
class Mutex
{
public:
    Mutex();
    virtual ~Mutex();

    void lock();
    void unlock();

private:

    // disallow copy constructor and assignments
    Mutex( const Mutex& rhs );
    Mutex operator=( const Mutex& rhs );

#ifdef _WIN32
    CRITICAL_SECTION m_cs;
#else
    pthread_mutex_t m_cs;
#endif

};


/**
    The lock class locks a mutex in its constructor and unlocks it in its destructor.
    This is a much safer (and easier) way to use a mutex to lock an entire function.
    E.g.

    Mutex mutex;

    void func()
    {
        Lock lock(mutex);
        ...
    }
*/
class Lock
{
public:
    Lock(Mutex &mutex) : _mutex(mutex) { _mutex.lock(); };
    ~Lock() { _mutex.unlock(); };

private:
    // disallow copy constructor and assignments
    Lock( const Lock& rhs );
    Lock operator=( const Lock& rhs );

    Mutex& _mutex;
};



#endif

