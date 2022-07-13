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
#include "V100IDCmd.h"
#include "string.h"

// CMD_XXX_XXXX *************************

Macro_ID_Create_DB::Macro_ID_Create_DB()
{
	 m_nCmd = CMD_ID_CREATE_DB;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Macro_ID_Create_DB::~Macro_ID_Create_DB()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Create_DB::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_DB_INIT_STRUCT);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_DbCreateParms, sizeof(_MX00_DB_INIT_STRUCT));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;

	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Create_DB::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_DbCreateParms, pPtr, sizeof(_MX00_DB_INIT_STRUCT));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Create_DB::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Create_DB::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Create_DB::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Create_DB::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

// CMD_XXX_XXXX *************************

Macro_ID_Set_Working_DB::Macro_ID_Set_Working_DB()
{
	 m_nCmd = CMD_ID_SET_WORKING_DB;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 m_nGroupID = 0;
}
Macro_ID_Set_Working_DB::~Macro_ID_Set_Working_DB()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Set_Working_DB::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(m_nGroupID);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_nGroupID, sizeof(m_nGroupID));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;

	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Set_Working_DB::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_nGroupID, pPtr, sizeof(m_nGroupID));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Set_Working_DB::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Set_Working_DB::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Set_Working_DB::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Set_Working_DB::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

// CMD_XXX_XXXX *************************

Atomic_ID_Get_User_Record::Atomic_ID_Get_User_Record()
{
	 m_nCmd = CMD_ID_GET_USER_RECORD;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 m_pTemplateInstances = NULL;
}
Atomic_ID_Get_User_Record::~Atomic_ID_Get_User_Record()
{
	if(m_pTemplateInstances)
	{
		FREE(m_pTemplateInstances);
		m_pTemplateInstances = NULL;
	}
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_User_Record::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_ID_USER_RECORD);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_UserRecordHeader, sizeof(m_UserRecordHeader));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_User_Record::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_UserRecordHeader, pPtr, sizeof(m_UserRecordHeader));
	
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_User_Record::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	m_nResponseBufferSize+=sizeof(_MX00_ID_USER_RECORD);
	m_nResponseBufferSize+=sizeof(_MX00_TEMPLATE_INSTANCE)*m_UserRecordHeader.nInstances;
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_UserRecordHeader, sizeof(m_UserRecordHeader));
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, m_pTemplateInstances, sizeof(_MX00_TEMPLATE_INSTANCE)*m_UserRecordHeader.nInstances);
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_User_Record::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	if(m_pTemplateInstances) 
	{
		FREE(m_pTemplateInstances);
		m_pTemplateInstances = NULL;
	}
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_UserRecordHeader, pPtr, sizeof(m_UserRecordHeader));
        if(NULL == pPtr)
           return false;

	
	// Create Template Instances
	m_pTemplateInstances = (_MX00_TEMPLATE_INSTANCE*)MALLOC(m_UserRecordHeader.nInstances*sizeof(_MX00_TEMPLATE_INSTANCE));
	pPtr = Unpack(m_pTemplateInstances, pPtr, m_UserRecordHeader.nInstances*sizeof(_MX00_TEMPLATE_INSTANCE));
        if(NULL == pPtr)
           return false;


	return true;
}

// How large is the Challenge packet?
int  Atomic_ID_Get_User_Record::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Get_User_Record::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
bool Atomic_ID_Get_User_Record::GetTemplates(_MX00_TEMPLATE_INSTANCE instanceRecords[])
{
	memcpy(instanceRecords, m_pTemplateInstances, sizeof(_MX00_TEMPLATE_INSTANCE)*m_UserRecordHeader.nInstances);
	return true;
}
_MX00_TEMPLATE_INSTANCE* Atomic_ID_Get_User_Record::GetNewTemplateBuffer(uint nNumInstances, uint nSizePerTemplate)
{
	if(m_pTemplateInstances)
	{
		FREE(m_pTemplateInstances);
		m_pTemplateInstances = NULL;
	}
	m_pTemplateInstances = (_MX00_TEMPLATE_INSTANCE*)MALLOC(nSizePerTemplate*nNumInstances);
	return m_pTemplateInstances;
}

