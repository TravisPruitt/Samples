/**
    @file TestThread.h
    @author Greg Strange
    @date Oct 2011

    Copyright (c) 2011, synapse.com
*/


#ifndef __TEST_THREAD_H
#define __TEST_THREAD_H

#include "Thread.h"

namespace Reader
{

    class TestThread : public Thread
    {
    public:
        TestThread();
        ~TestThread();

    private:
        // no copies please
        TestThread(const TestThread&);
        const TestThread& operator=(const TestThread&);

        void run();

    private:    // data
    };

}




#endif
