//*****************************************************************************
// logging.c 
//
//	Generic logging functions.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include "assert.h"
#include "json/json.h"
#include <stdint.h>
#include <cstdlib>
#include <cstdarg>
#include <cstdio>
#include <cstring>
// System Lib
#include <sys/stat.h>
#include <fcntl.h>
#ifdef _WIN32
#include <io.h>
#endif

#define KILO (1024)
#define MEGA (1024 * (KILO))
#define GIGA (1024 * (MEGA))

// Global-App
#include "test-server.h"
#include "config.h"
#include "json/json.h"
#include "Mutex.h"
#include "common.h"
// Local-ish


// logging.h is included in this file multiple times. Because of macro definitions, it's OK.
#include "logging.h"



#ifdef _WIN32
#define STDOUT_FILENO _fileno(stdout)
#define STDERR_FILENO _fileno(stderr)
#endif


namespace
{
	typedef enum {UNTERMED, TERMED, FORCE} TERMINATION;
	TERMINATION termd = FORCE; // TERMED if previous line was terminated
    Mutex _lock;
}


// buffer fills to capacity and then erases members as it is adding them; never fills up.

#define CIRCULAR_BUFFER_FD -27 // random #; identifies logs going to the circular buffer

namespace circBuf
{
	const uint32_t entries = 1024;
	const uint32_t entrySz = 128;
	char ring[entries * entrySz] = {"\0"};	// 128K
	
	uint32_t head = 0;
	uint32_t tail = 0;
	
	#define next(a) a = ((a + 1) % (entries))

    Mutex _mutex;

    inline void lock()		{	_mutex.lock(); }
    inline void unlock()	{	_mutex.unlock(); }

	inline uint32_t filled_ct()	{	return (head == tail)? 0			: (head > tail)? (head-tail)				: entries-(tail-head); }
	inline uint32_t open_ct()	{	return (head == tail)? entries-1	: (head > tail)? entries - (head-tail) -1	: (tail-head) - 1;	   }

	// allocates head & copies data to it. This will push the tail around if needed.
	// returns entry index, so that buffer can be used thereafter.
	// note that use of entry must end before get() attempts to read it.
	uint32_t add(char **entry)
	{
		lock();
		*entry = &ring[head*entrySz];

		next(head);
		if(head == tail) 	// push tail
			next(tail);
		
		int32_t ct = open_ct();

		// printf("add => head = %i, tail = %i ", head, tail);

		unlock();
		return(ct);	// number of entries not filled
	}

	uint32_t get(char **entry)	// get a block of data & de-allocate it.
	{
		lock();
		if(tail != head) 
		{
			*entry = &ring[tail*entrySz];
			next(tail);
		}
		int32_t ct = filled_ct();
		// printf("get => head = %i, tail = %i ", head, tail);

		unlock();
		return ct; // number of entries filled
	}

	char *valog(const char *tagg, const char *format, va_list args)
	{
		char *buffer;
		add(&buffer);
		uint32_t len = strlen(tagg);
		strcpy(buffer, tagg); strcpy(buffer + len, ": "); len += 2;	// prepend tagg.
		len += vsnprintf(buffer + len, (entrySz -1) - len, format, args);
		return buffer+ len; 
	}
	char *log(const char *t,    const char *f, ...) { va_list a; va_start(a,f); return valog(t,f,a); }

	void inline phex(const char *tagg, const uint8_t *bin, size_t len)
	{
		// "00: 00 00 00 00|00 00 00 00|00 00 00 00|00 00 00 00"
		char * 		pout;
		circBuf::add(&pout);
		pout = circBuf::log(tagg, ""); // buffer now has tagg prepended. pout is at end.
		size_t 		offset = 0;
		const char	hxch[] = "0123456789ABCDEF";
		uint8_t *	pin = (uint8_t *) bin;

		for(; offset < len;offset++)
		{
			if(offset % 16 == 0)
			{
				*(pout++) = hxch[ (offset >> 4) & 0x0F ];
				*(pout++) = '0';
				*(pout++) = ':';
				*(pout++) = ' ';
			}

			*(pout++) = hxch[ (*pin >> 4) & 0x0F ];
			*(pout++) = hxch[ *(pin++)    & 0x0F ];
			
			if(offset % 4 == 3)
			{
				if(offset % 16 == 15)
						*(pout++) = '\n';
				else	*(pout++) = '|';				
			}
			else
				*(pout++) = ' ';				
		}
		
		if(offset % 16)	// we did not terminate it yet
			*(pout++) = '\n';
	}



//#ifdef QA

