#include <linux/cdev.h>
#include <linux/module.h>
#include <linux/init.h>
#include <linux/spi/spi.h>
#include <linux/spinlock.h>
#include <linux/completion.h>
#include <linux/interrupt.h>
#include <linux/wait.h>
#include <linux/poll.h>
#include <linux/sched.h>
#include <linux/delay.h>
#include <linux/ioctl.h>
#include <linux/file.h>
#include <linux/mutex.h>
#include <linux/gpio.h>
#include <asm/uaccess.h>

#include <linux/spi/grover_radio.h>
#include "grover_radio_main.h"
#include "grover_radio_version.h"
#include "radiocommands.h"

#define DRV_NAME 		"grover_radio"
#define DRV_VERSION		VERSION
#define MAX_GROVER_DRIVERS	4
#define MAX_QUEUE_ITEMS		10000

/* Grover radio class */
struct class *grover_class;
/* First Grover radio major/minor */
static dev_t grover_radio_number;

uint32_t grover_trace_level = 0;

/*****************************************************************/
/****************  DEBUG  ****************************************/
/*****************************************************************/
#if 0
void radio_dump(struct radio *radio, void *buf, unsigned long len)
{
	uint8_t *b = buf;
	while(len >= 16) {
		radio_debug("%02x %02x %02x %02x %02x %02x %02x %02x"
			    "%02x %02x %02x %02x %02x %02x %02x %02x\n",
			    b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7],
			    b[8], b[9], b[10], b[11], b[12], b[13], b[14], b[15]);
		b += 16;
		len -= 16;
	}
	while(len--) {
		printk("%02x ", *b++);
	}
	printk("\n");
}
#endif

/*****************************************************************/
/********************* Radio queue management ********************/
/*****************************************************************/
static inline void radio_set_mux(struct radio *radio, int mux)
{
	unsigned int i;
	for(i = 0; i < radio->pdata.nr_mux_gpios; i++) {
		gpio_set_value(radio->pdata.mux_gpios[i],
			       mux & (1 << i));
	}
}

static inline struct queue_item* radio_alloc_queue_item(int txlen)
{
	struct queue_item *q;

	q = kzalloc(sizeof(*q) + txlen, GFP_ATOMIC);
	if(!q)
		return NULL;
	if(txlen) {
		q->buflen = txlen;
	}
	return q;
}

static inline void radio_free_queue_item(struct queue_item *q)
{
	kfree(q);
}

static inline struct app_item* radio_alloc_message(int id,
						   int radio,
						   int datalen)
{
	struct app_item *ai;

	ai = kmalloc(sizeof(struct app_item) + datalen, GFP_ATOMIC);
	if(!ai)
		return NULL;
	ai->rm.id = id;
	ai->rm.radio = radio;
	ai->rm.length = datalen;
	ai->rm.tbd = 0;
	return ai;
}

static inline void radio_insert_queue_item(struct radio *radio,
					   struct queue_item *q)
{
	unsigned long flags;

	spin_lock_irqsave(&radio->queue_lock, flags);
	list_add_tail(&q->list, &radio->queue);
	spin_unlock_irqrestore(&radio->queue_lock, flags);
}

static void radio_spi_complete_cb(void *ctx);
static void radio_init_spi_msg(struct radio *radio, int len)
{
	/* Init SPI members */
	memset(&radio->trn, 0, sizeof(radio->trn));
	spi_message_init(&radio->msg);
	spi_message_add_tail(&radio->trn, &radio->msg);
	radio->msg.complete = radio_spi_complete_cb;
	radio->msg.context = radio;

	/* Always use static RX and TX buffers */
	radio->trn.rx_buf = radio->rx_buf;
	radio->trn.tx_buf = radio->tx_buf;
	radio->trn.len = len;
}

static void radio_process_queue_item(struct radio *radio,
				     struct queue_item *q)
{
	int len;

	len = q->buflen;
	if(len > MAX_TRANSACTION_SZ)
		len = MAX_TRANSACTION_SZ;

	radio_init_spi_msg(radio, len);

	/* Copy TX data into static buffer */
	memcpy(radio->tx_buf, q->tx_buf, len);
	radio_set_mux(radio, q->radio);

	radio->radio = q->radio;
	radio->radios[q->radio].ready = 0;
	radio_free_queue_item(q);

	spi_async(radio->spi, &radio->msg);
}

static void radio_process_queue(struct radio *radio, int from_irq)
{
	unsigned long flags;
	struct list_head *item = NULL;
	struct queue_item *q;

	spin_lock_irqsave(&radio->queue_lock, flags);
	if(!from_irq && radio->transaction_ongoing) {
		spin_unlock_irqrestore(&radio->queue_lock, flags);
		return;
	}
	if(!list_empty(&radio->queue))
	{
		item = radio->queue.next;
		list_del(item);
		radio->transaction_ongoing = 1;
	} else {
		radio->transaction_ongoing = 0;
	}
	spin_unlock_irqrestore(&radio->queue_lock, flags);
	if(item) {
		q = list_entry(item, struct queue_item, list);
		radio_process_queue_item(radio, q);
	}
}

