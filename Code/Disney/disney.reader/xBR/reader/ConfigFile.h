/**
 *  ConfigFile.h - Configuration file
 *  July 26, 2011
 *  Greg Strange
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  The config file is a singleton with the data in the config file exposed as a Json::Value object.
 *  See jsoncpp documentation for how to access and modify the data.
*/


#ifndef __CONFIG_FILE_H
#define __CONFIG_FILE_H


#include <json/json.h>
#include "Mutex.h"


namespace Reader
{


class ConfigFile
{
public:
    ConfigFile(const char* path);
    ~ConfigFile();

    bool hasErrors();

    std::string getValue(const char* name, const char* defaultValue);
    int getValue(const char* name, int defaultValue);
    bool getValue(const char* name, bool defaultValue);
    double getValue(const char* name, double defaultValue);
    Json::Value getJsonValue(const char* name);

    std::vector<int> getIntArray(const char* name);
    std::vector<double> getDoubleArray(const char* name);

    void setValue(const char* name, int value);
    void setValue(const char* name, std::string& value);
    void setValue(const char* name, const char* value);
    void setValue(const char* name, bool value);
    void setValue(const char* name, double value);

    void open(const char *path);
    bool save();

private:  // methods
    // not implemented
    ConfigFile(const ConfigFile&);
    const ConfigFile& operator=(const ConfigFile&);

private:  // data
    std::string _path;
    Json::Value _values;
    Mutex _fileMutex;
    bool _hasErrors;
};


} // namespace Reader

#endif
