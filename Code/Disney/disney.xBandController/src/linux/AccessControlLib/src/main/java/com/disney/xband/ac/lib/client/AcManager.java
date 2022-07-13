package com.disney.xband.ac.lib.client;

import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.client.model.AcModel;
import com.disney.xband.ac.lib.client.model.Application;
import com.disney.xband.ac.lib.client.model.Asset;
import com.disney.xband.ac.lib.client.model.UrlPatternType;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/20/12
 * Time: 11:04 AM
 */
public class AcManager implements IAcManager {

    private final static String ALL_ROLES = "_ALL_"; // Special role meaning all authenticated users

    private HashMap<String, List<UrlPatternType>> roleAllowUrlPatternMap;
    private HashMap<String, List<UrlPatternType>> roleDenyUrlPatternMap;
    private HashSet<UrlPatternType> urlPatterns;
    private HashSet<String> roles;
    private HashSet<String> assets;
    private HashSet<UrlPatternType> unProtectedUrlPatterns;
    private HashMap<String, HashSet> roleAssetMap;
    private HashMap<String, HashSet> roleDenyAssetMap;
    private HashSet<UrlPatternType> localAuthUrlPatterns;
    private String appId;

    private final Logger logger;

    public AcManager(InputStream is, String appId) {
        assert((is != null) && !XbUtils.isEmpty(appId));

        this.logger = Logger.getLogger(this.getClass());
        this.appId = appId;

        final AcModel ac;

        try {
            final JAXBContext context = JAXBContext.newInstance(com.disney.xband.ac.lib.client.model.AcModel.class);
            ac = (AcModel) context.createUnmarshaller().unmarshal(is);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        final Iterator<Application> it = ac.getApplication().iterator();
        Application app = null;

        while(it.hasNext()) {
            final Application a = it.next();

            if(appId.equals(a.getAppId())) {
                app = a;
                break;
            }
        }

        if(app == null) {
            throw new RuntimeException("The AC model file does not contain data for application " + appId);
        }

        this.loadRolesMap(app);
    }

    ///////////////////// IAcManager implementation //////////////////////

    @Override
    public HashSet<String> getConfiguredRoles() {
        return this.roles;
    }

    @Override
    public HashSet<String> getConfiguredAssets() {
        return this.assets;
    }

    @Override
    public List<String> getRoles(XbSecureToken token) {
        assert(token != null);

        return token.getRoles();
    }

    @Override
    public boolean isUserInRole(XbSecureToken token, String role) {
        assert(token != null);

        if(XbUtils.isEmpty(role)) {
            return false;
        }

        final List<String> tokenRoles = new ArrayList<String>(token.getRoles());
        tokenRoles.add(AcManager.ALL_ROLES);
        final Iterator<String> roles = tokenRoles.iterator();

        while(roles.hasNext()) {
            if(role.equals(roles.next())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> getAssets(XbSecureToken token) {
        assert(token != null);

        if((token.getFunctionalAbilities() != null) && (token.getFunctionalAbilities().size() != 0)) {
            return token.getFunctionalAbilities();
        }

        final HashSet<String> assets = new HashSet<String>(8);
        final List<String> tokenRoles = new ArrayList<String>(token.getRoles());
        tokenRoles.add(AcManager.ALL_ROLES);
        final Iterator<String> roles = tokenRoles.iterator();

        while(roles.hasNext()) {
            final HashSet<String> a = this.roleAssetMap.get(roles.next());

            if(a != null) {
                final Iterator<String> assetIDs = a.iterator();

                while(assetIDs.hasNext()) {
                    final String assetID = assetIDs.next();
                    assets.add(assetID);
                }
            }
        }

        return new ArrayList<String>(assets);
    }

    @Override
    public List<String> getDenyAssets(XbSecureToken token) {
        assert(token != null);

        final HashSet<String> assets = new HashSet<String>(8);
        final List<String> tokenRoles = new ArrayList<String>(token.getRoles());
        tokenRoles.add(AcManager.ALL_ROLES);
        final Iterator<String> roles = tokenRoles.iterator();

        while(roles.hasNext()) {
            final HashSet<String> a = this.roleDenyAssetMap.get(roles.next());

            if(a != null) {
                final Iterator<String> assetIDs = a.iterator();

                while(assetIDs.hasNext()) {
                    final String assetID = assetIDs.next();
                    assets.add(assetID);
                }
            }
        }

        return new ArrayList<String>(assets);
    }

    @Override
    public boolean canAccessAsset(XbSecureToken token, String assetName) {
        assert(token != null);

        if(XbUtils.isEmpty(assetName)) {
            return false;
        }

        return this.getAssets(token).contains(assetName) ? true :  false;
    }

    @Override
    public boolean isAuthorized(XbSecureToken token, IAuthRequestWrapper request) {
        final String s = request.getQueryString();

        if((s != null) && (s.length() > 0)) {
            return this.isAuthorized(token, request.getRequestURL().toString() + "?" + request.getQueryString(), request.getMethod());
        }
        else {
            return this.isAuthorized(token, request.getRequestURL().toString(), request.getMethod());
        }
    }

    @Override
    public boolean isAuthorized(XbSecureToken token, String url) {
        return this.isAuthorized(token, url, "GET");
    }

    @Override
    public boolean isAuthorized(XbSecureToken token, String url, String method) {
        if(url == null) {
            return false;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Checking if the request is authorized to access: " + url);
        }

        try {
            url = AcManager.normalizeUrl(url);
        }
        catch(Exception e) {
            return false;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Normalized URL: " + url);
        }

        final Iterator<UrlPatternType> dList;
        final Iterator<UrlPatternType> aList;

        if((token.getSecuredEntities() != null) && (token.getSecuredEntities().size() != 0)) {
            if(logger.isTraceEnabled()) {
                logger.trace("Using auth data from token");
            }

            aList = getAuthorizedResourcesFromToken(token);
            dList = (new ArrayList<UrlPatternType>()).iterator();
        }
        else {
            if(logger.isTraceEnabled()) {
                logger.trace("Using local auth data");
            }

            aList = this.getAllowUrlPatterns(token).iterator();
            dList = this.getDenyUrlPatterns(token).iterator();
        }

        try {
            while (dList.hasNext()) {
                final UrlPatternType elem = dList.next();
                final String dUrl = elem.getValue();
                final String aMethod = elem.getMethod().equalsIgnoreCase("*") ? method : elem.getMethod();
                final Pattern p = Pattern.compile(dUrl);
                final Matcher m = p.matcher(url);

                if (m.matches() && aMethod.equalsIgnoreCase(method)) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("The url matches deny pattern: " + dUrl);
                    }

                    return false;
                }
            }

            while (aList.hasNext()) {
                final UrlPatternType elem = aList.next();
                final String aUrl = elem.getValue();
                final String aMethod = elem.getMethod().equalsIgnoreCase("*") ? method : elem.getMethod();
                final Pattern p = Pattern.compile(aUrl);
                final Matcher m = p.matcher(url);

                if (m.matches() && aMethod.equalsIgnoreCase(method)) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("The url matches allow pattern: " + aUrl);
                    }

                    return true;
                }
            }
        } catch (Exception e) {
            //System.out.println(e);
        }

        return false;
    }

