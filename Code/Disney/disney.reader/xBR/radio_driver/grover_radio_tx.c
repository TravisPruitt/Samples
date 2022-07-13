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
#include <linux/ioctl.h>
#include <linux/file.h>
#include <linux/mutex.h>
#include <linux/delay.h>
#include <linux/gpio.h>
#include <asm/uaccess.h>

#include <linux/spi/grover_radio.h>
#include "grover_radio_main.h"

#define DRV_NAME 		"grover_radio_tx"
#define SPI_SPEED_DFLT		0

struct tx_radio {
	struct spi_device	*spi;
	int 			sending;
	spinlock_t		lock;
	struct spi_message 	msg;
	struct spi_transfer 	trn;
	uint8_t			tx_buf[MAX_TRANSACTION_SZ];
	uint8_t			rx_buf[MAX_TRANSACTION_SZ];
	/* Data from app to be sent to a band. No pointers to band data
	 * can be held except when holding the band_data_lock.
	 * This is needed because of the timeout timer. */
	spinlock_t		band_data_lock;
	struct rb_root		band_data;
	unsigned long		band_data_count;

	/* Generic message to every band */
	int8_t			apb_rcvdss;	// only send to bands with ss above this value...
	uint8_t			apb[RADIO_MAX_TX_DATALEN];
	uint8_t			apb_len;	// Zero means no data

	unsigned long		missed;
	unsigned long		sent;

	/* Platform data */
	struct grover_tx_radio_platform_data pdata;
};

static struct tx_radio *s_tx_radio = NULL;

/*****************************************************************/
/******************** Band data structure PRIVATE ****************/
/*****************************************************************/

struct band_data {
	struct rb_node		node;
	uint64_t		band;
	unsigned int		datalen; // amount of data in 'buf'
	struct timer_list	timer;
	unsigned long		timeout; // in jiffies
	int8_t			rcvdss;	// filter; reply only to values > rcvdss.
	uint8_t			buf[RADIO_MAX_TX_DATALEN];
};

/* must be called with band_data_lock held */
static void empty_band_data(struct rb_root *root)
{
	struct band_data *data;
	struct rb_node* node;

	node = rb_first(root);
	while (node) {
		data = container_of(node, 
				    struct band_data,
				    node);
		node = rb_next(node); // Get pointer to next node
		rb_erase(&data->node, root); // Erase old node 
		del_timer_sync(&data->timer);
		kfree(data);
	}
}

/* must be called with band_data_lock held */
static void add_band_data(struct rb_root *root, struct band_data *data)
{
	struct rb_node **new = &(root->rb_node), *parent = NULL;

	/* Figure out where to put new node */
	while (*new) {
		struct band_data *this;
		this = container_of(*new, struct band_data, node);

		parent = *new;
		if (data->band < this->band)
			new = &((*new)->rb_left);
		else if (data->band > this->band)
			new = &((*new)->rb_right);
		else
			return;
	}

	/* Add new node and rebalance tree. */
	rb_link_node(&data->node, parent, new);
	rb_insert_color(&data->node, root);
	/* Now that data is safely in tree, start timer */
	if(data->timeout)
		mod_timer(&data->timer, jiffies + data->timeout);
}

/* must be called with band_data_lock held */
static struct band_data* search_band_data(struct rb_root *root,
					  uint64_t band)
{
	struct band_data *data;

	struct rb_node *node = root->rb_node;

	while (node) {
		data = container_of(node, 
				    struct band_data,
				    node);

		if (band < data->band)
			node = node->rb_left;
		else if (band > data->band)
			node = node->rb_right;
		else
			return data;
	}
	return NULL;
}

/*****************************************************************/
/******************** Band data structure API ********************/
/*****************************************************************/
static int radio_tx_del_band_data(struct tx_radio *tx_radio, uint64_t band)
{
	unsigned long flags;
	struct band_data *band_data;

	spin_lock_irqsave(&tx_radio->band_data_lock, flags);
	band_data = search_band_data(&tx_radio->band_data, band);

	if(band_data == NULL) {
		spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
		return -EINVAL;
	}

	rb_erase(&band_data->node, &tx_radio->band_data);
	tx_radio->band_data_count--;
	/* Timer must be stopped with lock held */
	del_timer_sync(&band_data->timer);

	spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
	/* Free band data structure */
	kfree(band_data);

	return 0;
}

