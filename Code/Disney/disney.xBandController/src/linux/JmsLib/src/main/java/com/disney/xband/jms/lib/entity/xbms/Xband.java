package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 { "assignmentDateTime" : "2011-02-22T04:09:47Z",
  "externalNumber" : 4320000000000000,
  "printedName" : "Carol Xband3",
  "productId" : "B11012",
  "publicId" : 1234,
  "secondaryState" : "ORIGINAL",
  "secureId" : 66,
  "self" : "/xband/0192B866-85BD-4369-9E85-84237315A110",
  "shortRangeTag" : 166,
  "state" : "ACTIVE",
  "guestId" : "0915EFB4-3B7D-45B4-93FD-D7145941D484",
  "guestIdType" : "swid",
  "xbandId" : "0192B866-85BD-4369-9E85-84237315A110",
  "xbandRequest" : "/xband-requests/6315A9F3-C19C-4530-8337-A73AA21C33D3",
  "xbandOwnerId" : "6422FA29-6BDB-4831-9F5F-6A2A165E3FA1",
  "options" : "/xband-options/0192B866-85BD-4369-9E85-84237315A110"
  "history" : "/xband-history/0192B866-85BD-4369-9E85-84237315A110"}
  "bandRole" : "Puck"
 */

@XmlRootElement
public class Xband 
{
	private String assignmentDateTime;
	private String externalNumber;
	private String printedName;
	private String productId;
	private long publicId;
	private String secondaryState;
	private long secureId;
	private String self;
	private String state;
	private String shortRangeTag;
	private String guestId;
	private String guestIdType;
	private String xbandId;
	private String xbandRequest;
	private String xbandOwnerId;
	private String options;
	private String history;
	private String bandRole;

	@XmlElement(name="assignmentDateTime")
	public String getAssignmentDateTime()
	{
		return this.assignmentDateTime;
	}

	public void setAssignmentDateTime(String assignmentDateTime)
	{
		this.assignmentDateTime = assignmentDateTime;
	}
	
	@XmlElement(name="externalNumber")
	public String getExternalNumber()
	{
		return this.externalNumber;
	}

	public void setExternalNumber(String externalNumber)
	{
		this.externalNumber = externalNumber;
	}
	
	@XmlElement(name="printedName")
	public String getPrintedName()
	{
		return this.printedName;
	}

	public void setPrintedName(String printedName)
	{
		this.printedName = printedName;
	}
	
	@XmlElement(name="productId")
	public String getProductId()
	{
		return this.productId;
	}

	public void setProductId(String productId)
	{
		this.productId = productId;
	}
	
	@XmlElement(name="publicId")
	public long getPublicId()
	{
		return this.publicId;
	}

	public void setPublicId(long publicId)
	{
		this.publicId = publicId;
	}
	
	@XmlElement(name="secondaryState")
	public String getSecondaryState()
	{
		return this.secondaryState;
	}

	public void setSecondaryState(String secondaryState)
	{
		this.secondaryState = secondaryState;
	}
	
	@XmlElement(name="secureId")
	public long getSecureId()
	{
		return this.secureId;
	}

	public void setSecureId(long secureId)
	{
		this.secureId = secureId;
	}
	
	@XmlElement(name="self")
	public String getSelf()
	{
		return this.self;
	}

	public void setSelf(String self)
	{
		this.self = self;
	}
	
	@XmlElement(name="state")
	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
	}
	
	@XmlElement(name="shortRangeTag")
	public String getShortRangeTag()
	{
		return this.shortRangeTag;
	}

	public void setShortRangeTag(String shortRangeTag)
	{
		this.shortRangeTag = shortRangeTag;
	}
	
	@XmlElement(name="guestId")
	public String getGuestId()
	{
		return this.guestId;
	}

	public void setGuestId(String guestId)
	{
		this.guestId = guestId;
	}
	
	@XmlElement(name="guestIdType")
	public String getGuestIdType()
	{
		return this.guestIdType;
	}

	public void setGuestIdType(String guestIdType)
	{
		this.guestIdType = guestIdType;
	}
	
	@XmlElement(name="xbandId")
	public String getXbandId()
	{
		return this.xbandId;
	}

	public void setXbandId(String xbandId)
	{
		this.xbandId = xbandId;
	}
	
	@XmlElement(name="xbandRequest")
	public String getXbandRequest()
	{
		return this.xbandRequest;
	}

	public void setXbandRequest(String xbandRequest)
	{
		this.xbandRequest = xbandRequest;
	}

	@XmlElement(name="xbandOwnerId")
	public String getXbandOwnerId()
	{
		return this.xbandOwnerId;
	}

	public void setXbandOwnerId(String xbandOwnerId)
	{
		this.xbandOwnerId = xbandOwnerId;
	}

	@XmlElement(name="options")
	public String getOptions()
	{
		return this.options;
	}

	public void setOptions(String options)
	{
		this.options = options;
	}
	
	@XmlElement(name="history")
	public String getHistory()
	{
		return this.history;
	}

	public void setHistory(String history)
	{
		this.history = history;
	}
	
	@XmlElement(name="bandRole")
	public String getBandRole()
	{
		return this.bandRole;
	}

	public void setBandRole(String bandRole)
	{
		this.bandRole = bandRole;
	}
}
