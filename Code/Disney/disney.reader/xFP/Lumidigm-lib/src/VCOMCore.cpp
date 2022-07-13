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
#include "V100CommandHandler.h"
#include "VCOMCore.h"
#include "V100Cmd.h"
#include "V100IDCmd.h"
#include "V100DYCmd.h"
#ifdef _GCC
#include "gccWindows.h"
#else
#include "windows.h"
#include "V100DeviceHandler.h"
#endif
#include "IMemMgr.h"
#define PERIOD 1

#if __SUPPORT_WIN32_USB__
#ifndef _GCC
#ifndef _WIN32_WCE
//#include "setupapi.h"
//#include "usbdriver.h"
#endif
EXTERN_C const GUID GUID_BULKLDI;
#endif


#include <iostream>

V100_ERROR_CODE V100_Get_Num_USB_Devices(int* nNumDevices)
{
#ifdef _GCC
	*nNumDevices = 1;
#elif _WIN32_WCE
	*nNumDevices = 1;
#else
	V100_ERROR_CODE rc = V100DeviceHandler::GetV100DeviceHandler()->GetNumDevices(nNumDevices);
	
	V100DeviceHandler::ReleaseV100DeviceHandler();
	return rc;
#endif
	
	return GEN_OK;
}
#endif

V100_ERROR_CODE V100_Open( V100_DEVICE_TRANSPORT_INFO *pDev)
{
	V100_ERROR_CODE rc = GEN_OK;

#ifdef _WIN32
#ifndef _WIN32_WCE

	Device* pDevice = new Device();

	rc = V100DeviceHandler::GetV100DeviceHandler()->GetDevice(pDev->DeviceNumber, pDevice);
	if(GEN_OK != rc && pDev->nBaudRate == 0) 
	{
		delete pDevice;
		pDevice = NULL;
		pDev->hInstance = NULL;
		return rc;
	}

	// The Device object is now the hInstance
	pDev->hInstance = pDevice;

#endif
#endif
 
	rc = GEN_OK;
	V100CommandHandler* pVCH = V100CommandHandler::GetCommandHandler(pDev);
	if(0 != pVCH->Initialize(pDev))
	{
		pVCH->Close(pDev);
		delete pVCH;
		rc = GEN_ERROR_START;
	}
	
#ifdef _WIN32
#ifndef _WIN32_WCE
	
	delete pDevice;
	pDevice = NULL;
	
	V100DeviceHandler::ReleaseV100DeviceHandler();

#endif
#endif

	return rc;
}
V100_ERROR_CODE V100_Close(V100_DEVICE_TRANSPORT_INFO *pDev)
{	
	V100CommandHandler* pVCH = V100CommandHandler::GetCommandHandler(pDev);
	pVCH->Close(pDev);
	delete pVCH;
	return GEN_OK;
}
// Capture
V100_ERROR_CODE V100_Capture(V100_DEVICE_TRANSPORT_INFO *pDev,
							  uchar* pCompositeImage,
							  uint&	 nWidth,
							  uint&  nHeight,
							  uchar* pTemplate,
							  uint&	 nTemplateSize,
							  int&	 nSpoof,
							  int	 getComposite,
							  int	 getTemplate)
{
	nSpoof = -1;
	bool bTimeout = false;
	_V100_ACQ_STATUS_TYPE Acq_Status;
	uint nImageSize;
	// Set Trigger
	if( GEN_OK != V100_Arm_Trigger(pDev,TRIGGER_ON)) return GEN_ERROR_INTERNAL;
	// Poll for completion
	//DWORD start = GetTickCount();
	while(bTimeout == false)
	{
		if( GEN_OK != V100_Get_Acq_Status(pDev, &Acq_Status)) 
		{
			return GEN_ERROR_INTERNAL;
		}
		switch(Acq_Status)
		{
		case ACQ_BUSY:		 
		case ACQ_PROCESSING: 
		case ACQ_NOOP: 
		case ACQ_MOVE_FINGER_UP:  
		case ACQ_MOVE_FINGER_DOWN:
		case ACQ_MOVE_FINGER_LEFT:
		case ACQ_MOVE_FINGER_RIGHT:
		case ACQ_FINGER_POSITION_OK:
		case ACQ_NO_FINGER_PRESENT:
		case ACQ_FINGER_PRESENT:
		{ 
			#ifdef _GCC
			usleep(PERIOD*1000);
			#else
			Sleep(PERIOD);
			#endif 
			continue; 
		} break;

		case ACQ_TIMEOUT:	 { bTimeout = true; continue; } break;
		case ACQ_CANCELLED_BY_USER: { bTimeout = true; break;}
		case ACQ_DONE:		 { bTimeout = true; break;}
		case ACQ_LATENT_DETECTED: { bTimeout = true; break;}
		}
	}
	//DWORD end = GetTickCount()-start;

	if(Acq_Status == ACQ_TIMEOUT)
	{
		_V100_INTERFACE_STATUS_TYPE pIST;
		if(GEN_OK != V100_Get_Status(pDev, &pIST)) return GEN_ERROR_INTERNAL;				
		if(pIST.Latent_Detected == 1)
		{
			return GEN_ERROR_TIMEOUT_LATENT;
		}
		else
		{
			return GEN_ERROR_TIMEOUT;
		}		
	}
	if(Acq_Status == ACQ_CANCELLED_BY_USER)
	{
		return GEN_OK;
	}
	if(Acq_Status == ACQ_LATENT_DETECTED)
	{
		return GEN_ERROR_TIMEOUT_LATENT;
	}
	if(Acq_Status != ACQ_DONE)
	{
		return GEN_ERROR_TIMEOUT;
	}
	// Get Composite Image, Spoof
	if(getComposite)
	{
		if(GEN_OK != V100_Get_Composite_Image(pDev, pCompositeImage, &nSpoof, &nImageSize)) 
		{
			return GEN_ERROR_PROCESSING;
		}
	}
	// Get Template
	if(getTemplate)
	{
		if(GEN_OK != V100_Get_Template(pDev, pTemplate, &nTemplateSize))
		{
			return GEN_ERROR_PROCESSING;
		}
	}
	// Retrieve Spoof Score
	_V100_DB_METRICS  dbMetrics;
	if(GEN_OK != V100_Get_DB_Metrics(pDev, &dbMetrics))
	{
		return GEN_ERROR_PROCESSING;
	}
	nSpoof = dbMetrics.nLastSpoofScore;
	//
	return GEN_OK;
}
V100_ERROR_CODE V100_Verify(V100_DEVICE_TRANSPORT_INFO *pDev, uchar* pTemplate, uint nTemplateSize, int& Spoof, uint& MatchScore)
{
	bool bTimeout = false;
	_V100_ACQ_STATUS_TYPE Acq_Status;
	uchar	pProbeTemplate[5000];
	uint	nProbeTplSize = 0;
	Spoof = -1;
	MatchScore = 0;
	// Set Trigger
	if( GEN_OK != V100_Arm_Trigger(pDev, TRIGGER_ON)) return GEN_ERROR_INTERNAL;
	// Poll for completion
	while(bTimeout == false)
	{
		if( GEN_OK != V100_Get_Acq_Status(pDev, &Acq_Status)) return GEN_ERROR_INTERNAL;
		switch(Acq_Status)
		{
		case ACQ_BUSY:       
		case ACQ_NOOP:       
		case ACQ_PROCESSING:
		case ACQ_MOVE_FINGER_UP:
		case ACQ_MOVE_FINGER_DOWN:
		case ACQ_MOVE_FINGER_LEFT:
		case ACQ_MOVE_FINGER_RIGHT:
		case ACQ_FINGER_POSITION_OK:
		case ACQ_NO_FINGER_PRESENT:
		case ACQ_FINGER_PRESENT:
			{ 
				#ifdef _GCC
				usleep(PERIOD*1000);
				#else
				Sleep(PERIOD);
				#endif 
				continue; 
			} break;
		case ACQ_TIMEOUT:    
			{
				bTimeout = true; 
				_V100_INTERFACE_STATUS_TYPE pIST;
				if(GEN_OK != V100_Get_Status(pDev, &pIST)) return GEN_ERROR_INTERNAL;				
				if(pIST.Latent_Detected == 1)
				{
					return GEN_ERROR_TIMEOUT_LATENT;
				}
				else
				{
					return GEN_ERROR_TIMEOUT;
				}					
			} break;
		case ACQ_CANCELLED_BY_USER:
			{
				bTimeout = true;
				return GEN_OK;
			}
		case ACQ_LATENT_DETECTED:
		case ACQ_DONE:       
			{ 
				bTimeout = true;
				break;
			}
		}
	}
	if(Acq_Status == ACQ_LATENT_DETECTED)
	{
		return GEN_ERROR_TIMEOUT_LATENT;
	}
	//
	if(GEN_OK != V100_Get_Template(pDev,pProbeTemplate,&nProbeTplSize)) return GEN_ERROR_PROCESSING;
	// Retrieve Spoof Score
	_V100_DB_METRICS  dbMetrics;
	// DB Metrics contain last spoof score.
	if(GEN_OK != V100_Get_DB_Metrics(pDev, &dbMetrics))
	{
		return GEN_ERROR_PROCESSING;
	}
	Spoof = dbMetrics.nLastSpoofScore;
	//
	if(GEN_OK != V100_Match(pDev, pProbeTemplate, nProbeTplSize, pTemplate, nTemplateSize, MatchScore))
	{
		return GEN_ERROR_MATCH; 
	}
	return GEN_OK;
}
// Match two templates
VCOM_CORE_EXPORT V100_ERROR_CODE V100_Match(V100_DEVICE_TRANSPORT_INFO *pDev,
											  uchar* pProbeTemplate,
											  uint	 nProbeTemplateSize,
											  uchar* pGalleryTemplate,
											  uint	 nGalleryTemplateSize,
											  uint&	 MatchScore)
{
	
	Macro_Match* pCommand  = reinterpret_cast<Macro_Match*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_MATCH));
	pCommand->SetProbeTemplateData(pProbeTemplate, nProbeTemplateSize);
	pCommand->SetGalleryTemplateData(pGalleryTemplate, nGalleryTemplateSize);
	ICmd* pResponse = reinterpret_cast<Macro_Match*>(V100CommandHandler::GetCommandHandler(pDev)->IssueCommand(pDev, pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;	
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)	// Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)arg;
	}
	pCommand = reinterpret_cast<Macro_Match*>(pResponse);
	MatchScore = pCommand->GetMatchScore();
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Arm_Trigger(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_TRIGGER_MODE mode)
{
	Atomic_Arm_Trigger* pCommand = reinterpret_cast<Atomic_Arm_Trigger*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_ARM_TRIGGER));
	// Set mode
	pCommand->SetTriggerType(mode);
	// Issue Command, Get Response
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)pDev,pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	// Is it an Error packet, or...
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)	// Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)arg;
	}
	// Genuine
	pCommand = dynamic_cast<Atomic_Arm_Trigger*>(pResponse);
	delete pCommand;
	return GEN_OK;
}
V100_ERROR_CODE V100_Get_Acq_Status(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_ACQ_STATUS_TYPE* pACQ_Status)
{
	
	Atomic_Get_Acq_Status* pCommand = reinterpret_cast<Atomic_Get_Acq_Status*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_ACQ_STATUS));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_Acq_Status*>(pResponse);
	*pACQ_Status = pCommand->GetAcqStatus();
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Vid_Stream(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_VID_STREAM_MODE mode)
{
	
	Macro_Vid_Stream* pCommand = reinterpret_cast<Macro_Vid_Stream*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_VID_STREAM));
	pCommand->SetVidStreamMode(mode);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_Vid_Stream*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Get_Image(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_IMAGE_TYPE type, uchar* pImage, uint& nImageSize)
{
	
	uchar* _pImage = NULL;
	uint   _nImageSize = 0;

	Atomic_Get_Image* pCommand = reinterpret_cast<Atomic_Get_Image*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_IMAGE));
	// What type of image do we want?
	pCommand->SetImageType(type);
	// Run it through
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	// If pResponse is NULL, there was a transport issue.
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	// Try a cast to error.
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg;
		pErr->GetArguement(arg);
		delete pErr;
		return (V100_ERROR_CODE)(arg);
	}
	// Cast to response type.
	pCommand = dynamic_cast<Atomic_Get_Image*>(pResponse);
	// Get Image Metrics
	pCommand->GetImageMetrics(&_pImage, _nImageSize);
	// Copy
	memcpy(pImage,_pImage, _nImageSize);
	nImageSize = _nImageSize;
	// Delete Command container.
	delete pCommand;
	pCommand = NULL;

	
	return GEN_OK;
}
V100_ERROR_CODE V100_Get_Composite_Image(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar* pImage, int* SpoofValue, uint* nImageSize)
{
	
	Atomic_Get_Composite_Image* pCommand = reinterpret_cast<Atomic_Get_Composite_Image*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_COMPOSITE_IMAGE));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	// Marshal Data.
	pCommand = dynamic_cast<Atomic_Get_Composite_Image*>(pResponse);
	memcpy(pImage,pCommand->GetImage(), pCommand->GetImageSize());
	*SpoofValue = pCommand->GetSpoofValue();
	*nImageSize = pCommand->GetImageSize();
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Set_Composite_Image(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar* pImage, uint nImageSize)
{
	Atomic_Set_Composite_Image* pCommand = reinterpret_cast<Atomic_Set_Composite_Image*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_SET_COMPOSITE_IMAGE));
	pCommand->SetImage(pImage);
	pCommand->SetImageSize(nImageSize);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_Composite_Image*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}
