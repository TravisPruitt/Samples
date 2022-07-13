/**
 *  @file   UpgradeService.h
 *  @date   April, 2012
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  Implements software istall/upgrade commands through web interface via opkg.
 */

#include <sys/stat.h>

#include "UpgradeService.h"
#include "Mutex.h"

using namespace Reader;


#define INSTALL_LOG "/var/log/install.log"
#define INSTALL_HISTORY "/var/log/install_history.log"
#define UPGRADE_LOG "/var/log/upgrade.log"
#define UPGRADE_HISTORY "/var/log/upgrade_history.log"


static Mutex mutex;
static bool upgrading = false;

/**
 *  Set the upgrading status to true if not already set
 *
 *  @returns true if status is changed and false if the
 *           status is already true.
 */
static bool enableUpgradingStatus()
{
    Lock lock(mutex);
    if (upgrading)
    {
        return false;
    }
    upgrading = true;
    return true;
}

/**
 *  Sets the upgrading status to false.
 */
static void disableUpgrading()
{
    Lock lock(mutex);
    upgrading = false;
}

/**
 *  Returns current upgrading status
 */
static bool currentUpgradingStatus()
{
    Lock lock(mutex);
    return upgrading;
}


UpgradeService::UpgradeService(struct mg_connection* connection)
    : conn(connection), prev_handler(SIG_DFL)
{
    if (currentUpgradingStatus() == false)
    {
        // Mongoose 3.1+ sets the SIGCHLD signal to SIG_IGN which
        // causes the system() function to not properly wait
        // for child processes. Restore it here in order for
        // the error handling code to function properly.
        prev_handler = signal(SIGCHLD, SIG_DFL);
    }
}


UpgradeService::~UpgradeService()
{
    if (prev_handler != SIG_DFL)
    {
        // Restore SIGCHLD handler
        signal(SIGCHLD, prev_handler);
    }
}


/**
 *  Executes the install command using opkg. The json parameter should
 *  have a string value called "url" which provides the URL to pull the
 *  install package.
 *
 *  This command will pass the '--force-downgrade' switch into opkg
 *  which should allow older versions to be installed. This mechanism
 *  for downgrading software does not appear to work 100% of the time.
 */
void UpgradeService::runInstall(const std::string& url)
{
    if(!enableUpgradingStatus())
    {
        sendErrorResponse();
        sendStringContent("Upgrade service is busy");
        return;
    }

    try
    {
        if (!remountFilesystem())
        {
            sendErrorResponse();
            sendStringContent("remount command failed");
            disableUpgrading();
            return;
        }

        std::string command = "opkg install --force-downgrade ";
        command += url + " 2>&1 > " INSTALL_LOG;

        int result = system(command.c_str());
        if (!WIFEXITED(result) || (WEXITSTATUS(result) != 0))
        {
            LOG_ERROR("Install command failed\n");
            sendErrorResponse();
        }
        else
        {
            LOG_INFO("Install successful. Rebooting...");
            reboot();
            sendOkResponse();
        }
        sendFileContent(INSTALL_LOG);

        // Copy and timestamp install log contents to history log
        system("date >> " INSTALL_HISTORY);
        system("cat " INSTALL_LOG " >> " INSTALL_HISTORY);

        disableUpgrading();
    }
    catch(std::exception& e)
    {
        // Prevent us from getting into an always busy situation
        disableUpgrading();
        throw;
    }
}


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
void UpgradeService::runUpgrade(const Json::Value& manifest)
{
    if(!enableUpgradingStatus())
    {
        sendErrorResponse();
        sendStringContent("Upgrade service is busy");
        return;
    }

    try
    {
        const Json::Value repos = manifest["repos"];
        if (repos.isNull() || !manifest["repos"].isArray())
        {
            sendBadRequestResponse();
            sendStringContent("Invalid repository data");
            disableUpgrading();
            return;
        }

        bool downgrade = false;
        if (manifest.isMember("downgrade") && manifest["downgrade"].isBool())
            downgrade = manifest["downgrade"].asBool();

        if (!remountFilesystem())
        {
            sendErrorResponse();
            sendStringContent("remount command failed");
            disableUpgrading();
            return;
        }

        bool result = writeConfigFiles(repos);
        if (result && downgrade)
        {
            doDowngrade();
            sendOkResponse();
            LOG_INFO("Started downgrade");
        }
        else if (result && doUpgrade())
        {
            sendOkResponse();
            LOG_INFO("Upgrade successful. Rebooting.");
            reboot();
        }
        else
        {
            LOG_ERROR("Upgrade failed. See upgrade.log for more details.");
            sendErrorResponse();
        }

        if (downgrade)
        {
            sendStringContent("");
        }
        else
        {
            sendFileContent(UPGRADE_LOG);

            // Copy and timestamp upgrade log contents to history log
            system("date >> " UPGRADE_HISTORY);
            system("cat " UPGRADE_LOG " >> " UPGRADE_HISTORY);
        }

        disableUpgrading();
    }
    catch(std::exception& e)
    {
        // Prevent us from getting into an always busy situation
        disableUpgrading();
        throw;
    }
}

