/**
    @file   I2CWin
    @author Greg Strange
    @date   Oct 2011


    I2C stubs so that program will compile on Windows.
*/

#include "I2C.h"


using namespace Reader;


I2C::I2C(int bus)
{
}


I2C::~I2C()
{
}

int I2C::read(uint8_t chip_addr, uint8_t *readbuf, unsigned readlen)
{
    return -1;
}

int I2C::write(uint8_t chip_addr, const uint8_t *writebuf, unsigned writelen)
{
    return -1;
}


