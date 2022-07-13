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
#include <sstream>
#include <iostream>
#include <fstream>

#include "grover_radio.h"
#include "ihex.h"

#define ARRAY_SIZE(a) (sizeof(a)/sizeof(a[0]))
#define MAX_FLASH_CMD_SIZE	3

// DEBUG commands
#define CHIP_ERASE	0x10
#define WR_CONFIG	0x18
#define RD_CONFIG	0x20
#define GET_PC		0x28
#define READ_STATUS	0x30
#define SET_HW_BRKPNT	0x38
#define HALT		0x40
#define RESUME		0x48
#define DEBUG_INSTR	0x50
#define STEP_INSTR	0x58
#define GET_BM		0x60
#define GET_CHIP_ID	0x68
#define BURST_WRITE	0x80

#define SRAM_SIZE 1024
#define WORDSIZE_8BIT (0 << 7)
#define TMODE_SINGLE (0 << 5)
#define DMA_TRIGGER_DBG_BW  31
#define DMA_TRIGGER_FLASH 18
#define INC_NONE 0
#define INC_PLUS_1_BYTE 1
#define SRC_INC 6
#define DST_INC 4
#define DMA_PRIORITY_HIGH 2

#define DBGDATA 0x6260
#define FWDATA 0x6273
#define FADDRH 0x6272
#define FADDRL 0x6271
#define CHIPINFO0 0x6276
#define CHIPINFO1 0x6277
#define DMA0CFGH 0xD5
#define DMA0CFGL 0xD4
#define DMAARM 0xD6
#define FCTL 0x6270

#define XDATA_FLASH_START	0x8000
#define CALDATA_OFFSET		0x7400
#define CALDATA_SIZE		4

// Flash image address in XDATA
#define FLASH_IMAGE_ADDR 0x0000
// DMA data structure address in XDATA
#define DMA_DATA_ADDR_XDATA (IHEX_PAGE_SIZE)

#define RADIO_ALL		0xFFFF

static unsigned nr_radios = 9; /* default to 8 radios */

static unsigned opt_verbose = 0;

int radio_do_debug_access(int fd,
			  uint8_t *tx, unsigned txlen,
			  uint8_t *rx, unsigned rxlen)
{
	int ret;
	struct radio_ioc_debug_tx_rx d;

	d.tx = tx;
	d.txlen = txlen;
	d.rx = rx;
	d.rxlen = rxlen;

	ret = ioctl(fd, RADIO_DEBUG_TX_RX, &d);
	if(ret) {
		printf("Cannot do RADIO_DEBUG_TX_RX: %s\n", strerror(errno));
	}
	return ret;
}

int radio_read_status(int fd, uint8_t *status)
{
	uint8_t tx[1] = { READ_STATUS };
	return radio_do_debug_access(fd, tx, 1, status, 1);
}

int radio_debug_instruction(int fd, uint8_t *opcode, unsigned len, uint8_t *accu)
{
	uint8_t operation[4];
	uint8_t scratch[1];

	operation[0] = DEBUG_INSTR | len;
	memcpy(&operation[1], opcode, len);

	return radio_do_debug_access(fd, operation, 1 + len, accu ? accu : scratch, 1);
}

int radio_debug_start(int fd, int radio)
{
	int ret;

	ret = ioctl(fd, RADIO_DEBUG_START, &radio);
	if(ret) {
		printf("Cannot do RADIO_DEBUG_START: %s\n", strerror(errno));
	}
	return ret;
}

int radio_debug_stop(int fd, int radio)
{
	int ret;

	ret = ioctl(fd, RADIO_DEBUG_STOP, &radio);
	if(ret) {
		printf("Cannot do RADIO_DEBUG_STOP: %s\n", strerror(errno));
	}
	return ret;
}

int radio_debug_reset(int fd, int radio)
{
	int ret;

	ret = ioctl(fd, RADIO_DEBUG_RESET, &radio);
	if(ret) {
		printf("Cannot do RADIO_DEBUG_RESET: %s\n", strerror(errno));
	}
	return ret;
}

int radio_read_chip_id(int fd, uint8_t *id)
{
	uint8_t tx[1];

	tx[0] = GET_CHIP_ID;
	return radio_do_debug_access(fd, tx, sizeof(tx), id, 2);
}

