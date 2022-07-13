package com.disney.xband.xbrc.lib.entity;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ParkEntryModelConfig
{
	private ReaderOmniServers readerOmniServers;
	private Collection<CastMember> castMembers;

	@XmlElement(name="readerOmniServers")
	public ReaderOmniServers getReaderOmniServers()
	{
		return readerOmniServers;
	}

	public void setReaderOmniServers(ReaderOmniServers readerOmniServers)
	{
		this.readerOmniServers = readerOmniServers;
	}

	@XmlElement(name="castMember")
	public Collection<CastMember> getCastMembers()
	{
		return castMembers;
	}

	public void setCastMembers(Collection<CastMember> castMembers)
	{
		this.castMembers = castMembers;
	}		
}