/*****************************************************************/
/********************* Application queue management **************/
/*****************************************************************/
void radio_insert_app_queue(struct radio *radio,
			    struct app_item *ai)
{
	unsigned long flags;
	struct list_head *item;
	struct app_item *d;

	spin_lock_irqsave(&radio->app_queue_lock, flags);
	/* Is there space in queue? */
	if(radio->app_queue_items < MAX_QUEUE_ITEMS) {
		radio->app_queue_items++;
	} else {
		/* If queue is full, delete oldest item */
		item = radio->app_queue.next;
		d = list_entry(item, struct app_item, list);
		list_del(item);
		kfree(d);
		radio->queue_overflows++;
	}
	list_add_tail(&ai->list, &radio->app_queue);
	spin_unlock_irqrestore(&radio->app_queue_lock, flags);
}

static void radio_clear_app_queue(struct radio *radio)
{
	unsigned long flags;
	struct list_head *item, *tmp;
	struct app_item *ai;

	spin_lock_irqsave(&radio->app_queue_lock, flags);
	list_for_each_safe(item, tmp, &radio->app_queue)
	{
		ai = list_entry(item, struct app_item, list);
		list_del(item);
		kfree(ai);
	}
	radio->app_queue_items = 0;
	spin_unlock_irqrestore(&radio->app_queue_lock, flags);
}

/*****************************************************************/
/*************** SPI transactions ********************************/
/*****************************************************************/
static void radio_handle_autorx(struct radio *radio)
{
	struct app_item *ai;
	uint8_t *rx_buf = (uint8_t*)radio->trn.rx_buf;

	if(rx_buf[0] == 0x00) // TODO: Or ping?
		return;

	radio->rx_pings++;

	radio_trace(GROVER_TRACE_AUTORX, "Got ping from radio %d\n", radio->radio);

	/* If inputdata copy to app queue */
	ai = radio_alloc_message(R_MSG_FROM_RADIO,
				 radio->radio,
				 radio->msg.actual_length);
	if(!ai) {
		radio_error("Cannot allocate memory\n");
		return;
	}
	memcpy(&ai->rm.msg, radio->trn.rx_buf,
	       radio->msg.actual_length);
	radio_insert_app_queue(radio, ai);
	wake_up_interruptible(&radio->app_wait_queue);
}

static void radio_check_for_reply(struct radio *radio)
{
	uint8_t *b = (uint8_t*)radio->trn.rx_buf;
	struct RadioMessage *rmsg;
	int8_t ss;
	uint64_t radio_addr;

	if(b[0] != RADIOCONTROL_GETPING)
		return;

	rmsg = (struct RadioMessage *) b;
	ss = rmsg->u.pingPackage.signalStrength;

	radio_addr = (uint64_t)b[1] << 32;
	radio_addr |= (uint64_t)b[2] << 24;
	radio_addr |= (uint64_t)b[3] << 16;
	radio_addr |= (uint64_t)b[4] << 8;
	radio_addr |= (uint64_t)b[5];

	if(radio_addr == 0)
		return;

	tx_radio_check_for_reply(radio_addr, ss);
}

static void radio_spi_cb(struct radio *radio)
{
	struct user_req *pending = radio->user_req;
	int rx_handled = 0;

	if(radio->msg.status) {
		radio_debug("%s status=%d\n",
			    __FUNCTION__, radio->msg.status);
		radio_debug("%s actual_length=%d\n",
			    __FUNCTION__, radio->msg.actual_length);
	}

	/* check for pending user request */
	if(pending && (pending->radio == radio->radio)) {
		if(pending->state == TX_ONGOING) { // We just finished TX
			pending->state = RX_PENDING;
		}
		else if(pending->state == RX_ONGOING) {
			/* Copy data away from SPI buffer so
			 * we can schedule next SPI transaction immediately */
			memcpy(pending->rx_buf, radio->trn.rx_buf,
			       radio->msg.actual_length);
			pending->status = radio->msg.status;
			rx_handled = 1;
			radio->user_req = NULL; /* Waiting application still has a
						   copy of the pointer so it is ok */
			complete(&pending->done);
		}
	}
	if(!rx_handled && radio->radios[radio->radio].autorx_enabled) {
		radio_handle_autorx(radio);
	}
	radio_check_for_reply(radio);
	radio_process_queue(radio, 1); // Process next transaction
}

static void radio_spi_complete_cb(void *ctx)
{
	struct radio *radio = ctx;
	radio_spi_cb(radio);
	/* TODO: Check if GPIO is still low, if it is re-schedule another request */
	/* If it is not re-enable interrupt */
}


