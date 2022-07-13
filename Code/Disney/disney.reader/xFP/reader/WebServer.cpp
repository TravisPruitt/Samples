/**
 *  @file   WebServer.cpp
 *  @date   October, 2012
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  This class implements the reader web interface.
 */


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <vector>
#include <fstream>
#include <memory>
#ifndef _WIN32
#include <sys/stat.h>
#include <sys/time.h>
#include <signal.h>
#endif

#include "standard.h"
#include "WebServer.h"
#include "json/json.h"
#include "version.h"
#include "DapConfig.h"
#include "log.h"
#include "EventLogger.h"
#include "DapReader.h"
#include "BiometricReader.h"
#include "base64.h"
#include "TestThread.h"
#include "Sensors.h"
#include "LightEffects.h"
#include "RFIDReader.h"
#include "FileSystem.h"
#include "UpgradeService.h"
#include "Media.h"
#include "Sound.h"


#ifdef _WIN32
#define SSL_CERTIFICATE_PATH "ssl.crt"
#else
#define SSL_CERTIFICATE_PATH "/usr/lib/ssl/server.pem"
#endif


#define DEFAULT_WEBSERVER_PORT "8080"
#define PROCESSED (void*)""


using namespace Reader;
using namespace RFID;

TestThread testThread;


static void sendStringContent(struct mg_connection* conn, const char* body)
{
    mg_printf(conn, "Content-Length: %d\r\n\r\n", strlen(body));
    mg_printf(conn, body);
}


static void sendStringContent(struct mg_connection* conn, const std::string& body)
{
    mg_printf(conn, "Content-Length: %d\r\n\r\n", body.size());
    mg_printf(conn, body.c_str());
}


static void sendJsonReply(struct mg_connection* conn, std::string& json)
{
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    mg_printf(conn, "Content-Type: application/json\r\n");
    sendStringContent(conn, json);
}


static void sendJsonReply(struct mg_connection* conn, Json::Value& json)
{
    Json::StyledWriter writer;
    std::string s = writer.write(json);
    sendJsonReply(conn, s);
}


static void sendOkReply(struct mg_connection* conn)
{
    mg_printf(conn, "HTTP/1.1 200 OK\r\n\r\n");
}


static void sendOkReply(struct mg_connection* conn, const std::string& msg)
{
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    sendStringContent(conn, msg);
}


static void sendBadRequestReply(struct mg_connection* conn, const char* msg)
{
    mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n");
    if (msg)
    {
        mg_printf(conn, "Content-Type: text/plain\r\n");
        sendStringContent(conn, msg);
    }
    else
    {
        mg_printf(conn, "\r\n");
    }
}


static void sendInternalErrorReply(struct mg_connection* conn)
{
    mg_printf(conn, "HTTP/1.1 500 Internal Server Error\r\n");
}


static void sendFileContents(struct mg_connection* conn, const char* filename, const char* mimetype)
{
    std::ifstream file(filename);
    if (file.fail())
    {
        LOG_ERROR("Unable to open '%s' for reading", filename);
        sendInternalErrorReply(conn);
        return;
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
        sendInternalErrorReply(conn);
        return;
    }
    file.close();

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    mg_printf(conn, "Content-Type: %s\r\n", mimetype);
    mg_printf(conn, "Content-Length: %d\r\n\r\n", size);
    mg_write(conn, buf.get(), size);
}


static void handleExpectContinue(struct mg_connection* conn)
{
    // It's conceivable that clients (i.e. curl) will send
    // and wait (for a time) for a 100 continue response.
    // Send a 100 continue to keep things moving quickly.
    const char* s = mg_get_header(conn, "Expect");
    if (s && ( strcasecmp(s, "100-continue") == 0))
    {
        mg_printf(conn, "HTTP/1.1 100 continue \r\n\r\n");
    }
}


static size_t getContentLength(struct mg_connection* conn)
{
    size_t length = 0;
    const char* s = mg_get_header(conn, "Content-Length");
    if (s)
    {
        length = atoi(s);
    }
    return length;
}


static bool hasParam(const struct mg_request_info* info, const char* name)
{
    char buf[1024];
    int queryLen = 0;
    if (info->query_string)
        queryLen = strlen(info->query_string);
    if ( queryLen &&
         (mg_get_var(info->query_string, queryLen, name, buf, sizeof(buf)) >= 0))
    {
        return true;
    }

    return false;
}


static std::string getParam(const char* paramName, const struct mg_request_info* request_info, Json::Value* jsonPayload, const char* defaultValue)
{
    char buf[1024];

    if ( (request_info->query_string != NULL) && (mg_get_var(request_info->query_string, strlen(request_info->query_string), paramName, buf, sizeof(buf)) >= 0) )
    {
        return buf;
    }

    if ( (jsonPayload != NULL) && jsonPayload->isMember(paramName) && (*jsonPayload)[paramName].isString())
    {
        return (*jsonPayload)[paramName].asString();
    }

    return defaultValue;
}


