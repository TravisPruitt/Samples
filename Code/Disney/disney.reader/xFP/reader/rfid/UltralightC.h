/**
    @file   UltralightC.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef ULTRALIGHT_C_H
#define ULTRALIGHT_C_H

#include "RFIDTag.h"
#include "SAM.h"


namespace RFID
{

class UltralightC : public RFIDTag
{
public:
    UltralightC(ByteArray& uid);
    virtual void readIds(SAM* sam, bool readSecureId);

private:    // methods
    void readPublicData(SAM* sam);
    void readSecureData(SAM* sam);

private:    // data
};


}

#endif
