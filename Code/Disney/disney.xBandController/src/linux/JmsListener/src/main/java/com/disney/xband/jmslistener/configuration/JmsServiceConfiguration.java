package com.disney.xband.jmslistener.configuration;

import java.util.Properties;


public interface JmsServiceConfiguration
{
	public String getJmsBrokerUrl();
	public String getJmsBrokerDomain();
	public String getJmsUser();
	public String getJmsPassword();
	public int getSharedConfigurations();
	public String getSharedConfigurationGroup();
	public int getJmsRetryPeriod();
    public long getNoTrafficToleranceSecs();
	
	public boolean hasPropertiesMissing();
	
	public void setProperties(Properties properties);
}
