/**
    StatusKeeper.h
    Greg Strange
    Dec 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __STATUS_KEEPER_H
#define __STATUS_KEEPER_H


#include "IStatus.h"
#include "log.h"
#include "Mutex.h"

#include <string>

namespace Reader
{

    class StatusKeeper : public IStatus
    {
    public:
        virtual IStatus::Status getStatus(std::string& msg);

    protected:
        StatusKeeper();
        ~StatusKeeper();

        void setStatus(IStatus::Status status, const char* msg);
        void clearStatus();

    private:
        StatusKeeper(const StatusKeeper&);
        const StatusKeeper& operator=(const StatusKeeper&);

        IStatus::Status _statusLevel;
        std::string _statusMsg;
        Mutex _statusMutex;
    };

}

#endif
