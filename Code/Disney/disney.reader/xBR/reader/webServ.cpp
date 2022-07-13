//*****************************************************************************
// webServ.cpp 
//
//    http server task for xBand Reader. Provides handlers for mongoose events. 
//*****************************************************************************
//
//    Written by Mike Wilson 
//    Copyright 2011, Synapse.com
//
//*****************************************************************************

#include <cstring>
#include <cstdio>
#include <unistd.h>
#include "grover.h"
#include "config.h"
#include "log.h"
#include "mongoose.h"
#include "FileSystem.h"
#include "webServ.h"
#include "json/json.h"


using namespace Reader;


namespace pages
{
    extern void *get_events         (MG_CONN conn, MG_INFO info);
    extern void *put_events         (MG_CONN conn, MG_INFO info);
    extern void *delete_events      (MG_CONN conn, MG_INFO info);
    extern void *put_reader_name    (MG_CONN conn, MG_INFO info);
    extern void *get_reader_info    (MG_CONN conn, MG_INFO info);
    extern void *put_stream         (MG_CONN conn, MG_INFO info);
    extern void *delete_stream      (MG_CONN conn, MG_INFO info);
    extern void *put_app_reset      (MG_CONN conn, MG_INFO info);
    extern void *put_sys_reset      (MG_CONN conn, MG_INFO info);
    extern void *put_install        (MG_CONN conn ,MG_INFO info);
    extern void *put_upgrade        (MG_CONN conn, MG_INFO info);
    extern void *put_reader_time    (MG_CONN conn, MG_INFO info);
    extern void *get_time_json      (MG_CONN conn, MG_INFO info);
    extern void *put_xband_cmds     (MG_CONN conn, MG_INFO info);
    extern void *get_xband_cmds     (MG_CONN conn, MG_INFO info);
    extern void *delete_xband_cmds  (MG_CONN conn, MG_INFO info);
    extern void *put_xband_beacon   (MG_CONN conn, MG_INFO info);
    extern void *delete_xband_beacon(MG_CONN conn, MG_INFO info);
    extern void *get_xband_beacon   (MG_CONN conn, MG_INFO info);
    extern void *delete_xband_output(MG_CONN conn, MG_INFO info);
    extern void *put_xband_options  (MG_CONN conn, MG_INFO info);
    extern void *get_xband_options  (MG_CONN conn, MG_INFO info);
    extern void *put_filter         (MG_CONN conn, MG_INFO info);
    extern void *get_filter         (MG_CONN conn, MG_INFO info);
    extern void *delete_filter      (MG_CONN conn, MG_INFO info);
    extern void *put_xbrc_url       (MG_CONN conn, MG_INFO info);
    extern void *delete_xbrc_url    (MG_CONN conn, MG_INFO info);
    extern void *get_diagnostics    (MG_CONN conn, MG_INFO info);
    extern void *get_reader_log     (MG_CONN conn, MG_INFO info);


const struct page_map
{
    const char *method;
    const char *addr;
    Page page;
} page_map[] = {
        { "GET",        "/events.json",         get_events },        \
        { "GET",        "/events",              get_events },        \
        { "PUT",        "/events",              put_events },        \
        { "DELETE",     "/events",              delete_events },    \
        { "PUT",        "/reader/name",         put_reader_name },    \
        { "GET",        "/reader/info.json",    get_reader_info },    \
        { "GET",        "/reader/info",         get_reader_info },    \
        { "GET",        "/reader/log",          get_reader_log }, \
        { "PUT",        "/update_stream",       put_stream },        \
        { "DELETE",     "/update_stream",       delete_stream },    \
        { "PUT",        "/application/reset",   put_app_reset },    \
        { "GET",        "/application/reset",   put_app_reset },    \
        { "PUT",        "/system/reset",        put_sys_reset },    \
        { "PUT",        "/install",             put_install },    \
        { "PUT",        "/upgrade",             put_upgrade },    \
        { "PUT",        "/time",                put_reader_time },    \
        { "GET",        "/time.json",           get_time_json },    \
        { "GET",        "/time",                get_time_json },    \
        { "PUT",        "/xband/commands",      put_xband_cmds },    \
        { "GET",        "/xband/commands",      get_xband_cmds },    \
        { "DELETE",     "/xband/commands",      delete_xband_cmds },\
        { "PUT",        "/xband/beacon",        put_xband_beacon }, \
        { "DELETE",     "/xband/beacon",        delete_xband_beacon }, \
        { "GET",        "/xband/beacon",        get_xband_beacon }, \
        { "DELETE",     "/xband/output",        delete_xband_output }, \
        { "PUT",        "/xband/options",       put_xband_options }, \
        { "GET",        "/xband/options",       get_xband_options }, \
        { "PUT",        "/filter",              put_filter },    \
        { "GET",        "/filter",              get_filter },    \
        { "GET",        "/filter.json",         get_filter },    \
        { "DELETE",     "/filter",              delete_filter }, \
        { "PUT",        "/xbrc",                put_xbrc_url }, \
        { "DELETE",     "/xbrc",                delete_xbrc_url }, \
        { "GET",        "/diagnostics",         get_diagnostics }, \
        { NULL, NULL, NULL }
    };


// New HTTP request has arrived from the client
void *request(MG_CONN conn,MG_INFO info)
{
    LOG_INFO("HTTP <-- %s\t%s %s", info->request_method, info->uri, info->query_string);

    // Treat POST and PUT the same
    std::string method = info->request_method;
    if (strcasecmp(method.c_str(), "POST") == 0)
        method = "PUT";

    // strip trailing slashes
    std::string uri = info->uri;
    while (uri[uri.size()-1] == '/')
        uri.erase(uri.size()-1);

    for (int i = 0; page_map[i].addr != NULL; i++)
    {
        if(strcasecmp(method.c_str(), page_map[i].method) != 0)
            continue;
        if(strcasecmp(uri.c_str(), page_map[i].addr) != 0)
            continue;
            
        try
        {
            return page_map[i].page(conn, info);
        }
        catch (std::exception& e)
        {
            // safety net deployed
            LOG_ERROR("Caught exception in HTTP callback: %s", e.what());
            mg_printf(conn, "HTTP/1.1 500 Internal Server Error\r\n\r\n");
            return PAGE_COMPLETE;
        }
    }

    if (Config::instance()->getValue("allow webpages", false))
    {
        // Pass request to the default mongoose handler
        return NULL;
    }
    else
    {
        // Echo requested URI back to the client
        LOG_WARN("HTTP --> 404\t%s", info->uri);
        mg_printf(conn, "HTTP/1.1 404 Not Found\r\n"
                        "Content-Type: text/plain\r\n\r\n"
                        "%s", info->uri);
        return PAGE_COMPLETE;  // Mark as processed
    }
}


// Mongoose logs an event, request_info.log_message
void *eventlog(MG_CONN conn ,MG_INFO info)
{
    UNUSED(conn);
    LOG_WARN("%s\n", info->log_message);
    return PAGE_COMPLETE;  // Mark as processed
}


// Mongoose initializes SSL. Instead of mg_connection *,
void *initssl(MG_CONN conn ,MG_INFO info)
{
    UNUSED(conn);
    UNUSED(info);
    LOG_WARN("mongoose initssl");
    return PAGE_COMPLETE;  // Mark as processed
}


// mongoose needs a function to call back, here it creates a POSIX
// message and sends it to a processing task.
void *callback(enum mg_event event,MG_CONN conn ,MG_INFO info) 
{
    switch(event)
    {
    case MG_NEW_REQUEST:
        return request(conn,info);   // New HTTP request has arrived from the client
    case MG_EVENT_LOG:
        return eventlog(conn,info);  // Mongoose logs an event, request_info.log_message
    case MG_INIT_SSL:
        return initssl(conn,info);   // Mongoose initializes SSL. Instead of mg_connection *,
    default:
        return NULL;
    }
}


void sendStringContent(MG_CONN conn, const char* body)
{
    mg_printf(conn, "Content-Length: %d\r\n\r\n", strlen(body));
    mg_printf(conn, body);
}


void sendStringReply(MG_CONN conn, const char* body)
{
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    mg_printf(conn, "Content-Type: text/plain\r\n");
    sendStringContent(conn, body);
}


void sendJsonReply(MG_CONN conn, std::string& json)
{
    mg_printf(conn, "HTTP/1.1 200 OK\r\n");
    mg_printf(conn, "Content-Type: application/json\r\n");
    sendStringContent(conn, json.c_str());
}


void sendJsonReply(MG_CONN conn, Json::Value& json)
{
    Json::StyledWriter writer;
    std::string s = writer.write(json);
    sendJsonReply(conn, s);
}


void sendBadRequestReply(MG_CONN conn, const char* msg)
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


void sendOkReply(MG_CONN conn)
{
    mg_printf(conn, "HTTP/1.1 200 OK\r\n\r\n");
}


} // namespace pages


