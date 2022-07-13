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

#define OPTION_SET_LRING_GAIN		0xAA

#define	CMD_GET_LRING_CMD			0xA5
#define	CMD_SET_LRING_CMD			0xA6
#define	CMD_SET_LRING				0xA7
#define	CMD_UPDATE_PIC_FIRMWARE		0xD0
#define CMD_SCRIPT_WRITE			0xD1
#define CMD_SCRIPT_READ				0xD2
#define CMD_SCRIPT_PLAY				0xD3

#define MAX_DIAGNOSTIC_DATA_SIZE	(1024*1024*2)

#define DIAGNOSTIC_DATA_SET_FULL	5000
#define DIAGNOSTIC_DATA_SET_IMAGES	5001
#define DIAGNOSTIC_DATA_SET_CONFIG  5002


#define DIAGNOSTIC_DATA_DIAG		5010
#define DIAGNOSTIC_DATA_TRANSACTION 5011

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
     LRING_BLINK_WHITE_3= 0x2002,
     LRING_SPIN_UP		= 0x2003,
     LRING_GRN_FADE		= 0x2004,
     LRING_BLU_FADE		= 0x2005,
     LRING_MANUAL		= 0x4000,
} LRING_Mode_Type;


typedef enum
{
	BRIGHTNESS_LEVEL_1 = 1,
	BRIGHTNESS_LEVEL_2,
	BRIGHTNESS_LEVEL_3,
	BRIGHTNESS_LEVEL_4,
	BRIGHTNESS_LEVEL_5,
	BRIGHTNESS_LEVEL_6,
	BRIGHTNESS_LEVEL_7,
	BRIGHTNESS_LEVEL_8
} LED_BRIGHTNESS;

typedef struct
{
	u8 red;
	u8 green;
	u8 blue;
} LED_State;


typedef struct
{
	LED_BRIGHTNESS	brightness;
	LED_State		led[12];
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
			resets_since_post,						//#of reset cycles since last CMD_RESET (NVM)
			lightring_FW_ID;					//Lightring controller FW version
	u8		Debug_blob[62];
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
