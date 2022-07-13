


#include <stdio.h>
#include <time.h>
#include "common.h"


std::string getTimeAsString()
{
    static char buf[200];
#ifndef _WIN32
    struct timespec ts;
    struct tm tm;

    clock_gettime(CLOCK_REALTIME, &ts);
    localtime_r(&ts.tv_sec, &tm);
    int length = strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S", &tm);
    sprintf(buf+length, ".%03li", ts.tv_nsec / (1000 * 1000));
#else
    tzset();
    time_t now = time(NULL);
    struct tm tm = *gmtime(&now);
    int length = strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S.000", &tm);
#endif
    return buf;
}