VCOM_CORE_EXPORT V100_ERROR_CODE V100_Get_FIR_Image(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_FIR_RECORD_TYPE FIRType, _V100_FINGER_PALM_POSITION FingerType, uchar* pFIRImage, uint* nFIRImageSize)
{
	Atomic_Get_FIR_Image* pCommand  = reinterpret_cast<Atomic_Get_FIR_Image*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_FIR_IMAGE));
	pCommand->SetFingerType(FingerType);
	pCommand->SetFIRType(FIRType);
	ICmd* pResponse = reinterpret_cast<Atomic_Get_FIR_Image*>(V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)pDev, pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;	
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)	// Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)arg;
	}
	pCommand = reinterpret_cast<Atomic_Get_FIR_Image*>(pResponse);

	if((*nFIRImageSize)  != pCommand->GetFIRImageSize())
	{
		return GEN_ERROR_PARAMETER;
	}
	
	*nFIRImageSize = pCommand->GetFIRImageSize();
	memcpy(pFIRImage, pCommand->GetFIRImage(), (*nFIRImageSize));	

	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Get_Template(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar* pTemplate, uint* nTemplateSize)
{
	
	Atomic_Get_Template* pCommand = reinterpret_cast<Atomic_Get_Template*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_TEMPLATE));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_Template*>(pResponse);
	// Marshal data
	uchar* pResultTemplate = NULL;
	uint   nResultTemplateSize = 0;
	// Grab template from container
	pResultTemplate = pCommand->GetTemplate(nResultTemplateSize);
	// Copy template and size
	memcpy(pTemplate, pResultTemplate, nResultTemplateSize);
	*nTemplateSize = nResultTemplateSize;
	// Delete Command
	delete pCommand;
	// Return ok
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Set_Template(const V100_DEVICE_TRANSPORT_INFO *pDev,uchar* pTemplate, uint nTemplateSize)
{
	
	Atomic_Set_Template* pCommand = reinterpret_cast<Atomic_Set_Template*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_SET_TEMPLATE));
	// Marshal Data into Template Container
	pCommand->SetTemplate(pTemplate, nTemplateSize);
	// Issue Command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	// Check for error
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	// 
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_Template*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Get_Config(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_INTERFACE_CONFIGURATION_TYPE* ICT)
{
	
	Atomic_Get_Config* pCommand = reinterpret_cast<Atomic_Get_Config*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_CONFIG));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_Config*>(pResponse);
	// 
	*ICT = pCommand->GetConfiguration();
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Get_Status(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_INTERFACE_STATUS_TYPE* pIST)
{
	
	Atomic_Get_Status* pCommand = reinterpret_cast<Atomic_Get_Status*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_STATUS));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_Status*>(pResponse);
	*pIST = pCommand->GetInterfaceStatusType();
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Get_LRing_Cmd(const V100_DEVICE_TRANSPORT_INFO *pDev, V100_LRING_API_TYPE* pCmd)
{
	
	Atomic_Get_LRing_Cmd* pCommand = reinterpret_cast<Atomic_Get_LRing_Cmd*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_GET_LRING_CMD)));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_LRing_Cmd*>(pResponse);
	*pCmd = pCommand->GetCmd();
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Set_LRing_Cmd(const V100_DEVICE_TRANSPORT_INFO *pDev, V100_LRING_API_TYPE* pCmd)
{
	
	Atomic_Set_LRing_Cmd* pCommand = reinterpret_cast<Atomic_Set_LRing_Cmd*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_SET_LRING_CMD)));
	// Set the command
	pCommand->SetCmd(*pCmd);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_LRing_Cmd*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Get_Cmd(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_INTERFACE_COMMAND_TYPE* pCmd)
{
	
	Atomic_Get_Cmd* pCommand = reinterpret_cast<Atomic_Get_Cmd*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_CMD));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_Cmd*>(pResponse);
	*pCmd = pCommand->GetCmd();
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Set_Cmd(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_INTERFACE_COMMAND_TYPE* pCmd)
{
	
	Atomic_Set_Cmd* pCommand = reinterpret_cast<Atomic_Set_Cmd*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_SET_CMD));
	// Set the command
	pCommand->SetCmd(*pCmd);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_Cmd*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}


