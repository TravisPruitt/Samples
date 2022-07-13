//*****************************************************************************
// command.cpp
//
//	xBand test-server command line reading
//*****************************************************************************
//
//	Written by Mike Wilson 
//	May 2012, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include <string.h>
#include <string>
#include <cstdio>
#include <vector>

// System Lib
#include <sys/stat.h>
#include <fcntl.h>
#include <json/json.h>
#ifdef _WIN32
#include <io.h>
#endif

// Global-App
#include "test-server.h"
#include "sendHttp.h"
#include "logging.h"
#include "config.h"
#include "webServ.h"
#include "Console.h"

// Local-ish


static int _signalStrength = -127;
static std::vector<std::string> _bands;
static std::string _command;

namespace
{
	void help ()
	{	logs::prompt(
		"\n"
		"delete\n"
		"    stop sending to bands\n"
		"\n"
		"stream on | off [[<option> <value>] ...]\n"
		"  set xBRs output stream on or off\n"
		"  OPTIONS - \n"
		"   max			= maximum # of events to send\n"
		"   msec    	= frequency of /stream messages to send\n"
		"   ss      	= minimum signal stregth to send\n"
		"\n"
		"config\n"
		"    reload server.conf settings and apply them.\n"
		"\n"
		"filter [<band_id>[...]]\n"
		"    only report the band / bands listed. No parameter = remove filter\n"
		"\n"
		"reboot\n"
		"    Reboot the xBR (restart linux from scratch)\n"
		"\n"
		"setbands <band_id> [<band_id> ...]\n"
		"    Set a list of bands xBR will 'reply' or 'broadcast' to.\n"
		"    band_id     = ALL|hex  - ALL = all bands. hex = 6 bytes, no spaces, (example: FE08EE7A05)\n"
		"\n"
		"broadcast <command> [options]\n"
		"reply <command> [options]\n"
		"   commands: Send command to a band, using broadcast (beacon) or reply method.\n"
		"	  fast-ping <sec> <msec> <duration>  cmd = 0x03\n"
		"	  slow-ping <sec> <msec> <duration>  cmd = 0x02\n"
		"	  fast-rcv <sec> <msec> <duration>   cmd = 0x01\n"
		"     power <level> <duration>           cmd = 0x04\n"
		"     hwver		                         cmd = 0x06\n"
		"     fwver			                     cmd = 0x07\n"
		"     carrieron		                     cmd = 0x08\n"
		"     carrieroff		                 cmd = 0x09\n"
		"     command10		                     cmd = 0x0A\n"
		"     command11		                     cmd = 0x0B\n"
		"        sec msec = seconds & milliseconds interval\n"
		"        duration = seconds until setting ends\n"
		"        level    = 0 -18dbm | 1 -12dbm | 2 -6dbm | 3 0dbm\n"
		"        duration = seconds until setting ends\n"
		"\n"
		"makehex <hex(10)|ascii(5)> create a hex file (named as the parameter)\n"
		"\n"
		);
	}

	bool usage (const char *f, ...)
	{
		va_list a;
		va_start(a,f);
		vprintf(f, a);
		logs::prompt(
			"Commands:\n"
			"    help\n"
			"    delete\n"
			"    stream on|off [[<option> <value>] ...]\n"
			"    reboot\n"
			"    config\n"
			"    filter [<band_id> ...]\n"
			"    setbands ALL|<band_id> [<band_id> ...]\n"
			"    broadcast <command> [options]\n"
			"    reply <command> [options]\n"
			"    makehex <hex(10)|ascii(5)>\n"


		);
		return false;
	}

	char * argv[10];
	uint8_t argc;
	uint8_t argi = 0;

