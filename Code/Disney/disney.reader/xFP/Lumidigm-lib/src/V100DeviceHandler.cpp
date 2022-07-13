#include "V100DeviceHandler.h"

#ifdef _GCC
#include "gccWindows.h"
#else
#include "windows.h"
#endif

#include "IMemMgr.h"

#ifndef _GCC
#include "ldiguid.h"
#include "setupapi.h"
#endif

#ifndef _GCC
#include "usbdriver.h"
#include "LDSNamedPipe.h"
#include "LumiServiceTypes.h"
#endif


V100DeviceHandler* g_pV100DeviceHandler = NULL;

HANDLE V100DeviceHandler::hSynchMutex = 0;


V100DeviceHandler* V100DeviceHandler::GetV100DeviceHandler()
{
#ifndef _GCC
	if(hSynchMutex)
	{
		WaitForSingleObject(hSynchMutex, INFINITE);
	}
#endif	
	if(g_pV100DeviceHandler != NULL)
	{
#ifndef _GCC
	if(hSynchMutex)
	{
		ReleaseMutex(hSynchMutex);
	}
#endif
		return g_pV100DeviceHandler;
	}
	g_pV100DeviceHandler = new V100DeviceHandler();
#ifndef _GCC
	if(hSynchMutex)
	{
		ReleaseMutex(hSynchMutex);
	}
#endif
	return g_pV100DeviceHandler;
}
void V100DeviceHandler::ReleaseV100DeviceHandler()
{
	#ifndef _GCC
	if(hSynchMutex)
	{
		WaitForSingleObject(hSynchMutex, INFINITE);
	}
#endif	
	if(g_pV100DeviceHandler != NULL)
	{
#ifndef _GCC
	if(hSynchMutex)
	{
		ReleaseMutex(hSynchMutex);
	}
#endif
		delete g_pV100DeviceHandler;
		g_pV100DeviceHandler = NULL;
	}
#ifndef _GCC
	if(hSynchMutex)
	{
		ReleaseMutex(hSynchMutex);
	}
#endif
}

V100DeviceHandler::V100DeviceHandler(void)
{
	m_nNumStreamingDevices = 0;
	m_nNumRegularDevices = 0;
	//
	memset(m_ServerName, 0, 2048); 
#ifndef _GCC
	if( NULL == V100DeviceHandler::hSynchMutex)
	{
		V100DeviceHandler::hSynchMutex = CreateMutex(NULL, FALSE, L"LumiDeviceHdlMutex");
	}
#endif
}

V100DeviceHandler::~V100DeviceHandler(void)
{
#ifndef _GCC
	if( NULL != V100DeviceHandler::hSynchMutex)
	{
		// Enter critical section to release and NULL static member
		// If you don't use a critical section, and the constructor for
		// V100CommandHandler is called by a different thread, there is a chance
		// that the mutex is closed, but not nulled, and bad things ensue.
		CRITICAL_SECTION critSec;
		InitializeCriticalSection(&critSec);
		EnterCriticalSection(&critSec);
		{
			ReleaseMutex(V100DeviceHandler::hSynchMutex);
			CloseHandle(V100DeviceHandler::hSynchMutex);
			V100DeviceHandler::hSynchMutex = NULL;
		}
		LeaveCriticalSection(&critSec);
		DeleteCriticalSection(&critSec);
	}	
#endif
	g_pV100DeviceHandler = NULL;
}


#ifdef _GCC
int ReadNumDevices();
#endif
void V100DeviceHandler::SetServerName(char* pServerName)
{
	strcpy(m_ServerName, pServerName);
}

V100_ERROR_CODE V100DeviceHandler::GetNumDevices(int* nNumDevices)
{
	V100_ERROR_CODE result = GEN_OK;
	*nNumDevices = 0;
	int nNumDev = 0;

#ifndef _GCC

	// Get the number of devices from the LDS
	LDSNamedPipe* namedPipe = new LDSNamedPipe();
	char ServerPath[2048];
	
	
	Device* pDevice = new Device();

	pDevice->GetCommServerName((uchar*)m_ServerName);

	if(pDevice) delete pDevice;

	sprintf(ServerPath, "\\\\%s\\PIPE\\LumiDeviceServiceMainPipe", m_ServerName);
	if( true == namedPipe->Initialize(Client, ServerPath))
	{
		LumiDevServiceCmdStruct ldscmd;
		memset(&ldscmd, 0, sizeof(LumiDevServiceCmdStruct));
		ldscmd.Command = LUMI_DS_CMD_GET_NUM_SENSORS;
		memset(ldscmd.strCommander, 0, 256);
		sprintf(ldscmd.strCommander, "V100DeviceHandler::GetNumDevices");

		if(false == namedPipe->WritePipe((uchar*) &ldscmd, sizeof(LumiDevServiceCmdStruct), 100))
		{
			result = GEN_ERROR_PIPE_WRITE;
			goto ExitGetNumDev;
		}

		if(false == namedPipe->ReadPipe((uchar*) &ldscmd, sizeof(LumiDevServiceCmdStruct), 100))
		{
			result = GEN_ERROR_PIPE_READ;
			goto ExitGetNumDev;
		}

		nNumDev = ldscmd.devNumber;
		result = GEN_OK;
	}
	else
	{
		// If LumiDeviceService is not running
		nNumDev = USBDriverInterface::QueryNumNonStrDevices( (LPGUID)&GUID_BULKLDI );
		result = GEN_OK;
	}
	
ExitGetNumDev:
	delete namedPipe;
	namedPipe = NULL;	

	*nNumDevices = nNumDev;

	return result;

#else
	*nNumDevices = ReadNumDevices();  //need to look through /dev/vcom* to do this right
	return true;
#endif
}

