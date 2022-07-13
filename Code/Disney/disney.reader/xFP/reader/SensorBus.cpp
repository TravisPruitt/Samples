/**
    SensorBus.cpp
    Dec 2011
    Greg Strange

    Copyright (c) 2011, synapse.com

    This is the I2C bus used by the sensors and light controls.  It is implemented as a
    singleton because it is needed by both the Sensors class and the class that controls
    lighting.
*/


#include "SensorBus.h"

using namespace Reader;


SensorBus::SensorBus() : I2C(I2C_SENSOR_BUS)
{
}

SensorBus::~SensorBus()
{
}

SensorBus* SensorBus::instance()
{
    static SensorBus _instance;
    return &_instance;
}
