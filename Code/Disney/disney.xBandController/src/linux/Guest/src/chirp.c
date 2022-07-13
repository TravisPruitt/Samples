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
static GUESTINFO * FindLoadableGuest();

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

			// have LRR in range, Talk to It
			err = PingLRR(pr, &pgi->band, d);
			if (err != 0)
				return err;
			bSentLRR = 1;

			// handle special readers
			if (strncmp(pr->pszName, "entry", 5)==0 &&
				pgi->state==INDETERMINATE)
			{
				// only set state here if the guest is not an xpass guest
				if (!pgi->bHaveXPass)
					pgi->state = ENTERED;
			}
			else if (strncmp(pr->pszName, "load", 4)==0 &&
					(pgi->state==ENTERED || pgi->state==MERGED) )
			{
				// handle load readers in a special fashion since we don't want the
				// car's to pick up guests prematurely

				// put them in LOADING state only when they get right under the load readers
				if (pgi->x >= pr->x)
					pgi->state = LOADING;
			}
 		}
		else
		{
			// have a tap reader. Ignore it if the guest doesn't have an xpass
			if (!pgi->bHaveXPass)
				continue;

			// early short circuit
			if (pgi->state==MERGED)
				continue;

			// handle entry and merge daps

			// entry dap
			if (pgi->state==INDETERMINATE &&
				strcmp(pr->pszName, "xpassentry")==0 &&
				pgi->x > pr->x)
			{
				// tap the entry dap
				err = TapDap(pr, &pgi->band);
				if (err!=0)
					return err;

				pgi->state = ENTERED;
			}
			else if (pgi->state==ENTERED &&
					 strcmp(pr->pszName, "merge")==0 &&
					 pgi->x > pr->x)
			{
				// tap the merge dap
				err = TapDap(pr, &pgi->band);
				if (err!=0)
					return err;

				pgi->state = MERGED;
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

		// bump the frequency
		pgi->band.nFrequency = (pgi->band.nFrequency + 1) % 4;
	}

	return 0;
}

static int
TapDap(READERINFO *pri, BANDINFO *pb)
{
	char szBuf[256];

	// format: RFID SecureId LRID
	sprintf(szBuf, "%16s %16s %16s\n", pb->pszTapID, pb->pszSecureID, pb->pszLRID);

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
	int nSignalStrength = (int)(17.25 * log10((0.01)/(d*d)));

	//Clamp Signal strength from -90 to -40
	if (nSignalStrength>-40)
	{
		nSignalStrength = -40;
	}
	else if(nSignalStrength<-90)
	{
		nSignalStrength = -90;
	}

	// map the frequency
	int nFrequency;
	switch(pb->nFrequency)
	{
		case 0:
			nFrequency = 2401;
			break;

		case 1:
			nFrequency = 2424;
			break;

		case 2:
			nFrequency = 2450;
			break;

		case 3:
		default:
			nFrequency = 2476;
			break;
	}

	// format: bandId packetsequence signalstrength channel frequency
	// NOTE: nChannel is always zero and is ignored by the reader (it queues the event twice - once per channel)
	sprintf(szBuf, "%16s %03d %02d %1d %02d\n", pb->pszLRID, pb->nPacketSequence, nSignalStrength, pb->nChannel, nFrequency);

	// TODO: error check the return codes
	// return WriteToReader(pri, szBuf);
	WriteToReader(pri, szBuf);

	return 0;
}

