package com.disney.xband.idms.lib.model.oneview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.idms.lib.model.LinkCollection;

@XmlRootElement
@XmlType(propOrder={"links","name","milestone","month","day","year","type",
					"startDate","endDate","recognitionDate","surpriseIndicator","comment","guests"})
public class Celebration 
{
	private LinkCollection links;

	private String name;
	private String milestone;
	private String type;
	private String month;
	private String day;
	private String year;
	private Date startDate;
	private Date endDate;
	private Date recognitionDate;
	private boolean surpriseIndicator;
	private String comment;
	private List<CelebrationGuest> guests;
	
	public Celebration()
	{
		this.links = new LinkCollection();
		this.guests = new ArrayList<CelebrationGuest>();
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks()
	{
		return this.links;
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
	
	@XmlElement(name="month")
	public String getMonth() 
	{
		return this.month;
	}
	
	public void setMonth(String month)
	{
		this.month = month;
	}

	@XmlElement(name="day")
	public String getDay() 
	{
		return this.day;
	}

	public void setDay(String day)
	{
		this.day = day;
	}

	@XmlElement(name="year")
	public String getYear() 
	{
		return this.year;
	}

	public void setYear(String year)
	{
		this.year = year;
	}

	@XmlElement(name="startDate")
	public Date getStartDate()
	{
		return this.startDate;
	}
	
	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}
	
	@XmlElement(name="endDate")
	public Date getEndDate() 
	{
		return this.endDate;
	}
	
	public void setEndDate(Date endDate) 
	{
		this.endDate = endDate;
	}
	
	@XmlElement(name="recognitionDate")
	public Date getRecognitionDate() 
	{
		return this.recognitionDate;
	}
	
	public void setRecognitionDate(Date recognitionDate) 
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
	
	@XmlElement(name="guests")
	public List<CelebrationGuest> getGuests() 
	{
		return this.guests;
	}
	
	public void Add(CelebrationGuest guest)
	{
		this.guests.add(guest);
	}

	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}

	public void setGuests(List<CelebrationGuest> guests)
	{
		this.guests = guests;
	}
}
