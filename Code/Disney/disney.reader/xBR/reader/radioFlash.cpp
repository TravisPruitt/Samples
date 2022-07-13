//*****************************************************************************
// radioflasher.cpp
//
// Handles flashing of radio firmware
//*****************************************************************************
//
//	Copyright 2012, Synapse.com
//
//*****************************************************************************

#include <stdlib.h>
#include <sys/ioctl.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <map>
#include <vector>
#include <string>
#include <iostream>
#include <fstream>

#include "grover.h"
#include "FileSystem.h"
#include "radioFlash.h"
#include "grover_radio.h"
#include "ihex.h"
#include "log.h"
#include "Radios.h"
#include "ReaderInfo.h"

#define ARRAY_SIZE(a) (sizeof(a)/sizeof(a[0]))
#define MAX_FLASH_CMD_SIZE    3

// DEBUG commands
#define CHIP_ERASE    0x10
#define WR_CONFIG    0x18
#define RD_CONFIG    0x20
#define GET_PC        0x28
#define READ_STATUS    0x30
#define SET_HW_BRKPNT    0x38
#define HALT        0x40
#define RESUME        0x48
#define DEBUG_INSTR    0x50
#define STEP_INSTR    0x58
#define GET_BM        0x60
#define GET_CHIP_ID    0x68
#define BURST_WRITE    0x80

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

#define XDATA_FLASH_START    0x8000
#define CALDATA_OFFSET        0x7400
#define CALDATA_SIZE        4

// Flash image address in XDATA
#define FLASH_IMAGE_ADDR 0x0000
// DMA data structure address in XDATA
#define DMA_DATA_ADDR_XDATA (IHEX_PAGE_SIZE)

using namespace Reader;

// Path to debug interface
static const char* const radio_debug_path = "/dev/grover_debug";


namespace radioFlash
{

uint8_t dma_data_structures[2][8] = {
    {
        // Debug interface -> SRAM
        DBGDATA >> 8,                    // src high
        DBGDATA & 0xFF,                    // src low
        FLASH_IMAGE_ADDR >> 8,                // dst high
        FLASH_IMAGE_ADDR & 0xFF,            // dst low
        0,                        // data len high byte
        0,                        // data len low byte
        WORDSIZE_8BIT | TMODE_SINGLE | DMA_TRIGGER_DBG_BW,
        INC_NONE << SRC_INC | INC_PLUS_1_BYTE << DST_INC | DMA_PRIORITY_HIGH,
    },
    {
        // SRAM -> Flash
        FLASH_IMAGE_ADDR >> 8,                // src high
        FLASH_IMAGE_ADDR & 0xFF,            // src low
        FWDATA >> 8,                    // dst high
        FWDATA & 0xFF,                    // dst low
        0,                         // data len high byte
        0,                        // data len low byte
        WORDSIZE_8BIT | TMODE_SINGLE | DMA_TRIGGER_FLASH,
        INC_PLUS_1_BYTE << SRC_INC | INC_NONE << DST_INC | DMA_PRIORITY_HIGH,
    },
};


static int radio_do_debug_access(int fd,
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
        LOG_DEBUG("Cannot do RADIO_DEBUG_TX_RX: %s", strerror(errno));
    }
    return ret;
}

static int radio_read_status(int fd, uint8_t *status)
{
    uint8_t tx[1] = { READ_STATUS };
    return radio_do_debug_access(fd, tx, 1, status, 1);
}

static int radio_debug_instruction(int fd, uint8_t *opcode, unsigned len, uint8_t *accu)
{
    uint8_t operation[4];
    uint8_t scratch[1];

    operation[0] = DEBUG_INSTR | len;
    memcpy(&operation[1], opcode, len);

    return radio_do_debug_access(fd, operation, 1 + len, accu ? accu : scratch, 1);
}

static int radio_debug_start(int fd, int radio)
{
    int ret;

    ret = ioctl(fd, RADIO_DEBUG_START, &radio);
    if(ret) {
        LOG_DEBUG("Cannot do RADIO_DEBUG_START: %s", strerror(errno));
    }
    return ret;
}

