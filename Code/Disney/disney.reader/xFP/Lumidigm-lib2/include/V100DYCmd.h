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

// CMD_UPDATE_PIC_FIRMWARE *************************

class Macro_Update_PIC_Firmware :  public ICmd
{
public:
	Macro_Update_PIC_Firmware();
	~Macro_Update_PIC_Firmware();
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
	bool SetData(uchar* pData, uint nDataSize);
	uchar* GetData();
	uint   GetDataSize();
private:
	// SPECIALIZED DATA GOES HERE
	uchar*  m_pFWData;
	uint	m_nDataSize;
};

/* ----------------------- Atomic_Script_Write  -------------------------*/

class Atomic_Script_Write :  public ICmd
{
public:
	Atomic_Script_Write();
	~Atomic_Script_Write();
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
	// SPEC
	bool		 SetFileAttr(_V100_FILE att) { m_FileAttr = att; return true; }
	_V100_FILE*	 GetFileAttr() { return &m_FileAttr; }
	bool		 SetFileStream(char* pPtr, uint nSZ);
	char*		 GetFileStream(){ return m_pFileStream;}
	uint*		 GetFileStreamSize() { return &m_FileAttr.FileSize;}
	char*		 GetFileName() { m_pFileName[m_szFileNameSize] = '\0'; return m_pFileName; }
	uint		 GetFileNameSize()  { return m_szFileNameSize; };
	bool		 SetFileName(char* pPtr, uint nSz);
private:
	// SPECIALIZED DATA GOES HERE
	_V100_FILE	 m_FileAttr;
	char		 m_pFileName[MAX_FILE_NAME_LENGTH];
	uint		 m_szFileNameSize;
	char*		 m_pFileStream;
};

/* ----------------------- Atomic_Script_Read  -------------------------*/

class Atomic_Script_Read :  public ICmd
{
public:
	Atomic_Script_Read();
	~Atomic_Script_Read();
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
	// SPEC
	bool		 SetFileAttr(_V100_FILE att) { m_FileAttr = att; return true; }
	_V100_FILE*	 GetFileAttr() { return &m_FileAttr; }
	bool		 SetFileStream(char* pPtr, uint nSZ);
	char*		 GetFileStream(){ return m_pFileStream;}
	uint*		 GetFileStreamSize() { return &m_FileAttr.FileSize;}
	char*		 GetFileName() { m_pFileName[m_szFileNameSize] = '\0'; return m_pFileName; }
	uint		 GetFileNameSize()  { return m_szFileNameSize; };
	bool		 SetFileName(char* pPtr, uint nSz);
private:
	// SPECIALIZED DATA GOES HERE
	_V100_FILE	 m_FileAttr;
	char		 m_pFileName[MAX_FILE_NAME_LENGTH];
	uint		 m_szFileNameSize;
	char*		 m_pFileStream;
};

/* ----------------------- Atomic_Script_Play  -------------------------*/

class Atomic_Script_Play :  public ICmd
{
public:
	Atomic_Script_Play();
	~Atomic_Script_Play();
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
	// SPEC
	char*		 GetScriptName() { return m_pScriptName; }
	uint		 GetScriptNameLength()  { return m_szScriptNameSize; };
	bool		 SetScriptName(char* pPtr);

	LED_BRIGHTNESS GetBrightness() { return m_brightness; }
	void           SetBrightness(LED_BRIGHTNESS brightness) 
					{ m_brightness = brightness; }
	
private:
	// SPECIALIZED DATA GOES HERE
	char		 m_pScriptName[MAX_FILE_NAME_LENGTH];
	uint		 m_szScriptNameSize;
	LED_BRIGHTNESS m_brightness;
};

