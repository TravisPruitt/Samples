package com.disney.xband.xfpe.xbrapi.action;

public class MatchAction extends BaseAction {

	@Override
	public String execute() throws Exception {
		try {
			// TODO: handle this command
			super.execute();
			return SUCCESS;
		}
		catch(Exception e) {
			logger.error("Caught exception in MatchAction.", e);
		}
		return ERROR;
	}
}
