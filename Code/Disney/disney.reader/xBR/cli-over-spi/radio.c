
#include <string.h>
#include <unistd.h>

#include "radio.h"
#include "monitor.h"
#include "radiocommands.h"
#include "groverlib.h"

static void setRadioParameter (ReaderRadio * radioPtr,
			       struct RadioMessage * messagePtr,
			       Byte argumentLength)
{
	// use this function to send a command with an argument
	uint8_t rx;

	groverlib_radio_tx_rx(radioPtr->index,
			      (Byte*)messagePtr,
			      sizeof(messagePtr->control) + argumentLength,
			      &rx, 1);
}



static void getRadioParameter (ReaderRadio * radioPtr,
			       struct RadioMessage * messagePtr,
			       Byte argumentLength)
{
	// use this function to send a 1-byte command and then receive
	// a reply with the given length

	uint8_t tx[1];

	tx[0] = messagePtr->control;

	groverlib_radio_tx_rx(radioPtr->index,
			      tx, 1,
			      (Byte*)messagePtr,
			      sizeof(messagePtr->control) + argumentLength);
}

static void radio_writeCommand(ReaderRadio *radio, Byte control, Byte arg)
{
    struct RadioMessage command;

    command.control = control;
    command.u.anyUnsignedByte = arg;
    setRadioParameter(radio, &command, 1);
}

static Byte radio_readCommand(ReaderRadio *radio, Byte control)
{
    struct RadioMessage command;

    command.control = control;
    getRadioParameter(radio, &command, 1);

    return command.u.anyUnsignedByte ;
}


char * radio_getFirmwareVersion (ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_GetVersion ;
    Byte argumentLength = sizeof(command.u.version) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    // put the result in a local static buffer
    static char versionString [ArrayLength (command.u.version)] ;
    strncpy (versionString, command.u.version, ArrayLength(versionString)) ;
    versionString [ArrayLength(versionString) - 1] = 0 ;    // ensure null termination

    return versionString ;
}



Byte radio_getResetCause (ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_GetResetStatus ;
    Byte argumentLength = sizeof(command.u.anyUnsignedByte) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.anyUnsignedByte ;
}

void radio_forceReset (ReaderRadio * radioPtr)
{
    groverlib_radio_reset(radioPtr->index);
}



void radio_setLED (ReaderRadio * radioPtr, Byte state)
{
    struct RadioMessage command ;

    command.control = RadioControl_SetYellowLED ;
    command.u.anyUnsignedByte = state ;
    Byte argumentLength = sizeof(command.u.anyUnsignedByte) ;

    setRadioParameter (radioPtr, & command, argumentLength) ;
}



void radio_setFrequency (ReaderRadio * radioPtr, Unsigned_16 MHz)
{
    struct RadioMessage command ;

    command.control = RadioControl_SetFrequency ;
    command.u.frequency = MHz ;
    Byte argumentLength = sizeof(command.u.frequency) ;

    setRadioParameter (radioPtr, & command, argumentLength) ;
}



Unsigned_16 radio_getFrequency (ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_GetFrequency ;
    Byte argumentLength = sizeof(command.u.frequency) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.frequency ;
}



void radio_setCrcEnable (ReaderRadio * radioPtr, Boolean enable)
{
    struct RadioMessage command ;

    command.control = RadioControl_SetRxCrcEnable ;
    command.u.enable = enable ;
    Byte argumentLength = sizeof(command.u.enable) ;

    setRadioParameter (radioPtr, & command, argumentLength) ;
}



void radio_setStatsEnable (ReaderRadio * radioPtr, Boolean enable)
{
    struct RadioMessage command ;

    command.control = RadioControl_SetStatsEnable ;
    command.u.enable = enable ;
    Byte argumentLength = sizeof(command.u.enable) ;

    setRadioParameter (radioPtr, & command, argumentLength) ;
}



RadioPacketStatistics radio_getStatistics  (ReaderRadio * radioPtr)
{
    struct RadioMessage command;

    memset(&command, 0, sizeof(command));

    command.control = RadioControl_GetPacketStatistics ;
    Byte argumentLength = sizeof(command.u.packetStats) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.packetStats ;
}



void radio_setRxEnable (ReaderRadio * radioPtr, Boolean enable)
{
    struct RadioMessage command ;

    command.control = RadioControl_SetRxEnable ;
    command.u.enable = enable ;
    Byte argumentLength = sizeof(command.u.enable) ;

    setRadioParameter (radioPtr, & command, argumentLength) ;
}

Boolean radio_getRxEnable (ReaderRadio * radioPtr)
{
#if 0
    struct RadioMessage command ;

    command.control = RadioControl_GetRxEnable ;
    Byte argumentLength = sizeof(command.u.enable) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.enable ;
#endif
    return radio_readCommand(radioPtr, RadioControl_GetRxEnable);
}

