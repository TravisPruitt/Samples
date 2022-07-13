package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.AuditEvent;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:15 PM
 */

/**
 * This interface is used by the an events provider to pull cached events from an events provider.
 * There were two implementations of the interface - local (in events provider) and remote (in events collector).
 */
public interface IAuditEventsProvider {
    List<AuditEvent> getAllEvents();
    List<AuditEvent> getEvents(long afterEventId);
    void deleteAllEvents();
    void deleteEvents(long upToEventId);
    boolean isAuditEnabled();
    long getLastAuditIdForHost(String host, boolean isCollectorHost);

    /**
     * Delete the oldest 1/3 events of the allowed maximum from the cache if "useDates" is false. Otherwise, delete
     * the events that have been stored for more then a number of days specified in the audit configuration.
     *
     * @param useDates Should the cleanup strategy be based on dates?
     */
    void cleanup(boolean useDates);
}
