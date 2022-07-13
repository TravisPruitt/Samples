package com.disney.xband.xi.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReturnMessage
{
	private String statusMessage;
	private String status = "success";
	private long msgTimeStamp;
    private int parkId=-1;

    public int getParkId() {
        return parkId;
    }

    public void setParkId(int parkId) {
        this.parkId = parkId;
    }


    public String getBuster() {
        return buster;
    }

    public void setBuster(String buster) {
        this.buster = buster;
    }

    private String buster = "";

	public ReturnMessage()
	{
		this.setMessageTimeStamp(System.currentTimeMillis());
	}

	private Map<String, Object> data = new HashMap<String, Object>();
    private Map<String, Date> dates = new HashMap<String, Date>();

    public void addDate(String key, Date d) {
        dates.put(key, d);
    }

    public Map<String, Object> getData() {
        return data;
    }

	public void addData(String key, Object value)
	{
		data.put(key, value);
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	public String getStatus()
	{
		return status;
	}

	public long getMessageTimeStamp()
	{
		return msgTimeStamp;
	}

	public void setMessageTimeStamp(long msgTimeStamp)
	{
		this.msgTimeStamp = msgTimeStamp;
	}
}