void Atomic_ID_Get_User_Record::SetUserRecords(const _MX00_ID_USER_RECORD& rec, const _MX00_TEMPLATE_INSTANCE instanceRecords[])
{
	if(m_pTemplateInstances)
	{
		FREE(m_pTemplateInstances);
		m_pTemplateInstances = NULL;
	}
	m_UserRecordHeader = rec;
	m_pTemplateInstances = (_MX00_TEMPLATE_INSTANCE*)MALLOC(sizeof(_MX00_TEMPLATE_INSTANCE)*m_UserRecordHeader.nInstances);
	_MX00_TEMPLATE_INSTANCE* pHdPtr = m_pTemplateInstances;
	//
	for( uint ii = 0 ; ii < m_UserRecordHeader.nInstances ; ii++)
	{
		memcpy(pHdPtr, &instanceRecords[ii], sizeof(_MX00_TEMPLATE_INSTANCE));
		pHdPtr++;
	}
}

// CMD_XXX_XXXX *************************

Atomic_ID_Get_User_Record_Header::Atomic_ID_Get_User_Record_Header()
{
	 m_nCmd = CMD_ID_GET_USER_RECORD_HEADER;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_ID_Get_User_Record_Header::~Atomic_ID_Get_User_Record_Header()
{
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_User_Record_Header::PackChallenge(uchar** pPacket, uint& nSize)
{
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
    if(NULL == pPtr) return false;
	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_User_Record_Header::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
    if(NULL == pPtr) return false;
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_User_Record_Header::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	m_nResponseBufferSize+=sizeof(_MX00_ID_USER_RECORD);
	
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_UserRecordHeader, sizeof(m_UserRecordHeader));
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_User_Record_Header::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_UserRecordHeader, pPtr, sizeof(m_UserRecordHeader));
        if(NULL == pPtr)
           return false;
	// Create Template Instances
	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Get_User_Record_Header::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Get_User_Record_Header::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
// CMD_XXX_XXXX *************************

Atomic_ID_Set_User_Record::Atomic_ID_Set_User_Record()
{
	 m_nCmd = CMD_ID_SET_USER_RECORD;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 //
	 m_pTemplateInstances = NULL;
}
Atomic_ID_Set_User_Record::~Atomic_ID_Set_User_Record()
{
	if(m_pTemplateInstances)
	{
		FREE(m_pTemplateInstances);
	}
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Set_User_Record::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_ID_USER_RECORD);
	m_nChallengeBufferSize+=sizeof(_MX00_TEMPLATE_INSTANCE)*m_UserRecordHeader.nInstances;
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_UserRecordHeader, sizeof(m_UserRecordHeader));
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, m_pTemplateInstances, sizeof(_MX00_TEMPLATE_INSTANCE)*m_UserRecordHeader.nInstances);
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Set_User_Record::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	if(m_pTemplateInstances) 
	{
		FREE(m_pTemplateInstances);
		m_pTemplateInstances = NULL;
	}
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_UserRecordHeader, pPtr, sizeof(m_UserRecordHeader));
        if(NULL == pPtr)
           return false;

	
	// Create Template Instances
	m_pTemplateInstances = (_MX00_TEMPLATE_INSTANCE*)MALLOC(m_UserRecordHeader.nInstances*sizeof(_MX00_TEMPLATE_INSTANCE));
	pPtr = Unpack(m_pTemplateInstances, pPtr, m_UserRecordHeader.nInstances*sizeof(_MX00_TEMPLATE_INSTANCE));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Set_User_Record::PackResponse(uchar** pPacket, uint& nSize)
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
bool Atomic_ID_Set_User_Record::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Set_User_Record::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Set_User_Record::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
void Atomic_ID_Set_User_Record::SetUserRecords(const _MX00_ID_USER_RECORD& rec, const _MX00_TEMPLATE_INSTANCE instanceRecords[])
{
	if(m_pTemplateInstances)
	{
		FREE(m_pTemplateInstances);
		m_pTemplateInstances = NULL;
	}
	m_UserRecordHeader = rec;
	m_pTemplateInstances = (_MX00_TEMPLATE_INSTANCE*)MALLOC(sizeof(_MX00_TEMPLATE_INSTANCE)*m_UserRecordHeader.nInstances);
	_MX00_TEMPLATE_INSTANCE* pHdPtr = m_pTemplateInstances;
	//
	for( uint ii = 0 ; ii < m_UserRecordHeader.nInstances ; ii++)
	{
		memcpy(pHdPtr, &instanceRecords[ii], sizeof(_MX00_TEMPLATE_INSTANCE));
		pHdPtr++;
	}
}
_MX00_TEMPLATE_INSTANCE* Atomic_ID_Set_User_Record::GetFingerInstance(uint nInstance)
{
	if( nInstance > m_UserRecordHeader.nInstances) return NULL;
	if(m_pTemplateInstances == NULL) return NULL;
	return &m_pTemplateInstances[nInstance];
}


