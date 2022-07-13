#ifndef __RGBLEDDRIVER_H__
#define __RGBLEDDRIVER_H__

#include "I2C.h"

namespace Reader
{

class RGBLedDriver
{
public:
    virtual int init()=0;

    virtual void setBrightness(unsigned channel,
                               unsigned brightness)=0;

    virtual void setGlobalBrightness(uint8_t value)=0;

    virtual void setGlobalBrightnessFactor(float factor)=0;

    virtual int update() = 0;
    virtual int forceUpdate() = 0;
};


} // namespace Reader

#endif // __RGBLEDDRIVER_H__
