/**
    @file   base64.h
    @author René Nyffenegger
    @date   Oct, 2011
*/

#ifndef __BASE64_H
#define __BASE64_H


#include <string>
#include <vector>
#include "standard.h"

void base64_encode(unsigned char const* , unsigned int len, std::string& result);
std::string base64_encode(unsigned char const* , unsigned int len);
void base64_encode(unsigned char const* input, unsigned int len, char* output);
std::vector<uint8_t> base64_decode(std::string const& s);


#endif
