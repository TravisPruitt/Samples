package com.disney.xband.xfpe.action;

import java.io.File;
import java.util.List;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;
import com.disney.xband.xfpe.simulate.DB;
import com.disney.xband.xfpe.simulate.PEGuestTestSuite;
import com.disney.xband.xfpe.simulate.SimulatorManager;
import com.opensymphony.xwork2.ActionSupport;

public class ManageTestsAction extends ActionSupport {
	private String testsuitename;
	private String action;
	private String result;
	private Long suiteId;
	private List<PEGuestTestSuite> testSuiteList;
	
	private File xmlfile;
	private String xmlfileContentType;
	private String xmlfileFileName;
	
	@BreadCrumb("Manage")
	@Override
	public String execute() throws Exception {
		
		if (action != null) {
			
			if (action.equals("import") && xmlfile != null)
				importCsvFile();
			
			if (action.equals("deletesuite"))
				deleteSuite(suiteId);
		}
		
		testSuiteList = DB.findAllTestSuites();
				
		return super.execute();
	}
	
	@Override
	public void validate() {
		super.validate();
		
		if (action != null && action.equals("import")) {
			if (testsuitename == null || testsuitename.isEmpty()) {
				addFieldError("testsuitename", "Test suite name cannot be empty.");
			}
		}
		
		// Execute will not get called if there were any field errors
		if (getFieldErrors() != null && !getFieldErrors().isEmpty())
			testSuiteList = DB.findAllTestSuites();
	}
	
	private void importCsvFile() {
		try {
			result = SimulatorManager.importCsvFile(null, testsuitename, xmlfile.getAbsolutePath());
		} catch (Exception e) {
			result = e.getLocalizedMessage();
		}
		finally {
			xmlfile.delete();
		}
	}
	
	private void deleteSuite(Long suiteId) {
		try {
			result = SimulatorManager.deleteSuite(suiteId);
		} catch (Exception e) {
			result = e.getLocalizedMessage();
		}
	}

	public String getTestsuitename() {
		return testsuitename;
	}

	public void setTestsuitename(String testsuitename) {
		this.testsuitename = testsuitename;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Long getSuiteId()
	{
		return suiteId;
	}

	public void setSuiteId(Long suiteId)
	{
		this.suiteId = suiteId;
	}

	public List<PEGuestTestSuite> getTestSuiteList()
	{
		return testSuiteList;
	}
	
	public File getXmlfile()
	{
		return xmlfile;
	}

	public void setXmlfile(File xmlfile)
	{
		this.xmlfile = xmlfile;
	}

	public String getXmlfileContentType()
	{
		return xmlfileContentType;
	}

	public void setXmlfileContentType(String xmlfileContentType)
	{
		this.xmlfileContentType = xmlfileContentType;
	}

	public String getXmlfileFileName()
	{
		return xmlfileFileName;
	}

	public void setXmlfileFileName(String xmlfileFileName)
	{
		this.xmlfileFileName = xmlfileFileName;
	}
}
