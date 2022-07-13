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
#include "TransportRS232.h"
#include "ICmd.h"
#include "XModem.h"
#include "stdio.h"
#ifndef _VCOM_EXAMPLE
#include "Logger.h"
#endif
#ifdef _VCOM_EXAMPLE
#define LOG(X)
#endif


#define MAXBUFFERSIZE					(1024*1024*1)

#ifdef _GCC
#include "gccWindows.h"
#endif

TransportRS232::TransportRS232(void) {
}

TransportRS232::~TransportRS232(void) {
    Close(NULL);
}
uint  TransportRS232::Initialize(V100_DEVICE_TRANSPORT_INFO* pTransport) {
    
    m_pXmodem = new XModem();
    XRC rc = m_pXmodem->OpenPort((Comport)(pTransport->nCOMIndex-1), pTransport->nBaudRate, 0, 1000);
    switch(rc) {
        case XD_ERR_CONFIGURE_PORT:
        case XD_ERR_OPEN_PORT: {
            return 1;
        } break;
        default: {
            return 0;
        } break;
    }
    
    return 0;
}
uint  TransportRS232::Close(V100_DEVICE_TRANSPORT_INFO* pTransport) {
    
    if(m_pXmodem) {
        m_pXmodem->ClosePort();
        delete m_pXmodem;
        m_pXmodem = NULL;
    }
    
#ifndef _GCC
	if(pTransport != NULL)
		CloseHandle(pTransport->hWrite);
#endif
    return 0;
}
bool TransportRS232::GetDeviceId(V100_DEVICE_TRANSPORT_INFO* pTransport, char* szDeviceId, uint& nLength)
{
	nLength = 0;
	return false;
}
// uchar* myPacket:		Packet to send
// uint   nTxSize:	    Size of packet to send
// uchar*
uint TransportRS232::TransmitCommand(V100_DEVICE_TRANSPORT_INFO* pTransport, uchar* myPacket, uint nTxSize, uchar* pResponse, uint& nRxSize) {
    
    int   nakCounter = 0;
    bool  ret = false;
    int   retry = 0;
    int   rc = 0;
    int   x = 0;
	DWORD start = 0;
    
    while(retry < 2) {
        // Flush input...
#ifdef _GCC
        m_pXmodem->flushinput();
#endif
        // Send our NAKs
        m_pXmodem->_outbyte(NAK);
        m_pXmodem->_outbyte(NAK);
// TRANSMIT
        rc = m_pXmodem->XModemTransmit(myPacket, nTxSize);
        //
		if(rc < 128) {
			goto Retry;
        };
        // Wait for Our 2 NAKs.
        nakCounter = 0;
		start = GetTickCount();
		while( nakCounter < 2)
		{
			if( (GetTickCount() - start) > TIMEOUT_VCOM_PROCESSING_MS )
			{
				goto Retry;
			}
			rc = m_pXmodem->_inbyte(0);
			if( rc == NAK ) nakCounter++;
		}

        // Need 2 NAKs to start receiving
        // RECEIVE
        x = m_pXmodem->XModemReceive(pResponse, nRxSize);
        
		if(x > 127) {
            ret = true;
            break;
        } 
Retry:
		m_pXmodem->flushinput();
		retry++;
    }
    
    return ret;
}
