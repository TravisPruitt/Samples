package com.disney.xband.xbrms.server.rest;

import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 2/1/13
 * Time: 7:49 AM
 */
public class ResourceBase {
    private static final String USER_ANONYMOUS = "Anonymous";

    protected
    @Context
    ServletContext context;

    protected static Logger logger = Logger.getLogger(ResourceBase.class);

    protected void setAuditParams(HttpServletRequest request) {
        final AuditConfig auditConfig = Auditor.getInstance().getConfig();

        if(auditConfig.isEnabled()) {
            auditConfig.setvHost(request.getServerName());
            auditConfig.setHost(request.getLocalName());
        }
    }

    protected void auditSuccess(final HttpServletRequest request) {
        this.doAudit(true, request);
    }

    protected void auditFailure(final HttpServletRequest request) {
        this.doAudit(false, request);
    }

    private void doAudit(final boolean isSuccess, final HttpServletRequest request) {
        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(auditor.isAuditEnabled()) {
            final String url = XbrmsUtils.getFullRequestUrl(request);
            final String method = request.getMethod();
            final AuditEvent event;

            if(isSuccess) {
                event = auditor.createAccessSuccess(method + "@" + url);
            }
            else {
                event = auditor.createAccessFailure(method + "@" + url);
            }

            auditor.audit(event);
        }
    }
}
