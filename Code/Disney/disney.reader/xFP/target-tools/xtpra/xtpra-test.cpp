#include <iostream>
#include <stdint.h>
#include <signal.h>
#include <cstring>
#include <cstdlib>
#include <errno.h>
#include "Sound.h"
#include "log.h"
#include "I2C.h"
#include "pca9554.h"
#include "sysgpio.h"

// OMAP pin functions to expander pins
#define EXPANDER_LED		0
#define EXPANDER_VOLTAGE	1
#define EXPANDER_RELAY		2 // also pushbutton
#define EXPANDER_GPI1		3
#define EXPANDER_GPO2		5 // 4
#define EXPANDER_GPO4		4 // 5
#define EXPANDER_GPO3		6
#define EXPANDER_GPO1		7

#define ARRAY_SIZE(x) (sizeof(x)/sizeof(x[0]))

using namespace Reader;
using namespace std;

static I2C i2c(3);
static PCA9554 expander(&i2c);
static SysGpio gpio;

static const int output_gpios[] = {
	71,
	72,
	73,
	74,
};

static const int input_gpio = 70;
static const int relay_gpio = 75;

void usage()
{
	cout << "xtpra-test [options]\n";
	cout << "Options\n";
	cout << "\t-i\ttest inputs\n";
	cout << "\t-o\ttest outputs\n";
	cout << "\t-r\ttest relay\n";
	cout << "\t-s\ttest sound\n";
}

int testcase_i2c()
{
	if(expander.test()) {
		cout << "I2C test: PASS\n";
	} else {
		cout << "I2C test: FAIL\n";
		return -EINVAL;
	}
	return 0;
}

unsigned get_expander_inputs()
{
	unsigned ret;

	/* The order has to be just right since
	 * expander gpios to not map 1:1 to outputs */
	ret = expander.get(EXPANDER_GPO1) |
		expander.get(EXPANDER_GPO2) << 1 |
		expander.get(EXPANDER_GPO3) << 2 |
		expander.get(EXPANDER_GPO4) << 3;

	return ret;
}

bool set_output_gpios(unsigned mask)
{
	for(unsigned i = 0; i < ARRAY_SIZE(output_gpios); ++i)
		if(!gpio.set(output_gpios[i], mask & (1 << i)))
			return false;

	return true;
}

int testcase_output()
{
	/*
	 * Set output GPIOs to 0000
	 * Verify by reading using extender.
	 * Set output GPIOs to 0001
	 * .... repeat until 1111
	 */
	for(unsigned i = 0; i < 16; ++i) {
		set_output_gpios(i);
		if(get_expander_inputs() != i) {
			cout << "OUTPUT test: FAIL\n";
			return -EINVAL;
		}
	}
	cout << "OUTPUT test: PASS\n";

	return 0;
}

int testcase_input()
{
	/* Use expander line 3 to toggle input
	 * and read back
	 */
	expander.output(EXPANDER_GPI1, 0);
	if(gpio.get(input_gpio) != false) {
		cout << "INPUT test: FAIL\n";
		return -EINVAL;
	}

	expander.set(EXPANDER_GPI1, true);
	if(gpio.get(input_gpio) != true) {
		cout << "INPUT test: FAIL\n";
		return -EINVAL;
	}

	cout << "INPUT test: PASS\n";
	return 0;
}

int testcase_relay()
{
	gpio.set(relay_gpio, false);

	if(expander.get(2) != true) {
		cout << "Relay test: FAIL\n";
		return -EINVAL;
	}

	gpio.set(relay_gpio, true);
	if(expander.get(2) != false) {
		cout << "Relay test: FAIL\n";
		return -EINVAL;
	}
	cout << "Relay test: PASS\n";
	return 0;
}

bool read_pushbutton(unsigned timeout_ms)
{
	unsigned i = timeout_ms / 10;
	while(1) {
		if(expander.get(EXPANDER_RELAY) == false)
			return true;
		if(i == 0)
			return false;
		i--;
		usleep(100 * 1000);
	}
}

int testcase_sound()
{
	gpio.set(relay_gpio, false);

        Sound::instance()->start();
	Sound::instance()->play("tap");
#if 0
	if(!read_pushbutton(4000)) {
		cout << "Sound test: FAIL\n";
		return -EINVAL;
	}
	cout << "Sound test: PASS\n";
#endif
	usleep(4 * 1000 * 1000);
	return 0;
}

int testcase_led()
{
	expander.output(EXPANDER_LED, false);
	return 0;
}

int testcase_voltage()
{
	if(!expander.get(EXPANDER_VOLTAGE)) {
		cout << "Voltage test: FAIL\n";
		return -EINVAL;
	}
	cout << "Voltage test: PASS\n";
	return 0;
}

int main(int argc, char* argv[])
{
	int c;
	bool test_all = true;
	bool test_input = false;
	bool test_output = false;
	bool test_relay = false;
	bool test_sound = false;
	bool test_i2c = false;
	bool test_voltage = false;
	bool test_led = false;

	expander.init();

	while ((c = getopt(argc, argv, "2iorsvlh")) != -1) {
		switch (c) {
			case 'h':
				usage();
				return 0;
			case '2':
				test_all = false;
				test_i2c = true;
				break;
			case 'i':
				test_all = false;
				test_input = true;
				break;
			case 'o':
				test_all = false;
				test_output = true;
				break;
			case 'r':
				test_all = false;
				test_relay = true;
				break;
			case 's':
				test_all = false;
				test_sound = true;
				break;
			case 'v':
				test_all = false;
				test_voltage = true;
				break;
			case 'l':
				test_all = false;
				test_led = true;
				break;
		}
	}

	if(test_i2c)
		testcase_i2c();
	if(test_voltage)
		testcase_voltage();
	if(test_output)
		testcase_output();
	if(test_input)
		testcase_input();
	if(test_relay)
		testcase_relay();
	if(test_sound)
		testcase_sound();
	if(test_led)
		testcase_led();

	if(test_all) {
		int ret = testcase_i2c();
		ret |= testcase_voltage();
		ret |= testcase_output();
		ret |= testcase_input();
		ret |= testcase_relay();
		ret |= testcase_sound();
		if(ret == 0)
			testcase_led();
	}
}
