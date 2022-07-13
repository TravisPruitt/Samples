package com.disney.xband.xi.model.jms;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="venue")
@XmlSeeAlso({JmsGuestState.class, JmsLocationInfo.class})
public class JmsGuestStates
{
	private List<JmsGuestState> guestStates;
	private String name;
	private String time;

	@XmlMixed
	public List<JmsGuestState> getGuestState()
	{
		return guestStates;
	}

	public void setGuestStates(List<JmsGuestState> guestStates)
	{
		this.guestStates = guestStates;
	}
	
	@XmlAttribute(name="name")
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}

	@XmlAttribute(name="time")
	public String getTime()
	{
		return time;
	}
	public void setTime(String time)
	{
		this.time = time;
	}
	
	
}
