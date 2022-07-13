/**
    @file   I2CLinux.cpp
    @author Juha Kuikka
    @date   Oct, 2011
*/


#include "I2C.h"
#include "log.h"
#include "standard.h"

#include <sys/ioctl.h>

#include <fcntl.h>
#include <string.h>
#include <errno.h>
#ifndef _ISOC99_SOURCE
#define _ISOC99_SOURCE
#endif
#include <math.h>
#include <unistd.h>

#include <linux/i2c.h>
#include <linux/i2c-dev.h>


using namespace Reader;


I2C::I2C(int bus)
{
	_fd = i2c_open(bus);
}


I2C::~I2C()
{
    if (_fd >= 0)
        close(_fd);
}


int I2C::read(uint8_t chip_addr, uint8_t *readbuf, unsigned readlen)
{
	return read_or_write(chip_addr, readbuf, readlen, I2C_M_RD);
}


int I2C::write(uint8_t chip_addr, const uint8_t *writebuf, unsigned writelen)
{
    // move to our own buffer because the linux function call will not take
    // a const uint8_t* buffer, even though we are only writing.
    uint8_t buf[writelen];
    memcpy(buf, writebuf, writelen);
	return read_or_write(chip_addr, buf, writelen, 0);
}


/* Do I2C write followed by read.
 * Using I2C_RDWR these are connected using RESTART,
 * so there is only one STOP at the end*/
int I2C::write_read(uint8_t chip_addr, uint8_t *writebuf, unsigned writelen, uint8_t *readbuf, unsigned readlen)
{
	struct i2c_msg msgs[2];
	struct i2c_rdwr_ioctl_data msgset;

	/* Write message */
	msgs[0].addr = chip_addr;
	msgs[0].flags = 0;
	msgs[0].buf = writebuf;
	msgs[0].len = writelen;

	/* Read message */
	msgs[1].addr = chip_addr;
	msgs[1].flags = I2C_M_RD;
	msgs[1].buf = readbuf;
	msgs[1].len = readlen;

	/* Configure message set */
	msgset.msgs = msgs;
	msgset.nmsgs = 2;

	if (ioctl(_fd, I2C_RDWR, &msgset) < 0) {
		LOG_ERROR("Error, I2C_RDWR failed, addr=0x%X: %s(%d)\n",
                  (unsigned)chip_addr, strerror(errno), errno);
		return -errno;
	}
	return 0;
}


int I2C::read_or_write(uint8_t chip_addr, uint8_t *buf, unsigned len, int flags)
{
	struct i2c_msg msgs[1];
	struct i2c_rdwr_ioctl_data msgset;

	/* Read message */
	msgs[0].addr = chip_addr;
	msgs[0].flags = flags;
	msgs[0].buf = buf;
	msgs[0].len = len;

	/* Configure message set */
	msgset.msgs = msgs;
	msgset.nmsgs = 1;

	if (ioctl(_fd, I2C_RDWR, &msgset) < 0) {
		LOG_ERROR("Error, I2C_RDWR failed, addr=0x%X: %s(%d)\n",
				  (unsigned)chip_addr, strerror(errno), errno);
		return -errno;
	}
	return 0;
}


int I2C::i2c_open(int bus)
{
	char buf[32];
	int ret;

	snprintf(buf, sizeof(buf), "/dev/i2c-%d", bus);
	ret = open(buf, O_RDWR);
	if (ret < 0) {
		LOG_ERROR("Error, could not open I2C bus %d: %s(%d)\n", bus, strerror(errno), errno);
		return -errno;
	}
	return ret;
}
