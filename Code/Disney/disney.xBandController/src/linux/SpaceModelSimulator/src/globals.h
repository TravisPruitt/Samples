/*
 * globals.h
 *
 *  Created on: Jun 19, 2011
 *      Author: mvellon
 */

#ifndef GLOBALS_H_
#define GLOBALS_H_

#include <sys/time.h>


/* types */
typedef struct ReaderInfo
{
	char *pszName;		// reader name
	char *pszQName;		// reader queue file name
	int bIsTap;			// TRUE if tap reader (instead of lrr)
	FILE *pfQ;			// reader queue FD
	double x, y;		// reader location
	double range;		// range of reader
	int nPortWeb;
} READERINFO;

typedef struct BandInfo
{
	// LRR
	char *pszLRID;				// LRR band id
	int nChannel;				// 0 or 1
	int nPacketSequence;		// packet sequence
	int nFrequency;				// frequency selector
	int cmsecXmitPeriod;		// msec between transmissions
	struct timeval tNextXmit;

	// Tap
	char *pszTapID;				// tap band id

} BANDINFO;

typedef enum GuestState
{
	INDETERMINATE=0,
	ENTERED,
	EXITED,
	ESCAPED
} GUESTSTATE;

typedef struct GuestInfo
{
	BANDINFO band;
	double x, y;						// guest location

	GUESTSTATE state;
	double dExitDelay;
	double xVector;
	double yVector;
	int bChirp;
	int bEscape;
	struct timeval tEntered;			// time entered
	struct timeval tLastSimulation;		// used in simulation calculations
} GUESTINFO;

typedef struct GuestsInfo
{
	int nCount;
	struct timeval tStart;
	char* pszUrlRoot;
	int nEscapeProbability;
	int nMaxExitDelaySeconds;
	GUESTINFO *pGuest;
} GUESTSINFO;

typedef struct AttractionInfo
{
	int xLen, yLen;		// length of attraction
	double vx, vy;		// mean guest velocity
	int cReaders;		// number of readers
	READERINFO *priEntryDap; // Entry Dap
} ATTRACTIONINFO;

typedef struct CarInfo
{
	BANDINFO band;
	char *pszCarName;
	double x, y;
	GUESTINFO *pgi1;		// first guest in the car
	GUESTINFO *pgi2;		// second guest in the car
} CARINFO;

typedef struct RideInfo
{
	double x, y;		// center of the ride circle
	double radius;		// radius of the circle
	double speed;		// speed of the circle (in degrees per second)
	int cCars;
	double angle;		// current angle
	CARINFO *pCarInfo;
	struct timeval tLastSimulation;
	int cmsecXmitPeriod;		// msec between transmissions
} RIDEINFO;

typedef struct ControllerInfo
{
	char *pszUrl;
} CONTROLLERINFO;

struct GuestxBandInfo;

typedef struct GuestxBandInfo
{
	int nGuestID;
	int nBandID;
	int nLRID;
	int nTapID;
	struct GuestxBandInfo* pNext;
} GUESTXBANDINFO;

/* Data */
extern ATTRACTIONINFO *pAttraction;
extern READERINFO *pReaders;
extern CONTROLLERINFO *pController;
extern GUESTSINFO *pGuests;
extern RIDEINFO *pRide;
extern GUESTXBANDINFO *pHead;
extern GUESTXBANDINFO *pCurrent;

extern int bNoLocs;
extern int bVerbose;

#endif /* GLOBALS_H_ */
