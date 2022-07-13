#include <stdlib.h>
#include <sys/select.h>
#include <sys/ioctl.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h>
#include <map>
#include <vector>
#include <string>
#include <set>
// #include "grover_radio_version.h"

typedef std::vector<int> radio_vector;
typedef std::map<uint64_t, radio_vector> band_radio_map; // map[vector[radio] = seq]
typedef std::map<uint64_t, unsigned long> band_packets_map;

static unsigned long bad_seq = 0;
static unsigned long bad_rcv_seq = 0;
static unsigned long packets = 0;
static unsigned long loopback_cmd_packets = 0;
static uint8_t band_address[5];
static std::set<uint64_t> my_bands;
static int filter = 0;
static uint8_t all_band_address[5] = { 0, 0, 0, 0, 0 };

static band_packets_map band_packets;
static band_packets_map band_bad_packets;
static band_radio_map band_radio_good_packets;
static band_radio_map band_radio_bad_packets;
static std::map<uint64_t, struct timeval> band_timestamp;

static int dump_messages = 0;
static int print_sequece_errors = 0;
static int print_status_by_band = 0;
static int all_points_bulletin = 0;
static unsigned req_ping_interval = 1000;
static unsigned ping_interval1 = 200;
static unsigned ping_interval2 = 1000;
static unsigned load_test = 0;
static unsigned tx_freq = 2482;
static unsigned tx_lifetime = 0;

#include "grover_radio.h"
#include "radiocommands.h"

#define TX_RADIO	8

int radio_send_tx_data(int fd, unsigned time_ms, uint8_t address[5], int8_t rcvdss);

int is_my_band(uint64_t addr)
{
	std::set<uint64_t>::iterator i = my_bands.find(addr);

	if(i == my_bands.end())
		return 0;

	return 1;
}


uint64_t get_band_address(uint8_t *address)
{
	return (uint64_t)address[0] << 32 |
		(uint64_t)address[1] << 24 |
		(uint64_t)address[2] << 16 |
		(uint64_t)address[3] << 8 |
		(uint64_t)address[4] << 0;
}

int is_band_address(uint8_t *address)
{
	unsigned int i;

	for(i = 0; i < 5; ++i) {
		if(address[i] != 0)
			return 1;
	}
	return 0;
}

static void radio_dump(void *buf, unsigned long len)
{
	uint8_t *b = (uint8_t*)buf;
	while(len >= 16) {
		printf("%02x %02x %02x %02x %02x %02x %02x %02x "
		       "%02x %02x %02x %02x %02x %02x %02x %02x\n",
		       b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7],
		       b[8], b[9], b[10], b[11], b[12], b[13], b[14], b[15]);
		b += 16;
		len -= 16;
	}
	while(len--) {
		printf("%02x ", *b++);
	}
	printf("\n");
}

int radio_send_tx_data(int fd, unsigned time_ms, uint64_t address, int8_t rcvdss)
{
	uint8_t addr[5];

	addr[0] = address >> 32;
	addr[1] = address >> 24;
	addr[2] = address >> 16;
	addr[3] = address >> 8;
	addr[4] = address;
	radio_dump(addr, 5);
	return radio_send_tx_data(fd, time_ms, addr, rcvdss);
}

void print_band_packets()
{
	band_packets_map::iterator it;
	std::pair<uint64_t, unsigned long> p;
	uint64_t id;
	int total_good, total_bad, i;

	for(it = band_packets.begin(); it != band_packets.end(); ++it) {
		p = *it;
		id = p.first;

		total_good = 0;
		total_bad = 0;
		for(i = 0; i < 8; i++) {
			total_good += band_radio_good_packets[id][i];
			total_bad += band_radio_bad_packets[id][i];
		}
		printf("%llx %8d/%-3d %8d/%-3d %8d/%-3d %8d/%-3d %8d/%-3d %8d/%-3d %8d/%-3d %8d/%-3d %8d/%-3d\n",
		       id,
		       band_radio_good_packets[id][0],
		       band_radio_bad_packets[id][0],
		       band_radio_good_packets[id][1],
		       band_radio_bad_packets[id][1],
		       band_radio_good_packets[id][2],
		       band_radio_bad_packets[id][2],
		       band_radio_good_packets[id][3],
		       band_radio_bad_packets[id][3],
		       band_radio_good_packets[id][4],
		       band_radio_bad_packets[id][4],
		       band_radio_good_packets[id][5],
		       band_radio_bad_packets[id][5],
		       band_radio_good_packets[id][6],
		       band_radio_bad_packets[id][6],
		       band_radio_good_packets[id][7],
		       band_radio_bad_packets[id][7],
		       total_good,
		       total_bad);
	}
}

