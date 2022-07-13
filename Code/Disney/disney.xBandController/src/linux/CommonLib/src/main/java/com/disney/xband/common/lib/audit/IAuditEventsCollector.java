package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/6/13
 * Time: 2:35 PM
 */

/**
 * This contract is used by events providers to "push" a message directly to an events collector.
 */
public interface IAuditEventsCollector {
    /**
     * Push an event to an events collector.
     *
     * @param event Event to push
     * @return true if audit is enabled for this events provider; false otherwise
     */
    boolean postEvent(AuditEvent event);

    /**
     * Get audit configuration of a specific application class.
     *
     * @param appClass
     * @return
     */
    AuditConfig getAuditConfig(AuditEvent.AppClass appClass);
}
