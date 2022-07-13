package com.disney.xband.gxp;

import org.apache.log4j.Logger;

public class Gxp
{
	public static final int nHttpPort = 7090;
	public static final int nHttpsPort = 0; // 7091;
	
	private static Logger logger = Logger.getLogger(WebServer.class);

	public static void main(String[] args)
	{
		try
		{
			// start the web server
			WebServer.INSTANCE.Initialize(nHttpPort, nHttpsPort);
			
			// Wait indefinitely
			if (nHttpPort>0)
				System.out.println("Listening for HTTP on port: " + nHttpPort);
			if (nHttpsPort>0)
				System.out.println("Listening for HTTPS on port: " + nHttpsPort);
			System.out.println("Hit Enter to stop...");
			System.in.read();
			
			WebServer.INSTANCE.Stop();
		}
		catch(Exception ex)
		{
			logger.error("Failed to start web server: " + ex.getLocalizedMessage());
		}
		System.out.println("Done.");

	}

}
