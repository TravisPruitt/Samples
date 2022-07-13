//*****************************************************************************
// sysIface.cpp 
//
//	utility to interact with the operating system
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************
//

// C/C++ Std Lib
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <cstring>
#include <cstdio>
#include <cstdlib>

// System Lib
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>

// Global-App
#include "log.h"
#include "sysIface.h"
// Local-ish

namespace sysIface
{
	// DHCP client is set up to retrieve the xBRC url, and it
	// places this url in the file "/var/cache/xbrc-url".
	// it is the only content of the file.
	std::string & load_xbrc_path(void)
	{
		static bool warn_once = false;
		static std::string xbrc;
		const char xbrc_file_path[] = "/var/lib/dhcp/xbrc-url";
		const char xbrc_default_url[] = "http://192.168.0.2:8080";
		
		// Read in _values saved in the config file
		LOG_DEBUG("load_xbrc_path(\"%s\")\n", xbrc_file_path);

		std::ifstream infile;
		infile.open(xbrc_file_path, std::ifstream::in);
		
		if (infile.fail())
		{
			xbrc = std::string(xbrc_default_url);
			{
				if(!warn_once)
					LOG_ERROR("unable to open file");
				warn_once = true;
			}
		}
		else
			warn_once = false;
			
		std::getline(infile, xbrc);
		
		// if not an emty string, see if last char is '/', if so remove it.
		if(xbrc.size() != 0 && xbrc.at(xbrc.size()-1) == '/')
			xbrc.resize(xbrc.size() - 1);

		// TODO: decide if we need to prepend http://   - make configurable to https, etc.
		
		infile.close();
		
		return xbrc;		
	}

	const char *getMAC(void)
	{
		int s;
		struct ifreq buffer;
		s = socket(PF_INET, SOCK_DGRAM, 0);

		memset(&buffer, 0x00, sizeof(buffer));
		strcpy(buffer.ifr_name, "eth0");

		ioctl(s, SIOCGIFHWADDR, &buffer);
		close(s);

		static char output[20];
		char *p = output;
		for( s = 0; s < 6; s++ )
		{
			p += sprintf(p, "%.2X:", (unsigned char)buffer.ifr_hwaddr.sa_data[s]);
		}
		*(--p) = '\0'; // erase the last ':'.
		return output;
	}

	const char *getLinuxVersion(void)
	{
		FILE *fIn = popen("uname -a", "r");
		if (!fIn) { return ""; }
		static char buffer[256];
		fgets(buffer, sizeof(buffer), fIn);
		pclose(fIn);
		return buffer;
	}

} // namespace
