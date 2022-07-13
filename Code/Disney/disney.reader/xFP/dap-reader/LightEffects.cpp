/**
    @file   LightEffects.cpp
    @author Greg Strange
    @date   Oct, 2011

    Copyright (c) 2011, synapse.com
*/




#include "standard.h"
#include "LightEffects.h"
#include "log.h"
#include "ticks.h"
#include "DapConfig.h"
#include "SensorBus.h"
#include "Sensors.h"
#include "Sound.h"
#include "BiometricReader.h"

#include <math.h>

#ifndef _WIN32
#include <strings.h>
#endif


using namespace Reader;


// This value exist so that the thread will quit within a reasonable amount of time when
// the program exits.
#define MAX_TIMEOUT      250

#define I2C_LED_ADDR      98
#define REG_PCA9533_LS0   5



static const double DefaultAmbientThresholds[] = {4500};
static const int DefaultGreenLevels[] = {10, 40};
static const int DefaultBlueLevels[] = {10, 40};
static const int DefaultBioLevels[] = {10, 30};
//static const int BioBlueLevels[] = {15, 50};
//static const int BioGreenLevels[] = {15, 70};
//static const int BioWhiteLevels[] = {10, 20};


const int ThinkingTiming[] = {250, 250, -2};
const int ExceptionTiming[] = {300, 180, 300, 180, 300, 720, 300, 180, 300, 180, 300, 720, 300, 180, 300, 180, 300, 720, -6};
const int ScanTiming[] = {120, 70, 120, 70, 120, 70, 10};
const int RetryTiming[] = {750, 50};
const int LoginOkTiming[] = {100, 200, 100, 10};
const int SolidOn[] = {10};    // use for any sequence that wants to run LEDs on and leave them on

static const struct LightSequence sequences[] = {

    {
        "green", NULL,                          // name, sound
        SolidOn, ArrayLength(SolidOn),          // Timing array, length of timing array
        Color::Green, Color::Green, Color::Off, // Colors: outer ring, inner ring, biometric reader
        0, false, NULL                          // minu duration, ignore tap, sequence to chain to 
    },
    {
        "outer_green", NULL,
        SolidOn, ArrayLength(SolidOn),
        Color::Green, Color::Off, Color::Off,
        0, false, NULL
    },
    {
        "inner_green", NULL,
        SolidOn, ArrayLength(SolidOn),
        Color::Off, Color::Green, Color::Off,
        0, false, NULL
    },
    {
        "blue", NULL,
        SolidOn, ArrayLength(SolidOn),
        Color::Blue, Color::Off, Color::Off,
        0, false, NULL
    },
    {
        "thinking", "tap.wav",
        ThinkingTiming, ArrayLength(ThinkingTiming),
        Color::Green, Color::Off, Color::Off,
        1000, false, NULL
    },
    {
        "entry_exception", "exception.wav",
        ExceptionTiming, ArrayLength(ExceptionTiming),
        Color::Blue, Color::Off, Color::Blue,
        0, true, NULL
    },
    {
        "entry_success", "success.wav",
        SolidOn, ArrayLength(SolidOn),
        Color::Green, Color::Green, Color::Green,
        0, false, NULL
    },
    {
        "entry_start_scan", "biocue.wav",
        ScanTiming, ArrayLength(ScanTiming),
        Color::Off, Color::Off, Color::White,
        0, true, NULL
    },
    {
        "entry_retry", "retry.wav",
        RetryTiming, ArrayLength(RetryTiming),
        Color::Off, Color::Off, Color::Blue,
        0, true, "entry_start_scan"
    },
    {
        "entry_login_ok", NULL,
        LoginOkTiming, ArrayLength(LoginOkTiming),
        Color::Green, Color::Off, Color::Off,
        0, false, NULL
    },

    // same as entry_success
    {
        "gxp_success", "success.wav",
        SolidOn, ArrayLength(SolidOn),
        Color::Green, Color::Green, Color::Green,
        0, false, NULL
    },

    // same as entry_exception
    {
        "gxp_exception", "exception.wav",
        ExceptionTiming, ArrayLength(ExceptionTiming),
        Color::Blue, Color::Off, Color::Blue,
        0, true, NULL
    },
};




static void initLevels(std::vector<int>& dest, const char* key, const int* defaultValues, size_t numDefaults)
{
    dest.clear();
    dest = DapConfig::instance()->getIntArray(key);
    if (dest.size() <= 0)
    {
       for (unsigned i = 0; i < numDefaults; ++i)
           dest.push_back(defaultValues[i]);
    }
}


