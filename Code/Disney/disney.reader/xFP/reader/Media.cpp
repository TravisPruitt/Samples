/**
 *  @file   Media.cpp - media configuration
 *  @date   June, 2012
 *  @author Corey Wharton
 *
 *  Functions for installing, removing, and managing reader media resources.
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <signal.h>
#ifndef _WIN32
#include <dirent.h>
#endif

#include "Media.h"
#include "FileSystem.h"
#include "DapConfig.h"
#include "Mutex.h"
#include "log.h"


namespace Reader
{


namespace Media
{



static Mutex _mutex;


const std::string getMediaHash()
{
    return DapConfig::instance()->getValue("media hash", "");
}


void setMediaHash(const char* hash)
{
    DapConfig::instance()->setValue("media hash", hash ? hash : "");
    DapConfig::instance()->save();
}


const std::string getIdleEffectName()
{
    return DapConfig::instance()->getValue("idle effect", "");
}


void setIdleEffectName(const char* name)
{
    DapConfig::instance()->setValue("idle effect", name);
    DapConfig::instance()->save();
}

#ifndef _WIN32
static bool _removeMediaPackage()
{
    // no point in rewriting rm
    std::string cmd = "rm -fr ";
    cmd += FileSystem::getMediaPath();
    system(cmd.c_str());

    // restore media directory
    int result = mkdir(FileSystem::getMediaPath(), S_IRWXU|S_IRWXG|S_IROTH|S_IXOTH);
    if ( result < 0 )
        LOG_WARN("Unable to create media directory");

    // Old hash is invalidated
    setMediaHash("");

    return result == 0;
}


bool removeMediaPackage()
{
    _mutex.lock();
    void (*prev_handler)(int) = signal(SIGCHLD, SIG_DFL);
    bool result = _removeMediaPackage();
    signal(SIGCHLD, prev_handler);
    _mutex.unlock();
    return result;
}


bool installMediaPackage(const char* package)
{
    _mutex.lock();

    // Save & set SIGCHLD handler to make sure we can get return
    // codes/exit status from system()
    void (*prev_handler)(int) = signal(SIGCHLD, SIG_DFL);

    _removeMediaPackage();
    
    // Untar the archive
    std::string cmd = "tar -C ";
    cmd += FileSystem::getMediaPath();
    cmd += " -xf ";
    cmd += package;
    int result = system(cmd.c_str());

    // Restore the signal handler
    signal(SIGCHLD, prev_handler);

    _mutex.unlock();

    // Report any success/failure
    return (WIFEXITED(result) && WEXITSTATUS(result) == 0);
}


static void getFileList(Json::Value& list, const char* path)
{
    // Open directory if it exists
    DIR *dp;
    if ((dp = opendir(path)) == NULL)
        return;

    // Add files to list after stripping extension
    std::string filename;
    struct dirent *dirp;
    while ((dirp = readdir(dp)) != NULL)
    {
        // Skip directories
        if ( dirp->d_type == DT_DIR )
            continue;

        filename = dirp->d_name;

        // Strip prefix
        size_t index;
        if ((index = filename.find(".")) != std::string::npos)
            filename.erase(index);

        // add it to list
        list.append(filename);
    }
    closedir(dp);
}
#endif


void getMediaInventory(Json::Value& inv)
{
#ifndef _WIN32
    inv["package"]["media hash"] = getMediaHash();

    const char* paths[] = { FileSystem::getAppPath(), FileSystem::getMediaPath() };
    const char* sections[] = { "defaults", "package" };
    const char* types[] = { "ledscripts", "sounds", "xbio" };
    std::string path;

    for (int i = 0; i < 2; ++i)
    {
        for (int j = 0; j < 3; ++j)
        {
            path = paths[i];
            path += "/";
            path += types[j];
            getFileList(inv[sections[i]][types[j]], path.c_str());
        }
    }
#endif
}


} // namespace Media


} // namespace Reader
