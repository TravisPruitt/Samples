#include "groverlib.h"
#include "types.h"
#include "radiocommands.h"
#include "random.h"

#include <termios.h>
#include <unistd.h>
#include <getopt.h>
#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <ctype.h>
#include <sys/time.h>
#include <signal.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>

#define MAX_RADIOS	(64)

void printargs(void);

static int packetcount;

void inthandler(int s)
{
	(void) s;
	printf("Sent %d packets\n", packetcount);
	exit(0);
}

void hexdump(uint8_t *data, int len)
{
	while(len--)
		printf("%02x ", *data++);
}

void generate_random_data(uint8_t *data, int len)
{
	int i;

	for (i = 0; i < len; i++)
		data[i] = random();
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

int check_quit(void)
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
	if(FD_ISSET(STDIN_FILENO, &set)) {
		read(STDIN_FILENO, buf, sizeof(buf));
		return 1;
	}
	return 0;
}

int run_test(int frequency, uint8_t test_radios[], int nr_test_radios, uint8_t *data, uint8_t *data2,
	     int datalen, int delay_us, int delay_us_pair, int nr_packets, int stop,
	     int runtime)
{  
	int i;
	uint8_t *rx_data;
	int random_data = 0;
	uint8_t buffer[datalen+1];
	uint8_t expected[datalen+1];
	time_t start_time;

	rx_data = (uint8_t *) malloc(datalen);
	if(!rx_data)
		return -1;

	if (data == NULL) {
		random_data = 1;
		data = (uint8_t *) malloc(datalen);
		if(!data) {
			free(rx_data);
			return -1;
		}
	}

	packetcount = 1;

	if(runtime)
		start_time = time(NULL);

	while(1) {
		if (random_data)
			generate_random_data(data, datalen);

		for (i = 0; i < nr_test_radios; i++) {
			int radioNumber = test_radios[i];

			buffer[0] = RadioControl_SetSpiTestData;
			memcpy(&buffer[1], data, datalen);

			/* We expect the same back on 2nd exchange */
			memcpy(expected, buffer, datalen + 1);

			groverlib_spitest(radioNumber, buffer, buffer,
					  datalen + 1, frequency);

			if(delay_us)
				usleep(delay_us);

			// send the 2nd packet (data is not significant) ;
			buffer[0] = RadioControl_SetSpiTestData;
			if (data2)
				memcpy(&buffer[1], data2, datalen);
			else
				memcpy(&buffer[1], data, datalen);

			groverlib_spitest(radioNumber, buffer, buffer,
					  datalen + 1, frequency);

			if(delay_us_pair)
				usleep(delay_us_pair);

			// compare the returned spi test data with that which was sent
			// in the 1st packet
#if 1
			if(memcmp(buffer, expected, datalen + 1)) {
				printf("R[%d] Error in packet %d:\n", radioNumber, packetcount);
				printf("R[%d] TX: ", radioNumber);
				hexdump(expected, datalen + 1);
				printf("\n");
				printf("R[%d] RX: ", radioNumber);
				hexdump(buffer, datalen + 1);
				printf("\n");
				if(stop)
					return -1;
			}
#endif
		}
		if(nr_packets)
			if(packetcount == nr_packets)
				return 0;
		/* If runtime was specified check for termination */
		if(runtime) {
			if(time(NULL) > (start_time + runtime))
				return 0;
		}
		packetcount++;
		if(check_quit())
			return 0;
	}
	return 0;
}

