/**
 *  @file   DapReader.h
 *  @author Greg Strange
 *  @author Corey Whartyon
 *  @date   Sept 13, 2011
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#ifndef __DAP_READER_H
#define __DAP_READER_H

#include "ticks.h"
#include "Mutex.h"
#include "IStatus.h"
#include "RemoteEEPROM.h"
#include <json/json.h>
#include <string>


namespace Reader
{
    class DapReader
    {
    public:
        static DapReader* instance();

        std::string getName();
        const char* getMacAddress();
        const char* getLinuxVersion();
        void getInfo(Json::Value& returnValue);
        void getDiagnostics(Json::Value& returnValue);
        std::string getTimeAsString();
        std::string getBoardType();
        bool lightsDisabled();

        const RemoteEEPROM& getCamillaEEPROMData();

        void setStatus(IStatus::Status newState, const char* msg);

        void recordTapReadTime(MILLISECONDS time); 

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

        // The following fields track the number of taps as well as their min/max/total
        // times since the collection period started/restarted
        MILLISECONDS _tapStatStartTime;
        unsigned _tapStatCount;
        MILLISECONDS _minTapStatTime;
        MILLISECONDS _maxTapStatTime;
        MILLISECONDS _totalTapStatTime;

        Mutex _statsMutex;

        // TODO - comment explain
        bool _readCamillaEEPROM;
        struct RemoteEEPROM _remote;

    private: // methods

        // singleton
        DapReader();
        ~DapReader();

        void readCamillaEEPROM();
        void initMacAddress();
        void resetTapStats();
        void reportTapStats(Json::Value& json);
    };
}


#endif
