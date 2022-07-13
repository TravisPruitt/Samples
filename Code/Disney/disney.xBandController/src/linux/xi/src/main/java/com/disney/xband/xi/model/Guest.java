package com.disney.xband.xi.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.disney.xband.xi.model.ReturnMessage;

public class Guest
{
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCelebrationType() {
        return celebrationType;
    }

    public void setCelebrationType(String celebrationType) {
        this.celebrationType = celebrationType;
    }

    public String getRecognitionDate() {
        return recognitionDate;
    }

    public void setRecognitionDate(String recognitionDate) {
        this.recognitionDate = recognitionDate;
    }

    // T(g.GuestID), g.FirstName, g.LastName, g.EmailAddress, g.CelebrationType, g.RecognitionDate
    String firstName;
    String lastName;
    String celebrationType;
    String recognitionDate;

    String email;
	String bandId;
	int guestId;
	Timestamp lastRead;

	List<GuestEntitlement> entitlementList = new ArrayList<GuestEntitlement>();
	List<GuestRead> guestReadList = new ArrayList<GuestRead>();
	List<GuestExperience> experienceList=new ArrayList<GuestExperience>();
	
	public Timestamp getLastRead()
	{
		return lastRead;
	}
	


	public void setLastRead(Timestamp lastRead)
	{
		this.lastRead = lastRead;
	}


	public Guest()
	{
		super();
	}

	public void setEmail(String e)
	{
		this.email = e;
	}

	public void setBandId(String e)
	{
		this.bandId = e;
	}

	public void setGuestId(int e)
	{
		this.guestId = e;
	}

	public Guest(String email, String bandId, int guestId)
	{
		super();
		this.email = email;
		this.bandId = bandId;
		this.guestId = guestId;
	}

	public void addGuestRead(String timestamp, int attractionid, String attraction, String queue, String readerlocation) {
		guestReadList.add(new GuestRead(timestamp, attractionid, attraction, queue, readerlocation));
	}
	
	public void addGuestExperience(String timestamp, int attractionid, String attraction, int waitTime) {
		experienceList.add(new GuestExperience(timestamp, attractionid, attraction, waitTime));
	}
	
	public void addEntitlement(int attrId, String attraction, String startTime, String endTime, String status) {
		entitlementList.add( new GuestEntitlement(attrId, attraction, startTime, endTime, status)); 	
	}

	@SuppressWarnings("unused")	
	private class GuestExperience {
		private String timestamp;
		private	int attractionId;
		private String attraction; 
		private int waitTime;
		private String queue;
		
		public GuestExperience(String timestamp,
				int attractionid,
				String attraction, 
				int waitTime ){
			
			this.timestamp=timestamp;
			this.attractionId=attractionid;
			this.attraction=attraction;
			this.waitTime=waitTime;
			
		}
	}
	
	@SuppressWarnings("unused")	
	private class GuestRead {
		private String timestamp;
		private	int attractionId;
		private String attraction; 
		private String queue;
		private	String readerLocation;
		
		public GuestRead(String timestamp,
				int attractionid,
				String attraction, 
				String queue,
				String readerlocation ){
			this.timestamp=timestamp;
			this.attractionId=attractionid;
			this.attraction=attraction;
			this.queue=queue;
			this.readerLocation=readerlocation;
		}
	}
	
	@SuppressWarnings("unused")	
	private class GuestEntitlement {
		private int attractionId;
		private String attraction;
		private String windowStart;
		private String windowEnd;
		private String windowStatus;
		
		public GuestEntitlement(
				int attractionId,
				String attraction, 
				String windowStart, 
				String windowEnd, 
				String status) {
			this.attractionId = attractionId;
			this.attraction=attraction;
			this.windowStart=windowStart;
			this.windowEnd=windowEnd;
			this.windowStatus=status;
		}
	}
}
