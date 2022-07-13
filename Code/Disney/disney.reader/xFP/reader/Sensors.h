/**
 *  @file   Sensors.h
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */

#ifndef __SENSORS_H
#define __SENSORS_H

#include "Thread.h"
#include "I2C.h"


namespace Reader
{


class Sensors : public Thread
{
public:
    static Sensors* instance();

    double getTemperature();
    double getTemperatureLimit();
    double getMaxTemperature();
    bool overTemp();

    double getIlluminance();

private:
    // singleton
    Sensors();
    ~Sensors();

    void run();
    double readTemperature();
    void resetAmbientLightSensor();
    void updateIlluminance();
    double readIlluminance();

private:
    double _temperature;
    double _maxTemperature;
    double _illuminance;

    double _temperatureLimit;
};


} // namespace Reader


#endif
