package com.disney.xband.xbrms.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.IAuditEventsProvider;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;
import com.disney.xband.common.scheduler.ui.SchedulerServer;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.db.XbrcConfigurationService;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XbrcModel;
import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.EnvPropertiesMapDto;
import com.disney.xband.xbrms.common.model.FacilityDto;
import com.disney.xband.xbrms.common.model.FacilityListDto;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.HealthItemListDto;
import com.disney.xband.xbrms.common.model.HealthItemListMapDto;
import com.disney.xband.xbrms.common.model.HealthItemType;
import com.disney.xband.xbrms.common.model.HealthStatusDto;
import com.disney.xband.xbrms.common.model.IHealthSystem;
import com.disney.xband.xbrms.common.model.IdmsDto;
import com.disney.xband.xbrms.common.model.IdmsListDto;
import com.disney.xband.xbrms.common.model.JmsListenerDto;
import com.disney.xband.xbrms.common.model.JmsListenerListDto;
import com.disney.xband.xbrms.common.model.LocationInfoListDto;
import com.disney.xband.xbrms.common.model.PerfMetricMetadataMapDto;
import com.disney.xband.xbrms.common.model.ProblemsReportDto;
import com.disney.xband.xbrms.common.model.ReaderInfoListDto;
import com.disney.xband.xbrms.common.model.XbrcConfigListDto;
import com.disney.xband.xbrms.common.model.XbrcConfigListMapDto;
import com.disney.xband.xbrms.common.model.XbrcConfigurationListDto;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.disney.xband.xbrms.common.model.XbrcIdListDto;
import com.disney.xband.xbrms.common.model.XbrcListDto;
import com.disney.xband.xbrms.common.model.XbrmsConfigDto;
import com.disney.xband.xbrms.common.model.XbrmsStatusDto;
import com.disney.xband.xbrms.server.managed.IdmsSystem;
import com.disney.xband.xbrms.server.managed.JmsListenerSystem;
import com.disney.xband.xbrms.server.managed.XbrcPerfService;
import com.disney.xband.xbrms.server.managed.XbrcService;
import com.disney.xband.xbrms.server.managed.XbrcSystem;
import com.disney.xband.xbrms.server.messaging.publisher.XbrmsConfigurationUpdatePublisher;
import com.disney.xband.xbrms.server.model.HealthItemDao;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import com.disney.xband.xbrms.server.model.XbrmsStatusBo;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/27/12
 * Time: 10:25 PM
 */
public class LocalCall implements IRestCall {
    private transient static final Logger logger = Logger.getLogger(LocalCall.class);
    private transient static final Logger plogger = Logger.getLogger("performance." + IRestCall.class.toString());
    private transient static final Pattern ipPattern = Pattern.compile(PkConstants.IP_PATTERN);

    private final XbrcPerfService xbrcPerfService;
    private final XbrcService xbrcService;
    private final SchedulerServer schedulerServer;

    public LocalCall() 
    {
        this.xbrcPerfService = new XbrcPerfService();
        this.xbrcService = new XbrcService();
        this.schedulerServer = new SchedulerServer();
        this.schedulerServer.initialize(XconnectScheduler.getInstance());
    }

    public void setServerUri(final String serverUri) {
        throw new UnsupportedOperationException();
    }

    /////////////////////////////// FacilitiesResource ///////////////////////////////

    @Override
    public FacilityListDto getFacilities() {
        final FacilityListDto xbrms = new FacilityListDto();

        final List<XbrcDto> facilities = SystemHealthConsumer.getInstance().getInventory(XbrcDto.class);

        // strip alpha characters from venueid
		for (XbrcDto facility: facilities)
		{
			if (facility.getFacilityId() == null)
				continue;

			facility.setFacilityId(facility.getFacilityId().replaceAll("\\D", ""));
		}

        xbrms.setFacility(facilities);

        return xbrms;
    }
    
    @Override
    public FacilityListDto getFacilitiesForSystemHealth() {
        final FacilityListDto xbrms = new FacilityListDto();

        final List<XbrcDto> facilities = SystemHealthConsumer.getInstance().getInventory(XbrcDto.class);

        xbrms.setFacility(facilities);

        return xbrms;
    }

    @Override
    public FacilityDto getFacilityById(String id) {
        final FacilityDto facilityDto = new FacilityDto();

        if (id == null || id.trim().isEmpty())
            return facilityDto;

        for (XbrcDto x : SystemHealthConsumer.getInstance().getInventory(XbrcDto.class))
        {
            if (x.getFacilityId() == null) {
                logger.warn((x.getName() == null ? "" : x.getName()) + ":" + x.getIp() + " doesn't have its facilityDto id set. This means that it will not be included in the data pool used by the /facilities/id call.");
                continue;
            }

            if (x.getFacilityId().equals(id))
                facilityDto.setFacility(x);
        }

        return facilityDto;
    }

    @Override
    public FacilityDto getFacilityByAddr(String ip) {
        final FacilityDto facilityDto = new FacilityDto();

        if (ip == null || ip.trim().isEmpty())
            return facilityDto;

        ip = ip.toLowerCase();

        final String[] nameIp = XbrmsUtils.resolveIp(ip, LocalCall.ipPattern, LocalCall.logger);

        if ((nameIp == null) || (nameIp[1] == null)) {
            return facilityDto;
        }

        for (XbrcDto x : SystemHealthConsumer.getInstance().getInventory(XbrcDto.class))
        {
            if (nameIp[0].equals(x.getHostname()) || nameIp[1].equals(x.getIp()))
            {
                facilityDto.setFacility(x);
                break;
            }
        }

        return facilityDto;
    }

    @Override
    public FacilityListDto getFacilitiesByModelType(String type) {
        final FacilityListDto xbrms = new FacilityListDto();

        try {
            final XbrcModel modelType = XbrcModel.getByModel(type);
            xbrms.setFacility(new ArrayList<XbrcDto>(xbrcService.findByModel(modelType)));

            return xbrms;
        }
        catch (Exception e) {
            LocalCall.logger.error("Failed to get xBRC inventory by model type " + type + ": " + e.getMessage());
        }

        xbrms.setFacility(new ArrayList<XbrcDto>());

        return xbrms;
    }

    @Override
    public FacilityListDto getFacilitiesByStatus(String statusDesc) {
        final FacilityListDto xbrms = new FacilityListDto();

        final List<XbrcDto> facilities = SystemHealthConsumer.getInstance().getInventory(XbrcDto.class);

        // strip alpha characters from venueid
		for (XbrcDto facility: facilities)
		{
			if (facility.getFacilityId() == null) {
				continue;
            }

			facility.setFacilityId(facility.getFacilityId().replaceAll("\\D", ""));
		}

        xbrms.setFacility(this.filterXbrcsByStatus(facilities, statusDesc));

        return xbrms;
    }

