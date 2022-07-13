#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdint.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <string.h>

#include <linux/i2c.h>
#include <linux/i2c-dev.h>

/* Do I2C write followed by read.
 * Using I2C_RDWR these are connected using RESTART,
 * so there is only one STOP at the end*/
int i2c_write_read(int bus, uint8_t chip_addr,
		   uint8_t *writebuf, unsigned writelen,
		   uint8_t *readbuf, unsigned readlen)
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

	if (ioctl(bus, I2C_RDWR, &msgset) < 0) {
		printf("Error, I2C_RDWR failed: %s(%d)\n", strerror(errno), errno);
		return -errno;
	}
	return 0;
}

int i2c_read_or_write(int bus, uint8_t chip_addr,
		      uint8_t *buf, unsigned len, int flags)
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

	if (ioctl(bus, I2C_RDWR, &msgset) < 0) {
		printf("Error, I2C_RDWR failed: %s(%d)\n", strerror(errno), errno);
		return -errno;
	}
	return 0;
}

int i2c_read(int bus, uint8_t chip_addr,
	     uint8_t *readbuf, unsigned readlen)
{
	return i2c_read_or_write(bus, chip_addr, readbuf, readlen, I2C_M_RD);
}

int i2c_write(int bus, uint8_t chip_addr,
	      uint8_t *writebuf, unsigned writelen)
{
	return i2c_read_or_write(bus, chip_addr, writebuf, writelen, 0);
}

int i2c_open(int bus)
{
	char buf[2048];
	int ret;

	snprintf(buf, sizeof(buf), "/dev/i2c-%d", bus);
	ret = open(buf, O_RDWR);
	if (ret < 0) {
		printf("Error, could not open I2C bus %d: %s(%d)\n", bus, strerror(errno), errno);
		return -errno;
	}
	return ret;
}

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