int radio_open()
{
	int fd;
	int ret;
	struct radio_debug_info info;
	const char debug[] = "/dev/grover_debug";
	const char grover[] = "/dev/grover_radio";
	const char *dev;

	/* First try to open "grover_debug", then fall back to "grover_radio" */

	dev = debug;
	fd = open("/dev/grover_debug", O_RDWR);
	if (fd < 0) {
		dev = grover;
		fd = open("/dev/grover_radio", O_RDWR);
		if (fd < 0) {
			printf("Cannot open radio: %s\n", strerror(errno));
			return fd;
		}
	}
	/* Check for amount of radios */
	ret = ioctl(fd, RADIO_DEBUG_GET_INFO, &info);
	if(ret < 0) {
		printf("Debug interface not supported on %s!\n", dev);
		close(fd);
		return ret;
	}
	nr_radios = info.nr_radios;
	printf("Found %u radios using %s: %s\n", info.nr_radios, dev, info.version);

	return fd;
}

void usage()
{
	printf("radioflasher <args> ...\n");
	printf("Arguments:\n");
	printf("\t -r <radio> \t\t- Select radio\n");
	printf("\t -a \t\t- Select all radios\n");
	printf("\t -s \t\tStart debug mode\n");
	printf("\t -t \t\tStop debug mode\n");
	printf("\t -R \t\tReset\n");
	printf("\t -v <mode> \t\tVerbose messages\n");

	printf("\t -f <filename> \t\tFlash file\n");
	printf("\t -V <filename> \t\tVerify flash against file\n");
	printf("\t -c\t\tRead cal data\n");
}

int radio_chip_erase(int fd, uint8_t *status)
{
	uint8_t tx[1] = { CHIP_ERASE };

	return radio_do_debug_access(fd, tx, 1, status, 1);
}


uint8_t dma_data_structures[2][8] = {
	{
		// Debug interface -> SRAM
		DBGDATA >> 8,					// src high
		DBGDATA & 0xFF,					// src low
		FLASH_IMAGE_ADDR >> 8,				// dst high
		FLASH_IMAGE_ADDR & 0xFF,			// dst low
		0,						// data len high byte
		0,						// data len low byte
		WORDSIZE_8BIT | TMODE_SINGLE | DMA_TRIGGER_DBG_BW,
		INC_NONE << SRC_INC | INC_PLUS_1_BYTE << DST_INC | DMA_PRIORITY_HIGH,
	},
	{
		// SRAM -> Flash
		FLASH_IMAGE_ADDR >> 8,				// src high
		FLASH_IMAGE_ADDR & 0xFF,			// src low
		FWDATA >> 8,					// dst high
		FWDATA & 0xFF,					// dst low
		0, 						// data len high byte
		0,						// data len low byte
		WORDSIZE_8BIT | TMODE_SINGLE | DMA_TRIGGER_FLASH,
		INC_PLUS_1_BYTE << SRC_INC | INC_NONE << DST_INC | DMA_PRIORITY_HIGH,
	},
};

static int load_dptr(int fd, uint16_t address)
{
	uint8_t opcode[3];

	// MOV DPTR, address
	opcode[0] = 0x90;
	opcode[1] = address >> 8;
	opcode[2] = address & 0xff;
	return radio_debug_instruction(fd, opcode, 3, NULL);
}

static void write_xdata_memory_block(int fd, uint16_t address,
				     uint8_t *values, uint16_t num_bytes)
{
	uint8_t opcode[3];
	unsigned i;

	load_dptr(fd, address);

	for(i = 0; i < num_bytes; i++) {
		// MOV A, immediate
		opcode[0] = 0x74;
		opcode[1] = values[i];
		radio_debug_instruction(fd, opcode, 2, NULL);

		// MOV @DPTR, A
		opcode[0] = 0xF0;
		radio_debug_instruction(fd, opcode, 1, NULL);

		// INC DPTR
		opcode[0] = 0xA3;
		radio_debug_instruction(fd, opcode, 1, NULL);
	}
}

static void write_xdata_memory(int fd, uint16_t address,
			       uint8_t value)
{
	return write_xdata_memory_block(fd, address, &value, 1);
}

static void read_xdata_memory_block(int fd, uint16_t address,
				    uint8_t *values, uint16_t num_bytes)
{
	uint8_t opcode[1];
	uint8_t accu;
	unsigned i;

	load_dptr(fd, address);

	for(i = 0; i < num_bytes; i++) {
		// MOVX A, @DPTR
		opcode[0] = 0xE0;
		radio_debug_instruction(fd, opcode, 1, &accu);

		values[i] = accu;

		// INC DPTR
		opcode[0] = 0xA3;
		radio_debug_instruction(fd, opcode, 1, NULL);
	}
}

