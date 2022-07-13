/**
 *  @file   GPIOTrigger.h
 *  @author Corey Wharton
 *  @date   Oct, 2011
 *
 *  Copyright (c) 2012, synapse.com
 */

#include <linux/input.h>

#include "Thread.h"
#include "ticks.h"

#ifndef __GPIOTRIGGER_H
#define __GPIOTRIGGER_H

// Since the input subsystem is being used to detect
// GPIO trigger events, the BTN_X codes are used indicate
// which "channel" was triggered and should be sequential
// values as defined in input.h.
#define MIN_GPIO_CHANNEL_CODE (BTN_2)
#define MAX_GPIO_CHANNEL_CODE (BTN_2)
#define NUM_GPIO_CHANNELS ((MAX_GPIO_CHANNEL_CODE - MIN_GPIO_CHANNEL_CODE) + 1)

namespace Reader
{

class GPIOTrigger : public Thread
{
public:
    static GPIOTrigger* instance();

private: // data

    // Contains the time of the last trigger event for each channel
    MILLISECONDS _lastTriggered[NUM_GPIO_CHANNELS];

    // Holds the minimum amount of time (in ms) between
    // trigger events
    MILLISECONDS _minTriggerInterval;

private: // methods
    // singleton
    GPIOTrigger();
    ~GPIOTrigger();

    void postTriggerEvent(unsigned channel);

    void run();
};

}

#endif  // __GPIOTRIGGER_H
