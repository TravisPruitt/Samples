#ifndef __RADIO_COMMANDS_H__
#define __RADIO_COMMANDS_H__

#define PACK    __attribute__((__packed__))
//#define PACK

#include <stdint.h>


typedef uint8_t RadioSpiTestData[16];
typedef char    RadioFirmwareVersion[8];
typedef uint8_t RadioAddress[5];

typedef enum
{
    Rssi_Continuous,
    Rssi_FreezeAtSync,
    Rssi_ReakDetect,
    Rssi_ContinuousBeforeSync_thenPeakDetect,
} RadioRssiMode;

typedef struct {
    RadioAddress    address;
    uint8_t         sequenceNumber;
    uint8_t         tbd[2];
} RadioPingPayload;

typedef struct {
    RadioPingPayload    payload;
    uint8_t             signalStrength;
} RadioPingPackage;


typedef struct
{
    Unsigned_32 rxOk ;
    Unsigned_32 rxCrcError ;
    Unsigned_32 sequenceNumbersSkipped ;
    Unsigned_32 tx ;
} RadioPacketStatistics ;


typedef enum
{
    RadioPacketNil,
    RadioPacketGood,
    RadioPacketBad
} RadioPacketStatus ;

// extended package for test
typedef struct
{
    RadioPingPayload  payload ;
    Byte              signalStrength ;
    Byte              status ;          // this is really a RadioPacketStatus
} RadioPingPackagePlus ;


typedef struct {
    RadioAddress    address;
    uint8_t         command;
    uint8_t         data[5];
} RadioCommandPacket;

struct RadioMessage {
    uint8_t         control;
    union
    {
        RadioFirmwareVersion    version;
        uint16_t                frequency;
        RadioAddress            address ;
        int8_t                  db;
        uint8_t                 beaconPeriod;
        uint8_t                 gain;
        uint8_t                 rssiMode;   // RadioRssiMode
        uint8_t                 length;
        uint8_t                 enable;
        uint8_t                 anyUnsignedByte ;
        RadioPingPackage        pingPackage;
        RadioPingPackagePlus    pingPackagePlus ;
        RadioCommandPacket      commandPacket;
        RadioPacketStatistics   packetStats ;
        RadioSpiTestData        spiTestData ;
    } u;
} PACK;

typedef enum
{
    RadioControl_DoNothing = 0,            // data from union above
    RadioControl_GetVersion,               // version
    RadioControl_SetFrequency,             // frequency (little-endian)
    RadioControl_GetFrequency,             // frequency (little-endian)

    // rx
    RadioControl_SetGain,                   // gain
    RadioControl_GetGain,                   // gain
    RadioControl_SetRssiMode,               // rssiMode
    RadioControl_GetRssiMode,               // rssiMode
    RadioControl_SetPingReceiveAddress,     // address
    RadioControl_GetPingReceiveAddress,     // address
    RadioControl_SetRxLength,               // length
    RadioControl_GetRxLength,               // length
    RadioControl_SetRxEnable,               // enable
    RadioControl_GetRxEnable,               // enable
    RadioControl_GetPing,                   // pingPackage

    // tx
    RadioControl_SetTxPower,                // gain
    RadioControl_GetTxPower,                // gain
    RadioControl_SetBeaconPacket,           // commandPacket
    RadioControl_GetBeaconPacket,           // commandPacket
    RadioControl_SetBeaconPeriod,           // beaconPeriod
    RadioControl_GetBeaconPeriod,           // beaconPeriod
    RadioControl_SetBeaconEnable,           // enable
    RadioControl_GetBeaconEnable,           // enable
    RadioControl_TxCommand,                 // commandPacket
    RadioControl_TxCommandRepeat,
    RadioControl_TxCommandGet,              // commandPacket

    // also...
    RadioControl_SetYellowLED,
    RadioControl_SetRxCrcEnable,
    RadioControl_SetCwEnable,
    RadioControl_GetSignalStrength,
    RadioControl_GetPacketStatistics,
    RadioControl_ForceReset,
    RadioControl_GetResetStatus,
    RadioControl_GetPingPlus,
    RadioControl_SetStatsEnable,
    RadioControl_SetSpiTestData,

} RadioControl;

#endif // __RADIO_COMMANDS_H__

