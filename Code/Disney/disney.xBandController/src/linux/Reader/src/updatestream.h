/*
 * updatestream.h
 *
 *  Created on: Jul 5, 2011
 *      Author: mvellon
 */

#ifndef UPDATESTREAM_H_
#define UPDATESTREAM_H_

#include "reader.h"

int CreateEventPostThread(struct Reader *pReader);
int FreeEventPostThread(struct Reader *pReader);
int EventPostResume(struct Reader *pReader);
int EventPostPause(struct Reader *pReader);

int EventPostUpdate(
    struct Reader *pReader,
    const char *pszURL,
    size_t sMaxEvents,
    size_t msecInterval,
    ssize_t sAfterEvent
    );

int EventPostShutdown(
    struct Reader *pReader,
    size_t sTimeToDeepSleep
    );

#endif /* UPDATESTREAM_H_ */
