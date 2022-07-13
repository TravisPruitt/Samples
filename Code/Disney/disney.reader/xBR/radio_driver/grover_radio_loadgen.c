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
#include <linux/ktime.h>
#include <linux/hrtimer.h>
#include <linux/random.h>
#include <asm/uaccess.h>
#include <mach/gpio.h>

#include <linux/spi/grover_radio.h>
#include "grover_radio_main.h"

#ifdef LOADGEN

unsigned int get_random_uint(void)
{
	unsigned int r;

	get_random_bytes(&r, sizeof(r));
	return r;
}

static int8_t get_random_ss(struct radio_loadgen *loadgen)
{
	int random_range = (int)(get_random_uint() % loadgen->ss_range);
	return loadgen->ss_min + random_range;
}

static struct app_item* generate_ping(uint8_t radio,
				      unsigned long id,
				      uint8_t seq,
				      int8_t ss,
				      uint8_t rseq)
{
	struct app_item *ai;

	ai = kzalloc(sizeof(struct app_item) + 11, GFP_ATOMIC);
	if(!ai)
		return NULL;

	ai->rm.id = R_MSG_FROM_RADIO;
	ai->rm.radio = radio;
	ai->rm.length = 11;
	ai->rm.tbd = 0;
	ai->rm.msg[0] = 0x0E;
	ai->rm.msg[1] = 0xFF;
	ai->rm.msg[2] = id >> 24;
	ai->rm.msg[3] = id >> 16;
	ai->rm.msg[4] = id >> 8;
	ai->rm.msg[5] = id;
	ai->rm.msg[6] = seq;		// sequence number
	ai->rm.msg[9] = (uint8_t)ss;	// signal strength
	ai->rm.msg[10] = rseq;		// receiver seq

	return ai;
}

static int loadgen_inject_data(struct radio *radio, unsigned long pings)
{
	struct app_item *ai;
	struct list_head new_items;
	unsigned long flags;
	int8_t ss;
	unsigned r;
	struct radio_loadgen *loadgen = &radio->loadgen;

	INIT_LIST_HEAD(&new_items);

	while(pings--)
	{
		ss = get_random_ss(loadgen);
		r = loadgen->radio;
		// Inject same ping twice, for two radios
		ai = generate_ping(r,
				   loadgen->band,
				   loadgen->seq,
				   ss,
				   loadgen->rseq);
		if(!ai)
			return -ENOMEM;
		list_add_tail(&ai->list, &new_items);
		ai = generate_ping(r + 4,
				   loadgen->band,
				   loadgen->seq,
				   ss,
				   loadgen->rseq);
		if(!ai)
			return -ENOMEM;
		list_add_tail(&ai->list, &new_items);

		// Increment radio #, if reach end also increment rseq
		if(++loadgen->radio >= 4) {
			loadgen->radio = 0;
			loadgen->rseq++;
		}
		// Increment band id, if reach end, also increment seq
		if(++loadgen->band >= loadgen->bands) {
			loadgen->band = 0;
			loadgen->seq++;
		}
	}
	// Put new items into app queue
	spin_lock_irqsave(&radio->app_queue_lock, flags);
	list_splice_tail(&new_items, &radio->app_queue);
	spin_unlock_irqrestore(&radio->app_queue_lock, flags);
	// Wake up for poll()
	wake_up_interruptible(&radio->app_wait_queue);

	return 0;
}

static int loadgen_timer_tick(struct radio* radio)
{
	ktime_t ktime;
	unsigned pings;
	struct radio_loadgen *loadgen = &radio->loadgen;

	// No pings, just wake up to re-check module parameter value
	if(loadgen->hz == 0) {
		ktime = ktime_set(0, 250 * 1E6);
		hrtimer_start(&loadgen->timer, ktime, HRTIMER_MODE_REL);
		return 0;
	}

	// How many pings to inject this time?
	if(loadgen->maxppw > 1)
		pings = get_random_uint() % loadgen->maxppw;
	else
		pings = 1;

	// Inject pings to queue
	loadgen_inject_data(radio, pings);

	// Reschedule timer
	ktime = ktime_set(0, 1000000000/loadgen->hz);
	hrtimer_start(&loadgen->timer, ktime, HRTIMER_MODE_REL);
	return 0;
}