V100_ERROR_CODE V100_Set_LED(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_LED_CONTROL Control)
{
	
	Atomic_Set_LED* pCommand = reinterpret_cast<Atomic_Set_LED*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_SET_LED));
	pCommand->SetLEDControl(Control);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_LED*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Config_Comport(const V100_DEVICE_TRANSPORT_INFO *pDev, uint nBaudRate)
{
	
	Atomic_Config_Comport* pCommand = reinterpret_cast<Atomic_Config_Comport*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_CONFIG_COMPORT));
	// Set Baud Rate
	pCommand->SetBaudRate(nBaudRate);
	//
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Config_Comport*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Reset(const V100_DEVICE_TRANSPORT_INFO *pDev)
{
	
	Atomic_Reset* pCommand = reinterpret_cast<Atomic_Reset*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_RESET));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Reset*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Set_Option(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_OPTION_TYPE OptType, uchar* pData, uint nDataSize)
{
	
	Atomic_Set_Option* pCommand = reinterpret_cast<Atomic_Set_Option*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_SET_OPTION));
	// Set the option
	pCommand->SetOption(OptType, pData, nDataSize);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_Option*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}
/********************** SPOOF *********************/
V100_ERROR_CODE V100_Match_Ex(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar* Tpl1, uint nSzT1, uchar* Tpl2, uint nSzT2, int& nMatchScore, int& nSpoofScore)
{
	
	Atomic_Match_Ex* pCommand = reinterpret_cast<Atomic_Match_Ex*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_MATCH_EX));
	// Set the option
	if( (nSzT1 < 1) || (Tpl1 == NULL) ) return GEN_ERROR_PARAMETER;
	// Set Probe Template
	pCommand->SetProbeTemplate(Tpl1, nSzT1);
	// Check Gallery Template
	if( ((nSzT2 < 1) || (Tpl2 == NULL)) == false )
	{
		pCommand->SetGalleryTemplate(Tpl2, nSzT2);
	}
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Match_Ex*>(pResponse);
	// Check result
	pCommand->GetMatchScore(nMatchScore);
	pCommand->GetSpoofScore(nSpoofScore);
	delete pCommand;
	
	return GEN_OK;
}

