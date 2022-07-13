/**
 *  @file   GPIOTrigger.cpp
 *  @author Corey Wharton
 *  @date   Aug, 2012
 *
 *  Copyright (c) 2012, synapse.com
 */

#include <stdio.h>
#include <fcntl.h>
#include <errno.h>

#include "GPIOTrigger.h"
#include "DapConfig.h"
#include "EventLogger.h"
#include "log.h"
#include "json/json.h"


#define GPIO_TRIGGER_INPUT_DEVICE "/dev/input/event0"


namespace Reader
{


GPIOTrigger::GPIOTrigger()
{
    _minTriggerInterval = DapConfig::instance()->getValue("min trigger interval", 1000);;
    memset(&_lastTriggered, 0, sizeof(_lastTriggered));
}


GPIOTrigger::~GPIOTrigger()
{
}


GPIOTrigger* GPIOTrigger::instance()
{
    static GPIOTrigger _instance;
    return &_instance;
}


void GPIOTrigger::postTriggerEvent(unsigned channel)
{
    LOG_DEBUG("GPIO trigger detected, channel %u", channel);

    MILLISECONDS currentTime = getMilliseconds();
    MILLISECONDS elapsedTime = currentTime - _lastTriggered[channel];

    if (elapsedTime > _minTriggerInterval)
    {
        // Queue a reader event
        Json::Value ev;
        ev["type"] = "xtp-gpio";
        ev["channel"] = channel;
        EventLogger::instance()->postEvent(ev);
        
        // Update last triggered time
        _lastTriggered[channel] = currentTime;
    }
    else
    {
        LOG_DEBUG("Trigger interval too short");
    }
}


/*
 *  Main thread
 */
void GPIOTrigger::run()
{
    fd_set rfds;
    int retval;
    struct timeval tv;
    struct input_event ev;

    LOG_INFO("Starting GPIO trigger thread");

    int fd = open(GPIO_TRIGGER_INPUT_DEVICE, O_RDONLY);
    if (fd < 0)
    {
        LOG_ERROR("Unable to open input device");
        return;
    }

    FD_ZERO(&rfds);

    while (!_quit)
    {
        FD_SET(fd, &rfds);

        memset(&tv, 0, sizeof(tv));
        tv.tv_sec = 0;
        tv.tv_usec = 500000;

        retval = select(fd+1, &rfds, NULL, NULL, &tv);
        if (retval <= 0)
        {
            if (retval < 0)
                LOG_ERROR("select(): %s", strerror(errno));
            continue;
        }

        retval = read(fd, &ev, sizeof(struct input_event));
        if (retval < (int) sizeof(struct input_event))
        {
            LOG_WARN("Invalid read size on input device");
            continue;
        }
        
        if (ev.code >= MIN_GPIO_CHANNEL_CODE &&
            ev.code <= MAX_GPIO_CHANNEL_CODE &&
            ev.value == 0 // Trigger start
            )
        {
            postTriggerEvent(ev.code - MIN_GPIO_CHANNEL_CODE);
        }
    }

    close(fd);
}


}  // namespace Reader
