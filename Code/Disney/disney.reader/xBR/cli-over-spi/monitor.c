
#include "monitor.h"

#include "character.h"
#include "radio.h"
#include "strings.h"
#include "time.h"

#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/time.h>
#include <termios.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <termios.h>
#include <sys/ioctl.h>
#include <map>
#include <vector>
#include <set>
#include <dirent.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>

/* Commands to the band */
#define RF_CMD_FAST_RX_ONLY_RATE            0x01
#define RF_CMD_SLOW_PING_RATE               0x02
#define RF_CMD_FAST_PING_RATE               0x03
#define RF_CMD_SET_POW                      0x04
#define RF_CMD_SET_MULTIPLE_RATES           0x05
#define RF_CMD_TRANSMIT_HARDWARE_VERSION    0x06
#define RF_CMD_TRANSMIT_FIRMWARE_VERSION    0x07
#define RF_CMD_TRANSMIT_CARRIER             0x08
#define RF_CMD_DISABLE_CARRIER              0x09
#define RF_CMD_FCC_TEST                     0x0F

#define MAX_RADIOS  64
#define TX_RADIO	(getRxRadioCount()) // TX radio index
#define TX_FREQ		2482
#define BROADCAST	0x0FF4BE492E4ULL
#define NO_BAND		0xFFFFFFFFFFFFFFFFULL
#define MaxLineSize 256

unsigned getRadioCount(void)
{
    return radio_getRadioCount();
}

unsigned getRxRadioCount(void)
{
    return getRadioCount() - 1;
}

static ReaderRadio radio [MAX_RADIOS] ;
static Byte selectedRadio ;

static char promptString [8] ;

typedef void (* MonitorFunctionPointer) (void) ;

typedef struct
{
    const char *  description;
    const int     value;
} ArgumentLookupTable ;
//
// ... table must be terminated last entry of NULL



static void txByte (Byte tx)
{
    putchar (tx) ;
}



static void txString (const char * tx)
{
    while (* tx)
        txByte (* tx ++) ;
}



static void txLine (const char * line)
{
    char localCopy [MaxLineSize] ;

    sprintf (localCopy, "%s\n", line);
    txString (localCopy);
}



void monitor_printfLine (const char *formatSpecifier, ...)
{
    va_list argptr ;
    char localCopy [MaxLineSize] ;

    va_start (argptr, formatSpecifier) ;
    vsprintf (localCopy, formatSpecifier, argptr) ;
    va_end   (argptr) ;

    txLine (localCopy) ;
}



static char* getNextArgument (void)
{
    char* tokenPtr = strtok (NULL, " \t");

    return tokenPtr != 0 ? tokenPtr : (char*)"" ;
}

typedef std::vector<char*> Arguments;

static int getAllArguments(Arguments &v)
{
    while(1) {
        char *arg = getNextArgument();
        if(!strcmp(arg, "")) {
            return v.size();
        } else {
            v.push_back(arg);
        }
    }
}



static Byte argument_indexWithinTable (const char * argument, const ArgumentLookupTable * argTablePtr)
{
    Byte index = 0;

    while (argTablePtr->description != NULL)
    {
        if (strings_matchCaseInsensitive (argument, argTablePtr->description))
        {
            break;
        }

        ++ argTablePtr ;
        ++ index;
    }

    return index;
}

void stdin_canonical(int enable)
{
	struct termios ttystate;

	tcgetattr(STDIN_FILENO, &ttystate);
	if (!enable)
	{
		ttystate.c_lflag &= ~ICANON;
		ttystate.c_cc[VMIN] = 1;
	}
	else
	{
		ttystate.c_lflag |= ICANON;
	}
	tcsetattr(STDIN_FILENO, TCSANOW, &ttystate);
}

int check_for_key(void)
{
	fd_set set;
	struct timeval tv;
	int ret;
	int buf[4];

	FD_ZERO(&set);
	FD_SET(STDIN_FILENO, &set); // stdin
	tv.tv_sec = 0;
	tv.tv_usec = 0;
	
	ret = select(STDIN_FILENO + 1, &set, NULL, NULL, &tv);
	if((ret == 1) && FD_ISSET(STDIN_FILENO, &set)) {
		read(STDIN_FILENO, buf, sizeof(buf));
		return 1;
	}
	return 0;
}

void check_quit(void)
{
	if(check_for_key())
		exit(0);
}

int is_process_running(const char *name)
{
    DIR *proc;
    struct dirent *dir;
    char path[PATH_MAX];
    int ret = 0;

    proc = opendir("/proc");
    if(!proc) {
        printf("Cannot open /proc: %s\n", strerror(errno));
        return 0;
    }

    do {
        dir = readdir(proc);
        if(dir) {
            snprintf(path, sizeof(path), "/proc/%s/comm", dir->d_name);
            int fd = open(path, O_RDONLY);
            if(fd == -1)
                continue;
            char buffer[1024];
            memset(buffer, 0, sizeof(buffer));
            read(fd, buffer, sizeof(buffer) - 1);
            close(fd);
            char *found = strstr(buffer, name);
            if(found) {
                ret = 1;
                break;
            }
        }
    } while(dir != NULL);

    closedir(proc);

    return ret;
}

int is_grover_running()
{
    return is_process_running("grover");
}


///////////



static void getResetCause (void)
{
    const char * resetDescription [] =
    {
        "power on or brownout",
        "external",
        "watchdog",
        "clock loss"
    } ;

    Byte resetCause = radio_getResetCause (& radio [selectedRadio]);

    if (resetCause < ArrayLength (resetDescription))
        monitor_printfLine ("%s", resetDescription [resetCause]);
    else
        monitor_printfLine ("reset cause %d not recognized", resetCause);
}


static void forceReset (void)
{
    radio_forceReset (& radio [selectedRadio]);
}



static void setRadioSelection (Byte index)
{
    if (index < getRadioCount())
    {
        selectedRadio = index ;

#if 0
        if (radio [selectedRadio] .spiAccess .busNumber == 0)
        {
            // set the slave-select mux
            mux_setAddress (radio [selectedRadio] .spiAccess .muxAddress) ;
        }
#endif

        sprintf (promptString, "\nr:%d > ", selectedRadio);
    }
}



static void selectRadio (void)
{
    Unsigned_8 scanResult ;
    unsigned newRadioSelection ;

    scanResult = sscanf (getNextArgument(), "%i", & newRadioSelection);
    if (scanResult != 1)
    {
        monitor_printfLine ("ERROR: [radio-number] missing or incorrectly formatted");
        return;
    }

    if (newRadioSelection >= getRadioCount())
    {
        monitor_printfLine ("ERROR: [radio-number] must be between %d and %d", 0, getRadioCount() - 1);
        return;
    }

    setRadioSelection (newRadioSelection) ;
}



static void getVersion (void)
{
    monitor_printfLine ("%s", radio_getFirmwareVersion (& radio [selectedRadio])) ;
}

static void getVersions (void)
{
    for(unsigned i = 0; i < getRadioCount(); i++)
        monitor_printfLine ("Radio %u: %s", i, radio_getFirmwareVersion (& radio [i])) ;
}


static void getVersionLoop (void)
{
	while(1)
		radio_getFirmwareVersion (& radio [selectedRadio]) ;
    //monitor_printfLine ("%s", radio_getFirmwareVersion (& radio [selectedRadio])) ;
}

static void setLED (void)
{
    const ArgumentLookupTable argInfo [] =
    {
        { "OFF",      0 },
        { "ON",       1 },
        { "BLINK",    2 },
        { "PACKET",   3 },
        { "0",        0 },
        { "1",        1 },
        { "2",        2 },
        { "3",        3 },
        { NULL, 	 0 }
    };

    Byte maxIndex = ArrayLength (argInfo) - 2 ;
    char * argument = getNextArgument ();
    Byte index = argument_indexWithinTable (argument, argInfo);

    if (index > maxIndex)
    {
        monitor_printfLine ("ERROR: argument \"%s\" not recognized", argument) ;
        return;
    }

    radio_setLED (& radio [selectedRadio], argInfo [index] .value);
}

static void ledCycle(void)
{
    int r;
    int running = 1;

	stdin_canonical(0);
    while(running)
    {
        for(r = 0; r < 9; r++)
        {
            radio_setLED(&radio[r], 1);
            usleep(250000);
        }
        usleep(1E6);
        for(r = 0; r < 9; r++)
        {
            radio_setLED(&radio[r], 0);
            usleep(10000);
        }
        if(check_for_key()) {
            stdin_canonical(1);
            running = 0;
            break;
        }
    }
}



static void setFrequency (void)
{
    Unsigned_8 scanResult ;
    Signed_32 frequency ;

    scanResult = sscanf (getNextArgument(), "%i", & frequency);
    if (scanResult != 1)
    {
        monitor_printfLine ("ERROR: [MHz] missing or incorrectly formatted");
        return;
    }

    radio_setFrequency (& radio [selectedRadio], frequency) ;
}



static void getFrequency (void)
{
    monitor_printfLine ("%d MHz", radio_getFrequency (& radio [selectedRadio])) ;
}


static void spiCheck (void)
{
    Byte index ;
    Unsigned_16 MHz = 2421 ;

    // turn off receivers
    index = getRadioCount();
    while (index --)
    {
        usleep (1e6 * 0.05) ;
        radio_setRxEnable (& radio [index], 0);
    }

    // all LEDs off
    index = getRadioCount();
    while (index --)
    {
        usleep (1e6 * 0.05) ;
        radio_setLED (& radio [index], 0);
    }

    // all LEDs on ;
    // all frequencies set to const
    index = getRadioCount();
    while (index --)
    {
        usleep (1e6 * 0.25) ;
        radio_setLED (& radio [index], 1);

        usleep (1000) ;
        radio_setFrequency (& radio [index], MHz) ;
    }

    // test loop
    int count = 0 ;
    while (1)
    {
        ++ count ;
        for(index = 0; index < getRadioCount(); index++)
        {
            // blink
            radio_setLED (& radio [index], 0);
            usleep (1e6 * 0.05) ;
            radio_setLED (& radio [index], 1);

            usleep (1000) ;
            radio_setFrequency (& radio [index], MHz) ;

            usleep (1e6 * 0.20) ;
            Unsigned_16 frequency = radio_getFrequency (& radio [index]) ;

            printf ("R%d wrote %d MHz, read back %d MHz: %s\n", index, MHz, frequency, MHz == frequency ? "PASS" : "FAIL");
            MHz++;
            if (MHz >= 2476)
                MHz = 2401;
        }
    }

}

static int readPacket(RadioCommandPacket *p)
{
    Unsigned_8 scanResult ;
    Unsigned_32 aByte ;
    Byte count ;

    // read the address
    count = 0 ;
    while (count < ArrayLength(p->address))
    {
        scanResult = sscanf (getNextArgument(), "%x", & aByte);
        if (scanResult != 1)
        {
            monitor_printfLine ("ERROR: byte %d missing or incorrectly formatted", count);
            return 1;
        }

        p->address[count] = aByte ;
        ++ count ;
    }

    // read the command
    scanResult = sscanf (getNextArgument(), "%x", & aByte);
    if (scanResult != 1)
    {
	    monitor_printfLine ("ERROR: byte %d missing or incorrectly formatted", count);
	    return 1;
    }

    p->command = aByte ;

    // read the data
    count = 0 ;
    while (count < ArrayLength(p->data))
    {
        scanResult = sscanf (getNextArgument(), "%x", & aByte);
        if (scanResult != 1)
        {
            monitor_printfLine ("ERROR: byte %d missing or incorrectly formatted", count);
            return 1;
        }

        p->data[count] = aByte ;
        ++ count ;
    }
    return 0;
}

