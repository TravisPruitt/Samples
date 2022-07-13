 /******************************************************************************
**  You are free to use this example code to generate similar functionality  
**  tailored to your own specific needs.  
**
**  This example code is provided by Lumidigm Inc. for illustrative purposes only.  
**  This example has not been thoroughly tested under all conditions.  Therefore 
**  Lumidigm Inc. cannot guarantee or imply reliability, serviceability, or 
**  functionality of this program.
**
**  This example code is provided by Lumidigm Inc. “AS IS” and without any express 
**  or implied warranties, including, but not limited to the implied warranties of 
**  merchantability and fitness for a particular purpose.
**
**	Date: Wednesday, 16 February 2011
**	Author: Lumidigm
**	Copyright 2011 Lumidigm
**
********************************************************************************/
// VCOMDisneyApp.cpp :  Defines the entry point for the console application. The 
//                      application provides sample code for general VCOM commands 
//                      and an example of using the 1:N Identification VCOM commands. 
// 
// Note: This application is designed to compile and run on different platforms,
//       specifically, Win32 and Linux. Platform-specific code is compiled
//       in/out as needed.
//  
// 
// SetupDeviceConnection: Sets up the device handle and establish communication 
//                        with the device.
// RunCaptureAndMatchTest Demonstrates how to perform 2 captures and a match.
// RunAPITest:            Demonstrates general VCOM API commands such as Capture, 
//                        Match, Verify images, getting command and
//                        configuration structures.
// RunFirmwareUpdateTest: Demonstrates updating the firmware on a V300D unit
// RunLightRingTest:	  Demonstrates setting the various LED states on the light ring
// WaitForOperationCompletion: Wait for long operations.
// 

#ifndef _GCC
#define SLEEP_TIME 100
#include "afxwin.h"
#include "windows.h"
#define ASCII_ENTER_CODE 13
#ifndef _WIN32_WCE
#include "conio.h"
#endif
#else
#define SLEEP_TIME 1
#include "gccWindows.h"
#include <stdio.h>
#include <string.h>
#include <termios.h>
#include <unistd.h>
#define ASCII_ENTER_CODE 10
#endif

#include <iostream>
#include "VCOMCore.h"

using namespace std;

/////////////////////////////////////////////////////////////////////////////////
// Constant definitions
/////////////////////////////////////////////////////////////////////////////////
const char* VERSION_STRING = "- Version 4.10D";
const char* SELECT_CONNECT_MENU =
{ 
	"\nSelect the communication method to use:\
	 \n 1)  USB\
	 \n 2)  RS-232\
	 \n <Q> Quit\
	 \n\n" 
};
const char* SELECT_COM_INDEX_MENU = 
{
	"\nEnter the COM port number to use:\
	\n(hit the enter key after typing the COM port number)\n"
};
const char* SELECT_TEST_MENU = 
{
	"\n\nSelect a Test Case\
	\n-----------------------------------------------\
	\n 1)  Capture, Match, Save Diagnostic Info\
	\n 2)  API Check\
	\n 3)  Firmware Update\
	\n 4)  Light-Ring Test\
    \n 5)  USB test\
	\n <Q> Quit\n\n"
};

const char* pFirmwareLoc = "V300D.ldr";

/////////////////////////////////////////////////////////////////////////////////
// enums
/////////////////////////////////////////////////////////////////////////////////
// A simple enumeration to keep track of the different Lumidigm devices quickly
typedef enum SUPPORTED_DEVICES
{
	VX00 = 0, 		// Venus Devices (All configurations)
	M100,			// Mercury M100
	M300,			// Mercury M300
	M310,			// Mariner M310
	V310,			// Voyager V310
	UNKNOWN_DEV,
} DeviceType;

const char* DeviceTypeStr[6] =
{
	"Venus V30X",
	"Mercury M100",
	"Mercury M30x",
	"Mariner M31x",
	"Voyager V31x",
	"Unknown Device"
};

DeviceType g_nDeviceType;

/////////////////////////////////////////////////////////////////////////////////
// Function Prototypes
/////////////////////////////////////////////////////////////////////////////////
bool SetupDeviceConnection(V100_DEVICE_TRANSPORT_INFO* pDev, bool bSetupForRS232);
void CloseDeviceConnection(V100_DEVICE_TRANSPORT_INFO* pDev);
bool ChooseTransport(V100_DEVICE_TRANSPORT_INFO* pDev);
bool ChooseAndRunTest(V100_DEVICE_TRANSPORT_INFO* pDev);
// Test Cases
bool RunCaptureAndMatchTest(V100_DEVICE_TRANSPORT_INFO* pDev);
bool RunUSBTest(V100_DEVICE_TRANSPORT_INFO* pDev);
bool RunFirmwareUpdateTest(V100_DEVICE_TRANSPORT_INFO* pDev);
bool RunLightRingTest(V100_DEVICE_TRANSPORT_INFO* pDev);
bool RunAPITest(V100_DEVICE_TRANSPORT_INFO* pDev);
bool RunIDTest(V100_DEVICE_TRANSPORT_INFO* pDev);
// Helper Functions
bool Capture(V100_DEVICE_TRANSPORT_INFO *pDev,
							  uchar* pCompositeImage,
							  uint&	 nWidth,
							  uint&  nHeight,
							  uchar* pTemplate,
							  uint&	 nTemplateSize,
							  int&	 nSpoof,
							  int	 getComposite,
							  int	 getTemplate);
_V100_OP_ERROR WaitForOperationCompletion(V100_DEVICE_TRANSPORT_INFO* pDev);
void PrintReturnCode(V100_ERROR_CODE rc);
void PrintOPReturnCode(_V100_OP_ERROR rc);
void PrintConfig(_V100_INTERFACE_CONFIGURATION_TYPE *config);
void PrintDBMetrics(_MX00_DB_METRICS* dbMetrics);
// Multi-platform handlers
void SleepVIK(int nMilliSecToSleep);
int  GetUserInput(void);

