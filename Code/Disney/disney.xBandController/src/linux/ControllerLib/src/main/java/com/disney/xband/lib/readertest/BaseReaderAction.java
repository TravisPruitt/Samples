package com.disney.xband.lib.readertest;

import java.util.Properties;

import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public abstract class BaseReaderAction {
	protected String name;
	protected Properties prop;
	protected ReaderInfo ri;
	protected HttpMethod method;
	protected Long waitMs;	
	
	public void initialize(Properties prop, ReaderInfo ri) throws Exception {
		this.prop = prop;
		this.ri = ri;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public HttpMethod getMethod() {
		return method;
	}
	
	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public Long getWaitMs() {
		return waitMs;
	}

	public void setWaitMs(Long waitMs) {
		this.waitMs = waitMs;
	}
	
	public void performAction() throws Exception {
		
		String url = ri.getURL() + "/" + getPath();			
		
		switch(getMethod()) {
		case GET: {
			StringBuffer dataReceived = new StringBuffer();
			ReaderApi.sendGet(url, dataReceived);
			}
			break;
		case PUT:			
			ReaderApi.sendPut(url, getData() == null ? "" : new String(getData()));
			break;
		case POST:
			ReaderApi.sendPost(url, getData() == null ? "" : new String(getData()));
			break;
		case DELETE:
			throw new Exception("The DELETE action is not implemented");
		}
	}
	
	public abstract String getPath();
	public void setPath(String path){};
	public abstract byte[] getData();
}
