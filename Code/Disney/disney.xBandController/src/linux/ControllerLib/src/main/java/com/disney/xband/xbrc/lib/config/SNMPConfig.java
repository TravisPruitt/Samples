package com.disney.xband.xbrc.lib.config;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;

@PersistName("SNMPConfig")
public class SNMPConfig extends Configuration {
	@MetaData(name="community", description = "Type of password Xbrc SNMP Agent uses to authenticate communication from SNMP Manager.", defaultValue = "mayhem")
	public static String community;
	@MetaData(name="agentListenPort", description = "Xbrc SNMP Agent binds to this port to listens to requests from SNMP Manager.", defaultValue = "8161")
	public static int agentListenPort;
	@MetaData(name="trapAddress", description = "UDP address to which the Xbrc SNMP Agent broadcasts trap/notification messages.", defaultValue = "0.0.0.0/162")
	public static String trapAddress;
	@MetaData(name="agentDispatcherPool", description = "Number of threads to be used by the Xbrc SNMP Agent to process incoming requests.", defaultValue = "10")
	public static int agentDispatcherPool;
	@MetaData(name="managerDispatcherPool", description = "Number of threads to be used by the Xbrc test SNMP Manager to process incoming requests.", defaultValue = "10")
	public static int managerDispatcherPool;
	@MetaData(name="context", description = "Collection of management information accessible by an SNMP entity.", defaultValue = "public")
	public static String context = "public";
	@MetaData(name="readerHttpConnTimeout", description = "Quit trying to open an http connection to a reader after this many milliseconds.", defaultValue = "1000")
	public static int readerHttpConnTimeout;
	@MetaData(name="maxOIDs", description = "Maximum number of OID numbers to walk or GETBULK.", defaultValue = "2000")
	public static int maxOIDs;
	@MetaData(name="getRetries", description = "How many time to re-send a get request when there is not immediate response.", defaultValue = "0")
	public static int getRetries;
	@MetaData(name="getTimeout", description = "Number of milliseconds to wait for a response to a GET request before re-sending the request.", defaultValue = "50000")
	public static int getTimeout;
	@MetaData(name="dateFormat", description = "Date format the Xbrc test SNMP Manager uses for logging.", defaultValue = "yyyy-MM-dd HH:mm:ss z")
	public static String dateFormat;
	
	@SuppressWarnings("unused")
	private transient static Logger logger = Logger.getLogger(SNMPConfig.class);

	public static SNMPConfig getInstance(){
		return SingletonHolder.instance;
	}
	
	@Override
	protected void initHook(Connection conn){}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		SNMPConfig.community = community;
	}

	public int getAgentListenPort() {
		return agentListenPort;
	}

	public void setAgentListenPort(int agentListenPort) {
		SNMPConfig.agentListenPort = agentListenPort;
	}

	/**
	 * @return the agentDispatcherPool
	 */
	public int getAgentDispatcherPool() {
		return agentDispatcherPool;
	}

	/**
	 * @return the managerDispatcherPool
	 */
	public int getManagerDispatcherPool() {
		return managerDispatcherPool;
	}

	/**
	 * @param agentDispatcherPool the agentDispatcherPool to set
	 */
	public void setAgentDispatcherPool(int agentDispatcherPool) {
		SNMPConfig.agentDispatcherPool = agentDispatcherPool;
	}

	/**
	 * @param managerDispatcherPool the managerDispatcherPool to set
	 */
	public void setManagerDispatcherPool(int managerDispatcherPool) {
		SNMPConfig.managerDispatcherPool = managerDispatcherPool;
	}

	public String getTrapAddress() {
		return trapAddress;
	}

	public void setTrapAddress(String trapAddress) {
		SNMPConfig.trapAddress = trapAddress;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		SNMPConfig.context = context;
	}

	public int getReaderHttpConnTimeout() {
		return readerHttpConnTimeout;
	}

	public void setReaderHttpConnTimeout(int readerHttpConnTimeout) {
		SNMPConfig.readerHttpConnTimeout = readerHttpConnTimeout;
	}

	public int getMaxOIDs() {
		return maxOIDs;
	}

	public void setMaxOIDs(int maxOIDs) {
		SNMPConfig.maxOIDs = maxOIDs;
	}

	/**
	 * @return the getRetries
	 */
	public int getGetRetries() {
		return getRetries;
	}

	/**
	 * @return the getTimeout
	 */
	public int getGetTimeout() {
		return getTimeout;
	}

	/**
	 * @param getRetries the getRetries to set
	 */
	public void setGetRetries(int getRetries) {
		SNMPConfig.getRetries = getRetries;
	}

	/**
	 * @param getTimeout the getTimeout to set
	 */
	public void setGetTimeout(int getTimeout) {
		SNMPConfig.getTimeout = getTimeout;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		SNMPConfig.dateFormat = dateFormat;
	}

	private SNMPConfig(){}
	
	private static class SingletonHolder {
		private static SNMPConfig instance = new SNMPConfig();
	}
}