namespace webServ
{
    struct mg_context *ctx;

    #define MAX_OPTIONS 6
    #define OPTFLDS ((MAX_OPTIONS*2)+1)

    #ifdef _WIN32
    #define SSL_CERTIFICATE_PATH "ssl.crt"
    #else
    #define SSL_CERTIFICATE_PATH "/usr/lib/ssl/server.pem"
    #endif

    #define DEFAULT_WEBSERVER_PORT "8080"

    char opts[OPTFLDS][40];
    char *options[OPTFLDS] =
    {    opts[0], opts[1], opts[2], opts[3], opts[4], 
        opts[5], opts[6], opts[7], opts[8], opts[9], 
        opts[10], opts[11], NULL
    };

    // Stores the main webserver listening port
    int listening_port;

    int getListeningPort()
    {
        return listening_port;
    }

    /**
     *    Takes a mongoose listening_port specification (i.e. '8080, 8090s') and saves
     *    the integer port number. Only the first port will be considered.
     */
    void parsePortSpecification(std::string spec)
    {
        // Only consider first port
        size_t pos = spec.find(",");
        if (pos != std::string::npos)
            spec.erase(pos);

        // Strip out SLL qualifier if present
        if (spec[spec.size()-1] == 's')
            spec.erase(spec.size()-1);

        // Convert to int
        listening_port = atoi(spec.c_str());
        if (listening_port == 0)
            LOG_ERROR("Unable to parse port specification");
        else
            LOG_INFO("Web server port is %d", listening_port);
    }

