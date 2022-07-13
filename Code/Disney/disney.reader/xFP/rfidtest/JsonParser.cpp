/**
    @file   JsonParser.cpp
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#include "standard.h"
#include "JsonParser.h"


using namespace JsonParser;



bool JsonParser::parseInt(Json::Value& json, const char* name, int& result)
{
    if (!json.isMember(name))
        return false;

    if (json[name].isInt())
    {
        result = json[name].asInt();
        return true;
    }

    if (json[name].isString())
    {
        std::string s = json[name].asString();
        if (s.size() > 0 && isdigit(s[0]))
        {
            result = strtol(s.c_str(), NULL, 0);
            return true;
        }
    }

    return false;
}


bool JsonParser::parseBool(Json::Value& json, const char* name, bool& result)
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