static uint8_t read_xdata_memory(int fd, uint16_t address)
{
	uint8_t opcode[1];
	uint8_t accu = 0x80;

	load_dptr(fd, address);

	// MOVX A, @DPTR
	opcode[0] = 0xE0;
	radio_debug_instruction(fd, opcode, 1, &accu);
	return accu;
}

static void write_data_memory(int fd, uint8_t address, uint8_t value)
{
	uint8_t opcode[3];

	// MOV address, immediate
	opcode[0] = 0x75;
	opcode[1] = address;
	opcode[2] = value;
	radio_debug_instruction(fd, opcode, 3, NULL);
}

void radio_configure_dma_channel(int fd, unsigned datalen, uint8_t *dma_data)
{
	dma_data[4] = datalen >> 8;
	dma_data[5] = datalen & 0xFF;

	// Send DMA data structure
	write_xdata_memory_block(fd, DMA_DATA_ADDR_XDATA, dma_data, 8);
	// Configure DMA to use data structure
	write_data_memory(fd, DMA0CFGH, DMA_DATA_ADDR_XDATA >> 8);
	write_data_memory(fd, DMA0CFGL, DMA_DATA_ADDR_XDATA & 0x00FF);
}

void radio_read_chip_info(int fd, uint8_t chip_info[2])
{
	chip_info[0] = read_xdata_memory(fd, CHIPINFO0);
	chip_info[1] = read_xdata_memory(fd, CHIPINFO1);
}

void radio_arm_dma_channel(int fd, unsigned channel)
{
	// Arm DMA
	write_data_memory(fd, DMAARM, 1 << channel);
}

void radio_configure_dma_to_sram(int fd, unsigned datalen)
{
	// Debug port -> SRAM
	radio_configure_dma_channel(fd, datalen, dma_data_structures[0]);
}

void radio_configure_dma_to_flash(int fd, unsigned datalen)
{
	// SRAM -> Flash
	radio_configure_dma_channel(fd, datalen, dma_data_structures[1]);
}

int radio_burst_write(int fd, uint8_t *data, unsigned datalen)
{
	uint8_t status;
	uint8_t buf[IHEX_PAGE_SIZE + 2];

	buf[0] = BURST_WRITE | (datalen >> 8);
	buf[1] = datalen & 0xff;
	memcpy(&buf[2], data, datalen);

	return radio_do_debug_access(fd, buf, datalen + 2, &status, 1);
}

void radio_start_flashing(int fd, unsigned byte_offset)
{
	write_xdata_memory(fd, FADDRH, (byte_offset >> 2) >> 8);
	write_xdata_memory(fd, FADDRL, (byte_offset >> 2) & 0xFF);
	radio_arm_dma_channel(fd, 0);
	write_xdata_memory(fd, FCTL, 0x02);
}

/* Configure DMA to move data from debug port to SRAM,
 * then use debug command BURST_WRITE to stream data to 
 * debug port */
void radio_transfer_to_sram(int fd, uint8_t *data, unsigned datalen)
{
	radio_configure_dma_to_sram(fd, datalen);
	radio_arm_dma_channel(fd, 0);
	radio_burst_write(fd, data, datalen);
}

/* Configure DMA to move data from SRAM to flash,
 * program flash for writing and enable */
void radio_flash_from_sram(int fd, unsigned byte_offset, unsigned datalen)
{
	unsigned iterations = 100;
	radio_configure_dma_to_flash(fd, datalen);
	radio_start_flashing(fd, byte_offset);

	while(read_xdata_memory(fd, FCTL) & 0x80) {
		// Wait for flashing to finish
		if(--iterations == 0) {
			printf("Flashing timed out!\n");
			break;
		}
		usleep(10 * 1000);
	}
}

int radio_verify_page(int fd, uint8_t *data, unsigned byte_offset, unsigned datalen)
{
	uint8_t buf[IHEX_PAGE_SIZE];

	read_xdata_memory_block(fd, byte_offset + XDATA_FLASH_START, buf, datalen);
	if(memcmp(buf, data, datalen)) {
		for(unsigned i = 0; i < datalen; i++) {
			if(buf[i] != data[i]) {
				printf("Data at %08x differs, got %02x, expected %02x\n",
				       byte_offset + i, buf[i], data[i]);
			}
		}
		printf("Verify fails at byte offset %08x.!\n", byte_offset);
		return -1;
	}
	return 0;
}

static void read_caldata(int fd, uint8_t *data)
{
	read_xdata_memory_block(fd, 
				CALDATA_OFFSET + XDATA_FLASH_START, 
				data, 
				CALDATA_SIZE);
}

