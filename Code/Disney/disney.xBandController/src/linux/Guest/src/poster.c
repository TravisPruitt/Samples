#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <curl/curl.h>
#include "poster.h"
#include "globals.h"

static size_t read_callback(void *ptr, size_t size, size_t nmemb, void *stream);
static size_t iCursor;
static size_t cBytesLeft;

void
SendLocations()
{
	CURL *curl;
	CURLcode res;

	// generate the JSON
	char *psz = (char *) malloc(pGuests->nCount * 100);

	strcpy( psz, "[");
	int i;
	for (i=0; i<pGuests->nCount; i++)
	{
		GUESTINFO *pgi = &pGuests->pGuest[i];
		char szOne[256];

		sprintf( szOne,
				"{ \"XID\": \"%s\", \"x\": %g, \"y\": %g, \"hasxpass\": \"%s\" }",
				 pgi->band.pszLRID, pgi->x, pgi->y, pgi->bHaveXPass ? "true" : "false");

		strcat(psz, szOne);
		if (i<pGuests->nCount-1)
			strcat(psz, ",\n");
		else
			strcat(psz, "\n");
	}
	strcat(psz, "]");

	struct timeval ts;
	gettimeofday(&ts, NULL);

	// format the timestamp as a UTC value
	struct tm tmTs;
	gmtime_r(&ts.tv_sec, &tmTs);    // convert to UTC
	char szTime[64];
	strftime(szTime, 64, "%Y-%m-%dT%H:%M:%S.", &tmTs);
	// tack on the milliseconds
	int ms = ts.tv_usec / 1000;
	char szMS[6];
	sprintf(szMS, "%.3d", ms);
	strcat( szTime, szMS);


	if (bVerbose)
		printf("Sending locations at time: %s\n", szTime);

	// get a curl handle
	curl = curl_easy_init();
	if(curl)
	{
		// we want to use our own read function
		curl_easy_setopt(curl, CURLOPT_READFUNCTION, read_callback);

		// enable uploading
		curl_easy_setopt(curl, CURLOPT_UPLOAD, 1L);

		// HTTP PUT please
		curl_easy_setopt(curl, CURLOPT_PUT, 1L);

		// specify target URL, and note that this URL should include a file
	    // name, not only a directory
		char szURL[1024];
		strcpy(szURL, pController->pszUrl);
		strcat(szURL, "/guestpositions");
		curl_easy_setopt(curl, CURLOPT_URL, szURL);

		// now specify which file to upload
		iCursor = 0;
		curl_easy_setopt(curl, CURLOPT_READDATA, psz);

#if 0

		// provide the size of the upload, we specicially typecast the value
	    // to curl_off_t since we must be sure to use the correct data size */
		cBytesLeft = strlen(psz) + 1;
		curl_easy_setopt(curl, CURLOPT_INFILESIZE_LARGE,
				(curl_off_t)(strlen(psz)+1) );
#else

		// the version of CURL on CENTOS 5.6 seems to have a problem with CURLOPT_INFILESIZE_LARGE.
		// use the regular version since we will not be sending >2GB anyway
		cBytesLeft = strlen(psz) + 1;
		curl_easy_setopt(curl, CURLOPT_INFILESIZE, (long) cBytesLeft);

#endif

		// Now run off and do what you've been told! */
		res = curl_easy_perform(curl);

		// always cleanup */
		curl_easy_cleanup(curl);
	}

	free(psz);
}

static size_t
read_callback(void *ptr, size_t size, size_t nmemb, void *stream)
{
	// calculate bytes to copy
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
	return cBytesToCopy;
}