static void setBeacon (void)
{
    RadioCommandPacket beacon ;

    if(readPacket(&beacon))
	    return;

    radio_setBeacon (& radio [selectedRadio], beacon) ;
}

static void getBeacon (void)
{
    RadioCommandPacket beacon = radio_getBeacon (& radio [selectedRadio]) ;

    monitor_printfLine ("%02x %02x %02x %02x %02x  %02x  %02x %02x %02x %02x %02x",
                        beacon.address [0],
                        beacon.address [1],
                        beacon.address [2],
                        beacon.address [3],
                        beacon.address [4],
                        beacon.command,
                        beacon.data [0],
                        beacon.data [1],
                        beacon.data [2],
                        beacon.data [3],
                        beacon.data [4] ) ;
}

static void setTxCmd (void)
{
    RadioCommandPacket txcmd ;

    if(readPacket(&txcmd))
	    return;

    radio_setTxCommand (& radio [selectedRadio], txcmd) ;
}

static void getTxCmd (void)
{
    RadioCommandPacket txcmd = radio_getTxCommand (& radio [selectedRadio]) ;

    monitor_printfLine ("%02x %02x %02x %02x %02x  %02x  %02x %02x %02x %02x %02x",
                        txcmd.address [0],
                        txcmd.address [1],
                        txcmd.address [2],
                        txcmd.address [3],
                        txcmd.address [4],
                        txcmd.command,
                        txcmd.data [0],
                        txcmd.data [1],
                        txcmd.data [2],
                        txcmd.data [3],
                        txcmd.data [4] ) ;
}


static void setBeaconEnable (void)
{
    static ArgumentLookupTable argInfo [] =
    {
        { "ON",       TRUE  },
        { "OFF",      FALSE },
        { "1",        TRUE  },
        { "0",        FALSE },
        { NULL , 0 }
    };

    Byte maxIndex = ArrayLength (argInfo) - 2 ;
    char * argument = getNextArgument ();
    Byte index = argument_indexWithinTable (argument, argInfo);

    if (index > maxIndex)
    {
        monitor_printfLine ("ERROR: argument \"%s\" not recognized", argument) ;
        return;
    }

    radio_setBeaconEnable (& radio [selectedRadio], argInfo [index] .value);
}



static void getBeaconEnable (void)
{
    monitor_printfLine ("%s", radio_getBeaconEnable (& radio [selectedRadio]) ? "ON" : "OFF") ;
}


static void spiTest (void)
{
    Byte index ;
    const Unsigned_16 MHz = 2421 ;

    // all LEDs off
    index = getRadioCount();
    while (index --)
    {
        usleep (1e6 * 0.05) ;
        radio_setLED (& radio [index], 0);
    }

    // all LEDs on ;
    // all frequencies set to const
    index = getRadioCount();
    while (index --)
    {
        usleep (1e6 * 0.25) ;
        radio_setLED (& radio [index], 1);

        usleep (1000) ;
        radio_setFrequency (& radio [index], MHz) ;
    }

    // test loop
    int count = 0 ;
    while (1)
    {
        ++ count ;
        index = getRadioCount();
        while (index --)
        {
            // blink
            radio_setLED (& radio [index], 0);
            usleep (1e6 * 0.05) ;
            radio_setLED (& radio [index], 1);

            usleep (1e6 * 0.20) ;
            Unsigned_16 frequency = radio_getFrequency (& radio [index]) ;
            if (frequency != MHz)
                return ;

            printf ("%d MHz, index = %d, count = %d\n", frequency, index, count) ;
        }
    }

}



static void berTest (void)
{
    static const Byte BER_RADIOS = getRxRadioCount() ;
    const Unsigned_16 MHz = 2476 ;
    RadioPacketStatistics stats[BER_RADIOS];
    unsigned int i ;

    for(i = 0; i < BER_RADIOS; i++)
    {
        usleep (1e6 * 0.05) ;
        radio_setRxEnable    (& radio [i], FALSE);
        radio_setFrequency   (& radio [i], MHz);
        radio_setCrcEnable   (& radio [i], FALSE);
        radio_setRxEnable    (& radio [i], TRUE);
        radio_setStatsEnable (& radio [i], TRUE);
    }

    while(1)
    {
        for(i = 0; i < BER_RADIOS; i++)
        {
            stats[i] = radio_getStatistics (& radio [i]);
            printf (" %10u/%-4u", stats[i].rxOk, stats[i].rxCrcError);
        }
        printf ("\n");

        usleep (1e6);
    }
}



static void spiTest_2 (void)
{
    Byte index ;
    const Byte Radios = getRxRadioCount() ;     // receivers only

    // test loop
    while (1)
    {
        usleep (1e6 * 0.10) ;

        // set each radio to a different frequency
        const Unsigned_16 MHz = 2401 ;
        index = Radios ;
        while (index --)
        {
            usleep (500) ;
            radio_setFrequency (& radio [index], MHz + 7 * index) ;
        }

        // verify each radio's frequency
        index = Radios ;
        while (index --)
        {
            usleep (500) ;
            radio_setLED (& radio [index], 1);      // on

            usleep (500) ;
            Unsigned_16 wroteFrequency = MHz + 7 * index ;
            Unsigned_16 readFrequency = radio_getFrequency (& radio [index]) ;
            if (readFrequency != wroteFrequency)
            {
                printf ("radio %d frequency: wrote %d read %d\n",
                        index, wroteFrequency, readFrequency) ;
            }
        }

        // verify each radio's version
        index = Radios ;
        while (index --)
        {
            char * actualVersion = radio_getFirmwareVersion (& radio [index]) ;
            if (0 != strcmp (actualVersion, "NFF-S m"))
            {
                printf ("radio %d version read %s\n", index, actualVersion) ;
            }
            else
            {
                // version read back ok
                radio_setLED (& radio [index], 0);      // off
            }
        }

    }

}



static void spiTest_3 (void)
{

    while (1)
    {
        usleep (1e6 * 0.250) ;

        // verify the radio's version
        char * actualVersion = radio_getFirmwareVersion (& radio [selectedRadio]) ;
        if (0 != strcmp (actualVersion, "NFF-S m"))
        {
            printf ("radio %d version read %s", selectedRadio, actualVersion) ;
            printf ("  %02x %02x %02x %02x %02x %02x %02x %02x \n",
                    actualVersion [0],
                    actualVersion [1],
                    actualVersion [2],
                    actualVersion [3],
                    actualVersion [4],
                    actualVersion [5],
                    actualVersion [6],
                    actualVersion [7] ) ;
            return ;
        }

    }

}

int seqDiff(int prev, int now)
{
	if(now >= prev)
		return now - prev;

	return now - prev + 256;
}

unsigned getTxFreq(void)
{
    char *tx_freq = getenv("TX_FREQ");
    if(!tx_freq)
        return TX_FREQ;
    else
        return strtoul(tx_freq, NULL, 10);
}

void setAddress(RadioCommandPacket *p, uint64_t address)
{
	p->address[0] = (address >> 32) & 0xFF;
	p->address[1] = (address >> 24) & 0xFF;
	p->address[2] = (address >> 16) & 0xFF;
	p->address[3] = (address >> 8) & 0xFF;
	p->address[4] = (address >> 0) & 0xFF;
}

uint64_t getAddress(RadioPingPayload *p)
{
	uint64_t a = 0;

	a |= ((uint64_t)p->address[0]) << 32;
	a |= ((uint64_t)p->address[1]) << 24;
	a |= ((uint64_t)p->address[2]) << 16;
	a |= ((uint64_t)p->address[3]) <<  8;
	a |= ((uint64_t)p->address[4]) <<  0;
	return a;
}

uint64_t getBandAddressList(void)
{
	uint64_t addr;
	char *addr_str;

	addr_str = getNextArgument();
	if (strcmp(addr_str, "") == 0)
		return NO_BAND;
	addr = strtoull(addr_str, NULL, 16);
	return addr;
}

uint64_t getBandAddress(void)
{
	uint64_t addr;
	char *addr_str;

	addr_str = getNextArgument();
	if (addr_str)
	{
		addr = strtoull(addr_str, NULL, 16);
		if (addr == 0) {
			addr = BROADCAST;
		}
	}
	return addr;
}

void disableBeacon(void)
{
	radio_setBeaconEnable(&radio[TX_RADIO], 0);
}

void enableBeacon(RadioCommandPacket *tx, int period)
{
	radio_setFrequency(&radio[TX_RADIO], getTxFreq());
	usleep(1000);
	radio_setBeacon(&radio[TX_RADIO], *tx);
	usleep(1000);
	radio_setBeaconPeriod(&radio[TX_RADIO], period);
	usleep(1000);
	radio_setBeaconEnable(&radio[TX_RADIO], 1);
}

int isPing(RadioPingPackage *ping)
{
	int i;

	for(i=0;i<5;i++)
		if(ping->payload.address[i] != 0)
			return 1;
	return 0;
}

#define BAND_SZ		20
#define SEEN_BANDS_TIME	15

struct seen_band {
    unsigned int    when;
    int             ss;
};
typedef std::map<uint64_t, struct seen_band> BandTimes;

void print_seen_bands(BandTimes & seen_bands, int ss)
{
	unsigned row = 2, col = 1;
	unsigned int diff, now = time(NULL);
	BandTimes::iterator it;
	uint64_t id;
	int ret;
	struct winsize ws;
	unsigned int max_row;

	ret = ioctl(fileno(stdout), TIOCGWINSZ, &ws);
	if(ret || ws.ws_row == 0 || ws.ws_col == 0) {
		max_row = 25;
	} else {
		max_row = ws.ws_row;
	}

	printf("\033[2J"); // Erase screen
	printf("\033[H"); // Cursor to top left
	it = seen_bands.begin();
	while(it != seen_bands.end()) {
//		p = *it;
        id = (*it).first;
        diff = now - (*it).second.when;
        if(diff > SEEN_BANDS_TIME) {
            seen_bands.erase(it++);
            continue;
        }
        if(ss) {
            diff = (*it).second.ss;
        }

		printf("\033[%d;%dH", row, col);
		printf("%010llx | %4d |\n",
		       id,
		       diff);
		row++;
		if(row == max_row) {
			row = 2;
			col += BAND_SZ;
		}
		it++;
	}
	printf("\033[H");
	printf("Saw %d bands in the last %d seconds\n", seen_bands.size(), SEEN_BANDS_TIME); 
}

unsigned getFreq(unsigned index)
{
	const unsigned frequency[] = {  2401, 2424, 2450, 2476 };
    return frequency[index % 4];
}

static void configureForRx(int all);

static void showBand(void)
{
    Arguments args;
    uint64_t band;
    unsigned r;
	RadioPingPackage ping;
	RadioPingPayload payload;
	Stopwatch testTimer;
    std::vector<int> ss(getRxRadioCount(), 0);

    getAllArguments(args);

    if(args.size() != 1) {
        printf("Give band id as an argument\n");
        return;
    }

    band = strtoul(args[0], NULL, 16);

    configureForRx(1);

	stopwatch_initialize(&testTimer);

    printf("x\n");

    while(1)
    {
        r++;
        if(r >= getRxRadioCount())
            r = 0;

		ping    = radio_repeatGetRxPacket(&radio[r]);
		payload = ping.payload ;

        if(stopwatch_elapsedMilliseconds(&testTimer) > 1000) {
            for(unsigned i = 0; i < getRxRadioCount(); i++) {
                if(i && ((i % 4) == 0)) {
                    printf("\n");
                }
                printf("%d\t", ss[i]);
            }
            printf("\n---\n");
            stopwatch_initialize(&testTimer);
        }

		if(!isPing(&ping))
			continue;

        if(getAddress(&payload) != band)
            continue;

        ss[r] = ping.signalStrength;
            
    }
}

