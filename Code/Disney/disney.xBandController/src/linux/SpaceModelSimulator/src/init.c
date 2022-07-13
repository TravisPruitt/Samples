/*
 * init.c
 *
 *  Created on: Jun 19, 2011
 *      Author: mvellon
 */

#include <stdio.h>
#include <string.h>
#include <libxml/parser.h>
#include <libxml/tree.h>
#include <math.h>
#include "init.h"
#include "globals.h"

// Data
ATTRACTIONINFO *pAttraction = NULL;
READERINFO *pReaders = NULL;
GUESTSINFO *pGuests = NULL;
CONTROLLERINFO *pController = NULL;
RIDEINFO *pRide = NULL;
GUESTXBANDINFO *pHead = NULL;
GUESTXBANDINFO *pCurrent = NULL;

int bNoLocs = 0;
int bVerbose = 0;

// local functions
static int ProcessConfigurationElements(xmlNode *a_node);
static int ProcessAttractionElement(xmlNode *pnode);
static int ProcessRideElement(xmlNode *pnode);
static int ProcessReadersElement(xmlNode *pnode);
static int ProcessGuestsElement(xmlNode *pnode);
static int ProcessControllerElement(xmlNode *pnode);
static char *GetAttribute(xmlNode *pnode, char *pszName);
static int GetIntAttribute(xmlNode *pnode, char *pszName, int bErrorIfMissing);
static double GetDoubleAttribute(xmlNode *pnode, char *pszName, int bErrorIfMissing);

int
ProcessConfigurationFile(char *pszFilename)
{
    xmlDoc *doc = NULL;
    xmlNode *root = NULL;

    /*
     * this initialize the library and check potential ABI mismatches
     * between the version it was compiled for and the actual shared
     * library used.
     */
    LIBXML_TEST_VERSION

    /*parse the file and get the DOM */
    doc = xmlReadFile(pszFilename, NULL, 0);

    if (doc == NULL)
    {
        printf("error: could not parse file %s\n", pszFilename);
        return 2;
    }

    /* Get the root element node */
    root = xmlDocGetRootElement(doc);

    // verify it's the "configuration" element
    if (strcmp((char *)root->name, "configuration")!=0)
    {
    	printf("Expected configuration element, got %s\n", root->name);
    	return 2;
    }

    ProcessConfigurationElements(root->children);

    /*free the document */
    xmlFreeDoc(doc);

    /*
     *Free the global variables that may
     *have been allocated by the parser.
     */
    xmlCleanupParser();

    return 0;
}

int BuildMasterGuestList()
{
	//NOTE: When generating the .csv file from MySQL with the list mapping GuestIDs to BandIDs
	//remove the header line.
	int err = 0;

	//Open simulated guests file
	FILE* fpSimulatedGuests = 0 ;

	fpSimulatedGuests = fopen ( "SimulatedGuests.csv","r" ) ;

	//Check and make sure file could be opened.
	if(fpSimulatedGuests == NULL)
	{
		fputs ("Error opening simulated guests file.",stderr);
		return 1;

	}

	//Point to last created item.
	GUESTXBANDINFO* pPrevious;

	int index;
	for(index =0 ; index< 1000; index++)
	{
		//Create object to store guest/band relationship
		pCurrent = (GUESTXBANDINFO*)malloc(sizeof (GUESTXBANDINFO));

		if(pCurrent != NULL)
		{

			//each line is two comma separated integer values
			char szLine [ 128 ];

			int iGuestID = 0;
			int iBandID = 0;

			//Read a line
			if(fgets ( szLine, sizeof(szLine), fpSimulatedGuests ) != NULL)
			{
				if(sscanf(szLine, "%d,%d", &iGuestID, &iBandID) == 2)
				{
					pCurrent->nGuestID = iGuestID;
					pCurrent->nBandID = iBandID;
					pCurrent->nLRID = 0;
					pCurrent->nTapID = 0;
					pCurrent->pNext = NULL;
				}
			}

			//Set the head if this is the first item.
			if(index == 0)
			{
				//Set the head object.
				pHead = pCurrent;
			}
			else
			{
				//Link the current object to the last.
				pPrevious->pNext = pCurrent;
			}

			//Set previous so the there is something to point to
			//for the next object
			pPrevious = pCurrent;
		}
		else
		{
			return 1;
		}

		//Set the current object for when the simulator starts.
		pCurrent = pHead;
	}


	//Close the file.
	fclose ( fpSimulatedGuests );

	return err;
}

/**
 * ProcessConfigurationElements:
 * @a_node: the initial xml node to consider.
 *
 */
static int
ProcessConfigurationElements(xmlNode *pNodeHead)
{
    xmlNode *pNode = NULL;

    for (pNode = pNodeHead; pNode!=NULL; pNode = pNode->next)
    {
        if (pNode->type == XML_ELEMENT_NODE)
        {
        	char *pszName = (char *) pNode->name;
        	int err;

            if (strcmp(pszName, "attraction")==0)
            	err = ProcessAttractionElement(pNode);
            else if(strcmp(pszName, "controller")==0)
            	err = ProcessControllerElement(pNode);
            else if (strcmp(pszName, "readers")==0)
            	err = ProcessReadersElement(pNode);
            else if (strcmp(pszName, "guests")==0)
            	err = ProcessGuestsElement(pNode);
            else
            {
            	err = 2;
            	printf("Unexpected element: %s\n", pszName);
            }

            if (err!=0)
            	return err;
        }
    }

    return 0;
}

