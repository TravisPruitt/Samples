package com.disney.xband.ac.server;

import com.disney.xband.ac.lib.*;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.security.UserContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class LoginValidation extends ActionSupport {
    private String username;
    private String password;
    private String errorMsg;
    private List<String> directories;
    private String directory;
    private String acReturnUrl;
    private static Logger logger = Logger.getLogger(LoginValidation.class);
    private static Properties manifest;

    public String execute() throws Exception {
        final HttpServletRequest request = ServletActionContext.getRequest();
        final HttpServletResponse response = ServletActionContext.getResponse();
        final HttpSession session = request.getSession(true);
        final IAuthService auth = (IAuthService) request.getSession().getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_AUTH_SERVICE);
        final XbSecureToken otoken = (XbSecureToken) session.getAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);
        final HashMap<String, String> props = (HashMap<String, String>) session.getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_PROPS);

        if(logger.isTraceEnabled()) {
            logger.trace("xAG SID = " + session.getId());
        }

        if(otoken != null) {
            session.setAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES, new Integer(0));
            session.setAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL, request.getRequestURL().toString() + "?" + request.getQueryString());

            return this.reauthenticate(request, response, otoken);
        }

        final Integer retries = (Integer) session.getAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES);
        int rt = 0;

        if(retries == null) {
            session.setAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES, new Integer(0));
            session.setAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL, request.getRequestURL().toString() + "?" + request.getQueryString());
        }
        else {
            rt = ((Integer) session.getAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES));

            if(XbUtils.isReturnUrlPresent(request.getQueryString())) {
                session.setAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL, request.getRequestURL().toString() + "?" + request.getQueryString());
            }

            if(!XbUtils.isEmpty(this.getUsername())) {
                ++rt;

                if(rt > Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_AUTH_ATTEMPTS_MAX))) {
                    session.invalidate();
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    return this.returnError(auth);
                }
            }

            session.setAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES, new Integer(rt));
        }

        return this.authenticate(request, response);
    }

    public String logout() throws Exception {
        final HttpServletRequest request = ServletActionContext.getRequest();
        final HttpSession session = request.getSession(true);
        final IAudit auditor = Auditor.getInstance().getAuditor();
        final XbSecureToken token = (XbSecureToken) request.getSession().getAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);

        if(token != null) {
            UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, ServletAuthRequestWrapper.getClientAddr(request)));
        }
        else {
            UserContext.instance.set(new UserContext("", request.getSession(true).getId(), null, null, ServletAuthRequestWrapper.getClientAddr(request)));
        }

        if(auditor.isAuditEnabled()) {
            auditor.audit(Auditor.getInstance().getAuditor().createLogoutSuccess("Logout succeeded."));
        }

        if(session != null) {
            session.invalidate();
        }

        return SUCCESS;
    }

    public String hello() throws Exception {
        return SUCCESS;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<String> getDirectories() {
        return directories;
    }

    public void setDirectories(List<String> directoryName) {
        this.directories = directoryName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getAcReturnUrl() {
        return this.acReturnUrl;
    }

    public void setAcReturnUrl(String acReturnUrl) {
        try {
            this.acReturnUrl = URLDecoder.decode(acReturnUrl, "UTF-8");
        }
        catch (Exception ignore) {
        }
    }

    public String getAcHomeUrl() {
        final HttpSession session = ServletActionContext.getRequest().getSession(false);

        if(session != null) {
            final HashMap<String, String> props = (HashMap<String, String>) session.getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_PROPS);
            return props.get(PkConstants.NAME_PROP_AC_HOME_URL);
        }

        return null;
    }

    public String getVersion() {
        return LoginValidation.getXagVersion(ServletActionContext.getServletContext());
    }

    //////////////////// Protected methods ///////////////////////

    protected String reauthenticate(HttpServletRequest request, HttpServletResponse response, XbSecureToken otoken) throws Exception {
        final HttpSession session = request.getSession();
        final IAuthService auth = (IAuthService) request.getSession().getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_AUTH_SERVICE);
        final HashMap<String, String> props = (HashMap<String, String>) request.getSession().getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_PROPS);
        final StringBuilder err = new StringBuilder();
        final XbSecureToken token = auth.authenticate(AuthRequestWrapperFactory.createAuthRequestWrapper(request), otoken, err);
        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(token == null) {
            if(logger.isInfoEnabled()) {
                logger.info("Re-authentication failed for user " + otoken.getLogonName());
            }

            if(auditor.isAuditEnabled()) {
                auditor.audit(auditor.createLogonFailure("Re-authentication failed"));
            }

            session.invalidate();
            final String result;

            try {
                result =
                    XbUtils.createReturnUrl(
                            (String) session.getAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL),
                            null,
                            "Re-authentication failed."
                    );
            }
            catch(Exception e) {
                logger.info("createReturnUrl() failed: " + e.toString());
                this.setErrorMsg("Redirection from the login page failed");
                return this.returnError(auth);
            }

            response.sendRedirect(result);
            response.flushBuffer();

            return "";
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Re-authentication succeeded.");
        }

        token.setInitSid(session.getId());
        session.setAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV, token);
        String result = null;

        final String fullRequestUrl = request.getRequestURL().toString() + "?" + request.getQueryString();

        if (!XbUtils.isNoncePresent(fullRequestUrl)) {
            try {
                result =
                        XbUtils.createReturnUrl(
                                fullRequestUrl,
                                XbSecureToken.encode(token),
                                null
                        );

                if (logger.isTraceEnabled()) {
                    logger.trace("Token data: " + token.toString());
                }

                session.removeAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES);
                session.removeAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL);

                if (logger.isTraceEnabled()) {
                    logger.trace("Redirecting request to " + result);
                }

                response.setHeader("Location", result);
                response.flushBuffer();

                return this.returnSuccess(auth);
            }
            catch (Exception e) {
                final String retUrl = XbUtils.createReturnUrl((String) session.getAttribute(fullRequestUrl));

                if(retUrl == null) {
                    logger.info("createReturnUrl() failed: " + e.toString());
                    // this.setErrorMsg("Redirection from the login page failed");
                    // return this.returnError(auth);
                    return "redirect-problem";
                }
                else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Token data: " + token.toString());
                    }

                    session.removeAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES);
                    session.removeAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL);
                    response.setHeader("Location", retUrl);
                    response.flushBuffer();

                    return this.returnSuccess(auth);
                }
            }
        }
        else {
            try {
                result =
                        XbUtils.createReturnUrl(
                                (String) session.getAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL),
                                token.getSid(),
                                null
                        );

                if (logger.isTraceEnabled()) {
                    logger.trace("Token data: " + token.toString());
                }

                session.removeAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES);
                session.removeAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL);

                if (logger.isTraceEnabled()) {
                    logger.trace("Redirecting request to URL " + result);
                }

                response.setHeader("Location", result);
                response.sendRedirect(result);
                response.flushBuffer();

                return this.returnSuccess(auth);
            }
            catch (Exception e) {
                final String retUrl =  XbUtils.createReturnUrl((String) session.getAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL));

                if(retUrl == null) {
                    logger.info("createReturnUrl() failed: " + e.toString());
                    // this.setErrorMsg("Redirection from the login page failed");
                    // return this.returnError(auth);
                    return "redirect-problem";
                }
                else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Token data: " + token.toString());
                    }

                    session.removeAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES);
                    session.removeAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL);

                    if (logger.isTraceEnabled()) {
                        logger.trace("Redirecting request to URL " + result);
                    }

                    response.setHeader("Location", result);
                    response.sendRedirect(result);
                    response.flushBuffer();

                    return this.returnSuccess(auth);
                }
            }
        }
    }

    protected String authenticate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final HttpSession session = request.getSession();
        final IAuthService auth = (IAuthService) request.getSession().getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_AUTH_SERVICE);
        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(XbUtils.isEmpty(this.getUsername())) {
            return this.returnSuccess(auth);
        }

        final StringBuilder err = new StringBuilder();
        final XbSecureToken token = auth.authenticate(AuthRequestWrapperFactory.createAuthRequestWrapper(request), this.directory, this.getUsername(), this.getPassword(), err);

        if (token != null) {
            if(logger.isTraceEnabled()) {
                logger.trace("Authentication succeeded.");
            }

            token.setInitSid(session.getId());
            session.setAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV, token);

            try {
                final String result =
                        XbUtils.createReturnUrl(
                                (String) session.getAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL),
                                token.getSid(),
                                null
                        );

                if(logger.isTraceEnabled()) {
                    logger.trace("Token data: " + token.toString());
                }

                session.removeAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES);
                session.removeAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL);
                response.sendRedirect(result);
                response.flushBuffer();

                return this.returnSuccess(auth);
            }
            catch(Exception e) {
                final String retUrl =  XbUtils.createReturnUrl((String) session.getAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL));

                if(retUrl == null) {
                    logger.info("createReturnUrl() failed: " + e.toString());
                    // err.append("Failed to create return URL");
                    return "redirect-problem";
                }
                else {
                    if(logger.isTraceEnabled()) {
                        logger.trace("Token data: " + token.toString());
                    }

                    session.removeAttribute(PkConstants.NAME_ATTR_AC_AUTH_RETRIES);
                    session.removeAttribute(PkConstants.NAME_ATTR_AC_RETURN_URL);
                    response.sendRedirect(retUrl);
                    response.flushBuffer();

                    return this.returnSuccess(auth);
                }
            }
        }
        else {
            if(logger.isInfoEnabled()) {
                logger.info("Authentication failed: " + err.toString());
            }

            if(auditor.isAuditEnabled()) {
                auditor.audit(auditor.createLogonFailure("Authentication failed"));
            }

            this.setErrorMsg(err.toString());
        }

        return this.returnError(auth);
    }

    private String returnSuccess(IAuthService auth) {
        if (auth.getClass().getName().indexOf("Keystone") >= 0) {
            this.setDirectories(auth.getDirectories());
            return "success-keystone";
        }
        else {
            return "success";
        }
    }

    private String returnError(IAuthService auth) {
        if (auth.getClass().getName().indexOf("Keystone") >= 0) {
            this.setDirectories(auth.getDirectories());
            return "error-keystone";
        }
        else {
            return "error";
        }
    }

    private static String getXagVersion(ServletContext context) {
        if (manifest == null) {
            InputStream is = null;

            try {
                manifest = new Properties();
                is = context.getResourceAsStream("/META-INF/MANIFEST.MF");
                manifest.load(is);
            }
            catch (IOException e) {
                logger.warn("Failed to read th MANIFEST.MF file. Version will not be available;");
            }
            finally {
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(Exception ignore) {
                    }
                }
            }
        }

		String version = manifest.getProperty("Implementation-Title");

		if (XbUtils.isEmpty(version)) {
			version = "Development";
        }

		return version;
	}
}
