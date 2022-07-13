/**
    @file   PWM.cpp
    @author Greg Strange
    @date   Oct, 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __PWM_H
#define __PWM_H

#include "Thread.h"
#include "Mutex.h"
#include "Event.h"
#include "ticks.h"
#include <stdio.h>


namespace Reader
{

class PWM : public Thread
{
public:
    PWM(const char* filePath);
    ~PWM();
    void on(unsigned int level);
    void off();

private:
    enum State { RAMPING_UP, ON, RAMPING_DOWN, OFF} _state;
    double _pwm;
    unsigned int _target;
    double _rampUpMultiplier;
    double _rampDownMultiplier;

    FILE* _controlFile;

    MILLISECONDS _startTime;

    Mutex _mutex;
    Event _event;

    void setPwm(double pwm);
    void startRampUp(uint32_t timeout);
    void run();
};



}




#endif
