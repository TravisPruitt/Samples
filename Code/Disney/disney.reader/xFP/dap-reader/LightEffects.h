/**
    @file   TapLigh.cpp
    @author Greg Strange
    @date   Oct, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __TAP_LIGHT_H
#define __TAP_LIGHT_H

#include "Thread.h"
#include "Mutex.h"
#include "Event.h"
#include "ticks.h"
#include "PWM.h"
#include <stdio.h>
#include <vector>


namespace Reader
{
namespace Color { enum Enum { Green, Blue, White, Off }; };

struct LightSequence {
    const char* name;
    const char* sound;
    const int* times;
    int numTimes;
    Color::Enum circle;
    Color::Enum mickey;
    Color::Enum xbio;
    unsigned minDuration;
    bool ignoreTap;
    const char* chainedSequence;
};

class LightEffects : public Thread
{
public:
    static LightEffects* instance();

    bool isValidLightEffect(const char* name);
    bool show(const char* name, uint32_t timeout);
    void tap(const char* name, uint32_t timeout);
    void off();

    bool isOff();
    MILLISECONDS millisecondsSinceOn();

private: // data
    PWM* _mickey;
    PWM* _circle;

    bool _lightsAreOff;
    bool _lightsActive;

    const LightSequence* _sequence;
    int _sequenceIndex;
    MILLISECONDS _sequenceTrigger;

    const LightSequence* _queued;
    uint32_t _queuedTimeout;
    bool _turnOff;

    int _blueLevel;
    int _greenLevel;

    MILLISECONDS _startTime;
    uint32_t _timeout;
    MILLISECONDS _lastOnTime;

    std::vector<double> _ambientThresholds;
    std::vector<int> _blueLevels;
    std::vector<int> _greenLevels;
    std::vector<int> _bioLevels;

    int _levelIndex;

    Mutex _mutex;
    Event _event;

private: // methods
    // singleton
    LightEffects();
    ~LightEffects();

    void run();
    void startSequence(const LightSequence* seq, uint32_t timeout);
    void updateSequence(MILLISECONDS now);
    void lightsOff();
    void adjustLevels();
    int getOutputLevel(std::vector<int>& levels, double ambient);
    int getOutputLevelIndex(std::vector<int>& levels, double ambient);
};



}




#endif
