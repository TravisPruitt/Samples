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


#include "TransportUSB.h"
#ifdef _GCC
#include <errno.h>
#include "gccWindows.h"
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#else
#include <windows.h>
#endif
#include <stdio.h>
#include "VCOMCore.h"
#ifndef _GCC
#include "V100DeviceHandler.h"
#include "Device.h"
#endif

#undef UNICODE
#ifdef __SUPPORT_WIN32_USB__
#ifndef _GCC
#include <setupapi.h>
#include <usbdi.h>
#include "ldiguid.h"
#endif
#include "usbcmdset.h"
#ifndef _GCC
#include "usbdriver.h"
#endif

TransportUSB::TransportUSB(void)
{
	memset(hPhysicalDeviceID,0,2048);
	handleToClose = 0;
#if _WIN32
	hMutexID = NULL;
	pUSB = new USBDriverInterface();
#endif
}
TransportUSB::~TransportUSB(void)
{
#if _WIN32
	delete pUSB;
	pUSB = NULL;
#endif
#ifdef _GCC
    close(handleToClose);
    handleToClose = 0;
#endif
}
// ITransport
uint TransportUSB::Initialize(V100_DEVICE_TRANSPORT_INFO* pTransport)
{
#ifdef _GCC
    char deviceName[255];
    sprintf(deviceName,"/dev/vcom%d",pTransport->DeviceNumber);
    handleToClose = open(deviceName, O_RDWR|O_NDELAY,0);
    pTransport->hWrite = pTransport->hRead = (void*)handleToClose;

	if(pTransport->hWrite == (void*)-1)
	{
		fprintf(stdout,"\n (%s,%d) - Failed open.", __FILE__, __LINE__);
		return USB_WRITE_PIPE_ERROR; //should be read write error?
	}
#else

	int nNumDev = 0;
	
	if(GEN_OK != V100_Get_Num_USB_Devices(&nNumDev))
		return USB_INVALID_DEVICE;

	if(nNumDev == 0)
		return USB_NO_DEVICES_FOUND;

	Device* device = reinterpret_cast<Device*>( pTransport->hInstance );

	if ( NULL == device )
		return USB_INVALID_DEVICE;

	if ( device->m_nMemberIndex >= uint(nNumDev) )
		return USB_INVALID_DEVICE;

	// open write handle
	pTransport->hWrite = pUSB->OpenDeviceHandle( (LPGUID)&GUID_BULKLDI, 
												  WRITE_PIPE, 
												  device->m_nMemberIndex,//pTransport->DeviceNumber, 
												  TRUE,
												  hPhysicalDeviceID);		// USE ASYNC
	if( pTransport->hWrite == INVALID_HANDLE_VALUE)
		return USB_WRITE_PIPE_ERROR;

	// open read handle
	pTransport->hRead = pUSB->OpenDeviceHandle( (LPGUID)&GUID_BULKLDI, 
												 READ_PIPE, 
												 device->m_nMemberIndex,//pTransport->DeviceNumber, 
												 TRUE,
												 hPhysicalDeviceID);
	
	if( pTransport->hRead == INVALID_HANDLE_VALUE)
		return USB_READ_PIPE_ERROR;
	// Must copy over the first 4 characters to 1) avoid collisions, 
	// and 2) Make sure that you can use the name as a mutex handle.
	strncpy(hPhysicalDeviceID,"LUMI",4);
#endif
	return USB_OK;
}
#if _WIN32
HANDLE TransportUSB::GetMutexId()
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

uint  TransportUSB::Close(V100_DEVICE_TRANSPORT_INFO* pTransport)
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
bool TransportUSB::GetDeviceId(V100_DEVICE_TRANSPORT_INFO* pTransport, char* szDeviceId, uint& nLength)
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


#define MSEC_TIMEOUT 15000