static int getParam(const char* paramName, const struct mg_request_info* request_info, Json::Value* jsonPayload, int defaultValue)
{
    char buf[32];

    if ( (request_info->query_string != NULL) )
    {
        mg_get_var(request_info->query_string, strlen(request_info->query_string), paramName, buf, sizeof(buf));
    }

    if ( (request_info->query_string != NULL) && 
         (mg_get_var(request_info->query_string, strlen(request_info->query_string), paramName, buf, sizeof(buf)) >= 0) &&
         isdigit(buf[0]) )
    {
        return atoi(buf);
    }
    else if ( (jsonPayload != NULL) && jsonPayload->isMember(paramName) && (*jsonPayload)[paramName].isInt())
    {
        return (*jsonPayload)[paramName].asInt();
    }

    return defaultValue;
}



static bool readJsonPayload(struct mg_connection* conn, Json::Value& returnValue)
{
    size_t length = getContentLength(conn);
    if (!length)
    {
        return false;
    }

    char* payload = (char*)alloca(length);
    if (!payload)
    {
        LOG_ERROR("HTTP in: Unable to allocate memory for reading payload\n");
        DapReader::instance()->setStatus(IStatus::Red, "Out of stack space");
        return false;
    }
    
    int bytesRead = mg_read(conn, payload, length);
    if (bytesRead != (int)length)
    {
        LOG_ERROR("HTTP in: payload length (%d) != Content-Length (%u)\n", bytesRead, length);
        return false;
    }

    Json::Reader reader;
    if (!reader.parse(payload, payload + length, returnValue, false))
    {
        LOG_ERROR("HTTP in: payload does not parse as json\n");
        return false;
    }
    return true;
}


static bool startsWith(const char* s, const char* startingString)
{
    while (*s && *startingString)
    {
        if (tolower(*s++) != tolower(*startingString++))
            return false;
    }
    return (*startingString == '\0');
}


extern void restart();
static bool doAppReset(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    sendOkReply(conn);
    LOG_INFO("application/reset command received...\n");
    restart();
    return true;
}


static bool doName(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        Json::Value value;
        value["name"] = DapConfig::instance()->getValue("name", "");
        sendJsonReply(conn, value);
    }
    else
    {
        Json::Value json;
        readJsonPayload(conn, json);

        // We do this check because an empty name parameter is considered valid.
        if (!hasParam(request_info, "name") && !json.isMember("name"))
        {
            sendBadRequestReply(conn, "Missing 'name' parameter\r\n");
            return true;
        }

        std::string name = getParam("name", request_info, &json, "");
        LOG_DEBUG("new reader name = <%s>\n", name.c_str());
        DapConfig::instance()->setValue("name", name);
        DapConfig::instance()->save();

        sendOkReply(conn);
    }
    return true;
}


static bool doTime(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        Json::Value value;
        value["time"] = DapReader::instance()->getTimeAsString();
        sendJsonReply(conn, value);
    }
    else
    {
#ifndef _WIN32
        Json::Value json;
        readJsonPayload(conn, json);

        std::string timeString = getParam("time", request_info, &json, "");
        if (timeString.size() <= 0)
        {
            sendBadRequestReply(conn, "Missing 'time' parameter\r\n");
            return true;
        }
        else
        {
            struct tm tm;

            char* s = strptime(timeString.c_str(), "%Y-%m-%dT%H:%M:%S", &tm);
            if (s == NULL)
            {
                sendBadRequestReply(conn, "Improperly formatted time parameter\r\n"
                                          "Correct format is yyyy-mm-ddThh:mm:s or"
                                          " yyyy-mm-ddThh:mm:ss.mmm\r\n");
                return true;
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
#endif
        sendOkReply(conn);
    }
    return true;
}


static bool doUpdateStream(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    readJsonPayload(conn, json);

    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        json["url"] = EventLogger::instance()->getPushUrl();
        json["interval"] = EventLogger::instance()->getPushInterval();
        json["max"] = EventLogger::instance()->getMaxEventsToPush();
        sendJsonReply(conn, json);
    }
    else if (strcasecmp(request_info->request_method, "delete") == 0)
    {
        EventLogger::instance()->setPushUrl(NULL);

        sendOkReply(conn);
    }
    else
    {
        int interval = getParam("interval", request_info, &json, -1);
        int max = getParam("max", request_info, &json, -1);
        int after = getParam("after", request_info, &json, EventLogger::instance()->getNextEventNumberToPush() - 1);
        std::string url = getParam("url", request_info, &json, "");
       
        LOG_DEBUG("Pushing events to '%s', interval %d, max %d, after %d\n", url.c_str(), interval, max, after);
        EventLogger::instance()->setPushParams(url.c_str(), interval, max, after);

        sendOkReply(conn);
    }

    return true;
}


static bool doEvents(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;

    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        int max = getParam("max", request_info, &json, -1);
        int after = getParam("after", request_info, &json, -1);
        
        EventLogger::instance()->getEvents(json, max, after);
        sendJsonReply(conn, json);
    }
    else // assume PUT or POST
    {
        if (!readJsonPayload(conn, json) ||
            (!json.isArray() && !json.isObject()))
        {
            sendBadRequestReply(conn, NULL);
            return true;
        }

        if (json.isArray())
        {
            for (unsigned i = 0; i < json.size(); ++i)
            {
                if (json[i].isObject())
                    EventLogger::instance()->postEvent(json[i]);
            }
        }
        else
        {
            EventLogger::instance()->postEvent(json);
        }
        sendOkReply(conn);
    }

    return true;
}


