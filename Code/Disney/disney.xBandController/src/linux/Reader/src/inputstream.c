/*
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <sys/time.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <errno.h>
#include <poll.h>
#include <pthread.h>
#include <unistd.h>

#include "inputstream.h"
#include "format.h"
#include "reader.h"

struct InputMessage
{
    enum InputState state;
    struct timeval tvNow;
    long long start;
    long long end;
};
void
EnqueueLegacyTap(
    struct InputStream *pInput,
    char *pszBuf)
{
    // format: RFID SecureID PublicID
    // where:
    //     RFID               is a 16 char hex value (uid)
    //     SecureID           is a 16 char hex value
	//     PublicID			  is a 16 char hex value (same as lrid)

    EVENTINFO ei = { 0 };
    ei.event.type = EventTypeBandTap;
    ei.event.tap = calloc(1, sizeof(struct TapEvent));
    assert(ei.event.tap != NULL);

    // skip leading stuff
    while ( *pszBuf == ' ' )
        pszBuf++;

    // read into szBandId
    char *pszDest = ei.event.tap->szBandId;
    while ( *pszBuf != ' ' && *pszBuf != '\n' && *pszBuf != '\0')
        *pszDest++ = *pszBuf++;

    // terminate
    *pszDest = '\0';

    // process secureId if present
    if (*pszBuf==' ')
    {
        // skip leading stuff
        while ( *pszBuf == ' ' )
            pszBuf++;

        // copy into secure id
        pszDest = ei.event.tap->szSid;

        while ( *pszBuf != ' ' && *pszBuf != '\n' && *pszBuf != '\0')
        	*pszDest++ = *pszBuf++;

        // terminate
        *pszDest = '\0';

        // process publicID if present
        if (*pszBuf==' ')
        {
            // skip leading stuff
            while ( *pszBuf == ' ' )
                pszBuf++;

            // copy into secure id
            pszDest = ei.event.tap->szPid;

            while ( *pszBuf != '\n' && *pszBuf != '\0')
            	*pszDest++ = *pszBuf++;

            // terminate
            *pszDest = '\0';
        }

    }

    gettimeofday(&ei.event.tv, NULL);

    // enqueue the event - algorithmically, they'll always be sorted
    pInput->pfnCallback(&ei, pInput->pvCallbackData);
}

void
EnqueueLegacyLRR(
    struct InputStream *pInput,
    char *pszBuf)
{
    // format: BandID PacketSequence SignalStrength BatteryLevel Freqency
    // where:
    //     BandID           is a 16 char hex value
    //     PacketSequence   is a 3 digit number from 0-255
    //     SignalStrength   is a 2 digit number from 0-63 indicating the strength in db
    //     Channel          is a 1 digit number indicating the channel
    //     Frequency        is a 2 digit number from 0-15

    // Sample buffer:
    // FEDCBA9876543210 123 05 1 12
    // 0000000000111111111122222222
    // 0123456789012345678901234567

    // This would contain an ID of FEDCBA9876543210, a packet sequence of 123, a signal strength of 5,
    // a channel of 1 and a frequency of 12

    // parse out the packetsequence, signal strength, channel and frequency
    int nSignalStrength=0, nChannel=0, nPacketSequence=0, nFrequency=0;

    sscanf(&pszBuf[17], "%d %d %d %d", &nPacketSequence, &nSignalStrength, &nChannel, &nFrequency);

    // enqueue
    EVENTINFO ei = { 0 };
    struct LongRangeEvent *ping2;
    ei.event.type = EventTypeBandPing;
    ei.event.ping = calloc(1, sizeof(struct LongRangeEvent));
    ping2 = calloc(1, sizeof(struct LongRangeEvent));
 
    assert(ei.event.ping != NULL);
    assert(ping2 != NULL);

    while ( *pszBuf == ' ' )
        pszBuf++;

    char *ptr = strchr(pszBuf, ' ');
    if (ptr)
    	*ptr = '\0';

    strcpy(ei.event.ping->szBandId, pszBuf);

    ei.event.ping->nPacketSequence = nPacketSequence;
    ei.event.ping->nSignalStrength = nSignalStrength;
    ei.event.ping->nChannel = nChannel;
    ei.event.ping->nFrequency = nFrequency;
    gettimeofday(&ei.event.tv, NULL);

    *ping2 = *ei.event.ping;
    // enqueue the event - algorithmically, they'll always be sorted
    pInput->pfnCallback(&ei, pInput->pvCallbackData);

    // change the channel and queue it again
    ei.event.ping = ping2;
    ei.event.ping->nChannel = (ei.event.ping->nChannel + 1) % 2;
    pInput->pfnCallback(&ei, pInput->pvCallbackData);
}


char* myfgets(char szBuf[], size_t size, int fd)
{
    size_t i;
    for (i = 0; i < size - 1; i++)
    {
        int n = read(fd, szBuf + i, 1);
        if (n != 1)
        {
            if (i == 0)
                return NULL;
            else
            {
                szBuf[i] = '\0';
                return szBuf;
            }
        }

        if (szBuf[i] == '\n')
        {
            szBuf[i + 1] = '\0';
            return szBuf;
        }
    }
    return szBuf;
}

static void *
ReadNamedPipeThread(void* pvData)
{
    int err = 0;
    struct InputStream *pInput = pvData;
    int bResume = 0;

    struct pollfd fds[2];
    fds[0].fd = pInput->fdThread[0];
    fds[0].events = POLLIN;

    fds[1].fd = pInput->fd;
    fds[1].events = POLLIN;

    for(;;)
    {
        int count = 1;
        if (bResume)
        {
            count++;
        }

        if (poll(fds, count, -1) > 0)
        {
            if (fds[0].revents & POLLIN)
            {
                struct InputMessage msg;
                assert(read(fds[0].fd, &msg, sizeof(msg)) == sizeof(msg));
                if (msg.state == InputStreamStateResume)
                    bResume = 1;
                else if (msg.state == InputStreamStatePause)
                    bResume = 0;
                else if (msg.state == InputStreamStateExit)
                {
                    goto cleanup;
                }
                else
                {
                    fprintf(stderr, "Don't know\n");
                }
            }
            if (count > 1 && (fds[1].revents & POLLIN))
            {
                char szBuf[256];
                if (myfgets(szBuf, 255, fds[1].fd)==NULL )
                {
                    fprintf(stderr, "Input Stream %s: error reading fifo. Errno: %d\n", pInput->pszName, errno);
                }
                if (pInput->InputType == InputTypeLegacyTap)
                    EnqueueLegacyTap(pInput, szBuf);
                else
                    EnqueueLegacyLRR(pInput, szBuf);
            }
        }
    }

cleanup:

    return err;
}

static void *
ReadJSONFileThread(void* pvData)
{
    struct InputStream *pInput = pvData;
    FILE *fp = pInput->fp;
    int bResume = 0;

    int bWantMoreEvents = 1;
    struct timeval tvNow;
    struct timeval tvLastEvent;

    fp = pInput->fp;
    assert(fp != NULL);
    long long start = 0;
    long long end = 0;

    for(;;)
    {
        char szBuf[2048];
        char error[256];
        json_value *event = NULL;

        int offset = 0;
        if (bResume == 0)
            offset = -1;
        else if (!bWantMoreEvents)
            offset = 3000;

        struct pollfd fds;
        fds.fd = pInput->fdThread[0];
        fds.events = POLLIN;
        fds.revents = 0;

        if (poll(&fds, 1, offset) > 0)
        {
            struct InputMessage msg;
            assert(read(fds.fd, &msg, sizeof(msg)) == sizeof(msg));
            if (msg.state == InputStreamStateResume)
            {
                bResume = 1;

                start = msg.start;
                end = msg.end;
                tvNow = msg.tvNow;
                timerclear(&tvLastEvent);
            }
            else if (msg.state == InputStreamStatePause)
            {
                bResume = 0;
            }
            else if (msg.state == InputStreamStateExit)
                return 0;
        }
        else
        {
            if (!bWantMoreEvents)
            {
                if (pInput->pfnCallback(NULL, pInput->pvCallbackData) == 0)
                {
                    bWantMoreEvents = 1;
                }
            }

            if (bWantMoreEvents)
            {
restart:
                if (fgets(szBuf, 2048, fp)==NULL )
                {
                    // Reached the end of the file. Need to prepare for next cycle.
                    start = 0;
                    rewind(fp);

                    if (end == -1)
                    {
                        tvNow = tvLastEvent;
                    }
                    else
                    {
                    	// The tvNow.tv_usec is only a long int (not long long).
                    	// Must avoid integer overflow problems here.
                    	long int endsec = end / 1000;
                    	long int endms = end % 1000;
                        tvNow.tv_usec += (endms * 1000);
                        tvNow.tv_sec +=  endsec;
                        // See if tv_usec grew to be over one second..
                        endsec = tvNow.tv_usec / 1000000;
                        tvNow.tv_sec +=  endsec;
                        tvNow.tv_usec %= 1000000;
                    }
                    timerclear(&tvLastEvent);

                    goto restart;
                }

                // HACK: Remove trailing comma -- input looks like this:
                // {sdasdf:"", blah:"asdfdsf"},
                {
                    char *pszComma = strrchr(szBuf, ',');
                    char *pszClose = strrchr(szBuf, '}');
                    if (pszComma > pszClose)
                        *pszComma = '\0'; // Remove comma
                }

                event = json_parse_ex(NULL, szBuf, error);
                if (event && event->type == json_object)
                {
                    int bad = 0;
                    EVENTINFO ei = { 0 };

                    if (pInput->InputType == InputTypeXBR)
                        bad = JSONXBREvent(event, pInput->pszName, &tvNow, &ei);
                    else if (pInput->InputType == InputTypeXTP)
                        bad = JSONBandTapEvent(event, pInput->pszName, &tvNow, &ei);
                    else if (pInput->InputType == InputTypeCar)
                        bad = JSONCarPingEvent(event, pInput->pszName, &tvNow, &ei);
                    else if (pInput->InputType == InputTypeXtpGpio)
                        bad = JSONXtpGpioEvent(event, pInput->pszName, &tvNow, &ei);

                    if (!bad && ei.event.offset >= start)
                    {
                        if (timercmp(&ei.event.tv, &tvLastEvent, >))
                            tvLastEvent = ei.event.tv;

                        if (pInput->pfnCallback(&ei, pInput->pvCallbackData) > 0)
                            bWantMoreEvents = 0;
                    }
                    json_value_free(event);
                }
            }
        }
    }

    return NULL;

}

int
CreateInputStream(
    const char *pszName,
    int (*pfnCallback)(EVENTINFO *, void*),
    void *pvCallbackData,
    const char *pszInputFilename,
    enum InputType InputType,
    long long start,
    long long end,
    struct InputStream **ppInput)
{
    int err = 0;
    struct InputStream *pInput = NULL;
    FILE *fp = NULL;
    int fd = -1;

    pInput = malloc(sizeof(struct InputStream));
    if (!pInput)
    {
        err = ENOMEM;
        goto cleanup;
    }

    if (pipe(pInput->fdThread) != 0)
    {
        err = errno;
        goto cleanup;
    }

    if (fcntl(pInput->fdThread[0], F_SETFL, O_NONBLOCK) < 0)
    {
        err = errno;
        goto cleanup;
    }

    if (fcntl(pInput->fdThread[1], F_SETFL, O_NONBLOCK) < 0)
    {
        err = errno;
        goto cleanup;
    }

    pInput->pszName = strdup(pszName);
    assert(pInput->pszName != NULL);

    pInput->pszInputFilename = strdup(pszInputFilename);
    if (pInput->pszInputFilename == NULL)
    {
        err = ENOMEM;
        goto cleanup;
    }
    pInput->InputType = InputType;
    pInput->pfnCallback = pfnCallback;
    pInput->pvCallbackData = pvCallbackData;
    pInput->fp = fp;
    pInput->fd = fd;
    pInput->start = start;
    pInput->end = end;

    if (pInput->InputType == InputTypeLegacyPing ||
        pInput->InputType == InputTypeLegacyTap)
    {
        unlink(pInput->pszInputFilename);
        mkfifo(pInput->pszInputFilename, 0600);

        fd = open(pInput->pszInputFilename, O_RDWR);
        if (fd == -1)
        {
            fprintf(stderr, "Input Stream %s: Error opening named pipe %s. Errno: %d\n", pInput->pszName, pInput->pszInputFilename, errno);
            err = EACCES;
            goto cleanup;
        }
        pInput->fd = fd;
        err = pthread_create(&pInput->Thread,  NULL, ReadNamedPipeThread, pInput);
    }
    else
    {
        fp = fopen(pInput->pszInputFilename, "r");
        if (!fp)
        {
            fprintf(stderr, "Could not open %s\n", pInput->pszInputFilename);
            err = EACCES;
            goto cleanup;
        }
        pInput->fp = fp;
        err = pthread_create(&pInput->Thread,  NULL, ReadJSONFileThread, pInput);
    }
    if (err != 0)
    {
        goto cleanup;
    }

    *ppInput = pInput;
    pInput = NULL;

cleanup:

    if (err)
    {
        if (pInput)
        {
            if (pInput->fp)
            {
                fclose(pInput->fp);
                fp = NULL;
            }
             if (pInput->fd != -1)
            {
                close(pInput->fd);
                fd = -1;
            }
            free(pInput->pszName);
            free(pInput->pszInputFilename);
            free(pInput);
        }
    }

    return err;
}

int
FreeInputStream(struct InputStream *pInput)
{
    int err = 0;
    if (pInput)
    {
        struct InputMessage msg;
        memset(&msg, 0, sizeof(msg));
        msg.state = InputStreamStateExit;
        assert(write(pInput->fdThread[1], &msg, sizeof(msg)) == sizeof(msg));

        void *pvRet;
        pthread_join(pInput->Thread, &pvRet);

        if (pInput->fp)
        {
            fclose(pInput->fp);
            pInput->fp = NULL;
        }

        if (pInput->fd != -1)
        {
            close(pInput->fd);
            pInput->fd = -1;
        }

        free(pInput->pszName);
        pInput->pszName = NULL;

        free(pInput->pszInputFilename);
        pInput->pszInputFilename = NULL;

        free(pInput);
    }
    return err;
}

int InputStreamSignalResume(struct InputStream *pInput, struct timeval *tvNow)
{
    int err = 0;

    if (pInput)
    {
        struct InputMessage msg;

        memset(&msg, 0, sizeof(msg));
        msg.state = InputStreamStateResume;
        msg.tvNow = *tvNow;
        msg.start = pInput->start;
        msg.end = pInput->end;
        assert(write(pInput->fdThread[1], &msg, sizeof(msg)) == sizeof(msg));
    }

    return err;
}

int InputStreamSignalPause(struct InputStream *pInput)
{
    int err = 0;

    if (pInput)
    {
        struct InputMessage msg;
        memset(&msg, 0, sizeof(msg));
        msg.state = InputStreamStatePause;
        assert(write(pInput->fdThread[1], &msg, sizeof(msg)) == sizeof(msg));
    }

    return err;
}