/************** End of Spoof ********************/

V100_ERROR_CODE V100_Set_Tag(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar* pTag, ushort nTagLength)
{
	
	Atomic_Set_Tag* pCommand = reinterpret_cast<Atomic_Set_Tag*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_SET_TAG));
	pCommand->SetTag((char*)pTag, nTagLength);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_Tag*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}


V100_ERROR_CODE V100_Get_Tag(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar* pTag, ushort& nTagLength)
{
	
	Atomic_Get_Tag* pCommand = reinterpret_cast<Atomic_Get_Tag*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_TAG));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_Tag*>(pResponse);
	char*  pTagToGet = NULL;
	pCommand->GetTag(&pTagToGet, nTagLength);
	memcpy(pTag, pTagToGet, nTagLength);
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Truncate_378(const V100_DEVICE_TRANSPORT_INFO *pDev, uint nMaxTemplateSize, const uchar* pInTemplate, uint nTplSize, uchar* pOutTemplate, uint& nActualSize)
{
	Atomic_Truncate_378* pCommand = reinterpret_cast<Atomic_Truncate_378*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_TRUNCATE_378));
	pCommand->SetTemplate(pInTemplate, nTplSize);
	pCommand->SetMaxTemplateSize(nMaxTemplateSize);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Truncate_378*>(pResponse);
	pCommand->GetTemplate(pOutTemplate, nActualSize);
	delete pCommand;
	return GEN_OK;
}

