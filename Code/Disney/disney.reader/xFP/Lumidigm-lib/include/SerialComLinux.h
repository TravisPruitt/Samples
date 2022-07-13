#pragma once
#ifndef _GCC
#include "windows.h"
#else
#include <stdio.h>
//#define BOOL    bool
#define BYTE    unsigned char
//#define DWORD   long
#include "gccWindows.h"
#endif
class SerialComLinux 
{
// Construction
public:
	SerialComLinux();
	virtual ~SerialComLinux();
public:
	void  ClosePort();
	BOOL  ReadByte(BYTE &resp);
	BOOL  WriteByte(BYTE bybyte);
	BOOL  WriteBytes(BYTE* bybyte, unsigned int nSize);
	BOOL  ReadBytes(BYTE* bybyte, unsigned int nSize);
	BOOL  OpenPort(char* portname);
	BOOL  SetCommunicationTimeouts(DWORD ReadIntervalTimeout,DWORD ReadTotalTimeoutMultiplier,DWORD ReadTotalTimeoutConstant,DWORD WriteTotalTimeoutMultiplier,DWORD WriteTotalTimeoutConstant);
	BOOL  ConfigureCOMPort(DWORD BaudRate,BYTE ByteSize,DWORD fParity,BYTE  Parity,BYTE StopBits);
	BOOL  m_bPortReady;
	BOOL  bWriteRC;
	BOOL  bReadRC;
	DWORD iBytesWritten;
	DWORD iBytesRead;
	DWORD dwBytesRead;
private:
        int   m_nfd;
};


