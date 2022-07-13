/**
    @file   Desfire.h
    @author Greg Strange
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef DESFIRE_H
#define DESFIRE_H

#include "RFIDTag.h"


namespace RFID
{

class Desfire : public RFIDTag
{
public:
    Desfire(UID& uid);

    virtual bool readIds(IReader& reader, bool readSecureId);

private:
    void selectApp(IReader& reader);
    bool readPublicData(IReader& reader);
    bool readSecureData(IReader& reader);
    bool readFile(IReader& reader, unsigned fileNo, unsigned offset, unsigned length, ByteArray& buf);
};


}

#endif
