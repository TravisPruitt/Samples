/**
 *  @file   LightEffects.cpp
 *  @date   Oct, 2011
 *  @author Greg Strange
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#include "standard.h"
#include "LightEffects.h"
#include "rgb/ColorEffect.h"
#include "log.h"
#include "ticks.h"
#include "DapConfig.h"
#include "SensorBus.h"
#include "Sensors.h"
#include "Sound.h"
#include "BiometricReader.h"
#include "FileSystem.h"
#include "Media.h"
#include "DapReader.h"

#include <math.h>
#ifndef _WIN32
#include <strings.h>
#endif


#define NO_EVENT_TIMEOUT      -1

#define MIN_ILLUMINANCE  10.0    // Typical office ambient light
#define MAX_ILLUMINANCE  400.0   // Full daylight (not direct sunlight)
#define MIN_BRIGHTNESS   0x80
#define MAX_BRIGHTNESS   0xFF
#define BRIGHTNESS_SLOPE (MAX_BRIGHTNESS-MIN_BRIGHTNESS)/(MAX_ILLUMINANCE-MIN_ILLUMINANCE)


namespace Reader
{


LightEffects::LightEffects() :
    _currentEffect(NULL), _isEffectTemp(false), _timeout(NO_EFFECT_TIMEOUT),
    _queuedEffect(NULL), _isQueuedTemp(false), _queuedTimeout(NO_EFFECT_TIMEOUT)
{
    _startTime = getMilliseconds();
}


LightEffects::~LightEffects()
{
    if (_cluster)
    {
        _clearScriptCache();
        _cluster->reset();
        delete _cluster;
        _cluster = NULL;
    }
}


LightEffects* LightEffects::instance()
{
    static LightEffects _instance;
    return &_instance;
}


bool LightEffects::isValidLightEffect(const char* name)
{
    Lock lock(_mutex);
    return _cluster ? (findScript(name) != NULL) : false;
}


/**
 *  Starts execution of the named script/effect.
 *
 *  @param   name     Name of the script/effect
 *  @param   timeout  Timeout in ms (0 == NO_EFFECT_TIMEOUT)
 *
 *  @return  True if successful and false otherwise
 */
bool LightEffects::show(const char* name, uint32_t timeout)
{
    Lock lock(_mutex);

    if (!_cluster)
        return false;

    LOG_DEBUG("Lights (sequence) '%s', Timeout %d", name, timeout);
    LedEffect* effect = findScript(name);
    if (!effect)
        return false;

    _show(effect, timeout, false);

    return true;
}


/**
    Show or queue up an effect

    @param  effect      Effect to show or queue
    @param  timeout     Timeout for the effect
    @param  isTemp      true if the effect should be freed when finished playing

*/
void LightEffects::_show(LedEffect* effect, uint32_t timeout, bool isTemp)
{
    // Don't play effects immediately if the current effects display
    // time is less than it's minimum display time.
    MILLISECONDS runTime = getMilliseconds() - _startTime;
    if (_currentEffect)
    {
        if (runTime < _currentEffect->getMinTime())
        {
            LOG_DEBUG("Current effect start time < minimum display time");
            queueEffect(effect, timeout, isTemp);
            return;
        }
        else if (_currentEffect->isLocked())
        {
            LOG_DEBUG("Current effect locked");
            queueEffect(effect, timeout, isTemp);
            return;
        }
    }

    setCurrentEffect(effect, timeout, isTemp);
    _event.signal();
}


/*
 *  Display a simple color effect defined by name
 *
 *  @param   name     Name of the color
 *  @param   timeout  Timeout in ms (0 == NO_EFFECT_TIMEOUT)
 *
 *  @return  True if successful and false otherwise
 */
bool LightEffects::showColor(const char* name, uint32_t timeout)
{
    Lock lock(_mutex);

    if (!_cluster)
        return false;

    LOG_DEBUG("Lights (color) '%s', Timeout %d", name, timeout);
    ColorEffect* effect = new ColorEffect(_cluster);
    if (!effect->setColorEffect(name))
    {
        delete effect;
        return false;
    }

    _show(effect, timeout, true);
    return true;
}


/*
 *  Display a simple color effect defined by RGB values
 *
 *  @param   r        Red value
 *  @param   g        Green value
 *  @param   b        Blue value
 *  @param   timeout  Timeout in ms (0 == NO_EFFECT_TIMEOUT)
 *
 *  @return  True if successful
 */
bool LightEffects::showColor(unsigned red, unsigned green, unsigned blue, uint32_t timeout)
{
    Lock lock(_mutex);

    if (!_cluster)
        return false;

    LOG_DEBUG("show color %d, %d, %d, timeout %d", red, green, blue, timeout);
    ColorEffect* effect = new ColorEffect(_cluster);
    effect->setColor(red, green, blue);

    _show(effect, timeout, true);
    return true;
}


