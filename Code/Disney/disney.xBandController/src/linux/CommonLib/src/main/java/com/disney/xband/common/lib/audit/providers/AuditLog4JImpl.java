package com.disney.xband.common.lib.audit.providers;

import java.util.Collection;

import com.disney.xband.common.lib.audit.AuditBase;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:06 PM
 */

/**
 * This class simply logs audit events to the log4j logging facility.
 */
public class AuditLog4JImpl extends AuditBase {
    public AuditLog4JImpl(final AuditConfig conf) {
        super(conf);
    }

    @Override
    public boolean audit(AuditEvent event) {
        if(this.logger.isInfoEnabled()) {
            this.logger.info(
                String.format(
                    "%s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                    event.getType(), event.getCategory(), event.getDateTime(), event.getAppClass(),
                    event.getAid(), event.getHost(), event.getvHost(), event.getUid(), event.getSid(),
                    event.getDesc(), event.getRid(), event.getrData()
                )
            );
        }

        return true;
    }
    
    @Override
    public boolean audit(Collection<AuditEvent> events) {
        if(events != null) {
            for(AuditEvent event : events) {
                this.audit(event);
            }
        }

        return true;
    };
}