/* Return 1 if got data, 0 if not.
 * Since we are holding the pointer to the band_data
 * also check if the timer should be refreshed  */
static int radio_get_band_data(struct tx_radio *tx_radio,
			       uint64_t band,
			       int8_t ss,
			       uint8_t ret_data[RADIO_MAX_TX_DATALEN],
			       uint8_t *datalen)
{
	unsigned long flags;
	struct band_data *data;
	int ret = 0;

	spin_lock_irqsave(&tx_radio->band_data_lock, flags);
	data = search_band_data(&tx_radio->band_data, band);
	if(data && ss >= data->rcvdss) {
		memcpy(ret_data, data->buf, data->datalen);
		*datalen = data->datalen;
		ret = 1;
		/* Reset timer */
		if(data->timeout)
			mod_timer(&data->timer, jiffies + data->timeout);
	}
	spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
	return ret;
}

/* Called in interrupt context */
void band_data_timeout_cb(unsigned long ctx)
{
	unsigned long flags;
	struct tx_radio *tx_radio = s_tx_radio;
	struct band_data *data =
		(struct band_data*) ctx;

	/* Since we do not allow references to band_data
	 * outside of locked areas we can safely
	 * remove and release this data */
	if(tx_radio == NULL)
		return;

	spin_lock_irqsave(&tx_radio->band_data_lock, flags);
	rb_erase(&data->node, &tx_radio->band_data);
	tx_radio->band_data_count--;
	kfree(data);
	spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
}

struct band_data * radio_alloc_band_data(uint64_t band,
					 const uint8_t *buf,
					 unsigned int datalen,
					 int8_t rcvdss,
					 unsigned int lifetime)
{
	struct band_data *data = NULL;

	data = kzalloc(sizeof(*data), GFP_KERNEL);
	if(!data)
		return ERR_PTR(-ENOMEM);

	if(copy_from_user(&data->buf, buf, datalen)) {
		kfree(data);
		return ERR_PTR(-EFAULT);
	}

	data->band = band;
	data->datalen = datalen;
	data->rcvdss = rcvdss;
	data->timeout = msecs_to_jiffies(lifetime * 1000);
	setup_timer(&data->timer, band_data_timeout_cb, (unsigned long)data);
	return data;
}

static void radio_add_band_data(struct tx_radio *tx_radio,
				struct band_data *data)
{
	unsigned long flags;

	spin_lock_irqsave(&tx_radio->band_data_lock, flags);
	add_band_data(&tx_radio->band_data, data);
	tx_radio->band_data_count++;
	spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
}

static int tx_radio_cancel_tx_data(struct tx_radio *tx_radio,
				   uint64_t band_address)
{
	unsigned long flags;

	if(band_address == 0) {
		spin_lock_irqsave(&tx_radio->band_data_lock, flags);
		if(tx_radio->apb_len == 0) {
			spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
			return -EINVAL;
		}
		tx_radio->apb_len = 0;
		spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
		return 0;
	}

	return radio_tx_del_band_data(tx_radio, band_address);
}

/*****************************************************************/
/******************** TX radio management ************************/
/*****************************************************************/

static void tx_radio_spi_complete(void *ctx)
{
	struct tx_radio *tx_radio = ctx;

	tx_radio->sending = 0;
}

