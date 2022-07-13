/*
 * webserver.c
 *
 *  Created on: Jun 22, 2011
 *      Author: mvellon
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <assert.h>

#include "reader-private.h"
#include "favicon.h"
#include "mongoose.h"
#include "format.h"
#include "json.h"
#include "updatestream.h"
#include "priority.h"


#define  MG_NOT_HANDLED (NULL)
#define  MG_OK ((void*)200)


struct Handler
{
    const char *uri;
    void *(*pfnHandler)(struct Reader *, struct mg_connection *, const struct mg_request_info *);
};

void Print(
    struct Reader *pReader,
    struct mg_conection *pconn,
    const struct mg_request_info *pr)
{
    char *buf = NULL;
    size_t n = 0;

    if (my_mg_read(pconn, &buf, &n))
    {
        fprintf(stderr, "[%s] %s %s %s\n", pReader->pszName, pr->request_method, pr->uri, pr->query_string ? pr->query_string : "");
    }
    else
    {
        fprintf(stderr, "[%s] %s %s %s\n%s\n", pReader->pszName, pr->request_method, pr->uri, pr->query_string ? pr->query_string : "", buf);
        free(buf);
    }
}

void*
ReturnOK(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr
    )
{
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\nOK\r\n");
    return (void*) 1;
}

int my_mg_read(struct mg_connection *pconn, char **ppResult, size_t *pLen)
{
    char buf[1024];
    size_t n;
    size_t CurrentSize = 0;
    char *result = NULL;
    int content_len = -1;

    const char *ContentLength =  mg_get_header(pconn, "Content-Length");
    if (ContentLength)
        content_len = atoi(ContentLength);
    if (content_len > 0)
    {
        result = malloc(content_len + 1);
        CurrentSize = mg_read(pconn, result, content_len);
    }
    else
    {
        result = malloc(1); // Make sure we have a byte for the end of string
        if (!result)
            goto error;

        while ((n = mg_read(pconn, buf, sizeof(buf))) > 0)
        {
            char *newresult = realloc(result, 1 + CurrentSize + n);
            if (newresult == NULL)
                goto error;

            result = newresult;
            memcpy(result + CurrentSize, buf, n);
            CurrentSize = CurrentSize + n;
        }
    }

    result[CurrentSize] = '\0';

    *ppResult = result;
    *pLen = CurrentSize;
    return 0;

error:
    free(result);
    return 1;
}


// HACK -- match given URI or URI with '/Xfpe/restful/...'
int strcmpuri(const char *uri, const char *suffix)
{
    const char *xfpe = "/Xfpe/restful/";

    if (strncmp(uri, xfpe, strlen(xfpe)) == 0)
    {
        const char *stripped = strchr(uri + strlen(xfpe), '/');
        if (stripped == NULL)
            return -1;
        return strcmp(stripped, suffix);
    }
    else
    {
        return strcmp(uri, suffix);
    }
}

/*
 * Handle any PUT commands
 */

/*
 * Handles DELETE xbrc /
 */
static void *
HandleDeleteXbrc(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    pReader->isDeleted = 1;

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n\r\n");

    return (void *) 1;
}

/*
 * Handles PUT /
 */
static void *
HandlePutXbrc(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    assert(pr->query_string!=NULL);

    char *p = pReader->pszControllerURL;
    pReader->pszControllerURL = strndup(pr->query_string + 4, 128);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n\r\n");

    if(p) {
       free(p);
    }

    return (void *) 1;
}

/*
 * Handle any GET commands
 */

/*
 * Handles GET /
 */
