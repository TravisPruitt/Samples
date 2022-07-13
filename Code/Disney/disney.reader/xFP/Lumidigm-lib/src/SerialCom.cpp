/******************************<%BEGIN LICENSE%>******************************/
// (c) Copyright 2011 Lumidigm, Inc. (Unpublished Copyright) ALL RIGHTS RESERVED.
//
//
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
//
/******************************<%END LICENSE%>******************************/
// SerialCom.cpp : implementation file
//

#include "windows.h"
#include "stdio.h"
#include "SerialCom.h"

SerialCom::SerialCom()
{
	m_strError = new char[255];
	sprintf(m_strError, "");		
}

SerialCom::~SerialCom()
{
	delete [] m_strError;
	m_strError = NULL;
}



/////////////////////////////////////////////////////////////////////////////
// SerialCom message handlers

BOOL SerialCom::OpenPort(char* port)
{
	char portname[255];
	sprintf_s(portname,"//./%s",port);

	hComm = CreateFileA(  portname,
						  GENERIC_READ | GENERIC_WRITE,
						  0,
						  0,
						  OPEN_EXISTING,
						  0,
						  0);

	if(hComm==INVALID_HANDLE_VALUE)
	{
		DWORD err = ::GetLastError();
		sprintf(m_strError, "OpenPort Failed for port (%s) with error code (%d)", portname, err);
		return false;
	}
	else
	{	
		return true;
	}
}

BOOL SerialCom::ConfigureCOMPort(DWORD BaudRate, BYTE ByteSize, DWORD fParity, BYTE Parity, BYTE StopBits)
{
	if((m_bPortReady = GetCommState(hComm, &m_dcb))==0)
	{
		DWORD err = ::GetLastError();
		sprintf(m_strError, "ConfigureCOMPort Failed, GetCommState returned error code (%d)", err);
		CloseHandle(hComm);
		return false;
	}
	m_dcb.BaudRate =BaudRate;
	m_dcb.ByteSize = ByteSize;
	m_dcb.Parity =Parity ;
	m_dcb.StopBits =StopBits;
	m_dcb.fBinary=TRUE;
	m_dcb.fDsrSensitivity=false;
	m_dcb.fParity=fParity;
	m_dcb.fOutX=false;
	m_dcb.fInX=false;
	m_dcb.fNull=false;
	m_dcb.fAbortOnError=FALSE;
	m_dcb.fOutxCtsFlow=FALSE;
	m_dcb.fOutxDsrFlow=false;
	m_dcb.fDtrControl=DTR_CONTROL_DISABLE;
	m_dcb.fDsrSensitivity=false;
	m_dcb.fRtsControl=RTS_CONTROL_DISABLE;
	m_dcb.fOutxCtsFlow=false;
	m_dcb.fOutxCtsFlow=false;

	m_bPortReady = SetCommState(hComm, &m_dcb);
	if(m_bPortReady ==0)
	{
		DWORD err = ::GetLastError();
		sprintf(m_strError, "ConfigureCOMPort Failed, SetCommState returned error code (%d)", err);
		CloseHandle(hComm);
		hComm = 0;
		return false;
	}
	SetCommunicationTimeouts(100,100,100,100,130);
	return true;
}

BOOL SerialCom::SetCommunicationTimeouts(DWORD ReadIntervalTimeout, DWORD ReadTotalTimeoutMultiplier, DWORD ReadTotalTimeoutConstant, DWORD WriteTotalTimeoutMultiplier, DWORD WriteTotalTimeoutConstant)
{
		if((m_bPortReady = GetCommTimeouts (hComm, &m_CommTimeouts))==0)
		   return false;
		m_CommTimeouts.ReadIntervalTimeout =ReadIntervalTimeout;
		m_CommTimeouts.ReadTotalTimeoutConstant =ReadTotalTimeoutConstant;
		m_CommTimeouts.ReadTotalTimeoutMultiplier =ReadTotalTimeoutMultiplier;
		m_CommTimeouts.WriteTotalTimeoutConstant = WriteTotalTimeoutConstant;
		m_CommTimeouts.WriteTotalTimeoutMultiplier =WriteTotalTimeoutMultiplier;
		m_bPortReady = SetCommTimeouts (hComm, &m_CommTimeouts);
		if(m_bPortReady ==0)
		{
			CloseHandle(hComm);
			return false;
		}
		return true;
}

BOOL SerialCom::WriteByte(BYTE bybyte)
{
	iBytesWritten=0;
	int Timeout = 0;
	while(Timeout < 40)
	{
		if(WriteFile(hComm,&bybyte,1,&iBytesWritten,NULL)==0)
		{
			Timeout++;	
		} else
		{
			break;
		} 
	}
	if(Timeout == 40) 
	{
		return false;
	}

	return true;
}
BOOL SerialCom::WriteBytes(BYTE* bybyte, unsigned int nSize)
{
	iBytesWritten=0;
	BOOL ret = WriteFile(hComm,bybyte,nSize,&iBytesWritten,NULL);
	return ( (ret == TRUE ) && (iBytesWritten == nSize) )?true:false;
}
BOOL SerialCom::ReadBytes(BYTE* bybyte, unsigned int nSize)
{
	int ii = 0;
	int nTimeoutCounter = 5;

	for(ii = 0 ; ( ii < int(nSize) && nTimeoutCounter ) ; ii++)
	{
		if( TRUE != ReadFile(hComm, bybyte+ii, 1, &iBytesRead, NULL) )
		{
			return false;
		} else
		if( iBytesRead == 0 && nSize > 0)
		{
			// Returned "TRUE" but 0 bytes read...lets try again for
			// non single-byte requests.
			nTimeoutCounter--;
			ii--;
			continue;
		}
	}
	return true;
}
BOOL SerialCom::ReadByte(BYTE	&resp)
{
	BYTE rx;
	resp=0;
	int Timeout = 0;
	DWORD dwBytesTransferred=0;
	int rc = ReadFile (hComm, &rx, 1, &dwBytesTransferred, 0);
	if (rc)
	{
		 if (dwBytesTransferred == 1)
		 {
			 resp=rx;
			 return true;
		 } 
	} else
	{
// deal with the error code 
		int dwError;
		switch (dwError = GetLastError()) 
		{ 
			case ERROR_HANDLE_EOF: 
			{ 
				// we have reached the end of
				// the file during asynchronous
				// operation
			} break;
			case ERROR_INVALID_HANDLE:
			{
				
			} break;

			// deal with other error cases 
		}   //end switch (dwError = GetLastError())
		printf("\nTimeout");
	}
				  
	return false;
}


void SerialCom::ClosePort()
{
	if(hComm)
	{
		CloseHandle(hComm);
		hComm = NULL;
	}
}
