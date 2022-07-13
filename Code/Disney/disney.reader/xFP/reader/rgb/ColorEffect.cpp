/**
 *  @file   ColorEffect.cpp
 *  @date   Sep, 2012
 *
 *  Copyright (c) 2012, synapse.com
*/


#include <string.h>
#include <sstream>

#include "ColorEffect.h"


namespace Reader
{


ColorEffect::ColorEffect(RGBCluster* cluster) :
    LedEffect(cluster), started(false), innerRing(true), outerRing(true)
{
}


int ColorEffect::next()
{
    if (!started)
    {
        doEffect();
        started = true;
    }

    // No need to update so set the next interval high
    return 10000;
}


void ColorEffect::reset(unsigned position)
{
    position = position; // silence compiler warning
}


void ColorEffect::getEffectName(std::string& name)
{
    std::stringstream ss;
    ss << color;
    name = ss.str();
}


uint32_t ColorEffect::getMinTime()
{
    return 0;
}


bool ColorEffect::isInterruptible()
{
    return true;
}


bool ColorEffect::isLocked()
{
    return false;
}


unsigned ColorEffect::getCurrentPosition()
{
    return 0;
}


static bool colors_set = false;


struct named_color
{
    named_color(const char* name, unsigned r, unsigned g, unsigned b)
    {
        this->name = name;
        this->color = RGBColor(r, g, b);
    }

    const char* name;
    RGBColor color;
};

typedef std::vector<named_color> color_list;
static color_list color_mapping;

/**
 *  Generate the named color table
 */
static void generateNamedColors()
{
    color_mapping.push_back(named_color("off", 0, 0, 0));
    color_mapping.push_back(named_color("red", 255, 0, 0));
    color_mapping.push_back(named_color("green", 0, 255, 0));
    color_mapping.push_back(named_color("blue", 0, 0, 255));
    color_mapping.push_back(named_color("yellow", 255, 255, 0));
    color_mapping.push_back(named_color("white", 255, 255, 255));
    colors_set = true;
}


/*
 *  Try to match the color in name to a known color. For
 *  now this simple means that if the word exists anywhere
 *  in the string we consider that a match. This won't work
 *  well for complex color names.
 */
static RGBColor* matchColor(const std::string& name)
{
    if (!colors_set)
        generateNamedColors();

    for (color_list::iterator it = color_mapping.begin(); it != color_mapping.end(); ++it)
    {
        if ( name.find(it->name) != std::string::npos )
            return &it->color;
    }

    // Didn't match anything
    return NULL;
}


/*
 *  Clear the named colors list. The list will be regenerated on first access
 */
void ColorEffect::resetNamedColors()
{
    color_mapping.clear();
    colors_set = false;
}


/*
 *  Set the effect
 *
 *  @param  name  name of the effect
 *
 *  @return True if the effect name is valid
 */
bool ColorEffect::setColorEffect(const char* name)
{
    RGBColor* c = matchColor(name);
    if (c)
    {
        color = *c;

        innerRing = outerRing = true;
        if (strstr(name, "inner") )
            outerRing = false;
        else if (strstr(name, "outer") )
            innerRing = false;

        return true;
    }
    else
        return false;
}


void ColorEffect::setColor(unsigned r, unsigned g, unsigned b)
{
    color.setColor(r, g, b);
}


/*
 *  Parses and displays the set effect
 */
void ColorEffect::doEffect()
{
    // Set the color at the appropriate place
    if (!outerRing)
        setInnerColor(color);
    else if (!innerRing)
        setOuterColor(color);
    else
    {
        cluster->setColorAll(color);
        cluster->update();
    }
}


/**
 *  Sets the inner ring to the given color
 */
void ColorEffect::setInnerColor(RGBColor& color)
{
    cluster->clearAll();
    for (int i = 24; i <= 47; ++i)
        cluster->setColor(i, color);
    cluster->update();
}


/*
 *  Sets the outer ring to the given color
 */
void ColorEffect::setOuterColor(RGBColor& color)
{
    cluster->clearAll();
    for (int i = 0; i <= 23; ++i)
        cluster->setColor(i, color);
    cluster->update();
}


} // namespace Reader
