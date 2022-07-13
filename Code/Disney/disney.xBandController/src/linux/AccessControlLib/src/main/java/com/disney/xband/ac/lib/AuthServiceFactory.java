package com.disney.xband.ac.lib;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/25/12
 * Time: 12:25 PM
 */
public class AuthServiceFactory {
    private static IAuthService authService;
    private static IAuthService authServiceLocal;

    public static synchronized IAuthService getAuthService(HashMap<String, String> props) {
        if (AuthServiceFactory.authService == null) {
            String authClass = props.get(PkConstants.NAME_PROP_AC_AUTH_SERVICE_CLASS);

            IAuthService auth;

            try {
                if(authClass.indexOf("KeystoneWebVanAuth") >= 0) {
                    final Object resolve = Class.forName("com.bitkoo.keystone.AuthWebVan").newInstance();
                }
                else {
                    if(authClass.indexOf("KeystoneAuth") >= 0) {
                        final Object resolve = Class.forName("com.bitkoo.keystone.Auth").newInstance();
                    }
                }

                auth = (IAuthService) Class.forName(authClass).newInstance();
                auth.init(props);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            AuthServiceFactory.authService = auth;
        }

        return AuthServiceFactory.authService;
    }

    public static synchronized IAuthService getLocalAuthService(HashMap<String, String> props) {
        return AuthServiceFactory.getLocalAuthService(props, false);
    }

    public static synchronized IAuthService getLocalAuthService(Properties props, boolean isXbrc) {
        return AuthServiceFactory.getLocalAuthService(XbUtils.propsToHashMap(props), isXbrc);
    }

    public static synchronized IAuthService getLocalAuthService(HashMap<String, String> props, boolean isXbrc) {
        if (AuthServiceFactory.authServiceLocal == null) {

            final String authClass = isXbrc ?
                props.get(PkConstants.NAME_PROP_AC_XBRC_LOCAL_AUTH_SERVICE_CLASS) :
                props.get(PkConstants.NAME_PROP_AC_LOCAL_AUTH_SERVICE_CLASS
            );

            IAuthService auth;

            try {
                if(authClass.indexOf("KeystoneWebVanAuth") >= 0) {
                    final Object resolve = Class.forName("com.bitkoo.keystone.AuthWebVan").newInstance();
                }
                else {
                    if(authClass.indexOf("KeystoneAuth") >= 0) {
                        final Object resolve = Class.forName("com.bitkoo.keystone.Auth").newInstance();
                    }
                }

                auth = (IAuthService) Class.forName(authClass).newInstance();
                auth.init(props);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            AuthServiceFactory.authServiceLocal = auth;
        }

        return AuthServiceFactory.authServiceLocal;
    }
}
