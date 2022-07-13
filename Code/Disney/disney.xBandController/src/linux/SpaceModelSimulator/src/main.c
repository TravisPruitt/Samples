/*
 * main.c
 *
 *  Created on: Jun 19, 2011
 *      Author: mvellon
 */

#include <stdio.h>
#include <string.h>
#include "init.h"
#include "globals.h"
#include "simulate.h"

// local data
static char *pszConfigFile = NULL;

/* Local functions */
static void Usage();
static int ProcessCommandLine(int argc, char **argv);

int
main(int argc, char** argv)
{
	int err=0;

	if (argc<2)
	{
		Usage();
		return 1;
	}

	err = ProcessCommandLine(argc, argv);
	if (err!=0)
		return err;

	// if no configuration file, this is an error
	if (NULL == pszConfigFile)
	{
		printf("No configuration file specified\n");
		return 1;
	}

	// else, presume that arg is the configuration file. Process it
	err = ProcessConfigurationFile(pszConfigFile);
	if (err!=0)
		return err;

	//TODO: put names in command line????
	err = BuildMasterGuestList();
		if (err!=0)
			return err;

	// display some stats
	if (bVerbose)
	{
		printf("The simulation will track %d guests\n", pGuests->nCount);
		printf("The attraction has %d readers\n", pAttraction->cReaders);
	}

	printf("Simulation started.\n");

	// Now, go simulate!
	err = Simulate();

	// TODO: free up memory in globals
	return err;
}

static int
ProcessCommandLine(int argc, char** argv)
{
	int i;
	for (i=1; i<argc; i++)
	{
		char *psz = argv[i];

		if (psz[0]=='-')
		{
			if (strcmp(psz, "--help")==0 ||
				strcmp(psz, "-?")==0)
			{
				Usage();
			}
			else if (strcmp(psz, "--nolocs")==0)
			{
				bNoLocs = 1;
			}
			else if (strcmp(psz, "--verbose")==0)
			{
				bVerbose = 1;
			}
		}
		else
		{
			if (NULL==pszConfigFile)
				pszConfigFile = psz;
			else
			{
				printf("Invalid arguments -- multiple configuration files\n");
				return 1;
			}
		}
	}
	return 0;
}

static void
Usage()
{
	printf("Guest - NGE guest simulator\n");
	printf("Usage:   guest [OPTIONS] FILENAME\n");
	printf("Where FILENAME is the simulation configuration file (documented separately)\n");
	printf("OPTIONS:\n");
	printf("  --nolocs    Do not report guest locations to the Controller/Server\n");
	printf("  --verbose   Display debug output\n");
	printf("  --help,-?   Display this usage information\n");
}

