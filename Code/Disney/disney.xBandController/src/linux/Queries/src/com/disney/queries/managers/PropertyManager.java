package com.disney.queries.managers;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.disney.xband.common.lib.security.InputValidator;
import org.apache.log4j.Logger;

public class PropertyManager {

	private String fileName = "Queries.properties";
	private String defaultFileName = "Queries.properties";
	private Properties pi;
	private boolean isRemotePropertyFile = false;
	private static String CONFIG_DIR;


	private static Logger logger = Logger.getLogger(PropertyManager.class);


	public PropertyManager()
	{
		CONFIG_DIR = InputValidator.validateDirectoryName(System.getProperty("config.dir"));

		pi = new Properties();
        InputStream is = null;

		try {
			if (CONFIG_DIR != null)
			{
				fileName=CONFIG_DIR + "/IDMSH2Web/IDMSLib.properties";
                is = this.getClass().getClassLoader().getResourceAsStream(fileName);
				pi.load(is);
				CheckRemoteSetting();
			}
			else
			{
                is = this.getClass().getClassLoader().getResourceAsStream(fileName);
				pi.load(is);
				CheckRemoteSetting();
			}

		} catch (IOException e) {

			logger.error(e.getMessage());
			logger.error(e);
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private void CheckRemoteSetting()
	{
        InputStream is = null;
		try
		{
			defaultFileName = getPropertyValue("remoteSettings");

			if (defaultFileName != null && defaultFileName.length() > 0)
			{
                is = new FileInputStream(InputValidator.validateFilePath(defaultFileName));
				// We have a remote settings file, so use it now.
				pi.load(is);
				fileName = defaultFileName;
				isRemotePropertyFile = true;
			}
			else
			{
				isRemotePropertyFile = false;
			}

		}
		catch (IOException e)
		{
			logger.error(e.getMessage());
			logger.error(e);

		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	public String getPropertyValue(String keyName)
	{
		String retVal = "";

		retVal = pi.getProperty(keyName);

		return retVal;
	}

	public void setPropertyValue(String keyName, String value)
	{
		pi.setProperty(keyName, value);
	}


	public void store(FileOutputStream writer, String comments)
	{
		try {
			pi.store(writer, comments);
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e);
		}
	}

	public boolean IsRemotePropertyFile()
	{
		return this.isRemotePropertyFile;
	}

	public String getFileName()
	{
		return this.fileName;
	}

}
