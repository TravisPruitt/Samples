#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>
#define _ISOC99_SOURCE
#include <math.h>
#include <unistd.h>
#include <stdlib.h>

#define I2C_SENSOR_BUS		3
#define I2C_LED_ADDR		98
#define REG_PCA9533_LS0		5
#define NR_LEDS			4

int i2c_read_byte(int bus, uint8_t chip_addr, uint8_t offs, uint8_t *data);
int i2c_write_byte(int bus, uint8_t chip_addr, uint8_t offs, uint8_t data);
int i2c_read(int bus, uint8_t chip_addr, uint8_t *readbuf, unsigned readlen);
int i2c_write(int bus, uint8_t chip_addr, uint8_t *writebuf, unsigned writelen);
int i2c_open(int bus);

void usage(void)
{
	printf("ledtest [on <led>|off]\n");
	printf("	on <led> turn <led on\n");
	printf("	off turn leds off\n");
	exit(0);
}

int led_disable(void)
{
	int bus = i2c_open(I2C_SENSOR_BUS);
	if (bus < 0)
		return 0;

	return i2c_write_byte(bus, I2C_LED_ADDR, REG_PCA9533_LS0, 0);
}

int led_enable(int led)
{
	uint8_t data;

	int bus = i2c_open(I2C_SENSOR_BUS);
	if (bus < 0)
		return 0;

	if (led >= NR_LEDS) {
		printf("Enter LED 0-3\n");
	}

	data = 1 << (led * 2);
	return i2c_write_byte(bus, I2C_LED_ADDR, REG_PCA9533_LS0, data);
}

int led_show(void)
{
	uint8_t data;
	int i;

	printf("X\n");
	int bus = i2c_open(I2C_SENSOR_BUS);
	if (bus < 0)
		return 0;

	i2c_read_byte(bus, I2C_LED_ADDR, REG_PCA9533_LS0, &data);
	printf("Got %x\n", data);
	for(i = 0; i < NR_LEDS; i++) {
		int status = (data >> (i*2)) & 0x03;
		if (status == 0)
			printf("Led %d off\n", i);
		if (status == 1)
			printf("Led %d on\n", i);
		if (status == 2)
			printf("Led %d PWM0\n", i);
		if (status == 3)
			printf("Led %d PWM1\n", i);
	}
	return 0;

}

int main(int argc, char *argv[])
{
	if (argc > 1) {
		if (strcmp(argv[1], "on") == 0) {
			if (argc == 3) {
				led_enable(strtoul(argv[2], NULL, 0));
				return 0;
			}
		}
		else if (strcmp(argv[1], "off") == 0) {
			led_disable();
			return 0;
		}
		else if (strcmp(argv[1], "show") == 0) {
			led_show();
			return 0;
		}
		else {
			usage();
		}
	} else
		usage();


	return 0;
}
