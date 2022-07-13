package com.disney.xband.xbrc.gxploadtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.xbrc.gxploadtest.entity.GxpMessage;
import com.disney.xband.xbrc.gxploadtest.entity.Log;

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
		
		// read the data file
		FileReader fr = new FileReader(InputValidator.validateFilePath(clo.getDataFile()));
		BufferedReader br = new BufferedReader(fr);
		List<GxpMessage> liRequests = new ArrayList<GxpMessage>();
		while (true)
		{
			String sLine = br.readLine();
			if (sLine==null)
				break;
			
			// skip blank lines
			if (sLine.trim().length()==0)
				continue;
			
			// process requests
			GxpMessage req = GxpMessage.processLine(sLine, clo);
			liRequests.add(req);
		}
		br.close();
		fr.close();
		
		// stress!
		Stressor.INSTANCE.stress(clo, liRequests);
		
		Log.close();
	}

}
