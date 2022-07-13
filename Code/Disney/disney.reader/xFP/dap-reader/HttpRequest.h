/**
    HttpRequest - HTTP Request
    August 2, 2011
    Greg Strange

    Copyright (c) 2011, synapse.com
*/


#ifndef __HTTP_REQUEST_H
#define __HTTP_REQUEST_H

#include "HttpConnection.h"
#include <curl/curl.h>
#include <json/json.h>
#include <stdint.h>
#include <vector>

namespace Reader
{

    class HttpRequest
    {
    public:
        HttpRequest(const char* method, const char* url);
        HttpRequest(HttpConnection *connection, const char* method, const char* url);
        ~HttpRequest();

        void setPayload(const char* payload, const char* type);
        void setPayload(Json::Value& payload);
        void setPayloadFromFile(const char* fileName);

        void enableSSLHostVerification();

        bool send();

        long getResponseCode();

        std::string getResponsePayloadAsString();

    private:
        enum HttpMethod { HTTP_GET, HTTP_POST, HTTP_PUT, HTTP_DELETE };

        std::string _verb;
        std::string _url;
        HttpMethod _method;

        // Indicates weather to use CA verification when initiating SSL
        // connections.
        bool _useSSLVerification;

        // Indicates wheather this class is managing the HttpConnection
        // object, so it can free the handle during deconstruction if
        // necessary.
        bool _manageConnection;

        HttpConnection *_connection;
        struct curl_slist* _headers;

        std::vector<uint8_t> _inPayload;
        std::vector<uint8_t> _outPayload;
        size_t _outIndex;

        char _errorDescription[CURL_ERROR_SIZE];

        void init(HttpConnection *connection, const char* method, const char* url);

        static size_t curlWrite(void* data, size_t numThings, size_t thingSize, void* requestPtr);
        static size_t curlRead(void* data, size_t numThings, size_t thingSize, void* requestPtr);

        size_t appendReceivedData(void* data, size_t length);
        size_t writePayloadData(void* dest, size_t length);
    };
}

#endif
