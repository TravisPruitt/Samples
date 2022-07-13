/*
 * main.c
 *
 *  Created on: Jun 19, 2011
 *      Author: mvellon
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <errno.h>

#include <curl/curl.h>

#include "mongoose.h"
#include "reader.h"

struct Knowledge
{
    int iId;
    char *pszUrl;
    struct Reader **ppReaders;
    size_t sReaders;
};


static void Usage()
{
    fputs(
"Reader - NGE reader simulator\n"
"Usage:   Reader [OPTIONS] FILENAME\n"
"Where FILENAME is an XML configuration (document separately)\n"
"OPTIONS:\n"
"  --name      Name of reader defined in XML configuration to simulate.\n"
"              If this option and --xbrc are not present, all entries are\n"
"              simulated.\n"
"  --xbrc      Name of controller defined in XML configuration to simulate.\n"
"              If this option and --name are not present, all entries are\n"
"              simulated.\n"
"  --verbose   Runs in verbose mode\n"
"  --help,-?   Display this usage information\n", stderr);
}

static int
ProcessCommandLine(
    struct Options *pOptions,
    int argc,
    char** argv)
{
    int i;
    for (i=1; i<argc; i++)
    {
        char *psz = argv[i];

        if (psz[0]=='-')
        {
            if (strcmp(psz, "--help")==0 ||
                strcmp(psz, "-?")==0)
            {
                Usage();
            }
            else if (strcmp(psz, "--xbrc")==0)
            {
                if (i==argc-1)
                {
                    Usage();
                    return 1;
                }
                pOptions->pszControllerName = argv[++i];
            }
            else if (strcmp(psz, "--name")==0)
            {
                if (i==argc-1)
                {
                    Usage();
                    return 1;
                }
                pOptions->pszReaderName = argv[++i];
            }
            else if (strcmp(psz, "--realmac")==0)
            {
                pOptions->bUseRealMac = 1;
            }
            else if (strcmp(psz, "--verbose")==0)
            {
                pOptions->bVerbose = 1;
                printf("** Verbose mode\n");
            }
        }
        else
        {
            if (NULL==pOptions->pszConfigFile)
                pOptions->pszConfigFile = psz;
            else
            {
                fprintf(stderr, "Invalid arguments -- multiple configuration files\n");
                return 1;
            }
        }
    }
    return 0;
}

static void
InterruptHandler(int sig)
{
}

static void
WaitForSignals()
{
    sigset_t waitSet;
    siginfo_t info = {0};
    struct sigaction action;

    memset(&action, 0, sizeof(action));

    sigfillset(&waitSet);

    action.sa_handler = InterruptHandler;
    action.sa_flags = 0;
    sigaction(SIGINT, &action, NULL);

    for (;;)
    {
        int ret = sigwaitinfo(&waitSet, &info);
        if (ret < 0 && errno == EINTR)
        {
            continue;
        }

        if (info.si_signo == SIGINT)
        {
            fprintf(stderr, "Got Ctrl-C\n");
            break;
        }
    }
}

/*
 */
static void *
Callback(enum mg_event event,
         struct mg_connection *pconn,
        const struct mg_request_info *pr)
{
    struct Knowledge *pKnow = pr->user_data;

    if (!strncmp(pr->uri, "/reader/", 8))
    {
        size_t i;
        char szName[256];
        sscanf(pr->uri, "/reader/%s", szName);

        for(i = 0; i < pKnow->sReaders; i++)
        {
            struct Reader *pReader = pKnow->ppReaders[i];
            if (!strcmp(ReaderName(pReader), szName))
            {
                int bFailedLastSend = 0;
                unsigned long long goodSendCount = 0;
                unsigned long long badSendCount = 0;
                int bFailedLastHello = 0;

                ReaderStats(pReader, &bFailedLastSend, &goodSendCount, &badSendCount, &bFailedLastHello);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: application/json\r\n\r\n"
                     "{ \"failedLastSend\":%s,"
                     " \"goodSendCount\":%llu,"
                     " \"badSendCount\":%llu,"
                     " \"failedLastHello\":%s }\r\n", bFailedLastSend ? "true" : "false", goodSendCount, badSendCount, bFailedLastHello ? "true" : "false");
                return (void*) 1;
            }
        }
        return NULL;
    }
    else if (!strcmp(pr->uri, "/readers"))
    {
        size_t i;

        mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                         "Content-Type: application/json\r\n\r\n");

        mg_printf(pconn, "{ \"id\": \"%d\", \"readers\": [ ", pKnow->iId);
        for (i = 0; i < pKnow->sReaders; i++)
        {
            char szUrl[256];
            struct Reader *pReader = pKnow->ppReaders[i];
            const char *pszName = ReaderName(pReader);
            enum EventType eType = ReaderType(pReader);
            const char *pszType = "";

            if (eType == EventTypeBandPing)
                pszType = "lrr";
            else if (eType == EventTypeBandTap)
                pszType = "tap";
            else if (eType == EventTypeCarPing)
                pszType = "car";

            sprintf(szUrl, "http://localhost:7000/reader/%s", pszName);
            if (i != 0)
                mg_printf(pconn, ",");
            mg_printf(pconn, "{ \"name\": \"%s\", \"type\": \"%s\", \"url\":\"%s\" }", pszName, pszType, szUrl);
        }
        mg_printf(pconn, "]}");

        return 1;
    }
 
    return NULL;
}


int
main(
    int argc,
    char** argv
    )
{
    int err = 0;
    struct Options options = { NULL, NULL, 0, 0 };
    struct Knowledge know;

    struct timeval tvNow;
    size_t i;
    struct mg_context *mg = NULL;
    char *dir = NULL;

    const char *aszOptions[] =
    {
         "listening_ports", "7000",
         "document_root", NULL,
         "extra_mime_types", ".json=application/json",
         NULL
    };

    know.iId = 0;
    know.pszUrl = NULL;
    know.ppReaders = NULL;
    know.sReaders = 0;

    if (argc<2)
    {
        Usage();
        return 1;
    }

    dir = get_current_dir_name();

    aszOptions[3] = dir;
    err = ProcessCommandLine(&options, argc, argv);
    if (err!=0)
        return err;

    // if no configuration file, this is an error
    if (NULL == options.pszConfigFile)
    {
        fprintf(stderr, "No configuration file specified\n");
        return 1;
    }

    curl_global_init(CURL_GLOBAL_ALL);
    xmlInitParser();

    err = CreateReadersFromConfiguration(&options, &know.ppReaders, &know.sReaders);
    if (err != 0)
        return err;

    gettimeofday(&tvNow, NULL);
    srand(tvNow.tv_usec);

    for (i = 0; i < know.sReaders; i++)
    {
        fprintf(stdout, "[%s] Starting\n", ReaderName(know.ppReaders[i]));
        StartReader(know.ppReaders[i], &tvNow);
    }

    know.iId = rand();
//    mg = mg_start(Callback, &know, aszOptions);

    WaitForSignals();

//    mg_stop(mg);
    mg = NULL;

    for (i = 0; i < know.sReaders; i++)
    {
        fprintf(stdout, "[%s] Stopping\n", ReaderName(know.ppReaders[i]));
        StopReader(know.ppReaders[i]);
    }

    for (i = 0; i < know.sReaders; i++)
    {
        DeleteReader(know.ppReaders[i]);
    }

    free(know.ppReaders);
    know.ppReaders = NULL;
    know.sReaders = NULL;

    xmlCleanupParser();
    curl_global_cleanup();

    free(dir);
    dir = NULL;

    return err;
}


