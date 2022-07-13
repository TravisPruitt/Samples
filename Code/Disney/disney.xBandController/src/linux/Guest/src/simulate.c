/*
 * simulate.c
 *
 *  Created on: Jun 19, 2011
 *      Author: mvellon
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <math.h>
#include <curl/curl.h>
#include "init.h"
#include "globals.h"
#include "simulate.h"
#include "chirp.h"
#include "poster.h"

#define PI 3.14151926
#define TWOPI (2.0*PI)
#define COMFORTZONE 2.2

// local functions
static int AssignGuestLocations();
static int AssignCarLocations();
static int SetCarLocations(double radStart);
static int Animate();
static int AnimateRide();
static int AnimateGuests(int *pcActiveGuests);
static double dblRand();
static void ShowGuests();
static void ShowCars();
static int GetBandInfo();

static size_t write_callback(void *ptr, size_t size, size_t nmemb, void *stream);
static char szData[2048];

int
Simulate()
{
    int err = 0;

    err = AssignGuestLocations();
    if (err!=0)
    	return err;

    err = AssignCarLocations();
    if (err!=0)
    	return err;

    if (bVerbose)
    {
    	ShowGuests();
    	ShowCars();
    }

    err = Animate();
    if (err!=0)
    	return err;

    printf("Simulation complete!");
	return 0;
}

static int
AssignGuestLocations()
{
	// initialize the random number generator
    srandom(1);

    // allocate the guest array
    GUESTINFO *pga = (GUESTINFO*)malloc(pGuests->nCount * sizeof (GUESTINFO));
    if (pga==NULL)
    	return 3;
    pGuests->pGuest = pga;

    // assign initial positions to all guests
    double xStart = 0 - (pAttraction->priEntry->range + 1.0);
    double xStartXPass = pAttraction->priXPassDap->x;
    double yLow = 0.0;
    double yHigh = pAttraction->yLen / 2.0;
    double yLowXPass = yHigh;
    double yHighXPass = (double) pAttraction->yLen;
    int iguest;
    int cStandby = 0;
    int cXPass = 0;
    for(iguest = 0; iguest < pGuests->nCount; iguest++)
    {
        GUESTINFO *pg = &pga[iguest];

        // assign a bandid
        /*int n1 = rand();
        int n2 = rand();
        long long lID = ((long long)n1 << 32) + n2;
        char szID[17];
        sprintf( szID, "%016llx", lID);*/

        int err = GetBandInfo();
		if (err != 0)
		{
			return err;
		}

		// Parse JSON
		char szID[2048];

		char* pszTemp = strstr(szData,"lrid");

		if (pszTemp==NULL)
		{
			printf("!! Invalid band info (%d): %s\n", pCurrent->nBandID, szData);
			exit(1);
		}

		int test = sscanf(pszTemp,"lrid\":\"%s",szID);
		char * pquote = strchr(szID, '"');
		if (pquote!=NULL)
			*pquote = '\0';

		if(test == 1)
		{
			// grab the lrid
			pg->band.pszLRID = strdup(szID);

			// look for a tapid
			pszTemp = strstr(szData,"tapId");

			test = sscanf(pszTemp,"tapId\":\"%s",szID);
			pquote = strchr(szID, '"');
			if (pquote!=NULL)
				*pquote = '\0';
			if(test == 1)
			{
				// grab the tap id
				pg->band.pszTapID = strdup(szID);

				// look for the secureid
				pszTemp = strstr(szData,"secureId");

				test = sscanf(pszTemp,"secureId\":\"%s",szID);
				pquote = strchr(szID, '"');
				if (pquote!=NULL)
					*pquote = '\0';
				if(test == 1)
				{
					// grab the secureId
					pg->band.pszSecureID = strdup(szID);
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

		//pg->band.pszLRID = strdup(pszLRID);
        //pg->band.pszTapID = strdup(pszTapID);
        int nRandom = random() % 100;
        pg->bHaveXPass = nRandom < pGuests->nXPassProbability;
        int ySpacing;
        if(pg->bHaveXPass)
        {
        	ySpacing = (int) (((yHighXPass - yLowXPass))/COMFORTZONE) ;
        	if (ySpacing==0)
        		ySpacing = 1;
            pg->x = xStartXPass - (cXPass / ySpacing) * COMFORTZONE;
            pg->y = yLowXPass + (cXPass % ySpacing) * COMFORTZONE;
            cXPass++;
        }
        else
        {
        	ySpacing = (int) (((yHigh - yLow))/COMFORTZONE) ;
        	if (ySpacing==0)
        		ySpacing = 1;
            pg->x = xStart - (cStandby / ySpacing) * COMFORTZONE;
            pg->y = yLow + (cStandby % ySpacing) * COMFORTZONE;
            cStandby++;
        }
        pg->state = INDETERMINATE;

        // Zero times
        pg->band.tNextXmit.tv_sec = pg->band.tNextXmit.tv_usec = 0;

        // set the channel to a random value 0-1
        pg->band.nChannel = random() & 1;

        // set packet sequence and frequency
        pg->band.nPacketSequence = 0;
        pg->band.nFrequency = random() % 16;

        // set the simulation time
        gettimeofday(&pg->tLastSimulation, NULL);

        // and the transmit period (1Hz)
        pg->band.cmsecXmitPeriod = 1000;

        //Set current linked list item to the next item in the list.
		pCurrent = pCurrent->pNext;
    }

    return 0;
}

static int GetBandInfo()
{
	int err = 0;
 	if(pCurrent != NULL)
	{
		CURL *curl;
		CURLcode res;

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
					"/xbands/id/%d", pCurrent->nBandID);
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
/*	// calculate bytes to copy
	size_t cBytesRequested = size * nmemb;
	size_t cBytesToCopy = cBytesRequested;
	if (cBytesLeft < cBytesToCopy)
		cBytesToCopy = cBytesLeft;
	if (cBytesToCopy > 0)
	{
		strncpy((char *) ptr, (char *) stream + iCursor, cBytesToCopy);
		iCursor += cBytesToCopy;
		cBytesLeft -= cBytesToCopy;
	}
	return cBytesToCopy;*/
	size_t written = size * nmemb;
	strncpy((char*)stream, ptr, size * nmemb);
	return written;
}

static int
AssignCarLocations()
{
	return SetCarLocations(0.0);
}

static int
SetCarLocations(double radStart)
{
	// divide the circle into the necessary number of wedges
	double radWedge = TWOPI / pRide->cCars;

	int i;
	for (i=0; i<pRide->cCars; i++)
	{
		CARINFO *pci = &pRide->pCarInfo[i];

		double rad = radStart + radWedge * i;
		if (rad > TWOPI)
			rad -= TWOPI;

		double x = pRide->x + cos(rad) * pRide->radius;
		double y = pRide->y + sin(rad) * pRide->radius;

		// set car position
		pci->x = x;
		pci->y = y;

		// if there's any guests in the car, move them too
		if (pci->pgi1 != NULL)
		{
			pci->pgi1->x = x-1;
			pci->pgi1->y = y;
		}
		if (pci->pgi2 != NULL)
		{
			pci->pgi2->x = x+1;
			pci->pgi2->y = y;
		}
	}

	return 0;

}

static int
Animate()
{
	// as long as there are still guests that haven't exited, keep looping
	int cActiveGuests;
	do
	{
		int err;

		err = AnimateRide();
		if (err!=0)
			return err;

		err = AnimateGuests(&cActiveGuests);
		if (err!=0)
			return err;

		if (pController->pszUrl!=NULL && !bNoLocs)
			SendLocations();

	} while (cActiveGuests>0);

	if (bVerbose)
		printf("\n");

	return 0;

}


static int
AnimateRide()
{
	// current time
	struct timeval tsNow;
	gettimeofday(&tsNow, NULL);

	// calculate elapsed time between simulation steps
	double dSeconds = tsNow.tv_sec - pRide->tLastSimulation.tv_sec + ((double) tsNow.tv_usec - pRide->tLastSimulation.tv_usec) / 1000000.0;

	// calculate new angular location
	double radstep = dSeconds * (pRide->speed * TWOPI)/360.0;

	// advance the angle
	pRide->angle += radstep;
	if (pRide->angle > TWOPI)
		pRide->angle -= TWOPI;

	// recalculate all the car positions
	SetCarLocations(pRide->angle);

	// see if it's time for any car bands to chirp
	int ic;
	if (bVerbose)
		printf("\nCar Status:\n-----------\n");
	for (ic=0; ic<pRide->cCars; ic++)
	{
		CARINFO *pci = &pRide->pCarInfo[ic];

		if (bVerbose)
		{
			printf("Car %s. Guest1: %s Guest2: %s\n",
					pci->pszCarName,
					(pci->pgi1==NULL) ? "(empty)" : pci->pgi1->band.pszLRID,
					(pci->pgi2==NULL) ? "(empty)" : pci->pgi2->band.pszLRID);
		}

		// is it time for the car's band to chirp?
		if ( (tsNow.tv_sec>pci->band.tNextXmit.tv_sec) ||
			 (tsNow.tv_sec==pci->band.tNextXmit.tv_sec && tsNow.tv_usec>pci->band.tNextXmit.tv_usec)	)
		{
			// yes!
			int err = ChirpCar(pci);
			if (err!=0)
				return err;

			// advance time between 90% and 110% of period
			int nNinety = (int) (0.90 * pRide->cmsecXmitPeriod);
			int nDelta = (int) ( (0.20 * pRide->cmsecXmitPeriod) * ((rand() % 100) / 99.0));
			int delay = nNinety + nDelta;
			long msecNow = tsNow.tv_usec / 1000;
			pci->band.tNextXmit = tsNow;
			if ( (msecNow+delay) < 1000)
			{
				pci->band.tNextXmit.tv_usec += delay*1000;
			}
			else
			{
				// TODO: deal with delay that causes multiple bumbs to tv_sec
				pci->band.tNextXmit.tv_sec++;
				pci->band.tNextXmit.tv_usec = (msecNow+delay-1000) * 1000;
			}
		}
	}

	if (pController->pszUrl!=NULL && !bNoLocs)
		SendLocations();

	pRide->tLastSimulation = tsNow;
	return 0;
}

static int
AnimateGuests(int *pcActiveGuests)
{
	// iterate through all the guests
	int ig;
	*pcActiveGuests = 0;
	for (ig=0; ig<pGuests->nCount; ig++)
	{
		// peg
		GUESTINFO *pgi = &pGuests->pGuest[ig];

		// current time
		struct timeval tsNow;
		gettimeofday(&tsNow, NULL);

		// only move people who are not loading or riding
		if (pgi->state!=LOADING && pgi->state!=RIDING)
		{

			// calculate elapsed time between simulation steps
			double dSeconds = tsNow.tv_sec - pgi->tLastSimulation.tv_sec + ((double) tsNow.tv_usec - pgi->tLastSimulation.tv_usec) / 1000000.0;

			if (dSeconds > 1.0)
			{
				if (bVerbose)
					printf("!! Simulation time (%g) is greater than 1.0. Clamped to 1.0\n", dSeconds);

				// clamp this to avoid skipping over readers if simulation bogs down
				dSeconds = 1.0;

			}

			// calculate a random velocity for the guest based on the attraction
			double vxg, vyg;

			// start with the attraction's x value, but skew it back by a fifth (bias is to move guest forward)
			vxg = (pAttraction->vx * dblRand() - pAttraction->vx/5.0) * dSeconds;

			// Start with the attraction's y value but skew it back by a half (let guest move up or down in equal ratios)
			vyg = (pAttraction->vy * dblRand() - pAttraction->vy/2.0) * dSeconds;

			// bounce the guest as needed

			double yHigh, yLow;
			if (pgi->bHaveXPass && pgi->state != MERGED)
			{
				// constrain to XPass queue
				yHigh = (double) pAttraction->yLen;
				yLow = pAttraction->yLen / 2.0;
			}
			else if (!pgi->bHaveXPass && pgi->x<pAttraction->priMergeDap->x)
			{
				// constrain to the standby line
				yHigh = pAttraction->yLen / 2.0;
				yLow = 0;
			}
			else
			{
				// constrain to attraction limits
				yLow = 0;
				yHigh = pAttraction->yLen;
			}

			if ( (pgi->y + vyg)>yHigh || (pgi->y + vyg)<yLow )
			{
				// bounce the velocity
				vyg = -vyg;

				// don't let the opposite happen either!
				if ( (pgi->y + vyg)>yHigh || (pgi->y + vyg)<yLow )
				{
					// zero the velocity
					vyg = 0.0;
				}
			}

			// test proposed location
			double xNew = pgi->x + vxg;
			double yNew = pgi->y + vyg;
			if (!PositionOccupied(pgi, xNew, yNew))
			{
				// advance the guest's location
				pgi->x = xNew;
				pgi->y = yNew;
			}

		}

		// is it time for the guest's band to chirp?
		if ( (tsNow.tv_sec>pgi->band.tNextXmit.tv_sec) ||
			 (tsNow.tv_sec==pgi->band.tNextXmit.tv_sec && tsNow.tv_usec>pgi->band.tNextXmit.tv_usec)	)
		{
			// yes!
			int err = ChirpGuest(pgi);
			if (err!=0)
				return err;

			// advance time between 750 and 1250 ms
			int delay = 750 + rand() % 500;
			long msecNow = tsNow.tv_usec / 1000;
			pgi->band.tNextXmit = tsNow;
			if ( (msecNow+delay) < 1000)
			{
				pgi->band.tNextXmit.tv_usec += delay*1000;
			}
			else
			{
				pgi->band.tNextXmit.tv_sec++;
				pgi->band.tNextXmit.tv_usec = (msecNow+delay-1000) * 1000;
			}

		}

		// update the simulation time
		pgi->tLastSimulation = tsNow;

		// special case: deal with guests who somehow got past the exit reader but didn't register
		if ( (pgi->x > pAttraction->priExit->x ) &&
			 pgi->state!=EXITED )
		{
			if (bVerbose)
				printf("!! Guest %d at location (%g, %g) got past the exit\n", ig, pgi->x, pgi->y);

			// send belated EXIT message?
			pgi->state = EXITED;
		}

		// if a guest has not exited, we have to keep running
		if ( pgi->state != EXITED)
			(*pcActiveGuests)++;

	}

	return 0;

}

int
PositionOccupied(GUESTINFO *pgiSkip, double x, double y)
{
	// iterate through all guests (skipping the supplied one) seeing if any are in the proposed position
	int i;
	for (i=0; i<pGuests->nCount; i++)
	{
		GUESTINFO *pgi = &pGuests->pGuest[i];
		if (pgi==pgiSkip)
			continue;

		// caculate distance
		double d = sqrt( (pgi->x-x)*(pgi->x-x) + (pgi->y-y)*(pgi->y-y));

		if (d<COMFORTZONE)
			return 1;
	}

	return 0;
}

static double
dblRand()
{
	double d = (double) random() / RAND_MAX;
	return d;
}

static void
ShowGuests()
{
	int i;
	printf("Guest List\n");
	printf("----------\n");
	for (i=0; i<pGuests->nCount; i++)
	{
		GUESTINFO *pg = &pGuests->pGuest[i];

		// only show non-exited guests
		if (pg->state==EXITED)
			continue;

        printf("%d Guest: %s\n", i, pg->band.pszLRID);
        printf("  Location: (%g, %g)\n", pg->x, pg->y);
        printf("  Has xPass: %s\n\n", pg->bHaveXPass ? "Yes" : "No");
	}

}

static void
ShowCars()
{
	int i;
	printf("Car List\n");
	printf("--------\n");
	for (i=0; i<pRide->cCars; i++)
	{
		CARINFO *pci = &pRide->pCarInfo[i];

        printf("%d Car: %s  Band: %s\n", i, pci->pszCarName, pci->band.pszLRID);
        printf("  Location: (%g, %g)\n", pci->x, pci->y);
	}

}
