package com.disney.xband.common.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.disney.xband.common.lib.security.InputValidator;
import org.apache.log4j.Logger;


public class NGEPropertiesDecoder
{
	private static Logger logger = Logger.getLogger(NGEPropertiesDecoder.class);
	private String propertiesPath = "file:///etc/nge/config/environment.properties";
	private String jasyptPropertiesPath = "file:///etc/nge/config/jasypt.properties";
	private String clientKey = "nge.jasypt.xconnect.key";
	private String clientSecret = "disney";
	private XconnectJasyptManager manager;
	
	public NGEPropertiesDecoder()
	{
	}
	
	public void initialize(String password) throws IOException
	{
		InputStream is = null;

		try
		{
			Properties jasyptprop = new Properties();
			is = URLReader.getInputStream(InputValidator.validateFilePath(jasyptPropertiesPath));
			jasyptprop.load(is);
			if (password == null)
				clientSecret = jasyptprop.getProperty(clientKey);
			else
				clientSecret = password;			
			manager = new XconnectJasyptManager(clientSecret);
		}
		catch(IOException e)
		{
			logger.warn("Could not load resource " + jasyptPropertiesPath + " Using the default decryption key.");
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		if (clientSecret == null)
			throw new IllegalArgumentException("The key " + clientKey + " is not set in the " + jasyptPropertiesPath + " file");
	}
	
	public void initialize() throws IOException
	{
		initialize(null);
	}

	public Properties read() throws IOException
	{		
		Properties prop = new Properties();
		InputStream fis = null;

        try {
            fis = URLReader.getInputStream(InputValidator.validateFilePath(propertiesPath));
		    prop.load(fis);
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }
        }
		
		for (String key : prop.stringPropertyNames())
		{
			String value = prop.getProperty(key);
			if (value.startsWith("ENC("))
			{
				String enc = value.substring(4,value.length()-1);
				String dec = manager.getDecryptedData(enc);
				prop.setProperty(key,dec);
			}
		}
		
		return prop;
	}
	
	public String encrypt(String data) throws IOException
	{
		return manager.getEncryptedData(data);
	}
	
	public String decrypt(String data) throws IOException
	{
		return manager.getDecryptedData(data);
	}
	
	public String getPropertiesPath()
	{
		return propertiesPath;
	}

	public void setPropertiesPath(String newPropertiesPath)
	{
		propertiesPath = InputValidator.validateFilePath(newPropertiesPath);
	}

	public String getJasyptPropertiesPath()
	{
		return jasyptPropertiesPath;
	}

	public void setJasyptPropertiesPath(String newJasyptPropertiesPath)
	{
		jasyptPropertiesPath = InputValidator.validateFilePath(newJasyptPropertiesPath);
	}
}
