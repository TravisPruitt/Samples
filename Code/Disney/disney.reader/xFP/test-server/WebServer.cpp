#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <string>
#include <sstream>

#include "standard.h"
#include "WebServer.h"
#include "version.h"
#include "log.h"
#include "HttpRequest.h"
#include "json/json.h"
#include "globals.h"


#define ArrayLength(a)  (sizeof(a) / sizeof(a[0]))
#define UNUSED(a)    (void)a

using namespace Reader;


#define LISTENING_PORT  "8081"



// TODO - use inet_ntoa?
static std::string ipAsString(long ip)
{
    char buf[100];
    sprintf(buf, "%d.%d.%d.%d", (int)(ip >> 24) & 0xff, (int)(ip >> 16) & 0xff, (int)(ip >> 8) & 0xff, (int)ip & 0xff);
    return std::string(buf);
}




static void handleHello(long ip, std::string& payload)
{
    Json::Reader reader;
    Json::Value value;
    uint32_t port = 8080;   // default port for reader
    static bool readerAlive = false;

    if (!readerAlive)
    {
        readerAlive = true;

        reader.parse(payload, value);
        if (value["port"].type() == Json::uintValue)
            port = value["port"].asUInt();
        else if (value["port"].type() == Json::intValue)
            port = value["port"].asInt();

        std::stringstream url;
    
        // save the source IP:port address as the URL of the reader
        url << ipAsString(ip);
        url << ":" << port;
        g_readerUrl = url.str();

        // If we know our IP address, then set the reader up to stream data to us
        if (g_ipAddress.size() > 0)
        {
            url << "/update_stream";
            url << "?url=" << g_ipAddress << ":" << LISTENING_PORT << "/update";
            if (g_updateInterval > 0)
            {
                url << "&interval=";
                url << g_updateInterval;
            }
            if (g_maxEventsPerMsg > 0)
            {
                url << "&max=";
                url << g_maxEventsPerMsg;
            }
            if (g_minSignalStrength != 0)
            {
                url << "&min-ss=" << g_minSignalStrength;
            }

            std::string s = url.str();

            LOG_DEBUG("POST %s\n", s.c_str());
            HttpRequest* req = new HttpRequest("post", s.c_str());
            req->send();
            delete req;
        }
    }
}



static void readPayload(struct mg_connection* conn, std::string& payload)
{
    char buf[500];
    int bytesRead;

    while ( (bytesRead = mg_read(conn, buf, sizeof(buf)-1)) != 0)
    {
        buf[bytesRead] = 0;
        payload += buf;
    }

    if (g_verbose)
    {
        LOG_INFO("Payload:\n");
        logPuts(LOG_LEVEL_INFO, payload.c_str());
    }
}


static void *callback(enum mg_event event, struct mg_connection *conn, const struct mg_request_info *request_info) 
{   
    printf("callback with URI %s\n", request_info->uri);

	if (event == MG_NEW_REQUEST) 
    {
        LOG_INFO("New request\n");
        LOG_INFO("URI: '%s'\n", request_info->uri);
        LOG_INFO("request method = %s\n", request_info->request_method);
        LOG_INFO("query string = <%s>\n", request_info->query_string);
        LOG_INFO("ip = <%s>\n", ipAsString(request_info->remote_ip).c_str());

        for (int i = 0; request_info->http_headers[i].name; ++i)
        {
            LOG_INFO("Header: %s: %s\n", request_info->http_headers[i].name, request_info->http_headers[i].value);
        }

        std::string payload;
        readPayload(conn, payload);

        mg_printf(conn, "HTTP/1.1 200 OK\r\n");

        if (strcasecmp(request_info->uri, "/hello") == 0)
        {
            handleHello(request_info->remote_ip, payload);
        }

        return (void*)"";  // Mark as processed
    } 
    else 
    {
        return NULL;
    }
}


WebServer* WebServer::instance()
{
    static WebServer server;
    return &server;
}



WebServer::WebServer()
{
}

WebServer::~WebServer()
{
}



void WebServer::start() 
{
    printf("listening on port "LISTENING_PORT"\n");
    const char *options[] = {"listening_ports", LISTENING_PORT, NULL};
	_context = mg_start(&callback, NULL, options);
}


void WebServer::stop()
{
    mg_stop(_context);
}