	bool qa_test_circbuf( void )
	{
		char *entry = NULL;
		uint32_t open = entries;
		uint32_t full = 0;

		uint32_t open_exp = entries - 1;
		uint32_t full_exp = 0;

		// run 100 tests, where we fill up 3/4 of the buffer, then read 1/3. 
		// keep track of the open & full values and check them for sanity.
		for(int test = 0; test < 100; test++)
		{
			uint32_t x;

			// printf("open = %i, full = %i\n", open_ct(), filled_ct());
			// printf("adding %i entries\n", 3*(entries/4));
			for(x = 0; x < 3*(entries/4); x++)
			{
				// printf("adding with %i entries left.\n", open);
				open = add(&entry);
				sprintf(entry,"added. now %i entries left.\n", open);
				
				// test the current state		
				if(open_exp) { open_exp = (open_exp - 1); }; // count down to 0 and then stay there.
				if(full_exp < entries-1) { full_exp = (full_exp + 1); }; // count down to 0 and then stay there.
				
				if(open == 0)
				{
					// printf("Lost a message due to circBuf overflow\n");
				}
				
				if(open != open_exp)
				{
					printf("TEST_FAILED. open = %i, expected %i.\n", open, open_exp);
					return false;
				}				
			}

			// printf("open = %i, full = %i\n", open_ct(), filled_ct());
			// printf("getting %i entries\n", entries/3);
			for(x = 0; x < entries/3; x++)
			{
				full = get(&entry);
				// printf("getting. Contents:\"%s\"\n", entry);

				// test the current state		
				if(open_exp < entries-1) { open_exp = (open_exp + 1); }; // count down to 0 and then stay there.

				if(full_exp) { full_exp = (full_exp - 1); }; // count down to 0 and then stay there.				

				if(full != full_exp)
				{
					printf("TEST_FAILED. full = %i, expected %i.\n", full, full_exp);
					return false;
				}
			}
		}

		// printf("open = %i, full = %i\n", open_ct(), filled_ct());

		// printf("logging %i entries\n", 136);
		for(int test = 0; test < 136; test++)
		{
			// printf("1234: added %i: open = %i, full = %i\n", test, open_ct(), filled_ct());

			char *p = log("1234", "added %i: open = %i, full = %i", test, open_ct(), filled_ct());
			strcpy(p," TEST STRING");

			// test the current state
			if(open_exp) { open_exp = (open_exp - 1); }; // count down to 0 and then stay there.
			if(full_exp < entries-1) { full_exp = (full_exp + 1); }; // count down to 0 and then stay there.

			if(open_ct() != open_exp)
			{
				printf("TEST_FAILED. open = %i, expected %i.\n", open, open_exp);
				return false;
			}
		}

		// printf("open = %i, full = %i\n", open_ct(), filled_ct());
		// printf("outputting %i entries (head = %i, tail = %i)\n", 140, head, tail);
		for(int test = 0; test < 140; test++)
		{
			char *p; full = get(&p);
			// printf("TEST %i: (@%p)\"%s\"\n",test, p, p);

			// test the current state
			if(open_exp < entries-1) { open_exp = (open_exp + 1); }; // count down to 0 and then stay there.

			if(full_exp) { full_exp = (full_exp - 1); }; // count down to 0 and then stay there.

			if(	 full != full_exp  )
			{
				printf("TEST_FAILED. full = %i, expected %i.\n", full, full_exp);
				return false;
			}
		}
		return true;
	} // qa_test_circbuf

// #endif

	uint32_t dump(int destination)
	{
        Lock lock(_lock);
		char *p;
		uint32_t ct = 0;
		while (get(&p))
		{
			write(destination, p, strlen(p));
#ifndef _WIN32
			fsync(destination);
#endif
			ct++;
		}
		
		return ct;
	} // dump()

} //namespace circBuf

