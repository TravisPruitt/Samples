//*****************************************************************************
// webServ.cpp 
//
//	http server task for xBand Reader. Provides handlers for mongoose events. 
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include <cstring>
#include <cstdio>
// System Lib
// Global-App
#include "logging.h"
#include "test-server.h"
#include "config.h"
#include "mongoose.h"
// Local-ish
#include "webServ.h"


namespace pages
{
	const struct page_map
	{
		const char *method;
		const char *addr;
		Page page;
	} page_map[] = { PAGE_MAP_DATA };
	
	// New HTTP request has arrived from the client
	void *request(MG_CONN conn,MG_INFO info)
	{
		int i;
		logs::http(RCVD "%s\n", info->request_method, info->uri);
		for (i = 0; page_map[i].addr != NULL; i++)
		{
			if(strcmp(info->request_method, page_map[i].method) != 0)
				continue;
			if(strcmp(info->uri, page_map[i].addr) != 0)
				continue;
			logs::http(RES1"\n");	
			return page_map[i].page(conn, info);
		}

		logs::info("request %s %s\n", info->request_method, info->uri);
		WARNING("404 returned");

		// Echo requested URI back to the client
		logs::http(RES0"\n");
		mg_printf(conn, "HTTP/1.1 404 Not Found\r\n" "Content-Type: text/plain\r\n\r\n" "%s", info->uri);
		return PAGE_COMPLETE;  // Mark as processed
	}

	// HTTP error must be returned to the client
	void *error(MG_CONN conn ,MG_INFO info)
	{
		UNUSED(conn);
		UNUSED(info);

		logs::http("WEBSVR ERROR.\n");
		return PAGE_COMPLETE;  // Mark as processed
	}

	// Mongoose logs an event, request_info.log_message
	void *eventlog(MG_CONN conn ,MG_INFO info)
	{
		UNUSED(conn);
		UNUSED(info);
		logs::http("WEBSRVR INFO.\n");
		return PAGE_COMPLETE;  // Mark as processed
	}

	// Mongoose initializes SSL. Instead of mg_connection *,
	void *initssl(MG_CONN conn ,MG_INFO info)
	{
		UNUSED(conn);
		UNUSED(info);
		WARNING("mongoose initssl");
		return PAGE_COMPLETE;  // Mark as processed
	}

	// mongoose needs a function to call back, here it creates a POSIX
	// message and sends it to a processing task.
	void *callback(enum mg_event event,MG_CONN conn ,MG_INFO info) 
	{
		// msg::send(messageQueue, MSG_MONGOOSE_EVENT, &mgMsg);
		switch(event)
		{
			case MG_NEW_REQUEST:return request(conn,info); // New HTTP request has arrived from the client
			case MG_HTTP_ERROR:	return error(conn,info);		// HTTP error must be returned to the client
			case MG_EVENT_LOG:	return eventlog(conn,info);	// Mongoose logs an event, request_info.log_message
			case MG_INIT_SSL:	return initssl(conn,info);		// Mongoose initializes SSL. Instead of mg_connection *,
			default:			break;
		}
		return PAGE_COMPLETE;
	}
	
	void *page404(MG_CONN conn, std::string errText)
	{
		WARNING("xbands commands failed");
		mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n" "Content-Type: text/plain\r\n\r\n"
						"%s", errText.c_str());
		return PAGE_COMPLETE;
	}
	
}

namespace webServ
{
	struct mg_context *ctx;

	#define MAX_OPTIONS 6
	#define OPTFLDS (((MAX_OPTIONS)+1)*2)
	char opts[OPTFLDS][40];
	char *options[OPTFLDS] =
	{ 	opts[0], opts[1], opts[2], opts[3], opts[4], 
		opts[5], opts[6], opts[7], opts[8], opts[9], 
		opts[10], opts[11], opts[12], opts[13] 
	};

	void exitHandler( int status, void *arg)
	{ 
		UNUSED (status);
		UNUSED (arg);
			
		if(ctx != NULL)
			stop();
	}

	// get server options from config file. Just grab every option set here and apply them to options.
	void getConfigOptions()
	{
		Json::Value	opt = config::value["webserverOptions"];	// the json object
		Json::Value::Members list = opt.getMemberNames(); 		// the names of the fields
		
		int iOpt = 0;
		uint32_t cL = 0;
		
		while (cL < list.size())  // Iterate field names
		{
			// copy name to array
			strcpy(opts[iOpt], list[cL].c_str());

			Json::Value val = opt[list[cL]];		// the field value
			
			// copy value as a string into array
			if     (val.isString())	sprintf(opts[iOpt+1], "%s", val.asString().c_str() );
			else if(val.isBool())	sprintf(opts[iOpt+1], "%d", val.asBool() );
			else if(val.isInt())	sprintf(opts[iOpt+1], "%d", val.asInt() );
			else if(val.isUInt()) 	sprintf(opts[iOpt+1], "%u", val.asUInt() );
			else if(val.isDouble())	sprintf(opts[iOpt+1], "%f", val.asDouble() );
			else 					sprintf(opts[iOpt+1], "type??");

			cL++;
			iOpt += 2;
			
			if(iOpt >= OPTFLDS)
			{	
				WARNING("webServ options exceed defined storage");
				break;
			}
			
		}
		// notify user of array this is the end.
		options[iOpt] = NULL;
	}

