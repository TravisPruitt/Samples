/**
    @file   WavFile.cpp
    @author Greg Strange
    @date   Nov, 2011

    Copyright (c) 2011, synapse.com
*/



#include "standard.h"
#include "WavFile.h"
#include "log.h"

#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/types.h>
#include <string.h>
#include <errno.h>
#ifndef _WIN32
#include <unistd.h>
#endif


#ifdef _WIN32
#include <io.h>
#endif



#define NUM_CHANNELS_OFFSET     2
#define SAMPLE_RATE_OFFSET      4
#define BITS_PER_SAMPLE_OFFSET  14

using namespace Reader;


WavFile::WavFile() : _fd(-1), _channels(0), _sampleRate(0), _bitsPerSample(0)
{
}

WavFile::~WavFile()
{
    close();
}


 void WavFile::logReadError(int bytesRead)
{
    if (bytesRead < 0)
        LOG_ERROR("Error reading .WAV file %s: %d %s\n", _filePath.c_str(), errno, strerror(errno));
    else
        LOG_ERROR("unexpected end of file in .WAV file %s\n", _filePath.c_str());
}


bool WavFile::open(const char* path)
{
    _filePath = path;
    uint8_t buf[1024];

    _fd = ::open(path, O_RDONLY);
    if (_fd < 0)
    {
        LOG_WARN("unable to open .wav file: %s\n", path);
        return false;
    }

    int bytesRead = ::read(_fd, buf, 12);
    if (bytesRead < 12)
    {
        logReadError(bytesRead);
        close();
        return false;
    }

    if ( (memcmp(buf, "RIFF", 4) != 0) || (memcmp(buf+8, "WAVE", 4) != 0) )
    {
        LOG_WARN("Invalid .WAV file %s\n", path);
        close();
        return false;

    }

    int chunkSize = seekToChunk("fmt ");
    if (chunkSize == 0)
    {
        LOG_WARN("Cannot find 'fmt ' chunk in .WAV file %s\n", path);
        close();
        return false;
    }

    if (chunkSize > (int)sizeof(buf))
    {
        LOG_WARN("invalid fmt chunk size in .WAV file %s\n", path);
        close();
        return false;
    }

    bytesRead = ::read(_fd, buf, chunkSize);
    if (bytesRead < (int)chunkSize)
    {
        logReadError(bytesRead);
        close();
        return false;
    }

    _channels = buf[NUM_CHANNELS_OFFSET] + (buf[NUM_CHANNELS_OFFSET+1] << 8);
    _sampleRate = buf[SAMPLE_RATE_OFFSET] +
                   (buf[SAMPLE_RATE_OFFSET+1] << 8) +
                   (buf[SAMPLE_RATE_OFFSET+2] << 16) +
                   (buf[SAMPLE_RATE_OFFSET+3] << 24);
    _bitsPerSample = buf[BITS_PER_SAMPLE_OFFSET] + (buf[BITS_PER_SAMPLE_OFFSET+1] << 8);

    _fullDataSize = _dataSize = seekToChunk("data");
    if (_dataSize <= 0)
    {
        LOG_WARN("Unable to find data chunk in .WAV file %s\n", path);
        close();
        return false;
    }

    _dataPointer = lseek(_fd, 0, SEEK_CUR);

    return true;
}


void WavFile::close()
{
    if (_fd >= 0)
    {
        ::close(_fd);
        _fd = -1;
    }
}



int WavFile::seekToChunk(const char* name)
{
    uint8_t buf[5000];

    while (true)
    {
        int bytesRead = ::read(_fd, buf, 8);
        if (bytesRead < 8)
        {
            logReadError(bytesRead);
            close();
            return 0;
        }

//        bool isFmtChunk = (memcmp(buf, "fmt ", 4) == 0);
        uint32_t chunkSize = buf[4] + (buf[5] << 8) + (buf[6] << 16) + (buf[7] << 24);
        if (memcmp(buf, name, 4) == 0)
            return chunkSize;

        if (chunkSize >= sizeof(buf))
        {
            LOG_ERROR("Chunk size too large, %d", chunkSize);
            close();
            return 0;
        }

        bytesRead = ::read(_fd, buf, chunkSize);
        if (bytesRead < (int)chunkSize)
        {
            logReadError(bytesRead);
            close();
            return 0;
        }
    }
}



int WavFile::read(uint8_t* buffer, unsigned int length)
{
    if (_fd >= 0)
    {
        int desiredBytes = (_dataSize < length) ? _dataSize : length;
        if (desiredBytes <= 0)
            return 0;

        int bytesRead = ::read(_fd, buffer, desiredBytes);
        if (bytesRead < 0)
            logReadError(bytesRead);

        _dataSize -= bytesRead;
        return bytesRead;
    }
    else
        return 0;
}


void WavFile::rewind()
{
    if (_fd >= 0)
    {
        off_t p = lseek(_fd, _dataPointer, SEEK_SET);
        if (p != _dataPointer)
        {
            LOG_WARN("rewind fail");
            close();
        }
        else
        {
            _dataSize = _fullDataSize;
            LOG_DEBUG("rewound");
        }
    }
}