static void showBandsFn(int ss)
{
	RadioPingPackage ping ;
	RadioPingPayload payload ;
	Byte radioNumber;
	Stopwatch testTimer;
	BandTimes seen_bands;

	for (radioNumber = 0; radioNumber < getRxRadioCount() ; ++ radioNumber)
	{
		usleep (1000);
		radio_forceReset (& radio [radioNumber]);
	}

	sleep(3);

	for (radioNumber = 0; radioNumber < getRxRadioCount() ; ++ radioNumber)
	{
		const Unsigned_16 PingTimeoutMilliseconds = 0;
		usleep (1000);
		radio_set_getPingTimeout(&radio[radioNumber], PingTimeoutMilliseconds);

		usleep (1000);
		radio_setFrequency(&radio[radioNumber], getFreq(radioNumber));

		usleep (1000);
		radio_setRxEnable(&radio[radioNumber], 1);
	}


	radioNumber = 0;

	stopwatch_initialize(&testTimer);
	stdin_canonical(0);

	while(1)
	{
		radioNumber = (radioNumber + 1) % getRxRadioCount();
		//radioNumber = 0;

		usleep (25000);

		ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		payload = ping.payload;

		if (isPing(&ping))
		{
			//stamp_seen_band(payload.address, seen_bands, &num_bands);
			seen_bands[getAddress(&payload)].when = time(NULL);
			seen_bands[getAddress(&payload)].ss = ping.signalStrength;
#if 0
			for(i = 0; i < 20; i++) {
				payload.address[4]++;
				stamp_seen_band(payload.address, seen_bands, &num_bands);
			}
#endif
		}

		if(stopwatch_elapsedMilliseconds(&testTimer) > 1000) {
			print_seen_bands(seen_bands, ss);
			stopwatch_initialize(&testTimer);
		}
		if(check_for_key()) {
			stdin_canonical(1);
			return;
		}
	}
}

static void showBands(void)
{
    showBandsFn(0);
}

static void showBandsSs(void)
{
    showBandsFn(1);
}

static void putToSleep(void)
{
	RadioCommandPacket tx;
	uint64_t addr;

	addr = getBandAddress();
	printf("Sending beacon to address %010llx\n", addr);

    radio_forceReset(&radio[TX_RADIO]);
	sleep(3);
	setAddress(&tx, addr);
	tx.command = RF_CMD_FAST_RX_ONLY_RATE;
	tx.data[0] = 0; // seconds MSB
	tx.data[1] = 10; // seconds LSB
	tx.data[2] = 0; // 10 milliseconds
	tx.data[3] = 0;
	tx.data[4] = 1;

	enableBeacon(&tx, 0);
	printf("Sending beacon. Hit any key to stop.\n");
	stdin_canonical(0);
	while(1) {
		usleep(500000);
		if(check_for_key()) {
			printf("Reseting TX radio\n");
			radio_forceReset(&radio[TX_RADIO]);
			stdin_canonical(1);
			return;
		}
	}
}

static void startRxOnRadio(int r, unsigned freq)
{
	usleep(1000);
	radio_set_getPingTimeout(&radio[r], 0);
	usleep(1000);
	radio_setFrequency(&radio[r], freq);
	usleep(1000);
	radio_setRxEnable(&radio[r], 1);
}

static void wakeUpList(void)
{
	RadioCommandPacket tx;
	uint64_t addr;
	unsigned i;
	RadioPingPackage ping;
	RadioPingPayload payload;

	addr = getBandAddressList();
    radio_forceReset(&radio[TX_RADIO]);
	for (i = 0; i < 4; i ++)
	{
		usleep (1000);
		radio_forceReset(&radio[i]);
	}
	sleep(3);
	for (i = 0; i < 4; i ++)
		startRxOnRadio(i, getFreq(i));

	while(addr != NO_BAND)
	{
		setAddress(&tx, addr);
		tx.command = RF_CMD_SLOW_PING_RATE;
		tx.data[0] = 0; // seconds MSB
		tx.data[1] = 1; // seconds LSB
		tx.data[2] = 0; // 10 milliseconds
		tx.data[3] = 0xFF;
		tx.data[4] = 0xFF;
		enableBeacon(&tx, 0);
		printf("Sending beacon to %010llx\n", addr);
		/* Wait for ping fron addr */
		while(1) {
			for (i = 0; i < 4; i ++) {
				ping    = radio_repeatGetRxPacket(&radio[i]);
				payload = ping.payload;

				if(payload.address[0] == tx.address[0] &&
				   payload.address[1] == tx.address[1] &&
				   payload.address[2] == tx.address[2] &&
				   payload.address[3] == tx.address[3] &&
				   payload.address[4] == tx.address[4])
				{
					printf("Got Ping from %010llx. Moving to next band.\n", addr);
					disableBeacon();
					goto next_band;
				}

			}
		}
next_band:
		addr = getBandAddressList();
	}
}

static void wakeUp(void)
{
	RadioCommandPacket tx;
	uint64_t addr;

	addr = getBandAddress();
	printf("Sending beacon to address %010llx\n", addr);

	radio_forceReset(&radio[TX_RADIO]);
	sleep(3);
	setAddress(&tx, addr);
	tx.command = RF_CMD_SLOW_PING_RATE;
	tx.data[0] = 0; // seconds MSB
	tx.data[1] = 1; // seconds LSB
	tx.data[2] = 0; // 10 milliseconds
	tx.data[3] = 0xFF;
	tx.data[4] = 0xFF;

	enableBeacon(&tx, 0);
	printf("Sending beacon. Hit any key to stop.\n");
	stdin_canonical(0);
	while(1) {
		usleep(500000);
		if(check_for_key()) {
			printf("Reseting TX radio\n");
			radio_forceReset(&radio[TX_RADIO]);
//			disableBeacon(TX_RADIO);
			stdin_canonical(1);
			return;
		}
	}
}

// Seconds Per Ping (slow ping)
static void sendSppBeacon(unsigned int spp, uint64_t addr, int pings)
{
	RadioCommandPacket tx;

	if(spp > 0xFFFF)
		spp = 0xFFFF;

    if(pings < 0 || pings > 0xFFFF)
        pings = 0xFFFF;

	printf("Sending command for %d pings (%d seconds interval) to address %010llx\n", pings, spp, addr);

    radio_forceReset(&radio[TX_RADIO]);
	sleep(3);
	setAddress(&tx, addr);
	tx.command = RF_CMD_SLOW_PING_RATE;
	tx.data[0] = (spp >> 8) & 0xFF; // seconds MSB
	tx.data[1] = spp & 0xFF; // seconds LSB
	tx.data[2] = 0;
    tx.data[3] = (pings >> 8) & 0xFF;
    tx.data[4] = pings & 0xFF;

	enableBeacon(&tx, 0);
	printf("Sending beacon. Hit any key to stop.\n");
	stdin_canonical(0);
	while(1) {
		usleep(500000);
		if(check_for_key()) {
			printf("Reseting TX radio\n");
			radio_forceReset(&radio[TX_RADIO]);
			stdin_canonical(1);
			return;
		}
	}
}

// Pings Per Second (fast ping)
static void sendPpsBeacon(unsigned int pps, uint64_t addr, int pings)
{
	RadioCommandPacket tx;
	unsigned int packet_interval_ms;
       
	if(pps > 10)
		pps = 10;
	packet_interval_ms = 1000 / pps;

    if(pings < 0 || pings > 0xFFFF)
        pings = 0xFFFF;

	printf("Sending command for %d pings (%d per second) to address %010llx\n", pings, pps, addr);

    radio_forceReset(&radio[TX_RADIO]);
	sleep(3);
	setAddress(&tx, addr);
	tx.command = RF_CMD_FAST_PING_RATE;
	tx.data[0] = 0; // seconds MSB
	tx.data[1] = 0; // seconds LSB
	tx.data[2] = packet_interval_ms / 10; // 10 milliseconds
    tx.data[3] = (pings >> 8) & 0xFF;
    tx.data[4] = pings & 0xFF;

	enableBeacon(&tx, 0);
	printf("Sending beacon. Hit any key to stop.\n");
	stdin_canonical(0);
	while(1) {
		usleep(500000);
		if(check_for_key()) {
			printf("Reseting TX radio\n");
			radio_forceReset(&radio[TX_RADIO]);
			stdin_canonical(1);
			return;
		}
	}
}

static void sendPpsBroadcast(void)
{
	char *pps = getNextArgument();
	if(strcmp(pps, "") == 0) {
		printf("Bad argument\n");
		return;
	}
	sendPpsBeacon(strtoul(pps, NULL, 10), BROADCAST, -1);

}

static void sendPpsUnicast(void)
{
	char *pps_str, *addr_str, *number_str;
    int nr_pings;

	addr_str = getNextArgument();
	if(strcmp(addr_str, "") == 0) {
		printf("Bad address.\n");
		return;
	}

	pps_str = getNextArgument();
	if(strcmp(pps_str, "") == 0) {
		printf("Bad pps value.\n");
		return;
	}
	number_str = getNextArgument();
	if(strcmp(number_str, "") == 0) {
        nr_pings = -1;
	} else {
        nr_pings = strtol(number_str, NULL, 10);
    }
	sendPpsBeacon(strtoul(pps_str, NULL, 10), strtoull(addr_str, NULL, 16), nr_pings);
}

static void sendSppUnicast(void)
{
	char *spp_str, *addr_str, *number_str;
    int nr_pings;

	addr_str = getNextArgument();
	if(strcmp(addr_str, "") == 0) {
		printf("Bad address.\n");
		return;
	}

	spp_str = getNextArgument();
	if(strcmp(spp_str, "") == 0) {
		printf("Bad spp value.\n");
		return;
	}
	number_str = getNextArgument();
	if(strcmp(number_str, "") == 0) {
        nr_pings = -1;
	} else {
        nr_pings = strtol(number_str, NULL, 10);
    }
	sendSppBeacon(strtoul(spp_str, NULL, 10), strtoull(addr_str, NULL, 16), nr_pings);
}

static void fccTest(void)
{
    Byte radioNumber ;
    char *arg = getNextArgument();
    uint8_t tx_power = strtoul(arg, NULL, 0);
    RadioCommandPacket tx;
    unsigned int i;

    /* Reset all 9 radios */
    for (radioNumber = 0 ; radioNumber < getRadioCount() ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_forceReset (& radio [radioNumber]) ;
    }
    sleep(3);

    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_setFrequency (& radio [radioNumber], getFreq(radioNumber)) ;
        usleep (1000) ;
        radio_setRxEnable  (& radio [radioNumber], 1);
    }

    radio_setFrequency(&radio[TX_RADIO], getTxFreq());
    printf("Tx power: %d\n", tx_power);
    radio_setTxPower(&radio[TX_RADIO], tx_power);

    stdin_canonical(0);
    while(1) {
	    uint8_t *ptr = (uint8_t*)&tx;
	    for(i=0;i<sizeof(tx);i++)
		    *ptr++ = random();
	    radio_setTxCommand(&radio[radioNumber], tx);
	    check_quit();
	    usleep(1000);
    }
}

static void hexdump(uint8_t *data, unsigned len);

