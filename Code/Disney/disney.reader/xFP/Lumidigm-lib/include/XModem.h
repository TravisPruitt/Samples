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

#ifdef _GCC
#include "SerialComLinux.h"
#else
#include "SerialCom.h"
#include "stdio.h"
#endif

#define SOH  0x01
#define STX  0x02
#define EOT  0x04
#define ACK  0x06
#define NAK  0x15
#define CAN  0x18
#define CTRLZ 0x1A

#define DLY_1S 10

//#define MAXRETRANS 25

#define MAXRETRANS 2


typedef unsigned char uchar;
typedef unsigned int  uint;
typedef unsigned short ushort;

typedef enum
{
	COM1 = 0,
	COM2,
	COM3,
	COM4,
	COM5,
	COM6,
	COM7,
	COM8,
	COM9,
	COM10
} Comport ;

typedef enum
{
	XD_OK = 0,
	XD_ERR_OPEN_PORT,
	XD_ERR_CONFIGURE_PORT,
	XD_ERR_CANCEL_REMOTE,
	XD_ERR_XMIT,
	XD_ERR_SYNCH,
	XD_ERR_RETRY,
	XD_ERR_NO_RESPONSE
} XRC;

class XModem
{
public:
	// Constructor
	XModem();
	virtual ~XModem();
	XRC		OpenPort(Comport cport, uint baudrate, uint parity, uint timeout);
	XRC		ClosePort();
	XRC		XModemTransmit(uchar* pBuffer, int nBytes);
	XRC		XModemReceive(uchar* pBuffer, int nBytes);

	// Comport
	int check(int crc, const unsigned char *buf, int sz);
	void flushinput(void);
	int _inbyte(int b);
	int _outbyte(unsigned char b);
	int _outbytes(unsigned char* b, unsigned int nSize);
	ushort crc16_ccitt(const uchar *buf, int len);
#ifdef _GCC
	SerialComLinux com;
#endif

#ifdef _WIN32
	SerialCom com;
#endif

};

