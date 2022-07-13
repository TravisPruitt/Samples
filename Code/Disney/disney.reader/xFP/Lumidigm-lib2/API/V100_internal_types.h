#ifndef __V100_INTERNAL_TYPES
#define __V100_INTERNAL_TYPES


#ifdef _VDSP
#pragma diag(suppress: 301) 
#endif

typedef unsigned char  uchar;
typedef unsigned short ushort;
typedef unsigned int   uint;


typedef struct
{
	uint	fw_id;
	uint	dev_id;
	uint    diag_level;	
	uint	diag_version_num;
	uint    nImageBlobSize;
} DiagHeader;

typedef enum
{	
	M300_Default	  = 0x0000,		// Application code
	M300_BootManager  = 0x1000,		// Boot Manager
	M300_Diagnostic   = 0x2000,		// ECU_Test Diagnostics
	M300_Application  = 0x3000,		// Application Code
	M300_Image		  = 0x4000		// Image File
} Merc_Flash_Loc;

typedef struct
{
	uint
	/* System spec information */
	System_Heap_Peak,		
	Algo_Heap_Peak,
	Topmain_Stack_Peak,
	ACQ_Stack_Peak,
	USB_Stack_Peak,
	BIT_Stack_Peak,
	PD_Stack_Peak,
	updater_Stack_Peak,
	Burnin_Stack_Peak,
	UART_Synch_Stack_Peak,
	Process_Stack_Peak,
	Mem_0_Alloc,
	Mem_1_Alloc,
	Mem_2_Alloc,
	Mem_3_Alloc;
	/* Timing */
	short PDTime;
	short AcqTime;
	short FF_CC_Time;
	short PreProcTime;
	short ExtractionTime;
	short MatchingTime;
	/* RS-232 Issues*/
	uint  Reserved_0;
	uint  Reserved_1;
	uint  Reserved_2;
	uint  Reserved_3;
} _V100_SYSTEM_DIAGNOSTICS;

/*
**	Mercury EEPROM
**  
*/

typedef struct
{
	unsigned int Product_ID,
		Serial_Number,
		Revision_Code,
		Product_Reserved;
	unsigned short
		DateCode,						
		Imager_ID,
		Imager_VER,
		Imager_MAN_Code_MSB,
		Imager_MAN_Code_LSB,
		Optical_Boresight_Row,		
		Optical_Boresight_Col,		
		DPI,
		Distortion_Coeff_0,
		Distortion_Coeff_1,
		Distortion_Coeff_2,
		Distortion_Coeff_3,
		PD_Brow,		
		PD_Bcol,
		PD_Top_Offset,
	 	PD_Bot_Offset,
	 	PD_Seed_Exp_MSB,
	 	PD_Seed_Exp_LSB,
	 	PD_Seed_Gain,
	 	Latent_TL_X,
		Latent_TL_Y,
		Latent_BR_X,
		Latent_BR_Y,
		RESERVED4,
		RESERVED5,
		RESERVED6,
		RESERVED7;
} _MX00_EEPROM_DATA;


/*
**	History Hooks Structure
**  This provides history diagnostics of key metrics
*/
typedef struct
{
    unsigned short
    	Num_Acq_Attempts,	// MUST be First Entry
    	
    	// SHARED TIME
    	PD_Stop,			// PD latency
    	SCALE_Stop,			// Scale & Correct latency
    	ACQ_Stop,			// ACQ latency
    	PROC_Stop,			// PROC latency
    	EXTRACT_Stop,		// Extractor latency
    	MATCH_Stop,			// Matcher latency
    	TOTAL_Stop,			// TOTAL latency
    	
    	// BLUE ONLY
    	Blue_Mask_Stop,		// Mask Creatation latency
    	Blue_MaskRatio,
    	Blue_MaskValid,
    	Blue_MaskPosValid,
    	Blue_MaskHistoValid,
    	Blue_MaskMAE,
    	Blue_MaskTime,
    	Blue_Mask_NSat,
    	Blue_Mask_Size,
    	
    	// WHITE ONLY
    	White_Color_Left,
		White_Color_Right, 
    	White_Present_Left,
    	White_Present_Right,
    	White_Hist_Left,
    	White_Hist_Right,
    	White_Exposure,
    	White_Centroid,
    	
    	// SHARED
    	Finger_Quality,
    	Exp1,
    	Exp2,
    	Exp3,
    	Exp4,
    	Exp1_Dark,
    	Exp2_Dark,
    	Exp3_Dark,
    	PD_Exp,
    	Color_Status,
    	Hist_Status,
    	Left_RMean,
    	Left_NSat,
    	Right_RMean,
    	Right_NSat,
    	BPwr1,
    	BPwr2,
    	BPwr3,
    	BPwr4,
    	Cb,
    	Cr,
    	Y,
    	MAE,
    	Cxy,
    	ErrorCode,
    	
    	// LATENTS
    	bColorCheck,
    	bIllumCheck,
    	bFreqsCheck,
    	ColorScore,
    	IllumScore,
    	FreqScore,
    	ColorEtime,
    	IllumEtime,
    	FreqEtime,
    	
    	// END MARK
    	End_Mark;	
} History_Type;

