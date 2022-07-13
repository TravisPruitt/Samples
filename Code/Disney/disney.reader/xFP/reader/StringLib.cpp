/**
    @file   StringLib.cpp
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#include "StringLib.h"
#include <string.h>



static bool ishex(char c)
{
    return ( (c >= '0' && c <= '9') ||
             (c >= 'a' && c <= 'f') ||
             (c >= 'A' && c <= 'F') );
}

static char formatNibble(uint8_t n)
{
    if (n <= 9)
        return n + '0';
    else
        return n - 10 + 'A';
}


std::string StringLib::formatBytes(const uint8_t* bytes, unsigned length, const char* pad)
{
    std::string s;
    for (unsigned i = 0; i < length; ++i)
    {
        s += formatNibble((bytes[i] >> 4) & 0x0f);
        s += formatNibble(bytes[i] & 0x0f);
        s += pad;
    }

    return s;
}


int convertNibble(char c)
{
    const char hexchars[] = "0123456789ABCDEF";
    const char *p = strchr(hexchars, toupper(c));
    if (p == NULL)
        return -1;
    else
        return (p - hexchars);
}

bool StringLib::hex2bandId(const char* str, uint64_t* result)
{
    uint64_t value = 0;

    for (unsigned i = 0; i < 10; ++i)
    {
        if (str[i] == '\0')
            return false;

        int nibble = convertNibble(str[i]);
        if (nibble < 0)
            return false;

        value = (value << 4) + nibble;
    }
    *result = value;
    return true;
}


/**
    @param  str       String to parse
    @param  buf       Where to put the results
    @param  limit     Max number of characters to parse from the input string
                      (of course buffer must be large enough to hold bytes if max
                       number of characters are parsed)

    @return Number of characters parsed from the input string
*/
int StringLib::hex2bin(const char* str, uint8_t* buf, size_t limit)
{
    if (str == NULL)
        return 0;

    size_t in = 0;
    size_t out = 0;
    while (ishex(str[in]) && in < limit)
    {
        int hi = convertNibble(str[in++]);
        int lo = ishex(str[in]) ? convertNibble(str[in++]) : 0;
        buf[out++] = (hi << 4) + lo;
    }

    return in;
}
