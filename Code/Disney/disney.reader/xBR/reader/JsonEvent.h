/**
    @file   JsonEvent.h 
    @author Greg Strange
    @date   August 2012
*/

#ifndef _JSON_EVENT_H
#define _JSON_EVENT_H


#include "Event.h"


namespace Reader
{

class JsonEvent : public Event
{
public:
    JsonEvent(Json::Value& json);
    ~JsonEvent();

    virtual void writeAsJson(std::ostream& str, unsigned eventNumber, const char* timeStamp);
    virtual std::string formatCompressed();
    virtual bool matchesFilter(BandId id, int minSignalStrength);

private:
    Json::Value _json;
};

}

#endif