static enum hrtimer_restart loadgen_timer_callback(struct hrtimer *timer)
{
	struct radio_loadgen *loadgen = container_of(timer, struct radio_loadgen, timer);
	struct radio *radio = container_of(loadgen, struct radio, loadgen);
	(void)loadgen_timer_tick(radio);
	return HRTIMER_NORESTART;
}

LOADGEN_SYSFS_FN(hz);
LOADGEN_SYSFS_FN(maxppw);
LOADGEN_SYSFS_FN(ss_min);
LOADGEN_SYSFS_FN(ss_range);
LOADGEN_SYSFS_FN(bands);

static DEVICE_ATTR(loadgen_hz, 0644, show_loadgen_hz, store_loadgen_hz);
static DEVICE_ATTR(loadgen_maxppw, 0644, show_loadgen_maxppw, store_loadgen_maxppw);
static DEVICE_ATTR(loadgen_ss_min, 0644, show_loadgen_ss_min, store_loadgen_ss_min);
static DEVICE_ATTR(loadgen_ss_range, 0644, show_loadgen_ss_range, store_loadgen_ss_range);
static DEVICE_ATTR(loadgen_bands, 0644, show_loadgen_bands, store_loadgen_bands);

void radio_loadgen_deinit(struct radio *radio)
{
	hrtimer_cancel(&radio->loadgen.timer);
}

int radio_loadgen_init(struct radio *radio)
{
	hrtimer_init(&radio->loadgen.timer, CLOCK_MONOTONIC, HRTIMER_MODE_REL);
	radio->loadgen.timer.function = loadgen_timer_callback;

	radio->loadgen.seq = 0;
	radio->loadgen.band = 0;
	return loadgen_timer_tick(radio);
}

int radio_loadgen_probe(struct radio *radio)
{
	int ret;

	/* Initialize default values */
	radio->loadgen.hz = 0;
	radio->loadgen.maxppw = 1;
	radio->loadgen.ss_min = -120;
	radio->loadgen.ss_range = 40;
	radio->loadgen.bands = 13;

	ret = device_create_file(radio->dev, &dev_attr_loadgen_hz);
	if(ret)
		goto out;
	ret = device_create_file(radio->dev, &dev_attr_loadgen_maxppw);
	if(ret)
		goto out_remove_hz;
	ret = device_create_file(radio->dev, &dev_attr_loadgen_ss_min);
	if(ret)
		goto out_remove_maxppw;
	ret = device_create_file(radio->dev, &dev_attr_loadgen_ss_range);
	if(ret)
		goto out_remove_ss_min;
	ret = device_create_file(radio->dev, &dev_attr_loadgen_bands);
	if(ret)
		goto out_remove_ss_range;

	return 0;

out_remove_ss_range:
	device_remove_file(radio->dev, &dev_attr_loadgen_ss_range);
out_remove_ss_min:
	device_remove_file(radio->dev, &dev_attr_loadgen_ss_min);
out_remove_maxppw:
	device_remove_file(radio->dev, &dev_attr_loadgen_maxppw);
out_remove_hz:
	device_remove_file(radio->dev, &dev_attr_loadgen_hz);
out:
	return ret;
}

void radio_loadgen_remove(struct radio *radio)
{
	device_remove_file(radio->dev, &dev_attr_loadgen_bands);
	device_remove_file(radio->dev, &dev_attr_loadgen_ss_range);
	device_remove_file(radio->dev, &dev_attr_loadgen_ss_min);
	device_remove_file(radio->dev, &dev_attr_loadgen_maxppw);
	device_remove_file(radio->dev, &dev_attr_loadgen_hz);
}
#endif

#ifdef LOADGEN_MODULE
static int __init loadgen_init(void)
{
}

static void __exit loadgen_exit(void)
{
}

MODULE_AUTHOR("Juha Kuikka <juha.kuikka@synapse.com>");
MODULE_DESCRIPTION("Grover load generator");
MODULE_LICENSE("GPL v2");
MODULE_VERSION(DRV_VERSION);

module_init(loadgen_init);
module_exit(loadgen_exit);
#endif