void fccCmd(void)
{
    char *arg;
    uint64_t band;
    unsigned freq, whitener, ms_betweeen_tx, test_duration_s;

    // 1. band
    band = getBandAddressList();
    if(band == NO_BAND) {
        monitor_printfLine("ERROR: Invalid argument");
    }

    // 2. freq
	arg = getNextArgument();
	if(strcmp(arg, "") == 0) {
		monitor_printfLine("ERROR: Invalid argument");
		return;
	}
	freq = strtoul(arg, NULL, 10);

    if(freq < 2400 || freq > 2500) {
		monitor_printfLine("ERROR: Invalid frequency");
		return;
    }

    // 3. whitener
	arg = getNextArgument();
	if(strcmp(arg, "") == 0) {
		monitor_printfLine("ERROR: Invalid argument");
		return;
	}
	whitener = strtoul(arg, NULL, 0);

    // 4. Time between transmissions, in ms
	arg = getNextArgument();
	if(strcmp(arg, "") == 0) {
		monitor_printfLine("ERROR: Invalid argument");
		return;
	}
	ms_betweeen_tx = strtoul(arg, NULL, 10);
    if(ms_betweeen_tx > 0x1FFF) { // 13 bits of value
		monitor_printfLine("ERROR: Invalid ms_betweeen_tx");
		return;
    }

    // 5. Test duration in seconcs
	arg = getNextArgument();
	if(strcmp(arg, "") == 0) {
		monitor_printfLine("ERROR: Invalid argument");
		return;
	}
	test_duration_s = strtoul(arg, NULL, 10);
    if(test_duration_s > 0x7FF) { // 11 bits of value
		monitor_printfLine("ERROR: Test duration");
		return;
    }

    // Start beacon
    RadioCommandPacket tx;
    memset(&tx, 0, sizeof(tx));

	setAddress(&tx, band);
	tx.command = RF_CMD_FCC_TEST;
    tx.data[0] = freq - 2400;
    tx.data[1] = whitener;
    tx.data[2] = ms_betweeen_tx & 0xFF;
    tx.data[3] = test_duration_s & 0xFF;
    tx.data[4] = ((test_duration_s >> 8) & 0x1F) << 3 |
                 ((ms_betweeen_tx >> 8) & 0x07);
    hexdump(tx.data, 5);
    enableBeacon(&tx, 0);
	printf("Sending beacon. Hit any key to stop.\n");
	stdin_canonical(0);
	while(1) {
		usleep(500000);
		if(check_for_key()) {
			printf("Reseting TX radio\n");
			radio_forceReset(&radio[TX_RADIO]);
			stdin_canonical(1);
			return;
		}
	}
}

FILE* openLogFile(const char *prefix)
{
	char fn[256];
	int i = 0;
	struct stat s;

	while(1) {
		snprintf(fn, sizeof(fn), "%s-log-%d.txt", prefix, i);
		if(stat(fn, &s) != 0) {
			printf("Log file: %s\n", fn);
			return fopen(fn, "w");
		}
		i++;
	}
}

static void seqTest(void)
{
    char *filter = NULL;
    unsigned long filter_addr = 0;
    int prev_seq[MAX_RADIOS];
    unsigned int ok_packets[MAX_RADIOS], missed_packets[MAX_RADIOS];
    unsigned int total_packets[MAX_RADIOS], recv_missed[MAX_RADIOS];
    uint32_t recv_seq[MAX_RADIOS];
    Byte radioNumber ;
    struct timeval now, timeout, interval, test_start, tmp;
    unsigned long time_ms;
    unsigned long lineNumber = 0;
    FILE *log;

    /* 500 ms interval */
    interval.tv_sec = 0;
    interval.tv_usec = 500 * 1000;
    gettimeofday(&timeout, NULL); // Force timeout on 1st iteration

    filter = getNextArgument();
    if (filter)
    {
	    filter_addr = strtoul(filter, NULL, 16);
	    if (filter_addr == 0)
		    filter = NULL;
	    else
		    printf("Radio filter: XXXXXX%04x\n", (unsigned int)filter_addr);
    }
    log = openLogFile("seq-test");

    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_forceReset (& radio [radioNumber]) ;
    }
    sleep(3);

    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        const Unsigned_16 PingTimeoutMilliseconds = 0 ;
        usleep (1000) ;
        radio_set_getPingTimeout (& radio [radioNumber], PingTimeoutMilliseconds) ;
        usleep (1000) ;
        radio_setFrequency (& radio [radioNumber], getFreq(radioNumber)) ;
        usleep (1000) ;
        radio_setRxEnable  (& radio [radioNumber], 1);
        prev_seq[radioNumber] = -1;
        ok_packets[radioNumber] = 0;
        missed_packets[radioNumber] = 0;
        total_packets[radioNumber] = 0;
	recv_seq[radioNumber] = 0;
	recv_missed[radioNumber] = 0;
    }

    radioNumber = 0 ;

    stdin_canonical(0);
    gettimeofday(&test_start, NULL); // Force timeout on 1st iteration
    while (1)
    {
	    for(radioNumber = 0; radioNumber < getRxRadioCount(); radioNumber++)
	    {
		    RadioPingPackage ping;
		    RadioPingPayload payload;

		    usleep(50e3);
		    ping    = radio_repeatGetRxPacket (& radio [radioNumber]) ;
		    payload = ping.payload;

		    if (!isPing(&ping))
			    continue;

		    //printf("%x %x %x %x\n", payload.address[3],  payload.address[4], (filter_addr >> 8) & 0xFF, filter_addr & 0xFF);
		    if (filter)
			    if ((payload.address[3] != ((filter_addr >> 8) & 0xFF)) ||
				(payload.address[4] != (filter_addr & 0xFF)))
				    continue;
		    /* We got a packet! */
		    if(prev_seq[radioNumber] == -1) { /* Special handling for first packets */
			    prev_seq[radioNumber] = payload.sequenceNumber;
			    recv_seq[radioNumber] = ping.receiverSequenceNumber;
			    continue;
		    }
		    /* print it */
		    gettimeofday(&now, NULL);
		    timersub(&now, &test_start, &tmp);
		    time_ms = tmp.tv_sec * 1000 + tmp.tv_usec / 1000;
		    /* put to a file */
		    fprintf(log, "%lu %lu %d %02x%02x%02x%02x%02x %3d %3d %3d\n",
			    lineNumber++,
			    time_ms,
			    radioNumber,
			    payload.address[0],
			    payload.address[1],
			    payload.address[2],
			    payload.address[3],
			    payload.address[4],
			    payload.sequenceNumber,
			    ping.signalStrength,
			    ping.receiverSequenceNumber);

		    total_packets[radioNumber]++;
		    /* Receiver sequence number */
		    if(ping.receiverSequenceNumber == ((recv_seq[radioNumber] + 1) % 256)) {
			    recv_seq[radioNumber] = ping.receiverSequenceNumber;
		    } else {
			    recv_missed[radioNumber] += seqDiff(recv_seq[radioNumber], ping.receiverSequenceNumber) - 1;
			    recv_seq[radioNumber] = ping.receiverSequenceNumber;

		    }
		    /* Band sequence number */
		    if(payload.sequenceNumber == ((prev_seq[radioNumber] + 4) % 256)) {
			    prev_seq[radioNumber] = payload.sequenceNumber;
			    ok_packets[radioNumber]++;
		    } else {
//			    int d = seqDiff(prev_seq[radioNumber], payload.sequenceNumber);
//			    printf("\nseq=%d, prev %d, diff=%d\n", payload.sequenceNumber, prev_seq[radioNumber], d);
			    missed_packets[radioNumber] += ((seqDiff(prev_seq[radioNumber], payload.sequenceNumber) / 4) - 1);
			    prev_seq[radioNumber] = payload.sequenceNumber;
		    }
	    }
	    gettimeofday(&now, NULL);
	    if(timercmp(&now, &timeout, >)) {
		    timeradd(&now, &interval, &timeout);
#if 1
		    timersub(&now, &test_start, &tmp);
		    time_ms = tmp.tv_sec * 1000 + tmp.tv_usec / 1000;
		    printf("%8lu:"
			   "%8u/%u/%-8u "
		           "%8u/%u/%-8u "
		           "%8u/%u/%-8u "
		           "%8u/%u/%-8u "
		           "%8u/%u/%-8u "
		           "%8u/%u/%-8u "
		           "%8u/%u/%-8u "
		           "%8u/%u/%-8u\n",
			   time_ms,
			   total_packets[0], missed_packets[0], recv_missed[0],
			   total_packets[1], missed_packets[1], recv_missed[1],
			   total_packets[2], missed_packets[2], recv_missed[2],
			   total_packets[3], missed_packets[3], recv_missed[3],
			   total_packets[4], missed_packets[4], recv_missed[4],
			   total_packets[5], missed_packets[5], recv_missed[5],
			   total_packets[6], missed_packets[6], recv_missed[6],
			   total_packets[7], missed_packets[7], recv_missed[7]);
#endif
	    }
	    check_quit();

    }
}

static void rssiTest (void)
{
    RadioPingPackage ping ;
    RadioPingPackage packets[MAX_RADIOS] ;
    RadioPingPayload payload ;
    unsigned int i;
    char *filter = NULL;
    unsigned long filter_addr = 0;

    Byte radioNumber ;
    memset(&packets, 0, sizeof(packets));

    filter = getNextArgument();
    if (filter)
    {
	    filter_addr = strtoul(filter, NULL, 16);
	    if (filter_addr == 0)
		    filter = NULL;
	    else
		    printf("Radio filter: XXXXXX%04x\n", (unsigned int)filter_addr);
    }

    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_forceReset (& radio [radioNumber]) ;
    }
    sleep(3);

    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_set_getPingTimeout (& radio [radioNumber], 100) ;
        usleep (1000) ;
        radio_setFrequency (& radio [radioNumber], getFreq(radioNumber)) ;
        usleep (1000) ;
        radio_setRxEnable  (& radio [radioNumber], 1);
        usleep (1000) ;
        // ignore first ping
        ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
    }


    usleep(100000);
    radioNumber = 0 ;

    while (1)
    {
	    for(radioNumber = 0; radioNumber < getRxRadioCount(); radioNumber++)
	    {
		    usleep (15000) ;

		    ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		    payload = ping.payload;

		    if (!isPing(&ping))
			    continue;

//		    	printf("%x %x %x %x\n", payload.address[3],  payload.address[4], (filter_addr >> 8) & 0xFF, filter_addr & 0xFF);
		    if (filter)
			    if ((payload.address[3] != ((filter_addr >> 8) & 0xFF)) ||
				(payload.address[4] != (filter_addr & 0xFF)))
				    continue;
		    /* print spaces */
#if 0
		    printf("%d %02x%02x%02x%02x%02x ",
			   radioNumber,
			   payload.address[0],
			   payload.address[1],
			   payload.address[2],
			   payload.address[3],
			   payload.address[4]
			  );
#endif
		    if(isPing(&packets[radioNumber])) {
			    for(i = 0; i < getRxRadioCount(); i++) {
				    if(isPing(&packets[i]))
					    printf("%8d", packets[i].signalStrength);
				    else
					    printf("     ---");
			    }
			    printf("\n");
			    memset(&packets, 0, sizeof(packets));
		    } else {
			    packets[radioNumber] = ping;
		    }
	    }
	    check_quit();
    }
}

