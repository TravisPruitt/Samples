package com.disney.xband.xbrms.client.action;

import java.util.Collection;
import java.util.Map;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItem;
import org.apache.log4j.Logger;

public class XbrcHealthAjaxAction extends BaseAction {

	private Map<String,Collection<HealthItem>> inventory;
    private static Logger logger = Logger.getLogger(XbrcHealthAjaxAction.class);

	@Override
	public String execute() throws Exception {
		
        try {
            this.inventory = HealthItem.fromDtoMap(XbrmsUtils.getRestCaller().getHealthItemsInventory().getMap());
        }
        catch(Exception e) {
            final String errorMessage = "Failed to retrieve health items inventory";

            if (logger.isDebugEnabled()) {
                logger.warn(errorMessage, e);
            }
            else {
                logger.warn(errorMessage);
            }
        }
		
		return super.execute();
	}
	
	public Map<String,Collection<HealthItem>> getInventory() {
		return inventory;
	}
}
