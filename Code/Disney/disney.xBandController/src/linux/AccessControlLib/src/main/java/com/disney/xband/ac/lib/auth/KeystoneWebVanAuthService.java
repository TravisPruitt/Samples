package com.disney.xband.ac.lib.auth;

import com.bitkoo.keystone.AuthWebVan;
import com.bitkoo.keystone.authwebservice.*;
import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.security.SecurityUtils;
import com.disney.xband.common.lib.security.UserContext;

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
public class KeystoneWebVanAuthService extends InternalAuthService {
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
            final WsWebVan authServiceWS = new WsWebVan(new URL(props.get(PkConstants.NAME_PROP_AC_KS_PRI_URL)), new QName("http://bitkoo.com/keystone", "Keystone3_Authorization_Service_-_DEV"));
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
        final AuthWebVan auth = this.createAuth();

        return this.reauthenticate(request, auth, ktoken, token.getLogonName(), err);
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request, String directory, String name, String password, StringBuilder err) {
        if(this.dirList.size() == 0) {
            this.logger.error("xAG: Authentication service is not configured. Please contact the system administrator.");
            err.append("Authentication service is not configured. Please contact the system administrator.");
            return null;
        }

        final AuthToken ktoken = this.getAuthTokenFromSession(request);
        final AuthWebVan auth = this.createAuth();

        if(ktoken != null) {
            return this.reauthenticate(request, auth, ktoken, name, err);
        }

        // final boolean result = auth.Login(directory.toLowerCase(), name, password);
        boolean result = auth.Login(directory.toLowerCase(), new Object[] {name, password});

        if(result) {
            if(this.logger.isTraceEnabled()) {
                 this.logger.trace("Keystone logon succeeded");
            }
        }
        else {
            if(this.logger.isInfoEnabled()) {
                 this.logger.info("Keystone logon failed");
            }

            err.append("Authentication failed");
            return null;
        }

        final XbSecureToken ret = this.createToken(request, auth, name, directory + "\\" + name, err);
        this.decorateToken(ret, request);

        UserContext.instance.set(
                new UserContext(ret.getLogonName(), ret.getInitSid(), null, ret, request.getClientAddress(), SecurityUtils.getSecureHash(password))
        );

        return ret;
    }

    @Override
    public List<String> getDirectories() {
        return this.dirList;
    }

    private XbSecureToken createToken(IAuthRequestWrapper request, AuthWebVan auth, String name, String displayName, StringBuilder err) {
        final XbSecureToken token = new XbSecureToken();
        token.setAuthenticated(true);
        token.setLogonName(name);
        token.setDisplayName(displayName);

        try {
            final Iterator<Role> roles = auth.getAssertion().getRoles().getRole().listIterator();
            final List<String> roleNames = new ArrayList<String>();
            final Set<String> abilityNames = new HashSet<String>();

            while(roles.hasNext()) {
                final Role role = roles.next();
                roleNames.add(role.getName());

                if(this.enableKeystoneAuthorization) {
                    try {
                        final Iterator<FunctionalAbility> abilities = role.getFunctionalAbilities().getFunctionalAbility().listIterator();

                        while(abilities.hasNext()) {
                            final FunctionalAbility ability = abilities.next();
                            abilityNames.add(ability.getName());
                        }
                    }
                    catch(Exception e) {
                        this.logger.warn("Failed to get a list of functional abilities from Keystone token: " + e.toString());
                        err.append("Authentication Failed (Functional Abilities List)");
                        return null;
                    }
                }
            }

            token.setFunctionalAbilities(new ArrayList(abilityNames));
            token.setRoles(roleNames);
        }
        catch(Exception e) {
            this.logger.warn("Failed to get a list of roles: " + e.toString());
            err.append("Authentication Failed (Roles List)");
            return null;
        }

        if(this.enableKeystoneAuthorization) {
            try {
                final Iterator<Entity> resources = auth.getAssertion().getSecuredEntities().getEntity().listIterator();
                final List<String> resourceNames = new ArrayList<String>();

                while(resources.hasNext()) {
                    final Entity resource = resources.next();
                    resourceNames.add(resource.getName());
                }

                token.setSecuredEntities(resourceNames);
            }
            catch(Exception e) {
                this.logger.warn("Failed to get a list of protected entities from Keystone token: " + e.toString());
                err.append("Authentication Failed (Protected Entities List)");
                return null;
            }
        }

        token.setAuthType(XbSecureToken.AuthType.FORM);
        token.setAuthTime(System.currentTimeMillis());
        token.setSid(request.getSessionId());
        request.setSessionAttribute(KeystoneWebVanAuthService.KS_TOKEN, new AuthTokenWrapper(auth.getKeystoneToken()));

        return token;
    }

    private AuthWebVan createAuth() {
        final AuthWebVan auth = new AuthWebVan();
        auth.setApplicationId(this.props.get(PkConstants.NAME_PROP_AC_KS_APP_ID));
        auth.setPrimaryAuthURL(this.props.get(PkConstants.NAME_PROP_AC_KS_PRI_URL));
        auth.setSecondaryAuthURL(this.props.get(PkConstants.NAME_PROP_AC_KS_SEC_URL));

        return auth;
    }

    private XbSecureToken reauthenticate(IAuthRequestWrapper request, AuthWebVan auth, AuthToken ktoken, String name, StringBuilder err) {
        final XbSecureToken token = (XbSecureToken) request.getSessionAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN_SRV);

        if((this.reauthSecs == 0) || (this.reauthSecs > (System.currentTimeMillis() - token.getAuthTime()) / 1000)) {
            return token;
        }

        final boolean result = auth.Login(ktoken);
        // boolean result = auth.Login(ktoken);
        // result = false;

        if(result) {
            if(this.logger.isTraceEnabled()) {
                 this.logger.trace("Keystone re-authentication succeeded");
            }

            request.setSessionAttribute(KeystoneWebVanAuthService.KS_TOKEN, new AuthTokenWrapper(auth.getKeystoneToken()));
        }
        else {
            request.removeSessionAttribute(KeystoneWebVanAuthService.KS_TOKEN);

            if(this.logger.isInfoEnabled()) {
                 this.logger.info("Keystone re-authentication failed");
            }

            err.append("Authentication failed");
            return null;
        }

        final XbSecureToken ntoken = this.createToken(request, auth, name, token.getDisplayName(), err);
        UserContext.instance.set(new UserContext(ntoken.getLogonName(), ntoken.getInitSid(), null, ntoken, request.getClientAddress()));
        return ntoken;
    }

    private AuthToken getAuthTokenFromSession(final IAuthRequestWrapper request) {
        final AuthTokenWrapper tw = (AuthTokenWrapper) request.getSessionAttribute(KeystoneWebVanAuthService.KS_TOKEN);

        if(tw != null) {
            return tw.get();
        }

        return null;
    }
}