static void mfgRssiTest (void)
{
    RadioPingPackage ping ;
#define MFG_RSSI_PINGS      10
#define MFG_RSSI_RUNTIME    20
    RadioPingPackage packets[MAX_RADIOS][MFG_RSSI_PINGS];
    unsigned ix[MAX_RADIOS] = { 0 };
    RadioPingPayload payload;
    int i, j;
    time_t start_time;
    int running = 1;
    int success = 1;
    Byte radioNumber ;
    char *arg;
    unsigned bank;
    int radioFirst, radioLast;

    memset(&packets, 0, sizeof(packets));

	arg = getNextArgument();
	if(strcmp(arg, "") == 0) {
		monitor_printfLine("ERROR: Invalid argument");
		return;
	}
	bank = strtoul(arg, NULL, 10);

	if(bank != 0 && bank != 1) {
		monitor_printfLine("ERROR: Invalid bank %d", bank);
		return;
	}

    monitor_printfLine("Receiving on Bank %d", bank);
    // TODO: review bank for xbr4
    if(bank == 0) {
        radioFirst = 0;
        radioLast = 3;
    } else {
        radioFirst = 4;
        radioLast = 7;
    }

    // Reset all radios
    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_forceReset (& radio [radioNumber]) ;
    }
    sleep(3);

    for (radioNumber = radioFirst ; radioNumber <= radioLast ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_set_getPingTimeout (& radio [radioNumber], 100) ;
        usleep (1000) ;
        radio_setFrequency (& radio [radioNumber], getFreq(radioNumber)) ;
        usleep (1000) ;
        radio_setRxEnable  (& radio [radioNumber], 1);
        usleep (1000) ;
        // ignore first ping
        ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
    }


    usleep(100000);
    radioNumber = 0 ;

    start_time = time(NULL);
    while (running)
    {
	    for(radioNumber = radioFirst; radioNumber <= radioLast; radioNumber++)
	    {
		    usleep (20000) ;

		    ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		    payload = ping.payload;

		    if (!isPing(&ping))
			    continue;

//            printf("%d: %d %02x\n", radioNumber, ping.signalStrength, payload.address[4]);

            packets[radioNumber][ix[radioNumber]++] = ping;
            if(ix[radioNumber] == MFG_RSSI_PINGS) {
                running = 0;
                break;
            }
	    }

	    if(time(NULL) > (start_time + MFG_RSSI_RUNTIME)) {
            running = 0;
            success = 0;
            break;
        }
    }
    for(i = radioFirst; i <= radioLast; i++) {
        printf("%8d", i);
    }
    printf("\n");
    for(j = 0; j < MFG_RSSI_PINGS; j++) {
        for(i = radioFirst; i <= radioLast; i++) {
            if(isPing(&packets[i][j])) {
                printf("%8d", packets[i][j].signalStrength);
            }
            else {
                printf("     ---");
                if(j < (MFG_RSSI_PINGS - 2))
                    success = 0;
            }
        }
        printf("\n");
    }
    printf("%s\n", success ? "PASS" : "FAIL");
}

static void rxTest (void)
{
    RadioPingPackage ping ;
    RadioPingPayload payload ;
    Byte radioNumber ;
    char *filter;
    unsigned long filter_addr;

    filter = getNextArgument();
    if (filter)
    {
	    filter_addr = strtoul(filter, NULL, 16);
	    if (filter_addr == 0)
		    filter = NULL;
	    else
		    printf("Radio filter: XXXXXX%04x\n", (unsigned int)filter_addr);
    }


    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        usleep (1000) ;
        radio_forceReset (& radio [radioNumber]) ;
    }

    sleep(3) ;

    for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
    {
        const Unsigned_16 PingTimeoutMilliseconds = 10 ;
        usleep (1000) ;
        radio_set_getPingTimeout (& radio [radioNumber], PingTimeoutMilliseconds) ;

        usleep (1000) ;
        radio_setFrequency (& radio [radioNumber], getFreq(radioNumber)) ;

        usleep (1000) ;
        radio_setRxEnable  (& radio [radioNumber], 1);
    }


    Unsigned_32 lineNumber = 0 ;
    radioNumber = 0 ;

    Stopwatch testTimer ;
    stopwatch_initialize (& testTimer) ;
    const Unsigned_16 MinutesDuration = 90 ;

    while (stopwatch_elapsedSeconds (& testTimer) < 60 * MinutesDuration)
    {
      #if 0
        radioNumber = (radioNumber + 1) % 4 ;
      #else
        radioNumber = (radioNumber + 1) % getRxRadioCount() ;
      #endif

        usleep (25e3) ;

//      if (! radio_isReadyForNextSpiTransaction (& radio [radioNumber]))
//          continue ;

        ping    = radio_repeatGetRxPacket (& radio [radioNumber]) ;
        payload = ping.payload ;

	if (filter)
		if ((payload.address[3] != ((filter_addr >> 8) & 0xFF)) ||
		    (payload.address[4] != (filter_addr & 0xFF)))
			continue;

        if (payload.address[0] |
            payload.address[1] |
            payload.address[2] |
            payload.address[3] |
            payload.address[4])
        {
            TimeSpec          timeNow ;
            DateAndTimeString timeString ;

            timeNow = time_now();
            time_toString (timeNow, timeString);

            ++ lineNumber ;

            printf ("\n");
            printf ("%8d %s   %2d %02x%02x%02x%02x%02x %3d %2d %3d",
                    lineNumber,
                    timeString,
                    radioNumber,
                    payload.address[0],
                    payload.address[1],
                    payload.address[2],
                    payload.address[3],
                    payload.address[4],
                    payload.sequenceNumber,
                    ping.signalStrength,
                    ping.receiverSequenceNumber);

            if (lineNumber % 600 == 0)
                fflush (stdout) ;

            if (payload.address[0] == 0)
            {
                fflush (stdout) ;
                break ;
            }
        }

    }

}



static void setCrcEnable (void)
{
    static ArgumentLookupTable argInfo [] =
    {
        { "ON",       TRUE  },
        { "OFF",      FALSE },
        { "1",        TRUE  },
        { "0",        FALSE },
        { NULL , 0}
    };

    Byte maxIndex = ArrayLength (argInfo) - 2 ;
    char * argument = getNextArgument ();
    Byte index = argument_indexWithinTable (argument, argInfo);

    if (index > maxIndex)
    {
        monitor_printfLine ("ERROR: argument \"%s\" not recognized", argument) ;
        return;
    }

    radio_setCrcEnable (& radio [selectedRadio], argInfo [index] .value);
}



static void setStatsEnable (void)
{
    static ArgumentLookupTable argInfo [] =
    {
        { "ON",       TRUE  },
        { "OFF",      FALSE },
        { "1",        TRUE  },
        { "0",        FALSE },
        { NULL , 0}
    };

    Byte maxIndex = ArrayLength (argInfo) - 2 ;
    char * argument = getNextArgument ();
    Byte index = argument_indexWithinTable (argument, argInfo);

    if (index > maxIndex)
    {
        monitor_printfLine ("ERROR: argument \"%s\" not recognized", argument) ;
        return;
    }

    radio_setStatsEnable (& radio [selectedRadio], argInfo [index] .value);
}



static void getStats (void)
{
    RadioPacketStatistics stats = radio_getStatistics (& radio [selectedRadio]);

    monitor_printfLine ("      ok: %12lu", stats .rxOk) ;
    monitor_printfLine (" bad crc: %12lu", stats .rxCrcError) ;
    monitor_printfLine (" skipped: %12lu", stats .sequenceNumbersSkipped) ;
//  monitor_printfLine ("      tx: %12lu", stats .tx) ;

}



static void runRxPacketLossTest (void)
{
    Unsigned_8 scanResult ;
    uint32_t seconds ;

    scanResult = sscanf (getNextArgument(), "%u", & seconds);
    if (scanResult != 1)
    {
        monitor_printfLine ("ERROR: [seconds] missing or incorrectly formatted");
        return;
    }

    if (seconds <= 0)
    {
        monitor_printfLine ("ERROR: [seconds] needs to be > 0");
        return;
    }


    radio_setStatsEnable (& radio [selectedRadio], TRUE);

    Stopwatch testTimer ;
    stopwatch_initialize (& testTimer) ;

    while (stopwatch_elapsedSeconds (& testTimer) < seconds)
    {
        RadioPingPackagePlus pingPlus ;

        usleep (500);   // tbd
        pingPlus = radio_getRxPacketPlus (& radio [selectedRadio]) ;

        switch (pingPlus.status)
        {
            case RadioPacketNil :
            case RadioPacketGood :
                // good or nil (non-existent) packets do not need to be shown
                break ;

            case RadioPacketBad :
            {
                // show the bad packet
                RadioPingPayload payload = pingPlus.payload ;

                printf ("%02x %02x %02x %02x %02x  %02x  %02x %02x  %02x",
                        payload.address[0],
                        payload.address[1],
                        payload.address[2],
                        payload.address[3],
                        payload.address[4],
                        payload.sequenceNumber,
                        payload.tbd[0],
                        payload.tbd[1],
                        pingPlus.signalStrength);
              #if 0
                printf ("  %02x", pingPlus.status);     // show extended packet status  
              #endif
                printf ("\n");

                break ;
            }

        }

    }

    usleep (2000) ;
    radio_setStatsEnable (& radio [selectedRadio], FALSE);

    usleep (2000) ;
    getStats();

    printf ("elapsed seconds: %5d\n", seconds) ;
}

static void setCwEnable (void)
{
    static ArgumentLookupTable argInfo [] =
    {
        { "ON",       TRUE  },
        { "OFF",      FALSE },
        { "1",        TRUE  },
        { "0",        FALSE },
        { NULL , 0}
    };

    Byte maxIndex = ArrayLength (argInfo) - 2 ;
    char * argument = getNextArgument ();
    Byte index = argument_indexWithinTable (argument, argInfo);

    if (index > maxIndex)
    {
        monitor_printfLine ("ERROR: argument \"%s\" not recognized", argument) ;
        return;
    }

    radio_setCwEnable(& radio [selectedRadio], argInfo [index] .value);
}


static void setRxEnable (void)
{
    static ArgumentLookupTable argInfo [] =
    {
        { "ON",       TRUE  },
        { "OFF",      FALSE },
        { "1",        TRUE  },
        { "0",        FALSE },
        { NULL , 0}
    };

    Byte maxIndex = ArrayLength (argInfo) - 2 ;
    char * argument = getNextArgument ();
    Byte index = argument_indexWithinTable (argument, argInfo);

    if (index > maxIndex)
    {
        monitor_printfLine ("ERROR: argument \"%s\" not recognized", argument) ;
        return;
    }

    radio_setRxEnable (& radio [selectedRadio], argInfo [index] .value);
}



static void getRxEnable (void)
{
    monitor_printfLine ("%s", radio_getRxEnable (& radio [selectedRadio]) ? "ON" : "OFF") ;
}

static void getTxPower(void)
{
    monitor_printfLine ("%d", radio_getTxPower(&radio[selectedRadio]));
}

static void setTxPower(void)
{
    char * argument = getNextArgument ();
    printf("TX power: %s\n", argument);
    radio_setTxPower(&radio[selectedRadio], strtol(argument, NULL, 10));
}

static void getBeaconPeriod(void)
{
    monitor_printfLine ("%d", radio_getBeaconPeriod(&radio[selectedRadio]));
}

static void setBeaconPeriod(void)
{
    char * argument = getNextArgument ();
    printf("Beacon period: %s\n", argument);
    radio_setBeaconPeriod(&radio[selectedRadio], strtoul(argument, NULL, 0));
}


static void getRxPacket (void)
{
    RadioPingPackage ping    = radio_getRxPacket (& radio [selectedRadio]) ;
    RadioPingPayload payload = ping.payload ;

    monitor_printfLine ("%02x %02x %02x %02x %02x  %02x  %02x %02x  %02x %02x",
                        payload.address[0],
                        payload.address[1],
                        payload.address[2],
                        payload.address[3],
                        payload.address[4],
                        payload.sequenceNumber,
                        payload.tbd[0],
                        payload.tbd[1],
                        ping.signalStrength,
			ping.receiverSequenceNumber);

}



static void getRxPacketPlus (void)
{
    RadioPingPackagePlus ping    = radio_getRxPacketPlus (& radio [selectedRadio]) ;
    RadioPingPayload     payload = ping.payload ;

    monitor_printfLine ("%02x %02x %02x %02x %02x  %02x  %02x %02x  %02x  %02x",
                        payload.address[0],
                        payload.address[1],
                        payload.address[2],
                        payload.address[3],
                        payload.address[4],
                        payload.sequenceNumber,
                        payload.tbd[0],
                        payload.tbd[1],
                        ping.signalStrength,
                        ping.status);

}



