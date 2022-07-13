#ifndef _RADIO_H_
#define _RADIO_H_

#include "types.h"

#include "radiocommands.h"


typedef struct
{
	unsigned index; // 1:1 mapping to radio index
} ReaderRadio ;


void    radio_set_getPingTimeout           (ReaderRadio *, Unsigned_16 milliseconds) ;
RadioPingPackage radio_repeatGetRxPacket (ReaderRadio * radioPtr);

char * radio_getFirmwareVersion (ReaderRadio *) ;

Byte radio_getResetCause (ReaderRadio *);
void radio_forceReset    (ReaderRadio *);

void radio_setLED (ReaderRadio *, Byte ledState) ;

void        radio_setFrequency  (ReaderRadio *, Unsigned_16 MHz) ;
Unsigned_16 radio_getFrequency  (ReaderRadio *) ;

void               radio_setBeacon (ReaderRadio *, RadioCommandPacket) ;
RadioCommandPacket radio_getBeacon (ReaderRadio *) ;

void    radio_setBeaconEnable (ReaderRadio *, Boolean enable) ;
Boolean radio_getBeaconEnable (ReaderRadio *) ;

void radio_setCrcEnable (ReaderRadio *, Boolean enable) ;

void    radio_setRxEnable (ReaderRadio *, Boolean enable) ;
Boolean radio_getRxEnable (ReaderRadio *) ;

void    radio_setTxPower (ReaderRadio *, int8_t) ;
int8_t radio_getTxPower (ReaderRadio *) ;

void                  radio_setStatsEnable (ReaderRadio *, Boolean enable) ;
RadioPacketStatistics radio_getStatistics  (ReaderRadio *);

int radio_getRssi (ReaderRadio *) ;

RadioPingPackage     radio_getRxPacket     (ReaderRadio *) ;
RadioPingPackage     radio_getRxPacketDuplex(ReaderRadio *) ;
RadioPingPackagePlus radio_getRxPacketPlus (ReaderRadio *) ;

void radio_setCwEnable(ReaderRadio *radioPtr, Byte enable);
Byte radio_getBeaconEnable(ReaderRadio * radioPtr);
void radio_setBeaconEnable(ReaderRadio *radioPtr, Byte enable);
Byte radio_getBeaconPeriod(ReaderRadio * radioPtr);
void radio_setBeaconPeriod(ReaderRadio *radioPtr, Byte period);
RadioCommandPacket radio_getTxCommand(ReaderRadio * radioPtr);
void radio_setTxCommand(ReaderRadio *radioPtr, RadioCommandPacket cmd);
void radio_TxCommandRepeat(ReaderRadio *radioPtr);

int radio_getRssiOffset(ReaderRadio *radioPtr);
void radio_setRssiOffset(ReaderRadio *radioPtr, int offset);

void radio_setFccTest(ReaderRadio *radioPtr, Byte enable);

unsigned radio_getRadioCount(void);

void radio_Initialize(void);


#endif

