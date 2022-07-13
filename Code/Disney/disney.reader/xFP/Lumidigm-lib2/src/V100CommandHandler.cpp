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
#include "stdio.h"
#include "V100CommandHandler.h"
#include "V100Cmd.h"
#include "V100IDCmd.h"
#include "V100DYCmd.h"
#include "ITransport.h"
#include "IMemMgr.h"

#ifdef _GCC
#include "gccWindows.h"
#else
#include "windows.h"
#include "Device.h"
#endif

#ifndef _WIN32_WCE
#include "TransportRS232.h"
#include "TransportRS232_NX.h"
#endif
#include "TransportUSB.h"
#ifndef _GCC
#include "TransportSE.h"
#endif
#include <map>


typedef std::pair<HANDLE, V100CommandHandler*> CmdHdlPair;
typedef std::map<HANDLE, V100CommandHandler*> CommandHandlerArray;
CommandHandlerArray cmdArr;

#ifndef _GCC
HANDLE V100CommandHandler::hSynchMutex = CreateMutex(NULL, FALSE, L"CMDHdlMutex");
#endif

V100CommandHandler* V100CommandHandler::GetCommandHandler(const V100_DEVICE_TRANSPORT_INFO* pID)
{
#ifndef _GCC
	WaitForSingleObject(hSynchMutex, INFINITE);
#endif
	// 1.  Search for ID, if it doesnt exist, create a new one, and return it.
	CommandHandlerArray::iterator it = cmdArr.find(pID->hInstance);
	if(it != cmdArr.end())
	{
#ifndef _GCC
		ReleaseMutex(hSynchMutex);
#endif
		return (it->second);
	}
	// If we can't find it, create a new one, and throw it into the DEVICE_TRANSPORT_INFO
	// During call to initialize, we will add it to the command map.  If initialize is never
	// called, we have a memory leak.
	V100CommandHandler* pCmdHandler = new V100CommandHandler();
#ifndef _GCC
	ReleaseMutex(hSynchMutex);
#endif
	return pCmdHandler;
}

V100CommandHandler::V100CommandHandler(void)
{
	

}

V100CommandHandler::~V100CommandHandler(void)
{
		
}
/* Initialize only gets called once. Per Instance. Add myself to the Cmd Queue */