int seqDiff(int prev, int now)
{
	if(now >= prev)
		return now - prev;

	return now - prev + 256;
}

static void radio_dump_message(int fd,
			       uint8_t *buf,
			       unsigned long max_sz,
			       int tx)
{
	struct radio_message *msg;
	unsigned int offs = 0;
	unsigned int r;
	int seq, rcv_seq, i;
	int prev_seq, prev_rcv_seq;
	static band_radio_map bands_seq;
	static unsigned long radio_rcv_seq[8] = { -1, -1, -1, -1, -1, -1, -1, -1 };
	uint64_t id;
	struct timeval tv;

	if(max_sz < sizeof(*msg))
		return;

	while(offs < max_sz) {
		msg = (struct radio_message*)(buf + offs);
		offs += sizeof(struct radio_message) + msg->length;

		r = msg->radio;
		id = get_band_address(&msg->msg[1]);
		if(id == 0)
			continue;

		if(filter && !is_my_band(id)) {
			// Store receiver sequence number regardless
			radio_rcv_seq[r] = msg->msg[10];
			continue;
		}

		if(msg->msg[0] == 0x0e) {
			for(i = 0; i < 5; i++)
				band_address[i] = msg->msg[i+1];
		}
		if(msg->msg[7])
			loopback_cmd_packets++;
		if(r > 7)
				continue;

		if(band_packets.count(id)) {
			// seen this band before
			band_packets[id]++;
			prev_seq = bands_seq[id][r];
		} else {
			// new band
			band_packets[id] = 1;
			bands_seq[id] = radio_vector(8, -1);
			prev_seq = -1;
			band_radio_good_packets[id] = radio_vector(8);
			band_radio_bad_packets[id] = radio_vector(8);
			band_bad_packets[id] = 0;
			gettimeofday(&tv, NULL);
			band_timestamp[id] = tv;
			if(tx) {
				radio_send_tx_data(fd, req_ping_interval, band_address, RADIO_TX_RCVSS_ALL);
				/* First ping, disregard loopback cmd status byte (basically ignore first packet) */
				if(!msg->msg[7])
					loopback_cmd_packets++;
			}
		}
		if(dump_messages) {
			gettimeofday(&tv, NULL);
			timersub(&tv, &band_timestamp[id], &tv);
			printf("%04lu MSG id: %d, from %d, len %d, tbd %d, data: ",
			       tv.tv_sec*1000 + tv.tv_usec/1000,
			       msg->id, msg->radio, msg->length, msg->tbd);
			radio_dump(msg->msg, msg->length);
		}

		seq = msg->msg[6];
		if(prev_seq >= 0) {
			if(seq != ((prev_seq + 4) % 256)) {
				int missing =
					(seqDiff(prev_seq, seq) / 4) - 1;
				if(print_sequece_errors) {
					printf("BAD SEQ %x, prev=%x!\n", seq, prev_seq);
#if 0
					printf("MSG id: %d, from %d, len %d, tbd %d, data: ",
					       msg->id, msg->radio, msg->length, msg->tbd);
					radio_dump(msg->msg, msg->length);
#endif
				}
				bad_seq++;
				band_bad_packets[id] += missing;
				band_radio_bad_packets[id][r] += missing;
			} else {
				band_radio_good_packets[id][r]++;
			}
		} else {
			band_radio_good_packets[id][r]++;
		}

		prev_rcv_seq = radio_rcv_seq[r];
		rcv_seq = msg->msg[10];
		if((prev_rcv_seq >= 0) && rcv_seq != ((prev_rcv_seq + 1) % 256)) {
			if(print_sequece_errors) {
				printf("BAD RCV SEQ %x, prev=%x!\n", rcv_seq, prev_rcv_seq);
				radio_dump(msg->msg, msg->length);
			}
			bad_rcv_seq++;
		}
		bands_seq[id][r] = seq;
		radio_rcv_seq[r] = rcv_seq;
		packets++;
		gettimeofday(&tv, NULL);
		band_timestamp[id] = tv;
	}
}

