package com.disney.xband.xbrms.common;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;

import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.common.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/4/12
 * Time: 12:06 PM
 */
public interface IRestCall {
    void setServerUri(final String serverUri);

    /////////////////////////////// FacilitiesResource ///////////////////////////////
    FacilityListDto getFacilities();
    FacilityListDto getFacilitiesForSystemHealth();
    FacilityDto getFacilityById(String id);
    FacilityDto getFacilityByAddr(String ip);
    FacilityListDto getFacilitiesByModelType(String type);
    FacilityListDto getFacilitiesByStatus(String statusDesc); // Possible values: red, green, yellow, notred, notgreen
    FacilityListDto getFacilitiesByModelTypeAndStatus(String type, String statusDesc);
    FacilityListDto getFacilitiesByReaderStatus(String readerStatusDesc);

    ///////////////////////////// HealthItemMetrics /////////////////////////////
    PerfMetricMetadataMapDto getMetricMetaById(String hiId);
    String getMetrics(String hiId, String metaName, String metaVersion, String startDateLong, String endDateLong);

    ///////////////////////////////// HealthItem  ///////////////////////////////
    HealthItemDto getHealthItemByAddr(String ip, String port);
    HealthItemDto getHealthItemById(String id);
    int addHealthItem(String ip, String port, String className);
    int deleteHealthItem(String hiId);
    int deactivateHealthItem(String hiId);
    HealthItemListMapDto getHealthItemsInventory();
    HealthItemListDto getHealthItemsInventoryForType(String className);
    int refreshHealthItemsStatus();

    /////////////////////////////// ReaderLocation //////////////////////////////
    ReaderInfoListDto getReadersForLocation(String xbrcId, String locationId);
    ReaderLocationInfo getLocationInfo(String xbrcId);
    ReaderInfoListDto getUnlinkedReaders(String xbrcId);
    ReaderInfoListDto getUnassignedReadersByReaderStatus(String statusDesc); // Possible values: red, green, yellow, notred, notgreen
    ReaderInfoListDto getUnlinkedReadersByReaderStatus(String xbrcId, String statusDesc); // Possible values: red, green, yellow, notred, notgreen
    LocationInfoListDto getLocations();
    int refreshReadersHealthStatus();
    ReaderInfoListDto getLinkedReadersByReaderStatus(String xbrcId, String statusDesc); // Possible values: red, green, yellow, notred, notgreen
    ReaderInfoListDto getUnassignedReaders();
    int deleteUnassignedReaderFromCache(String mac);
    
    ////////////////////////////// ReaderOperations /////////////////////////////
    ReaderInfo getReaderByMac(String xbrcId, String mac);
    ReaderInfo getUnassignedReaderByMac(String mac);
    int assignReader(String xbrcId, String readerMac);
    int identifyReaderComp(String xbrcId, String readerId);
    int rebootReaderComp(String xbrcId, String readerId);
    int restartReaderComp(String xbrcId, String readerId);

    /////////////////////////////////// Status //////////////////////////////////
    XbrmsStatusDto getStatus();
    ProblemsReportDto getRecentXbrmsProblems();
    int clearXbrmsProblemsList();

    ///////////////////////////////// Xbrc ////////////////////////////////
    // Configuration
    XbrcConfiguration getXbrcStoredConfig(String configId);
    XbrcConfiguration getXbrcConfig(String xbrcId, String name);
    XbrcConfigurationListDto getStoredXbrcConfigs();
    XbrcConfiguration parseXbrcConfigXml(String xml);
    int addXbrcConfig(String hiId, XbrcConfiguration conf);
    int deleteXbrcConfig(String configId);
    int deployXbrcConfig(String configId, String xbrcId);
    int updateXbrcConfig(XbrcConfig conf, String hiId);
    int replaceReader(String xbrcId, ReaderInfo oldReader, ReaderInfo newReader);

    // Properties
    XbrcConfigListMapDto getXbrcProperties(XbrcIdListDto xbrcList);

    // Reader Power Management
    int overrideSchedule(String xbrcId, String hours);
    
    //////////////////////////////// XbrmsConfig ////////////////////////////////
    XbrmsConfigDto getXbrmsConfig();
    int setXbrmsConfig(XbrmsConfigDto config);
    EnvPropertiesMapDto getServerProps();

    ////////////////////////////////// Audit //////////////////////////////////
    int pushAuditEvents(AuditEventList events);
    AuditEventList pullAuditEvents(long afterEventId);
    int deleteAuditEvents(long upToEventId);
    AuditConfig getAuditConfig();
    AuditConfig getSubordinateAuditConfigs();
    int setAuditConfig(AuditConfig config);
    
    ////////////////////////////// Scheduler ///////////////////////////////////
    SchedulerResponse onSchedulerMessage(SchedulerRequest schedulerRequest);
}