/**
 *  A tap happened: Show the selected lighting effect, but only if the current
 *  effect can be interrupted by a tap.
 *
 *  @param   name     Name of the color
 *  @param   timeout  Timeout in ms (0 == NO_EFFECT_TIMEOUT)
*/
void LightEffects::tap(const char* name, uint32_t timeout)
{
    Lock lock(_mutex);

    if(!_cluster)
        return;

    LOG_DEBUG("Lights (tap) '%s', Timeout %d", name, timeout);
    LedEffect* effect = findScript(name);
    if (!effect)
        return;

    // Ignore if the current or queued effect is uninterruptible
    if ((_currentEffect && !_currentEffect->isInterruptible()) ||
        (_queuedEffect && !_queuedEffect->isInterruptible()))
    {
        LOG_DEBUG("Ignoring: current or queued effect is not interruptable");
        return;
    }

    BiometricReader::instance()->lightOff();
    clearQueuedEffect();
    setCurrentEffect(effect, timeout);
    _event.signal();
}


/*
 *  Starts execution of a buffered script
 *
 *  @param   buf      Pointer to in memory script
 *  @param   size     Length of buffer
 *  @param   timeout  Timeout in ms (0 == NO_EFFECT_TIMEOUT)
 *
 *  @return  True if successful and false otherwise
 *
 *  This call stops any current script and tosses any queued script, regardless
 *  of min timeouts or script locks.
 */

bool LightEffects::showScript(const char* buf, size_t size, uint32_t timeout)
{
    Lock lock(_mutex);

    if (!_cluster)
        return false;

    LOG_DEBUG("Lights (script), Timeout %d", timeout);

    LedScript* script = new LedScript(_cluster);
    if (!script->loadBuffer(buf, size))
    {
        LOG_DEBUG("Unable to load script");
        delete script;
        return false;
    }

    _show(script, timeout, true);
    return true;
}


/*
 *  Displays the configured idle effect if one is set
 *
 *  @return  true if the idle effect is set
 */
bool LightEffects::showIdleEffect()
{
    Lock lock(_mutex);

    if (!_cluster)
        return false;

    if(_showIdleEffect())
    {
        _event.signal();
        return true;
    }

    return false;
}


bool LightEffects::_showIdleEffect()
{
    std::string effectName = Media::getIdleEffectName();
    if (!effectName.empty())
    {
        LedEffect* effect = findScript(effectName.c_str());
        if (effect)
        {
            LOG_DEBUG("Showing idle effect: %s", effectName.c_str());
            setCurrentEffect(effect, NO_EFFECT_TIMEOUT);
            return true;
        }
    }

    return false;
}


/*
 *  Adjusts the RGB color calibration factors
 */
void LightEffects::setColorCalibration(float r_factor, float r_offset,
                                       float g_factor, float g_offset,
                                       float b_factor, float b_offset)
{
    Lock lock(_mutex);

    if (!_cluster)
        return;

    _clearScriptCache();
    ColorEffect::resetNamedColors();
    RGBColor::setColorCalibration(r_factor, r_offset,
                                  g_factor, g_offset,
                                  b_factor, b_offset);
}


/*
 *  Adjusts the LED brightness based on the ambient light sensor
 *  readings using a linear function.
 */
void LightEffects::adjustBrightness(double illuminance)
{
    double lum;

    if (!_cluster)
        return;

    if (illuminance < MIN_ILLUMINANCE)
        lum = MIN_ILLUMINANCE;
    else if (illuminance > MAX_ILLUMINANCE)
        lum = MAX_ILLUMINANCE;
    else
        lum = illuminance;

    unsigned brightness = MIN_BRIGHTNESS + BRIGHTNESS_SLOPE*(lum - MIN_ILLUMINANCE);

    // enforce bounds - can be off due to truncation/rounding
    if (brightness > MAX_BRIGHTNESS)
        brightness = MAX_BRIGHTNESS;
    else if (brightness < MIN_BRIGHTNESS)
        brightness = MIN_BRIGHTNESS;

    setBrightness((uint8_t)brightness);
}


void LightEffects::setBrightness(uint8_t brightness)
{
    _mutex.lock();
    _cluster->setGlobalBrightness(brightness);
    _mutex.unlock();
}


void LightEffects::getCurrentEffectName(std::string& name)
{
    _mutex.lock();
    if(!_currentEffect)
        name = "RGBColor(0,0,0)";
    else
        _currentEffect->getEffectName(name);
    _mutex.unlock();
}


/**
 *  Stops execution of a script (if running), turns off all LEDS, and clears any queued effect
 */
