package com.disney.xband.common.lib.audit;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/1/13
 * Time: 11:50 PM
 */

import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;

/**
 * Interceptors can be used to modify an event message before it reaches an audit provider. Interceptors can be
 * registered/unregistered with the help of IAuditControl interface obtained via AuditFactory.
 */
public interface IAuditEventsInterceptor {
    /**
     * Initialize interceptor
     * @param conf Interceptor configuration parameters
     */
    void init(AuditInterceptorConfig conf);

    /**
     *
     * @return Interceptor ID.
     */
    String getId();

    /**
     * This method can potentially modify an event passed as a parameter. If you want to filter out an event
     * simply return null. This will stop the normal message flow through the chain of interceptors and audit
     * providers.
     *
     * @param event Event
     * @return Possible altered event or null to stop the event flow.
     */
    AuditEvent intercept(AuditEvent event);
}