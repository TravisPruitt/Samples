#ifndef _TIME_INCLUDED_
#define _TIME_INCLUDED_


#include "types.h"

#include <time.h>


typedef struct timespec TimeSpec;

typedef char        TimeString [32];
typedef char DateAndTimeString [32];


void time_delayMilliseconds (Unsigned_16 milliseconds);

Unsigned_32 time_nanosecondResolution (void);


unsigned long    time_now_ms      (void);
TimeSpec    time_now      (void);

TimeSpec    time_since    (TimeSpec);
TimeSpec    time_fromTo   (TimeSpec from, TimeSpec to);

void time_toDateAndTimeString  (TimeSpec, DateAndTimeString);

void        time_toString      (TimeSpec, TimeString);
Real        time_asRealSeconds (TimeSpec);


typedef struct
{
    TimeSpec    startTime ;
    Unsigned_32 cumulativeMilliseconds ;
    Boolean     running ;
} Stopwatch ;


void    stopwatch_initialize (Stopwatch *) ;
void    stopwatch_stop       (Stopwatch *) ;

Unsigned_32 stopwatch_elapsedMilliseconds (Stopwatch *) ;
Unsigned_32 stopwatch_elapsedSeconds      (Stopwatch *) ;


#endif
