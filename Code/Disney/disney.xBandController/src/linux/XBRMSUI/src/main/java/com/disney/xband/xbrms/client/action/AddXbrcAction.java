package com.disney.xband.xbrms.client.action;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

public class AddXbrcAction extends BaseAction {
	public String message;
	public String xbrcIp;
	public String xbrcPort;
	public String itemClassName;

    private static Logger logger = Logger.getLogger(AddXbrcAction.class);

	@Override
	public String execute() throws Exception {
        try {
            XbrmsUtils.getRestCaller().addHealthItem(this.xbrcIp, this.xbrcPort, this.itemClassName);

            return super.execute();
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch(Exception e)
        {
            this.message = e.getMessage();

            if (logger.isDebugEnabled()) {
                logger.error(e.getMessage());
            }
        }

        return SUCCESS;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isClose() {
		return message == null || message.isEmpty();
	}

	public String getXbrcIp() {
		return xbrcIp;
	}

	public void setXbrcIp(String xbrcIp) {
		this.xbrcIp = xbrcIp;
	}

	public String getXbrcPort() {
		return xbrcPort;
	}

	public void setXbrcPort(String xbrcPort) {
		this.xbrcPort = xbrcPort;
	}

	public String getItemClassName()
	{
		return itemClassName;
	}

	public void setItemClassName(String itemClassName)
	{
		this.itemClassName = itemClassName;
	}
}
