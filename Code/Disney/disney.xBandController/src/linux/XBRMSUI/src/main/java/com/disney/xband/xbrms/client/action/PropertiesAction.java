package com.disney.xband.xbrms.client.action;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEvent.Type;
import com.disney.xband.xbrms.client.XbrmsServerChooser;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.model.EnvPropertiesMapDto;
import com.disney.xband.xbrms.common.model.XbrmsConfigDto;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.ServletActionContext;

public class PropertiesAction extends BaseAction implements Preparable {

    private static Logger logger = Logger.getLogger(PropertiesAction.class);
    private XbrmsConfigDto xbrmsConfig;
    private AuditConfig auditConfig;
    private boolean auditConfigEnabled;
    private Map<Integer, String> auditConfigLevels;
    private int auditConfigLevel;
    
    // environment.properties properties, not editable
    private String jmsBroker;
    private String jmsUser;
    private String parkId;

    @Override
    public void prepare() throws Exception
    {
    	auditConfigLevels = new LinkedHashMap<Integer, String>();
    	for (Type auditLevel : AuditEvent.Type.values())
    	{
    		auditConfigLevels.put(new Integer(auditLevel.ordinal()), auditLevel.name());
    	}
    }
    
    @Override
    public String execute() throws Exception 
    {
        try {
            this.xbrmsConfig = XbrmsUtils.getRestCaller().getXbrmsConfig();
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch(Exception e)
        {
            final String errorMessage = "Failed to retrieve xBRMS configuration." + e.getMessage();
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }
        }
        
        try {
            this.auditConfig = XbrmsUtils.getRestCaller().getAuditConfig();
            
            auditConfigEnabled = this.auditConfig.isEnabled();
            auditConfigLevel = this.auditConfig.getLevel().ordinal();
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch(Exception e)
        {
            final String errorMessage = "Failed to retrieve Audit configuration." + e.getMessage();
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }
        }
        
        return initEnvironmentProperties();
    }

    public String update() throws Exception {

        if(this.xbrmsConfig == null) {
            ServletActionContext.getResponse().sendRedirect("properties.action");
            return SUCCESS;
        }
        
        this.auditConfig.setEnabled(this.auditConfigEnabled);
        this.auditConfig.setLevel(AuditEvent.Type.getByOrdinal(this.auditConfigLevel));
        
        try {
            final XbrmsConfigDto curXbrmsConfig = XbrmsUtils.getRestCaller().getXbrmsConfig();
            this.xbrmsConfig.setGlobalServer(curXbrmsConfig.isGlobalServer());
            XbrmsUtils.getRestCaller().setXbrmsConfig(this.xbrmsConfig);
            XbrmsUtils.getRestCaller().setAuditConfig(this.auditConfig);
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (Exception e) {
            final String errorMessage = "Failed to persist configuration properties." + e.getMessage();
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }
        }
        
        return initEnvironmentProperties();
    }
    
    private String initEnvironmentProperties()
    {
    	try
    	{
    		IRestCall caller = XbrmsUtils.getRestCaller();
    		EnvPropertiesMapDto properties = caller.getServerProps();
					
	    	// environment.properties properties
	        jmsBroker = properties.getMap().get("nge.eventserver.mgmtBrokerUrl");
	        jmsUser = properties.getMap().get("nge.eventserver.xbrc.uid");
	        parkId = properties.getMap().get("nge.xconnect.parkid");
	        
	        return SUCCESS;
    	}
    	catch (HTTPException e)
    	{
    		this.errMsg = e.getCause() != null ? "HTTP status code: " + e.getStatusCode() : "unknown";
    		this.addActionError(this.errMsg);
    		
			String errMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : "unknown";
			
			if (logger.isDebugEnabled())
				logger.error("Failed to retreive environment properties from the server"
						+ ". Cause: " + errMessage, e);
			else
				logger.error("Failed to retreive environment properties from the server"
						+ ". Cause: " + errMessage);
			
			return ERROR;
    	}
    }

    /**
     * @return the xbrmsConfig
     */
    public XbrmsConfigDto getXbrmsConfig() {
        return this.xbrmsConfig;
    }

    public void setXbrmsConfig(XbrmsConfigDto msConfig)
    {
        this.xbrmsConfig = msConfig;
    }

    public AuditConfig getAuditConfig()
	{
		return auditConfig;
	}

	public void setAuditConfig(AuditConfig auditConfig)
	{
		this.auditConfig = auditConfig;
	}

	public String getJmsBroker() {
        return jmsBroker;
    }

    public String getJmsUser() {
        return jmsUser;
    }
    
    public String getParkId() {
    	return parkId;
    }

	public boolean getAuditConfigEnabled()
	{
		return auditConfigEnabled;
	}

	public void setAuditConfigEnabled(boolean auditConfigEnabled)
	{
		this.auditConfigEnabled = auditConfigEnabled;
	}
	
	public Map<Integer, String> getAuditConfigLevels()
	{
		return auditConfigLevels;
	}

	public int getAuditConfigLevel()
	{
		return auditConfigLevel;
	}

	public void setAuditConfigLevel(int auditConfigLevel)
	{
		this.auditConfigLevel = auditConfigLevel;
	}
}