namespace
{

	inline void tag(const char *tagg, int destination) 
	{
		if(logs::tags)
			write(destination, tagg, strlen(tagg));
		if(!logs::timestamps)
			write(destination, ": ", 2);
	} //tag()

	inline void tstamp(int destination) 
	{
		if(logs::timestamps)
		{
            std::string timeString = getTimeAsString();
			write(destination, timeString.c_str(), timeString.size());
		}
	} //tstamp()
	
	void inline plog(int destination, const char *tagg, const char *format, va_list args) 
	{
		if(destination == -1) return;
		
        Lock lock(_lock);

		if(destination == CIRCULAR_BUFFER_FD) 	
			circBuf::log(tagg, format, args);// circular buffer logging
		else
		{
            char outstr[10000];
			switch(termd)
			{
				case UNTERMED:	break;
				case FORCE:		write(destination, "\n", 1); termd = TERMED;
				case TERMED:	tag(tagg, destination);	tstamp(destination); break;
			}
			vsprintf(outstr, format, args);
			unsigned tloc = strlen(outstr);
			if(tloc) termd = (outstr[tloc-1] == '\n')?TERMED:UNTERMED;
			write(destination, outstr, tloc);
#ifndef _WIN32
			fsync(destination);
#endif
		}
	} // plog()

	void inline phex(int destination, const char *tagg, const uint8_t *bin, size_t len)
	{
		if(destination == -1) return;
		
		if(destination == CIRCULAR_BUFFER_FD) 
		{
			circBuf::phex(tagg, bin, len);
			return;
		}
		
        Lock lock(_lock);
		// "00: 00 00 00 00|00 00 00 00|00 00 00 00|00 00 00 00"
		char 		out[64];
		char * 		pout = out;
		size_t 		offset = 0;
		const char	hxch[] = "0123456789ABCDEF";
		uint8_t *	pin = (uint8_t *) bin;

		for(; offset < len;offset++)
		{
			if(offset % 16 == 0)
			{
				switch(termd)
				{
					case FORCE:		write(destination, "\n", 1);	break;
					case UNTERMED:	break;
					case TERMED:	
						tag(tagg, destination);	
						tstamp(destination); 
						break;
				}		
				*(pout++) = hxch[ (offset >> 4) & 0x0F ];
				*(pout++) = '0';
				*(pout++) = ':';
				*(pout++) = ' ';
			}

			*(pout++) = hxch[ (*pin >> 4) & 0x0F ];
			*(pout++) = hxch[ *(pin++)    & 0x0F ];
				
			if(offset % 4 == 3)
			{
				if(offset % 16 == 15)
				{	
					*(pout++) = '\n';
					write(destination, out, pout - out);
#ifndef _WIN32
					fsync(destination);
#endif
					pout = out; // start buffer over
					termd = TERMED;
				}
				else
					*(pout++) = '|';				
			}
			else
				*(pout++) = ' ';				
		}
			
		if(offset % 16)	// we did not terminate it yet
			*(pout++) = '\n';
		write(destination, out, pout - out);
#ifndef _WIN32
		fsync(destination);
#endif
		termd = TERMED;
	} // phex()
} // namespace <> 

namespace logs
{
	typedef struct tag_LOGNAME
	{
		int		   	   *fd;			// location of file descriptor
		const char	   *title;		// tag; also key for config file.
		std::string 	filename;	// stderr/stdout/NULL/<filename>
		uint64_t		fileMaxSz;	// maximum file size, limited to this max.
	} LOGNAME;
	
	void setDestination(LOGNAME *logName);
	void addLogType(const char *title, int* fd, const char *filename);
	void setLogSz(const char *filename, uint64_t maxSz);
	
// careful here: logging.h is used in an alternate context here. DEFINE_LOG(TYPE,TITLE,DEST)
// is used in a different way to produce the implementation of the logging types.
// -------------------------------------------
#define IN_LOGGING_CPP_DEFINING
#undef __incl_logging_h

