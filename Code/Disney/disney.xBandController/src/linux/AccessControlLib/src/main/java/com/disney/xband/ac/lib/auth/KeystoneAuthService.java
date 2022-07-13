package com.disney.xband.ac.lib.auth;

import com.bitkoo.keystone.Auth;
import com.bitkoo.keystone.authwebservice.*;
import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache;
import com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCacheDao;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.security.SecurityUtils;
import com.disney.xband.common.lib.security.UserContext;
import org.apache.commons.codec.binary.Base64;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/28/12
 * Time: 11:57 AM
 */
public class KeystoneAuthService extends InternalAuthService {
    private static final String KS_TOKEN = "com.bitkoo.keystone.authwebservice.AuthToken";

    private List<String> dirList;
    private long reauthSecs;
    private HashMap<String, String> props;
    private boolean enableKeystoneAuthorization;

    private static class AuthTokenWrapper implements Serializable {
        private final AuthToken token;

        private AuthTokenWrapper(AuthToken token) {
            this.token = token;
        }

        private AuthToken get() {
            return this.token;
        }
    }

    @Override
    public void init(HashMap<String, String> props) {
        super.init(props);

        try {
            // In test environments and possibly Alpha lab, xAG will be connecting to Webvan server using mutual
            // X.509 authentication. This call give XbConnection module a chance to initialize PKI stuff.
            XbConnection.init(this.props);
        }
        catch(Exception e) {
            this.logger.warn("Failed to initialize X.509 client authentication for access to Keystone server", e);
        }

        this.dirList = new ArrayList<String>(16);

        try {
            final Ws authServiceWS =
                new Ws(new URL(props.get(PkConstants.NAME_PROP_AC_KS_PRI_URL)), new QName("http://bitkoo.com/keystone", "ws"));
            final List<DirectoryName> list = authServiceWS.getWsSoap().getDirectoryNames().getDirectoryName();

            for(DirectoryName entry : list) {
                this.dirList.add(entry.getPrefix());
            }
        }
        catch(Exception e) {
            this.logger.warn("Failed to retrieve a list of directories from Keystone server. Will be using a default one.");
        }

        if (this.dirList.size() == 0) {
            final String dirs = props.get(PkConstants.NAME_PROP_AC_KS_DIRS);

            if(XbUtils.isEmpty(dirs)) {
                this.logger.error(PkConstants.NAME_PROP_AC_KS_DIRS + " property must be populated!");
                return;
            }

            final String[] dirsList = dirs.split(",");

            if((dirsList == null) || (dirsList.length == 0)) {
                this.logger.error(PkConstants.NAME_PROP_AC_KS_DIRS + " property must not be empty!");
                return;
            }

            this.dirList.addAll(Arrays.asList(dirsList));
        }

        this.reauthSecs = Long.parseLong(props.get(PkConstants.NAME_PROP_AC_KS_REAUTH_SECS));
        this.enableKeystoneAuthorization = Boolean.parseBoolean(props.get(PkConstants.NAME_PROP_AC_KS_ENABLE_AUTHORIZATION));
        this.props = props;
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request, XbSecureToken token, StringBuilder err) {
        assert(token != null);

        final AuthToken ktoken = this.getAuthTokenFromSession(request);
        final Auth auth = this.createAuth();

        return this.reauthenticate(request, auth, ktoken, token.getLogonName(), err);
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request, String directory, String name, String password, StringBuilder err) {
        UserContext.instance.set(
            new UserContext(
                directory + "\\" + name,
                null,
                AuditEvent.AppClass.XBRMS.toString(),
                null,
                request.getClientAddress()
            )
        );

        if(this.dirList.size() == 0) {
            this.logger.error("xAG: Authentication service is not configured. Please contact the system administrator.");
            err.append("Authentication service is not configured. Please contact the system administrator.");

            return null;
        }

        final AuthToken ktoken = this.getAuthTokenFromSession(request);
        Auth auth = this.createAuth();

        if(ktoken != null) {
            return this.reauthenticate(request, auth, ktoken, name, err);
        }

        // final boolean result = auth.Login(directory.toLowerCase(), name, password)
        //boolean result = auth.Login(directory.toLowerCase(), new Object[] {name, password});
        boolean result = false;
        boolean isOfflineMode = false;
        final LogonCache interceptor =
            (LogonCache) Auditor.getInstance().getAuditFactory().getAuditControl().getInterceptor("com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache");
        final int conTO = interceptor == null ? 15000 : interceptor.getConnectionToMs();

        try {
            result = (Boolean) XbUtils.tryAction(
                KeystoneAuthService.class,
                "logon",
                new Class[] {Auth.class, String.class, String.class, String.class},
                new Object[] {auth, directory.toLowerCase(), name, password},
                conTO,
                this);
        }
        catch (Throwable t) {
            logger.warn("Keystone operation timed out when authenticating user " + name);
            isOfflineMode = true;
        }

        String rhash = null;
        XbSecureToken rtoken = null;

        if(isOfflineMode || (!result && (auth.getMessage() != null) && (auth.getMessage().indexOf("InaccessibleWSDLException") >= 0)))  {
            isOfflineMode = true;

            if(interceptor != null) {
                final String offlineData = interceptor.getOfflineUserData(directory + "\\" + name);

                try {
                    if(offlineData != null) {
                        rtoken = (XbSecureToken) XbUtils.objectFromXmlString(new String(Base64.decodeBase64(offlineData)), XbSecureToken.class);
                    }
                }
                catch (Exception e) {
                    this.logger.error("Failed to decode logon data for user " + name);
                }

                if(rtoken != null) {
                    rhash = rtoken.getPwHash();
                    result = SecurityUtils.isHashValid(password, rhash);
                }
            }
        }

        if(result) {
            if(this.logger.isTraceEnabled()) {
                if(isOfflineMode) {
                    this.logger.trace("Keystone offline logon succeeded");
                }
                else {
                    this.logger.trace("Keystone logon succeeded");
                }
            }
        }
        else {
            if(this.logger.isInfoEnabled()) {
                if(isOfflineMode) {
                    this.logger.info("Keystone logon failed");
                }
                else {
                    this.logger.info("Keystone offline logon failed");
                }
            }

            err.append("Authentication failed");
            return null;
        }

        final XbSecureToken ret = this.createToken(request, auth, name, directory + "\\" + name, err, rtoken);
        this.decorateToken(ret, request);

        if(isOfflineMode) {
            ret.setOffLineMode(true);

            UserContext.instance.set(
                new UserContext(ret.getDisplayName(), ret.getInitSid(), null, ret, request.getClientAddress())
            );
        }
        else {
            ret.setPwHash(SecurityUtils.getSecureHash(password));
            String serToken = null;

            try {
                serToken = Base64.encodeBase64String(XbUtils.objectToXmlString(ret, XbSecureToken.class).getBytes());

                UserContext.instance.set(
                    new UserContext(
                        ret.getDisplayName(),
                        ret.getInitSid(),
                        null,
                        ret,
                        request.getClientAddress(),
                        serToken
                    )
                );
            }
            catch (Exception e) {
                logger.error("Failed to save in UserContext logon data for user " + name);
            }

            if(serToken != null) {
                final AuditConfig auditConfig = Auditor.getInstance().getConfig();

                if(auditConfig.isEnabled()) {
                    try {
                        name = name.toUpperCase();
                        LogonCacheDao.getInstance().saveSerToken(auditConfig.getConnectionPool(), ret.getDisplayName(), serToken);
                    }
                    catch (Exception ignore) {
                        logger.error("Failed to persist logon data for user " + name);
                    }
                }
            }
        }

        return ret;
    }

