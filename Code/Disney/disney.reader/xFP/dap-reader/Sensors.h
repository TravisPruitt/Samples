

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

    double getLuminance();

private:
    // singleton
    Sensors();
    ~Sensors();

    void run();
    double readTemperature();
    double readLuminance();

private:
    double _temperature;
    double _maxTemperature;
    double _luminance;

    double _temperatureLimit;
};

}


#endif
