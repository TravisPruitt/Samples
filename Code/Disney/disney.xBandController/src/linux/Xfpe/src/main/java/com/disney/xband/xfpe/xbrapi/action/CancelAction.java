package com.disney.xband.xfpe.xbrapi.action;

import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.model.XfpeReaderLight;
import com.disney.xband.xfpe.model.XfpeReaderSequence;

public class CancelAction extends BaseAction {
	
	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			XfpeController.getInstance().onCommandBiometricCancel(readerId);
			return SUCCESS; 
		}
		catch(Exception e) {
			logger.error("Caught exception in LightAction.", e);
		}
		return ERROR;
	}
}
