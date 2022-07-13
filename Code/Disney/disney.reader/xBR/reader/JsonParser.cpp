/**
    @file   JsonParser.cpp
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#include <stdlib.h>
#include <strings.h>
#include "standard.h"
#include "JsonParser.h"
#include "log.h"


using namespace JsonParser;



int JsonParser::getInt(const Json::Value& json, const char* name, int defaultValue)
{
    int result;

    if (!parseInt(json, name, result))
        result = defaultValue;
    return result;
}


bool JsonParser::parseInt(const Json::Value& json, const char* name, int& result)
{
    if (!json.isMember(name))
        return false;

    if (json[name].isInt())
    {
        result = json[name].asInt();
        return true;
    }

LOG_DEBUG("%s is not an int", name);

    if (json[name].isString())
    {
        std::string s = json[name].asString();
        if (s.size() > 0 && (isdigit(s[0]) || s[0] == '-') )
        {
            result = strtol(s.c_str(), NULL, 0);
            return true;
        }
    }

    return false;
}


bool JsonParser::getBool(const Json::Value& json, const char* name, bool defaultValue)
{
    bool result;

    if (!parseBool(json, name, result))
        result = defaultValue;
    return result;
}


bool JsonParser::parseBool(const Json::Value& json, const char* name, bool& result)
{
    if (!json.isMember(name))
        return false;

    if (json[name].isBool())
    {
        result = json[name].asBool();
        return true;
    }

    if (json[name].isString())
    {
        std::string s = json[name].asString();
        if (strcasecmp(s.c_str(), "true") == 0)
        {
            result = true;
            return true;
        }
        else if (strcasecmp(s.c_str(), "false") == 0)
        {
            result = false;
            return true;
        }
    }

    return false;
}


std::string JsonParser::getString(const Json::Value& json, const char* name, const char* defaultValue)
{
    if (json.isMember(name) && json[name].isString())
        return json[name].asString();
    else
        return defaultValue;
}
