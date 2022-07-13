package com.disney.xband.ac.lib.client.filter;

import com.disney.xband.ac.lib.*;
import com.disney.xband.ac.lib.client.IAcManager;
import com.disney.xband.ac.lib.client.AcManager;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.ac.lib.client.XbHttpRequestVault;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.common.lib.security.UserContext;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/20/12
 * Time: 9:49 AM
 */

public class XbHttpRequestFilter implements Filter {

    protected FilterConfig filterConfig;
    protected IAcManager acManager;
    protected HashMap<String, String> props;
    protected long reauthSecs;
    protected IAuthService localAuth;
    protected boolean disableFilter;
    private SecureRandom random = new SecureRandom();
    private Logger logger;

    public void init(FilterConfig filterConfig) throws ServletException {
        // try {Thread.sleep(2000); } catch(Exception ignore) {}
        this.logger = Logger.getLogger(XbHttpRequestFilter.class);
        this.filterConfig = filterConfig;
        this.readConfig(filterConfig);
    }

    public void destroy() {
        this.filterConfig.getServletContext().removeAttribute(PkConstants.NAME_ATTR_AC_MANAGER);
        this.filterConfig.getServletContext().removeAttribute(PkConstants.NAME_ATTR_AC_PROPS);
        this.filterConfig = null;
    }

    public void
    doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws java.io.IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        final String validatedUrl = InputValidator.validateUrl(req.getRequestURL().toString());
        XbUtils.setAuthServerUrlComps(validatedUrl);
        final IAudit auditor = Auditor.getInstance().getAuditor();
        final String appId = req.getContextPath().substring(1);

        if(this.disableFilter) {
            request.setAttribute(PkConstants.NAME_ATTR_REQ_AC_DISABLED, "1");
            chain.doFilter(request, response);
            return;
        }

        final HttpServletResponse res = (HttpServletResponse) response;

        // Check if this is a logout request
        if(this.acManager.isLogout(AuthRequestWrapperFactory.createAuthRequestWrapper(req), this.props.get(PkConstants.NAME_PROP_AC_LOGOUT_PATH))) {
            final HttpSession session = req.getSession(false);
            XbSecureToken token = null;

            try {
                token = (XbSecureToken) session.getAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN);
            }
            catch(Exception ignore) {
            }

            if(session != null) {
                session.invalidate();
            }