static int radio_process_user_request(struct radio *radio, int rd)
{
	struct user_req *pending = radio->user_req;
	struct queue_item *q;
	unsigned int len;

	if(!pending) {
		return 0;
	}

	if(pending->radio != rd) {
		return 0;
	}

	if(radio->radios[pending->radio].ready) {
		/* 1st stage TX */
		if(pending->state == TX_PENDING) {
			/* If auto-RX is enabled we need to make sure
			 * that the first "manual" transaction is
			 * big enough to receive a ping */
			if(radio->radios[pending->radio].autorx_enabled)
				len = max(pending->tx_len, radio->radios[pending->radio].autorx_sz);
			else
				len = pending->tx_len;
			q = radio_alloc_queue_item(len);
			if(!q)
				return -ENOMEM;
			q->radio = pending->radio;
			memcpy(q->tx_buf, pending->tx_buf, pending->tx_len);
			pending->state = TX_ONGOING;
			radio_insert_queue_item(radio, q);
			radio_process_queue(radio, 0);
		}
		/* 2nd stage RX */
		else if(pending->state == RX_PENDING) {
			q = radio_alloc_queue_item(pending->rx_len);
			if(!q)
				return -ENOMEM;
			q->radio = pending->radio;
			if(radio->radios[pending->radio].autorx_enabled) {
				q->tx_buf[0] = RADIOCONTROL_GETPING;
			}
			pending->state = RX_ONGOING;
			radio_insert_queue_item(radio, q);
			radio_process_queue(radio, 0);
		}
	}
	return 1;
}

static void radio_read_ping(struct radio *radio, int rd)
{
	struct queue_item *q;

	q = radio_alloc_queue_item(radio->radios[rd].autorx_sz);
	if(!q)
		return;
	q->radio = rd;
	q->tx_buf[0] = RADIOCONTROL_GETPING;
	radio_insert_queue_item(radio, q);
	radio_process_queue(radio, 0);
}

static int radio_force_reset(struct radio* radio, int rd)
{
	struct queue_item *q;

	radio_trace(GROVER_TRACE_IOCTL,
		    "RADIO_FORCE_RESET radio=%d\n",
		    rd);

	if(rd >= radio->pdata.nr_radios)
		return tx_radio_reset(rd);

	q = radio_alloc_queue_item(1);
	if(!q)
		return -ENOMEM;
	q->radio = rd;
	q->tx_buf[0] = RADIOCONTROL_FORCERESET;
	radio_insert_queue_item(radio, q);
	radio_process_queue(radio, 0);

	return 0;
}

/* Interrupt from radio means that it is ready.
 * Either it is ready for a new command, or, if we asked
 * for a ping earlier it has a ping for us, OR the ping
 * timeout has been reached */
static irqreturn_t radio_interrupt_handler(int irq, void *ctx)
{
	struct radio *radio = ctx;
	int i;

	/* Since we don't have a way to map IRQ to GPIO
	 * we keep a local mapping */
	for(i = 0; i < radio->pdata.nr_radios; i++)
	{
		if(radio->radios[i].irq == irq) {
			break;
		}
	}

	if(i == radio->pdata.nr_radios) {
		radio_debug("%s: Unknown IRQ/GPIO\n", __FUNCTION__);
		return IRQ_HANDLED;
	}

	radio->radios[i].ready = 1;
	/* Process user request if we have one */
	if(!radio_process_user_request(radio, i)) {
		if(radio->radios[i].autorx_enabled) {
			/* If not, and RX is enabled, get ping */
			radio_read_ping(radio, i);
		}
	}

	return IRQ_HANDLED;
}

static int radio_ioc_ctrl_rd(struct radio *radio, unsigned long arg)
{
	struct radio_read rd;
	struct user_req *pending;
	int status;

	if(!mutex_trylock(&radio->user_req_mutex))
		return -EBUSY;

	if(copy_from_user(&rd,(void*) arg, sizeof(rd))) {
		status = -EFAULT;
		goto out_unlock_mutex;
	}

	radio_trace(GROVER_TRACE_IOCTL,
		    "RADIO_CTRL_RD radio=%u\n", rd.radio);

	/* Check size */
	if((rd.rx.len > MAX_TRANSACTION_SZ) ||
	   (rd.tx.len > MAX_TRANSACTION_SZ)) {
		status = -EINVAL;
		goto out_unlock_mutex;
	}

	/* Radios bigger than RX go all to TX radio */
	if(rd.radio >= radio->pdata.nr_radios) { // TODO: maybe change this to 128 ->
		status = tx_radio_ioc_ctrl_rd(&rd);
		goto out_unlock_mutex;
	}

	pending = kzalloc(sizeof(*pending) + rd.tx.len + rd.rx.len, GFP_KERNEL);
	if(!pending) {
		status= -ENOMEM;
		goto out_unlock_mutex;
	}

	/* Prepare pending request */
	pending->rx_len = rd.rx.len;
	pending->tx_len = rd.tx.len;
	pending->tx_buf = pending->buf;
	pending->rx_buf = pending->buf + pending->tx_len; // RX buffer is after TX buffer
	init_completion(&pending->done);
	pending->radio = rd.radio;
	if(copy_from_user(pending->tx_buf, rd.tx.buf, rd.tx.len)) {
		status = -EFAULT;
		goto out_free_pending;
	}

	radio->user_req = pending;
	radio_process_user_request(radio, pending->radio);

	if(wait_for_completion_timeout(&pending->done,
				       timeval_to_jiffies(&rd.rx_timeout)))
	{
		if(copy_to_user(rd.rx.buf,
				pending->rx_buf,
				pending->rx_len)) {
			status = -EFAULT;
		} else {
			status = pending->status;
		}
		/* IRQ resets radio->user_req on success */
	} else {
		radio->user_req = NULL;
		radio_info("timeout waiting for read to complete\n");
		status = -ETIMEDOUT;
	}

out_free_pending:
	kfree(pending);
out_unlock_mutex:
	mutex_unlock(&radio->user_req_mutex);
	return status;
}

