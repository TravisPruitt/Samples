#ifndef __PCA9554_H__
#define __PCA9554_H__

#include "gpiobank.h"

namespace Reader
{
	class PCA9554 : public GpioBank
	{
		public:
			PCA9554(I2C *bus, uint8_t address = 0x38);
			virtual ~PCA9554();
		public:
			virtual bool get(unsigned pin);
			virtual bool set(unsigned pin, bool value);
			virtual bool output(unsigned pin, bool value);
			virtual bool input(unsigned pin);
			virtual bool is_input(unsigned pin);
			virtual bool test();
			virtual bool init();
		private:
			bool readRegister(uint8_t reg, uint8_t *value);
			bool writeRegister(uint8_t reg, uint8_t value);
			bool clearBits(uint8_t reg, uint8_t bits);
			bool setBits(uint8_t reg, uint8_t bits);
		private:
			I2C *m_bus;
			uint8_t m_addr;
	};
};

#endif //  __PCA9554_H__
