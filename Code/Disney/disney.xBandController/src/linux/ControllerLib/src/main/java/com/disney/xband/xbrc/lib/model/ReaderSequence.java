package com.disney.xband.xbrc.lib.model;

public class ReaderSequence {
	private String name;
	private Long timeout;
	
	public ReaderSequence() {		
	}
	
	public ReaderSequence(String name, Long timeout) {
		this.name = name;
		this.timeout = timeout;
	}
	
	public String getName() {
		return name;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
}