LightEffects::LightEffects() : _lightsAreOff(true), _lightsActive(false), _sequenceIndex(0), _queued(NULL), _turnOff(false), _timeout(0), _levelIndex(0)
{
    _mickey = new PWM("/dev/pwm9");
    _circle = new PWM("/dev/pwm8");
    _mickey->off();
    _circle->off();
    _lastOnTime = getMilliseconds();

    _ambientThresholds = DapConfig::instance()->getDoubleArray("light thresholds");
    if (_ambientThresholds.size() <= 0)
    {
        for (unsigned i = 0; i < ArrayLength(DefaultAmbientThresholds); ++i)
            _ambientThresholds.push_back(DefaultAmbientThresholds[i]);
    }

    initLevels(_greenLevels, "green levels", DefaultGreenLevels, ArrayLength(DefaultGreenLevels));
    initLevels(_blueLevels, "blue levels", DefaultBlueLevels, ArrayLength(DefaultBlueLevels));
    initLevels(_bioLevels, "xbio levels", DefaultBioLevels, ArrayLength(DefaultBioLevels));
}



LightEffects::~LightEffects()
{
}


LightEffects* LightEffects::instance()
{
    static LightEffects _instance;
    return &_instance;
}


static void selectGreenCircle()
{
    static const uint8_t cmd[] = {REG_PCA9533_LS0, 1};
    SensorBus::instance()->write(I2C_LED_ADDR, cmd, sizeof(cmd));
}

static void selectBlueCircle()
{
    uint8_t const cmd[] = {REG_PCA9533_LS0, 4};
    SensorBus::instance()->write(I2C_LED_ADDR, cmd, sizeof(cmd));
}


static int findLightEffect(const char* name)
{
    for (int i = 0; i < (int)ArrayLength(sequences); ++i)
        if (strcasecmp(name, sequences[i].name) == 0)
            return i;
    return -1;
}


bool LightEffects::isValidLightEffect(const char* name)
{
    return (findLightEffect(name) >= 0);
}


bool LightEffects::show(const char* name, uint32_t timeout)
{
    LOG_DEBUG("lights '%s', timeout %d", name, timeout);
    if (Sensors::instance()->overTemp())
    {
        LOG_INFO("Over temp");
        return true;
    }

    int i = findLightEffect(name);
    if (i < 0)
    {
        LOG_ERROR("unknown lighting color or sequence %s\n", name);
        return false;
    }

    // If idle, adjust levels to ambient light
    if (!_sequence && !_queued)
        adjustLevels();

    // queue up the lighting effect
    _mutex.lock();
    _queued = &sequences[i];
    _queuedTimeout = timeout;
    _lightsActive = true;
    _mutex.unlock();
    _event.signal();

    _lastOnTime = getMilliseconds();

//    adjustLevels();
//    startSequence(&sequences[i], timeout);

    return true;
}


/**
    A tap happened.  Show the selected lighting effect, but only if the current
    or queued lighting effect can be interrupted by a tap.
*/
void LightEffects::tap(const char* name, uint32_t timeout)
{
    _mutex.lock();
    bool ignore = ( (_sequence && _sequence->ignoreTap) ||
                    (_queued && _queued->ignoreTap) );
    _mutex.unlock();

    if (!ignore)
        show(name, timeout);
}


void LightEffects::off()
{
    _mutex.lock();
    _turnOff = true;
    _mutex.unlock();
    _event.signal();
    LOG_INFO("Lights off");
}


void LightEffects::startSequence(const LightSequence* seq, uint32_t timeout)
{
    if (!seq)
    {
        LOG_ERROR("NULL lighting sequence");
        return;
    }

    if (seq->sound != NULL)
        Sound::instance()->play(seq->sound);

    if (seq->circle == Color::Green)
        selectGreenCircle();
    else if (seq->circle == Color::Blue)
        selectBlueCircle();
    else
        _circle->off();

    if (seq->mickey != Color::Green)
        _mickey->off();

    _startTime = getMilliseconds();
    _timeout = timeout;
    _sequence = seq;
    _sequenceIndex = 0;
    _sequenceTrigger = 0;
}



void LightEffects::updateSequence(MILLISECONDS now)
{
    // Not yet time for next state transition
    if ( (now - _startTime) < _sequenceTrigger)
        return;

    // Handle negative and 0 states.
    //     0 = skip this step
    //    <0 = Treated as a goto, moving th eindex pointer by this amount
    while ( (_sequenceIndex < _sequence->numTimes) && (_sequence->times[_sequenceIndex] <= 0) )
    {
        int v = _sequence->times[_sequenceIndex];
        if (v == 0)
            ++_sequenceIndex;
        else
            _sequenceIndex += v;
    }

    // Fell off the end, we're done
    if (_sequenceIndex >= _sequence->numTimes)
    {
        bool done = true;

        // If this sequence has a chained sequence, then start that one
        if (_sequence->chainedSequence)
        {
            int i = findLightEffect(_sequence->chainedSequence);
            if (i >= 0)
            {
                _sequence = &sequences[i];
                _sequenceIndex = 0;
                if (_sequence->sound)
                    Sound::instance()->play(_sequence->sound);
                done = false;
            }
        }

        if (done)
        {
            _sequence = NULL;
            if (_lightsAreOff)
            {
                _lightsActive = false;
                _lastOnTime = getMilliseconds();
            }
        }
    }

    // Next state
    else if (_sequence->times[_sequenceIndex] > 0)
    {
        _lightsAreOff = ((_sequenceIndex & 1) != 0);
        if (_lightsAreOff)
        {
            BiometricReader::instance()->lightOff();
            _circle->off();
            _mickey->off();
        }
        else
        {
            if (_sequence->circle == Color::Green)
                _circle->on(_greenLevel);
            else if (_sequence->circle == Color::Blue)
                _circle->on(_blueLevel);

            if (_sequence->mickey == Color::Green)
                _mickey->on(_greenLevel);

            if (_sequence->xbio == Color::White)
                BiometricReader::instance()->lightWhite();
            else if (_sequence->xbio == Color::Blue)
                BiometricReader::instance()->lightBlue();
            else if (_sequence->xbio == Color::Green)
                BiometricReader::instance()->lightGreen();
        }

        // re-arm the trigger for the next state change and move the state pointer
        _sequenceTrigger += _sequence->times[_sequenceIndex];
        ++_sequenceIndex;
    }
}



