package com.disney.xband.xbms.web;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessagesPost
{
	private int count;
	private int puckCount;
	private int castMemberCount;
	
	@XmlElement(name="count")
	public int getCount()
	{
		return this.count;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}

	@XmlElement(name="puckCount")
	public int getPuckCount()
	{
		return this.puckCount;
	}
	
	public void setPuckCount(int puckCount)
	{
		this.puckCount = puckCount;
	}
	
	@XmlElement(name="castMemberCount")
	public int getCastMemberCount()
	{
		return this.castMemberCount;
	}
	
	public void setCastMemberCount(int castMemberCount)
	{
		this.castMemberCount = castMemberCount;
	}
}
