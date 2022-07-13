/**
 *  @file   UpgradeService.h
 *  @date   April, 2012
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  Implements software istall/upgrade commands through web interface via opkg.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#ifndef _WIN32
#include <signal.h>
#endif

#include "json/json.h"
#include "mongoose.h"
#include "log.h"


namespace Reader
{

class UpgradeService
{
public:
    UpgradeService(struct mg_connection* conn);
    ~UpgradeService();

    /**
     *  Executes the install command using opkg. The json parameter should
     *  have a string value called "url" which provides the URL to pull the
     *  install package.
     *
     *  This command will pass the '--force-downgrade' switch into opkg
     *  which should allow older versions to be installed. This mechanism
     *  for downgrading software does not appear to work 100% of the time.
     */
    void runInstall(const std::string& url);

    /**
     *  Executes the update/upgrade command using opkg. The json parameter should
     *  have a string value called "repos" which provides an array of hash tables 
     *  specifying the name, priority, and location of the opkg feeds to use.
     *
     *  i.e.:
     *
     *  {
     *    "repos": [
     *      {
     *        "path": "repositories/base-0.0.2-0/all",
     *        "name": "all",
     *        "weight": "10"
     *      },
     *      {
     *        "path": "repositories/base-0.0.2-0/argv7a",
     *        "name": "armv7a",
     *        "weight": "30"
     *      },
     *      {
     *        "path": "repositories/base-0.0.2-0/overo",
     *        "name": "overo",
     *        "weight": "50"
     *      },
     *      {
     *        "path": "repositories/dap-reader-0.2.4-0",
     *        "name": "xBR",
     *        "weight": "90"
     *      }
     *    ]
     *  }
     *
     *  This manifest is used to create a series of configuration files
     *  used by opkg. These configurations files exist in the /etc/opkg
     *  directory and consist of:
     *
     *  1. The arch.conf file: Contains one line per entry in the manifest
     *     in the format - "arch <name> <weight>"
     *  2. One file per entry in the manifest called <name>.conf with a
     *     single line in the format - "src/gz <name> <path>"
     */
    void runUpgrade(const Json::Value& manifest);

private:
    struct mg_connection* conn;

    // Holds previous SIG_CHLD signal handler
    void (*prev_handler)(int);

    bool writeConfigFiles(const Json::Value& repos);
    bool doUpgrade();
    void doDowngrade();

    // Response code methods
    void sendOkResponse() { mg_printf(conn, "HTTP/1.1 200 OK\r\n"); }
    void sendBadRequestResponse() { mg_printf(conn, "HTTP/1.1 400 Bad Request\r\n"); }
    void sendErrorResponse() { mg_printf(conn, "HTTP/1.1 500 Internal Server Error\r\n"); }

    // HTTP response entity methods
    void sendStringContent(const char *body);
    void sendFileContent(const char *filename);

    bool remountFilesystem();
    void reboot();
};

}