typedef enum
{
  	VID_LED_1_ON   = 0x1000,
	VID_LED_2_ON   = 0x1001,
	VID_LED_3_ON   = 0x1002,
	VID_TIR_ON	   = 0x1003,
	VID_DARK_ON    = 0x1004,

} _V100_INTERNAL_VID_STREAM_MODE;

typedef enum
{
	MS_LED_PD_IR			= 2000,				// IR PD
    MS_LED_PD,		
	MS_LED_OFF,
	MS_LED_NONP_1_GREEN,						// Green Only
	MS_LED_NONP_1_BLUE,							// Blue Only
	MS_LED_NONP_2_GREEN,						// Green Only
	MS_LED_NONP_2_BLUE,							// Blue Only
	MS_LED_VIDEO_DISPLAY,
	MS_LED_EXT_NONE,
	MS_MERC_STATE_1			= 2009,		
	MS_MERC_STATE_2			= 2010,
} _V100_INTERNAL_LED_TYPE;

typedef enum 
{
	/**** START ****/	
	CMD_GET_CAL				 = 0x4B,		// INTERNAL get Calibration Struct
	CMD_SET_CAL				 = 0x4E,		// INTERNAL set Calibration Struct
	CMD_SET_RECORD			 = 0x4F,		// INTERNAL Sends "Record" from Host->Device
	CMD_WRITE_FLASH			 = 0x52,		// Trigger Flash Update
	CMD_GET_RECORD			 = 0x53,		// INTERNAL Device->Xfer of "Record"
	CMD_DIAG_NONE			 = 0x90,		
	CMD_GET_SYSTEM_STATE	 = 0x91,		// Get System State
	CMD_SET_LICENSE_KEY		 = 0x92,		// Set License Key
	CMD_GET_LICENSE_KEY		 = 0x93,
	CMD_WRITE_FILE			 = 0x94,
	CMD_READ_FILE			 = 0x95,
	CMD_GET_SPOOF_DETAILS	 = 0x96,
	CMD_GET_SPOOF_DETAILS_V2 = 0x97,
	CMD_GET_EEPROM			 = 0x98,
	CMD_SET_EEPROM			 = 0x99,
	/**** FILE MANAGEMENT ****/
	CMD_FILE_READ			=	0xB0,
	CMD_FILE_WRITE			=	0xB1,
	CMD_FILE_CD				=	0xB2,
	CMD_FILE_GETCWD			=   0xB3,
	CMD_FILE_GET_ATTRIBUTES =	0xB4,
//	CMD_FILE_DELETE			=	0xB5,  // Moved to Shared Types
	CMD_FILE_DIR_FINDFIRST  =	0xB6,
	CMD_FILE_DIR_FINDNEXT   =   0xB7,
	CMD_FORMAT_VOLUME		=	0xB8,
	/**** DIAGNOSTIC COMMANDS ****/
	CMD_LOOPBACK 		= 0xC0,		// Echo Back Packets
	CMD_BIT				= 0xC3,		// Run Built-In Test
	CMD_TEST			= 0xC4,		// Test Hook
	CMD_START_BURNIN	= 0xC5,		// Start Device Burn In Cycle
	CMD_STOP_BURNIN		= 0xC6,		// Stop Device Burn In Cycle
	CMD_QUERY_RECORD_ADDRESS	= 0x50,		// Return Base Address of Buffer
	CMD_QUERY_RECORD_SIZE		= 0x51,		// Return Size of Buffer
	CMD_QUERY_SUPPORT 			= 0x40,		// query for support
	CMD_BURN_CAL_IMAGES			= 0xC7,
} _V100_INTERNAL_COMMAND_SET;
/******************************************************************************
**
**	Extra Memory Locations to gather via Get/Set_Image 
**	
**	
**
******************************************************************************/
typedef enum
{
    IMAGE_EX_NONE	= 0x1000,	// not available
    SCALE_IMAGE_1,				// 1
	SCALE_IMAGE_2,				// 2
	SCALE_IMAGE_3,				// 3
	SYSTEM_MEMORY_MAP,			// Generated dynamically
	ALGO_MEMORY_MAP,			// Generated dynamically
	PD_HISTORY,					// PD History
	LOGGER_XML,					// 8
	DIAG_DATA_5,				// 9	
	DIAG_DATA_6,				// 10
	DIAG_DATA_7,				// 11
	DIAG_DATA_8,				// 12
	DIAG_DATA_9,				// 13
	DIAG_DATA_10,				// 14
	MEMORY_MAP_1,				// 15	0x100F
	MEMORY_MAP_2,				// 16	0x1010
	MEMORY_MAP_3,				// 17	0x1011
	MEMORY_MAP_4,				// 18	0x1012
} _V100_IMAGE_TYPE_EX;

