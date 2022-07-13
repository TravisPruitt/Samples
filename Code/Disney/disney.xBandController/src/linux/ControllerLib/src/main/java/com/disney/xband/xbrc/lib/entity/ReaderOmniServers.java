package com.disney.xband.xbrc.lib.entity;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class ReaderOmniServers
{
	private Collection<ReaderOmniServer> readerOmniServers;
	private Collection<OmniServer> omniServers;

	@XmlElement(name="readerOmniServer")
	public Collection<ReaderOmniServer> getReaderOmniServers()
	{
		return readerOmniServers;
	}

	public void setReaderOmniServers(Collection<ReaderOmniServer> readerOmniServers)
	{
		this.readerOmniServers = readerOmniServers;
	}
	
	@XmlElement(name="omniServer")
	public Collection<OmniServer> getOmniServers()
	{
		return omniServers;
	}

	public void setOmniServers(Collection<OmniServer> omniServers)
	{
		this.omniServers = omniServers;
	}
}