static void getRssi (void)
{
    monitor_printfLine ("%d dBm", radio_getRssi (& radio [selectedRadio])) ;
}

static void getRssiOffset(void)
{
	monitor_printfLine("%d", radio_getRssiOffset(&radio[selectedRadio]));
}

static void getRssiOffsets(void)
{
	unsigned i;
    int offset[MAX_RADIOS];

	for(i = 0; i < getRxRadioCount(); i++) {
		usleep(1000);
		offset[i] = radio_getRssiOffset(&radio[i]);
	}


	printf("rssi-offsets");
    for(i = 0; i < getRxRadioCount(); i++) {
        printf(" %d", offset[i]);
    }
    printf("\n");
}

static void setRssiOffsets(void)
{
	unsigned int i;
    int offset[MAX_RADIOS];
    Arguments args;

    getAllArguments(args);

    if(args.size() != getRxRadioCount()) {
			monitor_printfLine("ERROR: Invalid argument");
			return;
    }

	for(i = 0; i < getRxRadioCount(); i++) {
		offset[i] = strtoul(args[i], NULL, 10);
		if(offset[i] < -127 || offset[i] > 127) {
			monitor_printfLine("ERROR: Invalid offset %d", offset[i]);
			return;
		}
	}

	for(i = 0; i < getRxRadioCount(); i++) {
		radio_setRssiOffset(&radio[i], offset[i]);
	}
}

static void setRssiOffset(void)
{
    Arguments args;

    getAllArguments(args);

    if(args.size() != 1) {
		monitor_printfLine("ERROR: Invalid argument");
		return;
    }
	int offset;
	offset = strtol(args[0], NULL, 10);
	printf("Offset: %d\n", offset);
	radio_setRssiOffset(&radio[selectedRadio], offset);
}

// Arguments <bank> <dBm>
#define CAL_PACKETS 4 
static void calibrateRssiOffset(void)
{
	int dBm;
    unsigned bank;
	RadioPingPackage ping;
	RadioPingPackage pings[MAX_RADIOS][CAL_PACKETS*2];
	RadioPingPayload payload ;
	int packets[MAX_RADIOS], i, offset;
	Byte radioNumber, radioFirst, radioLast ;
    Arguments args;

    getAllArguments(args);

    if(args.size() != 2) {
		monitor_printfLine("ERROR: Invalid argument");
		return;
    }

	bank = strtoul(args[0], NULL, 10);
	dBm = strtoul(args[1], NULL, 10);

    // TODO: revise for xbr4
	if(bank > (getRxRadioCount() / 4)) {
		monitor_printfLine("ERROR: Invalid bank %d", bank);
		return;
	}

	if(dBm < -128 || dBm > 128) {
		monitor_printfLine("ERROR: Invalid dBm %d", dBm);
        return;
    }

    radioFirst = bank * 4;
    radioLast = radioFirst + 4;

	monitor_printfLine("Old calibration:");
	getRssiOffsets();
	monitor_printfLine("Calibrating Bank %d with signal strength %d dBm", bank, dBm);

	/* Reset all radios */
	for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
	{
		usleep (10000) ;
		radio_forceReset (& radio [radioNumber]) ;

		packets[radioNumber] = 0;
	}

	sleep(3) ;

	for (radioNumber = radioFirst ; radioNumber < radioLast ; ++radioNumber)
	{
		const Unsigned_16 PingTimeoutMilliseconds = 100 ;
		usleep (1000) ;
		radio_set_getPingTimeout (& radio [radioNumber], PingTimeoutMilliseconds) ;

		usleep (10000) ;
		radio_setRssiOffset (& radio [radioNumber], 0) ;

		usleep (100000) ;
		radio_setFrequency (& radio [radioNumber], getFreq(radioNumber)) ;

		usleep (10000) ;
		radio_setRxEnable  (& radio [radioNumber], 1);
	}

	for (radioNumber = radioFirst ; radioNumber < radioLast ; ++radioNumber)
	{
		ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		//discard packet
		usleep(30000);
	}

	radioNumber = radioLast;
	while(1)
	{
		radioNumber++;
		if(radioNumber >= radioLast)
			radioNumber = radioFirst;

		usleep (30000) ;
		ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		payload = ping.payload ;

		if(!isPing(&ping))
			continue;

		pings[radioNumber][packets[radioNumber]] = ping;
		packets[radioNumber]++;
		if(packets[radioNumber] >= (CAL_PACKETS * 2))
			break;
	}
	/* Do Magic */
	for(radioNumber = radioFirst; radioNumber < radioLast; radioNumber++) {
		if(packets[radioNumber] < CAL_PACKETS) {
			printf("ERROR: Radio %d only got %d packets\n",
			       radioNumber, packets[radioNumber]);
			return;
		}
	}

	usleep(100000);

	for(i = 0; i < CAL_PACKETS; i++) {
		printf("      %4d %4d %4d %4d\n",
		       pings[radioFirst][i].signalStrength,
		       pings[radioFirst+1][i].signalStrength,
		       pings[radioFirst+2][i].signalStrength,
		       pings[radioFirst+3][i].signalStrength
		      );
	}

	unsigned int sum, average;

	for(radioNumber = radioFirst; radioNumber < radioLast; radioNumber++) {
		i = CAL_PACKETS;
		sum = 0;
		while(i--) {
//			printf("%d %d\n", i, pings[radioNumber][i].signalStrength);
			sum += pings[radioNumber][i].signalStrength;
		}
		average = sum / CAL_PACKETS;
		offset = dBm - average;
		if(offset < -127 || offset > 127) {
			printf("Offset for radio %d = %d. Offset too big. Ignoring.\n",
			       radioNumber, offset);
		} else {
			printf("Offset for radio %d = %d. Offset OK. Programming.\n",
			       radioNumber, offset);
			radio_setRssiOffset(&radio[radioNumber], offset);
			usleep(1000);
		}
	}

}

#define MAX_RSP_DATA_SZ             (16)
#define HW_VERSION_SZ               (12)
#define FW_VERSION_SZ               (6)

static void configureForRx(int all)
{
    unsigned radioNumber;
    unsigned lastRadio = all ? getRxRadioCount() : 4;

	/* Reset all radios */
	for (radioNumber = 0 ; radioNumber < getRxRadioCount() ; ++ radioNumber)
	{
		usleep (10000) ;
		radio_forceReset (& radio [radioNumber]) ;
	}

	sleep(3) ;
    // TODO: revise for xbr4
	for (radioNumber = 0 ; radioNumber < lastRadio ; ++radioNumber)
	{
		const Unsigned_16 PingTimeoutMilliseconds = 10 ;
		usleep (1000) ;
		radio_set_getPingTimeout (& radio [radioNumber], PingTimeoutMilliseconds) ;

		usleep (100000) ;
		radio_setFrequency (& radio [radioNumber], getFreq(radioNumber)) ;

		usleep (10000) ;
		radio_setRxEnable  (& radio [radioNumber], 1);
	}

	for (radioNumber = 0 ; radioNumber < lastRadio ; ++radioNumber)
	{
        RadioPingPackage ping;
		ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		//discard packet
		usleep(30000);
	}
}

static void startMfgBeacon(uint64_t address, uint8_t cmd)
{
    RadioCommandPacket tx;
    memset(&tx, 0, sizeof(tx));
	setAddress(&tx, address);
	tx.command = cmd;
    enableBeacon(&tx, 0);
}

static void hexdump(uint8_t *data, unsigned len)
{
    for(unsigned i = 0; i < len; i++)
        printf("%02x ", data[i]);
    printf("\n");
}

static void printPing(RadioPingPackage *ping, int time)
{
    if(time) {
        char timeString[32];
        time_toString(time_now(), timeString);
        printf("%s ", timeString);
    }
    printf("ADDR %02x%02x%02x%02x%02x ",
           ping->payload.address[0],
           ping->payload.address[1],
           ping->payload.address[2],
           ping->payload.address[3],
           ping->payload.address[4]);
    printf("SEQ %02x ", ping->payload.sequenceNumber);
    printf("TBD %02x %02x ",
           ping->payload.tbd[0],
           ping->payload.tbd[1]);
    printf("SS %d ", (int) ping->signalStrength);
    printf("RSEQ %02x\n", ping->receiverSequenceNumber);
}


#if 0
static void receiveMfgData(uint64_t address, uint8_t cmd, uint8_t *mfgData)
{
    unsigned radioNumber;
	RadioPingPackage ping;
	RadioPingPayload payload ;

    configureForRx();
    printf("Band %06llx\n", address);
    startMfgBeacon(address, cmd);

	radioNumber = 4;
    stdin_canonical(0);
	while(1)
	{
		radioNumber++;
		if(radioNumber >= 4)
			radioNumber = 0;

		usleep (30000) ;
		ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		payload = ping.payload ;

		if(!isPing(&ping))
			continue;

//        printPing(&ping, 1);

        if(getAddress(&payload) == address) {
#if 0
            if(first_ping) {
                first_ping = 0;
                disableBeacon();
            }
#endif
            uint8_t debug = payload.tbd[0];
            uint8_t data = payload.tbd[1];
            unsigned offset = debug & 0x0F;

//            printf("Got debug data %02x %02x\n", debug, data);

            mfgData[offset] = data;
            if(debug & (1 << 4)) { // last data bit
                return;
            }
        }
        if(check_for_key()) {
            printf("Reseting TX radio\n");
            radio_forceReset(&radio[TX_RADIO]);
            stdin_canonical(1);
            return;
        }
	}
}
#endif


struct mfgData {
    uint8_t data[MAX_RSP_DATA_SZ];
};

static void receiveMfgDataBroadcast(uint64_t addr, uint8_t cmd, unsigned len)
{
    unsigned radioNumber;
	RadioPingPackage ping;
	RadioPingPayload payload ;
    std::map<uint64_t, struct mfgData> band_mfg_data;

    configureForRx(0);
    startMfgBeacon(addr, cmd);

	radioNumber = 4;
    stdin_canonical(0);

	while(1)
	{
		radioNumber++;
		if(radioNumber >= 4)
			radioNumber = 0;

		usleep (30000) ;
		ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		payload = ping.payload ;

		if(!isPing(&ping))
			continue;

//        printPing(&ping, 1);

        uint64_t address = getAddress(&payload);
        uint8_t debug = payload.tbd[0];
        uint8_t data = payload.tbd[1];
        unsigned offset = debug & 0x0F;
        band_mfg_data[address].data[offset] = data;

//        printf("Got debug data %02x %02x\n", debug, data);

        if(debug & (1 << 4)) { // last data bit
            printf("Got data from band %02x%02x%02x%02x%02x ",
                   payload.address[0],
                   payload.address[1],
                   payload.address[2],
                   payload.address[3],
                   payload.address[4]);
            printf("'");
            for(unsigned i = 0; i < len; i++)
                if(isprint(band_mfg_data[address].data[i]))
                    printf("%c", band_mfg_data[address].data[i]);
                else
                   printf(".");
            printf("' ");
            hexdump(band_mfg_data[address].data, len);
        }

        if(check_for_key()) {
            printf("Reseting TX radio\n");
            radio_forceReset(&radio[TX_RADIO]);
            stdin_canonical(1);
            return;
        }
    }
}

static void getMfgData(uint8_t cmd, unsigned len)
{
    uint64_t address;

    address = getBandAddressList();
    if(address == NO_BAND) {
        printf("No band address given, using broadcast\n");
        receiveMfgDataBroadcast(BROADCAST, cmd, len);
        return;
    }
    else
    {
#if 0
        uint8_t data[MAX_RSP_DATA_SZ];
        receiveMfgData(address, cmd, data);
        printf("Got data: '");
        for(unsigned i = 0; i < len; i++)
        {
            if(isprint(data[i]))
                printf("%c", data[i]);
            else
                printf(".");
        }
        printf("' ");
        hexdump(data, len);
#endif
        printf("Receiving from %010llx\n", address);
        receiveMfgDataBroadcast(address, cmd, len);

    }
}

