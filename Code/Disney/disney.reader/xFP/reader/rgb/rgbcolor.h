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

    static void setColorCalibration(float r_fact, float r_off,
                                    float g_fact, float g_off,
                                    float b_fact, float b_off);

public:
    void setColor(unsigned r, unsigned g, unsigned b);

    friend std::ostream& operator<< (std::ostream &out, const RGBColor &color);
    friend std::ostream& operator<< (std::ostream &out, RGBColor &color);

private:
    static float r_factor;
    static float r_offset;
    static float g_factor;
    static float g_offset;
    static float b_factor;
    static float b_offset;
};

typedef std::map<const std::string, RGBColor> colormap_t;

};


#endif //  __RGBCOLOR_H__
