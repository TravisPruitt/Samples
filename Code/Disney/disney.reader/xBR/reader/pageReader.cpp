//*****************************************************************************
// pageReader.cpp 
//
// pages handlers for xBand Reader: Reader
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include <cstring>
#include <cstdio>
#include <string> 
#include <algorithm>
#include <vector>
#include <fstream>
#include <memory>
// System Lib
#include "mongoose.h"
#include <json/json.h>
// Global-App
#include "grover.h"
#include "log.h"
#include "sysIface.h"
#include "config.h"
// Local-ish
#include "queryGetVar.h"
#include "webServ.h"
#include "version.h"
#include "EventLogger.h"
#include "Radios.h"
#include "ReaderInfo.h"
#include "Transmitter.h"

using namespace Reader;


namespace pages
{
	void *get_reader_info(MG_CONN conn, MG_INFO info)
	{
		UNUSED(info);
		Json::Value json;
		ReaderInfo::instance()->getInfo(json, true); // return detailed info == true
        sendJsonReply(conn, json);
		return PAGE_COMPLETE;
	}


    void *get_reader_log(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        const char* filename = "/var/log/reader.log";

        std::ifstream file(filename);
        if (file.fail())
        {
            LOG_ERROR("Unable to open '%s' for reading", filename);
            mg_printf(conn, "HTTP/1.1 500 Internal Server Error\r\n\r\n");
            return PAGE_COMPLETE;
        }
        
        // Get the file size
        file.seekg(0, std::ios::end);
        std::streamsize size = file.tellg();
        file.seekg(0, std::ios::beg);
        
        std::auto_ptr<char> buf(new char[size]);
        
        file.read(buf.get(), size);
        if (file.fail())
        {
            LOG_ERROR("Unable to read from file '%s'", filename);
            mg_printf(conn, "HTTP/1.1 500 Internal Server Error\r\n\r\n");
            return PAGE_COMPLETE;
        }
        file.close();
        
        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
        mg_printf(conn, "Content-Type: text/plain\r\n");
        mg_printf(conn, "Content-Length: %d\r\n\r\n", size);
        mg_write(conn, buf.get(), size);

        return PAGE_COMPLETE;
    }


    void *get_diagnostics(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);
		Json::Value json;
		ReaderInfo::instance()->getDiagnostics(json);
        sendJsonReply(conn, json);
		return PAGE_COMPLETE;
    }


	void *put_reader_name(MG_CONN conn, MG_INFO info)
	{
		UNUSED(info);
		LOG_DEBUG("put_reader_name()\n");

		cQueryGetVar query(conn, info);				

        if (!query.hasValue("name"))
        {
            mg_printf(conn, "HTTP/1.1 400 Bad request\r\n\r\n");
            mg_printf(conn, "Missing name parameter\r\n");
            return PAGE_COMPLETE;
        }

		std::string newName = query.value("name", std::string(""));
        if (newName.size() > 500)
        {
            mg_printf(conn, "HTTP/1.1 400 Bad request\r\n\r\n");
            mg_printf(conn, "name too long\r\n");
            return PAGE_COMPLETE;
        }

		Config::instance()->setValue("name", newName);
		Config::instance()->save();
        EventLogger::instance()->setReaderName(newName.c_str());

		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n");
		return PAGE_COMPLETE;
	}	


    void setTime(struct mg_connection* conn, std::string& timeString)
    {
        if (timeString.size() <= 0)
        {
            mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
            mg_printf(conn, "Missing time parameter\r\n");
            return;
        }
        else
        {
            struct tm tm;

            char* s = strptime(timeString.c_str(), "%Y-%m-%dT%H:%M:%S", &tm);
            if (s == NULL)
            {
                mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
                mg_printf(conn, "Improperly formatted time parameter\r\n");
                mg_printf(conn, "Correct format is yyyy-mm-ddThh:mm:s or yyyy-mm-ddThh:mm:ss.mmm\r\n");
                return;
            }
            else
            {
                struct timeval newTime, oldTime, diff;

                // get the current time
                gettimeofday(&oldTime, NULL);

                // parse the new time
                newTime.tv_sec = mktime(&tm);
                long mseconds = 0;
                if (s[0] == '.' && isdigit(s[1]))
                    mseconds = atoi(s+1);
                newTime.tv_usec = (mseconds < 1000) ? mseconds * 1000 : 0;

                // calculate the difference between the two
                timersub(&newTime, &oldTime, &diff);
                LOG_DEBUG("time diff = %d sec + %d usec\n", diff.tv_sec, diff.tv_usec);

                // is it a small change or large change?
                bool largeChange = false;
                bool smallChange = false;
                const static int LargeStep = 128000;

                if (diff.tv_sec > 0)
                {
                    largeChange = true;
                }
                else if (diff.tv_sec == 0)
                {
                    if (diff.tv_usec > LargeStep)
                        largeChange = true;
                    else if (diff.tv_usec != 0)
                        smallChange = true;
                }
                else if (diff.tv_sec == -1)
                {
                    if (diff.tv_usec < (1000000 - LargeStep))
                        largeChange = true;
                    else
                        smallChange = true;
                }
                else // diff.tv_sec < -1
                {
                    largeChange = true;
                }

                // fix the time
                if (largeChange)
                {
                    settimeofday(&newTime, NULL);
                    LOG_DEBUG("new time set\n");
                }
                else if (smallChange)
                {
                    adjtime(&diff, NULL);
                    LOG_DEBUG("time adjusted\n");
                }
            }
        }
        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    }

	
    void *put_reader_time(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);
        LOG_DEBUG("put_reader_time()\n");

        cQueryGetVar query(conn, info);
        std::string stime = query.value("time", std::string(""));
        setTime(conn, stime);
        return PAGE_COMPLETE;
    }


    void *put_xbrc_url(MG_CONN conn, MG_INFO info)
    {
        LOG_DEBUG("Received xBRC update request");
        
        cQueryGetVar query(conn, info);

        // Retrieve and validate that we were given a correctly formated url
        std::string url = query.value("url", "");
        if (url.empty() ||
            ((url.find("http://") != 0) && (url.find("https://") != 0)))
        {
            LOG_WARN("Received invalid xBRC URL parameter");
            mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
            return PAGE_COMPLETE;
        }
        
        Config::instance()->setXbrcUrl(url.c_str());

        Transmitter::stopBeacon();
        Transmitter::clearReplies();

        mg_printf(conn, "HTTP/1.1 200 OK\r\n\r\n");
        return PAGE_COMPLETE;
    }

    void *delete_xbrc_url(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);
        LOG_DEBUG("Received xBRC delete request");

        Config::instance()->clearXbrcUrl();

        Transmitter::stopBeacon();
        Transmitter::clearReplies();

        mg_printf(conn, "HTTP/1.1 200 OK\r\n\r\n");
        return PAGE_COMPLETE;
    }
}