void LightEffects::lightsOff()
{
    BiometricReader::instance()->lightOff();
    _mickey->off();
    _circle->off();
    _timeout = 0;
    _lightsAreOff = true;
    _lastOnTime = getMilliseconds();
    _lightsActive = false;
    _sequence = NULL;
}



void LightEffects::run()
{
    uint32_t eventTimeout;

    _mickey->start();
    _circle->start();
    while (!_quit)
    {
        _mutex.lock();
        MILLISECONDS now = getMilliseconds();

        if (Sensors::instance()->overTemp() || _turnOff)
        {
            // TODO -how to deal with lightsActive and LightsOff/On whatever
            lightsOff();
            _queued = NULL;
            _turnOff = false;
        }
        else
        {
            if ( ( (_timeout != 0) && (now - _startTime) >= _timeout))
            {
                // Overall timeout reached
                lightsOff();
            }
            else if (_sequence && _queued && (now - _startTime) >= _sequence->minDuration)
            {
                // sequence has played for it's minimum duration and we have a queued sequence
                lightsOff();
            }
            else if (_sequence)
            {
                // Update the currently playing sequence
                updateSequence(now);
            }

            if (_queued && (!_sequence || (now - _startTime) >= _sequence->minDuration) )
            {
                startSequence(_queued, _queuedTimeout);
                _queued = NULL;
                updateSequence(now);
            }
        }

        // Set appropriate timeout for next action
        eventTimeout = MAX_TIMEOUT;
        if (_timeout != 0)
            eventTimeout = _timeout - (now - _startTime);

        if (_sequence && _sequenceTrigger > 0)
        {
            int sequenceTimeout = (_sequenceTrigger - (now - _startTime));
            if (sequenceTimeout < 0)
                eventTimeout = 0;
            else if ((MILLISECONDS)sequenceTimeout < eventTimeout)
                eventTimeout = sequenceTimeout;
        }

        if (eventTimeout > MAX_TIMEOUT)
            eventTimeout = MAX_TIMEOUT;


        _mutex.unlock();

        MILLISECONDS expired = getMilliseconds() - now;
        if (expired < eventTimeout)
        {
            eventTimeout -= expired;
            _event.wait(eventTimeout);
        }
    }
    _mickey->stop();
    _circle->stop();
}


void LightEffects::adjustLevels()
{
    double amb = Sensors::instance()->getLuminance();
    _greenLevel = getOutputLevel(_greenLevels, amb);
    _blueLevel = getOutputLevel(_blueLevels, amb);
    int gain = getOutputLevel(_bioLevels, amb);
    BiometricReader::instance()->setLightGain(gain);

    LOG_DEBUG("amb %f, green %d, blue %d, xbio %d\n", amb, _greenLevel, _blueLevel, gain);
}



/**
    Get the correct output level for the light based on the ambient light sensor

    @param    levels    Array of output levels
    @param    ambient   Ambient light level
*/
int LightEffects::getOutputLevel(std::vector<int>& levels, double ambient)
{
    // Get the new output level index based on ambient light
    int i = getOutputLevelIndex(levels, ambient);

    // If the level has changed, then apply some hysteresis and see if the level
    // still changes
    if (i < _levelIndex)
    {
        i = getOutputLevelIndex(levels, ambient * 1.1);
        if (i > _levelIndex)
            i = _levelIndex;
    }
    else if (i > _levelIndex)
    {
        i = getOutputLevelIndex(levels, ambient * 0.9);
        if (i < _levelIndex)
            i = _levelIndex;
    }

    _levelIndex = i;
    return levels[i];
}


int LightEffects::getOutputLevelIndex(std::vector<int>& levels, double ambient)
{
    int i;

    for (i = 0; i < (int)_ambientThresholds.size() && ambient > _ambientThresholds[i]; ++i) ;

    return i < (int)levels.size() ? i : levels.size() - 1;
}



bool LightEffects::isOff() 
{ 
    return !_lightsActive;
}


MILLISECONDS LightEffects::millisecondsSinceOn()
{
    return getMilliseconds() - _lastOnTime;
}
