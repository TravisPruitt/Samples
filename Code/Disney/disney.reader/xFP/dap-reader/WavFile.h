/**
    @file   WavFile.cpp
    @author Greg Strange
    @date   Nov, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __WAV_FILE_H
#define __WAV_FILE_H

#include <stdint.h>
#include <string>

namespace Reader
{

class WavFile
{
public:
    WavFile();
    ~WavFile();
    bool open(const char* path);
    void close();
    uint16_t getNumChannels() { return _channels; };
    uint32_t getSampleRate() { return _sampleRate; };
    uint16_t getBitsPerSample() { return _bitsPerSample; };
    int read(uint8_t* buffer, unsigned int length);
    void rewind();

private:
    int seekToChunk(const char* name);
    void logReadError(int bytesRead);

    std::string _filePath;
    int _fd;
    uint16_t _channels;
    uint32_t _sampleRate;
    uint16_t _bitsPerSample;
    uint32_t _dataSize;
    uint32_t _fullDataSize;
    off_t _dataPointer;
};



}




#endif
