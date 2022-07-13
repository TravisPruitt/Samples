package com.disney.xband.xbrms.client.model;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;

public class XbrcUiKeyDpo implements IPresentationObject {
	private String id;
	private String ip;
	private String port;
	private String name;
	private HAStatusEnum haStatus;
	
	public XbrcUiKeyDpo(String id)
	{
		this.id = id;
	}
	
	public XbrcUiKeyDpo(String ip, String port, String name, String haStatus)
	{
		this.ip = ip;
		this.port = port;
		this.name = name;
		
		this.haStatus = HAStatusEnum.getStatus(haStatus);
		
		this.id = createUniqueXbrcId(ip, port);
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @return the ip
	 */
	public String getIp()
	{
		return ip;
	}

	/**
	 * @return the port
	 */
	public String getPort()
	{
		return port;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port)
	{
		this.port = port;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public HAStatusEnum getHaStatus()
	{
		return haStatus;
	}

	public void setHaStatus(HAStatusEnum haStatus)
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
				+ ((id == null) ? 0 : id.hashCode());

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
		XbrcUiKeyDpo other = (XbrcUiKeyDpo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}
	
	private String createUniqueXbrcId(String ip, String port)
	{
		StringBuffer uniqueId = new StringBuffer();
		
		uniqueId.append(ip.replace('.', '_')).append('-').append(port);
		
		return uniqueId.toString();
	}
}
