/******************************<%BEGIN LICENSE%>******************************/
// (c) Copyright 2008 Lumidigm, Inc. (Unpublished Copyright) ALL RIGHTS RESERVED.
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

#include "TransportUSB.h"
#include <windows.h>
#include <stdio.h>
#include "VCOMCore.h"
#include "usbcmdset.h"


TransportUSB::TransportUSB(void)
{

}
TransportUSB::~TransportUSB(void)
{

}
#define MAX_RETRIES 100
// ITransport
uint TransportUSB::Initialize(V100_DEVICE_TRANSPORT_INFO* pTransport)
{
	TCHAR portname[255];
	uint port = 1;
	wsprintf(portname,L"LDM%d:", port);

OpenPipes:
	pTransport->hWrite = pTransport->hRead = CreateFile(  portname,
											  GENERIC_READ | GENERIC_WRITE,
											  FILE_SHARE_READ | FILE_SHARE_WRITE,
											  0,
											  OPEN_EXISTING,
											  0,
											  0);

	if(pTransport->hWrite == INVALID_HANDLE_VALUE) 
	{
		DWORD err = GetLastError();
		if(ERROR_DEV_NOT_EXIST == err && port <= MAX_RETRIES)
		{
			// Handle multiple devices connected and device reboots
			port++;
			wsprintf(portname,L"LDM%d:", port);

			goto OpenPipes;
		}
			
		wprintf(L"Can't open read/write pipes! Max attempts reached (GLE = %u)\n", err);
		return 1;
	}

	COMMTIMEOUTS m_CommTimeouts;
	int m_bPortReady = 0;
	if((m_bPortReady = GetCommTimeouts(pTransport->hWrite, &m_CommTimeouts))==0)
	{
		wprintf(L"Port not ready!\n");
		return 2;
	}
	
	DWORD le = GetLastError();
	
	if(pTransport->hWrite == (void*)-1)
	{
		wprintf(L"Can't open %s, GLE = %u \n",portname,le);
		return USB_WRITE_PIPE_ERROR; //should be read write error?
	}
	// Save Transport Handle
	
	return 0;
}





uint  TransportUSB::Close(V100_DEVICE_TRANSPORT_INFO* pTransport)
{
	CloseHandle(pTransport->hWrite);
	return 0;
}

bool TransportUSB::GetDeviceId(V100_DEVICE_TRANSPORT_INFO* pTransport, char* szDeviceId, uint& nLength)
{
	// Not supported in WinCE just return 0
	nLength = sprintf(szDeviceId, "0");
	return true;
}

#define MSEC_TIMEOUT 5000

