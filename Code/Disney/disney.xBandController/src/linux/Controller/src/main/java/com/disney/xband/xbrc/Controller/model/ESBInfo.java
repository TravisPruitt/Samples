package com.disney.xband.xbrc.Controller.model;

import java.sql.Connection;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;
import com.disney.xband.xbrc.Controller.Controller;

@PersistName("ESBInfo")
public class ESBInfo  extends Configuration
{
	public transient static Logger logger = Logger.getLogger(ESBInfo.class);

	@PersistName("jmstopic")
	@MetaData(name = "jmstopic", description = "JMS topic")
	private String sJMSTopic;
	@PersistName("jmsdiscoverytimesec")
	@MetaData(name = "jmsdiscoverytimesec", description = "JMS discovery time in seconds", defaultValue = "60")
	private int jmsDiscoveryTimeSec;
	@PersistName("jmssendintervalms")
	@MetaData(name = "jmssendintervalms", description = "How often to send JMS messages in milliseconds.", defaultValue = "100")
	private int jmsSendIntervalMs;
	@PersistName("enablejms")
	@MetaData(name = "enablejms", defaultValue="true", description = "Set this flag to false to turn JMS connection off. No messages will be sent or received.")
	private boolean enablejms;
	
	@Override
	protected void initHook(Connection conn) 
	{
		Properties prop = Controller.getInstance().getProperties();
		
		if (prop.getProperty("jmstopic") != null)
			setJMSTopic(prop.getProperty("jmstopic"));
	}

	public String getJMSTopic()
	{
		return sJMSTopic;
	}

	public void setJMSTopic(String sJMSTopic)
	{
		this.sJMSTopic = sJMSTopic;
	}

	public int getJmsDiscoveryTimeSec()
	{
		return jmsDiscoveryTimeSec;
	}

	public void setJmsDiscoveryTimeSec(int jmsDiscoveryTimeSec)
	{
		this.jmsDiscoveryTimeSec = jmsDiscoveryTimeSec;
	}

	public void processConfigurationTable(Connection conn, Properties prop)
	{
		try
		{
			Config config = Config.getInstance();

			// read property/value pairs from the Config table
			config.read(conn, this);
		}
		catch (Exception e)
		{
			logger.error("!! Error processing configuration table in database: "
					+ e);
		}

		if (prop.getProperty("jmstopic") != null)
			setJMSTopic(prop.getProperty("jmstopic"));

	}

	public int getJmsSendIntervalMs()
	{
		return jmsSendIntervalMs;
	}

	public void setJmsSendIntervalMs(int jmsSendIntervalMs)
	{
		this.jmsSendIntervalMs = jmsSendIntervalMs;
	}

	public boolean isEnablejms() {
		return enablejms;
	}

	public void setEnablejms(boolean enablejms) {
		this.enablejms = enablejms;
	}

}
