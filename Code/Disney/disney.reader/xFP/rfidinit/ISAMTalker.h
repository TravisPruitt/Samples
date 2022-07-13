/**
    @file   ISAMTalker.h - Interface to SAM
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/



#ifndef __ISAM_INTERFACE_H
#define __ISAM_INTERFACE_H

#include "standard.h"
#include "ByteArray.h"


namespace RFID
{
    class ISAMTalker
    {
    public:
        virtual void sendSAMCommand(const uint8_t* payload, unsigned length) = 0;
        virtual void readSAMReply(Reader::ByteArray& reply, unsigned timeoutMs = 1000) = 0;
    };
};


#endif