static void *
HandleGetRoot(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    const char *type = "";

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/html\r\n\r\n");

    if (pReader->deviceType == DeviceTypeXBR)
    {
        type = "xBR";
    }
    else if (pReader->deviceType == DeviceTypeXTP)
    {
        type = "xTP";
    }
    else if (pReader->deviceType == DeviceTypeCar)
    {
        type = "xVehicle";
    }

    mg_printf(pconn,
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\r\n"
"   \"http://www.w3.org/TR/html4/strict.dtd\">\r\n"
"<html>\r\n"
"<head profile=\"http://www.w3.org/2005/10/profiles\">\r\n"
"<link rel=\"icon\" href=\"/favicon\" type=\"image/x-icon\">\r\n"
"<link rel=\"shortcut icon\" href=\"/favicon\" type=\"image/x-icon\">\r\n"
"<title>Reader Simulator</title></head>\r\n"
"<body>Reader simulator</body>\r\n"
"<table>\r\n");
    mg_printf(pconn,
" <tr><td>Name</td><td>%s</td></tr>\r\n", pReader->pszName);
    mg_printf(pconn,
" <tr><td>Type</td><td>%s</td></tr>\r\n", type);

/*
    if (pReader->pszHardwareType && !strcmp(pReader->pszHardwareType, "xBR4"))
    {
        mg_printf(pconn,
" <tr><td>Battery Level</td><td>%s</td></tr>\r\n");
        mg_printf(pconn,
" <tr><td>Battery Time</td><td>%s</td></tr>\r\n");
        mg_printf(pconn,
" <tr><td>Temperature</td><td>%s</td></tr>\r\n");
        mg_printf(pconn, 
" <tr><td>Antenna Power</td><td>%s</td></tr>\r\n");
    }
*/
    mg_printf(pconn,
"</table>\r\n"
"</html>\r\n");

    return (void *) 1;
}

/*
 * Handles GET /favicon.ico
 */
static void *
HandleGetFavicon(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: image/x-icon\r\n\r\n");

    mg_write(pconn, favicon, favicon_size);

    return (void *) 1;
}

static void*
HandleGetRadioPower(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: application/json\r\n\r\n");
    mg_printf(pconn,
    "{\n"
    "    \"group0\": {\n"
    "        \"state\": %d,\n"
    "        \"channels\": [0, 1]\n"
    "    },\n"
    "    \"group1\": {\n"
    "        \"state\": %d,\n"
    "        \"channels\": [2, 3]\n"
    "    },\n"
    "    \"group2\": {\n"
    "        \"state\": %d,\n"
    "        \"channels\": [4, 5]\n"
    "    },\n"
    "    \"group3\": {\n"
    "        \"state\": %d,\n"
    "        \"channels\": [6, 7]\n"
    "    }\n"
    "}", pReader->RadioPower[0], pReader->RadioPower[1], pReader->RadioPower[2], pReader->RadioPower[3]);
    return (void *) 1;
}

/*
 * Handles GET status
 */
static void *
HandleGetStatus(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    size_t sCount;
    struct timeval tvEarliest;
    struct timeval tvLatest;

    GetQueueStats(pReader->pQueue, &sCount, &tvEarliest, &tvLatest);

    // format the result in JSON form
    char szEarliest[64] = "";
    char szLatest[64] = "";
    if (sCount!=0)
    {
        FormatTime(&tvEarliest, szEarliest, 64);
        FormatTime(&tvLatest, szLatest, 64);
    }
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: application/json\r\n\r\n");
    mg_printf(pconn, "{ \"count\": %zd, \"earliest\": \"%s\", \"latest\": \"%s\", \"streamurl\": \"%s\" }\n", sCount, szEarliest, szLatest, pReader->szURL);
    return (void *) 1;
}

/*
 * Handles GET reader/name
 */
static void *
HandleGetReaderName(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    // format the result in JSON form
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: application/json\r\n\r\n");
    mg_printf(pconn, "{ \"reader-name\": \"%s\" }\n", pReader->pszName);

    return (void *) 1;
}

/*
 * Handles GET events
 *
 * Optional parameters:
 *         XLRID: Return data for this band id only
 *         since: Return only data after this eventnumber
 *         max: Return a maximum of this number of events
 */
