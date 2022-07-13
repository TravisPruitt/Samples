package com.disney.xband.xbrms.client;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import java.net.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/29/13
 * Time: 6:21 PM
 */
public class XbrmsServerChooser {
    private static Logger logger = Logger.getLogger(com.disney.xband.xbrms.common.ConfigProperties.class);
    
    private static class SingletonHolder {
        public static final XbrmsServerChooser instance = new XbrmsServerChooser();
    }

    public static XbrmsServerChooser getInstance() {
        return SingletonHolder.instance;
    }

    private XbrmsServerChooser() {
    }

    public boolean isMutiParkConfig() {
        return (
            (this.getConfiguredServers() != null) &&
            (this.getConfiguredServers().size() > 1)) &&
            !XbrmsUtils.isEmpty(this.getUiHost()
        );
    }

    public String getUiHost() {
        return  ConfigProperties.getInstance().getUiHost();
    }

    public Map<String, XbrmsDto> getConfiguredServers() {
        return ConfigProperties.getInstance().getXbrmsServers();
    }

    public String getUiRrl() {
        if (!XbrmsUtils.isEmpty(this.getUiHost())) {
            try {
                final URIBuilder uri = new URIBuilder(ServletActionContext.getRequest().getRequestURL().toString());
                final URIBuilder uri2 = new URIBuilder();
                uri2.setScheme(uri.getScheme());
                uri2.setHost(this.getUiHost());
                uri2.setPort(uri.getPort());
                uri2.setPath("/" + PkConstants.XBRMS_UI_APP_NAME);

                return uri2.build().toString();
            }
            catch (Exception e) {
            }
        }

        return null;
    }

    public Map<String, XbrmsDto> getServers() {
        if((this.getConfiguredServers() == null) || (this.getConfiguredServers().size() == 0)) {
            throw new XbrmsServerNotSetException();
        }

        return this.getConfiguredServers();
    }

    public void clearServerSelection() {
        ServletActionContext.getRequest().getSession().removeAttribute(PkConstants.PROP_NAME_XBRMS_UI_HOST_ALIAS);
        ServletActionContext.getRequest().getSession().removeAttribute(PkConstants.PROP_NAME_XBRMS_SERVER_URI);
    }

    public Map<String, String> getTargetLinks(final HttpServletRequest req) {
        final Map<String, String> res = new LinkedHashMap<String, String>();
        Map<String, String> tmp = this.getTargetLinks(true, req);

        if(tmp.size() > 0) {
            res.putAll(tmp);
        }

        tmp = this.getTargetLinks(false, req);

        if(tmp.size() > 0) {
            res.putAll(XbrmsUtils.sortByValue(tmp));
        }

        return res;
    }
    
    public XbrmsDto getActiveXbrms(HttpServletRequest req) {
        final Map<String, XbrmsDto> servers = XbrmsServerChooser.getInstance().getServers();
        final String uiHost = XbrmsServerChooser.getInstance().getServerUri(req).toString();
        final XbrmsDto server = servers.get(uiHost);

        return server;
    }

    public boolean isGlobalXbrms(HttpServletRequest req) {
        return this.getActiveXbrms(req).isGlobal();
    }
    
    public String getGlobalServerLink(HttpServletRequest req) {
        try {
            return this.getTargetLinks(req).keySet().iterator().next();
        }
        catch(Throwable ignore) {}
     
        return null;
    }

    private Map<String, String> getTargetLinks(boolean getGlobal, final HttpServletRequest req) {
        final Map<String, String> res = new HashMap<String, String>();
        final Map<String, XbrmsDto> map = new TreeMap<String, XbrmsDto>(this.getServers());

        final Iterator<String> it = map.keySet().iterator();

        try {
            while(it.hasNext()) {
                final String server = it.next();
                final XbrmsDto dto = map.get(server);

                if(getGlobal) {
                    if(dto.isGlobal()) {
                        if(res.size() > 0) {
                            break;
                        }
                    }
                    else {
                        continue;
                    }
                }
                else {
                    if(dto.isGlobal()) {
                        continue;
                    }
                }

                final URIBuilder uri = new URIBuilder(dto.getUrl());

                if(!XbrmsUtils.isEmpty(dto.getFqdnHostAlias())) {
                    uri.setHost(dto.getFqdnHostAlias());
                }

                uri.setPort(req.getServerPort()) ;

                uri.setPath("/" + PkConstants.XBRMS_UI_APP_NAME + "/");
                res.put(uri.build().toString(), map.get(server).getDesc());
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to construct the list of xBRMS server URLs");
        }

        return res;
    }

    public URI getServerUri(final HttpServletRequest req) {
        final Map svrs = this.getServers();

        if(svrs.size() == 1) {
             final String srv = XbrmsUtils.normalizeXbrmsServiceUrl((String) svrs.keySet().toArray()[0]);

            return UriBuilder.fromUri(srv).build();
        }

        if(req.getServerName().equals((String) req.getSession().getAttribute(PkConstants.PROP_NAME_XBRMS_UI_HOST_ALIAS))) {
            return (URI) req.getSession().getAttribute(PkConstants.PROP_NAME_XBRMS_SERVER_URI);
        }

        String server = null;
        final XbrmsDto srv = XbrmsServerChooser.getServerByUiAlias(this.getConfiguredServers(), req.getServerName());

        if (srv != null) {
            server = srv.getUrl();
        }
        else {
            throw new XbrmsServerNotSetException();
        }

        try {
            final String target = XbrmsUtils.normalizeXbrmsServiceUrl(server);
            final URI uri = UriBuilder.fromUri(target).build();

            // XbrmsUtils.getRestCaller().resetHttpClient();
            if(this.isServerAlive(uri)) {
               req.getSession().setAttribute(PkConstants.PROP_NAME_XBRMS_UI_HOST_ALIAS, req.getServerName());
               req.getSession().setAttribute(PkConstants.PROP_NAME_XBRMS_SERVER_URI, uri);
            }

            return uri;
        }
        catch (Exception e) {
            final String err = "Invalid xBRMS URL: " + server;
            logger.error(err);
            throw new RuntimeException("Invalid xBRMS URL.");
        }
    }

    private static XbrmsDto getServerByUiAlias(final Map<String, XbrmsDto> servers, String uiAlias) {
        if((servers != null) && (uiAlias != null)) {
            uiAlias = XbrmsUtils.getHostnamePart(uiAlias);

            for(String key : servers.keySet()) {
                final XbrmsDto entry = servers.get(key);
                String hostAlias = entry.getHostAlias();

                if(uiAlias.equals(hostAlias)) {
                    return entry;
                }
            }
        }

        return null;
    }

    private boolean isServerAlive(URI uri) {
        try {
            final URL url = new URL(URLDecoder.decode(uri.toURL().toString(), "UTF-8") + "/status");
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.connect();
            connection.getInputStream();

            return true;
        }
        catch (Exception ignore) {
            return false;
        }
    }
}
