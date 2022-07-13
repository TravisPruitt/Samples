/*
 * send.h
 */

#ifndef SEND_H
#define SEND_H
#include <curl/curl.h>

int OpenSend(CURL **ppCurl);
void CloseSend(CURL *curl);

int
Send(
    CURL *curl,
    const char *pszURL,
    const char *pszContentType,
    const char *pszData
);

#endif //SEND_H
