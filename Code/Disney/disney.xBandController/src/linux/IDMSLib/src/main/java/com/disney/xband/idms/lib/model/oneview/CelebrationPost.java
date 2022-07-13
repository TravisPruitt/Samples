package com.disney.xband.idms.lib.model.oneview;

import javax.xml.bind.annotation.XmlElement;

public class CelebrationPost 
{

	private String xid;
	private String name;
	private String milestone;
	private String type;
	private String role;
	private String date;
	private String startDate;
	private String endDate;
	private String recognitionDate;
	private boolean surpriseIndicator;
	private String comment;
	
	@XmlElement(name="xid")
	public String getXID() 
	{
		return this.xid;
	}
	
	public void setXID(String xid) 
	{
		this.xid = xid;
	}

	@XmlElement(name="name")
	public String getName() 
	{
		return this.name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}

	@XmlElement(name="milestone")
	public String getMilestone() 
	{
		return this.milestone;
	}
	
	public void setMilestone(String milestone) 
	{
		this.milestone = milestone;
	}

	@XmlElement(name="type")
	public String getType() 
	{
		return this.type;
	}
	
	public void setType(String type) 
	{
		this.type = type;
	}
	
	@XmlElement(name="role")
	public String getRole() 
	{
		return this.role;
	}
	
	public void setRole(String role) 
	{
		this.role = role;
	}
	
	@XmlElement(name="date")
	public String getDate() 
	{
		return this.date;
	}
	
	public void setDate(String date) 
	{
		this.date = date;
	}
	
	@XmlElement(name="startDate")
	public String getStartDate() 
	{
		return this.startDate;
	}
	
	public void setStartDate(String startDate) 
	{
		this.startDate = startDate;
	}
	
	@XmlElement(name="endDate")
	public String getEndDate() 
	{
		return this.endDate;
	}
	
	public void setEndDate(String endDate) 
	{
		this.endDate = endDate;
	}
	
	@XmlElement(name="recgonitionDate")
	public String getRecognitionDate() 
	{
		return this.recognitionDate;
	}
	
	public void setRecognitionDate(String recognitionDate) 
	{
		this.recognitionDate = recognitionDate;
	}
	
	@XmlElement(name="surpriseIndicator")
	public boolean hasSurpriseIndicator() 
	{
		return this.surpriseIndicator;
	}
	
	public void setSurpriseIndicator(boolean surpriseIndicator) 
	{
		this.surpriseIndicator = surpriseIndicator;
	}
	
	@XmlElement(name="comment")
	public String getComment() 
	{
		return this.comment;
	}
	
	public void setComment(String comment) 
	{
		this.comment = comment;
	}

}
