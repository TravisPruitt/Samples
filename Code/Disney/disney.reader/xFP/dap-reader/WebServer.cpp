#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <vector>
#ifndef _WIN32
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

#include "UpgradeService.h"


#ifdef _WIN32
#define SSL_CERTIFICATE_PATH "ssl.crt"
#else
#define SSL_CERTIFICATE_PATH "/usr/lib/ssl/server.pem"
#endif

#define DEFAULT_WEBSERVER_PORT "8080"

using namespace Reader;
using namespace RFID;

TestThread testThread;


static void sendStringContent(struct mg_connection* conn, const char* body)
{
    mg_printf(conn, "Content-Length: %d\r\n\r\n", strlen(body));
    mg_printf(conn, body);
}


static void sendJsonReply(struct mg_connection* conn, std::string& json)
{
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    mg_printf(conn, "Content-Type: application/json\r\n");
    sendStringContent(conn, json.c_str());
}


static void sendJsonReply(struct mg_connection* conn, Json::Value& json)
{
    Json::StyledWriter writer;
    std::string s = writer.write(json);
    sendJsonReply(conn, s);
}


static std::string getParam(const char* paramName, const struct mg_request_info* request_info, Json::Value* jsonPayload, const char* defaultValue)
{
    char buf[1000];

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
    char buf[100];

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
    size_t length = 0;
    const char* s = mg_get_header(conn, "Content-Length");
    if (s)
    {
        length = atoi(s);
    }

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



static bool doAmbientLight(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    Json::Value value;
    value["amb-light"] = Sensors::instance()->getLuminance();
    sendJsonReply(conn, value);
    return true;
}


extern void restart();
static bool doAppReset(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    LOG_INFO("application/reset command received...\n");
    restart();
    return true;
}


static bool doTemperature(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    Json::Value value;
    value["temp"] = Sensors::instance()->getTemperature();
    value["max temp"] = Sensors::instance()->getMaxTemperature();
    sendJsonReply(conn, value);
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

        std::string name = getParam("name", request_info, &json, "");
        if (!name.empty())
        {
            LOG_DEBUG("new reader name = <%s>\n", name.c_str());
            DapConfig::instance()->setValue("name", name);
            DapConfig::instance()->save();
        }

        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
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
            mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
            mg_printf(conn, "Missing time parameter\r\n");
            return true;
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
        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    }
    return true;
}




static bool doUpdateStream(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    readJsonPayload(conn, json);

    if (strcasecmp(request_info->request_method, "get") == 0)
    {
        mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n" "%s", EventLogger::instance()->getPushUrl().c_str());
    }
    else if (strcasecmp(request_info->request_method, "delete") == 0)
    {
        EventLogger::instance()->setPushUrl(NULL);

        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    }
    else
    {
        int interval = getParam("interval", request_info, &json, -1);
        int max = getParam("max", request_info, &json, -1);
        int after = getParam("after", request_info, &json, EventLogger::instance()->getNextEventNumberToPush()-1);
        std::string url = getParam("url", request_info, &json, "");
       
        LOG_DEBUG("Pushing events to '%s', interval %d, max %d, after %d\n", url.c_str(), interval, max, after);
        EventLogger::instance()->setPushParams(url.c_str(), interval, max, after);

        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
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
            mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
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
        mg_printf(conn, "HTTP/1.1 200 OK\r\n\r\n");
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



static bool doLight(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    Json::Value json;
    readJsonPayload(conn, json);

    int timeout = getParam("timeout", request_info, &json, 0);
    if (timeout < 0)    timeout = 0;

    std::string color = getParam("color", request_info, &json, "");
    if (color.empty())
        color = getParam("sequence", request_info, &json, "");

    if (!color.empty())
    {
        if (strcasecmp(color.c_str(), "off") == 0)
        {
            LightEffects::instance()->off();
        }
        else if (!LightEffects::instance()->show(color.c_str(), timeout))
        {
            LightEffects::instance()->off();
            mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
            mg_printf(conn, "Invalid 'color' or 'sequence' parameter\r\n");
            return true;
        }
    }
    else
    {
        mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
        mg_printf(conn, "Missing 'color' or 'sequence' parameter\r\n");
        return true;
    }

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
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
        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
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
            mg_printf(conn, "HTTP/1.1 200 OK\r\n");
        }
        else
        {
            mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
            mg_printf(conn, "Missing or bad parameter\r\n");
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
                mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
                mg_printf(conn, "'test_loop' value must be 'on' or 'off'\r\n");
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
                mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
                mg_printf(conn, "'secure_id' value must be 'on' or 'off'\r\n");
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
                mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
                mg_printf(conn, "'public_id' value must be 'on' or 'off'\r\n");
                return true;
            }
        }

        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
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
//    if (strcasecmp(request_info->request_method, "get") == 0)
//    {
//        mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
//        mg_printf(conn, "Did you mean PUT or POST?\r\n");
//        return true;
//    }

    Json::Value json;
    readJsonPayload(conn, json);

    std::string uid = getParam("uid", request_info, &json, "");
    if (uid.size() <= 0)
    {
        mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
        mg_printf(conn, "Missing 'uid' parameter\r\n");
        return true;
    }

    std::string pid = getParam("pid", request_info, &json, "");
    if (pid.size() <= 0)
    {
        mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
        mg_printf(conn, "Missing 'pid' parameter\r\n");
        return true;
    }

    std::string sid = getParam("sid", request_info, &json, "");
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

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;
}


