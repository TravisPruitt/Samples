/**
    StatusKeeper.cpp
    Greg Strange
    Dec 2011

    Copyright (c) 2011, synapse.com
*/



#include "StatusKeeper.h"
#include "Mutex.h"

#include <string.h>
#include <stdarg.h>


using namespace Reader;

StatusKeeper::StatusKeeper()
{
}

StatusKeeper::~StatusKeeper()
{
}

IStatus::Status StatusKeeper::getStatus(std::string& msg)
{
    Lock lock(_statusMutex);
    msg = _statusMsg;
    return _statusLevel;
}

void StatusKeeper::setStatus(IStatus::Status newState, const char* msg)
{

    if (newState == IStatus::Red || _statusLevel == IStatus::Green)
    {
        _statusMutex.lock();
        _statusMsg = msg;
        _statusLevel = newState;
        _statusMutex.unlock();
    }
}


void StatusKeeper::clearStatus()
{
    Lock lock(_statusMutex);
    _statusLevel = IStatus::Green;
    _statusMsg = "";
}

