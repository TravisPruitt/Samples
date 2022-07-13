/*
 * updatestream.c
 *
 *  Created on: Jul 5, 2011
 *      Author: mvellon
 */

#define _XOPEN_SOURCE
#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/utsname.h>
#include <time.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <poll.h>
#include "format.h"
#include "send.h"
#include "reader-private.h"
#include "version.h"


struct EventPostMessage
{
    char *pszURL;
    size_t sMaxEvents;
    size_t sUpdateInterval;
    ssize_t sAfterEvent;
    size_t sTimeToDeepSleep;
    enum EventPostState state;
};

// local functions
static void *EventPostThread(void *);

int
SendHello(struct Reader *pReader)
{
    if(pReader->isDeleted) {
        return 0;
    }

    int err;

    // fetch the os info
    char szOS[256];
    struct utsname osinfo;
    uname(&osinfo);
    sprintf(szOS, "%s %s", osinfo.sysname, osinfo.release);

    char szURL[1024] = "";
    if (pReader->pszControllerURL && pReader->pszControllerURL[0])
        sprintf(szURL, "%s/hello", pReader->pszControllerURL);
    else if (pReader->pszXBRMSURL && pReader->pszXBRMSURL[0])
        sprintf(szURL, "%s/XBRMS/rest/hello", pReader->pszXBRMSURL);
    else
        return 0;

    char szData[1024] = "";

    // current time
    char szTime[64];
    struct timeval tv;
    gettimeofday(&tv, NULL);
    FormatTime(&tv, szTime, 64);

    sprintf(szData, "{"
                    "\n\"mac\" : \"%s\""
                    ",\n\"port\":  %d"
                    ",\n\"reader name\" : \"%s\""
                    ",\n\"next eno\": %ld"
                    ",\n\"min xbrc version\": \"0.0.0.0\""
                    ",\n\"reader type\" : \"%s\""
                    ",\n\"reader version\": \"%s\""
                    ",\n\"linux version\": \"%s XfpeEmulator\""
    				",\n\"update stream\":\"%s\""
    				",\n\"time\":\"%s\"",
                    pReader->pszMac,
                    pReader->uPort,
                    pReader->pszName,
                    NextEventNumber(pReader->pQueue),
                    GetReaderType(pReader->deviceType),
                    READERVERSION,
                    szOS,
                    pReader->szURL,
                    szTime);
    if (pReader->pszHardwareType && pReader->pszHardwareType[0])
    {
        char buf[256];
        sprintf(buf, ",\n\"HW type\": \"%s\"", pReader->pszHardwareType);
        strcat(szData, buf);
    }
    strcat(szData, "\n}");

    fprintf(stdout, "%s\n", szData);
    
    err = Send(pReader->curl, szURL, "application/json", szData);
    if (err)
    {
        pReader->bFailedLastHello = 1;
        fprintf(stderr, "[%s] Failed: Sending Hello to %s\n", pReader->pszName, szURL);
    }
    else
    {
        pReader->bFailedLastHello = 0;
    }

    return err;
}

int
SendShutdown(struct Reader *pReader, size_t sEno, struct timeval *tv, size_t sTimeout)
{
    int err;
    char *msg = NULL;

    if(pReader->isDeleted) {
        return 0;
    }

    char szURL[1024] = "";
    sprintf(szURL, "%s/stream", pReader->pszControllerURL);

    msg = EventShutdown(pReader->pszName, sEno, tv, sTimeout);

    fprintf(stderr, "[%s] Shutdown event\n", pReader->pszName);
    err = Send(pReader->curl, szURL, "application/json", msg);

    free(msg);
    return err;
}

int CreateEventPostThread(struct Reader *pReader)
{
    int err = 0;

    pReader->fdEventPostThread[0] = -1;
    pReader->fdEventPostThread[1] = -1;

    if (pipe(pReader->fdEventPostThread) != 0)
    {
        err = errno;
        goto cleanup;
    }

    if (fcntl(pReader->fdEventPostThread[0], F_SETFL, O_NONBLOCK) < 0)
    {
        err = errno;
        goto cleanup;
    }

    if (fcntl(pReader->fdEventPostThread[1], F_SETFL, O_NONBLOCK) < 0)
    {
        err = errno;
        goto cleanup;
    }


    err = pthread_create(&pReader->EventPostThread, NULL, EventPostThread, pReader);
    if (err != 0)
    {
        goto cleanup;
    }

cleanup:

    if (err)
    {
        if (pReader->fdEventPostThread[0] != -1)
            close(pReader->fdEventPostThread[0]);

        if (pReader->fdEventPostThread[1] != -1)
            close(pReader->fdEventPostThread[1]);
    }
    return err;
}

