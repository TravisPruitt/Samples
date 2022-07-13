/**
 *  @file   HelloThread.h
 *  @author Greg Strange
 *  @date   Sept 15, 2011
 *
 *  Copyright (c) 2011-2012, synapse.com
 */


#ifndef __HELLO_THREAD_H
#define __HELLO_THREAD_H


#include <stdint.h>
#include <json/json.h>
#include <queue>
#include "Thread.h"
#include "Event.h"
#include "ticks.h"
#include "HttpConnection.h"


namespace Reader
{


class HelloThread: public Thread
{
public:
    static HelloThread* instance();

    void sayHello();

private:
    // singleton
    HelloThread();
    ~HelloThread();

    // no copies please
    HelloThread(const HelloThread&);
    const HelloThread& operator=(const HelloThread&);

    void run();
    void sendHello();
    void sendDiagnostics();

    HttpConnection _connection;
    bool _xbrcAlive;
    MILLISECONDS _lastHello;
    MILLISECONDS _lastDiagnostics;
};


} // namespace Reader



#endif
