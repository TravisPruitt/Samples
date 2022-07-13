/**
    @file   SAMOldMasterKey.h - SAM master key, key used to change all other keys
    @author Greg Strange
    @date   July 2012

    Copyright (c) 2012, synapse.com
*/



#ifndef __SAM_OLD_MASTER_KEY_H
#define __SAM_OLD_MASTER_KEY_H

#include "AES128Key.h"

namespace RFID
{
    const uint8_t SAMOldMasterKeyBytes[] = {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0};

    class SAMOldMasterKey : public AES128Key
    {
    public:
        SAMOldMasterKey() : AES128Key(SAMOldMasterKeyBytes, 16) {};
        ~SAMOldMasterKey() {};
    };
};


#endif

