package com.disney.xband.xi.model.jms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
public class JmsLoadMessage extends JmsMessage
{
	private String guestId;
	private boolean xPass;
	private String readerLocation;
	private int waitTime;
	private int mergeTime;
	private String carId;
	
	@XmlElement(name="waittime")
	public int getWaitTime()
	{
		return waitTime;
	}
	public void setWaitTime(int waitTime)
	{
		this.waitTime = waitTime;
	}
	
	@XmlElement(name="mergetime")
	public int getMergeTime()
	{
		return mergeTime;
	}
	public void setMergeTime(int mergeTime)
	{
		this.mergeTime = mergeTime;
	}
	
	@XmlElement(name="carid")
	public String getCarId()
	{
		return carId;
	}
	public void setCarId(String carId)
	{
		this.carId = carId;
	}
	
	@XmlElement(name="guestid")
	public String getGuestId()
	{
		return guestId;
	}
	public void setGuestId(String guestId)
	{
		this.guestId = guestId;
	}
	
	@XmlElement(name="xpass")
	public boolean isxPass()
	{
		return xPass;
	}
	public void setxPass(boolean xPass)
	{
		this.xPass = xPass;
	}
	
	@XmlElement(name="readerlocation")
	public String getReaderLocation()
	{
		return readerLocation;
	}
	public void setReaderLocation(String readerLocation)
	{
		this.readerLocation = readerLocation;
	}

}
