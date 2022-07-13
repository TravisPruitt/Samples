package com.disney.xband.xbrc.parkentrymodel;

import com.disney.xband.parkgreeter.lib.message.fromxbrc.ErrorCode;

public class CastAppException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private ErrorCode errorCode;
	
	public CastAppException(ErrorCode errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
	}
	
	public CastAppException(ErrorCode errorCode, String message, Exception e)
	{
		super(message,e);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode)
	{
		this.errorCode = errorCode;
	}
}
