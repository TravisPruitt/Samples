#include "event.h"

void updateTime(
    unsigned long long offset,
    struct timeval *tvIn,
    struct timeval *tvOut)
{
    struct timeval tv;

    tv.tv_sec = offset / 1000;
    offset %= 1000;
    tv.tv_usec = offset * 1000;

    if (tvIn)
    {
        timeradd(tvIn, &tv, &tv);
    }
    *tvOut = tv;
}
