/**
    @file   StringLib.h
    @author Greg Strange
    @date   April 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef STRING_LIB_H
#define STRING_LIB_H


#include "standard.h"
#include <string>



namespace StringLib
{
    bool ishex(char);
    std::string formatBytes(const uint8_t* bytes, unsigned length, const char* pad = " ");
    bool hex2bandId(const char* str, uint64_t* result);
    int hex2bin(const char* str, uint8_t* buf, size_t limit);
}


#endif




