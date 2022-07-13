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
		extern int getListeningPort(void);
		extern void start(void);
		extern void stop(void);
		extern void quit(void);		// finish servicing the current web page and then exit.
	}

	namespace pages
	{
		typedef struct mg_connection * MG_CONN;
		typedef const struct mg_request_info * MG_INFO;
		typedef void* (*Page)(MG_CONN,MG_INFO);

        extern void sendStringContent(MG_CONN conn, const char* body);
        extern void sendStringReply(MG_CONN conn, const char* body);
        extern void sendJsonReply(MG_CONN conn, std::string& json);
        extern void sendJsonReply(MG_CONN conn, Json::Value& json);
        extern void sendBadRequestReply(MG_CONN conn, const char* msg);
        extern void sendOkReply(MG_CONN conn);

		#define PAGE_COMPLETE (void *)""
		#define HTTP_HEADER_JSON "HTTP/1.1 200 OK\r\n" \
							"Content-Type: text/plain\r\n" \
							"\r\n"

		#define UNIMPLEMENTED()	{	UNUSED(info); \
									log::info("%s\n", __func__); \
									mg_printf(conn, HTTP_HEADER_JSON "%s() UNIMPLEMENTED\r\n", __func__);	\
									return PAGE_COMPLETE;	\
								}
							
	}
	
#endif