void LightEffects::off()
{
    if(!_cluster)
        return;

    LOG_INFO("Lights off");
    _mutex.lock();
    clearQueuedEffect();
    _off();
    _mutex.unlock();
}


void LightEffects::_off(bool clear)
{
    if (_currentEffect && _isEffectTemp)
        delete _currentEffect;
    _currentEffect = NULL;
    _isEffectTemp = false;
    if (clear)
    {
        _cluster->clearAll();
        _cluster->update();
    }
    _startTime = getMilliseconds();
}


/**
 *  Indicates whether LEDs are all turned off.
 *
 *  @return  True if all LEDs are currently off.
 */
bool LightEffects::isOff()
{
    if (!_cluster)
        return false;

    Lock lock(_mutex);
    _frameDrawnSinceLastOffCheck = false;
    return _cluster->isAllOff();
}


/**
 *  Indicates whether the LEDs have been updated since the last isOff() check.
 */
bool LightEffects::frameDrawnSinceLastOffCheck()
{
    if (!_cluster)
        return false;
    
    Lock lock(_mutex);
    return _frameDrawnSinceLastOffCheck;
}


/**
 *  Clears and deallocates the led script cache
 */
void LightEffects::clearScriptCache()
{
    _mutex.lock();
    _clearScriptCache();
    _mutex.unlock();
}


void LightEffects::_clearScriptCache()
{
    _off();
    scriptcache_t::iterator it;
    for(it = _scripts.begin(); it != _scripts.end(); it++)
        delete (*it).second;
    _scripts.clear();
}


/*
 *  Sets the color calibration from the Camilla EEPROM
 */
void LightEffects::doColorCalibration()
{
    const RemoteEEPROM& eeprom = DapReader::instance()->getCamillaEEPROMData();
    RGBColor::setColorCalibration(eeprom.u.camilla.redSlope,
                                  eeprom.u.camilla.redOffset,
                                  eeprom.u.camilla.greenSlope,
                                  eeprom.u.camilla.greenOffset,
                                  eeprom.u.camilla.blueSlope,
                                  eeprom.u.camilla.blueOffset);
}


/*
 *  Sets the current effect and signals the thread to start
 *
 *  @param  effect   The effect to display
 *  @param  timeout  The effect timeout
 *  @param  isTemp   Indicates whether to reap the LedEffect
 *                   object upon completion
 */
void LightEffects::setCurrentEffect(LedEffect* effect, uint32_t timeout, bool isTemp)
{
    Sound::instance()->stopPlay();
    unsigned pos = 0;
    if (_currentEffect)
        pos = _currentEffect->getCurrentPosition();
    _off(false);
    effect->reset(pos);
    _currentEffect = effect;
    _timeout = timeout;
    _isEffectTemp = isTemp;
    _startTime = 0;
}


/*
 *  Queues the specified effect if another effect is not already queued
 *
 *  @param  effect   The effect to queue
 *  @param  timeout  The effect timeout
 *  @param  isTemp   Indicates whether to reap the LedEffect
 *                   object upon completion
 */
void LightEffects::queueEffect(LedEffect* effect, uint32_t timeout, bool isTemp)
{
    LOG_DEBUG("Adding effect to queue");
    clearQueuedEffect();
    _queuedEffect = effect;
    _queuedTimeout = timeout;
    _isQueuedTemp = isTemp;
}


/**
 *  Runs the queued effect
 */
void LightEffects::runQueuedEffect()
{
    LOG_DEBUG("Running queued effect (timeout=%d)", _queuedTimeout);
    setCurrentEffect(_queuedEffect, _queuedTimeout, _isQueuedTemp);
    _queuedEffect = NULL;
    _queuedTimeout = NO_EFFECT_TIMEOUT;
    _isQueuedTemp = false;
}


/**
 *  Clears queued effect
 *
 *  This routine will remove the queued effect and deletes
 *  the instance if it's temporary, so it shouldn't be used
 *  in places where the effect is later needed. 
 */
void LightEffects::clearQueuedEffect()
{
    if (_queuedEffect && _isQueuedTemp)
        delete _queuedEffect;

    _queuedEffect = NULL;
    _queuedTimeout = NO_EFFECT_TIMEOUT;
    _isQueuedTemp = false;
}


/**
 *  Searches for the named led script.
 *
 *  This routine will first search the cache and then the
 *  filesystem and load it if it isn't cached yet.
 *
 *  @param   name  Name of the script to search for
 *
 *  @return  A pointer to a LedScript object or NULL
 *           if unable to find it.   
 */