static void *
HandleGetEvents(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    EVENTINFO *pEvents = NULL;
    size_t sEventCount = 0;
    struct timeval tvNow;
    size_t i;
    size_t sMaxEvents = 0;

    // If any query strings are specified, handle them now
    if (pr->query_string!=NULL)
    {
        char szMaxArgVal[6] = "";

        // handle "max"
        if (mg_get_var(pr->query_string, strlen(pr->query_string), "max", szMaxArgVal, sizeof(szMaxArgVal))>0)
        {
            sscanf(szMaxArgVal, "%zd", &sMaxEvents);
        }
    }
    gettimeofday(&tvNow, NULL);

    // Send the json
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: application/json\r\n\r\n");

    mg_printf(pconn, "{ \"reader-name\": \"%s\", \"events\": [ \n", pReader->pszName );
    DequeueMatching(pReader->pQueue, sMaxEvents, 0, &tvNow, &pEvents, &sEventCount);
    for (i = 0; i < sEventCount; i++)
    {
        char *pszEvent = NULL;
        int bLastEvent = 0;

        if (i == sEventCount - 1)
            bLastEvent = 1;

        // Output event structure
        if (pReader->deviceType == DeviceTypeXBR)
            pszEvent = EventXBRJSON(pReader, &pEvents[i]);
        else if (pReader->deviceType == DeviceTypeXTP)
            pszEvent = EventBandTapJSON(pReader, &pEvents[i]);

        mg_printf(pconn, "%s%s\n", pszEvent, bLastEvent ? "" : ",");
        free(pszEvent);
    }

    mg_printf(pconn, "] }\n");

    // indicate that we've handled the GET
    return (void *) 1;

}

static void *
HandlePostReaderName(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    if (pr->query_string!=NULL)
    {
        char szArgVal[256] = "";

        if (mg_get_var(pr->query_string, strlen(pr->query_string), "name", szArgVal, sizeof(szArgVal))>0)
        {
            char *pszName = strdup(szArgVal);
            if (pszName)
            {
                free(pReader->pszName);
                pReader->pszName = pszName;
            }

            mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                             "Content-Type: text/plain\r\n\r\nOK\r\n");
            return MG_OK;
        }
    }
    return MG_NOT_HANDLED;
}

/*
 * Handles PUT /
 */
static void *
HandleGetXbrc(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n\r\n");
    mg_printf(pconn, "{ \"url\": \"%s\" }", pReader->pszControllerURL ? pReader->pszControllerURL : "");

    return (void *) 1;
}

static void *
HandlePostLight(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    // grab the color argument
    char szColor[32];
    char szTimeout[32];

    if (  (mg_get_var(pr->query_string, strlen(pr->query_string), "Color", szColor, sizeof(szColor))>0) &&
          (mg_get_var(pr->query_string, strlen(pr->query_string), "Timeout", szTimeout, sizeof(szTimeout))>0) )
    {
        printf("Reader %s received Light message. Color=%s, Timeout=%s\n", pReader->pszName, szColor, szTimeout);
    }

    // reply
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\nOK\r\n");
    return (void *) 1;
}

static void *
HandlePostUpdateStream(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    size_t sMaxEvents = 0;
    ssize_t sAfterEvent = -1;
    size_t msecInterval = 200;

    pReader->szURL[0] = '\0';

    assert(pr->query_string!=NULL);

    char szArgVal[1024] = "";

    // get url parameter
    assert(mg_get_var(pr->query_string, strlen(pr->query_string), "url", pReader->szURL, sizeof(pReader->szURL))>0);

    // get interval parameter
    if (mg_get_var(pr->query_string, strlen(pr->query_string), "interval", szArgVal, sizeof(szArgVal))>0)
    {
        sscanf(szArgVal, "%zu", &msecInterval);
    }

    // get the max parameter
    if (mg_get_var(pr->query_string, strlen(pr->query_string), "max", szArgVal, sizeof(szArgVal))>0)
    {
        sscanf(szArgVal, "%zu", &sMaxEvents);
    }

    // get the max parameter
    if (mg_get_var(pr->query_string, strlen(pr->query_string), "after", szArgVal, sizeof(szArgVal))>0)
    {
        sscanf(szArgVal, "%zd", &sAfterEvent);
    }

    EventPostUpdate(pReader, pReader->szURL, sMaxEvents, msecInterval, sAfterEvent);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\nOK\r\n");
    return MG_OK; 
}

