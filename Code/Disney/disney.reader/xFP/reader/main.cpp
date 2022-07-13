#include <stdio.h>
#include <string.h>

#include "WebServer.h"
#include "log.h"
#include "DapConfig.h"
#include "version.h"
#include "log.h"
#include "BiometricReader.h"
#include "HttpRequest.h"
#include "RFIDReader.h"
#include "EventLogger.h"
#include "DapReader.h"
#include "HelloThread.h"
#include "FileSystem.h"
#include "TestThread.h"
#include "SensorBus.h"
#include "LightEffects.h"
#include "Sensors.h"
#include "Sound.h"
#include "GPIOTrigger.h"

#include <stdio.h>
#include <stdlib.h>
#include <curl/curl.h>
#include <signal.h>
#include <dirent.h>


#ifndef _WIN32
#define LOG_FILE_PATH   "/var/log/reader.log"
#else
#define LOG_FILE_PATH   "reader.log"
#endif


using namespace Reader;
using namespace RFID;


static TestThread testThread;
static bool _quit = false;
static bool _restart = false;
static Event _event;


static void usage()
{
    printf("Usage: dap-reader [options]\n"
           "Options:\n"
           "-h          Prints this message\n"
           "-d          Runs as a daemon\n"
           "-t          Runs in test mode\n"
           "-c <file>   Loads an alternative config file\n"
           "-v          Prints the application version\n");
}


static bool is_process_running(const char *name)
{
    DIR *proc;
    struct dirent *dir;
    char buffer[1024], path[PATH_MAX], pid[16];
    bool ret = false;
    FILE* fd;

    // determine current PID
    fd = fopen("/proc/self/stat", "r");
    memset(pid, 0, sizeof(pid));
    fread(pid, 1, sizeof(pid), fd);
    fclose(fd);

    // The first "word" is the PID so terminate on first space
    for (unsigned i = 0; i < sizeof(pid); ++i)
    {
        if (pid[i] == 0x20)
            pid[i] = 0;
    }

    proc = opendir("/proc");
    if(!proc)
        return false;
    do
    {
        dir = readdir(proc);
        if(dir)
        {
            // Ignore self and other unimportant directories
            if (isdigit(dir->d_name[0]) == 0 ||
                strcmp(dir->d_name, pid) == 0)
                continue;

            snprintf(path, sizeof(path), "/proc/%s/comm", dir->d_name);
            FILE* fd = fopen(path, "r");
            if(fd == NULL)
                continue;
            memset(buffer, 0, sizeof(buffer));
            fread(buffer, 1, sizeof(buffer) - 1, fd);
            fclose(fd);
            char *found = strstr(buffer, name);
            if(found)
            {
                ret = true;
                break;
            }
        }
    } while(dir != NULL);
    closedir(proc);

    return ret;
}


static void logReaderInfo()
{
    Json::Value info;
    DapReader::instance()->getInfo(info);
    Json::StyledWriter writer;
    std::string s = writer.write(info);
    logPuts(LOG_LEVEL_INFO, s.c_str());
}


/**
    Signal handler.
*/
#ifndef _WIN32
static void signalHandler(int sig)
{
    if (sig == SIGTERM || sig == SIGINT || sig == SIGHUP)
    {
        _quit = true;
    }
    else if (sig == SIGUSR1)
    {
        LOG_INFO("Received restart signal");
        _restart = true;  
    }

    _event.signal();
}
#endif



/**
    Restart the dap-reader

    Actually, this does not restart the dap-reader, it only causes us to exit
    with an exit code of 1.  Under normal conditions, the dap-reader is run
    from a monitoring script that restarts it if it quits.  This function
    sets a flag and signals the main thread which then exits and quits the
    program, allowing the monitoring script to restart us.  This method insures
    a true clean restart with all files closed, etc.
    
    If you started the dap-reader directly from the command line, then it will 
    just quit without restarting.
*/
void restart()
{
    _restart = true;
    _event.signal();
}