int FreeEventPostThread(struct Reader *pReader)
{
    int err = 0;

    if (pReader)
    {
        struct EventPostMessage msg;

        memset(&msg, 0, sizeof(msg));
        msg.state = EventPostStateExit;
        assert(write(pReader->fdEventPostThread[1], &msg, sizeof(msg)) == sizeof(msg));

        void *pvRet;
        pthread_join(pReader->EventPostThread, &pvRet);

        if (pReader->fdEventPostThread[0] != -1)
            close(pReader->fdEventPostThread[0]);

        if (pReader->fdEventPostThread[1] != -1)
            close(pReader->fdEventPostThread[1]);
    }

    return err;
}

int EventPostResume(struct Reader *pReader)
{
    int err = 0;
    struct EventPostMessage msg;

    memset(&msg, 0, sizeof(msg));
    msg.state = EventPostStateResume;
    assert(write(pReader->fdEventPostThread[1], &msg, sizeof(msg)) == sizeof(msg));

    return err;
}

int EventPostPause(struct Reader *pReader)
{
    int err = 0;
    struct EventPostMessage msg;

    memset(&msg, 0, sizeof(msg));
    msg.state = EventPostStatePause;
    assert(write(pReader->fdEventPostThread[1], &msg, sizeof(msg)) == sizeof(msg));

    return err;
}

int EventPostShutdown(struct Reader *pReader, size_t sTimeToDeepSleep)
{
    int err = 0;
    struct EventPostMessage msg;

    memset(&msg, 0, sizeof(msg));
    msg.state = EventPostStateDeepSleep;

    msg.sTimeToDeepSleep = sTimeToDeepSleep;

    assert(write(pReader->fdEventPostThread[1], &msg, sizeof(msg)) == sizeof(msg));

    return err;
}

int EventPostUpdate(struct Reader *pReader, const char *pszURL, size_t sMaxEvents, size_t msecInterval, ssize_t sAfterEvent)
{
    int err = 0;
    struct EventPostMessage msg;

    memset(&msg, 0, sizeof(msg));
    msg.state = EventPostStateUpdate;

    if (pszURL)
    {
        msg.pszURL = strdup(pszURL);
        assert(msg.pszURL);
    }
    msg.sMaxEvents = sMaxEvents;
    msg.sUpdateInterval = msecInterval;
    msg.sAfterEvent = sAfterEvent;

    assert(write(pReader->fdEventPostThread[1], &msg, sizeof(msg)) == sizeof(msg));

    return err;
}

void FreeEvents(EVENTINFO *pEvents, size_t sCount)
{
    size_t i;
    for (i = 0; i < sCount; i++)
    {
        free(pEvents[i].event.ptr);
    }
    free(pEvents);
}
/*
static
void printEvents(EVENTINFO *pEvents, int count, char* type)
{
	struct timeval tvNow;
	gettimeofday(&tvNow, NULL);

	long long nowMs = (((long long)tvNow.tv_sec) * 1000) + (tvNow.tv_usec/1000);

	int i;
	for (i = 0; i < count; i++)
	{
		EVENTINFO* pe = &pEvents[i];

		long long evMs = (((long long)pe->event.tv.tv_sec) * 1000) + (pe->event.tv.tv_usec/1000);

		printf("%s %llu,%ld,%ld,%lld,%lld,%lld\n", type, pe->event.offset, pe->event.tv.tv_sec, pe->event.tv.tv_usec,
											 evMs, nowMs, nowMs - evMs);
	}
}
*/

