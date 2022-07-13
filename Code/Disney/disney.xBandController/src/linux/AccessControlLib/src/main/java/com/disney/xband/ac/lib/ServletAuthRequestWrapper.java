package com.disney.xband.ac.lib;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/8/12
 * Time: 3:56 PM
 */
public class ServletAuthRequestWrapper implements IAuthRequestWrapper {
    final private HttpServletRequest request;

    public ServletAuthRequestWrapper(Object request) {
        this.request = (HttpServletRequest) request;
    }

    @Override
    public StringBuffer getRequestURL() {
        return this.request.getRequestURL();
    }

    @Override
    public String getSessionId() {
        return this.request.getSession().getId();
    }

    @Override
    public String getHeader(final String name) {
        return this.request.getHeader(name);
    }

    @Override
    public String getQueryString() {
        return this.request.getQueryString();
    }

    @Override
    public Object getAttribute(final String name) {
        return this.request.getAttribute(name);
    }

    @Override
    public Object getSessionAttribute(final String name) {
        return this.request.getSession().getAttribute(name);
    }

    @Override
    public void setSessionAttribute(final String name, final Object value) {
        this.request.getSession().setAttribute(name, value);
    }

    @Override
    public void removeSessionAttribute(final String name) {
        this.request.getSession().removeAttribute(name);
    }

    @Override
    public Object getSession() {
        return this.request.getSession();
    }

    @Override
    public String getParameter(final String name) {
        return this.request.getParameter(name);
    }

    @Override
    public String getMethod() {
        return this.request.getMethod();
    }
    
    @Override
    public String getClientAddress() {
    	return ServletAuthRequestWrapper.getClientAddr(this.request);
    }

    public static String getClientAddr(HttpServletRequest req) {
        final String addr = ServletAuthRequestWrapper.getXForwardedForHeader(req);

        if(addr == null) {
            return req.getRemoteHost();
        }

        return null;
    }

    private static String getXForwardedForHeader(HttpServletRequest req) {
        if(req.getHeader("X-Forwarded-For") != null) {
            return req.getHeader("X-Forwarded-For");
        }

        if(req.getHeader("x-forwarded-for") != null) {
            return req.getHeader("x-forwarded-for");
        }

        if(req.getHeader("X-FORWARDED-FOR") != null) {
            return req.getHeader("X-FORWARDED-FOR");
        }

        return null;
    }
}
