package com.disney.xband.xbrms.common.caching;

import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.xbrms.common.XbrmsUtils;
import org.apache.log4j.Logger;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 2/4/13
 * Time: 6:51 PM
 */

public class CacheFilter implements Filter {
    private static Logger logger;

    private static final String NAME_PARAM_CF_INCLUDE_URL = "include";
    private static final String NAME_PARAM_CF_EXCLUDE_URL = "exclude";
    private static final String NAME_PROP_CF_EXPIRATION_SECS = "nge.xconnect.cache.expiration.secs";
    private static final long DEFAULT_CACHE_EXPIRATION_SECS = 0;

    private FilterConfig config;
    private long cacheTimeoutMs;
    private HashMap<String, CachedItem> cache;
    private Thread cleaner;
    private boolean run;
    private List<Pattern> excluded;
    private List<Pattern> included;

    private class CachedItem {
        private long cTime;
        private byte[] data;
        private String contentType;
        private String uri;

        public CachedItem(final String uri, final byte[] data, final String contentType) {
            this.cTime = System.currentTimeMillis();
            this.setData(data);
            this.contentType = contentType;
            this.uri = uri;
        }

        public long getTime() {
            return cTime;
        }

        public void setTime(long cTime) {
            this.cTime = cTime;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public boolean isExpired() {
            if((this.cTime + cacheTimeoutMs) < System.currentTimeMillis()) {
                return true;
            }

            return false;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }

    @Override
    public void
    doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
    throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        // Check if the filter is disabled.
        if((this.cacheTimeoutMs <= 0 ) || (this.included.size() == 0)) {
            chain.doFilter(request, response);
            return;
        }

        String resourceId = request.getRequestURI();

        if(resourceId.contains("/invalidatehttpcache")) {
            this.cache.clear();
            response.setStatus(200);
            return;
        }

        // Cache only GET methods
        if(!request.getMethod().equalsIgnoreCase("GET")) {
            chain.doFilter(request, response);
            return;
        }

        if (request.getQueryString() != null) {
           resourceId += '?' + request.getQueryString();
        }

        final int ind = resourceId.indexOf("/", 1);

        if(ind < 0) {
            resourceId = "/";
        }
        else {
            resourceId = resourceId.substring(ind);
        }

        if(isExcluded(resourceId) || !isIncluded(resourceId)) {
            chain.doFilter(request, response);
            return;
        }

        String contentType = request.getHeader("Accept");

        if(XbrmsUtils.isEmpty(contentType)) {
            contentType = request.getContentType();
        }

        resourceId += ("#" + contentType);

        /*
        final HttpSession ses = request.getSession(false);

        if(ses == null) {
            chain.doFilter(request, response);
            return;
        }

        resourceId += ("#" + ses.getId());
        */

        CachedItem ci = this.cache.get(resourceId);

        if((ci == null) || ci.isExpired()) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final CacheResponseWrapper wrappedResponse = new CacheResponseWrapper(request, response, baos);

            chain.doFilter(req, wrappedResponse);
            ci = new CachedItem(resourceId, baos.toByteArray(), wrappedResponse.getContentType());

            this.cache.put(resourceId, ci);
            // logger.debug("Request CT: " + request.getContentType() + "; Response CT: " + wrappedResponse.getContentType());
        }
        else {
            // logger.debug("Cache -> Request CT: " + request.getContentType() + "; Response CT: " + response.getContentType());
        }

        response.setContentType(ci.getContentType());
        final ServletOutputStream sos = res.getOutputStream();

        InputStream fis = null;

