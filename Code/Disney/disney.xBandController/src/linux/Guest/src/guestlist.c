/*
 * guestList.c
 *
 *  Created on: Sept 1, 2011
 *      Author: tcrane
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <math.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>
#include <sys/time.h>
#include <fcntl.h>
#include <errno.h>
#include "globals.h"
#include "guestlist.h"

static LIST* pList;

int List_Init()
{
	pList = (LIST*) malloc(sizeof(LIST));
	if(pList == NULL)
	{
		return 1;
	}

	pList->pHead = NULL;
	pList->pTail = NULL;

	return 0;
}

int List_Empty()
{
	if(pList != NULL)
	{
		LISTNODE* pCurrent = pList->pHead;

		while(pCurrent != NULL)
		{
			LISTNODE* pTemp = pCurrent;
			pCurrent = pCurrent->pNext;
			free(pTemp);
		}

		free(pList);
		pList = NULL;
	}

	return 0;
}

int List_Add(GUESTINFO* pGuest)
{
	if(pList == NULL)
	{
		int err = List_Init();
		if(err != 0)
		{
			return err;
		}
	}

	LISTNODE *pNode = (LISTNODE *)malloc(sizeof(LISTNODE));
	if (pNode == NULL)
	{
		return 1;
	}

	pNode->pGuest = pGuest;

	//If there is no head then the list is empty, make the new node the head,
	//leave the tail to be null.
	if(pList->pHead == NULL)
	{
		pList->pHead = pNode;
	}
	else
	{
		//If there is no tail then make the tail the new node.
		if(pList->pTail == NULL)
		{
			pList->pTail = pNode;
		}
		else
		{
			//Set the previous Tail to the new node.
			pList->pTail->pNext = pNode;

			//Reset the tail node.
			pList->pTail = pNode;
		}
	}

	return 0;
}