V100_ERROR_CODE V100_Get_OP_Status(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_OP_STATUS& opStatus)
{
	Atomic_Get_OP_Status* pCommand = reinterpret_cast<Atomic_Get_OP_Status*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_OP_STATUS));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_OP_Status*>(pResponse);
	if(pCommand == NULL) { delete pResponse; return GEN_ERROR_INTERNAL; }
	pCommand->GetOPStatus(&opStatus);
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_Save_Last_Capture(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_SAVE_CAPTURE Record)		
{
	Macro_Save_Last_Capture* pCommand = reinterpret_cast<Macro_Save_Last_Capture*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_SAVE_LAST_CAPTURE)));
	// Set the command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_Save_Last_Capture*>(pResponse);
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_Update_Firmware(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar* pFirmwareStream, uint nFWStreamSize)	
{
	Macro_Update_Firmware* pCommand = reinterpret_cast<Macro_Update_Firmware*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_UPDATE_FIRMWARE)));
	// Set the command
	if(false == pCommand->SetData(pFirmwareStream, nFWStreamSize)) 
	{
		delete pCommand; return (V100_ERROR_CODE)(1);
	}
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_Update_Firmware*>(pResponse);
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_Get_GPIO(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar& mask)
{
	uchar Mask = 0;
	Atomic_Get_GPIO * pCommand = reinterpret_cast<Atomic_Get_GPIO*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_GET_GPIO)));
	// Set the command
	pCommand->SetMask(mask);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_GPIO*>(pResponse);
	Mask = pCommand->GetMask();
	memcpy(&mask, &Mask, sizeof(Mask));
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_Set_GPIO(const V100_DEVICE_TRANSPORT_INFO *pDev, uchar mask)
{
	Atomic_Set_GPIO * pCommand = reinterpret_cast<Atomic_Set_GPIO*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_SET_GPIO)));
	// Set the command
	pCommand->SetMask(mask);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_GPIO*>(pResponse);
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_Cancel_Operation(const V100_DEVICE_TRANSPORT_INFO *pDev)
{
	Atomic_Cancel_Operation* pCommand = reinterpret_cast<Atomic_Cancel_Operation*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_CANCEL_OPERATION)));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Cancel_Operation*>(pResponse);
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_WaitForFingerClear(V100_DEVICE_TRANSPORT_INFO *pDev)
{
	bool bTimeout = false;
	_V100_ACQ_STATUS_TYPE Acq_Status;
	// Set Trigger to finger detect mode
	if( GEN_OK != V100_Arm_Trigger(pDev,TRIGGER_FINGER_DETECT)) return GEN_ERROR_INTERNAL;
	// Poll for completion
	while(bTimeout == false)
	{
		if( GEN_OK != V100_Get_Acq_Status(pDev, &Acq_Status)) 
		{
			return GEN_ERROR_INTERNAL;
		}
		switch(Acq_Status)
		{
			case ACQ_BUSY:		 { Sleep(PERIOD); continue; } break;
			case ACQ_NOOP:		 { Sleep(PERIOD); continue; } break;
			case ACQ_PROCESSING: { Sleep(PERIOD); continue; } break;
			case ACQ_FINGER_PRESENT: { Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_UP:{ Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_DOWN:{ Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_LEFT:{ Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_RIGHT:{ Sleep(PERIOD); continue; } break;
			case ACQ_FINGER_POSITION_OK:{ Sleep(PERIOD); continue; } break;

			case ACQ_NO_FINGER_PRESENT: { bTimeout = true; break;}
			case ACQ_TIMEOUT:			{ bTimeout = true; break;}
			case ACQ_DONE:				{ bTimeout = true; break;}
			case ACQ_CANCELLED_BY_USER:	{ bTimeout = true; break;}
			case ACQ_LATENT_DETECTED:	{ bTimeout = true; break;}
		
		}
	}

	if(Acq_Status != ACQ_NO_FINGER_PRESENT)
	{
		return GEN_ERROR_INTERNAL;
	}

	// Turn off finger detect mode and poll for completion
	V100_ERROR_CODE rc = V100_Arm_Trigger(pDev,TRIGGER_OFF);
	if( rc != GEN_OK && rc != GEN_ERROR_APP_BUSY) return GEN_ERROR_INTERNAL;
	bTimeout = false;
	// Poll for completion
	while(bTimeout == false)
	{
		if( GEN_OK != V100_Get_Acq_Status(pDev, &Acq_Status)) 
		{
			return GEN_ERROR_INTERNAL;
		}
		switch(Acq_Status)
		{
			case ACQ_BUSY:		 { Sleep(PERIOD); continue; } break;
			case ACQ_NOOP:		 { Sleep(PERIOD); continue; } break;
			case ACQ_PROCESSING: { Sleep(PERIOD); continue; } break;
			case ACQ_FINGER_PRESENT: { Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_UP:{ Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_DOWN:{ Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_LEFT:{ Sleep(PERIOD); continue; } break;
			case ACQ_MOVE_FINGER_RIGHT:{ Sleep(PERIOD); continue; } break;
			case ACQ_FINGER_POSITION_OK:{ Sleep(PERIOD); continue; } break;
			case ACQ_NO_FINGER_PRESENT:{ Sleep(PERIOD); continue; } break;

			case ACQ_TIMEOUT:			{ bTimeout = true; break;}
			case ACQ_DONE:				{ bTimeout = true; break;}
			case ACQ_CANCELLED_BY_USER:	{ bTimeout = true; break;}
			case ACQ_LATENT_DETECTED:	{ bTimeout = true; break;}		
		}
	}
	
	if(Acq_Status != ACQ_DONE)
	{
		return GEN_ERROR_INTERNAL;
	}

	return GEN_OK;

}

/************** 1-1 VERIFICATION (Venus Only)****************/

V100_ERROR_CODE V100_Set_Verification_Rules(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_VERIFICATION_RULES  verRules)
{
	Atomic_Set_Verification_Rules* pCommand = reinterpret_cast<Atomic_Set_Verification_Rules*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_SET_VERIFICATION_RULES));
	pCommand->SetVerificationRules(verRules);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Set_Verification_Rules*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Get_Verification_Rules(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_VERIFICATION_RULES& verRules)
{
	
	Atomic_Get_Verification_Rules* pCommand = reinterpret_cast<Atomic_Get_Verification_Rules*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_VERIFICATION_RULES));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_Verification_Rules*>(pResponse);
	verRules = pCommand->GetVerificationRules();
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Enroll_User(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_USER_RECORD UserRecord)
{
	
	Macro_Enroll_User* pCommand = reinterpret_cast<Macro_Enroll_User*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_ENROLL_USER));
	// Set it
	pCommand->SetUserRecord(&UserRecord);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_Enroll_User*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}


V100_ERROR_CODE V100_Verify_User(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_USER_RECORD UserRecord)
{
	
	Macro_Verify_User* pCommand = reinterpret_cast<Macro_Verify_User*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_VERIFY_USER));
	// Set it
	pCommand->SetUserRecord(&UserRecord);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_Verify_User*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Delete_User(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_USER_RECORD UserRecord)
{
	char pFileToDel[255];
	uint nFNLength = 0;
	nFNLength = sprintf(pFileToDel, "A:/user/%d.tpl", UserRecord.UID);

	// Delete the file
	Atomic_File_Delete* pCommand = reinterpret_cast<Atomic_File_Delete*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand((_V100_COMMAND_SET)CMD_FILE_DELETE));
	
	pCommand->SetName(pFileToDel, nFNLength+1, FS_FILE_TYPE_FILE);
		
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_File_Delete*>(pResponse);
	delete pCommand;
	return GEN_OK;
}

V100_ERROR_CODE V100_Get_User(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_USER_RECORD* pUserRecord, uchar* pRecordData)
{
	
	Atomic_Get_User* pCommand = reinterpret_cast<Atomic_Get_User*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_USER));
	pCommand->SetArguement(0);
	pCommand->SetUserRecord(pUserRecord);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_User*>(pResponse);
	pCommand->GetUserRecord(pUserRecord);
	memcpy(pRecordData, pCommand->GetRecordData(), pUserRecord->nSizeRecord);
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Get_User_By_Index(const V100_DEVICE_TRANSPORT_INFO *pDev, uint nIndex, _V100_USER_RECORD* pUserRecord, uchar* pRecordData)
{
	
	Atomic_Get_User* pCommand = reinterpret_cast<Atomic_Get_User*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_USER));
	pCommand->SetArguement(1);
	pCommand->SetUserIndexToGet(nIndex);
	
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	
	pCommand = dynamic_cast<Atomic_Get_User*>(pResponse);
	pCommand->GetUserRecord(pUserRecord);
	memcpy(pRecordData, pCommand->GetRecordData(), pUserRecord->nSizeRecord);
	delete pCommand;
	
	return GEN_OK;
}
V100_ERROR_CODE V100_Add_User(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_USER_RECORD UserRecord, uchar* pRecordData)
{
	
	Atomic_Add_User* pCommand = reinterpret_cast<Atomic_Add_User*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_ADD_USER));
	pCommand->SetUserRecord(&UserRecord);
	pCommand->SetOpaqueData((char*)pRecordData);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Add_User*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}

