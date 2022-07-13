//*****************************************************************************
// spi.cpp
//
//	SPI interface for xBand Reader. 
//*****************************************************************************
//
//	Written by Mike Wilson 
//	Copyright 2011, Synapse.com
//
//*****************************************************************************

// C/C++ Std Lib
#include <cstring>
#include <cstdio>
#include <cerrno>

// System Lib
#include <pthread.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>

// Global-App
#include "grover.h"

#include "grover_radio.h"
#include "logging.h"

// Local-ish
#include "radiocommands.h"
#include "radio.h"
#include "spi.h"
inline long double Log(long double x)
{
	return __builtin_logl(x);
}

inline long double
 cos(long double __x)
 { return __builtin_cosl(__x); }

inline long double
sqrt(long double __x)
{ return __builtin_sqrtl(__x); }

#include "logging.h"

namespace
{
	#define BAND_CMD_CLR {{0},0,{0}}

	typedef struct
	{
		bool active;
		uint16_t 	freq;
		uint8_t 	led;
		bool 		crc;
		bool 		stats;
		bool 		autoRx;
		bool 		rxEnable;
		uint16_t 	ping_to;
		uint32_t 	ping_sz;
		int8_t 		rssi_offset;
	}RADIO_STATUS;
	
	#define RADIO_STATUS_CLR {false, 0, 0, false, false, false, false, 0, 0, 120}
	
	RADIO_STATUS radioStat[receivers::RECEIVER_CT] = {RADIO_STATUS_CLR};

	spi::BAND_CMD	beaconPacket = BAND_CMD_CLR;
	uint8_t			beaconPeriod = 0;
	bool			beaconEnable = false;

	typedef struct
	{
		spi::BAND_CMD	bandCmd;
		int8_t 			rcvdss;
		bool 			active;
	}BAND_INFO;

	#define BAND_DB_SZ 1000
	BAND_INFO bandCommands[BAND_DB_SZ] = {{BAND_CMD_CLR,-127, false}};

	// return a number between 0 and 1, which are normal distributed over repeated calls.
	// using the Box-Muller method.
	#define PI 3.14159265
	long double random_normDist ()
	{
		long double U,V;
		U = rand(); V = rand();
		return sqrt( -2 * Log(U) * cos (2 * PI * V));
	}

	long double random_normDist(long double lower, long double upper )
	{
		long double U,V;
		U = rand(); V = rand();

		long double range = upper - lower;
		return lower + (range *(sqrt( -2 * Log(U) * cos (2 * PI * V))));
	}
}

namespace spi
{
	const uint8_t 	MAX_TXLEN = 16;
	const struct radio_read rread_init = 
	{ 0, { NULL, 1 }, { NULL, 1 }, {0 ,200000} };

	#define RXTXPACK ((spi::RXTX *) (0))

	// Relate the command to it's data size.
	#define GET(CMD, FIELD) { CMD, #CMD, sizeof(RXTXPACK->packet.FIELD) + sizeof(RXTXPACK->cmd) }
	#define SET(CMD, FIELD)	{ CMD, #CMD, sizeof(RXTXPACK->packet.FIELD) + sizeof(RXTXPACK->cmd) }
	typedef struct 
	{
		uint8_t cmd;
		char	name[20];
		uint8_t sz;
	} CMD_SZ;
	
