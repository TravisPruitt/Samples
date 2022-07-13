/**
    @file   gson.cpp
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/


#include "standard.h"
#include "Gson.h"
#include <vector>
#include <string>


using namespace Gson;


const Value Value::null;


Value::Value() : _isObject(false)
{
}

Value::Value(const char* str) {}

Value::Value(std::string& str) {}

Value::~Value() {}


Value& Value::operator[](const char* key)
{
    if (_isObject)
    {
        for (size_t i = 0; i < _members.size(); ++i)
            if (strcasecmp(_members[i].key.c_str(), key) == 0)
                return *(_members[i].value);
        Pair pair;
        pair.key = key;
        pair.value = new Value();
        _members.push_back(pair);
        return *pair.value;
    }
    else
    {
//TODO        throw Exception("Gson value is not a member list");
    }
}

const Value& Value::operator[](const char* key) const
{
    if (_isObject)
    {
        for (int i = 0; i < _members.size(); ++i)
            if (strcasecmp(_members[i].key.c_str(), key) == 0)
                return *(_members[i].value);

        return Value::null;
    }
    else
    {
        return Value::null;
    }

}


#if 0

static void skipSpace(const char*& str)
{
    while (*str && isspace(*str)) ++str;
}


void Value::parseMembers(const char*& str)
{
    std::string key;
    std::string value;

    skipSpace(str);
    parseKey(str, key);
    if (*str == ':')
    {
        ++str;
        parseValue(str, value);
    }
    _pairs.push_back(Pair(key, value));

    if (*str == '"')
    {
        ++str;
        while (*str && *str != '"')
        {
            key += *str++;
        }
    }
    else if (*str != ':')
    {
        while (*str && *str != ':')
            key += *str++;

        rtrim(key);
    }
    else
    {
        // TODO
    }
}


bool parse(const char* str)
{
    skipSpace(str);
    if (*str == '{')
    {
        ++str;
        skipSpace(str);
    }

    parseMembers(str);
}

#endif
