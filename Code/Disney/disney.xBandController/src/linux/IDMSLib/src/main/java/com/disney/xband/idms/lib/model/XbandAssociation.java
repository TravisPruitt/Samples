package com.disney.xband.idms.lib.model;

import java.util.Date;

public class XbandAssociation
{
	private String externalNumber;
	private String longRangeTag;
	private String shortRangeTag;
	private String secureId;
	private String uid;
	private String publicId;
	private String printedName;
	private String xbmsId;
	private String bandType;
	private String primaryState;
	private String secondaryState;
	private String guestIdType;
	private String guestIdValue;
	private String xbandOwnerId;
	private String xbandRequestId;
    private Date assignmentDateTime;
	
	public String getExternalNumber() 
	{
		return this.externalNumber;
	}

	public void setExternalNumber(String externalNumber) 
	{
		this.externalNumber = externalNumber;
	}

	public String getShortRangeTag() 
	{
		return shortRangeTag;
	}

	public void setShortRangeTag(String shortRangeTag) 
	{
		this.shortRangeTag = shortRangeTag;
	}

	public String getLongRangeTag() 
	{
		return this.longRangeTag;
	}

	public void setLongRangeTag(String longRangeTag)
	{
		this.longRangeTag = longRangeTag;
	}

	public String getSecureId() 
	{
		return secureId;
	}

	public void setSecureId(String secureId) 
	{
		this.secureId = secureId;
	}

	public String getUID() 
	{
		return this.uid;
	}

	public void setUID(String uid) 
	{
		this.uid = uid;
	}

	public String getPublicId() 
	{
		return this.publicId;
	}

	public void setPublicId(String publicId) 
	{
		this.publicId = publicId;
	}

	public String getPrintedName() 
	{
		return this.printedName;
	}

	public void setPrintedName(String printedName) 
	{
		this.printedName = printedName;
	}

	public String getXbmsId() 
	{
		return this.xbmsId;
	}

	public void setXbmsId(String xbmsId) 
	{
		this.xbmsId = xbmsId;
	}

	public String getBandType() 
	{
		return this.bandType;
	}

	public void setBandType(String bandType) 
	{
		this.bandType = bandType;
	}

	public String getPrimaryState() 
	{
		return this.primaryState;
	}

	public void setPrimaryState(String primaryState) 
	{
		this.primaryState = primaryState;
	}
	
	public String getSecondaryState() 
	{
		return this.secondaryState;
	}

	public void setSecondaryState(String secondaryState) 
	{
		this.secondaryState = secondaryState;
	}
	
	public String getGuestIdType() 
	{
		return this.guestIdType;
	}

	public void setGuestIdType(String guestIdType) 
	{
		this.guestIdType = guestIdType;
	}
	
	public String getGuestIdValue() 
	{
		return this.guestIdValue;
	}

	public void setGuestIdValue(String guestIdValue) 
	{
		this.guestIdValue = guestIdValue;
	}
	
	public String getXbandOwnerId() 
	{
		return this.xbandOwnerId;
	}

	public void setXbandOwnerId(String xbandOwnerId) 
	{
		this.xbandOwnerId = xbandOwnerId;
	}
	
	public String getXbandRequestId() 
	{
		return this.xbandRequestId;
	}

	public void setXbandRequestId(String xbandRequestId) 
	{
		this.xbandRequestId = xbandRequestId;
	}

    public Date getAssignmentDateTime() {
        return assignmentDateTime;
    }

    public void setAssignmentDateTime(Date assignmentDateTime) {
        this.assignmentDateTime = assignmentDateTime;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer("XBandAssociation data:\n");

        sb.append("externalNumber: ");
        sb.append(externalNumber);
        sb.append("\n");

        sb.append("longRangeTag: ");
        sb.append(longRangeTag);
        sb.append("\n");

        sb.append("shortRangeTag: ");
        sb.append(shortRangeTag);
        sb.append("\n");

        sb.append("uid: ");
        sb.append(uid);
        sb.append("\n");

        sb.append("secureId: ");
        sb.append(secureId);
        sb.append("\n");

        sb.append("publicId: ");
        sb.append(publicId);
        sb.append("\n");

        sb.append("printedName: ");
        sb.append(printedName);
        sb.append("\n");

        sb.append("xbmsId: ");
        sb.append(xbmsId);
        sb.append("\n");

        sb.append("bandType: ");
        sb.append(bandType);
        sb.append("\n");

        sb.append("primaryState: ");
        sb.append(primaryState);
        sb.append("\n");

        sb.append("secondaryState: ");
        sb.append(secondaryState);
        sb.append("\n");

        sb.append("guestIdType: ");
        sb.append(guestIdType);
        sb.append("\n");

        sb.append("guestIdValue: ");
        sb.append(guestIdValue);
        sb.append("\n");

        sb.append("xbandOwnerId: ");
        sb.append(xbandOwnerId);
        sb.append("\n");

        sb.append("xbandRequestId: ");
        sb.append(xbandRequestId);
        sb.append("\n");

        sb.append("assignmentDateTime: ");
        sb.append(assignmentDateTime);
        sb.append("\n");

        return sb.toString();
    }
}
