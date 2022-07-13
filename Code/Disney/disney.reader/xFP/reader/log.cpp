/**
    log.c - File and console logging functions
    July 26, 2011
    Greg Strange

    Copyright (c) 2011, synapse.com

    Logging functions.
*/


#include "standard.h"
#include "log.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <time.h>
#include <sys/stat.h>


#define MAX_FILE_SIZE  (1024*1024)


static const char* logFileName = NULL;
static FILE* logFile = NULL;
static char* logHeading = NULL;
static void (*newLogCallback)() = NULL;


static LogLevel consoleLevel;
static LogLevel logLevel;




/**
    Try to open the log file

    @return true on success, false otherwise
*/
static bool openLogFile()
{
    if (!logFileName)
        return false;

    logFile = fopen(logFileName, "a");
    if (logFile)
    {
        time_t aclock;
        time(&aclock);   // Get time in seconds

        struct tm *logtime;
        logtime = localtime(&aclock);   // Convert time to struct tm form

        fprintf(logFile, "\n\nLog file opened at %s", asctime(logtime));
        if (logHeading)
            fputs(logHeading, logFile);

        if (newLogCallback)
            newLogCallback();
    }
    return (logFile != NULL);
}


/**
    Initialize log - must be called first.

    @param  fileName        log file name

    @return true on success, false if unable to open log file

    Log file is overwritten each time.
*/
bool logInit(const char* fileName, const char* heading)
{
    logFileName = strdup(fileName);
    if (heading)
        logHeading = strdup(heading);

    logLevel = DEFAULT_LOG_LEVEL;
    consoleLevel = DEFAULT_CONSOLE_LEVEL;

    if (logLevel > LOG_LEVEL_OFF)
    {
        return openLogFile();
    }
    else
    {
        logFile = NULL;
        return true;
    }
}



/**
    Change the header printed at the top of each new log file.
*/
void logSetHeader(const char* header)
{
    if (logHeading)
    {
        free(logHeading);
        logHeading = NULL;
    }
    if (header)
        logHeading = strdup(header);
}


/**
    Set a function to be called every time we create a new log file.
    This allows the caller to log information of thier choosing at the
    top of each new log file.
*/
void logSetNewLogCallback(void (*callback)())
{
    newLogCallback = callback;
}



/**
    Check if the file size has grown past the max size.  If it has, then we move this
    file to a file with the same name, but a ".old" extension and then open a new file.
    If a ".old" file already existed, it is deleted.  This way the amount of disk space
    for log files should not exceed MAX_FILE_SIZE * 2.
*/
static void checkFileSize()
{
    char oldFileName[1000];
    struct stat st;

    if (ftell(logFile) <= MAX_FILE_SIZE)
        return;

    if (logFile)
    {
        fclose(logFile);
        logFile = NULL;
    }

    strcpy(oldFileName, logFileName);
    strcat(oldFileName, ".old");

    if (stat(oldFileName, &st) == 0)
    {
        remove(oldFileName);
    }

    rename(logFileName, oldFileName);

    openLogFile();
}


/**
    Set the logging level for the console.

    @param  level = new console log level
*/
void logSetConsoleLevel(LogLevel level)
{
    consoleLevel = level;
}


/**
    Set the logging level for the log file

    @param  level = new log file log level

    @return true on success, false otherwise (unable to open log file)
*/
bool logSetLogLevel(LogLevel level)
{
    logLevel = level;
    if (logLevel > LOG_LEVEL_OFF && logFile == NULL)
    {
        return openLogFile();
    }
    else
    {
        return true;
    }
}



/**
    Return true if the given level is being logged to either the log file or console
*/
bool logIsLevelVisible(LogLevel level)
{
    return (logLevel >= level || consoleLevel >= level);
}



/**
    Clean up - should be called when program exits
*/
void logClose(void)
{
    if (logFile)
    {
        fclose(logFile);
        logFile = NULL;
    }

    if (logFileName)
    {
        free((void*)logFileName);
        logFileName = NULL;
    }

    if (logHeading)
    {
        free((void*)logHeading);
        logHeading = NULL;
    }
}



static void getTimeOld(char* buf, size_t bufSize)
{
    time_t aclock;
    time(&aclock);   // Get time in seconds

    struct tm *t;
    t = localtime(&aclock);   // Convert time to struct tm form

    snprintf(buf, bufSize, "%d:%02d:%02d", t->tm_hour, t->tm_min, t->tm_sec);
}


static void getTime(char* buf, size_t bufSize)
{
#ifndef WIN32
    struct timespec ts;
    struct tm tm;

    clock_gettime(CLOCK_REALTIME, &ts);
    localtime_r(&ts.tv_sec, &tm);
    int length = strftime(buf, bufSize, "%H:%M:%S", &tm);
    sprintf(buf+length, ".%03li", ts.tv_nsec / (1000 * 1000));
#else
    tzset();
    time_t now = time(NULL);
    struct tm tm = *gmtime(&now);
    strftime(buf, bufSize, "%H:%M:%S", &tm);
#endif
}


/**
    Log message

    The message will be displayed to the console and/or output to the log file
    depending on the defined levels for both of those outputs in log.h.
*/
void logPrintf(LogLevel level, char const* filename, int linenumber, const char* format, ...)
{
    char buf[10240];

    // A sanity check to protect against buffer overflows, but of course
    // we don't know how big the args are, so its still a crap shoot.
    if (strlen(format) > sizeof(buf)-50)    return;

    if (level <= logLevel || level <= consoleLevel || level == LOG_LEVEL_ERROR || level == LOG_LEVEL_WARN)
    {
        va_list args;
        va_start(args, format);

//        vsnprintf(buf, sizeof(buf), format, args);
        int length = vsprintf(buf, format, args);

        // Add a new line if there isn't one already
        if (length > 0 && buf[length-1] != '\n')
        {
            buf[length] = '\n';
            buf[length+1] = 0;
        }

        if (level <= consoleLevel)
        {
#if defined(WIN32) && defined(_DEBUG) && !defined(_CONSOLE)
            OutputDebugString(buf);
#else
            printf( "%s", buf);
#endif
        }

        // Send the message to the log file
        if (level <= logLevel && logFile != NULL)
        {
            char timeBuf[100];
            getTime(timeBuf, sizeof(timeBuf));
            fprintf(logFile, "%s[%s][%s %d] %s", 
                level == LOG_LEVEL_ERROR ? "**" : level == LOG_LEVEL_WARN ? "*" : "",
                timeBuf, filename, linenumber, 
                buf);
            fflush(logFile);
            checkFileSize();
        }
    }
}


/**
    Output some text.

    Use this for long strings instead of logPrintf() and the log macros and you
    don't have to worry about buffer overflow.
*/
void logPuts(LogLevel level, const char* text)
{
    if (level <= consoleLevel)
    {
#if defined(WIN32) && defined(_DEBUG) && !defined(_CONSOLE)
        OutputDebugString(text);
#else
        printf( "%s", text);
#endif
    }

    if (level <= logLevel && logFile != NULL)
    {
        fputs(text, logFile);
        fflush(logFile);
        checkFileSize();
    }
}

