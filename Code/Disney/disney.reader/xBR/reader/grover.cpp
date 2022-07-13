//*****************************************************************************
// grover.cpp 
//
//	xBand Reader: Main code entry point.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************
//

// C/C++ Std Lib
#include <cstring>
#include <cstdio>
#include <stdlib.h>
#include <signal.h>
#include <dirent.h>
// System Lib
// Global-App
#include "grover.h"
#include "log.h"
// Local-ish
#include "sysIface.h"
#include "config.h"
#include "webServ.h"
#include "version.h"
#include "EventLogger.h"
#include "EventBouncer.h"
#include "ThreadEvent.h"
#include "HelloThread.h"
#include "Receiver.h"
#include "Transmitter.h"
#include "Radios.h"
#include "radioFlash.h"
#include "ReaderInfo.h"


#define LOG_FILE_PATH  "/var/log/reader.log"


using namespace Reader;


static bool _quit = false;
static bool _restart = false;
static ThreadEvent _event;


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



/**
    Restart the dap-reader

    Actually, this does not restart the reader, it only causes us to exit
    with an exit code of 1.  Under normal conditions, the reader is run
    from a monitoring script that restarts it if it quits.  This function
    sets a flag and signals the main thread which then exits and quits the
    program, allowing the monitoring script to restart us.  This method insures
    a true clean restart with all files closed, etc.
    
    If you started the reader directly from the command line, then it will 
    just quit without restarting.
*/
void grover_restart()
{
    _restart = true;
    _event.signal();
}


bool grover_quitting()
{
    return _quit;
}


static void usage()
{
        printf(	"Usage: grover [options]\n"
				"Options:\n"
				"-h        Prints this message\n"
				"-c <file> Loads an alternative config file\n"
				"-v        Prints the application version\n"
				"-x        set the xBRC server url, /hello is appended to init communications:\n"
				"          ... -x http://<ip | domain>[:<port>][/<base path>]\n"
				"          ... -x <no parameter> ==> use /var/lib/dhcp/xbrc-url\n"
				"          ex: 	grover -x http://10.20.30.40\n"
               );
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


int main(int argc, char *argv[])
{
	char *xbrcUrl = NULL;
	
	// parse command line
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

			case 'c':
				// use alternate config file
				if (++i >= argc)
				{
					printf("Error: You must specify a file name with the '-c' option.\n\n");
					usage();
					exit(0);
				}
                Config::instance()->open(argv[i]);
				break;

			case 'x':
				// use alternate xBRC address (overrides the xbrc-url file)
				// null path forces unit to lose a previous override
				if ((i+1) >= argc || argv[i+1][0] == '-')
					xbrcUrl = (char *) "";
				else
					xbrcUrl = argv[++i];
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

    struct sigaction sa;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    sa.sa_handler = signalHandler;
    sigaction(SIGTERM, &sa, NULL);
    sigaction(SIGUSR1, &sa, NULL);
    sigaction(SIGINT, &sa, NULL);
    sigaction(SIGHUP, &sa, NULL);

    // Curl initialization
    curl_global_init(CURL_GLOBAL_ALL);

    // Open log file
    logInit(LOG_FILE_PATH, "Grover, long range reader " FULL_VERSION "\n");
    
    // Set the logging level from the config file
    int logLevel = Config::instance()->getValue("log level", 3);
    if (logLevel)
    {
        logSetLogLevel((LogLevel)logLevel);
        logSetConsoleLevel((LogLevel)logLevel);
    }

    // Reference singletons to be sure they get created safely before we start extra threads
    ReaderInfo::instance();
    EventLogger::instance();
    HelloThread::instance();
    Receiver::instance();
    EventBouncer::instance();

    // Initialize things
    Radios::init();
    radioFlash::checkForUpdates();
    Receiver::instance()->init();
    Transmitter::init();

    // Fire up threads
    webServ::start();
    EventLogger::instance()->start();
    HelloThread::instance()->start();
    Receiver::instance()->start();
    EventBouncer::instance()->start();

    while (!_quit && !_restart) 
        _event.wait(5000);

	// STOP
    EventLogger::instance()->cancel();
    HelloThread::instance()->cancel();
    EventBouncer::instance()->cancel();
	webServ::stop();
    curl_global_cleanup();

	LOG_INFO("ending");

    // Under normal circumstances, the xBR is run by a monitoring script that will
    // restart the xBR if it quits or exits with an exit code other than 0.
    exit(_quit ? 0 : _restart ? 1 : 2);
} 
