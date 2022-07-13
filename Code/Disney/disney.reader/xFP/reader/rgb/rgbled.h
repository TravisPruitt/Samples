#ifndef __RGBLED_H__
#define  __RGBLED_H__

#include "rgbleddriver.h"

namespace Reader
{


class RGBLed
{
public:
    typedef enum { RED, GREEN, BLUE } Color;

public:
    RGBLed();
    ~RGBLed();
    void setDriver(Color color, RGBLedDriver *chip, unsigned channel);
    void setColor(unsigned r, unsigned g, unsigned b);

private:
    // R G B
    RGBLedDriver *chip[3];
    unsigned channel[3];
    unsigned r, g, b; // cached
};


} // namespace Reader

#endif // __RGBLED_H__
