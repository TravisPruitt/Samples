package com.disney.xband.xbrms.common.model;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.common.lib.health.DiscoveryInfo;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.model.XbrcModel;

@XmlRootElement(name = "xbrc")
public class XbrcDto extends HealthItemDto implements IDataTransferObject {
    // Status + status message
    protected String model;
    // Venue code
    protected String facilityId;
    // Start time of performnace metric running average period
    private String startPerfTime;
    // is high availability being used
 	private String haEnabled = "false";
    // High Availability status
    protected String haStatus;

    public XbrcDto() {
    }

    public XbrcDto(DiscoveryInfo info) {
        super();
        this.ip = info.getIp();
        this.port = info.getPort();
        this.hostname = (info.getHostname() == null || info.getHostname().trim().isEmpty()) ? info.getIp() : info.getHostname().toLowerCase();
        this.facilityId = info.getVenue();

        // time of this discovery message
        Calendar calendar = Calendar.getInstance();
        this.lastDiscovery = calendar.getTime();

        // increment the time of this message by discovery interval to get the time of the next expected discovery message
        calendar.add(Calendar.SECOND, info.getDiscoveryInterval());
        this.nextDiscovery = calendar.getTime();

        // sending discovery messages, therefore active
        active = Boolean.TRUE;

        this.haStatus = info.getHaStatus();
    }

    public XbrcDto(Long id, String ip, Integer port, String hostname, String name, String facilityId,
                   Date lastDiscovery, Date nextDiscovery, HealthStatusDto status, Boolean active,
                   String haStatus, String vip, Integer vport, String model, String startPerfTime,
                   String haEnabled, boolean isAuditEnabled, String version) {
        super();
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.hostname = hostname == null ? ip : hostname;
        this.name = name;
        this.facilityId = facilityId;
        this.lastDiscovery = lastDiscovery;
        this.nextDiscovery = nextDiscovery;
        this.status = status;
        this.active = active;
        this.haStatus = haStatus;
        this.vip = vip;
        this.vport = vport;
        this.model = model;
        this.startPerfTime = startPerfTime;
        this.haEnabled = haEnabled;
        this.isAuditEnabled = isAuditEnabled;
        this.version = version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
    	this.name = name;
    }

    @XmlTransient
    @Override
    public String getType() {
        return HealthItemType.XBRC.name();
    }

    @IHealthField(id = "facilityId", name = "Facility Id", description = "Xbrc facility id", mandatory = true)
    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    @IHealthField(id = "model", name = "Model", description = "Xbrc model name", mandatory = true)
    public String getModel() {
        return XbrcModel.getShortModel(model);
    }

    public void setModel(String model) {
        this.model = model;
    }

    @XmlElement(name = "url")
    public String getUrl() {
        if (this.port == null || this.ip == null || this.ip.trim().isEmpty()) {
            return "";
        }

        if ((String.valueOf(this.port).indexOf("80") >= 0) || String.valueOf(this.port).contains("90")) {
            return "http://" + this.ip + ":" + this.port;
        }
        else {
            return "https://" + this.ip + ":" + this.port;
        }
    }

    @XmlTransient
    public String getAdminUrl() {
        if (this.port == null) {
            return "";
        }
        
        final String proto = (String.valueOf(this.port).indexOf("80") >= 0) || String.valueOf(this.port).contains("90") ? "http://" : "https://";
        final int port = this.port + 10;
       
        if (this.vip != null && !this.vip.trim().isEmpty() && this.vip.indexOf("#") == -1){
        	return proto + this.vip + ":" + port + "/UI";
        } 
        else if (this.hostname != null  && !this.hostname.trim().isEmpty()) {
        	return proto + this.hostname + ":" + port + "/UI";
        }
        else if (this.ip != null && !this.ip.trim().isEmpty()){
        	return proto + this.ip + ":" + port + "/UI";
        }
        
        return "";
    }

    void setAdminUrl(String url) {
    }

    @XmlTransient
    public boolean isValidHaStatus()
	{
		if (haStatus == null || haStatus.trim().isEmpty())
        	return false;
        
        if (Boolean.parseBoolean(haEnabled) && (vip == null || vip.trim().isEmpty() || vip.startsWith("#")))
        	return false;

        if(!Boolean.parseBoolean(haEnabled) && (HAStatusEnum.getStatus(haStatus) != HAStatusEnum.solo)) {
            return false;
        }
        
        return true;
	}

    @Override
    public XbrcDto clone() {
        return new XbrcDto(
                this.id,
                this.ip,
                this.port,
                this.hostname,
                this.name,
                this.facilityId,
                this.lastDiscovery,
                this.nextDiscovery,
                this.status,
                this.active,
                this.haStatus,
                this.vip,
                this.vport,
                this.model,
                this.startPerfTime,
                this.haEnabled,
                this.isAuditEnabled,
                this.version
        );
    }

    @IHealthField(id = "startPerfTime", name = "Start Perf Time", description = "Start time of performance metirc running average period.")
    public String getStartPerfTime() {
        return startPerfTime;
    }

	public void setStartPerfTime(String startPerfTime)
	{
		this.startPerfTime = startPerfTime;
	}

	@IHealthField(id = "haEnabled", name = "HA Enabled", description = "Indicates if high availability is enabled on that system.")
	public String getHaEnabled() {
		return haEnabled;
	}

	public void setHaEnabled(String haEnabled) {
		this.haEnabled = haEnabled;
	}

    @IHealthField(id = "haStatus", name = "HA", description = "Hight availability status", mandatory = true)
    public String getHaStatus() {
        return haStatus;
    }

    public void setHaStatus(String haStatus) {
        this.haStatus = haStatus;
    }

    @XmlTransient
    @Override
    public String getRid() {
        return "GET@/status";
    }

    @XmlTransient
    @Override
    public String getAppId() {
        return this.facilityId;
    }
}