void V100DeviceHandler::GetStreamingDevCount(int* nNumDevices)
{
	*nNumDevices = m_nNumStreamingDevices;
}

void V100DeviceHandler::GetRegDevCount(int* nNumDevices)
{
	*nNumDevices = m_nNumRegularDevices;
}

#ifdef _GCC
#include "dirent.h"
int ReadNumDevices()
{
    DIR *dp;
    struct dirent *ep;
    int nDevices = 0;
    
    dp = opendir ("/dev/");
    if (dp != NULL)
     {
       while (ep = readdir (dp))
       {
         if(strncmp(ep->d_name,"vcom",4) == 0)
         {
            nDevices++;
         }
       }
       (void) closedir (dp);
     }
    
    return nDevices;
}
#endif

V100_ERROR_CODE V100DeviceHandler::GetDevice(uint deviceNumber, Device* pDevice)
{
	V100_ERROR_CODE result = GEN_OK;
	char ServerPath[2048];
	sprintf(ServerPath,"\\\\%s\\PIPE\\LumiDeviceServiceMainPipe", pDevice->GetCommServerName());
	// Get the device information from the LDS
	LDSNamedPipe* namedPipe = new LDSNamedPipe();
	//
	if( true == namedPipe->Initialize(Client, ServerPath))
	{
		// Setup the command
		LumiDevServiceCmdStruct ldscmd;
		memset(&ldscmd, 0, sizeof(LumiDevServiceCmdStruct));
		ldscmd.Command = LUMI_DS_CMD_GET_SENSOR_INFO;
		ldscmd.devNumber = deviceNumber;
		memset(ldscmd.strCommander, 0, 256);
		sprintf(ldscmd.strCommander, "V100DeviceHandler::GetDevice");

		// Setup the return structures
		LumiDevStruct devInfo;
		memset(&devInfo, 0, sizeof(LumiDevStruct));
		LumiDevServicePipeStruct ldsps;
		memset(&ldsps, 0, sizeof(LumiDevServicePipeStruct));

		// Send the command
		if(false == namedPipe->WritePipe((uchar*) &ldscmd, sizeof(LumiDevServiceCmdStruct), 100))
		{
			result = GEN_ERROR_PIPE_WRITE;
			goto ExitGetDev;
		}

		// Get the info back
		if(false == namedPipe->ReadPipe((uchar*) &devInfo, sizeof(LumiDevStruct), 100))
		{
			result = GEN_ERROR_PIPE_READ;
			goto ExitGetDev;
		}

		// Check the error code
		if(LDEC_OK != devInfo.nErrorCode)
		{
			switch(devInfo.nErrorCode)
			{
			case LDEC_SEngine_Not_Ready:
			case LDEC_SEngine_Not_Running:
				result = GEN_ERROR_DEVICE_NOT_READY;
				break;
			case LDEC_Device_Pipe_Read_Error:
				result = GEN_ERROR_PIPE_READ;
				break;
			case LDEC_Device_Pipe_Write_Error:
				result = GEN_ERROR_PIPE_WRITE;
				break;
			case LDEC_Device_Not_Found:
			default:
				result = GEN_ERROR_DEVICE_NOT_FOUND;
				break;
			}
			goto ExitGetDev;
		}

		if(devInfo.nSensorType >= MERCURY_31X)
		{
			// We have a streaming device to map
			// Get the pipe name
			memset(&ldscmd, 0, sizeof(LumiDevServiceCmdStruct));
			ldscmd.Command = LUMI_DS_CMD_GET_NAMED_PIPE;
			ldscmd.devNumber = devInfo.nDeviceIndex;
			memset(ldscmd.strCommander, 0, 256);
			sprintf(ldscmd.strCommander, "V100DeviceHandler::GetDevice");

			if(false == namedPipe->WritePipe((uchar*) &ldscmd, sizeof(LumiDevServiceCmdStruct), 100))
			{
				result = GEN_ERROR_PIPE_WRITE;
				goto ExitGetDev;
			}

			if(false == namedPipe->ReadPipe((uchar*) &ldsps, sizeof(LumiDevServicePipeStruct), 100))
			{
				result = GEN_ERROR_PIPE_READ;
				goto ExitGetDev;
			}

			// Check for NULL pipe names, don't create the device pointer (don't care now about the device name)
			if(NULL == ldsps.ulPipeName)
			{
				result = GEN_ERROR_DEVICE_NOT_READY;
				goto ExitGetDev;
			}

			pDevice->Init(devInfo.nDeviceIndex, V100_STREAMING_DEVICE, ldsps.ulPipeName, ldsps.ulDeviceName, devInfo.nMemberIndex, devInfo.nSensorType);

			m_nNumStreamingDevices++;
		}
		else
		{
			// We have a regular device, just return it
			pDevice->Init(devInfo.nDeviceIndex, V100_NORMAL_DEVICE, ldsps.ulPipeName, ldsps.ulDeviceName, devInfo.nMemberIndex, devInfo.nSensorType);

			m_nNumRegularDevices++;
		}
	}
	else
	{
		// If LumiDeviceService is not running
		m_nNumRegularDevices = USBDriverInterface::QueryNumNonStrDevices( (LPGUID)&GUID_BULKLDI );
		// Add regular device to map
		for(int i = 0; i < m_nNumRegularDevices; i++)
		{
			if(i == deviceNumber)
			{
				pDevice->Init(i, V100_NORMAL_DEVICE, NULL, NULL, i, 0); 
				goto ExitGetDev;
			}
		}

		result = GEN_ERROR_DEVICE_NOT_FOUND;
	}

ExitGetDev:
	delete namedPipe;
	namedPipe = NULL;	

	return result;
}