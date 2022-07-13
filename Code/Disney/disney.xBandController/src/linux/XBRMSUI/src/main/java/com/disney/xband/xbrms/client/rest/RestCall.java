package com.disney.xband.xbrms.client.rest;

import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;
import com.disney.xband.xbrms.client.XbrmsServerChooser;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import javax.xml.ws.http.HTTPException;

import java.net.URI;
import java.util.*;
import com.disney.xband.xbrms.common.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/4/12
 * Time: 11:26 AM
 */
public class RestCall extends BaseCall implements IRestCall {

    private static class ClientsCache<K, V> extends LinkedHashMap<K, V> {
        private static final int MAX_ENTRIES = 1000;

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    }

    public static final int DEFAULT_CONNECTION_TO = 6000;
    private static final int HTTP_OK = 200;
    private URI serverUri;
    private Client connection;
    private ClientsCache<String, Client> cachedClients;

    private ClientConfig config;

    public RestCall() {
        this.config = new DefaultClientConfig();
        this.cachedClients = new ClientsCache<String, Client>();
    }

    public void setServerUri(final String serverUri) {
        this.serverUri = UriBuilder.fromUri(serverUri).build();
        this.connection = this.createRestClient();
        this.connection.setConnectTimeout(DEFAULT_CONNECTION_TO);
        this.connection.setReadTimeout(DEFAULT_CONNECTION_TO);
    }
    