    @Override
    public List<String> getDirectories() {
        return this.dirList;
    }

    private XbSecureToken createToken(
        IAuthRequestWrapper request,
        Auth auth,
        String name,
        String displayName,
        StringBuilder err,
        XbSecureToken offlineToken
     ) {
        final XbSecureToken token = new XbSecureToken();
        token.setAuthenticated(true);
        token.setLogonName(name);
        token.setDisplayName(displayName);
        token.setAuthType(XbSecureToken.AuthType.FORM);
        token.setAuthTime(System.currentTimeMillis());
        token.setSid(request.getSessionId());

        if (offlineToken == null) {
            try {
                final AuthorizationAssertion assertion = auth.getAssertion();
                final Iterator<Role> roles = assertion.getRoles().getRole().listIterator();
                final List<String> roleNames = new ArrayList<String>();
                final Set<String> abilityNames = new HashSet<String>();

                while (roles.hasNext()) {
                    final Role role = roles.next();
                    roleNames.add(role.getName());

                    if (this.enableKeystoneAuthorization) {
                        try {
                            final Iterator<FunctionalAbility> abilities = role.getFunctionalAbilities().getFunctionalAbility().listIterator();

                            while (abilities.hasNext()) {
                                final FunctionalAbility ability = abilities.next();
                                abilityNames.add(ability.getName());
                            }
                        }
                        catch (Exception e) {
                            this.logger.warn("Failed to get a list of functional abilities from Keystone token: " + e.toString());
                            err.append("Authentication Failed (Functional Abilities List)");
                            return null;
                        }
                    }
                }

                token.setFunctionalAbilities(new ArrayList(abilityNames));
                token.setRoles(roleNames);
            }
            catch (Exception e) {
                this.logger.warn("Failed to get a list of roles: " + e.toString());
                err.append("Authentication Failed (Roles List)");
                return null;
            }

            if (this.enableKeystoneAuthorization) {
                try {
                    final AuthorizationAssertion assertion = auth.getAssertion();
                    final Iterator<Entity> resources = assertion.getSecuredEntities().getEntity().listIterator();
                    final List<String> resourceNames = new ArrayList<String>();

                    while (resources.hasNext()) {
                        final Entity resource = resources.next();
                        resourceNames.add(resource.getName());
                    }

                    token.setSecuredEntities(resourceNames);
                }
                catch (Exception e) {
                    this.logger.warn("Failed to get a list of protected entities from Keystone token: " + e.toString());
                    err.append("Authentication Failed (Protected Entities List)");
                    return null;
                }
            }

            request.setSessionAttribute(KeystoneAuthService.KS_TOKEN, new AuthTokenWrapper(auth.getKeystoneToken()));
        }
        else {
            token.setRoles(offlineToken.getRoles());

            if (this.enableKeystoneAuthorization) {
                token.setFunctionalAbilities(offlineToken.getFunctionalAbilities());
                token.setSecuredEntities(offlineToken.getSecuredEntities());
            }

            token.setPwHash(offlineToken.getPwHash());
            token.setAuthData(offlineToken.getAuthData());
            request.setSessionAttribute(KeystoneAuthService.KS_TOKEN, new AuthTokenWrapper(new AuthToken()));
        }

        return token;
    }

