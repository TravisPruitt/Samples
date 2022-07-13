package com.disney.xband.common.lib.audit.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/3/13
 * Time: 10:41 AM
 */
@XmlRootElement(name = "AuditEventList")
public class AuditEventList {
    private List<AuditEvent> events;

    public List<AuditEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AuditEvent> events) {
        this.events = events;
    }
}
