package com.disney.xband.xi.model;


public class ErrorMessage {
	private String statusMessage;
	private String status = "fail";
	private String errorCode;
	private String src;

    public String getBuster() {
        return buster;
    }

    public void setBuster(String buster) {
        this.buster = buster;
    }

    private String buster = "";
	
	public ErrorMessage(String msg) {
		this.setStatusMessage(msg);
	}
	
	public ErrorMessage(String msg, String src) {
		this.statusMessage = msg;
		this.src = src;
	}

    public ErrorMessage(String msg, String src, String buster) {
		this.statusMessage = msg;
		this.src = src;
        this.buster = buster;
	}

	public void setErrorCode(String errcode) {
		this.errorCode = errcode;
	}
	public String getSrc() {return src;}
	public String getErrorCode() {return errorCode;}
	public String getStatus() { return status; }
	
	public void setStatus(String msg){
		this.status = msg;
	}
	
	public void setSource(String s) {
		this.src = s;
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
}