    /**
     *    Gets the webserver options from the config file.
     */
    void getConfigOptions()
    {
        Json::Value    opt = Config::instance()->getJsonValue("webserverOptions");    // the json object
        
        int iOpt = 0;
        uint32_t cL = 0;
        
        // Confirm/Set default webserverOptions
        if (!opt.isMember("listening_ports"))
        {
            opt["listening_ports"] = DEFAULT_WEBSERVER_PORT;
        }
        if (Config::instance()->getValue("allow webpages", false) && !opt.isMember("document_root"))
        {
            opt["document_root"] = FileSystem::getWWWPath();
        }
#if 0    // #ifndef_WIN32 - SSL not supported yet
        if (!opt.isMember("ssl_certificate"))
        {
            opt["ssl_certificate"] = SSL_CERTIFICATE_PATH;
        }
#endif

        Json::Value::Members list = opt.getMemberNames();

        while (cL < list.size())  // Iterate field names
        {
            if(iOpt >= OPTFLDS-1)
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
            if       (val.isString())    snprintf(opts[iOpt+1], sizeof(opts[iOpt+1]), "%s", val.asString().c_str() );
            else if(val.isBool())    sprintf(opts[iOpt+1], "%d", val.asBool() );
            else if(val.isInt())    sprintf(opts[iOpt+1], "%d", val.asInt() );
            else if(val.isUInt())    sprintf(opts[iOpt+1], "%u", val.asUInt() );
            else if(val.isDouble())    sprintf(opts[iOpt+1], "%f", val.asDouble() );
            else                    sprintf(opts[iOpt+1], "type??");

            LOG_INFO("Added webserver option '%s' -> '%s'", opts[iOpt], opts[iOpt+1]);

            cL++;
            iOpt += 2;
        }

        // notify user of array this is the end.
        options[iOpt] = NULL;
    }

    void start(void)
    {
        getConfigOptions();    // build options array from config file field

        ctx = mg_start(pages::callback, NULL, (const char**) options);
        if(ctx == NULL)
        {
            LOG_ERROR("Webserver failed to start");
            listening_port = 0;
        }
    }

