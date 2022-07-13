package com.disney.xband.common.lib.audit.model;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/31/13
 * Time: 10:37 AM
 */
public class AuditInterceptorConfig {
    private String id;
    private String params;

    public AuditInterceptorConfig(final String id, final String params) {
        this.id = id;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public String getParams() {
        return params;
    }
}
