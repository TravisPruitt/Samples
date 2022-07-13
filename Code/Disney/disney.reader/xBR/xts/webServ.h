//*****************************************************************************
// webServ.h 
//
//	Web Server pages & other definitions.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************
#ifndef __webserv_h
	#define __webserv_h

	#include "mongoose.h"
	#include <json/json.h>

	namespace webServ
	{
		extern void start(void);
		extern void stop(void);
		extern void quit(void);		// finish servicing the current web page and then exit.
	}

	namespace pages
	{
		typedef struct mg_connection * MG_CONN;
		typedef const struct mg_request_info * MG_INFO;
		typedef void* (*Page)(MG_CONN,MG_INFO);
		
		// information to provide on info page - and - hello messages.
		extern void reader_info( Json::Value & readerInfo);
		

		extern void *put_stream	(MG_CONN conn, MG_INFO info);
		extern void *put_hello		(MG_CONN conn, MG_INFO info);
		
		#define PAGE_MAP_DATA \
			{ "PUT",	"/stream",			put_stream },	\
			{ "PUT",	"/hello",			put_hello },	\
			{ NULL, NULL, NULL }
		
		#define PAGE_COMPLETE (void *)""
		#define HTTP_HEADER_JSON "HTTP/1.1 200 OK\r\n" \
							"Content-Type: text/plain\r\n" \
							"\r\n"

		#define UNIMPLEMENTED()	{	UNUSED(info); \
									logs::info("%s\n", __func__); \
									mg_printf(conn, HTTP_HEADER_JSON "%s() UNIMPLEMENTED\r\n", __func__);	\
									return PAGE_COMPLETE;	\
								}
							
	}
	
#endif
