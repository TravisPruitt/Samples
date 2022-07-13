package com.disney.xband.xbrc.ui.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 8/20/12
 * Time: 3:11 PM
 */

public class ExceptionAction extends BaseAction {
    private static Logger logger = Logger.getLogger(ExceptionAction.class);

    private Exception exception;
    private String errMsg;

	@Override
	public String execute() throws Exception {
		
		try {
			super.execute();
		} catch (Exception e) {
			// log the exception, but continue our processing.
			logger.error("Caught exception in the ExceptionAction. No, we did not really expect this to happen.", e);
		}
		
        if(exception != null) {
            this.addActionError("An error has occurred when processing the request: " + exception.getMessage());

            logger.trace("An error has occurred when processing the request: ", exception);           

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
                this.addActionError("You are not authorized to access the content.");
                logger.error("Not authorized to access " + ServletActionContext.getRequest().getRequestURL().toString());

                this.addActionMessage("Please, use Back button to navigate to the previous page.");
            }
        }

		return SUCCESS;
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
}
