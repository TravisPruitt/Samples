/**
    @file   SAMEnums.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef SAM_ENUMS_H
#define SAM_ENUMS_H

namespace RFID
{

/// PICC Authentication Options
namespace PICCAuthenticationMode { enum Enum 
{
    /// Default options = NoKeyDiversity | UseSAMKeyNo | TwoRoundDiversify | DiversifyAV1
    Default = 0x00,

    /// Keys are diversified using a supplied input vector
    DiversifyKeys = 0x01,

    /// Keys are not diversified
    NoKeyDiversity = 0x00,

    /// Key number is DESFire application key #
    UseDESFireKeyNo = 0x02,

    /// Key number is SAM KST entry #
    UseSAMKeyNo = 0x00,

    /// Diversify using one encryption round
    OneRoundDiversify = 0x08,

    /// Diversify using two encryption rounds
    TwoRoundDiversify = 0x00,

    /// Use AV2 diversification algorithm
    DiversifyAV2 = 0x10,

    /// Use AV1 compatible diversification
    DiversifyAV1 = 0x00
};}

}

#endif // SAM_ENUMS_H
