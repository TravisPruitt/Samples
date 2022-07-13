#include <stdint.h>
#include <stdio.h>
#include <iostream>
#include "rgbleddriver.h"
#include "rgbled.h"

namespace Reader
{

RGBLed::RGBLed() : r(0), g(0), b(0)
{
}

RGBLed::~RGBLed()
{
}

void RGBLed::setDriver(Color color, RGBLedDriver *chip, unsigned channel)
{
    this->chip[color] = chip;
    this->channel[color] = channel;
}

void RGBLed::setColor(unsigned r, unsigned g, unsigned b)
{
    if(this->r != r) {
        this->chip[RED]->setBrightness(channel[RED], r);
        this->r = r;
    }
    if(this->g != g) {
        this->chip[GREEN]->setBrightness(channel[GREEN], g);
        this->g = g;
    }
    if(this->b != b) {
        this->chip[BLUE]->setBrightness(channel[BLUE], b);
			this->b = b;
    }
}

} // namespace Reader