struct cmdSetGlobal {
    unsigned tx_power:2;
    unsigned timeout1:4;
    unsigned timeout2:4;
    unsigned timeout3:4;
    unsigned interval1:7;
    unsigned interval2:7;
    unsigned interval3:7;
    unsigned exponent1:1;
    unsigned exponent2:1;
    unsigned exponent3:1;
};

void fillSetGlobal(RadioCommandPacket *tx,
                   struct cmdSetGlobal *g)
{
	tx->command = RF_CMD_SET_MULTIPLE_RATES;
    //05 01 11 82 84 83
    tx->data[0] = g->tx_power << 4 | g->timeout3;
    tx->data[1] = g->timeout2 << 4 | g->timeout1;
    tx->data[2] = g->exponent3 << 7 | g->interval3;
    tx->data[3] = g->exponent2 << 7 | g->interval2;
    tx->data[4] = g->exponent1 << 7 | g->interval1;
    printf("SET_GLOBAL\n");
    printf("Mode1: %d %d\n", g->timeout1, g->exponent1 ? g->interval1 * 128 * 10 : g->interval1 * 10);
    printf("Mode2: %d %d\n", g->timeout2, g->exponent2 ? g->interval2 * 128 * 10 : g->interval2 * 10);
    printf("Mode3: %d %d\n", g->timeout3, g->exponent3 ? g->interval3 * 128 * 10 : g->interval3 * 10);
}

void startSetGlobalBeacon(uint64_t address,
                          struct cmdSetGlobal *global)
{
    RadioCommandPacket tx;
    memset(&tx, 0, sizeof(tx));
	setAddress(&tx, address);
    fillSetGlobal(&tx, global);
    enableBeacon(&tx, 0);
}

static void getHwVersion()
{
    getMfgData(RF_CMD_TRANSMIT_HARDWARE_VERSION, HW_VERSION_SZ);
}

static void getFwVersion()
{
    getMfgData(RF_CMD_TRANSMIT_FIRMWARE_VERSION, FW_VERSION_SZ);
}

static void testSetGlobal()
{
    struct cmdSetGlobal setGlobal = {
        0, // tx_power
        1, // timeout1 (10 intervals)  fast listen
        1, // timeout2 (10 intervals)  slow ping
        3, // timeout3 (25 intervals)  fast ping
        2, // interval1 (2 * 128 * 10 ms) fast listen
        100, // interval2 (1000 ms)     slow ping
        50 ,  //interval3 (0 ms)        fast ping
        1,   //exponent1
        0,   //exponent2
        0    //exponent3
    };

    typedef enum { SLOW_PING, FAST_RECEIVE } state_t;
    state_t state = SLOW_PING;

    uint64_t address = getBandAddressList();
    if(address == NO_BAND) {
        printf("No band address given!\n");
        return;
    }

    unsigned radioNumber;
	RadioPingPackage ping;
	RadioPingPayload payload ;
    unsigned long ping_time, first_ping_time, last_ping_time = 0;
    unsigned dT, pings = 0;

    startSetGlobalBeacon(address, &setGlobal);
    configureForRx(0);

	radioNumber = 4;
	while(1)
	{
		radioNumber++;
		if(radioNumber >= 4)
			radioNumber = 0;

		usleep (10000) ;
		ping    = radio_repeatGetRxPacket(&radio[radioNumber]);
		payload = ping.payload ;
        ping_time = time_now_ms();
        dT = ping_time - last_ping_time;

        switch(state) {
            case SLOW_PING:
                if(pings && (dT > 6000)) { // If no ping for a time, band has moved to FAST RECEIVE
                    printf("Got %u pings ==> FAST_RECEIVE\n", pings);
                    // Restart beacon
                    startSetGlobalBeacon(address, &setGlobal);
                    state = FAST_RECEIVE;
                    continue;
                }
                // This wasn't a ping
                if(!isPing(&ping))
                    continue;
                // Was our ping
                if(getAddress(&payload) == address) {
                    if(pings == 0) {
                        // on first ping disable beacon
                        disableBeacon();
                        first_ping_time = ping_time;
                    }
                    printf("dT %u ms\n", dT);
                    printPing(&ping, 1);
                    pings++; // Count pings
                    last_ping_time = ping_time;
                }
                break;
            case FAST_RECEIVE:
                if(!isPing(&ping))
                    continue;
                if(getAddress(&payload) == address) {
                    printf("Got ping %lu ms after last ping\n",
                           ping_time - last_ping_time);
                    pings = 0;
                    state = SLOW_PING;
                    last_ping_time = ping_time;
                }
                break;
        }
    }

}

int startTxPowerBeacon(uint64_t addr, unsigned power, unsigned timeout)
{
    RadioCommandPacket tx;

    if(power > 3) {
        printf("ERROR: Power needs to be between 0..3\n");
        return -1;
    }
    if(timeout > 60) {
        printf("ERROR: Max timeout is 60 seconds\n");
        return -1;
    }

    radio_forceReset(&radio[TX_RADIO]);
    sleep(3);
    memset(&tx, 0, sizeof(tx));
    setAddress(&tx, addr);
    tx.command = RF_CMD_SET_POW;
    tx.data[0] = power;
    tx.data[1] = timeout;
    enableBeacon(&tx, 0);

    return 0;
}

void txPowerCommand(void)
{
    unsigned long power, timeout;
    uint64_t address;
    unsigned i = 0;
    Arguments args;

    getAllArguments(args);

    if(args.size() != 2 && args.size() != 3) {
        monitor_printfLine("ERROR: Invalid argument");
        return;
    }

    if(args.size() == 2) {
        address = BROADCAST;
    } else {
        address = strtoull(args[i++], NULL, 16);
    }

    power = strtoul(args[i++], NULL, 10);
    timeout = strtoul(args[i++], NULL, 10);

    if(startTxPowerBeacon(address, power, timeout) == 0)
    {
        printf("Sending beacon. Hit any key to stop.\n");
        stdin_canonical(0);
        while(1) {
            usleep(500000);
            if(check_for_key()) {
                printf("Reseting TX radio\n");
                radio_forceReset(&radio[TX_RADIO]);
                stdin_canonical(1);
                return;
            }
        }
    }
}

void radioFccMode(void)
{
    Arguments args;
    unsigned long duration, interval;

    getAllArguments(args);

    if(args.size() == 2) {
        duration = strtoul(args[0], NULL, 10);
        interval = strtoul(args[1], NULL, 10);
    } else {
        printf("Invalid arguments\n");
        return;
    }

    printf("Starting test with duration %lu us, interval %lu us\n"
           "Hit any key to stop.\n",
           duration, interval);

    stdin_canonical(0);
    while(1) {
        /* Turn on */
        radio_setFccTest(&radio[selectedRadio], 1);
        /* Wait for duration */
        usleep(duration);
        /* Turn off */
        radio_setFccTest(&radio[selectedRadio], 0);
        /* Wait for interval */
        usleep(interval);

        if(check_for_key()) {
            radio_setFccTest(&radio[selectedRadio], 0);
            stdin_canonical(1);
            return;
        }
    }
}

void showInventory(void)
{
    Arguments args;
    unsigned long duration;
	RadioPingPackage ping ;
	RadioPingPayload payload ;
    unsigned i;
	Stopwatch testTimer;
    std::set<uint64_t> seen_bands;
	RadioCommandPacket tx;

    getAllArguments(args);

    if(args.size() == 1) {
        duration = strtoul(args[0], NULL, 10);
    } else {
        printf("Invalid arguments. Give duration of inventory read\n");
        return;
    }

	for (i = 0; i < getRxRadioCount() ; ++ i)
	{
		usleep (1000);
		radio_forceReset(&radio[i]);
	}
    usleep (1000);
    radio_forceReset(&radio[TX_RADIO]);

    printf("Resetting radios\n");

	sleep(3);

    printf("Starting RX\n");
	for (i = 0; i < getRxRadioCount(); i ++)
		startRxOnRadio(i, getFreq(i));

    printf("Starting TX\n");
	setAddress(&tx, BROADCAST);
	tx.command = RF_CMD_SLOW_PING_RATE;
	tx.data[0] = 0; // seconds MSB
	tx.data[1] = 1; // seconds LSB
	tx.data[2] = 0; // 10 milliseconds
	tx.data[3] = 0;
	tx.data[4] = 10;

	enableBeacon(&tx, 0);

	stopwatch_initialize(&testTimer);

    i = 0;

	while(1)
	{
		i = (i + 1) % getRxRadioCount();

		usleep (25000);

		ping    = radio_repeatGetRxPacket(&radio[i]);
		payload = ping.payload;

		if (isPing(&ping))
		{
            uint64_t band = getAddress(&payload);
//            printf("Got ping from %llx radio %u\n", band, i);
            seen_bands.insert(band);
		}

		if(stopwatch_elapsedSeconds(&testTimer) > duration) {
            FILE *log;

            log = openLogFile("inventory");
//            printf("Got %u bands:\n", seen_bands.size());
            for(std::set<uint64_t>::iterator it = seen_bands.begin() ;
                it != seen_bands.end();
                ++it)
            {
                printf("%llx\n", *it);
                fprintf(log, "%llx\n", *it);
            }
            fclose(log);
            /* Reset TX radio */
            radio_forceReset(&radio[TX_RADIO]);
            return;
		}
	}
}

///////////



static void showCommands (void);
static void showGivenCommand (void);



