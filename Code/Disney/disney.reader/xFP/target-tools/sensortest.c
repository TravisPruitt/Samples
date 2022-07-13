#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>
#define _ISOC99_SOURCE
#include <math.h>
#include <unistd.h>

#define I2C_SENSOR_BUS		3
#define I2C_THERMISTOR_ADDR	0x4B
#define I2C_ALS_ADDR		0x23

int i2c_read_byte(int bus, uint8_t chip_addr, uint8_t offs, uint8_t *data);
int i2c_read(int bus, uint8_t chip_addr, uint8_t *readbuf, unsigned readlen);
int i2c_write(int bus, uint8_t chip_addr, uint8_t *writebuf, unsigned writelen);
int i2c_read_word(int bus, uint8_t chip_addr, uint8_t offs, uint16_t *data);
int i2c_open(int bus);

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

int main()
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
	printf("ID byte is %02x\n", id); /* Should be 0xA1 */

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
