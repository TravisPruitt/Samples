#include <stdint.h>
#ifndef WIN32
#include <unistd.h>
#endif
#include <cstring>
#include <malloc.h>
#include "tlc59116.h"
#include "I2C.h"


#define REG_MODE1	0x00
#define REG_MODE2	0x01
#define REG_PWM0	0x02
#define REG_GRPPWM  0x12
#define REG_LEDOUT0	0x14
#define REG_ALLCALLADR	0x1B
#define DEFAULT_ALLCALL_ADDR	0x68
#define SWRST_ADDR		0x6B

namespace Reader
{


uint8_t TLC59116::allcalladr = DEFAULT_ALLCALL_ADDR;


TLC59116::TLC59116(I2C *i2c, uint8_t address) :
    bus(i2c), bus_address(address), dirty(false),
    brightness_factor(1)
{
    init();
}


TLC59116::~TLC59116()
{
}


int TLC59116::init()
{
    // Turn on oscillator, disable group addresses
    writeRegister(REG_MODE1, 0);
    
    // Wait 500us for oscillator to come up - according to spec
#ifndef _WIN32
    usleep(500);
#endif
    
    // Set PWM drivers to zero
    for(unsigned i = 0; i < 16; i++)
        writeRegister(i + REG_PWM0, 0);

    // Clear PWM register buffer
    memset(brightness, 0, sizeof(brightness));

    // Set all channels to PWM mode w/ group dimming
    for(unsigned i = 0; i < 4; i++)
        writeRegister(i + REG_LEDOUT0, 0xFF);
    
    return 0;
}


void TLC59116::setBrightness(unsigned channel, unsigned value)
{
    if(channel >= TLC59116_CHANNELS)
        return;
    brightness[channel] = value;
    dirty = true;
}


void TLC59116::setGlobalBrightness(uint8_t value)
{
    writeRegister(REG_GRPPWM, value*brightness_factor);
}


int TLC59116::writeRegister(uint8_t reg, uint8_t value)
{
    uint8_t buf[2] = { reg, value };
    return bus->write(bus_address, buf, sizeof(buf));
}


int TLC59116::writeRegisters(uint8_t start, uint8_t nr, uint8_t *val)
{
#ifdef _WIN32
    uint8_t* buf = (uint8_t*)alloca(nr+1);
#else
    uint8_t buf[nr+1];
#endif
    buf[0] = 0x80 | start; // Auto-increment
    memcpy(&buf[1], val, nr);
    return bus->write(bus_address, buf, sizeof(buf));
}


int TLC59116::update()
{
    if(dirty) {
        return forceUpdate();
    }
    return 0;
}


int TLC59116::forceUpdate()
{
    dirty = false;
    return writeRegisters(REG_PWM0, TLC59116_CHANNELS, brightness);
}


int TLC59116::setAllCallAddress(I2C *bus, uint8_t addr)
{
    uint8_t allcall[2] = { REG_ALLCALLADR, addr << 1};
    int ret = bus->write(allcalladr, allcall, 2);
    if(ret)
        return ret;
    allcalladr = addr;
    return 0;
}


int TLC59116::resetAll(I2C *bus)
{
    allcalladr = DEFAULT_ALLCALL_ADDR;
		uint8_t swrst[2] = { 0xA5, 0x5A };
		return bus->write(SWRST_ADDR, swrst, 2);
}


} // namespace Reader
