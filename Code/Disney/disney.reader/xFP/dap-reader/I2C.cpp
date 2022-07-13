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
#define _ISOC99_SOURCE
#include <math.h>
#include <unistd.h>

#include <linux/i2c.h>
#include <linux/i2c-dev.h>

#define I2C_SENSOR_BUS		3
#define I2C_THERMISTOR_ADDR	0x4B
#define I2C_ALS_ADDR		0x23


using namespace Reader;


I2C::I2C(int bus)
{
    UNUSED(bus);
	_fd = i2c_open(I2C_SENSOR_BUS);
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
		LOG_ERROR("Error, I2C_RDWR failed: %s(%d)\n", strerror(errno), errno);
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
		LOG_ERROR("Error, I2C_RDWR failed: %s(%d)\n", strerror(errno), errno);
		return -errno;
	}
	return 0;
}

int I2C::i2c_open(int bus)
{
	char buf[2048];
	int ret;

	snprintf(buf, sizeof(buf), "/dev/i2c-%d", bus);
	ret = open(buf, O_RDWR);
	if (ret < 0) {
		LOG_ERROR("Error, could not open I2C bus %d: %s(%d)\n", bus, strerror(errno), errno);
		return -errno;
	}
	return ret;
}

#if 0
int i2c_write_byte(int bus, uint8_t chip_addr, uint8_t offs, uint8_t data)
{
	uint8_t buf[2];

	buf[0] = offs;
	buf[1] = data;

	return i2c_write(bus, chip_addr, buf, sizeof(buf));
}

int i2c_write_word(int bus, uint8_t chip_addr, uint8_t offs, uint16_t data)
{
	uint8_t buf[3];
	
	buf[0] = offs;
	buf[1] = data >> 8;
	buf[2] = data;

	return i2c_write(bus, chip_addr, buf, sizeof(buf));
}

int i2c_read_byte(int bus, uint8_t chip_addr, uint8_t offs, uint8_t *data)
{
	uint8_t buf[1];
	buf[0] = offs;

	if (i2c_write_read(bus, chip_addr,
			   buf, 1,
			   buf, sizeof(buf)))
		return -errno;

	*data = buf[0];
	return 0;
}

int i2c_read_word(int bus, uint8_t chip_addr, uint8_t offs, uint16_t *data)
{
	uint8_t buf[2];
	buf[0] = offs;

	if (i2c_write_read(bus, chip_addr,
			   buf, 1,
			   buf, sizeof(buf)))
		return -errno;

	*data = (buf[0] << 8 | buf[1]);
	return 0;
}

int get_temperature(int bus, double *temp)
{
	uint16_t data;
	int16_t value;

	if (i2c_read_word(bus, I2C_THERMISTOR_ADDR, 0, &data) < 0) {
		return -errno;
	}

	value = (int16_t)data;
	value >>= 3;
	*temp = ((double)value) * 0.03125;
	return 0;
}

int get_luminance_hi(int bus, double *luminance)
{
	int ret;
	uint8_t buf[2];

	buf[0] = 0x12;
	ret = i2c_write(bus, I2C_ALS_ADDR, buf, 1);
	if (ret)
		return ret;
	usleep(1000 * 120);
	ret = i2c_read(bus, I2C_ALS_ADDR, buf, 2);
	if (ret)
		return ret;
	*luminance = ((double)(buf[0] << 8 | buf[1])) / 1.2;
	return 0;
}

int get_luminance_lo(int bus, double *luminance)
{
	int ret;
	uint8_t buf[2];

	buf[0] = 0x13;
	ret = i2c_write(bus, I2C_ALS_ADDR, buf, 1);
	if (ret)
		return ret;
	usleep(1000 * 16);
	ret = i2c_read(bus, I2C_ALS_ADDR, buf, 2);
	if (ret)
		return ret;
	*luminance = ((double)(buf[0] << 8 | buf[1])) / 1.2;
	return 0;
}


int main(int argc, char *argv[])
{
	double temp;
	uint8_t id;
	int bus;
	double luminance;

	bus = i2c_open(I2C_SENSOR_BUS);
	if (bus < 0)
		return 0;

	/* thermistor */
	printf("Thermistor:\n");
	i2c_read_byte(bus, I2C_THERMISTOR_ADDR, 5, &id);
	printf("ID byte is 02x\n", id); /* Should be 0xA1 */

	if (!get_temperature(bus, &temp)) {
		printf("Temperature is %f\n", temp);
	} else {
		printf("Could not read temperature\n");
	}

	printf("\nAmbient light sensor\n");
	get_luminance_hi(bus, &luminance);
	printf("H-resolution luminance: %f\n", luminance);
	get_luminance_lo(bus, &luminance);
	printf("L-resolution luminance: %f\n", luminance);

	close(bus);


	return 0;
}
#endif

