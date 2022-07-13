package com.disney.xband.ac.lib;

import java.security.Principal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 7/17/12
 * Time: 3:40 PM
 */
public interface IXbPrincipal extends Principal {
    String getDisplayName();
    List<String> getRoles();
    List<String> getAssets();
    List<String> getDenyAssets();
    boolean isUserInRole(String role);
    boolean canAccessAsset(String asset);
}