static int
ProcessAttractionElement(xmlNode *pnode)
{
	int err = 0;

    // preallocate the attraction node
    pAttraction = malloc(sizeof(ATTRACTIONINFO));
	if (pAttraction==NULL)
		return 3;

    pAttraction->xLen = pAttraction->yLen = 0;
    pAttraction->vx = pAttraction->vy = 0.0;
    pAttraction->priEntryDap  = NULL;

	// process the children
    xmlNode *pNodeC = NULL;

    for (pNodeC = pnode->children; pNodeC!=NULL; pNodeC = pNodeC->next)
    {
        if (pNodeC->type == XML_ELEMENT_NODE)
        {
        	if (strcmp((char *)pNodeC->name, "dimensions")==0)
        	{
        		int xLen = GetIntAttribute(pNodeC, "x", 1);
        		int yLen  = GetIntAttribute(pNodeC, "y", 1);
        		if (xLen==-1 || yLen==-1)
        			return 2;

        		pAttraction->xLen = xLen;
        		pAttraction->yLen = yLen;
        	}
        	else if (strcmp((char *)pNodeC->name, "guestvelocity")==0)
        	{
        		double vx = GetDoubleAttribute(pNodeC, "x", 1);
        		double vy = GetDoubleAttribute(pNodeC, "y", 1);
        		if (vx<0.0 || vy<0.0)
        			return 2;

        		pAttraction->vx = vx;
        		pAttraction->vy = vy;
        	}
        	else if (strcmp((char *)pNodeC->name, "ride")==0)
        	{
        		double x = GetDoubleAttribute(pNodeC, "x", 1);
        		double y = GetDoubleAttribute(pNodeC, "y", 1);
        		double radius = GetDoubleAttribute(pNodeC, "radius", 1);
        		double speed = GetDoubleAttribute(pNodeC, "speed", 1);
        		int xmitPeriod = GetIntAttribute(pNodeC, "xmitperiod", 1);

        		if ( x<0.0 ||
        			 y<0.0 ||
        			 radius < 0.0 ||
        			 speed < 0.0 ||
        			 xmitPeriod < 0)
        		{
        			printf("ride attribute has invalid or missing attributes\n");
        			return 2;
        		}
        		pRide = (RIDEINFO *) malloc(sizeof(RIDEINFO));
        		if (pRide==NULL)
        			return 2;

        		pRide->x = x;
        		pRide->y = y;
        		pRide->radius = radius;
        		pRide->speed = speed;
        		pRide->angle = 0.0;
        		pRide->cCars = 0;
        		pRide->cmsecXmitPeriod = xmitPeriod;

                // set the simulation time
                gettimeofday(&pRide->tLastSimulation, NULL);

        		err = ProcessRideElement(pNodeC);
        		if (err != 0)
        			return err;
        	}
        	else
        	{
        		printf("Unexpected element %s\n", (char *)pNodeC->name);
        		return 2;
        	}
        }
    }

	return 0;
}

static int
ProcessRideElement(xmlNode *pnode)
{
	// first, count the number of children
	pRide->cCars = 0;

	xmlNode *pc;
	for (pc=pnode->children; pc!=NULL; pc=pc->next)
	{
        if (pc->type == XML_ELEMENT_NODE && strcmp((char *)pc->name, "car")==0)
        	pRide->cCars++;
	}

	if (pRide->cCars==0)
	{
		printf("No cars specified\n");
		return 2;
	}

	pRide->pCarInfo = (CARINFO *) malloc(sizeof(CARINFO) * pRide->cCars);

	int iChild = 0;
	for (pc=pnode->children; pc!=NULL; pc=pc->next)
	{
        if (pc->type == XML_ELEMENT_NODE && strcmp((char *)pc->name, "car")==0)
        {
        	// get car attributes
        	char *pszCarName = GetAttribute(pc, "name");
        	char *pszXlrId = GetAttribute(pc, "xlrid");

        	if (pszCarName==NULL || pszXlrId==NULL)
        	{
        		printf("car element has missing attributes");
        		return 2;
        	}

        	CARINFO *pci = &pRide->pCarInfo[iChild];
        	pci->pszCarName = pszCarName;
        	pci->band.pszLRID = pszXlrId;
        	pci->band.pszTapID = NULL;
        	pci->band.nPacketSequence = 0;
        	pci->band.nFrequency = 0;
        	pci->band.nChannel = 0;
        	pci->band.cmsecXmitPeriod = pRide->cmsecXmitPeriod;
        	pci->x = 0;
        	pci->y = 0;
        	pci->pgi1 = NULL;
        	pci->pgi2 = NULL;
        	pci->band.tNextXmit.tv_sec = pci->band.tNextXmit.tv_usec = 0;
        	iChild++;
        }
	}

    return 0;
}

