/**
    @file   PWM.cpp
    @author Greg Strange
    @date   Oct, 2011

    Copyright (c) 2011, synapse.com
*/




#include "PWM.h"
#include "log.h"
#include "ticks.h"

#include <math.h>


using namespace Reader;


const double    PwmMin = 4;

const MILLISECONDS  RampUpInterval = 10;
const MILLISECONDS  RampUpTime = 100;
const int           RampUpSteps = RampUpTime / RampUpInterval;

const MILLISECONDS  RampDownInterval = 10;
const MILLISECONDS  RampDownTime = 100;
const int           RampDownSteps = RampDownTime / RampDownInterval;



//#define PWM_INCREMENT    5
//#define PWM_DECREMENT    5
//#define PWM_INTERVAL     25
//#define PWM_TARGET       100

// This value exist so that the thread will quit within a reasonable amount of time when
// the program exits.
#define MAX_TIMEOUT      250



PWM::PWM(const char* filePath) : _state(OFF), _controlFile(NULL)
{
#ifdef _WIN32
    filePath = NULL;        // ignore control file on Windows
#endif

    if (filePath)
    {
        _controlFile = fopen(filePath, "w");
        if (!_controlFile)
        {
            LOG_ERROR("unable to open PWM file: %s\n", filePath);
            return;
        }
    }

    setPwm(0);
}

PWM::~PWM()
{
    if (_controlFile)
        fclose(_controlFile);
}


void PWM::on(unsigned int level)
{
    _mutex.lock();
    _target = level;
    _rampUpMultiplier = pow((double)level / (double)PwmMin, 1 / (double)RampUpSteps);
    _rampDownMultiplier = pow((double)level / (double)PwmMin, 1 / (double)RampDownSteps);
    _state = RAMPING_UP;
    _startTime = getMilliseconds();
    _mutex.unlock();
    _event.signal();
}


void PWM::off()
{
    _mutex.lock();
    if (_state == RAMPING_UP || _state == ON)
        _state = RAMPING_DOWN;
    _mutex.unlock();
    _event.signal();
}


void PWM::run()
{
    uint32_t eventTimeout;

    while (!_quit)
    {
        MILLISECONDS now = getMilliseconds();
        _mutex.lock();

        if (_state == RAMPING_UP)
        {
            if (_pwm < PwmMin)
                _pwm = PwmMin;
            else
            {
                _pwm = _rampUpMultiplier * _pwm;
                if (_pwm >= _target)
                {
                    _pwm = _target;
                    _state = ON;
                }
            }

            setPwm(_pwm);
        }
        else if (_state == RAMPING_DOWN)
        {
            _pwm = _rampDownMultiplier / _pwm;
            if (_pwm < PwmMin)
            {
                _pwm = 0;
                _state = OFF;
            }

            setPwm(_pwm);
        }

        if (_state == RAMPING_UP)
            eventTimeout = RampUpInterval;
        else if (_state == RAMPING_DOWN)
            eventTimeout = RampDownInterval;
        else
            eventTimeout = MAX_TIMEOUT;

        _mutex.unlock();

        MILLISECONDS expired = getMilliseconds() - now;
        if (expired < eventTimeout)
        {
            eventTimeout -= expired;
            _event.wait(eventTimeout);
        }
    }
}



void PWM::setPwm(double pwm)
{
//    LOG_DEBUG("pwm = %f\n", pwm);
    _pwm = pwm;
    int output = (int)(pwm + 0.5);
    if (_controlFile)
    {
        fprintf(_controlFile, "%d", output);
        fflush(_controlFile);
    }
}

