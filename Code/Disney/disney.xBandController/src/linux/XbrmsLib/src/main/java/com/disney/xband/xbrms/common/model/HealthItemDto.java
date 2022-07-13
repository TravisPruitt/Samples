package com.disney.xband.xbrms.common.model;

import com.disney.xband.xbrms.common.PkConstants;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

@XmlSeeAlso({XbrcDto.class, IdmsDto.class, JmsListenerDto.class})
public abstract class HealthItemDto implements IDataTransferObject
{
    protected Long id;
    // IP address of the XBRC
    protected String ip;
    // Host name
    protected String hostname;
    // Port xbrc listens on
    protected Integer port;
    // Version
    protected String version;
    // Last discovery mesasge
    protected Date lastDiscovery;
    // Next expected discovery message
    protected Date nextDiscovery;
    /*
      * Active health items are displayed on the health page. Inactive health items
      * are kept in the database because of historical data tied to them:
      * ex: performance metrics data
      */
    protected Boolean active;
    // Virtual ip
    protected String vip;
    // Virtual port
    protected Integer vport;
    // System status + status message
    protected HealthStatusDto status;
    protected String name;
    protected String type;

    protected boolean isAuditEnabled;

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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
    	if (hostname != null)
    		this.hostname = hostname.toLowerCase();
    	else
    		this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getLastDiscovery() {
        return lastDiscovery;
    }

    public void setLastDiscovery(Date lastDiscovery) {
        this.lastDiscovery = lastDiscovery;
    }

    public Date getNextDiscovery() {
        return nextDiscovery;
    }

    public void setNextDiscovery(Date nextDiscovery) {
        this.nextDiscovery = nextDiscovery;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public Integer getVport() {
        return vport;
    }

    public void setVport(Integer vport) {
        this.vport = vport;
    }

    public HealthStatusDto getStatus() {
        return status;
    }

    public void setStatus(HealthStatusDto status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //////////////////////

    public boolean isAlive()
    {
        if ((getNextDiscovery() == null) || (System.currentTimeMillis() > (getLastDiscovery().getTime() + 2*60*1000)))
            return false;
        else
            return true;
    }

    public Boolean getAlive() {
        return this.isAlive();
    }

    public long getStatusFrequencyMs()
    {
        return PkConstants.STATUS_REFRESH_MS;
    }

    public Boolean isActive()
    {
        return active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HealthItemDto clone() {
        return this.clone();
    }

    public boolean isAuditEnabled() {
        return isAuditEnabled;
    }

    public void setAuditEnabled(boolean auditEnabled) {
        isAuditEnabled = auditEnabled;
    }

    @XmlTransient
    public String getRid() {
        return "";
    }

    @XmlTransient
    public String getAppId() {
        return "";
    }
}
