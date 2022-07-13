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
#include <asm/uaccess.h>

#include <linux/spi/grover_radio.h>
#include "grover_radio_main.h"

#define GROVER_RADIO_SYSFS_INT(name) \
static ssize_t show_radio_##name(struct device *dev, struct device_attribute *attr, char *buf) \
{ \
	struct radio *radio = dev_get_drvdata(dev); \
	return snprintf(buf, 16, "%d\n", radio->name); \
} \
static ssize_t store_radio_##name(struct device *dev, struct device_attribute *attr, const char *buf, size_t count) \
{ \
	struct radio *radio = dev_get_drvdata(dev); \
	radio->name = simple_strtol(buf, NULL, 10); \
	return count; \
}

#define GROVER_RADIO_SYSFS_UINT(name) \
static ssize_t show_radio_##name(struct device *dev, struct device_attribute *attr, char *buf) \
{ \
	struct radio *radio = dev_get_drvdata(dev); \
	return scnprintf(buf, PAGE_SIZE, "%u\n", radio->name); \
} \
static ssize_t store_radio_##name(struct device *dev, struct device_attribute *attr, const char *buf, size_t count) \
{ \
	struct radio *radio = dev_get_drvdata(dev); \
	radio->name = simple_strtoul(buf, NULL, 10); \
	return count; \
}

static ssize_t show_nr_radios(struct device *dev, struct device_attribute *attr, char *buf)
{
	struct radio *radio = dev_get_drvdata(dev);
	return scnprintf(buf, PAGE_SIZE, "%u\n", radio->pdata.nr_radios);
}

static ssize_t show_tx_missed(struct device *dev, struct device_attribute *attr, char *buf)
{
	return scnprintf(buf, PAGE_SIZE, "%u\n", tx_radio_get_missed());
}

static ssize_t store_tx_missed(struct device *dev, struct device_attribute *attr, const char *buf, size_t count)
{
	tx_radio_set_missed(simple_strtoul(buf, NULL, 10));
	return count;
}

static ssize_t show_tx_sent(struct device *dev, struct device_attribute *attr, char *buf)
{
	return scnprintf(buf, PAGE_SIZE, "%u\n", tx_radio_get_sent());
}

static ssize_t show_tx_band_data_count(struct device *dev, struct device_attribute *attr, char *buf)
{
	return scnprintf(buf, PAGE_SIZE, "%lu\n", tx_radio_get_band_data_count());
}

static ssize_t show_tx_apb(struct device *dev, struct device_attribute *attr, char *buf)
{
	return scnprintf(buf, PAGE_SIZE, "%d\n", tx_radio_get_apb_data());
}

static ssize_t show_grover_trace_level(struct device *dev, struct device_attribute *attr, char *buf)
{
	return scnprintf(buf, PAGE_SIZE, "%x\n", grover_trace_level);
}

static ssize_t store_grover_trace_level(struct device *dev, struct device_attribute *attr, const char *buf, size_t count)
{
	grover_trace_level = simple_strtoul(buf, NULL, 16);
	return count;
}

GROVER_RADIO_SYSFS_UINT(rx_pings);
GROVER_RADIO_SYSFS_UINT(radio_timeouts);
GROVER_RADIO_SYSFS_UINT(queue_overflows);

static DEVICE_ATTR(rx_pings, 0644, show_radio_rx_pings, store_radio_rx_pings);
static DEVICE_ATTR(radio_timeouts, 0644, show_radio_radio_timeouts, store_radio_radio_timeouts);
static DEVICE_ATTR(queue_overflows, 0644, show_radio_queue_overflows, store_radio_queue_overflows);
static DEVICE_ATTR(nr_radios, 0444, show_nr_radios, NULL);
static DEVICE_ATTR(tx_missed, 0644, show_tx_missed, store_tx_missed);
static DEVICE_ATTR(tx_sent, 0444, show_tx_sent, NULL);
static DEVICE_ATTR(tx_band_data_count, 0444, show_tx_band_data_count, NULL);
static DEVICE_ATTR(tx_apb, 0444, show_tx_apb, NULL);
static DEVICE_ATTR(trace_level, 0664, show_grover_trace_level, store_grover_trace_level);

static struct device_attribute *grover_radio_attrs[] = {
	&dev_attr_rx_pings,
	&dev_attr_radio_timeouts,
	&dev_attr_queue_overflows,
	&dev_attr_nr_radios,
	&dev_attr_tx_missed,
	&dev_attr_tx_sent,
	&dev_attr_tx_band_data_count,
	&dev_attr_tx_apb,
	&dev_attr_trace_level,
	NULL
};


int radio_sysfs_init(struct radio *radio)
{
	int ret, i;

	for(i = 0; grover_radio_attrs[i]; i++) {
		ret = device_create_file(radio->dev, grover_radio_attrs[i]);

		/* On error, cleanup and exit */
		if(ret) {
			while(i--)
				device_remove_file(radio->dev,
						   grover_radio_attrs[i]);
			return ret;
		}

	}

	return 0;
}

void radio_sysfs_deinit(struct radio *radio)
{
	int i;
	/* Order does not matter */
	for(i = 0; grover_radio_attrs[i]; i++)
		device_remove_file(radio->dev, grover_radio_attrs[i]);
}

