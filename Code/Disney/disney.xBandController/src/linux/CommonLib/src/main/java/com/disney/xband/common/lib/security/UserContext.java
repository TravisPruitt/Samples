package com.disney.xband.common.lib.security;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/13/13
 * Time: 4:11 PM
 */
public class UserContext {
    public static final ThreadLocal<Object> instance = new ThreadLocal<Object> () {
        @Override protected String initialValue() {
            return null;
        }
    };

    private String logonName;
    private String sid;
    private Object token;
    private String client;
    private String serToken;
    private String appClass;

    public UserContext(final String logonName, final String sid, final String appClass, final Object token, final String client) {
        init(logonName, sid, token, client, null, appClass);
    }

    public UserContext(final String logonName, final String sid, final String appClass, final Object token, final String client, final String hash) {
        init(logonName, sid, token, client, hash, appClass);
    }

    private void init(final String logonName, final String sid, final Object token, final String client, final String hash, final String appClass) {
        this.logonName = logonName == null ? "" : logonName;
        this.sid = sid == null ? "" : sid;
        this.token = token;
        this.client = client == null ? "" : client;
        this.serToken = hash;
        this.appClass = appClass;
    }

    public String getLogonName() {
        return logonName;
    }

    public void setLogonName(final String logonName) {
        this.logonName = logonName;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(final String sid) {
        this.sid = sid;
    }

    public Object getToken() {
        return token;
    }

    public void setToken(Object token) {
        this.token = token;
    }

	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		this.client = client;
	}

    public String getSerToken() {
        return serToken;
    }

    public void setSerToken(String serToken) {
        this.serToken = serToken;
    }

    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(String appClass) {
        this.appClass = appClass;
    }
}
