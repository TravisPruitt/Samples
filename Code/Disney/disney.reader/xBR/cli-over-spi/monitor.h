#ifndef _MONITOR_H_
#define _MONITOR_H_


#include "types.h"


void monitor_run (int argc, char *argv[]);


// this cannot be called until monitor_run() has started
//
void monitor_printfLine (const char * formatSpecifier, ...);


#endif