static int radio_debug_stop(int fd, int radio)
{
    int ret;

    ret = ioctl(fd, RADIO_DEBUG_STOP, &radio);
    if(ret) {
        LOG_DEBUG("Cannot do RADIO_DEBUG_STOP: %s", strerror(errno));
    }
    return ret;
}

static int radio_debug_get_info(int fd, radio_debug_info *info)
{
    int ret;

    ret = ioctl(fd, RADIO_DEBUG_GET_INFO, info);
    if(ret < 0)
    {
        LOG_DEBUG("Cannot do RADIO_DEBUG_GET_INFO: %s", strerror(errno));
    }
    return ret;
}

static int radio_read_chip_id(int fd, uint8_t *id)
{
    uint8_t tx[1];

    tx[0] = GET_CHIP_ID;
    return radio_do_debug_access(fd, tx, sizeof(tx), id, 2);
}

static int radio_open()
{
    int fd;
    int ret;
    struct radio_debug_info info;

    fd = open(radio_debug_path, O_RDWR);
    if (fd < 0) {
        return fd;
    }

    /* Check for amount of radios */
    ret = radio_debug_get_info(fd, &info);
    if(ret < 0)
    {
        close(fd);
        return ret;
    }
    LOG_DEBUG("Found %u radios: %s\n", info.nr_radios, info.version);

    return fd;
}

static int radio_chip_erase(int fd, uint8_t *status)
{
    uint8_t tx[1] = { CHIP_ERASE };

    return radio_do_debug_access(fd, tx, 1, status, 1);
}


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

static void radio_configure_dma_channel(int fd, unsigned datalen, uint8_t *dma_data)
{
    dma_data[4] = datalen >> 8;
    dma_data[5] = datalen & 0xFF;

    // Send DMA data structure
    write_xdata_memory_block(fd, DMA_DATA_ADDR_XDATA, dma_data, 8);
    // Configure DMA to use data structure
    write_data_memory(fd, DMA0CFGH, DMA_DATA_ADDR_XDATA >> 8);
    write_data_memory(fd, DMA0CFGL, DMA_DATA_ADDR_XDATA & 0x00FF);
}

static void radio_read_chip_info(int fd, uint8_t chip_info[2])
{
    chip_info[0] = read_xdata_memory(fd, CHIPINFO0);
    chip_info[1] = read_xdata_memory(fd, CHIPINFO1);
}

static void radio_arm_dma_channel(int fd, unsigned channel)
{
    // Arm DMA
    write_data_memory(fd, DMAARM, 1 << channel);
}

static void radio_configure_dma_to_sram(int fd, unsigned datalen)
{
    // Debug port -> SRAM
    radio_configure_dma_channel(fd, datalen, dma_data_structures[0]);
}

static void radio_configure_dma_to_flash(int fd, unsigned datalen)
{
    // SRAM -> Flash
    radio_configure_dma_channel(fd, datalen, dma_data_structures[1]);
}

static int radio_burst_write(int fd, uint8_t *data, unsigned datalen)
{
    uint8_t status;
    uint8_t buf[IHEX_PAGE_SIZE + 2];

    buf[0] = BURST_WRITE | (datalen >> 8);
    buf[1] = datalen & 0xff;
    memcpy(&buf[2], data, datalen);

    return radio_do_debug_access(fd, buf, datalen + 2, &status, 1);
}

static void radio_start_flashing(int fd, unsigned byte_offset)
{
    write_xdata_memory(fd, FADDRH, (byte_offset >> 2) >> 8);
    write_xdata_memory(fd, FADDRL, (byte_offset >> 2) & 0xFF);
    radio_arm_dma_channel(fd, 0);
    write_xdata_memory(fd, FCTL, 0x02);
}

/* Configure DMA to move data from debug port to SRAM,
 * then use debug command BURST_WRITE to stream data to 
 * debug port */
static int radio_transfer_to_sram(int fd, uint8_t *data, unsigned datalen)
{
    radio_configure_dma_to_sram(fd, datalen);
    radio_arm_dma_channel(fd, 0);
    return radio_burst_write(fd, data, datalen);
}

/* Configure DMA to move data from SRAM to flash,
 * program flash for writing and enable */
