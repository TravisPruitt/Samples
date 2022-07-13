package com.disney.xband.xbrc.ui.bean;

public class ReaderCounts {
	private String readerId;
	private Integer count;
		
	public ReaderCounts(String readerId, Integer count) {
		super();
		this.readerId = readerId;
		this.count = count;
	}
	
	public String getReaderId() {
		return readerId;
	}
	
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
}
