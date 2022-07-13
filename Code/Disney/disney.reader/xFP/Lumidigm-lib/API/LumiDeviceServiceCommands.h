#pragma once


typedef enum
{
	LUMI_DS_CMD_GET_NUM_SENSORS 	 = 0, 
	LUMI_DS_CMD_GET_NAMED_PIPE		 = 1, 
	LUMI_DS_CMD_GET_SENSOR_INFO		 = 2,
} LUMI_DEVICE_SERVICE_COMMANDS;


typedef struct 			
{
  LUMI_DEVICE_SERVICE_COMMANDS Command;				// The command to execute
  unsigned int                 devNumber;			// The device ID assigned by the LDS	
													// for LUMI_DS_CMD_GET_SENSOR_INFO it is the index into our device map
  char                         strCommander[256];	// A string to indicate who sent the command 
} LumiDevServiceCmdStruct;