	#define DEFINE_LOG(TYPE,TITLE,DEST)	 															\
		int		fd_##TYPE; 																	\
		void 	TYPE(const char *f, ...) { va_list a; va_start(a,f); plog(fd_##TYPE,TITLE": ",f,a); }	\
		void 	TYPE##_hex(const uint8_t *bin, size_t len) { phex(fd_##TYPE, TITLE ": ", bin, len); }	\
		// intentionally blank
	#define LOG_SIZE(TYPE,SIZE)	// used in other contexts
	#include "logging.h"
	#undef DEFINE_LOG
	#undef LOG_SIZE
	void warning(const char *f, ...) { va_list a; va_start(a,f); plog(STDERR_FILENO,"WARNING: ",f,a); }
	void aborting(const char *f, ...) { va_list a; va_start(a,f); plog(STDERR_FILENO,"ABORTING: ",f,a); }

// DEFINE_LOG(TYPE,TITLE,DEST) in logging.h is used here to produce a 
// list of the types with the destination.
// -------------------------------------------
#define IN_LOGGING_CPP_DEFINING
#undef __incl_logging_h

	void buildLogtypeTable()
	{
		//	warning("buildLogtypeTable()\n");
		
		#define DEFINE_LOG(TYPE,TITLE,DEST)			addLogType(TITLE,&fd_##TYPE,DEST);
		#define LOG_SIZE(TITLE,SIZE)				setLogSz(TITLE,SIZE);				
		#include "logging.h"
		#undef DEFINE_LOG
		#undef LOG_SIZE
		
		// warning("buildLogtypeTable complete.\n");
	} // buildLogtypeTable()

// End of using logging.h in alternate ways.
// -------------------------------------------

	void prompt(const char *f, ...)
	{
		va_list a; 
		va_start(a,f); 
		
        Lock lock(_lock);

		// alway prompt on a fresh line.
		if (termd == UNTERMED)
			write(STDOUT_FILENO, "\n", 1);
		
		// print out the prompt
        static char outstr[10000];
		vsprintf(outstr, f, a);
		unsigned tloc = strlen(outstr);
		write(STDOUT_FILENO, outstr, tloc);
#ifndef _WIN32
		fsync(STDOUT_FILENO);
#endif
		// following output always goes on a new line as well.
		termd = FORCE;
	} // prompt()

	bool tags = false;				// true if we want to have tags labeling each line
	bool timestamps = false;		// true if we want to have timestamps on each log
	const uint8_t LOGSMAX = 32;		// maximum # of logs we will manage (arbitrary)
	LOGNAME logNames[LOGSMAX];		// relate log name to file descriptor and output file
	uint8_t logNamesCnt = 0;		// actual # of logs we are using.
	const uint64_t defaultMaxFileSz = 1 * MEGA;

	// open a file descriptor for the log type. 
	void getFd(LOGNAME *logd)
	{
		char fdstr[20];
		char fsstr[20];
		int orig_fd = *logd->fd;
		sprintf(fsstr, "%lli", (long long unsigned int) logd->fileMaxSz);
		
		if(	logd->filename == "NULL" )
			goto all_done;	// nothing to open.

		if( *logd->fd != -1)
		{
			logd->filename = "NULL";
			WARNING("fd isn't NULL");
			goto all_done;
		}	

		if( logd->filename == "stdio")	// because I sometimes mess up...
			logd->filename = "stdout"; 

		if( logd->filename == "stdout")
		{
			*logd->fd = STDOUT_FILENO;
			goto all_done;
		}

		if( logd->filename == "stderr")
		{
			*logd->fd = STDERR_FILENO;
			goto all_done;
		}
		
		if( logd->filename == "CIRC")
		{
			*logd->fd = CIRCULAR_BUFFER_FD;
			goto all_done;
		}		

		if( logd->filename == "")
		{
			*logd->fd = -1;
			WARNING("Filename + \"\".");
			goto all_done;
		}

		*logd->fd = open(logd->filename.c_str(), O_WRONLY | O_CREAT , S_IREAD | S_IWRITE);

		//	warning("%s ==> fd = %i.\n", logd->filename.c_str(), *logd->fd);
		
		if( *logd->fd == -1)
		{
			WARN_ERRNO("open failed");
			logd->filename = "NULL";	
			goto all_done;
		}

	all_done:
		;
		sprintf(fdstr, "%i --> %i", orig_fd, *logd->fd);
		// warning("%20.20s	%20.20s	%20.20s	%20.20s\n", logd->title, logd->filename.c_str(), fdstr, fsstr);

	} //getFd()
	
	// add the logging type to logNames 
	void addLogType(const char *title, int* fd, const char *filename)
	{
		if (logNamesCnt >= LOGSMAX-1)
			abort();

		logNames[logNamesCnt].fd = fd;	
		*fd = -1;
		logNames[logNamesCnt].title = title;
		logNames[logNamesCnt].filename = filename;
		logNames[logNamesCnt].fileMaxSz = defaultMaxFileSz;
		
		getFd(&logNames[logNamesCnt]);
		logNamesCnt++;
	} // addLogType()
	
	// add the logging file size to logNames 
	void setLogSz(const char *filename, uint64_t maxSz)
	{
		for (int i = 0; i < logNamesCnt; i++)
		{
			if(logNames[i].filename == filename)
				logNames[i].fileMaxSz = maxSz;
		}	
	} // setLogSz()

	// check the file size of a logging output. 2 files are kept:
	// "<filename>", and "<filename>.2". 
	// Files are archived at 1/2 limit-size of file to the ".2" file.
	// if ".2" file exists, it is deleted first, then file becomes archived. 
	void limitFileSizes()
	{


	}



	void config_log(int x, char *type, std::string &filename)
	{
		// if we find a match, then set the destination to the one specified in the config file.
		if( strcmp( logNames[x].title, type))	// log type is different
			return;

		// warning("(match)\n");
		// warning("%i - '%s': '%s' ===> '%s'\n", x, logNames[x].title, logNames[x].filename.c_str(), filename.c_str());

		if(	logNames[x].filename == filename )	// the destination is same (no changes needed)
			return;
			
		// warning("(filename changed)\n");
		
		// if current file != NULL, then close it.
		if(logNames[x].filename != "NULL")
		{

			// warning("(non-null, close old file '%s')\n", logNames[x].filename.c_str());

			if( 	logNames[x].filename == "stdout"
				||	logNames[x].filename == "stderr"
			  )
			{
				*logNames[x].fd = -1;
			}
			else // regular file needs to be closed
			{
				close(*logNames[x].fd);
				*logNames[x].fd = -1;
			}	
		}	

		// warning("save filename\n");
		// record the new file name into our database
		logNames[x].filename = filename;

		// open new file (if non-NULL)
		getFd(&logNames[x]);
		
	} //config_log();


	void set_from_config(void)
	{
		// warning( "set_from_config()\n");
		
		// read the config - logging settings.
		const Json::Value Config = config::value["logging"];
		
		// warning( "entering loop\n");
		// loop through the config flags defined in config file
		for( Json::ValueIterator jI = Config.begin() ; jI != Config.end() ; jI++ ) 
		{
			const size_t STRLEN = 128;
			char type[STRLEN];
			
			// warning("copy type\n");
			strncpy(type, jI.memberName(), STRLEN -1);
			type[STRLEN-1] = '\0';

			// warning("get filename\n");
			std::string filename = Config[type].asString();
			// warning("look for config line for this type\n");
			// loop in our database looking for the type we found in the config file
			for (uint8_t x = 0; x < LOGSMAX; x++)
			{
				if( logNames[x].title == NULL)
					break;
				if( !strlen(logNames[x].title))
					ABORT("empty logName entry");
				config_log(x, type, filename);
			} // for ( each loggingValue in config::logging)
			
		}
		//	warning("set_from_config() complete.\n");
	} // set_from_config()

	void read_config(void)
	{
		set_from_config();

		// print oout a list of the active logging types.
		for (int i = 0; i < logNamesCnt; i++)
		{
			if(    (logNames[i].filename != "NULL")
				&& (logNames[i].fd != (int *) -1 )
				)
			{
				logs::info("  %s", logNames[i].title);
			}
		}	

		logs::info("\n");
	}


	void init(bool timestamps, bool tags)
	{
		logs::timestamps = timestamps;
		logs::tags = tags;
		
		buildLogtypeTable();
	}

	
} // namespace log
