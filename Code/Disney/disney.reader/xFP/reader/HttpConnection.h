/**
 *  @file   HttpConection - HTTP Connection
 *  @date   January, 2012
 *  @author Corey Wharton
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#ifndef __HTTP_CONNECTION_H
#define __HTTP_CONNECTION_H


#include <curl/curl.h>


namespace Reader
{


class HttpConnection
{
public:
    HttpConnection()
    {
        _handle = curl_easy_init();
    }
    
    ~HttpConnection()
    {
        curl_easy_cleanup(_handle);
    }
    
    CURLcode easy_perform()
    {
        return curl_easy_perform(_handle);
    }
    
    CURLcode easy_setopt(CURLoption option, const void *value)
    {
        return curl_easy_setopt(_handle, option, value);
    }
    
    CURLcode easy_setopt(CURLoption option, int value)
    {
        return curl_easy_setopt(_handle, option, value);
    }
    
    CURLcode easy_getinfo(CURLINFO info, const void *value)
    {
        return curl_easy_getinfo(_handle, info, value);
    }
    
    void easy_reset()
    {
        curl_easy_reset(_handle);
    }
    
private:
    CURL* _handle;
};


}


#endif // __HTTP_CONNECTION_H
