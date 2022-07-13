/**
    @file   Desfire.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef DESFIRE_H
#define DESFIRE_H

#include "RFIDTag.h"
#include "SAM.h"
#include "ByteArray.h"


namespace RFID
{

class Desfire : public RFIDTag
{
public:
    Desfire(ByteArray& uid);

    virtual void readIds(SAM* sam, bool readSecureId);

private:
    void selectApp(SAM* sam);
    void readPublicData(SAM* sam);
    void readSecureData(SAM* sam);
    void readFile(SAM* sam, unsigned fileNo, unsigned offset, unsigned length, ByteArray& buf);
};


}

#endif