static int
ProcessReadersElement(xmlNode *pnode)
{
	// first, count the number of children
	pAttraction->cReaders = 0;

	xmlNode *pc;
	for (pc=pnode->children; pc!=NULL; pc = pc->next)
	{
        if (pc->type == XML_ELEMENT_NODE && strcmp((char *)pc->name, "reader")==0)
        	pAttraction->cReaders++;
	}

	if (pAttraction->cReaders==0)
	{
		printf("No readers specified\n");
		return 2;
	}

	pReaders = (READERINFO *) malloc(pAttraction->cReaders * sizeof(READERINFO));
	if (pReaders == NULL)
		return 3;

	int iChild = 0;
	for (pc=pnode->children; pc!=NULL; pc=pc->next)
	{
        if (pc->type == XML_ELEMENT_NODE)
        {
        	if (strcmp((char *)pc->name, "reader")!=0)
        	{
        		printf("Unexpected element %s\n", (char *)pc->name);
        		return 2;
        	}

        	// get reader attributes
        	char *pszName = GetAttribute(pc, "name");
        	int nPortWeb = GetIntAttribute(pc, "webport", 1);
        	int x = GetIntAttribute(pc, "x", 1);
        	int y = GetIntAttribute(pc, "y", 1);
        	char *pszType = GetAttribute(pc, "type");
        	char *pszFile = GetAttribute(pc, "file");
        	int bIsTap = strcmp(pszType, (char *)"tap")==0;
        	double range = -1.0;
        	if (!bIsTap)
        		range = GetDoubleAttribute(pc, "range", 1);
        	if (	pszName==NULL ||
        			pszType==NULL ||
        			pszFile==NULL ||
        			x==-1 ||
        			y==-1 ||
        			(!bIsTap && range<0.0) )
        	{
        		printf("reader element has missing attribute(s)\n");
        		return 2;
        	}

        	pReaders[iChild].pszName = pszName;
        	pReaders[iChild].nPortWeb = nPortWeb;
        	pReaders[iChild].pszQName = pszFile;
        	pReaders[iChild].bIsTap = bIsTap;
        	pReaders[iChild].pfQ = NULL;
        	pReaders[iChild].x = x;
        	pReaders[iChild].y = y;
        	pReaders[iChild].range = range;

            // is this one of the specially named readers?
            if (strncmp(pszName, "entrance-exit", 13)==0)
            {
            	pAttraction->priEntryDap = &pReaders[iChild];
            }

            iChild++;

        }
	}
	return 0;
}

static int
ProcessGuestsElement(xmlNode *pnode)
{
	pGuests = (GUESTSINFO *) malloc(sizeof(GUESTSINFO));
	if (pGuests==NULL)
		return 3;

	// fetch the count
	pGuests->nCount = GetIntAttribute(pnode, "count", 1);
	if (pGuests->nCount<0)
	{
		return 2;

	}

	// fetch the escape probability
	pGuests->nEscapeProbability = GetIntAttribute(pnode, "escapeprobability", 0);

	// fetch the maximum exit delay
	pGuests->nMaxExitDelaySeconds = GetIntAttribute(pnode, "maxexitdelaysec", 0);

	char *psz=NULL;

	// fetch the URL
	psz = GetAttribute(pnode, "urlroot");
	if (psz == NULL)
	{
		printf("Missing xView URL Root\n");
		return 2;
	}

	pGuests->pszUrlRoot = psz;

	return 0;
}

static int
ProcessControllerElement(xmlNode *pnode)
{
	char *psz=NULL;

	// fetch the URL
	psz = GetAttribute(pnode, "url");
	if (psz == NULL)
	{
		printf("Missing controller URL\n");
		return 2;
	}

	pController = (CONTROLLERINFO *) malloc(sizeof(CONTROLLERINFO));
	if (pController==NULL)
		return 3;
	pController->pszUrl = psz;

	return 0;
}

static char *
GetAttribute(xmlNode *pnode, char *pszName)
{
	char *pszValue = NULL;
	char *psz;
	if (xmlHasProp(pnode, (xmlChar *) pszName) != NULL)
	{
		psz = (char *) xmlGetProp(pnode, (xmlChar *) pszName);
		pszValue = strdup(psz);
		xmlFree(psz);
	}
	return pszValue;
}

static int
GetIntAttribute(xmlNode *pnode, char *pszName, int bErrorIfBlank)
{
	char *pszValue = GetAttribute(pnode, pszName);
	if (pszValue==NULL && bErrorIfBlank)
	{
		printf("Node %s is missing attribute %s\n", pnode->name, pszName);
		return -1;
	}
	else
		return atoi(pszValue);
}

static double
GetDoubleAttribute(xmlNode *pnode, char *pszName, int bErrorIfBlank)
{
	char *pszValue = GetAttribute(pnode, pszName);
	if (pszValue==NULL && bErrorIfBlank)
	{
		printf("Node %s is missing attribute %s\n", pnode->name, pszName);
		return -1.0;
	}
	else
		return atof(pszValue);
}