LedScript* LightEffects::findScript(const std::string& name)
{
    // Return cached script if it exists
    if (_scripts.find(name) != _scripts.end())
    {
        LOG_DEBUG("Found cached LED script");
        LedScript* script = _scripts[name];
        return script;
    }

    // First look for script file in data directory
    std::string path = FileSystem::getMediaPath();
    path += "/ledscripts/" + name + ".csv";
    if (FileSystem::fileExists(path.c_str()))
        return cacheScript(name, path);

    // Next fall back to default location
    path = FileSystem::getAppPath();
    path += "/ledscripts/" + name + ".csv";
    if (FileSystem::fileExists(path.c_str()))
        return cacheScript(name, path);

    LOG_DEBUG("Unable to locate LED script: %s", name.c_str());
    return NULL;
}


/**
 *  Loads a script from a file into the script cache under
 *  the given name.
 *
 *  @param   name  Name of the script
 *  @param   path  Path to the script file
 *
 *  @return  A pointer to a LEDScript object or NULL if
 *           an error occurs.
 */
LedScript* LightEffects::cacheScript(const std::string& name, const std::string& path)
{
    LOG_DEBUG("Loading script into cache: %s", path.c_str());

    LedScript *newScript = new LedScript(_cluster);
    if (!newScript->loadFile(path.c_str()))
    {
        LOG_DEBUG("Failed to load script: %s", path.c_str());
        delete newScript;
        return NULL;
    }
    newScript->setEffectName(name.c_str());
    _scripts[name] = newScript;

    return newScript;
}


/**
 *  Runs the next LED script command
 *
 *  @return  Returns the delay in ms to the next event.
 */
int LightEffects::executeNext()
{
    MILLISECONDS cmdStartTime = getMilliseconds();

    if (!_startTime)
        _startTime = cmdStartTime;
    
    // Display queued effect as soon as current effects run time is
    // greater than it's min run time except when the current effect
    // is locked.
    MILLISECONDS runTime = cmdStartTime - _startTime;
    if (_queuedEffect &&
        runTime >= _currentEffect->getMinTime() &&
        !_currentEffect->isLocked())
    {
        runQueuedEffect();
        runTime = 0;
    }
    
    // Execute the next script command
    int nextEvent = _currentEffect->next();
    if (_cluster->getErrorStatus())
    {
        DapReader::instance()->setStatus(IStatus::Red, "LED error");
        _cluster->clearErrorStatus();
    }

    // Calculate the command execution time and adjust the delay
    MILLISECONDS delta = getMilliseconds() - cmdStartTime;
    if (nextEvent > 0)
    {
        if ((int)delta > nextEvent)
            nextEvent = 0;
        else
            nextEvent -= delta;
    }

    if ((nextEvent < 0) ||
        (_timeout && runTime >= _timeout))
    {
        // We have timed out or reached the end of the script
        LOG_DEBUG("Light effect terminating");

        BiometricReader::instance()->lightOff();

        if (_queuedEffect)
        {
            runQueuedEffect();
        }
        else if (_showIdleEffect())
        {
            nextEvent = 0;
        }
        else
        {
            _off();
            nextEvent = NO_EVENT_TIMEOUT;
        }
    }
    else if ( _timeout &&
              (int)(_timeout - runTime) < nextEvent )
    {
        // timeout occurs before end on next event so truncate
        nextEvent = _timeout - runTime;
    }

    return nextEvent;
}


/*
 *  Main thread
 */
void LightEffects::run()
{
    int eventTimeout = NO_EVENT_TIMEOUT;

    _mutex.lock();

    // Get color calibration factors from camilla EEPROM and apply them
    doColorCalibration();

    _cluster = new RGBCluster;
    _cluster->init(true, 0x6F);

    // Set brightness level, MAX_BRIGHTNESS if in auto
    int br = DapConfig::instance()->getBrightnessSetting();
    _cluster->setGlobalBrightness(br == AUTO_BRIGHTNESS ? MAX_BRIGHTNESS : br);

    _mutex.unlock();

    while (!_quit)
    {
        if (Sensors::instance()->overTemp() && _currentEffect != NULL)
        {
            off();
            eventTimeout = NO_EVENT_TIMEOUT;
            LOG_WARN("Over max temp (T>%.1f)", Sensors::instance()->getTemperatureLimit());
            DapReader::instance()->setStatus(IStatus::Yellow, "Temperature high");
        }

        _mutex.lock();
        if ( _currentEffect )
        {
            // Execute the next script command
            _frameDrawnSinceLastOffCheck = true;
            eventTimeout = executeNext();
        }
        else
        {
            eventTimeout = NO_EVENT_TIMEOUT;
        }
        _mutex.unlock();

        if (eventTimeout > 0)
            _event.wait(eventTimeout);
        else if (eventTimeout == NO_EVENT_TIMEOUT)
            _event.wait();
    }
}


} // namespace Reader
