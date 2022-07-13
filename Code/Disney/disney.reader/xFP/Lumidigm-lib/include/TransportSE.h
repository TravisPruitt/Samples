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
#pragma once

#include "ITransport.h"

class NamedPipe;

class TransportSE : public ITransport
{
public:
	TransportSE(void);
	~TransportSE(void);
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
#endif
	NamedPipe* pSE;
private:
	void GetServerName(char* pP);
};
