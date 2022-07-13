/**
    DapConfig.h - Configuration file
    Dec 2011
    Greg Strange

    Copyright (c) 2011, synapse.com
*/


#ifndef __DAP_CONFIG_H
#define __DAP_CONFIG_H


#include "ConfigFile.h"


namespace Reader
{


class DapConfig : public ConfigFile
{
public:
    static DapConfig* instance();

    std::string getXbrcUrl();
    void setXbrcUrl(const char* url);
    void clearXbrcUrl();    
    void save();

private:  // methods
    // singleton
    DapConfig();
    ~DapConfig();

    std::string readXbrcUrlFromDhcp();

    // not implemented
    DapConfig(const DapConfig&);
    const DapConfig& operator=(const DapConfig&);

private:  // data
    bool triedXbrms;
};


} // namespace reader


#endif
