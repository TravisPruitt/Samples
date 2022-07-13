#include <iostream>
#include "rgbcolor.h"
#include "log.h"


// Calibration factor defaults
#define DEFAULT_RED_FACTOR    0.6
#define DEFAULT_RED_OFFSET    4
#define DEFAULT_GREEN_FACTOR  1.00
#define DEFAULT_GREEN_OFFSET  0
#define DEFAULT_BLUE_FACTOR   0.40
#define DEFAULT_BLUE_OFFSET   4


namespace Reader
{


// Set calibration values to defaults
float RGBColor::r_factor = DEFAULT_RED_FACTOR;
float RGBColor::r_offset = DEFAULT_RED_OFFSET;
float RGBColor::g_factor = DEFAULT_GREEN_FACTOR;
float RGBColor::g_offset = DEFAULT_GREEN_OFFSET;
float RGBColor::b_factor = DEFAULT_BLUE_FACTOR;
float RGBColor::b_offset = DEFAULT_BLUE_OFFSET;


RGBColor::RGBColor(unsigned r, unsigned g, unsigned b)
{
    setColor(r, g, b);
}

    
RGBColor::RGBColor() : r(0), g(0), b(0)
{
}


void RGBColor::setColor(unsigned r, unsigned g, unsigned b)
{
    // Apply a linear scaling factor and offset to
    // the color values.
    this->r = r ? (r * r_factor) + r_offset : 0;
    this->g = g ? (g * g_factor) + g_offset : 0;
    this->b = b ? (b * b_factor) + b_offset : 0;
}


void RGBColor::setColorCalibration(float r_fact, float r_off,
                                   float g_fact, float g_off,
                                   float b_fact, float b_off)
{
    // Make sure the calibration values are set and within range
    r_factor = (r_fact >= 0.0 || r_fact <= 1.0) ? r_fact : DEFAULT_RED_FACTOR;
    r_offset = (r_off >= 0    || r_off <= 255)  ? r_off  : DEFAULT_RED_OFFSET;
    g_factor = (g_fact >= 0.0 || g_fact <= 1.0) ? g_fact : DEFAULT_GREEN_FACTOR;
    g_offset = (g_off >= 0    || g_off <= 255)  ? g_off  : DEFAULT_GREEN_OFFSET;
    b_factor = (b_fact >= 0.0 || b_fact <= 1.0) ? b_fact : DEFAULT_BLUE_FACTOR;
    b_offset = (b_off >= 0    || b_off <= 255)  ? b_off  : DEFAULT_BLUE_OFFSET;

    LOG_DEBUG("Color calibration:");
    LOG_DEBUG("Red:   %f, %f", (double)r_factor, (double)r_offset);
    LOG_DEBUG("Green: %f, %f", (double)g_factor, (double)g_offset);
    LOG_DEBUG("Blue:  %f, %f", (double)b_factor, (double)b_offset);
}


std::ostream& operator<< (std::ostream &out, const RGBColor &color)
{
    out << "RGBColor(" << color.r << "," << color.g << "," << color.b << ")";
    return out;
}

     
std::ostream& operator<< (std::ostream &out, RGBColor &color)
{
    return operator<<(out, const_cast<const RGBColor&>(color));
}


} // namespace Reader