// CMD_XXX_XXXX *************************

Macro_ID_Enroll_User_Record::Macro_ID_Enroll_User_Record()
{
	 m_nCmd = CMD_ID_ENROLL_USER_RECORD;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Macro_ID_Enroll_User_Record::~Macro_ID_Enroll_User_Record()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Enroll_User_Record::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_ID_USER_RECORD);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_nUserRecord, sizeof(_MX00_ID_USER_RECORD));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Enroll_User_Record::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_nUserRecord, pPtr, sizeof(_MX00_ID_USER_RECORD));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Enroll_User_Record::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Enroll_User_Record::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Enroll_User_Record::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Enroll_User_Record::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

// CMD_XXX_XXXX *************************

Macro_ID_Verify_User_Record::Macro_ID_Verify_User_Record()
{
	 m_nCmd = CMD_ID_VERIFY_USER_RECORD;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Macro_ID_Verify_User_Record::~Macro_ID_Verify_User_Record()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Verify_User_Record::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_ID_USER_RECORD);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_UserRecord, sizeof(_MX00_ID_USER_RECORD));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;

	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Verify_User_Record::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_UserRecord, pPtr, sizeof(_MX00_ID_USER_RECORD));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Verify_User_Record::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Verify_User_Record::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Verify_User_Record::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Verify_User_Record::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}


// CMD_XXX_XXXX *************************

Macro_ID_Identify_378::Macro_ID_Identify_378()
{
	 m_nCmd = CMD_ID_IDENTIFY_378;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;

	m_pTemplate	= NULL;
	m_nTemplateSize = 0;
}
Macro_ID_Identify_378::~Macro_ID_Identify_378()
{
	if(m_pTemplate) FREE(m_pTemplate); m_pTemplate = NULL;
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Identify_378::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=m_nTemplateSize;
	m_nChallengeBufferSize+=sizeof(m_nTemplateSize);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_nTemplateSize, sizeof(m_nTemplateSize));
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, m_pTemplate, m_nTemplateSize);
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Identify_378::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_nTemplateSize, pPtr, sizeof(m_nTemplateSize));
        if(NULL == pPtr)
           return false;

	if( m_nTemplateSize > MAX_378_TEMPLATE_SIZE ) return false;
	if( m_pTemplate ) FREE(m_pTemplate);
	m_pTemplate = (uchar*)MALLOC(m_nTemplateSize);
	pPtr = Unpack(m_pTemplate, pPtr, m_nTemplateSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Identify_378::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Identify_378::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Identify_378::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Identify_378::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
bool Macro_ID_Identify_378::SetTemplate(uchar* p378Template, uint nSizeTemplate)
{
	if( m_pTemplate ) FREE(m_pTemplate);
	m_pTemplate = (uchar*)MALLOC(nSizeTemplate);
	memcpy(m_pTemplate, p378Template, nSizeTemplate);
	m_nTemplateSize = nSizeTemplate;
	return true;
}

/****** CMD_ID_IDENTIFY ********/

// CMD_XXX_XXXX *************************

Macro_ID_Identify::Macro_ID_Identify()
{
	 m_nCmd = CMD_ID_IDENTIFY;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Macro_ID_Identify::~Macro_ID_Identify()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Identify::PackChallenge(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Identify::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Identify::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Identify::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Identify::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Identify::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

//

// CMD_XXX_XXXX *************************

Macro_ID_Delete_DB::Macro_ID_Delete_DB()
{
	 m_nCmd = CMD_ID_DELETE_DB;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Macro_ID_Delete_DB::~Macro_ID_Delete_DB()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Delete_DB::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(m_nDBToDelete);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	//
	pPtr = Pack(pPtr, &m_nDBToDelete, sizeof(m_nDBToDelete));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Delete_DB::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_nDBToDelete, pPtr, sizeof(m_nDBToDelete));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Delete_DB::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Delete_DB::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Delete_DB::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Delete_DB::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

// CMD_XXX_XXXX *************************

Atomic_ID_Get_DB_Metrics::Atomic_ID_Get_DB_Metrics()
{
	 m_nCmd = CMD_ID_GET_DB_METRICS;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_ID_Get_DB_Metrics::~Atomic_ID_Get_DB_Metrics()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_DB_Metrics::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_DB_METRICS);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_dbMetrics, sizeof(_MX00_DB_METRICS));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_DB_Metrics::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_dbMetrics, pPtr, sizeof(m_dbMetrics));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_DB_Metrics::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	m_nResponseBufferSize+=sizeof(_MX00_DB_METRICS);
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_dbMetrics, sizeof(m_dbMetrics));
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_DB_Metrics::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_dbMetrics, pPtr, sizeof(_MX00_DB_METRICS));
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Get_DB_Metrics::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Get_DB_Metrics::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