	typedef enum
	{
		BAND_BCAST		= 0,
		BAND_REPLY		= 1,
		BAND_DELETE_ALL	= 2,
		XBR_STREAM		= 3,
		XBR_RESET		= 4,
		XBR_REBOOT		= 5,
		SVR_EXIT		= 6,
		SVR_CONFIG		= 7,
		SVR_FILTER		= 8,
		SVR_HELP		= 9,
		BAND_LIST		= 10,
		MAKEHEX         = 11,
		XBR_EOL					// always at end (End Of List)
	}COMMANDS;

    uint8_t getArgs(void);
	bool 	parse(void);
	uint8_t RequiredArgs(uint8_t count);
	bool 	bandCommand(COMMANDS command);
	int 	str_find(char * opt, const char* const* options);
	bool 	usage(const char *f, ...);
	bool 	configStream(void);
	void 	setStream(bool onOff);

}


namespace user_input
{
	void init()
	{
	}

	bool process()
	{
        if (getArgs())
        {
			parse();
            return true;
        }
        else
            return false;
	}
}

namespace
{
	const char hnib[] = "0123456789ABCDEF";

	bool parseBandId(char * arg, char *id);
	bool makeHexFile(const char *name);

	bool parse(void)
	{
		const char * const options[] =
		{
			"broadcast",// BAND_BCAST,
			"reply",	// BAND_REPLY,
			"delete",	// BAND_DELETE_ALL,
			"stream",	// XBR_STREAM,
			"reset",	// XBR_RESET,
			"reboot",	// XBR_REBOOT,
			"exit",		// SVR_EXIT
			"config",	// SVR_CONFIG
			"filter",	// SVR_FILTER
			"help",		// SVR_HELP
			"setbands", // BAND_LIST
			"makehex",  // MAKEHEX
			NULL		// XBR_EOL	(End of List)
		};

		if(!argc)
			return false;

		char * arg = argv[argi];

		COMMANDS command = (COMMANDS) str_find(arg, options);
		Json::StyledWriter writer;

		if(command < 0)
		{
			usage("unknown command '%s'\n", arg);
			return false;
		}

		logs::pars("command = %s\n", options[command]);

		switch (command)
		{
		case SVR_HELP:	help(); break;

		case BAND_BCAST: // no break
			return bandCommand(command);
		case BAND_REPLY:
			return bandCommand(command);
			break;
		case BAND_LIST:
            _bands.clear();

			arg = argv[++argi];
			while(argi < argc)
			{
				char id[11] = "0000000000";
				if (! parseBandId(arg, id) )
					return false;
                _bands.push_back(std::string(id));
				arg = argv[++argi];
			}
			break;

		case SVR_CONFIG:
			logs::pars("re-loading configuration.\n");
			config::load();
			webServ::stop();
			webServ::start();
			logs::read_config();
			setStream(reader::streamSet);
			break;

		case XBR_STREAM:
			if (!RequiredArgs(1))
				return usage("%s: parameter required: 'on' or 'off'.\n", arg);
			arg = argv[++argi];
			reader::streamSet = (strcmp(arg, "on")) ? 0 : 1;
			if ( ! configStream()) return false;
			setStream(reader::streamSet);
			break;

		case SVR_FILTER:
			config::load();

			if (config::value.isMember("filter"))
				config::value.removeMember("filter");
			
			arg = argv[++argi];
			if(argi < argc)
			{
				Json::Value filter = (Json::arrayValue);
				while(argi < argc)
				{
					char id[11] = "0000000000";
					if (! parseBandId(arg, id) )
						return false;

					Json::Value	filterValue = id;
					filter.append(filterValue);
					arg = argv[++argi];
				}
				config::value["filter"] = filter;
				logs::pars("%s\n", writer.write(config::value["filter"]).c_str());
			}
			else
				logs::pars("Removed all filters\n");
			config::save();
			config::load();
			logs::pars("%s\n", writer.write(config::value["filter"]).c_str());
			break;

		case SVR_EXIT:			
			logs::pars("exiting now.\n");	
			exit(0);	
			break;

		case BAND_DELETE_ALL:
			{
				Json::ArrayIndex xbri = 0;
				while (xbri < xbr_list::xbrs.size())
				{
					Json::Value xbr = xbr_list::xbrs[xbri];
					logs::pars("delete bands reader: %s(IP=%s).\n", xbr["reader name"].asCString(), xbr["IP"].asCString());	
					sendHttp::deleteBands(xbr);
					xbri++;
				} // while
			} // reset()
			break;

		case XBR_RESET:
			return usage("Error: reset is disabled.\n");	break;
			{
				Json::ArrayIndex xbri = 0;
				while (xbri < xbr_list::xbrs.size())
				{
					Json::Value xbr = xbr_list::xbrs[xbri];
					logs::pars("reset reader: %s(IP=%s).\n", xbr["reader name"].asCString(), xbr["IP"].asCString());
					sendHttp::reset(xbr);
					xbri++;
				} // while
			} // reset()
			break;

		case XBR_REBOOT:
			{
				Json::ArrayIndex xbri = 0;
				while (xbri < xbr_list::xbrs.size())
				{
					Json::Value xbr = xbr_list::xbrs[xbri];
					logs::pars("reboot reader: %s(IP=%s).\n", xbr["reader name"].asCString(), xbr["IP"].asCString());
					sendHttp::reboot(xbr);
					xbri++;
				} // while
			} // reset()
			break;
		case MAKEHEX:
			{
				bool ret = makeHexFile(argv[++argi]);
				if (!ret) return usage("Error: makehex failed!\n");
				break;
			}
		default:	return usage("Error: Invalid parameter '%s'\n", arg);	break;
		} // switch(index)

		logs::pars("Success!");
		return true;
	}

