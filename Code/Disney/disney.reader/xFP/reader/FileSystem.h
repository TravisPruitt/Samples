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

    const char* getAppPath();
    const char* getAppDataPath();
    const char* getConfigPath();
    const char* getFirmwarePath();
    const char* getWWWPath();
    const char* getMediaPath();

    bool createTempFile(char* tmpl, std::ofstream& f);
    bool fileExists(const char* path);
    long fileSize(const char* path);
    bool findFirmwareFile(const char *prefix, std::string& filename);
};

}

#endif
