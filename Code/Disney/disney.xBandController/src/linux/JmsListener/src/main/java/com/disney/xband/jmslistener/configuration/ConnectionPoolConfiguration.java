package com.disney.xband.jmslistener.configuration;

public interface ConnectionPoolConfiguration
{
	public String getDatabaseDriver();
	public String getDatabaseService();
	public String getDatabaseUser();
	public String getDatabasePassword();
	public int getMaxPoolSize();
	public String getMaxStatements();	
	public String getMaxStatementsPerConnection();
}
