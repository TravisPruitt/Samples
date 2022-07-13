/**
    @file   PingEvent.h 
    @author Greg Strange
    @date   May 2012
*/

#ifndef _PING_EVENT_H
#define _PING_EVENT_H


#include "Event.h"
#include "grover.h"

namespace Reader
{


class PingEvent : public Event
{
public:
	PingEvent(BandId address,               // information from ping packet
              uint8_t sequenceNumber, 
              uint8_t debug, 
              uint8_t wakeups,

              int8_t signalStrength,        // information from radio
              unsigned frequency, 
              unsigned channel, 
              uint8_t receiverSequenceNumber);

    PingEvent(Json::Value& json);

    ~PingEvent();

    virtual void writeAsJson(std::ostream& out, unsigned eventNumber, const char* timestamp);
    virtual std::string formatCompressed();
    virtual bool matchesFilter(BandId id, int minSignalStrength);


private:
    BandId _address;            // information from band ping packet
    uint8_t _sequenceNumber;
    uint8_t _debug;
    uint8_t _wakeups;

    int8_t _signalStrength;     // information from radio
    unsigned _frequency;
    unsigned _channel;
    uint8_t _receiverSequenceNumber;
};

}

#endif

