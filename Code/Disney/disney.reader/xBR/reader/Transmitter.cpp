/**
    @file   Transmitter.cpp 
    @author Greg Strange

    Copyright (c) 2011, synapse.com

    Code for controlling the transmit radio
*/


// C/C++ Std Lib
#include <ctime>
#include <string>
#include <cstring>
#include <cstdio>
#include <stdint.h>
#include <sys/types.h>

#include "grover_radio.h"
#include "grover.h"
#include "log.h"
#include "Transmitter.h"
#include "StringLib.h"
#include "config.h"
#include "Radios.h"


using namespace Reader;


#define DEFAULT_FREQUENCY      2482
#define DEFAULT_REPLY_TIMEOUT  3600


static std::string radioVersion;
static Radio* _radio;

// Holds the current reply timeout in seconds which is sent to the radio driver
// when new bands are added in reply mode. The radio driver will remove a band
// from the reply list if it doesn't recieve a ping in this timeframe. A value
// of zero = no timeout.
static unsigned _replyTimeout;


void Transmitter::init()
{
    _radio = &(Radios::txRadio());

    _replyTimeout = DEFAULT_REPLY_TIMEOUT;

    // setup the transmitter
    uint16_t frequency = Config::instance()->getValue("tx frequency", DEFAULT_FREQUENCY);
    if (_radio->setFrequency(frequency) == 0)
        LOG_DEBUG("tx frequency set to %d", frequency);
}


/******************************************************************************

                           Beacon functions

******************************************************************************/


/**
    Send a beacon command.  I.e. a command that is sent continuously back to back
*/
void Transmitter::startBeacon(uint64_t id, uint8_t *data)
{
    clearReplies();

    RadioCommandPacket bandCmd;
    B64_8n5(id, bandCmd.address);
    
    // data contains the command, and we copy over band.data during this copy
    memcpy(&bandCmd.command, data, BANDCMD_MSG_SZ);
    _radio->setBeacon(bandCmd, 0 /* msec between xBR transmits */);
}		


/**
    Stop the beacon
*/
void Transmitter::stopBeacon()
{
    _radio->clearBeacon();
}


bool Transmitter::getBeacon(uint64_t& id, uint8_t* cmd)
{
    RadioCommandPacket bandCmd;
    uint8_t period;
    id = 0;

    if (_radio->getBeacon(&bandCmd, &period))
    {
        id = B8n5_64(bandCmd.address);
        memcpy(cmd, &bandCmd.command, BANDCMD_MSG_SZ);
        return true;
    }

    return false;
}


/******************************************************************************

                               Reply functions

******************************************************************************/


/**
    Set up a reply command (commands sent in reply to a received ping)
*/
void Transmitter::setReply(uint64_t band_id, int8_t threshold, const uint8_t *data)
{
    stopBeacon();

    std::string cmd = StringLib::formatBytes(data, 6);
    LOG_DEBUG("reply to %llx with '%s', ss %d, timeout %u", band_id, cmd.c_str(), threshold, _replyTimeout);
    _radio->setReply(band_id, threshold, _replyTimeout, data);
}


/**
    Reply to all pings with a command.
*/
void Transmitter::setReplyAll(int8_t threshold, uint8_t *data)
{
    stopBeacon();

    std::string cmd = StringLib::formatBytes(data, 6);
    LOG_DEBUG("reply to all with '%s', ss %d", cmd.c_str(), threshold);
    _radio->setReply(ALL_BANDS, threshold, _replyTimeout, data);
}


/**
    Remove the reply for a particular band
*/
void Transmitter::clearReply(BandId id)
{
    _radio->clearReply(id);
}


/**
    Remove the reply for all bands
*/
void Transmitter::clearReplies()
{
    _radio->clearReply(ALL_BANDS);
}


/*
 *  Sets the reply timeout (in seconds) that gets sent to the
 *  radio driver when new bands are added in reply mode.
 */
void Transmitter::setReplyTimeout(unsigned timeout)
{
    _replyTimeout = timeout;
}

/*
 *  Get the current reply timeout
 */
unsigned Transmitter::getReplyTimeout()
{
    return _replyTimeout;
}
