package com.disney.xband.common.lib.audit.interceptors.pwcache;

public class LogonCacheItem {
    private String uid;
    private String hash;
    private long lastUpdated;

    public LogonCacheItem(final String uid, final String hash, final long lastUpdated) {
        this.uid = uid;
        this.hash = hash;
        this.lastUpdated = lastUpdated;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}