void exit_usage(void)
{
	printf("spi-test [OPTION]\n"
	       "Version: " VERSION "\n"
	       "Mandatory arguments to long options are mandatory for short options too.\n"
	       "\n"
	       "  -h, --help                 show this help\n"
	       "  -s, --speed=SPEED          set SPI bus speed to SPEED Hz\n"
	       "  -r, --radio=RADIO          enable indicated RADIO  for test\n"
	       "                             repeat for multiple radios\n"
	       "  -d, --data=DATA            send/read DATA to radios\n"
	       "                             DATA is HEX bytes, i.e. 00FFEE1122\n"
	       "  -2, --data2=DATA           send/read DATA to radios in 2nd packet\n"
	       "                             DATA is HEX bytes, i.e. 00FFEE1122\n"
	       "  -a, --random-data          send random data\n"
	       "  -l, --random-data-length=LENGTH\n"
	       "                             send LENGTH bytes of random data\n"
	       "  -R, --random-seed=SEED     use SEED for random seed\n"
	       "                             if not specified use timer\n"
	       "  -w, --wait-us=USECS        delay USECS microseconds between packets\n"
	       "  -W, --wait-us-pair=USECS   delay USECS microseconds between packet pairs\n"
	       "  -p, --packets=PACKETS      send PACKET packets\n"
	       "                             if not specified send indefinitely\n"
	       "  -S, --stop-on-error        stop test when error is encountered\n"
	       "  -t, --time=SECS            run test for SECS\n"
	       "  -m, --mfgtest              at the end of test specify PASS/FAIL.\n"
	       "                             also enables --stop-on-error\n"
	       "  -v, --version              show version\n"
	       "\n"
	       "Default values are:\n"
	       );
	printargs();
	exit(0);
}


static int speed = 3000000;
static const char *data_str = "55AAF0AF";
static const char *data2_str = NULL;
static int random_data = 0;
static int random_data_len = 4;
static int random_seed = -1;
static int wait_us = 500;
static int wait_us_pair = 500;
static int packets = 0;
static int stop_on_error = 0;
static int runtime = 0;
static int mfgtest = 0;
static int nr_radios = 0;
static uint8_t radios[MAX_RADIOS];

void printargs(void)
{
	int i;

	printf("  --speed=%d\n", speed);
	printf("  --radio=[ ");
	for(i = 0; i < nr_radios; i++)
		printf("%d ", i);
	printf("]\n");
	printf("  --data=%s\n", data_str);
	if (data2_str)
		printf("  --data2=%s\n", data2_str);
	else
		printf("  --data2=<same as --data>\n");
	printf("  --random-data=%d\n", random_data);
	printf("  --random-data-len=%d\n", random_data_len);
	printf("  --random-seed=%d\n", random_seed);
	printf("  --wait-us=%d\n", wait_us);
	printf("  --wait-us-pair=%d\n", wait_us_pair);
	printf("  --packets=%d\n", packets);
	printf("  --stop-on-error=%d\n", stop_on_error);
	printf("  --time=%d\n", runtime);
}

#if 0
int get_radios(const char *str, uint8_t indices[])
{
	int len, i, r;
	len = strlen(str);

	for (i = 0; i < len; i++) {
		r = str[i] - '0';
		if (r < 0 || r >= NR_RADIOS)
			return -1;
		indices[i] = (uint8_t) r;
	}
	return i;
}
#endif

int parsehex(int c)
{
	static char hex[] = "0123456789ABCDEF";
	char *i;

	c = toupper(c);
	i = index(hex, c);
	if(!i)
		return -1;
	return (int)(i - hex);
}

uint8_t* read_data(const char *str, int *buflen)
{
	int len, i, c, di = 0;
	uint8_t *data;
	uint8_t byte;

	len = strlen(str);

	if(len % 2)
		return NULL;
	*buflen = len / 2;
	data = (uint8_t *) malloc(*buflen);
	if(!data)
		return NULL;

	for (i = 0; i < len; i+=2) {
		c = parsehex(str[i]);
		if(c < 0) {
			free(data);
			return NULL;
		}
		byte = ((uint8_t)c) << 4;
		c = parsehex(str[i + 1]);
		if(c < 0) {
			free(data);
			return NULL;
		}
		byte |= ((uint8_t)c);
		data[di++] = byte;
	}
	return data;
}

