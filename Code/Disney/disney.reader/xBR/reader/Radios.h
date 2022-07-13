/**
 *  @file   Radios.h
 *  @author Greg Strange
 *  @author Corey Wharton 
 *
 *  Copyright (c) 2012, synapse.com
*/


#ifndef __RADIOS_H
#define __RADIOS_H


#include "standard.h"
#include "Radio.h"


#define MAX_NUM_RADIO_DRIVERS 2


namespace Reader
{

    
class Radios
{
public:
    static void init();
    static unsigned totalNumberOfRadios();
    static Radio& radio(int radioNumber);
    static Radio& txRadio();
    static std::string getDriverVersion();

    static const int* getDrivers();

private:
    static int getNumRadios(int fd);
};


} // namespace Reader


#endif