/*
 * Handles GET/POST lrid
 *
 * Adds an event to the queue for later 'broadcast'
 * Parameters:
 */
static void *
HandleLrid(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    int iError = 0;
    char szLRID[64] = "";
    char szSignalStrength[64] = "";
    char szFrequency[64] = "";
    char szPacketSequence[64];
    char szChannel[64];
    char szTimeDelta[64];
    char error[128] = "";

    char *buf = NULL;
    size_t n;

    EVENTINFO ei = { 0 };
    ei.event.type = EventTypeBandPing;
    ei.event.ping = calloc(1, sizeof(struct LongRangeEvent));

    assert(ei.event.ping != NULL);

    int nSignalStrength = 0;
    int nFrequency = 0;
    int nPacketSequence = 0;
    int nChannel = 0;

    if (pr->query_string!=NULL)
    {
        buf = pr->query_string;
        n = strlen(buf);
    }
    else
    {
        if (my_mg_read(pconn, &buf, &n))
        {
            fprintf(stderr, "Error reading data\n");
            goto error;
        }
    }

    int ret = 0;
    ret = mg_get_var(buf, n, "lrid", szLRID, sizeof(szLRID));
    if (ret <= 0)
    {
        iError = 1;
        fprintf(stderr, "Missing 'lrid'\n");
        strcat(error, "Missing 'lrid'\r\n");
    }
    ret = mg_get_var(buf, n, "ss", szSignalStrength, sizeof(szSignalStrength));
    if (ret <= 0)
    {
        iError = 1;
        fprintf(stderr, "Missing 'ss'\n");
        strcat(error, "Missing 'ss'\r\n");
    }
    ret = mg_get_var(buf, n, "freq", szFrequency, sizeof(szFrequency));
    if (ret <= 0)
    {
        iError = 1;
        fprintf(stderr, "Missing 'freq'\n");
        strcat(error, "Missing 'freq'\r\n");
    }
    ret = mg_get_var(buf, n, "pno", szPacketSequence, sizeof(szPacketSequence));
    if (ret <= 0)
    {
        iError = 1;
        fprintf(stderr, "Missing 'pno'\n");
        strcat(error, "Missing 'pno'\r\n");
    }
    ret = mg_get_var(buf, n, "chan", szChannel, sizeof(szChannel));
    if (ret <= 0)
    {
        iError = 1;
        fprintf(stderr, "Missing 'chan'\n");
        strcat(error, "Missing 'chan'\r\n");
    }
    ret = mg_get_var(buf, n, "dt", szTimeDelta, sizeof(szTimeDelta));
    if (ret <= 0)
    {
        ei.event.offset = 0;
        gettimeofday(&ei.event.tv, NULL);
    }
    else
    {
        ei.event.offset = atoll(szTimeDelta);
        gettimeofday(&ei.event.tv, NULL);
        updateTime(ei.event.offset, &ei.event.tv, &ei.event.tv);
    }

    if (iError)
        goto error;


    nSignalStrength = atoi(szSignalStrength);
    nFrequency = atoi(szFrequency);
    nPacketSequence = atoi(szPacketSequence);
    nChannel = atoi(szChannel);

    strncpy(ei.event.ping->szBandId, szLRID, 16);
    ei.event.ping->szBandId[16] = '\0';
    ei.event.ping->nSignalStrength = nSignalStrength;
    ei.event.ping->nFrequency = nFrequency;
    ei.event.ping->nPacketSequence = nPacketSequence;
    ei.event.ping->nChannel = nChannel;
    Enqueue(pReader->pQueue, &ei);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\n"
                     "[%s] Done: %s\r\n", pReader->pszName, buf);
    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }

    // indicate that we've handled the GET
    return MG_OK;