    @Override
    public FacilityListDto getFacilitiesByModelTypeAndStatus(String type, String statusDesc) {
        final FacilityListDto xbrms = new FacilityListDto();

        try {
            final XbrcModel modelType = XbrcModel.getByModel(type);
            final List<XbrcDto> facilities = new ArrayList<XbrcDto>(xbrcService.findByModel(modelType));
            xbrms.setFacility(this.filterXbrcsByStatus(facilities, statusDesc));

            return xbrms;
        }
        catch (Exception e) {
            LocalCall.logger.error("Failed to get xBRC inventory by model type " + type + ": " + e.getMessage());
        }

        return xbrms;
    }
    
    @Override
    public FacilityListDto getFacilitiesByReaderStatus(String readerStatusDesc)
    {
    	final FacilityListDto xbrms = new FacilityListDto();
    	
    	try
    	{
    		final List<XbrcDto> allFacilities = SystemHealthConsumer.getInstance().getInventory(XbrcDto.class);
    		
    		final List<XbrcDto> filteredFacilities = new ArrayList<XbrcDto>();
    		
    		for (XbrcDto xbrc : allFacilities)
    		{
    			List<ReaderInfo> unfilteredReaderList = SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getAssignedLinkedReadersList(xbrc.getIp(), xbrc.getPort());
                List<ReaderInfo> filteredReaderList = filterReadersByStatus(unfilteredReaderList, readerStatusDesc);
                
                if (filteredReaderList == null || filteredReaderList.size() == 0)
                	continue;
                
                filteredFacilities.add(xbrc);
    		}
    		
    		xbrms.setFacility(filteredFacilities);
    		
    	}
    	catch (Exception e)
    	{
    		LocalCall.logger.error("Failed to get xBRC inventory containing reader in state " + readerStatusDesc, e);
    	}
    	
    	return xbrms;
    }

    ///////////////////////////// HealthItemMetrics /////////////////////////////

