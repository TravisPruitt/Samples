package com.disney.xband.xfpe.xbrapi.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.xfpe.RestfulRequest;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport {
	protected static Logger logger = Logger.getLogger(BaseAction.class);
	protected String readerId;
	protected String requestBody;
	static protected int readerIdIndex = "/Xfpe/restful/".length();

	@Override
	public String execute() throws Exception {
				
		// Parse the reader id from a request that looks like /Xfpe/restful/<reader id>/....
		String uri = ServletActionContext.getRequest().getRequestURI();
		int e = uri.indexOf('/', readerIdIndex);
		readerId = uri.substring(readerIdIndex, e);
		
		// Set the request body.
		HttpServletRequest req = ServletActionContext.getRequest();
		if (req instanceof StrutsRequestWrapper) {
			StrutsRequestWrapper wrapper = (StrutsRequestWrapper)req;
			if (wrapper.getRequest() instanceof RestfulRequest)
				requestBody = ((RestfulRequest)wrapper.getRequest()).getRequestBody();
		}
		
		return super.execute();
	}
	
	public String getReaderId() {
		return readerId;
	}

	public String getRequestBody() {
		return requestBody;
	}
}
