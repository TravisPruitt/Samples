#pragma once

#include "windows.h"

typedef enum
{
	Server,
	Client
} PipeMode;

typedef unsigned char uchar;
typedef unsigned int  uint;

class LDSNamedPipe
{
public:
	LDSNamedPipe();
	~LDSNamedPipe(void);
	bool Initialize(PipeMode mode, LPCSTR lpPipeName);
	bool ReadPipe( uchar* pBuffer, uint nSize, uint nTimeoutMS);
	bool WritePipe(uchar* pBuffer, uint nSize, uint nTimeoutMS);
	bool GetPipeName(LPCSTR lpPipeName);
	bool Flush();
	bool Disconnect();

private:
	//
	bool InitServer();
	bool InitClient();
	// Error handling
	bool DisplayLastError();
	PipeMode	m_mode;
	HANDLE		m_hPipe;
	LPCSTR		m_lpPipeName;
	PipeMode	m_pipeMode;

			

};
