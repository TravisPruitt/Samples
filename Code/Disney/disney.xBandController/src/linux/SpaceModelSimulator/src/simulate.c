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
static int Animate();
//static int AnimateRide();
static int AnimateGuests(int *pcActiveGuests);
static double dblRand();
static void ShowGuests();
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

    if (bVerbose)
    {
    	ShowGuests();
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
    srand((unsigned)time(NULL));

    // allocate the guest array
    GUESTINFO *pga = (GUESTINFO*)malloc(pGuests->nCount * sizeof (GUESTINFO));
    if (pga==NULL)
    	return 3;
    pGuests->pGuest = pga;
     gettimeofday(&pGuests->tStart, NULL);

    int iguest;

    // assign initial positions to all guests
    double xStart = pAttraction->priEntryDap->x;
    double yLow = 5.0;
    double yHigh = 15.0;
    int cStandby = 0;
    int ySpacing;

	for(iguest = 0; iguest < pGuests->nCount; iguest++)
    {
        GUESTINFO *pg = &pga[iguest];

        int err = GetBandInfo();
		if (err != 0)
		{
			return err;
		}

		//Parse JSON
		char szID[2048];

		char* pszTemp = strstr(szData,"lrid");

		int test = sscanf(pszTemp,"lrid\":\"%s",szID);

		if(test == 1)
		{
			pg->band.pszLRID = strdup(szID);
			pg->band.pszLRID[16] = '\0';
			pszTemp = strstr(szData,"tapId");

			test = sscanf(pszTemp,"tapId\":\"%s",szID);
			if(test == 1)
			{
				pg->band.pszTapID = strdup(szID);
				pg->band.pszTapID[16] = '\0';
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

	    ySpacing = (int) (((yHigh - yLow))/COMFORTZONE) ;
		if (ySpacing==0)
			ySpacing = 1;

        pg->x = xStart - (cStandby / ySpacing) * COMFORTZONE;
        pg->y = yLow + (cStandby % ySpacing) * COMFORTZONE;
        cStandby++;

        pg->state = INDETERMINATE;

        //Ensure guest has a chance to wander around.
        pg->dExitDelay = dblRand() * pGuests->nMaxExitDelaySeconds;

        //Initialize room bias
        //Leave xVector as 1.0 so guest moves into room
        pg->xVector = 1.0;

        //Randomly set yVector so guests will disperse over time.
		if(dblRand()< 0.5)
		{
			pg->yVector = -1.0;
		}
		else
		{
			pg->yVector = 1.0;
		}


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

        pg->bEscape = random() % 100 < pGuests->nEscapeProbability;

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
Animate()
{
	// as long as there are still guests that haven't exited, keep looping
	int cActiveGuests;
	do
	{
		int err;

		err = AnimateGuests(&cActiveGuests);
		if (err!=0)
			return err;

		if (pController->pszUrl!=NULL && !bNoLocs)
			SendLocations();

		// snooze for half a second
		struct timespec ts = {1, 0};
		nanosleep(&ts, NULL);

	} while (cActiveGuests>0);

	if (bVerbose)
		printf("\n");

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

		// y coordinate limits.
		double yHigh, yLow;
		double xHigh, xLow;
		xLow = 0.0;
		xHigh = pAttraction->xLen;
		yLow = 0.0;
		yHigh = pAttraction->yLen;

		// calculate elapsed time between simulation steps
		double dSeconds = tsNow.tv_sec 	- pgi->tLastSimulation.tv_sec + ((double) tsNow.tv_usec - pgi->tLastSimulation.tv_usec) / 1000000.0;

		// calculate a random velocity for the guest based on the attraction
		double vxg, vyg;

		// After skew it back by a fifth (bias is to move guest forward)
		vxg = (pAttraction->vx * dblRand() - (pAttraction->vx/5.0)) * dSeconds;

		// Start with the attraction's y value but skew it back by a half (let guest move up or down in equal ratios)
		vyg = (pAttraction->vy * dblRand() - (pAttraction->vy/2.0)) * dSeconds;

		//Before guest is in room constrain y value to be width of door.
		if(pgi->state == INDETERMINATE)
		{
			yLow = 5;
			yHigh = 15;

		}
		else if(pgi->state == ENTERED)
		{
			// calculate elapsed time between entered and now
			double dSecondsSinceEntered = tsNow.tv_sec - pgi->tEntered.tv_sec + ((double) tsNow.tv_usec - pgi->tEntered.tv_usec) / 1000000.0;

			//Make sure we can hit the tap reader when in the entrance.
			xLow = pAttraction->priEntryDap->x - 1.0;

			//There are walls here so block entry
			//TODO: Figure out now to not hard code?
			if(pgi->y <=5 || pgi->y> 15)
			{
				xLow = pAttraction->priEntryDap->x;
			}

			yHigh = pAttraction->yLen;

			if (dSecondsSinceEntered > pgi->dExitDelay)
			{
				if(pgi->xVector != -1.0)
				{
					// Skew x towards the exit once delay has been exceeded
					pgi->xVector = -1.0;
				}


			}
			else
			{
				if(pgi->x > pAttraction->priEntryDap->x + (pAttraction->xLen/4.0))
				{
					// move guest in equal directions on x axis
					//vxg = (pAttraction->vx * dblRand() - (pAttraction->vx/2.0)) * dSeconds;

					if(pgi->xVector == 0.0)
					{
						if(dblRand()< 0.50)
						{
							pgi->xVector = -1.0;
						}
						else
						{
							pgi->xVector = 1.0;
						}
					}
					else
					{
						pgi->xVector = 1.0;
					}
				}
			}
		}
		else if(pgi->state == EXITED || pgi->state == ESCAPED)
		{
			//Let user move far away after exiting
			xLow = -100;
			yLow = 5;
			yHigh = 15;
			xHigh = 15;

			//Make sure guest keeps moving out.
			pgi->xVector = -1.0;
		}

		vxg *= pgi->xVector;
		vyg *= pgi->yVector;

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

		if ( (pgi->x + vxg)>xHigh || (pgi->x + vxg)<xLow)
		{
			// bounce the velocity
			vxg = -vxg;

			//Change the direction.
			pgi->xVector *= -1.0;

			// don't let the opposite happen either!
			if ( (pgi->x + vxg)>xHigh || (pgi->x + vxg)<xLow )
			{
				// zero the velocity
				vxg = 0.0;
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

		// is it time for the guest's band to chirp?
		if ( (tsNow.tv_sec>pgi->band.tNextXmit.tv_sec) ||
			 (tsNow.tv_sec==pgi->band.tNextXmit.tv_sec && tsNow.tv_usec>pgi->band.tNextXmit.tv_usec))

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

		// if a guest has not exited or escaped, we have to keep running
		if ( pgi->state != EXITED && pgi->state != ESCAPED)
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
        printf("  Has xPass: %s\n\n", "No");
	}

}

