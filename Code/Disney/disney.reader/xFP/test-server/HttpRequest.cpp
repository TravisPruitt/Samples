/**
    HttpRquest.cpp - HTTP Request
    Aug 2, 2011
    Greg Strange

    Copyright (c) 2011, synapse.com


    Class for creating and executing HTTP requests.

    The interface provided by this class is designed to match the needs of the Disney
    reader projects.  Thus, some obvious methods for other applications may be left
    out.  The code is currently implemented using libcurl, but the interface should
    mask this fact and it should be fairly easy to rewrite the underlying code to
    use raw sockets or other library.
*/




#include "standard.h"
#include "HttpRequest.h"
#include "log.h"
#include <vector>
#include <memory.h>


using namespace Reader;



/**
    Constructor

    @param    method    HTTP method (e.g. "post", "put" or "get")
    @param    url       URL to hit
*/
HttpRequest::HttpRequest(const char* method, const char* url) : _headers(NULL), _outIndex(0)
{
    _verb = method;
    _url = url;

    // Create the curl handle and set the URL, but save most of the curl settings for
    // the send() function so they are all in one place.
    _handle = curl_easy_init();
    curl_easy_setopt(_handle, CURLOPT_URL, url);

    if (strcasecmp(method, "post") == 0)
        _method = HTTP_POST;
    else if (strcasecmp(method, "put") == 0)
        _method = HTTP_PUT;
    else if (strcasecmp(method, "delete") == 0)
        _method = HTTP_DELETE;
    else
        _method = HTTP_GET;
}


/**
    Destructor
*/
HttpRequest::~HttpRequest()
{
    if (_headers)
        curl_slist_free_all(_headers);

    curl_easy_cleanup(_handle);
}


/**
    Add a payload to the request.

    @param    newPayload    Points to the text payload
    @param    type          Payload type for the "Content-Type" header field
*/
void HttpRequest::setPayload(const char* newPayload, const char* type)
{
    size_t length = strlen(newPayload); 
    _outPayload.resize(length);
    memcpy(&_outPayload.front(), newPayload, length);

    char buf[100];
    snprintf(buf, sizeof(buf)-1, "Content-Type: %s", type);
    _headers = curl_slist_append(_headers, buf);
}




/**
    Add a json payload to the request

    @param  value   Json value to write as the payload
*/
void HttpRequest::setPayload(Json::Value& value)
{
    Json::StyledWriter writer;
    std::string json = writer.write(value);
    setPayload(json.c_str(), "application/json");
}


/**
    Use the contents of a file as the payload
*/
void HttpRequest::setPayloadFromFile(const char* filePath)
{
    FILE* file = fopen(filePath, "r+b");
    if (file)
    {
        fseek(file, 0, SEEK_END);
        int length = ftell(file);
        _outPayload.resize(length);
        rewind(file);
        fread(&_outPayload.front(), 1, length, file);
        fclose(file);
    }
    else
    {
        LOG_WARN("unable to open '%s' for HTTP payload\n", filePath);
    }
}






