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

#define MAX_FILE_SIZE (1024*1024)
#define MAX_FILE_NAME_LENGTH 64

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

// CMD_UPDATE_PIC_FIRMWARE *************************

Macro_Update_PIC_Firmware::Macro_Update_PIC_Firmware()
{
	 m_nCmd = _V100_COMMAND_SET(CMD_UPDATE_PIC_FIRMWARE);
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;

	m_pFWData		= NULL;
	m_nDataSize		= 0;

}
Macro_Update_PIC_Firmware::~Macro_Update_PIC_Firmware()
{
	if(m_pFWData) 
	{	
		FREE(m_pFWData);
		m_pFWData = NULL;
	}	
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_Update_PIC_Firmware::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(unsigned int);
	m_nChallengeBufferSize+=m_nDataSize;
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_nDataSize, sizeof(unsigned int));
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, m_pFWData, m_nDataSize);
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;

	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_Update_PIC_Firmware::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_nDataSize, pPtr, sizeof(unsigned int));
        if(NULL == pPtr)
           return false;

	if(m_pFWData) FREE(m_pFWData);
	if(m_nDataSize > MAX_FW_DATA_SIZE ) return false;
	m_pFWData = (uchar*)MALLOC(m_nDataSize);
	pPtr = Unpack(m_pFWData, pPtr, m_nDataSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_Update_PIC_Firmware::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	
	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_Update_PIC_Firmware::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_Update_PIC_Firmware::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_Update_PIC_Firmware::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

bool   Macro_Update_PIC_Firmware::SetData(uchar* pData, uint nDataSize)
{
	if( m_pFWData)
	{
		FREE(m_pFWData);
		m_pFWData = NULL;
	}
	if( nDataSize > MAX_FW_DATA_SIZE ) return false;
	m_pFWData = (uchar*)MALLOC(nDataSize);
	memcpy(m_pFWData, pData, nDataSize);
	m_nDataSize = nDataSize;
	return true;
}
uchar* Macro_Update_PIC_Firmware::GetData()
{
	return m_pFWData;
}
uint   Macro_Update_PIC_Firmware::GetDataSize()
{
	return m_nDataSize;
}

// CMD_SCRIPT_WRITE

Atomic_Script_Write::Atomic_Script_Write()
{
	 m_nCmd = (_V100_COMMAND_SET)CMD_SCRIPT_WRITE;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 // SPEC
	 memset(&m_FileAttr, 0, sizeof(m_FileAttr));
	 m_pFileStream = NULL;
}
Atomic_Script_Write::~Atomic_Script_Write()
{
	if( m_pFileStream != NULL )
	{
		FREE(m_pFileStream);
	}
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_Script_Write::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	m_nChallengeBufferSize += sizeof(m_szFileNameSize);
	m_nChallengeBufferSize += m_szFileNameSize;
	m_nChallengeBufferSize += sizeof(_V100_FILE);
	m_nChallengeBufferSize += m_FileAttr.FileSize;

	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
	// Pack File Attribute
	pPtr = Pack(pPtr, &m_FileAttr, sizeof(_V100_FILE));
	pPtr = Pack(pPtr, &m_szFileNameSize, sizeof(m_szFileNameSize));
	pPtr = Pack(pPtr, m_pFileName, m_szFileNameSize);
	pPtr = Pack(pPtr, m_pFileStream, m_FileAttr.FileSize);
	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Script_Write::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	pPtr = Unpack(&m_FileAttr, pPtr, sizeof(_V100_FILE));
	pPtr = Unpack(&m_szFileNameSize, pPtr, sizeof(m_szFileNameSize));
	pPtr = Unpack(m_pFileName, pPtr, m_szFileNameSize);
	if(m_pFileStream != NULL )
	{
		FREE(m_pFileStream);
	}
	m_pFileStream = (char*)MALLOC(m_FileAttr.FileSize);
	pPtr = Unpack(m_pFileStream, pPtr, m_FileAttr.FileSize);
	
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_Script_Write::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
	uchar* pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE);
	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Script_Write::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	return true;
}
// How large is the Challenge packet?
int  Atomic_Script_Write::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_Script_Write::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
bool Atomic_Script_Write::SetFileStream(char* pPtr, uint nSZ)
{
	if( nSZ > MAX_FILE_SIZE) return false;
	
	if(m_pFileStream)
	{
		FREE(m_pFileStream);
		m_pFileStream = NULL;
	}
	m_pFileStream = (char*)MALLOC(nSZ);
	memcpy(m_pFileStream, pPtr, nSZ);
	m_FileAttr.FileSize = nSZ;
	
	return true;
}
bool Atomic_Script_Write::SetFileName(char* pPtr, uint nSz)
{
	if( nSz > MAX_FILE_NAME_LENGTH ) return false;
	memcpy(m_pFileName, pPtr, nSz);
	m_pFileName[nSz] = '\0';
	m_szFileNameSize = nSz;
	return true;
}


