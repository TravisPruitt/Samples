package com.disney.controller;

import java.util.Date;

public class daemon implements Runnable  {

    private Thread thMain = null;

    public void destroy()
    {}

    public void init(String[] args)
    {
        try
        {

        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }

    public void start() throws Exception
    {
        thMain = new Thread(new daemon());

        if (thMain != null)
            thMain.start();

    }

    public void stop()
    {
         com.disney.Main.Terminate();

         Date dtStart = new Date();
         for(;;)
         {
             if (thMain.isAlive())
                 break;

             if ((new Date().getTime() - dtStart.getTime()) > 10000)
                break;
         }

    }

    @Override
    public void run()
    {
        com.disney.Main.main(new String[] {});

    }


}