	uint8_t RequiredArgs(uint8_t count)
	{
		if((argc - argi) >= count)
			return (argc - argi);
		return 0;
	}

    char cliString[1000] = {0};

    uint8_t getArgs()
    {
        if (!Console::instance()->getLine(cliString, sizeof(cliString)))
            return 0;

        argc = 0;
        argv[argc] = strtok(cliString, " \t\r\n");
        while (argv[argc] != NULL)
        {
            argv[++argc] = strtok(NULL, " \t\r\n");
        }
        argi = 0;
        return argc;
    }

	int str_find(char * opt, const char* const* options)
	{
		uint32_t i;
		for (i = 0; i < XBR_EOL; i++)
		{
			if(options[i] == NULL)
				return -1;

			if(strcmp(options[i], opt) == 0)
				return i;
		}
		return -1;
	}

	bool bandCommand(COMMANDS command)
	{
		char * arg = argv[++argi];

		if(!RequiredArgs(1)) return usage("%s: needs minimum of 1 parameter\n", arg);

		uint8_t 	cmd2bd  = 0;
		uint16_t 	sec		= 0;
		uint8_t 	msec10	= 0;
		uint16_t 	toutCt	= 0;
		uint8_t		power   = 0;

		if(strcmp(arg, "fast-ping") == 0 )
		{
			arg = argv[++argi];
			if(!RequiredArgs(3)) return usage("%s: needs 3 parameters\n", arg);
			cmd2bd = 3;
			sec		= atoi(arg); 		arg = argv[++argi];
			msec10	= atoi(arg) / 10;	arg = argv[++argi];
			toutCt	= atoi(arg);
		}
		else if(strcmp(arg, "slow-ping") == 0 )
		{
			arg = argv[++argi];
			if(!RequiredArgs(3)) return usage("%s: needs 3 parameters\n", arg);
			cmd2bd = 2;
			sec		= atoi(arg); 		arg = argv[++argi];
			msec10	= atoi(arg) / 10;	arg = argv[++argi];
			toutCt	= atoi(arg);
		}
		else if(strcmp(arg, "fast-rcv") == 0 )
		{
			arg = argv[++argi];
			if(!RequiredArgs(3)) return usage("%s: needs 3 parameters\n", arg);
			cmd2bd = 1;
			sec		= atoi(arg); 		arg = argv[++argi];
			msec10	= atoi(arg) / 10;	arg = argv[++argi];
			toutCt	= atoi(arg);
		}
		else if(strcmp(arg, "power") == 0 )
		{
			arg = argv[++argi];
			if(!RequiredArgs(2)) return usage("%s: needs 2 parameters\n", arg);
			cmd2bd = 4;
			power	= atoi(arg);	arg = argv[++argi];
			sec		= atoi(arg);
		}
		else if(strcmp(arg, "hwver") == 0 )
		{
			cmd2bd = 0x06;
		}
		else if(strcmp(arg, "fwver") == 0 )
		{
			cmd2bd = 0x07;
		}
		else if(strcmp(arg, "carrieron") == 0 )
		{
			cmd2bd = 0x08;
		}
		else if(strcmp(arg, "carrieroff") == 0 )
		{
			cmd2bd = 0x09;
		}
		else if(strcmp(arg, "command10") == 0 )
		{
			cmd2bd = 0x0A;
		}
		else if(strcmp(arg, "command11") == 0 )
		{
			cmd2bd = 0x0B;
		}
		else if(strcmp(arg, "global") == 0 )
		{
			arg = argv[++argi];
			usage("%s: not implemented\n", arg);
		}
		else
			usage("%s: unknown parameter\n", arg);

		char bandCmd[13] = "000000000000";

		bandCmd[0]	= hnib[(cmd2bd >> 4)	& 0x0F];
		bandCmd[1]	= hnib[(cmd2bd >> 0)	& 0x0F];

		switch (cmd2bd)
		{
		case 1:
		case 2:
		case 3:
			bandCmd[2]	= hnib[(sec >> 12)		& 0x0F];
			bandCmd[3]	= hnib[(sec >> 8)		& 0x0F];
			bandCmd[4]	= hnib[(sec >> 4)		& 0x0F];
			bandCmd[5]	= hnib[(sec >> 0)		& 0x0F];

			bandCmd[6]	= hnib[(msec10 >> 4)	& 0x0F];
			bandCmd[7]	= hnib[(msec10 >> 0)	& 0x0F];

			bandCmd[8]	= hnib[(toutCt >> 12) 	& 0x0F];
			bandCmd[9]	= hnib[(toutCt >> 8)	& 0x0F];
			bandCmd[10] = hnib[(toutCt >> 4)	& 0x0F];
			bandCmd[11] = hnib[(toutCt >> 0)	& 0x0F];
			break;
		case 4:
			bandCmd[2]	= hnib[(power >> 4)		& 0x0F];
			bandCmd[3]	= hnib[(power >> 0)		& 0x0F];
			bandCmd[4]	= hnib[(sec >> 4)		& 0x0F];
			bandCmd[5]	= hnib[(sec >> 0)		& 0x0F];
			break;
		default:	// leave the other bytes as 0.
			break;
		}
        _command = bandCmd;

		Json::ArrayIndex xbri = 0;
		while (xbri < xbr_list::xbrs.size())
		{
			Json::Value xbr = xbr_list::xbrs[xbri];
			logs::pars("command to bands @ reader: %s(IP=%s).\n", xbr["reader name"].asCString(), xbr["IP"].asCString());
            if (command == BAND_BCAST)
                sendHttp::beacon(xbr, _bands, _command);
            else
                sendHttp::commandBands(xbr, _bands, _command, _signalStrength);
			xbri++;
		} // while
		return true;
	}

