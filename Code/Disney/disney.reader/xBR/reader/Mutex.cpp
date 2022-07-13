/**
    @file       Mutex.cpp
    @author     Greg Strange
    @date       Sept 14, 2011
*/

#include "Mutex.h"


#ifndef _WIN32


#include <pthread.h>

Mutex::Mutex()
{
    pthread_mutex_init(&m_cs, NULL);
}

Mutex::~Mutex()
{
    pthread_mutex_destroy(&m_cs);
}

void Mutex::lock()
{
    pthread_mutex_lock(&m_cs);
}

void Mutex::unlock()
{
    pthread_mutex_unlock(&m_cs);
}


#else

Mutex::Mutex()
{
    InitializeCriticalSection(&m_cs);
}

Mutex::~Mutex()
{
    DeleteCriticalSection(&m_cs);
}

void Mutex::lock()
{
    EnterCriticalSection(&m_cs);
}

void Mutex::unlock()
{
    LeaveCriticalSection(&m_cs);
}


#endif