/**
    Send the request

    @return    true on success, false otherwise
*/
bool HttpRequest::send()
{
    LOG_INFO("HTTP>> %s --> %s\n", _verb.c_str(), _url.c_str());

    curl_easy_setopt(_handle, CURLOPT_NOSIGNAL, 1);
    curl_easy_setopt(_handle, CURLOPT_HTTPHEADER, NULL);
    curl_easy_setopt(_handle, CURLOPT_HTTP_TRANSFER_DECODING, 0);

    curl_easy_setopt(_handle, CURLOPT_WRITEFUNCTION, (void*)curlWrite);
    curl_easy_setopt(_handle, CURLOPT_WRITEDATA, (void*)this);
    curl_easy_setopt(_handle, CURLOPT_READFUNCTION, (void*)curlRead);
    curl_easy_setopt(_handle, CURLOPT_READDATA, (void*)this);
    curl_easy_setopt(_handle, CURLOPT_ERRORBUFFER, _errorDescription);

    if (_method == HTTP_PUT)
    {
        curl_easy_setopt(_handle, CURLOPT_PUT, 1);
        curl_easy_setopt(_handle, CURLOPT_UPLOAD, 1);
        curl_easy_setopt(_handle, CURLOPT_INFILESIZE, _outPayload.size());
    }
    else if (_method == HTTP_POST)
    {
        curl_easy_setopt(_handle, CURLOPT_HTTPPOST, 1);
        curl_easy_setopt(_handle, CURLOPT_POSTFIELDS, NULL);
        curl_easy_setopt(_handle, CURLOPT_POSTFIELDSIZE, _outPayload.size());
    }
    else if (_method == HTTP_DELETE)
    {
        curl_easy_setopt(_handle, CURLOPT_CUSTOMREQUEST, "DELETE");
    }
    else
    {
        curl_easy_setopt(_handle, CURLOPT_HTTPGET, 1);
    }

    curl_easy_setopt(_handle, CURLOPT_HTTPHEADER, _headers);

    // Connection timeout in seconds
    curl_easy_setopt(_handle, CURLOPT_CONNECTTIMEOUT, 2);

    // If transfer speed falls below CURLOPT_LOW_SPEED_LIMIT bytes/second for more than
    // CURLOPT_LOW_SPEED_TIME seconds, the request is terminated.
    curl_easy_setopt(_handle, CURLOPT_LOW_SPEED_LIMIT, 100);
    curl_easy_setopt(_handle, CURLOPT_LOW_SPEED_TIME, 5);

    int result = curl_easy_perform(_handle);

    if (result == 0)
        LOG_DEBUG("  response code = %d\n", getResponseCode());
    else
        LOG_WARN("  HTTP request curl response %d\n", result);
    return result == 0;
}



long HttpRequest::getResponseCode()
{
    long code;
    curl_easy_getinfo(_handle, CURLINFO_RESPONSE_CODE, &code);
    return code;
}



std::string HttpRequest::getResponsePayloadAsString()
{
    if (_inPayload.size() > 0)
        return std::string((char*)(&(_inPayload[0])), _inPayload.size());
    else
        return std::string();
}



/******************************************************************************
                            Private Methods
******************************************************************************/


/**
    Received data callback from curl.

    @param   data        Points to received data
    @param   numThings   Data points to numThings*thingSize bytes
    @param   thingSize
    @param   requestPtr  Points to the HttpRequest object

    @return  Number of bytes handled

    See libcurl API documentation on line for more info about this callback.
*/
size_t HttpRequest::curlWrite(void* data, size_t numThings, size_t thingSize, void* requestPtr)
{
    HttpRequest* request =  (HttpRequest*)requestPtr;
    return request->appendReceivedData(data, numThings * thingSize);
}


/**
    Append some received data to the receive payload.

    @param    data    Points to received data
    @param    length  Length of received data

    @return   Number of bytes appended (i.e. length)
*/
size_t HttpRequest::appendReceivedData(void* data, size_t length)
{
    size_t beforeSize = _inPayload.size();

    LOG_DEBUG("resizing from %d to %d\n", beforeSize, beforeSize + length);

    _inPayload.resize(beforeSize + length);
    memcpy(&_inPayload.front() + beforeSize, data, length);
    return length;
}




/**
    Need payload data callback from curl.

    @param   dest        Points where to write data to
    @param   numThings   Data points to numThings*thingSize bytes
    @param   thingSize
    @param   requestPtr  Points to the HttpRequest object

    @return  Number of bytes written

    See libcurl API documentation on line for more info about this callback.
*/
size_t HttpRequest::curlRead(void* dest, size_t numThings, size_t thingSize, void* requestPtr)
{
    HttpRequest* request =  (HttpRequest*)requestPtr;
    return request->writePayloadData(dest, numThings * thingSize);
}



/**
    Append some received data to the receive payload.

    @param    dest      Points where to write data to
    @param    length    Number of bytes to write

    @return   Number of bytes written
*/
size_t HttpRequest::writePayloadData(void* dest, size_t length)
{
    size_t bytesWritten = _outPayload.size() - _outIndex;
    if (bytesWritten > length)
        bytesWritten = length;

    memcpy(dest, &_outPayload.front() + _outIndex, bytesWritten);
    _outIndex += bytesWritten;
    return bytesWritten;
}


