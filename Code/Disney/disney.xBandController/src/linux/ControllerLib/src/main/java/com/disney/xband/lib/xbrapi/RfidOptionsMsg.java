package com.disney.xband.lib.xbrapi;

public class RfidOptionsMsg {
	
	private boolean testLoop = false;
	private boolean secureId = false;

	public boolean isTestLoop()
	{
		return testLoop;
	}

	public void setTestLoop(boolean testLoop)
	{
		this.testLoop = testLoop;
	}

	public boolean isSecureId()
	{
		return secureId;
	}

	public void setSecureId(boolean secureId)
	{
		this.secureId = secureId;
	}
}
