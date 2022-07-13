package com.disney.xband.xbrms.client.action;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

public class ReaderHealthRefreshAction extends BaseAction {
    private static Logger logger = Logger.getLogger(ReaderHealthAction.class);

	@Override
	public String execute() throws Exception {
        try {
            XbrmsUtils.getRestCaller().refreshReadersHealthStatus();
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (Exception e) {
            final String errorMessage = "Failed to refresh readers health status.";

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }
        }

		return super.execute();
	}
}
