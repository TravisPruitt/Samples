/**
    @file   RemoteEEPROM.h 
    @date   September 2012

    Copyright (c) 2012, synapse.com

    Defines the contents of EEPROM on remote hardware.

    Currently, the only remote hardaware that this applies to is Camilla, but
    other hardware and other data structures versions can be supported through
    use of the 'format' and 'hw' fields that should always be the first two
    fields in the structure.
*/



#ifndef __REMOTE_EEPROM_H
#define __REMOTE_EEPROM_H


// This enum defines which structure to use.
enum {
    RemoteEEPROM_format_Camilla_1 = 1,
    RemoteEEPROM_format_unknown = 0xffff
};



// This is the first revision of the structure for Camilla
struct CamillaEEPROM_1
{
    char description[64];           // description of hardware (type and revision)
    char serialNumber[32];          // remote hardware serial number
    char buildInfo[64];             // text field typically containing build date and run number

    // Color calibration values
    //    Valid offsets are >= 0 and <= 255
    //    valid slopes are > 0 and <= 1
    // Any invalid values are replaced by built in defaults
    float redOffset;
    float redSlope;
    float greenOffset;
    float greenSlope;
    float blueOffset;
    float blueSlope;
};


struct RemoteEEPROM
{
    
    uint16_t format;                // remote eeprom format.  (RemoteEEPROM_format_Camilla_1)
    union {
        struct CamillaEEPROM_1 camilla;
    } u;
};


#endif
