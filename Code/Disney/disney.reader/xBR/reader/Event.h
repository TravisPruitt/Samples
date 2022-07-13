/**
    @file   Event.h 
    @author Greg Strange
    @date   May 2012
*/

#ifndef _EVENT_H
#define _EVENT_H


#include <string>
#include <ostream>
#include "grover.h"


namespace Reader
{

class Event
{
public:
    virtual ~Event() {};

    virtual void writeAsJson(std::ostream& str, unsigned eventNumber, const char* timeStamp) = 0;
    virtual std::string formatCompressed() = 0;
    virtual bool matchesFilter(BandId id, int minSignalStrength) = 0;

protected:
    Event() {};
};

}

#endif