error:

    free(ei.event.ping);
    mg_printf(pconn, "HTTP/1.1 501 Not Implemented\r\n"
                     "Content-Type: text/plain\r\n\r\n"
                     "[%s] Error: %s\r\n", pReader->pszName, error);

    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }

    return MG_OK;
}

/*
 * Handles GET/POST rfid
 *
 * Adds an event to the queue for later 'tap'
 * Parameters:
*/
static void *
HandleRfid(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    char szTimeDelta[64];
    char *buf = NULL;
    size_t n;

    EVENTINFO ei = { 0 };
    ei.event.type = EventTypeBandTap;
    ei.event.tap = calloc(1, sizeof(struct TapEvent));

    if (pr->query_string!=NULL)
    {
        buf = pr->query_string;
        n = strlen(pr->query_string);
    }
    else
    {
        if (my_mg_read(pconn, &buf, &n))
        {
            fprintf(stderr, "[%s] Error handling '%s': Could not read data\n", pReader->pszName, pr->uri);
            goto error;
        }
    }
    int ret = 0;

    ret = mg_get_var(buf, n, "rfid", ei.event.tap->szBandId, sizeof(ei.event.tap->szBandId));
    ret = mg_get_var(buf, n, "uid", ei.event.tap->szUid, sizeof(ei.event.tap->szUid));
    ret = mg_get_var(buf, n, "sid", ei.event.tap->szSid, sizeof(ei.event.tap->szSid));
    ret = mg_get_var(buf, n, "pid", ei.event.tap->szPid, sizeof(ei.event.tap->szPid));
    ret = mg_get_var(buf, n, "iin", ei.event.tap->szIin, sizeof(ei.event.tap->szIin));

    ret = mg_get_var(buf, n, "dt", szTimeDelta, sizeof(szTimeDelta));
    if (ret <= 0)
    {
        ei.event.offset = 0;
        gettimeofday(&ei.event.tv, NULL);
    }
    else
    {
        ei.event.offset = atol(szTimeDelta);
        gettimeofday(&ei.event.tv, NULL);
        updateTime(ei.event.offset, &ei.event.tv, &ei.event.tv);
    }

    Enqueue(pReader->pQueue, &ei);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\n"
                     "[%s] Done: %s\r\n", pReader->pszName, pr->query_string);

    // indicate that we've handled the GET
    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_OK;

error:
    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_NOT_HANDLED;
}

/*
 * Handles GET/POST avms
 *
 * Adds an event to the queue for later vehicle association
 * Parameters:
*/
static void *
HandleAvms(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    char szTimeDelta[64];
    char *buf = NULL;
    size_t n;

    EVENTINFO ei = { 0 };
    ei.event.type = EventTypeCarPing;
    ei.event.car = calloc(1, sizeof(struct CarEvent));

    if (pr->query_string!=NULL)
    {
        buf = pr->query_string;
        n = strlen(pr->query_string);
    }
    else
    {
        if (my_mg_read(pconn, &buf, &n))
        {
            fprintf(stderr, "[%s] Error handling '%s': Could not read data\n", pReader->pszName, pr->uri);
            goto error;
        }
    }
    int ret = 0;

    ret = mg_get_var(buf, n, "vehid", ei.event.car->szVehicleId, sizeof(ei.event.car->szVehicleId));
    ret = mg_get_var(buf, n, "attid", ei.event.car->szAttractionId, sizeof(ei.event.car->szAttractionId));
    ret = mg_get_var(buf, n, "sceid", ei.event.car->szSceneId, sizeof(ei.event.car->szSceneId));
    ret = mg_get_var(buf, n, "locid", ei.event.car->szLocationId, sizeof(ei.event.car->szLocationId));
    ret = mg_get_var(buf, n, "conf", ei.event.car->szConfidence, sizeof(ei.event.car->szConfidence));

    ret = mg_get_var(buf, n, "dt", szTimeDelta, sizeof(szTimeDelta));
    if (ret <= 0)
    {
        ei.event.offset = 0;
        gettimeofday(&ei.event.tv, NULL);
    }
    else
    {
        ei.event.offset = atoll(szTimeDelta);
        gettimeofday(&ei.event.tv, NULL);
        updateTime(ei.event.offset, &ei.event.tv, &ei.event.tv);
    }

    Enqueue(pReader->pQueue, &ei);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\n"
                     "[%s] Done: %s\r\n", pReader->pszName, pr->query_string);

    // indicate that we've handled the GET
    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_OK;