/*****************************************************************/
/******************* File ops ************************************/
/*****************************************************************/
static ssize_t radio_read(struct file *file, char *buff, size_t count, loff_t *offp)
{
	struct radio *radio = file->private_data;
	struct list_head *item = NULL;
	struct app_item *ai = NULL;
	ssize_t sz_all = 0, sz = 0;
	unsigned int items;
	unsigned long flags;
	int ret = 0;
	struct list_head copy_list;

	INIT_LIST_HEAD(&copy_list);

	/* Grab the contents of the app_queue */
	spin_lock_irqsave(&radio->app_queue_lock, flags);
	list_splice_tail_init(&radio->app_queue, &copy_list);
	items = radio->app_queue_items;
	radio->app_queue_items = 0;
	spin_unlock_irqrestore(&radio->app_queue_lock, flags);

	/* Always non-blocking, use poll */
	if(list_empty(&copy_list))
		return 0;

	/* Process as much as we can fit into buff */
	while(!list_empty(&copy_list)) {
		item = copy_list.next;
		ai = list_entry(item, struct app_item, list);
		sz = sizeof(struct radio_message) + ai->rm.length;
		if((sz + sz_all) <= count) {
			if(copy_to_user(buff, &ai->rm, sz)) {
				ret = -EFAULT;
				goto out_put_list_back;
			}
			list_del(item);
			kfree(ai);
			buff += sz;
			sz_all += sz;
			items--;
		} else { // No more space
			ret = sz_all;
			goto out_put_list_back;
		}
	}
	/* If we got here the copy_list is empty, just return */
	return sz_all;

out_put_list_back:
	spin_lock_irqsave(&radio->app_queue_lock, flags);
	/* put to front */
	list_splice(&copy_list, &radio->app_queue);
	radio->app_queue_items += items;
	spin_unlock_irqrestore(&radio->app_queue_lock, flags);

	return ret;
}

static int radio_set_rx_en(struct radio* radio, uint32_t r, int enable)
{
	if(radio->radios[r].autorx_enabled == enable)
		return -EINVAL;

	if(enable) {
		radio->radios[r].autorx_enabled = 1;
		/* Kick off a transaction if radio is ready */
		if(radio->radios[r].ready) {
			radio_read_ping(radio, r);
		}
	}
	else {
		radio->radios[r].autorx_enabled = 0;
	}
	return 0;
}

static uint32_t radio_get_rx_en(struct radio* radio, uint32_t r)
{
	return radio->radios[r].autorx_enabled;
}

int radio_ioc_get_info(struct radio *radio, struct radio_info *info)
{
	memset(info, 0, sizeof(*info));
	info->features = RADIO_FEATURE_RX | RADIO_FEATURE_TX;

	strncpy(info->version, DRV_VERSION, RADIO_VERSION_LEN);

	info->rx_radios = radio->pdata.nr_radios;

	return 0;
}

static unsigned int radio_poll(struct file *filp, struct poll_table_struct *pt)
{
	struct radio *radio = filp->private_data;
	unsigned int mask = 0;
	unsigned long flags;

	poll_wait(filp, &radio->app_wait_queue, pt);
	spin_lock_irqsave(&radio->app_queue_lock, flags);
	if(!list_empty(&radio->app_queue))
		mask = POLLIN | POLLRDNORM;
	spin_unlock_irqrestore(&radio->app_queue_lock, flags);
	return mask;
}

