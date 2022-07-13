/**
    @file   Radios.cpp
    @author Greg Strange

    Copyright (c) 2012, synapse.com

    This class contructs a Radio instance for each radio and initializes them all
    at program start up.  The only way to access a radio is through this class.

    The class also opens a single file handle to the radio driver that is shared
    by all of the radios.
*/


#include "Radios.h"
#include "ticks.h"
#include "ReaderInfo.h"
#include "FileSystem.h"
#include "log.h"

#include <vector>
#include <fcntl.h>
#include <sys/ioctl.h>


using namespace Reader;


#define DRIVER_PREFIX     "/dev/grover_radio"
#define DEBUG_DRIVER_PATH "/dev/grover_debug"


static int _drivers[MAX_NUM_RADIO_DRIVERS] = { -1, -1 };
static std::vector<Radio*> _radios;
static Radio* _txRadio = NULL;


/**
 *  Reset all of the radios.
 *
 *
 *  Uses the reset line if it is available, else it uses the reset command.
 *  The reset line is availble on newer hardware (V3 and newer) and is controlled
 *  through the debug driver.
 */
static void resetRadios()
{
    // v3 and later hardware has some additional control lines controlled by the
    // separate "debug" driver.  If the radio has the debug driver, then use the 
    // hardware reset, it's faster
    int fd = open(DEBUG_DRIVER_PATH, O_RDWR);
    if (fd >= 0)
    {
        LOG_DEBUG("using debug interface to reset radios");

        for (std::vector<Radio*>::iterator it = _radios.begin(); it != _radios.end(); it++)
            (*it)->hardReset(fd);

        close(fd);
        sleepMilliseconds(10);
    }
    else
    {
        // If no debug driver, then use the reset command
        LOG_DEBUG("using 'normal' interface to reset radios");
        
        for (std::vector<Radio*>::iterator it = _radios.begin(); it != _radios.end(); it++)
            (*it)->softReset();
        
        // Wait for the resets to finish
        // When using the reset command Skip says 1500 is a good value, 1000 ms sometimes fails.
        // When using the hardware reset line even no delay works, but we add a short one to be safe
        sleepMilliseconds(1500);
    }
}


/**
 *  Create and initialize all of the radios.
 *
 *  Must be called before using any of the radios.
 */
void Radios::init()
{
    for (int i = 0; i < MAX_NUM_RADIO_DRIVERS; ++i)
    {
        char dev_path[256];

        // construct device node path and check for existence
        snprintf(dev_path, sizeof(dev_path), DRIVER_PREFIX "%d", i);
        if (!FileSystem::fileExists(dev_path))
        {
            LOG_DEBUG("Device node '%s' doesn't exist", dev_path);
            continue;
        }

        _drivers[i] = open(dev_path, O_RDWR);
        if (_drivers[i] < 0)
        {
            LOG_ERROR("Unable to open radio driver");
            ReaderInfo::instance()->setStatus(IStatus::Red, "Unable to open radio driver");
            continue;
        }

        for (int j = 0; j < getNumRadios(_drivers[i]); ++j)
            _radios.push_back(new Radio(_drivers[i], j));
    }
    
    // Ensure we have at least on driver instance opened.
    if (_drivers[0] < 0)
    {
        LOG_ERROR("Unable to open radio driver");
        ReaderInfo::instance()->setStatus(IStatus::Red, "Unable to open radio driver");
        return;
    }

    // Create TX radio instance using first driver instance
    _txRadio = new Radio(_drivers[0], getNumRadios(_drivers[0]));

    resetRadios();

    // Initialize each radio
    for (std::vector<Radio*>::iterator it = _radios.begin(); it != _radios.end(); it++)
        (*it)->init();
    _txRadio->init();
}


size_t Radios::totalNumberOfRadios()
{
    return _radios.size();
}


/**
 *  Get a reference to one of the radios.
 */
Radio& Radios::radio(int radioNumber)
{
    return *_radios[radioNumber];
}


/**
 *  Get a reference to the transmit radio.
 */
Radio& Radios::txRadio()
{
    return *_txRadio;
}


static bool getDriverInfo(int fd, struct radio_info* info)
{
    int result = ioctl(fd, RADIO_GET_INFO, info);
    return (result == 0);
}


/**
    Get the version of the radio driver
*/
std::string Radios::getDriverVersion()
{
    struct radio_info info;
    return getDriverInfo(_drivers[0], &info) ? info.version : "old";
}


/*
 *  Get the number of RX radios attached to given driver
 */
int Radios::getNumRadios(int fd)
{
    struct radio_info info;
    return getDriverInfo(fd, &info) ? info.rx_radios : 0;
}


const int* Radios::getDrivers()
{
    return _drivers;
}
