package com.disney.xband.ac.lib.client.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.client.IAcManager;

import java.security.Principal;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/18/12
 * Time: 2:54 PM
 */
public class XbHttpRequestWrapper extends HttpServletRequestWrapper {
    private IAcManager am;
    private XbSecureToken token;

    public XbHttpRequestWrapper(HttpServletRequest request, XbSecureToken token) {
        super(request);
        am = (IAcManager) request.getSession().getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_MANAGER);
        this.token = token;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.am.isUserInRole(token, role);
    }

    @Override
    public Principal getUserPrincipal() {
        return new XbPrincipal(this.token, this.am);
    }
}