int radio_ioc_spitest(struct radio *radio, unsigned long arg)
{
	int ret = 0;
	struct radio_ioc_spitest spitest;
	struct spi_message	msg;
	struct spi_transfer	trn;
	void *tx, *rx;
	unsigned len, speed;

	if(copy_from_user(&spitest,(void*) arg, sizeof(spitest))) {
		return -EFAULT;
	}

	len = spitest.length;
	speed = spitest.spi_speed;

	tx = kmalloc(len, GFP_KERNEL);
	if(tx == NULL)
		return -ENOMEM;

	rx = kmalloc(len, GFP_KERNEL);
	if(rx == NULL) {
		ret = -ENOMEM;
		goto out_free_tx;
	}

	if(copy_from_user(tx,(void*) spitest.tx, len)) {
		ret = -EFAULT;
		goto out_free_rx;
	}

	/* Prepare SPI message */
	memset(&trn, 0, sizeof(trn));
	spi_message_init(&msg);
	trn.tx_buf = tx;
	trn.rx_buf = rx;
	trn.len = len;
	trn.speed_hz = speed;
	spi_message_add_tail(&trn, &msg);

	if(spitest.radio < radio->pdata.nr_radios) {
		/* Set mux */
		radio_set_mux(radio, spitest.radio);
		ret = spi_sync(radio->spi, &msg);
	}
	else { /* TX radio */
		ret = tx_radio_ioc_spitest(tx, rx, len, speed);
	}

	if(spitest.rx != NULL) {
		if(copy_to_user(spitest.rx, rx, len)) {
			ret = -EFAULT;
			goto out_free_rx;
		}
	}

out_free_rx:
	kfree(rx);
out_free_tx:
	kfree(tx);

	return ret;
}

/* setup and call proper function per IOCTL request */
static long radio_ioctl(struct file *file, unsigned int req,
			unsigned long arg)
{
	struct radio *radio = file->private_data;
	struct radio_autorx_enable rx_en;
	struct radio_ping_sz ping_sz;
	struct radio_tx_data tx_data;
	struct radio_info info;
	int ret = 0;
	int val;
	uint64_t band_address;

	/* copy from user memory for each request type */
	switch(req) {
		case RADIO_SET_PING_SZ:
		case RADIO_GET_PING_SZ:
			if(copy_from_user(&ping_sz, (void*)arg, sizeof(ping_sz)))
				return -EFAULT;
			if(ping_sz.radio >= radio->pdata.nr_radios)
				return -EINVAL;
			break;
		case RADIO_SET_AUTORX_EN:
		case RADIO_GET_AUTORX_EN:
			if(copy_from_user(&rx_en, (void*)arg, sizeof(rx_en)))
				return -EFAULT;
			if(rx_en.radio >= radio->pdata.nr_radios)
				return -EINVAL;
			break;
		case RADIO_FORCE_RESET:
			if(copy_from_user(&val, (void*)arg, sizeof(val)))
				return -EFAULT;
			break;
		case RADIO_SET_TX_DATA:
			if(copy_from_user(&tx_data, (void*)arg, sizeof(tx_data)))
				return -EFAULT;
			break;
		case RADIO_CANCEL_TX_DATA:
			if(copy_from_user(&band_address, (void*)arg, sizeof(band_address)))
				return -EFAULT;
			break;
	}

	/* Process each request type */
	switch(req) {
		case RADIO_FORCE_RESET:
			ret = radio_force_reset(radio, val);
			break;
		case RADIO_SET_AUTORX_EN:
			radio_trace(GROVER_TRACE_IOCTL, "RADIO_SET_AUTORX_EN enable=%d\n", rx_en.enable);
			ret = radio_set_rx_en(radio, rx_en.radio, rx_en.enable ? 1 : 0);
			break;
		case RADIO_GET_AUTORX_EN:
			rx_en.enable = radio_get_rx_en(radio, rx_en.radio);
			radio_trace(GROVER_TRACE_IOCTL, "RADIO_GET_AUTORX_EN enable=%d\n", rx_en.enable);
			break;
		case RADIO_SET_PING_SZ:
			radio_trace(GROVER_TRACE_IOCTL, "RADIO_SET_PING_SZ size=%d\n", ping_sz.size);
			radio->radios[ping_sz.radio].autorx_sz = ping_sz.size;
			break;
		case RADIO_GET_PING_SZ:
			radio_trace(GROVER_TRACE_IOCTL, "RADIO_GET_PING_SZ size=%d\n", ping_sz.size);
			ping_sz.size = radio->radios[ping_sz.radio].autorx_sz;
			break;
		case RADIO_CTRL_RD:
			ret = radio_ioc_ctrl_rd(radio, arg);
			break;
		case RADIO_SET_TX_DATA:
			ret = tx_radio_ioc_set_tx_data(&tx_data);
			break;
		case RADIO_CANCEL_TX_DATA:
			ret = tx_radio_ioc_cancel_tx_data(band_address);
			break;
		case RADIO_CANCEL_ALL_TX_DATA:
			tx_radio_clear_band_data();
			ret = 0;
			break;
		case RADIO_GET_INFO:
			radio_trace(GROVER_TRACE_IOCTL, "RADIO_GET_INFO\n");
			ret = radio_ioc_get_info(radio, &info);
			break;
		case RADIO_SPITEST:
			ret = radio_ioc_spitest(radio, arg);
			break;
		default:
			ret = -EINVAL;
			break;
	}
	
	/* copy back to user for GET_ */
	switch (req) {
		case RADIO_GET_PING_SZ:
			if(copy_to_user((void*)arg, &ping_sz, sizeof(ping_sz)))
				return -EFAULT;
			break;
		case RADIO_GET_AUTORX_EN:
			if(copy_to_user((void*)arg, &rx_en, sizeof(rx_en)))
				return -EFAULT;
			break;
		case RADIO_GET_INFO:
			if(copy_to_user((void*)arg, &info, sizeof(info)))
				return -EFAULT;
			break;
	}
	return ret;
}

