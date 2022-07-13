/**
    ConfigFile.h - Configuration file
    July 26, 2011
    Greg Strange

    Copyright (c) 2011, synapse.com

    The config file is a singleton with the data in the config file exposed as a Json::Value object.
    
    The config file contents are formatted as Json.  The Json::value object allows you to easily
    access key/value pairs using [<name>] referencing.  For example, you can access the
    reader name as:

        ConfigFile::instance()->_values["name"].asCString();

    You can change the reader name with:

        ConfigFile::instance()->_values["name"] = "charlie";

    And you can overwrite the config file with ConfigFile::instance()->save();

    See jsoncpp documentation for more information on how to access an modify the JSON data.
*/


#include "standard.h"
#include "ConfigFile.h"
#include <json/json.h>
#include "log.h"
#include "Mutex.h"

#ifndef _WIN32
#include <string.h>
#endif

#include <iostream>
#include <fstream>
#include <string>
#include <fcntl.h>
#include <sys/types.h>

#ifdef _WIN32
#include <io.h>
#endif

#ifndef _WIN32
#include <unistd.h>
#endif

using namespace Reader;


/**
    Constructor.  Reads the config file and populates the "_values" object
    with the JSON data.
*/
ConfigFile::ConfigFile(const char* path) : _hasErrors(false)
{
    open(path);
}


ConfigFile::~ConfigFile()
{
}

/**
    Open and load a specified config file, creating it if it
    doesn't already exist.
*/
void ConfigFile::open(const char *path)
{
    _path = path;

    // Read in _values saved in the config file
    LOG_DEBUG("Opening config file <%s>", _path.c_str());

    std::ifstream infile;
    infile.open(_path.c_str(), std::ifstream::in);
    if (infile.fail())
    {
        LOG_WARN("Unable to open config file");
    }
    else
    {
        Json::Reader jsonReader;
        if (!jsonReader.parse(infile, _values, true))
        {
            LOG_WARN("Unable to parse config file");
            _hasErrors = true;
        }

        infile.close();
    }
}


/**
    Returns true if errors were detected in the config file when it was parsed.
*/
bool ConfigFile::hasErrors()
{
    return _hasErrors;
}

/**
    Overwrite the config file with the current data in the "_values" object.

    To do things properly so that the config file cannot be corrupted this method
    does the following (as taught by Dr. Juha):

    1) Writes config to a temporary file
    2) calls fsync() to force writing file to flash
    3) Does a rename to the proper config file name

    rename() is an atomic operation that is guaranteed to not leave a corrupted
    file on a sudden power loss.
*/
bool ConfigFile::save()
{
    LOG_DEBUG("Saving config file <%s>", _path.c_str());

    Lock lock(_fileMutex);

    std::string tempPath = _path;
    tempPath += ".temp";

    int fd = ::open(tempPath.c_str(), O_WRONLY | O_CREAT);
    if (fd < 0)
    {
        LOG_ERROR("Unable to open config file for writing");
        return false;
    }

    Json::StyledWriter jsonWriter;
    std::string config = jsonWriter.write(_values);
    
    int bytesWritten = write(fd, config.c_str(), config.size());
    if (bytesWritten < 0)
    {
        LOG_ERROR("Error writing to config file");
        return false;
    }

    fsync(fd);
    close(fd);

#ifdef _WIN32
    unlink(_path.c_str());
#endif

    if (rename(tempPath.c_str(), _path.c_str()) < 0)
    {
        LOG_ERROR("Error renaming config file");
        return false;
    }

    return true;
}


/**
    Return a value as a pointer to a C string, or NULL if value not found.
*/
std::string ConfigFile::getValue(const char* name, const char* defaultValue)
{
    if (_values.isMember(name) && _values[name].isString())
        return _values[name].asString();
    else
        return defaultValue;
}



/**
    Get an integer value

    @return true on success, or false if value does not exist or is not an int value
*/
int ConfigFile::getValue(const char* name, int defaultValue)
{
    if (_values.isMember(name) && _values[name].isInt())
        return _values[name].asInt();
    else
        return defaultValue;
}

/**
    Get a boolean value

    @returns    true on success, false if the value does not exists

    Boolean values can be specified as integers or strings.  The recommended values are
    0/1, or "on"/"off", or "true"/"false".  However, most any value is accepted.
    Integers are considered true if they are non-zero.  Strings are considered true if
    they have the value of either "true" or "on" (ignoring case).
*/
bool ConfigFile::getValue(const char* name, bool defaultValue)
{
    bool value = defaultValue;

    if (_values.isMember(name))
    {
        if (_values[name].isString())
        {
            const char* s = _values[name].asCString();
            value = (strcasecmp(s, "true") == 0 || strcasecmp(s, "on") == 0);
        }
        else
            value = _values[name].asBool();
    }

    return value;
}

double ConfigFile::getValue(const char* name, double defaultValue)
{
    if (_values.isMember(name) && (_values[name].isDouble() || _values[name].isInt()) )
        return _values[name].asDouble();
    else
        return defaultValue;
}


Json::Value ConfigFile::getJsonValue(const char* name)
{
    if (_values.isMember(name) && _values[name].isObject())
        return _values[name];
    else
        return Json::nullValue;
}

void ConfigFile::setValue(const char* name, int value)
{
    _values[name] = value;
}

void ConfigFile::setValue(const char* name, double value)
{
    _values[name] = value;
}

void ConfigFile::setValue(const char* name, std::string& value)
{
    _values[name] = value;
}

void ConfigFile::setValue(const char* name, const char* value)
{
    _values[name] = value;
}

void ConfigFile::setValue(const char* name, bool value)
{
    _values[name] = value;
}


std::vector<int> ConfigFile::getIntArray(const char* name)
{
    std::vector<int> result;

    if (_values.isMember(name))
    {
        Json::Value value = _values[name];
        if (value.isArray())
        {
            int size = value.size();
            for (int i = 0; i < size; ++i)
            {
                if (value[i].isInt())
                {
                    result.push_back(value[i].asInt());
                }
            }
        }
    }
    return result;
}

std::vector<double> ConfigFile::getDoubleArray(const char* name)
{
    std::vector<double> result;

    if (_values.isMember(name))
    {
        Json::Value value = _values[name];
        if (value.isArray())
        {
            int size = value.size();
            for (int i = 0; i < size; ++i)
            {
                if (value[i].isDouble() || value[i].isInt())
                {
                    result.push_back(value[i].asDouble());
                }
            }
        }
    }
    return result;
}
