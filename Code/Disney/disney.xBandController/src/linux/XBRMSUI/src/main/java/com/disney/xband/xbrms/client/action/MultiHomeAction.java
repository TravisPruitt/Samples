package com.disney.xband.xbrms.client.action;

import com.disney.xband.xbrms.client.XbrmsServerChooser;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/29/13
 * Time: 6:20 PM
 */
public class MultiHomeAction extends BaseAction {
    private static Logger logger = Logger.getLogger(MultiHomeAction.class);
    protected Map<String, String> servers;

    @Override
    public String execute() throws Exception 
    {
        try {
            this.servers = XbrmsServerChooser.getInstance().getTargetLinks(ServletActionContext.getRequest());
        }
        catch(Exception ignore) {}

        return SUCCESS;
    }

    @Override
    public String getProductTitle() {
        return "";
    }
}