static void tx_radio_prepare(struct tx_radio *tx_radio,
			     void *tx_buf, void *rx_buf,
			     unsigned int len,
			     unsigned int speed,
			     void (*complete_fn)(void*))
{
	memcpy(tx_radio->tx_buf, tx_buf, len);
	memset(&tx_radio->trn, 0, sizeof(&tx_radio->trn));
	spi_message_init(&tx_radio->msg);
	spi_message_add_tail(&tx_radio->trn, &tx_radio->msg);
	tx_radio->msg.complete = complete_fn;
	tx_radio->msg.context = tx_radio;

	tx_radio->trn.speed_hz = speed;
	tx_radio->trn.tx_buf = tx_radio->tx_buf;
	tx_radio->trn.rx_buf = tx_radio->rx_buf;
	tx_radio->trn.len = len;
}

static int tx_radio_send_receive_sync(void *tx_buf, void *rx_buf,
				      unsigned int len, unsigned int speed)
{
	struct tx_radio *tx_radio = s_tx_radio;
	int ret;

	if(len > MAX_TRANSACTION_SZ)
		return -EINVAL;

	tx_radio_prepare(tx_radio, tx_buf, rx_buf, len, speed, NULL);
	ret = spi_sync(tx_radio->spi, &tx_radio->msg);
	return ret;
}

static int tx_radio_is_busy(struct tx_radio *tx_radio)
{
	if(gpio_is_valid(tx_radio->pdata.drdy_n_gpio)) {
		return gpio_get_value(tx_radio->pdata.drdy_n_gpio);
	}
	else {
		/* Never busy if HW does not have this feature */
		return 0;
	}
}

/* Called in interrupt context */
static int tx_radio_send(struct tx_radio *tx_radio, void *buf, unsigned int len)
{
	if (tx_radio->sending) {
//		printk(KERN_DEBUG "%s: Already sending!\n", DRV_NAME);
		tx_radio->missed++;
		return 0;
	}

	if (tx_radio_is_busy(tx_radio)) {
//		printk(KERN_DEBUG "%s: Busy!!\n", DRV_NAME);
		tx_radio->missed++;
		return 0;
	}

	tx_radio->sent++;
	tx_radio->sending = 1;

	tx_radio_prepare(tx_radio, buf, NULL, len,
			 SPI_SPEED_DFLT,
			 tx_radio_spi_complete);
	// our callback is called for spi_async, it resets sending flag
	spi_async(tx_radio->spi, &tx_radio->msg);
	return 1;
}

/*****************************************************************/
/****************  API  ******************************************/
/*****************************************************************/
void tx_radio_clear_band_data(void)
{
	unsigned long flags;
	struct tx_radio *tx_radio = s_tx_radio;

	radio_trace(GROVER_TRACE_IOCTL,
		    "RADIO_CANCEL_ALL_TX_DATA\n");

	if(tx_radio == NULL)
		return;

	spin_lock_irqsave(&tx_radio->band_data_lock, flags);
	// TODO: make a copy of root, clear original, release lock and then empty
	empty_band_data(&tx_radio->band_data);
	tx_radio->apb_len = 0;
	tx_radio->band_data_count = 0;
	spin_unlock_irqrestore(&tx_radio->band_data_lock, flags);
}

int tx_radio_ioc_set_tx_data(struct radio_tx_data *tx_data)
{
	int ret = 0;
	struct band_data *band;
	struct tx_radio *tx_radio = s_tx_radio;

	radio_trace(GROVER_TRACE_IOCTL,
		    "RADIO_SET_TX_DATA band=%012llx "
		    "rcvdss=%d datalen=%u lifetime=%u data="
		    "%02x %02x %02x %02x %02x %02x "
		    "%02x %02x %02x %02x %02x %02x\n",
		    tx_data->band_address,
		    tx_data->rcvdss,
		    tx_data->datalen,
		    tx_data->lifetime,
		    tx_data->data[0],
		    tx_data->data[1],
		    tx_data->data[2],
		    tx_data->data[3],
		    tx_data->data[4],
		    tx_data->data[5],
		    tx_data->data[6],
		    tx_data->data[7],
		    tx_data->data[8],
		    tx_data->data[9],
		    tx_data->data[10],
		    tx_data->data[11]);

	if(tx_radio == NULL)
		return -ENODEV;

	if(tx_data->datalen > RADIO_MAX_TX_DATALEN)
		return -EINVAL;

	/* Cancel (possibly) existing tx data (and apb) */
	(void)tx_radio_cancel_tx_data(tx_radio, tx_data->band_address);

	/* Generic message to all bands */
	if(tx_data->band_address == 0) {
		if(copy_from_user(&tx_radio->apb, tx_data->data, tx_data->datalen)) {
			return -EFAULT;
		}
		tx_radio->apb_len = tx_data->datalen;
		tx_radio->apb_rcvdss = tx_data->rcvdss;
		return 0;
	}

	/* Create new data */
	band = radio_alloc_band_data(tx_data->band_address,
				     tx_data->data,
				     tx_data->datalen,
				     tx_data->rcvdss,
				     tx_data->lifetime);
	if(IS_ERR(band)) {
		/* Note that we do not guarantee atomic operation */
		ret = PTR_ERR(band);
		goto out;
	}

	/* Add data */
	radio_add_band_data(tx_radio, band);

out:
	return ret;
}