/**
 *  Write out the opkg configuraton files based on the given
 *  list of repos.
 *
 *  @returns true on success and false otherwise
 */
bool UpgradeService::writeConfigFiles(const Json::Value& repos)
{
    // Remove old opkg configuration files
    system("rm -f /etc/opkg/*.conf");
    
    FILE *arch_file = fopen("/etc/opkg/arch.conf", "w");
    if (arch_file == NULL)
    {
        LOG_ERROR("Unable to open opkg arch.conf file");
        return false;
    }
    
    // Write out new opkg configuration files
    for (u_int i = 0; i < repos.size(); ++i)
    {
        std::string path = repos[i].get("path", "").asString();
        std::string name = repos[i].get("name", "").asString();
        std::string weight = repos[i].get("weight", "").asString();
        
        if (path.empty() || name.empty() || weight.empty())
        {
            LOG_ERROR("Invalid repository entry in manifest. Ignoring.\n");
            continue;
        }
        
        // Create and write configuration file
        std::string opkg_filename = "/etc/opkg/" + name + ".conf";
        FILE *opkg_file = fopen(opkg_filename.c_str(), "w");
        if (opkg_file == NULL)
        {
            LOG_ERROR("Unable to open file '%s' for writing\n", opkg_filename.c_str());
            continue;
        }
        std::string arch_entry = "src/gz " + name + " " + path + "\n";
        fwrite(arch_entry.c_str(), 1, arch_entry.size(), opkg_file);
        fclose(opkg_file);
        
        // Write new repository entry to arch.conf file
        arch_entry = "arch " + name + " " + weight + "\n";
        fwrite(arch_entry.c_str(), 1, arch_entry.size(), arch_file);
    }
    
    fclose(arch_file);
    return true;
}


/**
 *  Executes the opkg update and upgrade commands.
 *
 *  @return true on success and false otherwise
 */
bool UpgradeService::doUpgrade()
{
    // Update repository indexes
    int result = system("opkg update 2>&1 > " UPGRADE_LOG);
    if (!WIFEXITED(result) || (WEXITSTATUS(result) != 0))
    {
        LOG_ERROR("'opkg update' command failed\n");
        return false;
    }
    
    // Perform the upgrade
    result = system("opkg upgrade 2>&1 >> " UPGRADE_LOG);
    if (!WIFEXITED(result) || (WEXITSTATUS(result) != 0))
    {
        LOG_ERROR("'opkg upgrade' command failed\n");
        return false;
    }

    return true;
}


/*
 *  Copies and executes the downgrade script in a separate
 *  process.
 */
void UpgradeService::doDowngrade()
{
    system("cp /mayhem/downgrade.sh /tmp");
    int result = chmod("/tmp/downgrade.sh", S_IRUSR|S_IXUSR);
    if (result < 0)
    {
        LOG_ERROR("Unable to perform downgrade");
        return;
    }

    if (fork() == 0)
    {
        execv("/tmp/downgrade.sh", NULL);
        _exit(0);
    }
}


/**
 *  Sends the given string and the body ofan HTTP response.
 */
void UpgradeService::sendStringContent(const char *body)
{
    mg_printf(conn, "Content-Type: text/plain\r\n");
    mg_printf(conn, "Content-Length: %d\r\n\r\n", strlen(body)+2);
    mg_printf(conn, body);
    mg_printf(conn, "\r\n");
}


/**
 *  Sends the contents of the specified file as the body of an HTTP
 *  response.
*/
void UpgradeService::sendFileContent(const char* filename)
{
    FILE *file = fopen(filename, "rb");
    if(file == NULL)
    {
        LOG_ERROR("Unable to open '%s' for reading", filename);
        mg_printf(conn, "\r\n");
        return;
    }

    // Get file length
    fseek(file, 0, SEEK_END);
    long file_size = ftell(file);
    rewind(file);

    char *buf = (char*) malloc(file_size);
    if (buf == NULL)
    {
        LOG_ERROR("Memory allocation error");
        mg_printf(conn, "\r\n");
        return;
    }

    size_t read = fread(buf, 1, file_size, file);
    fclose(file);
    if (read == (size_t) file_size)
    {
        mg_printf(conn, "Content-Length: %d\r\n\r\n", read);
        mg_write(conn, buf, read);
    }
    else
    {
        LOG_ERROR("Error reading file '%s'", filename);
        mg_printf(conn, "\r\n");
    }

    free(buf);
}


/**
 *  Remounts the root filesystem read-write.
*/
bool UpgradeService::remountFilesystem()
{
    // Remount the root filesystem as read/write
    int result = system("mount / -oremount,rw");
    if (!WIFEXITED(result) || (WEXITSTATUS(result) != 0))
    {
        LOG_ERROR("remount command failed\n");
        return false;
    }
    return true;
}

void UpgradeService::reboot()
{
    if (fork() == 0)
    {
        sleep(1);
        system("reboot");
        _exit(0);
    }
}