/******************************************************************************
**
**	Policy Structure 
**	
**	READ/WRITE 
**
******************************************************************************/

typedef struct 
{
	unsigned short
	// Raw Images
	Get_Raw_Image,
	Set_Raw_Image,
	Get_Scaled_Image,
	Set_Scaled_Image,
	Get_Composite_Image,
	Set_Composite_Image,
	Get_Template,
	Set_Template,
	Get_Cal,
	Set_Cal,
	Get_Config,
	Set_Config,
	Get_Cmd,
	Set_Cmd,
	Set_Record,
	Get_Record,
	Write_Flash,
	Set_LED,
	Set_License_Key,
	Get_License_Key,
	Set_Enrollment_Rules,
	Calculate_Match,
	Calculate_Extract,
	Calculate_Identify,
	Calculate_Verify,
	Set_Spoof_Info,
	Calculate_Spoof_1,
	Calculate_Spoof_2,
	Calculate_Spoof_3,
	System_Diagnostics,
	Vid_Stream;
	unsigned short
	RESERVED_0,
	RESERVED_1,
	RESERVED_2,
	RESERVED_3,
	RESERVED_4,
	RESERVED_5,
	RESERVED_6,
	RESERVED_7,
	RESERVED_8,
	RESERVED_9;
} _V100_DEVICE_POLICY;

typedef enum 
{
	OPTION_SET_BORESITE  			= 0x1000,
	OPTION_SET_AGC_DEFAULT 			= 0x2000,
	OPTION_SET_AGC_MANUFACTURING	= 0x4000,
	OPTION_SET_LATENT_DETECTION		= 0x1001,
	OPTION_BURN_CAL_IMAGES			= 0x1002,
	OPTION_SET_FINGER_SAMPLING_MODE = 0x1004,
	OPTION_SAVE_DEBUG_IMAGES		= 0x1008,
	OPTION_SET_FINGER_LIFT_MODE		= 0x2001,
	OPTION_SET_CAL_DATA				= 0x1010,
	OPTION_SET_DPI					= 0x1011,		
	OPTION_SAVE_BORESITE			= 0x1012,		
	OPTION_SET_MFG_STATE			= 0x1013,
	OPTION_SET_FLUSH_QUEUE			= 0x06, // this is here to make sure we don't use this enum for something else. Its already used in logging firmware 16048 for mercury
} _V100_INTERNAL_OPTION_TYPE;

/*
**	Types of finger samplings available
**	Selected using CMD_SET_OPTION
*/

