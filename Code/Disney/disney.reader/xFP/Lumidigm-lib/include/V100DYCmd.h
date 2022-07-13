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

#include "ICmd.h"
#include "IMemMgr.h"
#include "V100_shared_types.h"
#include "V100_disney_types.h"
#include "string.h"


/* ----------------------- Atomic_Get_Cmd -------------------------*/
class Atomic_Get_LRing_Cmd:  public ICmd
{
public:
	Atomic_Get_LRing_Cmd();
	~Atomic_Get_LRing_Cmd();
// ICmd
     // Takes content of Command, and packs it into pPacket
	virtual bool PackChallenge(uchar** pPacket, uint& nSize);
	// Unpacks packet passed in into interal data structure
	virtual bool UnpackChallenge(const uchar* pPacket, uint nSize);
    // Takes content of Command, and packs it into pPacket
	virtual bool PackResponse(uchar** pPacket, uint& nSize);
	// Unpacks packet passed in into interal data structure
	virtual bool UnpackResponse(const uchar* pPacket, uint nSize);
	// How large is the Challenge packet?
	virtual int  GetChallengeBufferSize();
	// How large is the Response packet?
	virtual int  GetResponseBufferSize();
	//
	V100_LRING_API_TYPE	GetCmd()	  { return m_nICT;};
	bool	SetCmd(V100_LRING_API_TYPE* _ICT) { m_nICT = *_ICT; return true;};
private:
	// SPECIALIZED DATA GOES HERE
	V100_LRING_API_TYPE				m_nICT;
	uint						    m_nSize;

};
/* ----------------------- Atomic_Set_Cmd -------------------------*/
class Atomic_Set_LRing_Cmd :  public ICmd
{
public:
	Atomic_Set_LRing_Cmd();
	~Atomic_Set_LRing_Cmd();
// ICmd
     // Takes content of Command, and packs it into pPacket
	virtual bool PackChallenge(uchar** pPacket, uint& nSize);
	// Unpacks packet passed in into interal data structure
	virtual bool UnpackChallenge(const uchar* pPacket, uint nSize);
    // Takes content of Command, and packs it into pPacket
	virtual bool PackResponse(uchar** pPacket, uint& nSize);
	// Unpacks packet passed in into interal data structure
	virtual bool UnpackResponse(const uchar* pPacket, uint nSize);
	// How large is the Challenge packet?
	virtual int  GetChallengeBufferSize();
	// How large is the Response packet?
	virtual int  GetResponseBufferSize();
	// Set/Get CMD
	V100_LRING_API_TYPE* GetCmd()	 
	{
		return &m_nICT;
	};
	bool	SetCmd(V100_LRING_API_TYPE	_ICT) 
	{
		m_nICT = _ICT;
		m_nSize = sizeof(m_nICT);
		return true;
	};
private:
	// SPECIALIZED DATA GOES HERE
	V100_LRING_API_TYPE				m_nICT;
	uint						    m_nSize;
};
