package com.disney.xband.xbrc.Controller;

import com.disney.xband.ac.lib.IAuthRequestWrapper;
import org.simpleframework.http.Request;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/8/13
 * Time: 12:49 PM
 */
public class SfRequestWrapper implements IAuthRequestWrapper {
    private Request request;

    public SfRequestWrapper(Request request) {
        this.request = request;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(this.request.getAddress().toString());
    }

    @Override
    public String getHeader(String name) {
        return this.request.getValue(name);
    }

    @Override
    public String getQueryString() {
        return this.request.getQuery().toString();
    }

    @Override
    public String getMethod() {
        return this.request.getMethod();
    }

    @Override
    public Object getAttribute(String name) {
        return this.request.getAttribute(name);
    }

    @Override
    public String getParameter(String name) {
        try {
            return this.request.getParameter(name);
        }
        catch (IOException ignore) {
        }

        return null;
    }

    @Override
    public Object getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSessionId() {
        return "";
    }

    @Override
    public Object getSessionAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionAttribute(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSessionAttribute(String name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getClientAddress() {
    	return SfRequestWrapper.getClientAddr(this.request);
    }

    public static String getClientAddr(Request req) {
        final String addr = SfRequestWrapper.getXForwardedForHeader(req);

        if(addr == null) {
            return req.getClientAddress().getAddress().getHostAddress();
        }

        return null;
    }

    private static String getXForwardedForHeader(Request req) {
        if(req.getValue("X-Forwarded-For") != null) {
            return req.getValue("X-Forwarded-For");
        }

        if(req.getValue("x-forwarded-for") != null) {
            return req.getValue("x-forwarded-for");
        }

        if(req.getValue("X-FORWARDED-FOR") != null) {
            return req.getValue("X-FORWARDED-FOR");
        }

        return null;
    }
}