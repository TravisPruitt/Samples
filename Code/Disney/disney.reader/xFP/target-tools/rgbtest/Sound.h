/**
    @file   Sound.h
    @author Greg Strange
    @date   Oct, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __SOUND_H
#define __SOUND_H

#include "Thread.h"
#include "Event.h"
#include "WavFile.h"


namespace Reader
{

class Sound : public Thread
{
public:
    static Sound* instance();
    void play(const char* filePath);

private:    // data
    WavFile* _activeFile;
    WavFile* _pendingFile;
    Event _event;

private:  // method
    // singleton
    Sound();
    ~Sound();

    bool findSoundFile(const char* name, std::string& path);
    void run();
};



}




#endif
