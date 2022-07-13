#include <linux/cdev.h>
#include <linux/module.h>
#include <linux/init.h>
#include <linux/spi/spi.h>
#include <linux/spinlock.h>
#include <linux/completion.h>
#include <linux/miscdevice.h>
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
#include "grover_radio_version.h"

#define CLK_DELAY_US		1
#define T_DIR_CHANGE_US		100
#define DD_SETTLE_TIME		10
#define DRV_NAME 		"grover_debug"
#define DRV_VERSION		VERSION
#define DIRECTION_INPUT		0
#define DIRECTION_OUTPUT	1
#define DEBUG_ENABLED		1
#define DEBUG_DISABLED		0

/* Grover radio class */
extern struct class *grover_class;

struct radio_debug
{
	/* First Grover radio debug major/minor */
	dev_t	major_number;
	struct grover_radio_debug_platform_data	pdata;
	/* Character device for this driver */
	struct cdev		cdev;
	struct device		*dev;
	struct mutex		file_open;
};

static void radio_debug_gpio_dd_input(struct radio_debug *rdbg, int input)
{
	if(input) {
		if(gpio_is_valid(rdbg->pdata.gpio_direction))
			gpio_set_value(rdbg->pdata.gpio_direction, DIRECTION_INPUT);

		gpio_direction_input(rdbg->pdata.gpio_dd);
	}
	else {
		if(gpio_is_valid(rdbg->pdata.gpio_direction))
			gpio_set_value(rdbg->pdata.gpio_direction, DIRECTION_OUTPUT);

		gpio_direction_output(rdbg->pdata.gpio_dd, 1);
	}
}

static void radio_debug_send_byte(struct radio_debug *rdbg, uint8_t data)
{
	int i = 8;

	while(i--) {
		/* Set data */
		gpio_set_value(rdbg->pdata.gpio_dd, (data >> i) & 1);
		/* Clock */
		gpio_set_value(rdbg->pdata.gpio_dc, 1);
		udelay(CLK_DELAY_US);
		gpio_set_value(rdbg->pdata.gpio_dc, 0);
		udelay(CLK_DELAY_US);
	}
}

static void radio_debug_read_byte(struct radio_debug *rdbg, uint8_t *data)
{
	int val, i = 8;
	uint8_t d = 0;

	while(i--) {
		/* Clock */
		gpio_set_value(rdbg->pdata.gpio_dc, 1);
		udelay(CLK_DELAY_US);
		gpio_set_value(rdbg->pdata.gpio_dc, 0);
		/* Data valid on falling clock edge */
		val = gpio_get_value(rdbg->pdata.gpio_dd);
		d = (d << 1) | val;
		udelay(CLK_DELAY_US);
	}
	*data = d;
}

static int radio_wait_for_dd_low(struct radio_debug *rdbg)
{
	int i = T_DIR_CHANGE_US / 10;
	uint8_t dummy;

	while(i--) {
		if(gpio_get_value(rdbg->pdata.gpio_dd) == 0)
			return 0;
		radio_debug_read_byte(rdbg, &dummy); // Clock 8 bits
		udelay(10);
	}
//	dev_err(rdbg->dev, "Radio DD line did not go low.\n");
	return 1;
}

static int radio_debug_execute(struct radio_debug *rdbg,
			       uint8_t *tx, unsigned int txlen,
			       uint8_t *rx, unsigned int rxlen)
{
	int ret = 0;

	while(txlen--) {
		radio_debug_send_byte(rdbg, *tx++);
	}
	radio_debug_gpio_dd_input(rdbg, 1);
	udelay(DD_SETTLE_TIME); // Wait for DD to settle

	if(radio_wait_for_dd_low(rdbg)) {
		ret = -ETIMEDOUT;
		goto out;
	}
	while(rxlen--) {
		radio_debug_read_byte(rdbg, rx++);
	}
out:
	radio_debug_gpio_dd_input(rdbg, 0);
	return ret;
}

int radio_ioc_debug_tx_rx(struct radio_debug *rdbg,
			  struct radio_ioc_debug_tx_rx *debug)
{
	int ret;
	uint8_t *tx, *rx;

	if(debug->txlen > 2050 ||
	   debug->rxlen > 3)
		return -EINVAL;

	tx = kzalloc(debug->txlen, GFP_KERNEL);
	if(!tx)
		return -ENOMEM;

	rx = kzalloc(debug->rxlen, GFP_KERNEL);
	if(!rx) {
		ret = -ENOMEM;
		goto out_free_tx;
	}

	if(copy_from_user(tx,(void*) debug->tx, debug->txlen)) {
		ret = -EFAULT;
		goto out_free_rx;
	}

