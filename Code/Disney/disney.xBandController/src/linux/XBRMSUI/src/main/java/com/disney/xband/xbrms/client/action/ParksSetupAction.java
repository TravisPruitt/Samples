package com.disney.xband.xbrms.client.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

import com.disney.xband.xbrms.client.XbrmsUiServletContextListener;
import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.XbrmsConfigDto;
import com.disney.xband.xbrms.common.model.XbrmsDto;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 3/12/13
 * Time: 4:45 AM
 */
public class ParksSetupAction extends BaseAction {
    private static Logger logger = Logger.getLogger(ParksSetupAction.class);
    private Collection<XbrmsDto> servers;
    private String url;
    private String fqdnHostAlias;
    private String desc;
    private String action;
    private String uiHost;
    private int id;
    private boolean global;
    private boolean valid;
    private String msg;

    @Override
    public String execute() throws Exception {
        if (this.action != null)
        {
            Collection<XbrmsDto> srvs = null;

            if(ConfigProperties.getInstance().getXbrmsServers() !=null) {
               srvs = ConfigProperties.getInstance().getXbrmsServers().values();
            }

            if(XbrmsUtils.isEmpty(this.uiHost)) {
                this.uiHost = ConfigProperties.getInstance().getUiHost();
            }

            if (this.action.equals("delete")) {
                if(srvs != null) {
                    XbrmsDto xbrms = null;

                    for(XbrmsDto dto : srvs) {
                        if(dto.getId() == this.id) {
                            xbrms = dto;
                            break;
                        }
                    }

                    if(xbrms != null) {
                        try {
                            ConfigProperties.getInstance().removeXbrmsServer(xbrms.getUrl());
                        }
                        catch (Exception e) {
                            final String err = "Failed to delete xbrms \"" + xbrms.getDesc() + "\": " + e.getMessage();
                            logger.warn(err);
                            this.addActionError(err);
                        }
                    }
                }
            }
            else {
                if (this.action.equals("add")) {
                    XbrmsDto xbrms = null;

                    try {
                        xbrms = ConfigProperties.getInstance().createXbrmsServer(this.url, this.fqdnHostAlias, this.desc, null, false, -1);
                        ConfigProperties.getInstance().addXbrmsServer(this.url, this.fqdnHostAlias, this.desc, null, global);
                    }
                    catch (Exception e) {
                        final String err = "Failed to add xBRMS service: " + e.getMessage();
                        logger.warn(err);
                        this.addActionError(err);
                    }
                }
                else {
                    if (this.action.equals("save")) {
                        try {
                            ConfigProperties.getInstance().setUiHost(this.uiHost);
                            ConfigProperties.getInstance().writeSettings();
                            XbrmsUiServletContextListener.isAuditInitialized = false;
                            XbrmsUiServletContextListener.serversNotified = false;

                            final Map<String, XbrmsDto> map = ConfigProperties.getInstance().getXbrmsServers();

                            if(map != null) {
                                final List<String> parks = new ArrayList<String>();

                                for(XbrmsDto m : map.values()) {
                                    if(!m.isGlobal()) {
                                        parks.add(m.getUrl());
                                    }
                                }

                                for(XbrmsDto m : map.values()) {
                                    try {
                                        final XbrmsConfigDto conf = XbrmsUtils.getRestCaller(m.getUrl()).getXbrmsConfig();

                                        if(conf == null) {
                                            throw new RuntimeException();
                                        }

                                        conf.setGlobalServer(m.isGlobal());
                                        conf.setParksUrlList(parks);
                                        XbrmsUtils.getRestCaller(m.getUrl()).setXbrmsConfig(conf);
                                    }
                                    catch (Exception e) {
                                        final String err = "Failed to notify xBRMS about its role: " + m.getAddr();
                                        logger.warn(err);
                                        //this.addActionError(err);
                                    }
                                }
                            }
                        }
                        catch (Exception e) {
                            final String err = "Failed to save the parks configuration: " + e.getMessage();
                            logger.warn(err);
                            this.addActionError(err);
                        }
                    }
                }
            }
        }

        try {
            final Map<String, XbrmsDto> srvs = ConfigProperties.getInstance().getXbrmsServers();

            if(XbrmsUtils.isEmpty(this.uiHost)) {
                this.uiHost = ConfigProperties.getInstance().getUiHost();
            }

            ConfigProperties.getInstance().setUiHost(this.uiHost);
            final List<XbrmsDto> res;

            if(srvs != null) {
                res = new ArrayList<XbrmsDto>(srvs.values());
            }
            else {
                res = new ArrayList<XbrmsDto>();
            }

            java.util.Collections.sort(res, new Comparator<XbrmsDto>() {
                @Override
                public int compare(XbrmsDto o1, XbrmsDto o2) {
                    return o1.getDesc().compareTo(o2.getDesc());
                }
            });

            this.servers = res;
        }
        catch (Exception e) {
            final String err = "Failed to retrieve a list of parks from the global server: " + e.getMessage();
            logger.warn(err);
            this.addActionError(err);
        }

        return super.execute();
    }

    public String tryServerUrl() {
        XbrmsDto xbrms = null;

        try {
            String tmp = System.currentTimeMillis() + "";
            xbrms = ConfigProperties.getInstance().createXbrmsServer(this.url, tmp, tmp, null, false, -1);

            if(xbrms == null) {
                throw new Exception("Incorrect server URL");
            }

            ConfigProperties.getInstance().addXbrmsServer(this.url, tmp, tmp, null, global);
        }
        catch (Exception e) {
            this.valid = false;
            this.msg = e.getMessage();
            return SUCCESS;
        }

        try {
            XbrmsUtils.getRestCaller(xbrms.getUrl()).getStatus();
        }
        catch (Exception e) {
            this.valid = false;
            this.msg = "Failed to connect to the specified xBRMS server.";
            return SUCCESS;
        }
        finally {
            ConfigProperties.getInstance().removeXbrmsServer(xbrms.getUrl());
        }

        this.valid = true;
        return SUCCESS;
    }

    public Collection<XbrmsDto> getXbrmsServers() {
        return this.servers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFqdnHostAlias() {
        return fqdnHostAlias;
    }

    public void setFqdnHostAlias(String fqdnHostAlias) {
        this.fqdnHostAlias = fqdnHostAlias;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public String getUiHost() {
        return uiHost;
    }

    public void setUiHost(String uiHost) {
        this.uiHost = uiHost;
    }

    public boolean isChanged() {
        return ConfigProperties.getInstance().isChanged();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}