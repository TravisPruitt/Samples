//#define NONBLOCKING

/*
 * chirp.c
 *
 *  Created on: Jun 20, 2011
 *      Author: mvellon
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
#include "chirp.h"
#include "simulate.h"

#define COMFORTZONE 2.2

static int TapDap(READERINFO *pri, BANDINFO *pb);
static int PingLRR(READERINFO *pri, BANDINFO *pb, double d);
static int WriteToReader(READERINFO *pri, char *psz);

int
ChirpGuest(GUESTINFO *pgi)
{
	int err = 0;

	// iterate through the readers, deciding which ones to write to. Handle special
	// cases (xpass readers, merge readers, etc.)

	int ir;
	int bSentLRR = 0;
	for (ir=0; ir<pAttraction->cReaders; ir++)
	{
		// peg
		READERINFO *pr = &pReaders[ir];

		// ignore far off LRRs
		if (!pr->bIsTap)
		{
			// calculate distance d
			double xd = (double) pgi->x - pr->x;
			double yd = (double) pgi->y - pr->y;
			double d = sqrt(xd*xd + yd*yd);

			// if too far, ignore it
			if (d >= pr->range)
			{
				// yep - ignore it
				continue;
			}

//			if (bVerbose)
//				printf("Chirp Guest %s x:%f y:%f\n", pgi->x,pgi->y, pgi->band);

			// have LRR in range, Talk to It
			err = PingLRR(pr, &pgi->band, d);
			if (err != 0)
				return err;
			bSentLRR = 1;
		}
		else
		{
			// handle entry/exit dap
			if ((pgi->state==INDETERMINATE || pgi->state==ENTERED) &&
				strcmp(pr->pszName, "entrance-exit")==0)
			{
				// current time
				struct timeval tsNow;
				gettimeofday(&tsNow, NULL);

				// calculate elapsed time between entered and now
				double dSecondsSinceEntered = tsNow.tv_sec - pgi->tEntered.tv_sec + ((double) tsNow.tv_usec - pgi->tEntered.tv_usec) / 1000000.0;

				if(pgi->state==INDETERMINATE &&
						pgi->x > pr->x)
				{
					pgi->state = ENTERED;

					// set the time entered
					gettimeofday(&pgi->tEntered, NULL);

					// tap the entry/exit dap
					err = TapDap(pr, &pgi->band);
					if (err!=0)
						return err;

				}
				else if(pgi->state==ENTERED &&
						dSecondsSinceEntered > pgi->dExitDelay &&
						pgi->x <= pr->x)
				{
					if(!pgi->bEscape)
					{
						pgi->state = EXITED;

						// tap the entry/exit dap
						err = TapDap(pr, &pgi->band);
						if (err!=0)
							return err;

					}
					else
					{
						//Don't tap the dap, means escape
						pgi->state = ESCAPED;
					}
				}
			}
			else
			{
				// ignore other daps
				continue;
			}
		}
	}

	// if we sent anything to an LRR, update the packet sequence number
	if (bSentLRR)
	{
		pgi->band.nPacketSequence = (pgi->band.nPacketSequence + 1) % 256;

		// bump the frequency every 16
//		if ( (pgi->band.nPacketSequence | 0xF) == 0 )
			pgi->band.nFrequency = (pgi->band.nFrequency + 1) % 4;
	}

	return 0;
}

static int
TapDap(READERINFO *pri, BANDINFO *pb)
{
	char szBuf[256];

	// format: bandID
	sprintf(szBuf, "%16s\n", pb->pszTapID);

	// TODO: error check the return codes
	// return WriteToReader(pri, szBuf);
	WriteToReader(pri, szBuf);

	return 0;
}

#define DBREFERENCE 0.001
static int
PingLRR(READERINFO *pri, BANDINFO *pb, double d)
{
	char szBuf[256];

	// calculate signal strength as the inverse of the square of the distance
	int nSignalStrength = (int)(20.0 * log10((1.0/(d*d))/DBREFERENCE));
	if (nSignalStrength>63)
		nSignalStrength = 63;
	else if(nSignalStrength<0)
	{
		nSignalStrength = 0;
	}

	int nFreq = 2401;

	switch(pb->nFrequency)
	{
		case 0:
		{
			nFreq = 2401;
			break;
		}

		case 1:
		{
			nFreq = 2424;
			break;

		}
		case 2:
		{
			nFreq = 2450;
			break;

		}
		case 3:
		{
			nFreq = 2476;
			break;

		}
	}

	// format: bandId packetsequence signalstrength channel frequency
	sprintf(szBuf, "%16s %03d %02d %1d %02d\n", pb->pszLRID, pb->nPacketSequence, nSignalStrength, pb->nChannel, nFreq);

	// TODO: error check the return codes
	// return WriteToReader(pri, szBuf);
	WriteToReader(pri, szBuf);

	return 0;
}

static int
WriteToReader(READERINFO *pri, char *psz)
{
//	if (bVerbose)
//		printf("Sending %s to reader %s\n", psz, pri->pszName);

	// if the reader FIFO hasn't been opened yet, do it now
	if (pri->pfQ==NULL)
	{
#ifdef NONBLOCKING
		int nfd = open(pri->pszQName, O_RDWR);
		if (nfd==-1)
		{
			printf("!! Error opening named pipe %s. Errno: %d\n", pri->pszQName, errno);
			return 2;
		}

		// set non blocking
		int err = fcntl(nfd, F_SETFL, O_WRONLY | O_NONBLOCK );
		if (err==-1)
		{
			printf("!! Error setting O_NONBLOCK named pipe %s. Errno: %d\n", pri->pszQName, errno);
			return 2;
		}
#else
		int nfd = open(pri->pszQName, O_WRONLY);
		if (nfd==-1)
		{
			printf("!! Error opening named pipe %s. Errno: %d\n", pri->pszQName, errno);
			return 2;
		}
#endif

		pri->pfQ = fdopen(nfd, "w");
		if (pri->pfQ==NULL)
		{
			printf("!! Error opening named pipe %s\n", pri->pszQName);
			return 2;
		}
	}
	if (fputs(psz, pri->pfQ)==EOF)
	{
		printf("!! Failed to write to pipe %s\n", pri->pszQName);
		return 2;
	}
	fflush(pri->pfQ);

	return 0;
}