// CMD_XXX_XXXX *************************

Atomic_ID_Get_System_Metrics::Atomic_ID_Get_System_Metrics()
{
	 m_nCmd = CMD_ID_GET_SYSTEM_METRICS;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 m_pDBMetrics = NULL;
}
Atomic_ID_Get_System_Metrics::~Atomic_ID_Get_System_Metrics()
{
	if(m_pDBMetrics) FREE(m_pDBMetrics);
	m_pDBMetrics = NULL;
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_System_Metrics::PackChallenge(uchar** pPacket, uint& nSize)
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
bool Atomic_ID_Get_System_Metrics::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_System_Metrics::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	m_nResponseBufferSize+=sizeof(_MX00_DB_METRICS)*m_nMetricsTracked;
	SetArguement(m_nMetricsTracked);
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	// Pack structures
	for(uint ii = 0; ii < m_nMetricsTracked ; ii++)
	{
		pPtr = Pack(pPtr, &m_pDBMetrics[ii], sizeof(_MX00_DB_METRICS));
                if(NULL == pPtr)
                    return false;

	}
	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_System_Metrics::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	// IMPLEMENT
	m_nMetricsTracked = (uint)m_nArg;
	if(m_pDBMetrics) FREE(m_pDBMetrics);
	m_pDBMetrics = (_MX00_DB_METRICS*)MALLOC(m_nMetricsTracked*sizeof(_MX00_DB_METRICS));
	// Pack structures
	for(uint ii = 0; ii < m_nMetricsTracked ; ii++)
	{
		pPtr = Unpack(&m_pDBMetrics[ii], pPtr, sizeof(_MX00_DB_METRICS));
                if(NULL == pPtr)
                   return false;

	}
	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Get_System_Metrics::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;

}
// How large is the Response packet?
int  Atomic_ID_Get_System_Metrics::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}
void Atomic_ID_Get_System_Metrics::SetNumMetrics(uint nMetrics)
{
	if(m_pDBMetrics)
	{
		FREE(m_pDBMetrics);
		m_pDBMetrics = NULL;
	}
	m_nMetricsTracked = nMetrics;
	if(nMetrics == 0) return;
	m_pDBMetrics = (_MX00_DB_METRICS*)MALLOC(sizeof(_MX00_DB_METRICS)*nMetrics);
	m_nMetricsTracked = nMetrics;
}
void Atomic_ID_Get_System_Metrics::AddMetric(_MX00_DB_METRICS* pMetric, uint nMetricToSet)
{
	if( nMetricToSet > (m_nMetricsTracked-1)) return;
	memcpy(&m_pDBMetrics[nMetricToSet], pMetric, sizeof(_MX00_DB_METRICS));
}

// CMD_XXX_XXXX *************************