uint TransportUSB::TransmitCommand(V100_DEVICE_TRANSPORT_INFO* pTransport, uchar* myPacket, uint nTxSize, uchar* pResponse, uint& nRxSize)
{

	int		nakCounter = 0;
	bool	ret = true;
	DWORD	Count = 0;
	// Fill out simple header
	USBCB usbcb;								// command block
	usbcb.ulCommand = 0x00;						// command
	usbcb.ulCount	= nTxSize;					// not used 
	usbcb.ulData	= 0x0;						// not used 
	nRxSize = 0;
// Send Challenge Header Packet
	if (!WriteBytes(pTransport->hWrite, &usbcb, sizeof(usbcb), MSEC_TIMEOUT))
		return FALSE;
// Send Challenge Data Packet
	if (!WriteBytes(pTransport->hWrite, myPacket, usbcb.ulCount, MSEC_TIMEOUT))
		return FALSE;
// Read Response Header Packet
	if (!ReadBytes(pTransport->hRead, &usbcb, sizeof(usbcb), MSEC_TIMEOUT))
		return FALSE;
	if (!ReadBytes(pTransport->hRead, pResponse, usbcb.ulCount, MSEC_TIMEOUT))
		return FALSE;
	nRxSize = usbcb.ulCount; 
	return ret;
}
bool TransportUSB::WriteBytes(HANDLE hWrite, void* pPacket, uint nPacketSize, uint mSecTimeout)
{
  	
	// Send X number of 64K packets	     
    int nIterations   = nPacketSize/MAX_DATA_BYTES_EZEXTENDER;
    int nRemainder    = nPacketSize%MAX_DATA_BYTES_EZEXTENDER;
    DWORD ulActualBytes = 0;

	if(nPacketSize == MAX_DATA_BYTES_EZEXTENDER)
	{
		nIterations = 0;
		nRemainder  = MAX_DATA_BYTES_EZEXTENDER;
	}

    for(int ii = 0 ; ii < nIterations ; ii++)
    { 
  	    char* pData = (char*)pPacket + ii*MAX_DATA_BYTES_EZEXTENDER;
  	    uint nElementCount = MAX_DATA_BYTES_EZEXTENDER;
  	     
	    if (!WriteBytes(hWrite, pData, nElementCount, mSecTimeout) )
	    {
		     return false;
		}
     }
     // Write remainder
     char* pData = (char*)pPacket + nIterations*MAX_DATA_BYTES_EZEXTENDER;
	 uint nElementCount = nRemainder;
 	 bool  bTimeOut = false;
	 
	DWORD Start = GetTickCount();
	while(!bTimeOut)
	{
		if (!WriteFile(hWrite,pData,nElementCount,&ulActualBytes,NULL))
		{
			if( (GetTickCount() - Start) < mSecTimeout) continue;
			bTimeOut = true;
		} else
		{
			return true;
		}
	}

	return bTimeOut;
}
/*
** Reads bytes, returns false on timeout or other error
*/
bool TransportUSB::ReadBytes(HANDLE hRead, void* pPacket, uint nPacketSize, uint mSecTimeout)
{
	DWORD nRxSize;
	int nIterations   = nPacketSize/MAX_DATA_BYTES_EZEXTENDER;
	int nRemainder    = nPacketSize%MAX_DATA_BYTES_EZEXTENDER;
    unsigned char * pRxBuffer = (unsigned char *)pPacket;
	    
	if(nPacketSize == MAX_DATA_BYTES_EZEXTENDER)
	{
		nIterations = 0;
		nRemainder  = MAX_DATA_BYTES_EZEXTENDER;
	}
	for(int ii = 0 ; ii < nIterations ; ii++)
    { 
  	     pRxBuffer = (unsigned char *)pPacket + ii*MAX_DATA_BYTES_EZEXTENDER;
		 if(FALSE == ReadBytes(hRead, pRxBuffer, MAX_DATA_BYTES_EZEXTENDER, mSecTimeout))
		 {
			return false;
		 }
    }
    // Read and time remainder, since this is the only thing that actually reads a pipe anyways.
	bool  bTimeOut = false;
	DWORD Start = GetTickCount();
    pRxBuffer = (unsigned char *)pPacket + nIterations*MAX_DATA_BYTES_EZEXTENDER;

#ifdef _GCC
	nRxSize = read((int)hRead, pRxBuffer, nRemainder);
	if(nRxSize == -1)
		perror("ERROR: TransportUSB::ReadBytes:  ");
    //if( (GetTickCount() - Start) >= mSecTimeout)
    //            bTimeOut = true;


#else
    
	while(!bTimeOut)
	{
		int nX = ReadFile (hRead, pRxBuffer, nRemainder, &nRxSize, 0);
		if (nRxSize == 0)
		{
			if( (GetTickCount() - Start) < mSecTimeout) 
			{
				Sleep(10);
				continue;
			} else
			{
				bTimeOut = true;
			}
		} else
		{
			if( nRemainder > 10000)
			{
				int breakme = 1;
			}
			return true;
		}
	}
#endif
	// Check actual received size
    if(nRxSize != nRemainder)
	 {
	 	printf("nRxSize:%d is not equal to nRemainder:%d",nRxSize,nRemainder);
		return false;
	 }
	return (bTimeOut==true)?false:true;
}