error:
    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_NOT_HANDLED;
}

/*
 * Handles POST upgrade
 *
 * Update the reader version to match upgrade package.
 * Parameters:
*/
static void *
HandlePostUpgrade(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    char *buf = NULL;
    size_t n;

    if (pr->query_string!=NULL)
    {
        fprintf(stderr, "query_string: '%s'\n", pr->query_string);
    }

    if (my_mg_read(pconn, &buf, &n))
    {
        fprintf(stderr, "Error reading data\n");
    }
    else
    {
        fprintf(stdout, "data: '%s'\n", buf);
        free(buf);
    }

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\n"
                     "[%s] Done: %s\r\n", pReader->pszName, pr->query_string);

    return MG_OK;
}

static void *
HandlePostEvents(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    struct timeval ts;
    int nEvents = 0;
    char *buf = NULL;
    size_t n;

    gettimeofday(&ts, NULL);

    if (pr->query_string!=NULL)
    {
        fprintf(stderr, "Unexpected query string\n");
    }
    else
    {
        char error[256];
        json_value *res = NULL;
        size_t i;

        if (my_mg_read(pconn, &buf, &n))
        {
            goto error;
        }
        res = json_parse_ex(NULL, buf, error);
        if (res && res->type == json_object && res->u.object.length == 1 && res->u.object.values && !strcmp(res->u.object.values->name, "events"))
        {
            json_value *array = res->u.object.values[0].value;
            if (array->type == json_array)
            {
                for (i = 0; i < array->u.array.length; i++)
                {
                    json_value *event = array->u.array.values[i];
                    if (event->type == json_object)
                    {
                        EVENTINFO ei = { 0 };

                        if (pReader->deviceType == DeviceTypeXBR)
                            JSONBandPingEvent(event, NULL, &ts, &ei);
                        else if (pReader->deviceType == DeviceTypeXTP)
                            JSONBandTapEvent(event, NULL, &ts, &ei);
                        else if (pReader->deviceType == DeviceTypeCar)
                            JSONCarPingEvent(event, NULL, &ts, &ei);

                        Enqueue(pReader->pQueue, &ei);
                        nEvents++;
                    }
                }
            }
            json_value_free(res);
            mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                             "Content-Type: text/plain\r\n\r\n"
                             "[%s] Received %d events\r\n", pReader->pszName, nEvents);

            free(buf);
            buf = NULL;
            return MG_OK;
        }
    }
error:
    free(buf);
    buf = NULL;
    return MG_NOT_HANDLED;
}

static void *
HandlePutSystemShutdown(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    char *buf = NULL;
    size_t n;
    char szArgVal[64];
    size_t sTimeToDeepSleep = 15; // Minutes. FIXME. Don't know if there is a default.

    if (pReader->pszHardwareType && strcmp(pReader->pszHardwareType, "xBR4"))
        goto error;

    if (pr->query_string!=NULL)
    {
        buf = pr->query_string;
        n = strlen(pr->query_string);
    }
    else
    {
        if (my_mg_read(pconn, &buf, &n))
        {
            fprintf(stderr, "[%s] Error handling '%s': Could not read data\n", pReader->pszName, pr->uri);
            goto error;
        }
    }


    if (mg_get_var(buf, strlen(buf), "timeout", szArgVal, sizeof(szArgVal))>0)
    {
        sTimeToDeepSleep = atol(szArgVal);
    }

    EventPostShutdown(pReader, sTimeToDeepSleep);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                        "Content-Type: text/plain\r\n\r\nOK\r\n");

    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_OK;
