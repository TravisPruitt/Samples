package com.disney.xband.xi.model;

public class BlueLane
{
	String attraction = null;
	int attractionId;
	/**
	 * @return the attractionId
	 */
	public int getAttractionId()
	{
		return attractionId;
	}

	/**
	 * @param attractionId the attractionId to set
	 */
	public void setAttractionId(int attractionId)
	{
		this.attractionId = attractionId;
	}

	int blEventCount = 0;
	int blOverrideCount = 0;

	public BlueLane()
	{

	}

	/**
	 * @return the attraction
	 */
	public String getAttraction()
	{
		return attraction;
	}

	/**
	 * @param attraction
	 *            the attraction to set
	 */
	public void setAttraction(String attraction)
	{
		this.attraction = attraction;
	}

	/**
	 * @return the blEventCount
	 */
	public int getBlEventCount()
	{
		return blEventCount;
	}

	/**
	 * @param blEventCount
	 *            the blEventCount to set
	 */
	public void setBlEventCount(int blEventCount)
	{
		this.blEventCount = blEventCount;
	}

	/**
	 * @return the blOverrideCount
	 */
	public int getBlOverrideCount()
	{
		return blOverrideCount;
	}

	/**
	 * @param blOverrideCount
	 *            the blOverrideCount to set
	 */
	public void setBlOverrideCount(int blOverrideCount)
	{
		this.blOverrideCount = blOverrideCount;
	}

}
