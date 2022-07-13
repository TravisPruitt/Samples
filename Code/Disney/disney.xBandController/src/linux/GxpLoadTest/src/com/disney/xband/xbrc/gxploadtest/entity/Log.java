package com.disney.xband.xbrc.gxploadtest.entity;

import com.disney.xband.common.lib.security.InputValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	
	public static void setLogFile(String sName) throws IOException
	{
		// delete
		new File(sName).delete();
		fw = new FileWriter(InputValidator.validateFilePath(sName));
		bw = new BufferedWriter(fw);
	}
	
	public static void log(String s)
	{
		try
		{
			if (bw!=null)
			{
				bw.write(s + "\n");
				bw.flush();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void close()
	{
		try
		{
			if  (bw!=null)
				bw.close();
			if (fw!=null)
				fw.close();
		}
		catch (IOException e)
		{
		}
	}

	public static String displayDate(Date dt)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		return sdf.format(dt);
	}
	
	

}
