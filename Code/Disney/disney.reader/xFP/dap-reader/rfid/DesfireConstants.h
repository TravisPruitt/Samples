/**
    @file   DesfiireConstants.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef __DESFIRE_CONSTANTS_H
#define __DESFIRE_CONSTANTS_H


namespace RFID
{


/// DESFire Native Protocol Command Values
namespace DesfireCommand { enum Enum 
{
    GetChallengeTokenDES_DESFire4 = 0x0A,
    GetChallengeTokenDES = 0x1A,
    GetChallengeTokenAES = 0xAA,
        
    More = 0xAF,

    SelectAID = 0x5A,       

    GetKeySettings = 0x45,
    ChangeKeySettings = 0x54,

    ChangeKey = 0xC4,

    GetKeyVersion = 0x64,

    CreateApplication = 0xCA,
    DeleteApplication = 0xDA,

    GetAIDs = 0x6A,
    GetDFNames = 0x6D,
       
    FormatPICC = 0xFC,

    GetVersion = 0x60,
    FreeMemory = 0x6E,

    SetConfiguration = 0x5C,

    GetUID = 0x51,

    GetFileIDs = 0x6F,
    GetISOFileIDs = 0x61,

    GetFileSettings = 0xF5,
    ChangeFileSettings = 0x5F,

    CreateStdFile = 0xCD,
    CreateBackupFile = 0xCB,
    CreateValueFile = 0xCC,
    CreateLinearLog = 0xC1,
    CreateCyclicLog = 0xC0,

    DeleteFile = 0xDF,

    ReadData = 0xBD,
    WriteData = 0x3D,

    GetValue = 0x6C,
    Credit = 0x0C,
    Debit = 0xDC,
    LimitedCredit = 0x1C,

    WriteRecord = 0x3B,
    ReadRecords = 0xBB,
    ClearRecords = 0xEB,

    CommitTrans = 0xC7,
    AbortTrans = 0xA7
};}

// TODO - fill in all desfire status/error codes?
namespace DesfireStatus { enum Enum 
{
    OK = 0x00,
    ADDITIONAL_FRAME = 0xAF
};}

}

#endif