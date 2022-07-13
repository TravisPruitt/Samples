package com.disney.xband.xbrms.client.action;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.IdmsDto;

public class IdmsAction extends BaseAction {

    private HealthItem idms;
    private Long id;
    private int tabSelected;
    private String databaseURL = "";
    private String databaseUserName = "";
    private String remoteSettings = "";
    private String ip = "";
    private Integer port = 0;

    private static Logger logger = Logger.getLogger(IdmsAction.class);

    @Override
    public String execute() throws Exception {

        if (id == null)
            this.addActionError(this.getText("idms.id.missing"));

        HealthItemDto item = null;

        try {
            item = XbrmsUtils.getRestCaller().getHealthItemById("" + id);
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
        }

        if (item == null || !(item instanceof IdmsDto)) {
            this.addActionMessage(this.getText("idms.not.found", new String[]{ id.toString() }));

        }

        ip = item.getIp();
        port = item.getPort();
        idms = new HealthItem(item);

        return super.execute();
    }

    public String getDatabaseUserName() {
        return databaseUserName;
    }

    public void setDatabaseUserName(String databaseUserName) {
        this.databaseUserName = databaseUserName;
    }


    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public HealthItem getIdms() {
        return idms;
    }

    public void setIdms(HealthItem idms) {
        this.idms = idms;
    }

    public int getTabSelected() {
        return tabSelected;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getRemoteSettings() {
        return this.remoteSettings;
    }

    public void setRemoteSettings(String remoteSettings) {
        this.remoteSettings = remoteSettings;
    }
}
