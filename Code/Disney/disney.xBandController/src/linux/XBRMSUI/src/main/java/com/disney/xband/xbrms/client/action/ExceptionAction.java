package com.disney.xband.xbrms.client.action;

import javax.servlet.http.HttpServletRequest;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 8/20/12
 * Time: 3:11 PM
 */

public class ExceptionAction extends ActionSupport {
    private static Logger logger = Logger.getLogger(ExceptionAction.class);

    private Exception exception;
    private String errMsg;

	@Override
	public String execute() throws Exception 
	{
		if (isAjaxAction())
		{
			// ajax calls shouldn't redirect on error to an error page, but instead present the error in the same page
			if (exception != null)
				errMsg = "An error has occurred while processing the request: " + exception.toString();
			else if (errMsg == null)
				errMsg = "An error has occurred while processing the request";
			
			return "ajax_error";
		}

		if(exception != null) {
			if(exception instanceof XbrmsServerNotSetException) {
				return "server_unknown";
			}

			if((exception.getMessage() != null) && exception.getMessage().contains("Authorization failed")) {
				this.addActionError("You are not authorized to access the content.");
				return SUCCESS;
			}

			if((exception.getMessage() != null) && exception.getMessage().contains("URI is")) {
				this.addActionError("Invalid xBRMS server URL: " + exception.getMessage());
				return SUCCESS;
			}

			if((exception.getMessage() == null) || (exception.getMessage().contains("A message body"))) {
				this.addActionError("xBRMS server operation failed. Make sure nge.xconnect.xbrms.server.url property is correct and the server is up and running.");
				return SUCCESS;
			}

			if((exception.getMessage() != null) && (exception.getMessage().indexOf("<html><head>") >= 0)) {
				this.addActionError("Problems communicating with xBRMS service. Make sure xBRMS server is healthy.");
			}
			else {
				this.addActionError("An error has occurred when processing the request: " + exception.getMessage());
			}

			if(logger.isTraceEnabled()) {
				logger.trace("An error has occurred when processing the request: " + exception.toString());
			}

			this.addActionMessage("Please, use Back button to navigate to the previous page.");
		} else {
			if(errMsg != null) {
				this.addActionError(errMsg);
				logger.error(errMsg);

				if(errMsg.indexOf("authorized") >= 0) {
					this.addActionMessage("Please, use Back button to navigate to the previous page.");
				}
				else {
					this.addActionMessage("Please, check configuration properties or try again later.");
				}
			}
			else {
				this.addActionError("You are not authorized to access the content or perform the action.");
				logger.error("Not authorized to access " + ServletActionContext.getRequest().getRequestURL().toString());

				this.addActionMessage("Please, use Back button to navigate to the previous page.");
			}
		}

		return super.execute();
	}

    /**
     * Sets exception.
     *
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    
    public String getProductTitle() {
        return XbrmsUtils.getRestCaller().getXbrmsConfig().getName();
	}
    
    protected boolean isAjaxAction() 
    {
    	HttpServletRequest request = ServletActionContext.getRequest();
    	
    	return request.getParameterMap().containsKey("NoAcRedirect") ||
				(request.getHeader("NoAcRedirect") != null &&
					request.getHeader("NoAcRedirect").trim().length() > 0) ||
				(ServletActionContext.getContext().getName() != null &&
				ServletActionContext.getContext().getName().toLowerCase().indexOf("ajax") >= 0);
    }
}