int
ChirpCar(CARINFO *pci)
{
	int err = 0;

	// iterate through the readers, deciding which ones to write to. Handle special
	// cases

	int ir;
	int bSentLRR = 0;
	for (ir=0; ir<pAttraction->cReaders; ir++)
	{
		// peg
		READERINFO *pr = &pReaders[ir];

		// ignore taps
		if (pr->bIsTap)
			continue;

		// ignore far away LRRs

		// calculate distance d
		double xd = (double) pci->x - pr->x;
		double yd = (double) pci->y - pr->y;
		double d = sqrt(xd*xd + yd*yd);

		// if too far, ignore it
		if (d >= pr->range)
		{
			// yep - ignore it
			continue;
		}

		// have LRR in range, Talk to It
		err = PingLRR(pr, &pci->band, d);
		if (err != 0)
			return err;

		// set flag
		bSentLRR = 1;

		// handle special readers
		if (strncmp(pr->pszName, "load", 4)==0)
		{
			// if the car has room, load it, but only if we're really close to the reader
			if (d<1.5 && pci->pgi1==NULL && pci->pgi2==NULL)
			{
				// find two guests that are at the load location
				GUESTINFO *pgLoadable1 = FindLoadableGuest();
				if (pgLoadable1!=NULL)
				{
					pci->pgi1 = pgLoadable1;
					pgLoadable1->state = RIDING;

					if (bVerbose)
						printf("Guest %s is riding car %s\n", pgLoadable1->band.pszLRID, pci->pszCarName);
					GUESTINFO *pgLoadable2 = FindLoadableGuest();
					if (pgLoadable2!=NULL)
					{
						pci->pgi2 = pgLoadable2;
						pgLoadable2->state = RIDING;
						if (bVerbose)
							printf("Guest %s is riding car %s\n", pgLoadable2->band.pszLRID, pci->pszCarName);
					}
					else
					{
						if (bVerbose)
							printf("No one to load in second position\n");
					}
				}
				else
				{
					if (bVerbose)
						printf("No one to load\n");
				}
			}
		}
		else if (strncmp(pr->pszName, "exit", 4)==0)
		{
			// if the car has people in it, unload it
			if (pci->pgi1!=NULL)
			{
				// proposed exit
				double xx = pr->x;
				double xy = pr->y+COMFORTZONE/2;

				// assure the guest chirps at exit. Set the location at exit to assure Chirp happens
				pci->pgi1->x = xx;
				pci->pgi1->y = xy;
				ChirpGuest(pci->pgi1);

				// if there's someone there, go out further
				while (PositionOccupied(pci->pgi1, xx, xy))
					xx += COMFORTZONE;

				pci->pgi1->x = xx;
				pci->pgi1->y = xy;
				pci->pgi1->state = EXITED;

				// set the time of the last simulation to avoid a big "jump" when the guest gets off
				// the car due to a large time delta
				gettimeofday(&pci->pgi1->tLastSimulation, NULL);

				if (bVerbose)
					printf("Guest %s has gotten off car %s\n", pci->pgi1->band.pszLRID, pci->pszCarName);

				pci->pgi1 = NULL;
			}
			if (pci->pgi2!=NULL)
			{
				// proposed exit
				double xx = pr->x;
				double xy = pr->y-COMFORTZONE/2;

				// assure the guest chirps at exit. Set the location at exit to assure Chirp happens
				pci->pgi2->x = xx;
				pci->pgi2->y = xy;
				ChirpGuest(pci->pgi2);

				// if there's someone there, go out further
				while (PositionOccupied(pci->pgi2, xx, xy))
					xx += COMFORTZONE;

				pci->pgi2->x = xx;
				pci->pgi2->y = xy;
				pci->pgi2->state = EXITED;

				// set the time of the last simulation to avoid a big "jump" when the guest gets off
				// the car due to a large time delta
				gettimeofday(&pci->pgi2->tLastSimulation, NULL);

				if (bVerbose)
					printf("Guest %s has gotten off car %s\n", pci->pgi2->band.pszLRID, pci->pszCarName);

				pci->pgi2 = NULL;
			}
		}
	}


	// if we sent anything to an LRR, update the packet sequence number
	if (bSentLRR)
	{
		pci->band.nPacketSequence = (pci->band.nPacketSequence + 1) % 256;

		// bump the frequency every 16
		if ( (pci->band.nPacketSequence | 0xF) == 0 )
			pci->band.nFrequency = (pci->band.nFrequency + 1) % 16;
	}

	return 0;
}

static GUESTINFO *
FindLoadableGuest()
{
	int i;
	for (i=0; i<pGuests->nCount; i++)
	{
		GUESTINFO *pgi = &pGuests->pGuest[i];

		// skip people not at load
		if (pgi->state != LOADING)
			continue;

		// take first one
		return pgi;
	}

	return NULL;
}


static int
WriteToReader(READERINFO *pri, char *psz)
{
	if (bVerbose)
		printf("Sending %s to reader %s\n", psz, pri->pszName);

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
