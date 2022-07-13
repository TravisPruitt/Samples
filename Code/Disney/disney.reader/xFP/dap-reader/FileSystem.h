/**
    FileSystem - Provides filesystem path information and services.

    Copyright (c) 2012, synapse.com
*/

#ifndef __FILE_SYSTEM_H
#define __FILE_SYSTEM_H


#ifdef WIN32
#include <direct.h>
#else
#include <unistd.h>
#endif

#include <string>

namespace Reader
{

namespace FileSystem
{
#ifdef WIN32
    const char PathSeperator = '\\';
#else
    const char PathSeperator = '/';
#endif

    const char* getAppDataPath();
    const char* getConfigPath();
    const char* getFirmwarePath();

    bool findFirmwareFile(const char *prefix, std::string& filename);
};

}

#endif
