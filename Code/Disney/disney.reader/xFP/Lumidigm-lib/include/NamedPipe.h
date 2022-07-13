#pragma once

#include "windows.h"
#include <string>

typedef enum
{
	Server,
	Client
} PipeMode;

typedef unsigned char uchar;
typedef unsigned int  uint;

class NamedPipe
{
public:
	NamedPipe();
	~NamedPipe(void);
	bool Initialize(PipeMode mode, uchar* pPipeName);
	bool ReadPipe( uchar* pBuffer, uint nSize, uint nTimeoutMS);
	bool WritePipe(uchar* pBuffer, uint nSize, uint nTimeoutMS);
private:
	//
	bool InitServer( uchar* pPipeName);
	bool InitClient( uchar* pPipeName);
	// Error handling
	bool DisplayLastError();
	PipeMode	m_mode;
	HANDLE		m_hPipe;

};
