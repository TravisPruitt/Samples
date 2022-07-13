package com.disney.xband.ac.lib.auth;

import com.disney.xband.ac.lib.*;
import com.disney.xband.ac.lib.auth.model.XbUserType;
import com.disney.xband.ac.lib.auth.model.XbUsersType;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.common.lib.security.UserContext;

import javax.xml.bind.JAXBContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/25/12
 * Time: 12:22 PM
 */
public class InternalAuthService extends BaseAuthService {
    private HashMap<String, String> userPass;
    private HashMap<String, HashSet<String>> userRoles;

    @Override
    public void init(HashMap<String, String> props) {
        super.init(props);

        InputStream is = null;
        final XbUsersType users;

        try {
            String path = (String) props.get(PkConstants.NAME_PROP_AC_USERS_FILE);

            if (path != null) {
                try {
                    // For Douglas Morato: The input stream gets closed in the finally clause
                    is = new FileInputStream(InputValidator.validateFilePath(path));
                }
                catch (Exception e) {
                    this.logger.fatal("Failed to read users file [" + path + "] ", e);
                    throw new RuntimeException(e);
                }
            }
            else {
                props.put(PkConstants.NAME_PROP_AC_USERS_FILE, PkConstants.DEFAULT_AC_USERS_FILE);
            }

            if (is == null) {
                path = props.get(PkConstants.NAME_PROP_AC_USERS_PATH);

                try {
                    // For Douglas Morato: The "is" is null at this point. The input stream gets closed in the finally clause
                    is = new FileInputStream(InputValidator.validateFilePath(path));
                }
                catch (Exception e) {
                    this.logger.fatal("Failed to read users file [" + path + "] ", e);
                    throw new RuntimeException(e);
                }
            }

            try {
                final JAXBContext context = JAXBContext.newInstance(XbUsersType.class);
                users = (XbUsersType) context.createUnmarshaller().unmarshal(is);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
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

        this.userPass = new HashMap<String, String>(8);
        this.userRoles = new HashMap<String, HashSet<String>>(8);

        final String defaultRole = users.getDefaultRole();
        final Iterator<XbUserType> it = users.getXbUser().iterator();

        while(it.hasNext()) {
            final XbUserType user = it.next();

            if(this.userPass.containsKey(user.getName())) {
                continue;
            }

            this.userPass.put(user.getName(), user.getPassword());

            final HashSet<String> roles = new HashSet<String>(4);

            if(user.getRole() != null) {
                final Iterator<String> it2 = user.getRole().iterator();

                while(it2.hasNext()) {
                    final String role = it2.next();

                    if(!roles.contains(role)) {
                        roles.add(role);
                    }
                }
            }

            if(roles.size() == 0) {
                roles.add(defaultRole);
            }

            this.userRoles.put(user.getName(), roles);
        }
    }

    @Override
    public List<String> getDirectories() {
        return null;
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request, String directory, String name, String password, StringBuilder err) {
        if (name != null) {
            if (password == null) {
                password = "";
            }

            if (password.equals(this.userPass.get(name))) {
                final XbSecureToken token = new XbSecureToken();
                token.setAuthenticated(true);
                token.setLogonName(name);
                token.setDisplayName(name);
                final List<String> roles = new ArrayList<String>(this.userRoles.get(name));
                token.setRoles(roles);
                token.setAuthType(XbSecureToken.AuthType.FORM);
                token.setAuthTime(System.currentTimeMillis());
                token.setSid(request.getSessionId());

                this.decorateToken(token, request);
                UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, request.getClientAddress()));
                return token;
            }
        }

        err.append("Incorrect user name or password");
        return null;
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request, XbSecureToken token, StringBuilder err) {
        assert(token != null);

        token.setAuthTime(System.currentTimeMillis());
        UserContext.instance.set(new UserContext(token.getLogonName(), token.getInitSid(), null, token, request.getClientAddress()));
        return token;
    }

    /*
    public static void main(String[] args) {
        HashMap<String, String> props = new HashMap<String, String>(8);
        props.put(PkConstants.NAME_PROP_AC_USERS_FILE, "/usr/share/disney.xband/AC/xb-users.xml");
        props.put(PkConstants.NAME_PROP_AC_AUTH_SERVICE_CLASS, PkConstants.DEFAULT_AC_AUTH_SERVICE_CLASS);

        IAuthService auth = AuthServiceFactory.getAuthService(props, null);
        auth.authenticate(null, "admin", "admin");
    }
    */
}
