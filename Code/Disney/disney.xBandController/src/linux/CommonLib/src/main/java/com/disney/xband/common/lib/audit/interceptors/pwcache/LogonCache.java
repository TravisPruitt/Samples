package com.disney.xband.common.lib.audit.interceptors.pwcache;

import com.disney.xband.common.lib.audit.interceptors.FilterBase;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/12/13
 * Time: 2:02 AM
 */
public class LogonCache extends FilterBase {
    private static Logger logger = Logger.getLogger(LogonCache.class);

    private static Map<String, LogonCacheItem> cache;
    private AuditConfig auditConfig;
    private int connectionToMs;
    private int cacheValidDays;
    private boolean clearLogonData;

    public void init(final LogonCacheConfig interceptorConfig, final AuditConfig auditConfig) {
        super.init(interceptorConfig);
        this.auditConfig = auditConfig;
        this.cacheValidDays = 14;
        this.connectionToMs = 15000;
        this.cache = LogonCacheDao.getInstance().getAll(auditConfig.getConnectionPool(), this.cacheValidDays);
    }

    @Override
    public AuditEvent intercept(AuditEvent event) {
        if((AuditEvent.Category.valueOf(event.getCategory()) == AuditEvent.Category.LOGIN) && (this.cache != null)) {
            try {
                this.cachePwHash(event);
            }
            catch (Exception e) {
                logger.error("Failed to cache user logon data: " + e.getMessage());
            }
        }

        return event;
    }

    public String getOfflineUserData(final String uid) {
        // Check L1 cache
        LogonCacheItem item = LogonCache.cache.get(uid);

        if(item == null) {
            // Now check L2 cache
            item = LogonCacheDao.getInstance().getOne(this.auditConfig.getConnectionPool(), uid);

            if(item != null) {
                LogonCache.cache.put(item.getUid(), item);
            }
        }

        if((item == null) || (item.getHash() == null) || (item.getUid() == null)) {
            return null;
        }

        if((item.getLastUpdated() + (this.cacheValidDays * 86400000)) < System.currentTimeMillis()) {
            return null;
        }

        return item.getHash();
    }

    private AuditEvent cachePwHash(final AuditEvent event) {
        final String serToken = event.getrData();
        String name = event.getUid();

        if((serToken != null) && (serToken.length() != 0) && (name != null) && (name.length() != 0) && (auditConfig != null)) {
            try {
                name = name.toUpperCase();
                cache.put(name, new LogonCacheItem(name, serToken, System.currentTimeMillis()));
                LogonCacheDao.getInstance().saveSerToken(this.auditConfig.getConnectionPool(), name, serToken);
            }
            catch (Exception ignore) {
            }

            if(this.isClearLogonData()) {
                event.setrData("");
            }
        }

        return event;
    }

    public boolean isClearLogonData() {
        return clearLogonData;
    }

    public void setClearLogonData(boolean clearLogonData) {
        this.clearLogonData = clearLogonData;
    }

    public int getConnectionToMs() {
        return connectionToMs;
    }

    public void setConnectionToMs(int connectionToSecs) {
        this.connectionToMs = connectionToSecs * 1000;
    }

    public int getCacheValidDays() {
        return cacheValidDays;
    }

    public void setCacheValidDays(int cacheValidDays) {
        this.cacheValidDays = cacheValidDays;
    }
}