static bool doInfo(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    Json::Value root;
    DapReader::instance()->getInfo(root);
    sendJsonReply(conn, root);
    return true;
}


static bool doLedScript(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    size_t length = getContentLength(conn);
    if (!length)
    {
        sendBadRequestReply(conn, "Missing or zero Content-Length");
        return true;
    }

    handleExpectContinue(conn);

    std::vector<char> payload;
    payload.reserve(length);

    int bytesRead = mg_read(conn, &payload.front(), length);
    if (bytesRead != (int)length)
    {
        LOG_ERROR("HTTP in: payload length (%d) != Content-Length (%u)", bytesRead, length);
        sendBadRequestReply(conn, NULL);
        return true;
    }

    // Get the timeout if any
    Json::Value json;
    int timeout = getParam("timeout", request_info, &json, 0);
    bool result = LightEffects::instance()->showScript(&payload.front(), length, timeout);
    if (!result)
    {
        sendBadRequestReply(conn, "Unable to execute LED script");
        return true;
    }

    sendOkReply(conn);
    return true;
}


static bool doInstallMediaPackage(struct mg_connection* conn, const struct mg_request_info* request_info)
{
#ifndef _WIN32

    // The DELETE method removes an existing package
    if (strcasecmp(request_info->request_method, "delete") == 0)
    {
        if (!Media::removeMediaPackage())
        {
            sendInternalErrorReply(conn);
        }
        else
        {
            sendOkReply(conn);
        }
        return true;
    }

    // Assume all other methods are for installing a new package

    handleExpectContinue(conn);

    // Create a temp file to store the payload
    const std::auto_ptr<char> tmpFilePath(new char[32]);
    strcpy(tmpFilePath.get(), "/tmp/media_XXXXXX");
    std::ofstream outfile;
    FileSystem::createTempFile(tmpFilePath.get(), outfile);
    if (outfile.fail())
    {
        LOG_ERROR("HTTP in: Unable to open file for media content");
        DapReader::instance()->setStatus(IStatus::Yellow, "Unable to create media file");
        return false;
    }

    std::vector<char> payload;
    payload.reserve(1024);

    // Write out request content to temp file
    int bytesRead = 0;
    while ( (bytesRead = mg_read(conn, &payload.front(), 1024)) > 0)
    {
        outfile.write(&payload.front(), bytesRead);
    }
    outfile.close();

    bool result = Media::installMediaPackage(tmpFilePath.get());
    remove(tmpFilePath.get());
    if (!result)
    {
        LOG_ERROR("HTTP in: Install media command failed");
        DapReader::instance()->setStatus(IStatus::Yellow, "Install media command failed");
        sendInternalErrorReply(conn);
        return true;
    }

    // Set new hash if specified
    Media::setMediaHash(mg_get_header(conn, "X-Media-Hash"));

    // Cached effects are no longer valid, so clear them.
    LightEffects::instance()->clearScriptCache();

    // Update xBio script set
    BiometricReader::instance()->updateScriptSet();

    sendOkReply(conn);
#else
    mg_printf(conn, "HTTP/1.1 501 Not implemented\r\n\r\n");
    mg_printf(conn, "Upgrade service not available on Windows build\r\n");
#endif

    return true;
}


static bool doMediaInventory(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (strcasecmp(request_info->request_method, "get") != 0)
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    Json::Value inv;
    Media::getMediaInventory(inv);

    Json::StyledWriter writer;
    std::string output = writer.write(inv);

    sendOkReply(conn, output);
    return true;
}


static bool doBrightness(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;

    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        json["brightness"] = LightEffects::instance()->getBrightness();
        sendJsonReply(conn, json);
    }
    else
    {
        readJsonPayload(conn, json);

        int brightness = getParam("value", request_info, &json, -1);
        if (brightness < 0)
        {
            sendBadRequestReply(conn, "Bad 'value' parameter\r\n");
        }
        else
        {
            LightEffects::instance()->setBrightness(brightness);
            sendOkReply(conn);
        
        }
    }

    return true;
}


static bool doCalibration(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    double r_factor, r_offset, g_factor, g_offset, b_factor, b_offset;
    Json::Value json;

    readJsonPayload(conn, json);
    std::string value = getParam("value", request_info, &json, "");

    const char *s = value.c_str();;
    r_factor = atof(s);
    while (*s && *s != ',') ++s;
    r_offset = atof(++s);
    while (*s && *s != ',') ++s;
    g_factor = atof(++s);
    while (*s && *s != ',') ++s;
    g_offset = atof(++s);
    while (*s && *s != ',') ++s;
    b_factor = atof(++s);
    while (*s && *s != ',') ++s;
    b_offset = atof(++s);

    if (r_factor &&
        g_factor &&
        b_factor)
    {
        LightEffects::instance()->setColorCalibration(r_factor, r_offset,
                                                      g_factor, g_offset,
                                                      b_factor, b_offset);
        sendOkReply(conn );
    }
    else
        sendBadRequestReply(conn, "Bad 'value' parameter\r\n");
    
    return true;
}


