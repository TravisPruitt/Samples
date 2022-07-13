/**
    @file   FeigConstants.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef COMMANDS_H
#define COMMANDS_H

namespace RFID
{

// These defines came originally from some Feig library code.
// I have tried to organize them according to the Feig reader system manual, but
// some of the commands seem to be undocumented and a few of the commands may not
// be commands at all.  The comments below show the Feig reader system manual
// sections for the various commands.
namespace Command { enum Enum
{
    // Reader configuration commands
    ReadConfig = 0x80,
    WriteConfig = 0x81,
    SaveConfig = 0x82,
    SetDefaults = 0x83,
    WriteMifareKey = 0xA2,
    WriteUltralightKey = 0xA3,

    // Reader control
    BaudRateDetection = 0x52,
    StartFlashLoader = 0x55,
    CPUReset = 0x63,
    SystemReset = 0x64,
    GetSoftwareVersion = 0x65,
    GetReaderInfo = 0x66,
    RFReset = 0x69,
    SetRFPower = 0x6A,
    SetNoiseLevel = 0x6C,
    GetNoiseLevel = 0x6D,
    ReaderDiagnostic = 0x6E,
    BaseAntennaTuning = 0x6F,
    SetOutput = 0x71,
    SetOutput2 = 0x72,
    GetInput = 0x74,
    AdjustAntenna = 0x75,
    ReaderLogin = 0xA0,

    // ISO Host Commands
    ISOCmd = 0xB0,

        // ISO Host sub-commands
        Inventory = 0x01,
        Select = 0x25,
        ReadMultipleBlocks = 0x23,

    // ISO 1443 Special Host Commands
    ISO1443SpecialHostCmd = 0xB2,

        // ISO 1443 sub-commands
        ISO1443TCL = 0xBE,
        AuthenticateUltralightC = 0xB2,

    // ISO Host Commands for DESFire
    DESFireCmd = 0xC1,

        // Desfire sub-commands
        ReadDESFireData = 0xBD,

    // Special commands for transponder communications
    MifareValueCmd = 0x30,

    // SAM Commands
    SAMCmd = 0xC0,

        // SAM sub-commands
        SAM_Activate = 0x01,
        SAM_T0 = 0xBD,
        SAM_T1 = 0xBE,

    // Unknown commands
    Destroy = 0x18,         // EPC Kill Command
    Halt = 0x1A,
    Reset = 0x1B,
    EAS = 0x1C,
    Read = 0x21,            // ISC LR200/LR2000 Only
    BufferedRead = 0x22,
    ReadBufferInfo = 0x31,
    ClearBuffer = 0x32,
    InitBuffer = 0x33,
    ForceNotifyTrigger = 0x34,
    SetSystemTimer = 0x85,
    GetSystemTimer = 0x86,
    SetSystemDate = 0x87,
    GetSystemData = 0x88,
    AuthentAES = 0xAA,
};}


namespace APDUCommand { enum Enum
{
    IS014443Cmd = 0xB2
};}

namespace CommandMode { enum Enum 
{
    First = 0x80,
    More = 0x40,
    Ping = 0x08,
    NAD = 0x04,
    CID = 0x02,
    INF = 0x01
};}


/// Mode flags for inventory operations
namespace InventoryMode { enum Enum 
{
    /// Default settings - no other flags set
    Default = 0x00,

    /// Used to retrieve additional data when a previous inventory 
    /// request returned a 0x94 More Data Available status
    MoreData = 0x80,

    /// Used to enable reader-driven inventory with asynchronous notification.
    /// Not supported for ISO/IEC 14443 tags
    Notification = 0x40,

    /// Used to enable Presence Check mode - 
    /// only valid if AntiCollisionMode includes OnlyNewTags & AntiCollisionEnabled
    PresenceCheck = 0x20,
};}


/// Mode flags for the Select command
namespace SelectMode { enum Enum 
{
    /// The command includes the UID of the tag to select
    Addressed = 0x01,

    /// The UID length is specified
    UIDLengthSpecified = 0x10,

    /// Card information will be included in the response
    ReturnCardInfo = 0x20
};}

namespace FeigStatus { enum Enum 
{
    OK = 0x00,

    // Transponder status
    NoTransponder = 0x01,
    DataFalse = 0x02,
    WriteError = 0x03,
    AddressError = 0x04,
    WrongTransponderType = 0x05,
    AuthentError = 0x08,
    GeneralError = 0x0E,
    RFCommError = 0x83,
    DataBufferOverlow = 0x93,
    MoreData = 0x94,
    ISO15693Error = 0x95,
    ISO14443Error = 0x96,
    CryptoProcessingError = 0x97,

    // Parameter status
    EEPROMFailure = 0x10,
    ParameterRangeError = 0x11,

    // Interface status
    UnknownCommand = 0x80,
    LengthError = 0x81,
    CommandUnavailable = 0x82,

    // Reader status
    HardwareWarning = 0xf1,

    // SAM status
    NoSAM = 0x31,
    SAMNotActivated = 0x32,
    SAMAlreadyActivated = 0x33,
    ProtocolNotSupported = 0x34,
    SAMCommError = 0x35,
    Timeout = 0x36,
    UnsupportedSAMBaudRate = 0x37

}; }


/// <summary>
/// SAM activation/deactivation states
/// </summary>
namespace SAMActivationMode { enum Enum
{
    /// Deactivates a SAM
    Deactivate = 0x00,

    /// Activates a SAM in T=0 protocol mode
    ActivateT0 = 0x01,

    /// Activates a SAM in T=1 protocol mode
    ActivateT1 = 0x03,

    /// Requests the ATR (Answer-To-Reset) sequence from a SAM
    GetATR = 0x02,

    /// Requested communication speed will be specified with the SAM activation command
    SpecifyBaud = 0x08
}; }


    
/// SAM communication rates
namespace SAMBaudRate { enum Enum 
{
    baud2400 = 0x51,
    baud3200 = 0x41,
    baud4800 = 0x31,
    baud6400 = 0x21,
    baud9600 = 0x01,
    baud19200 = 0x02,
    baud38400 = 0x03,
    baud51200 = 0xC5,
    baud57600 = 0x38,
    baud76800 = 0x35,
    baud102400 = 0xC6,
    baud115200 = 0x18,
    baud153600 = 0xB6,
    baud307200 = 0x96
};}



}


#endif // COMMANDS_H