typedef enum
{
	FINGER_SAMPLING_NONE			= 0x0000,			
	FINGER_SAMPLING_LOW	   			= 0x0001, // low frames per sec
	FINGER_SAMPLING_HIGH			= 0x0002, // high frames per sec
	FINGER_SAMPLING_DYNAMIC			= 0x0003, // Default mode
} _V100_FINGER_SAMPLING_MODE;	

/* 
** Types of finger lift modes available	
**	Selected using CMD_SET_OPTION
*/

typedef enum
{
	FINGER_LIFT_NONE 	   		= 0x0000,
	FORCE_FINGER_LIFT_ON  		= 0x0001, // Default mode. User required to lift finger after acquisition
	FORCE_FINGER_LIFT_OFF		= 0x0002,
} _V100_FINGER_LIFT_MODE;	

/******************************************************************************
**
**	Calibration Structure
**	Platform Specific Information Stored in User FLASH 
**
******************************************************************************/
typedef struct
{
	unsigned short  
		Device_Serial_Number,		// Unique Device Serial Number
	 	Hardware_Rev,				// HW Revision Number 			(xxx.xxx.xxx)
 		Boresight_X,				// Imager Window Readout Offset
 		Boresight_Y,				// Imager Window Readout Offset
		Struct_Size,				// Size in Bytes of This Structure
		DPI,						// DPI
		Baud_Rate,					// Initial Baud Rate
		Flow_Control,				// Flow Control
		COM2_Mode,					// COM2 - Mode
		Override_Trigger,			// Override_Trigger
		Override_Heartbeat,			// Override_Heartbeat
		PD_Level,					// Presense Detect Level
		Policy_Key,					// Policy Key (1 = All, 2 = Gen, 3 = Image)
		Processing_Parameter1,		// Processing Paramter 1
		Processing_Parameter2,		// Processing Paramter 2
		Processing_Parameter3,		// Processing Paramter 3
		Processing_Parameter4,		// Processing Paramter 4
		Processing_Parameter5,		// Processing Paramter 5
		Processing_Parameter6,		// Processing Paramter 6
		Processing_Parameter7,		// Processing Paramter 7
		Processing_Parameter8,		// Processing Paramter 8
		Processing_Parameter9,		// Processing Paramter 9
 		RESERVED_0,					// Reserve 8 DWords 
 		RESERVED_1,
 		RESERVED_2,
 		RESERVED_3,
 		RESERVED_4,
 		RESERVED_5,
 		RESERVED_6,
 		RESERVED_7,
 		RESERVED_8,
 		RESERVED_9,
 		RESERVED_10,
 		RESERVED_11,
 		RESERVED_12,
 		RESERVED_13,
 		RESERVED_14,
 		RESERVED_15,
 		RESERVED_16,
		Burnin_Mode;
} _V100_INTERFACE_CALIBRATION_HDR;


typedef unsigned char _V100_CALIBRATION_DATA[4096];		// Calibration Coeffs

typedef struct
{
    
 	_V100_INTERFACE_CALIBRATION_HDR 
 		Hdr;
    _V100_CALIBRATION_DATA 
    	Data;
    
} _V100_INTERFACE_CALIBRATION_TYPE;


/******************************************************************************
**
**	LEGACY Calibration Structure
**	
**
******************************************************************************/
typedef struct
{
	unsigned short  
		Device_Serial_Number,		// Unique Device Serial Number
	 	Hardware_Rev,				// HW Revision Number 			(xxx.xxx.xxx)
 		Boresight_X,				// Imager Window Readout Offset
 		Boresight_Y,				// Imager Window Readout Offset
		Struct_Size,				// Size in Bytes of This Structure
 		RESERVED_0,
 		RESERVED_1,
 		RESERVED_2;
 		
} _V100_INTERFACE_CALIBRATION_HDR_REV_0;

typedef struct
{
    
 	_V100_INTERFACE_CALIBRATION_HDR_REV_0 Hdr;
    _V100_CALIBRATION_DATA  Data;
    
} _V100_INTERFACE_CALIBRATION_TYPE_REV_0;


typedef enum
{
    ePOLICY_ALL     = 0x01,
    ePOLICY_LEVEL_1 = 0x02,
    ePOLICY_LEVEL_2 = 0x03,
    ePOLICY_LEVEL_3 = 0x04,
} _V100_DEVICE_POLICY_ENUM;    