    private Auth createAuth() {
        final Auth auth = new Auth();
        auth.setApplicationId(this.props.get(PkConstants.NAME_PROP_AC_KS_APP_ID));
        auth.setPrimaryAuthURL(this.props.get(PkConstants.NAME_PROP_AC_KS_PRI_URL));
        auth.setSecondaryAuthURL(this.props.get(PkConstants.NAME_PROP_AC_KS_SEC_URL));

        return auth;
    }

    private XbSecureToken reauthenticate(IAuthRequestWrapper request, Auth auth, AuthToken ktoken, String name, StringBuilder err) {
        final XbSecureToken token = (XbSecureToken) request.getSessionAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);

        if(
            (this.reauthSecs == 0) ||
            (this.reauthSecs > (System.currentTimeMillis() - token.getAuthTime()) / 1000) ||
            token.isOffLineMode() ||
            (ktoken.getToken() == null)
        ) {
            token.setAuthTime(System.currentTimeMillis());
            return token;
        }

        final boolean result = auth.Login(ktoken);
        // boolean result = auth.Login(ktoken);
        // result = false;

        if(result) {
            if(this.logger.isTraceEnabled()) {
                 this.logger.trace("Keystone re-authentication succeeded");
            }

            request.setSessionAttribute(KeystoneAuthService.KS_TOKEN, new AuthTokenWrapper(auth.getKeystoneToken()));
        }
        else {
            request.removeSessionAttribute(KeystoneAuthService.KS_TOKEN);

            if(this.logger.isInfoEnabled()) {
                 this.logger.info("Keystone re-authentication failed");
            }

            err.append("Authentication failed");
            return null;
        }

        final XbSecureToken ntoken = this.createToken(request, auth, name, token.getDisplayName(), err, null);
        UserContext.instance.set(new UserContext(ntoken.getDisplayName(), ntoken.getInitSid(), null, ntoken, request.getClientAddress()));
        return ntoken;
    }

    private AuthToken getAuthTokenFromSession(final IAuthRequestWrapper request) {
        final AuthTokenWrapper tw = (AuthTokenWrapper) request.getSessionAttribute(KeystoneAuthService.KS_TOKEN);

        if(tw != null) {
            return tw.get();
        }

        return null;
    }

    public boolean logon(final Auth auth, final String directory, final String name, final String password) {
        return auth.Login(directory.toLowerCase(), new Object[] {name, password});
    }
}
