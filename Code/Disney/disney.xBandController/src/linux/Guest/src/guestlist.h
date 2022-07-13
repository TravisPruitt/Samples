/*
 * guestlist.h
 *
 *  Created on: Sept 7, 2011
 *      Author: tcrane
 */

#ifndef GUESTLIST_H_
#define GUESTLIST_H_

struct ListNode;

typedef struct ListNode
{
	GUESTINFO* pGuest;
	struct ListNode* pNext;
} LISTNODE;

typedef struct List
{
	LISTNODE* pHead;
	LISTNODE* pTail;
} LIST;

int List_Init();
int List_Empty();
int List_Add(GUESTINFO* pGuest);


#endif /* GUESTLIST_H_ */