	ret = radio_debug_execute(rdbg,
				  tx, debug->txlen,
				  rx, debug->rxlen);
	if(ret == 0) {
		if(copy_to_user(debug->rx, rx, debug->rxlen)) {
			ret = -EFAULT;
		}
	}

out_free_rx:
	kfree(rx);
out_free_tx:
	kfree(tx);
	return ret;
}

static void radio_debug_set_mux(struct radio_debug *rdbg, int nr)
{
	unsigned i = 0;

	/* Tri-state mux */
	if(nr == -1) {
		for(i = 0; i < rdbg->pdata.nr_mux_gpios; i++) {
			gpio_set_value_cansleep(rdbg->pdata.gpio_mux[i], 1);
		}
		return;
	}

	for(i = 0; i < rdbg->pdata.nr_mux_gpios; i++) {
		gpio_set_value_cansleep(rdbg->pdata.gpio_mux[i],
					rdbg->pdata.gpio_mux_conf[nr] & (1 << i));
	}
}

static void radio_debug_enable(struct radio_debug *rdbg, int enable)
{
	/* If Flash Freeze line is availabe, use it to
	 * enable/disable MUX control FPGA */
	if(!gpio_is_valid(rdbg->pdata.gpio_ff))
		return;

	mdelay(1);

	gpio_set_value_cansleep(rdbg->pdata.gpio_ff,
				enable ? DEBUG_ENABLED : DEBUG_DISABLED);
}

/* This init sequence matches what TI smartRF05 EVM board does */
static int radio_ioc_debug_start(struct radio_debug *rdbg, int nr)
{
	if(nr >= rdbg->pdata.nr_radios)
		return -EINVAL;

	radio_debug_enable(rdbg, 1);

	radio_debug_set_mux(rdbg, nr);

	/* Force radio into debug mode */
	/* 1. Pull RESETn, DC, DD low */
	gpio_set_value(rdbg->pdata.gpio_dd, 0);
	gpio_set_value(rdbg->pdata.gpio_dc, 0);
	gpio_set_value(rdbg->pdata.gpio_resetn, 0);
	mdelay(2);
	/* 2. (Re)Reset radio */
	gpio_set_value(rdbg->pdata.gpio_resetn, 1);
	mdelay(2);
	gpio_set_value(rdbg->pdata.gpio_resetn, 0);
	/* 3. Two falling-edges transitions on DC */
	mdelay(2);
	gpio_set_value(rdbg->pdata.gpio_dc, 1);
	udelay(CLK_DELAY_US);
	gpio_set_value(rdbg->pdata.gpio_dc, 0);
	udelay(CLK_DELAY_US);
	gpio_set_value(rdbg->pdata.gpio_dc, 1);
	udelay(CLK_DELAY_US);
	gpio_set_value(rdbg->pdata.gpio_dc, 0);
	mdelay(2);
	/* 4. Release reset */
	gpio_set_value(rdbg->pdata.gpio_resetn, 1);
	mdelay(2);
	return 0;
}

static void radio_debug_reset(struct radio_debug *rdbg, int nr)
{
	gpio_set_value(rdbg->pdata.gpio_resetn, 0);
	mdelay(1);
	gpio_set_value(rdbg->pdata.gpio_resetn, 1);
}

static int radio_ioc_debug_reset(struct radio_debug *rdbg, int nr)
{
	if(nr >= rdbg->pdata.nr_radios)
		return -EINVAL;

	radio_debug_enable(rdbg, 1);

	radio_debug_set_mux(rdbg, nr);
	radio_debug_reset(rdbg, nr);
	radio_debug_set_mux(rdbg, -1);

	radio_debug_enable(rdbg, 0);

	return 0;
}

static int radio_ioc_debug_stop(struct radio_debug *rdbg, int nr)
{
	if(nr >= rdbg->pdata.nr_radios)
		return -EINVAL;

	radio_debug_reset(rdbg, nr);
	radio_debug_set_mux(rdbg, -1);

	radio_debug_enable(rdbg, 0);

	return 0;
}

static int radio_ioc_debug_get_info(struct radio_debug *rdbg,
				    struct radio_debug_info *info)
{
	memset(info, 0, sizeof(*info));

	info->nr_radios = rdbg->pdata.nr_radios;
	strncpy(info->version, DRV_VERSION, RADIO_VERSION_LEN);
	return 0;
}

