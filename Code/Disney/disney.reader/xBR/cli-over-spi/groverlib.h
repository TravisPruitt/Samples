#ifndef _GROVERLIB_H_
#define _GROVERLIB_H_

#include <stdint.h>

/* Returns the number of radios found */
int groverlib_initialize(void);
/* Returns the number of radios found */
int groverlib_radio_count(void);
/* Returns zero on success, negative error */
int groverlib_spitest(int radio, uint8_t *tx, uint8_t *rx,
		      unsigned length, unsigned speed);

/* Returns zero on success, negative error */
int groverlib_radio_reset(int radio);

/* Returns zero on success, negative error */
int groverlib_radio_tx_rx(int radio,
			  uint8_t *tx,
			  unsigned txlen,
			  uint8_t *rx,
			  unsigned rxlen);

int groverlib_radio_reset(int radio);
#endif

