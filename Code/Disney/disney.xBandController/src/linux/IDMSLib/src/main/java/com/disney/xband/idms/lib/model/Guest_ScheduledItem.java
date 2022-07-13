package com.disney.xband.idms.lib.model;

public class Guest_ScheduledItem
{

	private long guestScheduledItemId;
	private long guestId;
	private long scheduledItemId;
	private boolean isOwner;
	
	public long getGuestScheduledItemId() {
		return guestScheduledItemId;
	}
	public void setGuestScheduledItemId(long guestScheduledItemId) {
		this.guestScheduledItemId = guestScheduledItemId;
	}
	public long getGuestId() {
		return guestId;
	}
	public void setGuestId(long guestId) {
		this.guestId = guestId;
	}
	public long getScheduledItemId() {
		return scheduledItemId;
	}
	public void setScheduledItemId(long scheduledItemId) {
		this.scheduledItemId = scheduledItemId;
	}
	public boolean isOwner() {
		return isOwner;
	}
	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	
	
	
}
