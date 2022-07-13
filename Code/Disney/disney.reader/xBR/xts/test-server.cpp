//*****************************************************************************
// test-server.cpp 
//
//	for xBand Reader: Server to exercise a network setup with xBR readers
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Aug 2011, Synapse.com
//
//	- Receives "hello" from xBR & responds by setting it's name and stream-to address
//	- Receives "stream" posts and stores the data to a event logging file.
//
//  - This device will act as DHCP server. 
//
//*****************************************************************************

// C/C++ Std Lib
#include <cstring>
#include <string>
#include <cstdio>

// System Lib
#include <sys/types.h>

#ifndef _WIN32
#include <ifaddrs.h>
#include <netinet/in.h> 
#include <arpa/inet.h>
#include <signal.h>
#endif

// Global-App
#include "test-server.h"
#include "logging.h"
#include "config.h"

// Local-ish
#include "sendHttp.h"
#include "webServ.h"
#include "version.h"


int getIpAddr (char * buffer);


namespace reader
{
	bool streamSet = false;
}


#ifndef _WIN32
namespace signals
{
	pid_t app_process_id;
	
	void handler(int sig)
	{
		if (sig == SIGTERM)
			exit(0);
	}

	void init(pid_t mypid)
	{
		app_process_id = mypid;
		
		struct sigaction sa;
		sigemptyset(&sa.sa_mask);
		sa.sa_flags = 0;
		sa.sa_handler = handler;
		sigaction(SIGTERM, &sa, NULL);
	}
	
	void send(int sig)
	{
		kill(app_process_id,sig);
	}
}
#endif

namespace app
{
	void exitHandler( int status, void *arg)
	{ 
		UNUSED (status);
		UNUSED (arg);
		
		WARNING("Exiting via exit handler\n");
	}

	void sigHandler(int sig)
	{
		UNUSED (sig);
		WARNING("Exiting via signal");
		exit(1);	// which will turn around and call the exitHandlers.
	}

	void init()
	{
#ifndef _WIN32
		on_exit(exitHandler, NULL);

		/* Establish handler for SIGINT */
		if (signal(SIGINT, sigHandler) == SIG_ERR)
			ABORT("could not set up signal ahndler");
#endif
	} // init()
} // namespace app


static void usage()
{
        printf(	"Usage: test-server [options]\n"
				"Options:\n"
				"-h        Prints this message\n"
				"-c <file> Loads an alternative config file\n"
				"-v        Prints the application version\n"
               );
}

int main(int argc, char *argv[])
{
#ifndef _WIN32	
	signals::init(getpid()); // setup a signal handler for SIGTERM (which just exits).
#endif

	app::init();	// register exit handlers and default signal handling.
	
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
				config::configFilePath = argv[i];
				break;

			case 'v':
				// print version and exit
				printf( FULL_VERSION "\n");
				exit(0);

			default:
				printf("Error: Invalid option -- '%s'\n", argv[i]);
				usage();
				exit(0);
			}
		}
	}

    // INITIALIZATION
	
	printf("READER (" READER_TYPE ")\n");
	printf("VERSION: " FULL_VERSION "\n");
	
	logs::init(	/*use timestamps*/ true, /*use tags*/ true);
	
	config::init();	// loads plus applys defaults to missing members.
	logs::read_config(); // must be preceeded by config::init()

	webServ::start();

	user_input::init();

    logs::prompt("=>"); //prompt
	while(1)
	{
		if (user_input::process())
            logs::prompt("=>"); //prompt
	}

	// STOP
	WARNING("Application Exiting\n");
	// key::stop();		// uses exit handlers
	// webServ::stop();
	
	return EXIT_SUCCESS;
} 

int getIpAddr (char * buffer) {
#ifndef _WIN32
    struct ifaddrs * ifAddrStruct=NULL;
    struct ifaddrs * ifa=NULL;
    void * tmpAddrPtr=NULL;

    getifaddrs(&ifAddrStruct);

	char *p = buffer; *p = '\0';

    for (ifa = ifAddrStruct; ifa != NULL; ifa = ifa->ifa_next) {
        if (ifa ->ifa_addr->sa_family==AF_INET) { // check it is IP4
            // is a valid IP4 Address
            tmpAddrPtr=&((struct sockaddr_in *)ifa->ifa_addr)->sin_addr;
            char addressBuffer[INET_ADDRSTRLEN];
            inet_ntop(AF_INET, tmpAddrPtr, addressBuffer, INET_ADDRSTRLEN);

            if(		strstr(ifa->ifa_name, "eth") != NULL) // is eth device
            {
            	strcat(p, addressBuffer); p += strlen(p); *p++ = ' ';
            	printf("%s IP Address %s\n", ifa->ifa_name, addressBuffer);
            }
        } else if (ifa->ifa_addr->sa_family==AF_INET6) { // check it is IP6
            // is a valid IP6 Address
            tmpAddrPtr=&((struct sockaddr_in6 *)ifa->ifa_addr)->sin6_addr;
            char addressBuffer[INET6_ADDRSTRLEN];
            inet_ntop(AF_INET6, tmpAddrPtr, addressBuffer, INET6_ADDRSTRLEN);
            // ---  no ipv6 right now. ----
            /*
             strcat(p, addressBuffer); p += strlen(p); *p++ = ' ';
             printf("%s IP Address %s\n", ifa->ifa_name, addressBuffer);
            */
        } 
    }
    *p++ = '\0';
     
    if (ifAddrStruct!=NULL) 
		freeifaddrs(ifAddrStruct);
#else
    strcpy(buffer, "unknown");
#endif
    return strlen(buffer);
}


std::string format (const char *fmt, ...)
{
    va_list ap;
    va_start (ap, fmt);
    std::string buf = vformat (fmt, ap);
    va_end (ap);
    return buf;
}

std::string vformat(const char *fmt, va_list ap)
{
	// Allocate a buffer on the stack that's big enough for us almost
	// all the time.  Be prepared to allocate dynamically if it doesn't fit.
	size_t size = 1024;
	char stackbuf[1024];
	std::vector<char> dynamicbuf;
	char *buf = &stackbuf[0];

	while (1) {
		// Try to vsnprintf into our buffer.
		int needed = vsnprintf (buf, size, fmt, ap);
		// NB. C99 (which modern Linux and OS X follow) says vsnprintf
		// failure returns the length it would have needed.  But older
		// glibc and current Windows return -1 for failure, i.e., not
		// telling us how much was needed.

		if (needed <= (int)size && needed >= 0) {
			// It fit fine so we're done.
			return std::string (buf, (size_t) needed);
		}

		// vsnprintf reported that it wanted to write more characters
		// than we allotted.  So try again using a dynamic buffer.  This
		// doesn't happen very often if we chose our initial size well.
		size = (needed > 0) ? (needed+1) : (size*2);
		dynamicbuf.resize (size);
		buf = &dynamicbuf[0];
	} // while
	
} // vformat()
