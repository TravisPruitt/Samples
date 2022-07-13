package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.AuditEvent;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:01 PM
 */
public interface IAudit {
    boolean isAuditEnabled();

    // Raw audit event
    boolean audit(AuditEvent event);
    boolean audit(Collection<AuditEvent> events);

    // Generic audit event
    AuditEvent create(AuditEvent.Type type, AuditEvent.Category category, String desc, String resourceId, String resourceData, String host, String vHost);
    AuditEvent create(AuditEvent.Type type, AuditEvent.Category category, String desc, String resourceId, String resourceData);
    AuditEvent create(AuditEvent.Type type, AuditEvent.Category category, String desc);

    // Logon audit
    AuditEvent createLogonSuccess(String desc);
    AuditEvent createLogonSuccess();
    AuditEvent createLogonFailure(String desc);
    AuditEvent createLogonFailure();

    // Logout audit
    AuditEvent createLogoutSuccess();
    AuditEvent createLogoutFailure();
    AuditEvent createLogoutSuccess(String desc);
    AuditEvent createLogoutFailure(String desc);

    // Code execution audit
    AuditEvent createAccessSuccess(String resourceId);
    AuditEvent createAccessFailure(String resourceId);

    // Data view audit
    AuditEvent createReadSuccess(String resourceId);
    AuditEvent createReadFailure(String resourceId);

    // Date change audit
    AuditEvent createWriteSuccess(String desc, String resourceId, String resourceData);
    AuditEvent createWriteFailure(String desc, String resourceId, String resourceData);
    AuditEvent createWriteSuccess(String resourceId, String resourceData);
    AuditEvent createWriteFailure(String resourceId, String resourceData);

    // Health events
    AuditEvent createFatal(AuditEvent.Category category, String desc);
    AuditEvent createError(AuditEvent.Category category, String desc);
    AuditEvent createWarn(AuditEvent.Category category, String desc);
    AuditEvent createInfo(AuditEvent.Category category, String desc);

    // Health events with empty category
    AuditEvent createFatal(String desc);
    AuditEvent createError(String desc);
    AuditEvent createWarn(String desc);
    AuditEvent createInfo(String desc);
}
