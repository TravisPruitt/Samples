package com.disney.xband.lib.xbrapi;

public class TalkPayload {
	private Integer range;
	private Integer rate;
	private Integer gap;
	private Integer volume;
	private String text;

	public Integer getRange() {
		return range;
	}
	public Integer getRate() {
		return rate;
	}
	public Integer getGap() {
		return gap;
	}
	public Integer getVolume() {
		return volume;
	}
	public String getText() {
		return text;
	}
	public void setRange(Integer range) {
		this.range = range;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	public void setGap(Integer gap) {
		this.gap = gap;
	}
	public void setVolume(Integer volume) {
		this.volume = volume;
	}
	public void setText(String text) {
		this.text = text;
	}
}
