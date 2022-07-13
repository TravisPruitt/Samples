//*****************************************************************************
// queryGetVar.h 
//
//	Works with mongoose server; used to retrieve variables from the get-query.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************
// USAGE:
//
//		// get the mongoose buffer.
//		cQueryGetVar query(conn, info);				
//
//	 	// retrieve the value "TheNumber" to number. If "TheNumber" 
//		//	is undefined, return the default value "0".
//		int number = query.value("TheNumber", 0);
//
//		// retrieve the value "STRINGVAL" to strVal. If "STRINGVAL"
//		//	is missing, return the default, "MISSING VALUE".
//		string strVal	= query.value("STRINGVAL", std::string("MISSING VALUE") );
//
//		// retrieve the payload (contents of the data, for POST or PUT)
//		// - checks the content-length, and returns the data as a large string.
//		string strPayload = query.payload( ""/* default = no payload */ );
//		

#ifndef __queryGetVar_h
	#define __queryGetVar_h

	#include "mongoose.h"
	#include <sstream>
	#include <cstring>	
	#include <stdint.h>	
	
	class cQueryGetVar
	{
	public:
		cQueryGetVar(const struct mg_connection *conn, const struct mg_request_info * info);
	public:
        bool hasValue(const char* name);
		int32_t	value(std::string var_name, const int32_t defaultValue);
		std::string value(std::string var_name, const std::string defaultValue);
		std::string payload(const std::string defaultValue);
		bool 		payload(Json::Value &root);

        bool getUnsignedValue(const char* name, uint32_t& result);
        
	private:
		char 				   *queryBuffer;
		size_t 					queryLen;
		struct mg_connection   *m_conn;
		struct mg_request_info *m_info;
		static const size_t 	bufflen = 4096;
		char 					buffer[bufflen];
	};

#endif