static int radio_enable_interrupts(struct radio *radio);
static void radio_disable_interrupts(struct radio *radio);

static int radio_open(struct inode *inode, struct file *file)
{
	struct radio *radio = container_of(inode->i_cdev, struct radio, cdev);
	int ret = 0;

	mutex_lock(&radio->file_open);
	file->private_data = radio;
	radio->file_open_count++;
	if(radio->file_open_count == 1)
		ret = radio_enable_interrupts(radio);
	radio_trace(GROVER_TRACE_OPENCLOSE, "Radio opened, open_count=%d\n", radio->file_open_count);
	mutex_unlock(&radio->file_open);

	return ret;
}

static int radio_release(struct inode *inode, struct file *file)
{
	struct radio *radio = file->private_data;
	unsigned int i;

	/* TODO: Check for radio->transaction_ongoing
	 * and wait for SPI termination before closing file
	 */
	mutex_lock(&radio->file_open);
	radio_trace(GROVER_TRACE_OPENCLOSE, "Radio closed, open_count=%d\n", radio->file_open_count);
	if(radio->file_open_count == 1) {
		for(i = 0; i < radio->pdata.nr_radios; i++)
			radio_set_rx_en(radio, i, 0);
		radio_disable_interrupts(radio);
		tx_radio_clear_band_data();
		radio_clear_app_queue(radio);
	}
	file->private_data = NULL;
	radio->file_open_count--;
	mutex_unlock(&radio->file_open);
	return 0;
}

static const struct file_operations radio_file_ops = {
	.owner		= THIS_MODULE,
	.unlocked_ioctl = radio_ioctl,
	.open		= radio_open,
	.release	= radio_release,
	.poll		= radio_poll,
	.read		= radio_read,
};

/*****************************************************************/
/******************* Init / Deinit *******************************/
/*****************************************************************/
static int radio_enable_interrupts(struct radio *radio)
{
	int i, ret;

	if(radio->pdata.nr_radios &&
	   !gpio_is_valid(radio->pdata.irq_gpios[0]))
		return 0;
	for(i = 0; i < radio->pdata.nr_radios; i++) {
		ret = request_irq(gpio_to_irq(radio->pdata.irq_gpios[i]),
				  radio_interrupt_handler,
				  IRQF_TRIGGER_FALLING,
				  "grover_radio",
				  radio);
		/* Cleanup */
		if(ret < 0) {
			radio_error("Cannot request IRQ %d for GPIO %d\n",
				    gpio_to_irq(radio->pdata.irq_gpios[i]),
				    radio->pdata.irq_gpios[i]);
			while(i) {
				free_irq(gpio_to_irq(radio->pdata.irq_gpios[i--]), radio);
			}
			return ret;
		}
		radio_info("GPIO%d is %d\n",
			   radio->pdata.irq_gpios[i],
			   gpio_get_value(radio->pdata.irq_gpios[i]));
		if(!gpio_get_value(radio->pdata.irq_gpios[i]))
			radio->radios[i].ready = 1;
	}
#ifdef LOADGEN
	radio_loadgen_init(radio);
#endif
	return 0;
}

static void radio_disable_interrupts(struct radio *radio)
{
	int i;

	for(i = 0; i < radio->pdata.nr_radios; i++) {
		free_irq(gpio_to_irq(radio->pdata.irq_gpios[i]), radio);
	}
#ifdef LOADGEN
	radio_loadgen_deinit(radio);
#endif
}

static int radio_init_platform_data(struct radio *radio,
				    struct grover_radio_platform_data *pdata)
{
	int ret, i;

	radio->pdata = *pdata;
	/* Request MUX gpios */
	for(i = 0; i < pdata->nr_mux_gpios; i++) {
		if(!gpio_is_valid(pdata->mux_gpios[i]))
			continue;
		ret = gpio_request(pdata->mux_gpios[i], "grover_radio_mux");
		if(ret < 0) {
			dev_err(&radio->spi->dev,
				"Could not request GPIO %d for mux[%d]\n",
				pdata->mux_gpios[i],
				i);
			while(i--)
				gpio_free(pdata->mux_gpios[i]);
			return ret;
		}
		gpio_direction_output(pdata->mux_gpios[i], 0);
	}

