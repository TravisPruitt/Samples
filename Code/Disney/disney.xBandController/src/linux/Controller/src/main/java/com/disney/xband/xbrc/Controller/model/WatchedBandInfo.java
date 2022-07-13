package com.disney.xband.xbrc.Controller.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.LrrEvent;

@XmlRootElement
public class WatchedBandInfo
{
	private Date dtLastAccess;
	private LinkedList<LrrEvent> liEvents = new LinkedList<LrrEvent>();
	private Long nPno = -1l;

	public static Logger logger = Logger.getLogger(WatchedBandInfo.class);

	public WatchedBandInfo()
	{
		dtLastAccess = new Date();
	}

	public void AddEvent(LrrEvent ev, int maxEventsBeforePurge)
	{
//		if (!ev.getPno().equals(nPno))
//		{
//			liEvents.clear();
//			nPno = ev.getPno();
//		}

		if (liEvents.size() >= maxEventsBeforePurge)
			liEvents.pop();

		liEvents.add(ev);
	}

	public Date getLastAccessTime()
	{
		return dtLastAccess;
	}

	@XmlElementWrapper
	@XmlElement(name = "event")
	public List<LrrEvent> getEvents()
	{
		dtLastAccess = new Date();
		return liEvents;
	}
}