uint  V100CommandHandler::Initialize(V100_DEVICE_TRANSPORT_INFO* pTransport)
{
	ITransport* pTr = NULL;

#ifdef _WIN32_WCE
	
	// WinCE only supports old style USB devices
	pTr = new TransportUSB();

#else

#ifdef _GCC

	// Special for gcc
	if(pTransport->nBaudRate && pTransport->nBaudRate != 0xFF)
	{
		if(pTransport->hRead)
		{
			if(PROTOCOL_VCOM_SIMPLE == (size_t)pTransport->hRead)
			{
				pTr = new TransportRS232_NX();
				pTransport->hRead = NULL;
			}
		}
		else
		{
			pTr = new TransportRS232();
		}
	}
	else
	{
		pTr = new TransportUSB();
	}

#else

	// Check if we need an RS-232 transport connection
	if(pTransport->nBaudRate && pTransport->nBaudRate != 0xFF)
	{
		if(pTransport->hRead)
		{
			if(PROTOCOL_VCOM_SIMPLE == (unsigned int)((__int64)pTransport->hRead&0xFFFFFFFF))
			{
				pTr = new TransportRS232_NX();
				pTransport->hRead = NULL;
			}
		}
		else
		{
			pTr = new TransportRS232();
		}
	}
	else
	{
		Device* dev = reinterpret_cast<Device*>( pTransport->hInstance );

		if(dev)
		{
			switch(dev->GetDeviceType())
			{
			case V100_STREAMING_DEVICE:
				{
					pTr = new TransportSE();
				}break;
			case V100_NORMAL_DEVICE:
				{
					pTr = new TransportUSB();
				}break;
			}
		}
		else
		{	
			pTransport->hInstance = NULL; // clear out the device pointer
			return GEN_ERROR_START;
		}
	}
#endif
#endif
		
	uint rc = 0;
	rc = pTr->Initialize(pTransport);
	if(0 != rc)
	{
		if(pTr)
		{
			delete pTr;
			pTr = NULL;
			pTransport->hInstance = NULL; 
		}
		return rc;
	}

	// Add it to command map.
	pTransport->hInstance = NULL; // clear out the device pointer
	pTransport->hInstance = dynamic_cast<ITransport*>(pTr);
#ifndef _GCC
	WaitForSingleObject(hSynchMutex, INFINITE);
#endif
	cmdArr.insert(CmdHdlPair(pTransport->hInstance, this));
#ifndef _GCC
	ReleaseMutex(hSynchMutex);
#endif
	return 0;
}
void  V100CommandHandler::Close(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	ITransport* pTransportLayer = reinterpret_cast<ITransport*>(pDev->hInstance);
	if(pTransportLayer == NULL) return;
	pTransportLayer->Close(pDev);
	if(pTransportLayer == NULL) return;
	delete pTransportLayer;
	// Remove yourself
	CommandHandlerArray::iterator it = cmdArr.find(pDev->hInstance);
	if(it != cmdArr.end())
	{
		cmdArr.erase(it);
	}

	pDev->hInstance = NULL;
}
ICmd* V100CommandHandler::IssueCommand(V100_DEVICE_TRANSPORT_INFO* pDev, ICmd* pChallenge)
{
	// If you can't do it, return
	if(pChallenge == NULL) return NULL;
	// Transmit it.
	ICmd* pResponse = TransmitCommand(pDev, pChallenge);
	// Delete the Challenge.
	delete pChallenge;
	pChallenge = NULL;
	// Return the response.
	return pResponse;
}
ICmd* V100CommandHandler::GetResponse(V100_DEVICE_TRANSPORT_INFO* pDev, uchar* pResponseBytes, uint nSize)
{
	_V100_COMMAND_SET cmd;
	memcpy(&cmd,&pResponseBytes[2],sizeof(cmd));
	ICmd* pCmd = CreateCommand(cmd);
	if(pCmd == NULL) return NULL;
	pCmd->UnpackResponse(pResponseBytes,nSize);
	return pCmd;
}
	
