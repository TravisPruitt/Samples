/**
    @file   ByteArray.h
    @author Greg Strange
    @date   Feb 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef BYTE_ARRAY_H
#define BYTE_ARRAY_H

#include "StringLib.h"
#include <string.h>
#include <vector>



namespace Reader
{

class ByteArray : public std::vector<uint8_t>
{
public:
    const uint8_t* data() const { return &(*this)[0]; }

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

    void rotateLeft()
    {
        if (size() < 2)
            return;

        uint8_t first = (*this)[0];
        for (unsigned i = 0; i < size()-1; ++i)
            (*this)[i] = (*this)[i+1];
        (*this)[size()-1] = first;
    }

    void rotateRight()
    {
        if (size() < 2)
            return;

        uint8_t last = (*this)[size()-1];
        for (unsigned i = size()-1; i > 0; --i)
            (*this)[i] = (*this)[i-1];
        (*this)[0] = last;
    }

    bool compare(const uint8_t* bytes, unsigned length)
    {
        if (length != size())
            return false;

        for (unsigned i = 0; i < length; ++i)
            if ((*this)[i] != bytes[i])
                return false;

        return true;
    }

    bool operator==(const ByteArray& other)
    {
        return compare(other.data(), other.size());
    }

    bool operator!=(const ByteArray& other)
    {
        return !(*this == other);
    }

    std::string toString()
    {
        return StringLib::formatBytes(&(*this)[0], size());
    }

};


}

#endif
