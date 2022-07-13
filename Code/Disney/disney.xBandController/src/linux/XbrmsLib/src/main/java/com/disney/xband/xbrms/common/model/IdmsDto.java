package com.disney.xband.xbrms.common.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "idms")
public class IdmsDto extends HealthItemDto implements IDataTransferObject {
    // When the app was started
    private String startTime;
    private String databaseURL;
    private String databaseUserName;
    private String databaseVersion;
    private String readOnly;

    public IdmsDto() {
    }

    public IdmsDto(Long id, String hostname, String ip, Integer port, String version,
                   Date lastDiscovery, Date nextDiscovery, HealthStatusDto status,
                   Boolean active, String vip, Integer vport,
                   String startTime, String databaseURL, String databaseUserName,
                   String databaseVersion, String readOnly) {
        super();
        this.id = id;
        this.ip = ip;
        this.hostname = (hostname == null || hostname.trim().isEmpty()) ? ip : hostname.toLowerCase();
        this.port = port;
        this.version = version;
        this.lastDiscovery = lastDiscovery;
        this.nextDiscovery = nextDiscovery;
        this.status = status;
        this.active = active;
        this.vip = vip;
        this.vport = vport;
        this.startTime = startTime;
        this.databaseURL = databaseURL;
        this.databaseUserName = databaseUserName;
        this.databaseVersion = databaseVersion;
        this.readOnly = readOnly;
    }

    @Override
    public HealthItemDto clone() {
        return new IdmsDto(super.getId(), super.getHostname(), super.getIp(), super.getPort(), super.getVersion(),
                super.getLastDiscovery(), super.getNextDiscovery(), super.getStatus(), super.getActive(), super.getVip(),
                super.getVport(), startTime, databaseURL, databaseUserName,
                databaseVersion, readOnly);
    }

    @IHealthField(id = "startTime", name = "Start Time", description = "When this IDMS was started.")
    public String getStartTime()
    {
        return this.startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }
    @IHealthField(id = "databaseURL", name = "Database URL", description = "Database URL.")
    public String getDatabaseURL()
    {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL)
    {
        this.databaseURL = databaseURL;
    }
    @IHealthField(id = "databaseUserName", name = "Database User", description = "Database user.")
    public String getDatabaseUserName()
    {
        return databaseUserName;
    }

    public void setDatabaseUserName(String databaseUserName)
    {
        this.databaseUserName = databaseUserName;
    }
    @IHealthField(id = "databaseVersion", name = "Database Version", description = "Database version.", mandatory = true)
    public String getDatabaseVersion()
    {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion)
    {
        this.databaseVersion = databaseVersion;
    }
    @IHealthField(id = "readOnly", name = "Read Only", description = "Read only.")
    public String getReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(String readOnly)
    {
        this.readOnly = readOnly;
    }

    @Override
    public String getType() {
        return HealthItemType.IDMS.name();
    }

    @Override
    public String getName() {
        return "IDMS";
    }

    @Override
    public void setName(String name) {
    }

    @XmlTransient
    @Override
    public String getRid() {
        return "GET@/IDMS/status";
    }

    @XmlTransient
    @Override
    public String getAppId() {
        return this.getId() + "";
    }
}