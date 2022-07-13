/*
 * priority.c
 *
 *
 * Priority queue based on timestamp.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <pthread.h>
#include <assert.h>

#include "priority.h"

int CreateQueue(EVENTQUEUE **ppQueue)
{
    int err;
    EVENTQUEUE *pQueue = NULL;
    pthread_mutex_t *pMutex = NULL;

    pQueue = malloc(sizeof(EVENTQUEUE));
    if (!pQueue)
    {
        err = ENOMEM;
        goto cleanup;
    }

    pMutex = malloc(sizeof(pthread_mutex_t));
    if (!pMutex)
    {
        err = ENOMEM;
        goto cleanup;
    }

    err = pthread_mutex_init(pMutex, NULL);
    assert(err == 0);

    pQueue->pMutex = pMutex;
    pMutex = NULL;

    pQueue->Events = NULL;
    pQueue->sSize = 0;
    pQueue->sCount = 0;
    pQueue->iNextEventNumber = 0;

    *ppQueue = pQueue;
    pQueue = NULL;

cleanup:
    FreeQueue(pQueue);
    return err;
}

void FreeQueue(EVENTQUEUE *pQueue)
{
    if (pQueue)
    {
        if (pQueue->Events)
        {
            size_t i;
            for (i = 0; i < pQueue->sCount; i++)
                free(pQueue->Events[i].ptr);
            free(pQueue->Events);
        }
        if (pQueue->pMutex)
        {
            pthread_mutex_destroy(pQueue->pMutex);
            free(pQueue->pMutex);
        }
        free(pQueue);
    }
}

/*
 * EventCount - returns the count of events in the queue
 */
size_t EventCount(EVENTQUEUE *pQueue)
{
    size_t count;
    assert(pthread_mutex_lock(pQueue->pMutex) == 0);
    count = pQueue->sCount;
    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
    return count;
}

size_t NextEventNumber(EVENTQUEUE *pQueue)
{
    size_t number;
    assert(pthread_mutex_lock(pQueue->pMutex) == 0);
    number = pQueue->iNextEventNumber;
    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
    return number;
}

size_t AcquireNextEventNumber(EVENTQUEUE *pQueue)
{
    size_t number;
    assert(pthread_mutex_lock(pQueue->pMutex) == 0);
    number = pQueue->iNextEventNumber++;
    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
    return number;
}

void swap(EVENT *ei1, EVENT *ei2)
{
    EVENT tmp = *ei1;
    *ei1 = *ei2;
    *ei2 = tmp;
}

int RemoveEvent(EVENTQUEUE *pQueue, EVENTINFO *ei, const struct timeval *tvNow)
{
    size_t rootIndex = 0;
    size_t childIndex;
    size_t swapIndex;

    if (pQueue->sCount < 1)
    {
        return 1;
    }
    if (tvNow != NULL && timercmp(&pQueue->Events[0].tv, tvNow, >))
    {
        return 1;
    } 

    ei->event = pQueue->Events[rootIndex];
    ei->nEventNumber = pQueue->iNextEventNumber++;
    if (pQueue->sCount > 1)
    {
        pQueue->Events[rootIndex] = pQueue->Events[pQueue->sCount - 1];
    }
    pQueue->sCount--;

    while (rootIndex * 2 + 1 < pQueue->sCount)
    {
        childIndex = rootIndex * 2 + 1; // left child
        swapIndex = rootIndex;
        if (timercmp(&pQueue->Events[childIndex].tv, &pQueue->Events[swapIndex].tv, <))
            swapIndex = childIndex;
        if (childIndex + 1 < pQueue->sCount &&
            timercmp(&pQueue->Events[childIndex+1].tv, &pQueue->Events[swapIndex].tv, <))
            swapIndex = childIndex + 1;
        if (swapIndex != rootIndex)
        {
            swap(&pQueue->Events[rootIndex], &pQueue->Events[swapIndex]);
            rootIndex = swapIndex;
        }
        else
            return 0;
    }
    return 0;
}

/*
 * Enqueue - adds an element to the queue, overwriting the earliest element if the queue is full
 */
