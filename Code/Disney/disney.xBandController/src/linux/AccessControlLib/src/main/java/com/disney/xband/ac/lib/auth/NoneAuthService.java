package com.disney.xband.ac.lib.auth;

import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbSecureToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 7/24/12
 * Time: 2:14 PM
 */
/**
 * Authentication through this provider always succeeds.The list of roles will contain a configurable default role.
 */
public class NoneAuthService extends BaseAuthService {
    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request) {
        final XbSecureToken token = new XbSecureToken();
        final String role = this.props.get(PkConstants.NAME_PROP_AC_SERVICE_ACCT_ROLE);
        final List<String> roles = new ArrayList<String>();
        token.setLogonName(role);
        token.setDisplayName(role);
        roles.add(role);
        token.setRoles(roles);
        token.setAuthType(XbSecureToken.AuthType.NONE);
        token.setAuthTime(System.currentTimeMillis());

        return token;
    }
}
