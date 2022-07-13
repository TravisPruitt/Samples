/**
    log.h - File and console logging
    July 26, 2011
    Greg Strange

    Copyright (c) 2011, synapse.com
*/



#ifndef __LOG_H
#define __LOG_H


#include <stdio.h>
typedef enum { LOG_LEVEL_OFF = 0, LOG_LEVEL_ERROR, LOG_LEVEL_WARN, LOG_LEVEL_INFO, LOG_LEVEL_DEBUG } LogLevel;

#define DEFAULT_LOG_LEVEL       LOG_LEVEL_DEBUG
#define DEFAULT_CONSOLE_LEVEL   LOG_LEVEL_DEBUG

bool logInit(const char* fileName, const char* heading = NULL);
void logSetConsoleLevel(LogLevel level);
bool logSetLogLevel(LogLevel level);
void logPrintf(LogLevel level, char const* filename, int linenumber, const char* format, ...);
void logPuts(LogLevel level, const char* text);
void logClose();


#ifdef WIN32

#ifdef LOG_OFF

    #define LOG_DEBUG(format, ...)
    #define LOG_INFO(format, ...)
    #define LOG_WARN(format, ...)
    #define LOG_ERROR(format, ...)

#else

    #define LOG_DEBUG(format, ...)   logPrintf(LOG_LEVEL_DEBUG, __FILE__, __LINE__, format, __VA_ARGS__)
    #define LOG_INFO(format, ...)    logPrintf(LOG_LEVEL_INFO, __FILE__, __LINE__, format, __VA_ARGS__)
    #define LOG_WARN(format, ...)    logPrintf(LOG_LEVEL_WARN, __FILE__, __LINE__, format, __VA_ARGS__)
    #define LOG_ERROR(format, ...)   logPrintf(LOG_LEVEL_ERROR, __FILE__, __LINE__, format, __VA_ARGS__)

#endif

#else

#ifdef LOG_OFF
    #define LOG_DEBUG(format...)
    #define LOG_INFO(format...)
    #define LOG_WARN(format...)
    #define LOG_ERROR(format...)
#else
    #define LOG_DEBUG(format...)   logPrintf(LOG_LEVEL_DEBUG, __FILE__, __LINE__, format)
    #define LOG_INFO(format...)    logPrintf(LOG_LEVEL_INFO, __FILE__, __LINE__, format)
    #define LOG_WARN(format...)    logPrintf(LOG_LEVEL_WARN, __FILE__, __LINE__, format)
    #define LOG_ERROR(format...)   logPrintf(LOG_LEVEL_ERROR, __FILE__, __LINE__, format)
#endif

#endif


#endif