	/* Request IRQ GPIOs */
	for(i = 0; i < pdata->nr_radios; i++) {
		ret = gpio_request(pdata->irq_gpios[i], "grover_radio irq");
		if(ret < 0) {
			dev_err(&radio->spi->dev,
				"Could not request GPIO %d for irq[%d]\n",
				pdata->irq_gpios[i],
				i);
			while(i--)
				gpio_free(pdata->irq_gpios[i]);
			goto out_free_muxes;
		}
		gpio_direction_input(pdata->irq_gpios[i]);
	}

	/* Request bank control GPIOs */
	for(i = 0; i < pdata->nr_radio_banks; i++) {
		if(gpio_is_valid(pdata->bank_enable_gpio[i])) {
			ret = gpio_request(pdata->bank_enable_gpio[i],
					   "grover_radio_en");
			if(ret < 0) {
				dev_err(&radio->spi->dev,
					"Could not request GPIO %d for en[%d]\n",
					pdata->bank_enable_gpio[i],
					i);
				while(i--)
					gpio_free(pdata->bank_enable_gpio[i]);
				goto out_free_irq_gpios;
			}
			/* For now default high */
			gpio_direction_output(pdata->bank_enable_gpio[i], 1);
		}
	}

	radio->radios = kzalloc(pdata->nr_radios * sizeof(*radio->radios), GFP_KERNEL);
	if(!radio->radios) {
		ret = -ENOMEM;
		goto out_free_bank_enable_gpios;
	}

	for(i = 0; i < pdata->nr_radios; i++) {
		radio->radios[i].autorx_sz = pdata->autorx_size;
		radio->radios[i].irq = gpio_to_irq(pdata->irq_gpios[i]);
	}

	return 0;

out_free_bank_enable_gpios:
	for(i = 0; i < pdata->nr_radio_banks; i++)
		if(gpio_is_valid(pdata->bank_enable_gpio[i]))
			gpio_free(pdata->bank_enable_gpio[i]);
out_free_irq_gpios:
	for(i = 0; i < pdata->nr_radios; i++)
		gpio_free(pdata->irq_gpios[i]);
out_free_muxes:
	for(i = 0; i < pdata->nr_mux_gpios; i++)
		if(gpio_is_valid(pdata->mux_gpios[i]))
			gpio_free(pdata->mux_gpios[i]);

	return ret;
}

static void radio_deinit_platform_data(struct radio *radio)
{
	int i;

	for(i = 0; i < radio->pdata.nr_radios; i++)
		gpio_free(radio->pdata.irq_gpios[i]);

	for(i = 0; i < radio->pdata.nr_mux_gpios; i++)
		if(gpio_is_valid(radio->pdata.mux_gpios[i]))
			gpio_free(radio->pdata.mux_gpios[i]);

	for(i = 0; i < radio->pdata.nr_radio_banks; i++)
		if(gpio_is_valid(radio->pdata.bank_enable_gpio[i]))
			gpio_free(radio->pdata.bank_enable_gpio[i]);

	kfree(radio->radios);
}