/////////////////////////////////////////////////////////////////////////////////
// Main function
/////////////////////////////////////////////////////////////////////////////////
#ifdef _GCC
int main(int argc, char * argv[])
#else
int _tmain(int argc, _TCHAR* argv[])
#endif
{
	// Initialize the device handle
	V100_DEVICE_TRANSPORT_INFO Dev;	
	memset(&Dev,0,sizeof(V100_DEVICE_TRANSPORT_INFO));

	fprintf(stdout,"\n\nVCOM Integration Example %s", VERSION_STRING);
	fprintf(stdout,"\n-----------------------------------------------");

	V100_ERROR_CODE rc = GEN_OK;
	
    uint nCommType   = 1;
	
	g_nDeviceType = VX00;
	
    bool bValidSelection = false;
	bool bSetupForRS232  = false;
	// Choose transport (USB or RS232)
	if(false == ChooseTransport(&Dev))
	{
		goto Exit;
	}
	
	// Choose which test to run
	if(false == ChooseAndRunTest(&Dev))
	{
		goto Exit;
	}
    CloseDeviceConnection(&Dev);
 
Exit:
	printf("\n\nPress Enter to continue...");
	getchar();
	return 0;
}

bool ChooseTransport(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	if(false == SetupDeviceConnection(pDev, false)) { return false; }					
    return true;
}

bool ChooseAndRunTest(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	// Determine the test to run
	bool bValidSelection = false;
	while(false == bValidSelection)
	{		
		fprintf(stdout, "%s", SELECT_TEST_MENU);

		int ch = GetUserInput();
		switch(ch)
		{
		case '1':
			{
				bValidSelection = true;
				for( int ii = 0 ; ii < 100 ; ii++)
				{
					if(false == RunCaptureAndMatchTest(pDev))
					{
						fprintf(stdout,"\n\n Capture and Match Test failed!");
						return false;
					} 
					fprintf(stdout,"\n***** Iteration %d", ii);
				}
			} break;
		case '2':
			{
				bValidSelection = true;
				if(false == RunAPITest(pDev))
				{
					fprintf(stdout,"\n\n API Test failed!");
					return false;
				}
			} break;
		case '3':
			{
				bValidSelection = true;
				if(false == RunFirmwareUpdateTest(pDev))
				{
					fprintf(stdout,"\n\n Firmware Update Test failed!");
					return false;
				}
			} break;
		case '4':
			{
				bValidSelection = true;
				if(false == RunLightRingTest(pDev))
				{
					fprintf(stdout,"\n\n Light-Ring Test failed!");
					return false;
				}
			} break;
		case '5':
			{
				bValidSelection = true;
                if (false == RunUSBTest(pDev))
                {
                    fprintf(stdout, "\n\n USB test failed!");
                    return false;
                }
			} break;
		case 'q':
		case 'Q':
			{
				bValidSelection = true;
			} break;
		default:
			{
				fprintf(stdout, "Invalid Option! Please try again.\n");
			} break;
		}
	}	
	return true;
}


bool SetupDeviceConnection(V100_DEVICE_TRANSPORT_INFO* pDev, bool bSetupForRS232)
{
	V100_ERROR_CODE rc = GEN_OK;

		// Query number of devices connected
	int nNumDevices = 0;
	fprintf(stdout, "\nV100_Get_Num_USB_Devices:  ");
	rc =  V100_Get_Num_USB_Devices(&nNumDevices);
	PrintReturnCode(rc);
	// If no devices found, return and display error message
	if(nNumDevices == 0)
	{
		fprintf(stdout,"\nNo devices found\n");
		return false;
	}
	// Open the device
	fprintf(stdout, "\nV100_Open:  ");
	rc = V100_Open(pDev);
	PrintReturnCode(rc);

	// If cannot open the device, return and display error message
	if( rc != GEN_OK)
	{
		fprintf(stdout, "\n Cannot open the device\n");
		return false;
	}	

	// Get Configuration Structure 
	_V100_INTERFACE_CONFIGURATION_TYPE config;
	fprintf(stdout, "\nV100_Get_Config:  ");
	rc = V100_Get_Config(pDev, &config);
	PrintReturnCode(rc);
	
	// If cannot get Configuration structure, return
	if( rc != GEN_OK)
	{
		fprintf(stdout, "\n Cannot get configuration from the device\n");
		return false;
	}
	PrintConfig(&config);

	// Set the device type
	g_nDeviceType = UNKNOWN_DEV;
	if(config.Vendor_Id == 0x0525 && config.Product_Id == 0x3424)
		g_nDeviceType = VX00; // Venus sensor
	else if(config.Vendor_Id == 0x1FAE)
	{
		if(config.Product_Id == 0x212C)
			g_nDeviceType = M300; // M30X Mercury Sensor
		else if(config.Product_Id == 0x0041)
			g_nDeviceType = M310; // M31X Mariner Sensor
		else if(config.Product_Id == 0x0021)
			g_nDeviceType = V310; // V31X Voyager Sensor
	}

	cout << "Sensor Type: ....................... " << DeviceTypeStr[g_nDeviceType] << endl;

	if(bSetupForRS232)
	{
		// Set the device with new baudrate
		fprintf(stdout, "\n\nSetting device with new baudrate 57600...");
		// Config Comport
		uint BaudRate = 57600;
		fprintf(stdout, "\nV100_Config_Comport:  ");
		rc =  V100_Config_Comport(pDev, BaudRate);
		PrintReturnCode(rc);

		//If cannot configure comport return and display error message
		if( rc != GEN_OK)
		{
			fprintf(stdout, "\n Cannot config the comport\n");
			return false;
		}

		// Reboot the device with new baudrate
		fprintf(stdout, "\nV100_Close:  ");
		rc = V100_Close(pDev);
		PrintReturnCode(rc);

		pDev->nBaudRate = BaudRate;

		fprintf(stdout, "\nV100_Open:  ");
		rc = V100_Open(pDev);
		PrintReturnCode(rc);

		// If cannot open the device, return and display error message
		if( rc != GEN_OK)
		{
			fprintf(stdout, "\nCannot reopen the device\n");
			return false;
		}

		SleepVIK(1000);
	}

	// Get Command Structure
	_V100_INTERFACE_COMMAND_TYPE cmd;
	fprintf(stdout, "\nV100_Get_Cmd:  ");
	rc = V100_Get_Cmd(pDev, &cmd);
	PrintReturnCode(rc);
	if(rc != GEN_OK)
	{
		fprintf(stdout, "\nCannot get command structure\n");
		return false;
	}

	// Set preprocessing, extraction, matching
	// Turn on all on board processing
	cmd.Select_PreProc    = 1;
	cmd.Select_Matcher    = 1;
	cmd.Select_Extractor  = 1;
	cmd.Override_Trigger  = 0;

	// Set the Command structure with all on board processing turned on
	fprintf(stdout, "\nV100_Set_Cmd:  ");
	rc = V100_Set_Cmd(pDev, &cmd);
	PrintReturnCode(rc);
	if(rc != GEN_OK)
	{
		fprintf(stdout, "\nCannot set command structure\n");
		return false;
	}

	return true;
}

