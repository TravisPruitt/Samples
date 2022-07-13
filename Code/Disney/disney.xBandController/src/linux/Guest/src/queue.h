/*
 * queue.h
 *
 *  Created on: Sept 1, 2011
 *      Author: tcrane
 */

#ifndef QUEUE_H_
#define QUEUE_H_

struct QueueNode;

typedef struct QueueNode
{
	int nGuestID;
	int nBandID;
	char* pszLRID;
	char* pszTapID;
	struct QueueNode* pNext;
} QUEUENODE;


int Queue_Init();
int Queue_Empty();
int Enqueue(QUEUENODE* pQueueNode);
int Dequeue(QUEUENODE* pQueueNode);


#endif /* QUEUE_H_ */
