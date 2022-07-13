package com.disney.xband.xi.model;



public class Entitlement {
	private String attraction;
	private int attractionId;
	
	private int hour;
	private int guestsInQueue=0;
	private int availableEntitlements=0;
	private int selectedEntitlements=0;
	private int redeemedEntitlements=0;
    private int selectedInPark=0;
	private int totalBlueLane=0;
	private int overridesBlueLane=0;
	
	public Entitlement() {
		
	}



	public String getAttraction()
	{
		return attraction;
	}
	public void setAttraction(String attraction)
	{
		this.attraction = attraction;
	}
	public int getGuestsInQueue()
	{
		return guestsInQueue;
	}
	public void setGuestsInQueue(int guestsInQueue)
	{
		this.guestsInQueue = guestsInQueue;
	}
	public int getAvailableEntitlements()
	{
		return availableEntitlements;
	}
	public void setAvailableEntitlements(int availableEntitlements)
	{
		this.availableEntitlements = availableEntitlements;
	}
	public int getSelectedEntitlements()
	{
		return selectedEntitlements;
	}
	public void setSelectedEntitlements(int selectedEntitlements)
	{
		this.selectedEntitlements = selectedEntitlements;
	}
	public int getRedeemedEntitlements()
	{
		return redeemedEntitlements;
	}
	public void setRedeemedEntitlements(int redeemedEntitlements)
	{
		this.redeemedEntitlements = redeemedEntitlements;
	}

    public int getSelectedInPark()
    {
        return this.selectedInPark;
    }

    public void setSelectedInPark(int selectedInPark)
    {
        this.selectedInPark = selectedInPark;
    }


	public int getTotalBlueLane()
	{
		return totalBlueLane;
	}



	public void setTotalBlueLane(int totalBlueLane)
	{
		this.totalBlueLane = totalBlueLane;
	}
	public int getOverridesBlueLane()
	{
		return overridesBlueLane;
	}
	public void setOverridesBlueLane(int overridesBlueLane)
	{
		this.overridesBlueLane = overridesBlueLane;
	}
	public int getHour()
	{
		return hour;
	}
	public void setHour(int hour)
	{
		this.hour = hour;
	}
	public int getAttractionId()
	{
		return attractionId;
	}
	public void setAttractionId(int attractionId)
	{
		this.attractionId = attractionId;
	}
}