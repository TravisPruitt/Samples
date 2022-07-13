package com.disney.xband.xbrms.common.model;

import com.disney.xband.common.lib.health.StatusType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "healthStatus")
public class HealthStatusDto implements IDataTransferObject {
    private StatusType status;
    private String message;

    public HealthStatusDto() {
    }

    public HealthStatusDto(StatusType status, String message) {
        this.status = status;
        this.message = message;
    }

    public HealthStatusDto(String status, String message) {
        if (status == null) {
            this.status = StatusType.Red;
            this.message = "Status not available";
        }
        else {
            this.status = StatusType.valueOf(status);
            this.message = message;
        }
    }

    @XmlElement
    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
