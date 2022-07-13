/*********************************************************************************
**
** Copyright(c) 2005 Analog Devices, Inc. All Rights Reserved.
** This software is proprietary and confidential.  By using this software you agree
** to the terms of the associated Analog Devices License Agreement.
**
**	$Id: usbcmdset.h 7472 2008-07-30 21:54:04Z cduran $
**	$Date: 2008-07-30 15:54:04 -0600 (Wed, 30 Jul 2008) $
**	$Rev: 7472 $
**	$Author: cduran $
**
*********************************************************************************/

#ifndef _USBCMD_H_
#define _USBCMD_H_

///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
// defines for both host and device
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////


typedef struct _USBCB			// USB command block
{
  unsigned int ulCommand;				// command to execute
  unsigned int ulData;					// generic data field
  unsigned int ulCount;					// number of bytes to transfer
} USBCB, *PUSBCB;


enum _VERSION_STRINGS			// version string info
{
	FW_BUILD_DATE,				// build date of firmware
	FW_BUILD_TIME,				// build time of firmware
	FW_VERSION_NUMBER,			// version number of firmware
	FW_TARGET_PROC,				// target processor of firmware
	FW_APPLICATION_NAME,		// application name of firmware
	NUM_VERSION_STRINGS			// number of version strings
};



#define	MAX_VERSION_STRING_LEN		32
#define VERSION_STRING_BLOCK_SIZE	(NUM_VERSION_STRINGS*MAX_VERSION_STRING_LEN)

#define LOOPBACK_HEADER_BYTES		4						// bytes in header of loopback data

#ifdef _WIN32_WCE
#define MAX_DATA_BYTES_EZEXTENDER	0x04000					// max bytes to send
#else
#define MAX_DATA_BYTES_EZEXTENDER	0x10000					// max bytes to send
#endif

#define MIN_DATA_BYTES_EZEXTENDER	LOOPBACK_HEADER_BYTES	// min bytes to send

#define FILE_OPEN_MODE_OFFSET		0						// byte offset for mode for file open
#define FILE_OPEN_FILENAME_OFFSET	4						// byte offset for filename for file open

#define USBIO_STDIN_FD 	0		// file descriptor for stdin on Blackfin
#define USBIO_STDOUT_FD 1		// file descriptor for stdout on Blackfin
#define USBIO_STDERR_FD 2		// file descriptor for stderr on Blackfin

///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
// defines for host only
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

#ifdef _HOSTAPP_

enum _ERROR_VALUES			// error values
{
	OPERATION_PASSED = 0,
	UNSUPPORTED_COMMAND,
	IO_WRITE_USBCB_FAILED,
	IO_READ_USBCB_FAILED,
	IO_READ_DATA_FAILED,
	IO_WRITE_DATA_FAILED,
	OUT_OF_MEMORY_ON_HOST,
	NO_AVAILABLE_FILE_PTRS,
	COULD_NOT_CONNECT,
};

#endif	// _HOSTAPP_


///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
// defines for device only
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

#ifndef _HOSTAPP_

#endif // ! _HOSTAPP_


#endif // _USBCMD_H_