static const struct
{
    const char *			format ;
    MonitorFunctionPointer  procedure ;
} command [] = {
	{
		"?                                          # show all monitor commands",
		(MonitorFunctionPointer) showCommands,
	},
	{
		"help [command]                             # show help for given command",
		(MonitorFunctionPointer) showGivenCommand,
	},
	{

		"radio [radio-number]                       # select radio to address",
		(MonitorFunctionPointer) selectRadio,
	},
	{
		"v?                                         # get firmware version",
		(MonitorFunctionPointer) getVersion,
	},
	{
		"vv?                                        # get firmware version",
		(MonitorFunctionPointer) getVersionLoop,
	},
	{
		"reset?                                     # get cause of last reset",
		(MonitorFunctionPointer) getResetCause,
	},
	{
		"reset                                      # force a watchdog reset",
		(MonitorFunctionPointer) forceReset,
	},

	{
		"led [off|on|blink|packet|0|1|2|3]          # set yellow LED state",
		(MonitorFunctionPointer) setLED,
	},
	{
		"freq [MHz]                                 # set radio frequency",
		(MonitorFunctionPointer) setFrequency,
	},
	{
		"freq?                                      # get radio frequency",
		(MonitorFunctionPointer) getFrequency,
	},
	{
		"crc [off|on|0|1]                           # 0 = do not discard packets with crc error",
		(MonitorFunctionPointer) setCrcEnable,
	},
	{
		"rx-en [off|on|0|1]                         # en/disable the receiver",
		(MonitorFunctionPointer) setRxEnable,
	},
	{
		"rx-en?                                     # get rx en/disable state",
		(MonitorFunctionPointer) getRxEnable,
	},
	{
		"r                                          # get a received ping packet",
		(MonitorFunctionPointer) getRxPacket,
	},
	{
		"r+                                         # get a received ping packet with extra status",
		(MonitorFunctionPointer) getRxPacketPlus,
	},
	{
		"rssi                                       # get rx signal strength 0 to ~0x40, 0x80=nil",
		(MonitorFunctionPointer) getRssi,
	},
	{
		"stats-en [off|on|0|1]                      # en/disable statistics, 1=[re]start statistics",
		(MonitorFunctionPointer) setStatsEnable,
	},
	{
		"stats?                                     # get statistics",
		(MonitorFunctionPointer) getStats,
	},
	{
		"stats [seconds]                            # run rx packet loss test for number of seconds",
		(MonitorFunctionPointer) runRxPacketLossTest,
	},
	{
		"spi-test                                   # run spi test",
		(MonitorFunctionPointer) spiTest,
	},
	{
		"bertest                                    # run ber test",
		(MonitorFunctionPointer) berTest,
	},
	{
		"spi-check                                  # run spi check",
		(MonitorFunctionPointer) spiCheck,
	},
	{
		"spi-test-2                                 # run spi test 2",
		(MonitorFunctionPointer) spiTest_2,
	},
	{
		"spi-test-3                                 # run spi test 3",
		(MonitorFunctionPointer) spiTest_3,
	},
	{
		"rx-test                                    # run receive test",
		(MonitorFunctionPointer) rxTest,
	},
	{
		"rssi-test                                  # run rssi test",
		(MonitorFunctionPointer) rssiTest,
	},
	{
		"seq-test                                   # receive test with sequence numbers",
		(MonitorFunctionPointer) seqTest,
	},
	{
		"tx-power <gain>                            # set TX power (values: -15...0)",
		(MonitorFunctionPointer) setTxPower,
	},
	{
		"tx-power?                                  # get TX power",
		(MonitorFunctionPointer) getTxPower,
	},
	{
		"beacon-en [off|on|0|1]                     # en/disable the beacon",
		(MonitorFunctionPointer) setBeaconEnable,
	},
	{
		"beacon-en?                                 # get beacon en/disable state",
		(MonitorFunctionPointer) getBeaconEnable,
	},
	{
		"beacon-period <ms>                         # set beacon period",
		(MonitorFunctionPointer) setBeaconPeriod,
	},
	{
		"beacon-period?                             # get beacon period",
		(MonitorFunctionPointer) getBeaconPeriod,
	},
	{
		"beacon AA AA AA AA AA  CC  DD DD DD DD DD  # set beacon Address, Command, Data",
		(MonitorFunctionPointer) setBeacon,
	},
	{
		"beacon?                                    # get beacon Address, Command, Data",
		(MonitorFunctionPointer) getBeacon,
	},
	{
		"txcmd AA AA AA AA AA  CC  DD DD DD DD DD   # TX command Address, Command, Data",
		(MonitorFunctionPointer) setTxCmd,
	},
	{
		"txcmd?                                     # get TX command Address, Command, Data",
		(MonitorFunctionPointer) getTxCmd,
	},
	{
		"cw-en [off|on|0|1]                         # en/disable Cw",
		(MonitorFunctionPointer) setCwEnable,
	},
	{
		"fcc-test                                   # FCC test",
		(MonitorFunctionPointer) fccTest,
	},
	{
		"wake-up [address]                          # Wake up band. Address is\n"
		"                                             AABBCCDDEE. Empty for broadcast",
		(MonitorFunctionPointer) wakeUp,
	},
	{
		"sleep [address]                            # Put band to sleep. Address is\n"
		"                                             AABBCCDDEE. Empty broadcast",
		(MonitorFunctionPointer) putToSleep,
	},
	{
		"rssi-offset?                               # Get rssi offset",
		(MonitorFunctionPointer) getRssiOffset,
	},
	{
		"rssi-offset <value>                        # Set rssi offset",
		(MonitorFunctionPointer) setRssiOffset,
	},
	{
		"rssi-offsets?                              # Get rssi offsets",
		(MonitorFunctionPointer) getRssiOffsets,
	},
	{
		"rssi-offsets <r1> <r2> ... <r8>            # Set rssi offsets",
		(MonitorFunctionPointer) setRssiOffsets,
	},
	{
		"rssi-calibrate <bank> <dBm>                # Calibrate rssi offset",
		(MonitorFunctionPointer) calibrateRssiOffset,
	},
	{
		"pps-unicast <band> <pps> [# pings]         # Send unicast pings/second command to <band>",
		(MonitorFunctionPointer) sendPpsUnicast,
	},
	{
		"pps-broadcast <pps>                        # Send broadcast pings/second command",
		(MonitorFunctionPointer) sendPpsBroadcast,
	},
	{
		"spp-unicast <band> <pps> [# pings]         # Send unicast seconds/ping command to <band>",
		(MonitorFunctionPointer) sendSppUnicast,
	},
	{
		"show-bands                                 # Listen to bands and show them",
		(MonitorFunctionPointer) showBands,
	},
	{
		"show-bands-ss                              # Listen to bands and show them with signal strength",
		(MonitorFunctionPointer) showBandsSs,
	},
	{
		"wake-up-list <addr1> ... <addrN>           # Wake up bands. Address is\n"
		"                                             AABBCCDDEE. ",
		(MonitorFunctionPointer) wakeUpList,
	},
	{
		"band-hw-ver <addr>                         # Get band HW version",
		(MonitorFunctionPointer) getHwVersion,
	},
	{
		"band-fw-ver <addr>                         # Get band FW version",
		(MonitorFunctionPointer) getFwVersion,
	},
	{
		"test-global <addr>                         # test global set command",
		(MonitorFunctionPointer) testSetGlobal,
	},
	{
		"mfg-rssi-test <bank>                       # run manufacturing rssi test",
		(MonitorFunctionPointer) mfgRssiTest,
	},
	{
		"led-cycle                                  # cycle leds",
		(MonitorFunctionPointer) ledCycle,
	},
	{
		"fcc-cmd <band> <freq> <whitener> <ms_between_tx> <testduration_s>\n"
        "                                           # Send FCC command beacon to band",
		(MonitorFunctionPointer) fccCmd,
	},
	{
		"txpwr-cmd [band] <power> <timeout>         # send TX power beacon to band or broadcast",
		(MonitorFunctionPointer) txPowerCommand,
	},
    {
        "radio-fcc-mode <duration> <interval>       # Enable FCC mode on radio\n"
        "                                           # Duration and interval are in us",
        (MonitorFunctionPointer) radioFccMode,
    },
    {
        "versions?                                  # show all radio versions\n",
        (MonitorFunctionPointer) getVersions,
    },
    {
        "inventory <seconds>                        # wake up all bands and listen\n"
        "                                           # to them, show inventory after\n"
        "                                           # <seconds>",
        (MonitorFunctionPointer) showInventory,
    },
	{
		"show-band <address>                        # Listen to a band and show it",
		(MonitorFunctionPointer) showBand,
	},

#if 0

    { RadioControl_SetGain                  , "set-gain"        , "set rx gain  1-high, 0=low"          },
    { RadioControl_GetGain                  , "get-gain"        , "get rx gain"                         },

    { RadioControl_SetRssiMode              , "set-rssi-mode"   , "rx signal strength mode 0..3"        },
    { RadioControl_GetRssiMode              , "get-rssi-mode"   , "signal strength measurement mode"    },

    { RadioControl_SetPingReceiveAddress    , "set-ping-addr"   , "set ping receive address"            },
    { RadioControl_GetPingReceiveAddress    , "get-ping-addr"   , "get ping receive address"            },

    { RadioControl_SetRxLength              , "set-ping-length" , "set the ping packet length"          },
    { RadioControl_GetRxLength              , "get-ping-length" , "get the ping packet length"          },


    { RadioControl_SetTxPower               , "set-tx-db"       , "set transmit power  -15=min, 0=max"  },
    { RadioControl_GetTxPower               , "get-tx-db"       , "get transmit power"                  },

    { RadioControl_SetBeaconPeriod          , "set-beacon-ms"   , "set beacon period 0..255 msec"       },
    { RadioControl_GetBeaconPeriod          , "get-beacon-ms"   , "get beacon period"                   },

    { RadioControl_TxCommand                , "tx-cmd"          , "transmit a command packet once"      },
    { RadioControl_TxCommandReadBack        , "tx-cmd?"         , "read back the command packet"        },
    { RadioControl_TxCommandRepeat          , "tx"              , "repeat last tx-cmd"                  },


    { RadioControl_SetCwEnable              , "cw"              , "CW  0=disable, 1-enable"             },

#endif


} ;



static int lookupCommand (char * commandToFind)
{
    // return the index of commandToFind within command[]

    char commandName [64];

    unsigned int index = 0 ;

    while (index < ArrayLength (command))
    {
        sscanf (command [index].format, "%s", commandName);

        if (strings_matchCaseInsensitive (commandToFind, commandName))
        {
            break;
        }

        ++ index ;
    }

    return index;
}



static void showGivenCommand (void)
{
    // show help for given command

    char * commandToShow = getNextArgument ();

    unsigned int index = lookupCommand (commandToShow);

    if (index < ArrayLength(command))
    {
        monitor_printfLine (command [index].format) ;
    }
    else
    {
        monitor_printfLine ("ERROR: command \"%s\" not recognized", commandToShow) ;
    }

}



static void showCommands (void)
{
    // display all monitor commands

    unsigned int index = 0 ;

    while (index < ArrayLength (command))
    {
        monitor_printfLine (command [index].format) ;
        ++ index ;
    }

}



static char * getLine (void)
{
    static char inputLine [MaxLineSize] ;

    char * lineInPtr = inputLine ;
    size_t maxLength = ArrayLength(inputLine) - 1 ;
    int bytesRead = getline (& lineInPtr, & maxLength, stdin) ;

    // remove the line termination character
    inputLine [-- bytesRead] = 0 ;

    return inputLine ;
}


static void initializeRadios(void)
{
    printf("%s\n", __FUNCTION__);
    radio_Initialize();

    memset(radio, 0, sizeof(radio));

    for(unsigned i = 0; i < getRadioCount(); i++)
        radio[i].index = i;
}



void monitor_run (int argc, char *argv[])
{

    initializeRadios () ;
    setRadioSelection (0) ;

    char * inputString ;
    char * token ;
    Unsigned_16 index ;


    monitor_printfLine ("");
    monitor_printfLine ("READER RADIO CLI/SPI");
    monitor_printfLine ("TX frequency %u. Override with TX_FREQ environment.", getTxFreq());
    if(is_grover_running()) {
        monitor_printfLine ("Grover is running, quitting. Stop it with \"/etc/init.d/grover stop\"\n");
        return;
    }


    Boolean prompt = TRUE;

    if (argc > 0)
    {
	char *input = NULL;
	int i, inputsz = 0;

	for (i = 0; i < argc; i++) {
		inputsz += strlen(argv[i]) + 1; // for space
	}
	input = (char*)malloc(inputsz + 1); // for null
	input[0] = 0;
	for (i = 0; i < argc; i++) {
		input = strcat(input, argv[i]);
		input = strcat(input, " ");
	}
	inputString = input;
	printf("input: %s\n", inputString);
        token = strtok (inputString, " \t") ;

        index = lookupCommand (token);

        if (index < ArrayLength(command))
        {
            command [index].procedure () ;
        }
        else
        {
            monitor_printfLine ("ERROR: command \"%s\" not recognized", token) ;
        }
	return;
    }

    while (1)
    {
        if (prompt)
        {
            txString (promptString) ;
        }
        prompt = TRUE ; // default: always prompt for command

        inputString = getLine () ;

        // remove leading white space
        while (character_isWhitespace (* inputString))
        {
            ++ inputString;
        }

        // handle empty string
        if (* inputString == 0)
        {
            continue;
        }

        token = strtok (inputString, " \t") ;

        index = lookupCommand (token);

        if (index < ArrayLength(command))
        {
            command [index].procedure () ;
        }
        else
        {
            monitor_printfLine ("ERROR: command \"%s\" not recognized", token) ;
        }

    }

}

/* vim: set shiftwidth=4 tabstop=4 expandtab: */

