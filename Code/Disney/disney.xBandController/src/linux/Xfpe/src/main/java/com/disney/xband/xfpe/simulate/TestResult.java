package com.disney.xband.xfpe.simulate;

public class TestResult
{
	private PEGuestTest test;
	private String actualResult;
	
	public PEGuestTest getTest()
	{
		return test;
	}
	public void setTest(PEGuestTest test)
	{
		this.test = test;
	}
	public String getActualResult()
	{
		return actualResult;
	}
	public void setActualResult(String actualResult)
	{
		this.actualResult = actualResult;
	}
	public boolean isSuccess()
	{
		return test != null && actualResult != null &&
				test.getFinalResult().toString().equals(actualResult);
	}
}
