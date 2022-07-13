/**
 *  @file   Sensors.cpp
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include "Sensors.h"
#include "DapReader.h"
#include "DapConfig.h"
#include "SensorBus.h"
#include "LightEffects.h"
#include "ticks.h"
#include "log.h"


#define I2C_SENSOR_BUS		3
#define I2C_THERMISTOR_ADDR	0x4B
#define I2C_ALS_ADDR		0x23


#define READ_INTERVAL             1000    // milliseconds
#define ILLUMINANCE_FILTER_FACTOR 0.3     // simple first order filter


using namespace Reader;


Sensors::Sensors() : 
    _temperature(0), 
    _maxTemperature(0),
    _illuminance(300)
{
    _temperatureLimit = DapConfig::instance()->getValue("max temperature", 70.0);
    resetAmbientLightSensor();
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
double Sensors::getIlluminance()
{
    return _illuminance;
}


double Sensors::readTemperature()
{
	int16_t value;
    uint8_t buf[2];
    double temp;

    if (SensorBus::instance()->read(I2C_THERMISTOR_ADDR, buf, 2) < 0)
    {
        DapReader::instance()->setStatus(IStatus::Yellow, "Unable to read temperature sensor");
        return 0;
    }

    value = (buf[0] << 8) + buf[1];
	value >>= 3;
	temp = ((double)value) * 0.03125;
    return temp;
}


void Sensors::updateIlluminance()
{
    // Only take the light level reading if all LEDs are off.
    if (LightEffects::instance()->isOff())
    {
        // Wait a minimum amout of time for things to settle after the LEDs
        // go off or we can get occasional high reads
        sleepMilliseconds(120);

        double illuminance = readIlluminance();

        // Throw away result if the light came on during a sensor read
        if (LightEffects::instance()->frameDrawnSinceLastOffCheck())
            return;

        // Apply a simple first order filter so lights don't react quite as fast
        // to abrupt changes in light levels
        double diff = illuminance - _illuminance;
        _illuminance = _illuminance + diff * ILLUMINANCE_FILTER_FACTOR;
    }
}


void Sensors::resetAmbientLightSensor()
{
    uint8_t buf[] = { 0x07 };
	int ret = SensorBus::instance()->write(I2C_ALS_ADDR, buf, 1);
	if (ret)
        LOG_WARN("Unable to reset light sensor");
}


/**
 *  NOTE: This function blocks for 120+ milliseconds
*/
double Sensors::readIlluminance()
{
	int ret;
	uint8_t buf[2];
    double illuminance;

	buf[0] = 0x10;
	ret = SensorBus::instance()->write(I2C_ALS_ADDR, buf, 1);
	if (ret)
    {
        DapReader::instance()->setStatus(IStatus::Yellow, "Unable to read light sensor");
		return 0;
    }

	sleepMilliseconds(120);

	ret = SensorBus::instance()->read(I2C_ALS_ADDR, buf, 2);
	if (ret)
    {
        DapReader::instance()->setStatus(IStatus::Yellow, "Unable to read light sensor");
		return 0;
    }

	illuminance = ((double)(buf[0] << 8 | buf[1])) / 1.2;
    return illuminance;
}


void Sensors::run()
{
    MILLISECONDS lastUpdate = getMilliseconds();

    while (!_quit)
    {
        _temperature = readTemperature();
        if (_temperature > _maxTemperature)
            _maxTemperature = _temperature;
        //LOG_TRAFFIC("Temperature: %.2f", _temperature);
        
        // Only attempt to read the ambient light sensor if light effects
        // are enabled. 
        if (!DapReader::instance()->lightsDisabled())
        {
            updateIlluminance();
            if (DapConfig::instance()->getBrightnessSetting() == AUTO_BRIGHTNESS)
                LightEffects::instance()->adjustBrightness(_illuminance);
            //LOG_TRAFFIC("Illuminance: %.2f", _illuminance);
        }
            
        MILLISECONDS elapsed = getMilliseconds() - lastUpdate;
        
        if (elapsed < READ_INTERVAL)
            _event.wait(READ_INTERVAL - elapsed);
        
        lastUpdate += READ_INTERVAL;
    }
}