int radio_open()
{
	int fd;

	fd = open("/dev/grover_radio", O_RDWR);
	if (fd < 0) {
		printf("Cannot open radio: %s\n", strerror(errno));
		return fd;
	}
	return fd;
}


int radio_read(int fd, int radio, void *tx, unsigned long txarglen,
	       void *rx, unsigned long rxarglen)
{
	struct radio_read r;
	int ret;

	r.radio = (uint8_t) radio;
	r.tx.buf = (uint8_t*)tx;
	r.tx.len = txarglen + 1;
	r.rx.buf = (uint8_t*)rx;
	r.rx.len = rxarglen + 1;
	r.rx_timeout.tv_sec = 0;
	r.rx_timeout.tv_usec = 200000;

	while(1) {
		fflush(stdout);
		ret = ioctl(fd, RADIO_CTRL_RD, &r);
		if (ret < 0) {
			if(errno == ETIMEDOUT) {
				printf("timeout, trying again\n");
				continue;
			}
			printf("Error on RADIO_CTRL_RD: %s\n", strerror(errno));
			return ret;
		} else if(ret == 0) {
			return 0;
		}
	}
	return ret;
}

int radio_write(int fd, int radio, void *msg, unsigned long arglen)
{
	uint8_t rx_buf[1];
	int ret;

	ret = radio_read(fd, radio, msg, arglen, rx_buf, 0);
	return ret;
}

int radio_rxenable(int fd, int radio, int value)
{
	struct RadioMessage command;

	memset(&command, 0, sizeof(command));
	command.control = RadioControl_SetRxEnable;
	command.u.enable = value ? 1 : 0;
	return radio_write(fd, radio, &command, sizeof(command.u.enable));
}

int radio_autorx_enable(int fd, int radio, int value)
{
	struct radio_autorx_enable r;

	r.radio = radio;
	r.enable = value;
	fflush(stdout);
	if(ioctl(fd, RADIO_SET_AUTORX_EN, &r))
		printf("Cannot RADIO_SET_AUTORX_EN: %s\n", strerror(errno));
	return 0;
}

int radio_setfreq(int fd, int radio, int freq)
{
	struct RadioMessage command;

	memset(&command, 0, sizeof(command));
	command.control = RadioControl_SetFrequency;
	command.u.frequency = (uint16_t) freq;
	return radio_write(fd, radio, &command, sizeof(command.u.frequency));
}

int radio_loop(int radio)
{
	struct RadioMessage command, reply;
	uint8_t cmd[] = { 1, 2, 4, 8 };
	int fd, ret;

	fd = radio_open();
	if (fd < 0)
		return fd;

	while(1) {
		memset(&command, 0, sizeof(command));
		memset(&reply, 0, sizeof(reply));
		command.control = RadioControl_SetSpiTestData;
		reply.control = RadioControl_SetSpiTestData;
		memcpy(command.u.spiTestData, cmd, sizeof(cmd));
		ret= radio_read(fd, radio,
				&command, 4,
				&reply, 4);
		if(memcmp(cmd, reply.u.spiTestData, sizeof(cmd))) {
			printf("Data differs!\n");
			printf("TX: ");
			radio_dump(cmd, sizeof(cmd));
			printf("\n");
			printf("RX: ");
			radio_dump(reply.u.spiTestData, sizeof(cmd));
			printf("\n");
		}
	}
	return ret;
}

int radio_getfreq(int fd, int radio)
{
	struct RadioMessage command, reply;

	int ret;
	memset(&command, 0, sizeof(command));
	memset(&reply, 0, sizeof(reply));

	command.control = RadioControl_GetFrequency;
	ret = radio_read(fd, radio, &command, 0,
			 &reply, sizeof(reply.u.frequency));
	printf("Frequency = %d\n", reply.u.frequency);
	return ret;
}

