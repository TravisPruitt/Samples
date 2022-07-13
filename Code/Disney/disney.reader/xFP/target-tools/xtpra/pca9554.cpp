#include <cstdio>
#include "pca9554.h"

#define REG_INPUT	0x00
#define REG_OUTPUT	0x01
#define REG_POLARITY	0x02
#define REG_DIRECTION	0x03

#define PIN_INPUT	1
#define PIN_OUTPUT	0

#define PIN(x)		(1 << x)

namespace Reader
{
	PCA9554::PCA9554(I2C *bus, uint8_t address) :
		m_bus(bus), m_addr(address)
	{
	}

	PCA9554::~PCA9554()
	{
	}


	bool PCA9554::get(unsigned pin)
	{
		uint8_t val;
		readRegister(REG_INPUT, &val);
		return val & PIN(pin);
	}

	bool PCA9554::set(unsigned pin, bool value)
	{
		if(value)
			setBits(REG_OUTPUT, PIN(pin));
		else
			clearBits(REG_OUTPUT, PIN(pin));
		return true;
	}

	bool PCA9554::output(unsigned pin, bool value)
	{
		// Set output value
		this->set(pin, value);
		// Set direction to output
		clearBits(REG_DIRECTION, PIN(pin));
		return true;
	}

	bool PCA9554::input(unsigned pin)
	{
		setBits(REG_DIRECTION, PIN(pin));
		return true;
	}

	bool PCA9554::is_input(unsigned pin)
	{
		uint8_t val;
		readRegister(REG_DIRECTION, &val);
		return val & PIN(pin);
	}

	bool PCA9554::init()
	{
		writeRegister(REG_OUTPUT, 0xFF);
		writeRegister(REG_POLARITY, 0x00);
		writeRegister(REG_DIRECTION, 0xFF);
		return true;
	}

	bool PCA9554::writeRegister(uint8_t reg, uint8_t value)
	{
		uint8_t buf[2] = { reg, value };
		return m_bus->write(m_addr, buf, sizeof(buf));
	}

	bool PCA9554::readRegister(uint8_t reg, uint8_t *value)
	{
		uint8_t buf[1] = { reg };
		uint8_t buf2[1];

		int ret = m_bus->write_read(m_addr,
					    buf,
					    sizeof(buf),
					    buf2,
					    sizeof(buf2));
		*value = buf2[0];

		return ret == 0 ? true : false;
	}

	bool PCA9554::clearBits(uint8_t reg, uint8_t bits)
	{
		uint8_t val;

		readRegister(reg, &val);
		val &= ~bits;
		writeRegister(reg, val);
		return true;
	}

	bool PCA9554::setBits(uint8_t reg, uint8_t bits)
	{
		uint8_t val;

		readRegister(reg, &val);
		val |= bits;
		writeRegister(reg, val);
		return true;
	}

	bool PCA9554::test()
	{
		uint8_t val;

		return readRegister(0, &val);
	}
};