	bool parseBandId(char * arg, char * id)
	{
		if(strcmp(arg, "ALL") == 0)	// ALL -> leave the default...
			strcpy(id, "FF4BE492E4");
		else
		{
			if(strlen(arg) != 10)
			return usage("%s: Must be ALL or 5 bytes i.e. 0102030405\n", arg);

			const char *s	= arg;
			char *d			= (char *) id;

			while(*s)
			{
				if (strchr(hnib, toupper(*s)) == NULL)
					return usage("%s: hex = 0-9, A-F only.\n", arg);
				*d++ = toupper(*s++);
			}
		}
		return true;
	}

	bool configStream(void)
	{
		logs::pars("setting stream parameters.\n");

		typedef enum
		{
			STRM_MAX	= 0,
			STRM_MSEC	= 1,
			STRM_SS		= 2,
			STRM_EOL	// always at end (End Of List)
		}STRM;

		const char * const strmArgList[] =
		{
			"max",		// STRM_MAX
			"msec",		// STRM_MSEC
			"ss",		// STRM_SS
			NULL		// XBR_EOL	(End of List)
		};

		config::load(); 				// pull any changes that may have occurred in the config file.

		while ( argi < argc )
		{
			char * arg = argv[++argi];
			if ( argi >= argc ) break;
			if((argc - argi)%2)
				return usage("syntax error. Should be even # of argument-value pairs.\n");

			STRM strmArg = (STRM) str_find(arg, strmArgList);

			Json::Value address = config::value["address"];

			arg = argv[++argi];
			switch (strmArg)
			{
			case STRM_MAX:		config::value["stream_max"] 		= atoi(arg);	break;
			case STRM_MSEC:		config::value["stream_msec"] 		= atoi(arg);	break;
			case STRM_SS:		config::value["stream_min_ss"] 		= atoi(arg);	break;
			case STRM_EOL:		break;
			}
			config::save();
		}
		setStream(reader::streamSet);
		return true;
	}