int
Enqueue(EVENTQUEUE *pQueue, EVENTINFO *pei)
{
    int err = 0;

    size_t childIndex;

    assert(pei->event.tv.tv_usec >= 0);
    assert(pthread_mutex_lock(pQueue->pMutex) == 0);

    if (pQueue->sSize == 0 || pQueue->sCount == pQueue->sSize - 1)
    {
        size_t sNewSize = pQueue->sSize + 10000;
        EVENT *pNewEvents = malloc(sizeof(EVENT) * sNewSize);
        if (!pNewEvents)
        {
            err = ENOMEM;
            goto cleanup;
        }

        if (pQueue->Events)
        {
            memcpy(pNewEvents, pQueue->Events, pQueue->sSize * sizeof(EVENT));
            free(pQueue->Events);
        }
        pQueue->Events = pNewEvents;
        pQueue->sSize = sNewSize;
    }

    // Insert item into heap
    childIndex = pQueue->sCount;
    pQueue->Events[childIndex] = pei->event;
    pQueue->sCount++;
    while ( childIndex != 0 &&
            timercmp(&pQueue->Events[childIndex].tv, &pQueue->Events[childIndex / 2].tv, <))
    {
        swap(&pQueue->Events[childIndex], &pQueue->Events[childIndex / 2]);
        childIndex = childIndex / 2;
    }

cleanup:
    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
    return err;
}

/*
 * Dequeue - returns the earliest element from the queue. NULL if empty
 */
int
Dequeue(EVENTQUEUE *pQueue, EVENTINFO *ei, const struct timeval *tvNow)
{
    int ret = 1;

    assert(pthread_mutex_lock(pQueue->pMutex) == 0);

    ret = RemoveEvent(pQueue, ei, tvNow);

    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
    return ret;
}

int
DequeueMatching(EVENTQUEUE *pQueue, size_t sMaxEvents, size_t sFirstEvent, const struct timeval *tvNow, EVENTINFO **ppEvents, size_t *psCount)
{
    int err = 0;

    EVENTINFO *pEvents = NULL;
    size_t sSize = 10;
    size_t sCount = 0;
    EVENTINFO ei = { 0 };

    assert(pthread_mutex_lock(pQueue->pMutex) == 0);

    if (sMaxEvents < 1 || sMaxEvents > pQueue->sCount)
        sMaxEvents = pQueue->sCount;

    pEvents = malloc(sizeof(EVENTINFO) * sSize);
    if (!pEvents)
    {
        err = ENOMEM;
        goto cleanup;
    }

    for (sCount = 0; sCount < sMaxEvents && RemoveEvent(pQueue, &ei, tvNow) == 0;)
    {
        if (ei.nEventNumber >= sFirstEvent)
        {
            pEvents[sCount++] = ei;

            // When the array is filled, allocate a large one. If we can't, stop processing.
            if (sCount == (sSize - 1))
            {
                size_t sNewSize = sSize + 10;
                EVENTINFO *pNewEvents = malloc(sizeof(EVENTINFO) * sNewSize);
                if (pNewEvents == NULL)
                    break;

                memcpy(pNewEvents, pEvents, sSize * sizeof(EVENTINFO));
                free(pEvents);
                pEvents = pNewEvents;
                sSize = sNewSize;
            }
        }
    }

    if (sCount == 0)
    {
        free(pEvents);
        pEvents = NULL;
    }

    *ppEvents = pEvents;
    pEvents = NULL;
    *psCount = sCount;
    sCount = 0;

cleanup:
    if (pEvents)
    {
        free(pEvents);
        pEvents = NULL;
        sCount = 0;
    }
    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
    return err;
}

/*
 * DeleteEvents - removes all the elements from the queue
 */
void
DeleteEvents(EVENTQUEUE *pQueue)
{
    assert(pthread_mutex_lock(pQueue->pMutex) == 0);
    pQueue->sCount = 0;
    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
}

/*
 * GetQueueStats - returns count, earliest and latest element times
 */
void
GetQueueStats(EVENTQUEUE *pQueue, size_t *psEvents, struct timeval *ptvEarliest, struct timeval *ptvLatest)
{
    assert(pthread_mutex_lock(pQueue->pMutex) == 0);

    // get count
    *psEvents = pQueue->sCount;
    if (pQueue->sCount)
    {
        size_t i;
        struct timeval tvLatest;

        // if queue not empty, grab times from elements

        // earliest
        *ptvEarliest = tvLatest = pQueue->Events[0].tv;

        // have to search for the latest;
        for (i = 1; i < pQueue->sCount; i++)
            if (timercmp(&pQueue->Events[i].tv, &tvLatest, >))
                tvLatest = pQueue->Events[i].tv;
        *ptvLatest = tvLatest;
    }
    else
    {
        // return zeros if the queue is empty
        struct timeval tvZero = {0, 0};
        *ptvEarliest = tvZero;
        *ptvLatest = tvZero;
    }
    assert(pthread_mutex_unlock(pQueue->pMutex) == 0);
}