void init_random(int seed)
{
	struct timeval tv;

	if (seed > 0) {
		srandom(seed);
	} else {
		gettimeofday(&tv, NULL);
		printf("Using random seed %lu\n", (unsigned long)tv.tv_usec);
		srandom(tv.tv_usec);
	}
}


int main (int argc, char *argv[])
{
	uint8_t *data, *data2 = NULL;
	int datalen;
	int ret, i;

	static struct option long_options[] = {
		{ "help", 0, 0, 'h' },
		{ "speed", 1, 0, 's' },
		{ "radio", 1, 0, 'r' },
		{ "data", 1, 0, 'd' },
		{ "data2", 1, 0, '2' },
		{ "random-data", 0, 0, 'a' },
		{ "random-data-len", 1, 0, 'l' },
		{ "random-seed", 1, 0, 'R' },
		{ "wait-us", 1, 0, 'w' },
		{ "wait-us-pair", 1, 0, 'W' },
		{ "packets", 1, 0, 'p' },
		{ "stop-on-error", 0, 0, 'S' },
		{ "version", 0, 0, 'v' },
		{ "time", 1, 0, 't' },
		{ "mfgtest", 0, 0, 'm' },
		{ 0, 0, 0, 0 },
	};
	const char *short_options = "hs:r:d:2:al:R:w:W:p:Svt:m";
	int c, option_index;

	signal(SIGINT, SIG_DFL);
	stdin_canonical(0);

	while (1) {
		c = getopt_long (argc, argv, short_options,
				 long_options, &option_index);

		if (c == -1)
			break;
		switch (c) {
			case 0:
				printf ("option %s", long_options[option_index].name);
				if (optarg)
					printf (" with arg %s", optarg);
				printf ("\n");
				break;
			case 'h':
				exit_usage();
				break;
			case 's':
				speed = atoi(optarg);
				break;
			case 'r':
				radios[nr_radios++] = atoi(optarg);
				break;
			case 'd':
				data_str = optarg;
				break;
			case '2':
				data2_str = optarg;
				break;
			case 'a':
				random_data = 1;
				break;
			case 'l':
				random_data_len = atoi(optarg);
				break;
			case 'R':
				random_seed = atoi(optarg);
				break;
			case 'w':
				wait_us = atoi(optarg);
				break;
			case 'W':
				wait_us_pair = atoi(optarg);
				break;
			case 'p':
				packets = atoi(optarg);
				break;
			case 'S':
				stop_on_error = 1;
				break;
			case 'v':
				printf("VERSION=%s\n", VERSION);
				return 0;
			case 't':
				runtime = atoi(optarg);
				break;
			case 'm':
				mfgtest = 1;
				stop_on_error = 1;
				break;
			case '?':
				exit_usage();
				break;
		}
	}

	groverlib_initialize();
	printf("Found %d radios\n", groverlib_radio_count());

	printf("Got arguments\n");

	if (nr_radios == 0) {
		nr_radios = groverlib_radio_count();
		for(i = 0; i < nr_radios; i++)
			radios[i] = i;
	}

	printargs();

	if (random_data) {
		init_random(random_seed);
		data = NULL;
		datalen = random_data_len;
	} else {
		data = read_data(data_str, &datalen);
		if (!data) {
			printf("Bad argument for --data\n");
			exit_usage();
		}
	}
	if (data2_str) {
		int data2len;
		data2 = read_data(data2_str, &data2len);
		if (datalen != data2len) {
			printf("Error, lenght of --data and --data2 must match\n");
			return 0;
		}
	}
#if 0
	for (i = 0; i < datalen; i++)
		printf("Data[%d] = %02x\n", i, data[i]);
#endif

	ret = run_test(speed, radios, nr_radios, data, data2, datalen,
		       wait_us, wait_us_pair, packets, stop_on_error,
		       runtime);
	if(mfgtest)
		printf("%s\n", ret ? "FAIL" : "PASS");
	return(0);
}
