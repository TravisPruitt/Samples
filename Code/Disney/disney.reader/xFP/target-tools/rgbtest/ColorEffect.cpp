/**
 *  @file   ColorEffect.cpp
 *  @date   Sep, 2012
 *
 *  Copyright (c) 2012, synapse.com
*/


#include <string.h>

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

struct named_colors_t
{
    const char* name;
    RGBColor color;
};


/*
 *  Try to match the color in name to a known color. For
 *  now this simple means that if the word exists anywhere
 *  in the string we consider that a match. This won't work
 *  well for complex color names.
 */
static RGBColor* matchColor(const std::string& name)
{
    static struct named_colors_t color_mapping[] = {
        { "red",    RGBColor(255, 0, 0)     },
        { "green",  RGBColor(0, 255, 0)     },
        { "blue",   RGBColor(0, 0, 255)     },
        { "yellow", RGBColor(255, 255, 0)   },
        { "white",  RGBColor(255, 255, 255) }
    };

    for (unsigned i = 0; i < (sizeof(color_mapping)/sizeof(color_mapping[0])); ++i)
        if ( name.find(color_mapping[i].name) != std::string::npos )
            return &color_mapping[i].color;

    // Didn't match anything
    return NULL;
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
    color.r = r;
    color.g = g;
    color.b = b;
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
