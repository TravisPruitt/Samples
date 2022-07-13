package com.disney.xband.xfpe.action;

import java.util.List;

import com.disney.xband.xfpe.simulate.DB;
import com.disney.xband.xfpe.simulate.PEGuestTest;
import com.disney.xband.xfpe.simulate.PEGuestTestSuite;
import com.disney.xband.xfpe.simulate.Simulator;
import com.disney.xband.xfpe.simulate.TestResult;
import com.opensymphony.xwork2.ActionSupport;

public class TestSuiteAction extends ActionSupport
{
	private Long suiteId;
	private PEGuestTestSuite testSuite;
	private List<PEGuestTest> testList;
	
	@Override
	public String execute() throws Exception
	{		
		testSuite = DB.getPEGuestTestSuite(suiteId);
		testList = DB.findAllTests(suiteId);
		
		return super.execute();
	}
	
	public int getTestCount() {
		if (testList == null)
			return 0;
		
		return testList.size();
	}

	public Long getSuiteId()
	{
		return suiteId;
	}

	public void setSuiteId(Long suiteId)
	{
		this.suiteId = suiteId;
	}

	public PEGuestTestSuite getTestSuite()
	{
		return testSuite;
	}

	public void setTestSuite(PEGuestTestSuite testSuite)
	{
		this.testSuite = testSuite;
	}

	public List<PEGuestTest> getTestList()
	{
		return testList;
	}

	public void setTestList(List<PEGuestTest> testList)
	{
		this.testList = testList;
	}
}
