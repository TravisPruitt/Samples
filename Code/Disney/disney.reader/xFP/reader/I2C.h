#ifndef __I2C_H
#define __I2C_H


#include <stdint.h>


#define I2C_SENSOR_BUS		3
#define I2C_THERMISTOR_ADDR	0x4B
#define I2C_ALS_ADDR		0x23


namespace Reader
{

    class I2C
    {
    public:
        I2C(int bus);
        ~I2C();

        int read(uint8_t chip_addr, uint8_t *readbuf, unsigned readlen);
        int write(uint8_t chip_addr, const uint8_t *writebuf, unsigned writelen);
        int write_read(uint8_t chip_addr, uint8_t *writebuf, unsigned writelen, uint8_t *readbuf, unsigned readlen);

    private:   // methods
        int i2c_open(int bus);
        int read_or_write(uint8_t chip_addr, uint8_t *buf, unsigned len, int flags);

    private:   // data
        int _fd;
    };

}


#endif
