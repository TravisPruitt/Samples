package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/3/13
 * Time: 10:53 AM
 */
public interface IAuditControl {

    /**
     * Add an interceptor at the beginning of the interceptors chain. If an interceptor with a specific ID already
     * exists in the chain the method does nothing.
     *
     * @param interceptor Interceptor to add.
     * @param conf Interceptor configuration.
     */
    void addInterceptor(final IAuditEventsInterceptor interceptor, final AuditInterceptorConfig conf);

    /**
     * Add an interceptor at a specific position in the interceptors chain. If an interceptor with a specific ID already
     * exists in the chain the method does nothing. If "position" is greater then the length of the chain
     * the interceptor will be added to the end.
     *
     * @param interceptor Interceptor to add.
     * @param conf Interceptor configuration.
     * @param position Position in the interceptors chain.
     */
    void addInterceptorAt(final IAuditEventsInterceptor interceptor, final AuditInterceptorConfig conf, int position);

    /**
     * Get audit events interceptor with the provided id.
     *
     * @param id Interceptor ID, typically a full class name.
     * @return Audit events interceptor.
     */
    IAuditEventsInterceptor getInterceptor(String id);

    /**
     * Remove an interceptor from the interceptors chain. If an interceptor with the specified ID does not exist in the
     * chain the method does nothing.
     *
     * @param id Interceptor ID.
     */
    void removeInterceptor(String id);

    /**
     * Get an event provider if there is one.
     *
     * @return Event provider or null if none exists in the providers stack.
     */
    IAuditEventsProvider getEventsProvider(String className);
}
