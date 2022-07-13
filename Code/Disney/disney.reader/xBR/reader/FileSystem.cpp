/**
    FileSystem.cpp - Provides filesystem path information and services.

    Copyright (c) 2012, synapse.com
*/


#include "FileSystem.h"

#include "Mutex.h"

#ifdef WIN32
#include <Windows.h>

#else
#include <sys/stat.h>
#include <unistd.h>
#include <sys/param.h>
#include <dirent.h>
#include <stdlib.h>

#endif

using namespace Reader;


const char* FileSystem::getAppDataPath()
{
    static std::string path;
    static Mutex mutex;

    mutex.lock();

    if (path.empty())
    {
#ifndef WIN32
        static const char* AppDataPath = "/var/mayhem";

        // create the directory if it does not exist
        struct stat st;
        if (stat(AppDataPath, &st) != 0)
            mkdir(AppDataPath, S_IRWXU | S_IRWXG | S_IRWXO | S_ISUID);
        path = AppDataPath;

#else
        char buf[MAX_PATH];
        GetModuleFileName(0, buf, sizeof(buf)-1);
        path = buf;
        path = path.substr(0, path.rfind("\\"));
#endif
    }

    mutex.unlock();

    return path.c_str();
}


const char* FileSystem::getConfigPath()
{
    return getAppDataPath();
}


const char* FileSystem::getFirmwarePath()
{
#ifndef WIN32
    return "/mayhem/firmware/";
#else
    std::string fp = getAppDataPath();
    fp += "\\";
    return fp.c_str();
#endif
}


const char* FileSystem::getWWWPath()
{
#ifndef WIN32
    return "/mayhem/www";
#else
    return getAppDataPath();
#endif
}


/**
 *  Returns true if the specified file exists and false
 *  otherwise.
 *
 *  @param   path  Path to file to test for existence.
 *
 *  @return  True if the file exists and false oherwise.
 */
bool FileSystem::fileExists(const char* path)
{
#ifndef WIN32
    struct stat st;
    return ( stat(path, &st) == 0 );
#else
    DWORD fileAttr = GetFileAttributes(path);
    return ( fileAttr != 0xFFFFFFFF );
#endif
}


/**
 *  Searches the firmware directory for a file with the specified prefix.
 *
 *  Note: If multiple files match the given prefix, this function will
 *        return on the first file found.
 *
 *  @param   prefix    Prefix of the file to search for
 *  @param   filename  reference to a string object in which the
 *                     full path to the file will be placed.
 *
 *  @return  Returns true if a file is located.
 */
bool FileSystem::findFirmwareFile(const char *prefix, std::string& filename)
{
#ifndef WIN32
    // Open firmware directory if it exists
    DIR *dp;
    struct dirent *dirp;
    if ((dp = opendir(FileSystem::getFirmwarePath())) == NULL)
    {
        return false;
    }

    // Search for a file with the given prefix
    bool found = false;
    while ((dirp = readdir(dp)) != NULL)
    {
        // Skip directories
        if ( dirp->d_type == DT_DIR )
            continue;

        filename = dirp->d_name;
        if (filename.find(prefix) == 0)
        {
            // Insert directory path
            filename.insert(0, getFirmwarePath());
            found = true;
            break;
        }
    }
    closedir(dp);

    return found;

#else
    WIN32_FIND_DATA fd;
    HANDLE fh;

    std::string searchStr = prefix;
    searchStr.insert(0, getFirmwarePath());
    searchStr += "*";

    fh = FindFirstFile(searchStr.c_str(), &fd);
    if (fh == INVALID_HANDLE_VALUE)
        return false;
    FindClose(fh);

    // Found a match - append firmware directory
    filename = getFirmwarePath();
    filename += fd.cFileName;

    return true;

#endif
}
