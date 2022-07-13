/**
    @file   Radio.cpp
    @author Mike Wilson

    Copyright (c) 2012, synapse.com

    Each instance of this class represents an interface to a single radio.
    It is assumed that all of the radios will be created and initialized once
    at start up. To assure that this is the case, the constructor, reset
    and init methods are all protected and should only be used by the friend
    class "Radios".
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

// Global-App
#include "grover.h"

#include "grover_radio.h"
#include "log.h"
#include "Radio.h"
#include "Mutex.h"

// Local-ish
#include "radiocommands.h"
#include "StringLib.h"


using namespace Reader;


const uint8_t     MAX_TXLEN = 16;
const struct radio_read rread_init = 
{ 0, { NULL, 1 }, { NULL, 1 }, {0 ,200000} };


/*****************************************************************************
  
              Protected methods - used by Radios only

*****************************************************************************/


Radio::Radio(int driverFileHandle, uint8_t radioNumber) : _fd(driverFileHandle), _offset(0)
{ 
    bzero(&rxb, sizeof(rxb));
    bzero(&txb, sizeof(txb));
    rread = rread_init;
    rread.radio = radioNumber;
    rread.tx.buf = (uint8_t *)&txb;
    rread.rx.buf = (uint8_t *)&rxb;
}

Radio::~Radio()
{ 
}



/**
    init - should be called once for each radio at program start up.  Gets and
    stores the version and offset values so that they can quickly be retrieved
    later without requiring communication with the radio.
*/
void Radio::init()
{
    // Get firmware version
    char buf[10];
    memset(buf, 0, sizeof(buf));
    getParm(RadioControl_GetVersion, (void *)buf, sizeof(buf));

    // Be sure there is a null terminator (will truncates if length is = sizeof(buf))
    buf[sizeof(buf)-1] = 0;
    _version = buf;

    // get calibration offset value
    getParm(RadioControl_GetRssiOffset, (void *)&_offset, sizeof(_offset));
}


/**
    Do a hard reset using the reset line to the radio.

    @param  fd  Open file descriptor to the radio debug driver

    NOTE: On V3 and later readers, there are two radio drivers.  One driver
    controls the SPI interface to the radios.  The second driver, called
    the debug driver, controls the reset line and some debug pins that allow
    us to flash new code into the radio.  On V2 and earlier systems, the
    debug and reset lines did not exist, thus there is no debug driver.
    This class largely works with the SPI interface to the radios and thus
    does not keep an open file descriptor for the debug driver, and
    therefore it must be passed into this function.
*/
bool Radio::hardReset(int fd)
{
    int status = ioctl(fd, RADIO_DEBUG_RESET, &rread);
    if (status)
    {
        LOG_ERROR("Reset of radio %d failed, returned %d", rread, status);
        setStatus(IStatus::Yellow, "Unable to reset radio");
        return false;
    }
    else
        return true;
}



/**
    Do a soft reset of the radio.  Sends the reset SPI command, so this
    will only work if the radio firmware is running and handles the
    command.
*/
bool Radio::softReset()
{
    int status = ioctl(_fd, RADIO_FORCE_RESET, &rread);
    if (status)
    {
        LOG_ERROR("Reset of radio %d failed, returned %d", rread, status);
        setStatus(IStatus::Yellow, "Radio reset command failed");
        return false;
    }
    else
        return true;
}




/*****************************************************************************
  
                              Public methods

*****************************************************************************/


std::string Radio::getVersion()
{
    return _version;
}


int8_t Radio::getOffset()
{
    return _offset;
}


int Radio::getRadioNumber()
{
    return rread.radio;
}


int Radio::setFrequency(uint16_t MHz)
{
    return setParm(RadioControl_SetFrequency, (void *) &MHz, sizeof(MHz));
}


int Radio::setRxEnable(bool enable)
{
    return setParm(RadioControl_SetRxEnable, (void *) &enable, 1);
}


int Radio::setPingParms(uint16_t pto, uint8_t sz)
{
    int prob = setParm(RadioControl_Set_GetPingTimeout, (void *) &pto, sizeof(pto));
    return (prob) ? prob : setPingSz(sz);
}


void Radio::setReply(uint64_t band_id, int8_t rcvdss, unsigned timeout, const uint8_t *data)
{
    reply(band_id, rcvdss, timeout, data);
}


void Radio::clearReply(uint64_t band_id)
{
    replyStop(band_id);
}


int Radio::setBeacon(RadioCommandPacket bandCmd, uint8_t period)
{
    int success = 0;    // GOOD
    
    success = setParm(RadioControl_SetBeaconPacket, (void *) &bandCmd, sizeof(bandCmd));
    if(success)
        return success;
    usleep(1000);
    
    success = setParm(RadioControl_SetBeaconPeriod, (void *) &period, sizeof(period));
    if(success)
        return success;
    usleep(1000);
    
    bool enable = true;
    success = setParm(RadioControl_SetBeaconEnable, (void *) &enable, 1);
    return success;
}


bool Radio::getBeacon(RadioCommandPacket *bandCmd, uint8_t *period)
{
    uint8_t enabled;
    getParm(RadioControl_GetBeaconEnable, (void *) &enabled, 1);
    usleep(1000);

    getParm(RadioControl_GetBeaconPacket, (void *) bandCmd, sizeof(RadioCommandPacket));
    usleep(1000);
    
    getParm(RadioControl_GetBeaconPeriod, (void *) &period, 1);

    return enabled != 0;
}