error:
    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_NOT_HANDLED;
}

static void *
HandlePutRadioPower(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    char *buf = NULL;
    size_t n;
    char szArgVal[64];
    int iRadioPower[] = {-1, -1, -1, -1};
    size_t i;

    if (pReader->pszHardwareType && strcmp(pReader->pszHardwareType, "xBR4"))
        goto error;

    if (pr->query_string!=NULL)
    {
        buf = pr->query_string;
        n = strlen(pr->query_string);
    }
    else
    {
        if (my_mg_read(pconn, &buf, &n))
        {
            fprintf(stderr, "[%s] Error handling '%s': Could not read data\n", pReader->pszName, pr->uri);
            goto error;
        }
    }


    if (mg_get_var(buf, strlen(buf), "group0", szArgVal, sizeof(szArgVal))>0)
    {
        iRadioPower[0] = atol(szArgVal);
    }

    if (mg_get_var(buf, strlen(buf), "group1", szArgVal, sizeof(szArgVal))>0)
    {
        iRadioPower[1] = atol(szArgVal);
    }

    if (mg_get_var(buf, strlen(buf), "group2", szArgVal, sizeof(szArgVal))>0)
    {
        iRadioPower[2] = atol(szArgVal);
    }

    if (mg_get_var(buf, strlen(buf), "group3", szArgVal, sizeof(szArgVal))>0)
    {
        iRadioPower[3] = atol(szArgVal);
    }

    for (i = 0; i < 4; i++)
    {
        if (iRadioPower[i] != -1)
        {
            pReader->RadioPower[i] = iRadioPower[i];
        }
    }

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                        "Content-Type: text/plain\r\n\r\nOK\r\n");

    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_OK;
error:
    if (buf != pr->query_string)
    {
        free(buf);
        buf = NULL;
    }
    return MG_NOT_HANDLED;
}

static void *
HandleDeleteUpdateStream(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    EventPostUpdate(pReader, NULL, -1, 100, -1);

    // send ok reply
    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\nOK\r\n");
    return MG_OK;
}

static void *
HandleDeleteEvents(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr)
{
    DeleteEvents(pReader->pQueue);

    mg_printf(pconn, "HTTP/1.1 200 OK\r\n"
                     "Content-Type: text/plain\r\n\r\nOK\r\n");
    return MG_OK;
}

struct Handler ReaderPutHandlers[] = {
    { "/xbrc", HandlePutXbrc },
    { "/system/shutdown", HandlePutSystemShutdown}, // v4
    { "/radio/power", HandlePutRadioPower} //v4
};

struct Handler ReaderGetHandlers[] = {
    { "/", HandleGetRoot},
    { "/favicon.ico", HandleGetFavicon},
    { "/status", HandleGetStatus},
    { "/events", HandleGetEvents},
    { "/reader/name", HandleGetReaderName},
    { "/xbrc", HandleGetXbrc },
    { "/lrid", HandleLrid },
    { "/rfid", HandleRfid },
    { "/rfid/tap", HandleRfid},
    { "/avms", HandleAvms },
    { "/xband/commands", ReturnOK },
    { "/xband/beacon", ReturnOK },
    { "/xband/options", ReturnOK },
    { "/radio/power", HandleGetRadioPower }
};