    // force mongoose to close down.
    void stop(void)
    {
        fflush(stdout);
        if (ctx)
        {
            mg_stop(ctx);
            ctx = NULL;
        }
        listening_port = 0;
    }
    
    // called when system is expected to reset as a result of receiving an HTTP request
    // It actually does nothing, as we will just let the threads dies a natural death, 
    // which should allow the threads here to finish.
    void quit(void)
    {
        // This is a test of the Emergency Broadcast System. The broadcasters of 
        // your area in voluntary cooperation with federal, state and local authorities
        // have developed this system to keep you informed in the event of an emergency.
        ;
        // If this had been an actual emergency, you would have been 
        // instructed where to tune in your area for news and official information.
    }
} // namespace webServ

/*
cgi_extensions = Comma-separated list of CGI extensions. All files 
* having these extensions are treated as CGI scripts. 
* Default: ".cgi,.pl,.php"

cgi_environment = Extra environment variables to be passed to the 
* CGI script in addition to standard ones. The list must be comma-
* separated list of X=Y pairs, like this: 
* "VARIABLE1=VALUE1,VARIABLE2=VALUE2". Default: ""

put_delete_passwords_file = PUT and DELETE passwords file. This must 
* be specified if PUT or DELETE methods are used. Default: ""

cgi_interpreter = Use cgi_interpreter as a CGI interpreter for all 
* CGI scripts regardless script extension. Default: "". Mongoose decides 
* which interpreter to use by looking at the first line of a CGI script.

protect_uri = Comma separated list of URI=PATH pairs, specifying that 
* given URIs must be protected with respected password files. 
* Default: ""

authentication_domain = Authorization realm. Default: "mydomain.com"

ssi_extensions = Comma separated list of SSI extensions. Unknown SSI 
* directives are silently ignored. Currently, two SSI directives 
* supported, "include" and "exec". Default: "shtml,shtm"

access_log_file = Access log file. Default: "", no logging is done.

enable_directory_listing = Enable/disable directory listing. 
* Default: "yes"

error_log_file = Error log file. Default: "", no errors are logged.

global_passwords_file = Location of a global passwords file. If set, 
* per-directory .htpasswd files are ignored, and all requests must be 
* authorised against that file. Default: ""

index_files = Comma-separated list of files to be treated as directory 
* index files. Default: "index.html,index.htm,index.cgi"

access_control_list = Specify access control list (ACL). ACL is a 
* comma separated list of IP subnets, each subnet is prepended by '-' 
* or '+' sign. Plus means allow, minus means deny. If subnet mask is 
* omitted, like "-1.2.3.4", then it means single IP address. Mask may 
* vary from 0 to 32 inclusive. On each request, full list is traversed, 
* and last match wins. Default setting is to allow all. For example, to 
* allow only 192.168/16 subnet to connect, run 
* "mongoose -0.0.0.0/0,+192.168/16". Default: ""

extra_mime_types = Extra mime types to recognize, in form 
* "extension1=type1,exten- sion2=type2,...". Extension must include dot. 
* Example: "mongoose -m .cpp=plain/text,.java=plain/text". Default: ""

listening_ports = Comma-separated list of ports to listen on. If the 
* port is SSL, a letter 's' must be appeneded, for example, "-p 80,443s" 
* will open port 80 and port 443, and connections on port 443 will be 
* SSL-ed. It is possible to specify an IP address to bind to. In this 
* case, an IP address and a colon must be prepended to the port number. 
* For example, to bind to a loopback interface on port 80 and to all 
* interfaces on HTTPS port 443, use "mongoose -p 127.0.0.1:80,443s". 
* Default: "8080"

document_root = Location of the WWW root directory. A comma separated 
* list of URI_PREFIX=DIRECTORY pairs could be appended to it, allowing 
* Mongoose to serve from multiple directories. For example, 
* "mongoose -p /var/www,/config=/etc,/garbage=/tmp". Default: "."

ssl_certificate = Location of SSL certificate file. Default: ""

num_threads = Number of worker threads to start. Default: "10"

run_as_user = Switch to given user's credentials after startup. 
* Default: ""
*/
