#ifndef EVENT_H
#define EVENT_H

#include <sys/time.h>

enum EventType
{
    EventTypeUnknown = 0,
    EventTypeXBRDiag = 1,
    EventTypeBandPing = 2,
    EventTypeBandTap = 3,
    EventTypeCarPing = 4,
    EventTypeXtpGpio = 5
};

struct TapEvent
{
    char szBandId[17];
    char szUid[17];
    char szPid[17];
    char szSid[17];
    char szIin[17];
};

struct XtpGpioEvent
{
	char channel[17];
};

struct LongRangeEvent
{
    char szBandId[17];		// band id
    int nSignalStrength;	// 0-63 in dB
    int nChannel;			// 1 or 2
    int nPacketSequence;	// packet sequence number
    int nFrequency;			// frequency used
};

struct CarEvent
{
    char szVehicleId[64];
    char szAttractionId[17];
    char szSceneId[17];
    char szLocationId[17];
    char szConfidence[17];
};

#define XBR4_RADIOS 32
#define XBR4_ANTENNAS 8
struct XBRDiagEvent
{
    char szStatus[16];
    char szStatusMsg[64];
    char szTxRadioStatus[16];
    char szTxRadioStatusMsg[64];
    char szRadioStatus[XBR4_RADIOS][16];
    char szRadioStatusMsg[XBR4_RADIOS][64];
    char szBatteryLevel[16];
    char szBatteryCurrent[16];
    char szBatteryTime[16];
    char szTemperature[16];
};

typedef struct Event
{
    struct timeval tv;
    unsigned long long offset;
    enum EventType type;
    union {
        void *ptr;
        struct TapEvent *tap;
        struct LongRangeEvent *ping;
        struct CarEvent *car;
        struct XtpGpioEvent *xtpgpio;
        struct XBRDiagEvent *xbrdiag;
    };
} EVENT;

typedef struct EventInfo
{
    unsigned int nEventNumber;
    EVENT event;
} EVENTINFO;

void updateTime(
    unsigned long long offset,
    struct timeval *tvIn,
    struct timeval *tvOut);

#endif
