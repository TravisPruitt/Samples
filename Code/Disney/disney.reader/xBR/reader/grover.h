//*****************************************************************************
// grover.h
//
// Grover Project Header file - Provides global definitions specific
//   to the grover project.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

#ifndef __incl_grover_h

#define __incl_grover_h

#include "assert.h"
#include "json/json.h"
#include "standard.h"
#include <cstdlib>

#define READER_TYPE "Long Range"
typedef uint64_t BandId;

#define KILO (1024)
#define MEGA (1024 * (KILO))
#define GIGA (1024 * (MEGA))

#define PACK    __attribute__((__packed__))
//#define PACK	


extern void grover_restart();
extern bool grover_quitting();


#endif // __incl_grover_h