void flash_chunk(int fd, unsigned byte_offset, uint8_t *data, unsigned datalen)
{
	radio_transfer_to_sram(fd, data, datalen);
	radio_flash_from_sram(fd, byte_offset, datalen);
}

int verifyPage(int fd, unsigned ix, page_t &page)
{
	unsigned byte_address = page.min;
	unsigned datalen = (page.max - page.min) + 1;
	// align to dword boundary
	byte_address &= ~0x3;

	datalen += (page.min - byte_address); // pad in front
	datalen = (datalen + 3) & ~3; // pad in back

	return radio_verify_page(fd,
				 &page.data[byte_address],
				 (ix * IHEX_PAGE_SIZE) + byte_address,
				 datalen);
}

void flashPage(int fd, unsigned ix, page_t &page)
{
	unsigned byte_address = page.min;
	unsigned datalen = (page.max - page.min) + 1;
	// align to dword boundary
	byte_address &= ~0x3;

	datalen += (page.min - byte_address); // pad in front
	datalen = (datalen + 3) & ~3; // pad in back

	flash_chunk(fd, (ix * IHEX_PAGE_SIZE) + byte_address,
		    &page.data[byte_address], datalen);
}

static int radio_debug_read_config(int fd, uint8_t *config)
{
	uint8_t tx[1] = { RD_CONFIG };

	return radio_do_debug_access(fd, tx, 1, config, 1);
}

static int radio_debug_write_config(int fd, uint8_t config)
{
	uint8_t tx[2] = { WR_CONFIG, config };
	uint8_t status;

	return radio_do_debug_access(fd, tx, 2, &status, 1);
}


int radio_debug_enable_dma(int fd)
{
	return radio_debug_write_config(fd, 0x22);
}

static int radio_debug_init(int fd, int radio)
{
	uint8_t chip_id[2];
	uint8_t chip_info[2];
	uint8_t config;
	uint8_t status;

	// Force radio to debug mode
	radio_debug_start(fd, radio);
	// Must send NOP first, otherwise bad things happen
	uint8_t nop = 0x00;
	radio_debug_instruction(fd, &nop, 1, NULL);

	// Read Status
	radio_read_chip_id(fd, chip_id);

	if(chip_id[0] == 0x43) {
		printf("Found CC2543\n");
	}
	else {
		printf("Unknown CHIP ID %02x\n", chip_id[0]);
		return -1;
	}
	printf("\tversion \t - %02x\n", chip_id[1]);
	radio_read_chip_info(fd, chip_info);
	printf("\tflash size\t - %d kB\n", (chip_info[0] & 0x70) == 0x10 ? 18 : 32);
	printf("\tSRAM size\t - %d kB\n", (chip_info[1] & 0x07) ? 2 : 1);
	printf("\tUSB\t\t - %s\n", chip_info[0] & 0x08 ? "yes" : "no");

	if(opt_verbose > 0)
		printf("Chip info: %02x %02x\n", chip_info[0], chip_info[1]);
	radio_debug_read_config(fd, &config);
	if(opt_verbose > 0)
		printf("Debug config %02x\n", config);
	radio_read_status(fd, &status);
	if(opt_verbose > 0)
		printf("Debug status %02x\n", status);

	// Force radio again into debug mode since reading CHIP_ID disables erase.
	radio_debug_start(fd, radio);
	return 0;
}

int radio_verify(int fd, int radio, pagemap_t &pagemap)
{
	int ret;

	if((ret = radio_debug_init(fd, radio)))
		return ret;

	ret = 0;
	for(pagemap_t::iterator i = pagemap.begin(); i != pagemap.end(); ++i) {
		ret |= verifyPage(fd, i->first, i->second);
	}
	radio_debug_stop(fd, radio);
	return ret;
}

int flash_radio(int fd, int radio, pagemap_t &pagemap)
{
	int ret;
	uint8_t status;
	unsigned iterations = 100;
	uint8_t caldata[CALDATA_SIZE];

	// Enter debug mode
	if((ret = radio_debug_init(fd, radio)))
		return ret;

	// Save caldata
	read_caldata(fd, caldata);
	// Push caldata into pagemap
	page_t &page = pagemap[CALDATA_OFFSET / IHEX_PAGE_SIZE];
	std::copy(caldata, caldata + CALDATA_SIZE, &page.data[0]);

	// erase chip
	printf("Erasing... ");
	radio_chip_erase(fd, &status);
	// Wait for erase to finish
	while(status & 0x80) {
		radio_read_status(fd, &status);
		if(--iterations == 0) {
			printf("Timed out waiting erase to finish\n");
			return -ETIMEDOUT;
			break;
		}
		usleep(10 * 1000);
	}
	printf("Finished\n");
	// Enable DMA
	radio_debug_enable_dma(fd);
	// flash per page

	printf("Flashing");
	fflush(stdout);
	for(pagemap_t::iterator i = pagemap.begin(); i != pagemap.end(); ++i) {
		flashPage(fd, i->first, i->second);
		printf(".");
		fflush(stdout);
	}
	printf("Finished\n");
	printf("Verifying");
	fflush(stdout);
	for(pagemap_t::iterator i = pagemap.begin(); i != pagemap.end(); ++i) {
		verifyPage(fd, i->first, i->second);
		printf(".");
		fflush(stdout);
	}
	printf("Finished\n");
	radio_debug_stop(fd, radio);
	return 0;
}

