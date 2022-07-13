//*****************************************************************************
// queryGetVar.cpp
//
//	Works with mongoose server; used to retrieve variables from the get-query.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

#include "mongoose.h"
#include <sstream>
#include <cstdlib>
#include <cstring>
#include <string>
#include <json/json.h>
#include "queryGetVar.h"	

cQueryGetVar::cQueryGetVar(const struct mg_connection *conn, const struct mg_request_info * info)
{
	m_conn = (struct mg_connection *) conn;
	m_info = (struct mg_request_info *) info;
	queryBuffer = info->query_string;
	if(queryBuffer == NULL)
		queryLen = 0;
	else
		queryLen = strlen(queryBuffer);
}


bool cQueryGetVar::hasValue(const char* name)
{
    return (queryLen && mg_get_var(queryBuffer, queryLen, name, buffer, bufflen) >= 0);
}


int32_t cQueryGetVar::value(std::string var_name, const int32_t defaultValue)
{  
	if(!queryLen)
		return defaultValue;	

	mg_get_var(queryBuffer, queryLen, var_name.c_str(), buffer, bufflen);
	
	if(strlen(buffer) == 0)
		return defaultValue;

	std::stringstream Val;
	Val << buffer;
	
	int32_t result;
	
	return Val >> result ? result : defaultValue;				
}

std::string cQueryGetVar::value(std::string var_name, const std::string defaultValue)
{  

	if(!queryLen)
		return defaultValue;	

	mg_get_var(queryBuffer, queryLen, var_name.c_str(), buffer, bufflen);
	
	if(strlen(buffer) == 0)
		return defaultValue;

	std::string result(buffer);
	return result;				
}

std::string cQueryGetVar::payload(const std::string defaultValue)
{
	char *buf;
	size_t buf_len;

	buf_len = 0;
	buf = NULL;
	
	std::string pload = defaultValue;

	char *cl = (char *) mg_get_header(m_conn, "Content-Length");

	if( 	(	!strcmp(m_info->request_method, "POST")
			||	!strcmp(m_info->request_method, "PUT")
			)
		&& 	cl != NULL
	  ) 
	{
		buf_len = atoi(cl);
		buf = (char *) malloc(buf_len);
		mg_read(m_conn, buf, buf_len);
		pload.append(buf,buf_len);
		free(buf);
	}
	return pload;
}

bool cQueryGetVar::payload(Json::Value &root)
{
	char *buf;
	size_t buf_len;

	buf_len = 0;
	buf = NULL;
	
	char *cl = (char *) mg_get_header(m_conn, "Content-Length");

	if( 	(	!strcmp(m_info->request_method, "POST")
			||	!strcmp(m_info->request_method, "PUT")
			)
		&& 	cl != NULL
	  ) 
	{
		buf_len = atoi(cl);
		buf = (char *) malloc(buf_len);
		mg_read(m_conn, buf, buf_len);
		
		std::string pload = "";
		pload.append(buf,buf_len);
		free(buf);
		
		Json::Reader reader;	
		return reader.parse(pload, root);

	}
	return 0;
}



bool cQueryGetVar::getUnsignedValue(const char* name, uint32_t& result)
{
	if (!queryLen)
		return false;	

	mg_get_var(queryBuffer, queryLen, name, buffer, bufflen);
	
	if (strlen(buffer) == 0 || !isdigit(buffer[0]))
		return false;

    result = strtoul(buffer, NULL, 10);
    return true;
}

