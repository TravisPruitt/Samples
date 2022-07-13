//*****************************************************************************
// pageBands.cpp 
//
// pages handlers for xBand Reader: xBand commands
//*****************************************************************************
//
// Written by Mike Wilson 
// Copyright 2011-2012, Synapse.com
//
//*****************************************************************************

#include <ctime>
#include <string>
#include <cstring>
#include "mongoose.h"
#include <json/json.h>

#include "grover.h"
#include "log.h"
#include "sysIface.h"
#include "config.h"
#include "queryGetVar.h"
#include "Radio.h"
#include "webServ.h"
#include "Transmitter.h"
#include "JsonParser.h"
#include "StringLib.h"

namespace pages
{
    /**
        Parse a single command in Json format. The format of a command can one of
        the following two:

        {
            "XLRID" : "<xBand ID>",
            "ss" : <signal strength>,
            "cmd" : "<command>"
        }

        OR...

        {
            "XLRID" : [ "<id1>", "<id2>", ..., "<idn>" ],
            "ss" : <signal strength>,
            "cmd" : "<command>"
        }

        XLRID is optional and defaults to repying to all xBands
        ss (signal strength) is option and defautls to -127
    */
    bool parseJsonBandCommand(const Json::Value& json)
    {
        int threshold = JsonParser::getInt(json, "ss", -127);

        std::string cmdString = JsonParser::getString(json, "cmd", "");
        uint8_t command[BANDCMD_MSG_SZ];
        if (cmdString.size() < (sizeof(command) * 2) || !StringLib::hex2bin(cmdString.c_str(), command, sizeof(command)*2))
            return false;

        // If XLRID is not specified, then send to all bands
        // (0 to the radio driver means all bands)
        if (!json.isMember("XLRID"))
        {
            Transmitter::setReply(ALL_BANDS, threshold, command);
        }

        // If the XLRID is not an array, try to intrepet as a single band Id
        else if (!json["XLRID"].isArray())
        {
            std::string bandString = JsonParser::getString(json, "XLRID", "");
            uint64_t bandId;
            if (!StringLib::hex2bandId(bandString.c_str(), &bandId))
                return false;
            Transmitter::setReply(bandId, threshold, command);
        }

        // else we have an array of band Ids
        else
        {
            const Json::Value idList = json["XLRID"];
            for (unsigned i = 0; i < idList.size(); ++i)
            {
                if (idList[i].isString())
                {
                    uint64_t bandId = 0;
                    std::string bandString = idList[i].asString();
                    if (bandString.size() <= 0 || !StringLib::hex2bandId(bandString.c_str(), &bandId))
                        return false;

                    Transmitter::setReply(bandId, threshold, command);
                }
            }
        }
        return true;
    }


    void *put_xband_cmds(MG_CONN conn, MG_INFO info)
    {
        // get the mongoose buffer.
        cQueryGetVar query(conn, info);    
        
        // extract the put contents
        Json::Value json;
        bool parseok = query.payload(json);
    
        // If not Json payload, then look for URL encoded parameters
        if ( !parseok || json.empty())
        {
            // TODO - combine with code from put_xband_beacon
            std::string bandString = query.value("XLRID", "");
            std::string cmdString = query.value("cmd", "");
            int threshold = query.value("ss", -127);

            uint8_t command[BANDCMD_MSG_SZ];
            if (cmdString.size() < (sizeof(command) * 2) || !StringLib::hex2bin(cmdString.c_str(), command, sizeof(command)*2))
            {
                sendBadRequestReply(conn, "Illformed or missing 'cmd' parameter");
                return PAGE_COMPLETE;
            }

            uint64_t bandId = ALL_BANDS;
            if (bandString.size() > 0)
            {
                if (!StringLib::hex2bandId(bandString.c_str(), &bandId))
                {
                    sendBadRequestReply(conn, "Illformed 'XLRID' parameter");
                    return PAGE_COMPLETE;
                }
            }

            Transmitter::setReply(bandId, threshold, command);
        }

        // else command data should be in the json payload
        else
        {
            // Allow list to be preceded by "commands"
            if (json.isObject() && json.isMember("commands"))
                json = json["commands"];

            if (!json.isArray())
                parseJsonBandCommand(json);

            else
            {
                for (Json::ArrayIndex i = 0; i < json.size(); i++)
                    parseJsonBandCommand(json[i]);
            }
        }
        
        sendOkReply(conn);
        return PAGE_COMPLETE;
    }