	void setStream(bool onOff)
	{
		// iterate over the list of xBRS
		Json::ArrayIndex xbri = 0;

		while (xbri < xbr_list::xbrs.size())
		{
			Json::Value xbr = xbr_list::xbrs[xbri];
			if(onOff)
			{
				logs::pars("reader: %s(IP=%s) stream turned on.\n", xbr["reader name"].asCString(), xbr["IP"].asCString());
				sendHttp::streamAssign(xbr);
			}
			else
			{
				logs::pars("reader: %s(IP=%s) stream turned off.\n", xbr["reader name"].asCString(), xbr["IP"].asCString());
				sendHttp::streamDelete(xbr);
			}
			xbri++;
		} // while
	}

	bool makeHexFile(const char *name)
	{
		char arg[24] = "";
		strncpy(arg,name,10);

		int len = strlen(name);
		if(len == 5)
		{
			// should be ascii. No checks required. Convert to hex.
			sprintf(arg, "%02x%02x%02x%02x%02x", name[0],name[1],name[2],name[3],name[4]);
		}
		else if (len != 10) return false;

		char ok[11] = "0000000000";

		if ( ! parseBandId(arg, ok))
			return false;

		sprintf(arg, "053FF000%s", ok);

		const char hnib[] = "0123456789ABCDEF";
		uint8_t cksum = 0;
		int hx;
		for (hx = 0; hx < 19; hx+=2)
		{	uint8_t ho, lo;
			ho = strchr(hnib, arg[hx])-hnib;
			arg[hx] = tolower(arg[hx]);
			lo = strchr(hnib, arg[hx+1])-hnib;
			arg[hx+1] = tolower(arg[hx+1]);
			cksum += ((ho & 0xF) << 4) + (lo & 0xF);
		}

		char buffer[1024];

		cksum = ~(cksum) + 1;
		sprintf(buffer, ":%s%02x\r\n:00000001FF\r\n",arg, cksum);

		char tname[64];
		sprintf(tname, "%s.hex", name);

		int fd = open(tname, O_WRONLY | O_CREAT , S_IREAD | S_IWRITE);
		if(fd == -1)
			return false;

		write(fd, buffer, strlen(buffer));
		close(fd);
		return true;
	}

}
