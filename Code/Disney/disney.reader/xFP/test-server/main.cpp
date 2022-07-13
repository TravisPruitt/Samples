#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "standard.h"
#include "WebServer.h"
#include "log.h"
#include "version.h"
#include "log.h"
#include "Console.h"
#include "ticks.h"
#include "HttpRequest.h"
#include "globals.h"

#include <ctype.h>
#include <string>


using namespace Reader;


std::string g_ipAddress;
std::string g_readerUrl;
uint32_t g_updateInterval = 0;
uint32_t g_maxEventsPerMsg = 0;
int g_minSignalStrength = 0;
bool g_verbose = false;



static void doHttpRequest(const char* verb, const char* cmdLine)
{
    while (*cmdLine && isspace(*cmdLine))    ++cmdLine;

    std::string url;
    while (*cmdLine && !isspace(*cmdLine))
        url += *cmdLine++;

    while (*cmdLine && isspace(*cmdLine))    ++cmdLine;
    const char* fileName = cmdLine;

    if (!isdigit(url[0]))
    {
        url = g_readerUrl + "/" + url;
    }

    LOG_DEBUG("%s %s to %s\n", verb, fileName, url.c_str());

    HttpRequest req(verb, url.c_str());
    if (fileName && *fileName)
    {
        req.setPayloadFromFile(fileName);
    }
    if (req.send())
        LOG_INFO("request sent\n");
    else
        LOG_INFO("request failed\n");

    int resultCode = req.getResponseCode();
    LOG_INFO("Result code %d\n", resultCode);
    LOG_INFO("Response:\n");
    logPuts(LOG_LEVEL_INFO, req.getResponsePayloadAsString().c_str());
}



static void runConsole()
{
    char buf[1000];
    bool quit = false;

    printf(">");
    while (!quit)
    {
        if (Console::instance()->getLine(buf, sizeof(buf)))
        {
            if (strcasecmp(buf, "quit") == 0 || strcasecmp(buf, "exit") == 0)
                quit = true;
            else if (strncasecmp(buf, "put ", 4) == 0)
                doHttpRequest("put", buf+4);
            else if (strncasecmp(buf, "get ", 4) == 0)
                doHttpRequest("get", buf+4);
            else if (strncasecmp(buf, "post ", 5) == 0)
                doHttpRequest("post", buf+5);
            else if (strncasecmp(buf, "delete", 6) == 0)
                doHttpRequest("delete", buf+6);
            printf(">");
        }

        sleepMilliseconds(100);
    }
}


void usage()
{
    printf("usage:\n");
    printf("    test-server [options]\n");
    printf("options:\n");
    printf("    -i <out IP address>\n");
    printf("    -m <max events>\n");
    printf("    -r <reader URL>\n");
    printf("    -s <min signal strength>\n");
    printf("    -u <update interval>\n");
    printf("    -v verbose\n");
    printf("examples:\n");
    printf("    test-server -i 127.0.0.1\n");
    printf("    test-server -i 10.75.3.199 -r 10.75.3.124:8080\n");
}

int main(int argc, char** argv)
{
    for (int i = 1; i < argc; ++i)
    {
        if (argv[i][0] == '-')
        {
            switch (argv[i][1])
            {
            case 'i':
            case 'I':
                {
                    char *url = argv[i] + 2;
                    if (*url == '\0')
                    {
                        if (++i >= argc)
                        {
                            usage();
                            exit(1);
                        }
                        url = argv[i];
                    }
                    g_ipAddress = argv[i];
                }
                break;
            case 'r':
            case 'R':
                {
                    char *url = argv[i] + 2;
                    if (*url == '\0')
                    {
                        if (++i >= argc)
                        {
                            usage();
                            exit(1);
                        }
                        url = argv[i];
                    }
                    g_readerUrl = url;
                }
                break;

            case 's':
            case 'S':
                {
                    char* s = argv[i] + 2;
                    if (*s == '\0')
                    {
                            if (++i >= argc)
                            {
                                usage();
                                exit(1);
                            }
                            s = argv[i];
                    }
                    if ( (*s != '-') && !isdigit(*s))
                    {
                        usage();
                        exit(1);
                    }
                    g_minSignalStrength = atoi(s);
                }
                break;


            case 'u':
            case 'U':
                {
                    char* s = argv[i] + 2;
                    if (*s == '\0')
                    {
                            if (++i >= argc)
                            {
                                usage();
                                exit(1);
                            }
                            s = argv[i];
                    }
                    if (!isdigit(*s))
                    {
                        usage();
                        exit(1);
                    }
                    g_updateInterval = atoi(s);
                }
                break;

            case 'm':
            case 'M':
                {
                    char* s = argv[i] + 2;
                    if (*s == '\0')
                    {
                            if (++i >= argc)
                            {
                                usage();
                                exit(1);
                            }
                            s = argv[i];
                    }
                    if (!isdigit(*s))
                    {
                        usage();
                        exit(1);
                    }
                    g_maxEventsPerMsg = atoi(s);
                }
                break;

            case 'v':
            case 'V':
                g_verbose = true;
                break;

            default:
                usage();
                exit(1);
            }

        }
        else
        {
            usage();
            exit(1);
        }
    }

    logInit("test-server.log", "Test server " VERSION "\n");

    LOG_DEBUG("running\n");

    WebServer::instance()->start();

    runConsole();

    WebServer::instance()->stop();

    logClose();

    return 0;
}

