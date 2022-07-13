#include "windows.h"
#include "NamedPipe.h"
#include "strsafe.h"

#define g_szPipeName "\\\\.\\Pipe\\MyNamedPipe"
#define BUFFER_SIZE	524288

NamedPipe::NamedPipe()
{
	m_hPipe = 0;
}

NamedPipe::~NamedPipe(void)
{
	CloseHandle(m_hPipe);
}

bool NamedPipe::Initialize(PipeMode mode,  uchar* pPipeName)
{
	bool bRet = false; 
	 
	switch(mode)
	{	
		case Server:
		{
			bRet = InitServer(pPipeName);
		} break;
		case Client:
			{
			bRet = InitClient(pPipeName);
		} break; 
	}
	return bRet;
}

bool NamedPipe::InitServer( uchar* pPipeName)
{
	if( m_hPipe ) return false;


	SECURITY_ATTRIBUTES sa;
	sa.lpSecurityDescriptor = (PSECURITY_DESCRIPTOR)malloc(SECURITY_DESCRIPTOR_MIN_LENGTH);
	InitializeSecurityDescriptor(sa.lpSecurityDescriptor, SECURITY_DESCRIPTOR_REVISION);
	// ACL is set as NULL in order to allow all access to the object.
	SetSecurityDescriptorDacl(sa.lpSecurityDescriptor, TRUE, NULL, FALSE);
	sa.nLength = sizeof(sa);
	sa.bInheritHandle = TRUE;	




	m_hPipe  = CreateNamedPipeA( (LPCSTR)&pPipeName[0], // pipe name 
							  PIPE_ACCESS_DUPLEX,       // read/write access 
							  PIPE_TYPE_MESSAGE |       // message type pipe 
							  PIPE_READMODE_MESSAGE |   // message-read mode 
							  PIPE_WAIT,                // blocking mode 
							  PIPE_UNLIMITED_INSTANCES, // max. instances  
							  BUFFER_SIZE,              // output buffer size 
							  BUFFER_SIZE,              // input buffer size 
							  NMPWAIT_USE_DEFAULT_WAIT, // client time-out 
							  &sa);//NULL);                    // default security attribute 
	
	//Wait for the client to connect
	BOOL bClientConnected = ConnectNamedPipe(m_hPipe, NULL);
	
	return (m_hPipe != INVALID_HANDLE_VALUE)?true:false;
}


bool NamedPipe::InitClient( uchar* pPipeName )
{	
	bool bRC = false;
	if( m_hPipe ) return bRC;	

	for(int nCounter = 0; nCounter < 50; nCounter++) // 50 * 20 ms = 1 second
	{
		// Open the pipe
		m_hPipe = CreateFileA((LPCSTR)&pPipeName[0], // pipe name 
							  GENERIC_READ |  // read and write access 
							  GENERIC_WRITE, 
							  FILE_SHARE_READ|FILE_SHARE_WRITE|FILE_SHARE_DELETE,              // no sharing 
							  NULL,           // default security attributes
							  OPEN_EXISTING,  // opens existing pipe 
							  0,              // default attributes 
							  NULL);          // no template file 		
	 
		if (m_hPipe != INVALID_HANDLE_VALUE) 
		{
			bRC = true;
			break;
		} 

		// Exit if an error other than ERROR_PIPE_BUSY occurs. 
		if (GetLastError() != ERROR_PIPE_BUSY) 
		{
			printf("\nError occurred while creating the pipe: err = %d, pipe name = %s", GetLastError(), pPipeName); 
			break;  
		}

		//printf("\nPipe %s is busy on iteration %d.  \n  Sleep 20 miliseconds and try again. \n  If iteration >= 50, return false.", pPipeName, nCounter);

		if(!WaitNamedPipeA((LPCSTR)&pPipeName[0], 1000))//NMPWAIT_USE_DEFAULT_WAIT);
		{
			DWORD nLE = GetLastError();

			// If the server has not started just wait and try again
			if(ERROR_FILE_NOT_FOUND == nLE)
			{
				fprintf(stdout, "Could not connect to pipe in timeout period! Pipe not created yet\n");
			}
			else
			{
				fprintf(stdout, "Could not connect to pipe in timeout period! Error: %d\n",nLE);
			}
		}
		
	}

	return bRC;

}

bool NamedPipe::ReadPipe( uchar* pBuffer, uint nSize, uint nTimeoutMS)
{
	DWORD nSizeRead = 0;
	if( m_hPipe == NULL) return false;
	if(TRUE != ReadFile(m_hPipe, pBuffer, nSize, &nSizeRead, NULL))
	{
		DisplayLastError();
		fprintf(stdout,"\n NamedPipe::WritePipe (VCOMCore)- Error reading file: %d", GetLastError());	
	}
	return true;
}

bool NamedPipe::WritePipe(uchar* pBuffer, uint nSize, uint nTimeoutMS)
{
	DWORD nSizeWritten = 0;
	if( m_hPipe == NULL) return false;
	if(TRUE != WriteFile(m_hPipe, pBuffer, nSize, &nSizeWritten, NULL))
	{
		DisplayLastError();
		fprintf(stdout,"\n NamedPipe::WritePipe (VCOMCore)- Error writing file: %d", GetLastError());	
	}
	return true;
}

// Last Error

bool NamedPipe::DisplayLastError()
{
    // Retrieve the system error message for the last-error code

    LPVOID lpMsgBuf;
    LPVOID lpDisplayBuf;
    DWORD dw = GetLastError(); 

    FormatMessage(
        FORMAT_MESSAGE_ALLOCATE_BUFFER | 
        FORMAT_MESSAGE_FROM_SYSTEM |
        FORMAT_MESSAGE_IGNORE_INSERTS,
        NULL,
        dw,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
        (LPTSTR) &lpMsgBuf,
        0, NULL );

    // Display the error message and exit the process

    lpDisplayBuf = (LPVOID)LocalAlloc(LMEM_ZEROINIT,(lstrlen((LPCTSTR)lpMsgBuf) + 40) * sizeof(TCHAR)); 
    StringCchPrintf((LPTSTR)lpDisplayBuf, 
        LocalSize(lpDisplayBuf) / sizeof(TCHAR),
        TEXT("\nError %d: %s"), 
        dw, lpMsgBuf); 
    
	wprintf((wchar_t*)lpDisplayBuf);

	LocalFree(lpMsgBuf);
    LocalFree(lpDisplayBuf);
	return true;
}

