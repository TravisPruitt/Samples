/**
    @file   SerialPort.cpp
    @author Roy Sprowl
    @date   Seept 19, 2011

   
*/


#include "SerialPort.h"
#include "windows.h"
#include "log.h"
#include "ticks.h"

using namespace Reader;

SerialPort::SerialPort(const char* portName) : _portName(portName), _handle(INVALID_HANDLE_VALUE)
{
}

SerialPort::~SerialPort()
{
    close();
}

bool SerialPort::init(unsigned baudRate, char parity)
{
    _handle = CreateFile(_portName.c_str(),
        GENERIC_READ | GENERIC_WRITE,
        0,
        NULL,
        OPEN_EXISTING,
        FILE_FLAG_OVERLAPPED,
        NULL);

    if (_handle == INVALID_HANDLE_VALUE)
    {
        if(GetLastError() == ERROR_FILE_NOT_FOUND)
        {
            // TODO: serial port does not exist.
            int j = 0;
        }
        // TODO: some other error occurred.
        // TODO: error handling here
        int j = 0;
    }

    DCB dcbSerialParams = {0};
    dcbSerialParams.DCBlength=sizeof(dcbSerialParams);
    if (!GetCommState(_handle, &dcbSerialParams)) 
    {
        //error getting state
        int j = 0;
    }
    dcbSerialParams.BaudRate = baudRate;
    dcbSerialParams.ByteSize = 8;
    dcbSerialParams.StopBits = ONESTOPBIT;

    if (tolower(parity) == 'e')
        dcbSerialParams.Parity = EVENPARITY;
    else if (tolower(parity) == 'o')
        dcbSerialParams.Parity = ODDPARITY;
    else
        dcbSerialParams.Parity = NOPARITY;

    //dcbSerialParams.
    if (!SetCommState(_handle, &dcbSerialParams))
    {
        //error setting serial port state
        return false;
    }

    //COMMTIMEOUTS timeouts = {0};
    //timeouts.ReadIntervalTimeout = 500;
    //timeouts.ReadTotalTimeoutConstant = 50;
    //timeouts.ReadTotalTimeoutMultiplier = 10;
    //timeouts.WriteTotalTimeoutConstant = 500;
    //timeouts.WriteTotalTimeoutMultiplier = 10;
    //if(!SetCommTimeouts(_handle, &timeouts))
    //{
    //    //TODO: error occured
    //    return false;
    //}    

    return true;
};


void SerialPort::close()
{
    if (_handle != INVALID_HANDLE_VALUE)
        CloseHandle(_handle);

    _handle = INVALID_HANDLE_VALUE;
}


bool SerialPort::write(const uint8_t* pBuffer, size_t numBytesToWrite )
{
    DWORD numBytesWritten = 0;
    OVERLAPPED overlapped = { 0 };
    overlapped.hEvent = CreateEvent(NULL, FALSE, FALSE, NULL);

    if (!WriteFile(_handle, pBuffer, numBytesToWrite, &numBytesWritten, &overlapped))
    {
        if (GetLastError() != ERROR_IO_PENDING)
            goto write_error;
    }

    DWORD result = WaitForSingleObject(overlapped.hEvent, 1000);
    if (result != WAIT_OBJECT_0)
    {
        if (result == WAIT_TIMEOUT)
            goto write_timeout;
        else
            goto write_error;
    }

    if (!GetOverlappedResult(_handle, &overlapped, &numBytesWritten, TRUE))
        goto write_error;

    CloseHandle(overlapped.hEvent);
    return true;

write_timeout:
    CancelIo(_handle);
    CloseHandle(overlapped.hEvent);
    LOG_WARN("serial port write timeout\n");
    return false;

write_error:
    CancelIo(_handle);
    CloseHandle(overlapped.hEvent);
    LOG_TRAFFIC("serial port write error\n");
    return false;
}



bool SerialPort::readByte(uint8_t* returnValue, unsigned timeoutMs)
{
    DWORD numBytesRead = 0;
    unsigned char buffer[2];

    //doing overlapped for async behavior, with timeout period...
    OVERLAPPED overlapped = {0};
    overlapped.hEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
      
    if (!ReadFile(_handle, buffer, 1, NULL, &overlapped))
    {
        if (GetLastError() != ERROR_IO_PENDING)
            goto read_error;
    }

    while (numBytesRead < 1)
    {
        //error on timeout, other?
        DWORD result = WaitForSingleObject(overlapped.hEvent, timeoutMs);
        if (result != WAIT_OBJECT_0)  
        {
            if (result == WAIT_TIMEOUT)
                goto read_timeout;
            else
                goto read_error;
        }

        //get bytes read
        if (!GetOverlappedResult(_handle, &overlapped, &numBytesRead, TRUE))
            goto read_error;
    }

    CloseHandle(overlapped.hEvent);
    *returnValue = buffer[0];
    return true;

read_timeout:
    CancelIo(_handle);
    CloseHandle(overlapped.hEvent);
    LOG_INFO("serial port read timeout\n");
    return false;

read_error:
    CancelIo(_handle);
    CloseHandle(overlapped.hEvent);
    LOG_TRAFFIC("serial port read error\n");
    return false;
}



void SerialPort::flushInput()
{
    // Not implemented on Windows.
    // This is a nice to have, but not strictly necessary on Windows for the test we do on Windows
}

