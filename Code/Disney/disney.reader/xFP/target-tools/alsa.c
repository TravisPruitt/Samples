/**
    .WAV file should have the following properties:

    44100 Hz, 2 ch, s16le, 1411.2 kbit/100.00% (ratio: 176400->176400)


    From sample code on:
        http://linuxgazette.net/181/jangir.html
*/
#include <alsa/asoundlib.h>


int main(int argc, char** argv)
{
    int fd, ret;
    snd_pcm_t *handle;
    snd_pcm_sframes_t frames;
    static const char *device = "default"; /* playback device */
    unsigned char buf[2*1024];


    if (argc < 2)
    {
        printf("need .wav file name\n");
        exit(1);
    }

    /* open audio file */
    fd = open(argv[1], O_RDONLY);
    if (fd <= 0)
    {
        printf("unable to open file '%s'\n", argv[1]);
        exit(1);
    }

    /* open playback device */
    int result = snd_pcm_open(&handle, device, SND_PCM_STREAM_PLAYBACK, 0);
    if (result < 0)
    {
        printf("unable to open sound device\n");
        printf("ERROR: %s\n", snd_strerror(result));
        close(fd);
        exit(1);
    }

    /* configure playback device as per input audio file */

#if 0
    snd_pcm_set_params(handle,
        	SND_PCM_FORMAT_S16_LE,
        	SND_PCM_ACCESS_RW_INTERLEAVED,
        	1 /* channels */,
        	44100 /* sample rate */,
        	1,
        	500000/* 0.5 sec */);
#else
    snd_pcm_set_params(handle,
        	SND_PCM_FORMAT_S16_LE,
        	SND_PCM_ACCESS_RW_INTERLEAVED,
        	1 /* channels */,
        	48000 /* sample rate */,
        	1,
        	5000/* 0.5 sec */);
#endif

    /* play audio file */
    read(fd, buf, 44);
    while((ret = read(fd, buf, sizeof(buf))) > 0) 
    {
        snd_pcm_sframes_t total_frames = snd_pcm_bytes_to_frames(handle, ret);
        frames = snd_pcm_writei(handle, buf, total_frames);
    }

    snd_pcm_close(handle);

    close(fd);
    return 0;
}