static void run(bool testMode)
{
    // Open log file
    logInit(LOG_FILE_PATH, "DAP-Reader " FULL_VERSION "\n");
    
    // Set the logging level from the config file
    int logLevel = DapConfig::instance()->getValue("log level", 3);
    if (logLevel)
    {
        logSetLogLevel((LogLevel)logLevel);
        logSetConsoleLevel((LogLevel)logLevel);
    }

    // Determine if we are in test mode from config file
    testMode = DapConfig::instance()->getValue("test loop", false);

    // Reference all of the singletons while we still have a single thread to insure
    // they all get created safely.
    DapReader::instance();
    WebServer::instance();
    EventLogger::instance();
    LightEffects::instance();
    Sensors::instance();
    Sound::instance();
    RFIDReader::instance();
    BiometricReader::instance();
    SensorBus::instance();
    HelloThread::instance();
    GPIOTrigger::instance();

    // Fire up the threads
    RFIDReader::instance()->start();
    Sensors::instance()->start();
    BiometricReader::instance()->start();
    EventLogger::instance()->start();
    WebServer::instance()->start();
    if (DapReader::instance()->lightsDisabled())
    {
        LOG_INFO("Light effects are off");
        GPIOTrigger::instance()->start();
    }
    else
    {
        LightEffects::instance()->start();
    }
    Sound::instance()->start();
    HelloThread::instance()->start();

    if (!DapReader::instance()->lightsDisabled())
    {
        // This might run before the LightEffects thread is fully initialized
        // so loop until it is ready.
        while (!LightEffects::instance()->showColor("green", 100))
            sleepMilliseconds(1);
    }
        
    Sound::instance()->play("success");

    // wait for threads to gather version information from xbio and RFID reader
    // then output that information to the log file and set callback to send
    // the same information at the start of each new log file.
    sleepMilliseconds(2000);
    logReaderInfo();
    logSetNewLogCallback(logReaderInfo);

    // Start test thread if in test mode
    if (testMode)
    {
        LOG_DEBUG("starting test loop");
        testThread.start();
    }

    // Sit around and wait for SIGTERM
    while (!_quit && !_restart) _event.wait(5000);

    if (_restart)
    {
        // pause a couple of seconds to allow things to wrap up.
        // Most importantly, the restart command is received via HTTP
        // This delay allows us time to send the response
        sleepMilliseconds(2000);
    }

    LOG_DEBUG("quitting reader");

    if (testMode)
    {
        testThread.stop();
    }

    // shut down the threads
    HelloThread::instance()->stop();
    WebServer::instance()->stop();
    EventLogger::instance()->stop();
    BiometricReader::instance()->stop();
    RFIDReader::instance()->stop();
    LightEffects::instance()->stop();
    Sound::instance()->stop();
    Sensors::instance()->cancel();
    GPIOTrigger::instance()->stop();

    // close log file
    LOG_DEBUG("closing and exiting\n");
    logClose();
}


int main(int argc, char** argv) 
{
    bool testMode = false;
    bool beDaemon = false;

    // parse command line arguements
    for (int i = 1; i < argc; ++i)
    {
        char* arg = argv[i];

        if (*arg == '-')
        {
            ++arg;
            switch (*arg)
            {
            case 'h':
                // print help message
                usage();
                exit(0);

            case 'd':
                // turn ourselves into a daemon
                beDaemon = true;
                break;
                
            case 't':
                // run in test mode
                testMode = true;
                break;
                
            case 'c':
                // use alternate config file
                if (++i >= argc)
                {
                    printf("Error: You must specify a file name with the '-c' option.\n\n");
                    usage();
                    exit(0);
                }
                DapConfig::instance()->open(argv[i]);
                break;

            case 'v':
                // print version and exit
                printf(FULL_VERSION"\n");
                exit(0);

            default:
                printf("Error: Invalid option -- '%s'\n", argv[i]);
                usage();
                exit(0);
            }
        }
    }

    // Don't allow multiple instances                                                                       
    if (is_process_running(argv[0]))
    {
        printf("%s is already running, exiting...\n", argv[0]);
        exit(1);
    }

    // Set up signal handler for SIGTERM
#ifndef _WIN32
    struct sigaction sa;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    sa.sa_handler = signalHandler;
    sigaction(SIGTERM, &sa, NULL);
    sigaction(SIGUSR1, &sa, NULL);
    sigaction(SIGINT, &sa, NULL);
    sigaction(SIGHUP, &sa, NULL);

    // Turn ourselves into a daemon if asked to
    if (beDaemon)
        daemon(true, false);
#endif

    // Curl initialization
    curl_global_init(CURL_GLOBAL_ALL);

    run(testMode);

    curl_global_cleanup();

    // Under normal conditions, the dap-reader is run by a monitoring script that will
    // restart the dap-reader if it quits or exits with an exit code other than 0.
    exit(_quit ? 0 : _restart ? 1 : 2);
}
