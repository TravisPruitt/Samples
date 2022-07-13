//*****************************************************************************
// sendHttp.h
//
// Test Server - Send routines for  information to xBR devices
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

#ifndef __incl_send_h

	#define __incl_send_h

	#include <string>
    #include <vector>

	namespace sendHttp	
	{
		using std::string;
		
		bool setReaderName(Json::Value &xbr);
		bool setReaderTime(Json::Value &xbr);
		bool streamAssign(Json::Value &xbr);
		bool streamDelete(Json::Value &xbr);
		// bool modeAssign(Json::Value &xbr);
		bool reset(Json::Value &xbr);
		bool reboot(Json::Value &xbr);
		bool beacon(Json::Value& xbr, std::vector<std::string>& bands, std::string& command);
		bool commandBands(Json::Value& xbr, std::vector<std::string>& bands, std::string& command, int signalStrength);
		bool deleteBands(Json::Value &xbr);
	}

#endif // __incl_send_h
