#ifndef __LEDEFFECT__H_
#define __LEDEFFECT__H_


#include "rgbcluster.h"


namespace Reader
{


/*
 * LedEffect - base class
 */
class LedEffect
{
public:
    LedEffect(RGBCluster* cluster)
    {
        this->cluster = cluster; 
    }

    virtual ~LedEffect() {}

    /* 
     *  Executes the next effect and returns the time
     *  until the next event in ms.
     */
    virtual int next()=0;

    /*
     *  Reset effect to initial state
     */
    virtual void reset()
    {
    }

    /*
     *  Returns the minimum number of milliseconds this
     *  effect must be on before it can be interrupted by
     *  certain events.
     */
    virtual uint32_t getMinTime()=0;

    /*
     *  Returns false if this effect cannot be interrupted by
     *  a tap event.
     */
    virtual bool isInterruptible()=0;

protected:
    RGBCluster* cluster;
};


} // namespace Reader


#endif // __LEDEFFECT__H_
