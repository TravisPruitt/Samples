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
     *  Reset effect to initial state. The position parameter is
     *  used to pass in the position of previous effect (if any).
     */
    virtual void reset(unsigned position)=0;

    /*
     *  Returns a string representation (name) of the effect.
     */
    virtual void getEffectName(std::string& name)=0;

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

    /*
     *  Returns true if the effect is locked and cannot be interrupted.
     */
    virtual bool isLocked()=0;

    /*
     *  Returns the current position of the effect or zero if N/A.
     */
    virtual unsigned getCurrentPosition()=0;

protected:
    RGBCluster* cluster;
};


} // namespace Reader


#endif // __LEDEFFECT__H_
