package com.disney.xband.xbrms.client.model;

import java.text.SimpleDateFormat;

import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrms.common.XbrmsUtils;

public class XbrcConfigurationDpo implements IPresentationObject
{
	private XbrcConfiguration conf;
	private static String dateFormat = "d MMM yyyy HH:mm:ss";
	
	public XbrcConfigurationDpo(XbrcConfiguration conf)
	{
		this.conf = conf;	
	}
	
	private String format(Long date){
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(date);
	}
	
	public String getCreateTime()
	{
		return format(conf.getCreateTime().getTime());
	}

	public XbrcConfiguration getConf()
	{
		return conf;
	}

	public void setConf(XbrcConfiguration conf)
	{
		this.conf = conf;
	}
	
	public String getModel()
	{
		try
		{
			return conf.getModel().substring("com.disney.xband.xbrc.".length(), conf.getModel().length() - ".CEP".length());
		}
		catch(Exception e)
		{
			return conf.getModel();
		}
	}
	
	public String getDescription()
	{
		return XbrmsUtils.escapeHTML(conf.getDescription());
	}
	
	public String getName()
	{
		return XbrmsUtils.escapeHTML(conf.getName());
	}
	
	public String getVenueName()
	{
		return XbrmsUtils.escapeHTML(conf.getVenueName());
	}
}
