package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.*;

public class ExitMessage extends EventMessage {

	private static final long serialVersionUID = 1L;
			
	private String carId;
	
	private Statistics statistics;
	
	@XmlElement(name="statistics")
	public Statistics getStatistics()
	{
		return this.statistics;
	}
	
	public void setStatistics(Statistics statistics)
	{
		this.statistics = statistics;
	}	
	
	@XmlElement(name="carid")
	public String getCarId()
	{
		return this.carId;
	}
	
	public void setCarId(String carId)
	{
		this.carId = carId;
	}	
}
