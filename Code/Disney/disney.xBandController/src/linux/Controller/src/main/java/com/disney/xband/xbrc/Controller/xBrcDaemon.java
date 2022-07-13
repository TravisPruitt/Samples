package com.disney.xband.xbrc.Controller;

import java.util.Date;

public class xBrcDaemon implements Runnable
{
	private Thread thMain = null;

	public void destroy()
	{
	}

	public void init(String[] args)
	{
		try
		{

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	public void start() throws Exception
	{
		thMain = new Thread(new xBrcDaemon());

		// start the thread
		if (thMain != null)
			thMain.start();

	}

	public void stop()
	{
		// stop the controller
		Controller.Terminate();

		// wait for a while
		Date dtStart = new Date();
		for (;;)
		{
			if (!thMain.isAlive())
				break;

			// out of time?
			if ((new Date().getTime() - dtStart.getTime()) > 10000)
				break;
		}

	}

	@Override
	public void run()
	{
		// run the regular xBRC startup code
		Controller.main(new String[] {});
	}

}
