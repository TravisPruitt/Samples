package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.AuditConfig;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/17/13
 * Time: 12:16 PM
 */
public class Auditor {
    private IAudit auditor;
    private IAuditEventsProvider eventsProvider;
    private AuditConfig config;
    private IAuditEventsProvider defaultProvider;
    private IAudit defaultAuditor;
    private AuditConfig defaultConfig;
    private AuditFactory auditFactory;

    private Auditor() {
        this.defaultConfig = new AuditConfig();
        this.defaultConfig.setImplStack("com.disney.xband.common.lib.audit.providers.AuditLog4JImpl");
        this.defaultConfig.setEnabled(true);
        this.defaultProvider = new AuditFactory(this.defaultConfig).getAuditControl().getEventsProvider("AuditLog4JImpl");
        this.defaultAuditor = new AuditFactory(this.defaultConfig).getAudit();
    }

    public IAudit getAuditor() {
        return this.auditor == null ? this.defaultAuditor : this.auditor;
    }

    public void setAuditor(final IAudit auditor) {
        this.auditor = auditor;
    }

    public IAuditEventsProvider getEventsProvider() {
        return this.eventsProvider == null ? this.defaultProvider : this.eventsProvider;
    }

    public void setEventsProvider(final IAuditEventsProvider eventsProvider) {
        this.eventsProvider = eventsProvider;
    }

    public AuditConfig getConfig() {
        return this.config == null ? this.defaultConfig : this.config;
    }

    public void setConfig(AuditConfig config) {
        this.config = config;
    }

    public AuditFactory getAuditFactory() {
        return auditFactory;
    }

    public void setAuditFactory(AuditFactory auditFactory) {
        this.auditFactory = auditFactory;
    }

    private static class SingletonHolder {
        public static final Auditor instance = new Auditor();
    }

    public static Auditor getInstance() {
        return SingletonHolder.instance;
    }
}