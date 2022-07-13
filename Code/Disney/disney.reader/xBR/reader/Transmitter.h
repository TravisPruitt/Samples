/**
    @file   Transmitter.h 
    @author Mike Wilson, Greg Strange

    Copyright (c) 2011, synapse.com
*/


#ifndef __TRANSMITTER_H
#define __TRANSMITTER_H

#include "grover.h"
#include "Radio.h"
#include <string>
#include <vector>

using namespace Reader;

class Transmitter
{
public:
    // A beacon sent to this ID will be received by all bands
    static const BandId BroadcastId = 0xFF4BE492E4ull;

    // must be called before using any other functions
    static void init();

    // beacon commands (sent continously back to back)
    static void startBeacon(uint64_t id, uint8_t* cmd);
    static void stopBeacon();
    static bool getBeacon(uint64_t& id, uint8_t* cmd);

    // targeted commands (sent in response to pings)
    static void setReply(uint64_t band_id, int8_t threshold, const uint8_t *data);
    static void setReplyAll(int8_t threshold, uint8_t *data);
    static void clearReply(BandId id);
    static void clearReplies();
    static void setReplyTimeout(unsigned timeout);
    static unsigned getReplyTimeout();
};

#endif 
