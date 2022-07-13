package com.disney.xband.common.lib.audit.interceptors.pwcache;

import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/12/13
 * Time: 10:43 AM
 */
public class LogonCacheConfig extends AuditInterceptorConfig {

    public LogonCacheConfig(final String id, final String params) {
        super(id, params);
    }

    public LogonCacheConfig(final String id, final Properties props) {
        super(id, null);
    }
}
