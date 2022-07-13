/**
    @file   SerialPort.h
    @author Greg Strange
    @date   Sept 21, 2011
*/

#ifndef __SERIAL_PORT_H
#define __SERIAL_PORT_H

#include "standard.h"
#include <string>

#ifdef _WIN32
#include <windows.h>
#endif


namespace Reader
{
class SerialPort
{
public:
    SerialPort(const char* portName);
    ~SerialPort();

    bool init(unsigned baudRate, char parity);
    void close();

    bool write(const uint8_t* data, size_t length);
    bool readByte(uint8_t* returnByte, unsigned timeoutMs);
    void flushInput();

private:
    std::string _portName;

#ifdef _WIN32
    HANDLE _handle;
#else
    int _fd;
#endif
};

}

#endif