// Reboot device if necessary
void CloseDeviceConnection(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	V100_ERROR_CODE rc = GEN_OK;
	V100_Close(pDev);
}

bool RunFirmwareUpdateTest(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	V100_ERROR_CODE rc = GEN_OK;
	_V100_OP_ERROR	Oprc;

	FILE* fFile = fopen(pFirmwareLoc, "r+b");
	
	if(fFile == NULL)
	{
		fprintf(stdout,"\n Firmware File not found - check your working directory.");
		return false;
	}
	//
	uchar* pFWFile = new uchar[1024*1024];		// MB should be enough
	unsigned int nSz = (unsigned int)fread(pFWFile, 1, 1024*1024, fFile);
	fclose(fFile);
	
	// Make the call
	fprintf(stdout,"\nV100_Update_Firmware....");
	rc = V100_Update_Firmware(pDev, pFWFile, nSz);
	PrintReturnCode(rc);
	if( GEN_OK != rc )  
	{
		fprintf(stdout,"\n Error updating firmare.");
		return false;
	}
	//
	fprintf(stdout, "\nWaitForOperationCompletion:  ");
	Oprc = WaitForOperationCompletion(pDev);
	PrintOPReturnCode(Oprc);
	if(Oprc !=0)
	{
		fprintf(stdout, "\nError: V100_Update_Firmware was not successful");
		return false;
	}
	//
	fprintf(stdout, "\n\nFirmware Updated succesfully.   Rebooting Device");
	//
	rc = V100_Reset(pDev);
	V100_Close(pDev);
	// Give it a few seconds to reboot
	SleepVIK(2000);
	// Reopen Device handle
	SetupDeviceConnection(pDev, false);
	return true;
}

bool RunLightRingTest(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	V100_ERROR_CODE rc = GEN_OK;

	V100_LRING_API_TYPE lCmd;
	// Get LRing Config Structure
    rc = V100_Get_LRing_Cmd(pDev, &lCmd);

	// Have some light-ring fun

	fprintf(stdout,"\n\nRed: ");	
	fprintf(stdout, "\nV100_Set_LED:  ");
	rc = V100_Set_LED(pDev, _V100_LED_CONTROL(LRING_RED));
	PrintReturnCode(rc);
	SleepVIK(1000);
	
	PrintReturnCode(rc);
	
	fprintf(stdout,"\n\nWhite: ");	
	fprintf(stdout, "\t\tV100_Set_LED:  ");
	rc = V100_Set_LED(pDev, _V100_LED_CONTROL(LRING_WHITE));
	SleepVIK(1000);
	PrintReturnCode(rc);

	fprintf(stdout,"\n\nBlue: ");	
	fprintf(stdout, "\t\tV100_Set_LED:  ");
	rc = V100_Set_LED(pDev, _V100_LED_CONTROL(LRING_BLUE));
	SleepVIK(1000);
	PrintReturnCode(rc);

	fprintf(stdout,"\n\nProgress: ");	
	fprintf(stdout, "\tV100_Set_LED:  ");
	rc = V100_Set_LED(pDev, _V100_LED_CONTROL(LRING_PROGRESS));
	SleepVIK(1000);
	PrintReturnCode(rc);

	fprintf(stdout,"\n\nPendelum: ");	
	fprintf(stdout, "\tV100_Set_LED:  ");
	rc = V100_Set_LED(pDev, _V100_LED_CONTROL(LRING_PRESET_1));
	SleepVIK(1000);
	PrintReturnCode(rc);

	return true;
}

