package com.disney.xband.ac.lib;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/8/12
 * Time: 4:11 PM
 */
public interface IAuthRequestWrapper extends Serializable {
    StringBuffer getRequestURL();
    String getHeader(String name);
    String getQueryString();
    String getMethod();
    Object getAttribute(String name);
    String getParameter(String name);
    Object getSession();
    String getSessionId();
    Object getSessionAttribute(String name);
    void setSessionAttribute(String name, Object value);
    void removeSessionAttribute(String name);
    String getClientAddress();
}
