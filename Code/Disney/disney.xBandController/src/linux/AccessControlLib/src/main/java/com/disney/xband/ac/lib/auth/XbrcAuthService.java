package com.disney.xband.ac.lib.auth;

import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.security.UserContext;
import org.apache.commons.codec.binary.Base64;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/28/12
 * Time: 11:43 AM
 */
/**
 * This provider authenticates requests that do not have XbSecureToken against xAG token service.
 */
public class XbrcAuthService extends LocalAuthService {
    @Override
    public void init(final HashMap<String, String> props) {
        super.init(props);
    }

    @Override
    protected XbSecureToken authenticateHttpBasic(IAuthRequestWrapper request) {
        final String basicAuthPasswd = this.props.get(PkConstants.NAME_PROP_AC_HTTP_BASIC_AUTH_PASSWD);
        String auth = request.getHeader("Authorization");

        if (XbUtils.isEmpty(auth) || (auth.length() < 6)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + "). Authorization header: " + auth);
            }

            return null;
        }

        auth = auth.substring(6);

        if (XbUtils.isEmpty(auth)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + "). Authorization header: " + auth);
            }

            return null;
        }

        try {
            final XbSecureToken token = XbSecureToken.decode(auth);

            if (token == null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("HTTP Basic: failed to deserialize xConnect token");
                }

                return null;
            }

            if(XbUtils.isEmpty(basicAuthPasswd)) {
                token.setAuthenticated(true);
                return token;
            }

            if (XbUtils.isEmpty(token.getAuthData())) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + "). AuthData() is not present");
                }

                return null;
            }

            if (!basicAuthPasswd.equals(token.getAuthData())) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + ") due to password mismatch.");
                }

                return null;
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication succeeded for request to (" + request.getRequestURL().toString() + ") on behalf of user " + token.getLogonName());
            }

            token.setAuthenticated(true);
            UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, request.getClientAddress()));
            return token;
        }
        catch (Exception e) {
            try {
                final String creds = new String(Base64.decodeBase64(auth));
                final int ind = creds.indexOf(":");
                final String user = creds.substring(0, ind);
                final String pwd = creds.substring(ind + 1);
                final XbSecureToken token = this.getToken(user, pwd);

                if(token != null) {
                    UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, request.getClientAddress()));
                    return token;
                }
            }
            catch(Exception e2) {
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + "). Authorization header: " + auth);
            }

            return null;
        }
    }
}
