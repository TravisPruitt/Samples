/**
 *  @file   Receiver.h 
 *  @author Mike Wilson
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#ifndef __RECEIVER_H
#define __RECEIVER_H


#include "Thread.h"


class Receiver : public Thread
{
public:
    static Receiver* instance();
    void init();

    // filtering
    void setSignalStrengthFilter(int ss);
    int getSignalStrengthFilter();
    void setBandIdFilter(BandId bandId);
    BandId getBandIdFilter();

private:
    Receiver();
    ~Receiver();

    // No copies please
    Receiver(const Receiver&);
    const Receiver& operator=(const Receiver&);

    void run();
    void processData(int driver);
    void processPing(uint8_t radioNum, uint8_t *buffer, uint8_t length);

private: // data
    int _minss;
    BandId _filterId;
};


#endif
