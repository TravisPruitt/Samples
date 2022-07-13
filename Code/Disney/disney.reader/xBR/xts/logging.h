//*****************************************************************************
// logging.h 
//
//	Generic logging macros.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

#ifndef __incl_logging_h

	#define __incl_logging_h

	#include <cerrno>
    #include "standard.h"

	#define INTERNAL

	#ifndef DEFINE_LOG
		#undef INTERNAL  // this is included as a normal header file in this instance.
		#define DEFINE_LOG(TYPE,TITLE,DEST)	 															\
			extern int  fd_##TYPE;																		\
			extern void TYPE(const char *format, ...); 													\
			extern void TYPE##_hex(const uint8_t *bin, size_t len);										\
			// intentionally blank
		#define LOG_SIZE(TYPE,SIZE)	// used in other contexts
		namespace logs
		{
	#endif // IN_EXTERNAL

	// USER MODIFIED SECTION FOLLOWS VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

	// DEFINE NEW LOG TYPES HERE:
	//--------------------------------
	// DEFINE_LOG( <logtype>, <log title>, <default destination file> );
	// Use: 	logs::<logtype>(<printf arguments>);
	//
	// Example: DEFINE_LOG(info, "INFO", stdout);
	// 			logs::info("Happy string %s\n", "string parameter");
	// results: prints "Happy string string parameter<cr>" to stdout.

	DEFINE_LOG(info,"INFO","NULL"); // always leave this ON
	DEFINE_LOG(func,"FUNC","NULL");
	DEFINE_LOG(evnt,"EVNT","NULL");
	DEFINE_LOG(http,"HTTP","stdout");
	DEFINE_LOG(curl,"CURL","NULL");
	DEFINE_LOG(page,"PAGE","NULL");
	DEFINE_LOG(pars,"PARS","NULL");
	DEFINE_LOG(conf,"CONF","NULL");	
	DEFINE_LOG(read,"READ","NULL");
	DEFINE_LOG(test,"SRVR","NULL");

	// USER MODIFIED SECTION ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

	#ifndef INTERNAL
	
		// call before doing any other logging
		extern void init(bool timestamps, bool tags);
	
		// if you need to warn/abort with a dynamic string, preceed it with logs::info().
		#define WARNING(TEXT)		{ logs::warning("%s@%i" TEXT "\n", __FILE__,__LINE__); }
		#define WARN_ERRNO(TEXT)	{ logs::warning("%s@%i: \"" TEXT "\"(%s)\n", __FILE__,__LINE__, strerror(errno)); }
		#define ABORT(TEXT)			{ logs::warning("%s@%i" TEXT "\n", __FILE__,__LINE__); exit(-1); }
		#define ABORT_ERRNO(TEXT)   { logs::aborting("%s@%i" TEXT " ERROR = %s\n", __FILE__, __LINE__, strerror(errno)); exit(-1); }
		extern void warning(const char *format, ...);
		extern void aborting(const char *format, ...);
		extern void prompt(const char *format, ...);	
			
			
		extern bool tags;			// set true to include the name of log in each event
		extern bool timestamps;		// set true to include a timestamp in each event
		
		//   manage_files()		Called periodically from process manager to check for deleted log files
		//						and to limit file size.
		extern void manage_files(void);		
				
		// limitFileSz()		Sets the maximum file size to allow a logging destination to grow to.
		extern void limitFileSz(FILE** file, int32_t size);

		// read_config()		Read the config file logging settings, and apply them.
		extern void read_config(void);
	} // namespace log
	#endif // IN_EXTERNAL

	#undef DEFINE_LOG
	#undef LOG_SIZE

#endif // __incl_logging_h
