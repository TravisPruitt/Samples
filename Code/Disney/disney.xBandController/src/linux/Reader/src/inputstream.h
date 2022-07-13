/*
 * inputstream.h
 *
 */

#ifndef INPUTSTREAM_H_
#define INPUTSTREAM_H_

#include <stdio.h>
#include "event.h"
#include <pthread.h>

enum InputType {
    InputTypeLegacyTap,
    InputTypeLegacyPing,
    InputTypeXBR,
    InputTypeXTP,
    InputTypeCar,
    InputTypeXtpGpio
};

enum InputState {
    InputStreamStateExit = 0,
    InputStreamStateResume = 1,
    InputStreamStatePause = 2,
    InputStreamStateDone = 3
};

struct InputStream
{
    char *pszName;

    pthread_t Thread;
    enum InputState state;
    int fdThread[2];

    char *pszInputFilename;
    int fd;
    FILE *fp;
    enum InputType InputType;
    struct timeval tvNow;
    long long start;
    long long end;

    int (*pfnCallback)(EVENTINFO *ei, void *pvUserData);
    void *pvCallbackData;
};

int
CreateInputStream(
    const char *pszName,
    int (*pfnCallback)(EVENTINFO *, void *pvCallbackData),
    void *pvCallbackData,
    const char *pszInputFilename,
    enum InputType InputType,
    long long start,
    long long end,
    struct InputStream **);

int
InputStreamSignalResume(
    struct InputStream *pInput,
    struct timeval *tvNow);

int
InputStreamSignalPause(
    struct InputStream *pInput);

int
FreeInputStream(
    struct InputStream *);

#endif /* INPUTSTREAM_H_ */
