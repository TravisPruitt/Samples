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
#ifndef _GCC
#include "Windows.h"
#endif
#include "TransportRS232_NX.h"
#include "ICmd.h"
#include "STranRS232.h"
#include "stdio.h"


#define MAXBUFFERSIZE	1024*1024*1

#ifdef _GCC
#include "gccWindows.h"
#endif

TransportRS232_NX ::TransportRS232_NX (void) 
{
}

TransportRS232_NX ::~TransportRS232_NX (void) 
{
    Close(NULL);
}
uint  TransportRS232_NX::Initialize(V100_DEVICE_TRANSPORT_INFO* pTransport) 
{
    
    m_pSTran = new STranRS232();
	if(m_pSTran == NULL) return false;

    if(false == m_pSTran->OpenPort((Comport)(pTransport->nCOMIndex-1), pTransport->nBaudRate&0x0FFFFFFF, 0, 1000))
	{
		return 1;
	}
    return 0;
}
uint  TransportRS232_NX::Close(V100_DEVICE_TRANSPORT_INFO* pTransport) {
    
    if(m_pSTran) {
        m_pSTran->ClosePort();
        delete m_pSTran;
        m_pSTran = NULL;
    }
    
#ifndef _GCC
	if(pTransport != NULL)
		CloseHandle(pTransport->hWrite);
#endif
    return 0;
}
bool TransportRS232_NX::GetDeviceId(V100_DEVICE_TRANSPORT_INFO* pTransport, char* szDeviceId, uint& nLength)
{
	nLength = 0;
	return false;
}
// uchar* myPacket:		Packet to send
// uint   nTxSize:	    Size of packet to send
// uchar*
uint TransportRS232_NX::TransmitCommand(V100_DEVICE_TRANSPORT_INFO* pTransport, uchar* myPacket, uint nTxSize, uchar* pResponse, uint& nRxSize) 
{
	// Little Endian, packet size.
    unsigned int nPacketSize = 0;
    // Transmit that we are sending simple RS-232
    m_pSTran->Transmit(ACK);
    m_pSTran->Transmit(ACK);
	// Transmit packet size.
	nPacketSize = nTxSize;
	if(false == m_pSTran->Transmit((uchar*)&nPacketSize, sizeof(unsigned int)))
	{
		return 0;
	}
	if(nPacketSize == 0 || nPacketSize > MAX_PACKET_SIZE)
	{
		return 0;
	}
    // Transmit packet
	if(false == m_pSTran->Transmit(myPacket, nTxSize))
	{
		return 0;
	}
	nPacketSize = 0;
    
	// Receive packet size - need a 5 second time-out here.
	// This allows the remote device to fullfill the command.
	// All commands must return within 5 seconds to fit VCOM spec,
	// though all protocols.
	DWORD start = GetTickCount();
	nPacketSize = 0;
	// Wait until we have 4 bytes
	int nBytesCaptured = 0;
	uchar bytes[4];
	while(nBytesCaptured < 4)
	{
		if(true == m_pSTran->Receive(bytes+nBytesCaptured, 1))
		{
			nBytesCaptured++;
		}
		if( ( GetTickCount() - start ) > TIMEOUT_VCOM_PROCESSING_MS ) 
		{
			return 0;
		}
	}
	
	memcpy(&nPacketSize, bytes, sizeof(unsigned int));
	//
	// Sanity check
	if(nPacketSize == 0 || nPacketSize > MAX_PACKET_SIZE)
	{
		return 0;
	}
 	// The second receive should be immediately after
	// the first is sent, so standard time-outs will do.
	// 
	if(false == m_pSTran->Receive(pResponse, nPacketSize))
	{
   		return 0;
	}
    return 1;
}
