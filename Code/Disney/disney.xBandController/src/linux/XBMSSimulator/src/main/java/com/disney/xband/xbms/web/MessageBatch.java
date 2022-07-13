package com.disney.xband.xbms.web;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessageBatch
{
	private int messageBatchId;
	private String messageBatchDescription;
	private String startDateTime;
	private String finishDateTime;
	private List<Message> messages;
	
	@XmlElement(name="messageBatchId")
	public int getMessageBatchId()
	{
		return this.messageBatchId;
	}
	
	public void setMessageBatchId(int messageBatchId)
	{
		this.messageBatchId = messageBatchId;
	}

	@XmlElement(name="messageBatchDescription")
	public String getMessageBatchDescription()
	{
		return this.messageBatchDescription;
	}
	
	public void setMessageBatchDescription(String messageBatchDescription)
	{
		this.messageBatchDescription = messageBatchDescription;
	}

	@XmlElement(name="startDateTime")
	public String getStartDateTime()
	{
		return this.startDateTime;
	}
	
	public void setStartDateTime(String startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	@XmlElement(name="finishDateTime")
	public String getFinishDateTime()
	{
		return this.finishDateTime;
	}
	
	public void setFinishDateTime(String finishDateTime)
	{
		this.finishDateTime = finishDateTime;
	}

	@XmlElement(name="messages")
	public List<Message> getMessages()
	{
		return this.messages;
	}
	
	public void setMessages(List<Message> messages)
	{
		this.messages = messages;
	}
}
