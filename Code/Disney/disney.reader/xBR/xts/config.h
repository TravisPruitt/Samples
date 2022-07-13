/**
    ConfigFile.h - Configuration file
    August 10, 2011
    Mike Wilson
*/

#ifndef __CONFIG_FILE_H
#define __CONFIG_FILE_H

#include "json/json.h"
#include "logging.h"

                                                         
namespace config
{
	#define DEFAULT_CONFIG_FILE "server.conf"
	
	extern Json::Value value;
    extern void save();
	extern void load();
	extern void init();		// this performs a load and also looks for default values that are not populated.

	extern const char *configFilePath;
};


//     NOTE: Single quotes are replaced with double quotes before this text is parsed allowing
//     you to use single quotes around string _values instead of having to type \" each time.

#define RULE_1 \
" 'j': \
{ \
	'idList': \
	[ \
		'ALL' \
	], \
	'xmtMode': 'REPLY', \
	'rcvThresh': -80, \
	'data': '010003000064' \
} \
"

#define RULE_2 \
" 'k': \
{ \
	'idList': \
	[ \
		'ALL' \
	], \
	'xmtMode': 'CW-BCAST', \
	'rcvThresh': -90, \
	'data': '010003000064' \
} \
"

#define RULE_3 \
" 'l': \
{ \
	'idList': \
	[ \
		'0102030405', '1112131415', '2122232425', '3132333435', \
		'4142434445', '5152535455', '6162636465', '7172737475', \
		'8182838485', '9192939495' \
	], \
	'xmtMode': 'REPLY', \
	'rcvThresh': -75, \
	'data': '010003000064' \
} \
"
#define DEFAULT \
"{ \
	'webserverOptions' : { \
		'error_log_file' : 'stdout', \
		'document_root' : '.', \
		'enable_directory_listing' : 'no', \
		'listening_ports' : 8080 \
	}, \
	'address' : \
	{ \
		'IP' : '10.75.3.70', \
		'port' : 8080, \
		'hello' : '/hello', \
		'stream' : '/stream' \
	}, \
    'stream_max' : 50, \
    'stream_msec' : 200, \
	'stream_min_ss' : -127, \
    'logging': \
    { \
		'CONF':'NULL', \
		'INFO':'NULL', \
		'FUNC':'NULL', \
		'EVNT':'stdout', \
		'HTTP':'stdout', \
		'CURL':'NULL', \
		'PAGE':'NULL', \
		'PARS':'NULL', \
		'SRVR':'NULL', \
		'READ':'NULL'  \
	} \
}"

/*
}, \
'rules': \
{ \
"RULE_1", \
"RULE_2", \
"RULE_3"  \
*/

#endif
