#include <stdio.h>
#include <stdarg.h>

#include "log.h"

static LogLevel log_level = DEFAULT_LOG_LEVEL;

bool logSetLogLevel(LogLevel level)
{
    if (level <= LOG_LEVEL_TRAFFIC)
        log_level = level;
    return true;
}

void logPrintf(LogLevel level, char const* filename, int linenumber, const char* format, ...)
{
    if (level >= log_level)
        return;

	printf("%d %s %d: ", level, filename, linenumber);
	va_list args;
	va_start(args, format);
	vprintf(format, args);
}
