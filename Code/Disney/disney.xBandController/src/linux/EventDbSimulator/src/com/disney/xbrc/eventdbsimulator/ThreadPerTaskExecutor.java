package com.disney.xbrc.eventdbsimulator;

import java.util.concurrent.Executor;

class ThreadPerTaskExecutor implements Executor 
{
    public void execute(Runnable r) 
    {
        new Thread(r).start();
    }
}
