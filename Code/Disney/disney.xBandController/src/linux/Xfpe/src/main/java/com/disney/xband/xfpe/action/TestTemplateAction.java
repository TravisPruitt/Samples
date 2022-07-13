package com.disney.xband.xfpe.action;

import com.disney.xband.xfpe.simulate.DB;
import com.disney.xband.xfpe.simulate.PEGuestTest;
import com.opensymphony.xwork2.ActionSupport;

public class TestTemplateAction extends ActionSupport
{
	private Long testId;
	private PEGuestTest test;
	
	@Override
	public String execute() throws Exception
	{		
		test = DB.getPEGuestTest(testId);
		return super.execute();
	}

	public Long getTestId()
	{
		return testId;
	}

	public void setTestId(Long testId)
	{
		this.testId = testId;
	}

	public PEGuestTest getTest()
	{
		return test;
	}
}
