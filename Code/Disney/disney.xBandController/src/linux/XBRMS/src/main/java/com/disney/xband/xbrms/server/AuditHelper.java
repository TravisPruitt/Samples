package com.disney.xband.xbrms.server;

import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.lib.security.UserContext;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.*;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import com.disney.xband.xbrms.server.model.XbrmsStatusBo;

import java.net.URI;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/23/13
 * Time: 10:52 AM
 */
public class AuditHelper {
    final static HashMap<String, String> reportedStatusForIp = new HashMap<String, String>(256);
    final static HashMap<String, Boolean> lastStatusForIp = new HashMap<String, Boolean>(256);

    private static void generateStatusEvent(final String addr, final boolean isAlive) {
        final IAudit auditor = Auditor.getInstance().getAuditor();

        final AuditEvent event = auditor.create(
            isAlive ? AuditEvent.Type.INFO : AuditEvent.Type.ERROR, AuditEvent.Category.STATUS, isAlive ? "Green" : "Red", null, null, addr, addr
        );

        event.setHost("");
        event.setvHost(addr);
        event.setAppId(XbrmsConfigBo.getInstance().getDto().getId());

        event.setClient(
            XbrmsUtils.isEmpty(XbrmsStatusBo.getInstance().getDto().getHostname()) ?
                XbrmsStatusBo.getInstance().getDto().getIp() :
                XbrmsStatusBo.getInstance().getDto().getHostname()
        );
        event.setUid("xconnect-service");
        event.setAppClass(AuditEvent.AppClass.XBRMS.toString());

        lastStatusForIp.put(addr, isAlive);

        auditor.audit(event);
    }

    public static void generateStatusChangeEvent(final String url, final boolean isAlive) {
        //try { Thread.sleep(8000); } catch(Exception e) { }
        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(!auditor.isAuditEnabled()) {
            return;
        }

        URI uri;

        try {
            uri = new URI(url);
        }
        catch (Exception e) {
            return;
        }

        if(uri == null) {
           return;
        }

        final String addr = uri.getHost();
        final Boolean lastStatus = lastStatusForIp.get(addr);

        if(lastStatus == null) {
            generateStatusEvent(addr, isAlive);
            return;
        }

        if(lastStatus == isAlive) {
            return;
        }

        lastStatusForIp.put(addr, isAlive);

        final AuditEvent event = auditor.create(
            isAlive ? AuditEvent.Type.INFO : AuditEvent.Type.ERROR,
            AuditEvent.Category.STATUS_CHANGE,
            isAlive ? "Red to Green" : "Green to Red",
            null, null, addr, addr
        );

        event.setHost("");
        event.setvHost(addr);
        event.setAppId(XbrmsConfigBo.getInstance().getDto().getId());
        event.setClient(
            XbrmsUtils.isEmpty(XbrmsStatusBo.getInstance().getDto().getHostname()) ?
                XbrmsStatusBo.getInstance().getDto().getIp() :
                XbrmsStatusBo.getInstance().getDto().getHostname()
        );
        event.setUid("xconnect-service");
        event.setAppClass(AuditEvent.AppClass.XBRMS.toString());

        auditor.audit(event);
    }

