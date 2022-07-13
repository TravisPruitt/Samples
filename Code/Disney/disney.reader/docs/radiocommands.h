#ifndef __RADIO_COMMANDS_H__
#define __RADIO_COMMANDS_H__

#define PACK	__attribute__((__packed__))
//#define PACK

typedef char RadioFirmwareVersion[8];
typedef uint8_t RadioAddress[5];

typedef enum
{
	Rssi_Continuous,
	Rssi_FreezeAtSync,
	Rssi_ReakDetect,
	Rssi_ContinuousBeforeSync_thenPeakDetect,
} RadioRssiMode;

typedef struct {
	RadioAddress	address;
	uint8_t		sequenceNumber;
	uint8_t		tbd[2];
} RadioPingPayload;

typedef struct {
	RadioPingPayload	payload;
	uint8_t			signalStrength;
} RadioPingPackage;

typedef struct {
	RadioAddress    address;
	uint8_t		command;
	uint8_t		data[5];
} RadioCommandPacket;

struct RadioMessage {
	uint8_t		control;
	union
	{
		RadioFirmwareVersion		version;
		uint8_t				frequency[2];
		int8_t				db;
		uint8_t				beaconPeriod;
		uint8_t				gain;
		uint8_t				rssiMode; // RadioRssiMode
		uint8_t				length;
		uint8_t				enable;
		RadioPingPackage		pingPackage;
		RadioCommandPacket		commandPacket;
	} u;
} PACK;

typedef enum
{
	RadioControl_DoNothing = 0,		// data from union above
	RadioControl_GetVersion,		// version
	RadioControl_SetFrequency,		// frequency (little-endian)
	RadioControl_GetFrequency,		// frequency (little-endian)
	// rx
	RadioControl_SetGain,			// gain
	RadioControl_GetGain,			// gain
	RadioControl_SetRssiMode,		// rssiMode
	RadioControl_GetRssiMode,		// rssiMode
	RadioControl_SetPingReceiveAddress,	// address
	RadioControl_GetPingReceiveAddress,	// address
	RadioControl_SetRxLength,		// length
	RadioControl_GetRxLength,		// length
	RadioControl_SetRxEnable,		// enable
	RadioControl_GetRxEnable,		// enable
	RadioControl_GetPing,			// pingPackage
	// tx
	RadioControl_SetTxPower,		// gain
	RadioControl_GetTxPower,		// gain
	RadioControl_SetBeaconPacket,		// commandPacket
	RadioControl_GetBeaconPacket,		// commandPacket
	RadioControl_SetBeaconPeriod,		// beaconPeriod
	RadioControl_GetBeaconPeriod,		// beaconPeriod
	RadioControl_SetBeaconEnable,		// enable
	RadioControl_GetBeaconEnable,		// enable
	RadioControl_TxCommand,			// commandPacket
	RadioControl_TxCommandRepeat,
	RadioControl_TxCommandGet,		// commandPacket
} RadioControl;

#endif // __RADIO_COMMANDS_H__