int radio_read_caldata(int fd, unsigned radio)
{
	int ret;
	uint8_t caldata[CALDATA_SIZE];

	if((ret = radio_debug_init(fd, radio)))
		return ret;
	read_caldata(fd, caldata);
	radio_debug_stop(fd, radio);
	printf("Calibration data: ");
	for(unsigned i = 0; i < CALDATA_SIZE; i++)
		printf("%02x ", caldata[i]);
	printf("\n");
	return 0;
}


int main(int argc, char *argv[])
{
	int c, fd, radio = -1;
	ihexreader hexreader;
	pagemap_t *pagemap;
	unsigned opt_reset = 0;
	unsigned opt_start = 0;
	unsigned opt_stop = 0;
	char *opt_file = NULL;
	char *opt_verify = NULL;
	int opt_caldata = 0;
	unsigned opt_info = 0;

	if(argc < 2) {
		usage();
		return 0;
	}

	if(strcmp(argv[1], "--version") == 0 ||
	   strcmp(argv[1], "-v" ) == 0) {
		printf("%s\n", VERSION);
		return 0;
	}

	while ((c = getopt (argc, argv, "str:Raf:V:ci")) != -1) {
		switch(c) {
			case 'r':
				radio = strtoul(optarg, NULL, 10);
				break;
			case 'R':
				opt_reset = 1;
				break;
			case 's':
				opt_start = 1;
				break;
			case 't':
				opt_stop = 1;
				break;
			case 'f':
				opt_file = optarg;
				break;
			case 'a':
				radio = RADIO_ALL;
				break;
			case 'V':
				opt_verify = optarg;
				break;
			case 'c':
				opt_caldata = 1;
				break;
			case 'i':
				opt_info = 1;
				break;
		}
	}

	if(radio == -1) {
		printf("Select radio to act on\n");
		return 0;
	}

	fd = radio_open();
	if(fd < 0) {
		return 0;
	}

	if(radio == RADIO_ALL) {
		if(opt_info) {
			for(unsigned i = 0; i < nr_radios; i++) {
				// Enter debug mode
				if(radio_debug_init(fd, i))
					printf("Could not ID radio %u\n", i);
				radio_debug_stop(fd, i);
			}
			return 0;
		}
		if(opt_file) {
			pagemap = hexreader.read(opt_file);
			if(!pagemap)
				return 0;
			for(unsigned i = 0; i < nr_radios; i++)
				flash_radio(fd, i, *pagemap);
			return 0;
		}
		if(opt_reset) {
			printf("Reset\n");
			for(unsigned i = 0; i < nr_radios; i++)
				radio_debug_reset(fd, i);
			return 0;
		}
		printf("Do What?\n");
	} else if(radio >= 0 && radio < (int)nr_radios) {
		if(opt_info) {
			if(radio_debug_init(fd, radio))
				printf("Could not ID radio %d\n", radio);
			radio_debug_stop(fd, radio);
			return 0;
		}
		if(opt_verify) {
			pagemap = hexreader.read(opt_verify);
			if(!pagemap)
				return 0;
			radio_verify(fd, radio, *pagemap);
			return 0;
		}
		if(opt_file) {
			pagemap = hexreader.read(opt_file);
			if(!pagemap)
				return 0;
			return flash_radio(fd, radio, *pagemap);
		}
		if(opt_start) {
			printf("Start\n");
			return radio_debug_start(fd, radio);
		}
		if(opt_stop) {
			printf("Stop\n");
			return radio_debug_stop(fd, radio);
		}
		if(opt_reset) {
			printf("Reset\n");
			return radio_debug_reset(fd, radio);
		}
		if(opt_caldata) {
			return radio_read_caldata(fd, radio);
		}
		printf("Do What?\n");
	}
	else {
		printf("Bad radio number %d\n", radio);
	}
	return 0;
}
