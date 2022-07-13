#ifndef __RADIO_API_H__
#define __RADIO_API_H__

#include "radiocommands.h"

#define RADIO_MAX_MESSAGE_SIZE  (sizeof(struct RadioMessage))
#define RADIO_MAX_RX_SIZE (sizeof(RadioCommandPacket) + sizeof(uint8_t))

struct radio_transaction
{
	/* Userspace pointer to buffer */
	uint8_t	*buf;
	/* Length of buffer (and transaction) */
	int	len;
};

struct radio_write
{
	/* Number or radio addressed
	 * 0-127 are RX radios and 128-255 are TX radios */
	uint8_t radio;
	/* Data to send, first byte is control */
	struct radio_transaction	tx;
};

struct radio_read
{
	/* Number or radio addressed
	 * 0-127 are RX radios and 128-255 are TX radios */
	uint8_t radio;
	/* Data to send, first byte is control */
	struct radio_transaction	tx;
	/* Data received, first byte is control. */
	struct radio_transaction	rx;
}

// These are returned through read()
struct radio_message
{
	int radio;
	int length;
	uint8_t msg[0];
};

#define RADIO_SET_ENABLED	_IOW('r', 0, int)
#define RADIO_GET_ENABLED	_IOR('r', 1, int)
#define RADIO_SET_DFL_TR_LEN   _IOW('r', 2, int) // User for polled/interrupt reads
#define RADIO_GET_DFL_TR_LEN   _IOR('r', 3, int)
#define RADIO_CTRL_WR       _IOW('r', 4, struct radio_write)
#define RADIO_CTRL_RD       _IOWR('r', 5, struct radio_read)

#endif // __RADIO_API_H__