    @Override
    public PerfMetricMetadataMapDto getMetricMetaById(String hiId) {
        try {
            final HealthItemDto item = this.getHealthItemById(hiId);

            if (item == null) {
                throw new RuntimeException("Health item with ID " + hiId + " not found.");
            }

            Map<String, PerfMetricMetadata> metricsMeta = null;

            try {
                metricsMeta = SystemHealthConsumer.getInstance().getMetricMetaByIp(item.getIp(), item.getPort() + "");
            }
            catch (Exception e) {
                LocalCall.logger.warn("Failed to retrieve performance metric metadata.");
            }

            if (metricsMeta == null || metricsMeta.size() == 0) {
                throw new RuntimeException("No performance metadata found.");
            }

            final PerfMetricMetadataMapDto dto = new PerfMetricMetadataMapDto();
            dto.setMap(metricsMeta);

            return dto;
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Failed to get health item. Invalid port number.");
        }
        catch (Exception e) {
            final String error = "Failed to get metrics metadata: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public String getMetrics(String hiId, String perfMetricName, String perfMetricVersion, String startDateLong, String endDateLong) {
        try {
            final Long startDate = Long.parseLong(startDateLong);
            final Long endDate = Long.parseLong(endDateLong);

            final HealthItemDto item = this.getHealthItemById(hiId);

            if (item == null) {
                throw new RuntimeException("Health item with ID " + hiId + " not found.");
            }

            String metrics = null;

            if (perfMetricName != null && !perfMetricName.isEmpty() &&
                    perfMetricVersion != null && !perfMetricVersion.isEmpty())
            {

                // find a single metric by name
                metrics = xbrcPerfService.find(
                        item.getId().intValue(),
                        item.getIp(),
                        startDate.longValue(),
                        endDate.longValue(),
                        perfMetricName,
                        perfMetricVersion);
            }
            else {
                // find all metrics for current facility
                metrics = xbrcPerfService.findAll(
                        item.getId().intValue(),
                        item.getIp(),
                        startDate.longValue(),
                        endDate.longValue());
            }

            if (metrics == null) {
                throw new RuntimeException("Failed to retrieve metrics from database for health item ID=" + hiId);
            }

            return metrics;
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Invalid start/end date format.");
        }
        catch (Exception e) {
            final String error = "Failed to get performance metrics: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    ///////////////////////////////// HealthItem  ///////////////////////////////

    @Override
    public HealthItemDto getHealthItemByAddr(String ip, String portS) {
        try {
            final Integer port = Integer.parseInt(portS);
            return SystemHealthConsumer.getInstance().getHealthItemByAddr(ip, port);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Failed to get health item. Invalid port number.");
        }
        catch (Exception e) {
            final String error = "Failed to get health item: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public HealthItemDto getHealthItemById(String hiId) {
        try {
            final Long id = Long.parseLong(hiId);

            if (hiId != null && id >= 0) {
                return SystemHealthConsumer.getInstance().getHealthItemById(id);
            }
            else {
                return null;
            }
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Failed to get health item. Invalid ID.");
        }
        catch (Exception e) {
            final String error = "Failed to get health item: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public int addHealthItem(String xbrcIp, String xbrcPort, String itemClassName)
    {
        int port = 0;

        try {
            port = Integer.parseInt(xbrcPort);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Invalid port number");
        }

        // First see if we already have this XBRC
        HealthItemDto item = SystemHealthConsumer.getInstance().getHealthItem(xbrcIp, port);

        if (item != null) 
        {
        	if (item.getHostname().equals(item.getIp()) && !item.getIp().equals(xbrcIp))
        	{
        		// we didn't used to have a host name for this item and now we do, update
        		item.setHostname(xbrcIp);
        		HealthItemDao.getInstance().update(item);
        	}
        	
        	if (item.isActive())
        		return 0;
        	else
        	{
        		try
        		{
        			HealthItemDao.getInstance().toogleActiveFlag(item.getId(), true);
        		} catch (Exception e){
        			throw new RuntimeException("Could not activate " + itemClassName);
        		}
        	}
        }

        try
        {
        	item = this.createHealthItem(xbrcIp, port, itemClassName);

        	SystemHealthConsumer.getInstance().addHealthItem(item);
        } catch (Exception e){
        	throw new RuntimeException("Could not create " + itemClassName);
        }

        return 0;
    }

    @Override
    public int deleteHealthItem(String hiId) {
        try {
            final Long id = Long.parseLong(hiId);

            if (hiId != null && id >= 0) {
                SystemHealthConsumer.getInstance().removeHealthItem(id);

                return 0;
            }
            else {
                return -1;
            }
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Failed to delete health item. Invalid ID. Must be Long");
        }
    }

    @Override
    public int deactivateHealthItem(String hiId) {
        try {
            final Long id = Long.parseLong(hiId);

            if (hiId != null && id >= 0) {
                SystemHealthConsumer.getInstance().deactivateHealthItem(id);

                return 0;
            }
            else {
                return -1;
            }
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Failed to deactivate health item. Invalid ID. Must be Long");
        }
    }

    @Override
    public HealthItemListMapDto getHealthItemsInventory() {
        try {
            final Map<String, HealthItemListDto> list = SystemHealthConsumer.getInstance().getInventoryMap();
            final HealthItemListMapDto dto = new HealthItemListMapDto();

            if(list == null) {
                dto.setMap(new HashMap<String, HealthItemListDto>());
            }
            else {
                dto.setMap(list);
            }

            return dto;
        }
        catch (Exception e) {
            final String error = "getHealthItemsInventory() failed: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public HealthItemListDto getHealthItemsInventoryForType(String className) {
        try {
            final HealthItemListDto dto = new HealthItemListDto();
            dto.setHealthItem(new ArrayList<HealthItemDto>(SystemHealthConsumer.getInstance().getInventory(className)));
            return dto;
        }
        catch (Exception e) {
            final String error = "getHealthItemsInventoryForType() failed: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(e.getMessage());
        }
    }

    public XbrcListDto getXbrcListDto() {
        try {
            final XbrcListDto dto = new XbrcListDto();
            dto.setHealthItem(new ArrayList<XbrcDto>(SystemHealthConsumer.getInstance().getInventory(XbrcDto.class)));
            return dto;
        }
        catch (Exception e) {
            final String error = "getHealthItemsInventoryForType() failed: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(e.getMessage());
        }
    }

    public IdmsListDto getIdmsListDto() {
        try {
            final IdmsListDto dto = new IdmsListDto();
            dto.setHealthItem(new ArrayList<IdmsDto>(SystemHealthConsumer.getInstance().getInventory(IdmsDto.class)));
            return dto;
        }
        catch (Exception e) {
            final String error = "getHealthItemsInventoryForType() failed: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(e.getMessage());
        }
    }

    public JmsListenerListDto getJmsListenerListDto() {
        try {
            final JmsListenerListDto dto = new JmsListenerListDto();
            dto.setHealthItem(new ArrayList<JmsListenerDto>(SystemHealthConsumer.getInstance().getInventory(JmsListenerDto.class)));
            return dto;
        }
        catch (Exception e) {
            final String error = "getHealthItemsInventoryForType() failed: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int refreshHealthItemsStatus() {
    	try {
            SystemHealthConsumer.getInstance().refreshStatus();
        }
        catch (Exception e) {
            LocalCall.logger.error("Failed to refresh health items status");
        }

        return 0;
    }

    /////////////////////////////// ReaderLocation //////////////////////////////

    public ReaderInfo getReaderById(String xbrcId, String readerId) {

		if (PkConstants.XBRMS_KEY.equals(xbrcId)) // Unassigned reader
		{
			try {
				return ReaderLocationInfoCache.getInstance().getUnassignedReaderByMac(readerId);
			}
			catch (Exception e)
 			{
				final String error = "Failed to get reader\'s info by MAC: " + e.getMessage();
				LocalCall.logger.error(error);
				throw new RuntimeException(error);
   			}
   		}
   		else
   		{
    	   	try {
    	   		long readerIdLong;

	            try {
	                readerIdLong = Long.parseLong(readerId);
	            }
	            catch (NumberFormatException e) {
	                throw new RuntimeException("Invalid reader ID:  " + readerId);
	            }
	
	            final HealthItemDto item = this.getHealthItemById(xbrcId);
	
	            if (item == null) {
	                return null;
	            }
	
	            if (!item.getType().equals(HealthItemType.XBRC.name())) {
	                return null;
	            }
	
	            return ReaderLocationInfoCache.getInstance().getAssignedReaderById(item.getIp(), item.getPort(), readerIdLong);
	        }
	        catch (Exception e) {
	            final String error = "Failed to get reader\'s info by ID: " + e.getMessage();
	            LocalCall.logger.error(error);
	            throw new RuntimeException(error);
	        }
   		}
    }

    @Override
    public ReaderInfoListDto getReadersForLocation(String xbrcId, String locationId) {
        try {
            final HealthItemDto xbrc = this.getHealthItemById(xbrcId);

            if (xbrc == null) {
                return null;
            }

            if (!xbrc.getType().equals(HealthItemType.XBRC.name())) {
                throw new RuntimeException("Health item with ID=" + xbrcId + " is not xBRC type.");
            }

            Integer locationIdInt = null;

            try {
                locationIdInt = Integer.parseInt(locationId);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Failed to get health item. Invalid port number.");
            }

            final ReaderInfoListDto dto = new ReaderInfoListDto();
            dto.setReaderInfoList(SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getAssignedReaderLocationList(xbrc.getIp(), xbrc.getPort(), locationIdInt));

            return dto;
        }
        catch (Exception e) {
            final String error = "Failed to get readers inventory: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public ReaderLocationInfo getLocationInfo(String xbrcId) {
        try {
            final HealthItemDto xbrc = this.getHealthItemById(xbrcId);

            if (xbrc == null) {
                return null;
            }

            if (!xbrc.getType().equals(HealthItemType.XBRC.name())) {
                throw new RuntimeException("Health item with ID=" + xbrcId + " is not xBRC type.");
            }

            return SystemHealthConsumer.getInstance().getReaderLocationInfo((XbrcDto) xbrc);
        }
        catch (Exception e) {
            final String error = "Failed to get location info: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public LocationInfoListDto getLocations() {
        try {
            final LocationInfoListDto dto = new LocationInfoListDto();
            dto.setLocationInfoList(SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getAssignedReaderLocationList());

            return dto;
        }
        catch (Exception e) {
            final String error = "Failed to get locations inventory: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public ReaderInfoListDto getUnassignedReaders() {
        try 
        {
            SystemHealthConsumer.getInstance().getReaderLocationInfoCache().updateUnassignedReaders();
            
            ReaderInfoListDto container = new ReaderInfoListDto();
            container.setReaderInfoList(SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getUnassignedReaders());
            
            return container;
        }
        catch (Exception e) {
            final String error = "Failed to get unassigned readers inventory: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public ReaderInfoListDto getUnassignedReadersByReaderStatus(final String statusDesc) {
        try 
        {
        	List<ReaderInfo> resultList = new LinkedList<ReaderInfo>();
        	ReaderInfoListDto container = new ReaderInfoListDto();
        	container.setReaderInfoList(resultList);
        	
        	// refresh readers cache
            SystemHealthConsumer.getInstance().getReaderLocationInfoCache().updateUnassignedReaders();
            
            // grab all readers, regardless of status
            final List<ReaderInfo> unfilteredList = SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getUnassignedReaders();
            
            if (unfilteredList == null || unfilteredList.size() == 0)
            	return container;
            
            // filter the list of readers by status
            resultList = this.filterReadersByStatus(unfilteredList, statusDesc);

            return container;
        }
        catch (Exception e) {
            final String error = "Failed to get unassingned readers inventory: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }
    
    @Override
    public ReaderInfoListDto getUnlinkedReaders(String xbrcId) {
        try {
            final HealthItemDto xbrc = this.getHealthItemById(xbrcId);

            if (xbrc == null) {
                return null;
            }

            if (!xbrc.getType().equals(HealthItemType.XBRC.name())) {
                throw new RuntimeException("Health item with ID=" + xbrcId + " is not xBRC type.");
            }

            final ReaderInfoListDto dto = new ReaderInfoListDto();
            List<ReaderInfo> rlist = SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getAssignedUnlinkedReadersList(xbrc.getIp(), xbrc.getPort());

            if(rlist == null) {
                rlist = new ArrayList<ReaderInfo>();
            }

            dto.setReaderInfoList(rlist);

            return dto;
        }
        catch (Exception e) {
            final String error = "Failed to get readers inventory: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public ReaderInfoListDto getUnlinkedReadersByReaderStatus(String xbrcId, String statusDesc) {
        try {
            final HealthItemDto xbrc = this.getHealthItemById(xbrcId);

            if (xbrc == null)
                return null;

            if (!xbrc.getType().equals(HealthItemType.XBRC.name()))
                throw new RuntimeException("Health item with ID=" + xbrcId + " is not xBRC type.");

            final ReaderInfoListDto dto = new ReaderInfoListDto();
            
            if (HAStatusEnum.unknown.name().equalsIgnoreCase(((XbrcDto)xbrc).getHaStatus()) 
            		&& !XbrmsUtils.isEmpty(xbrc.getVip()) && !xbrc.getVip().startsWith("#"))
            {
            	/*
            	 * Special case. Both HA paired xBRCs could be of ha status unknown at the moment.
            	 * In this case, we should get reader info by bypassing the cache and going directly to the VIP, 
            	 * so that when one of them does become master, we are still displaying the correct reader info.
            	 */
            	List<ReaderInfo> unfilteredReaderList = SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getAssignedUnlinkedReadersList(xbrc.getVip(), xbrc.getPort()); 
                List<ReaderInfo> filteredReaderList = filterReadersByStatus(unfilteredReaderList, statusDesc);
                
                if (filteredReaderList == null)
                	filteredReaderList = new ArrayList<ReaderInfo>();
                
                dto.setReaderInfoList(filteredReaderList);
            }
            else
            {
	            List<ReaderInfo> rlist = SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getAssignedUnlinkedReadersList(xbrc.getIp(), xbrc.getPort());
	
	            if(rlist == null)
	                rlist = new ArrayList<ReaderInfo>();
	
	            rlist = this.filterReadersByStatus(rlist, statusDesc);
	
	            dto.setReaderInfoList(rlist);
            }
            
            return dto;
        }
        catch (Exception e) {
            final String error = "Failed to get readers inventory: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public int refreshReadersHealthStatus() {
        try {
            SystemHealthConsumer.getInstance().refreshStatus();
        }
        catch (Exception e) {
            LocalCall.logger.error("Failed to refresh readers status");
        }

        return 0;
    }
    
    @Override
    public int deleteUnassignedReaderFromCache(String mac)
    {
    	if (mac == null || mac.trim().length() == 0)
    		return 400;
    	
    	try
    	{
	    	ReaderInfo reader = ReaderLocationInfoCache.getInstance().getUnassignedReaderByMac(mac);
	    	if (reader == null)
	    		return 404;
	    	
	    	SystemHealthConsumer.getInstance().getReaderLocationInfoCache().removeUnassignedReader(reader);
    	}
    	catch (Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    	
    	return 200;
    }

    ////////////////////////////// ReaderOperations /////////////////////////////
    @Override
    public ReaderInfo getUnassignedReaderByMac(String mac) {
        try 
        {
            return ReaderLocationInfoCache.getInstance().getUnassignedReaderByMac(mac);
        }
        catch (Exception e) {
            final String error = "Failed to get reader\'s info by MAC address: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public ReaderInfo getReaderByMac(String xbrcId, String mac) {
        try {
            final HealthItemDto item = this.getHealthItemById(xbrcId);

            if (item == null)
                return null;

            if (!item.getType().equals(HealthItemType.XBRC.name()))
                return null;
            
            if (HAStatusEnum.unknown.name().equalsIgnoreCase(((XbrcDto)item).getHaStatus()) 
            		&& !XbrmsUtils.isEmpty(item.getVip()) && !item.getVip().startsWith("#"))
            {
            	/*
            	 * Special case. Both HA paired xBRCs could be of ha status unknown at the moment.
            	 * In this case, we should get reader info by bypassing the cache and going directly to the VIP, 
            	 * so that when one of them does become master, we are still displaying the correct reader info.
            	 */
            	final ReaderLocationInfo rlInfo = this.xbrcService.getReaderLocationInfo(item.getVip(), item.getPort());
            	if (rlInfo == null)
            		return null;
            	
            	final List<LocationInfo> locationInfo = rlInfo.getReaderlocationinfo();
            	for (LocationInfo locInfo : locationInfo)
            	{
            		for (ReaderInfo reader : locInfo.getReaders())
            		{
            			if (reader.getMacAddress().equals(mac))
            				return reader;
            		}
            	}
            }
            else
            {
            	return ReaderLocationInfoCache.getInstance().getAssignedReaderByMac(item.getIp(), item.getPort(), mac);
            }
        }
        catch (Exception e) {
            final String error = "Failed to get reader\'s info by MAC address: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
        
		return null;
    }

    @Override
    public int assignReader(String xbrcAddress, String readerMac) 
    {
    	if (XbrmsUtils.isEmpty(xbrcAddress) || XbrmsUtils.isEmpty(readerMac))
    		return 1;
    	
        ReaderInfo reader = null;
        try {
            reader = this.getUnassignedReaderByMac(readerMac);

            if (reader == null) {
                throw new Exception("Reader \"" + readerMac + "\" not found.");
            }

            final String[] parts = xbrcAddress.split(":");
            final String port = parts.length > 1 ? parts[1] : "8080";

            final boolean isOk = SystemHealthConsumer.getInstance().getReaderLocationInfoCache().assignUnassignedReader(parts[0], port, reader, false);

            try {
                // Just a precaution. I suspect that the reader's "hello" thread may not immediately realize it has been
                // reassigned.
                Thread.sleep(1000);
            }
            catch (Exception ignore) {
            }

            try {
                if (isOk && reader != null) {
                    SystemHealthConsumer.getInstance().getReaderLocationInfoCache().removeUnassignedReader(reader);
                }
            }
            catch(Exception ignore) {
            }

            if(!isOk) {
                throw new RuntimeException("Cannot communicate with the reader.");
            }
        }
        catch (Exception e) {
            final String error = "Failed to assign reader " + (reader == null ? "" : (reader.getMacAddress() + "@" + reader.getIpAddress())) + ": " + e.getMessage();
            logger.error(error);
            throw new RuntimeException(error);
        }

        return 0;
    }

    public int identifyReader(String xbrcId, String readerId, String seqName) {
        try {
            return ReaderApi.readerPlayIdentificationSequence(
                    this.getReaderById(xbrcId, readerId),
                    seqName,
                    PkConstants.READER_IDENTIFICATION_SEQUENCE_TIMEOUT);
        }
        catch (Exception e) {
            final String error = "Failed to play identification sequence (readerId=" + readerId + "): " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public int identifyReaderComp(String xbrcId, String readerId) {
        try {
            final ReaderInfo reader = this.getReaderById(xbrcId, readerId);

            if (reader == null) {
                return 404;	// Resource not found at the moment
            }

            if (reader.getType() != null && !reader.getType().hasLight()) {
                return 501;	// Not implemented
            }

            int resultStatusCode = ReaderApi.readerPlayIdentificationSequence(
                   reader,
                    "identify",
                    PkConstants.READER_IDENTIFICATION_SEQUENCE_TIMEOUT);
            
            return resultStatusCode;
        }
        catch (Exception e) {
            final String error = "Failed to play identification sequence (xbrcId:" + xbrcId + ", readerId:" + readerId + "): " + e.toString();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public int rebootReaderComp(String xbrcId, String readerId) {
        try {
            final ReaderInfo reader = this.getReaderById(xbrcId, readerId);

            if (reader == null) {
                throw new RuntimeException("Failed to find reader.");
            }

            int httpStatusCode = ReaderApi.sendRebootReader(this.getReaderById(xbrcId, readerId));

            if (httpStatusCode >= 400) {
                final String error = "Request to reader failed with http status code " + httpStatusCode;

                if (LocalCall.logger.isInfoEnabled()) {
                    LocalCall.logger.info(error);
                }

                throw new RuntimeException(error);
            }
            
            return httpStatusCode;
        }
        catch (Exception e) {
            final String error = "Failed to play identification sequence (readerId=" + readerId + "): " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public int restartReaderComp(String xbrcId, String readerId) {
        try {
            final ReaderInfo reader = this.getReaderById(xbrcId, readerId);

            if (reader == null) {
                throw new RuntimeException("Failed to find reader.");
            }
            
            return ReaderApi.sendRestartReader(this.getReaderById(xbrcId, readerId));
        }
        catch (Exception e) {
            final String error = "Failed to restart reader (readerId=" + readerId + "): " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    /////////////////////////////////// Status //////////////////////////////////

    @Override
    public XbrmsStatusDto getStatus() {
        XbrmsStatusBo.getInstance().getStatus();
        final XbrmsStatusDto dto = XbrmsStatusBo.getInstance().getDto();
        dto.setTime(Calendar.getInstance().getTime());
        return dto;
    }

    @Override
    public ProblemsReportDto getRecentXbrmsProblems() {
        return ProblemsReportBo.getInstance().getDto();
    }

    @Override
    public int clearXbrmsProblemsList() {
        ProblemsReportBo.getInstance().clearErrors();

        return 0;
    }

    ///////////////////////////////// xBRC ////////////////////////////////

    // xBRC configuration

    @Override
    public XbrcConfiguration getXbrcStoredConfig(String configId) {
        Connection conn = null;
        XbrcConfiguration conf = null;

        try {
            conn = SSConnectionPool.getInstance().getConnection();
            Long configIdLong = null;

            try {
                configIdLong = Long.parseLong(configId);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Invalid xBRC config ID: " + configId);
            }

            conf = XbrcConfigurationService.find(conn, configIdLong);
            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
        }
        catch (SQLException e) {
            final String reason = this.processSqlException(e, "getting a stored configuration record");
            throw new RuntimeException(reason);
        }
        catch (Exception e) {
            LocalCall.logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
        }

        return conf;
    }

    @Override
    public XbrcConfiguration getXbrcConfig(String xbrcId, String name) {
        XbrcDto xbrcDto = null;
        XbrcConfiguration conf = null;

        try {
            xbrcDto = this.findXbrc(Long.parseLong(xbrcId));
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Invalid xBRC ID: " + xbrcId);
        }

        if (xbrcDto != null) {
            try {
                conf = this.xbrcService.getConfiguration(xbrcDto, name);
            }
            catch (Exception e) {
                final String error = "Failed to get XBRC configuration: " + e.getMessage();
                LocalCall.logger.error(error);
                throw new RuntimeException(error);
            }
        }

        return conf;
    }

    public XbrcConfiguration getXbrcConfig(String xbrcIp, String xbrcPort, String name) {
        XbrcConfiguration conf = null;

        try {
            final int port = Integer.parseInt(xbrcPort);
            final XbrcDto xbrcDto = new XbrcDto();
            xbrcDto.setIp(xbrcIp);
            xbrcDto.setHostname(xbrcIp);
            xbrcDto.setPort(port);
            xbrcDto.setVip(xbrcIp);
            xbrcDto.setVport(port);

            conf = this.xbrcService.getConfiguration(xbrcDto, name);
        }
        catch (NumberFormatException e) {
            final String error = "Invalid xBRC port: " + xbrcPort;
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
        catch (Exception e) {
            final String error = "Failed to get XBRC configuration: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }

        return conf;
    }

    @Override
    public XbrcConfiguration parseXbrcConfigXml(String xml) {
        try {
            return this.xbrcService.parseConfiguration(xml);
        }
        catch (Exception e) {
            LocalCall.logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public XbrcConfigurationListDto getStoredXbrcConfigs() {
        Connection conn = null;

        try {
            conn = SSConnectionPool.getInstance().getConnection();

            final XbrcConfigurationListDto list = new XbrcConfigurationListDto();
            list.setXbrcConfiguration(new ArrayList<XbrcConfiguration>(XbrcConfigurationService.getAllNoXml(conn, "model,venuename,name")));

            return list;
        }
        catch (SQLException e) {
            final String reason = this.processSqlException(e, "getting stored configuration records");
            throw new RuntimeException(reason);
        }
        catch (Exception e) {
            LocalCall.logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
        }
    }

    @Override
    public int addXbrcConfig(String hiId, XbrcConfiguration conf) {
        Connection conn = null;
        XbrcConfiguration aConf = conf;

        try {
            conn = SSConnectionPool.getInstance().getConnection();

            if(XbrmsUtils.isEmpty(conf.getName())) {
                throw new Exception("Configuration name must not be empty.");
            }

            final Collection<XbrcConfiguration> eList = XbrcConfigurationService.getAllNoXml(conn, "model,venuename,name");

            if(eList != null) {
                for(XbrcConfiguration eConf : eList) {

                    if(conf.getName().equalsIgnoreCase(eConf.getName())) {
                        throw new Exception("Configuration with name " + eConf.getName() + " already exists.");
                    }
                }
            }

            if (!hiId.equals("-1")) {
                XbrcDto xbrcDto = this.findXbrc(Long.parseLong(hiId));
                aConf = this.xbrcService.getConfiguration(xbrcDto, "current");

                aConf.setName(conf.getName());
                aConf.setDescription(conf.getDescription());
                aConf.setVenueCode(conf.getVenueCode());
                aConf.setVenueName(conf.getVenueName());
            }

            // conn = SSConnectionPool.getInstance().getConnection();
            XbrcConfigurationService.save(conn, aConf);
            XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
        }
        catch (SQLException e) {
            final String reason = this.processSqlException(e, "adding a configuration record");
            throw new RuntimeException(reason);
        }
        catch (Exception e) {
            LocalCall.logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
        }

        return 0;
    }

    @Override
    public int deleteXbrcConfig(String configId) {
        Connection conn = null;

        try {
            conn = SSConnectionPool.getInstance().getConnection();
            XbrcConfigurationService.delete(conn, Long.parseLong(configId));
            XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
        }
        catch (SQLException e) {
            final String reason = this.processSqlException(e, "deleting a stored configuration record");
            throw new RuntimeException(reason);
        }
        catch (Exception e) {
            LocalCall.logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
        }

        return 0;
    }

    @Override
    public int deployXbrcConfig(String configId, String xbrcId) {
        Connection conn = null;

        try {
            conn = SSConnectionPool.getInstance().getConnection();
            XbrcDto xbrcDto = null;

            try {
                xbrcDto = this.findXbrc(Long.parseLong(xbrcId));

                if (xbrcDto == null) {
                    return 0;
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Invalid xBRC ID: " + xbrcId);
            }

            Long configIdLong = null;

            try {
                configIdLong = Long.parseLong(configId);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Invalid xBRC config ID: " + configId);
            }

            final XbrcConfiguration conf = XbrcConfigurationService.find(conn, configIdLong);

            if (conf == null) {
                throw new RuntimeException("Cannot find xBRC configuration with ID: " + configId);
            }

            if (xbrcDto.getModel() == null || !XbrcModel.getLongModel(xbrcDto.getModel()).equals(conf.getModel())) {
                throw new RuntimeException("Failed to deploy XBRC configuration: models do not match");
            }

            try {
                // We must set the configuration name to current for XBRC to actually switch to it.
                conf.setName("current");
                this.xbrcService.putConfiguration(conf, xbrcDto);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to put XBRC configuration to " + xbrcDto.getIp() + ": " + e.getMessage());
            }

            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
        }
        catch (SQLException e) {
            final String reason = this.processSqlException(e, "deploying a stored configuration record");
            throw new RuntimeException(reason);
        }
        catch (Exception e) {
            LocalCall.logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
        }

        return 0;
    }

    @Override
    public int updateXbrcConfig(XbrcConfig conf, String hiId) {
        try {
            final XbrcDto xbrc = (XbrcDto) this.getHealthItemById(hiId);

            if(xbrc == null) {
                 throw new RuntimeException("xBRC not found.");
            }

            if (!xbrc.getType().equals(HealthItemType.XBRC.name())) {
                throw new RuntimeException("Invalid health item type.");
            }

            if(!xbrcService.updateConfiguration(conf, xbrc.getIp(), xbrc.getPort())) {
                throw new RuntimeException("Unknown reason.");
            }

            return 0;
        }
        catch (Exception e) {
            final String message = "Failed to update xBRC configuration: " + e.getMessage();
            LocalCall.logger.error(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public int overrideSchedule(String xbrcId, String sHours) {
        try {
            XbrcDto xbrcDto = null;

            try {
                xbrcDto = this.findXbrc(Long.parseLong(xbrcId));

                if (xbrcDto == null) {
                    return 0;
                }
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Invalid xBRC ID: " + xbrcId);
            }

            Long hours;

            try {
                hours = Long.parseLong(sHours);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Invalid hours: " + sHours);
            }

            try {
                // Perform schedule override
                this.xbrcService.putScheduleOverride(xbrcDto, hours);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to override schedule of " + xbrcDto.getIp() + ": " + e.getMessage());
            }

            return 0;
        }
        catch (Exception e) {
            LocalCall.logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int replaceReader(String xbrcId, ReaderInfo oldReader, ReaderInfo newReader) {
        throw new NotImplementedException();
    }

    public int replaceReader(String xbrcId, String oldMac, String oldName, String newReader) {
    	int returnedCode = 500;
    	
        try {
            if(newReader == null) {
                 throw new RuntimeException("Unassigned ReaderInfo object must not be null");
            }

            if(LocalCall.logger.isInfoEnabled()) {
                LocalCall.logger.info("About to replace reader " + oldMac);
            }

            XbrcDto xbrc = null;
            try
            {
            	xbrc = (XbrcDto)SystemHealthConsumer.getInstance().getHealthItemById(Long.parseLong(xbrcId));
            }
           	catch(Exception e)
            {
            	LocalCall.logger.error("Failed to find xBRC with id " + xbrcId, e);
            }

            if(xbrc == null) {
                 throw new RuntimeException("Failed to find xBRC with id " + xbrcId);
            }
            
            returnedCode = (new XbrcService()).replaceReader(xbrc, oldName, newReader);
            
            if (returnedCode <= 200)
            	return returnedCode;
        }
        catch (Exception e) {
            final String message = "Failed to replace reader " + oldMac + " -- " + e.getMessage();
            LocalCall.logger.error(message);
            throw new RuntimeException(message);
        }
        
        if (returnedCode == 404)
        	throw new RuntimeException("Reader " + oldName + " not found. Reader replacement was unsuccessful.");
        else if (returnedCode == 500)
        	throw new RuntimeException("Failed to replace the reader " + oldName);
        else if (returnedCode == 424)
        	throw new RuntimeException("Failed to replace the reader " + oldName + ". Replacement reader was not accessible at the time of the request.");
        else
        	throw new RuntimeException("Failed to replace the reader " + oldName + ". HTTP returned code " + returnedCode);
    }

    // xBRC properties

    @Override
    public XbrcConfigListMapDto getXbrcProperties(XbrcIdListDto list) {
        try {
            final List<String> hiIds = list.getXbrcId();

            if(hiIds == null) {
                 throw new IllegalArgumentException("xBRC IDs list must not be empty.");
            }

            final List<XbrcDto> xbrcs = new LinkedList<XbrcDto>();

            for(String hiId : hiIds) {
                final XbrcDto xbrc = (XbrcDto) this.getHealthItemById(hiId);

                if(xbrc == null) {
                    throw new RuntimeException("xBRC not found. ID=" + hiId);
                }

                if (!xbrc.getType().equals(HealthItemType.XBRC.name())) {
                    throw new RuntimeException("Invalid health item type. ID=" + hiId);
                }

                xbrcs.add(xbrc);
            }

            final Map<String, XbrcConfigListDto> map = this.xbrcService.getProperties(xbrcs);
            final XbrcConfigListMapDto dto = new XbrcConfigListMapDto();

            if(map == null) {
                dto.setMap(new HashMap<String, XbrcConfigListDto>());
            }
            else {
                dto.setMap(map);
            }

            return dto;

        }
        catch (Exception e) {
            final String message = "Failed to get xBRC properties: " + e.getMessage();
            LocalCall.logger.error(message);
            throw new RuntimeException(message);
        }
    }

    //////////////////////////////// XbrmsConfig ////////////////////////////////
    @Override
    public XbrmsConfigDto getXbrmsConfig() {

    	Connection conn = null;

		long begin = System.currentTimeMillis();

        try {
            conn = SSConnectionPool.getInstance().getConnection();

            Config.getInstance().read(conn, XbrmsConfigBo.getInstance().getDto());

            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
        }
        catch (SQLException e) {
            final String reason = this.processSqlException(e, "trying to read persisted xbrms properties");
            throw new RuntimeException(reason);
        }
        catch (Exception e) {
            final String message = "Failed to read configuration properties!";
            LocalCall.logger.error(message);
            throw new RuntimeException(message);
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;

            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Reading XbrmsConfigBo configuration");
            }
        }

        return XbrmsConfigBo.getInstance().getDto();
    }

    @Override
    public int setXbrmsConfig(final XbrmsConfigDto config)
    {
    	XbrmsConfigBo.getInstance().setDto(config);

    	// persist the chages
        XbrmsConfigurationUpdatePublisher.getInstance().notifySubscribers();

        return 0;
    }

    @Override
    public ReaderInfoListDto getLinkedReadersByReaderStatus(final String xbrcId, final String statusDesc) {
        try {
        	final XbrcDto xbrc = (XbrcDto)this.getHealthItemById(xbrcId);

            if (xbrc == null) {
                return null;
            }
            
            ReaderInfoListDto dto = new ReaderInfoListDto();
            
            if (HAStatusEnum.unknown.name().equalsIgnoreCase(xbrc.getHaStatus()) 
            		&& !XbrmsUtils.isEmpty(xbrc.getVip()) && !xbrc.getVip().startsWith("#"))
            {
            	/*
            	 * Special case. Both HA paired xBRCs could be of ha status unknown at the moment.
            	 * In this case, we should get reader info by bypassing the cache and going directly to the VIP, 
            	 * so that when one of them does become master, we are still displaying the correct reader info.
            	 */
            	final ReaderLocationInfo rlInfo = this.xbrcService.getReaderLocationInfo(xbrc.getVip(), xbrc.getPort());
            	if (rlInfo == null)
            		return null;
            	
            	final List<LocationInfo> locationInfoList = rlInfo.getReaderlocationinfo();
            	if (locationInfoList == null)
            		return null;
            	
            	List<ReaderInfo> unfilteredReaderList = new LinkedList<ReaderInfo>(); 
                for (LocationInfo locInfo : locationInfoList)
                {
                	if (locInfo.getReaders() == null)
                		continue;
                	
                	for (ReaderInfo readerInfo : locInfo.getReaders())
                	{
                		unfilteredReaderList.add(readerInfo);
                	}
                }
                List<ReaderInfo> filteredReaderList = filterReadersByStatus(unfilteredReaderList, statusDesc);
                if (filteredReaderList == null)
                	filteredReaderList = new ArrayList<ReaderInfo>();
                dto.setReaderInfoList(filteredReaderList);
            }
            else
            {
            	// get reader info by DIP from the cache
	            SystemHealthConsumer.getInstance().getReaderLocationInfoCache().updateReaderLocationInfo(xbrc.getIp(), xbrc.getPort(), xbrc.getName());
	            final List<ReaderInfo> unfilteredReaderList = SystemHealthConsumer.getInstance().getReaderLocationInfoCache().getAssignedLinkedReadersList(xbrc.getIp(), xbrc.getPort());
	
	            List<ReaderInfo> filteredReaderList = filterReadersByStatus(unfilteredReaderList, statusDesc);
	
	            if (filteredReaderList == null)
	            	filteredReaderList = new ArrayList<ReaderInfo>();
	            
	            dto.setReaderInfoList(filteredReaderList);
            }

            return dto;
        }
        catch (Exception e) {
            final String error = "Failed to get linked readers inventory: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    @Override
    public EnvPropertiesMapDto getServerProps() {
        final EnvPropertiesMapDto dto = new EnvPropertiesMapDto();
        dto.setMap(new HashMap<String, String>());

        final Properties props = ConfigProperties.getInstance();
        final Set<Object> names = props.keySet();
        String val;

        for(Object name : names) {
            final String sname = (String) name;
            val = (String) props.get(name);

            if(sname != null) {
                if(
                    sname.toLowerCase().contains("password") ||
                    sname.toLowerCase().contains("passwd") ||
                    sname.toLowerCase().contains("pwd") ||
                    sname.toLowerCase().contains("pw") ||
                    sname.toLowerCase().contains("secret")
                ) {
                    val = "[ENCRYPTED]";
                }

                dto.getMap().put(sname, val);
            }
        }

        return dto;
    }

    ////////////////////////////////// Audit //////////////////////////////////
    @Override
    public int pushAuditEvents(AuditEventList events) {
        if((events == null) || (events.getEvents() == null)) {
            return 0;
        }

        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(auditor.isAuditEnabled()) {
            auditor.audit(events.getEvents());
        }

        return 0;
    }

    @Override
    public AuditEventList pullAuditEvents(long afterEventId) {
        AuditEventList list = new AuditEventList();
        list.setEvents(new ArrayList<AuditEvent>());

        final IAuditEventsProvider provider = Auditor.getInstance().getEventsProvider();

        if(provider.isAuditEnabled()) {
            if(afterEventId < 0) {
                list.setEvents(provider.getAllEvents());
            }
            else {
                list.setEvents(provider.getEvents(afterEventId));
            }
        }

        return list;
    }

    @Override
    public int deleteAuditEvents(long upToEventId) {
        final IAuditEventsProvider provider = Auditor.getInstance().getEventsProvider();

        if(provider.isAuditEnabled()) {
            if(upToEventId < 0) {
                provider.deleteAllEvents();
            }
            else {
                provider.deleteEvents(upToEventId);
            }
        }

        return 0;
    }

    @Override
    public AuditConfig getAuditConfig() {
        return Auditor.getInstance().getConfig();
    }

    @Override
    public AuditConfig getSubordinateAuditConfigs() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int setAuditConfig(AuditConfig config) 
    {
    	Auditor.getInstance().setConfig(config);
    	
    	XbrmsConfigurationUpdatePublisher.getInstance().notifySubscribers();
		
		return 1;
    }
    
    //////////////////////////////Scheduler ///////////////////////////////////

    @Override
    public SchedulerResponse onSchedulerMessage(SchedulerRequest schedulerRequest)
    {
    	try
    	{
    		return schedulerServer.processRequest(schedulerRequest);
    	}
    	catch (Exception e) {
            final String error = "Failed to process scheduler request: " + e.getMessage();
            LocalCall.logger.error(error);
            throw new RuntimeException(error);
        }
    }

    //////////////////////////// Private methods /////////////////////////////////
    /**
     * This method should only be used to create the object. Do not persist/
     * add to the cache in this method.
     *
     * @param xbrcIp
     * @param port
     * @param itemClassName
     * @return
     */
    private HealthItemDto createHealthItem(String xbrcIp, int port, String itemClassName) {
    	HealthItemDto item = null;

    	try {
    		final Class<?> c = Class.forName("com.disney.xband.xbrms.common.model." + itemClassName);
    		item = (HealthItemDto) c.newInstance();

    		final String[] nameIp = XbrmsUtils.resolveIp(xbrcIp, LocalCall.ipPattern, LocalCall.logger);

    		if ((nameIp != null) && (nameIp[1] != null)) {
    			item.setHostname(nameIp[0]);
    			item.setIp(nameIp[1]);
    		}

    		item.setPort(port);
    	}
    	catch (Exception e) {
    		throw new RuntimeException("Could not create " + itemClassName);
    	}

    	IHealthSystem hs = null;

    	try {
    		if(itemClassName.equals("XbrcDto")) {
    			hs = new XbrcSystem((XbrcDto) item);
    		}
    		else {
    			if(itemClassName.equals("IdmsDto")) {
    				hs = new IdmsSystem((IdmsDto) item);
    			}
    			else {
    				if(itemClassName.equals("JmsListenerDto")) {
    					hs = new JmsListenerSystem((JmsListenerDto) item);
    				}
    			}
    		}

    		item = hs.getDto();
    	}
    	catch (Exception e) {
    		throw new RuntimeException("Failed to connect to " + item.getType() + " at " + xbrcIp + ":" + port);
    	}

    	return item;
    }
    
    /**
     * @param id
     * @return
     */
    private XbrcDto findXbrc(Long id) {
        for (XbrcDto x : SystemHealthConsumer.getInstance().getInventory(XbrcDto.class)) {
            if (x.getId().equals(id)) {
                return x;
            }
        }

        return null;
    }

    private String processSqlException(final SQLException e, String activity) {
        
    	return SSConnectionPool.handleSqlException(e, activity);
    }

    private List<XbrcDto> filterXbrcsByStatus(final List<XbrcDto> facilities, final String statusDesc) {
        final List<XbrcDto> res = new ArrayList<XbrcDto>();

        if(XbrmsUtils.isEmpty(statusDesc) || (facilities == null)) {
            return res;
        }

        for (XbrcDto facility: facilities) {
            if("true".equalsIgnoreCase(facility.getHaEnabled()) && !facility.isValidHaStatus()) {
                facilities.remove(facility);
                continue;
            }

            final String status = facility.getStatus().getStatus().toString();

            if(statusDesc.equalsIgnoreCase(status)) {
              res.add(facility);
            }
            else {
                if(
                   (statusDesc.equalsIgnoreCase("notred") && !status.equalsIgnoreCase("red")) ||
                   (statusDesc.equalsIgnoreCase("notgreen") && !status.equalsIgnoreCase("green"))
                ) {
                    res.add(facility);
                }
            }
        }

        return res;
    }

    private List<ReaderInfo> filterReadersByStatus(List<ReaderInfo> rlist, String statusDesc) {
        if(XbrmsUtils.isEmpty(statusDesc) || (rlist == null)) {
            return rlist;
        }

        final List<ReaderInfo> retReaderList = new ArrayList<ReaderInfo>();

        for(ReaderInfo reader : rlist) {
            final String status = reader.getStatus().toString();

            if (statusDesc.equalsIgnoreCase(status)) {
                retReaderList.add(reader);
            }
            else {
                if (
                    (statusDesc.equalsIgnoreCase("notred") && !status.equalsIgnoreCase("red")) ||
                    (statusDesc.equalsIgnoreCase("notgreen") && !status.equalsIgnoreCase("green"))
                ) {
                    retReaderList.add(reader);
                }
            }
        }

        return retReaderList;
    }
}
