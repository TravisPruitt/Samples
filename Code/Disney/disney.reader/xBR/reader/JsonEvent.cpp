/**
    JsonEvent.cpp
    Greg Strange
    August 2012

    Copyright (c) 2012, synapse.com
*/


#include "standard.h"
#include "JsonEvent.h"
#include "StringLib.h"
#include "JsonParser.h"
#include "log.h"
#include <string.h>
#include <string>



using namespace Reader;


JsonEvent::JsonEvent(Json::Value& json) : _json(json)
{
}


JsonEvent::~JsonEvent()
{
}



void JsonEvent::writeAsJson(std::ostream& out, unsigned eventNumber, const char* timeStamp)
{
    _json["eno"] = eventNumber;
    _json["time"] = timeStamp;
    Json::StyledStreamWriter writer;
    writer.write(out, _json);
}


std::string JsonEvent::formatCompressed()
{
    Json::FastWriter writer;
    return writer.write(_json);
}


bool JsonEvent::matchesFilter(BandId id, int minss)
{
    UNUSED(id);
    UNUSED(minss);
    return true;
}

