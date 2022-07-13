/**
    ReaderInfo.h
    Greg Strange
    August 2012

    Copyright (c) 2012, synapse.com
*/


#ifndef __READER_INFO_H
#define __READER_INFO_H

#include "ticks.h"
#include "Mutex.h"
#include "IStatus.h"
#include <json/json.h>
#include <string>


namespace Reader
{
    class ReaderInfo
    {
    public:
        static ReaderInfo* instance();

        std::string getName();
        const char* getMacAddress();
        const char* getLinuxVersion();
        void getInfo(Json::Value& returnValue, bool detailed);
        void getDiagnostics(Json::Value& returnValue);
        std::string getTimeAsString();
        std::string getBoardType();

        void setStatus(IStatus::Status newState, const char* msg);

    private:  // data
        std::string _mode;
        std::string _macAddress;

        std::string _errorMsg;
        MILLISECONDS _errorWhen;
        unsigned _errorSeenCount;

        std::string _warnMsg;
        MILLISECONDS _warnWhen;
        unsigned _warnSeenCount;

        Mutex _errorMutex;

    private: // methods

        // singleton
        ReaderInfo();
        ~ReaderInfo();

        void initMacAddress();
    };
}


#endif
