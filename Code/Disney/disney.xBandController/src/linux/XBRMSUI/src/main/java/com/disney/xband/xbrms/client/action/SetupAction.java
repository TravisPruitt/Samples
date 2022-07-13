package com.disney.xband.xbrms.client.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.XbrmsStatusDto;
import com.opensymphony.xwork2.Preparable;

public class SetupAction extends BaseAction implements Preparable {

    private String adminDbUser;
    private String adminDbPassword;
    private String xbrmsDbUser;
    private String xbrmsDbPassword;
    private String xbrmsDbUrl;

    private static Logger logger = Logger.getLogger(SetupAction.class);

    @Override
    public void prepare() throws Exception 
    {
        final Map<String, String> props = XbrmsUtils.getRestCaller().getServerProps().getMap();

        if (props != null) {
            xbrmsDbUser = ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_USER);
            xbrmsDbPassword = ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_PASS);

            if (xbrmsDbPassword != null) {
                xbrmsDbPassword.replaceAll(".", "*");    // hide the password
            }

            this.xbrmsDbUrl = ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_URL);
        }
    }

    @Override
    public String execute() throws Exception {

        switch (XbrmsUtils.getRestCaller().getStatus().getDbStatus()) {
            case Red:
                this.addActionError(XbrmsUtils.getRestCaller().getStatus().getDbStatusMessage());
                return INPUT;
            case Yellow:
                this.addActionMessage(XbrmsUtils.getRestCaller().getStatus().getDbStatusMessage());
                return INPUT;
        }

        switch (XbrmsUtils.getRestCaller().getStatus().getStatus()) {
            case Red:
                this.addActionError(XbrmsUtils.getRestCaller().getStatus().getStatusMessage());
                return INPUT;
            case Yellow:
                this.addActionMessage(XbrmsUtils.getRestCaller().getStatus().getStatusMessage());
                return INPUT;
        }

        return super.execute();
    }

    public String getAdminDbUser() {
        return adminDbUser;
    }

    public void setAdminDbUser(String adminDbUser) {
        this.adminDbUser = adminDbUser;
    }

    public String getAdminDbPassword() {
        return adminDbPassword;
    }

    public void setAdminDbPassword(String adminDbPassword) {
        this.adminDbPassword = adminDbPassword;
    }

    public String getXbrmsDbUser() {
        return xbrmsDbUser;
    }

    public void setXbrmsDbUser(String xbrmsDbUser) {
        this.xbrmsDbUser = xbrmsDbUser;
    }

    public String getXbrmsDbPassword() {
        return xbrmsDbPassword;
    }

    public void setXbrmsDbPassword(String xbrmsDbPassword) {
        this.xbrmsDbPassword = xbrmsDbPassword;
    }

    public XbrmsStatusDto getStatus() {
        return XbrmsUtils.getRestCaller().getStatus();
    }

    public String getXbrmsDbUrl() {
        return xbrmsDbUrl;
    }

    public void setXbrmsDbUrl(String xbrmsDbUrl) {
        this.xbrmsDbUrl = xbrmsDbUrl;
    }
}