Atomic_ID_Get_Result::Atomic_ID_Get_Result()
{
	 m_nCmd = CMD_ID_GET_RESULT;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_ID_Get_Result::~Atomic_ID_Get_Result()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_Result::PackChallenge(uchar** pPacket, uint& nSize)
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
bool Atomic_ID_Get_Result::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_Result::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;

	m_nResponseBufferSize+=sizeof(_MX00_ID_RESULT);
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_nID_Result, sizeof(_MX00_ID_RESULT));
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_Result::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_nID_Result, pPtr, sizeof(_MX00_ID_RESULT));
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Get_Result::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Get_Result::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

Atomic_ID_Delete_User_Record::Atomic_ID_Delete_User_Record()
{
	 m_nCmd = CMD_ID_DELETE_USER_RECORD;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_ID_Delete_User_Record::~Atomic_ID_Delete_User_Record()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Delete_User_Record::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_ID_USER_RECORD);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_UserRecord, sizeof(m_UserRecord));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Delete_User_Record::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_UserRecord, pPtr, sizeof(m_UserRecord));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Delete_User_Record::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Delete_User_Record::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Delete_User_Record::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Delete_User_Record::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

/* Set parameters */

Atomic_ID_Set_Parameters::Atomic_ID_Set_Parameters()
{
	 m_nCmd = CMD_ID_SET_PARAMETERS;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_ID_Set_Parameters::~Atomic_ID_Set_Parameters()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Set_Parameters::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+=sizeof(_MX00_ID_PARAMETERS);
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_Parameters, sizeof(m_Parameters));
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;

	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Set_Parameters::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_Parameters, pPtr, sizeof(m_Parameters));
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Set_Parameters::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Set_Parameters::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Set_Parameters::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Set_Parameters::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

/* Get Parameters */

Atomic_ID_Get_Parameters::Atomic_ID_Get_Parameters()
{
	 m_nCmd = CMD_ID_GET_PARAMETERS;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Atomic_ID_Get_Parameters::~Atomic_ID_Get_Parameters()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_Parameters::PackChallenge(uchar** pPacket, uint& nSize)
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
bool Atomic_ID_Get_Parameters::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Atomic_ID_Get_Parameters::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	// Fill in 
	m_nResponseBufferSize+=sizeof(m_Parameters);
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_Parameters, sizeof(m_Parameters));
        if(NULL == pPtr)
           return false;

	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Atomic_ID_Get_Parameters::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_Parameters, pPtr, sizeof(m_Parameters));
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Atomic_ID_Get_Parameters::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Atomic_ID_Get_Parameters::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}


// CMD_XXX_XXXX *************************

Macro_ID_Verify_378::Macro_ID_Verify_378()
{
	 m_nCmd = CMD_ID_VERIFY_378;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
	 m_pTemplate = NULL;
}
Macro_ID_Verify_378::~Macro_ID_Verify_378()
{
	if(m_pTemplate) FREE(m_pTemplate);
}
// ICmd
void Macro_ID_Verify_378::SetTemplate(uchar* pTpl, uint nTplSize)
{
	if(m_pTemplate) FREE(m_pTemplate);
	m_pTemplate = (uchar*)MALLOC(MAX_TEMPLATE_SIZE);
	if(nTplSize > MAX_TEMPLATE_SIZE) return;
	memcpy(m_pTemplate, pTpl, nTplSize);
	m_nTemplateSize = nTplSize;
	return;
}
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Verify_378::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize+= sizeof(_MX00_ID_USER_RECORD);
	m_nChallengeBufferSize+= sizeof(uint);
	m_nChallengeBufferSize+= m_nTemplateSize;
	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_UserRecord, sizeof(m_UserRecord));
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, &m_nTemplateSize, sizeof(uint));
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, m_pTemplate, m_nTemplateSize);
        if(NULL == pPtr)
           return false;

	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Verify_378::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_UserRecord, pPtr, sizeof(m_UserRecord));
        if(NULL == pPtr)
           return false;

	pPtr = Unpack(&m_nTemplateSize, pPtr, sizeof(uint));
        if(NULL == pPtr)
           return false;

	if(m_pTemplate) FREE(m_pTemplate);
	m_pTemplate = (uchar*)MALLOC(m_nTemplateSize);
	pPtr = Unpack(m_pTemplate, pPtr, m_nTemplateSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Verify_378::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_ID_Verify_378::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Verify_378::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Verify_378::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}


