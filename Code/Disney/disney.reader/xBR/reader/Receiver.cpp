/**
 *  @file   Receiver.cpp 
 *  @author Mike Wilson
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 *
 *  Class that manages all of the receive radios.  This class takes care of
 *  initializing the receive radios and processing the incoming pings.
 */


// C/C++ Std Lib
#include <cstring>
#include <cstdio>
#include <cerrno>

// System Lib
#include <pthread.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>

#include "grover.h"
#include "grover_radio.h"
#include "log.h"
#include "radiocommands.h"
#include "Receiver.h"
#include "StringLib.h"
#include "Radio.h"
#include "EventLogger.h"
#include "config.h"
#include "PingEvent.h"
#include "Radios.h"
#include "ReaderInfo.h"


using namespace Reader;


#define RXTXPACK        ((RXTX *) (0))
#define PING_TIMEOUT_MS 1000 
#define PING_SZ         11
#define BAND_ID_SIZE    5


// List of radio receive frequencies
const uint16_t freqs[] = { 2401, 2424, 2450, 2476 };

const uint8_t MAX_TXLEN = 16;
const struct radio_read rread_init = { 0, { NULL, 1 }, { NULL, 1 }, {0 ,200000} };


Receiver* Receiver::instance()
{
    static Receiver _instance;
    return &_instance;
}


Receiver::Receiver() :
    _minss(-127)
{
}


Receiver::~Receiver()
{
}


/**
 *  Initialize the receive radios.
 */
void Receiver::init()
{
    // grab signal strength filter value from config file if it's there
    _minss = Config::instance()->getValue("min ss", -127);

    std::string filterId = Config::instance()->getValue("band id", "");
    BandId id;
    if (StringLib::hex2bandId(filterId.c_str(), &id))
        _filterId = id;

    unsigned numRadios = Radios::totalNumberOfRadios();
    for (unsigned rcvr = 0; rcvr < numRadios; rcvr++)
    {
        Radio& radio = Radios::radio(rcvr);

        uint16_t frequency = freqs[radio.getRadioNumber() % 4];
        LOG_DEBUG("for radio %u using %u", rcvr, (unsigned)frequency);

        // Change frequency and ping parameters with receiver off
        if (radio.setRxEnable(0))
            continue;
        if (radio.setFrequency(frequency))
            continue;
        if (radio.setPingParms(PING_TIMEOUT_MS, PING_SZ))
            continue;

        // Now turn it on and tell it to start receiving pings
        if (radio.setRxEnable(1))
            continue;
        if (radio.setAutoRxPing(1))
            continue;
    }
}


/**
 *  Set a minimum signal threshold. Pings with a signal strength < this value
 *  will be ignored.
 */
void Receiver::setSignalStrengthFilter(int minss)
{
    _minss = minss;
}


/**
 *  Get the current signal strength filter value.
 */
int Receiver::getSignalStrengthFilter()
{
    return _minss;
}


/**
 *  Set up a band ID filter.
 *
 *  @param  id  Band id to filter on. If non-zero, then only pings from this band
 *              ID will be accepted. If 0, then pings from all bands are accepted.
 */
void Receiver::setBandIdFilter(BandId id)
{
    _filterId = id;
}


/**
 *  Get the current band ID filter value.
 */
BandId Receiver::getBandIdFilter()
{
    return _filterId;
}
    

/**
 *  Receive thread run method.
 */
void Receiver::run()
{
    const int* _drivers = Radios::getDrivers();
    fd_set rd_set;

    while (!_quit)
    {
        int max_fd = 0;
        FD_ZERO(&rd_set);
        for (int i = 0; i < MAX_NUM_RADIO_DRIVERS; ++i)
        {
            // Skip unopened driver
            if (_drivers[i] < 0)
                continue;

            FD_SET(_drivers[i], &rd_set);
            if (_drivers[i] > max_fd)
                max_fd = _drivers[i];
        }
        ++max_fd;

        int status = select(max_fd, &rd_set, NULL, NULL, NULL);
        if (status <= 0)	
        {	
            if(status == 0)
                LOG_DEBUG("Select: Timeout");
            else
                LOG_DEBUG("Select: status=%d, errno=%d: %s", status, errno, strerror(errno));
            continue;
        }

        for (int i = 0; i < MAX_NUM_RADIO_DRIVERS; ++i)
        {
            if (_drivers[i] < 0)
                continue;

            if(FD_ISSET(_drivers[i], &rd_set))
                processData(_drivers[i]);
        }
    }
}


/*
 *  Process all incoming data for the given driver instance.
 */
void Receiver::processData(int driver)
{
    const size_t BUF_SZ = 512;
    uint8_t buf[BUF_SZ];

    // successful select; we have data to read
    int status = read(driver, buf, BUF_SZ);
    if (status <= 0)	
    {	
        if(status == 0)
            LOG_DEBUG("Read returned 0");
        else
            LOG_DEBUG("Read: status=%d, errno=%d: %s", status, errno, strerror(errno));
        return;
    }

    // successfully read some data
    int len = status;
    
    // loop through the data, and call appropriate functions for each one.
    struct radio_message *rmsg = (struct radio_message *) buf;	
    
    int offset = 0;
    while (offset < len)
    {
        rmsg = (struct radio_message *) &buf[offset];
        int msgSz = sizeof(struct radio_message) + rmsg->length;

        RadioMessage *rxtx = (RadioMessage *) rmsg->msg;
            
        if(rmsg->id == R_MSG_FROM_RADIO)
        {
            // filter out GetPings with signal_strength == 0.
            // (These occur when we timeout waiting for a packet)
            if (rxtx->control != RadioControl_GetPing ||
                rxtx->u.pingPackage.signalStrength)
                processPing(rmsg->radio, rmsg->msg, rmsg->length);
        }
        else
            LOG_DEBUG("Unhandled R_MSG type %i\n", rmsg->id);
        
        offset += msgSz;   
    }
}



/**
 *  Process a single ping.
 */
void Receiver::processPing(uint8_t radioNum, uint8_t *buffer, uint8_t length)
{
    RadioMessage *rxtx = (RadioMessage *) buffer;
    
    if (rxtx->control != RadioControl_GetPing)
    {
        LOG_WARN("cmd not a ping");
        return;
    }

    if (length < sizeof(RadioPingPackage))
    {
        LOG_WARN("ping too short");
        return;
    }
    
    RadioPingPackage *ping = &rxtx->u.pingPackage;

    if (ping->signalStrength < _minss)
        return;

    BandId bandId = 0;
    for (int i = 0; i < BAND_ID_SIZE; ++i)
        bandId = (bandId << 8) + ping->payload.address[i];

    if (_filterId && bandId != _filterId)
        return;

    PingEvent* event = new PingEvent(bandId,
                                     ping->payload.sequenceNumber, 
                                     ping->payload.tbd[0],
                                     ping->payload.tbd[1],
                                     ping->signalStrength, 
                                     freqs[radioNum % 4],
                                     radioNum/4,
                                     ping->receiverSequenceNumber);

    EventLogger::instance()->postEvent(event);
}
