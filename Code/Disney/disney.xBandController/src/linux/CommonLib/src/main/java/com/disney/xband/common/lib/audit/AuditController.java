package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/1/13
 * Time: 1:20 PM
 */
public class AuditController extends AuditBase implements IAuditControl {
    protected Logger logger = Logger.getLogger(AuditController.class);

    private AuditBase[] targets;
    private List<IAuditEventsInterceptor> interceptorsChain;

    public AuditController(final AuditConfig conf, final AuditBase... target) {
        super(conf);

        this.targets = target;
        this.interceptorsChain = new ArrayList<IAuditEventsInterceptor>();
    }

    //////////////////////////////////////////////////////////////////////////////
    //                          IAudit implementation                           //
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean audit(AuditEvent event) {
        try {
            if(AuditEvent.Type.valueOf(event.getType()).ordinal() > this.conf.getLevel().ordinal()) {
                return false;
            }
        }
        catch(Exception e) {
            logger.error("Invalid AuditEvent type: " + event.toString());
            return false;
        }

        try {
            for(IAuditEventsInterceptor interceptor : this.interceptorsChain) {
                if(interceptor.intercept(event) == null) {
                    return false;
                }
            }
        }
        catch(Exception ignore) {
            // If another thread is removing an interceptor from the chain this block may throw but since
            // this is an extremely rare case we do not want to introduce synchronization.
        }

        if(this.targets != null) {
            for(IAudit target : this.targets) {
                try {
                    target.audit(event);
                }
                catch(Exception ignore) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
    
    @Override
	public boolean audit(Collection<AuditEvent> events)
	{
		if (events == null || events.size() == 0)
			return false;
		
		Collection<AuditEvent> filteredEvents = new LinkedList<AuditEvent>();
		
		for (AuditEvent event : events)
		{
			try {
	            if(AuditEvent.Type.valueOf(event.getType()).ordinal() > this.conf.getLevel().ordinal()) {
	                continue;
	            }
	        }
	        catch(Exception e) {
	            logger.error("Invalid AuditEvent type: " + event.toString());
	            continue;
	        }

            boolean isSkipEvent = false;

	        try {
	            for(IAuditEventsInterceptor interceptor : this.interceptorsChain) {
	                if(interceptor.intercept(event) == null) {
                        isSkipEvent = true;
	                    break;
	                }
	            }
	        }
	        catch(Exception ignore) {
	            // If another thread is removing an interceptor from the chain this block may throw but since
	            // this is an extremely rare case we do not want to introduce synchronization.
	        }

            if(!isSkipEvent) {
	            filteredEvents.add(event);
            }
		}

        if(filteredEvents.size() <= 0) {
            return false;
        }

        if(this.targets != null) {
            for(IAudit target : this.targets) {
                try {
                    target.audit(filteredEvents);
                }
                catch(Exception ignore) {
                    return false;
                }
            }

            return true;
        }

        return false;
	}

    //////////////////////////////////////////////////////////////////////////////
    //                      IAuditControl implementation                        //
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Add an interceptor at the beginning of the interceptors chain. If an interceptor with a specific ID already
     * exists in the chain the method does nothing.
     *
     * @param interceptor Interceptor to add.
     * @param conf Interceptor configuration.
     */
    public synchronized void addInterceptor(final IAuditEventsInterceptor interceptor, final AuditInterceptorConfig conf) {
        if(interceptor != null) {
            interceptor.init(conf);
            this.getInterceptorsChain().add(0, interceptor);
        }
    }

    /**
     * Add an interceptor at a specific position in the interceptors chain. If an interceptor with a specific ID already
     * exists in the chain the method does nothing. If "position" is greater then the length of the chain
     * the interceptor will be added to the end.
     *
     * @param interceptor Interceptor to add.
     * @param conf Interceptor configuration.
     * @param position Position in the interceptors chain.
     */
    public synchronized void addInterceptorAt(final IAuditEventsInterceptor interceptor, final AuditInterceptorConfig conf, int position) {
        if(interceptor != null) {
            interceptor.init(conf);
            this.getInterceptorsChain().add(position, interceptor);
        }
    }

    /**
     * Remove an interceptor from the interceptors chain. If an interceptor with the specified ID does not exist in the
     * chain the method does nothing.
     *
     * @param id Interceptor ID.
     */
    public synchronized void removeInterceptor(String id) {
        if(id == null) {
            return;
        }

        int i = 0;

        for(IAuditEventsInterceptor interceptor : this.getInterceptorsChain()) {
            if(id.equals(interceptor.getId())) {
                this.getInterceptorsChain().remove(i);
                break;
            }

            ++i;
        }
    }

    /**
     * Get audit events interceptor with the provided id.
     *
     * @param id Interceptor ID, typically a full class name.
     * @return Audit events interceptor.
     */
    public IAuditEventsInterceptor getInterceptor(String id) {
        if(id != null) {
            int i = 0;

            for(IAuditEventsInterceptor interceptor : this.getInterceptorsChain()) {
                if(id.equals(interceptor.getId())) {
                    return this.getInterceptorsChain().get(i);
                }

                ++i;
            }
        }

        return null;
    }

    @Override
    public IAuditEventsProvider getEventsProvider(String className) {
        if(this.targets != null && (className != null)) {
            for(IAuditEventsProvider target : this.targets) {
                try {
                    final String clazz = target.getClass().getName();

                    if(clazz.endsWith(className)) {
                        return target;
                    }
                }
                catch(Exception ignore) {
                }
            }
        }

        return null;
    }

    //////////////////////////////////////////////////////////////////////////////
    //                              Misc methods                                //
    //////////////////////////////////////////////////////////////////////////////

    List<IAuditEventsInterceptor> getInterceptorsChain() {
        return interceptorsChain;
    }
}
