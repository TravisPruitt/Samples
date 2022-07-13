package com.disney.xband.xi.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.*;

import javax.xml.bind.DatatypeConverter;

public class ConfigUtil
{
	protected static String fileUrl = "/opt/apps/share/disney.xband/xi/xidata.properties";
	private Properties prop;

	public static String convertHexToString(String inStr)
	{
		return new String(DatatypeConverter.parseBase64Binary(inStr));
	}

	public static void setConfig(Properties p) {
		FileOutputStream fos = null;
    	try
    	{
    		fos = new FileOutputStream(ConfigUtil.fileUrl);
    		p.store(fos, "xi webservice properties");
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		if (fos != null)
    			try
    			{
    				fos.close();
    			}
    			catch (IOException ioe)
    			{
    			}
    	}	
	}
	
	public static Properties getConfig() {
		Properties prop = new Properties();
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(fileUrl);
			prop.load(fis);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fis != null)
				try
				{
					fis.close();
				}
				catch (IOException ioe)
				{
				}
		}
		
		return prop;
	}
	
	public ConfigUtil()
	{
		prop = new Properties();
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(fileUrl);
			prop.load(fis);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fis != null)
				try
				{
					fis.close();
				}
				catch (IOException ioe)
				{
				}
		}
	}

}