int Radio::clearBeacon()
{
    bool enable = 0;
    return setParm(RadioControl_SetBeaconEnable, (void *) &enable, 1);
}


/******************************************************************************

                 Low level radio interface methods

******************************************************************************/


int Radio::xchg(void)
{
    if (_fd < 0)    return -1;

    std::string bytes = StringLib::formatBytes(rread.tx.buf, rread.tx.len);
    LOG_TRAFFIC(">>radio %i: %s", rread.radio, bytes.c_str());

    int result = ioctl(_fd, RADIO_CTRL_RD, &rread);
    if (result < 0)
    {
        // program often hangs here on SIGTERM. It's OK.
        LOG_ERROR("RADIO_CTRL_RD failed, result %d, errno %d", result, errno);
        setStatus(IStatus::Red, "Radio interface error");
        return errno;
    }

    bytes = StringLib::formatBytes(rread.rx.buf, rread.rx.len);
    LOG_TRAFFIC("<<         %s", bytes.c_str());

    return 0;
}


// returns 0 or errno
int Radio::setParm(uint8_t cmd, void *parm = NULL, size_t length = 0)
{
    rread.tx.len = length+1;    // add one for command
    txb.control = cmd;
    if (parm && length) 
        memcpy(&txb.u, parm, length);
    int prob = xchg(); 
    
    return prob?prob:0;
}


int Radio::getParm(uint8_t cmd, void *parm = NULL, size_t length = 0)    
{
    txb.control = cmd;
    rread.rx.len = length+1;    // add one for command
    int prob = xchg(); 

    if(prob != 0)
        return 0;

    if(parm == NULL || length == 0)
        return 0;

    memcpy(parm, (void*)&(rxb.u), length);
    return length;
}


int Radio::setPingSz(uint32_t size)    
{
    if (_fd < 0)    return -1;

    // set the size of receive ping packets
    struct radio_ping_sz sz = {rread.radio, size};
    sz.radio = rread.radio;
    sz.size = size;
    if(ioctl(_fd, RADIO_SET_PING_SZ, &sz))
    {
        LOG_DEBUG("ping size %i\n", size);
        LOG_ERROR("RADIO_SET_PING_SZ");
        setStatus(IStatus::Red, "Radio interface error");
        return errno;
    }
    return 0;
}


int Radio::setAutoRxPing(bool enable)    
{
    if (_fd < 0)    return -1;

    // turn on receive ping interrupt.
    struct radio_autorx_enable auto_rx;
    auto_rx.radio = rread.radio;
    auto_rx.enable = enable;
    fflush(stdout);
    if(ioctl(_fd, RADIO_SET_AUTORX_EN, &auto_rx))
    {    
        LOG_ERROR("RADIO_SET_AUTORX_EN");
        setStatus(IStatus::Red, "Radio interface error");
        return errno;
    }
    return 0;
}


int Radio::reply(uint64_t band_id, int8_t rcvdss, unsigned timeout, const uint8_t *data)
{
    if (_fd < 0)
        return -1;

    // data to send
    RadioMessage radioTx;
    radioTx.control = RadioControl_TxCommand;            // command to xmit-radio
    B64_8n5(band_id, radioTx.u.commandPacket.address);   // copy band id, from U64 to U8[].
    radioTx.u.commandPacket.command = data[0];           // command sent to band
    memcpy ((uint8_t*) &radioTx.u.commandPacket.data, &data[1], BANDCMD_PARMS_SZ); // parameters 

    // point to data to send & copy to kernel
    struct radio_tx_data tx;
    memset(&tx, 0, sizeof(radio_tx_data));
    tx.data = (uint8_t*) &radioTx;
    tx.datalen = sizeof(radioTx.control) + sizeof(radioTx.u.commandPacket);
    tx.rcvdss = rcvdss;
    tx.lifetime = timeout;
    tx.band_address = band_id;

    std::string str = StringLib::formatBytes(tx.data, tx.datalen);
    LOG_DEBUG("band id %llx, ss %d, timeout %u, data %s", tx.band_address, tx.rcvdss, tx.lifetime, str.c_str());
    
    int status = ioctl(_fd, RADIO_SET_TX_DATA, &tx);
    if (status)
    {    
        LOG_ERROR("RADIO_SET_TX_DATA");
        setStatus(IStatus::Red, "Radio interface error");
        return errno;
    }
    return 0;
}


int Radio::replyStop(uint64_t band_id)
{
    if (_fd < 0)    return -1;

    LOG_DEBUG("Radio::replyStop(%llx)\n", (long long unsigned int) band_id);
    if(band_id == ALL_BANDS)
    {    
        if (ioctl(_fd, RADIO_CANCEL_ALL_TX_DATA, 0))
        {    
            LOG_ERROR("RADIO_CANCEL_ALL_TX_DATA");
            setStatus(IStatus::Red, "Radio interface error");
            return errno;
        }
    }
    else
    {
        if (ioctl(_fd, RADIO_CANCEL_TX_DATA, band_id))
        {
            LOG_ERROR("RADIO_CANCEL_TX_DATA");
            setStatus(IStatus::Red, "Radio interface error");
            return errno;
        }
    }
    return 0;
}