    /////////////////////////////// FacilitiesResource ///////////////////////////////
    public FacilityListDto getFacilities() {
        try {
            return (FacilityListDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }
    
    public FacilityListDto getFacilitiesForSystemHealth() {
        try {
            return (FacilityListDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    public FacilityDto getFacilityById(String id) {
        try {
            return (FacilityDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").path("id").path(id).accept(
                    MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    public FacilityDto getFacilityByAddr(String addr) {
        try {
            return (FacilityDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").path("addr").path(addr).accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    @Override
    public FacilityListDto getFacilitiesByModelType(String type) {
        try {
            return (FacilityListDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").path("type").path(type).accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    @Override
    public FacilityListDto getFacilitiesByModelTypeAndStatus(String type, String statusDesc) {
        try {
            return (FacilityListDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").path("type").path(type).path("status").path(statusDesc).accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    @Override
    public FacilityListDto getFacilitiesByStatus(String statusDesc) {
        try {
            return (FacilityListDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").path("status").path(statusDesc).accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }
    
    @Override
    public FacilityListDto getFacilitiesByReaderStatus(String readerStatusDesc)
    {
    	try
    	{
    		return (FacilityListDto) this.getHttpClient().resource(this.getServerUri()).path("xbrcs").path("readerStatus").path(readerStatusDesc)
    				.accept(MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
    	}
    	catch (UniformInterfaceException e)
    	{
    		RestCall.handleResult(e);
    	}
    	
    	return null;
    }

    ///////////////////////////// HealthItemMetrics /////////////////////////////

    @Override
    public PerfMetricMetadataMapDto getMetricMetaById(String hiId) {
        try {
            return (PerfMetricMetadataMapDto) this.getHttpClient().resource(this.getServerUri()).path(
                    "performance").path("meta").path(hiId).accept(MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    @Override
    public String getMetrics(String hiId, String metaName, String metaVersion, String startDateLong, String endDateLong) {
        try {
            if(XbrmsUtils.isEmpty(metaName)) {
                metaName = "null";
            }

            if(XbrmsUtils.isEmpty(metaVersion)) {
                metaVersion = "null";
            }

            return (String) this.getHttpClient().resource(this.getServerUri()).path(
                    "performance").path("metrics").path(hiId).path(metaName).path(metaVersion).path(startDateLong).path(endDateLong).accept(MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    ///////////////////////////////// HealthItem  ///////////////////////////////

    @Override
    public HealthItemDto getHealthItemByAddr(String ip, String port) {
        try {
            return (HealthItemDto) this.getHttpClient().resource(this.getServerUri()).path("health").path("addr").path(ip).path(port).accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public HealthItemDto getHealthItemById(String id) {
        try {
            return (HealthItemDto) this.getHttpClient().resource(this.getServerUri()).path("health").path("id").path(id).accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int addHealthItem(String addr, String port, String className) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("health").path("add").path(addr).path(port).path(className).accept(
            MediaType.APPLICATION_XML_TYPE).post();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public int deleteHealthItem(String hiId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("health").path("delete").path(hiId).accept(
                MediaType.APPLICATION_XML_TYPE).delete();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public int deactivateHealthItem(String hiId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("health").path("deactivate").path(hiId).accept(
            MediaType.APPLICATION_XML_TYPE).post();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public HealthItemListMapDto getHealthItemsInventory() {
        final HealthItemListDto xbrc = this.getHealthItemsInventoryForType("XbrcDto", this.getHttpClient(), this.getHttpConfig());
        final HealthItemListDto idms = this.getHealthItemsInventoryForType("IdmsDto", this.getHttpClient(), this.getHttpConfig());
        final HealthItemListDto jms = this.getHealthItemsInventoryForType("JmsListenerDto", this.getHttpClient(), this.getHttpConfig());

        final Map<String, HealthItemListDto> res = new HashMap<String, HealthItemListDto>();
        final HealthItemListMapDto ret = new HealthItemListMapDto();
        HealthItemListDto dto;

        if(xbrc.getHealthItem().size() > 0) {
            dto = new HealthItemListDto();
            dto.setHealthItem(xbrc.getHealthItem());
            res.put(XbrcDto.class.getName(), dto);
        }

        if(idms.getHealthItem().size() > 0) {
            dto = new HealthItemListDto();
            dto.setHealthItem(idms.getHealthItem());
            res.put(IdmsDto.class.getName(), dto);
        }

        if(jms.getHealthItem().size() > 0) {
            dto = new HealthItemListDto();
            dto.setHealthItem(jms.getHealthItem());
            res.put(JmsListenerDto.class.getName(), dto);
        }

        ret.setMap(res);

        return ret;
    }

    @Override
    public HealthItemListDto getHealthItemsInventoryForType(String className) {
        return this.getHealthItemsInventoryForType(className, this.getHttpClient(), this.getHttpConfig());
    }

    private HealthItemListDto getHealthItemsInventoryForType(String className, Client client, ClientConfig cc) {
        final HealthItemListDto ret = new HealthItemListDto();
        final List<HealthItemDto> retList = new ArrayList<HealthItemDto>();
        ret.setHealthItem(retList);

        try {
            if ("XbrcDto".equals(className)) {
                final XbrcListDto list = (XbrcListDto) client.resource(this.getServerUri()).path("health").path("type").path("xbrc").accept(
                        MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();

                if ((list != null) && (list.getHealthItem() != null)) {
                    for (XbrcDto item : list.getHealthItem()) {
                        retList.add(item);
                    }
                }
            }
            else {
                if ("IdmsDto".equals(className)) {
                    final IdmsListDto list = (IdmsListDto) client.resource(this.getServerUri()).path("health").path("type").path("idms").accept(
                            MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();

                    if ((list != null) && (list.getHealthItem() != null)) {
                        for (IdmsDto item : list.getHealthItem()) {
                            retList.add(item);
                        }
                    }
                }
                else {
                    if ("JmsListenerDto".equals(className)) {
                        final JmsListenerListDto list = (JmsListenerListDto) client.resource(this.getServerUri()).path("health").path("type").path("jmslistener").accept(
                                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();

                        if ((list != null) && (list.getHealthItem() != null)) {
                            for (JmsListenerDto item : list.getHealthItem()) {
                                retList.add(item);
                            }
                        }
                    }
                    else {
                        throw new RuntimeException("Incorrect health item type.");
                    }
                }
            }
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return ret;
    }

    @Override
    public int refreshHealthItemsStatus() {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("health").path("refresh").accept(
            MediaType.APPLICATION_XML_TYPE).get(String.class);
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    /////////////////////////////// ReaderLocation //////////////////////////////

    @Override
    public ReaderInfoListDto getReadersForLocation(String xbrcId, String locationId) {
        try {
            return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("xbrc").path(xbrcId).path("location").path(locationId).accept(
                    MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public ReaderLocationInfo getLocationInfo(String xbrcId) {
        try {
            return (ReaderLocationInfo) this.getHttpClient().resource(this.getServerUri()).path("locations").path("xbrc").path(xbrcId).accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public LocationInfoListDto getLocations() {
        try {
            return (LocationInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("locations").accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public ReaderInfoListDto getUnassignedReaders() {
        try {
            return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("unassigned").accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public ReaderInfoListDto getUnassignedReadersByReaderStatus(String statusDesc) {   // Possible values: red, green, yellow, notred, notgreen
        try {
            return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("unassigned").path("status").path(statusDesc).accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public ReaderInfoListDto getUnlinkedReadersByReaderStatus(String xbrcId, String statusDesc) {   // Possible values: red, green, yellow, notred, notgreen
        try {
            if(XbrmsUtils.isEmpty(statusDesc)) {
                return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("xbrc").path(xbrcId).path("unlinked").accept(
                    MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
            }

            return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("xbrc").path(xbrcId).path("unlinked").path("status").path(statusDesc).accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }
    
    @Override
    public ReaderInfoListDto getUnlinkedReaders(String xbrcId) {   // Possible values: red, green, yellow, notred, notgreen
        try {
            return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("xbrc").path(xbrcId).path("unlinked").accept(
                    MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int refreshReadersHealthStatus() {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("readers").path("refresh").accept(
            MediaType.APPLICATION_XML_TYPE).get(String.class);
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }
    
    @Override
    public ReaderInfoListDto getLinkedReadersByReaderStatus(String xbrcId, String statusDesc) {   // Possible values: red, green, yellow, notred, notgreen
        try {
            if(XbrmsUtils.isEmpty(statusDesc)) {
                return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("xbrc").path(xbrcId).path("linked").accept(
                    MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
            }

            return (ReaderInfoListDto) this.getHttpClient().resource(this.getServerUri()).path("readers").path("xbrc").path(xbrcId).path("linked").path("status").path(statusDesc).accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }
    
    //@Path("/delete/unlinked/mac/{mac}")
    @Override
    public int deleteUnassignedReaderFromCache(String mac)
    {
    	try
    	{
    		this.getHttpClient().resource(this.getServerUri()).path("readers").path("unassigned").path("mac").path(mac)
    				.accept(MediaType.APPLICATION_XML_TYPE).delete();
    	}
    	catch (UniformInterfaceException e)
    	{
    		RestCall.handleResult(e);
    	}
    	
    	return RestCall.HTTP_OK;
    }

    ////////////////////////////// ReaderOperations /////////////////////////////
    @Override
    public ReaderInfo getUnassignedReaderByMac(String mac) {
        try {
            return (ReaderInfo) this.getHttpClient().resource(this.getServerUri()).path("reader").path("info").path("mac").path(mac).accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }
    
    @Override
    public ReaderInfo getReaderByMac(String xbrcId, String readerMac) {
        try {
            return (ReaderInfo) this.getHttpClient().resource(this.getServerUri()).path("reader").path("info").path(xbrcId).path(readerMac).accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int assignReader(String xbrcAddress, String readerMac) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("reader").path("assign").path(xbrcAddress).path(readerMac).put();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }

        catch(Exception ignore) {
        }        return RestCall.HTTP_OK;
    }

    @Override
    public int identifyReaderComp(String xbrcId, String readerMac) {
        try {
        	WebResource resource = this.getHttpClient().resource(this.getServerUri());
        	resource.path("reader").path("identify").path(xbrcId).path(readerMac).put();
        	
        	return resource.head().getClientResponseStatus().getStatusCode();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e);
        }
    }

    @Override
    public int restartReaderComp(String xbrcId, String readerId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("reader").path("restart").path(xbrcId).path(readerId).put();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public int rebootReaderComp(String xbrcId, String readerId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("reader").path("reboot").path(xbrcId).path(readerId).put();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }

    /////////////////////////////////// Status //////////////////////////////////

    @Override
    public XbrmsStatusDto getStatus() {
        try {
            return (XbrmsStatusDto) this.getHttpClient().resource(this.getServerUri()).path("status").path("info").accept(
	        	MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
             RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public ProblemsReportDto getRecentXbrmsProblems() {
        try {
            return (ProblemsReportDto) this.getHttpClient().resource(this.getServerUri()).path("status").path("problems").accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int clearXbrmsProblemsList() {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("status").path("problems").path("clear").accept(
            MediaType.APPLICATION_XML_TYPE).put();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    ///////////////////////////////// Xbrc ////////////////////////////////

    // xBRC configuration
    @Override
    public XbrcConfiguration getXbrcStoredConfig(String configId) {
        try {
            return (XbrcConfiguration) this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("stored").path(configId).accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public XbrcConfiguration getXbrcConfig(String xbrcId, String name) {
        try {
            return (XbrcConfiguration) this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("fetch").path("id").path(xbrcId).path(name).accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public XbrcConfigurationListDto getStoredXbrcConfigs() {
        try {
            return (XbrcConfigurationListDto) this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("configs").accept(
                MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public XbrcConfiguration parseXbrcConfigXml(String xml) {
        try {
            return (XbrcConfiguration) this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("parse").accept(
                MediaType.APPLICATION_XML_TYPE).put(DtoWrapper.class, xml).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int addXbrcConfig(String xbrcId, XbrcConfiguration conf) {
        try {
            if(xbrcId == null) {
                xbrcId = "-1";
            }

            this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("add").path(xbrcId).accept(
            MediaType.APPLICATION_XML_TYPE).post(conf);
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public int deleteXbrcConfig(String configId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("delete").path(configId).accept(
                MediaType.APPLICATION_XML_TYPE).delete();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public int updateXbrcConfig(XbrcConfig conf, String xbrcId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("update").path(xbrcId).accept(
            MediaType.APPLICATION_XML_TYPE).post(conf);
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public int replaceReader(String xbrcId, ReaderInfo oldReader, ReaderInfo newReader) {
    	try {
        	WebResource resource = this.getHttpClient().resource(this.getServerUri());
        	resource.path("readers").path("replace")
            	.path("xbrc").path("id").path(xbrcId)
            	.path("reader").path("mac").path(oldReader.getMacAddress()).path("name").path(oldReader.getName())
            	.put(XmlUtil.convertToXml(newReader, ReaderInfo.class));
            
            try {
                resource.path("invalidatehttpcache").put();
            }
            catch(Exception ignore) {}
            
            return resource.head().getClientResponseStatus().getStatusCode();
        }
        catch (JAXBException e) {
           	throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deployXbrcConfig(String configId, String xbrcId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("deploy").path(configId).path(xbrcId).put();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }

    // Reader Power Management
    
    @Override
    public int overrideSchedule(String xbrcId, String hours) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("schedule").path("override").path(xbrcId).path(hours).put();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }
    
    // Properties

    @Override
    public XbrcConfigListMapDto getXbrcProperties(XbrcIdListDto xbrcsList) {
        try {
            return (XbrcConfigListMapDto) this.getHttpClient().resource(this.getServerUri()).path("xbrc").path("config").path("props").accept(
                MediaType.APPLICATION_XML_TYPE).put(DtoWrapper.class, xbrcsList).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    //////////////////////////////// XbrmsConfig ////////////////////////////////

    @Override
    public XbrmsConfigDto getXbrmsConfig() {
        try {
            return (XbrmsConfigDto) this.getHttpClient().resource(this.getServerUri()).path("config").path("xbrms").accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int setXbrmsConfig(XbrmsConfigDto config) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("config").path("xbrms").put(config);
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public EnvPropertiesMapDto getServerProps() {
        try {
            return (EnvPropertiesMapDto) this.getHttpClient().resource(this.getServerUri()).path("config").path("properties").accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e);
        }

        return null;
    }

    ////////////////////////////////// Audit //////////////////////////////////

    @Override
    public int pushAuditEvents(AuditEventList events) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("audit").path("config").put(events);
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public AuditEventList pullAuditEvents(long afterEventId) {
        try {
            return (AuditEventList)
                this.getHttpClient().resource(this.getServerUri()).path("audit").path("pull").path("" + afterEventId).accept(
                        MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int deleteAuditEvents(long upToEventId) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("audit").path("delete").path(upToEventId + "").accept(
                MediaType.APPLICATION_XML_TYPE).delete();
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        try {
            this.getHttpClient().resource(this.getServerUri()).path("invalidatehttpcache").put();
        }
        catch(Exception ignore) {
        }

        return RestCall.HTTP_OK;
    }

    @Override
    public AuditConfig getAuditConfig() {
        try {
            return (AuditConfig)
                this.getHttpClient().resource(this.getServerUri()).path("audit").path("config").accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public AuditConfig getSubordinateAuditConfigs() {
        try {
            return (AuditConfig)
                this.getHttpClient().resource(this.getServerUri()).path("audit").path("configs").accept(
				MediaType.APPLICATION_XML_TYPE).get(DtoWrapper.class).getContent();
        }
        catch (UniformInterfaceException e) {
            RestCall.handleResult(e.getResponse());
        }

        return null;
    }

    @Override
    public int setAuditConfig(AuditConfig config) {
        try {
            this.getHttpClient().resource(this.getServerUri()).path("audit").path("config").put(config);
        }
        catch (UniformInterfaceException e) {
            return RestCall.handleResult(e.getResponse());
        }

        return RestCall.HTTP_OK;
    }
    
    //////////////////////////////Scheduler ///////////////////////////////////
    
    @Override
    public SchedulerResponse onSchedulerMessage(SchedulerRequest schedulerRequest)
    {
    	SchedulerResponse response = null;
    	try {
          	response = (SchedulerResponse)
        		  this.getHttpClient().resource(this.getServerUri()).path("scheduler").path("message")
        		  	.accept(MediaType.APPLICATION_XML_TYPE).put(DtoWrapper.class, schedulerRequest).getContent();
           
           return response;
        }
        catch (UniformInterfaceException e) 
        {
        	if(e.getResponse().getStatus() >= 300) {
                throw new RuntimeException(e.getResponse().getEntity(String.class));
            }
        	
        	return response;
        }
    }

    ////////////////////////////// Private methods /////////////////////////////////

    private Client getHttpClient() {
        /*
        if(this.getConnection() != null) {
            return this.getConnection();
        }
        */

        String sesId = null;
        Client client = null;

        try {
            sesId = ServletActionContext.getRequest().getSession(false).getId();
            client = this.cachedClients.get(sesId);
        }
        catch(Exception e) {
        }

        if(client == null) {
            client = this.createRestClient();

            synchronized (this.cachedClients) {
                if(sesId != null) {
                    this.cachedClients.put(sesId, client);
                }
            }
        }

        return client;
    }

    private Client createRestClient() {
        final Client client = Client.create(this.config);

        client.addFilter(new ClientFilter() {
            @Override
            public ClientResponse handle(ClientRequest clientRequest) throws ClientHandlerException {
                clientRequest.getHeaders().putSingle("Authorization", XbConnection.getAuthorizationString());
                return this.getNext().handle(clientRequest);
            }
        });

        return client;
    }

    private ClientConfig getHttpConfig() {
        return this.config;
    }

    private static int handleResult(ClientResponse response) {
        final int status = response.getStatus();

        if(status >= 300) {
            throw new RuntimeException(response.getEntity(String.class));
        }

        return status;
    }

    private static int handleResult(UniformInterfaceException originalException)
    {
    	final ClientResponse response = originalException.getResponse();
    	final int status = response.getStatus();
    	
    	if (status >= 300)
    	{
    		HTTPException exc = new HTTPException(status);
    		exc.initCause(originalException);
    		exc.setStackTrace(exc.getStackTrace());
    		throw exc;
    	}
    	
    	return status;
    }
    
    private URI getServerUri() {
        if(this.serverUri != null) {
            return this.serverUri;
        }
        else {
            return XbrmsServerChooser.getInstance().getServerUri(ServletActionContext.getRequest());
        }
    }

    public Client getConnection() {
        return connection;
    }
}
