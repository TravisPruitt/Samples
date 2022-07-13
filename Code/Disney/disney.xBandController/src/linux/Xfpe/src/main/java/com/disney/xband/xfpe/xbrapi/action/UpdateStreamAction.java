package com.disney.xband.xfpe.xbrapi.action;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.xfpe.Utils;
import com.disney.xband.xfpe.controller.XfpeController;

public class UpdateStreamAction extends BaseAction {
	private String interval;
	private String max;
	private String after;
	private List<String> urls = new LinkedList<String>();
	
	private static Logger logger = Logger.getLogger(UpdateStreamAction.class);
			
	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			
			// These get a bit annoying since they happen all the time.
			//logger.trace("Received update_stream message: url=" + url + 
			//		", interval=" + interval +
			//		", max=" + max +
			//		", after=" + after );
			
			if (urls.isEmpty()) {
				XfpeController.getInstance().onCommandSetUpdateStream(
					readerId,
					null,
					null,
					null,
					null);
			}
			else {
				XfpeController.getInstance().onCommandSetUpdateStream(
					readerId,
					urls,
					Utils.parseLong(interval), 
					Utils.parseLong(max), 
					Utils.parseLong(after));
			}
	
			return SUCCESS; 
		}
		catch(Exception e) {
			logger.error("Caught exception in UpdateStreamAction.", e);
		}
		return ERROR;
	}
		
	public void setUrl(String url) {
		urls.add(url);
	}
	public void setUrl1(String url) {
		urls.add(url);
	}
	public void setUrl2(String url) {
		urls.add(url);
	}
	public void setUrl3(String url) {
		urls.add(url);
	}
	public void setUrl4(String url) {
		urls.add(url);
	}	
	public void setUrl5(String url) {
		urls.add(url);
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getAfter() {
		return after;
	}
	public void setAfter(String after) {
		this.after = after;
	}
}
