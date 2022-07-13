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
#pragma once

#include "ITransport.h"


#ifdef __SUPPORT_WIN32_USB__
typedef enum
{
	USB_OK = 0,
	USB_NO_DEVICES_FOUND,
	USB_INVALID_DEVICE,
	USB_INVALID_REPLY,
	USB_CONFIG_ERROR,
	USB_READ_PIPE_ERROR,
	USB_WRITE_PIPE_ERROR,
	USB_GENERAL_ERROR,
	USB_INVALID_INPUT_ARG,
	USB_ADDRESS_QUERY,
	USB_CRC_ERROR
} USB_ERROR_CODE;

class USBDriverInterface;

class TransportUSB : public ITransport
{
public:
	TransportUSB(void);
	~TransportUSB(void);
	// ITransport
	uint Initialize(V100_DEVICE_TRANSPORT_INFO* pTransport);
	uint Close(V100_DEVICE_TRANSPORT_INFO* pTransport);
	uint TransmitCommand(V100_DEVICE_TRANSPORT_INFO* pTransport, uchar* myPacket, uint nTxSize, uchar* pResponse, uint& nRxSize);
	bool GetDeviceId(V100_DEVICE_TRANSPORT_INFO* pTransport, char* szDeviceId, uint& nLength);
#ifdef _WIN32
	HANDLE GetMutexId();
#endif
private:
	bool WriteBytes(HANDLE hWriteHandle, void* pPacket, uint nPacketSize, uint mSecTimeout);
	bool ReadBytes(HANDLE hReadHandle, void* pPacket, uint nPacketSize, uint mSecTimeout);
	// Mutex Handle
	static const int MutexTimeout = 10000;
	char hPhysicalDeviceID[2048];
#ifdef _WIN32
	HANDLE				hMutexID;		//Mutex
	USBDriverInterface* pUSB;
#endif
	int handleToClose;
};
#endif