int radio_reset(int fd, int radio)
{
	int ret;

	fflush(stdout);
	ret = ioctl(fd, RADIO_FORCE_RESET, &radio);
	if (ret) {
		perror("Cannot do ioctl RADIO_FORCE_RESET");
	}
	return ret;
}

int radio_send_tx_data(int fd, unsigned time_ms, uint8_t address[5], int8_t rcvdss)
{
	int ret;
	struct radio_tx_data tx;
	struct RadioMessage cmd;

	memset(&tx, 0, sizeof(tx));
	memset(&cmd, 0, sizeof(cmd));

	if(!all_points_bulletin)
		tx.band_address = get_band_address(address);
	cmd.control = 23; // RadioControl_TxCommand
	if(!all_points_bulletin) {
		cmd.u.commandPacket.address[0] = address[0];
		cmd.u.commandPacket.address[1] = address[1];
		cmd.u.commandPacket.address[2] = address[2];
		cmd.u.commandPacket.address[3] = address[3];
		cmd.u.commandPacket.address[4] = address[4];
	}
	if(time_ms >= 1000)
		cmd.u.commandPacket.command = 2;
	else
		cmd.u.commandPacket.command = 3;
	cmd.u.commandPacket.data[0] = 0; // seconds msb
	cmd.u.commandPacket.data[1] = time_ms / 1000; // Seconds lsb
	cmd.u.commandPacket.data[2] = (time_ms % 1000) / 10; // time 10 ms
	cmd.u.commandPacket.data[3] = 0;
	cmd.u.commandPacket.data[4] = 30;
	tx.data = (uint8_t*)&cmd;
	tx.datalen = sizeof(cmd.control) + sizeof(cmd.u.commandPacket);
	tx.rcvdss = rcvdss;
	tx.lifetime = tx_lifetime;
	
	//printf("TX Contents: (len = %i, rvcvss = %i)\n", tx.datalen, tx.rcvdss);
	/*
	printf("%2.2x | %2.2x %2.2x %2.2x %2.2x %2.2x | %2.2x | %2.2x %2.2x %2.2x %2.2x %2.2x\n",
			tx.data[0], tx.data[1], tx.data[2], tx.data[3], tx.data[4], 
			tx.data[5], tx.data[6], tx.data[7], tx.data[8], tx.data[9],
			tx.data[10], tx.data[11]
		);
	*/
	
	fflush(stdout);
	ret = ioctl(fd, RADIO_SET_TX_DATA, &tx);
	if (ret) {
		perror("Cannot do ioctl RADIO_SET_TX_DATA");
	}
	return ret;
}

int radio_receive(int fd, int tx, int8_t rcvdss)
{
	fd_set rd_set;
	int ret;
	uint8_t buf[512];
	struct timeval timeout, interval, now;
	struct timeval timeout2, interval2;
	static int x = 0;
	static unsigned long prev_packets = 0;
	struct timeval select_to;

	interval.tv_sec = 1;
	interval.tv_usec = 0;
	interval2.tv_sec = 5;
	interval2.tv_usec = 0;

	gettimeofday(&timeout, NULL);
	timeradd(&timeout, &interval, &timeout);
	gettimeofday(&timeout2, NULL);

	while(1) {
		FD_ZERO(&rd_set);
		FD_SET(fd, &rd_set);

		select_to.tv_sec = 0;
		select_to.tv_usec = 100000;
		ret = select(fd + 1, &rd_set, NULL, NULL, &select_to);
		if (ret == 0) {
//			printf("Select: Timeout");
		}
		if (ret < 0) {
			printf("Select: Ret=%d, errno=%d: %s\n", ret, errno, strerror(errno));
		}
		if (ret > 0) {
			ret = read(fd, buf, sizeof(buf));
			if (ret == 0) {
				printf("Read returned 0\n");
			}
			if (ret < 0) {
				printf("Read: Ret=%d, errno=%d: %s\n", ret, errno, strerror(errno));
			}
			if (ret > 0) {
//				printf("Read: Got data: ret=%d\n", ret);
//				printf("Raw: \n");
//				radio_dump(buf, ret);
				radio_dump_message(fd, buf, ret, tx);
			}
		}
		gettimeofday(&now, NULL);
		if(timercmp(&now, &timeout, >)) {
			//				printf("\033[2J"); // Erase screen
			//				printf("\033[H"); // Cursor to top left
			printf("Packets: %lu  %lu packets/second. Bad seq packets %lu (%f), bad rcv seq packets %lu, cmd loopback %lu (%f).\n",
			       packets, packets - prev_packets, bad_seq,
			       (double)bad_seq / (double)packets,
			       bad_rcv_seq,
			       loopback_cmd_packets,
			       (double)loopback_cmd_packets / (double)packets);
			if(print_status_by_band)
				print_band_packets();
			prev_packets = packets;
			timeout = now;
			timeradd(&timeout, &interval, &timeout);
		}
		if(tx && timercmp(&now, &timeout2, >)) {
			x ^= 1;
			if(x)
				req_ping_interval = ping_interval1;
			else
				req_ping_interval = ping_interval2;
			if(all_points_bulletin) {
				radio_send_tx_data(fd, req_ping_interval, all_band_address, rcvdss);
			}
			else if(is_band_address(band_address)) {
				radio_send_tx_data(fd, req_ping_interval, band_address, rcvdss);
			}
			printf("Set TX to %d ms.\n", req_ping_interval);
			timeout2 = now;
			timeradd(&timeout2, &interval2, &timeout2);
		}
	}
}


