/**
 *  @file   LightEffects.h
 *  @date   Oct, 2011
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
*/


#ifndef __LIGHTEFFECTS_H
#define __LIGHTEFFECTS_H


#include <stdio.h>
#include <vector>

#include "Thread.h"
#include "Mutex.h"
#include "Event.h"
#include "ticks.h"
#include "rgb/rgbcluster.h"
#include "rgb/LedScript.h"


#define NO_EFFECT_TIMEOUT 0


namespace Reader
{

class LightEffects : public Thread
{
public:
    static LightEffects* instance();

    bool isValidLightEffect(const char* name);

    bool show(const char* name, uint32_t timeout = NO_EFFECT_TIMEOUT);
    bool showColor(const char* name, uint32_t timeout = NO_EFFECT_TIMEOUT);
    bool showColor(unsigned red, unsigned green, unsigned blue, uint32_t timeout = NO_EFFECT_TIMEOUT);
    void tap(const char* name, uint32_t timeout = NO_EFFECT_TIMEOUT);
    bool showScript(const char* buf, size_t size, uint32_t timeout = NO_EFFECT_TIMEOUT);
    bool showIdleEffect();

    void setColorCalibration(float r_factor, float r_offset,
                             float g_factor, float g_offset,
                             float b_factor, float b_offset);
    void adjustBrightness(double illuminance);
    void setBrightness(uint8_t brightness);

    uint8_t getBrightness()
    {
        return _cluster->getGlobalBrightness();
    }

    void getCurrentEffectName(std::string& name);

    void off();
    bool isOff();
    bool frameDrawnSinceLastOffCheck();

    void clearScriptCache();

private: // data
    Mutex _mutex;

    // Driver for RGB LEDs
    RGBCluster* _cluster;

    typedef std::map<const std::string, LedScript*> scriptcache_t;

    // Holds a cache of runnable led scripts
    scriptcache_t _scripts;

    // Holds a pointer to the currently executing effect or
    // NULL if off
    LedEffect* _currentEffect;

    // Indicates that the currently running script is temporary
    // and should be reaped on completion.
    bool _isEffectTemp;

    // Hold the time to timeout of the curently running effect
    // or NO_EFFECT_TIMEOUT if no timeout is set
    MILLISECONDS _timeout;

    // Holds queued effect or NULL if none
    LedEffect* _queuedEffect;

    // Indicates if the queued effect is temporary or not
    bool _isQueuedTemp;

    // Hold the time to timeout of the queued effect
    // or NO_EFFECT_TIMEOUT if no timeout is set
    MILLISECONDS _queuedTimeout;

    // Start time for currently running effect
    MILLISECONDS _startTime;

    // Flag to indicate that a new frame was drawn since the last isOff() read
    bool _frameDrawnSinceLastOffCheck;

private: // methods
    // singleton
    LightEffects();
    ~LightEffects();

    void doColorCalibration();

    void _show(LedEffect* effect, uint32_t timeout, bool isTemp);
    void _off(bool clear = true);
    void _clearScriptCache();
    bool _showIdleEffect();

    void setCurrentEffect(LedEffect* effect, uint32_t timeout, bool isTemp = false);
    void queueEffect(LedEffect* effect, uint32_t timeout, bool isTemp = false);
    void runQueuedEffect();
    void clearQueuedEffect();

    LedScript* findScript(const std::string& name);
    LedScript* cacheScript(const std::string& name, const std::string& path);

    int executeNext();
    void run();
};


} // namespace Reader


#endif // __LIGHTEFFECTS_H
