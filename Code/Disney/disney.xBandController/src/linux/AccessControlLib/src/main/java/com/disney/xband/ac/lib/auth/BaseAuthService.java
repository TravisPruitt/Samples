package com.disney.xband.ac.lib.auth;

import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.IAuthService;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.common.lib.security.UserContext;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 7/17/12
 * Time: 1:29 PM
 */
public class BaseAuthService implements IAuthService {
    protected Logger logger;
    protected HashMap<String, String> props;
    protected OmniService omniService;

    @Override
    public void init(HashMap<String, String> props) {
        assert(props != null);

        this.logger = Logger.getLogger(this.getClass());
        this.props = props;
        this.omniService = new OmniService();
        this.omniService.init(props);
    }

    @Override
    public List<String> getDirectories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request, XbSecureToken token, StringBuilder err) {
        assert(token != null);

        token.setAuthTime(System.currentTimeMillis());
        UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, request.getClientAddress()));
        return token;
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request, String directory, String name, String password, StringBuilder err) {
        throw new UnsupportedOperationException();
    }

    protected XbSecureToken decorateToken(XbSecureToken token, IAuthRequestWrapper req) {
        final String appId = XbUtils.getAppIdFromUrl(req.getRequestURL().toString());
        OmniService.OmniUser omniUser = null;

        if("UI".equals(appId)) {
            omniUser = this.omniService.getOmniUser(token.getLogonName());
        }

        if(omniUser == null) {
            omniUser = new OmniService.OmniUser(token.getLogonName(), "", "");
        }

        token.setOmniUser(omniUser.getOmniUsername());
        token.setOmniPw(omniUser.getOmniPassword());

        UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, req.getClientAddress()));

        return token;
    }
}
