package com.disney.xband.xfpe.action;

import java.util.List;

import com.disney.xband.xfpe.simulate.PEGuestTestSuite;
import com.disney.xband.xfpe.simulate.Simulator;
import com.disney.xband.xfpe.simulate.TestResult;
import com.opensymphony.xwork2.ActionSupport;

public class TestResultsAction extends ActionSupport
{
	Integer failedCount = null;
	
	@Override
	public String execute() throws Exception
	{		
		return super.execute();
	}
	
	public PEGuestTestSuite getLastTestSuite()
	{
		return Simulator.getInstance().getLastSuite();
	}
	
	public List<TestResult> getLastTestResults()
	{
		return Simulator.getInstance().getLastSuiteResults();
	}
	
	public boolean isSuccess() {
		return getTestCount() > 0 && getFailedCount() == 0;
	}
	
	public int getTestCount() {
		if (Simulator.getInstance().getLastSuiteResults() == null)
			return 0;
		
		return Simulator.getInstance().getLastSuiteResults().size();
	}
	
	public int getFailedCount() {
		if (failedCount != null)
			return failedCount;
		failedCount = 0;
		for (TestResult tr : Simulator.getInstance().getLastSuiteResults()) {
			if (!tr.isSuccess())
				failedCount++;
		}
		return failedCount;
	}
	
	public int getSuccessCount() {
		return getTestCount() - getFailedCount();
	}
}