void usage(void) {
	printf("radiotool [-d] [-s] [-b] [-f <band>] [-r <rcv-ss] command [arguments]\n");
	printf("Commands:\n");
	printf("radiotool freq <freq> <radio>[...]          tune radio to frequency\n");
	printf("radiotool freq                              get radio frequency from radios\n");
	printf("radiotool reset <radio>[...]                reset radios in list\n");
	printf("radiotool reset                             resets all radios\n");
	printf("radiotool loop <radio>                      loop data through spi for testing\n");
	printf("radiotool receive                           all rcvrs, report all bands\n");
	printf("radiotool receive <radio>[...]              receive bands\n");
	printf("radiotool command                           reply with command, all rcvrs, all bands\n");
	printf("radiotool command <radio>[...]              reply with command, if > ss\n");
	printf("radiotool version <radio>\n");
	printf("radiotool debug\n");
	printf("Options:\n");
	printf("    -d              dump driver messages\n");
	printf("    -s              print sequence number analysis\n");
	printf("    -b              print band status by band\n");
	printf("    -f <band_id>    filter by band id\n");
	printf("    -r <rcv-ss>     RX-TX turnaround signal strength filter: -127 (report ALL)) - 0 (report NONE)\n");
	printf("    -l <#bands>     stress-test the turnaround logic by injecting # bogus band ids into driver\n");
	printf("    -t <tx-freq>    set the TX frequency, defaults to 2482\n");
	printf("    -v              show radiotool version\n");
	printf("    --version       show radiotool version\n");
	printf("    -i <ms>         set ping interval 1 for command mode, default 200ms\n");
	printf("    -I <ms>         set ping interval 2 for command mode, default 1000ms\n");
	printf("    -L <s>          TX lifetime in seconds, defauls to 0\n");
}

int radio_version(int radio)
{
	struct RadioMessage command, reply;

	int fd, ret;
	fd = radio_open();
	if (fd < 0)
		return fd;

	memset(&command, 0, sizeof(command));
	memset(&reply, 0, sizeof(reply));

	command.control = RadioControl_GetVersion;
	ret = radio_read(fd, radio, &command, 0,
			 &reply, sizeof(reply.u.version));
	printf("Radio %d version = %s\n", radio, reply.u.version);
	return ret;
}

int radio_setpingtimeout(int fd, int radio, uint16_t timeout)
{
	struct RadioMessage command;

	memset(&command, 0, sizeof(command));
	command.control = RadioControl_Set_GetPingTimeout;
	command.u.pingTimeout = timeout;
	return radio_write(fd, radio, &command, sizeof(command.u.pingTimeout));
}

#define NR_RADIOS	8
#define PING_SZ		11

