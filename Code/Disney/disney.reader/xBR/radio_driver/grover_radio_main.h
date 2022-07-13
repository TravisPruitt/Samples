#ifndef __GROVER_RADIO_MAIN_H__
#define __GROVER_RADIO_MAIN_H__

#define LOADGEN
#ifdef LOADGEN
#include "grover_radio_loadgen.h"
#endif

#define radio_error(str, args...) dev_err(radio->dev, str, ## args)
#define radio_debug(str, args...) dev_err(radio->dev, str, ## args)
#define radio_info(str, args...) dev_err(radio->dev, str, ## args)

extern uint32_t grover_trace_level;
#define GROVER_TRACE_IOCTL	(1 << 0)
#define GROVER_TRACE_OPENCLOSE	(1 << 1)
#define GROVER_TRACE_AUTORX	(1 << 2)
#define radio_trace(level, str, args...) if(grover_trace_level & (level)) printk(KERN_DEBUG str, ## args)

struct queue_item
{
	struct list_head 	list;
	int 			radio;
	int			buflen; // also transaction len
	uint8_t			tx_buf[0];
};

struct radio_state {
	int			ready;
	int			autorx_enabled;
	int			autorx_sz;
	int			irq;
};

struct user_req {
	int			radio;
	struct completion       done;
	int			state;
	int			status;
	int			tx_len;
	int			rx_len;
	uint8_t			*tx_buf;
	uint8_t			*rx_buf;
	uint8_t			buf[0]; // TX data followed by RX data
};

struct app_item {
	struct list_head        list;
	struct radio_message	rm;
};

struct radio
{
	int			enabled;
	struct user_req	   	*user_req; // only one pending read
	struct mutex		user_req_mutex;

	// For transaction requests
	spinlock_t		queue_lock; // for queue
	struct list_head 	queue; // for items

	/* Data for current SPI transaction */
	int			radio;
	int			transaction_ongoing;

	/* Static buffers etc. */
	uint8_t			*tx_buf;
	uint8_t			*rx_buf;
	struct spi_message	msg;
	struct spi_transfer	trn;
	struct spi_device	*spi;

	/* Radio states */
	struct radio_state	*radios;

	// For poll/read data and file ops
	spinlock_t		app_queue_lock;
	struct list_head	app_queue;
	struct mutex		file_open;
	int			file_open_count;
	wait_queue_head_t	app_wait_queue;
	unsigned int		app_queue_items;

	struct grover_radio_platform_data pdata;

#ifdef LOADGEN
	struct radio_loadgen	loadgen;
#endif
	/* Character device for this instance */
	struct cdev		cdev;
	struct device		*dev;
	/* Sysfs files */
	unsigned int		tx_missed;
	unsigned int		rx_pings;
	unsigned int		radio_timeouts;
	unsigned int		queue_overflows;
};

#define MAX_TRANSACTION_SZ		255
#define NR_RADIOS			8
#define RADIOCONTROL_GETPING		14
#define RADIOCONTROL_FORCERESET		31

#define TX_PENDING			0
#define TX_ONGOING			1
#define RX_PENDING			2
#define RX_ONGOING			3

#define ENTER printk(KERN_DEBUG ">%s\n", __FUNCTION__)
#define EXIT printk(KERN_DEBUG "<%s\n", __FUNCTION__)
#define EXIT_R(val) printk(KERN_DEBUG "<%s (%d)\n", __FUNCTION__, val)

void radio_dump(struct radio *radio, void *buf, unsigned long len);

/* TX radio API */
void tx_radio_check_for_reply(uint64_t addr, int8_t ss);
int tx_radio_reset(int rd);
void tx_radio_clear_band_data(void);
int tx_radio_ioc_ctrl_rd(struct radio_read *rd);
int tx_radio_ioc_set_tx_data(struct radio_tx_data *tx_data);
int tx_radio_ioc_cancel_tx_data(uint64_t);
int tx_radio_ioc_spitest(uint8_t *tx, uint8_t *rx,
			 unsigned length, unsigned speed);

/* TX radio sysfs API */
unsigned int tx_radio_get_missed(void);
void tx_radio_set_missed(unsigned long);
unsigned int tx_radio_get_sent(void);
unsigned long tx_radio_get_band_data_count(void);
int tx_radio_get_apb_data(void);

/* Load generator API */
int radio_loadgen_init(struct radio *radio);
void radio_loadgen_deinit(struct radio *radio);
int radio_loadgen_probe(struct radio *radio);
void radio_loadgen_remove(struct radio *radio);

/* Sysfs interfaces */
int radio_sysfs_init(struct radio *radio);
void radio_sysfs_deinit(struct radio *radio);

/* vim: set sw=8 ts=8 noexpandtab: */

#endif // __GROVER_RADIO_MAIN_H__
