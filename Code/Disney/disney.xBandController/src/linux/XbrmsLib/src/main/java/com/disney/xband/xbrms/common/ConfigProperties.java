package com.disney.xband.xbrms.common;

import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import com.disney.xband.xbrms.common.model.XbrmsListDto;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import java.io.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/27/12
 * Time: 3:53 PM
 */
public abstract class ConfigProperties extends Properties {
    private Map<String, XbrmsDto> servers;
    private XbrmsDto globalServer;
    private String uiHost;
    private int maxId;
    private boolean changed;

    private static ConfigProperties instance;
    protected static Logger logger = Logger.getLogger(com.disney.xband.xbrms.common.ConfigProperties.class);

    public abstract void readConfigurationOptionsFromEnvironmentProperties() throws IOException;
    public abstract void readConfigurationOptionsFromDatabase() throws ClassNotFoundException, SQLException, IllegalAccessException, IllegalArgumentException;

    public static ConfigProperties getInstance() {
        return ConfigProperties.instance;
    }

    public static synchronized void init(final boolean isServer) throws Exception {
        //try {Thread.sleep(8000);} catch (Exception e) {}
        if (ConfigProperties.instance == null) {
            final Class c = isServer ?
                    Class.forName("com.disney.xband.xbrms.server.ServerConfigProperties") :
                    Class.forName("com.disney.xband.xbrms.client.ClientConfigProperties");

            final Method m = c.getDeclaredMethod("getInstance", null);
            ConfigProperties.instance = (ConfigProperties) m.invoke(null, null);
        }
    }

    public void initXbrmsServers() {
        if(XbrmsUtils.isEmpty(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE)) {
            this.servers = this.initXbrmsServers(this);
        }
        else {
            this.readSettings();
        }

        this.globalServer = ConfigProperties.getGlobalXbrmsServer(this.servers);
        this.changed = false;
    }

    public Map<String, XbrmsDto> getXbrmsServers() {
        return XbrmsUtils.sortByValue(this.servers);
    }

    public void setXbrmsServers(Map<String, XbrmsDto> servers) {
        this.servers = servers;
    }

    public XbrmsDto getGlobalXbrmsServer() {
        return this.globalServer;
    }

    public String getUiHost() {
        return uiHost;
    }

    public void setUiHost(String uiHost) {
        this.uiHost = uiHost;
    }

    private static XbrmsDto getGlobalXbrmsServer(final Map<String, XbrmsDto> servers) {
        if(servers != null) {
            for(String key : servers.keySet()) {
                final XbrmsDto val = servers.get(key);

                if(val.isGlobal()) {
                    return val;
                }
            }
        }

        return null;
    }

    public synchronized void addXbrmsServer(
            final String urlParam,
            String hostAliasParam,
            final String descParam,
            final String logoParam,
            final boolean isGlobal
    ) {
        final XbrmsDto dto = createXbrmsServer(urlParam, hostAliasParam, descParam, null, isGlobal, ++this.maxId);

        if(this.servers != null) {
            boolean globalExists = false;

            for(XbrmsDto server : this.servers.values()) {
                if(server.getUrl().equalsIgnoreCase(dto.getUrl())) {
                    throw new RuntimeException("Park server URL must be unique");
                }

                if(server.getHostAlias().equalsIgnoreCase(dto.getHostAlias())) {
                    throw new RuntimeException("UI host alias must be unique");
                }

                if(server.getDesc().equalsIgnoreCase(dto.getDesc())) {
                    throw new RuntimeException("Park description must be unique");
                }

                if(server.isGlobal()) {
                    globalExists = true;
                }
            }

            if(globalExists && dto.isGlobal()) {
                throw new RuntimeException("There can be only one global xBRMS server");
            }
        }

        this.servers.put(dto.getUrl(), dto);
        this.globalServer = ConfigProperties.getGlobalXbrmsServer(this.servers);
        this.changed = true;
    }

    public synchronized void removeXbrmsServer(final String urlParam) {
        if(XbrmsUtils.isEmpty(urlParam)) {
            logger.error("xBRMS server URL parameter is empty");
            return;
        }

        final String normUrl = XbrmsUtils.normalizeXbrmsServiceUrl(urlParam);

        if(this.servers.get(normUrl) != null) {
            if(this.servers.remove(normUrl) != null) {
                this.changed = true;
            }
        }
        else {
            if(this.servers.get(urlParam) != null) {
                if(this.servers.remove(urlParam) != null) {
                    this.changed = true;
                }
            }
        }

        this.globalServer = ConfigProperties.getGlobalXbrmsServer(this.servers);
    }

    public void removeXbrmsServer(final int id) {
        String toRemove = null;

        for(String url : this.servers.keySet()) {
            if(this.servers.get(url).getId() == id) {
                toRemove = url;
                break;
            }
        }

        if(toRemove == null) {
            logger.error("xBRMS server with id=" + id + " not found.");
            return;
        }

        this.servers.remove(toRemove);
        this.changed = true;
    }

    private Map<String, XbrmsDto> initXbrmsServers(final ConfigProperties props) {
        // try { Thread.sleep(10000); } catch(Exception e) {};
        Map<String, XbrmsDto> servers = null;

        try {
            servers = this.doInitXbrmsServers(props);
        }
        catch (Exception e) {
            logger.fatal(e.getMessage() + " Exiting!!!");
            System.exit(-1);
        }

        this.uiHost = (String) props.get(PkConstants.PROP_NAME_XBRMS_UI_HOST);

        if((this.uiHost != null) && this.uiHost.toLowerCase().startsWith("http")) {
            try {
                final URIBuilder tryUri = new URIBuilder(this.uiHost);
                this.uiHost = tryUri.getHost();
            }
            catch (Exception e) {
                logger.fatal("Invalid value of " + PkConstants.PROP_NAME_XBRMS_UI_HOST + " property. Exiting!!!");
                System.exit(-1);
            }
        }

        return servers;
    }