#define MAXBUFFERSIZE	1024*(1024+1)*2
ICmd* V100CommandHandler::TransmitCommand(V100_DEVICE_TRANSPORT_INFO* pDev, ICmd* pCommand)
{
	uchar* myPacket = NULL;
	uint  nSize = 0;
	uint  nRxBufferSize = MAXBUFFERSIZE;
	ICmd*  pRcv = NULL;
	ITransport* pTransportLayer = reinterpret_cast<ITransport*>(pDev->hInstance);
	if(NULL == pTransportLayer) return false;
	// Pack Challenge
	if(false == pCommand->PackChallenge(&myPacket, nSize)) return false;
	// Allocate space for Response
	uchar* pRxBuffer = (uchar*)MALLOC(nRxBufferSize);
	// Transmit Challenge, Get Response
	if(false == pTransportLayer->TransmitCommand(pDev, myPacket, nSize, pRxBuffer, nRxBufferSize))
	{ 
		FREE(pRxBuffer);
		return NULL;
	}
	// Create Response Cmd
	pRcv = GetResponse(pDev, pRxBuffer, nRxBufferSize);
	// Free Response Buffer
	FREE(pRxBuffer);

	return pRcv;
}
ICmd* V100CommandHandler::CreateCommand(_V100_COMMAND_SET cmdSet)
{
	ICmd* pGenericCommand = NULL;
	switch(cmdSet)
	{
		case CMD_MATCH:							// Match 2 Templates
		{
			pGenericCommand = new Macro_Match();
		} break;
		// ATOMIC
		case CMD_VID_STREAM:					// Enable Video Streaming Mode
		{
			pGenericCommand = new Macro_Vid_Stream();
		} break;
		// ATOMIC COMMANDS
		case CMD_GET_IMAGE:					// Upload Native Image Buffer
		{
			pGenericCommand = new Atomic_Get_Image();
		} break;		
		case CMD_SET_IMAGE:			// Download Native Image Buffer
		{
			pGenericCommand = new Atomic_Set_Image();
		} break;
		case CMD_GET_COMPOSITE_IMAGE:		// Upload Composite Image Buffer
		{
			pGenericCommand = new Atomic_Get_Composite_Image();
		} break;
		case CMD_SET_COMPOSITE_IMAGE:		// Download Composite Image to Buffer
		{
			pGenericCommand = new Atomic_Set_Composite_Image();
		} break;
		case CMD_GET_FIR_IMAGE:					// Upload Composite Image Buffer in FIR format
		{
			pGenericCommand = new Atomic_Get_FIR_Image();
		} break;
		case CMD_GET_TEMPLATE:				// Device->Host Template Xfer
		{
			pGenericCommand = new Atomic_Get_Template();
		} break;
		case CMD_SET_TEMPLATE:				// Host->Device Template Xfer
		{
			pGenericCommand = new Atomic_Set_Template();
		} break;
		case CMD_ARM_TRIGGER:				// Trigger Acquisition
		{
			pGenericCommand = new Atomic_Arm_Trigger();
		} break;
		case CMD_GET_ACQ_STATUS:				// ACQ Status
		{
			pGenericCommand = new Atomic_Get_Acq_Status();
		} break;
		case CMD_GET_CONFIG:					// get Configuration structure
		{
			pGenericCommand = new Atomic_Get_Config();
		} break;
		case CMD_GET_STATUS:					// get Status Structure
		{
			pGenericCommand = new Atomic_Get_Status();
		} break;
		case CMD_GET_CMD:					// get Command Struct
		{
			pGenericCommand = new Atomic_Get_Cmd();
		} break;
		case CMD_SET_CMD:					// set Command Struct
		{
			pGenericCommand = new Atomic_Set_Cmd();
		} break;
		case CMD_SET_LED:					// Diagnostic Turn on LED by Number
		{
			pGenericCommand = new Atomic_Set_LED();
		} break;
		case CMD_CONFIG_COMPORT:
		{
			pGenericCommand = new Atomic_Config_Comport();
		} break;
		case CMD_PROCESS:
		{
			pGenericCommand = new Atomic_Process();
		} break;
		case CMD_RESET:						// Reset Processor
		{
			pGenericCommand = new Atomic_Reset();
		} break;
		case CMD_ERROR:
		{
			pGenericCommand = new Atomic_Error();
		} break;
		case CMD_SET_OPTION:
		{
			pGenericCommand = new Atomic_Set_Option();
		} break;
		case CMD_MATCH_EX:
		{
			pGenericCommand = new Atomic_Match_Ex();
		} break;
		case CMD_SPOOF_GET_TEMPLATE:
		{
			pGenericCommand = new Atomic_Spoof_Get_Template();
		} break;
		case CMD_SET_VERIFICATION_RULES:
		{
			pGenericCommand = new Atomic_Set_Verification_Rules();
		} break;
		case CMD_GET_VERIFICATION_RULES:
		{
			pGenericCommand = new Atomic_Get_Verification_Rules();
		} break;
		case CMD_ENROLL_USER:
		{
			pGenericCommand = new Macro_Enroll_User();
		} break;
		case CMD_VERIFY_USER:
		{
			pGenericCommand = new Macro_Verify_User();
		} break;
		case CMD_DELETE_USER:
		{
			pGenericCommand = new Atomic_Delete_User();
		} break;
		case CMD_GET_DB_METRICS:
		{
			pGenericCommand = new Atomic_Get_DB_Metrics();
		} break;
		case CMD_FORMAT_DB:
		{
			pGenericCommand = new Atomic_Format_DB();
		} break;
		case CMD_GET_USER:
		{
			pGenericCommand = new Atomic_Get_User();
		} break;
		case CMD_ADD_USER:
		{
			pGenericCommand = new Atomic_Add_User();
		} break;		
		case CMD_GET_OP_STATUS:
		{
			pGenericCommand = new Atomic_Get_OP_Status();
		} break;
		case CMD_GET_TAG:
		{
			pGenericCommand = new Atomic_Get_Tag();
		} break;
		case CMD_SET_TAG:
		{
			pGenericCommand = new Atomic_Set_Tag();
		} break;
		case CMD_TRUNCATE_378:
		{
			pGenericCommand = new Atomic_Truncate_378();
		} break;	
		case CMD_SAVE_LAST_CAPTURE:
		{
			pGenericCommand = new Macro_Save_Last_Capture();
		} break;
		case CMD_UPDATE_FIRMWARE:
		{
			pGenericCommand = new Macro_Update_Firmware();
		} break;
		case CMD_UPDATE_PIC_FIRMWARE:
		{
			pGenericCommand = new Macro_Update_PIC_Firmware();
		} break;
		case CMD_SCRIPT_WRITE:
		{
			pGenericCommand = new Atomic_Script_Write();
		} break;
		case CMD_SCRIPT_READ:
		{
			pGenericCommand = new Atomic_Script_Read();
		} break;
		case CMD_SCRIPT_PLAY:
		{
			pGenericCommand = new Atomic_Script_Play();
		} break;
		case CMD_SET_GPIO:
		{
			pGenericCommand = new Atomic_Set_GPIO();
		} break;
		case CMD_GET_GPIO:
		{
			pGenericCommand = new Atomic_Get_GPIO();
		} break;
		case CMD_CANCEL_OPERATION:
		{
			pGenericCommand = new Atomic_Cancel_Operation();
		} break;
		case CMD_ID_CREATE_DB:
		{
			pGenericCommand = new Macro_ID_Create_DB();
		} break;
		case CMD_ID_SET_WORKING_DB:		
		{
			pGenericCommand = new Macro_ID_Set_Working_DB();
		} break;
		case CMD_ID_GET_USER_RECORD:		
		{
			pGenericCommand = new Atomic_ID_Get_User_Record();
		} break;
		case CMD_ID_GET_USER_RECORD_HEADER:
		{
			pGenericCommand = new Atomic_ID_Get_User_Record_Header();
		} break;
		case CMD_ID_SET_USER_RECORD:		
		{
			pGenericCommand = new Atomic_ID_Set_User_Record();
		} break;
		case CMD_ID_ENROLL_USER_RECORD:	
		{
			pGenericCommand = new Macro_ID_Enroll_User_Record();
		} break;
		case CMD_ID_VERIFY_378:
		{
			pGenericCommand = new Macro_ID_Verify_378();
		} break;
		case CMD_ID_VERIFY_USER_RECORD:
		{
			pGenericCommand = new Macro_ID_Verify_User_Record();
		} break;
		case CMD_ID_IDENTIFY_378:		
		{
			pGenericCommand = new Macro_ID_Identify_378();
		} break;
		case CMD_ID_IDENTIFY:
		{
			pGenericCommand = new Macro_ID_Identify();
		} break;
		case CMD_ID_DELETE_DB:			
		{
			pGenericCommand = new Macro_ID_Delete_DB();
		} break;
		case CMD_ID_GET_DB_METRICS:	
		{
			pGenericCommand = new Atomic_ID_Get_DB_Metrics();
		} break;
		case CMD_ID_GET_SYSTEM_METRICS:
		{
			pGenericCommand = new Atomic_ID_Get_System_Metrics();
		} break;
		case CMD_ID_GET_RESULT:			
		{
			pGenericCommand = new Atomic_ID_Get_Result();
		} break;
		case CMD_ID_DELETE_USER_RECORD:
		{
			pGenericCommand = new Atomic_ID_Delete_User_Record();
		} break;
		case CMD_ID_SET_PARAMETERS:
		{
			pGenericCommand = new Atomic_ID_Set_Parameters();
		} break;
		case CMD_ID_GET_PARAMETERS:
		{
			pGenericCommand = new Atomic_ID_Get_Parameters();
		} break;
		case CMD_ID_VERIFY_MANY:
		{
			pGenericCommand = new Macro_ID_Verify_Many();
		}break;
		case CMD_FILE_DELETE:			
		{
			pGenericCommand = new Atomic_File_Delete();
		} break;
		case CMD_GET_LRING_CMD:
		{
			pGenericCommand = new Atomic_Get_LRing_Cmd();
		} break;
		case CMD_SET_LRING_CMD:
		{
			pGenericCommand = new Atomic_Set_LRing_Cmd();
		} break;

        default:
          break;			
	}
	return pGenericCommand;
}