int tx_radio_ioc_cancel_tx_data(uint64_t band_address)
{
	struct tx_radio *tx_radio = s_tx_radio;
	radio_trace(GROVER_TRACE_IOCTL,
		    "RADIO_CANCEL_TX_DATA band=%012llx\n",
		    band_address);

	if(tx_radio == NULL)
		return -ENODEV;

	return tx_radio_cancel_tx_data(tx_radio, band_address);
}

int tx_radio_ioc_ctrl_rd(struct radio_read *rd)
{
	int got_radio, ret = -EINVAL;
	unsigned long flags;
	struct tx_radio *tx_radio = s_tx_radio;

	if(tx_radio == NULL)
		return -ENODEV;

	spin_lock_irqsave(&tx_radio->lock, flags);
	if(tx_radio->sending == 0) {
		tx_radio->sending = 1;
		got_radio = 1;
	} else {
		got_radio = 0;
	}
	spin_unlock_irqrestore(&tx_radio->lock, flags);

	if(!got_radio)
		return -EBUSY;

	if(copy_from_user(tx_radio->tx_buf, rd->tx.buf, rd->tx.len)) {
		ret = -EFAULT;
		goto out_free_radio;
	}

	ret = tx_radio_send_receive_sync(tx_radio->tx_buf, tx_radio->rx_buf,
					 rd->tx.len, SPI_SPEED_DFLT);
	if(ret)
		goto out_free_radio;
	udelay(1000); // Wait for radio to becore ready (TX has no status line)
	memset(tx_radio->tx_buf, 0, rd->rx.len); // Send zeros
	ret = tx_radio_send_receive_sync(tx_radio->tx_buf, tx_radio->rx_buf,
					 rd->rx.len,
					 SPI_SPEED_DFLT);
	if(ret)
		goto out_free_radio;

	if(copy_to_user(rd->rx.buf, tx_radio->rx_buf, rd->rx.len)) {
		ret = -EFAULT;
		goto out_free_radio;
	}
	// fall through

out_free_radio:
	tx_radio->sending = 0;
	return ret;
}

int tx_radio_ioc_spitest(uint8_t *tx, uint8_t *rx, unsigned length, unsigned speed)
{
	return tx_radio_send_receive_sync(tx, rx, length, speed);
}

extern void radio_dump(struct radio *radio, void *buf, unsigned long len);

/* Called from interrupt context */
void tx_radio_check_for_reply(uint64_t addr, int8_t ss)
{
	int got_data;
	struct tx_radio *tx_radio = s_tx_radio;
	uint8_t data[RADIO_MAX_TX_DATALEN];
	uint8_t datalen = 0;

	if(tx_radio == NULL)
		return;

	got_data = radio_get_band_data(tx_radio, addr,
				       ss,
				       data, &datalen);
	if(got_data) {
		tx_radio_send(tx_radio, data, datalen);
	} else {
		if(tx_radio->apb_len && ss >= tx_radio->apb_rcvdss) {
			/* Set band address in APB message */
			tx_radio->apb[1+0] = addr >> 32;
			tx_radio->apb[1+1] = addr >> 24;
			tx_radio->apb[1+2] = addr >> 16;
			tx_radio->apb[1+3] = addr >> 8;
			tx_radio->apb[1+4] = addr;
			tx_radio_send(tx_radio, tx_radio->apb, tx_radio->apb_len);
		}
	}
}

