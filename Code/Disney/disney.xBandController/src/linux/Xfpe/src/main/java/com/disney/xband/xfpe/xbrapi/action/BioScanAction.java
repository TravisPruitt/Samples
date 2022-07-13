package com.disney.xband.xfpe.xbrapi.action;

import com.disney.xband.common.lib.JsonUtil;
import com.disney.xband.xfpe.bean.BioScanPayload;
import com.disney.xband.xfpe.controller.XfpeController;

public class BioScanAction extends BaseAction {
	private String template;	
	private String error;
	
	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			if (template == null && requestBody != null && !requestBody.isEmpty())
			{
				BioScanPayload payload = JsonUtil.convertToPojo(requestBody, BioScanPayload.class);
				template = payload.getXbioTemplate();
			}
			XfpeController.getInstance().onCommandSimulateBioScan(readerId, template);
			return SUCCESS; 
		}
		catch(Exception e) {
			logger.error("Caught exception in BioScanAction.", e);
			error = e.toString();
			return INPUT;
		}
	}
	
	public String getTemplate()
	{
		return template;
	}

	public void setTemplate(String template)
	{
		this.template = template;
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
