
#include "time.h"

#include <sys/time.h>
#include <stdio.h>



Unsigned_32 stopwatch_elapsedMilliseconds (Stopwatch * sw)
{
    TimeSpec delta;

    if (! sw->running)
    {
        return sw->cumulativeMilliseconds ;
    }

    delta = time_since (sw->startTime);

    return delta.tv_sec  *    1000 +
           delta.tv_nsec / 1000000 ;
}



Unsigned_32 stopwatch_elapsedSeconds (Stopwatch * sw)
{
    return stopwatch_elapsedMilliseconds (sw) / 1000;
}



void stopwatch_stop (Stopwatch * sw)
{
    TimeSpec delta;

    if (! sw->running)
    {
        return;
    }

    delta = time_since (sw->startTime);

    sw->cumulativeMilliseconds +=
        delta.tv_sec  *    1000 +
        delta.tv_nsec / 1000000 ;

    sw->running = FALSE;
}



void stopwatch_initialize (Stopwatch * sw)
{
    sw->startTime = time_now ();

    sw->cumulativeMilliseconds = 0;
    sw->running = TRUE;
}



void time_delayMilliseconds (Unsigned_16 milliseconds)
{
    Unsigned_16 seconds;

    TimeSpec delayTime;
    TimeSpec remainingTime;

    seconds = milliseconds / 1000;
    milliseconds -= seconds * 1000;

    delayTime.tv_sec = seconds;
    delayTime.tv_nsec = milliseconds * 1000000;

    nanosleep (& delayTime, & remainingTime);
}


unsigned long time_now_ms (void)
{
    TimeSpec t = time_now();
    return (t.tv_sec * 1000) + (t.tv_nsec / 1000000);
}

TimeSpec time_now (void)
{
    // return the current time

    TimeSpec timeNow;

    clock_gettime (CLOCK_REALTIME, & timeNow);

    return timeNow;
}



TimeSpec time_fromTo (TimeSpec from, TimeSpec to)
{
    TimeSpec delta;

    delta.tv_sec = to.tv_sec - from.tv_sec;

    if (to.tv_nsec < from.tv_nsec)
    {
        -- delta.tv_sec;
        delta.tv_nsec = to.tv_nsec - from.tv_nsec + 1000000000;
    }
    else
    {
        delta.tv_nsec = to.tv_nsec - from.tv_nsec;
    }

    return delta;
}



TimeSpec time_since (TimeSpec start)
{
    return time_fromTo (start, time_now ());
}



Real time_asRealSeconds (TimeSpec time)
{
    return (Real) time.tv_sec + (time.tv_nsec / 1.0e9) ;
}



void time_toString (TimeSpec time, TimeString timeString)
{
    // return a string which represents the given time in the format
    //      hh:mm:ss.ddd         (hours:minutes:seconds.milliseconds)

    Unsigned_64 secondsSinceMidnight;
    Byte hours;
    Byte minutes;
    Byte seconds;
    Word milliseconds;

    #define SecondsPerDay   (60 * 60 * 24)

    secondsSinceMidnight = time.tv_sec % SecondsPerDay;

    seconds = (secondsSinceMidnight       ) % 60 ;
    minutes = (secondsSinceMidnight /   60) % 60 ;
    hours   = (secondsSinceMidnight / 3600)      ;

    milliseconds = time.tv_nsec / 1000000;

    sprintf (timeString, "%02d:%02d:%02d.%03d",
                         hours, minutes, seconds, milliseconds);
}



void time_toDateAndTimeString (TimeSpec time, DateAndTimeString dataAndTimeString)
{
    // return a string which represents the given time in the format
    //      yyyy/mm/dd-hh:mm:ss.ddd
    //      (year/month/day-hours:minutes:seconds.milliseconds)

    TimeString timeString;
    struct tm * dateAndTimePtr;

    time_toString (time, timeString);

    dateAndTimePtr = localtime ((time_t *) & time.tv_sec) ;

    sprintf (dataAndTimeString, "%04d/%02d/%02d-%s",
                dateAndTimePtr->tm_year + 1900,     // tm_year = years since 1900
                dateAndTimePtr->tm_mon + 1,         // tm_mon  = months since January [0,11]
                dateAndTimePtr->tm_mday,            // tm_mday = day of the month [1,31]
                timeString);
}



Unsigned_32 time_nanosecondResolution (void)
{
    // return the clock resolution in nanoseconds

    TimeSpec resolution;

    clock_getres (CLOCK_REALTIME, & resolution);

    return resolution.tv_nsec ;
}

