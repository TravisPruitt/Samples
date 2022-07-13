package com.disney.xband.ac.server;

import com.disney.xband.ac.lib.*;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.common.lib.security.UserContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/9/12
 * Time: 4:20 PM
 */
public class TokenService extends ActionSupport implements Preparable {
    private static final String INVALID = "invalid";
    private static Logger logger = Logger.getLogger(LoginValidation.class);
    private static boolean isLocalAuth;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private IAuthService auth;
    private XbSecureToken otoken;
    private String directory;
    private String userName;
    private String password;
    private String tokenRef;
    private HashMap<String, String> props;
	private InputStream tokenInputStream;
    private boolean enableTokenDelegation;
    private String contentType;
    private String appclass;

    @Override
    public void prepare() throws Exception {
        this.request = ServletActionContext.getRequest();

        if(XbUtils.getIpPathComp() == null) {
            XbUtils.setAuthServerUrlComps(this.request.getRequestURL().toString());
        }

        this.response = ServletActionContext.getResponse();
        this.session = request.getSession(true);
        this.auth = (IAuthService) request.getSession().getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_AUTH_SERVICE);
        this.otoken = (XbSecureToken) session.getAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);
        this.props = (HashMap<String, String>) session.getServletContext().getAttribute(PkConstants.NAME_ATTR_AC_PROPS);
        this.enableTokenDelegation = Boolean.parseBoolean(props.get(PkConstants.NAME_PROP_AC_ENABLE_TOKEN_DELEGATION));
        this.contentType = this.request.getHeader("Accept");

        if(logger.isTraceEnabled()) {
            logger.trace("xAG SID = " + session.getId());
        }

        this.setParams();
        final AuditConfig auditConfig = Auditor.getInstance().getConfig();

        if(auditConfig.isEnabled()) {
            final HttpServletRequest request = ServletActionContext.getRequest();
            auditConfig.setvHost(request.getServerName());
            auditConfig.setHost(request.getLocalName());
        }
    }

    public String getToken() throws Exception {
        final IAudit auditor = Auditor.getInstance().getAuditor();

        try {
            if (TokenService.isLocalAuth) {
                return this.getTokenS2S();
            }

            if (this.otoken != null) {
                this.session.removeAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);
                this.otoken = null;
                // return INVALID;
            }

            final XbSecureToken tokenRemote = this.getTokenRemote();

            if ((tokenRemote == null) || !tokenRemote.isAuthenticated()) {
                this.session.invalidate();

                throw new RuntimeException();
            }

            final String appClass = this.appclass == null ? AuditEvent.AppClass.GREETER_APP.toString() : this.appclass;
            UserContext.instance.set(new UserContext(tokenRemote.getDisplayName(), "", appClass, tokenRemote, ServletAuthRequestWrapper.getClientAddr(this.request)));

            this.session.setAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV, tokenRemote);
            final XbSecureToken tClone = XbSecureToken.fromJson(tokenRemote.toJson());
            tClone.setSid("");
            tClone.setInitSid("");

            if ((this.contentType != null) && (this.contentType.equalsIgnoreCase("application/xml"))) {
                final StringWriter writer = new StringWriter();
                final JAXBContext context = JAXBContext.newInstance(XbSecureToken.class);
                final Marshaller m = context.createMarshaller();
                m.marshal(tClone, writer);
                this.tokenInputStream = new ByteArrayInputStream(writer.toString().getBytes());
            }
            else {
                this.tokenInputStream = new ByteArrayInputStream(tClone.toJson().getBytes());
            }

            if(auditor.isAuditEnabled()) {
                auditor.audit(auditor.createLogonSuccess("GetToken operation succeeded"));
            }

            return SUCCESS;
        }
        catch (Exception e) {
        }

        if(auditor.isAuditEnabled()) {
            auditor.audit(auditor.createLogonFailure("GetToken operation failed"));
        }

        return ERROR;
    }

    public String getTokenS2S() throws Exception {
        if(this.otoken != null) {
            this.session.removeAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);
            this.otoken = null;
            // return INVALID;
        }

        XbSecureToken tokenLocal = null;

        tokenLocal = this.auth.authenticate(
            AuthRequestWrapperFactory.createAuthRequestWrapper(this.request),
            this.directory,
            this.userName,
            this.password,
            new StringBuilder()
        );

        if(tokenLocal == null) {
            this.session.invalidate();

            return ERROR;
        }
        else {
            this.session.setAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV, tokenLocal);
            this.tokenInputStream = new ByteArrayInputStream(tokenLocal.toJson().getBytes());

            return SUCCESS;
        }
    }

    public String validateToken() throws Exception {
        final IAudit auditor = Auditor.getInstance().getAuditor();

        try {
            if (TokenService.isLocalAuth) {
                return this.validateTokenS2S();
            }

            if (this.tokenRef == null) {
                if(auditor.isAuditEnabled()) {
                    auditor.audit(auditor.createLogonFailure("Token validation failed"));
                }

                return INVALID;
            }

            final XbSecureToken token = this.validateTokenRemote();

            if ((token == null) || !token.isAuthenticated()) {
                throw new RuntimeException();
            }

            UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, ServletAuthRequestWrapper.getClientAddr(this.request)));

            this.session.setAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV, token);
            final XbSecureToken tClone = XbSecureToken.fromJson(token.toJson());
            tClone.setSid("");

            if ((this.contentType != null) && (this.contentType.equalsIgnoreCase("application/xml"))) {
                final StringWriter writer = new StringWriter();
                final JAXBContext context = JAXBContext.newInstance(XbSecureToken.class);
                final Marshaller m = context.createMarshaller();
                m.marshal(tClone, writer);
                this.tokenInputStream = new ByteArrayInputStream(writer.toString().getBytes());
            }
            else {
                this.tokenInputStream = new ByteArrayInputStream(tClone.toJson().getBytes());
            }

            if(auditor.isAuditEnabled()) {
                auditor.audit(auditor.createLogonSuccess("Token validation succeeded"));
            }

            return SUCCESS;
        }
        catch (Exception e) {
        }

        if(auditor.isAuditEnabled()) {
            auditor.audit(auditor.createLogonFailure("Token validation failed"));
        }

        return ERROR;
    }

    public String validateTokenS2S() throws Exception {
        if(this.tokenRef == null) {
            return INVALID;
        }

        if(this.otoken != null) {
            if(this.tokenRef.indexOf(this.otoken.getTokenRefId()) < 0) {
                return INVALID;
            }

            final XbSecureToken token =
                    this.auth.authenticate(AuthRequestWrapperFactory.createAuthRequestWrapper(this.request), this.otoken, new StringBuilder());

            if(token == null) {
                return ERROR;
            }
            else {
                this.session.setAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV, token);
                this.tokenInputStream = new ByteArrayInputStream(token.toJson().getBytes());

                return SUCCESS;
            }
        }
        else {
            return ERROR;
        }
    }

    public String invalidateToken() throws Exception {
        final IAudit auditor = Auditor.getInstance().getAuditor();
        XbSecureToken token = null;

        try {
            token = (XbSecureToken) this.session.getAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);
            UserContext.instance.set(new UserContext("", "", null, null, ServletAuthRequestWrapper.getClientAddr(this.request)));
        }
        catch (Exception e) {
        }

        if(token != null) {
            UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, ServletAuthRequestWrapper.getClientAddr(this.request)));
        }

        if(TokenService.isLocalAuth) {
            return this.invalidateTokenS2S();
        }

        if(this.tokenRef == null) {
            return INVALID;
        }

        this.invalidateTokenRemote();
        this.tokenInputStream = new ByteArrayInputStream(new byte[0]);

        if(auditor.isAuditEnabled()) {
            auditor.audit(auditor.createLogoutSuccess("Token has been invalidated."));
        }

        return SUCCESS;
    }

    public String invalidateTokenS2S() throws Exception {
        if(this.tokenRef == null) {
            return INVALID;
        }

        if((this.otoken == null) && (this.tokenRef.indexOf(this.otoken.getTokenRefId()) < 0)) {
            return INVALID;
        }

        this.session.invalidate();
        this.tokenInputStream = new ByteArrayInputStream(new byte[0]);

        return SUCCESS;
    }

    // PARAMS SETTERS

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenRef() {
        return tokenRef;
    }

    public void setTokenRef(String tokenRef) {
        this.tokenRef = tokenRef;
    }

    public InputStream getTokenInputStream() {
        return tokenInputStream;
    }

    // PRIVATE METHODS

    private XbSecureToken getTokenRemote() {
        HttpURLConnection rc = this.getConnection(
                XbUtils.createGetTokenUrl(this.props, this.session.getServletContext().getContextPath().substring(1), this.enableTokenDelegation),
                null,
                false
        );

        if(rc == null) {
            return new XbSecureToken();
        }

        InputStream is = null;

        try {
            if (rc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = rc.getInputStream();
                return XbSecureToken.fromJson(is);
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            return new XbSecureToken();
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }

    private XbSecureToken validateTokenRemote() {
        final String[] ids = this.tokenRef.split("-");

        HttpURLConnection rc = this.getConnection(
                    XbUtils.createValidateTokenUrl(this.props, this.session.getServletContext().getContextPath().substring(1), this.enableTokenDelegation),
                    ids[0],
                    true
        );

        if(rc == null) {
            return new XbSecureToken();
        }

        InputStream is = null;

        try {
            if (rc.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (ids.length > 1) {
                    rc = this.getConnection(
                            XbUtils.createValidateTokenUrl(this.props, this.session.getServletContext().getContextPath().substring(1), this.enableTokenDelegation),
                            ids[1],
                            true
                    );

                    if (rc == null) {
                        return new XbSecureToken();
                    }

                    if (rc.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                    else {
                        is = rc.getInputStream();
                        return XbSecureToken.fromJson(is);
                    }
                }
                else {
                    return null;
                }
            }
            else {
                is = rc.getInputStream();
                return XbSecureToken.fromJson(is);
            }
        }
        catch (Exception e) {
            return new XbSecureToken();
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }

    private int invalidateTokenRemote() {
        final String[] ids = this.tokenRef.split("-");

        try {
            HttpURLConnection connection = this.getConnection(
                    XbUtils.createInvalidateTokenUrl(this.props, this.session.getServletContext().getContextPath().substring(1), enableTokenDelegation),
                    ids[0],
                    true
            );

            if ((connection == null) || (connection.getResponseCode() != HttpURLConnection.HTTP_OK)) {
                if (ids.length > 1) {
                    connection = this.getConnection(
                            XbUtils.createInvalidateTokenUrl(this.props, this.session.getServletContext().getContextPath().substring(1), this.enableTokenDelegation),
                            ids[1],
                            true
                    );

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                         return -1;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    return -1;
                }
            }
            else {
                return 0;
            }
        }
        catch (Exception e) {
            return -1;
        }
    }

    private HttpURLConnection getConnection(final String url, final String sid, final boolean isTokenRef) {
        try {
            final HttpURLConnection connection = XbConnection.getConnection(new URL(url));

            if(sid != null) {
                connection.setRequestProperty("Cookie", "JSESSIONID=" + InputValidator.validateHttpCookieVal(sid));
            }

            connection.setRequestProperty("Authorization", InputValidator.validateHttpHeaderVal(this.createAuthorizationHeader(isTokenRef)));
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setReadTimeout(5000);
            connection.connect();
            connection.getResponseCode();
            return connection;
        }
        catch(Exception e) {
            if(TokenService.logger.isTraceEnabled()) {
                TokenService.logger.trace(
                    "Failed to connect to Token Service on " +
                    props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                    XbUtils.getIpPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) + ":" + props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                    XbUtils.getPortPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_PORT)
                );
            }

            return null;
        }
    }

    private String createAuthorizationHeader(final boolean isTokenRef) {
         String res = "";

         if(isTokenRef) {
             if(this.tokenRef != null) {
                 res = this.tokenRef;
             }
         }
         else {
             if((this.userName != null) && (this.password != null)) {
                 if(this.directory != null) {
                     res = this.directory + "\\" + this.userName + ":" + this.password;
                 }
                 else {
                     res = this.userName + ":" + this.password;
                 }
             }
             else {
                 if(this.userName != null) {
                     if(this.directory != null) {
                         res = this.directory + "\\" + this.userName;
                     }
                     else {
                         res = this.userName;
                     }
                 }
             }
         }

         return "Basic " + Base64.encodeBase64String(res.getBytes());
     }

    private void setParams() {
        String[] params;

        try {
            params = XbUtils.parseAuthorizationHeader(this.request.getHeader("Authorization"));

            if(!XbUtils.isEmpty(params[0])) {
                this.tokenRef = params[0];
            }

            if(!XbUtils.isEmpty(params[1])) {
                this.directory = params[1];
            }

            if(!XbUtils.isEmpty(params[2])) {
                this.userName = params[2];
            }

            if(!XbUtils.isEmpty(params[3])) {
                this.password = params[3];
            }
        }
        catch (Exception e) {
        }
    }

    public String getAppclass() {
        return appclass;
    }

    public void setAppclass(String appclass) {
        this.appclass = appclass;
    }
}