	const CMD_SZ cmdSize[] = 
	{   // 0x00
		GET(DoNothing,enable),				GET(GetVersion,version),			
		SET(SetFrequency,frequency),		GET(GetFrequency,frequency),	
		SET(SetGain,gain),					GET(GetGain,gain),					
		SET(SetRssiMode,rssiMode),			GET(GetRssiMode,rssiMode),			
		// 0x08
		SET(SetPingRcvAddress,address),		GET(GetPingRcvAddress,address),		
		SET(SetRxLength,length),			GET(GetRxLength,length),			
		SET(SetRxEnable,enable),			GET(GetRxEnable,enable),			
		GET(GetPing,ping),					SET(SetTxPower,gain),				
		// 0x10
		GET(GetTxPower,gain),				SET(SetBeaconPacket,bandCmd),	
		GET(GetBeaconPacket,bandCmd),	    SET(SetBeaconPeriod,beaconPeriod),	
		GET(GetBeaconPeriod,beaconPeriod),	SET(SetBeaconEnable,enable),		
		GET(GetBeaconEnable,enable),		SET(TxCommand,bandCmd),		
		// 0x18
		SET(TxCommandRepeat,bandCmd),	    SET(TxCommandGet,bandCmd),	
		SET(SetYellowLED,led),				SET(SetRxCrcEnable,enable),		
		SET(SetCwEnable,enable),			GET(GetSignalStrength,signal_strength),		
		GET(GetPacketStats,packetStats),	SET(ForceReset,enable),		
		// 0x20
		GET(GetResetStatus,enable),			GET(GetPingPlus,pingPlus),		
		SET(SetStatsEnable,enable),			SET(SetSpiTestData,spiTestData),		
		SET(Set_GetPingTimeout,pingTimeout),GET(GetPert_Timing,frequency),		
		GET(GetPert_SeqNum,frequency),		SET(SetRssiOffset,rssiOffset),
		
		GET(GetRssiOffset,rssiOffset)
	};
	void init()
	{
	}

	cRadio::cRadio(void)			
	{ 
	}

	cRadio::cRadio(uint8_t radio)
	{ 
		Open(radio);
	}

	cRadio::~cRadio()
	{ 
		Close();
	}

	void cRadio::Open(uint8_t radio)
	{
		rread.radio = radio;
	} // open()

	void cRadio::Close(void)
	{
	} // close()

	int cRadio::xchg(void)
	{
		return 0;
	} // xchg()

	// returns 0 or errno
	int cRadio::setParm(uint8_t cmd, void* parm = NULL)
	{
		assert(cmd == cmdSize[cmd].cmd);

		switch(cmd)
		{
			case SetYellowLED:		memcpy(&radioStat[rread.radio].led,	parm, cmdSize[cmd].sz);	break;
			case SetFrequency:		memcpy(&radioStat[rread.radio].freq,	parm, cmdSize[cmd].sz);	break;
			case SetRxCrcEnable:	memcpy(&radioStat[rread.radio].crc,	parm, cmdSize[cmd].sz);	break;
			case SetStatsEnable:	memcpy(&radioStat[rread.radio].stats,	parm, cmdSize[cmd].sz);	break;
			case SetRxEnable:		memcpy(&radioStat[rread.radio].rxEnable,parm, cmdSize[cmd].sz);	break;
			case Set_GetPingTimeout:memcpy(&radioStat[rread.radio].ping_to, parm, cmdSize[cmd].sz);	break;
			case SetBeaconPacket:	memcpy(&beaconPacket,	parm, cmdSize[cmd].sz);	break;
			case SetBeaconPeriod:	memcpy(&beaconPeriod,	parm, cmdSize[cmd].sz);	break;
			case SetBeaconEnable:	memcpy(&beaconEnable,	parm, cmdSize[cmd].sz);	break;

		}
		return 0;
	} // setParm()

	int cRadio::getParm(uint8_t cmd, void *parm = NULL)
	{
		assert(cmd == cmdSize[cmd].cmd);

		switch (cmd)
		{
			case GetVersion:
				{
					char *p = (char*) parm;
					p[0] = 'F'; p[1] = 'A'; p[2] = 'K'; p[3] = 'E'; p[4] = '\0';
				}
				break;
			case GetResetStatus: 	memcpy(parm, &radioStat[rread.radio].led,			cmdSize[cmd].sz); break;
			case GetFrequency: 		memcpy(parm, &radioStat[rread.radio].freq, 			cmdSize[cmd].sz); break;
			case GetRssiOffset: 	memcpy(parm, &radioStat[rread.radio].rssi_offset, 	cmdSize[cmd].sz); break;
			case GetRxEnable: 		memcpy(parm, &radioStat[rread.radio].rxEnable, 		cmdSize[cmd].sz); break;
			default: ABORT("unplanned xchg - bad variable"); break;
		}
		return cmdSize[cmd].sz;
	} // getParm()

	int cRadio::setPingSz(uint32_t size)	
	{
		radioStat[rread.radio].ping_sz = size;
		return 0;
	} // setPingSz()