typedef enum
{
    FLASH_ADDRESS_CONFIGURATION = 0x2000000,
} _V100_FLASH_LOCATION;

typedef struct
{
	uint nSOH;
    uint nFlashAddress;
    uint nFileSize;
	uint lTime;
} _V100_FILE_HEADER;

typedef struct
{ 
  uint nStartOfFrame;              	//0x00C0FFEE
  uint nSizeOfCalData;
  uint nLocPatch[3];                // x, y, l
  uchar SpoofCalData[48];
} _V100_SPOOF_CAL;


// One of each of these per first 3 planes.

// The "C" structure has a struct in a struct for portablity.
// The MATLAB structure is just for the wrapper
#ifdef _WIN32
#pragma pack(push)
#pragma pack(1)
#endif

typedef struct
{
    _V100_SPOOF_CAL spoofCalStruct;
    ushort 			nGPSums[12];
    long			nSumX[12];
    long long		nSumX2[12];
    uint			nNsum;
    //
} _V100_SPOOF_METRICS;

typedef struct
{
    _V100_SPOOF_CAL spoofCalStruct;
    ushort 			nGPSums[12];
    long			nSumX[48];
    long long		nSumX2[48];
    uint			nNsum[4];
    //
} _V100_SPOOF_METRICS_V2;

typedef struct
{
   	uint			m_pStartMemPool;
	uint			m_pEndMemPool;
	uint			m_pCurMemStart;
	uint			m_pFirstMemoryNode;
	uint			m_nMemPoolLength;
	uint			m_nCurrentlyAllocated;
	uint			m_nPeakAllocation;
	uint			m_bInitialized;
} _V100_MEMORY_METRICS_HDR;

typedef struct
{
	uint	nSize;
	uint	pNext;
} _V100_MEMORY_METRICS_NODE;

// Moved to shared types
//typedef enum
//{
// 	FS_FILE_TYPE_UNKNOWN = 0x00,
// 	FS_FILE_TYPE_FILE	  = 0x01,
// 	FS_FILE_TYPE_DIR     = 0x02,
//} _V100_FILE_ATTR;


#ifdef _WIN32
#pragma pack(pop)
#endif



// Moved to shared types
//typedef struct 
//{
//	uint		FileSize;		//  [In/Out] Size of File	
//	uint		TimeStamp;		//  [In/Out] (optional) Time stamp.
//	uint		Permissions;	//  [In/Out] (optional) Permissions
//	uint		FileType;		//  [In/Out] FileType (0 = ASCII, 1=BINARY)
//	uint		Hash;			//  [Out]	   Hash Code
//    _V100_FILE_ATTR    Attribute;		//  [Out]    Is File?  Is Dir?
//} _V100_FILE;


typedef struct 
{
	uint NumFreeSpace;
	uint NumUsedSpace;
	uint NumFiles;
	uint NumDirectories;
} _V100_FILE_SYSTEM_ATTRIBUTE;


// The MATLAB structure is just for the wrapper

// Each Record consists of one _V100_USER_RECORD followed by
// (nSizeOpaque/sizeof(_V100_USER_RECORD_ENTRY)) _V100_USER_RECORD_ENTRYs, followed by 
// nSizeMetaData MetaData.



typedef struct
{
    long			nSumX[12];			// 48 bytes
    long long		nSumX2[12];			// 96 bytes
    uint			nNsum;				// 4 bytes
    ushort 			nGPSums[12];		// 24 bytes 	
} _V100_SPOOF_METRICS_MATLAB;

/*
**  DEFINITIONS
*/
#define MAX_FILE_NAME_LENGTH			64
#define OFFSET_BYTES_SPOOF_CAL_DATA		1260
#define MAX_FILE_SIZE					(1024*1024)
#define MAX_USER_DATA_SIZE				256

extern "C" _V100_DEVICE_POLICY POLICY_ALL;
extern "C" _V100_DEVICE_POLICY POLICY_LEVEL_1;
extern "C" _V100_DEVICE_POLICY POLICY_LEVEL_2;
extern "C" _V100_DEVICE_POLICY POLICY_LEVEL_3;


#endif
