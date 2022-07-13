package com.disney.xband.xi.model.jms;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="venue")
@XmlSeeAlso({JmsEntryMessage.class, JmsMergeMessage.class, JmsLoadMessage.class, JmsExitMessage.class})
public class JmsMessages
{
	private List<JmsMessage> messages;
	private String name;
	private String time;


	@XmlMixed
	public List<JmsMessage> getMessages()
	{
		return messages;
	}

	public void setMessages(List<JmsMessage> messages)
	{
		this.messages = messages;
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
