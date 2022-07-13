//*****************************************************************************
// pageEvent.cpp 
//
//	pages handlers for xBand Reader: Events
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include <ctime>
#include <string>
#include <cstring>
// System Lib
#include "mongoose.h"
#include <json/json.h>
// Global-App
#include "grover.h"
#include "log.h"
#include "sysIface.h"
#include "config.h"
#include "queryGetVar.h"
#include "StringLib.h"
#include "EventBouncer.h"
// Local-ish
#include "webServ.h"
#include "EventLogger.h"
//#include "radio.h"
#include "Receiver.h"

using namespace Reader;

namespace pages
{
	// request for all un-sent events.
	void *get_events(MG_CONN conn, MG_INFO info)
	{
		uint32_t after;
        bool useAfter;
		uint32_t max;
		bool tail;
		std::string band_id;

		cQueryGetVar query(conn, info);

        useAfter = query.getUnsignedValue("after", after);
		max = query.value("max", -1);
		tail = query.value("tail", false);

        // Get band ID to filter on
		std::string idString = query.value("XLRID", "");
        BandId bandId;
        if (!StringLib::hex2bandId(idString.c_str(), &bandId))
            bandId = 0;

        // Get min signal strength filter
        int minSignalStrength = query.value("min-ss", -127);

        std::string str;
        EventLogger::instance()->getEvents(str, max, after, useAfter, tail, bandId, minSignalStrength);
		
		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n");
		mg_write (conn, str.data(), str.length());
		return PAGE_COMPLETE;
	}

	// tells us where to send a stream of POST data.
	// /update_stream?url=168.192.0.2&min-ss=-127
	void *put_stream(MG_CONN conn, MG_INFO info)
	{
		UNUSED(info);
		LOG_DEBUG("post_stream\n");

		cQueryGetVar params(conn, info);

		#define MINSS_INVALID 0
		#define MAX_INVALID -1
		#define URL_INVALID ""

		int interval = params.value("Interval", -1); 
		int max = params.value("Max", -1);
        int minss = params.value("min-ss", -127);
        uint32_t after;
        bool useAfter = params.getUnsignedValue("after", after);
		std::string url = params.value("url", "");

        // Get band ID to filter on
		std::string idString = params.value("XLRID", "");
        BandId bandId;
        if (!StringLib::hex2bandId(idString.c_str(), &bandId))
            bandId = 0;


        if (max == 0)
        {
            mg_printf(conn, "HTTP/1.1 400 Bad request\r\n\r\n");
            mg_printf(conn, "Invalid 'max' parameter\r\n");
            return PAGE_COMPLETE;
        }

        if (interval >= 0 && interval < 50)
        {
            mg_printf(conn, "HTTP/1.1 400 Bad request\r\n\r\n");
            mg_printf(conn, "'interval' must be at least 50ms\r\n");
            return PAGE_COMPLETE;
        }

        EventLogger::instance()->setPushParams(url.c_str(), interval, max, after, useAfter, bandId, minss);
		
		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n");
		return PAGE_COMPLETE;
	}

	// tells us where to send a stream of POST data.
	void *put_filter(MG_CONN conn, MG_INFO info)
	{
		UNUSED(info);
		LOG_DEBUG("put_filter\n");
        bool filterSet = false;


		cQueryGetVar get(conn, info);

		int minSS = get.value("min-ss", -999);
        if (minSS != -999)
		{	
            Receiver::instance()->setSignalStrengthFilter(minSS);
            filterSet = true;
		}


	    std::string str = get.value("XLRID", "");
        BandId id;
        if (StringLib::hex2bandId(str.c_str(), &id))
        {
            Receiver::instance()->setBandIdFilter(id);
            filterSet = true;
        }
        else if (strcasecmp(str.c_str(), "all") == 0)
        {
            Receiver::instance()->setBandIdFilter(0);
            filterSet = true;
        }

        if (!filterSet)
            sendBadRequestReply(conn, "No valid filters given");
        else
            mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n");

		return PAGE_COMPLETE;
	}


    void *get_filter(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        Json::Value json;

        json["min-ss"] = Receiver::instance()->getSignalStrengthFilter();

        char buf[20];
        sprintf(buf, "%010llx", Receiver::instance()->getBandIdFilter());
        json["XLRID"] = buf;
        
        Json::StyledWriter writer;
        std::string str = writer.write(json);

        mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n"
                        "%s",str.c_str());
        return PAGE_COMPLETE;
    }


    void *delete_filter(MG_CONN conn, MG_INFO info)
	{
		UNUSED(info);

		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n");
        Receiver::instance()->setBandIdFilter(0);
        Receiver::instance()->setSignalStrengthFilter(-127);
        return PAGE_COMPLETE;
    }
	
	void *delete_stream	(MG_CONN conn, MG_INFO info)
	{
		UNUSED(info);

        EventLogger::instance()->setPushUrl(NULL);
		
		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n");
		return PAGE_COMPLETE;
	}


	void *put_events(MG_CONN conn, MG_INFO info)
    {
		UNUSED(info);
		
		// get the mongoose buffer.
		cQueryGetVar query(conn, info);	
		
		// extract the put contents
		Json::Value json;
		bool parseok = query.payload(json);
	
        // If not Json payload, then look for URL encoded parameters
		if ( !parseok || json.empty())
        {
            sendBadRequestReply(conn, "No events given");
            return PAGE_COMPLETE;
        }

        int count = EventBouncer::instance()->addEvents(json);
        if (count <= 0)
        {
            sendBadRequestReply(conn, "No events given");
            return PAGE_COMPLETE;
        }

		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n"
                        "%d events added", count);

		return PAGE_COMPLETE;
    }


	void *delete_events(MG_CONN conn, MG_INFO info)
    {
		UNUSED(info);
		
        EventBouncer::instance()->clear();
		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n");

		return PAGE_COMPLETE;
    }
}
