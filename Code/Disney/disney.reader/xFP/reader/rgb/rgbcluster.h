/**
 *  @file   rgbcluster.h
 *  @author Juha Kuikka
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#ifndef __RGBCLUSTER_H__
#define __RGBCLUSTER_H__


#include <vector>
#include <iostream>
#include "rgbled.h"
#include "rgbcolor.h"
#include "rgbleddriver.h"


namespace Reader
{


class RGBCluster
{
public:
    RGBCluster();
    virtual ~RGBCluster();

public:
    void init(bool reset = true, int allcall = 0x6F);
    void setColor(unsigned led, const RGBColor &color);
    void setColorAll(const RGBColor &color);
    void setGlobalBrightness(uint8_t level);

    uint8_t getGlobalBrightness()
    {
        return globalBrightness;
    }
 
    void clearAll();
    void update();
    void reset(bool init = true);
    bool isAllOff();

    bool getErrorStatus()
    {
        return (error_count > error_threshold);
    }

    void clearErrorStatus()
    {
        error_count = 0;
    }

private:
    void _setGlobalBrightness(uint8_t level);

private:
    typedef std::vector<RGBLedDriver*> drivers_t;
    typedef std::vector<RGBLed*> leds_t;

    // Keeps track of the number off errors in a period
    // of writes. Successful writes decrement the counter
    // to zero, while failures increment it. This should
    // prevent an error status from occuring on rare
    // intermittent i2c errors.
    unsigned error_count;

    // Number of errors to tolerate before an error status
    // is given.
    static const unsigned error_threshold = 5;

    int _allcall;
    uint8_t globalBrightness;
    drivers_t drivers;
    leds_t leds;
    I2C *bus;
};


} // namespace Reader


#endif // __RGBCLUSTER_H__
