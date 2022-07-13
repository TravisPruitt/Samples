package com.disney.xband.xbrms.server.messaging;

public class FutureResult<T>
{
	private boolean success;
	private String errorMessage;
	private T result;
	
	public FutureResult(boolean success, String errorMessage, T result)
	{
		this.success = success;
		this.errorMessage = errorMessage;
		this.result = result;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public T getResult()
	{
		return result;
	}
}
