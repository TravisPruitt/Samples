#ifndef __SENSOR_BUS_H
#define __SENSOR_BUS_H

#include "I2C.h"

namespace Reader
{


class SensorBus : public I2C
{
public:
    static SensorBus* instance();

private:
    // singleton
    SensorBus();
    ~SensorBus();
};

}


#endif
