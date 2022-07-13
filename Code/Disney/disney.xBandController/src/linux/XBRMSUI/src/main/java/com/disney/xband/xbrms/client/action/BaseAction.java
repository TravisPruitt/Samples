package com.disney.xband.xbrms.client.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.disney.xband.xbrms.client.XbrmsAccessHelper;
import com.disney.xband.xbrms.client.XbrmsServerChooser;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.ProblemDto;
import com.disney.xband.xbrms.common.model.ProblemsReportDto;
import com.disney.xband.xbrms.common.model.XbrmsConfigDto;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import com.disney.xband.xbrms.common.model.XbrmsStatusDto;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 9/13/12
 * Time: 11:47 AM
 */
public class BaseAction extends ActionSupport {
    private static Logger logger = Logger.getLogger(BaseAction.class);
    private static String MULTI_HOME_ACTION_NAME = "MultiHomeAction";
	private XbrmsConfigDto ci;
	protected String errMsg;
	
	// Internal server error
	protected static final String STATUS_500 = "500";
	// Bad request, malformed syntax
	protected static final String STATUS_401 = "401";

    @Override
    public String execute() throws Exception {

        return super.execute();
    }

	public BaseAction()
	{
		XbrmsServerChooser.getInstance().clearServerSelection();
	}

	/**
	 * This empty method is here to stop Struts from logging debug messages when validate() is not found.
	 * 
	 * Override as needed.
	 */
	@Override
	public void validate() {}

	public String getProductTitle() {
        try {
            return XbrmsUtils.getRestCaller().getXbrmsConfig().getName();
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (Exception e) {
            return "";
        }
	}

    public boolean isMultiParkConfig() {
        return XbrmsServerChooser.getInstance().isMutiParkConfig();
    }
    
    public boolean canAccessAsset(String assetName)
    {
    	return XbrmsAccessHelper.getInstance().canAccessAsset(assetName, ServletActionContext.getRequest());
    }
    
    public boolean isGlobalXbrms()
    {
    	return XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest());
    }

    public String getChooserHref() {
        final String uiUrl = XbrmsServerChooser.getInstance().getUiRrl();

        return XbrmsUtils.isEmpty(uiUrl) ? BaseAction.MULTI_HOME_ACTION_NAME : uiUrl + "/" + BaseAction.MULTI_HOME_ACTION_NAME;
    }
    
    protected Collection<XbrmsDto> getConfiguredXbrmss() {
        return this.getConfiguredXbrmss(false);
    }

    public String getVersion() {
       return XbrmsUtils.getXbrmsVersion(logger, ServletActionContext.getServletContext());
    }

    public Collection<XbrmsDto> getConfiguredXbrmss(boolean includeGlobal) {
        final Map<String, XbrmsDto> servers = XbrmsServerChooser.getInstance().getConfiguredServers();
        Collection<XbrmsDto> xbrmss = new LinkedList<XbrmsDto>();

        if(servers != null) {
        	for(String key : servers.keySet()) {
                if(!includeGlobal && servers.get(key).isGlobal()) {
                    continue;
                }

                xbrmss.add(servers.get(key));
            }
        }
        
        return xbrmss;
    }
    
    public Map<String, String> getServers() {
        return XbrmsServerChooser.getInstance().getTargetLinks(ServletActionContext.getRequest());
    }
    
    public List<ProblemDto> getProblems() {
    	try
        {
        	XbrmsStatusDto status = XbrmsUtils.getRestCaller().getStatus();
            final ProblemsReportDto issues = status.getRecentProblems();

            if((issues != null) && (issues.getErrors() != null) && (issues.getErrors().size() > 0)) {
            	return new ArrayList<ProblemDto>(issues.getErrors().values());
            }
        }
        catch (Exception e) {}
    	
    	return new ArrayList<ProblemDto>(0);
    }
    
    public String getErrMsg()
    {
    	return this.errMsg;
    }
}