static bool showColor(const char* name, int timeout)
{
    if (isdigit(name[0]))
    {
        const char* s = name;
        unsigned red = atoi(s);
        while (isdigit(*s)) ++s;
        while (*s && !isdigit(*s))  ++s;
        unsigned green = atoi(s);
        while (isdigit(*s)) ++s;
        while (*s && !isdigit(*s))  ++s;
        unsigned blue = atoi(s);
        LightEffects::instance()->showColor(red, green, blue, timeout);
    }
    else if (!LightEffects::instance()->showColor(name, timeout))
    {
        return false;
    }

    return true;
}


static bool doColor(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    readJsonPayload(conn, json);

    int timeout = getParam("timeout", request_info, &json, 0);
    if (timeout < 0) timeout = 0;

    std::string name = getParam("name", request_info, &json, "");
    if (!name.empty())
    {
        if (!showColor(name.c_str(), timeout))
        {
            sendBadRequestReply(conn, NULL);
            return true;
        }
    }
    else
    {
        sendBadRequestReply(conn, "Missing 'name' parameter\r\n");
        return true;
    }

    sendOkReply(conn);
    return true;
}


static bool doSequence(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    std::string name;

    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        LightEffects::instance()->getCurrentEffectName(name);
        json["name"] = name;
        sendJsonReply(conn, json);
        return true;
    }

    readJsonPayload(conn, json);

    int timeout = getParam("timeout", request_info, &json, 0);
    if (timeout < 0) timeout = 0;

    name = getParam("name", request_info, &json, "");
    if (!name.empty())
    {
        if (!LightEffects::instance()->show(name.c_str(), timeout))
        {
            // If there is no sequence named "off", then "off" will turn things off
            if (strcasecmp(name.c_str(), "off") == 0)
                showColor(name.c_str(), timeout);
            else
            {
                sendBadRequestReply(conn, NULL);
                return true;
            }
        }
    }
    else
    {
        sendBadRequestReply(conn, "Missing 'name' parameter\r\n");
        return true;
    }

    sendOkReply(conn);
    return true;
}


static bool doMediaOff(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    LightEffects::instance()->off();
    sendOkReply(conn);
    return true;
}


static bool doLight(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    readJsonPayload(conn, json);

    int timeout = getParam("timeout", request_info, &json, 0);
    if (timeout < 0)    timeout = 0;

    std::string color = getParam("color", request_info, &json, "");
    std::string sequence = getParam("sequence", request_info, &json, "");

    if (!color.empty())
    {
        if (!showColor(color.c_str(), timeout))
        {
            sendBadRequestReply(conn, NULL);
            return true;
        }
    }
    else if (!sequence.empty())
    {
        if (!LightEffects::instance()->show(sequence.c_str(), timeout))
        {
            sendBadRequestReply(conn, NULL);
            return true;
        }
    }
    else
    {
        sendBadRequestReply(conn, "Missing 'color' or 'sequence' parameter\r\n");
        return true;
    }

    sendOkReply(conn);
    return true;
}


static bool doLog(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);
    sendFileContents(conn, "/var/log/reader.log", "text/plain");
    return true;
}


static bool doSound(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    readJsonPayload(conn, json);

    std::string name = getParam("name", request_info, &json, "");
    if (!name.empty())
    {
        Sound::instance()->play(name.c_str());
    }
    else
    {
        sendBadRequestReply(conn, "Missing 'name' parameter\r\n");
        return true;
    }

    sendOkReply(conn);
    return true;
}


/**
    Play one of the pre-loaded xbio lighting script
*/
static bool doBioSequence(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }


    Json::Value json;
    readJsonPayload(conn, json);

    std::string name = getParam("name", request_info, &json, "");
    if (!name.empty())
    {
        if (strcasecmp(name.c_str(), "off") == 0)
        {
            BiometricReader::instance()->lightOff();
        }
        else if (!BiometricReader::instance()->playScript(name.c_str()))
        {
            sendBadRequestReply(conn, NULL);
            return true;
        }
    }
    else
    {
        sendBadRequestReply(conn, "Missing 'name' parameter\r\n");
        return true;
    }

    sendOkReply(conn);
    return true;
}



/**
    Get or set the xbio lighting scripts
*/
static bool doBioScriptSet(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    static const unsigned MAX_SCRIPT_SIZE = 1024*1024;

    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        std::auto_ptr<char> buffer(new char[MAX_SCRIPT_SIZE]); // 1MB is max size

        unsigned size = BiometricReader::instance()->readScriptSet(buffer.get());
        if (!size)
        {
            LOG_WARN("Unable to read script set from xBio device");
            sendInternalErrorReply(conn);
            return true;
        }

        // Don't rely on xbio to provide the null terminator
        if (size >= MAX_SCRIPT_SIZE)
            size = MAX_SCRIPT_SIZE - 1;
        buffer.get()[size] = 0;

        sendOkReply(conn, buffer.get());
    }
    else
    {
        size_t length = getContentLength(conn);
        if (!length)
        {
            sendBadRequestReply(conn, "Missing or zero Content-Length\r\n");
            return true;
        }

        std::auto_ptr<char> buffer(new char[length]);

        int bytesRead = mg_read(conn, buffer.get(), length);
        if (bytesRead != (int)length)
        {
            LOG_ERROR("HTTP in: payload length (%d) != Content-Length (%u)", bytesRead, length);
            sendBadRequestReply(conn, "Bad payload size");
            return true;
        }

        if (!BiometricReader::instance()->writeScriptSet(buffer.get(), length))
        {
            LOG_WARN("Unable to write script set to xBio device");
            sendInternalErrorReply(conn);
            return true;
        }

        BiometricReader::instance()->restart();

        sendOkReply(conn);
    }
    
    return true;
}


