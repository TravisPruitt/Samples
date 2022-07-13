//*****************************************************************************
// test-server.h
//
// test-server Project Header file - Provides global definitions specific
//   to the xBR-test-server project.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

#ifndef __incl_test_server_h

	#define __incl_test_server_h

	#include "assert.h"
	#include "json/json.h"
	#include <stdint.h>
	#include <cstdlib>
	#include <cstdarg>
	
	#define READER_TYPE "xTS - xBand Test Server"
	#define UNUSED(a) (void)a


	#define KILO (1024)
	#define MEGA (1024 * (KILO))
	#define GIGA (1024 * (MEGA))
	
	#define PACK    __attribute__((__packed__))
	//#define PACK	

	#define NANOSECOND (1ull)
	#define MICROSECOND (1000ull * (NANOSECOND))
	#define MILLISECOND (1000ull * (MICROSECOND))
	#define SECOND (1000ull * (MILLISECOND) )
	#define MINUTE (60ull   * (SECOND)      )
	#define HOUR   (60ull   * (MINUTE)      )
	#define DAY    (24ull   * (HOUR)		)

	// HTTP communication logging
	#define POST " POST  -->      "
	#define DEL  " DEL   -->      "
	#define RES1 " 200   -->      "
	#define RES0 " 404   -->      "
	
	#define RCVD "      <--  %-4s "
	#define HTPF "      <--  %-4i "
	#define DATA "      <--       "
	
	#define TCPF "---TCP DOWN--- "

	namespace reader
	{
		extern bool streamSet;
	}

	namespace xbr_list
	{
		extern Json::Value xbrs; // an array
	}
	
	namespace user_input
	{
		void init();
		bool process();

	}

	namespace key 
	{	
		extern bool stop_please;
		extern void start(void);
		extern void stop(void);
	}
	
	extern std::string format (const char *fmt, ...);
	extern std::string vformat(const char *fmt, va_list ap);
	
#endif // __incl_test_server_h