bool RunCaptureAndMatchTest(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	// Initialize variables	
	_V100_INTERFACE_CONFIGURATION_TYPE	config;
	V100_ERROR_CODE						rc;
	uchar*	pCompositeImage1 = NULL;
	uchar*	pCompositeImage2 = NULL;
	uchar*  pDiagnosticData  = NULL;
	uchar	pTemplate1[2048];
	uchar   pTemplate2[2048];
	uint	nWidth;
	uint	nHeight;
	uint	nTemplateSize1, nTemplateSize2;
	uint	Score;
	int     nSpoof;
	bool    retCode = true;
	int     nGetCompImg = 0;
	uint    nImageSize = 0;

	fprintf(stdout, "\n\n--- Running Capture and Match Test ---\n");

	nGetCompImg = 1; // Get composite image over USB
	// Get Config Structure
	fprintf(stdout, "\nV100_Get_Config:  ");
	rc = V100_Get_Config(pDev, &config);
	PrintReturnCode(rc);
	if(rc != GEN_OK)
	{
		fprintf(stdout, "\nError occured getting config structure\n");
		retCode = false;
		goto Abort;
	}

	// Initialize width and height of Composite image
	nWidth = config.Composite_Image_Size_X;
	nHeight = config.Composite_Image_Size_Y;
	
	pCompositeImage1 = new uchar[nWidth*nHeight];
	pCompositeImage2 = new uchar[nWidth*nHeight];
	pDiagnosticData  = new uchar[MAX_DIAGNOSTIC_DATA_SIZE];
	// Capture first image and get template
	fprintf(stdout,"\n\nFirst Capture...  Place finger on sensor");	
	if(false == Capture(pDev, pCompositeImage1, nWidth, nHeight, pTemplate1, nTemplateSize1, nSpoof, nGetCompImg, 1))
	{
		goto Abort;
	}
	// Grab the diagnostic data

	// Capture second image and get template
	fprintf(stdout,"\n\nSecond Capture...  Place finger on sensor");
	if(false == Capture(pDev, pCompositeImage2, nWidth, nHeight, pTemplate2, nTemplateSize2, nSpoof, nGetCompImg, 1))
	{
		goto Abort;
	}
	fprintf(stdout,"\n\nGathering Diagnostic Data...");
	
	fprintf(stdout,"\n\nGetting Full Set...");
	rc = V100_Get_Image(pDev, _V100_IMAGE_TYPE(DIAGNOSTIC_DATA_SET_FULL), pDiagnosticData, nImageSize);
	fprintf(stdout,"got %d bytes", nImageSize);
	if( rc != GEN_OK )
	{
		goto Abort;
	}
	fprintf(stdout,"\n\nGetting Sub-Set...");
	rc = V100_Get_Image(pDev, _V100_IMAGE_TYPE(DIAGNOSTIC_DATA_SET_IMAGES), pDiagnosticData, nImageSize);
	fprintf(stdout,"got %d bytes", nImageSize);
	if( rc != GEN_OK )
	{
		goto Abort;
	}
	
	fprintf(stdout,"\n\nGetting Minimal...");
	rc = V100_Get_Image(pDev, _V100_IMAGE_TYPE(DIAGNOSTIC_DATA_SET_CONFIG), pDiagnosticData, nImageSize);
	fprintf(stdout,"got %d bytes", nImageSize);
	if( rc != GEN_OK )
	{
		goto Abort;
	}
	// Match templates
	fprintf(stdout,"\n\nMatching...");
	fprintf(stdout, "\nV100_Match:  ");
	rc = V100_Match(pDev, pTemplate1, nTemplateSize1, pTemplate2, nTemplateSize2, Score);
	
	PrintReturnCode(rc);
	fprintf(stdout,"\nMatch Score %d\n\n",Score);


Abort:	
	delete[] pCompositeImage1;
	delete[] pCompositeImage2;
	return retCode;
}

bool RunUSBTest(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	// Initialize variables	
	_V100_INTERFACE_CONFIGURATION_TYPE	config;
	V100_ERROR_CODE						rc;
	uchar*	pCompositeImage1 = NULL;
	uchar*	pCompositeImage2 = NULL;
	uchar*  pDiagnosticData  = NULL;
	uchar	pTemplate1[2048];
	uchar   pTemplate2[2048];
	uint	nWidth;
	uint	nHeight;
	uint	nTemplateSize1, nTemplateSize2;
	uint	Score;
	int     nSpoof;
	bool    retCode = true;
	int     nGetCompImg = 0;
	uint    nImageSize = 0;

	fprintf(stdout, "\n\n--- Running USB Test ---\n");

	nGetCompImg = 1; // Get composite image over USB
	// Get Config Structure
	fprintf(stdout, "\nV100_Get_Config:  ");
	rc = V100_Get_Config(pDev, &config);
	PrintReturnCode(rc);
	if(rc != GEN_OK)
	{
		fprintf(stdout, "\nError occured getting config structure\n");
		retCode = false;
		goto Abort;
	}

	// Initialize width and height of Composite image
	nWidth = config.Composite_Image_Size_X;
	nHeight = config.Composite_Image_Size_Y;
	
	pCompositeImage1 = new uchar[nWidth*nHeight];
	pCompositeImage2 = new uchar[nWidth*nHeight];
	pDiagnosticData  = new uchar[MAX_DIAGNOSTIC_DATA_SIZE];
	// Capture first image and get template
	fprintf(stdout,"\n\nFirst Capture...  Place finger on sensor");	
	if(false == Capture(pDev, pCompositeImage1, nWidth, nHeight, pTemplate1, nTemplateSize1, nSpoof, nGetCompImg, 1))
	{
		goto Abort;
	}
	// Grab the diagnostic data

	// Capture second image and get template
	fprintf(stdout,"\n\nSecond Capture...  Place finger on sensor");
	if(false == Capture(pDev, pCompositeImage2, nWidth, nHeight, pTemplate2, nTemplateSize2, nSpoof, nGetCompImg, 1))
	{
		goto Abort;
	}
	
	fprintf(stdout,"\n\nGathering Diagnostic Data...");

    while (1)
    {
        fprintf(stdout,"\n\nGetting Full Set...");
        rc = V100_Get_Image(pDev, _V100_IMAGE_TYPE(DIAGNOSTIC_DATA_SET_FULL), pDiagnosticData, nImageSize);
        fprintf(stdout,"got %d bytes", nImageSize);
        if( rc != GEN_OK )
        {
            goto Abort;
        }
        fprintf(stdout,"\n\nGetting Sub-Set...");
        rc = V100_Get_Image(pDev, _V100_IMAGE_TYPE(DIAGNOSTIC_DATA_SET_IMAGES), pDiagnosticData, nImageSize);
        fprintf(stdout,"got %d bytes", nImageSize);
        if( rc != GEN_OK )
        {
            goto Abort;
        }
        
        fprintf(stdout,"\n\nGetting Minimal...");
        rc = V100_Get_Image(pDev, _V100_IMAGE_TYPE(DIAGNOSTIC_DATA_SET_CONFIG), pDiagnosticData, nImageSize);
        fprintf(stdout,"got %d bytes", nImageSize);
        if( rc != GEN_OK )
        {
            goto Abort;
        }
    }


Abort:	
	delete[] pCompositeImage1;
	delete[] pCompositeImage2;
	return retCode;
}

