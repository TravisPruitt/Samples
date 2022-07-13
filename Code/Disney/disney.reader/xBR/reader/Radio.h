/**
    @file   Radio.h
    @author Mike Wilson

    Copyright (c) 2012, synapse.com
*/

#ifndef __RADIO_H
#define __RADIO_H

#include <sys/time.h>       // TODO - this should be included in grover_radio.h
#include "grover_radio.h"
#include "radiocommands.h"
#include "StatusKeeper.h"
#include <string>


#define B64_8n5(B64,B8n5) { (B8n5)[0] = ((B64) >> 32) & 0xFF; \
                            (B8n5)[1] = ((B64) >> 24) & 0xFF; \
                            (B8n5)[2] = ((B64) >> 16) & 0xFF; \
                            (B8n5)[3] = ((B64) >> 8)  & 0xFF; \
                            (B8n5)[4] = ((B64) >> 0)  & 0xFF; \
                          }

#define B8n5_64(B8n5)     ( (uint64_t)(B8n5)[0] << 32 | (uint64_t)(B8n5)[1] << 24 | \
                            (uint64_t)(B8n5)[2] << 16 | (uint64_t)(B8n5)[3] <<  8 | \
                            (uint64_t)(B8n5)[4] << 0                                \
                          )


#define BANDCMD_CMD_SZ        (sizeof (((RadioCommandPacket *) (0))->command))
#define BANDCMD_PARMS_SZ    (sizeof (((RadioCommandPacket *) (0))->data))
#define BANDCMD_MSG_SZ        (BANDCMD_CMD_SZ + BANDCMD_PARMS_SZ)     // # of bytes to send to bands on a xmt    


namespace Reader
{
    
class Radio : public StatusKeeper
{
public:
    // must call init() before getVersion() and getOffset() are valid
    void init();
    bool hardReset(int fd);
    bool softReset();
    
    std::string getVersion();
    int8_t getOffset();
    int getRadioNumber();

    // Frequency Commands
    int setFrequency(uint16_t MHz);
    
    // Receiver Commands
    int setRxEnable(bool enable);
    int setPingParms(uint16_t pto, uint8_t sz);
    int setAutoRxPing(bool enable);

    // Beacon commands
    int setBeacon(RadioCommandPacket bandCmd, uint8_t period);
    bool getBeacon(RadioCommandPacket *bandCmd, uint8_t *period);
    int clearBeacon();

    // Reply commands
    // use ALL_BANDS to apply to all bands.
    void setReply(uint64_t band_id, int8_t rcvdss, unsigned timeout, const uint8_t *data);
    void clearReply(uint64_t band_id);

protected:
    // Only the Radios class should construct and initialize radios
    friend class Radios;
    Radio(int driverFileHandle, uint8_t radioNumber);
    ~Radio(void);

private:    // methods
    
    // interface to the SPI device through these routines
    // returns sizeof(parm) (based on cmd)
    int setParm(uint8_t cmd, void *parm, size_t length);
    int getParm(uint8_t cmd, void *parm, size_t length);    

    int xchg(void);
    int setPingSz(uint32_t size);
    
    int reply(uint64_t band_id, int8_t rcvdss, unsigned timeout, const uint8_t *data);
    int replyStop(uint64_t band_id);

    // driver information
    static int getNumRadios();
    
private:        // data
    int _fd;
    std::string _version;
    int8_t _offset;

    struct radio_read rread;
    RadioMessage txb; 
    RadioMessage rxb;
};

}

#endif
