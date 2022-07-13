package com.disney.xband.xi.model.jms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class JmsMessage
{
	private long eventId;
	private Date timestamp;
	private String type;

	@XmlAttribute(name="type")
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	@XmlAttribute(name="time")
	public String getTimestamp()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String s = sdf.format(timestamp);
		return s;
	}

	public void setTimestamp(Timestamp ts)
	{
		this.timestamp = new Date(ts.getTime());
	}

	@XmlElement(name="messageid")
	public long getEventId()
	{
		return eventId;
	}

	public void setEventId(long eventId)
	{
		this.eventId = eventId;
	}
	
	
}