static int __devinit radio_probe(struct spi_device *spi)
{
	int ret = -ENOMEM;
	struct radio *radio;
	struct grover_radio_platform_data *pdata;

	ENTER;

	pdata = spi->dev.platform_data;
	if(!pdata) {
		dev_err(&spi->dev, "No platform data provided!\n");
		return -EINVAL;
	}

	spi->bits_per_word = 8;
	spi->mode = SPI_MODE_0;
	ret = spi_setup(spi);
	if (ret < 0)
		return ret;

	radio = kzalloc(sizeof(*radio), GFP_KERNEL);
	if(!radio)
		return -ENOMEM;

	radio->spi = spi;
	mutex_init(&radio->file_open);
	mutex_init(&radio->user_req_mutex);
	radio->tx_buf = kzalloc(MAX_TRANSACTION_SZ, GFP_KERNEL);
	if(!radio->tx_buf)
		goto out_free_radio;

	radio->rx_buf = kzalloc(MAX_TRANSACTION_SZ, GFP_KERNEL);
	if(!radio->rx_buf)
		goto out_free_tx;

	/* Initialize items in radio */
	init_waitqueue_head(&radio->app_wait_queue);
	INIT_LIST_HEAD(&radio->app_queue);
	INIT_LIST_HEAD(&radio->queue);
	spin_lock_init(&radio->app_queue_lock);
	spin_lock_init(&radio->queue_lock);

	if((ret = radio_init_platform_data(radio, pdata)))
		goto out_free_rx;

	spi_set_drvdata(spi, radio);

	/* Initialize and add chrdev */
	cdev_init(&radio->cdev, &radio_file_ops);
	radio->cdev.owner = THIS_MODULE;

	ret = cdev_add(&radio->cdev,
		       (grover_radio_number + radio->pdata.id), 1);
	if(ret) {
		dev_err(&spi->dev, "Cannot add cdev: %d\n", ret);
		goto out_deinit_platform;
	}

	/* Create device for sysfs layer */
	radio->dev = device_create(grover_class,
				   &spi->dev,
				   grover_radio_number + radio->pdata.id,
				   radio,
				   "grover_radio%d",
				   radio->pdata.id);
	if(IS_ERR(radio->dev)) {
		ret = PTR_ERR(radio->dev);
		dev_err(&spi->dev, "Cannot create device: %d\n", ret);
		goto out_del_cdev;
	}

	/* Crate sysfs files */
	ret = radio_sysfs_init(radio);
	if(ret)
		goto out_destroy_device;

	/* Probe load generator */
	ret = radio_loadgen_probe(radio);
	if(ret)
		goto out_deinit_sysfs;

	EXIT_R(0);
	return 0;

out_deinit_sysfs:
	radio_sysfs_deinit(radio);
out_destroy_device:
	device_destroy(grover_class,
		       grover_radio_number + radio->pdata.id);
out_del_cdev:
	cdev_del(&radio->cdev);
out_deinit_platform:
	radio_deinit_platform_data(radio);
out_free_rx:
	kfree(radio->rx_buf);
out_free_tx:
	kfree(radio->tx_buf);
out_free_radio:
	kfree(radio);

	EXIT_R(ret);
	return ret;
}

static int __devexit radio_remove(struct spi_device *spi)
{
	struct radio *radio = spi_get_drvdata(spi);

	radio_loadgen_remove(radio);
	cdev_del(&radio->cdev);
	device_destroy(grover_class,
		       grover_radio_number + radio->pdata.id);
	radio_deinit_platform_data(radio);
	tx_radio_clear_band_data();
	kfree(radio->rx_buf);
	kfree(radio->tx_buf);
	kfree(radio);
	spi_set_drvdata(spi, NULL);
	return 0;
}

/*****************************************************************/
/****************  MODULE INIT  **********************************/
/*****************************************************************/

static struct spi_driver rx_radio_driver = {
	.driver = {
		.name	= DRV_NAME,
		.owner	= THIS_MODULE,
	},
	.probe	= radio_probe,
	.remove	= __devexit_p(radio_remove),
};

extern struct platform_driver radio_debug_driver;
extern struct spi_driver tx_radio_driver;

static int __init radio_init(void)
{
	int ret;

	printk(KERN_INFO "Initializing %s driver version: %s\n", DRV_NAME, DRV_VERSION);

	/* Allocate major/minor numbers for chrdev */
	ret = alloc_chrdev_region(&grover_radio_number,
				  0,
				  MAX_GROVER_DRIVERS,
				  DRV_NAME);
	if(ret) {
		printk(KERN_ERR "%s: Cannot allocate chrdev: %d\n", DRV_NAME, ret);
		goto out;
	}

	/* Create device class */
	grover_class = class_create(THIS_MODULE, "grover");
	if(IS_ERR(grover_class)) {
		ret = PTR_ERR(grover_class);
		printk(KERN_ERR "%s: Cannot create class: %d\n", DRV_NAME, ret);
		goto out_unreg_chrdev;
	}

	ret = platform_driver_register(&radio_debug_driver);
	if(ret)
		goto out_destroy_class;
	ret = spi_register_driver(&rx_radio_driver);
	if(ret)
		goto out_unreg_debug;
	ret = spi_register_driver(&tx_radio_driver);
	if(ret)
		goto out_unreg_rx;

	return 0;

out_unreg_rx:
	spi_unregister_driver(&rx_radio_driver);
out_unreg_debug:
	platform_driver_unregister(&radio_debug_driver);
out_destroy_class:
	class_destroy(grover_class);
out_unreg_chrdev:
	unregister_chrdev_region(MAJOR(grover_radio_number), MAX_GROVER_DRIVERS);
out:
	return ret;
}

static void __exit radio_exit(void)
{
	spi_unregister_driver(&rx_radio_driver);
	spi_unregister_driver(&tx_radio_driver);
	platform_driver_unregister(&radio_debug_driver);

	class_destroy(grover_class);
	unregister_chrdev_region(MAJOR(grover_radio_number), MAX_GROVER_DRIVERS);
}

MODULE_AUTHOR("Juha Kuikka <juha.kuikka@synapse.com>");
MODULE_DESCRIPTION("Grover radio driver");
MODULE_LICENSE("GPL v2");
MODULE_VERSION(DRV_VERSION);

module_init(radio_init);
module_exit(radio_exit);
