/**
 *  @file   ColorEffect.h
 *  @date   Sep, 2012
 *
 *  Copyright (c) 2012, synapse.com
*/


#ifndef __COLOREFFECT__H_
#define __COLOREFFECT__H_


#include "LedEffect.h"
#include "rgbcolor.h"
#include "rgbcluster.h"


namespace Reader
{


/*
 *  ColorEffect - Displays a simple solid color effect
 */
class ColorEffect : public LedEffect
{
public:
    ColorEffect(RGBCluster* cluster);

    virtual int next();

    virtual uint32_t getMinTime()
    {
        // Color effects have no minimum display time
        return 0;
    }

    virtual bool isInterruptible()
    {
        // Always interruptible
        return true;
    }

    bool setColorEffect(const char* name);
    void setColor(unsigned r, unsigned g, unsigned b);

private:
    void doEffect();
    void setInnerColor(RGBColor& color);
    void setOuterColor(RGBColor& color);

private:
    bool started;
    RGBColor color;
    bool innerRing;
    bool outerRing;
};


} // namespace Reader


#endif // __COLOREFFECT__H_