            if(token != null) {
                UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), appId, token, ServletAuthRequestWrapper.getClientAddr(req)));
            }
            else {
                UserContext.instance.set(new UserContext("", req.getSession(true).getId(), appId, null, ServletAuthRequestWrapper.getClientAddr(req)));
            }

            if(auditor.isAuditEnabled()) {
                auditor.audit(auditor.createLogoutSuccess("Logout succeeded."));
            }

            final String acUrl = XbUtils.createLogoutServiceUrl(this.props, XbUtils.getAppBaseUrl(validatedUrl));

            if (logger.isTraceEnabled()) {
                logger.trace("Redirecting logout request " + validatedUrl + " to xAG: " + acUrl);
            }

            res.sendRedirect(acUrl);

            return;
        }

        // Check if the request is for a protected resource
        if(!this.acManager.isProtected(AuthRequestWrapperFactory.createAuthRequestWrapper(req))) {
            chain.doFilter(request, response);
            return;
        }

        final HttpSession session = req.getSession(true);
        XbSecureToken token = (XbSecureToken) session.getAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN);
        XbHttpRequestVault orgReq = (XbHttpRequestVault) session.getAttribute(PkConstants.NAME_ATTR_AC_SAVED_REQUEST);

        if(logger.isTraceEnabled()) {
            logger.trace("Filter SID = " + session.getId());
        }

        boolean isReturnedReq = false;

        if(token == null) {
            // Check if the request requires local authentication
            final boolean isLocal = this.acManager.isLocalAuth(AuthRequestWrapperFactory.createAuthRequestWrapper(req));

            if(isLocal) {
                // Call the local authentication service
                token = this.localAuth.authenticate(AuthRequestWrapperFactory.createAuthRequestWrapper(req));

                if(token == null) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("Local authentication failed");
                    }

                    // Return error if local authentication failed
                    res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed.");
                    return;

                    // Adam&Glenn suggested that should we try interactive authentication when local authentication fails
                    // assuming that the request is coming from a web browser pointing to a RESTful endpoint. Hence the
                    // return statement was commented out.
                }
                else {
                    // Save the original request in the vault.
                    // orgReq = new XbHttpRequestVault(req);

                    if(!this.acManager.isAuthorized(token, AuthRequestWrapperFactory.createAuthRequestWrapper(req))) {
                        if(logger.isInfoEnabled()) {
                            logger.info("Authorization failed for url " + req.getRequestURL().toString());
                        }

                        res.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN, "Authorization failed");

                        return;
                    }

                    if(logger.isTraceEnabled()) {
                        logger.trace("Authorization succeeded for url " + validatedUrl);
                    }

                    try {
                        // Allow the request in
                        chain.doFilter(new XbHttpRequestWrapper(req, token), response);

                        return;
                    }
                    catch(ServletException e) {
                        if(logger.isTraceEnabled()) {
                            logger.trace(e.toString());
                        }

                        throw e;
                    }
                }
            }

            if(token == null) {
                final String thisUrl = XbHttpRequestFilter.getThisUrl(req);

                if (orgReq == null) {
                	
                    if (XbHttpRequestFilter.noRedirect(req)) {
                        res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }

                    // Forward the request to the remote authentication service
                    session.setAttribute(PkConstants.NAME_ATTR_AC_SAVED_REQUEST, new XbHttpRequestVault(req));
                    // The nonce will be used by XAG to hide sensitive information (session id) in the web browser
                    // address field.
                    final String nonce = new BigInteger(160, random).toString().substring(0, 32);
                    session.setAttribute(PkConstants.NAME_ATTR_AC_SAVED_NONCE, nonce);
                    final String acUrl = XbUtils.createLogonServiceUrl(this.props, thisUrl, nonce, req.getMethod());

                    if (logger.isTraceEnabled()) {
                        logger.trace("Redirecting request " + validatedUrl + " to xAG: " + acUrl);
                    }

                    if(!this.checkXagAvailability(req, res, validatedUrl)) {
                        return;
                    }

                    res.sendRedirect(acUrl);
                    return;
                }
                else {
                    // Handle the result returned by the remote authentication service.
                    isReturnedReq = true;
                    String reqId = InputValidator.validateHttpParamVal(req.getParameter(PkConstants.NAME_PARAM_AC_REQUEST_ID));

                    if(reqId == null) {
                        session.invalidate();
                        res.sendRedirect(thisUrl);

                        return;
                    }

                    try {
                        reqId = XbUtils.dexorId(reqId, (String) session.getAttribute(PkConstants.NAME_ATTR_AC_SAVED_NONCE));
                    }
                    catch(Exception e) {
                        session.invalidate();
                        res.sendRedirect(thisUrl);

                        return;
                    }

                    if (XbUtils.isEmpty(reqId)) {
                        final String error = InputValidator.validateHttpParamVal(req.getParameter(PkConstants.NAME_PARAM_AC_ERROR));

                        if (logger.isTraceEnabled()) {
                            logger.trace("xAG returned error: " + error);
                        }

                        session.invalidate();
                        res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);

                        return;
                    }

                    try {
                        token = this.reauthenticate(req, res, reqId, validatedUrl);

                        UserContext.instance.set(
                            new UserContext(
                                token.getDisplayName(),
                                token.getInitSid(),
                                appId,
                                token,
                                ServletAuthRequestWrapper.getClientAddr(req),
                                Base64.encodeBase64String(XbUtils.objectToXmlString(token, XbSecureToken.class).getBytes())
                            )
                        );

                        if(auditor.isAuditEnabled()) {
                            auditor.audit(auditor.createLogonSuccess("Authentication succeeded"));
                        }

                        if (logger.isTraceEnabled()) {
                            logger.trace("Authentication succeeded for request ID: " + reqId);
                        }
                    }
                    catch (Exception e) {
                        UserContext.instance.set(new UserContext("", "", appId, null, ServletAuthRequestWrapper.getClientAddr(req)));

                        if(auditor.isAuditEnabled()) {
                            auditor.audit(auditor.createLogonFailure("Authentication failed"));
                        }

                        if (logger.isTraceEnabled()) {
                            logger.trace("Authentication failed for request ID: " + reqId);
                        }

                        session.invalidate();

                        res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }

                    session.removeAttribute(PkConstants.NAME_ATTR_AC_SAVED_REQUEST);

                    //if(!orgReq._getMethod().equalsIgnoreCase("GET")) {
                    //    req = new XbHttpRequestVault(req, orgReq);
                    //}
                }
            }
        }
        else {
            // Re-authenticate the request if necessary
            long timeDelta = System.currentTimeMillis() - token.getAuthTime();
            final String thisUrl = XbHttpRequestFilter.getThisUrl(req);

            if (timeDelta > this.reauthSecs) {
                try {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Re-authenticating request " + req.getRequestURL().toString());
                    }

                    token = this.reauthenticate(req, res, token.getSid(), validatedUrl);

                    UserContext.instance.set(
                        new UserContext(
                            token.getDisplayName(), token.getInitSid(), appId, token, ServletAuthRequestWrapper.getClientAddr(req)
                        )
                    );

                    if(auditor.isAuditEnabled()) {
                        auditor.audit(auditor.createLogonSuccess("Re-authentication succeeded"));
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace("Re-authentication succeeded for request " + req.getRequestURL().toString());
                    }
                }
                catch (Exception e) {
                    UserContext.instance.set(new UserContext("", "", appId, null, ServletAuthRequestWrapper.getClientAddr(req)));

                    if(auditor.isAuditEnabled()) {
                        auditor.audit(auditor.createLogonFailure("Re-authentication failed"));
                    }

                    if (logger.isInfoEnabled()) {
                        logger.info("Re-authentication failed for request " + req.getRequestURL().toString());
                    }

                    try {
                        session.invalidate();
                    }
                    catch (Exception ignore) {
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace("Redirecting request " + thisUrl);
                    }
                    
                    if (!XbHttpRequestFilter.noRedirect(req)) {
                        res.sendRedirect(thisUrl);
                    }
                    else {
                        res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    }

                    return;
                }
            }
        }

        UserContext.instance.set(
            new UserContext(
                token.getDisplayName(),
                token.getInitSid(),
                appId,
                token,
                ServletAuthRequestWrapper.getClientAddr(req)
            )
        );

        // Authorize a previously authenticated request
        // if(!this.acManager.isAuthorized(token, orgReq)) {
        if(!this.acManager.isAuthorized(token, AuthRequestWrapperFactory.createAuthRequestWrapper(req))) {
            auditor.audit(auditor.createAccessFailure(req.getMethod() + "@" + req.getRequestURI()));

            if(logger.isInfoEnabled()) {
                logger.info("Authorization failed for url " + req.getRequestURL().toString());
            }

            //res.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
            res.sendRedirect(
                    XbUtils.getAppBaseUrl(validatedUrl) + "/" + "ExceptionAction?errMsg=" +
                    URLEncoder.encode("You are not authorized to access the content", "UTF-8")
            );

            return;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Authorization succeeded for url " + validatedUrl);
        }

        try {
            // Allow the request in
            chain.doFilter(new XbHttpRequestWrapper(req, token), response);
        }
        catch(ServletException e) {
            if(logger.isTraceEnabled()) {
                logger.trace(e.toString());
            }

            throw e;
        }
        catch(IOException e) {
            if(logger.isTraceEnabled()) {
                logger.trace(e.toString());
            }

            throw e;
        }
    }

    private static String getThisUrl(HttpServletRequest req) {
        if(req.getQueryString() == null) {
            return InputValidator.validateUrl(req.getRequestURL().toString());
        }

        return InputValidator.validateUrl(req.getRequestURL().toString()) + "?" + InputValidator.validateQueryString(req.getQueryString());
    }
    
    private static boolean noRedirect(HttpServletRequest req) {
    	final String thisUrl = XbHttpRequestFilter.getThisUrl(req);

    	// no redirect for ajax calls
        if (thisUrl.toLowerCase().indexOf("ajax") >= 0)
        	return true;
        
        // no redirect if the NoAcRedirect header is set
        if (req.getHeader("NoAcRedirect") != null)
        	return true;
     
        // no redirect if a NoAcRedirect url parameter is set
        if (req.getParameter("NoAcRedirect") != null)
        	return true;
        
        // no redirect if a NoAcRedirect form value is set
        if (req.getParameterValues("NoAcRedirect") != null)
        	return true;
        
        return false;
    }    

    private XbSecureToken reauthenticate(HttpServletRequest req, HttpServletResponse res, String sid, String validatedUrl) throws Exception 
    {
    	try
    	{
        final String authUrl = XbUtils.createLogonServiceReauthUrl(this.props, validatedUrl, sid, null);

        if(logger.isTraceEnabled()) {
            logger.trace("Openning connection to xAG to get secure token: " + validatedUrl);
        }

        final HttpURLConnection connection = XbConnection.getConnection(new URL(authUrl));
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("Cookie", "JSESSIONID=" + sid);
        // XbHttpRequestFilter.setXForHeader(req, connection);
        
        if(logger.isTraceEnabled()) {
            logger.trace("About to connect to xAG: " + authUrl);
        }
        connection.connect();
        
        final Map<String, List<String>> results = connection.getHeaderFields();
        final List<String> result = results.get("Location");
        final String retToken = URLDecoder.decode(XbUtils.getTokenFromUrl(InputValidator.validateHttpHeaderVal(result.get(0))), "UTF-8");
        final XbSecureToken token = XbSecureToken.decode(retToken);

        req.getSession().setAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN, token);

        if(logger.isTraceEnabled()) {
            logger.trace("Successfully obtained the token");
        }

        return token;
    	}
    	catch (Throwable t)
    	{
    		logger.fatal("Error trying to re-authenticate a requst against a xAG filter.", t);
    		throw new Exception(t);
    	}
    }

    private boolean checkXagAvailability(HttpServletRequest req, HttpServletResponse res, String validatedUrl) throws IOException {
        try {
            this.tryToConnectToXag(req, res);
        }
        catch(Exception ignore) {
            try {
                req.getSession(true).invalidate();
                
                if (XbHttpRequestFilter.noRedirect(req)) {
                    res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }

                final String redirUrl =
                    XbUtils.getAppBaseUrl(
                        validatedUrl) + "/" + "ExceptionAction?errMsg=" +
                        URLEncoder.encode(
                            "Authentication service is not responding via " +
                            XbUtils.getAuthServiceUrl(this.props) +
                            " Make sure nge.xconnect.ac.logonServer property contains valid address of xAG service.", "UTF-8"
                        );

                if(redirUrl.indexOf("/xi/") > 0) {
                    res.sendRedirect(XbUtils.getAppBaseUrl(validatedUrl) + "/" + "xag_error.html");
                }
                else {
                    res.sendRedirect(redirUrl);
                }
            }
            catch (Exception e) {
                res.sendError(
                    javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Authentication service is not responding via " + XbUtils.getAuthServiceUrl(this.props)
                );
            }

            return false;
        }

        return true;
    }

    private void tryToConnectToXag(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final String authUrl = XbUtils.createHelloServiceUrl(this.props);

        if(logger.isTraceEnabled()) {
            logger.trace("Testing xAG availability ...");
        }

        final HttpURLConnection connection = XbConnection.getConnection(new URL(authUrl));
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setReadTimeout(10000);
        connection.connect();
        connection.getInputStream();
    }

    private void logoutFromXag(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final String acUrl = XbUtils.createLogoutServiceUrl(this.props);

        if(logger.isTraceEnabled()) {
            logger.trace("Logging out of xAG ...");
        }

        final HttpURLConnection connection = XbConnection.getConnection(new URL(acUrl));
        connection.setRequestProperty("Cookie", "JSESSIONID=" + req.getSession().getId());
        // XbHttpRequestFilter.setXForHeader(req, connection);
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setReadTimeout(5000);
        connection.connect();
        connection.getInputStream();
    }

    private void readConfig(FilterConfig filterConfig) {
        // Try to load properties from web.xml
        InputStream is = null;

        try {
            final HashMap<String, String> props = new HashMap<String, String>(16);
            final Enumeration<String> en = filterConfig.getServletContext().getInitParameterNames();

            while (en.hasMoreElements()) {
                final String pName = en.nextElement();
                props.put(pName, filterConfig.getServletContext().getInitParameter(pName));
            }

            final String appId = filterConfig.getServletContext().getContextPath().substring(1);

            // Now try to load properties from the a configuration file
            XbUtils.loadProperties(props, appId, this.logger);

            if ("true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_DISABLE_FILTER))) {
                this.disableFilter = true;
                return;
            }

            filterConfig.getServletContext().setAttribute(PkConstants.NAME_ATTR_AC_PROPS, props);

            if (props.get(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE) != null) {
                try {
                    is = new FileInputStream(InputValidator.validateFilePath(props.get(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE)));
                }
                catch (Exception e) {
                    this.logger.fatal("Failed to read roles map file " + props.get(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE), e);
                    throw new RuntimeException(e);
                }
            }
            else {
                props.put(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE, PkConstants.DEFAULT_AC_ROLES_MAP_FILE);
            }

            if (is == null) {
                final String path = filterConfig.getServletContext().getRealPath(props.get(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE));

                try {
                    is = new FileInputStream(InputValidator.validateFilePath(path));
                }
                catch (Exception e) {
                    this.logger.fatal("Failed to read roles map file " + path, e);
                    throw new RuntimeException(e);
                }
            }

            this.acManager = new AcManager(is, appId);
            props.put(PkConstants.NAME_PROP_AC_USERS_PATH, filterConfig.getServletContext().getRealPath(props.get(PkConstants.NAME_PROP_AC_USERS_FILE)));
            this.localAuth = AuthServiceFactory.getLocalAuthService(props);
            this.props = props;
            this.reauthSecs = Long.parseLong(props.get(PkConstants.NAME_PROP_AC_REAUTH_SECS)) * 1000;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }

        try {
            XbConnection.init(props);
        }
        catch(Exception e) {
            this.logger.fatal("Failed to initialize XbConnection.", e);
            throw new RuntimeException(e);
        }

        if(logger.isTraceEnabled()) {
            logger.trace("xConnect servlet filter will be using these properties: \n" + XbUtils.dumpProperties(props));
        }

        filterConfig.getServletContext().setAttribute(PkConstants.NAME_ATTR_AC_MANAGER, this.acManager);
    }
}