static bool doSetIdleSequence(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (strcasecmp(request_info->request_method, "delete") == 0)
    {
        Media::setIdleEffectName("");
        LightEffects::instance()->off();
        sendOkReply(conn);
        return true;
    }

    Json::Value json;
    readJsonPayload(conn, json);
    std::string name = getParam("name", request_info, &json, "");
    if (name.empty())
    {
        sendBadRequestReply(conn, "Missing 'name' parameter\r\n");
        return true;
    }
    if (!LightEffects::instance()->isValidLightEffect(name.c_str()))
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    Media::setIdleEffectName(name.c_str());
    LightEffects::instance()->showIdleEffect();

    sendOkReply(conn);
    return true;
}


/**
    Set the tap options
*/
static bool doTapOptions(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        Json::Value value;
        value["color"] = RFIDReader::instance()->getTapLight();
        value["timeout"] = RFIDReader::instance()->getTapLightTimeout();
        value["sound"] = RFIDReader::instance()->getTapSound();
        sendJsonReply(conn, value);
    }
    else if (strcasecmp(request_info->request_method, "delete") == 0)
    {
        RFIDReader::instance()->setTapLight("");
        RFIDReader::instance()->setTapLightTimeout(0);
        RFIDReader::instance()->setTapSound("");
        sendOkReply(conn);
    }
    else
    {
        bool success = false;
        Json::Value json;
        readJsonPayload(conn, json);

        // light timeout
        int timeout = getParam("timeout", request_info, &json, -1);
        if (timeout >= 0)
        {
            RFIDReader::instance()->setTapLightTimeout(timeout);
            success = true;
        }

        // light color or sequence
        std::string color = getParam("color", request_info, &json, "");
        if (color.empty())
            color = getParam("sequence", request_info, &json, "");

        if (!color.empty())
        {
            int light = LightEffects::instance()->isValidLightEffect(color.c_str());

            if (light >= 0)
            {
                RFIDReader::instance()->setTapLight(color);
                success = true;
            }
        }

        // sound
        std::string sound = getParam("sound", request_info, &json, "");
        if (!sound.empty())
        {
            RFIDReader::instance()->setTapSound(sound.c_str());
            success = true;
        }

        if (success)
        {
            sendOkReply(conn);
        }
        else
        {
            sendBadRequestReply(conn, "Missing or bad parameter\r\n");
        }
    }
    return true;
}


/**
    Set the RFID reader options
*/
static bool doRfidOptions(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        Json::Value value;
        value["test_loop"] = RFIDReader::instance()->getTestMode() ? "on" : "off";
        value["public_id"] = RFIDReader::instance()->isPublicIdEnabled() ? "on" : "off";
        value["secure_id"] = RFIDReader::instance()->isSecureIdEnabled() ? "on" : "off";
        sendJsonReply(conn, value);
    }
    else
    {
        Json::Value json;
        readJsonPayload(conn, json);

        std::string mode = getParam("test_loop", request_info, &json, "");
        if (mode.size() > 0)
        {
            if (strcasecmp(mode.c_str(), "on") == 0)
                RFIDReader::instance()->setTestMode(true);
            else if (strcasecmp(mode.c_str(), "off") == 0)
                RFIDReader::instance()->setTestMode(false);
            else
            {
                sendBadRequestReply(conn, "'test_loop' value must be 'on' or 'off'\r\n");
                return true;
            }
        }

        mode = getParam("secure_id", request_info, &json, "");
        if (mode.size() > 0)
        {
            if (strcasecmp(mode.c_str(), "on") == 0)
                RFIDReader::instance()->enableSecureId(true);
            else if (strcasecmp(mode.c_str(), "off") == 0)
                RFIDReader::instance()->enableSecureId(false);
            else
            {
                sendBadRequestReply(conn, "'secure_id' value must be 'on' or 'off'\r\n");
                return true;
            }
        }

        mode = getParam("public_id", request_info, &json, "");
        if (mode.size() > 0)
        {
            if (strcasecmp(mode.c_str(), "on") == 0)
                RFIDReader::instance()->enablePublicId(true);
            else if (strcasecmp(mode.c_str(), "off") == 0)
                RFIDReader::instance()->enablePublicId(false);
            else
            {
                sendBadRequestReply(conn, "'public_id' value must be 'on' or 'off'\r\n");
                return true;
            }
        }

        sendOkReply(conn);
    }
    return true;
}


