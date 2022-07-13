/*
 * queue.c
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
#include <curl/curl.h>
#include "globals.h"
#include "queue.h"

typedef struct Queue
{
	QUEUENODE* pFirst;
	QUEUENODE* pLast;
} QUEUE;

static QUEUE* pQueue;

static int GetBandInfo(QUEUENODE* pNode);
static size_t write_callback(void *ptr, size_t size, size_t nmemb, void *stream);


int Queue_Init()
{
	pQueue = (QUEUE*) malloc(sizeof(QUEUE));
	if(pQueue == NULL)
	{
		return 1;
	}

	pQueue->pFirst = NULL;
	pQueue->pLast = NULL;

	return 0;
}

int Queue_Empty()
{
	if(pQueue != NULL)
	{
		//Free up all the nodes in the queue first.
		while(pQueue->pFirst != pQueue->pLast)
		{
			QUEUENODE* pTemp = pQueue->pFirst;
			pQueue->pFirst = pQueue->pFirst->pNext;

			free(pTemp);
		}


		free(pQueue);
		pQueue = NULL;
	}

	return 0;
}

int Enqueue(QUEUENODE* pNode)
{
	if(pQueue == NULL)
	{
		int err = Queue_Init();
		if(err != 0)
		{
			return err;
		}
	}

	if(pNode->pszLRID == NULL &&
	   pNode->pszTapID == NULL)
	{
		char szLRID[2048];
		char szTapID[2048];
		int err = GetBandInfo(pNode);
		if (err != 0)
		{
			return err;
		}

		pNode->pszLRID = strdup(szLRID);
		pNode->pszTapID = strdup(szTapID);
	}


	if (pQueue->pFirst == NULL)
	{
		pQueue->pFirst = pNode;
		pQueue->pLast = pNode;
	}
	else
	{
		pQueue->pLast->pNext = pNode;
		pQueue->pLast = pNode;
	}

	pNode->pNext = NULL;

	return 0;
}

int Dequeue(QUEUENODE* pNode)
{
	if(pQueue == NULL)
	{
		return 2;
	}

	if(pQueue->pFirst == NULL)
	{
		pNode = NULL;
		return 1;
	}

	pNode->nGuestID = pQueue->pFirst->nGuestID;
	pNode->nBandID = pQueue->pFirst->nBandID;
	pNode->pszLRID = strdup(pQueue->pFirst->pszLRID);
	pNode->pszTapID = strdup(pQueue->pFirst->pszTapID);

	QUEUENODE* pTemp = pQueue->pFirst;

	if(pQueue->pFirst == pQueue->pLast)
	{
		pQueue->pFirst = NULL;
		pQueue->pLast = NULL;
	}
	else
	{
		pQueue->pFirst = pQueue->pFirst->pNext;
	}

	free(pTemp);
	return 0;
}

static int GetBandInfo(QUEUENODE* pNode)
{
	int err = 0;
    CURL *curl;
	CURLcode res;

	char szData[2048];

	// get a curl handle
	curl = curl_easy_init();
	if(curl)
	{
		// specify target URL, and note that this URL should include a file
		// name, not only a directory
		char szURL[1024];
		strcpy(szURL, pGuests->pszUrlRoot);
		char szOne[256];

		sprintf( szOne,
				"/xbands/id/%d", pNode->nBandID);
		strcat(szURL, szOne);

		// we want to use our own write function
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_callback);

		curl_easy_setopt(curl, CURLOPT_WRITEDATA, szData);

		curl_easy_setopt(curl, CURLOPT_URL, szURL);

		// Now run off and do what you've been told! */
		res = curl_easy_perform(curl);

		if(res != CURLE_OK)
		{
			err = 1;
		}

		// always cleanup
		curl_easy_cleanup(curl);

		char szID[17];

		//Parse JSON
		char* pszTemp = strstr(szData,"lrid");

		int test = sscanf(pszTemp,"lrid\":\"%s",szID);

		if(test == 1)
		{
			szID[16] = '\0';
			pNode->pszLRID = strdup(szID);
			pszTemp = strstr(szData,"tapId");

			test = sscanf(pszTemp,"tapId\":\"%s",szID);
			if(test == 1)
			{
				szID[16] = '\0';
				pNode->pszTapID = strdup(szID);
			}
			else
			{
				err = 1;
			}
		}
		else
		{
			err = 1;
		}
	}
 	else
 	{
 		err = 1;
 	}

	return err;
}

static size_t
write_callback(void *ptr, size_t size, size_t nmemb, void *stream)
{
	strncpy((char*)stream, ptr, size * nmemb);
	return size * nmemb;
}

