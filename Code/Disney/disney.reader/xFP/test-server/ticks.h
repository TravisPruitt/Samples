/**
    ticks.h
    Greg Strange

    Copyright (c) 2011, synapse.com
*/
#ifndef __TICKS_H
#define __TICKS_H

#include <stdint.h>

typedef uint32_t MILLISECONDS;
typedef uint32_t MICROSECONDS;

#ifdef __cplusplus
extern "C" {
#endif

MILLISECONDS getMilliseconds();
MICROSECONDS getMicroseconds();
void sleepMilliseconds(MILLISECONDS delay);

#ifdef __cplusplus
}
#endif

#endif

