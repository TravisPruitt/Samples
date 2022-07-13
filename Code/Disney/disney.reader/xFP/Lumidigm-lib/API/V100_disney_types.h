/******************************************************************************
**
**	FILENAME
**		V100_shared_types.h
**
**	DESCRIPTION:
**		DISNEY VERSION of V100_Shared_Types.h
**
**	COMMENTS:
**		VCOM Interface data types.
**
**	REVISION INFORMATION:
**		$Id: V100_shared_types.h 15955 2011-09-28 01:49:37Z alitz $
**		$Rev: 15955 $
**		$Date: 2011-09-27 19:49:37 -0600 (Tue, 27 Sep 2011) $
**		$Author: alitz $
**	 
**	COPYRIGHT INFORMATION:	
**		This software is proprietary and confidential.  
**		By using this software you agree to the terms and conditions of the 
**		associated Lumidigm Inc. License Agreement.
**
**		Lumidigm Inc Copyright 2011 All Rights Reserved.
**
******************************************************************************/

#ifndef __V100_DISNEYTYPES__
#define __V100_DISNEYTYPES__


#define	CMD_GET_LRING_CMD			0xA5
#define	CMD_SET_LRING_CMD			0xA6
#define	CMD_SET_LRING				0xA7

#define MAX_DIAGNOSTIC_DATA_SIZE	(1024*1024*2)
#define DIAGNOSTIC_DATA_SET_FULL	5000
#define DIAGNOSTIC_DATA_SET_IMAGES	5001
#define DIAGNOSTIC_DATA_SET_CONFIG  5002

typedef unsigned short u16;		// 16 bits
typedef unsigned char  u8;		// 8 bits
/*
**  Disney
*/
typedef enum
{
     LRING_OFF			= 0x1000,                                 
     LRING_WHITE		= 0x1001,                                
     LRING_RED			= 0x1002,
     LRING_GREEN		= 0x1003,
     LRING_BLUE			= 0x1004,
     LRING_PROGRESS		= 0x2000,
     LRING_PRESET_1		= 0x2001,
     LRING_PRESET_2		= 0x2002,
     LRING_PRESET_3		= 0x2003,
     LRING_PRESET_4		= 0x2004,
     LRING_MANUAL		= 0x4000,
} LRING_Mode_Type;

typedef struct
{
     u16
		   bAmbient_Light_State,            // Enable or Over-ride Ambient Light Controller
		   iProgress_Msec,                  // Time Btw Progress Meter Updates
           iTimeOut_Msec,                   // SW Safety Time Out, TO transitions to LRING_OFF
           iSlew_On_Msec,                   // Brightness Slew On Time
           iSlew_Off_Msec,                  // Brightness Slew Off Time
           iRed_Brightness,                 // With Ambient Controller Off, Sets Target Brightness [0-max]
           iGrn_Brightness,                 // With Ambient Controller Off, Sets Target Brightness [0-max]
           iBlu_Brightness,                 // With Ambient Controller Off, Sets Target Brightness [0-max]
           iALT[8][2],                      // Ambient Light LUT [0-max][lux]     
		   bLED_STATE[12][3];               // Manaul LED On/Off State in LRING_MANUAL   
	u16    RESERVED[16];	     
} V100_LRING_API_TYPE;


typedef struct {
	u16
			sensor_ID,							//Serial # of this sensor
			sensor_FW_ID,						//Sensor FW version
			temperature_platen_local,			//Temperature at platen degrees C
			temperature_platen_remote,			//Temperature at platen degrees C
			temperature_light_ring_local,
			temperature_light_ring_remote,
			ambient_light_1,					//Ambient light sensor #1 value
			ambient_light_2,					//Ambient light sensor #2 value
			last_post_result,					//0==OK, else failure location (TBD) (NVM)
			resets_since_post;						//#of reset cycles since last CMD_RESET (NVM)
	u8		Debug_blob[64];
} _V500_DIAGNOSTIC_PACKET;

typedef struct {
 	u16
 			sensor_ID,				//Serial # of this sensor
			sensor_FW_ID,			//Sensor FW version
			/** current or last transaction **/
			image_quality,			//Image quality (0-255) from last scan
			image_area,				//Image area from last scan(non masked region
			lift_off,				//finger not present at end of acquisition
			movement,				//movement occurred during acquisition
			position;				//finger position (centroid of mask)
	
	u8		WDWTpl[2096];			//live scan template
} _V500_TRANSACTION_DATA;


#endif
