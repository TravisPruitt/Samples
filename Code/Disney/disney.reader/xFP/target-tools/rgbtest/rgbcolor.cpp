#include <iostream>
#include <algorithm>
#include <stdio.h>
#include "rgbcolor.h"

namespace Reader
{

RGBColor::RGBColor(unsigned r, unsigned g, unsigned b)
{
    // Apply a linear scaling factor to Red and Blue
    // values unless we are already at extremly low
    // values.
	this->r = r > 8 ? (r * 0.85) : r;
    this->g = g;
	this->b = b > 8 ? (b * 0.5) : b;
}
    
RGBColor::RGBColor() : r(0), g(0), b(0)
{
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
