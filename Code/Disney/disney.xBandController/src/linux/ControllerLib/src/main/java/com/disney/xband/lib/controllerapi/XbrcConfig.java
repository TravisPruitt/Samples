package com.disney.xband.lib.controllerapi;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.common.lib.ConfigInfo;

@XmlRootElement(name="venue")
public class XbrcConfig {
	
	// attributes
	private String id;
	private String name;
	private String time;
	
	// elements
	private String ip;
	private String port;
	private String haStatus;
	
	private LinkedList<ConfigInfo> configuration;
	
	@XmlAttribute
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	/**
	 * @return the configuration
	 */
	@XmlElementWrapper
	@XmlElement(name="config")
	public LinkedList<ConfigInfo> getConfiguration() {
		return configuration;
	}
	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(LinkedList<ConfigInfo> configuration) {
		this.configuration = configuration;
	}
	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}
	/**
	 * @return the time
	 */
	@XmlAttribute
	public String getTime() {
		return time;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the hostName
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param hostName the hostName to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getPort()
	{
		return port;
	}
	public void setPort(String port)
	{
		this.port = port;
	}
	public String getHaStatus()
	{
		return haStatus;
	}
	public void setHaStatus(String haStatus)
	{
		this.haStatus = haStatus;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XbrcConfig other = (XbrcConfig) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!ip.equals(other.port))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
