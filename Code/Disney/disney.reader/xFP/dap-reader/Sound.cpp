/**
    @file   FeigReder.cpp
    @date   Sept 2011

    .WAV file should have the following properties:

    44100 Hz, 2 ch, s16le, 1411.2 kbit/100.00% (ratio: 176400->176400)


    From sample code on:
        http://linuxgazette.net/181/jangir.html
*/


#include "Sound.h"
#include "log.h"
#include "ticks.h"
#include "DapConfig.h"
#include "DapReader.h"

#ifndef _WIN32
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <alsa/asoundlib.h>
#endif



using namespace Reader;


#ifndef _WIN32
Sound::Sound() : _activeFile(NULL), _pendingFile(NULL)
{
    // Set up volumes
    // These volumes have been chosen from trial and error as the maximum volume
    // we can achieve without clipping.
    system("amixer sset \"DAC2 Analog\" 2dB unmute");
    system("amixer sset \"DAC2 Digital Coarse\" 0dB unmute");
    system("amixer sset \"DAC2 Digital Fine\" 0dB unmute");
    system("amixer sset \"Headset\" 6dB unmute");

    // Check if we should mute the sound
    // We mute the DAC2 Analog amplifier because the Headset amplifier does not
    // have a mute control.
    if (DapConfig::instance()->getValue("mute", false))
    {
        LOG_DEBUG("Muting sounds\n");
        system("amixer sset \"DAC2 Analog\" 0dB mute");
    }
}


Sound::~Sound()
{
    // restore sound if it was muted
    system("amixer sset \"DAC2 Analog\" 2dB unmute");
}


Sound* Sound::instance()
{
    static Sound _instance;
    return &_instance;
}


/**
    Play a sound.

    Opens the sound file with the handle in "_pendingFile"
    _activeFile is the sound file currently being played, if any.

    This allows us to queue up a second sound while a first sound is
    playing.
*/
void Sound::play(const char* filePath)
{
    LOG_DEBUG("play %s\n", filePath);

    /* open audio file */
    if (_pendingFile == NULL)
    {
        _pendingFile = new WavFile();
        if (!_pendingFile->open(filePath))
        {
            delete _pendingFile;
            _pendingFile = NULL;
            DapReader::instance()->setStatus(IStatus::Yellow, "Corrupted sound file");
        }

        _event.signal();
    }
}


static void showAlsaResult(const char* text, int result)
{
    if (result < 0)
        LOG_WARN("Sound: %s returned %d - %s\n", text, result, snd_strerror(result));
}


void Sound::run()
{
    bool firstTime = true;
    snd_pcm_t *handle;
    static const char *device = "default"; /* playback device */
    unsigned char buf[2*1024];
    int result;

    /* open playback device */
    result = snd_pcm_open(&handle, device, SND_PCM_STREAM_PLAYBACK, 0);
    if (result < 0)
    {
        LOG_ERROR("Error opening sound device: %d - %s\n", result, snd_strerror(result));
        DapReader::instance()->setStatus(IStatus::Red, "Unable to open sound device");
        return;
    }

    /* play audio file */
    while (!_quit)
    {
        while (!_quit && _pendingFile)
        {
            LOG_DEBUG("sound run loop got file to play\n");
            _activeFile = _pendingFile;
            _pendingFile = NULL;

            if (!firstTime)
            {
                result = snd_pcm_drop(handle);
                showAlsaResult("snd_pcm_drop", result);
            }
            firstTime = false;

            unsigned int channels = _activeFile->getNumChannels();
            unsigned int rate = _activeFile->getSampleRate();
            unsigned int sampleSize = _activeFile->getBitsPerSample();
            snd_pcm_format_t format = (sampleSize == 16) ? SND_PCM_FORMAT_S16_LE : SND_PCM_FORMAT_S24_3LE;
            LOG_DEBUG("channels %d, sample rate %d, bits/sample %d\n", channels, rate, sampleSize);

            /* configure playback device as per input audio file */
            result = snd_pcm_set_params(handle,
                    format,
                    SND_PCM_ACCESS_RW_INTERLEAVED,
                    channels /* channels */,
                    rate /* sample rate */,
                    1,
                    0);
            showAlsaResult("snd_pcm_set_params", result);

            result = snd_pcm_prepare(handle);
            showAlsaResult("snd_pcm_prepare", result);

            int ret = 0;
            while ( (_pendingFile == NULL) && ((ret = _activeFile->read(buf, sizeof(buf))) > 0) )
            {
                snd_pcm_sframes_t total_frames = snd_pcm_bytes_to_frames(handle, ret);
                showAlsaResult("snd_pcm_bytes_to_frames", total_frames);
                if (total_frames > 0)
                {
                    snd_pcm_sframes_t frames = snd_pcm_writei(handle, buf, total_frames);
                    if (frames < 0)
                    {
                        frames = snd_pcm_recover(handle, frames, 1);
                        showAlsaResult("snd_pcm_writei", frames);
                    }
                }
            }
            if (ret < 0)
                DapReader::instance()->setStatus(IStatus::Yellow, "Error reading sound file");

            delete _activeFile;
            _activeFile = NULL;
        }

        _event.wait(5000);
    }

    result = snd_pcm_close(handle);
    showAlsaResult("snd_pcm_close", result);
}


#else

Sound::Sound() : _activeFile(NULL), _pendingFile(NULL)
{
}

Sound::~Sound()
{
}

Sound* Sound::instance()
{
    static Sound _instance;
    return &_instance;
}


void Sound::play(const char* filePath)
{
}


void Sound::run()
{
}

#endif
