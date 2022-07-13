package com.disney.xband.ac.lib.client.filter;

import com.disney.xband.ac.lib.IXbPrincipal;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.client.IAcManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 7/17/12
 * Time: 3:41 PM
 */
public class XbPrincipal implements IXbPrincipal {
    private XbSecureToken token;
    private IAcManager am;

    public XbPrincipal(XbSecureToken token, IAcManager am) {
        this.token = token;
        this.am = am;
    }

    @Override
    public String getName() {
        return this.token.getLogonName();
    }

    @Override
    public String getDisplayName() {
        return this.token.getDisplayName();
    }

    @Override
    public List<String> getRoles() {
        return this.am.getRoles(this.token);
    }

    @Override
    public List<String> getAssets() {
        return this.am.getAssets(this.token);
    }

    @Override
    public List<String> getDenyAssets() {
        return this.am.getDenyAssets(this.token);
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.am.isUserInRole(this.token, role);
    }

    @Override
    public boolean canAccessAsset(String asset) {
        return this.am.canAccessAsset(this.token, asset);
    }
}
