package com.disney.xband.xfpe.xbrapi.action;

import com.disney.xband.xfpe.controller.XfpeController;


public class EnrollAction extends BaseAction {
	
	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			XfpeController.getInstance().onCommandBiometricEnroll(readerId);
			return SUCCESS;
		}
		catch(Exception e) {
			logger.error("Caught exception in EnrollAction.", e);
		}
		return ERROR;
	}
}