    public static void generateStatusEvent(
            final HealthStatusDto status,
            final HealthItemDto dto
    ) {
        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(!auditor.isAuditEnabled() || (dto.getHostname() == null)) {
            return;
        }

        if(reportedStatusForIp.containsKey(dto.getHostname())) {
            return;
        }
        else {
            reportedStatusForIp.put(dto.getHostname(), "");
        }

        final AuditEvent.Type type;

        if(status.getStatus() == StatusType.Green) {
            type = AuditEvent.Type.INFO;
        }
        else {
            if(status.getStatus() == StatusType.Yellow) {
                type = AuditEvent.Type.WARN;
            }
            else {
                type = AuditEvent.Type.ERROR;
            }
        }

        UserContext.instance.set(new UserContext(null, null, AuditEvent.AppClass.XBRC.toString(), null, null));

        final AuditEvent event = auditor.create(
            type,
            AuditEvent.Category.STATUS,
            status.getStatus().toString(),
            dto.getRid(),
            status.getStatus() == StatusType.Green ? "" : status.getMessage(),
            XbrmsUtils.isEmpty(dto.getHostname()) ? dto.getIp() : dto.getHostname(),
            dto.getVip()
        );

        // event.setHost(XbrmsUtils.isEmpty(dto.getHostname()) ? dto.getIp() : dto.getHostname());
        // event.setvHost(dto.getVip());
        event.setAppId("");
        event.setClient(
            XbrmsUtils.isEmpty(XbrmsStatusBo.getInstance().getDto().getHostname()) ?
                XbrmsStatusBo.getInstance().getDto().getIp() :
                XbrmsStatusBo.getInstance().getDto().getHostname()
        );
        event.setUid("xconnect-service");
        /*
        event.setRid(dto.getRid());

        if((dto.getStatus() != null) && (dto.getStatus().getStatus() != StatusType.Green)) {
            event.setrData(dto.getStatus().getMessage());
        }
        */

        if(dto instanceof XbrcDto) {
            event.setAppClass(AuditEvent.AppClass.XBRC.toString());
            event.setAppId((((XbrcDto) dto).getFacilityId()));
        }
        else {
            if(dto instanceof IdmsDto) {
                event.setAppClass(AuditEvent.AppClass.IDMS.toString());
            }
            else {
                if(dto instanceof JmsListenerDto) {
                    event.setAppClass(AuditEvent.AppClass.JSMLISTENER.toString());
                }
                else {
                    event.setAppClass(AuditEvent.AppClass.UNKNOWN.toString());
                }
            }
        }

        auditor.audit(event);
    }

    public static void generateStatusChangeEvent(
            final HealthStatusDto oldStatus,
            final HealthStatusDto newStatus,
            final HealthItemDto dto
    ) {
        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(!auditor.isAuditEnabled() || (dto.getHostname() == null)) {
            return;
        }

        if(newStatus.getStatus().ordinal() == oldStatus.getStatus().ordinal()) {
            return;
        }

        UserContext.instance.set(new UserContext(null, null, AuditEvent.AppClass.XBRC.toString(), null, null));

        final AuditEvent event;

        if(newStatus.getStatus().ordinal() > oldStatus.getStatus().ordinal()) {
            event = auditor.create(
                    newStatus.getStatus() == StatusType.Yellow ? AuditEvent.Type.WARN : AuditEvent.Type.ERROR,
                    AuditEvent.Category.STATUS_CHANGE,
                    oldStatus.getStatus().toString() + " to " + newStatus.getStatus().toString(),
                    dto.getRid(),
                    newStatus.getStatus() == StatusType.Green ? "" : newStatus.getMessage(),
                    XbrmsUtils.isEmpty(dto.getHostname()) ? dto.getIp() : dto.getHostname(),
                    dto.getVip()
            );
        }
        else {
            // Good
            event = auditor.create(
                    AuditEvent.Type.INFO,
                    AuditEvent.Category.STATUS_CHANGE,
                    oldStatus.getStatus().toString() + " to " + newStatus.getStatus().toString(),
                    dto.getRid(),
                    newStatus.getStatus() == StatusType.Green ? "" : newStatus.getMessage(),
                    XbrmsUtils.isEmpty(dto.getHostname()) ? dto.getIp() : dto.getHostname(),
                    dto.getVip()
            );
        }

        // Impersonate the event
        // event.setHost(XbrmsUtils.isEmpty(dto.getHostname()) ? dto.getIp() : dto.getHostname());
        // event.setvHost(dto.getVip());
        event.setAppId("");
        event.setClient(
            XbrmsUtils.isEmpty(XbrmsStatusBo.getInstance().getDto().getHostname()) ?
                XbrmsStatusBo.getInstance().getDto().getIp() :
                XbrmsStatusBo.getInstance().getDto().getHostname()
        );
        event.setUid("xconnect-service");

        /*
        event.setRid(dto.getRid());

        if(newStatus.getStatus() != StatusType.Green) {
            event.setrData(newStatus.getMessage());
        }
        */

        if(dto instanceof XbrcDto) {
            event.setAppClass(AuditEvent.AppClass.XBRC.toString());
            event.setAppId((((XbrcDto) dto).getFacilityId()));
        }
        else {
            if(dto instanceof IdmsDto) {
                event.setAppClass(AuditEvent.AppClass.IDMS.toString());
            }
            else {
                if(dto instanceof JmsListenerDto) {
                    event.setAppClass(AuditEvent.AppClass.JSMLISTENER.toString());
                }
                else {
                    event.setAppClass(AuditEvent.AppClass.UNKNOWN.toString());
                }
            }
        }

        auditor.audit(event);
    }
}
