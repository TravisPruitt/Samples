package com.disney.xband.xfpe.model;

public class XfpeListener {
	private String url;
	private Long intervalMs;
	private Long maxEvents;
	private Long afterEno;
		
	public XfpeListener(String url, Long intervalMs, Long maxEvents,
			Long afterEno) {
		super();
		this.url = url;
		this.intervalMs = intervalMs;
		this.maxEvents = maxEvents;
		this.afterEno = afterEno;
	}
	
	public XfpeListener() {
	}	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getIntervalMs() {
		return intervalMs;
	}
	public void setIntervalMs(Long intervalMs) {
		this.intervalMs = intervalMs;
	}
	public Long getMaxEvents() {
		return maxEvents;
	}
	public void setMaxEvents(Long maxEvents) {
		this.maxEvents = maxEvents;
	}
	public Long getAfterEno() {
		return afterEno;
	}
	public void setAfterEno(Long afterEno) {
		this.afterEno = afterEno;
	}
}
