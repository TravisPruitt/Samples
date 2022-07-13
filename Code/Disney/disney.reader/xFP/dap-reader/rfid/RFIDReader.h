/**
    @file   RFIDReader.h
    @author Greg Strange
    @date   Aug 30, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __RFID_READER_H
#define __RFID_READER_H

#include "Thread.h"
#include "FeigReader.h"
#include "Mutex.h"
#include "Event.h"
#include "ticks.h"

namespace RFID
{

    using namespace Reader;

    class RFIDReader : public Thread, public StatusKeeper
    {
    public:
        static RFIDReader* instance();

        const char* getFirmwareVersion() { return _reader.getFirmwareVersion(); };
        const char* getDescription() { return _reader.getDescription(); };
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
        FeigReader _reader;
        RFIDTag* _lastTag;
        Mutex _lastTagMutex;
        MILLISECONDS _lastRFIDTime;
        MILLISECONDS _debounceTime;

        bool _publicIdEnabled;
        bool _secureIdEnabled;
        bool _inTestMode;
        bool _desiredMode;
        std::string _tapLight;
        MILLISECONDS _tapLightTimeout;
        std::string _tapSound;
    };

}




#endif
