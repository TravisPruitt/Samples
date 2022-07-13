package com.disney.xband.ac.lib;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/8/12
 * Time: 4:18 PM
 */

/**
 * This is a request wrapper for SimpleFramework.
 */
public class SfAuthRequestWrapper implements IAuthRequestWrapper {
    public SfAuthRequestWrapper(Object request) {

    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public String getHeader(final String name) {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public Object getAttribute(final String name) {
        return null;
    }

    @Override
    public Object getSessionAttribute(final String name) {
        return null;
    }

    @Override
    public void setSessionAttribute(final String name, final Object value) {
    }

    @Override
    public void removeSessionAttribute(final String name) {
    }

    @Override
    public Object getSession() {
        return null;
    }

    @Override
    public String getParameter(final String name) {
        return null;
    }

    @Override
    public String getMethod() {
        return null;
    }
    
    @Override
    public String getClientAddress(){
    	return null;
    }
}
