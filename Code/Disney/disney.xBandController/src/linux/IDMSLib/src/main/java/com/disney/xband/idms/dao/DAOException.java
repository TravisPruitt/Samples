package com.disney.xband.idms.dao;

public class DAOException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4634185891686576872L;
	
	public DAOException(Throwable throwable)
	{
		super(throwable);
	}
	
	public DAOException(String message)
	{
		super(message);
	}
	
	public DAOException(String message, Throwable throwable)
	{
		super(message,throwable);
	}
}
