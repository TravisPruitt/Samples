package com.disney.xband.xbrms.client.action;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

public class DeployXbrcConfigAction extends BaseAction
{
	public String xbrcId;
	public String configId;
	public String error;
	
	private static Logger logger = Logger.getLogger(DeployXbrcConfigAction.class);
	
	@Override
	public String execute() throws Exception
	{
        try
        {
            XbrmsUtils.getRestCaller().deployXbrcConfig(this.configId, this.xbrcId);

            return super.execute();
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch(Exception e)
        {
            final String errorMessage = "Failed to deploy a stored configuration record. " + e.getMessage();
            this.addActionError(errorMessage);

            setError(errorMessage);
            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }

            return INPUT;
        }
	}

	public String getXbrcId()
	{
		return xbrcId;
	}

	public void setXbrcId(String xbrcId)
	{
		this.xbrcId = xbrcId;
	}

	public String getConfigId()
	{
		return configId;
	}

	public void setConfigId(String configId)
	{
		this.configId = configId;
	}

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}
}