void radio_setBeacon(ReaderRadio *radioPtr, RadioCommandPacket beacon)
{
    struct RadioMessage command ;

    command.control = RadioControl_SetBeaconPacket;
    command.u.commandPacket = beacon;
    Byte argumentLength = sizeof(command.u.commandPacket);
    setRadioParameter (radioPtr, & command, argumentLength) ;
}

RadioCommandPacket radio_getBeacon(ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_GetBeaconPacket;
    Byte argumentLength = sizeof(command.u.commandPacket) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.commandPacket ;
}

void radio_TxCommandRepeat(ReaderRadio *radioPtr)
{
    struct RadioMessage command;
    command.control = RadioControl_TxCommandRepeat;
    setRadioParameter(radioPtr, &command, 0);
}

void radio_setTxCommand(ReaderRadio *radioPtr, RadioCommandPacket cmd)
{
    struct RadioMessage command ;

    command.control = RadioControl_TxCommand;
    command.u.commandPacket = cmd;
    Byte argumentLength = sizeof(command.u.commandPacket);
    setRadioParameter (radioPtr, & command, argumentLength) ;
}

RadioCommandPacket radio_getTxCommand(ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_TxCommandGet;
    Byte argumentLength = sizeof(command.u.commandPacket) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.commandPacket ;
}

void radio_setTxPower(ReaderRadio * radioPtr, int8_t gain)
{
    radio_writeCommand(radioPtr, RadioControl_SetTxPower, (uint8_t)gain);
}

int8_t radio_getTxPower(ReaderRadio * radioPtr)
{
    return (int8_t)radio_readCommand(radioPtr, RadioControl_GetTxPower);
}

void radio_setBeaconPeriod(ReaderRadio *radioPtr, Byte period)
{
    radio_writeCommand(radioPtr, RadioControl_SetBeaconPeriod, period);
}

Byte radio_getBeaconPeriod(ReaderRadio * radioPtr)
{
    return radio_readCommand(radioPtr, RadioControl_GetBeaconPeriod);
}

void radio_setBeaconEnable(ReaderRadio *radioPtr, Byte enable)
{
    radio_writeCommand(radioPtr, RadioControl_SetBeaconEnable, enable);
}

Byte radio_getBeaconEnable(ReaderRadio * radioPtr)
{
    return radio_readCommand(radioPtr, RadioControl_GetBeaconEnable);
}

void radio_setCwEnable(ReaderRadio *radioPtr, Byte enable)
{
    radio_writeCommand(radioPtr, RadioControl_SetCwEnable, enable);
}

int radio_getRssi (ReaderRadio * radioPtr)
{
    return (int8_t)radio_readCommand(radioPtr, RadioControl_GetSignalStrength);
}

RadioPingPackage radio_repeatGetRxPacket (ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_GetPing ;
    Byte argumentLength = sizeof(command.u.pingPackage) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.pingPackage ;
}

RadioPingPackage radio_getRxPacket (ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_GetPing ;
    Byte argumentLength = sizeof(command.u.pingPackage) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.pingPackage ;
}

#if 0
RadioPingPackage radio_getRxPacketDuplex (ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    memset(&command, 0, sizeof(command));
    command.control = RadioControl_GetPing ;
    Byte argumentLength = sizeof(command.u.pingPackage) ;

    getRadioParameterDuplex(radioPtr, & command, argumentLength) ;

    return command.u.pingPackage ;
}
#endif


RadioPingPackagePlus radio_getRxPacketPlus (ReaderRadio * radioPtr)
{
    struct RadioMessage command ;

    command.control = RadioControl_GetPingPlus ;
    Byte argumentLength = sizeof(command.u.pingPackagePlus) ;

    getRadioParameter (radioPtr, & command, argumentLength) ;

    return command.u.pingPackagePlus ;
}

void radio_set_getPingTimeout (ReaderRadio * radioPtr, Unsigned_16 milliseconds)
{
    struct RadioMessage command ;

    command.control = RadioControl_Set_GetPingTimeout ;
    command.u.pingTimeout = milliseconds ;
    Byte argumentLength = sizeof(command.u.pingTimeout) ;

    setRadioParameter (radioPtr, & command, argumentLength) ;
}

int radio_getRssiOffset(ReaderRadio *radioPtr)
{
    return (int8_t)radio_readCommand(radioPtr, RadioControl_GetRssiOffset);
}

void radio_setRssiOffset(ReaderRadio *radioPtr, int offset)
{
    radio_writeCommand(radioPtr, RadioControl_SetRssiOffset, (uint8_t)offset);
}

void radio_setFccTest(ReaderRadio *radioPtr, Byte enable)
{
    radio_writeCommand(radioPtr, RadioControl_SetFccEnable, enable);
}

unsigned radio_getRadioCount(void)
{
	return groverlib_radio_count();
}

void radio_Initialize(void)
{
	groverlib_initialize();
}
