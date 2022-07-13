package com.disney.xband.lib.xbrapi;

import java.util.Date;

import com.disney.xband.xview.lib.model.Guest;

public abstract class XbrEvent {
	
	// Event type
	private XbrEventType type;	
	// Event number. (eno)
	private Long eno;
	// Event timestamp. (time)
	private Date time;
	// ReaderName assigned during deserialization
	private String readerName;
	// The guest object or null if not found
	private transient Guest guest;
	
	public XbrEvent(){}
	
	public XbrEvent(XbrEventType type) {
		this.type = type;
	}
	public XbrEventType getType() {
		return type;
	}
	public void setType(XbrEventType type) {
		this.type = type;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Long getEno() {
		return eno;
	}
	public void setEno(Long eno) {
		this.eno = eno;
	}
	public String getReaderName() {
		return readerName;
	}
	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}
	
	// Returns the bandId (either xlrid or rfid)
	public abstract String getID();


	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((readerName == null) ? 0 : readerName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XbrEvent other = (XbrEvent) obj;
		if (readerName == null) {
			if (other.readerName != null)
				return false;
		} else if (!readerName.equals(other.readerName))
			return false;
		return true;
	}
}