// CMD_SCRIPT_READ

Atomic_Script_Read::Atomic_Script_Read()
{
	 m_nCmd = (_V100_COMMAND_SET)CMD_SCRIPT_READ;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 // SPEC
	 memset(&m_FileAttr, 0, sizeof(m_FileAttr));
	 m_pFileStream = NULL;
}
Atomic_Script_Read::~Atomic_Script_Read()
{
	if( m_pFileStream != NULL )
	{
		FREE(m_pFileStream);
	}
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_Script_Read::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	m_nChallengeBufferSize += sizeof(m_szFileNameSize);
	m_nChallengeBufferSize += m_szFileNameSize;
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
	// Pack File Attribute
	pPtr = Pack(pPtr, &m_szFileNameSize, sizeof(m_szFileNameSize));
	pPtr = Pack(pPtr, m_pFileName, m_szFileNameSize);
	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Script_Read::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	pPtr = Unpack(&m_szFileNameSize, pPtr, sizeof(m_szFileNameSize));
	pPtr = Unpack(m_pFileName, pPtr, m_szFileNameSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_Script_Read::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
	m_nResponseBufferSize +=sizeof(m_FileAttr);
	m_nResponseBufferSize +=m_FileAttr.FileSize;
	
	uchar* pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE);
	pPtr = Pack(pPtr, &m_FileAttr, sizeof(m_FileAttr));
	pPtr = Pack(pPtr, m_pFileStream, m_FileAttr.FileSize);
	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Script_Read::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	pPtr = Unpack(&m_FileAttr, pPtr, sizeof(m_FileAttr));

	if( m_pFileStream == NULL )
	{
		m_pFileStream = (char*)MALLOC(m_FileAttr.FileSize);
	}
	pPtr = Unpack(m_pFileStream, pPtr, m_FileAttr.FileSize);

	return true;
}
// How large is the Challenge packet?
int  Atomic_Script_Read::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_Script_Read::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
bool Atomic_Script_Read::SetFileStream(char* pPtr, uint nSZ)
{
	if( nSZ > MAX_FILE_SIZE) return false;
	
	if(m_pFileStream)
	{
		FREE(m_pFileStream);
		m_pFileStream = NULL;
	}
	// SHALLOW COPY
	m_pFileStream = pPtr;
	m_FileAttr.FileSize = nSZ;
	
	return true;
}
bool Atomic_Script_Read::SetFileName(char* pPtr, uint nSz)
{
	if( nSz > MAX_FILE_NAME_LENGTH ) return false;
	memcpy(m_pFileName, pPtr, nSz);
	m_pFileName[nSz] = '\0';
	m_szFileNameSize = nSz;
	return true;
}

// CMD_SCRIPT_PLAY

Atomic_Script_Play::Atomic_Script_Play()
{
	 m_nCmd = (_V100_COMMAND_SET)CMD_SCRIPT_PLAY;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 // SPEC
	 memset(m_pScriptName, 0, sizeof(m_pScriptName));
	 m_szScriptNameSize = 0;
}
Atomic_Script_Play::~Atomic_Script_Play()
{
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_Script_Play::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	m_nChallengeBufferSize += sizeof(m_szScriptNameSize);
	m_nChallengeBufferSize += m_szScriptNameSize;
	m_nChallengeBufferSize += sizeof(m_brightness);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
	// Pack File Attribute
	pPtr = Pack(pPtr, &m_szScriptNameSize, sizeof(m_szScriptNameSize));
	pPtr = Pack(pPtr, m_pScriptName, m_szScriptNameSize);
	pPtr = Pack(pPtr, &m_brightness, sizeof(m_brightness));
	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Script_Play::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	pPtr = Unpack(&m_szScriptNameSize, pPtr, sizeof(m_szScriptNameSize));
	pPtr = Unpack(m_pScriptName, pPtr, m_szScriptNameSize);
	pPtr = Unpack(&m_brightness, pPtr, sizeof(m_brightness));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_Script_Play::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
	
	uchar* pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE);
	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_Script_Play::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_Script_Play::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_Script_Play::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
bool Atomic_Script_Play::SetScriptName(char* pPtr)
{
	if (!pPtr) return false;
	size_t nSz = strlen(pPtr);
	if( nSz > MAX_FILE_NAME_LENGTH ) return false;
	memcpy(m_pScriptName, pPtr, nSz);
	m_szScriptNameSize = (uint)nSz;
	return true;
}
