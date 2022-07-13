package com.disney.xband.xbrms.client.action;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrms.common.XbrmsUtils;

public class GetXbrcConfigAction extends BaseAction
{
	private String id;
	private XbrcConfiguration conf;
	private String error;
	
	@Override
	public String execute() throws Exception
	{
		try {
			conf = XbrmsUtils.getRestCaller().getXbrcConfig(this.id, "current");
		}
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
		catch(Exception e)
		{
			LOG.warn(e.getMessage());
			error = e.getMessage();
			return INPUT;
		}
		
		return super.execute();
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getError()
	{
		return error;
	}

	public XbrcConfiguration getConf()
	{
		return conf;
	}

	public void setConf(XbrcConfiguration conf)
	{
		this.conf = conf;
	}
}