bool RunAPITest(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	// Initialize variables	
	_V100_INTERFACE_CONFIGURATION_TYPE	config;
	V100_ERROR_CODE						rc;
	_V100_OPTION_TYPE					OptType;
	_V100_PRESENCE_DETECTION_TYPE		PdType;

	uchar*	pCompositeImage1 = NULL;
	uchar*	pCompositeImage2 = NULL;
	uchar	pTemplate1[2048];
	uchar   pTemplate2[2048];

	uint	nWidth;
	uint	nHeight;
	uint	nTemplateSize1, nTemplateSize2;
	uint	Score;
	uint	nImageSize;
	
	int		nSpoof;
	int		nTimeouts;
	bool    retCode = true;
	
	DWORD dwStartTime = 0;

	fprintf(stdout, "\n\n--- Running API Test ---\n");

	// Get Config Structure
	fprintf(stdout, "\nV100_Get_Config:  ");
	rc = V100_Get_Config(pDev, &config);
	PrintReturnCode(rc);
	if(rc != GEN_OK)
	{
		fprintf(stdout, "\nError occured getting config structure\n");
		retCode = false;
		goto Abort;
	}

	// Initialize width and height of Composite image
	nWidth = config.Composite_Image_Size_X;
	nHeight = config.Composite_Image_Size_Y;
	
	pCompositeImage1 = new uchar[nWidth*nHeight];
	pCompositeImage2 = new uchar[nWidth*nHeight];

	// Capture first image and get composite image
	fprintf(stdout,"\n\nFirst Capture(Capture and Get Composite image)... Place finger on sensor");
	nTimeouts = 0;

	if(false == Capture(pDev, pCompositeImage1, nWidth, nHeight, NULL, nTemplateSize1, nSpoof, 1, 0))
	{
		goto Abort;
	}
	// Get latest extracted template from the device
	fprintf(stdout,"\n\nGetting template...");
	fprintf(stdout, "\nV100_Get_Template:  ");
	rc = V100_Get_Template(pDev, pTemplate1, &nTemplateSize1);
	PrintReturnCode(rc);

	// Capture second image and get template 
	fprintf(stdout,"\n\nSecond Capture(Capture and Get Template)... Place finger on sensor");
	nTimeouts = 0;

	if(false == Capture(pDev, NULL, nWidth, nHeight, pTemplate2, nTemplateSize2, nSpoof, 0, 1))
	{
		goto Abort;
	}

	// Match templates
	fprintf(stdout,"\n\nMatching...");
	fprintf(stdout, "\nV100_Match:  ");
	rc = V100_Match(pDev,
					pTemplate1,
					nTemplateSize1,
					pTemplate2,
					nTemplateSize2,
					Score);
	
	PrintReturnCode(rc);
	fprintf(stdout,"\nMatch Score %d",Score);

	//Change presence detection threshold
	fprintf(stdout,"\n\nChanging presence detection level to low sensitivity...");
	OptType = OPTION_PD_LEVEL;
	// Low sensitivity means hard to detect
	PdType = PD_SENSITIVITY_LOW;
	fprintf(stdout, "\nV100_Set_Option:  ");
	rc = V100_Set_Option(pDev, OptType,(uchar *)(&PdType), sizeof(_V100_PRESENCE_DETECTION_TYPE));
	PrintReturnCode(rc);

	// Verify with second template
	fprintf(stdout,"\n\nVerifying with second capture... Place finger on sensor");	
	fprintf(stdout, "\nV100_Verify:  ");
	rc = V100_Verify(pDev,
					 pTemplate2,
					 nTemplateSize2,
					 nSpoof,
					 Score);
					 
	PrintReturnCode(rc);
	fprintf(stdout,"\nMatch Score %d",Score);
	
	// Get current composite image
	fprintf(stdout,"\n\nGetting Composite Image...");
	fprintf(stdout, "\nV100_Get_Composite_Image:  ");
	rc = V100_Get_Composite_Image(pDev, pCompositeImage2, &nSpoof, &nImageSize);
	PrintReturnCode(rc);

	

Abort:
	
	delete[] pCompositeImage1;
	delete[] pCompositeImage2;

	return retCode;
}


bool Capture(V100_DEVICE_TRANSPORT_INFO *pDev,
							  uchar* pCompositeImage,
							  uint&	 nWidth,
							  uint&  nHeight,
							  uchar* pTemplate,
							  uint&	 nTemplateSize,
							  int&	 nSpoof,
							  int	 getComposite,
							  int	 getTemplate)
{
	V100_ERROR_CODE rc = GEN_OK;
	int nTimeouts = 0;
	// If timeout attempt 10 times to capture the image before giving up
	while(nTimeouts < 10)
	{		
		fprintf(stdout, "\nV100_Capture:  ");
		rc = V100_Capture(pDev, pCompositeImage, nWidth, nHeight, pTemplate, nTemplateSize, nSpoof, getComposite, getTemplate);
		PrintReturnCode(rc);
		if(rc == GEN_ERROR_TIMEOUT)
			nTimeouts++;	
		else if(rc == GEN_ERROR_TIMEOUT_LATENT)
		{
			nTimeouts++;
			fprintf(stdout, " -- Please lift your finger and place again\n");
			fprintf(stdout, "\nV100_Capture:  ");
		}
		else
			break;
	}	
	if( rc != GEN_OK)
	{
		fprintf(stdout, "\nError occured while capturing image\n");
		return false;
	}
	return true;
}