int radio_setpingsz(int fd, int radio, int size)
{
	struct radio_ping_sz sz;
	int ret;

	sz.radio = radio;
	sz.size = size;
	ret = ioctl(fd, RADIO_SET_PING_SZ, &sz);
	if(ret) {
		printf("Cannot set ping size to %d: %s\n", size, strerror(errno));
	}
	return ret;
}

int radio_init_tx(int fd, unsigned r)
{
	radio_reset(fd, r);
	printf("Tuning TX radio (%d) to %u MHz\n", r, tx_freq);
	usleep(3000000);
	return radio_setfreq(fd, r, tx_freq);
}

int radio_init_rx(int fd, unsigned nr, int *radios)
{
	int freq[8] = { 2401, 2424, 2450, 2476, 2401, 2424, 2450, 2476 };
	uint64_t i;
	uint8_t address[5] = { 0, 0, 0, 0, 0 };
	uint64_t a = 0x4368617264ULL;

	for(i = 0; i < nr; i++) {
		radio_reset(fd, radios[i]);
	}
	printf("Radios reset, waiting 3s...\n");
	usleep(3000000);

	for(i = 0; i < nr; i++) {
		uint16_t to = 4000;
		printf("------------------------------\n");
		printf("Radio %d - setpingsz %d\n", radios[i], PING_SZ);
		radio_setpingsz(fd, radios[i], PING_SZ);
		printf("Radio %d - setfreq %d\n", radios[i], freq[i]);
		radio_setfreq(fd, radios[i], freq[i]);
		printf("Radio %d - setpingtimeout %d\n", radios[i], to);
		radio_setpingtimeout(fd, radios[i], to);
		printf("Radio %d - RX enable\n", radios[i]);
		radio_rxenable(fd, radios[i], 1);
		printf("Radio %d - RX auto enable\n", radios[i]);
		radio_autorx_enable(fd, radios[i], 1);
	}
	if(load_test) {
		printf("Setting bogus TX data for %d bands.\n", load_test);
		for(i = a; i < (a + load_test); ++i) {
			address[0] = i >> 32;
			address[1] = i >> 24;
			address[2] = i >> 16;
			address[3] = i >> 8;
			address[4] = i & 0xFF;
			radio_send_tx_data(fd, 100, address, RADIO_TX_RCVSS_ALL);
		}
	}
	return 0;
}

int radio_receive_many(int fd, int *radios, int nr, int tx, int8_t rcvdss)
{
	radio_init_rx(fd, nr, radios);
	radio_receive(fd, tx, rcvdss);
	return 0;
}

int radio_receive_one(int fd, int radio, int tx, int8_t rcvdss)
{
	int radios[1] = { radio };
	radio_init_rx(fd, 1, radios);
	radio_receive(fd, tx, rcvdss);

	return 0;
}

int radio_receive_all(int fd, int tx, int8_t rcvdss)
{
	int radios[8] = { 0, 1, 2, 3, 4, 5, 6, 7 };
	radio_init_rx(fd, 8, radios);
	radio_receive(fd, tx, rcvdss);
	return 0;
}

int radio_info(int fd)
{
	struct radio_info info;
	int ret;

	ret = ioctl(fd, RADIO_GET_INFO, &info);
	if(ret < 0) {
		printf("Cannot get radio info: %s\n", strerror(errno));
		return ret;
	}
	printf("Grover radio info:\n");
	printf("\tFeatures: %08x\n", info.features);
	if(info.features & RADIO_FEATURE_RX)
		printf("\t\t\t(rx)\n");
	if(info.features & RADIO_FEATURE_TX)
		printf("\t\t\t(tx)\n");
	printf("\tVersion: %s\n", info.version);
	printf("\tRX radios: %d\n", info.rx_radios);
	return 0;
}

int parse_radios(int *radioList, int argc, char *argv[], int arg)
{
	int count = 0;
	while (arg < argc && count < 10)
	{
		*radioList++ = strtoul(argv[arg], NULL, 10);
		arg++;
		count++;
	}
	return count;
}

static void version(void)
{
	printf("%s\n", VERSION);
}

