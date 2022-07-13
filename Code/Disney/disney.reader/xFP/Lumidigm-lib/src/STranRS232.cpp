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
#include "STranRS232.h"
#ifdef _GCC
#include "SerialComLinux.h"
#else
#include "SerialCom.h"
#include "stdio.h"
#endif
#include "stdlib.h"
#include "string.h"


STranRS232::STranRS232()
{

}
STranRS232::~STranRS232()
{

}



#ifdef _GCC
#define FALSE       0
#define NOPARITY    0
#define ONESTOPBIT  0
#endif

 
bool STranRS232::OpenPort(Comport cport, uint baudrate, uint parity, uint timeout)
{
#ifdef _WIN32
	sprintf_s(comStrBuffer, 255, "COM%d", (int(cport)+1));
#endif
#ifdef _GCC
	sprintf(comStrBuffer,"/dev/ttyS%d", (int)(cport));
#endif

	if(false == com.OpenPort(comStrBuffer)) return false;
	if(false == com.ConfigureCOMPort(baudrate,8,FALSE,NOPARITY,ONESTOPBIT)) return false;
	if(false == com.SetCommunicationTimeouts(100, 100, 100, 100, 100)) return false;
 
	return true;
}
bool STranRS232::ClosePort()
{
    com.ClosePort();
    return true;
}
bool STranRS232::Transmit(uchar pBuffer)
{
	return (FALSE != com.WriteByte(pBuffer))?true:false;
}
bool STranRS232::Transmit(unsigned char *src, int srcsz)
{
	return (FALSE != com.WriteBytes(src,srcsz))?true:false;
}
bool STranRS232::Receive(unsigned char *dest, int destsz)
{
	return (FALSE != com.ReadBytes(dest,destsz))?true:false;
}