	int cRadio::setAutoRxPing(bool enable)	
	{
		radioStat[rread.radio].autoRx = enable;
		return 0;
	} // setAutoRxPing()

	int cRadio::hardwareReset ()
	{
		RADIO_STATUS sc = RADIO_STATUS_CLR;
		radioStat[rread.radio] = sc;
		return 0;
	} // hardwareReset()
	
	int cRadio::reply(uint64_t band_id, int8_t rcvdss, const uint8_t *data)
	{
		// look for the first unused band location.
		int x = 0;
		for (; x < BAND_DB_SZ; x++)
		{
			if (bandCommands[x].active == false)
				break;
		}

		// convert 64 bit band_id to 5 byte value.
		B64_8n5(band_id,bandCommands[x].bandCmd.address);
		bandCommands[0].bandCmd.command  = data[0];
		bandCommands[0].bandCmd.parms[0] = data[1];
		bandCommands[0].bandCmd.parms[1] = data[2];
		bandCommands[0].bandCmd.parms[2] = data[3];
		bandCommands[0].bandCmd.parms[3] = data[4];
		bandCommands[0].bandCmd.parms[4] = data[5];
		bandCommands[0].rcvdss = rcvdss;
		bandCommands[x].active = true;
		return 0;
	} // reply()
	
	int cRadio::replyStop(uint64_t band_id)
	{
		if(band_id == ALL_BANDS)
		{	
			// clear out entire table of active bands.
			for (int x = 0; x < BAND_DB_SZ; x++)
			{
				bandCommands[x].active = false;
			}
		}
		else
		{
			BAND_INFO band;
			B64_8n5(band_id,band.bandCmd.address);

			// search table of active bands for this band, then mark it inactive.
			for (int x = 0; x < BAND_DB_SZ; x++)
			{
				if ( memcmp(bandCommands[x].bandCmd.address, band.bandCmd.address, sizeof(spi::BANDADDR)) == 0)
				{
					bandCommands[x].active = false;
					break;
				}
			} // end for
		}
		return 0;
	} // replyStop()

	namespace fakeBand
	{
		void (*cback)(uint8_t, uint8_t *, uint8_t);
		
		thread::threadId	tid[100];
		
		typedef struct
		{
			uint64_t 			band_id;
			int8_t 				avgss;
			bool 				active;
			thread::threadId	tid;
		}SETTINGS;
		
		SETTINGS settings[100];
		
		createBand(int indice)
		{
			int priority = 70;
			void *arg = (void *) indice;
			tid[indice] = thread::create(pingThread, priority, arg);
			usleep(100000);
		}
		
		// persistant thread; created for each represented band.
		void * pingThread(void *val)
		{
			int indice = (int) val;
			while(1)
			{
				struct radio_message *rmsg = (struct radio_message *) buf;
				
				
				
				cback(rmsg->radio,rmsg->msg,rmsg->length);
				cback(rmsg->radio,rmsg->msg,rmsg->length);
				sleep(1);
			}
		}
	}

	namespace rxThread
	{
		int fd = -1;
		void 				(*cback)(uint8_t, uint8_t *, uint8_t);
		thread::threadId	tid;
		pthread_mutex_t		mutex;

		// runs as a separate thread.
		void * pingReadThread(void *cradio)
		{	
			if (cradio == 0) // init the ping thread.
			{
				// loop through the list of bands in the config file.
				// for each band, start a timer.
				return NULL;
			}

			// When the timer expires: (cradio has the index in the band table)
			// - Reset the timer
			// - use callback to record a ping event.
			
			// filter out GetPings with signal_strength == 0.
/*			if( 	rxtx->cmd != GetPing
				||	rxtx->packet.signal_strength
			  )
				cback(rmsg->radio,rmsg->msg,rmsg->length);
*/
			return NULL;
		} // pingReadThread()
		
		void start( void(*callback)(uint8_t radio, uint8_t * buff, uint8_t len))
		{
			cback = callback; // called by pingReadThread().

// TODO: Write the following. pingReadThread failed to start last time I 
//	ran the code below.
			
			// loop through the list of bands in the config file.
			// for each band, start a timer.


//			int priority = 50;
//			void *arg = NULL;
//			tid = thread::create(pingReadThread, priority, arg);
		}

	} // namespace rxThread

} // namespace spi
