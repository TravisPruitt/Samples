/**
    PingEvent.cpp
    Greg Strange
    May 2012

    Copyright (c) 2012, synapse.com
*/


#include "standard.h"
#include "PingEvent.h"
#include "StringLib.h"
#include "JsonParser.h"
#include <string.h>
#include <string>



using namespace Reader;


PingEvent::PingEvent(BandId address, 
                     uint8_t sequenceNumber, 
                     uint8_t debug, 
                     uint8_t wakeups,

                     int8_t signalStrength, 
                     unsigned frequency, 
                     unsigned channel, 
                     uint8_t receiverSequenceNumber) :
    _address(address),
    _sequenceNumber(sequenceNumber),
    _debug(debug),
    _wakeups(wakeups),

    _signalStrength(signalStrength),
    _frequency(frequency),
    _channel(channel),
    _receiverSequenceNumber(receiverSequenceNumber)
{
}


PingEvent::PingEvent(Json::Value& json)
{
    std::string bandString = JsonParser::getString(json, "XLRID", "");
    _address = 0;
    StringLib::hex2bandId(bandString.c_str(), &_address);

    _sequenceNumber = JsonParser::getInt(json, "pno", 0);
    _debug = JsonParser::getInt(json, "dbg", 0);
    _wakeups = JsonParser::getInt(json, "wkup", 0);
    _frequency = JsonParser::getInt(json, "freq", 2405);
    _channel = JsonParser::getInt(json, "chan", 0);
    _signalStrength = JsonParser::getInt(json, "ss", -127);
    _receiverSequenceNumber = JsonParser::getInt(json, "rxno", 0);
}



PingEvent::~PingEvent()
{
}



void PingEvent::writeAsJson(std::ostream& out, unsigned eventNumber, const char* timeStamp)
{
    char buf[20];
    sprintf(buf, "%010llx", _address);

    out << "  {\n";
    out << "    \"eno\": " << eventNumber << ",\n";
    out << "    \"time\": \"" << timeStamp << "\",\n";
    out << "    \"XLRID\": \"" << buf << "\",\n";
    out << "    \"pno\": " << (int)_sequenceNumber << ",\n";
    out << "    \"dbg\": " << (int)_debug << ",\n";
    out << "    \"wkup\": " << (int)_wakeups << ",\n";
    out << "    \"freq\": " << (int)_frequency << ",\n";
    out << "    \"chan\": " << (int)_channel << ",\n";
    out << "    \"ss\": " << (int)_signalStrength << ",\n";
    out << "    \"rxno\": " << (int)_receiverSequenceNumber << "\n";
    out << "  }";
}


std::string PingEvent::formatCompressed()
{
    char buf[100];
    sprintf(buf, "%010llx %.3d %d %d %.2d %02x", _address, _sequenceNumber, _frequency, _channel, _signalStrength, _debug);
    return buf;
}


/**
    Does the event match the filter.

    @param  id      Band ID. 0 = any band.
    @param  minss   Minimum signal strength

*/
bool PingEvent::matchesFilter(BandId id, int minss)
{
    return ( ( (id == 0) || (id == _address) ) && (_signalStrength >= minss) );
}
