//*****************************************************************************
// sendHttp.h
//
// Test Server - Send routines for  information to xBR devices
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include <string>
#include <cstring>
#include <sstream>
#include <stdlib.h>
// System Lib
#include <sys/types.h>
// Global-App
#include "HttpRequest.h"
#include "logging.h"
#include "config.h"
// Local-ish
#include "test-server.h"
#include "sendHttp.h"
#include "common.h"

using namespace Reader;

namespace
{
	bool sendRequest( HttpRequest& http, const char *url)
	{
		if( !http.send() )
		{
			logs::http(TCPF "TCP connection failed when requesting %s\n", url);
			return false;
		}	
		
		long rs = http.getResponseCode();
		
		if( rs != 200 )
		{
			logs::http(HTPF "%s\n", rs, url);
			return false;
		}
		
		logs::http(RCVD "\n", "200");
		return true;
	}
}

namespace sendHttp	
{
	bool setReaderName(Json::Value &xbr)
	{
		// prepare http message and send it.
		Json::StyledWriter writer;
		std::string str = writer.write(xbr);
			
		std::string name = xbr.get("reader name","NONE").asCString();
		std::string url = xbr.get("URL","NONE").asCString();

		if(name == "NONE")
		{
			ABORT("NO NAME!");
			return false;
		}
		if(url == "NONE")
		{
			ABORT("NO URL!");
			return false;
		}

		char reqURL[800];
		sprintf(reqURL, "%s/reader/name?name=%s", url.c_str(),name.c_str());
		
		logs::http(POST "%s\n", reqURL);
		
		HttpRequest req("POST", reqURL);
		req.setPayload("", "text/plain");
		return sendRequest(req, reqURL);
	}

	bool streamDelete(Json::Value &xbr)
	{
		// prepare http message and send it.
		char reqURL[200];
		sprintf(reqURL, "%s%s", xbr["URL"].asCString(), "/update_stream");
		
		logs::http(DEL "%s\n", reqURL);
		
		HttpRequest req("DELETE", reqURL);
		req.setPayload("", "text/plain");
		return sendRequest(req, reqURL);
	}
	
	bool streamAssign(Json::Value &xbr)
	{
		// prepare http message and send it.

		Json::Value address = config::value["address"];
		
		std::string IP		= address["IP"].asString();
		std::string stream	= address["stream"].asString();
		uint32_t port		= address["port"].asInt();
		int max				= config::value["stream_max"].asInt();
		int msec			= config::value["stream_msec"].asInt();
		int min_ss			= config::value["stream_min_ss"].asInt();
		
		std::string addr;
		if(port == 80)
			addr = IP;
		else 
			addr = format("%s:%i", IP.c_str(), port);

		char reqURL[800];
		char *p = reqURL;
		p += sprintf(p, "%s/update_stream?url=%s%s&interval=%i&max=%i&min-ss=%i",
				xbr["URL"].asCString(), addr.c_str(), stream.c_str(), msec, max, min_ss);

		logs::http(POST "%s\n", reqURL);
		
		HttpRequest req("POST", reqURL);		
		req.setPayload("", "text/plain");
		return sendRequest(req, reqURL);
	}	
	
	bool setReaderTime(Json::Value &xbr)
	{
		char reqURL[800];
        std::string timeString = getTimeAsString();
		sprintf(reqURL, "%s/time?time=%s", xbr["URL"].asCString(), timeString.c_str());
		
		logs::http(POST "%s\n", reqURL);
		HttpRequest req("POST", reqURL);		
		req.setPayload("", "text/plain");
		return sendRequest(req, reqURL);
	}
	
	//	void modeAssign(Json::Value &xbr)
	//	{
	//		// prepare http message and send it.
	//		char reqURL[800];
	//		sprintf(reqURL, "%s/mode?mode=normal", xbr["URL"].asCString());
	//		
	//		logs::http(POST "%s\n", reqURL);
	//		HttpRequest req("POST", reqURL);		
	//		req.setPayload("", "text/plain");
	//		return sendRequest(req);
	//	}	
	
	bool reset(Json::Value &xbr)
	{
		// prepare http message and send it.
		char reqURL[80];
		sprintf(reqURL, "%s/application/reset", xbr["URL"].asCString());
		logs::http(POST "%s\n", reqURL);
		HttpRequest req("POST", reqURL);		
		req.setPayload("", "text/plain");
		return sendRequest(req, reqURL);
	}

	bool reboot(Json::Value &xbr)
	{
		// prepare http message and send it.
		char reqURL[80];
		sprintf(reqURL, "%s/system/reset", xbr["URL"].asCString());
		logs::http(POST "%s\n", reqURL);
		HttpRequest req("POST", reqURL);		
		req.setPayload("", "text/plain");
		return sendRequest(req, reqURL);
	}

	bool deleteBands(Json::Value &xbr)
	{
		// prepare http message and send it.
        std::string url = xbr["URL"].asString() +  "/xband/commands";
		logs::http(DEL "%s\n", url.c_str());
		HttpRequest req1("DELETE", url.c_str());
		bool result1 = sendRequest(req1, url.c_str());

        url = xbr["URL"].asString() +  "/xband/beacon";
		logs::http(DEL "%s\n", url.c_str());
		HttpRequest req2("DELETE", url.c_str());
		return (sendRequest(req2, url.c_str()) && result1);
	}

	bool beacon(Json::Value &xbr, std::vector<std::string>& bands, std::string& command)
    {
        std::string url = xbr["URL"].asString() + "/xband/beacon";
        
        if (bands.size() > 0)
        {
            url += "?XLRID=";
            url += bands[0];
        }

        url += "&cmd=";
        url += command;

		logs::http(POST "%s\n", url.c_str());
		HttpRequest req("POST", url.c_str());
        return sendRequest(req, url.c_str());
    }

	bool commandBands(Json::Value &xbr, std::vector<std::string>& bands, std::string& command, int signalStrength)
	{
        std::string url = xbr["URL"].asString() + "/xband/commands";

        Json::Value json;

        if (bands.size() > 0)
        {
            for (size_t i = 0; i < bands.size(); ++i)
            {
                json["XLRID"].append(bands[i]);
            }
        }
        json["cmd"] = command;
        json["ss"] = signalStrength;

		logs::http(POST "%s\n", url.c_str());
		HttpRequest req("POST", url.c_str());
		req.setPayload(json);

		return sendRequest(req, url.c_str());
	}	
	
	// TODO - request the /reader/info.json page.

}
