/**
    @file       TestThread.cpp
    @author     Greg Strange
    @date       Oct 2011

    Copyright (c) 2011, synapse.com
*/



#include "TestThread.h"
#include "LightEffects.h"
#include "BiometricReader.h"
#include "log.h"


using namespace Reader;




TestThread::TestThread()
{
}


TestThread::~TestThread()
{
}

void TestThread::run()
{
    bool blue = false;

    while (!_quit)
    {
#if 1
        // tap

        // biometric read
        LightEffects::instance()->show("entry_start_scan", 0);
        BiometricReader::instance()->startEnroll();
        sleepMilliseconds(4000);

        BiometricReader::instance()->cancelRead();
        sleepMilliseconds(6000);

        // blue or green
        if (blue)
        {
            LightEffects::instance()->show("entry_exception", 15000);
            sleepMilliseconds(15000);
        }
        else
        {
            LightEffects::instance()->show("entry_success", 5000);
            sleepMilliseconds(5000);
        }

        blue = !blue;
#else
        LightEffects::instance()->show("blue", 30000);
        sleepMilliseconds(1000);
        for (int gain = 10; gain <= 100; gain += 10)
        {
            BiometricReader::instance()->setLightGain(gain);
            sleepMilliseconds(500);
            BiometricReader::instance()->lightWhite();
            sleepMilliseconds(2000);
            BiometricReader::instance()->lightOff();
            sleepMilliseconds(500);
        }

        sleepMilliseconds(3000);
#endif
    }
}




