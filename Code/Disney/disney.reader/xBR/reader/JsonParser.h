/**
    @file   JsonParser.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef JSON_PARSER_H
#define JSON_PARSER_H

#include "json/json.h"
#include <ostream>


namespace JsonParser
{

bool parseInt(const Json::Value& json, const char* name, int& result);
int getInt(const Json::Value& json, const char* name, int defaultValue);

bool parseBool(const Json::Value& json, const char* name, bool& result);
bool getBool(const Json::Value& json, const char* name, bool defaultValue);

std::string getString(const Json::Value& json, const char* name, const char* defaultValue);

}

#endif