    private Map<String, XbrmsDto> doInitXbrmsServers(final Properties props) {
        final Map<String, XbrmsDto> servers = new ConcurrentHashMap<String, XbrmsDto>();

        for (int i = -1, j = 0; i < PkConstants.XBRMS_SERVERS_MAX; ++i) {
            final String suffix = (i < 0) ? "" : ("." + i);

            if(props.get(PkConstants.PROP_NAME_XBRMS_SERVER_URL + suffix) == null) {
                continue;
            }

            final XbrmsDto dto = this.createXbrmsServer(
                (String) props.get(PkConstants.PROP_NAME_XBRMS_SERVER_URL + suffix),
                (String) props.get(PkConstants.PROP_NAME_XBRMS_UI_HOST_ALIAS + suffix),
                (String) props.get(PkConstants.PROP_NAME_XBRMS_SERVER_DESC + suffix),
                (String) props.get(PkConstants.PROP_NAME_XBRMS_SERVER_LOGO + suffix),
                i == -1,
                j++);

            servers.put(dto.getUrl(), dto);
        }

        return servers;
    }

    public XbrmsDto createXbrmsServer(
            final String urlParam,
            String hostAliasParam,
            final String descParam,
            final String logoParam,
            final boolean isGlobal,
            final int id
    ) {
        if (XbrmsUtils.isEmpty(urlParam)) {
            throw new RuntimeException("Invalid server URL specified");
        }

        if (XbrmsUtils.isEmpty(hostAliasParam)) {
            throw new RuntimeException("Invalid UI host alias specified");
        }

        if (XbrmsUtils.isEmpty(descParam)) {
            throw new RuntimeException("Invalid server description specified");
        }

        final String normUrl = XbrmsUtils.normalizeXbrmsServiceUrl(urlParam);
        final URIBuilder uri;
        final String addr;

        try {
            uri = new URIBuilder(normUrl);
            addr = uri.getHost();
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid xBRMS server URL: " + urlParam);
        }

        if(hostAliasParam != null) {
            try {
                uri.setHost(hostAliasParam).build().toString();
            }
            catch (Exception e) {
                throw new RuntimeException("Invalid value of UI host alias: " + hostAliasParam);
            }
        }

        final String fqdnHostAlias = hostAliasParam;

        if(hostAliasParam == null) {
            throw new RuntimeException("UI host alias is not set.");
        }
        else {
            try {
                final URIBuilder tryUri = new URIBuilder("http://" + XbrmsUtils.getHostnamePart(hostAliasParam));
                hostAliasParam = tryUri.getHost();
            }
            catch (Exception e) {
                throw new RuntimeException("Invalid value of UI host alias: " + hostAliasParam);
            }
        }

        final XbrmsDto dto = new XbrmsDto();

        dto.setUrl(normUrl);
        dto.setDesc(descParam);
        dto.setLogo(logoParam);
        dto.setGlobal(isGlobal);
        dto.setAddr(addr);
        dto.setId(id);
        dto.setHostAlias(hostAliasParam);
        dto.setFqdnHostAlias(fqdnHostAlias);

        return dto;
    }

    public synchronized void readSettings() {
        if(this.getClass().getName().indexOf("ClientConfigProperties") < 0) {
            return;
        }

        InputStream is = null;
        XbrmsListDto xbrmss = null;
        final Map<String, XbrmsDto> servers = new ConcurrentHashMap<String, XbrmsDto>();

        try {
            try {
                is = new FileInputStream(InputValidator.validateFilePath((String) this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE)));
            }
            catch (Exception e) {
                this.logger.info("Failed to read file [" + this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE) + "] ");
            }

            try {
                final JAXBContext context = JAXBContext.newInstance(XbrmsListDto.class);
                xbrmss = (XbrmsListDto) context.createUnmarshaller().unmarshal(is);
            }
            catch (Exception e) {
                this.logger.info("Failed to parse file [" + this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE) + "] ");
            }
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

        if((xbrmss != null) && (xbrmss.getXbrmsItem() != null) && (xbrmss.getXbrmsItem().size() != 0)) {
            for(XbrmsDto park : xbrmss.getXbrmsItem()) {
                servers.put(park.getUrl(), park);
                maxId = park.getId() > maxId ? park.getId() : maxId;
            }
        }

        if(xbrmss != null) {
            this.uiHost = xbrmss.getUiServer();
        }

        this.servers = servers;
    }

    public synchronized void writeSettings() {
        OutputStream os = null;
        final XbrmsListDto xbrmss;

        try {
            if(XbrmsUtils.isEmpty(this.getUiHost())) {
                throw new RuntimeException("UI host name property cannot be empty");
            }

            try {
                os = new FileOutputStream(InputValidator.validateFilePath((String) this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE)));
            }
            catch (Exception e) {
                this.logger.error("Failed to open file for writing [" + this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE) + "]: " + e.getMessage());
                throw new RuntimeException(e);
            }

            try {
                final JAXBContext context = JAXBContext.newInstance(XbrmsListDto.class);
                final XbrmsListDto list = new XbrmsListDto();
                list.setXbrmsItem(this.servers == null ? new ArrayList<XbrmsDto>() : new ArrayList<XbrmsDto>(this.servers.values()));
                list.setUiServer(this.getUiHost());
                context.createMarshaller().marshal(list, os);
                this.changed = false;
            }
            catch (Exception e) {
                this.logger.error("Failed to write file [" + (String) this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE) + "]:  " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }

    public boolean isChanged() {
        return changed;
    }
}
