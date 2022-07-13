/**
    Config.h - Configuration file
    Dec 2011
    Greg Strange

    Copyright (c) 2011, synapse.com
*/


#ifndef __CONFIG_H
#define __CONFIG_H

#include "ConfigFile.h"


namespace Reader
{

class Config : public ConfigFile
{
public:
    static Config* instance();

    std::string getXbrcUrl();
    void setXbrcUrl(const char* url);
    void clearXbrcUrl();
    void save();

private:  // methods
    // singleton
    Config();
    ~Config();

    // not implemented
    Config(const Config&);
    const Config& operator=(const Config&);

    std::string readXbrcUrlFromDhcp();

private:  // data
    bool triedXbrms;
};

}

#endif