static bool doCancel(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);
    LOG_DEBUG("WebServer doCancel()\n");
    BiometricReader::instance()->cancelRead();
    LightEffects::instance()->off();
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;
}


static bool doEnroll(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);
    LightEffects::instance()->show("entry_start_scan", 0);
    BiometricReader::instance()->startEnroll();
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;
}


static bool doMatch(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

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
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;
}


/**
    Set the biometric reader options.
*/
static bool doBiometricOptions(struct mg_connection* conn, const struct mg_request_info* request_info)
{
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

        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
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

    const uint8_t* image;
    unsigned int imageSize;

    BiometricReader::instance()->getImage(&image, &imageSize);

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    mg_printf(conn, "Content-Type: application/octet-stream\r\n");
    mg_printf(conn, "Content-Length: %d\r\n", imageSize);
    mg_printf(conn, "\r\n");
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

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;
}


static bool doBioFirmware(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    size_t length = 0;
    const char* s = mg_get_header(conn, "Content-Length");
    if (s)
        length = atoi(s);

    LOG_DEBUG("content lenth = %u\n", length);

    if (!length)
    {
        LOG_ERROR("HTTP in: missing or 0 Content-Length for firmware image\n");
        return true;
    }

    uint8_t* image = (uint8_t*)alloca(length);
    if (!image)
    {
        LOG_ERROR("HTTP in: Unable to allocate memory for firmawre image\n");
        return true;
    }

    int bytesRead = mg_read(conn, image, length);
    if (bytesRead != (int)length)
    {
        LOG_ERROR("HTTP in: payload length (%d) != Content-Length (%u)\n", bytesRead, length);
        return true;
    }

    BiometricReader::instance()->startFirmwareUpdate(image, bytesRead);

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;
}


static bool doTestStart(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    if (testThread.stopped())
        testThread.start();

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;

}



static bool doTestStop(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

    testThread.stop();

    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    return true;

}


static bool doSystemReset(struct mg_connection* conn, const struct mg_request_info* request_info)
{
    UNUSED(request_info);

#ifndef _WIN32
    if (fork() != 0)
    {
        mg_printf(conn, "HTTP/1.1 200 OK\r\n");
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
        mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
        mg_printf(conn, "Missing url parameter\r\n");
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
        mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
        mg_printf(conn, "Invalid repository data\r\n");
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
            mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n\r\n");
            return true;
        }

        DapConfig::instance()->setXbrcUrl(url.c_str());
    }

    mg_printf(conn, "HTTP/1.1 200 OK\r\n\r\n");
    return true;
}


struct ServiceInfo {
    const char* uri;
    bool (*callback)(struct mg_connection* conn, const struct mg_request_info* request_info);
};

static const ServiceInfo services[] = {
    {"amb_light", doAmbientLight},
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
    {"reader/info", doInfo},
    {"reader/info.json", doInfo},
    {"reader/name", doName},
    {"rfid/options", doRfidOptions},
    {"rfid/tap", doRfidTap},
    {"system/reboot", doSystemReset},
    {"system/reset", doSystemReset},
    {"tap/options", doTapOptions},
    {"temperature", doTemperature},
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
        LOG_DEBUG("<<HTTP %s %s %s\n", request_info->request_method, request_info->uri, request_info->query_string);

        char* uri = request_info->uri;
        while (*uri == '/')    ++uri;
        for (unsigned i = 0; i < ArrayLength(services); ++i)
        {
            try
            {
                if (startsWith(uri, services[i].uri))
                    return (services[i].callback(conn, request_info) ? (void*)"" : NULL);
            }
            catch (std::exception& e)
            {
                // This shouldn't happen - If it does then try to recover and give the
                // client some indication that something bad has happened.
                LOG_ERROR("Exception: %s", e.what());
                mg_printf(conn, "HTTP/1.1 500 Internal Server Error\r\n\r\n");
                return (void*)"";
            }
        }

        // Echo requested URI back to the client
        mg_printf(conn, "HTTP/1.1 404 Not Found\r\n");
        mg_printf(conn, "Content-Type: text/plain\r\n");
        mg_printf(conn, "\r\n");
        mg_printf(conn, "No such service. (%s)\r\n", request_info->uri);
        return (void*)"";  // Mark as processed
    }
    else if (event == MG_EVENT_LOG)
    {
        LOG_WARN("%s\n", request_info->log_message);
        return (void*)"";  // Mark as processed;
    }
    else 
    {
        return NULL;
    }
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

    Json::Value    opt = DapConfig::instance()->getJsonValue("webserverOptions");

    // Confirm/Set default webserverOptions
    if (!opt.isMember("listening_ports"))
    {
        opt["listening_ports"] = DEFAULT_WEBSERVER_PORT;
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