uint TransportUSB::TransmitCommand(V100_DEVICE_TRANSPORT_INFO* pTransport, uchar* myPacket, uint nTxSize, uchar* pResponse, uint& nRxSize)
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
		fprintf(stdout,"\n (%s,%d) - Failed write (usbcb).", __FILE__, __LINE__);
		
		return FALSE;
	}
// Send Challenge Data Packet
	if (!WriteBytes(pTransport->hWrite, myPacket, usbcb.ulCount, MSEC_TIMEOUT))
	{
#ifndef _GCC
		ReleaseMutex(hEvent);
#endif
		fprintf(stdout,"\n (%s,%d) - Failed write (Packet).", __FILE__, __LINE__);
		return FALSE;
	}
// Read Response Header Packet
	if (!ReadBytes(pTransport->hRead, &usbcb, sizeof(usbcb), MSEC_TIMEOUT))
	{
#ifndef _GCC
		ReleaseMutex(hEvent);
#endif
		fprintf(stdout,"\n (%s,%d) - Failed read (usbcb).", __FILE__, __LINE__);

		return FALSE;
	}
	if (!ReadBytes(pTransport->hRead, pResponse, usbcb.ulCount, MSEC_TIMEOUT))
	{
#ifndef _GCC
		ReleaseMutex(hEvent);
#endif
		fprintf(stdout,"\n (%s,%d) - Failed read (packet).", __FILE__, __LINE__);

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

#ifdef _GCC
	int nTOcount = 0;

	while(!bTimeOut)
	{
		ulActualBytes = write((size_t)hWrite, pData, nElementCount);
		if((int)ulActualBytes == 0)
		{
			if(nTOcount++ >= 5000)//5 sec timeout
			{
				bTimeOut = true;
				fprintf(stdout, "\nError TransportUSB::WriteBytes Timed out\n");
				break;
			}
			else
			{
				usleep(10);
				continue;
			}
		}
		else if((int)ulActualBytes == -1)
		{
			bTimeOut = true;
			perror("ERROR: TransportUSB::WriteBytes:  ");
			break;
		}
		else
			break;
	}

	if(bTimeOut == true) 
	{
		return false;
	}
#else	
	DWORD Start = GetTickCount();
	while(!bTimeOut)
	{
		if (!pUSB->WritePipe(hWrite, pData, nElementCount, &ulActualBytes))
		{
			return false;
		} else
		{
			return true;
		}
	}
#endif
	return true;
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
    	pRxBuffer = (unsigned char *)pPacket + nIterations*MAX_DATA_BYTES_EZEXTENDER;

#ifdef _GCC
	int nTOcount = 0;

	while(!bTimeOut)
	{
		nRxSize = read((size_t)hRead, pRxBuffer, nRemainder);
		if(nRxSize == 0)
		{
		
			if(nTOcount++ >= 5000)//5 sec timeout
			{
				bTimeOut = true;
				fprintf(stdout, "\nError TransportUSB::ReadBytes Timed out (%d bytes) \n", nRemainder);
				break;
			}
			else
			{
				usleep(10);
				continue;
			}
		}
		else if(nRxSize == (DWORD)-1)
		{
			bTimeOut = true;
			perror("ERROR: TransportUSB::ReadBytes:  ");
			break;
		}
		else
			break;
	}

#else
    
	DWORD Start = GetTickCount();
	while(!bTimeOut)
	{
		if (!pUSB->ReadPipe(hRead, pRxBuffer, nRemainder, (LPDWORD)&nRxSize))
		{
			if( (GetTickCount() - Start) < mSecTimeout) continue;
			bTimeOut = true;
		} else
		{
			return true;
		}
	}
#endif
	// Check actual received size
    if( (nRxSize != (DWORD)nRemainder) && (bTimeOut == false) )
	 {
	 	printf("nRxSize:%d is not equal to nRemainder:%d",(int)nRxSize,(int)nRemainder);
		return false;
	 }
	if(bTimeOut == true) 
	{
		return false;
	}
	return true;
}

#endif