    void *delete_xband_cmds(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        Transmitter::clearReplies();

        sendOkReply(conn);
        return PAGE_COMPLETE;
    }


    void *get_xband_cmds(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        unsigned reply_count = 0;
        unsigned apb = 0;
        char buf[16];

        memset(buf, 0, sizeof(buf));
        FILE *fd = fopen("/sys/class/grover/grover_radio0/tx_band_data_count", "r");
        fread(buf, sizeof(buf)-1, 1, fd);
        fclose(fd);
        sscanf(buf, "%u", &reply_count);

        memset(buf, 0, sizeof(buf));
        fd = fopen("/sys/class/grover/grover_radio0/tx_apb", "r");
        fread(buf, sizeof(buf)-1, 1, fd);
        fclose(fd);
        sscanf(buf, "%u", &apb);

        Json::Value json;
        json["enabled"] = (reply_count > 0 || apb != 0) ? true : false;
        json["reply_count"] = reply_count;
        json["reply_all"] = apb != 0 ? true : false;

        sendJsonReply(conn, json);
        return PAGE_COMPLETE;
    }


    void *put_xband_beacon(MG_CONN conn, MG_INFO info)
    {
        cQueryGetVar query(conn, info);
        std::string bandString = query.value("XLRID", "");
        std::string cmdString = query.value("cmd", "");

        uint8_t command[BANDCMD_MSG_SZ];
        if (cmdString.size() < (sizeof(command) * 2) || !StringLib::hex2bin(cmdString.c_str(), command, sizeof(command)*2))
        {
            sendBadRequestReply(conn, "Illformed or missing 'cmd' parameter");
            return PAGE_COMPLETE;
        }

        uint64_t bandId = Transmitter::BroadcastId;
        if (bandString.size() > 0)
        {
            if (!StringLib::hex2bandId(bandString.c_str(), &bandId))
            {
                sendBadRequestReply(conn, "Illformed 'XLRID' parameter");
                return PAGE_COMPLETE;
            }
        }

        {
            std::string id = StringLib::formatBytes((uint8_t*)&bandId, 5);
            std::string cmd = StringLib::formatBytes(command, sizeof(command));
            LOG_DEBUG("beacon '%s' to '%s'", cmd.c_str(), id.c_str());
        }

        Transmitter::startBeacon(bandId, command);
        
        sendOkReply(conn);
        return PAGE_COMPLETE;
    }


    void *delete_xband_beacon(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        Transmitter::stopBeacon();

        sendOkReply(conn);
        return PAGE_COMPLETE;
    }


    void *get_xband_beacon(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        uint64_t id;
        uint8_t cmd[BANDCMD_MSG_SZ];

        Json::Value json;
        json["enabled"] = false;

        if (Transmitter::getBeacon(id, cmd))
        {
            char buf[20];
            sprintf(buf, "%010llx", id);

            json["enabled"] = true;
            json["XLRID"] = buf;
            json["cmd"] = StringLib::formatBytes(cmd, sizeof(cmd), "");
        }

        sendJsonReply(conn, json);

        return PAGE_COMPLETE;
    }


    /**
        Turn off all transmissions to bands - both beacons and replies
    */
    void *delete_xband_output(MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        Transmitter::clearReplies();
        Transmitter::stopBeacon();

        sendOkReply(conn);
        return PAGE_COMPLETE;
    }

    
    void *put_xband_options  (MG_CONN conn, MG_INFO info)
    {
        cQueryGetVar query(conn, info);
        
        unsigned timeout = query.value("reply_timeout", Transmitter::getReplyTimeout());
        Transmitter::setReplyTimeout(timeout);
        
        sendOkReply(conn);
        return PAGE_COMPLETE;
    }


    void *get_xband_options  (MG_CONN conn, MG_INFO info)
    {
        UNUSED(info);

        Json::Value json;

        json["reply_timeout"] = Transmitter::getReplyTimeout();

        sendJsonReply(conn, json);
        return PAGE_COMPLETE;
    }

} // namespace pages
