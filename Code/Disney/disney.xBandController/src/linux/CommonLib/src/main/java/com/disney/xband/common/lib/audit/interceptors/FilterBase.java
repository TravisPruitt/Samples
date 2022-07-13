package com.disney.xband.common.lib.audit.interceptors;

import com.disney.xband.common.lib.audit.IAuditEventsInterceptor;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/31/13
 * Time: 10:32 AM
 */
public class FilterBase implements IAuditEventsInterceptor {
    protected Logger logger = Logger.getLogger(FilterBase.class);
    protected AuditInterceptorConfig config;

    @Override
    public void init(final AuditInterceptorConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return this.config.getId();
    }

    @Override
    public AuditEvent intercept(AuditEvent event) {
        return event;
    }
}