int main(int argc, char *argv[])
{
	unsigned long radio, value;
	int c;
	int command = 0, command_one = 0;
	int radios[9];
	int8_t rcvdss = RADIO_TX_RCVSS_ALL;

	if(argc < 2) {
		usage();
		return 0;
	}

	if(strcmp(argv[1], "--version") == 0 ||
	   strcmp(argv[1], "-v") == 0) {
		version();
		return 0;
	}

	while ((c = getopt (argc, argv, "l:dsbaf:r:t:vi:I:L:")) != -1) {
		switch (c) {
			case 'l':
				load_test = strtoul(optarg, NULL, 10);
				break;
			case 'd':
				dump_messages = 1;
				break;
			case 's':
				print_sequece_errors = 1;
				break;
			case 'b':
				print_status_by_band = 1;
				break;
			case 'a':
				all_points_bulletin = 1;
				break;
			case 'f':
				filter = 1;
				my_bands.insert(strtoull(optarg, NULL, 16));
				break;
			case 'r':
				rcvdss = strtol(optarg, NULL, 10);
				break;
			case 't':
				tx_freq = strtol(optarg, NULL, 10);
				break;
			case 'i':
				ping_interval1 = strtol(optarg, NULL, 10);
				req_ping_interval = ping_interval1;
				break;
			case 'I':
				ping_interval2 = strtol(optarg, NULL, 10);
				break;
			case 'L':
				tx_lifetime = strtol(optarg, NULL, 10);
				break;
		}
	}

	if(strcmp(argv[1], "reset") == 0) {

		int fd = radio_open();
		if (fd < 0)
			return fd;

		if(argc == 2) {
			for(int i = 0; i < 9; i++)
				radio_reset(fd, i);
		}

		int cnt = parse_radios(radios, argc, argv, 2);

		for(int i = 0; i < cnt; i++)
			radio_reset(fd, radios[i]);
		return 0;
	}

	if(strcmp(argv[1], "freq") == 0) {
		int fd = radio_open();
		if (fd < 0)
			return fd;

		if(argc == 2) {							// get all radios
			for (int i=0;i<9;i++) {
				radio_getfreq(fd, i);
			}
		}
		else if(argc == 3) {					// illegal
			usage();
			return 0;
		}
		else {
			value = strtoul(argv[3], NULL, 10);
			int cnt = parse_radios(radios, argc, argv, 4);
			for(int i = 0; i < cnt; i++){
				radio_setfreq(fd, radios[i], value);
			}
		}
		return 0;
	}

	// command and receive commands are very similar, so use same code block.
	command = 0;

	if(strcmp(argv[optind], "command") == 0) {
		command = 1;
	}

	if(strcmp(argv[optind], "command1") == 0) {
		command_one = 1;
	}
	
	if(strcmp(argv[optind], "receive") == 0 || command || command_one) {
		int receive_all = 0;
		optind++;

		int fd = radio_open();
		if (fd < 0)
			return fd;

		if(argc == optind) {
			receive_all = 1;
		}

		if(command || command_one) {
			radio_init_tx(fd, TX_RADIO);
			// Pre-set the TX data if we know the address of the band
			if(filter) {
				for(std::set<uint64_t>::const_iterator i = my_bands.begin();
				    i != my_bands.end();
				    ++i) {
					radio_send_tx_data(fd, req_ping_interval, *i, RADIO_TX_RCVSS_ALL);
				}
			}
		}

		if(receive_all) {
			radio_receive_all(fd, command, rcvdss);
		} else {
			int cnt = parse_radios(radios, argc, argv, optind);
			radio_receive_many(fd, radios, cnt, command, rcvdss);
		}
		return 0;
	}

	if(strcmp(argv[1], "loop") == 0) {
		if(argc != 3) {
			usage();
			return 0;
		}
		radio = strtoul(argv[2], NULL, 10);
		radio_loop(radio);
		return 0;
	}

	if(strcmp(argv[1], "version") == 0) {
		if(argc != 3 && argc != 2) {
			usage();
			return 0;
		}
		if(argc == 2) {
			for(radio = 0; radio < 9; radio++)
				radio_version(radio);
		} else {
			radio = strtoul(argv[2], NULL, 10);
			radio_version(radio);
		}
		return 0;
	}

	if(strcmp(argv[1], "info") == 0) {
		int fd = radio_open();
		if (fd < 0)
			return fd;
		radio_info(fd);
	}
	return 0;
}
