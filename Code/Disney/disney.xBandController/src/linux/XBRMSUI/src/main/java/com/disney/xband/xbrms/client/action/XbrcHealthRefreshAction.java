package com.disney.xband.xbrms.client.action;

import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

public class XbrcHealthRefreshAction extends BaseAction {
    private static Logger logger = Logger.getLogger(XbrcHealthRefreshAction.class);

	@Override
	public String execute() throws Exception {
        try {
            XbrmsUtils.getRestCaller().refreshHealthItemsStatus();
        }
        catch(Exception e) {
            final String errorMessage = "Failed to refresh health items status: " + e.getMessage();
            this.addActionError(errorMessage);

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
