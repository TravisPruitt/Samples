package com.disney.xband.lib.xbrapi;

public class ShutdownEvent extends XbrEvent {

	private String timeout;
		
	@Override
	public String getID() {
		// There is no id for this event except the time when it occurred.
		return new Long(super.getTime().getTime()).toString();
	}

	public ShutdownEvent() {
		super(XbrEventType.Shutdown);
	}
	
	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}
