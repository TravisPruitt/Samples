/**
 *  @file   rgbcluster.cpp
 *  @author Juha Kuikka
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include <iostream>
#include <algorithm>

#include "log.h"
#include "rgbcluster.h"
#include "I2C.h"
#include "rgbleddriver.h"
#include "tlc59116.h"
#include "rgbled.h"


#define NR_DRIVERS          10
#define NR_LEDS             48


struct led_mappings
{
    unsigned driver;
    unsigned channel;
};


static const struct led_mappings led_driver_mapping[NR_LEDS][3] = {
    //   Red       Green      Blue
    // Driver, Channel
    // Outer circle, from 12 clockwise
    { { 1, 12 }, { 1, 11 }, { 1, 10 }, }, // Led 0, D1
    { { 1, 9  }, { 1, 8  }, { 1, 7  }, }, // Led 1, D2
    { { 1, 6  }, { 1, 5  }, { 1, 4  }, }, // Led 2, D3
    { { 0, 11 }, { 0, 10 }, { 0, 9  }, }, // Led 3, D4
    { { 0, 5  }, { 0, 4  }, { 0, 3  }, }, // Led 4, D5
    { { 3, 8  }, { 3, 7  }, { 3, 6  }, }, // Led 5, D6
    { { 3, 5  }, { 3, 4  }, { 3, 3  }, }, // Led 6, D7
    { { 3, 2  }, { 3, 1  }, { 3, 0  }, }, // Led 7, D30
    { { 8, 8  }, { 8, 7  }, { 8, 6  }, }, // Led 8, D28
    { { 8, 5  }, { 8, 4  }, { 8, 3  }, }, // Led 9, D35
    { { 8, 2  }, { 8, 1  }, { 8, 0  }, }, // Led 10, D34
    { { 6, 11 }, { 6, 10 }, { 6, 9  }, }, // Led 11, D33
    { { 6, 8  }, { 6, 7  }, { 6, 6  }, }, // Led 12, D41
    { { 6, 5  }, { 6, 4  }, { 6, 3  }, }, // Led 13, D36
    { { 6, 2  }, { 6, 1  }, { 6, 0  }, }, // Led 14, D43
    { { 4, 14 }, { 4, 13 }, { 4, 12 }, }, // Led 15, D26
    { { 4, 11 }, { 4, 10 }, { 4, 9  }, }, // Led 16, D29
    { { 5, 14 }, { 5, 13 }, { 5, 12 }, }, // Led 17, D23
    { { 5, 11 }, { 5, 10 }, { 5, 9  }, }, // Led 18, D24
    { { 5, 2  }, { 5, 1  }, { 5, 0  }, }, // Led 19, D21
    { { 7, 11 }, { 7, 10 }, { 7, 9  }, }, // Led 20, D20
    { { 7, 5  }, { 7, 4  }, { 7, 3  }, }, // Led 21, D19
    { { 2, 5  }, { 2, 4  }, { 2, 3  }, }, // Led 22, D18
    { { 2, 2  }, { 2, 1  }, { 2, 0  }, }, // Led 23, D17
    // Inner circle, from 12 clockwise
    { { 2, 11 }, { 2, 10 }, { 2, 9  }, }, // Led 24, D14
    { { 1, 15 }, { 2, 15 }, { 2, 14 }, }, // Led 25, D16
    { { 9, 3  }, { 9, 4  }, { 9, 5  }, }, // Led 26, Antenna D2
    { { 1, 3  }, { 1, 2  }, { 1, 1  }, }, // Led 27, D15
    { { 1, 0  }, { 0, 13 }, { 0, 12 }, }, // Led 28, D13
    { { 0, 8  }, { 0, 7  }, { 0, 6  }, }, // Led 29, D12
    { { 0, 2  }, { 0, 1  }, { 0, 0  }, }, // Led 30, D11
    { { 9, 6  }, { 9, 7  }, { 9, 8  }, }, // Led 31, Antenna D3
    { { 3, 11 }, { 3, 10 }, { 3, 9  }, }, // Led 32, D44
    { { 3, 14 }, { 3, 13 }, { 3, 12 }, }, // Led 33, D37
    { { 8, 11 }, { 8, 10 }, { 8, 9  }, }, // Led 34, D38
    { { 8, 14 }, { 8, 13 }, { 8, 12 }, }, // Led 35, D39
    { { 6, 14 }, { 6, 13 }, { 6, 12 }, }, // Led 36, D40
    { { 4, 2  }, { 4, 1  }, { 4, 0  }, }, // Led 37, D48
    { { 4, 5  }, { 4, 4  }, { 4, 3  }, }, // Led 38, D25
    { { 4, 8  }, { 4, 7  }, { 4, 6  }, }, // Led 39, D46
    { { 5, 8  }, { 5, 7  }, { 5, 6  }, }, // Led 40, D45
    { { 9, 9  }, { 9, 10 }, { 9, 11 }, }, // Led 41, Antenna D4
    { { 5, 5  }, { 5, 4  }, { 5, 3  }, }, // Led 42, D31
    { { 7, 14 }, { 7, 13 }, { 7, 12 }, }, // Led 43, D22
    { { 7, 8  }, { 7, 7  }, { 7, 6  }, }, // Led 44, D10
    { { 7, 2  }, { 7, 1  }, { 7, 0  }, }, // Led 45, D8
    { { 9, 0  }, { 9, 1  }, { 9, 2  }, }, // Led 46, Antenna D1
    { { 2, 8  }, { 2, 7  }, { 2, 6  }, }, // Led 47, D9
};


static const uint8_t led_driver_addresses[NR_DRIVERS] = {
    0x60, // U2
    0x68, // U3
    0x64, // U4
    0x6C, // U5
    0x62, // U6
    0x6A, // U7
    0x66, // U8
    0x6E, // U9
    0x69, // U10
    0x65  // U3 Antenna
};


namespace Reader
{


struct deleter
{
    template <typename T>
    void operator()(const T& ptr) const
        {
            delete ptr;
        }
};
    

RGBCluster::RGBCluster() :
    error_count(0), globalBrightness(0xFF), bus(NULL)
{
}


RGBCluster::~RGBCluster()
{
    std::for_each(leds.begin(), leds.end(), deleter());
    leds.clear();
    
    std::for_each(drivers.begin(), drivers.end(), deleter());
    drivers.clear();
    
    if (bus)
    {
        delete bus;
        bus = NULL;
    }
}
    
    
void RGBCluster::init(bool reset, int allcall)
{
    drivers.reserve(NR_DRIVERS);
    leds.reserve(NR_LEDS);
    
    bus = new I2C(I2C_SENSOR_BUS);

    // Reset all drivers
    if(reset)
    {
        if(TLC59116::resetAll(bus))
            LOG_ERROR("Could not reset drivers");
    }

    // Change all call address
    _allcall = allcall;
    if(_allcall > 0)
    {
        if(TLC59116::setAllCallAddress(bus, allcall))
            LOG_ERROR("Could not set all call address: 0x%02X", _allcall);
    }

    for(unsigned i = 0; i < NR_DRIVERS; i++)
    {
        TLC59116 *ic = new TLC59116(bus, led_driver_addresses[i]);
        drivers.push_back(ic);
    }

    // Adjust brightness factor of antenna LED driver
    drivers[9]->setGlobalBrightnessFactor(0.30);

    for(unsigned i = 0; i < NR_LEDS; i++)
    {
        RGBLed *led = new RGBLed();
        led->setDriver(RGBLed::RED,
                       drivers[led_driver_mapping[i][RGBLed::RED].driver],
                       led_driver_mapping[i][RGBLed::RED].channel);
        led->setDriver(RGBLed::GREEN,
                       drivers[led_driver_mapping[i][RGBLed::GREEN].driver],
                       led_driver_mapping[i][RGBLed::GREEN].channel);
        led->setDriver(RGBLed::BLUE,
                       drivers[led_driver_mapping[i][RGBLed::BLUE].driver],
                       led_driver_mapping[i][RGBLed::BLUE].channel);
        leds.push_back(led);
    }
}


void RGBCluster::setColor(unsigned led, const RGBColor &color)
{
    // Prevent overrun
    if (led >= leds.size())
        return;
    leds[led]->setColor(color.r, color.g, color.b);
}


void RGBCluster::setColorAll(const RGBColor &color)
{
    for(leds_t::const_iterator i = leds.begin();
        i != leds.end();
        ++i)
    {
        (*i)->setColor(color.r, color.g, color.b);
    }
}


void RGBCluster::setGlobalBrightness(uint8_t level)
{
    if (globalBrightness != level)
    {
        globalBrightness = level;
        _setGlobalBrightness(level);
    }
}


void RGBCluster::clearAll()
{
    setColorAll(RGBColor(0, 0, 0));
}


void RGBCluster::update()
{
    for(drivers_t::const_iterator i = drivers.begin();
        i != drivers.end();
        ++i)
    {
        int ret = (*i)->update();

        // increment or decrement the error counter
        // depending on success of the update.
        if (ret < 0)
            ++error_count;
        else if (error_count > 0)
            --error_count;
    }
}


void RGBCluster::reset(bool init)
{
    clearAll();
    if(TLC59116::resetAll(bus))
        LOG_DEBUG("Could not reset drivers");

    // Change all call address
    if(_allcall > 0)
    {
        if(TLC59116::setAllCallAddress(bus, _allcall))
            LOG_ERROR("Could not set all call address: 0x%02X", _allcall);
    }

    if (init)
    {
        // Reinitialize
        drivers_t::iterator it;
        for(it = drivers.begin(); it != drivers.end(); it++)
            (*it)->init();

        _setGlobalBrightness(globalBrightness);
    }
}


bool RGBCluster::isAllOff()
{
    drivers_t::iterator it;
    for (it = drivers.begin(); it != drivers.end(); it++)
    {
        if (!(*it)->isAllOff())
            return false;
    }

    return true;
}


void RGBCluster::_setGlobalBrightness(uint8_t level)
{
    drivers_t::iterator it;
    for(it = drivers.begin(); it != drivers.end(); it++)
        (*it)->setGlobalBrightness(level);
}


} //namespace Reader
