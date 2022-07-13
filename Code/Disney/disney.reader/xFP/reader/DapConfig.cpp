/**
 *  @file   DapConfig.cpp
 *  @date   Dec 2011
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include "standard.h"
#include "DapConfig.h"
#include "FileSystem.h"
#include "DapReader.h"
#include "xbrms.h"

#ifndef _WIN32
#include <string.h>
#endif
#include <string>


namespace Reader
{


static const char* getConfigFilePath()
{
    static std::string path = FileSystem::getConfigPath();
    path += FileSystem::PathSeperator;
    path += "config.json";
    return path.c_str();
}


DapConfig::DapConfig() :
    ConfigFile(getConfigFilePath()), triedXbrms(false)
{
    if (ConfigFile::hasErrors())
        DapReader::instance()->setStatus(IStatus::Yellow, "Invalid config file");
}


DapConfig::~DapConfig()
{
}


DapConfig* DapConfig::instance()
{
    static DapConfig _instance;
    return &_instance;
}

void DapConfig::save()
{
    if (!ConfigFile::save())
        DapReader::instance()->setStatus(IStatus::Red, "Unable to save config file");
}


int DapConfig::getBrightnessSetting()
{
    // Default is -1 == auto
    int brightness = getValue("brightness", AUTO_BRIGHTNESS);
    if (brightness < AUTO_BRIGHTNESS)
        brightness = AUTO_BRIGHTNESS;
    if (brightness > 255)
        brightness = 255;

    return brightness;
}


#ifdef _WIN32
#define XBRC_URL_FILE   "xbrc-url.txt"
#else
#define XBRC_URL_FILE   "/var/lib/dhcp/xbrc-url"
#endif


/**
 *  Read the xbrc url set by the DHCP server, or empty string if
 *  there is no xbrc url set by DHCP.
*/
std::string DapConfig::readXbrcUrlFromDhcp()
{
    FILE* fp;
    char buf[200];

    fp = fopen(XBRC_URL_FILE, "r");
    if (fp)
    {
        memset(buf, 0, sizeof(buf));
        fgets(buf, sizeof(buf)-1, fp);
        int i = strlen(buf);
        if (i > 0 && buf[i-1] == '\n')
            buf[i-1] = 0;
        fclose(fp);
        return buf;
    }
    else
        return "";
}


/**
 *  Get the xbrc url.
 *
 *  @return xBRC URL or an empty string if there is no xBRC URL set
 *
 *  The xbrc url comes from one of four places.
 *
 *  1) It is stored in the config file and
 *  2) It is placed in a file by the DHCP server
 *  3) It is given by the xBRMS
 *  4) Last used or pushed URL
 *
 *  This method checks location #1 first, and if there is no xbrc URL set
 *  in the config file, then it looks for one from the DHCP server. Next,
 *  it will attempt to find an xBRMS via a DNS SRV record lookup and say
 *  hello. If the xBRMS is reached then it will later respond by sending
 *  a request to the reader to change it's xBRC URL. The DNS method will
 *  only be attempted once. If none of those methods succeed then it will
 *  check the value of the "last xrbr url" config value. This config value
 *  is set to either the last used xBRC value or the value pushed via the
 *  REST interface.
 */
std::string DapConfig::getXbrcUrl()
{
    // First priority is manual configuration
    std::string url = getValue("xbrc url", "");

    // Second priority is from DHCP
    if (url.size() == 0)
        url = readXbrcUrlFromDhcp();

    // Third priority is via DNS SRV lookup
    if(url.size() == 0 && !triedXbrms)
    {
        contact_xbrms();
        triedXbrms = true;
    }

    // Last priority is last used
    if (url.size() == 0)
        return getValue("last xbrc url", "");

    // If last url doesn't match the current then save it
    if (url != getValue("last xbrc url", ""))
        setXbrcUrl(url.c_str());

    return url;
}


void DapConfig::setXbrcUrl(const char* url)
{
    setValue("last xbrc url", url);
    save();
}


void DapConfig::clearXbrcUrl()
{
    setValue("last xbrc url", "");
    triedXbrms = false;
    save();
}


} // namespace Reader
