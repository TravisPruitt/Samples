
#include "monitor.h"
#include <stdio.h>
#include <string.h>

int main (int argCount, char * argString [])
{
    if ((argCount == 2) &&
	((strcmp(argString[1], "--version") == 0) ||
	(strcmp(argString[1], "-v") == 0))) {
        printf("%s\n", VERSION);
	return 0;
    }

    monitor_run(argCount - 1, argString + 1);

    return 0 ;
}

