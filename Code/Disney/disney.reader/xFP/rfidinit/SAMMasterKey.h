/**
    @file   SAMMasterKey.h - SAM master key, key used to change all other keys
    @author Greg Strange
    @date   July 2012

    Copyright (c) 2012, synapse.com
*/



#ifndef __SAM_MASTER_KEY_H
#define __SAM_MASTER_KEY_H

#include "DesKey.h"

namespace RFID
{
    const uint8_t SAMMasterKeyBytes[8] = {0, 0, 0, 0,  0, 0, 0, 0};

    class SAMMasterKey : public DesKey
    {
    public:
        SAMMasterKey() : DesKey(SAMMasterKeyBytes, 8) {};
        ~SAMMasterKey() {};
    };
};


#endif
