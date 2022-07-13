#ifndef __GROVER_RADIO_LOADGEN_H__
#define __GROVER_RADIO_LOADGEN_H__

struct radio_loadgen
{
	struct hrtimer		timer;
	uint8_t			seq;
	unsigned long		band;
	uint8_t			radio;
	uint8_t 		rseq;
	/* Configuration values */
	/* Load generator wakeup frequency */
	int			hz;
	/* max packets per wakeup */
	int			maxppw;
	/* Minimum signal strength (for random) */
	int			ss_min;
	/* Signal strenght range, up to this is added to ss_min */
	int			ss_range;
	/* How many different bands (band ids) to generate
	   This number must be (4*N) + 1 for proper sequence
	   number generation */
	int			bands;
};

#define LOADGEN_SYSFS_FN(name) \
static ssize_t show_loadgen_##name(struct device *dev, struct device_attribute *attr, char *buf) \
{ \
	struct radio *radio = dev_get_drvdata(dev); \
	return snprintf(buf, 16, "%d\n", radio->loadgen.name); \
} \
static ssize_t store_loadgen_##name(struct device *dev, struct device_attribute *attr, const char *buf, size_t count) \
{ \
	struct radio *radio = dev_get_drvdata(dev); \
	radio->loadgen.name = simple_strtol(buf, NULL, 10); \
	return count; \
}

#endif /* __GROVER_RADIO_LOADGEN_H__ */