V100_ERROR_CODE V100_Get_DB_Metrics(const V100_DEVICE_TRANSPORT_INFO *pDev, _V100_DB_METRICS* dbMetrics)
{
	
	Atomic_Get_DB_Metrics* pCommand = reinterpret_cast<Atomic_Get_DB_Metrics*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_GET_DB_METRICS));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Get_DB_Metrics*>(pResponse);
	pCommand->GetDBMetrics(dbMetrics);
	delete pCommand;
	
	return GEN_OK;

}

V100_ERROR_CODE V100_Format_DB(const V100_DEVICE_TRANSPORT_INFO *pDev)
{
	
	Atomic_Format_DB* pCommand = reinterpret_cast<Atomic_Format_DB*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_FORMAT_DB));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_Format_DB*>(pResponse);
	delete pCommand;
	
	return GEN_OK;
}

/************** End of 1-1 VERIFICATION (Venus Only)****************/

/************** 1-N IDENTIFICATION (Mercury Only)****************/

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Create_DB(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_DB_INIT_STRUCT dbInitStructIn) 				
{
	Macro_ID_Create_DB* pCommand = reinterpret_cast<Macro_ID_Create_DB*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_CREATE_DB)));
	pCommand->SetDBInitParms(&dbInitStructIn);
	// Set the command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Create_DB*>(pResponse);
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Delete_DB(const V100_DEVICE_TRANSPORT_INFO *pDev, uint nDbNo)
{
	Macro_ID_Delete_DB* pCommand = reinterpret_cast<Macro_ID_Delete_DB*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_DELETE_DB)));
	//
	pCommand->SetDBToDelete(nDbNo);
	// Set the command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Delete_DB*>(pResponse);
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Set_Working_DB(const V100_DEVICE_TRANSPORT_INFO *pDev, uint nDB)
{
	Macro_ID_Set_Working_DB* pCommand = reinterpret_cast<Macro_ID_Set_Working_DB*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_SET_WORKING_DB)));
	// Set the command
	pCommand->SetGroupID(nDB);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Set_Working_DB*>(pResponse);
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Get_User_Record(const V100_DEVICE_TRANSPORT_INFO *pDev, short nIndex, _MX00_ID_USER_RECORD& rec, _MX00_TEMPLATE_INSTANCE instanceRecords[])		
{
	Atomic_ID_Get_User_Record* pCommand = reinterpret_cast<Atomic_ID_Get_User_Record*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_GET_USER_RECORD)));
	// Set the command
	pCommand->SetArguement(nIndex);
	pCommand->SetUserRecordHeader(rec);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Get_User_Record*>(pResponse);
	memcpy(&rec,pCommand->GetUserRecordHeader(), sizeof(rec));
	pCommand->GetTemplates(instanceRecords);
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Get_User_Record_Header(const V100_DEVICE_TRANSPORT_INFO *pDev, short nIndex, _MX00_ID_USER_RECORD& rec)
{
	Atomic_ID_Get_User_Record_Header* pCommand = reinterpret_cast<Atomic_ID_Get_User_Record_Header*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_GET_USER_RECORD_HEADER)));
	// Set the command
	pCommand->SetArguement(nIndex);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Get_User_Record_Header*>(pResponse);
	memcpy(&rec,pCommand->GetUserRecordHeader(), sizeof(rec));
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Set_User_Record(const V100_DEVICE_TRANSPORT_INFO *pDev, const _MX00_ID_USER_RECORD rec, const _MX00_TEMPLATE_INSTANCE instanceRecords[])
{
	Atomic_ID_Set_User_Record* pCommand = reinterpret_cast<Atomic_ID_Set_User_Record*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_SET_USER_RECORD)));
	// Set the command
	pCommand->SetUserRecords(rec, instanceRecords);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Set_User_Record*>(pResponse);
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Delete_User_Record(const V100_DEVICE_TRANSPORT_INFO *pDev, const _MX00_ID_USER_RECORD rec, bool nDeleteAllFingers)
{
	Atomic_ID_Delete_User_Record* pCommand = reinterpret_cast<Atomic_ID_Delete_User_Record*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_DELETE_USER_RECORD)));
	// Set the command
	pCommand->SetUserRecord((_MX00_ID_USER_RECORD&)rec);
	pCommand->SetArguement( (nDeleteAllFingers)?1:0 );
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Delete_User_Record*>(pResponse);
	delete pCommand;
	return GEN_OK;
}


VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Enroll_User_Record(const V100_DEVICE_TRANSPORT_INFO *pDev,  _MX00_ID_USER_RECORD UserRecord)
{
	Macro_ID_Enroll_User_Record* pCommand = reinterpret_cast<Macro_ID_Enroll_User_Record*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_ENROLL_USER_RECORD)));
	// Set the command
	pCommand->SetUserRecord(&UserRecord);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Enroll_User_Record*>(pResponse);
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Identify_378(const V100_DEVICE_TRANSPORT_INFO *pDev, const uchar* pTemplate, uint nSizeTemplate)
{
	Macro_ID_Identify_378* pCommand = reinterpret_cast<Macro_ID_Identify_378*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_IDENTIFY_378)));
	pCommand->SetTemplate((uchar*)pTemplate, nSizeTemplate);
	// Set the command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Identify_378*>(pResponse);
	delete pCommand;
	return GEN_OK;
}

V100_ERROR_CODE V100_ID_Identify(const V100_DEVICE_TRANSPORT_INFO *pDev)
{
	Macro_ID_Identify* pCommand = reinterpret_cast<Macro_ID_Identify*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(CMD_ID_IDENTIFY));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Identify*>(pResponse);
	delete pCommand;
	return GEN_OK;
}
				
VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Get_DB_Metrics(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_DB_METRICS* dbMetrics, bool bGetCurrent)
{
	Atomic_ID_Get_DB_Metrics* pCommand = reinterpret_cast<Atomic_ID_Get_DB_Metrics*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_GET_DB_METRICS)));
	// Set the command
	pCommand->SetArguement((bGetCurrent)?1:0);
	pCommand->SetDBMetrics((*dbMetrics));
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Get_DB_Metrics*>(pResponse);
	memcpy(dbMetrics, pCommand->GetDBMetrics(), sizeof(_MX00_DB_METRICS));
	delete pCommand;
	return GEN_OK;
}