// CMD_XXX_XXXX *************************

Macro_Save_Last_Capture::Macro_Save_Last_Capture()
{
	 m_nCmd = CMD_SAVE_LAST_CAPTURE;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;
}
Macro_Save_Last_Capture::~Macro_Save_Last_Capture()
{

}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_Save_Last_Capture::PackChallenge(uchar** pPacket, uint& nSize)
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
bool Macro_Save_Last_Capture::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
	return (pPtr!=NULL)? true:false;
}
// Takes content of Command, and packs it into pPacket
bool Macro_Save_Last_Capture::PackResponse(uchar** pPacket, uint& nSize)
{
	// Check for error.
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
        if(NULL == pPtr)
           return false;

	
	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return false;
}
// Unpacks packet passed in into interal data structure
bool Macro_Save_Last_Capture::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return false;
}
// How large is the Challenge packet?
int  Macro_Save_Last_Capture::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_Save_Last_Capture::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

// CMD_XXX_XXXX *************************

Macro_Update_Firmware::Macro_Update_Firmware()
{
	 m_nCmd = CMD_UPDATE_FIRMWARE;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;

	m_pFWData		= NULL;
	m_nDataSize		= 0;

}
Macro_Update_Firmware::~Macro_Update_Firmware()
{
	if(m_pFWData) 
	{	
		FREE(m_pFWData);
		m_pFWData = NULL;
	}	
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_Update_Firmware::PackChallenge(uchar** pPacket, uint& nSize)
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
bool Macro_Update_Firmware::UnpackChallenge(const uchar* pPacket, uint nSize)
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
bool Macro_Update_Firmware::PackResponse(uchar** pPacket, uint& nSize)
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
bool Macro_Update_Firmware::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
        if(NULL == pPtr)
           return false;

	return true;
}
// How large is the Challenge packet?
int  Macro_Update_Firmware::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_Update_Firmware::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

bool   Macro_Update_Firmware::SetData(uchar* pData, uint nDataSize)
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
uchar* Macro_Update_Firmware::GetData()
{
	return m_pFWData;
}
uint   Macro_Update_Firmware::GetDataSize()
{
	return m_nDataSize;
}


// CMD_ID_VERIFY_MANY *************************

Macro_ID_Verify_Many::Macro_ID_Verify_Many()
{
	 m_nCmd = CMD_ID_VERIFY_MANY;
	 // Challenge buffer size...
	 m_nChallengeBufferSize = ENVELOPE_INFO_SIZE;
	 m_pChallengeBuffer = NULL;
	 m_nResponseBufferSize = ENVELOPE_INFO_SIZE;
     m_pResponseBuffer = NULL;

	 m_pTemplates = NULL;
	 m_pSzTemplates = NULL;
	 m_nNumTemplates = 0;
	 m_nSzAllTemplates = 0;
}
Macro_ID_Verify_Many::~Macro_ID_Verify_Many()
{

	if(m_pSzTemplates)
	{
		FREE(m_pSzTemplates);
		m_pSzTemplates = NULL;
	}

	if(m_pTemplates) 
	{
		FREE(m_pTemplates);
		m_pTemplates = NULL;
	}

	m_nNumTemplates = 0;
	m_nSzAllTemplates = 0;
}
// ICmd
 // Takes content of Command, and packs it into pPacket
