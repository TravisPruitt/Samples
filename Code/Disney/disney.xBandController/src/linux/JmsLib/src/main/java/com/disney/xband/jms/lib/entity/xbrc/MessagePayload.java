package com.disney.xband.jms.lib.entity.xbrc;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class MessagePayload implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String venueName;

		private String time;
		
		@XmlAttribute(name="name")
		public String getVenueName()
		{
			return this.venueName;
		}
		
		public void setVenueName(String venueName)
		{
			this.venueName = venueName;
		}
		
		@XmlAttribute(name="time")
		public String getTime()
		{
			return this.time;
		}
		
		public void setTime(String time)
		{
			this.time = time;
		}
		
	}

