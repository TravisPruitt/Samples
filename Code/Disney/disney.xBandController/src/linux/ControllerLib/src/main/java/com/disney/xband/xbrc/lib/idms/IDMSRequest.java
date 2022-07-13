package com.disney.xband.xbrc.lib.idms;

public class IDMSRequest
{
	public enum RequestTypeEnum
	{
		bandFromLRID,
		bandFromRFID,
		bandFromSecureId,
		guestFromBandId,
		guestFromGuestId,
		celebrationListFromGuestId
	}
	
	private RequestTypeEnum requestType;
	private String sID;

	public IDMSRequest()
	{
	}
	
	public IDMSRequest(RequestTypeEnum requestType, String sID)
	{
		this.requestType = requestType;
		this.sID = sID;
	}

	public RequestTypeEnum getRequestType()
	{
		return requestType;
	}

	public void setRequestType(RequestTypeEnum requestType)
	{
		this.requestType = requestType;
	}

	public String getID()
	{
		return sID;
	}

	public void setID(String sID)
	{
		this.sID = sID;
	}
}