int tx_radio_reset(int rd)
{
	uint8_t tx[1] = { RADIOCONTROL_FORCERESET };
	uint8_t rx[1];
	return tx_radio_send_receive_sync(tx, rx, SPI_SPEED_DFLT, 1);
}

/*****************************************************************/
/****************  Sysfs API   ***********************************/
/*****************************************************************/

unsigned int tx_radio_get_missed(void)
{
	struct tx_radio *tx_radio = s_tx_radio;

	if(tx_radio == NULL)
		return 0;

	return tx_radio->missed;
}

unsigned int tx_radio_get_sent(void)
{
	struct tx_radio *tx_radio = s_tx_radio;

	if(tx_radio == NULL)
		return 0;

	return tx_radio->sent;
}

void tx_radio_set_missed(unsigned long missed)
{
	struct tx_radio *tx_radio = s_tx_radio;

	if(tx_radio == NULL)
		return;

	tx_radio->missed = missed;
}

unsigned long tx_radio_get_band_data_count(void)
{
	struct tx_radio *tx_radio = s_tx_radio;

	if(tx_radio == NULL)
		return 0;

	return tx_radio->band_data_count;
}

int tx_radio_get_apb_data(void)
{
	struct tx_radio *tx_radio = s_tx_radio;

	if(tx_radio == NULL)
		return 0;

	return !!tx_radio->apb_len;
}

static int tx_radio_init_platform_data(struct tx_radio *tx_radio)
{
	if(gpio_is_valid(tx_radio->pdata.drdy_n_gpio)) {
		if(gpio_request(tx_radio->pdata.drdy_n_gpio, "tx_drdy_n")) {
			dev_err(&tx_radio->spi->dev, "Could not request GPIO %d\n",
				tx_radio->pdata.drdy_n_gpio);
			return -EINVAL;
		}
		gpio_direction_input(tx_radio->pdata.drdy_n_gpio);
	}
	return 0;
}

static void tx_radio_deinit_platform_data(struct tx_radio *tx_radio)
{
	if(gpio_is_valid(tx_radio->pdata.drdy_n_gpio)) {
		gpio_free(tx_radio->pdata.drdy_n_gpio);
	}
}

/*****************************************************************/
/****************  probe/init  ***********************************/
/*****************************************************************/
static int __devinit tx_radio_probe(struct spi_device *spi)
{
	int ret = -ENOMEM;
	struct tx_radio *tx_radio;
	struct grover_tx_radio_platform_data *pdata;

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
		goto out;

	tx_radio = kzalloc(sizeof(*tx_radio), GFP_KERNEL);
	if(!tx_radio) {
		ret = -ENOMEM;
		goto out;
	}

	tx_radio->pdata = *pdata;
	spin_lock_init(&tx_radio->band_data_lock);
	spin_lock_init(&tx_radio->lock);
	tx_radio->spi = spi;
	s_tx_radio = tx_radio;

	if(tx_radio_init_platform_data(tx_radio))
		goto out_free;

	spi_set_drvdata(spi, tx_radio);

	EXIT_R(ret);
	return ret;

out_free:
	kfree(tx_radio);
out:
	EXIT_R(ret);
	return ret;
}

static int __devexit tx_radio_remove(struct spi_device *spi)
{
	struct tx_radio *tx_radio = spi_get_drvdata(spi);

	tx_radio_deinit_platform_data(tx_radio);
	kfree(tx_radio);
	s_tx_radio = NULL;
	return 0;
}

struct spi_driver tx_radio_driver = {
	.driver = {
		.name	= DRV_NAME,
		.owner	= THIS_MODULE,
	},
	.probe	= tx_radio_probe,
	.remove	= __devexit_p(tx_radio_remove),
};
