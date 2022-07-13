/**

    Copyright (c) 2011, synapse.com
*/


#ifndef __VERSION_H
#define __VERSION_H


// These values are passed to the compiler as command line defines on Linux
#ifdef _WIN32
#define VERSION "1.1.0"
#define GIT_COMMITS "0"
#define BUILD_TYPE "Windows build"
#define FULL_VERSION VERSION"-"GIT_COMMITS" - "__DATE__" "BUILD_TYPE
#endif

// Minimum xbrc version required for compatibility
#define MIN_XBRC_VERSION "0.0.0.0"



#endif
