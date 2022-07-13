//*****************************************************************************
// sysIface.h 
//
//	System Interface definitions.
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************
#ifndef __SYSIFACE_H
#define __SYSIFACE_H

#include <string>

namespace sysIface
{
	extern const char *getLinuxVersion(void);
	extern const char *getMAC(void);
	extern std::string & load_xbrc_path(void);
}

#endif // __SYSIFACE_H