/*****************************************************************/
/******************* Init / Deinit *******************************/
/*****************************************************************/
static int radio_debug_init_pdata(struct radio_debug *rdbg)
{
	int i, ret;

	ret = gpio_request(rdbg->pdata.gpio_dd, "dd");
	if(ret) {
		dev_err(rdbg->dev, "Could not request GPIO %d\n",
			rdbg->pdata.gpio_dd);
		goto out;
	}
	ret = gpio_request(rdbg->pdata.gpio_dc, "dc");
	if(ret) {
		dev_err(rdbg->dev, "Could not request GPIO %d\n",
			rdbg->pdata.gpio_dc);
		goto out_free_dd;
	}
	ret = gpio_request(rdbg->pdata.gpio_resetn, "resetn");
	if(ret) {
		dev_err(rdbg->dev, "Could not request GPIO %d\n",
			rdbg->pdata.gpio_resetn);
		goto out_free_dc;
	}

	if(gpio_is_valid(rdbg->pdata.gpio_direction)) {
		ret = gpio_request(rdbg->pdata.gpio_direction, "direction");
		if(ret) {
			dev_err(rdbg->dev, "Could not request GPIO %d\n",
				rdbg->pdata.gpio_direction);
			goto out_free_resetn;
		}
		gpio_direction_output(rdbg->pdata.gpio_direction, DIRECTION_OUTPUT);
	}

	if(gpio_is_valid(rdbg->pdata.gpio_ff)) {
		ret = gpio_request(rdbg->pdata.gpio_ff, "ff");
		if(ret) {
			dev_err(rdbg->dev, "Could not request GPIO %d\n",
				rdbg->pdata.gpio_ff);
			goto out_free_direction;
		}
		gpio_direction_output(rdbg->pdata.gpio_ff, DEBUG_DISABLED);
	}

	for(i = 0; i < rdbg->pdata.nr_mux_gpios; i++) {
		ret = gpio_request(rdbg->pdata.gpio_mux[i], "radio dbg mux");
		if(ret < 0) {
			dev_err(rdbg->dev, "Could not request GPIO %d\n",
				rdbg->pdata.gpio_mux[i]);
			while(i--)
				gpio_free(rdbg->pdata.gpio_mux[i]);
			goto out_free_ff;
		}
		/* Default to high, which configures mux to tri-state */
		gpio_direction_output(rdbg->pdata.gpio_mux[i], 1);
	}

	gpio_direction_output(rdbg->pdata.gpio_dd, 1);
	gpio_direction_output(rdbg->pdata.gpio_dc, 1);
	gpio_direction_output(rdbg->pdata.gpio_resetn, 1);

	return 0;

out_free_ff:
	if(gpio_is_valid(rdbg->pdata.gpio_ff))
		gpio_free(rdbg->pdata.gpio_ff);
out_free_direction:
	if(gpio_is_valid(rdbg->pdata.gpio_direction))
		gpio_free(rdbg->pdata.gpio_direction);
out_free_resetn:
	gpio_free(rdbg->pdata.gpio_resetn);
out_free_dc:
	gpio_free(rdbg->pdata.gpio_dc);
out_free_dd:
	gpio_free(rdbg->pdata.gpio_dd);
out:
	return ret;
}

static void radio_debug_deinit_pdata(struct radio_debug *rdbg)
{
	unsigned i;

	for(i = 0; i < rdbg->pdata.nr_mux_gpios; i++)
		gpio_free(rdbg->pdata.gpio_mux[i]);
	if(gpio_is_valid(rdbg->pdata.gpio_ff))
		gpio_free(rdbg->pdata.gpio_ff);
	if(gpio_is_valid(rdbg->pdata.gpio_direction))
		gpio_free(rdbg->pdata.gpio_direction);
	gpio_free(rdbg->pdata.gpio_dd);
	gpio_free(rdbg->pdata.gpio_dc);
	gpio_free(rdbg->pdata.gpio_resetn);
}

