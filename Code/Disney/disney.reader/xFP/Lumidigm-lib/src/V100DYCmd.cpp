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
#include "V100Cmd.h"
#include "V100DYCmd.h"
#include "string.h"

Atomic_Get_LRing_Cmd::Atomic_Get_LRing_Cmd()
{
	 m_nCmd = _V100_COMMAND_SET(CMD_GET_LRING_CMD);
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_Get_LRing_Cmd::~Atomic_Get_LRing_Cmd()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_Get_LRing_Cmd::PackChallenge(uchar** pPacket, uint& nSize)
{
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	
	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Get_LRing_Cmd::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_Get_LRing_Cmd::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	m_nResponseBufferSize = 0;
	m_nResponseBufferSize += ENVELOPE_INFO_SIZE;					// Static Envelope size
	// Size field
	m_nSize = sizeof(m_nICT);
	//
	m_nResponseBufferSize += sizeof(m_nSize);		
	// The Structure
	m_nResponseBufferSize += sizeof(m_nICT);		
	//
	uchar* pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	// Pack size of structure
	pPtr = Pack(pPtr, &m_nSize, sizeof(m_nSize));
        if(NULL == pPtr)
           return false;

	// Pack Structure
	pPtr = Pack(pPtr, &m_nICT, m_nSize);
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Get_LRing_Cmd::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	// Unpack size
	pPtr = Unpack(&m_nSize, pPtr, sizeof(m_nSize));
        if(NULL == pPtr)
           return false;

	if( m_nSize != sizeof(m_nICT) ) return false;
	pPtr = Unpack(&m_nICT, pPtr, m_nSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_Get_LRing_Cmd::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_Get_LRing_Cmd::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
Atomic_Set_LRing_Cmd::Atomic_Set_LRing_Cmd()
{
	 m_nCmd = _V100_COMMAND_SET(CMD_SET_LRING_CMD);
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_nChallengeBufferSize += sizeof(m_nSize);
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_Set_LRing_Cmd::~Atomic_Set_LRing_Cmd()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_Set_LRing_Cmd::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize += m_nSize;
	uchar* pPtr= GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	// Pack Size
	pPtr = Pack(pPtr, &m_nSize, sizeof(m_nSize));
        if(NULL == pPtr)
           return false;

	// Pack Structure
	pPtr = Pack(pPtr, &m_nICT, m_nSize);
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Set_LRing_Cmd::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	// Unpack Size
	pPtr = Unpack(&m_nSize, pPtr, sizeof(m_nSize));
        if(NULL == pPtr)
           return false;

	// Unpack Structure
	pPtr = Unpack(&m_nICT, pPtr, m_nSize);
	//
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_Set_LRing_Cmd::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	uchar* pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Set_LRing_Cmd::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_Set_LRing_Cmd::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_Set_LRing_Cmd::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