static
void* EventPostThread(void *pvData)
{
    struct Reader *pReader = pvData;
    struct pollfd fds;

    struct timeval tvNextUpdate;
    struct timeval tvNextHello;

    struct timeval tvHelloInterval;
    struct timeval tvUpdateInterval;

    char *pszURL = NULL;
    size_t sMaxEvents = 200;
    size_t sFirstEvent = 0;
    int bResume = 0;
    int bSendToHardware = 0;
    int bSendToXbrc = 0;
    int bSendHello = 1;

    // If the device has been 'shutdown' (powered down, deep sleep), don't send anything.
    size_t bDeepSleep = 0;
    struct timeval tvDeepSleep;

    char *pszAvmsEventUrl = NULL;

    timerclear(&tvNextUpdate);
    timerclear(&tvNextHello);

    tvHelloInterval.tv_sec = 30;
    tvHelloInterval.tv_usec = 0;


    // AVMS (car) messages must be very precise. No delay of any sort.
    if (pReader->deviceType == DeviceTypeCar || pReader->deviceType == DeviceTypeXtpGpio)
    {
        tvUpdateInterval.tv_sec = 0;
        tvUpdateInterval.tv_usec = 1000;
    }
    else
    {
        tvUpdateInterval.tv_sec = 0;
        tvUpdateInterval.tv_usec = 200000;
    }

    if (pReader->deviceType == DeviceTypeCar)
    {
        // no hello messages for the AVMS reader
        bSendHello = 0;
    }

    if (pReader->deviceType == DeviceTypeXBR ||
        pReader->deviceType == DeviceTypeXTP ||
        pReader->deviceType == DeviceTypeXtpGpio)
    {
        if (pReader->pszHardwareAddress)
        {
            bSendToHardware = 1;
        }
    }

    fds.fd = pReader->fdEventPostThread[0];
    fds.events = POLLIN;
    for (;;)
    {
        struct timeval tvDiff;
        struct timeval tvNext;
        int offset;

        tvNext = tvNextUpdate;
        if (!bSendToHardware) // If we are doing hardware, only need to send ping/taps, not hello/diagnostic
        {
            if (timercmp(&tvNextHello, &tvNext, <))
              tvNext = tvNextHello;
        }

        offset = 0;
        if (!bResume)
        {
            offset = -1; // Pause until we get a message
        }
        else
        {
            struct timeval tvNow;
            gettimeofday(&tvNow, NULL);
            if (timercmp(&tvNow, &tvNext, <))
            {
                timersub(&tvNext, &tvNow, &tvDiff);
                offset = tvDiff.tv_sec * 1000 + tvDiff.tv_usec / 1000;
            }
        }

        if (poll(&fds, 1, offset) > 0) // We got a message
        {
            struct EventPostMessage msg;
            int n = read(fds.fd, &msg, sizeof(msg));
            assert(n == sizeof(msg));

            if (msg.state == EventPostStateResume)
            {
                struct timeval tvNow;
                gettimeofday(&tvNow, NULL);
                tvNextHello = tvNextUpdate = tvNow;

                bResume = 1;
            }
            else if (msg.state == EventPostStatePause)
            {
                bResume = 0;
            }
            else if (msg.state == EventPostStateDeepSleep)
            {
                struct timeval tvNow;
                gettimeofday(&tvNow, NULL);
                bDeepSleep = 1;
                tvDeepSleep = tvNow;
                tvDeepSleep.tv_sec += msg.sTimeToDeepSleep * 60 + 30;

                size_t sNextEventNumber = AcquireNextEventNumber(pReader->pQueue);
                SendShutdown(pReader, sNextEventNumber, &tvNow, msg.sTimeToDeepSleep);
            }
            else if (msg.state == EventPostStateUpdate)
            {
                // GPIO events need to be played back very precisely.
                // Do not let xBRC change the frequency of the updates.
                if (pReader->deviceType != DeviceTypeXtpGpio &&
                    pReader->deviceType != DeviceTypeCar)
                {
                    tvUpdateInterval.tv_sec = msg.sUpdateInterval / 1000;
                    tvUpdateInterval.tv_usec = (msg.sUpdateInterval % 1000) * 1000;
                }

                if (msg.pszURL)
                {
                    free(pszURL);
                    pszURL = msg.pszURL;
                }
                if (pszURL)
                    bSendToXbrc = 1;
                else
                    bSendToXbrc = 0;
                sMaxEvents = msg.sMaxEvents;
                sFirstEvent = msg.sAfterEvent + 1;

                fprintf(stderr, "max = %zd, after = %zd\n", sMaxEvents, msg.sAfterEvent);
            }
            else if (msg.state == EventPostStateExit)
            {
                free(pszURL);
                free(pszAvmsEventUrl);
                return 0;
            }
        }
        else
        {
            struct timeval tvNow;

            gettimeofday(&tvNow, NULL);
            if (bDeepSleep)
            {
                if (timercmp(&tvDeepSleep, &tvNow, <=))
                {
                    bDeepSleep = 0;
                    tvNextHello = tvNow; // Force HELLO
                }
            }

            if (!bSendToHardware && bSendHello)
            {
                if (!bDeepSleep)
                {
                    if (timercmp(&tvNextHello, &tvNow, <=))
                    {
                        SendHello(pReader);
                        timeradd(&tvNextHello, &tvHelloInterval, &tvNextHello);
                    }
                }
            }

            if (timercmp(&tvNextUpdate, &tvNow, <=))
            {
                // now send data
                if (pReader->deviceType == DeviceTypeCar)
                {
                    EVENTINFO *pEvents = NULL;
                    size_t sEventCount = 0;
                    DequeueMatching(pReader->pQueue, sMaxEvents, sFirstEvent, &tvNow, &pEvents, &sEventCount);
                    if (sEventCount >0 && !bDeepSleep)
                    {
                        size_t i;
                        fprintf(stdout, "[%s] Sending %zd events\n", pReader->pszName, sEventCount);

                        if (pszAvmsEventUrl == NULL)
                        {
                            pszAvmsEventUrl = malloc(strlen(pReader->pszControllerURL) + strlen("/videvent") + 1);
                            assert(pszAvmsEventUrl);
                            sprintf(pszAvmsEventUrl, "%s%s", pReader->pszControllerURL, "/videvent");
                        }

                        for (i = 0; i < sEventCount; i++)
                        {
                            char *pszEvent = EventCarPingXML(&pEvents[i]);
                            if (pszEvent)
                            {
                                int res = Send(pReader->curl, pszAvmsEventUrl, "application/xml", pszEvent);
                                if (res)
                                {
                                    fprintf(stderr, "[%s] Failed to send, %d\n", pReader->pszName, res);
                                    pReader->badSendCount++;
                                    pReader->bFailedLastSend = 1;
                                }
                                else
                                {
                                    pReader->goodSendCount++;
                                    pReader->bFailedLastSend = 0;
                                }
                                free(pszEvent);
                            }
                        }
                        FreeEvents(pEvents, sEventCount);
                    }
                }
                else if (bSendToXbrc || bSendToHardware)
                {
                    EVENTINFO *pEvents = NULL;
                    char *pszEvents = NULL;
                    size_t sEventCount = 0;

                    DequeueMatching(pReader->pQueue, sMaxEvents, sFirstEvent, &tvNow, &pEvents, &sEventCount);
                    if (sEventCount < 1 || bDeepSleep)
                        goto noevents;

                    if (bSendToXbrc)
                    {
                        if (pReader->deviceType == DeviceTypeXBR)
                        {
                            pszEvents = FormatJSONEvents(pReader, pEvents, sEventCount, EventXBRJSON);
                        }
                        else if (pReader->deviceType == DeviceTypeXTP)
                        {
                            pszEvents = FormatJSONEvents(pReader, pEvents, sEventCount, EventBandTapJSON);
                        }
                        else if (pReader->deviceType == DeviceTypeXtpGpio)
                        {
                            pszEvents = FormatJSONEvents(pReader, pEvents, sEventCount, EventXtpGpioJSON);
                        }
                    }

                    if (bSendToHardware)
                    {
                        size_t i;
                        size_t sErrors = 0;
                        for (i = 0; i < sEventCount; i++)
                        {
                            char szUrl[256];
                            char *pszEvent = NULL;

                            if (pReader->deviceType == DeviceTypeXBR)
                                pszEvent = EventBandPingREST(&pEvents[i]);
                            else if (pReader->deviceType == DeviceTypeXTP)
                                pszEvent = EventBandTapREST(&pEvents[i]);

                            if (pszEvent)
                            {
                                sprintf(szUrl, "http://%s%s", pReader->pszHardwareAddress, pszEvent);
                                if (Send(pReader->curl, szUrl, "text/html", NULL))
                                    sErrors++;
                                free(pszEvent);
                            }
                        }
                        fprintf(stderr, "[%s] Sending %zd events resulted in %zd failures.\n", pReader->pszName, sEventCount, sErrors);
                    }

                    if (pszEvents)
                    {
                        if (Send(pReader->curl, pszURL, "application/json", pszEvents))
                        {
                            fprintf(stderr, "[%s] Sending failed. Dropping %zd events.\n", pReader->pszName, sEventCount);
                            pReader->badSendCount++;
                            pReader->bFailedLastSend = 1;
                        }
                        else
                        {
                            fprintf(stderr, "[%s] Sent %zd events.\n", pReader->pszName, sEventCount);
                            pReader->goodSendCount++;
                            pReader->bFailedLastSend = 0;
                        }
                     }

                     free(pszEvents);
                     pszEvents = NULL;

                     FreeEvents(pEvents, sEventCount);
                     pEvents = NULL;
                }
noevents:
                timeradd(&tvNextUpdate, &tvUpdateInterval, &tvNextUpdate);
            }
        }
    }

    return 0;
}