static int radio_flash_from_sram(int fd, unsigned byte_offset, unsigned datalen)
{
    unsigned iterations = 100;
    radio_configure_dma_to_flash(fd, datalen);
    radio_start_flashing(fd, byte_offset);

    while(read_xdata_memory(fd, FCTL) & 0x80) {
        // Wait for flashing to finish
        if(--iterations == 0) {
            LOG_TRAFFIC("Flashing timed out!");
            return -1;
        }
        usleep(10 * 1000);
    }
    return 0;
}

static int radio_verify_page(int fd, uint8_t *data, unsigned byte_offset, unsigned datalen)
{
    uint8_t buf[IHEX_PAGE_SIZE];
    
    read_xdata_memory_block(fd, byte_offset + XDATA_FLASH_START, buf, datalen);
    if(memcmp(buf, data, datalen)) {
        for(unsigned i = 0; i < datalen; i++) {
            if(buf[i] != data[i]) {
                LOG_TRAFFIC("Data at %08x differs, got %02x, expected %02x",
                            byte_offset + i, buf[i], data[i]);
            }
        }
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

static int flash_chunk(int fd, unsigned byte_offset, uint8_t *data, unsigned datalen)
{
    if (radio_transfer_to_sram(fd, data, datalen) < 0)
        return -1;
    return radio_flash_from_sram(fd, byte_offset, datalen);
}

static int verifyPage(int fd, unsigned ix, page_t &page)
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

static int flashPage(int fd, unsigned ix, page_t &page)
{
    unsigned byte_address = page.min;
    unsigned datalen = (page.max - page.min) + 1;
    // align to dword boundary
    byte_address &= ~0x3;

    datalen += (page.min - byte_address); // pad in front
    datalen = (datalen + 3) & ~3; // pad in back

    return flash_chunk(fd, (ix * IHEX_PAGE_SIZE) + byte_address,
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


static int radio_debug_enable_dma(int fd)
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
        LOG_DEBUG("Found CC2543");
    }
    else {
        LOG_DEBUG("Unknown CHIP ID %02x", chip_id[0]);
        return -1;
    }
    LOG_DEBUG("\tversion \t - %02x", chip_id[1]);

    radio_read_chip_info(fd, chip_info);
    LOG_DEBUG("\tflash size\t - %d kB", (chip_info[0] & 0x70) == 0x10 ? 18 : 32);
    LOG_DEBUG("\tSRAM size\t - %d kB", (chip_info[1] & 0x07) ? 2 : 1);
    LOG_DEBUG("\tUSB\t\t - %s", chip_info[0] & 0x08 ? "yes" : "no");
    LOG_DEBUG("Chip info: %02x %02x", chip_info[0], chip_info[1]);
    
    radio_debug_read_config(fd, &config);
    LOG_DEBUG("Debug config %02x", config);
    radio_read_status(fd, &status);
    LOG_DEBUG("Debug status %02x", status);

    // Force radio again into debug mode since reading CHIP_ID disables erase.
    radio_debug_start(fd, radio);
    return 0;
}

static int flash_radio(int fd, int radio, pagemap_t &pagemap)
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
    LOG_DEBUG("Erasing firmware on radio: %d", radio);
    radio_chip_erase(fd, &status);
    // Wait for erase to finish
    while(status & 0x80) {
        radio_read_status(fd, &status);
        if(--iterations == 0) {
            LOG_DEBUG("Timed out waiting erase to finish");
            return -ETIMEDOUT;
         }
        usleep(10 * 1000);
    }

    // Enable DMA
    radio_debug_enable_dma(fd);

    // flash per page
    LOG_DEBUG("Flashing firmware on radio: %d", radio);
    for(pagemap_t::iterator i = pagemap.begin(); i != pagemap.end(); ++i) {
        if (flashPage(fd, i->first, i->second) < 0)
        {
            LOG_DEBUG("Firmware flashing failed on radio: %d", radio);
            return -1;
        }
    }

    LOG_DEBUG("Verifying firmware on radio: %d", radio);
    for(pagemap_t::iterator i = pagemap.begin(); i != pagemap.end(); ++i) {
        if (verifyPage(fd, i->first, i->second) < 0)
        {
            LOG_DEBUG("Firmware verification failed on radio: %d", radio);
            return -1;
        }
    }

    radio_debug_stop(fd, radio);
    return 0;
}

static bool debug_interface_exists()
{
    struct stat st;
    return ( stat(radio_debug_path, &st) == 0 );
}

/**
 *  Parses the radio firmaware version from the file path.
 *
 *  @param   filename  Firmware file path
 *  @param   version   reference to a string object in which the
 *                     firmware version will be placed.
 *
 *  @return  Returns true if a valid version is parsed.
 */
static bool parse_firmware_version(std::string filename, std::string& version)
{
    // Validate prefix and remove along with directory
    size_t pos = filename.find("radio-");
    if (pos == std::string::npos)
        return false;
    filename.erase(0, pos+6);
    
    // Validate and remove extension
    pos = filename.rfind(".hex");
    if (pos == std::string::npos)
        return false;
    filename.erase(pos);
    
    version = filename;
    
    return true;
}

/**
 *  Determines which radios need to be upgraded (if any), and generates
 *  a list. 
 *
 *  @param  file_version  Version the match radios against
 *  @param  radios        Reference to an array that will contain
 *                        the radios that do not match the firmware
 *                        version.
 */
static void check_radios_for_upgrade(std::string& file_version, std::vector<int>& radios)
{
    unsigned numRadios = Radios::totalNumberOfRadios();
    for (size_t i = 0; i < numRadios; ++i)
    {
        std::string ver = Radios::radio(i).getVersion();
        
        if (file_version.compare(ver.c_str()) != 0)
            radios.push_back(i);
    }
}

/**
 *  Upgrades the specified radios using the given firmware file.
 *
 *  @param  filename  Path to the firmware file
 *  @param  radios    An array of radios to upgrade
 */
static void do_radio_upgrades(std::string& filename, std::vector<int>& radios)
{   
    ihexreader hexreader;
    if(!hexreader.read(filename.c_str()))
    {
        LOG_WARN("Unable to parse radio firmware file");
        ReaderInfo::instance()->setStatus(IStatus::Yellow, "Invalid radio firmware file");
        return;
    }

    int fd = radio_open();
    if(fd < 0)
    {
        LOG_WARN("Unable to open radio debug interface for radio flashing");
        ReaderInfo::instance()->setStatus(IStatus::Yellow, "Unable to upgrade radio firmware");
        return;
    }
    
    std::vector<int>::iterator it;
    bool upgradeFailed = false;
    for (it = radios.begin(); it < radios.end() && !grover_quitting(); it++)
    {
        LOG_INFO("Upgrading radio %d", *it);

        int retries = 3;
        while (flash_radio(fd, *it, *hexreader.data()) < 0)
        {
            LOG_WARN("Failed to flash radio: %d", *it);
            if (--retries <= 0)
            {
                upgradeFailed = true;
                break;
            }
            LOG_INFO("Retrying upgrade on radio: %d", *it);
        }
        if (retries <= 0)
        usleep(1000*100);

        // re-initialize the radio
        Radios::radio(*it).init();
    }

    if (upgradeFailed)
        ReaderInfo::instance()->setStatus(IStatus::Yellow, "Unable to upgrade radio firmware");
    
    close(fd);
}

/**
 *  Checks for a radio firmaware file and initiates an update
 *  if it detects a new version on any of the radios.
 *
 *  Note: This routine only checks to see if the version
 *  differs from the current version and can downgrade
 *  firmware as well as upgrade it.
 */
void checkForUpdates()
{
    if (!debug_interface_exists())
        return;

    LOG_INFO("Checking for radio firmware update");

    std::string filename, file_version;
    if (FileSystem::findFirmwareFile("radio-", filename) &&
        parse_firmware_version(filename, file_version))
    {
        LOG_DEBUG("Found radio firmware file: %s", filename.c_str());

        std::vector<int> upgradable;
        check_radios_for_upgrade(file_version, upgradable);

        if (!upgradable.empty())
            do_radio_upgrades(filename, upgradable);
    }
}

} // namespace radio
