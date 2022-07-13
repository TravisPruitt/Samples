//*****************************************************************************
// pages.cpp
//
// page handlers for xTS server
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include <cstring>
#include <vector>
#include <fstream>
#include <algorithm>
// System Lib
#ifndef _WIN32
#include <arpa/inet.h>
#endif
#include "mongoose.h"
#include <json/json.h>
// Global-App
#include "test-server.h"
#include "logging.h"
#include "config.h"
// Local-ish
#include "sendHttp.h"
#include "queryGetVar.h"
#include "webServ.h"
#include "pages.h"

namespace xbr_list
{
	using namespace std;
	
	Json::Value xbrs(Json::arrayValue);

	Json::Value null;

	// looks for a specific value in xbrs, an array of json objects
	// returns the array index in which the value is found, or -1 if not found
	Json::Value & find_xbr(std::string &pairName, std::string &pairValue)
	{
		Json::ArrayIndex xbri = 0;
		logs::read("# of readers %i\n", (uint32_t) xbrs.size());
		while (xbri < xbrs.size())
		{	
			logs::read("examine reader # %i\n", (uint32_t) xbri);
			logs::read("look-for '%s':'%s'\n", pairName.c_str(), pairValue.c_str());
			logs::read("(its %s)\n", xbrs[xbri][pairName].asCString());
			if( xbrs[xbri][pairName] == pairValue )
			{
				logs::read("Found reader # %i\n", (uint32_t) xbri);
				return xbrs[xbri];
			}
			xbri++;
		}
		return null;
	}
	
	// Adds an xbr to the list of xbrs. Creates a new name for the 
	// xbr based on index of the xbr record in the xbrs array.
	Json::Value & add_xbr(Json::Value & new_xbr)
	{

		Json::StyledWriter writer;
		logs::read("Adding xBR:\n%s", writer.write(new_xbr).c_str());

		stringstream new_name;
		new_name << "xBR" << (int) xbrs.size();
		new_xbr["reader name"] = new_name.str();
		
		if(new_xbr["reader type"] == "Long Range")
			new_xbr["reader type"] = "LRR";

		string mac = new_xbr["mac"].asString();
		
		char smac[100];
		strcpy(smac, mac.c_str());
		char *s = smac;
		char *d = s;
		while (*s)
		{
			if(*s != ':')
			{
				*d = *s;
				d++;
			}
			s++;
		}
		*d = '\0';

		new_xbr["smac"] = smac;
	
		xbrs.append(new_xbr);
		Json::ArrayIndex sz = xbrs.size();
		
		logs::read("Added reader # %i, = %s\n", (uint32_t) sz, new_name.str().c_str());
		
		return new_xbr;
	}
	
	void save_names()
	{
		const char namesfile[] = "readerNames.txt";
		
		logs::read("saving names file <%s>\n", namesfile);

		std::ofstream outfile;
		outfile.open(namesfile, std::ofstream::out);
		if (outfile.fail())
		{
			ABORT("unable to open namesfile file for writing");
		}

		Json::ArrayIndex xbri = 0;
		while (xbri < xbrs.size())
		{	
			outfile << "NAME = "<< xbrs[xbri]["reader name"].asCString() << endl;
			outfile << "IP = " << xbrs[xbri]["IP"].asCString() << endl;
			outfile << "MAC = " << xbrs[xbri]["mac"].asCString() << endl;
			outfile << endl;
			xbri++;
		}
		
		outfile.close();
	}
	
	void getip(pages::MG_INFO info, char *ip_addr)
	{
		uint32_t ip = info->remote_ip;
		uint8_t ip0,ip1,ip2,ip3;
		ip0 = ip & 0xff;
		ip = ip >> 8;
		ip1 = ip & 0xff;
		ip = ip >> 8;
		ip2 = ip & 0xff;
		ip = ip >> 8;
		ip3 = ip & 0xff;
		sprintf(ip_addr, "%i.%i.%i.%i", ip3,ip2,ip1,ip0);

		logs::read("getip = %s\n", ip_addr);

	}
	
	std::string url;
	
	std::string &getUrl(Json::Value &xbr_data)
	{
		logs::read("getUrl = %s\n", xbr_data.get("IP","192.168.0.2").asCString());
		std::stringstream hurl; // barf
		// copy value as a string into array
		unsigned port = 80;
		Json::Value portVal =xbr_data["port"];
		if     (portVal.isString())	port = atoi(portVal.asString().c_str());
		else if(portVal.isInt())	port = portVal.asInt();
		else if(portVal.isUInt()) 	port = portVal.asUInt();

		hurl << "http://" << xbr_data.get("IP", "").asString() << ":" << port;
		url = hurl.str();
		return url;
	}

}

namespace pages
{
	using namespace xbr_list;
	using namespace std;
	
	// reader has requested PUT to hello page
	// 	PUT <ControllerServer>/hello
	void *put_hello	(pages::MG_CONN conn, pages::MG_INFO info)
	{
		UNUSED(info);
		logs::page("put_hello()\n");

		bool failed = false;

		Json::Value hello_data;
		cQueryGetVar query(conn, info);	// instance of the page data from the reader
		Json::Reader r;			// parser instance
		r.parse(query.payload(std::string("")), hello_data);
			
		// compute the IP address from the server info & add that to the hello_data record
		char ip_addr[16];
		xbr_list::getip(info, ip_addr);
		hello_data["IP"] = ip_addr;
		hello_data["URL"] = getUrl(hello_data);

		
		Json::StyledWriter writer;
		logs::read("hello_data = \n%s\n", writer.write(hello_data).c_str());
		
		string mac = hello_data.get("mac", "NONE").asString();

		Json::Value xbr;

		if (mac == "NONE")
		{
			hello_data["mac"] = "NONE";
			WARNING("NO mac in hello_data");
		
			// try to find an IP address instead
			string ip = hello_data.get("IP", "NONE").asString();
			string ipn = "IP";
			if (ip == "NONE")
				failed = true;
			else	
			
				xbr = find_xbr(ipn, ip);
		}
		else
		{
			string macn = "mac";
			xbr = find_xbr(macn, mac);
		}

		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n"
						"");
		
		if(failed)
			return PAGE_COMPLETE;
		
		if(xbr == null)	// Did not find a corresponding MAC code, device is new.
		{	
			xbr = add_xbr(hello_data);		
			
			Json::StyledWriter writer;
			logs::page("New xbr, = \n%s\n", writer.write(xbr).c_str());
			
			// save the names database to a file that we can look at.
			save_names();
			
		}

		// TODO: extract the interface info from the received message and use it to set the stream address.
		
		// tell the xbr it's name (PUT name).
		sendHttp::setReaderName(xbr);

		// tell the xbr the time.
		sendHttp::setReaderTime(xbr);

		// tell the xFP that the mode is normal
		//   sendHttp::modeAssign(xbr);

		// tell the xbr to send data (PUT / DELETE stream).
		if(reader::streamSet)
			sendHttp::streamAssign(xbr);
		else
			sendHttp::streamDelete(xbr);
			
		return PAGE_COMPLETE;
	}		
}