V100_ERROR_CODE V100_ID_Get_System_Metrics(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_DB_METRICS** dbMetrics, uint& nNumDBsFound)
{
	Atomic_ID_Get_System_Metrics* pCommand = reinterpret_cast<Atomic_ID_Get_System_Metrics*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_GET_SYSTEM_METRICS)));
	// Set the command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Get_System_Metrics*>(pResponse);
	
	nNumDBsFound = pCommand->GetNumMetrics();
	// IMPLEMENT
	*dbMetrics = (_MX00_DB_METRICS*)MALLOC(sizeof(_MX00_DB_METRICS)*nNumDBsFound);
	_MX00_DB_METRICS* pPtr = *dbMetrics;
	//
	for(uint ii = 0; ii < nNumDBsFound ; ii++)
	{
		*pPtr = (*(pCommand->GetDBMetrics(ii)));
		pPtr++;
	}

	delete pCommand;
	return GEN_OK;
}

V100_ERROR_CODE V100_ID_Release_System_Metrics(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_DB_METRICS* pDBMetrics)
{
	if(NULL != pDBMetrics)
	{
		FREE(pDBMetrics);
	}

	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Get_Result(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_ID_RESULT& res)
{
	Atomic_ID_Get_Result* pCommand = reinterpret_cast<Atomic_ID_Get_Result*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_GET_RESULT)));
	// Set the command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Get_Result*>(pResponse);
	if( pCommand == NULL ) { delete pResponse; pResponse = NULL; return GEN_ERROR_INTERNAL; } 

	memcpy(&res, pCommand->GetResult(), sizeof(_MX00_ID_RESULT));
	delete pCommand;
	return GEN_OK;
}

VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Set_Parameters(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_ID_PARAMETERS  param)
{
	Atomic_ID_Set_Parameters* pCommand = reinterpret_cast<Atomic_ID_Set_Parameters*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_SET_PARAMETERS)));
	// Set the command
	pCommand->SetParameters(&param);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Set_Parameters*>(pResponse);
	delete pCommand;
	return GEN_OK;
}
VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Get_Parameters(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_ID_PARAMETERS& param)		
{
	Atomic_ID_Get_Parameters* pCommand = reinterpret_cast<Atomic_ID_Get_Parameters*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_GET_PARAMETERS)));
	// Set the command
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Atomic_ID_Get_Parameters*>(pResponse);
	memcpy(&param, pCommand->GetParameters(), sizeof(_MX00_ID_PARAMETERS));
	delete pCommand;
	return GEN_OK;
}
VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Verify_User_Record(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_ID_USER_RECORD UserRecord, short nConsiderFinger)		
{
	Macro_ID_Verify_User_Record* pCommand = reinterpret_cast<Macro_ID_Verify_User_Record*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_VERIFY_USER_RECORD)));
	// Set the command
	pCommand->SetArguement(nConsiderFinger);
	pCommand->SetUserRecord(&UserRecord);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Verify_User_Record*>(pResponse);
	return GEN_OK;
}
VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Verify_378(const V100_DEVICE_TRANSPORT_INFO *pDev, _MX00_ID_USER_RECORD UserRecord, unsigned char* pTemplate, uint nSizeTemplate, short nConsiderFinger)		
{
	Macro_ID_Verify_378* pCommand = reinterpret_cast<Macro_ID_Verify_378*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_VERIFY_378)));
	// Set the command
	pCommand->SetArguement(nConsiderFinger);
	pCommand->SetUserRecord(&UserRecord);
	pCommand->SetTemplate(pTemplate, nSizeTemplate);
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Verify_378*>(pResponse);
	return GEN_OK;
}
VCOM_CORE_EXPORT V100_ERROR_CODE V100_ID_Verify_Many(V100_DEVICE_TRANSPORT_INFO *pDev, uchar** pTemplates, uint* pSizeTemplates, uint nNumTemplates)
{
	Macro_ID_Verify_Many* pCommand = reinterpret_cast<Macro_ID_Verify_Many*>(V100CommandHandler::GetCommandHandler(pDev)->CreateCommand(_V100_COMMAND_SET(CMD_ID_VERIFY_MANY)));
	// Set the command
	if(false == pCommand->SetTemplates(pTemplates, pSizeTemplates, nNumTemplates))
	{
		delete pCommand;
		return GEN_ERROR_PARAMETER;
	}
	ICmd* pResponse = (V100CommandHandler::GetCommandHandler(pDev)->IssueCommand((V100_DEVICE_TRANSPORT_INFO*)(pDev),pCommand));
	if(pResponse == NULL) return GEN_ERROR_COMM_TIMEOUT;
	Atomic_Error* pErr = dynamic_cast<Atomic_Error*>(pResponse);
	if(pErr)  // Handle Error
	{
		short arg; pErr->GetArguement(arg); delete pErr; return (V100_ERROR_CODE)(arg);
	}
	pCommand = dynamic_cast<Macro_ID_Verify_Many*>(pResponse);
	delete pCommand;
	return GEN_OK;
}
/************** End of 1-N IDENTIFICATION (Mercury Only)****************/
