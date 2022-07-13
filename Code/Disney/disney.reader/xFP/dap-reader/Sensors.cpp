

#include "Sensors.h"
#include "DapConfig.h"
#include "SensorBus.h"
#include "LightEffects.h"
#include "ticks.h"
#include "log.h"

//#include <unistd.h>

#define I2C_SENSOR_BUS		3
#define I2C_THERMISTOR_ADDR	0x4B
#define I2C_ALS_ADDR		0x23


#define READ_INTERVAL              1000    // milliseconds
#define LUMINANCE_FILTER_FACTOR    0.3     // simple first order filter


using namespace Reader;




Sensors::Sensors() : 
    _temperature(0), 
    _maxTemperature(0),
    _luminance(1000)    // typical daylight reading, used as starting point
{
    _temperatureLimit = DapConfig::instance()->getValue("max temperature", 70.0);
}


Sensors::~Sensors()
{
}


Sensors* Sensors::instance()
{
    static Sensors _instance;
    return &_instance;
}



/**
    Get the latest temperature reading in C

    Returns 0 if temperature has not yet been read
*/
double Sensors::getTemperature()
{
    return _temperature;
}


/**
    Returns the over temperature threshold
*/
double Sensors::getTemperatureLimit()
{
    return _temperatureLimit;
}



/**
    Get the max temperature reading in C

    Returns 0 if temperature has not yet been read
*/
double Sensors::getMaxTemperature()
{
    return _maxTemperature;
}


/**
    Are we over the safe temperature limit?
*/
bool Sensors::overTemp()
{
    return _temperature > _temperatureLimit;
}



/**
    Get the latest ambient light sensor reading in lux.
*/
double Sensors::getLuminance()
{
    return _luminance;
}


double Sensors::readTemperature()
{
	int16_t value;
    uint8_t buf[2];
    double temp;

    if (SensorBus::instance()->read(I2C_THERMISTOR_ADDR, buf, 2) < 0)
        return 0;

    value = (buf[0] << 8) + buf[1];
	value >>= 3;
	temp = ((double)value) * 0.03125;
    return temp;
}


/**
    NOTE: This function blocks for 120+ milliseconds
*/
double Sensors::readLuminance()
{
	int ret;
	uint8_t buf[2];
    double luminance;

	buf[0] = 0x10;
	ret = SensorBus::instance()->write(I2C_ALS_ADDR, buf, 1);
	if (ret)
		return 0;
	sleepMilliseconds(120);
	ret = SensorBus::instance()->read(I2C_ALS_ADDR, buf, 2);
	if (ret)
		return 0;

	luminance = ((double)(buf[0] << 8 | buf[1])) / 1.2;
    return luminance;
}


void Sensors::run()
{
    MILLISECONDS lastUpdate = getMilliseconds();

    while (!_quit)
    {
        _temperature = readTemperature();
        if (_temperature > _maxTemperature)
            _maxTemperature = _temperature;

        // Only take the light level reading if the lights are off throughout the reading.
        while (true)
        {
            if (LightEffects::instance()->isOff())
            {
                MILLISECONDS start = getMilliseconds();
                double luminance = readLuminance();
                MILLISECONDS lightsOffTime = LightEffects::instance()->millisecondsSinceOn();
                MILLISECONDS elapsed = getMilliseconds() - start;
                if (lightsOffTime > elapsed)
                {
                    // Apply a simple first order filter so lights don't react quite as fast
                    // to abrupt changes in light levels
                    double diff = luminance - _luminance;
                    _luminance = _luminance + diff * LUMINANCE_FILTER_FACTOR;
                    break;
                }
            }
            sleepMilliseconds(500);
        }

        MILLISECONDS elapsed = getMilliseconds() - lastUpdate;
        if (elapsed < READ_INTERVAL)
            sleepMilliseconds(READ_INTERVAL - elapsed);

        lastUpdate += READ_INTERVAL;
    }
}