        try {
            fis = new ByteArrayInputStream(ci.getData());

            for (int i = fis.read(); i != -1; i = fis.read()) {
                sos.write((byte) i);
            }

            sos.flush();
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // try {Thread.sleep(8000); } catch(Exception ignore) {}
        CacheFilter.logger = Logger.getLogger(CacheFilter.class);
        this.config = filterConfig;
        final HashMap<String, String> props = CacheFilter.readConfig(filterConfig);

        // Expiration timeout
        this.cacheTimeoutMs = 1000 * Long.parseLong(props.get(CacheFilter.NAME_PROP_CF_EXPIRATION_SECS));

        // Include URLs
        this.included = new ArrayList<Pattern>(8);
        String param = props.get(CacheFilter.NAME_PARAM_CF_INCLUDE_URL);

        if(param == null) {
            return;
        }

        String[] items = param.split(";");

        if((items == null) || (items.length == 0)) {
            return;
        }

        for(String item : items) {
            this.included.add(Pattern.compile(item));
        }

        // Exclude URLs
        this.excluded = new ArrayList<Pattern>(8);
        param = props.get(CacheFilter.NAME_PARAM_CF_EXCLUDE_URL);

        if(param != null) {
            items = param.split(";");

            if((items != null) && (items.length != 0)) {
                for(String item : items) {
                    this.excluded.add(Pattern.compile(item));
                }
            }
        }

        this.cache = new HashMap<String, CachedItem>(512);

        this.run = true;
        this.cleaner = new Thread(new Runnable() {
            @Override
            public void run() {
                while(run) {
                    try {
                        if(cache.size() > 0) {
                            for(CachedItem ci : cache.values()) {
                                if(ci.isExpired()) {
                                    cache.remove(ci.getUri());
                                }
                            }
                        }

                        Thread.sleep(30000);
                    }
                    catch(Exception ignore) {
                    }
                }
            }
        });

        this.cleaner.start();
    }

    @Override
    public void destroy() {
        this.run = false;
        this.cleaner.interrupt();
        this.config = null;
    }

    private boolean isIncluded(final String path) {
        return this.isOnList(path, true);
    }

    private boolean isExcluded(final String path) {
        return this.isOnList(path, false);
    }

    private boolean isOnList(final String path, final boolean included) {
        final Iterator<Pattern> list = included ? this.included.iterator() : this.excluded.iterator();

        while (list.hasNext()) {
            final Pattern p = list.next();
            final Matcher m = p.matcher(path);

            if (m.matches()) {
                return true;
            }
        }

        return false;
    }

    private static HashMap<String, String> readConfig(FilterConfig filterConfig) {
        // Try to load properties from web.xml
        InputStream is = null;

        try {
            final HashMap<String, String> props = new HashMap<String, String>(16);
            final Enumeration<String> en = filterConfig.getInitParameterNames();

            while (en.hasMoreElements()) {
                final String pName = en.nextElement();
                props.put(pName, filterConfig.getInitParameter(pName));
            }

            final String appId = filterConfig.getServletContext().getContextPath().substring(1);

            // Now try to load properties from the a configuration file
            CacheFilter.loadProperties(props, appId, logger);

            return props;
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
    }

    private static void loadProperties(HashMap<String, String> props, String appId, Logger logger) {
        final NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
        final String sPropFile = System.getProperty("environment.properties");

        if (sPropFile != null) {
            logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
            decoder.setPropertiesPath(sPropFile);
        }

        final String sJasyptPropFile = System.getProperty("jasypt.properties");
        if (sJasyptPropFile != null) {
            logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
            decoder.setJasyptPropertiesPath(sJasyptPropFile);
        }

        try {
        	decoder.initialize();
            Properties prop = decoder.read();
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                props.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        catch (Exception e) {
            logger.fatal("Failed to read the properties file [" + decoder.getPropertiesPath() + "]: " + e.toString());
            throw new RuntimeException();
        }

        // Populate defaults
        final String propName = CacheFilter.NAME_PROP_CF_EXPIRATION_SECS + "." + appId;
        final String propValue = props.get(propName) != null ? props.get(propName) : props.get(CacheFilter.NAME_PROP_CF_EXPIRATION_SECS);

        if(propValue == null) {
            props.put(CacheFilter.NAME_PROP_CF_EXPIRATION_SECS, CacheFilter.DEFAULT_CACHE_EXPIRATION_SECS + "");
        }
        else {
            props.put(CacheFilter.NAME_PROP_CF_EXPIRATION_SECS, propValue);
        }
    }
}