struct Handler ReaderPostHandlers[] = {
    { "/reader/name", HandlePostReaderName },
    { "/update_stream", HandlePostUpdateStream },
    { "/light", HandlePostLight },
    { "/events", HandlePostEvents },
    { "/lrid", HandleLrid },
    { "/rfid", HandleRfid },
    { "/rfid/tap", HandleRfid },
    { "/avms", HandleAvms },
    { "/upgrade", HandlePostUpgrade },
    { "/mode", ReturnOK },
    { "/time", ReturnOK },
    { "/rfid/options", ReturnOK },
    { "/tap/options", ReturnOK },
    { "/biometric/options", ReturnOK },
    { "/xband/commands", ReturnOK },
    { "/xband/beacon", ReturnOK },
    { "/xband/options", ReturnOK },
    { "/system/shutdown", HandlePutSystemShutdown}, // v4
    { "/radio/power", HandlePutRadioPower} //v4
};

struct Handler ReaderDeleteHandlers[] = {
    { "/update_stream",  HandleDeleteUpdateStream},
    { "/events", HandleDeleteEvents},
    { "/xband/commands", ReturnOK },
    { "/xband/beacon", ReturnOK },
    { "/xband/output", ReturnOK },
    { "/xbrc", HandleDeleteXbrc }
};

void* ExecuteHandler(
    struct Reader *pReader,
    struct mg_connection *pconn,
    const struct mg_request_info *pr,
    struct Handler handlers[],
    size_t stHandlers)
{
    size_t i;
    for (i = 0; i < stHandlers; i++)
    {
        if (!strcmpuri(pr->uri, handlers[i].uri))
        {
            return handlers[i].pfnHandler(pReader, pconn, pr);
        }
    }
    return MG_NOT_HANDLED;
}

/*
 * Function called by mongoose. Here's where the reader parses out special URIs
 * for RESTful purposes
 */
static void *
ReaderCallback(enum mg_event event,
               struct mg_connection *pconn,
               const struct mg_request_info *pr)
{
    struct Reader* pReader = NULL;

    assert(pr);
    pReader = pr->user_data;
    assert(pReader != NULL);

    if (event==MG_EVENT_LOG)
    {
        fprintf(stderr, "Reader %s: Mongoose error: %s\n in response to request %s\n", pReader->pszName, pr->log_message, pr->uri);
        return MG_OK;
    }
    else if (event == MG_NEW_REQUEST)
    {
        Print(pReader, pconn, pr);

        // see if it's a GET
        if (!strcmp(pr->request_method, "GET"))
            return ExecuteHandler(pReader, pconn, pr, ReaderGetHandlers, sizeof(ReaderGetHandlers)/sizeof(ReaderGetHandlers[0]));
        else if (!strcmp(pr->request_method, "POST"))
            return ExecuteHandler(pReader, pconn, pr, ReaderPostHandlers, sizeof(ReaderPostHandlers)/sizeof(ReaderPostHandlers[0]));
        else if (!strcmp(pr->request_method, "DELETE"))
            return ExecuteHandler(pReader, pconn, pr, ReaderDeleteHandlers, sizeof(ReaderDeleteHandlers)/sizeof(ReaderDeleteHandlers[0]));
        else if (!strcmp(pr->request_method, "PUT"))
            return ExecuteHandler(pReader, pconn, pr, ReaderPutHandlers, sizeof(ReaderPutHandlers)/sizeof(ReaderPutHandlers[0]));
        else
            return MG_NOT_HANDLED;
    }
    return MG_OK;
}

/*
 * Starts the web server
 */
int
CreateWebServer(struct Reader *pReader)
{
    const char *aszOptions[] =
    {
         "listening_ports", NULL,
         "document_root", "/",
         "extra_mime_types", ".json=application/json",
         NULL
    };

    // inject the port number into aszOptions
    char szBuf[16];
    sprintf(szBuf, "%d", pReader->uPort);
    aszOptions[1] = szBuf;

    // start mongoose - see the callback to find out where the action is
    pReader->pmongoose = mg_start(ReaderCallback, pReader, aszOptions);

    return 0;
}

/*
 * Frees the web server
 */
int
FreeWebServer(struct Reader *pReader)
{
    if (pReader->pmongoose)
    {
        mg_stop(pReader->pmongoose);
        pReader->pmongoose = NULL;
    }
    return 0;
}