/**
    Put a tap event into the event queue.
    Intended for testing purposes.

    This command is verb agnostic, that is it works with PUT, POST or GET at the request
    of the test team because they are using a tool that wanst to send GETs.
*/
static bool doRfidTap(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    readJsonPayload(conn, json);

    std::string uid = getParam("uid", request_info, &json, "");
    if (uid.empty())
    {
        sendBadRequestReply(conn, "Missing 'uid' parameter\r\n");
        return true;
    }

    std::string pid = getParam("pid", request_info, &json, "");
    if (pid.empty())
    {
        sendBadRequestReply(conn, "Missing 'pid' parameter\r\n");
        return true;
    }

    std::string sid = getParam("sid", request_info, &json, "");
    if (RFIDReader::instance()->isSecureIdEnabled() && sid.empty())
    {
        sendBadRequestReply(conn, "Missing 'sid' parameter\r\n");
        return true;
    }

    std::string iin = getParam("iin", request_info, &json, "");

    Json::Value ev;
    ev["type"] = "RFID";
    ev["uid"] = uid;
    ev["pid"] = pid;
    if (RFIDReader::instance()->isSecureIdEnabled())
    {
        if (sid.size() > 0)
            ev["sid"] = sid;
        if (iin.size() > 0)
            ev["iin"] = iin;
    }

    EventLogger::instance()->postEvent(ev);

    sendOkReply(conn);
    return true;
}


static bool doCancel(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    LOG_DEBUG("WebServer doCancel()");
    BiometricReader::instance()->cancelRead();
    LightEffects::instance()->off();
    sendOkReply(conn);
    return true;
}


static bool doEnroll(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    BiometricReader::instance()->startEnroll();
    sendOkReply(conn);
    return true;
}


static bool doMatch(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    Json::Value payload;
    readJsonPayload(conn, payload);
    std::vector<std::vector<uint8_t> > templates;

    if (payload.isMember("templates") && !payload["templates"].empty())
    {
        Json::Value jsonTemplates = payload["templates"];
        for (int i = 0; jsonTemplates.isValidIndex(i); ++i)
        {
            if (jsonTemplates[i].isString())
            {
                std::vector<uint8_t> t = base64_decode(jsonTemplates[i].asString());
                templates.push_back(t);
            }
        }
    }

    LightEffects::instance()->show("entry_start_scan", 0);
    BiometricReader::instance()->startMatch();
    sendOkReply(conn);
    return true;
}


/**
    Set the biometric reader options.
*/
static bool doBiometricOptions(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        Json::Value value;
        value["image_capture"] = BiometricReader::instance()->getTestMode() ? "on" : "off";
        sendJsonReply(conn, value);
    }
    else
    {
        Json::Value json;
        readJsonPayload(conn, json);

        std::string mode = getParam("image_capture", request_info, &json, "off");
        if (strcasecmp(mode.c_str(), "on") == 0)
            BiometricReader::instance()->setTestMode(true);
        else
            BiometricReader::instance()->setTestMode(false);

        sendOkReply(conn);
    }
    return true;
}


/**
    Get the diagnostics info
*/
static bool doDiagnostics(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    Json::Value value;
    DapReader::instance()->getDiagnostics(value);
    sendJsonReply(conn, value);
    return true;
}


static bool doBioImage(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    const uint8_t* image;
    unsigned int imageSize;

    BiometricReader::instance()->getImage(&image, &imageSize);

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    mg_printf(conn, "Content-Type: application/octet-stream\r\n");
    mg_printf(conn, "Content-Lenth: %d\r\n\r\n", imageSize);
    mg_write(conn, image, imageSize);
    return true;
}


/**
    ?RFID
    ?xbio-image-id
*/
static bool doBioImageSend(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    // Grab all of the parameters and return them as json items in the event
    std::vector<std::string> keys;
    std::vector<std::string> values;
    char* s = request_info->query_string;
    if (s)
        while (*s)
        {
            char* start = s;
            while (*s && *s != '=') ++s;
            if (*s == '=')
            {
                keys.push_back(std::string(start, s - start));
                ++s;
                start = s;
                while (*s && *s != '&') ++s;
                values.push_back(std::string(start, s - start));
                if (*s)    ++s;
            }
        }

    const uint8_t* image;
    unsigned int imageSize;

    BiometricReader::instance()->getImage(&image, &imageSize);

    // Don't create these on the stack because they can be real big
    // TODO - we can eliminate some of the copies to optimize this.  For example,
    // we could have a call to EventLogger to get an event reference to populate
    static Json::Value ev;
    static char imageBuf[3 * 1024 * 1024];

    base64_encode(image, imageSize, imageBuf);
    ev["xbio-images"] = imageBuf;
    ev["type"] = "bio-image";
    
    for (int i = 0; i < (int)keys.size(); ++i)
        ev[keys[i]] = values[i];

    EventLogger::instance()->postEvent(ev);

    sendOkReply(conn);
    return true;
}


static bool doBioFirmware(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (!BiometricReader::instance()->isPresent())
    {
        sendBadRequestReply(conn, NULL);
        return true;
    }

    size_t length = getContentLength(conn);
    if (!length)
    {
        sendBadRequestReply(conn, "Missing or zero Content-Length\r\n");
        return true;
    }

    uint8_t* image = (uint8_t*)alloca(length);
    if (!image)
    {
        LOG_ERROR("HTTP in: Unable to allocate memory for firmware image");
        sendInternalErrorReply(conn);
        return true;
    }

    int bytesRead = mg_read(conn, image, length);
    if (bytesRead != (int)length)
    {
        LOG_ERROR("HTTP in: payload length (%d) != Content-Length (%u)", bytesRead, length);
        sendBadRequestReply(conn, "Bad payload size");
        return true;
    }

    BiometricReader::instance()->startFirmwareUpdate(image, bytesRead);

    sendOkReply(conn);
    return true;
}


