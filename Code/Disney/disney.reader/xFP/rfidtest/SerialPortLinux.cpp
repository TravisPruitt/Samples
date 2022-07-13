/**
    @file    SerialPortLinux.cpp
    @author  Greg Strange
    @date    Sept 2011


    Linux implementation of SerialPort class defined in SerialPort.h.
*/
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <termios.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>

#include "SerialPort.h"
#include "log.h"
#include "ticks.h"



using namespace Reader;



SerialPort::SerialPort(const char* portName) : _portName(portName)
{
}


SerialPort::~SerialPort()
{
    close();
}

void SerialPort::close()
{
    if (_fd)
        ::close(_fd);
}

/**
    Initialize the port.

    @param    baudRate    desired baud rate
    @param    parity      Parity ('o' for odd, 'e' for even, 'n' or anything else for none)

    NOTE: Currently baud rates 1200-38400 are implemented, but other baud rates are
    available, including faster baud rates.  Just need to find the right config bits
    to set.
*/
bool SerialPort::init(unsigned baudRate, char parity)
{
    _fd = open(_portName.c_str(), O_RDWR | O_NOCTTY ); 
    if (_fd <0) 
    {
        perror("Error opening serial port: ");
        char buf[200];
        strerror_r(errno, buf, sizeof(buf));
        LOG_ERROR("error opening serial port: %s\n", buf);
        return false;
    }

    struct termios newtio;

    // 8 data bits, 1 stop bit, even parity
    bzero(&newtio, sizeof(newtio));
    newtio.c_cflag = CS8 | (CLOCAL + CREAD);
    newtio.c_iflag = 0;
    newtio.c_oflag = 0;
    newtio.c_lflag = 0;

    switch (baudRate)
    {
    case 1200:  newtio.c_cflag |= B1200;  break;
    case 2400:  newtio.c_cflag |= B2400;  break;
    case 4800:  newtio.c_cflag |= B4800;  break;
    case 9600:  newtio.c_cflag |= B9600;  break;
    case 19200: newtio.c_cflag |= B9600;  break;
    case 38400: newtio.c_cflag |= B38400; break;
    case 115200: newtio.c_cflag |= B115200; break;
    default:
        LOG_ERROR("unimplemented baud rate requested\n");
        return false;
    }

    if (tolower(parity) == 'e')
        newtio.c_cflag |= PARENB;
    else if (tolower(parity) == 'o')
        newtio.c_cflag |= (PARENB | PARODD);
    
    // reads return immediately
    newtio.c_cc[VMIN]     = 0;
    newtio.c_cc[VTIME]    = 0;

    tcflush(_fd, TCIFLUSH);
    tcsetattr(_fd, TCSANOW, &newtio);
    return true;
}



/**
    Read and toss any characters waiting to be tossed
*/
void SerialPort::flushInput()
{
    uint8_t buf[100];
    size_t bytesRead;

    if (_fd < 0)    return;

    do
    {
        bytesRead = read(_fd, buf, sizeof(buf));
    }
    while (bytesRead >= 1 && bytesRead <= sizeof(buf));
}


/**
    Read a byte

    @param    returnByte    Where to put the byte
    @param    timeoutMs     Max time to wait for a byte (milliseconds)

    @return   true on success, false on timeout or other erro
*/
bool SerialPort::readByte(uint8_t* returnByte, unsigned timeoutMs)
{
    if (!_fd)
        return false;

    fd_set readset, exceptset;
    struct timeval timeout;

    timeout.tv_sec = timeoutMs / 1000;
    timeout.tv_usec = (timeoutMs % 1000) * 1000;

    FD_ZERO(&readset);
    FD_SET(_fd, &readset);

    FD_ZERO(&exceptset);
    FD_SET(_fd, &exceptset);

    int result = select(_fd+1, &readset, NULL, &exceptset, &timeout);
    if (result == 0)
    {
        LOG_DEBUG("read of %s timed out\n", _portName.c_str());
        return false;
    }
    else if (result < 0)
    {
        char buf[200];
        strerror_r(errno, buf, sizeof(buf));
        LOG_ERROR("select error: %d - %s\n", errno, buf);
        return false;
    }

    if (!FD_ISSET(_fd, &readset) )
    {
        LOG_ERROR("available but not!\n");
        return false;
    }

    if (read(_fd, returnByte, 1) == 0)
    {
        char buf[200];
        strerror_r(errno, buf, sizeof(buf));
        LOG_ERROR("read of %s failed, error %d - %s\n", _portName.c_str(), errno, buf);
        return false;
    }

    return true;
}


/**
    write some bytes
*/
bool SerialPort::write(const uint8_t* data, size_t length)
{
    return (_fd && ::write(_fd, data, length) == (int)length);
}

