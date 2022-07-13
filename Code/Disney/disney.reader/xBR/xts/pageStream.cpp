//*****************************************************************************
// pageStream.cpp
//
// pages handlers for xBand Reader: xTS test server - stream input handler
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
#include <string>
#include <time.h>
// System Lib
#include "mongoose.h"
#include <json/json.h>
// Global-App
#include "test-server.h"
#include "logging.h"
#include "config.h"
// Local-ish
#include "queryGetVar.h"
#include "webServ.h"
#include "pages.h"

namespace
{
using namespace std;

class BANDD
{
	public:
		BANDD()		{timestamp = "";name = ""; eyed = "";}
		~BANDD() 	{}

		void 		getEvent(Json::Value &event);	// parse event data into class members
		bool 		filterKeep(BANDD &bandData);	// decide if this record should be filtered out of report.
		bool 		calcDeltas(void);				// calc differences since last record of this band
		char * 		eventString(char *outString);	// generate an event as a string

	public:
		uint64_t	t;
		uint64_t	d_t;	// delta in t
		string		timestamp;
		string 		type;
		string 		name;
		string 		eyed;
		int 		nope;		// ping #, from band
		int			noee;		// event #, from xBR
		int			last_noee;
		int 		d_nope;
		int 		ss;
		int 		freq;
		int			freqId;	// 0-3, frequencyIndex
		int			d_freqId;
		int 		chan;
		int			d_chan;
		int			debug;
		int			wkup;

	};

	typedef std::vector<BANDD> BIK;
	typedef std::vector<BANDD>::iterator BIKI;
	BIK bands_I_know;

	bool bds(BANDD i, BANDD j)
	{
		return (	i.noee	 <	j.noee   );
	}

	bool BANDD::filterKeep(BANDD &bandData)
	{
		Json::Value filter = config::value["filter"];
		if(!filter.isNull() && filter.size())
		{
			for ( unsigned index = 0; index < filter.size(); ++index )  // Iterates over the sequence elements.
			{
                if (strcasecmp(bandData.eyed.c_str(), filter[index].asString().c_str()) == 0)
					return true;
			}
			return false;
		}
		return true;	// no filter specified
	}

	void BANDD::getEvent(Json::Value &event)
	{
		// convert time into mS
		uint32_t timeInSecondsSinceEpoch = 0;
		uint32_t millisec = 0;
		// get the string
		string stime = event.get("time","").asString();
		const char *tm_str = stime.c_str();

#ifndef _WIN32
		// convert the string to broken-down-time
		struct tm Time;		// broken down time
		char *decp = strptime(tm_str, "%Y-%m-%dT%H:%M:%S", &Time);

		// the milliseconds will be left at the end of the string
		if(decp != NULL)
			sscanf(decp, ".%3u", &millisec);

		// convert broken down time into seconds
		time_t tt = mktime(&Time);

		// our output
		timeInSecondsSinceEpoch = tt;

		t 			= timeInSecondsSinceEpoch * 1000 + millisec;
#else
        t = 0;
#endif
		// type 	= R.get("reader type","?").asString();
		// name 	= R.get("reader name","?").asString();
		eyed 		= event.get("XLRID","?").asString();
		nope		= event.get("pno",-1).asInt();
		noee		= event.get("eno",-1).asInt();
		ss 			= event.get("ss",-1).asInt();
		freq		= event.get("freq",0).asInt();
		debug		= event.get("dbg",0).asInt();
		wkup		= event.get("wkup",0).asInt();
		chan		= event.get("chan",-1).asInt();
		timestamp 	= event["time"].asString().substr(5).replace(2,1,"/").replace(5,1,"-");
	}

	bool BANDD::calcDeltas(void)
	{
		// check to see if this band ID is in the list of "bands_I_know"
		BIKI bi;
		bool found = false;
		for(bi = bands_I_know.begin(); bi < bands_I_know.end(); bi++)
		{
			if( bi->eyed == eyed)	// already in database
			{
				// calc deltas and place them in orderedEvents.
				last_noee	= bi->noee;
				d_t 		= t - bi->t;
				freqId 		= (freq - 2401) / 23;
				d_nope 		= ((nope - bi->nope) + 256) % 256;
				d_freqId 	= ((freqId - bi->freqId) + 4)%4;
				d_chan 		= chan - bi->chan;
				*bi 		= *this;
				found 		= true;
				break;
			}
		}

		if(!found)	// add to database
		{
			d_t			= 0;
			freqId		= (freq - 2401) / 23;
			d_nope		= 0;
			d_freqId 	= 0;
			d_chan 		= 0;
			bands_I_know.push_back(*this);
		}
		return found;
	}

	char *BANDD::eventString(char *outString)
	{
		char *p = outString;
		p += sprintf(p, "%5i, ", 		noee);
		p += sprintf(p, "%s, ", 		eyed.c_str());
		p += sprintf(p, "%-18.18s",		timestamp.c_str());
		p += sprintf(p, "(%06li), ",	d_t);
		p += sprintf(p, "%03i(%03i), ", nope, d_nope);
		p += sprintf(p, "%i, ", 		ss);
		p += sprintf(p, "%i(%i), ", 	freqId, d_freqId);
		p += sprintf(p, "%i, ", 		chan);
		p += sprintf(p, "%i, %02xh",	wkup, debug);
		return p;
	}

}

namespace pages
{

	void *put_stream(pages::MG_CONN conn, pages::MG_INFO info)
	{
		logs::page("put_stream()\n");

		Json::Value stream_data;
		cQueryGetVar query(conn, info);	// instance of the page data from the reader
		Json::Reader r;			// parser instance
//        printf("payload = '%s'\n", query.payload(std::string("")).c_str());
		r.parse(query.payload(std::string("")), stream_data);
		stream_data["URL"] = xbr_list::getUrl(stream_data);

        if (!stream_data.isMember("events"))
        {
            logs::info("no events\n");
            return PAGE_COMPLETE;
        }

        Json::Value events = stream_data["events"];
        if (!events.isArray())
        {
            logs::info("invalid json payload, 'events' is not an array\n");
            return PAGE_COMPLETE;
        }
		uint32_t eventCount = (uint32_t) events.size();

		// compute the IP address from the server info
		// and find the reader name for that IP.
		char ip_addr[16];
		xbr_list::getip(info, ip_addr);
		std::string ip(ip_addr);
		string name("IP");
		Json::Value xBR = xbr_list::find_xbr(name, ip);

		BIK orderedEvents;
		BIKI fx = orderedEvents.begin();

		for (uint32_t x =0; x < eventCount; x++)
		{
			Json::Value event = events[x];
			BANDD bd;
			bd.getEvent(event);

			if(bd.filterKeep(bd))
				orderedEvents.push_back(bd);
		}

		// order the received packets in chronological order
		std::sort(orderedEvents.begin(),orderedEvents.end(), bds);

		for (BIKI i = orderedEvents.begin(); i < orderedEvents.end(); i++)
		{
			i->calcDeltas();

			char outString[512];
			char *p = i->eventString(outString);	// p points to terminator of outString
			logs::evnt(DATA "%s\n", outString);

			p += sprintf(p, "%5i, ", 		i->noee);
			p += sprintf(p, "%s, ", 		i->eyed.c_str());
			p += sprintf(p, "%-18.18s",		i->timestamp.c_str());

		}

		mg_printf(conn, "HTTP/1.1 200 OK\r\n" "Content-Type: text/plain\r\n\r\n"
						"");
		return PAGE_COMPLETE;
	}

} // namespace pages
