/**
 *  @file   DapConfig.h
 *  @date   Dec 2011
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
*/


#ifndef __DAP_CONFIG_H
#define __DAP_CONFIG_H


#include "ConfigFile.h"


// Specifies that brightness is dynamically adjusted
#define AUTO_BRIGHTNESS  -1


namespace Reader
{


class DapConfig : public ConfigFile
{
public:
    static DapConfig* instance();

    int getBrightnessSetting();

    std::string getXbrcUrl();
    void setXbrcUrl(const char* url);
    void clearXbrcUrl();
    void save();

private:  // methods
    // singleton
    DapConfig();
    ~DapConfig();

    // not implemented
    DapConfig(const DapConfig&);
    const DapConfig& operator=(const DapConfig&);

    std::string readXbrcUrlFromDhcp();

private:  // data
    bool triedXbrms;
};


} // namespace Reader

#endif