// Wait for completion
_V100_OP_ERROR WaitForOperationCompletion(V100_DEVICE_TRANSPORT_INFO* pDev)
{
	int nLastInst = -1;
	_V100_OP_STATUS opStatus;
	
	while(1)
	{
		V100_ERROR_CODE rc = V100_Get_OP_Status(pDev, opStatus);
		if(GEN_OK != rc)
		{
			fprintf(stdout, "\nError: Call to V100_Get_OP_Status failed with code: ");
			PrintReturnCode(rc);
			fprintf(stdout, "\nError: WaitForOperationCompletion failed with code: ");
		    return ERROR_CAPTURE_INTERNAL;
		}

		switch(opStatus.nMode)
		{
		case OP_IN_PROGRESS:
			if(opStatus.eMacroCommand == CMD_ID_ENROLL_USER_RECORD)
			{
				if(opStatus.eAcqStatus != ACQ_NOOP) // Ignore the NO OP where the system is deciding if it can enroll the user
				{
					if(nLastInst != opStatus.nParameter)
						fprintf(stdout, "\nWaiting for insertion %d ", opStatus.nParameter);
				}
				nLastInst = opStatus.nParameter;
			}
			if(opStatus.eAcqStatus == ACQ_FINGER_PRESENT)
			{
				fprintf(stdout, "\nPlease lift your finger");
			}
			break;
		case OP_ERROR:
			if((_V100_OP_ERROR)opStatus.nParameter != ERROR_CAPTURE_LATENT)
			{
				fprintf(stdout, "\nError occured. Error Code: %d\n", opStatus.nParameter);
			}
			return (_V100_OP_ERROR)opStatus.nParameter;
			break;
		case OP_COMPLETE:
			fprintf(stdout, "\nOperation complete\n");
			return STATUS_OK;
		}

		fprintf(stdout, ".");
		SleepVIK(100);
		fflush(stdout);
	}
}

// Print error code
void PrintReturnCode(V100_ERROR_CODE rc)
{
	switch(rc)
	{
		case	GEN_OK: 
			fprintf(stdout,"GEN_OK");
			break;
		case	GEN_ENCRYPTION_FAIL:
			fprintf(stdout,"GEN_ENCRYPTION_FAIL");
			break;
		case	GEN_DECRYPTION_FAIL:
			fprintf(stdout, "GEN_DECRYPTION_FAIL");
			break;
		case	GET_PD_INIT_FAIL:
			fprintf(stdout,"GET_PD_INIT_FAIL");
			break;
		case	PD_HISTOGRAM_FAIL:
			fprintf(stdout,"PD_HISTOGRAM_FAIL");
			break;
		case	INVALID_ACQ_MODE:
			fprintf(stdout, "INVALID_ACQ_MODE");
			break;
		case	BURNIN_THREAD_FAIL:
			fprintf(stdout,"BURNIN_THREAD_FAIL");
			break;
		case	UPDATER_THREAD_FAIL:
			fprintf(stdout,"UPDATER_THREAD_FAIL");
			break;
		case	GEN_ERROR_START:
			fprintf(stdout, "GEN_ERROR_START");
			break;
		case	GEN_ERROR_PROCESSING:
			fprintf(stdout,"GEN_ERROR_PROCESSING,");
			break;
		case	GEN_ERROR_VERIFY:
			fprintf(stdout,"GEN_ERROR_VERIFY,");
			break;
		case	GEN_ERROR_MATCH:
			fprintf(stdout, "GEN_ERROR_MATCH");
			break;
		case	GEN_ERROR_INTERNAL: 
			fprintf(stdout,"GEN_ERROR_INTERNAL");
			break;
		case	GEN_ERROR_INVALID_CMD:
			fprintf(stdout,"GEN_ERROR_INVALID_CMD");
			break;
		case	GEN_ERROR_PARAMETER:
			fprintf(stdout, "GEN_ERROR_PARAMETER");
			break;
		case	GEN_NOT_SUPPORTED:
			fprintf(stdout,"GEN_NOT_SUPPORTED");
			break;
		case	GEN_INVALID_ARGUEMENT:
			fprintf(stdout,"GEN_INVALID_ARGUEMENT");
			break;
		case	GEN_ERROR_TIMEOUT:
			fprintf(stdout, "GEN_ERROR_TIMEOUT");
			break;
		case	GEN_ERROR_LICENSE:
			fprintf(stdout,"GEN_ERROR_LICENSE");
			break;
		case	GEN_ERROR_COMM_TIMEOUT:
			fprintf(stdout,"GEN_ERROR_COMM_TIMEOUT");
			break;
		case	GEN_FS_ERR_CD:
			fprintf(stdout,"GEN_FS_ERR_CD");
			break;
		case	GEN_FS_ERR_DELETE:
			fprintf(stdout,"GEN_FS_ERR_DELETE");
			break;
		case	GEN_FS_ERR_FIND:
			fprintf(stdout,"GEN_FS_ERR_FIND");
			break;
		case	GEN_FS_ERR_WRITE:
			fprintf(stdout,"GEN_FS_ERR_WRITE");
			break;
		case	GEN_FS_ERR_READ:
			fprintf(stdout,"GEN_FS_ERR_READ");
			break;
		case	GEN_FS_ERR_FORMAT:
			fprintf(stdout,"GEN_FS_ERR_FORMAT");
			break;
		case	GEN_ERROR_MEMORY:
			fprintf(stdout,"GEN_ERROR_MEMORY");
			break;
		case	GEN_ERROR_RECORD_NOT_FOUND:
			fprintf(stdout,"GEN_ERROR_RECORD_NOT_FOUND");
			break;
		case	GEN_VER_INVALID_RECORD_FORMAT:
			fprintf(stdout,"GEN_VER_INVALID_RECORD_FORMAT");
			break;
		case	GEN_ERROR_DB_FULL:
			fprintf(stdout,"GEN_ERROR_DB_FULL");
			break;
		case	GEN_ERROR_INVALID_SIZE:
			fprintf(stdout,"GEN_ERROR_INVALID_SIZE");
			break;
		case	GEN_ERROR_TAG_NOT_FOUND:
			fprintf(stdout,"GEN_ERROR_TAG_NOT_FOUND");
			break;
		case	GEN_ERROR_APP_BUSY:
			fprintf(stdout,"GEN_ERROR_APP_BUSY");
			break;
		case	GEN_ERROR_DEVICE_UNCONFIGURED:
			fprintf(stdout,"GEN_ERROR_DEVICE_UNCONFIGURED");
			break;
		case	GEN_ERROR_TIMEOUT_LATENT:
			fprintf(stdout,"GEN_ERROR_TIMEOUT_LATENT");
			break;
		case	GEN_ERROR_DB_NOT_LOADED:
			fprintf(stdout,"GEN_ERROR_DB_NOT_LOADED");
			break;
		case	GEN_ERROR_DB_DOESNOT_EXIST:
			fprintf(stdout,"GEN_ERROR_DB_DOESNOT_EXIST");
			break;
		case	GEN_ERROR_ENROLLMENTS_DO_NOT_MATCH:
			fprintf(stdout,"GEN_ERROR_ENROLLMENTS_DO_NOT_MATCH");
			break;
		case	GEN_ERROR_USER_NOT_FOUND:
			fprintf(stdout,"GEN_ERROR_USER_NOT_FOUND");
			break;
		case	GEN_ERROR_DB_USER_FINGERS_FULL:
			fprintf(stdout,"GEN_ERROR_DB_USER_FINGERS_FULL");
			break;
		case    GEN_ERROR_DB_USERS_FULL:
			fprintf(stdout, "GEN_ERROR_DB_USERS_FULL");
			break;
		case    GEN_ERROR_USER_EXISTS:
			fprintf(stdout, "GEN_ERROR_USER_EXISTS");
			break;
		case    GEN_ERROR_DEVICE_NOT_FOUND:
			fprintf(stdout, "GEN_ERROR_DEVICE_NOT_FOUND");
			break;
		case    GEN_ERROR_DEVICE_NOT_READY:
			fprintf(stdout, "GEN_ERROR_DEVICE_NOT_READY");
			break;
		case    GEN_ERROR_PIPE_READ:
			fprintf(stdout, "GEN_ERROR_PIPE_READ");
			break;
		case    GEN_ERROR_PIPE_WRITE:
			fprintf(stdout, "GEN_ERROR_PIPE_WRITE");
			break;
		case	GEN_LAST:
			fprintf(stdout,"GEN_LAST");
			break;
		default:
			fprintf(stdout,"UNKOWN ERROR");
			break;
	}
}

