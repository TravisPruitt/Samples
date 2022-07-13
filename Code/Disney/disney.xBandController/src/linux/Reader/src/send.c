/*
 * updatestream.c
 *
 *  Created on: Jul 5, 2011
 *      Author: mvellon
 */

#define _XOPEN_SOURCE
#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/utsname.h>
#include <time.h>
#include <pthread.h>
#include <signal.h>
#include <curl/curl.h>
#include <unistd.h>
#include "version.h"

struct WriteThis
{
    const char *readptr;
    long sizeLeft;
};

static size_t
read_callback(void *ptr, size_t size, size_t nmemb, void *userdata)
{
    struct WriteThis *wt = (struct WriteThis*)userdata;

    if (size * nmemb < 1)
        return 0;

    if (wt->sizeLeft)
    {
        size_t readBytes = wt->sizeLeft;
        if (size * nmemb < wt->sizeLeft)
            readBytes = size * nmemb;

        memcpy(ptr, wt->readptr, readBytes);
        wt->readptr += readBytes;
        wt->sizeLeft -= readBytes;
        return readBytes;
    }
    return 0;
}

int OpenSend(CURL **ppCurl)
{
    CURL *curl = NULL;

    // get a curl handle
    curl = curl_easy_init();
    if (!curl)
        return 1;

    *ppCurl = curl;

    return 0;
}

void CloseSend(CURL *curl)
{
    curl_easy_cleanup(curl);
}

int
Send(
    CURL *curl,
    const char *pszURL,
    const char *pszContentType,
    const char *pszData
)
{
    CURLcode res;
    char szError[CURL_ERROR_SIZE] = "";
    int nErr = 0;
    struct WriteThis wt;

    // we want to use our own read function
    curl_easy_setopt(curl, CURLOPT_READFUNCTION, read_callback);

    // now specify which file to upload
    if (pszData)
    {
        wt.readptr = pszData;
        wt.sizeLeft = strlen(pszData);
        curl_easy_setopt(curl, CURLOPT_READDATA, &wt);

        // enable uploading
        curl_easy_setopt(curl, CURLOPT_UPLOAD, 1L);

        // HTTP PUT
        curl_easy_setopt(curl, CURLOPT_PUT, 1L);
    }
    else
    {
        curl_easy_setopt(curl, CURLOPT_HTTPGET, 1L);
    }

    // specify target URL, and note that this URL should include a file
    // name, not only a directory
    curl_easy_setopt(curl, CURLOPT_URL, pszURL);

    // set the error buffer
    curl_easy_setopt(curl, CURLOPT_ERRORBUFFER, szError);

    // if we get a return code >=400, fail don't just swallow it
    curl_easy_setopt(curl, CURLOPT_FAILONERROR, 1);

    struct curl_slist *headers = NULL;
    {
        char buf[128];
        sprintf(buf, "Content-Type: %s", pszContentType);
        headers = curl_slist_append(headers, buf);
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
    }

    // the version of CURL on CENTOS 5.6 seems to have a problem with CURLOPT_INFILESIZE_LARGE.
    // use the regular version since we will not be sending >2GB anyway
    curl_easy_setopt(curl, CURLOPT_INFILESIZE, wt.sizeLeft);

    // Now run off and do what you've been told! */
    res = curl_easy_perform(curl);
    if (res!=0)
    {
        nErr = res;
    }

    curl_slist_free_all(headers);
    headers = NULL;

    // always cleanup */
    curl_easy_reset(curl);

    return nErr;
}