	void start(void)
	{
#ifndef _WIN32
		on_exit(exitHandler, (void *) NULL);
#endif

		getConfigOptions();	// build options array from config file field

		ctx = mg_start(pages::callback, NULL, (const char**) options);
		if(ctx == NULL)
			ABORT("mongoose: No Context!");
	}

	// force mongoose to close down.
	void stop(void)
	{
		fflush(stdout);
		mg_stop(ctx);
	}
	
	// called when system is expected to reset as a result of receiving an HTTP request
	// It actually does nothing, as we will just let the threads dies a natural death, 
	// which should allow the threads here to finish.
	void quit(void)
	{
		// This is a test of the Emergency Broadcast System. The broadcasters of 
		// your area in voluntary cooperation with federal, state and local authorities
		// have developed this system to keep you informed in the event of an emergency.
		;
		// If this had been an actual emergency, you would have been 
		// instructed where to tune in your area for news and official information.
	}
}

/*
cgi_extensions 	= Comma-separated list of CGI extensions. All files 
* having these extensions are treated as CGI scripts. 
* Default: ".cgi,.pl,.php"

cgi_environment = Extra environment variables to be passed to the 
* CGI script in addition to standard ones. The list must be comma-
* separated list of X=Y pairs, like this: 
* "VARIABLE1=VALUE1,VARIABLE2=VALUE2". Default: ""

put_delete_passwords_file = PUT and DELETE passwords file. This must 
* be specified if PUT or DELETE methods are used. Default: ""

cgi_interpreter = Use cgi_interpreter as a CGI interpreter for all 
* CGI scripts regardless script extension. Default: "". Mongoose decides 
* which interpreter to use by looking at the first line of a CGI script.

protect_uri = Comma separated list of URI=PATH pairs, specifying that 
* given URIs must be protected with respected password files. 
* Default: ""

authentication_domain = Authorization realm. Default: "mydomain.com"

ssi_extensions = Comma separated list of SSI extensions. Unknown SSI 
* directives are silently ignored. Currently, two SSI directives 
* supported, "include" and "exec". Default: "shtml,shtm"

access_log_file = Access log file. Default: "", no logging is done.

enable_directory_listing = Enable/disable directory listing. 
* Default: "yes"

error_log_file = Error log file. Default: "", no errors are logged.

global_passwords_file = Location of a global passwords file. If set, 
* per-directory .htpasswd files are ignored, and all requests must be 
* authorised against that file. Default: ""

index_files = Comma-separated list of files to be treated as directory 
* index files. Default: "index.html,index.htm,index.cgi"

access_control_list = Specify access control list (ACL). ACL is a 
* comma separated list of IP subnets, each subnet is prepended by '-' 
* or '+' sign. Plus means allow, minus means deny. If subnet mask is 
* omitted, like "-1.2.3.4", then it means single IP address. Mask may 
* vary from 0 to 32 inclusive. On each request, full list is traversed, 
* and last match wins. Default setting is to allow all. For example, to 
* allow only 192.168/16 subnet to connect, run 
* "mongoose -0.0.0.0/0,+192.168/16". Default: ""

extra_mime_types = Extra mime types to recognize, in form 
* "extension1=type1,exten- sion2=type2,...". Extension must include dot. 
* Example: "mongoose -m .cpp=plain/text,.java=plain/text". Default: ""

listening_ports = Comma-separated list of ports to listen on. If the 
* port is SSL, a letter 's' must be appeneded, for example, "-p 80,443s" 
* will open port 80 and port 443, and connections on port 443 will be 
* SSL-ed. It is possible to specify an IP address to bind to. In this 
* case, an IP address and a colon must be prepended to the port number. 
* For example, to bind to a loopback interface on port 80 and to all 
* interfaces on HTTPS port 443, use "mongoose -p 127.0.0.1:80,443s". 
* Default: "8080"

document_root = Location of the WWW root directory. A comma separated 
* list of URI_PREFIX=DIRECTORY pairs could be appended to it, allowing 
* Mongoose to serve from multiple directories. For example, 
* "mongoose -p /var/www,/config=/etc,/garbage=/tmp". Default: "."

ssl_certificate = Location of SSL certificate file. Default: ""

num_threads = Number of worker threads to start. Default: "10"

run_as_user = Switch to given user's credentials after startup. 
* Default: ""
*/
