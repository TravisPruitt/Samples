/*
 * priority.h
 *
 */

#ifndef PRIORITY_H_
#define PRIORITY_H_

#include "reader.h"
#include <sys/time.h>
#include <time.h>
#include <pthread.h>

typedef struct EventQueue
{
    pthread_mutex_t *pMutex;
    size_t iNextEventNumber; // Assigned when event is removed from queue
    size_t sSize;    // Maximum number of events array holds
    size_t sCount;   // Current number of events
    EVENT *Events;
} EVENTQUEUE;

int CreateQueue(EVENTQUEUE**);
void FreeQueue(EVENTQUEUE*);
size_t EventCount(EVENTQUEUE*);
int Enqueue(EVENTQUEUE*, EVENTINFO *pei);
int DequeueMatching(EVENTQUEUE *pQueue, size_t sMaxEvents, size_t sFirstEvent, const struct timeval *tvNow, EVENTINFO **ppEvents, size_t *psCount);
void DeleteEvents(EVENTQUEUE*);
void GetQueueStats(EVENTQUEUE*, size_t *psEvents, struct timeval *ptvEarliest, struct timeval *ptvLatest);
size_t NextEventNumber(EVENTQUEUE*);
size_t AcquireNextEventNumber(EVENTQUEUE*);
#endif /* PRIORITY_H_ */
