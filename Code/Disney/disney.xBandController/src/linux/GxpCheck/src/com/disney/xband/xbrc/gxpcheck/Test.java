package com.disney.xband.xbrc.gxpcheck;

import java.io.IOException;
import com.disney.xband.xbrc.gxpcheck.entity.Log;
import com.disney.xband.common.lib.security.InputValidator;

public class Test
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		CLOptions clo = CLOptions.parse(args);
		if (clo==null)
			return;
		
		if (clo.hasDisplayUsage())
		{
			CLOptions.usage();
			return;
		}
		
		if (clo.getLogFile()!=null)
			Log.setLogFile(InputValidator.validateFilePath(clo.getLogFile()));
		
		// stress!
		Checker.INSTANCE.check(clo);
		
		Log.close();
	}

}
