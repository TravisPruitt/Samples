/**
    @file   gson.h
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com


    Usage:

        Gson::Value value(input);
        value["rocks"] = 5;

        if (value.hasInt("rocks"))
            rocks = value["rocks"].asInt();

        or
            rocks = value.getInt("rocks", defaultValue);


    Accepts "sloppy json" input.  This includes properly formed json + json without commas
    and non-string values enclosed in quotes.
   
    Differences from jsoncpp

    1) Order of members is preserved
*/

#ifndef GSON_H
#define GSON_H

#include <vector>
#include <string>

// Not using [] operators because of side effects where elements must be created when accessing
// if they don't already exists. 
namespace Gson
{
    class Value
    {
    public:
        Value();
        Value(const char* str);
        Value(std::string& str);
        ~Value();

        Value &operator[](const char *key);
        const Value& Value::operator[](const char* key) const;

        Value &operator=(const Value &other);
        Value &operator=(int value);

        bool hasMember(const char* key);

        bool isInt();
        bool hasInt(const char* key);
        int asInt(int defaultValue = 0);
        int getInt(const char* key, int defaultValue = 0);

        static const Value null;

    private:
        bool _isObject;
        std::string _preComment;
        std::string _value;
        std::string _postComment;
        struct Pair { std::string key; Gson::Value* value; };
        std::vector<Pair> _members;
    };
}

#endif
