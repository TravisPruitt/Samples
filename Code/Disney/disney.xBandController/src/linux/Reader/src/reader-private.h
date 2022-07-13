#ifndef READERPRIVATE_H
#define READERPRIVATE_H

#include "reader.h"
#include "inputstream.h"
#include "priority.h"
#include "webserver.h"

#include <curl/curl.h>
#include <stdint.h>
#include <pthread.h>

enum EventPostState {
    EventPostStateExit = 0,
    EventPostStateResume = 1,
    EventPostStatePause = 2,
    EventPostStateUpdate = 3,
    EventPostStateDeepSleep = 4,
    EventPostStateDone = 5
};

struct Reader
{
    char *pszName;    // Can be changed by xBRC

    int BatteryLevel;
    int BatteryTime;
    int RadioPower[4];

    // These are fixed at runtime
    enum DeviceType deviceType;
    uint16_t uPort;
    struct InputStream *pInputStream;
    int x, y;            // reader location
    char *pszMac; // formatted MAC address
    char *pszControllerURL;
    char *pszXBRMSURL;
    char *pszHardwareAddress;
    char *pszHardwareType; // Arbitrary string, only submitted if non-NULL

    EVENTQUEUE *pQueue; // Maintains it's own queues

    char szURL[256];

    struct mg_context *pmongoose;
    pthread_mutex_t MongooseMutex;

    // Thread for posting events to xBRC
    int fdEventPostThread[2];
    pthread_t EventPostThread;


    CURL *curl;

    // Keep track of how the xBRC is responding.
    int bFailedLastSend;
    unsigned long long goodSendCount;
    unsigned long long badSendCount;

    int bFailedLastHello;
    int isDeleted;
};

#endif