// Print error code
void PrintOPReturnCode(_V100_OP_ERROR rc)
{	
	switch(rc)
	{
		case	STATUS_OK: 
			fprintf(stdout,"STATUS_OK");
			break;
		case	ERROR_UID_EXISTS:
			fprintf(stdout,"ERROR_UID_EXISTS");
			break;
		case	ERROR_ENROLLMENT_QUALIFICATION:
			fprintf(stdout, "ERROR_ENROLLMENT_QUALIFICATION");
			break;
		case	ERROR_UID_DOES_NOT_EXIST:
			fprintf(stdout,"ERROR_UID_DOES_NOT_EXIST");
			break;
		case	ERROR_DB_FULL:
			fprintf(stdout,"ERROR_DB_FULL");
			break;
		case	ERROR_QUALIFICATION:
			fprintf(stdout, "ERROR_QUALIFICATION");
			break;
		case	ERROR_DEV_TIMEOUT:
			fprintf(stdout,"ERROR_DEV_TIMEOUT");
			break;
		case	ERROR_USER_CANCELLED:
			fprintf(stdout,"ERROR_USER_CANCELLED");
			break;
		case	ERROR_SPOOF_DETECTED:
			fprintf(stdout, "ERROR_SPOOF_DETECTED");
			break;
		case	ERROR_DB_EXISTS:
			fprintf(stdout,"ERROR_DB_EXISTS,");
			break;
		case	ERROR_DB_DOES_NOT_EXIST:
			fprintf(stdout,"ERROR_DB_DOES_NOT_EXIST,");
			break;
		case	ERROR_ID_DB_TOO_LARGE:
			fprintf(stdout, "ERROR_ID_DB_TOO_LARGE");
			break;
		case	ERROR_ID_DB_EXISTS: 
			fprintf(stdout,"ERROR_ID_DB_EXISTS");
			break;
		case	ERROR_ID_USER_EXISTS:
			fprintf(stdout,"ERROR_ID_DB_USER_EXISTS");
			break;
		case	ERROR_ID_USER_NOT_FOUND:
			fprintf(stdout, "ERROR_ID_DB_USER_NOT_FOUND");
			break;
		case	STATUS_ID_MATCH:
			fprintf(stdout,"STATUS_ID_MATCH");
			break;
		case	STATUS_ID_NO_MATCH:
			fprintf(stdout,"STATUS_ID_NO_MATCH");
			break;
		case	ERROR_ID_PARAMETER:
			fprintf(stdout, "ERROR_ID_PARAMETER");
			break;
		case	ERROR_ID_GENERAL:
			fprintf(stdout,"ERROR_ID_GENERAL");
			break;
		case	ERROR_ID_FILE:
			fprintf(stdout,"ERROR_ID_FILE");
			break;
		case	ERROR_ID_NOT_INITIALIZED:
			fprintf(stdout,"ERROR_ID_NOT_INITIALIZED");
			break;
		case	ERROR_ID_DB_FULL:
			fprintf(stdout,"ERROR_ID_DB_FULL");
			break;
		case	ERROR_ID_DB_DOESNT_EXIST:
			fprintf(stdout,"ERROR_ID_DB_DOESNT_EXIST");
			break;
		case	ERROR_ID_DB_NOT_LOADED:
			fprintf(stdout,"ERROR_ID_DB_NOT_LOADED");
			break;
		case	ERROR_ID_RECORD_NOT_FOUND:
			fprintf(stdout,"ERROR_ID_RECORD_NOT_FOUND");
			break;
		case	ERROR_ID_FS:
			fprintf(stdout,"ERROR_ID_FS");
			break;
		case	ERROR_ID_CREATE_FAIL:
			fprintf(stdout,"ERROR_ID_CREATE_FAIL");
			break;
		case	ERROR_ID_INTERNAL:
			fprintf(stdout,"ERROR_ID_INTERNAL");
			break;
		case	ERROR_ID_MEM:
			fprintf(stdout,"ERROR_ID_MEM");
			break;	
		case	STATUS_ID_USER_FOUND:
			fprintf(stdout,"STATUS_ID_USER_FOUND");
			break;
		case	STATUS_ID_USER_NOT_FOUND:
			fprintf(stdout,"STATUS_ID_USER_NOT_FOUND");
			break;
		case	ERROR_ID_USER_FINGERS_FULL:
			fprintf(stdout,"ERROR_ID_USER_FINGERS_FULL");
			break;
		case	ERROR_ID_USER_MULTI_FINGERS_NOT_FOUND:
			fprintf(stdout,"ERROR_ID_USER_MULTI_FINGERS_NOT_FOUND");
			break;
		case	ERROR_ID_USERS_FULL:
			fprintf(stdout,"ERROR_ID_USERS_FULL");
			break;
		case	ERROR_ID_OPERATION_NOT_SUPPORTED:
			fprintf(stdout,"ERROR_ID_OPERATION_NOT_SUPPORTED");
			break;
		case	ERROR_ID_NOT_ENOUGH_SPACE:
			fprintf(stdout,"ERROR_ID_NOT_ENOUGH_SPACE");
			break;
		case	ERROR_CAPTURE_TIMEOUT:
			fprintf(stdout,"ERROR_CAPTURE_TIMEOUT");
			break;
		case	ERROR_CAPTURE_LATENT:
			fprintf(stdout,"ERROR_CAPTURE_LATENT");
			break;
		case	ERROR_CAPTURE_CANCELLED:
			fprintf(stdout,"ERROR_CAPTURE_CANCELLED");
			break;
		case	ERROR_CAPTURE_INTERNAL:
			fprintf(stdout,"ERROR_CAPTURE_INTERNAL");
			break;
		case	STATUS_NO_OP:
			fprintf(stdout,"STATUS_NO_OP");
			break;
	}

}

