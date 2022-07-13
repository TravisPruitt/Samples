//*****************************************************************************
// pages.h
//
// page handlers for xTS server
//*****************************************************************************
//
//	Written by Mike Wilson
//	Copyright 2011, Synapse.com
//
//*****************************************************************************


namespace xbr_list
{
	using namespace std;

	// extern Json::Value xbrs(Json::arrayValue);
	// extern Json::Value null;
	extern Json::Value & find_xbr(std::string &pairName, std::string &pairValue);
	extern Json::Value & add_xbr(Json::Value & new_xbr);
	extern void save_names();
	extern void getip(pages::MG_INFO info, char *ip_addr);
	// extern std::string url;
	extern std::string &getUrl(Json::Value &xbr_data);

}
