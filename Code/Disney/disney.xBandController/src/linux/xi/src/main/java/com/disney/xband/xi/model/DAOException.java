package com.disney.xband.xi.model;

public class DAOException extends Exception
{

	private static final long serialVersionUID = -6029693214546247442L;

	private String context;
	private String friendlyMessage;

	public DAOException(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DAOException(Throwable arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DAOException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
		this.setFriendlyMessage(arg0);
		// TODO Auto-generated constructor stub
	}

	public String getContext()
	{
		return this.context;
	}

	public void setContext(String context)
	{
		this.context = context;

	}

	public String getFriendlyMessage()
	{
		return friendlyMessage;
	}

	public void setFriendlyMessage(String friendlyMessage)
	{
		this.friendlyMessage = friendlyMessage;
	}

}
