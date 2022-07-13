/**
    @file   ticks.c         Platform independent millisecond and microsecond timers
    @Author Greg Strange

    Copyright (c) 2011, synapse.com
*/

#include "ticks.h"

#ifdef _WIN32
#include "Windows.h"


/**
    Get the millisecond counter.
    
    @return millisecond counter
*/
MILLISECONDS getMilliseconds()
{
    return GetTickCount();
}


/**
    Get the microsecond counter.
    
    @return Microsecond counter
    
    QueryPerformanceCounter() uses 64 bit counters.  The frequency depends on the clock frequency
    of the processor.  With 64 bits, the counter should not wrap for several decades even with
    processors well up in the GHz range.

    If the hardware does not support the QueryPerformanceCounter() then we use the GetTickCount()
    millisecond clock instead.
*/
MICROSECONDS getMicroseconds()
{
    __int64 counter;
    __int64 frequency;

    if (!QueryPerformanceCounter((LARGE_INTEGER*)&counter) ||
        !QueryPerformanceFrequency((LARGE_INTEGER*)&frequency) ||
        frequency == 0) 
    {
        return GetTickCount() * 1000;
    }

    return (MICROSECONDS)( ((double)counter / (double)frequency) * 1000.0 * 1000.0);
}


/**
    Sleep for 'delay' milliseconds

    @param  delay   How long to sleep in milliseconds
*/
void sleepMilliseconds(MILLISECONDS delay)
{
    Sleep(delay);
}



#else

#include <sys/time.h>
#include <unistd.h>

MILLISECONDS getMilliseconds()
{
    struct timeval t;
    gettimeofday(&t, NULL);
    return (t.tv_sec * 1000 + t.tv_usec / 1000);
}

MICROSECONDS getMicroseconds()
{
    struct timeval t;
    gettimeofday(&t, NULL);

    MICROSECONDS temp = t.tv_sec;
    return (temp * 1000 * 1000) + t.tv_usec;
}

void sleepMilliseconds(MILLISECONDS delay)
{
    usleep(delay * 1000);
}

#endif