static bool doTestStart(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (testThread.stopped())
        testThread.start();

    sendOkReply(conn);
    return true;

}


static bool doTestStop(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    testThread.stop();

    sendOkReply(conn);
    return true;

}


static bool doSystemReset(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

#ifndef _WIN32
    if (fork() != 0)
    {
        sendOkReply(conn);
        LOG_INFO("Reboot command received...\n");
        return true;
    }
    else
    {
        sleepMilliseconds(2000);
        system("reboot");
        exit(0);    // should never get here
    }
#else
    mg_printf(conn, "HTTP/1.1 501 Not implemented\r\n\r\n");
    mg_printf(conn, "Reboot service not available on Windows build\r\n");
    return true;
#endif
}


static bool doInstall(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    LOG_INFO("Install command received...\n");

#ifndef _WIN32
    Json::Value json;
    readJsonPayload(conn, json);
    std::string url = getParam("url", request_info, &json, "");
    if (url.empty())
    {
        sendBadRequestReply(conn, "Missing 'url' parameter\r\n");
        return true;
    }

    UpgradeService service(conn);
    service.runInstall(url);

#else
    mg_printf(conn, "HTTP/1.1 501 Not implemented\r\n\r\n");
    mg_printf(conn, "Upgrade service not available on Windows build\r\n");
#endif

    return true;
}


static bool doUpgrade(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    LOG_INFO("Upgrade command received...\n");

#ifndef _WIN32
    Json::Value json;
    if (!readJsonPayload(conn, json))
    {
        sendBadRequestReply(conn, "Invalid repository data\n");
        return true;
    }

    UpgradeService service(conn);
    service.runUpgrade(json);

#else
    mg_printf(conn, "HTTP/1.1 501 Not implemented\r\n\r\n");
    mg_printf(conn, "Upgrade service not available on Windows build\r\n");
#endif

    return true;
}


static bool doUpdateXbrcUrl(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    if (strcasecmp(request_info->request_method, "delete") == 0)
    {
        LOG_DEBUG("Received xBRC delete request");
        DapConfig::instance()->clearXbrcUrl();
    }
    else
    {
        LOG_DEBUG("Received xBRC update request");

        Json::Value json;
        readJsonPayload(conn, json);

        // Retrieve and validate that we were given a correctly formated url
        std::string url = getParam("url", request_info, &json, "");
        if (url.empty() ||
            ((url.find("http://") != 0) && (url.find("https://") != 0)))
        {
            LOG_WARN("Received invalid xBRC URL parameter");
            sendBadRequestReply(conn, NULL);
            return true;
        }

        DapConfig::instance()->setXbrcUrl(url.c_str());
    }

    sendOkReply(conn);
    return true;
}


struct ServiceInfo {
    const char* uri;
    bool (*callback)(struct mg_connection* conn, const struct mg_request_info* request_info);
};


static const ServiceInfo services[] = {
    {"application/reset", doAppReset},
    {"biometric/cancel", doCancel},
    {"biometric/enroll", doEnroll},
    {"biometric/firmware", doBioFirmware},
    {"biometric/image/send", doBioImageSend},
    {"biometric/image", doBioImage},
    {"biometric/match", doMatch},
    {"biometric/options", doBiometricOptions},
    {"diagnostics", doDiagnostics},
    {"events", doEvents},
    {"events/info", doEvents},
    {"light", doLight},
    {"media/brightness", doBrightness},
    {"media/calibration", doCalibration},
    {"media/color", doColor},
    {"media/idle", doSetIdleSequence},
    {"media/inventory", doMediaInventory},
    {"media/ledscript", doLedScript},
    {"media/off", doMediaOff},
    {"media/package", doInstallMediaPackage},
    {"media/sequence", doSequence},
    {"media/sound", doSound},
    {"media/xbioscripts", doBioScriptSet},
    {"media/xbio", doBioSequence},
    {"reader/info", doInfo},
    {"reader/info.json", doInfo},
    {"reader/log", doLog },
    {"reader/name", doName},
    {"rfid/options", doRfidOptions},
    {"rfid/tap", doRfidTap},
    {"system/reboot", doSystemReset},
    {"system/reset", doSystemReset},
    {"tap/options", doTapOptions},
    {"test/start", doTestStart},
    {"test/stop", doTestStop},
    {"time.json", doTime},
    {"time", doTime},
    {"install", doInstall},
    {"upgrade", doUpgrade},
    {"update_stream", doUpdateStream},
    {"xbrc", doUpdateXbrcUrl},
};


