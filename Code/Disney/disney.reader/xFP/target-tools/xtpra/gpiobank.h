#ifndef __GPIOEXPANDER_H__
#define __GPIOEXPANDER_H__
#include "I2C.h"

namespace Reader
{
	class GpioBank
	{
		public:
			virtual bool get(unsigned pin) = 0;
			virtual bool set(unsigned pin, bool value) = 0;
			virtual bool output(unsigned pin, bool value) = 0;
			virtual bool input(unsigned pin) = 0;
			virtual bool is_input(unsigned pin) = 0;
			virtual bool test() = 0;
			virtual bool init() = 0;
	};
};
#endif // __GPIOEXPANDER_H__
