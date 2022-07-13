/******************************<%BEGIN LICENSE%>******************************/
// (c) Copyright 2009 Lumidigm, Inc. (Unpublished Copyright) ALL RIGHTS RESERVED.
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


#include "TransportSE.h"
#include <windows.h>
#include <stdio.h>
#include "VCOMCore.h"
#include "NamedPipe.h"
#include "Device.h"
#include "V100DeviceHandler.h"

typedef struct _USBCB			// USB command block
{
  unsigned int ulCommand;				// command to execute
  unsigned int ulData;					// generic data field
  unsigned int ulCount;					// number of bytes to transfer
} USBCB, *PUSBCB;

#define MAX_DATA_BYTES_EZEXTENDER	65535

#undef UNICODE

TransportSE::TransportSE(void)
{
	memset(hPhysicalDeviceID,0,2048);
#if _WIN32
	hMutexID = NULL;
	pSE = new NamedPipe();
#endif
}
TransportSE::~TransportSE(void)
{
#if _WIN32
	delete pSE;
	pSE = NULL;
#endif
}
// ITransport
uint TransportSE::Initialize(V100_DEVICE_TRANSPORT_INFO* pTransport)
{
	Device* dev = reinterpret_cast<Device*>( pTransport->hInstance );	

	if ( NULL == dev )
		return 2;

	DWORD NumDevices = 0;
	/*
	** Find Number of USB Devices Active
	*/
	int nNumDev = 0;
	V100DeviceHandler::GetV100DeviceHandler()->SetServerName((char*)dev->GetCommServerName());
	V100DeviceHandler::GetV100DeviceHandler()->GetNumDevices(&nNumDev);
	NumDevices = nNumDev;
	if ( !NumDevices )
		return 1;

	// make sure it's a valid device number

	if ( dev->m_nMemberIndex >= NumDevices )
		return 2;



	uchar pipeName[SZ_OF_PIPE_NAME];
	uchar serverName[SZ_OF_PIPE_NAME];

	memset(&pipeName, 0, SZ_OF_PIPE_NAME);
	dev->GetCommPipeName(pipeName);
	memset(serverName, 0, SZ_OF_PIPE_NAME);
	dev->GetCommServerName(serverName);

	uchar tmpPipeName[SZ_OF_PIPE_NAME];
	memset(tmpPipeName, 0, SZ_OF_PIPE_NAME);

	sprintf((char*)tmpPipeName, "\\\\%s\\PIPE\\%s", serverName, pipeName);
	Sleep(100);
	return (true == pSE->Initialize(Client, tmpPipeName))?0:7;
}

#if _WIN32
HANDLE TransportSE::GetMutexId()
{
	if( strcmp(hPhysicalDeviceID,"") == 0)
	{
		return NULL;
	}
	if(hMutexID == NULL)
	{
		hMutexID = CreateMutexA(NULL, FALSE, hPhysicalDeviceID);
	}
	return hMutexID;
}
#endif

uint  TransportSE::Close(V100_DEVICE_TRANSPORT_INFO* pTransport)
{
#ifdef _GCC
// fix this later, check log, may not be needed may happen automatically
//	close((int)pTransport->hWrite);
#else
	CloseHandle(pTransport->hWrite);
	CloseHandle(pTransport->hRead);
	CloseHandle(hMutexID);
#endif
	return 0;
}
bool TransportSE::GetDeviceId(V100_DEVICE_TRANSPORT_INFO* pTransport, char* szDeviceId, uint& nLength)
{
	nLength = 0;
#ifdef _WIN32
	strcpy(szDeviceId, hPhysicalDeviceID);
	nLength = (uint)strlen(szDeviceId)+1;
	return true;
#else
	return false;
#endif
}


#define MSEC_TIMEOUT 5000

uint TransportSE::TransmitCommand(V100_DEVICE_TRANSPORT_INFO* pTransport, uchar* myPacket, uint nTxSize, uchar* pResponse, uint& nRxSize)
{
#ifndef _GCC
	HANDLE hEvent = GetMutexId();
	// Wait for it
	if(WAIT_TIMEOUT == WaitForSingleObject(hEvent, MutexTimeout))
	{
		fprintf(stdout,"\nMutex timed-out");
		return FALSE;
	}
#endif
	bool	ret = true;
	// Fill out simple header
	USBCB usbcb;								// command block
	usbcb.ulCommand = 0x00;						// command
	usbcb.ulCount	= nTxSize;					// not used 
	usbcb.ulData	= 0x0;						// not used 
	nRxSize = 0;
// Send Challenge Header Packet
	if (!WriteBytes(pTransport->hWrite, &usbcb, sizeof(usbcb), MSEC_TIMEOUT))
	{
#ifndef _GCC
		ReleaseMutex(hEvent);
#endif
		return FALSE;
	}
// Send Challenge Data Packet
	if (!WriteBytes(pTransport->hWrite, myPacket, usbcb.ulCount, MSEC_TIMEOUT))
	{
#ifndef _GCC
		ReleaseMutex(hEvent);
#endif
		return FALSE;
	}
// Read Response Header Packet
	if (!ReadBytes(pTransport->hRead, &usbcb, sizeof(usbcb), MSEC_TIMEOUT))
	{
#ifndef _GCC
		ReleaseMutex(hEvent);
#endif
		return FALSE;
	}
	if (!ReadBytes(pTransport->hRead, pResponse, usbcb.ulCount, MSEC_TIMEOUT))
	{
#ifndef _GCC
		ReleaseMutex(hEvent);
#endif
		return FALSE;
	}
	if( pResponse[0] != 0x0D || pResponse[1] != 0x56 )
	{
		int breakme;
                breakme = 1;	
	}
	nRxSize = usbcb.ulCount;
#ifndef _GCC
	ReleaseMutex(hEvent);
#endif
	return ret;
}
bool TransportSE::WriteBytes(HANDLE hWrite, void* pPacket, uint nPacketSize, uint mSecTimeout)
{
	char* pData = (char*)pPacket;
	uint nElementCount =  nPacketSize;
	 
 	bool  bTimeOut = false;
	DWORD Start = GetTickCount();
	while(!bTimeOut)
	{
		if (!pSE->WritePipe((uchar*)pData, nElementCount, 10))
		{
			return false;
		} else
		{
			return true;
		}
	}

	return true;
}
/*
** Reads bytes, returns false on timeout or other error
*/
bool TransportSE::ReadBytes(HANDLE hRead, void* pPacket, uint nPacketSize, uint mSecTimeout)
{
    bool bTimeOut = false;
	unsigned char * pRxBuffer = (unsigned char *)pPacket;
	int nRemainder = nPacketSize;
	DWORD Start = GetTickCount();
	
	while(!bTimeOut)
	{
		if (!pSE->ReadPipe(pRxBuffer, nRemainder, 10))
		{
			if( (GetTickCount() - Start) < mSecTimeout) continue;
			bTimeOut = true;
		} else
		{
			return true;
		}
	}

	if(bTimeOut == true) 
	{
		return false;
	}
	return true;
}


