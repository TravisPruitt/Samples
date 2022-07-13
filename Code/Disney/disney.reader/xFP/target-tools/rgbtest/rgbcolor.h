#ifndef __RGBCOLOR_H__
#define __RGBCOLOR_H__

#include <map>

namespace Reader
{

class RGBColor
{
public:
    RGBColor(unsigned r, unsigned g, unsigned b);
    RGBColor();
    unsigned r, g, b;

public:
    friend std::ostream& operator<< (std::ostream &out, const RGBColor &color);
    friend std::ostream& operator<< (std::ostream &out, RGBColor &color);
};

typedef std::map<const std::string, RGBColor> colormap_t;

};
#endif //  __RGBCOLOR_H__
