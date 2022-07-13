package com.disney.xband.xbrc.Controller;

import com.disney.xband.xbrc.Controller.model.ProtectedResource;
import com.disney.xband.xbrc.Controller.model.ProtectedResourceCollection;
import org.apache.log4j.Logger;
import org.simpleframework.http.Request;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/13/13
 * Time: 12:42 PM
 */
public class ResourcesMap {
    private static Logger logger = Logger.getLogger(ResourcesMap.class);
    private Map<String, ResourceEntry> map;
    private List<ResourceEntry> packs;
    private ResourceEntry defaultResource;

    public static class ResourceEntry {
        private String key;
        private String path;
        private String method;
        private boolean isAuthRequired;
        private boolean isSslRequired;
        private boolean isAuditRequired;

        public ResourceEntry(
            final String path,
            final String method,
            final boolean isAuthRequired,
            final boolean isSslRequired,
            final boolean isAuditRequired
        ) {
            this.path = path;
            this.method = method;
            this.isAuthRequired = isAuthRequired;
            this.isSslRequired = isSslRequired;
            this.isAuditRequired = isAuditRequired;

            this.key = ResourceEntry.createKey(path, method);
        }

        public String getKey() {
            return key;
        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }

        public boolean isAuthRequired() {
            return isAuthRequired;
        }

        public boolean isSslRequired() {
            return isSslRequired;
        }

        public boolean isAuditRequired() {
            return isAuditRequired;
        }

        public static String createKey(final String path, final String method) {
            return method + "<->" + path;
        }
    }

    public ResourcesMap() {
        ProtectedResourceCollection rc = new ProtectedResourceCollection();
        InputStream is = null;
        FileInputStream fis = null;

        try {
            final JAXBContext context = JAXBContext.newInstance(ProtectedResourceCollection.class);
            final Unmarshaller m = context.createUnmarshaller();
            fis = new FileInputStream("/etc/nge/config/xbrc-resources.xml");
            rc = (ProtectedResourceCollection) m.unmarshal(fis);
        }
        catch (Exception ignore) {
            try {
                final JAXBContext context = JAXBContext.newInstance(ProtectedResourceCollection.class);
                final Unmarshaller m = context.createUnmarshaller();
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("xbrc-resources.xml");
                rc = (ProtectedResourceCollection) m.unmarshal(is);
            }
            catch (Exception e) {
                logger.error("Failed to read xbrc-resources.xml");
                System.exit(1);
            }
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore2) {
                }
            }

            if(fis != null) {
                try {
                    fis.close();
                }
                catch (Exception ignore2) {
                }
            }
        }

        map = new HashMap<String, ResourceEntry>();
        packs = new ArrayList<ResourceEntry>();

        if(rc.getAccessControlDisabled()) {
            defaultResource = new ResourceEntry("", "", false, false, true);

            return;
        }
        else {
            defaultResource = new ResourceEntry("", "", true, false, true);
        }

        for(ProtectedResource pr : rc.getResource()) {
            if((pr.getPath() == null) || (pr.getPath().length() == 0)) {
                logger.error("Empty resource specification. Skipped.");
            }

            final ResourceEntry r;

            if(pr.getPath().endsWith("/*")) {
                r = new ResourceEntry(pr.getPath().substring(0, pr.getPath().lastIndexOf("/*")), pr.getMethod(), pr.getAuthenticate(), pr.getSsl(), pr.getAudit());
                packs.add(r);
            }
            else {
                r = new ResourceEntry(pr.getPath(), pr.getMethod(), pr.getAuthenticate(), pr.getSsl(), pr.getAudit());
                map.put(r.getKey(), r);
            }
        }
    }

    public ResourceEntry getResourceEntry(Request request) {
        ResourceEntry r = map.get(ResourceEntry.createKey(request.getPath().getPath(), request.getMethod()));

        if((r != null) && (!r.getMethod().equals(request.getMethod()))) {
            r = null;
        }

        if(r == null) {
            for(ResourceEntry p : this.packs) {
                if(request.getPath().getPath().startsWith(p.getPath()) && request.getMethod().equals(p.getMethod())) {
                    r = p;
                    break;
                }
            }
        }

        return r == null ? defaultResource : r;
    }
}
