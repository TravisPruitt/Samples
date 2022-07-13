/**
    @file   RFIDReader.h
    @author Greg Strange
    @date   Aug 30, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __RFID_READER_H
#define __RFID_READER_H

#include "RFIDTag.h"
#include "Thread.h"
#include "StatusKeeper.h"
#include "Mutex.h"
#include "Event.h"
#include "ticks.h"
#include "SynapseReader.h"

namespace RFID
{

    using namespace Reader;

    class RFIDReader : public Thread, public StatusKeeper
    {
    public:
        static RFIDReader* instance();

        void getInfo(Json::Value& json);
        std::string getLastUID();

        void setTestMode(bool);
        bool getTestMode() { return _desiredMode; };

        void enablePublicId(bool which);
        bool isPublicIdEnabled() { return _publicIdEnabled; }

        void enableSecureId(bool which);
        bool isSecureIdEnabled() { return _secureIdEnabled; }

        void setTapLight(std::string);
        std::string getTapLight() { return _tapLight; };

        void setTapLightTimeout(MILLISECONDS);
        MILLISECONDS getTapLightTimeout() { return _tapLightTimeout; };

        void setTapSound(const char*);
        std::string getTapSound() { return _tapSound; };

        void enable();

        virtual IStatus::Status getStatus(std::string& msg);

        uint32_t getRestartCount() { return _restartCount; };

    private:
        // singleton
        RFIDReader();
        ~RFIDReader();

        // no copies please
        RFIDReader(const RFIDReader&);
        const RFIDReader& operator=(const RFIDReader&);

        void postEvent(const RFIDTag* tag);
        void run();

    private:    // data
        SynapseReader _reader;
        RFIDTag* _lastTag;
        Mutex _lastTagMutex;
        MILLISECONDS _lastRFIDTime;
        MILLISECONDS _debounceTime;

        bool _publicIdEnabled;
        bool _secureIdEnabled;
        bool _inTestMode;
        bool _desiredMode;
        MILLISECONDS _testModeInterval;
        std::string _tapLight;
        MILLISECONDS _tapLightTimeout;
        std::string _tapSound;

        uint32_t _restartCount;
    };

}




#endif
