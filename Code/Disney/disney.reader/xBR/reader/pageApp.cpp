//*****************************************************************************
// pageApp.cpp 
//
//    pages handlers for xBand Reader: Application
//*****************************************************************************
//
//    Written by Mike Wilson 
//    Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include "unistd.h"
#include <cstdio>
// System Lib
#include "mongoose.h"
#include <json/json.h>
#include <signal.h>
// Global-App
#include "grover.h"
#include "log.h"
#include "sysIface.h"
#include "config.h"
#include "queryGetVar.h"
// Local-ish
#include "webServ.h"
#include "ticks.h"

#include "UpgradeService.h"


namespace pages
{
    void *put_app_reset(MG_CONN conn ,MG_INFO info)
    {
        UNUSED(info);

        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
        LOG_INFO("application/reset command received...\n");
        grover_restart();
        return PAGE_COMPLETE;
    }
    
    void *put_sys_reset(MG_CONN conn ,MG_INFO info)
    {
        UNUSED(info);

        if (fork() != 0)
        {
            mg_printf(conn, "HTTP/1.1 200 OK\r\n");
            LOG_INFO("Reboot command received...\n");
        }
        else
        {
            sleepMilliseconds(2000);
            system("reboot");
            exit(0);    // should never get here
        }
        return PAGE_COMPLETE;
    }
    
    void *put_install (MG_CONN conn ,MG_INFO info)
    {
        UNUSED(info);
        
        LOG_INFO("Install command received...\n");

        // get the mongoose buffer.
        cQueryGetVar query(conn, info);    
        
        // extract the put contents
        std::string url = query.value("url", "");        
        if(url.empty())
        {
            sendBadRequestReply(conn, "Missing url parameter");
            return PAGE_COMPLETE;
        }
        
        Reader::UpgradeService service(conn);
        service.runInstall(url);

        return PAGE_COMPLETE;
    } // put_install

    void *put_upgrade(MG_CONN conn ,MG_INFO info)
    {
        UNUSED(info);
  
        LOG_INFO("Upgrade command received...\n");
      
        // get the mongoose buffer.
        cQueryGetVar query(conn, info);    
        
        // extract the put contents
        Json::Value json;
        if ( !query.payload(json) )
        {
            sendBadRequestReply(conn, "Invalid repository data");
            return PAGE_COMPLETE;
        }

        Reader::UpgradeService service(conn);
        service.runUpgrade(json);

        return PAGE_COMPLETE;
    } // put_upgrade()

    void *get_time_json(MG_CONN conn ,MG_INFO info)
    {
        UNUSED(info);
        LOG_DEBUG("get_time_json\n");

        /*{
            “time”: “2011-06-23T18:30:23”
          }*/

        // timestamp
        struct timespec timestamp;        // now as recorded
        clock_gettime(CLOCK_REALTIME, &timestamp);

        // timestamp as a string
        char tmStr[80];
        struct tm * timeinfo = gmtime(&timestamp.tv_sec);
        int ct = strftime (tmStr,80,"%Y-%m-%dT%H:%M:%S",timeinfo);
        sprintf(&tmStr[ct], ".%.3li", timestamp.tv_nsec / (1000 * 1000) );

        // json formatted 

        Json::Value root;            // the stuff
        root["time"] = tmStr;
        
        Json::StyledWriter writer;    // utility to write the stuff
        std::string json = writer.write(root);

        // TODO : write contents to page before returning

        mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n"
                        "%s",json.c_str());
        return PAGE_COMPLETE;

    }    //    get_time_json
} // namespace pages