    @Override
    public boolean isProtected(String url) {
        return this.isProtected(url, "GET");
    }

    @Override
    public boolean isProtected(String url, String method) {
        if(url == null) {
            return false;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Checking if url " + url + " is protected");
        }

        try {
            url = AcManager.normalizeUrl(url);
        }
        catch(Exception e) {
            return true;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Normalized URL: " + url);
        }

        final Iterator<UrlPatternType> aList = this.unProtectedUrlPatterns.iterator();

        try {
            while (aList.hasNext()) {
                final UrlPatternType elem = aList.next();
                final String aUrl = elem.getValue();
                final String aMethod = elem.getMethod().equalsIgnoreCase("*") ? method : elem.getMethod();
                final Pattern p = Pattern.compile(aUrl);
                final Matcher m = p.matcher(url);

                if (m.matches() && aMethod.equalsIgnoreCase(method)) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("The url matches unprotected url pattern: " + aUrl);
                    }

                    return false;
                }
            }
        } catch (Exception e) {
            // ignore
        }

        return true;
    }

    @Override
    public boolean isProtected(IAuthRequestWrapper request) {
        if(request.getQueryString() != null) {
            return this.isProtected(request.getRequestURL().toString() + "?" + request.getQueryString(), request.getMethod());
        }
        else {
            return this.isProtected(request.getRequestURL().toString(), request.getMethod());
        }
    }

    @Override
    public boolean isLogout(IAuthRequestWrapper request, String logoutUrl) {
        return this.isLogout(request.getRequestURL().toString(), logoutUrl);
    }

    @Override
    public boolean isLogout(String url, String logoutUrl) {
        if(url == null) {
            return false;
        }

        try {
            url = AcManager.normalizeUrl(url);
        }
        catch(Exception e) {
            return false;
        }

        try {
            logoutUrl = AcManager.normalizeLogoutUrl(logoutUrl);
        }
        catch(Exception e) {
            return false;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Normalized URL: " + url);
        }

        if(!url.equals(logoutUrl)) {
            return false;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Url " + url + " is a logout url");
        }

        return true;
    }

    @Override
    public boolean isLocalAuth(String url) {
        return this.isLocalAuth(url, "GET");
    }
    @Override
    public boolean isLocalAuth(String url, String method) {
        if(url == null) {
            return false;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Checking if url " + url + " requires local authentication");
        }

        try {
            url = AcManager.normalizeUrl(url);
        }
        catch(Exception e) {
            return true;
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Normalized URL: " + url);
        }

        final Iterator<UrlPatternType> urls = this.localAuthUrlPatterns.iterator();

        try {
            while (urls.hasNext()) {
                final UrlPatternType elem = urls.next();
                final String aUrl = elem.getValue();
                final String aMethod = elem.getMethod().equalsIgnoreCase("*") ? method : elem.getMethod();
                final Pattern p = Pattern.compile(aUrl);
                final Matcher m = p.matcher(url);

                if (m.matches() && aMethod.equalsIgnoreCase(method)) {
                     if(logger.isTraceEnabled()) {
                        logger.trace("The url " + aUrl + " requires local authentication");
                    }

                    return true;
                }
            }
        } catch (Exception e) {
            // ignore
        }

        return false;
    }

    @Override
    public boolean isLocalAuth(IAuthRequestWrapper request) {
        return this.isLocalAuth(request.getRequestURL().toString() + "?" + request.getQueryString());
    }
    //////////////////////////////////////////////////////////////////////////

    ////////////////////////// Private methods //////////////////////////////
    private Iterator<UrlPatternType> getAuthorizedResourcesFromToken(XbSecureToken token) {
        final String appId1 = this.appId + "/";
        final String appId2 = "/" + this.appId + "/";
        final List<UrlPatternType> aList = new ArrayList<UrlPatternType>(16);
        final Iterator<String> it = token.getSecuredEntities().iterator();

        String resource;

        while(it.hasNext()) {
            resource = it.next();

            if(resource.startsWith(appId1)) {
                resource = resource.substring(appId1.length());
                final UrlPatternType elem = new UrlPatternType();
                elem.setValue(resource);
                aList.add(elem);
            }
            else {
                if(resource.startsWith(appId2)) {
                    resource = resource.substring(appId2.length());
                    final UrlPatternType elem = new UrlPatternType();
                    elem.setValue(resource);
                    aList.add(elem);
                }
            }
        }

        return aList.iterator();
    }

    private List<UrlPatternType> getAllowUrlPatterns(XbSecureToken token) {
        assert(token != null);

        final HashSet<UrlPatternType> res = new HashSet<UrlPatternType>(32);
        final List<String> tokenRoles = new ArrayList<String>(token.getRoles());
        tokenRoles.add(AcManager.ALL_ROLES);
        final Iterator<String> it = tokenRoles.iterator();

        while(it.hasNext()) {
            final String role = it.next();

            if(this.roleAllowUrlPatternMap.containsKey(role)) {
                final Iterator<UrlPatternType> list = this.roleAllowUrlPatternMap.get(role).iterator();

                while(list.hasNext()) {
                    final UrlPatternType url = list.next();
                    res.add(url);
                }
            }
        }

        return new ArrayList<UrlPatternType>(res);
    }

    private List<UrlPatternType> getDenyUrlPatterns(XbSecureToken token) {
        assert(token != null);

        final HashSet<UrlPatternType> res = new HashSet<UrlPatternType>(32);
        final List<String> tokenRoles = new ArrayList<String>(token.getRoles());
        tokenRoles.add(AcManager.ALL_ROLES);
        final Iterator<String> it = tokenRoles.iterator();

        while(it.hasNext()) {
            final String role = it.next();

            if(this.roleDenyUrlPatternMap.containsKey(role)) {
                final Iterator<UrlPatternType> list = this.roleDenyUrlPatternMap.get(role).iterator();

                while(list.hasNext()) {
                    final UrlPatternType url = list.next();
                    res.add(url);
                }
            }
        }

        return new ArrayList<UrlPatternType>(res);
    }

    private void loadRolesMap(Application app) {
        this.roles = new HashSet<String>(8);
        this.urlPatterns = new HashSet<UrlPatternType>(32);
        this.assets = new HashSet<String>(8);
        this.roleAssetMap = new HashMap<String, HashSet>(8);
        this.roleDenyAssetMap = new HashMap<String, HashSet>(8);
        this.roleAllowUrlPatternMap = new HashMap<String, List<UrlPatternType>>(8);
        this.roleDenyUrlPatternMap = new HashMap<String, List<UrlPatternType>>(8);
        this.unProtectedUrlPatterns = new HashSet<UrlPatternType>(4);
        this.localAuthUrlPatterns = new HashSet<UrlPatternType>(32);

        final List<UrlPatternType> unprot = app.getUnprotectedUrlPattern();

        if(unprot != null) {
            final Iterator<UrlPatternType> unprotIt = unprot.iterator();

            while(unprotIt.hasNext()) {
                final UrlPatternType elem = unprotIt.next();
                elem.setValue(AcManager.normalizeUrl(elem.getValue()));
                this.unProtectedUrlPatterns.add(elem);
            }
        }

        final Iterator<Asset> assets = app.getAsset().iterator();

        while(assets.hasNext()) {
            final Asset asset = assets.next();
            assert(!this.assets.contains(asset.getAssetId()));
            this.assets.add(asset.getAssetId());
            final HashSet<String> rolesForAsset = new HashSet<String>(8);

            final Iterator<String> aRoles = asset.getAllowRole().iterator();
            final HashSet<String> aList = new HashSet<String>(8);

            while(aRoles.hasNext()) {
                final String r = aRoles.next();
                aList.add(r);
                this.roles.add(r);
                rolesForAsset.add(r);
            }

            final Iterator<String> dRoles = asset.getDenyRole().iterator();
            final HashSet<String> dList = new HashSet<String>(8);

            while(dRoles.hasNext()) {
                final String r = dRoles.next();
                dList.add(r);
                this.roles.add(r);
                rolesForAsset.remove(r);
            }

            final Iterator<UrlPatternType> urls = asset.getUrlPattern().iterator();
            final HashSet<UrlPatternType> uList = new HashSet<UrlPatternType>(16);

            while(urls.hasNext()) {
                final UrlPatternType elem = urls.next();
                String url = AcManager.normalizeUrl(elem.getValue());

                if("*".equals(url)) {
                    url = ".*";
                }

                elem.setValue(url);
                this.urlPatterns.add(elem);
                uList.add(elem);

                if((asset.isIsLocalAuth() != null) && asset.isIsLocalAuth()) {
                    this.localAuthUrlPatterns.add(elem);
                }
            }

            Iterator<String> roles = aList.iterator();

            while(roles.hasNext()) {
                final String role = roles.next();
                final HashSet<UrlPatternType> uSet;

                if(this.roleAllowUrlPatternMap.get(role) == null) {
                    uSet = new HashSet<UrlPatternType>();
                }
                else {
                    uSet = new HashSet<UrlPatternType>(this.roleAllowUrlPatternMap.get(role));
                }

                uSet.addAll(uList);
                this.roleAllowUrlPatternMap.put(role, new ArrayList<UrlPatternType>(uSet));

                // Fill out roleAssetMap
                final HashSet<String> aSet;

                if(this.roleAssetMap.get(role) == null) {
                    aSet = new HashSet<String>();
                }
                else {
                    aSet = this.roleAssetMap.get(role);
                }

                if(!aSet.contains(asset.getAssetId()) && !dList.contains(role)) {
                    aSet.add(asset.getAssetId());
                    this.roleAssetMap.put(role, aSet);
                }
            }

            roles = dList.iterator();

            while(roles.hasNext()) {
                final String role = roles.next();
                final HashSet<UrlPatternType> uSet;

                if(this.roleDenyUrlPatternMap.get(role) == null) {
                    uSet = new HashSet<UrlPatternType>();
                }
                else {
                    uSet = new HashSet<UrlPatternType>(this.roleDenyUrlPatternMap.get(role));
                }

                uSet.addAll(uList);
                this.roleDenyUrlPatternMap.put(role, new ArrayList<UrlPatternType>(uSet));

                // Fill out roleDenyAssetMap
                final HashSet<String> dSet;

                if(this.roleDenyAssetMap.get(role) == null) {
                    dSet = new HashSet<String>();
                }
                else {
                    dSet = this.roleDenyAssetMap.get(role);
                }

                if(!dSet.contains(asset.getAssetId())) {
                    dSet.add(asset.getAssetId());
                    this.roleDenyAssetMap.put(role, dSet);
                }
            }
        }
    }

    private static String normalizeUrl(String s) {
        assert(s != null);

        s = s.trim();

        if(XbUtils.isEmpty(s)) {
            return s;
        }

        int ind = s.indexOf("://");

        if(ind >= 0) {
            s = s.substring(ind + 3);
            ind = s.indexOf("/");

            if(ind < 0) {
                return "";
            }

            s = s.substring(ind + 1);
            ind = s.indexOf("/");

            if(ind < 0) {
                return "";
            }

            s = s.substring(ind + 1);
        }
        else {
            if(s.startsWith("/")) {
                s = s.substring(1);
            }
        }

        if(s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }

        return s;
    }

    private static String normalizeLogoutUrl(String url) {
        final int ind = url.lastIndexOf("/");

        if(ind >= 0) {
            return url.substring(ind + 1);
        }

        return url;
    }

    /*
    public static void main(String[] args) throws Exception {
        //
        ObjectFactory f = new ObjectFactory();
        AcModel m = f.createAcModel();
        m.setLastUpdated(getXMLGregorianCalendarNow());
        m.setSchemaVersion("1.0.0");
        Application app = f.createApplication();
        app.setAppId("UI");
        List<Application> apps = m.getApplication();
        apps.add(app);
        List<Asset> as = app.getAsset();
        Asset a = f.createAsset();
        a.setAssetId("Asset1");
        as.add(a);
        List<String> d = a.getUrlPattern();
        d.add("attractionview");
        d = a.getAllowRole();
        d.add("admin");
        d = a.getDenyRole();
        d.add("guest");

        try {
            final JAXBContext context = JAXBContext.newInstance(com.disney.xband.ac.lib.client.model.AcModel.class);
            context.createMarshaller().marshal(m, new FileOutputStream("/tmp/ac-model2.xml"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        //

        AcManager ac = new AcManager(new FileInputStream("/tmp/ac-model.xml"), "UI");

        XbSecureToken t = new XbSecureToken();
        List<String> roles = new ArrayList<String>(8);
        roles.add("admin");
        t.setRoles(roles);

        boolean result = ac.isAuthorized(t, "http://docs.oracle.com/javase/14aa");
        System.out.println(result);

        result = ac.isProtected("/login/index.html");
        System.out.println(result);

        result = ac.isLocalAuth("/14a");
        System.out.println(result);
    }
    */
    /*
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<AcModel>
    <SchemaVersion>1.0.0</SchemaVersion>
    <LastUpdated>2012-06-20T14:44:45.533-07:00</LastUpdated>
    <Application>
        <AppId>UI</AppId>
        <UnprotectedUrlPattern>/logon/.*</UnprotectedUrlPattern>
        <Asset>
            <AssetId>Asset1</AssetId>
            <IsLocalAuth>false</IsLocalAuth>
            <UrlPattern>/14a*</UrlPattern>
            <AllowRole>_ALL_</AllowRole>
            <AllowRole>admin</AllowRole>
        </Asset>
    </Application>
</AcModel>
     */
}
