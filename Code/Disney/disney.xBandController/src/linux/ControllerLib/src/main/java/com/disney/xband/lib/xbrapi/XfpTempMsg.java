package com.disney.xband.lib.xbrapi;

public class XfpTempMsg {

	/** Reader current internal temperature "now" **/
	private Double now;
	/** Reader maximum recorded temperature "max" **/
	private Double max;

	public Double getNow() {
		return now;
	}

	public void setNow(Double temperature) {
		this.now = temperature;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

}
