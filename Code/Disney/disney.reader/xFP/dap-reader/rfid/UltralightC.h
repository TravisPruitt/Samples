/**
    @file   UltralightC.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef ULTRALIGHT_C_H
#define ULTRALIGHT_C_H

#include "RFIDTag.h"
#include "IReader.h"


namespace RFID
{

class UltralightC : public RFIDTag
{
public:
    UltralightC(UID& uid);
    virtual bool readIds(IReader& reader, bool readSecureId);

private:    // methods
    bool readPublicData(IReader& reader);
    bool readSecureData(IReader& reader);

private:    // data
};


}

#endif
