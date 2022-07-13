package com.disney.xband.ac.lib.client;

import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.XbSecureToken;

import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/18/12
 * Time: 5:30 PM
 */
public interface IAcManager {
    boolean isProtected(IAuthRequestWrapper request);
    boolean isProtected(String url);
    boolean isProtected(String url, String method);
    boolean isLogout(IAuthRequestWrapper request, String logoutUrl);
    boolean isLogout(String url, String logoutUrl);
    boolean isLocalAuth(IAuthRequestWrapper request);
    boolean isLocalAuth(String url);
    boolean isLocalAuth(String url, String method);

    List<String> getRoles(XbSecureToken token);
    boolean isUserInRole(XbSecureToken token, String roleName);

    List<String> getAssets(XbSecureToken token);
    List<String> getDenyAssets(XbSecureToken token);
    boolean canAccessAsset(XbSecureToken token, String assetName);

    boolean isAuthorized(XbSecureToken token, IAuthRequestWrapper request);
    boolean isAuthorized(XbSecureToken token, String url);
    boolean isAuthorized(XbSecureToken token, String url, String method);

    HashSet<String> getConfiguredRoles();
    HashSet<String> getConfiguredAssets();
}