/*****************************************************************/
/******************* File ops ************************************/
/*****************************************************************/
static long radio_debug_ioctl(struct file *file, unsigned int req,
			      unsigned long arg)
{
	int ret;
	struct radio_ioc_debug_tx_rx debug;
	struct radio_debug *rdbg = file->private_data;
	int val;
	struct radio_debug_info info;

	/* copy from user memory for each request type */
	switch(req) {
		case RADIO_DEBUG_START:
		case RADIO_DEBUG_STOP:
		case RADIO_DEBUG_RESET:
			/* val is the radio index to act on */
			if(copy_from_user(&val, (void*)arg, sizeof(val)))
				return -EFAULT;
			break;
		case RADIO_DEBUG_TX_RX:
			if(copy_from_user(&debug, (void*)arg, sizeof(debug)))
				return -EFAULT;
			break;
		default:
			ret = -EINVAL;
			break;
	}

	/* Perform ioctl */
	switch(req) {
		/* Debug / flashing commands */
		case RADIO_DEBUG_START:
			ret = radio_ioc_debug_start(rdbg, val);
			break;
		case RADIO_DEBUG_STOP:
			ret = radio_ioc_debug_stop(rdbg, val);
			break;
		case RADIO_DEBUG_RESET:
			ret = radio_ioc_debug_reset(rdbg, val);
			break;
		case RADIO_DEBUG_TX_RX:
			ret = radio_ioc_debug_tx_rx(rdbg, &debug);
			break;
		case RADIO_DEBUG_GET_INFO:
			ret = radio_ioc_debug_get_info(rdbg, &info);
			break;
		default:
			ret = -EINVAL;
			break;
	}
	/* Write back to userspace */
	switch(req) {
		case RADIO_DEBUG_GET_INFO:
			if(copy_to_user((void*)arg, &info, sizeof(info)))
				ret = -EINVAL;
			break;
	}
	return ret;
}

static int radio_debug_open(struct inode *inode, struct file *file)
{
	struct radio_debug *rdbg = container_of(inode->i_cdev, struct radio_debug, cdev);

	/* We support single open only */
	if(!mutex_trylock(&rdbg->file_open))
		return -EBUSY;

	file->private_data = rdbg;
	return 0;
}

static int radio_debug_release(struct inode *inode, struct file *file)
{
	struct radio_debug *rdbg = file->private_data;

	mutex_unlock(&rdbg->file_open);
	return 0;
}

static const struct file_operations radio_debug_file_ops = {
	.owner		= THIS_MODULE,
	.unlocked_ioctl = radio_debug_ioctl,
	.open		= radio_debug_open,
	.release	= radio_debug_release,
};

static int radio_debug_probe(struct platform_device *pdev)
{
	int ret;
	struct grover_radio_debug_platform_data *pdata;
	struct radio_debug *rdbg;

	pdata = pdev->dev.platform_data;
	if(!pdata) {
		dev_err(&pdev->dev, "No platform data provided!\n");
		return -EINVAL;
	}

	rdbg = kzalloc(sizeof(*rdbg), GFP_KERNEL);
	if(!rdbg)
		return -ENOMEM;

	/* Allocate major/minor numbers for chrdev */
	ret = alloc_chrdev_region(&rdbg->major_number,
				  0,
				  1,
				  DRV_NAME);
	if(ret) {
		printk(KERN_ERR "%s: Cannot allocate chrdev: %d\n", DRV_NAME, ret);
		goto out;
	}

	mutex_init(&rdbg->file_open);

	rdbg->pdata = *pdata;
	/* Initialize according to platform data */
	ret = radio_debug_init_pdata(rdbg);
	if(ret)
		goto out_unreg_region;

	/* Initialize and add chrdev */
	cdev_init(&rdbg->cdev, &radio_debug_file_ops);
	rdbg->cdev.owner = THIS_MODULE;

	ret = cdev_add(&rdbg->cdev,
		       rdbg->major_number, 1);
	if(ret) {
		dev_err(&pdev->dev, "Cannot add cdev: %d\n", ret);
		goto out_deinit_pdata;
	}

	/* Create device for sysfs layer */
	rdbg->dev = device_create(grover_class,
				   &pdev->dev,
				   rdbg->major_number,
				   rdbg,
				   "grover_debug");
	if(IS_ERR(rdbg->dev)) {
		ret = PTR_ERR(rdbg->dev);
		dev_err(&pdev->dev, "Cannot create device: %d\n", ret);
		goto out_del_cdev;
	}

	platform_set_drvdata(pdev, rdbg);

	return 0;

out_del_cdev:
	cdev_del(&rdbg->cdev);
out_deinit_pdata:
	radio_debug_deinit_pdata(rdbg);
out_unreg_region:
	unregister_chrdev_region(MAJOR(rdbg->major_number), 1);
out:
	return ret;
}

static int radio_debug_remove(struct platform_device *pdev)
{
	struct radio_debug *rdbg = platform_get_drvdata(pdev);

	device_destroy(grover_class, rdbg->major_number);
	cdev_del(&rdbg->cdev);
	radio_debug_deinit_pdata(rdbg);
	unregister_chrdev_region(MAJOR(rdbg->major_number), 1);
	kfree(rdbg);
	platform_set_drvdata(pdev, NULL);
	return 0;
}

struct platform_driver radio_debug_driver = {
	.driver = {
		.name	= DRV_NAME,
		.owner	= THIS_MODULE,
	},
	.probe	= radio_debug_probe,
	.remove	= __devexit_p(radio_debug_remove),
};