bool Macro_ID_Verify_Many::PackChallenge(uchar** pPacket, uint& nSize)
{
	m_nChallengeBufferSize += sizeof(uint);
	m_nChallengeBufferSize += sizeof(uint)* m_nNumTemplates;
	m_nChallengeBufferSize += m_nSzAllTemplates;

	uchar* pPtr = GenerateChallengeHeader(m_nArg,m_nChallengeBufferSize-ENVELOPE_INFO_SIZE);
	
	pPtr = Pack(pPtr, &m_nNumTemplates, sizeof(unsigned int));
        if(NULL == pPtr)
           return false;

	pPtr = Pack(pPtr, m_pSzTemplates, sizeof(uint)*m_nNumTemplates);
        if(NULL == pPtr)
           return false;
	
	pPtr = Pack(pPtr, m_pTemplates, m_nSzAllTemplates);
    if(NULL == pPtr)
       return false;
	
	// Fill in some stuff
	*pPacket = m_pChallengeBuffer;
	nSize = m_nChallengeBufferSize;
	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Verify_Many::UnpackChallenge(const uchar* pPacket, uint nSize)
{
	if(m_pSzTemplates) 
	{
		FREE(m_pSzTemplates);
	}
	if(m_pTemplates)
	{
		 FREE(m_pTemplates);
		 m_pTemplates = NULL;

	}

	uchar* pPtr = UnpackChallengeHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
		if(NULL == pPtr)
           return false;
	
	pPtr = Unpack(&m_nNumTemplates, pPtr, sizeof(uint));
	 if(NULL == pPtr)
           return false;   
	
	m_pSzTemplates = (uint*)MALLOC(m_nNumTemplates*sizeof(uint));
	pPtr = Unpack(m_pSzTemplates, pPtr, m_nNumTemplates*sizeof(uint));
	if(NULL == pPtr)
           return false; 

	m_nSzAllTemplates = 0;
	for(uint ii=0; ii< m_nNumTemplates; ii++)
	{
		m_nSzAllTemplates += m_pSzTemplates[ii];	
	}

	m_pTemplates = (uchar*)MALLOC(m_nSzAllTemplates);
	pPtr = Unpack(m_pTemplates, pPtr, m_nSzAllTemplates);
	if(NULL == pPtr)
           return false; 

	return true;
}
// Takes content of Command, and packs it into pPacket
bool Macro_ID_Verify_Many::PackResponse(uchar** pPacket, uint& nSize)
{
	if(CheckErrorCode(pPacket,nSize) == false) return true;
	uchar*pPtr = GenerateResponseHeader(0,m_nResponseBufferSize-ENVELOPE_INFO_SIZE); 
    if(NULL == pPtr)
    {
       return false;
    }
	// Response
	*pPacket = m_pResponseBuffer;
	nSize = m_nResponseBufferSize; 
	return true;
}
// Unpacks packet passed in into interal data structure
bool Macro_ID_Verify_Many::UnpackResponse(const uchar* pPacket, uint nSize)
{
	// Remember to allocate memory for composite image, and template, if requested.
	uchar* pPtr = UnpackResponseHeader(pPacket, m_nsohv, m_nArg, m_nOpaqueDataSize);
    if(NULL == pPtr)
    {
       return false;
    }
	return true;
}
// How large is the Challenge packet?
int  Macro_ID_Verify_Many::GetChallengeBufferSize()
{
	return m_nChallengeBufferSize;
}
// How large is the Response packet?
int  Macro_ID_Verify_Many::GetResponseBufferSize()
{
	return m_nResponseBufferSize;
}

bool Macro_ID_Verify_Many::SetTemplates(uchar** pTemplates, uint* pSizeTemplates, uint nNumTemplates)
{
	if(m_pSzTemplates)
	{
		FREE(m_pSzTemplates);
		m_pSzTemplates = NULL;
	}

	if(m_pTemplates)  
	{
		FREE(m_pTemplates);
		m_pTemplates = NULL;
	}

	if(nNumTemplates == 0 || nNumTemplates > 30) return false;

	m_nNumTemplates = nNumTemplates;
	m_pSzTemplates = (uint*)MALLOC(nNumTemplates* sizeof(uint));
	memcpy(m_pSzTemplates, pSizeTemplates, sizeof(uint)*nNumTemplates);

	m_nSzAllTemplates = 0;
	for(uint ii=0; ii< nNumTemplates; ii++)
	{
		if(m_pSzTemplates[ii] > MAX_TEMPLATE_SIZE) return false;
		m_nSzAllTemplates += m_pSzTemplates[ii];	
	}

	m_pTemplates = (uchar*)MALLOC(m_nSzAllTemplates);	
	uchar* pTplsTemp = m_pTemplates;
	for(uint jj=0; jj< nNumTemplates; jj++)
	{
		memcpy(pTplsTemp, pTemplates[jj], m_pSzTemplates[jj]);
		pTplsTemp += m_pSzTemplates[jj];
	}
	return true;
}


