package com.disney.xband.xi.model.jms;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="jmsmessagehistogram")
public class JmsMessageHistogram
{
	private JmsDAO.HistogramGrouping grouping;
	private List<JmsMessageHistogramInterval> intervals;

	@XmlElement(name="grouping")
	public String getGrouping()
	{
		return grouping.toString();
	}

	public void setGrouping(String grouping)
	{
		try
		{
			this.grouping = JmsDAO.HistogramGrouping.valueOf(grouping.toUpperCase());
		}
		catch(Exception ex)
		{
		}
	}

	@XmlElement(name="interval")
	public List<JmsMessageHistogramInterval> getIntervals()
	{
		return intervals;
	}

	public void setIntervals(List<JmsMessageHistogramInterval> intervals)
	{
		this.intervals = intervals;
	}
}
