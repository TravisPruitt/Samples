/**
 *  @file   tlc59116.h
 *  @author Juha Kuikka
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include "rgbleddriver.h"
#include "I2C.h"


#define TLC59116_CHANNELS	(16)


namespace Reader
{


class TLC59116 : public RGBLedDriver
{
public:
    TLC59116(I2C *i2c, uint8_t address);
    virtual ~TLC59116();

    virtual int init();

    virtual void setBrightness(unsigned channel,
                               unsigned brightness);
    virtual void setGlobalBrightness(uint8_t value);

    virtual void setGlobalBrightnessFactor(float factor)
    {
        brightness_factor = factor;
    }

    virtual int update();
    virtual int forceUpdate();

    virtual bool isAllOff();

private:
    int writeRegister(uint8_t reg, uint8_t value);
    int writeRegisters(uint8_t start, uint8_t nr, uint8_t *val);
    
private:
    I2C *bus;
    uint8_t bus_address;
    bool dirty;
    uint8_t brightness[TLC59116_CHANNELS];
    float brightness_factor;
    
public: // static
    static int setAllCallAddress(I2C *bus, uint8_t addr);
    static int resetAll(I2C *bus);
    
private: // static
    static uint8_t allcalladr;
};


} // namespace Reader
