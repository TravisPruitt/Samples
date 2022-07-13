#include <sys/time.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <stdint.h>
#include <sys/ioctl.h>
#include <string.h>
#include <unistd.h>

#include "grover_radio.h"

#define MAX_RADIO	64
#define MAX_DRIVERS	8

struct radio
{
	int fd;
	int index;
};

struct radio_context
{
	unsigned nr_radios;
	struct radio r[MAX_RADIO];
};

static struct radio_context r;

#ifndef MAX_PATH
#define MAX_PATH	2048
#endif

static void radio_probe(int fd)
{
	int ret;
	unsigned i;
	struct radio_info info;

	ret = ioctl(fd, RADIO_GET_INFO, &info);
	if(ret < 0)
		return;

	for(i = 0; i < info.rx_radios; i++) {
		r.r[r.nr_radios].fd = fd;
		r.r[r.nr_radios++].index = i;
	}
}

int groverlib_initialize(void)
{
	char path[MAX_PATH];
	int fd;
	int i;

	for(i = 0; i < MAX_DRIVERS; i++)
	{
		snprintf(path, MAX_PATH, "/dev/grover_radio%d", i);

		fd = open(path, O_RDWR);
		if(fd < 0) {
			if(r.nr_radios) {
				// Set up TX radio
				r.r[r.nr_radios].fd = r.r[r.nr_radios - 1].fd;
				r.r[r.nr_radios].index = 0xff;
				r.nr_radios++;
			}
			goto out;
		}

		radio_probe(fd);
	}

out:
#if 0
	for(i = 0; i < MAX_RADIO; i++)
	{
		printf("[%d] fd=%d index=%d\n",
		       i, r.r[i].fd, r.r[i].index);
	}
#endif
	return r.nr_radios;
}

int groverlib_radio_count(void)
{
	return r.nr_radios;
}

static int radio_fd(int radio)
{
	return r.r[radio].fd;
}

#define FD(radio) radio_fd(radio)

int groverlib_spitest(int radio, uint8_t *tx, uint8_t *rx,
		      unsigned length, unsigned speed)
{
	struct radio_ioc_spitest s;
	int ret;

	memset(&s, 0, sizeof(s));

	s.radio = r.r[radio].index;
	s.tx = tx;
	s.rx = rx;
	s.length = length;
	s.spi_speed = speed;

	ret = ioctl(FD(radio), RADIO_SPITEST, &s);
	if(ret) {
		printf("Could not do RADIO_SPI_TEST: %s\n", strerror(errno));
	}
	return ret;
}

int groverlib_radio_tx_rx(int radio,
			  uint8_t *tx,
			  unsigned txlen,
			  uint8_t *rx,
			  unsigned rxlen)
{
	struct radio_read rd;
	int ret;

	memset(&rd, 0, sizeof(rd));
	rd.radio = r.r[radio].index;
	rd.tx.buf = tx;
	rd.tx.len = txlen;
	rd.rx.buf = rx;
	rd.rx.len = rxlen;
	rd.rx_timeout.tv_sec = 1;

	ret = ioctl(FD(radio), RADIO_CTRL_RD, &rd);
	if(ret < 0)
		printf("RADIO_CTRL_RD failed: %s\n", strerror(errno));
	return ret;
}

int groverlib_radio_reset(int radio)
{
	return ioctl(FD(radio), RADIO_FORCE_RESET, &radio);
}
