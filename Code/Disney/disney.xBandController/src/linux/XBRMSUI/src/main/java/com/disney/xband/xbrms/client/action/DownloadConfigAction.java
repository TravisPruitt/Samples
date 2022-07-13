package com.disney.xband.xbrms.client.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

public class DownloadConfigAction extends BaseAction
{
	private InputStream inputStream;
	private String id;
	private Logger logger = Logger.getLogger(DownloadConfigAction.class);
	
	@Override
	public String execute() throws Exception
	{
		try
		{
            final XbrcConfiguration conf = XbrmsUtils.getRestCaller().getXbrcStoredConfig(this.id);
			inputStream = new ByteArrayInputStream(conf.getXml().getBytes());
            return super.execute();
		}
		catch(Exception e)
		{
            final String errorMessage = "Failed to read the XbrcConfiguration record with id=" + id + ": " + e.getMessage();
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }

            return INPUT;
		}
	}

	public InputStream getInputStream()
	{
		return inputStream;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
