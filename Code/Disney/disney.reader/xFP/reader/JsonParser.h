/**
    @file   JsonParser.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef JSON_PARSER_H
#define JSON_PARSER_H

#include "json/json.h"


namespace JsonParser
{

bool parseInt(Json::Value& json, const char* name, int& result);
bool parseBool(Json::Value& json, const char* name, bool& result);

}

#endif
