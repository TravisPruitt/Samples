package com.disney.xband.xfpe.xbrapi.action;

import com.disney.xband.xfpe.controller.XfpeController;


public class BioImageSendAction extends BaseAction {
	
	private String RFID;
	private String transactionId;
	private String templateId;	

	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			XfpeController.getInstance().onCommandBiometricImageSend(readerId,RFID, Long.parseLong(transactionId), Long.parseLong(templateId));
			return SUCCESS;
		}
		catch(Exception e) {
			logger.error("Caught exception in BioImageSendAction.", e);
		}
		return ERROR;
	}

	public String getRFID() {
		return RFID;
	}

	public void setRFID(String rFID) {
		RFID = rFID;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
