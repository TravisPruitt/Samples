/**
    @file   HelloThread.cpp
    @author Greg Strange
    @date   Sept 15, 2011

    Copyright (c) 2011, synapse.com
*/


#include "standard.h"
#include "HelloThread.h"
#include "ticks.h"
#include "log.h"
#include "HttpRequest.h"
#include "DapConfig.h"
#include "DapReader.h"
#include "EventLogger.h"


using namespace Reader;

#define FAST_HELLO_INTERVAL     (5 * 1000)
#define SLOW_HELLO_INTERVAL     (60 * 1000)
#define DIAGNOSTICS_INTERVAL    (60 * 1000)


HelloThread::HelloThread() : _xbrcAlive(false)
{
    // set it up to send first hello right away
    _lastHello = getMilliseconds() - FAST_HELLO_INTERVAL;

    // And send first diagnostics the normal interval from now
    _lastDiagnostics = getMilliseconds();
}


HelloThread::~HelloThread()
{
}

HelloThread* HelloThread::instance()
{
    static HelloThread _instance;
    return &_instance;
}


/**
    Increase the hello frequency
*/
void HelloThread::sayHello()
{
    _xbrcAlive = false;
}


/**
    Send the hello message.
*/
void HelloThread::sendHello()
{
    std::string url = DapConfig::instance()->getXbrcUrl();

    // skip it if no XBRC url is set
    if (url.size() == 0)
        return;

    // append the '/', unless it is already there
    if (url[url.size()-1] != '/')
        url += "/";

    // append the hello URL
    url += "hello";

    HttpRequest* req = new HttpRequest(&_connection, "put", url.c_str());
    if (DapConfig::instance()->getValue("ssl verify host", false))
    {
        req->enableSSLHostVerification();
    }

    Json::Value info;
    DapReader::instance()->getInfo(info);
    req->setPayload(info);
    _xbrcAlive = ( (req->send() && req->getResponseCode() == 200));
    if (!_xbrcAlive)
        DapReader::instance()->setStatus(IStatus::Red, "Message to xBRC failed");

    delete req;
}





/**
    Post the diagnostics event
*/
void HelloThread::sendDiagnostics()
{
    Json::Value eventJson;
    DapReader::instance()->getDiagnostics(eventJson);
    EventLogger::instance()->postEvent(eventJson);
}



void HelloThread::run()
{
    // Initial delay allows time for other threads to spin up and initialize the Feig and Biometric reader
    // Without the delay, the first 'hello' could be lacking some Feig and Lumidigm information.
    sleepMilliseconds(500);

    while (!_quit)
    {
        MILLISECONDS now = getMilliseconds();

        MILLISECONDS interval = _xbrcAlive ? SLOW_HELLO_INTERVAL : FAST_HELLO_INTERVAL;
        if ( (now - _lastHello) >= interval)
        {
            sendHello();
            _lastHello = now;
        }

        if (_xbrcAlive && (now - _lastDiagnostics) >= DIAGNOSTICS_INTERVAL)
        {
            sendDiagnostics();
            _lastDiagnostics = now;
        }

        sleepMilliseconds(1000);
    }
}
