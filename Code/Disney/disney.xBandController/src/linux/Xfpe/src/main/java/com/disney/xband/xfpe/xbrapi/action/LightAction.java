package com.disney.xband.xfpe.xbrapi.action;

import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.model.XfpeReaderLight;
import com.disney.xband.xfpe.model.XfpeReaderSequence;

public class LightAction extends BaseAction {
	private String color;
	private String sequence;
	private String timeout;
	
	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			//LightMsg msg = XbrJsonMapper.mapLightMsg(requestBody);
			Long lTimeout = 0l;
			if (timeout != null)
				lTimeout = Long.parseLong(timeout);
			if (color != null)
				XfpeController.getInstance().onCommandChangeLight(readerId, XfpeReaderLight.valueOf(color), lTimeout);
			else if (sequence != null)
				XfpeController.getInstance().onCommandPlaySequence(readerId, XfpeReaderSequence.valueOf(sequence), lTimeout);
			return SUCCESS; 
		}
		catch(Exception e) {
			logger.error("Caught exception in LightAction.", e);
		}
		return ERROR;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}
	
	public String getSequence()
	{
		return sequence;
	}

	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}
	
	public void setName(String name)
	{
		this.sequence = name;
	}

	public String getTimeout()
	{
		return timeout;
	}

	public void setTimeout(String timeout)
	{
		this.timeout = timeout;
	}
}