static void *callback(enum mg_event event, struct mg_connection *conn, const struct mg_request_info *request_info) 
{
	if (event == MG_NEW_REQUEST) 
    {
        LOG_INFO("HTTP <-- %s\t%s %s\n", request_info->request_method, request_info->uri, request_info->query_string);

        char* uri = request_info->uri;
        while (*uri == '/')    ++uri;
        for (unsigned i = 0; i < ArrayLength(services); ++i)
        {
            try
            {
                if (startsWith(uri, services[i].uri))
                    return (services[i].callback(conn, request_info) ? PROCESSED : NULL);
            }
            catch(std::exception& e)
            {
                // This shouldn't happen - If it does then try to recover and give the
                // client some indication that something bad has happened.
                LOG_ERROR("Exception: %s", e.what());
                mg_printf(conn, "HTTP/1.1 500 Internal Server Error\r\n\r\n");
                return PROCESSED;
            }
        }

        if (DapConfig::instance()->getValue("allow webpages", false))
        {
            // Pass the request to the default mongoose handler
            return NULL;
        }
        else
        {
            // Echo requested URI back to the client
            LOG_WARN("HTTP --> 404\t%s", request_info->uri);
            mg_printf(conn, "HTTP/1.1 404 Not Found\r\n");
            mg_printf(conn, "Content-Type: text/plain\r\n\r\n");
            mg_printf(conn, "No such service. (%s)\r\n", request_info->uri);
            return PROCESSED;
        }
    }
    else if (event == MG_EVENT_LOG)
    {
        LOG_WARN("%s\n", request_info->log_message);
        return PROCESSED;
    }

    return NULL;
}


WebServer::WebServer() :
    listening_port(0), _context(NULL)
{
}


WebServer::~WebServer()
{
    stop();
}


WebServer* WebServer::instance()
{
    static WebServer _instance;
    return &_instance;
}

/**
 *  Takes a mongoose listening_port specification (i.e. '8080, 8090s') and saves
 *  the integer port number. Only the first port will be considered.
 */
void WebServer::parsePortSpecification(std::string spec)
{
    // Only consider first port
    size_t pos = spec.find(",");
    if (pos != std::string::npos)
        spec.erase(pos);
    
    // Strip out SLL qualifier if present
    if (spec[spec.size()-1] == 's')
        spec.erase(spec.size()-1);
    
    // Convert to int and save
    listening_port = atoi(spec.c_str());
    if (listening_port == 0)
        LOG_ERROR("Unable to parse port specification");
    else
        LOG_INFO("Web server port is %d", listening_port);
}


/**
 *  Gets the webserver options from the config file and returns a pointer
 *  to the options array.
 */
const char** WebServer::getConfigOptions()
{
    // These arrays hold the mongoose webserver options
    static char opts[max_option_fields][max_option_size];
    static char *options[max_option_fields] = { 
        opts[0], opts[1], opts[2], opts[3], opts[4], 
        opts[5], opts[6], opts[7], opts[8], opts[9], 
        opts[10], opts[11], NULL
    };

    uint32_t iOpt = 0;
    uint32_t cL = 0;

    Json::Value opt = DapConfig::instance()->getJsonValue("webserverOptions");

    // Confirm/Set default webserverOptions
    if (!opt.isMember("listening_ports"))
    {
        opt["listening_ports"] = DEFAULT_WEBSERVER_PORT;
    }
    if (DapConfig::instance()->getValue("allow webpages", false) && !opt.isMember("document_root"))
    {
        opt["document_root"] = FileSystem::getWWWPath();
    }
#ifndef _WIN32
    if (!opt.isMember("ssl_certificate"))
    {
        opt["ssl_certificate"] = SSL_CERTIFICATE_PATH;
    }
#endif

    Json::Value::Members list = opt.getMemberNames();

    while (cL < list.size())  // Iterate field names
    {
        if(iOpt >= max_option_fields-1)
        {
            LOG_WARN("Webserver options exceed defined storage");
            break;
        }

        // copy name to array
        strncpy(opts[iOpt], list[cL].c_str(), sizeof(opts[iOpt]));
        Json::Value val = opt[list[cL]];        // the field value

        if (strcasecmp(opts[iOpt], "listening_ports") == 0)
            parsePortSpecification(val.asCString());

        // copy value as a string into array
        if     (val.isString())  snprintf(opts[iOpt+1], sizeof(opts[iOpt+1]), "%s", val.asString().c_str() );
        else if(val.isBool())    sprintf(opts[iOpt+1], "%d", val.asBool() );
        else if(val.isInt())     sprintf(opts[iOpt+1], "%d", val.asInt() );
        else if(val.isUInt())    sprintf(opts[iOpt+1], "%u", val.asUInt() );
        else if(val.isDouble())  sprintf(opts[iOpt+1], "%f", val.asDouble() );
        else                     sprintf(opts[iOpt+1], "type??");

        LOG_INFO("Added webserver option '%s' -> '%s'", opts[iOpt], opts[iOpt+1]);

        cL++;
        iOpt += 2;
    }
    
    // notify user of array this is the end.
    options[iOpt] = NULL;

    return (const char**) options;
}


bool WebServer::start() 
{
	_context = mg_start(&callback, NULL, getConfigOptions());

    if (_context == 0)
    {
        LOG_ERROR("Webserver failed to start");
        DapReader::instance()->setStatus(IStatus::Red, "Unable to start web server");
        listening_port = 0;
    }

    return (_context != 0);
}


void WebServer::stop()
{
    if (_context)
    {
        mg_stop(_context);
        _context = NULL;
    }
    listening_port = 0;
}
