package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:06 PM
 */
public class AuditFactory {
    private static Logger logger = Logger.getLogger(com.disney.xband.common.lib.audit.AuditFactory.class);
    private AuditConfig config;
    private AuditController audit;

    public AuditFactory(final AuditConfig config) {
        this.config = config;

        try {
            if (this.config.isEnabled()) {
                if (this.config.getImplStack() != null) {
                    final String[] targets = this.config.getImplStack().split("[,;: \t]+");
                    final AuditBase[] audits = new AuditBase[targets.length];
                    int i = 0;

                    if (targets.length > 0) {
                        for (String target : targets) {
                            if (AuditFactory.logger.isDebugEnabled()) {
                                AuditFactory.logger.debug("Configuring AUDIT implementation: " + target);
                            }

                            final Class classDefinition = Class.forName(target);
                            final Constructor cons = classDefinition.getConstructor(AuditConfig.class);
                            audits[i++] = (AuditBase) cons.newInstance(this.config);
                        }

                        this.audit = new AuditController(config, audits);
                    }


                    // Register interceptors
                    if ((this.config.getInterceptors() != null)) {
                        for(AuditInterceptorConfig ic : this.config.getInterceptors()) {
                            final IAuditEventsInterceptor interceptor = (IAuditEventsInterceptor) Class.forName(ic.getId()).newInstance();
                            this.audit.addInterceptor(interceptor, ic);
                        }
                    }

                    return;
                }
            }
        }
        catch (Exception e) {
            AuditFactory.logger.fatal("Failed to configure AUDIT subsystem!!! Exception: " + e.getMessage());
        }

        AuditFactory.logger.warn("AUDIT subsystem is not configured.");
        this.audit = new AuditController(config);
    }

    public IAudit getAudit() {
        return this.audit;
    }

    public IAuditControl getAuditControl() {
        return this.audit;
    }
}