// Print Configuration structure
void PrintConfig(_V100_INTERFACE_CONFIGURATION_TYPE *config)
{
	cout << endl << endl;
	cout << "Printing Configuration structure..." << endl;
	cout << "Device Serial Number: .............. " << config -> Device_Serial_Number << endl;
	cout << "Hardware Revision Number: .......... " << config -> Hardware_Rev << endl;
	cout << "Firmware Revision Number: .......... " << config -> Firmware_Rev << endl;
	cout << "Spoof Revision Number: ............. " << config -> Spoof_Rev << endl;
	cout << "PreProcessor Revision Number: ...... " << config -> PreProc_Rev << endl;
	cout << "Feature Extractor Revision Number: . " << config -> Feature_Extractor_Rev << endl;
	cout << "Matcher Revision Number: ........... " << config -> Matcher_Rev << endl;
	cout << "Identifier Revision Number: ........ " << config -> ID_Rev << endl;
}

// Print DB Metrics structure
void PrintDBMetrics(_MX00_DB_METRICS* dbMetrics)
{
	cout << endl << endl;
	cout << "Printing DB Metrics structure..." << endl;
	cout << "GroupID: " << dbMetrics->nGroupID << endl;
	cout << "MaxUsers: " << dbMetrics->nMaxUsers << endl;
	cout << "FingersPerUser: " << dbMetrics->nFingersPerUser << endl;
	cout << "InstancesPerFinger: " << dbMetrics->nInstancesPerFinger << endl;
	cout << "Flags: " << dbMetrics->nFlags << endl;
	cout << "CurEnrolledUserFingers: " << dbMetrics->nCurEnrolledUserFingers << endl;
}

// Multi-platform support for Sleep
void SleepVIK(int nMilliSecToSleep)
{
#ifdef _GCC
	usleep(nMilliSecToSleep*1000);
#else
	Sleep(nMilliSecToSleep);
#endif
}

// Multi-platform support for getting user input
int GetUserInput(void)
{
	int ch;
#ifdef _WIN32
	ch = getch();
#else
	struct termios oldt, newt;
	tcgetattr( STDIN_FILENO, &oldt );
	newt = oldt;
	newt.c_lflag &= ~( ICANON | ECHO );
	tcsetattr( STDIN_FILENO, TCSANOW, &newt );
	ch = getchar();
	tcsetattr( STDIN_FILENO, TCSANOW, &oldt );
#endif
	return ch;
}
