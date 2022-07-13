/*
 * format.c
 *
 *  Created on: Jul 5, 2011
 *      Author: mvellon
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>

#include "reader-private.h"
#include "priority.h"
#include "format.h"

char *FormatJSONEvents(struct Reader *pReader, EVENTINFO *pEvents, size_t sCount, char* (*EventJSON)(struct Reader *pReader, EVENTINFO *pei))
{
    size_t i;
    char *psz = NULL;
    char *pszCurrent = NULL;

    for (i = 0; i < sCount; i++)
    {
        int bLastEvent = 0; // Don't place a comma on the last event

        if (i == sCount - 1)
            bLastEvent = 1;

        // Output event structure
        char *pszEvent = EventJSON(pReader, &pEvents[i]);
        if (i == 0)
        {
            size_t avgsize = strlen(pszEvent) + 98;
            psz = malloc(sCount * avgsize + 80);
            if (!psz)
                goto cleanup;
            pszCurrent = psz + sprintf(psz, "{ \"reader name\": \"%s\", \"events\": [\n", pReader->pszName);
        }
        pszCurrent = stpcpy(pszCurrent, pszEvent);
        free(pszEvent);

        // add a comma if necessary
        if (!bLastEvent)
        {
            pszCurrent = stpcpy(pszCurrent, ",\n");
        }
        else
        {
            pszCurrent = stpcpy(pszCurrent, "\n] }\n");
        }
    }

cleanup:
    return psz;
}

void
FormatTime(struct timeval *pts, char *pszBuf, int cb)
{
    // format the timestamp as a UTC value
    struct tm tmTs;
    gmtime_r(&pts->tv_sec, &tmTs);    // convert to UTC
    strftime(pszBuf, cb, "%Y-%m-%dT%H:%M:%S.", &tmTs);

    // tack on the milliseconds
    int ms = pts->tv_usec / 1000;
    char szMS[6];
    sprintf(szMS, "%.3d+00:00", ms);
    strcat( pszBuf, szMS);
}

char *
EventBandPingREST(EVENTINFO *pei)
{
    char szBuf[512] = "";

    sprintf(szBuf, "/lrid?lrid=%s&pno=%d&freq=%d&ss=%d&chan=%d",
                            pei->event.ping->szBandId,
                            pei->event.ping->nPacketSequence,
                            pei->event.ping->nFrequency,
                            pei->event.ping->nSignalStrength,
                            pei->event.ping->nChannel);
    return strdup(szBuf);
}

char *
EventBandPingJSON(struct Reader *pReader, EVENTINFO *pei)
{
    char szBuf[512] = "";

    // format the timestamp as a UTC value
    char szTime[64];
    FormatTime(&pei->event.tv, szTime, 64);

    sprintf(szBuf, "   { \"eno\": %d,  \"XLRID\": \"%s\", \"pno\": %d, \"time\": \"%s\", \"freq\": %d, \"ss\": %d, \"chan\": %d }",
                            pei->nEventNumber,
                            pei->event.ping->szBandId,
                            pei->event.ping->nPacketSequence,
                            szTime,
                            pei->event.ping->nFrequency,
                            pei->event.ping->nSignalStrength,
                            pei->event.ping->nChannel);
    return strdup(szBuf);
}

char *
EventXtpGpioJSON(struct Reader *pReader, EVENTINFO *pei)
{
    char szBuf[512] = "";

    // format the timestamp as a UTC value
    char szTime[64];
    FormatTime(&pei->event.tv, szTime, 64);

    sprintf(szBuf, "   { \"type\":\"xtp-gpio\", \"eno\": %d,  \"channel\": \"%s\", \"time\": \"%s\" }",
                            pei->nEventNumber,
                            pei->event.xtpgpio->channel,
                            szTime);
    return strdup(szBuf);
}

char *
EventBandTapREST(EVENTINFO *pei)
{
    char szTmp[64];
    char szBuf[512] = "";

    int bFirst = 1;

    sprintf(szBuf, "/rfid/tap?");

    if (pei->event.tap->szUid[0])
    {
        if (bFirst)
            bFirst = 0;
        else
            strcat(szBuf, "&");

        sprintf(szTmp, "uid=%s", pei->event.tap->szUid);
        strcat(szBuf, szTmp);
    }

    if (pei->event.tap->szSid[0])
    {
        if (bFirst)
            bFirst = 0;
        else
            strcat(szBuf, "&");

        sprintf(szTmp, "sid=%s", pei->event.tap->szSid);
        strcat(szBuf, szTmp);
    }

    if (pei->event.tap->szPid[0])
    {
        if (bFirst)
            bFirst = 0;
        else
            strcat(szBuf, "&");

        sprintf(szTmp, "pid=%s", pei->event.tap->szPid);
        strcat(szBuf, szTmp);
    }

    if (pei->event.tap->szIin[0])
    {
        if (bFirst)
            bFirst = 0;
        else
            strcat(szBuf, "&");

        sprintf(szTmp, "iin=%s", pei->event.tap->szIin);
        strcat(szBuf, szTmp);
    }

    return strdup(szBuf);
}

char *
EventBandTapJSON(struct Reader *pReader, EVENTINFO *pei)
{
    char szTmp[512];
    char szBuf[2048] = "";

    // format the timestamp as a UTC value
    char szTime[64];
    FormatTime(&pei->event.tv, szTime, 64);

    sprintf(szBuf, "   { \"eno\": %d", pei->nEventNumber);
    if (pei->event.tap->szBandId)
    {
        sprintf(szTmp, ", \"XRFID\": \"%s\"", pei->event.tap->szBandId);
        strcat(szBuf, szTmp);
    }

    if (pei->event.tap->szUid[0])
    {
        sprintf(szTmp, ", \"uid\": \"%s\"", pei->event.tap->szUid);
        strcat(szBuf, szTmp);
    }

    if (pei->event.tap->szSid[0])
    {
        sprintf(szTmp, ", \"sid\": \"%s\"", pei->event.tap->szSid);
        strcat(szBuf, szTmp);
    }

    if (pei->event.tap->szPid[0])
    {
        sprintf(szTmp, ", \"pid\": \"%s\"", pei->event.tap->szPid);
        strcat(szBuf, szTmp);
    }

    if (pei->event.tap->szIin[0])
    {
        sprintf(szTmp, ", \"iin\": \"%s\"", pei->event.tap->szIin);
        strcat(szBuf, szTmp);
    }

    sprintf(szTmp, ", \"time\": \"%s\" }", szTime);
    strcat(szBuf, szTmp);

    return strdup(szBuf);
}

char * EventXBRDiagnostic(struct Reader *pReader, EVENTINFO *ei)
{
    char szTmp[512];
    char szBuf[2048] = "";
    size_t i;

    // format the timestamp as a UTC value
    char szTime[64];
    FormatTime(&ei->event.tv, szTime, 64);

    sprintf(szBuf, "{ \"eno\": %d", ei->nEventNumber);

    sprintf(szTmp, "\n, \"time\": \"%s\"", szTime);
    strcat(szBuf, szTmp);

    sprintf(szTmp, "\n, \"type\": \"xbr-diagnostics\"");
    strcat(szBuf, szTmp);

    sprintf(szTmp, "\n, \"status\": \"%s\"", ei->event.xbrdiag->szStatus);
    strcat(szBuf, szTmp);

    sprintf(szTmp, "\n, \"status msg\": \"%s\"", ei->event.xbrdiag->szStatusMsg);
    strcat(szBuf, szTmp);

    sprintf(szTmp, "\n, \"rx radio status\" : [");
    strcat(szBuf, szTmp);
    for (i = 0; i < XBR4_RADIOS; i++)
    {
        // Different xBR versions have different numbers of radios.
        if (!ei->event.xbrdiag->szRadioStatus[i][0])
            break;

        if (i != 0)
            strcat(szBuf, ",");
        sprintf(szTmp,
"{\n"
"    \"status\": \"%s\",\n"
"    \"msg\": \"%s\"\n"
"}\n",
                ei->event.xbrdiag->szRadioStatus[i],
                ei->event.xbrdiag->szRadioStatusMsg[i]);

        strcat(szBuf, szTmp);
    
    }
    sprintf(szTmp, " ]");
    strcat(szBuf, szTmp);

    sprintf(szTmp, ",\n \"tx radio status\": {\n"
"  \"msg\": \"%s\",\n"
"  \"status\": \"%s\"\n"
"}", ei->event.xbrdiag->szTxRadioStatus, ei->event.xbrdiag->szTxRadioStatusMsg);
    strcat(szBuf, szTmp);

    if (pReader->pszHardwareType && !strcmp(pReader->pszHardwareType, "xBR4"))
    {
        sprintf(szTmp, ",\n \"bat level\": %s", ei->event.xbrdiag->szBatteryLevel);
        strcat(szBuf, szTmp);

        if (ei->event.xbrdiag->szBatteryTime[0])
        {
            sprintf(szTmp, ",\n \"bat time\": %s", ei->event.xbrdiag->szBatteryTime);
            strcat(szBuf, szTmp);
        }

        if (ei->event.xbrdiag->szBatteryCurrent[0])
        {
            sprintf(szTmp, ",\n \"bat current\": %s", ei->event.xbrdiag->szBatteryCurrent);
            strcat(szBuf, szTmp);
        }

        sprintf(szTmp, ",\n \"temp\": %s", ei->event.xbrdiag->szTemperature);
        strcat(szBuf, szTmp);
    }

    sprintf(szTmp, "}\n");
    strcat(szBuf, szTmp);

    return strdup(szBuf);
}

char *
EventShutdown(const char *name, size_t eno, struct timeval *tv, size_t timeout)
{
    char szTmp[512];
    char szBuf[2048] = "";

    // format the timestamp as a UTC value
    char szTime[64];
    FormatTime(tv, szTime, 64);

    sprintf(szBuf, "{ \"reader name\": \"%s\", \"type\": \"reader-shutdown\", \"events\": [  { \"eno\": %zd", name, eno);

    sprintf(szTmp, ", \"time\": \"%s\"", szTime);
    strcat(szBuf, szTmp);

    sprintf(szTmp, ", \"timeout\": \"%zd\"", timeout);
    strcat(szBuf, szTmp);

    sprintf(szTmp, " } ] }");
    strcat(szBuf, szTmp);

    return strdup(szBuf);
}

char *
EventXBRJSON(struct Reader *pReader, EVENTINFO *pei)
{
    if (pei->event.type == EventTypeBandPing)
        return EventBandPingJSON(pReader, pei);
    else if (pei->event.type == EventTypeXBRDiag)
        return EventXBRDiagnostic(pReader, pei);
    return NULL;
}

char *
EventCarPingXML(EVENTINFO *pei)
{
    char szBuf[512] = "";
    char szTmp[64];

    // format the timestamp as a UTC value
    char szTime[64];
    FormatTime(&pei->event.tv, szTime, 64);

    sprintf(szBuf, "<message type=\"VEHICLE\" time=\"%s\">", szTime);

    if (pei->event.car->szVehicleId[0])
    {
        sprintf(szTmp, "<vehicleid>%s</vehicleid>", pei->event.car->szVehicleId);
        strcat(szBuf, szTmp);
    }

    if (pei->event.car->szAttractionId[0])
    {
        sprintf(szTmp, "<attractionid>%s</attractionid>", pei->event.car->szAttractionId);
        strcat(szBuf, szTmp);
    }

    if (pei->event.car->szSceneId[0])
    {
        sprintf(szTmp, "<sceneid>%s</sceneid>", pei->event.car->szSceneId);
        strcat(szBuf, szTmp);
    }

    if (pei->event.car->szLocationId[0])
    {
        sprintf(szTmp, "<locationid>%s</locationid>", pei->event.car->szLocationId);
        strcat(szBuf, szTmp);
    }

    if (pei->event.car->szConfidence[0])
    {
        sprintf(szTmp, "<confidence>%s</confidence>", pei->event.car->szConfidence);
        strcat(szBuf, szTmp);
    }

    strcat(szBuf, "</message>");

    return strdup(szBuf);
}

int JSONXBREvent(json_value *event, const char *name, struct timeval *tv, EVENTINFO *ei)
{
    int err = 0;
    enum EventType type = EventTypeUnknown;
    unsigned long long nDeltaTime = 0;
    size_t val, i, val1;

    struct LongRangeEvent ping = { 0 };
    struct XBRDiagEvent xbrdiag = { 0 };

    for (val = 0; val < event->u.object.length; val++)
    {
        if (!strcmp(event->u.object.values[val].name, "tx radio status"))
        {
            json_value *status = event->u.object.values[val].value;
            if (status->type == json_object)
            {
                for (val1 = 0; val1 < status->u.object.length; val1++)
                {
                    char *pszVal = status->u.object.values[val1].value->u.string.ptr;
                    char *pszName = status->u.object.values[val1].name;
                    if (!strcmp(pszName, "status"))
                    {
                        strcpy(xbrdiag.szTxRadioStatus, pszVal);
                        continue;
                    }
                    else if (!strcmp(pszName, "msg"))
                    {
                        strcpy(xbrdiag.szTxRadioStatusMsg, pszVal);
                        continue;
                    }
                }
            }
        }
        else if (!strcmp(event->u.object.values[val].name, "rx radio status") &&
            event->u.object.values[val].value->type == json_array)
        {
            json_value *array = event->u.object.values[val].value;
            for (i = 0; i < array->u.array.length; i++)
            {
                json_value *status = array->u.array.values[i];
                if (status->type == json_object)
                {
                    for (val1 = 0; val1 < status->u.object.length; val1++)
                    {
                        char *pszVal = status->u.object.values[val1].value->u.string.ptr;
                        char *pszName = status->u.object.values[val1].name;
                        if (!strcmp(pszName, "status"))
                        {
                            strcpy(xbrdiag.szRadioStatus[i], pszVal);
                            continue;
                        }
                        else if (!strcmp(pszName, "msg"))
                        {
                            strcpy(xbrdiag.szRadioStatusMsg[i], pszVal);
                            continue;
                        }
                    }
                }
            }
        }
        else if (event->u.object.values[val].value->type == json_string)
        {
            char *pszVal = event->u.object.values[val].value->u.string.ptr;
            char *pszName = event->u.object.values[val].name;
            if (!strcmp(pszName, "name"))
            {
                if (name && strcmp(pszVal, name))
                {
                    err = 1;
                    goto error;
                }
            }
            if (!strcmp(pszName, "type"))
            {
                if (!strcmp(pszVal, "xbr-diagnostics"))
                    type = EventTypeXBRDiag;
                else
                     fprintf(stderr, "Unhandled type %s\n", pszVal);
                continue;
            }
            if (type == EventTypeUnknown || type == EventTypeBandPing)
            {
                if (!strcmp(pszName, "lrid"))
                {
                    strcpy(ping.szBandId, pszVal);
                    type = EventTypeBandPing;
                }
                else if (!strcmp(pszName, "ss"))
                {
                    ping.nSignalStrength = atoi(pszVal);
                    type = EventTypeBandPing;
                }
                else if (!strcmp(pszName, "freq"))
                {
                    ping.nFrequency = atoi(pszVal);
                    type = EventTypeBandPing;
                }
                else if (!strcmp(pszName, "pno"))
                {
                    ping.nPacketSequence = atoi(pszVal);
                    type = EventTypeBandPing;
                }
                else if (!strcmp(pszName, "chan"))
                {
                    ping.nChannel = atoi(pszVal);
                    type = EventTypeBandPing;
                }
                else if (!strcmp(pszName, "dt"))
                {
                    nDeltaTime = atoll(pszVal);
                }
                if (type != EventTypeUnknown)
                    continue;
            }
            if (type == EventTypeUnknown || type == EventTypeXBRDiag)
            {
                if (!strcmp(pszName, "status"))
                {
                    strcpy(xbrdiag.szStatus, pszVal);
                }
                else if (!strcmp(pszName, "status msg"))
                {
                    strcpy(xbrdiag.szStatusMsg, pszVal);
                }
                else if (!strcmp(pszName, "bat level"))
                {
                    strcpy(xbrdiag.szBatteryLevel, pszVal);
                }
                else if (!strcmp(pszName, "bat time"))
                {
                    strcpy(xbrdiag.szBatteryTime, pszVal);
                }
                else if (!strcmp(pszName, "bat current"))
                {
                    strcpy(xbrdiag.szBatteryCurrent, pszVal);
                }
                else if (!strcmp(pszName, "temp"))
                {
                    strcpy(xbrdiag.szTemperature, pszVal);
                }
                else if (!strcmp(pszName, "dt"))
                {
                    nDeltaTime = atoll(pszVal);
                }
                if (type != EventTypeUnknown)
                    continue;
            }
        }
    }
    if (type == EventTypeBandPing)
    {
        ei->event.ping = calloc(1, sizeof(struct LongRangeEvent));
        if (!ei->event.ping)
        {
            err = 3;
            goto error;
        }
        *(ei->event.ping) = ping;
    }
    else if (type == EventTypeXBRDiag)
    {
        ei->event.xbrdiag = calloc(1, sizeof(struct XBRDiagEvent));
        if (!ei->event.xbrdiag)
        {
            err = 3;
            goto error;
        }
        *(ei->event.xbrdiag) = xbrdiag;
    }
    else
    {
        err = 2;
        goto error;
    }
    ei->event.type = type;
    ei->event.offset = nDeltaTime;
    updateTime(ei->event.offset, tv, &ei->event.tv);

error:
    return err;
}

int JSONBandPingEvent(json_value *event, const char *name, struct timeval *tv,  EVENTINFO *ei)
{
    int err = 0;
    unsigned long long nDeltaTime = 0;
    size_t val;
    struct LongRangeEvent ping = { 0 };

    for (val = 0; val < event->u.object.length; val++)
    {
        if (event->u.object.values[val].value->type == json_string)
        {
            char *pszVal = event->u.object.values[val].value->u.string.ptr;
            char *pszName = event->u.object.values[val].name;
            if (!strcmp(pszName, "name"))
            {
                if (name && strcmp(pszVal, name))
                {
                    err = 1;
                    goto error;
                }
            }
            else if (!strcmp(pszName, "lrid"))
            {
                strcpy(ping.szBandId, pszVal);
            }
            else if (!strcmp(pszName, "ss"))
            {
                ping.nSignalStrength = atoi(pszVal);
            }
            else if (!strcmp(pszName, "freq"))
            {
                ping.nFrequency = atoi(pszVal);
            }
            else if (!strcmp(pszName, "pno"))
            {
                ping.nPacketSequence = atoi(pszVal);
            }
            else if (!strcmp(pszName, "chan"))
            {
                ping.nChannel = atoi(pszVal);
            }
            else if (!strcmp(pszName, "dt"))
            {
                nDeltaTime = atoll(pszVal);
            }
        }
    }

    ei->event.ping = calloc(1, sizeof(struct LongRangeEvent));
    if (!ei->event.ping)
    {
        err = 3;
        goto error;
    }
    *(ei->event.ping) = ping;
    ei->event.type = EventTypeBandPing;
    ei->event.offset = nDeltaTime;
    updateTime(ei->event.offset, tv, &ei->event.tv);

error:
    return err;
}

int JSONBandTapEvent(json_value *event, const char *name, struct timeval *tv, EVENTINFO *ei)
{
    int err = 0;
    unsigned long long nDeltaTime = 0;
    size_t val;
    struct TapEvent tap;

    for (val = 0; val < event->u.object.length; val++)
    {
        if (event->u.object.values[val].value->type == json_string)
        {
            char *pszVal = event->u.object.values[val].value->u.string.ptr;
            char *pszName = event->u.object.values[val].name;
            if (!strcmp(pszName, "name"))
            {
                if (name && strcmp(pszVal, name))
                {
                    err = 1;
                    goto error;
                }
            }
            else if (!strcmp(pszName, "rfid"))
            {
                strcpy(tap.szBandId, pszVal);
            }
            else if (!strcmp(pszName, "uid"))
            {
                strcpy(tap.szUid, pszVal);
            }
            else if (!strcmp(pszName, "sid"))
            {
                strcpy(tap.szSid, pszVal);
            }
            else if (!strcmp(pszName, "pid"))
            {
                strcpy(tap.szPid, pszVal);
            }
            else if (!strcmp(pszName, "iin"))
            {
                strcpy(tap.szIin, pszVal);
            }
            else if (!strcmp(pszName, "dt"))
            {
                nDeltaTime = atoll(pszVal);
            }
        }
    }
    ei->event.tap = calloc(1, sizeof(struct TapEvent));
    if (!ei->event.tap)
    {
        err = 3;
        goto error;
    }
    *(ei->event.tap) = tap;
    ei->event.type = EventTypeBandTap;
    ei->event.offset = nDeltaTime;
    updateTime(ei->event.offset, tv, &ei->event.tv);

error:
    return err;
}

int JSONCarPingEvent(json_value *event, const char *name, struct timeval *tv, EVENTINFO *ei)
{
    int err = 0;
    unsigned long long nDeltaTime = 0;
    size_t val;
    struct CarEvent car = { 0 };

    for (val = 0; val < event->u.object.length; val++)
    {
        if (event->u.object.values[val].value->type == json_string)
        {
            char *pszVal = event->u.object.values[val].value->u.string.ptr;
            char *pszName = event->u.object.values[val].name;
            if (!strcmp(pszName, "name"))
            {
                if (name && strcmp(pszVal, name))
                {
                    err = 1;
                    goto error;
                }
            }
            else if (!strcmp(pszName, "CarID"))
            {
                strcpy(car.szVehicleId, pszVal);
            }
            else if (!strcmp(pszName, "vehid"))
            {
                strcpy(car.szVehicleId, pszVal);
            }
            else if (!strcmp(pszName, "attid"))
            {
                strcpy(car.szAttractionId, pszVal);
            }
            else if (!strcmp(pszName, "sceid"))
            {
                strcpy(car.szSceneId, pszVal);
            }
            else if (!strcmp(pszName, "locid"))
            {
                strcpy(car.szLocationId, pszVal);
            }
            else if (!strcmp(pszName, "conf"))
            {
                strcpy(car.szConfidence, pszVal);
            }
            else if (!strcmp(pszName, "dt"))
            {
                nDeltaTime = atoll(pszVal);
            }
        }
    }
    ei->event.car = calloc(1, sizeof(struct CarEvent));
    if (!ei->event.car)
    {
        err = 3;
        goto error;
    }
    *(ei->event.car) = car;
    ei->event.type = EventTypeCarPing;
    ei->event.offset = nDeltaTime;
    updateTime(ei->event.offset, tv, &ei->event.tv);

error:
    return err;
}

int JSONXtpGpioEvent(json_value *event, const char *name, struct timeval *tv, EVENTINFO *ei)
{
    int err = 0;
    unsigned long long nDeltaTime = 0;
    size_t val;
    struct XtpGpioEvent xtpgpio = { 0 };

    // defaults
    strcpy(xtpgpio.channel, "0");

    for (val = 0; val < event->u.object.length; val++)
    {
        if (event->u.object.values[val].value->type == json_string)
        {
            char *pszVal = event->u.object.values[val].value->u.string.ptr;
            char *pszName = event->u.object.values[val].name;
            if (!strcmp(pszName, "name"))
            {
                if (name && strcmp(pszVal, name))
                {
                    err = 1;
                    goto error;
                }
            }
            else if (!strcmp(pszName, "channel"))
            {
                strcpy(xtpgpio.channel, pszVal);
            }
            else if (!strcmp(pszName, "dt"))
            {
                nDeltaTime = atoll(pszVal);
            }
        }
    }
    ei->event.xtpgpio = calloc(1, sizeof(struct XtpGpioEvent));
    if (!ei->event.xtpgpio)
    {
        err = 3;
        goto error;
    }
    *(ei->event.xtpgpio) = xtpgpio;
    ei->event.type = EventTypeXtpGpio;
    ei->event.offset = nDeltaTime;
    updateTime(ei->event.offset, tv, &ei->event.tv);

error:
    return err;
}

