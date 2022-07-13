/**
    @file   ByteArray.h
    @author Greg Strange
    @date   Feb 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef BYTE_ARRAY_H
#define BYTE_ARRAY_H

#include <string.h>
#include <vector>



namespace Reader
{

class ByteArray : public std::vector<uint8_t>
{
public:
    const uint8_t* data() { return &(*this)[0]; }

    void append(uint8_t value) 
    { 
        push_back(value); 
    }

    void append(const uint8_t* bytes, unsigned int count)
    {
        if (count)
        {
            unsigned pos = size();
            resize(size() + count);
            memcpy(&(*this)[pos], bytes, count);
        }
    }

    void append(std::vector<uint8_t> bytes)
    {
        append(&bytes[0], bytes.size());
    }


    void copy(uint8_t* dest)
    {
        memcpy(dest, data(), size());
    }

};


}

#endif
