package com.disney.xband.xbrms.server;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.IAuditEventsProvider;
import com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.SslUtils;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.LocationInfoDto;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.disney.xband.xbrms.server.managed.XbrcService;
import com.disney.xband.xbrms.server.managed.XbrmsService;
import com.disney.xband.xbrms.server.model.HealthItemDao;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;

/**
 * ReaderLocationInfo cache.
 */
public class ReaderLocationInfoCache 
{
    private Logger logger = Logger.getLogger(ReaderLocationInfoCache.class);
    private XbrcService xbrcService;
    private Map<String, ReaderLocationInfo> rlInfoMap;
    private List<ReaderInfo> unassignedRLInfoCache;
    private Object sLock;
    private Object wLock;
    private volatile boolean startCaching;
    private boolean forceRefresh;
    private SimpleDateFormat sdf;
    
    private ThreadPoolExecutor executor;
    
    private static long lastEventCollectionTime;
    private static long lastEventCollectionTimeParks;
    private static Pattern ipPattern;

    private static class SingletonHolder {
		public static final ReaderLocationInfoCache instance = new ReaderLocationInfoCache();
	}
	
	public static ReaderLocationInfoCache getInstance() {
		return SingletonHolder.instance;
	}
	    
    /**
     * Create an INSTANCE of ReaderLocationInfoCache class.
     */
    private ReaderLocationInfoCache() 
    {
    	// assigned readers
        this.xbrcService = new XbrcService();
        this.rlInfoMap = new HashMap<String, ReaderLocationInfo>(256);
        this.sLock = new Object();
        this.wLock = new Object();
        this.sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        ipPattern = Pattern.compile(PkConstants.IP_PATTERN);
        
        // lost, un-assigned readers
        this.initUnassignedReaderLocationInfo();
        
        executor = new ThreadPoolExecutor(
        		XbrmsConfigBo.getInstance().getDto().getStatusThreadPoolCoreSize(), 
        		XbrmsConfigBo.getInstance().getDto().getStatusThreadPoolMaximumSize(), 
        		XbrmsConfigBo.getInstance().getDto().getStatusThreadPoolKeepAliveTime(), 
        		TimeUnit.SECONDS, 
        		new  LinkedBlockingDeque<Runnable>()
        );
    }

    /**
     * Start caching.
     */
    public void start() {
        this.startCaching = true;

        final Runnable caching = new Runnable() {
            @Override
            public void run() {
                while (startCaching) {
                    // Clean up the cache if necessary
                    synchronized (sLock) {
                        final Iterator<String> it = rlInfoMap.keySet().iterator();

                        if (it.hasNext()) {
                            final String key = it.next();
                            if (!HealthItemDao.getInstance().exists(key)) {
                                rlInfoMap.remove(key);
                            }
                        }
                    }

                    // Update the cache
                    final boolean active = true;
                    
                    // map of health item info: [ip:port, name]
                    final Map<String, String> keys = HealthItemDao.getInstance().discoverAlive(XbrcDto.class, active);

                    if (keys != null)
                    {
	                    final String[] keySet = keys.keySet().toArray(new String[0]);
	                    for (int i = 0; i < keySet.length; i++) 
	                    {
	                    	final String key = keySet[i];
	                    	
	                    	executor.execute(
	                    			new Runnable() {
	                    				@Override
	                    				public void run() {
	                    					try {
	                    						refreshAssignedReaderLocationInfo(key, keys.get(key));
	                    					}
	                    					catch (Exception e) {
	                    						// refresh() does the logging.
	                    					}
	                    				}
	                    			}
	                    	);
	                    }
                    }

                    try {
                        updateAuditConfig();
                        collectAuditEvents();
                        cleanupAuditEvents();
                    }
                    catch (Exception e) {
                    }

                    // Pause if necessary
                    synchronized (wLock) {
                        if (forceRefresh) {
                            forceRefresh = false;
                        }
                        else {
                            try {
                                wLock.wait(XbrmsConfigBo.getInstance().getDto().getAssignedReaderCacheRefresh_sec() * 1000);
                            }
                            catch (InterruptedException ignore) {
                            }
                        }
                    }
                    
                    if (executor != null)
                    	logger.trace("Readers cache thread count: out of " + executor.getPoolSize() + " available threads " + executor.getActiveCount() + " are active.");
                }
            }
        };

        (new Thread(caching)).start();
    }

    /**
     * Stop caching.
     */
    public void stop() {
        this.startCaching = false;
        
        executor.shutdown();
    }

    /**
     * Force refresh of the cache content.
     */
    public void refresh() {
        synchronized (wLock) {
            this.forceRefresh = true;
            wLock.notify();
        }
    }

    /**
     * Add an unassigned reader.
     * 
     * @param reader ReaderInfo INSTANCE
     */
    public void addUnassignedReader(ReaderInfo reader) 
    {
    	if (reader == null)
    		return;
    	
    	synchronized (this.unassignedRLInfoCache) 
        {
            if(reader.getName() != null) 
            {
                boolean found = false;

                for(ReaderInfo ri : this.unassignedRLInfoCache) 
                {
                    if(ri.getName().equals(reader.getName()) || ri.getMacAddress().equals(reader.getMacAddress())) 
                    {
                        this.updateReaderInfo(ri, reader);
                        found = true;
                        break;
                    }
                }

                if(!found) 
                	this.unassignedRLInfoCache.add(reader);
            }
            else 
            {
                ProblemsReportBo.getInstance().setLastError(ProblemAreaType.AssignReader, "addUnassignedReader(): \"name\" property of unassigned reader cannot be empty (mac=" + reader.getMacAddress() + ")");
                this.logger.error("addUnassignedReader(): \"name\" property of unassigned reader cannot be empty");
                return;
            }
        }
    }

    /**
     * Assign a reader to
     * @param xbrc
     * @param reader
     * @return
     */
    public boolean assignUnassignedReader(String xbrcAddress, String xbrcPort, ReaderInfo reader, boolean setError) throws IllegalStateException 
    {
    	if (reader == null || XbrmsUtils.isEmpty(reader.getIpAddress()))
    		return false;
    	
    	if (xbrcAddress == null || XbrmsUtils.isEmpty(xbrcAddress))
    		return false;
    	
    	if (xbrcPort == null || XbrmsUtils.isEmpty(xbrcPort))
    		xbrcPort = "8080";
    	
    	HttpURLConnection connection = null;
        try {
            String templ = (String) ConfigProperties.getInstance().get(PkConstants.PROP_READER_URL_TEMPLATE);

            if(templ == null) {
                templ = "http://%s:%s/xbrc?url=http://%s:%s";
            }

            String url = String.format(templ, reader.getIpAddress(), reader.getPort(), xbrcAddress, xbrcPort);

            if(logger.isTraceEnabled()) {
                logger.trace("Using the following URL for reader reassignment: " + url);
            }

            connection = SslUtils.getConnection(new URL(url));
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.connect();

            final int result = connection.getResponseCode();

            if((result >= 200) && (result < 300)) {
                return true;
            }

            return false;
        }
        catch(Exception e) {
            if(setError) 
            	ProblemsReportBo.getInstance().setLastError(ProblemAreaType.AssignReader, "Failed to assign reader " + (reader == null ? "null" : (reader.getName() + "@" + reader.getIpAddress())), e);

            logger.error("Failed to assign reader " + (reader == null ? "" : (reader.getName() + "@" + reader.getIpAddress())) + " to xbrc " + xbrcAddress + ":" + xbrcPort + ". Exception: " + e.toString());
            return false;
        }
        finally
        {
            if(connection != null) {
                try {
        	        connection.disconnect();
                }
                catch(Exception ignore) {
                }
            }
        }
    }

    /**
     * Remove an unassigned reader.
     * 
     * @param reader ReaderInfo INSTANCE
     */
    public void removeUnassignedReader(ReaderInfo reader) 
    { 
    	if (reader == null)
    		return;
    	
        synchronized (this.unassignedRLInfoCache) {
            if(reader.getName() != null) {

                for(int i = 0; i < this.unassignedRLInfoCache.size(); ++i) {
                    if(this.unassignedRLInfoCache.get(i).getName().equals(reader.getName())) {
                    	this.unassignedRLInfoCache.remove(i);
                        break;
                    }
                }
            }
            else {
                this.logger.warn("removeUnassignedReader(): \"name\" property of unassigned reader cannot be empty");
                return;
            }
        }
    }

    /**
     * Get an item from the cache.
     *
     * @param item XbrcDto info.
     * @return Reader location info.
     * @throws Exception
     */
    public ReaderLocationInfo getAssignedReaderLocationInfo(XbrcDto item) throws Exception {
        final String key = SystemHealthConsumer.createHealthItemKey(item.getIp(), item.getPort());
        ReaderLocationInfo rlInfo = this.rlInfoMap.get(key);

        if (rlInfo == null) {
            rlInfo = updateReaderLocationInfo(item.getIp(), item.getPort(), item.getName());
        }

        return rlInfo;
    }

    /**
     * Get xbrc->locations->readers list.
     *
     * @return Reader location info.
     * @throws Exception
     */
    public List<LocationInfoDto> getAssignedReaderLocationList() throws Exception 
    {
        final List<LocationInfoDto> list = new ArrayList<LocationInfoDto>();
        final Collection<XbrcDto> inventory = HealthItemDao.getInstance().getHealthItems(XbrcDto.class, true);	// active only
        
        String key = null;
        for(XbrcDto xbrc : inventory) 
        {
        	if (!xbrc.isValidHaStatus())
            	continue;
            
            if (xbrc.getHaStatus().toLowerCase().equals(HAStatusEnum.slave.name()))
            	continue;
            
        	key = SystemHealthConsumer.createHealthItemKey(xbrc.getIp(), xbrc.getPort());
        	if (key == null)
        		continue;
        	
            final ReaderLocationInfo rlInfo = this.rlInfoMap.get(key);
            
            if((rlInfo != null) && (xbrc != null)) {
                final LocationInfoDto el = new LocationInfoDto();
                el.setXbrc(xbrc);
                el.setLoc(rlInfo);
                list.add(el);
            }
        }
        
        return list;
    }
    
    /**
     * Get readers for a single location/xbrc combination.
     * 
     * @param xbrcIp
     * @param xbrcPort
     * @param locationId
     * @return
     * @throws Exception
     */
    public List<ReaderInfo> getAssignedReaderLocationList(String xbrcIp, int xbrcPort, int locationId) throws Exception 
    {
    	String key = SystemHealthConsumer.createHealthItemKey(xbrcIp, xbrcPort);
        		
        final ReaderLocationInfo rlInfo = this.rlInfoMap.get(key);
        if (rlInfo.getReaderlocationinfo() == null)
        	return null;
        
        for (LocationInfo locInfo : rlInfo.getReaderlocationinfo())
        {
        	if (locInfo.getId() != locationId)
        		continue;
        	
        	return locInfo.getReaders();
        }
        
        return null;
    }
    
    /**
     * Get UNKNOWN readers for a single xbrc.
     *
     * @param xbrcIp
     * @return
     * @throws Exception
     */
    public List<ReaderInfo> getAssignedUnlinkedReadersList(String xbrcIp, int xbrcPort) throws Exception
    {
    	String key = SystemHealthConsumer.createHealthItemKey(xbrcIp, xbrcPort);

        final ReaderLocationInfo rlInfo = this.rlInfoMap.get(key);

        if ((rlInfo == null) || (rlInfo.getReaderlocationinfo() == null)) {
        	return null;
        }

        for (LocationInfo locInfo : rlInfo.getReaderlocationinfo())
        {
        	if (!"UNKNOWN".equalsIgnoreCase(locInfo.getLocationTypeName())) {
        		continue;
            }

        	return locInfo.getReaders();
        }

        return null;
    }
    
    /**
     * Get readers for a single xbrc. Exclude the UNKNOWN location's readers.
     *
     * @param xbrcIp
     * @return
     * @throws Exception
     */
    public List<ReaderInfo> getAssignedLinkedReadersList(String xbrcIp, int xbrcPort) throws Exception
    {
    	String key = SystemHealthConsumer.createHealthItemKey(xbrcIp, xbrcPort);

        final ReaderLocationInfo rlInfo = this.rlInfoMap.get(key);

        if ((rlInfo == null) || (rlInfo.getReaderlocationinfo() == null)) {
        	return null;
        }

        List<ReaderInfo> result = new LinkedList<ReaderInfo>();
        
        for (LocationInfo locInfo : rlInfo.getReaderlocationinfo())
        {
        	if ("UNKNOWN".equalsIgnoreCase(locInfo.getLocationTypeName()))
        		continue;

        	if (locInfo.getReaders() == null)
        		continue;
        	
        	for (ReaderInfo readerInfo : locInfo.getReaders())
        	{
        		result.add(readerInfo);
        	}
        }

        return result;
    }

    /**
     * Unassigned readers list.
     *
     * @return Reader location info.
     * @throws Exception
     */
    public List<ReaderInfo> getUnassignedReaders() throws Exception 
    {
    	return unassignedRLInfoCache;
    }

    /**
     * Update xbrc->locations->unassigned readers list.
     *
     * @return Reader location info.
     * @throws Exception
     */
    public void updateUnassignedReaders() throws Exception 
    {
        final long curTime = System.currentTimeMillis();
        final List<ReaderInfo> silentReaders = new ArrayList<ReaderInfo>(32);

        final Iterator<ReaderInfo> readers = this.unassignedRLInfoCache.iterator();

        while((readers != null) && readers.hasNext()) {
        	final ReaderInfo reader = readers.next();

        	if((curTime - reader.getTimeLastHello()) > 
        		(XbrmsConfigBo.getInstance().getDto().getUnassignedRreaderCacheCleanup_sec() * 1000)) 
        	{
        		silentReaders.add(reader);
        	}
        }

        final Iterator<ReaderInfo> remove = silentReaders.iterator();

        while(remove.hasNext()) {
            this.removeUnassignedReader(remove.next());
        }
    }

    /**
     * REmove an item from the cache.
     *
     * @param item XbrcDto info.
     */
    public void removeAssignedReaderLocationInfo(XbrcDto item) {
        final String key = SystemHealthConsumer.createHealthItemKey(item.getIp(), item.getPort());

        if(key != null) {
            synchronized (sLock) {
                this.rlInfoMap.remove(key);
            }
        }
    }
    
    public ReaderLocationInfo updateReaderLocationInfo(String ip, int port, String name) throws Exception 
    {
    	final String key = SystemHealthConsumer.createHealthItemKey(ip, port);
    	
        if (key == null || key.trim().isEmpty())
        	return null;
        
        try {
            final ReaderLocationInfo rlInfo = this.xbrcService.getReaderLocationInfo(ip, port);

            if (rlInfo != null) {
                synchronized (sLock) {
                    this.rlInfoMap.put(key, rlInfo);
                }

                return rlInfo;
            }
            else {
                synchronized (sLock) {
                    this.rlInfoMap.remove(key);
                }

                if (this.logger.isInfoEnabled()) {
                    this.logger.info("XbrcDiscoveryConsumer.createHealthItemKey() returned null for health item " +
                            key + ". The monitored rest is probably not running.");
                };
               	
               	return null;
            }
        }
        catch (Exception e) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("updateReaderLocationInfo() call failed for XBRC at " + key);
            };
        }
        
        return null;
    }

    /**
     * Update an item in the cache.
     *
     * @param item XbrcDto info.
     * @return Reader location info.
     * @throws Exception
     */
    public ReaderLocationInfo refreshAssignedReaderLocationInfo(String key, String name) throws Exception 
    {
        if (key == null || key.trim().isEmpty())
        	return null;
        
        String[] keyParts = key.split(":");
		if (keyParts.length != 2)
			return null;
		
		final String ip = keyParts[0];
		int port = 8080;
		try
		{
			port = Integer.parseInt(keyParts[1]);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
        
        try {
            final ReaderLocationInfo rlInfo = this.xbrcService.getReaderLocationInfo(ip, port);

            if (rlInfo != null) {
                synchronized (sLock) {
                    this.rlInfoMap.put(key, rlInfo);
                }

                return rlInfo;
            }
            else {
                synchronized (sLock) {
                    this.rlInfoMap.remove(key);
                }

                if (this.logger.isInfoEnabled()) {
                    this.logger.info("XbrcDiscoveryConsumer.createHealthItemKey() returned null for health item " +
                            key + ". The monitored rest is probably not running.");
                };
               	
               	return null;
            }
        }
        catch (Exception e) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("updateReaderLocationInfo() call failed for XBRC at " + key);
            };
        }
        
        return null;
    }
    
    /**
     * Get a reader by id.
     * 
     * @param xbrcIp this reader belongs to
     * @param xbrcPort Xbrc port
     * @param readerId Reader's id
     */
    public ReaderInfo getAssignedReaderById(String xbrcIp, int xbrcPort, long readerId) {
    	final String key = SystemHealthConsumer.createHealthItemKey(xbrcIp, xbrcPort);
    	 
        final List<LocationInfo> locationInfo = this.rlInfoMap.get(key).getReaderlocationinfo();

        synchronized (locationInfo) {
        	for (LocationInfo locInfo : locationInfo)
        	{
	        	for (ReaderInfo reader : locInfo.getReaders())
	        	{
	        		if (reader.getId() == readerId)
	        			return reader;
	        	}
        	}
        }
        
        return null;
    }
    
    public ReaderInfo getAssignedReaderByMac(String key, String macAddress) {
    	 
        final List<LocationInfo> locationInfo = this.rlInfoMap.get(key).getReaderlocationinfo();

        synchronized (locationInfo) {
        	for (LocationInfo locInfo : locationInfo)
        	{
	        	for (ReaderInfo reader : locInfo.getReaders())
	        	{
	        		if (reader.getMacAddress().equals(macAddress))
	        			return reader;
	        	}
        	}
        }
        
        return null;
    }
    
    public ReaderInfo getAssignedReaderByMac(String macAddress) {

        final Collection<ReaderLocationInfo> locs = this.rlInfoMap.values();

        for(ReaderLocationInfo rli : locs) {
            final List<LocationInfo> locationInfo = rli.getReaderlocationinfo();

            synchronized (locationInfo) {
        	    for (LocationInfo locInfo : locationInfo) {
	        	    for (ReaderInfo reader : locInfo.getReaders()) {
	        		    if (reader.getMacAddress().equals(macAddress))
	        			    return reader;
	        	    }
        	    }
            }
        }

        return null;
    }

    /**
     * Get a reader by mac address.
     * 
     * @param xbrcIp this reader belongs to
     * @param xbrcPort XbrcDto port
     * @param macAddress Reader's MAC
     */
    public ReaderInfo getAssignedReaderByMac(String xbrcIp, int xbrcPort, String macAddress) {
    	final String key = SystemHealthConsumer.createHealthItemKey(xbrcIp, xbrcPort);
    	 
    	return getAssignedReaderByMac(key, macAddress);
    }
    
    public ReaderInfo getUnassignedReaderByMac(String macAddress) 
    {
    	if (macAddress == null || macAddress.trim().isEmpty())
    		return null;
    	
        synchronized (this.unassignedRLInfoCache) 
        {
        	for (ReaderInfo reader : this.unassignedRLInfoCache) 
        	{
        		if (reader.getMacAddress().equals(macAddress))
        			return reader;
        	}
        }

        return null;
    }
    
    /**
     * Initialize structures for unassigned readers.
     */
    private void initUnassignedReaderLocationInfo() 
    {
        this.unassignedRLInfoCache = new LinkedList<ReaderInfo>();
    }

    private void updateReaderInfo(ReaderInfo or, ReaderInfo nr) {
        or.setIpAddress(nr.getIpAddress());
        or.setVersion(nr.getVersion());
        or.setHardwareType(nr.getHardwareType());
        or.setMediaPackageHash(nr.getMediaPackageHash());
        or.setTimeLastHello(nr.getTimeLastHello());
        or.setMinXbrcVersion(nr.getMinXbrcVersion());
        or.setLinuxVersion(nr.getLinuxVersion());
    }
    
    private void collectAuditEvents() {
        try {
            collectAuditEventsFromXbrcs();
        }
        catch (Exception ignore) {
        }

        if(XbrmsConfigBo.getInstance().getDto().isGlobalServer()) {
            try {
                collectAuditEventsFromParks();
            }
            catch (Exception ignore) {
            }
        }
    }
    
    private void updateAuditConfig() {
        try {
            final LogonCache interceptor =
                (LogonCache) Auditor.getInstance().getAuditFactory().getAuditControl().getInterceptor("com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache");

            interceptor.setCacheValidDays(XbrmsConfigBo.getInstance().getDto().getKsExpireLogonDataAfterDays());
            interceptor.setConnectionToMs(XbrmsConfigBo.getInstance().getDto().getKsConnectionToSecs());
        }
        catch(Exception ignore) {
        }
    }

    private void cleanupAuditEvents() {
        try {
            if(XbrmsConfigBo.getInstance().getDto().isGlobalServer()) {
                Auditor.getInstance().getEventsProvider().cleanup(true);
            }
            else {
                Auditor.getInstance().getEventsProvider().cleanup(false);
            }
        }
        catch (Exception e) {
            logger.warn(
                "Failed to clean up audit events on " +
                (XbrmsConfigBo.getInstance().getDto().isGlobalServer() ? "global xBRMS " : "park xBRMS ") +
                XbrmsConfigBo.getInstance().getDto().getName()
            );
        }
    }

    private void collectAuditEventsFromXbrcs() throws Exception {
        if((System.currentTimeMillis() - lastEventCollectionTime) < (Auditor.getInstance().getConfig().getPullIntervalSecs() * 1000)) {
            return;
        }

        lastEventCollectionTime = System.currentTimeMillis();

        final IAuditEventsProvider provider = Auditor.getInstance().getEventsProvider();
        final IAudit audit = Auditor.getInstance().getAuditor();

        if(!provider.isAuditEnabled()) {
            return;
        }

        final boolean active = true;
        final Map<String, HealthItemDto> allHi = HealthItemDao.getInstance().findInventory(active);

        if(allHi != null) {
            for(String key : allHi.keySet()) {
                final HealthItemDto dto = allHi.get(key);

                if(!(dto instanceof XbrcDto)) {
                    continue;
                }
                
                if (!dto.isAlive())
                	continue;

                long lastId = -100;

                try {
                    lastId = provider.getLastAuditIdForHost(dto.getHostname(), false);

                    if(lastId < 0) {
                        lastId = provider.getLastAuditIdForHost(dto.getIp(), false);
                    }
                }
                catch (Exception e) {
                    logger.warn("Failed to get last audit event ID from xBRC " + dto.getHostname());
                    continue;
                }

                if(lastId < -1) {
                    continue;
                }

                if(lastId == -1) {
                    lastId = 0;
                }

                final XbrcService xbrcService = new XbrcService();
                AuditEventList events = null;

                try {
                    events = xbrcService.getEvents((XbrcDto) dto, lastId);
                }
                catch (Exception e) {
                    logger.warn("Failed to pull audit events from xBRC " + dto.getHostname());
                    continue;
                }

                if((events == null) || (events.getEvents() == null)) {
                    continue;
                }

                long maxId = 0;

                for(AuditEvent event : events.getEvents()) {
                    event.setAid(event.getId());

                    if(event.getId() > maxId) {
                        maxId = event.getId();
                    }
                }

                if(audit.audit(events.getEvents())) {
                    try {
                        xbrcService.deleteEvents((XbrcDto) dto, maxId);
                    }
                    catch (Exception e) {
                        logger.warn("Failed to delete audit events on xBRC " + dto.getHostname());
                        continue;
                    }
                }
            }
        }
    }
    
    private void collectAuditEventsFromParks() throws Exception {
        if((System.currentTimeMillis() - lastEventCollectionTimeParks) < (Auditor.getInstance().getConfig().getPullIntervalSecs() * 1000)) {
            return;
        }

        lastEventCollectionTimeParks = System.currentTimeMillis();

        final IAuditEventsProvider provider = Auditor.getInstance().getEventsProvider();
        final IAudit audit = Auditor.getInstance().getAuditor();

        try {
            final LogonCache interceptor =
                (LogonCache) Auditor.getInstance().getAuditFactory().getAuditControl().getInterceptor("com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache");
            interceptor.setClearLogonData(true);
        }
        catch(Exception ignore) {
        }

        if(!provider.isAuditEnabled()) {
            return;
        }

        final List<String> allXbrms = XbrmsConfigBo.getInstance().getDto().getParksUrlList();

        if(allXbrms != null) {
            for(String url : allXbrms) {
                final URI uri = new URI(url);
                final String collectorHost = uri.getHost() + ":" + uri.getPort();
                long lastId = -100;

                try {
                    lastId = provider.getLastAuditIdForHost(collectorHost, true);
                }
                catch (Exception e) {
                    logger.warn("Failed to get last audit event ID from xBRMS " + uri.toString());
                    continue;
                }

                if(lastId < -1) {
                    continue;
                }

                if(lastId == -1) {
                    lastId = 0;
                }

                final XbrmsService xbrmsService = new XbrmsService();
                AuditEventList events = null;

                try {
                    events = xbrmsService.getEvents(uri, lastId);
                    AuditHelper.generateStatusChangeEvent(url, true);
                }
                catch (Exception e) {
                    logger.warn("Failed to pull audit events from xBRMS " + uri.toString());
                    AuditHelper.generateStatusChangeEvent(url, false);
                    continue;
                }

                if((events == null) || (events.getEvents() == null)) {
                    continue;
                }

                long maxId = 0;
                String[] addr;

                for(AuditEvent event : events.getEvents()) {
                    event.setAid(event.getId());
                    event.setCollectorHost(collectorHost);

                    if(event.getHost() != null) {
                        addr = XbrmsUtils.resolveIp(event.getHost(), ipPattern, logger);

                        if(addr != null) {
                            event.setHost(addr[0]);
                        }
                    }

                    if(event.getClient() != null) {
                        addr = XbrmsUtils.resolveIp(event.getClient(), ipPattern, logger);

                        if(addr != null) {
                            event.setClient(addr[0]);
                        }
                    }

                    if(event.getId() > maxId) {
                        maxId = event.getId();
                    }
                }

                if(audit.audit(events.getEvents()) ) {
                    try {
                        xbrmsService.deleteEvents(uri, maxId);
                        AuditHelper.generateStatusChangeEvent(url, true);
                    }
                    catch (Exception e) {
                        logger.warn("Failed to delete audit events on xBRMS " + uri.toString());
                        AuditHelper.generateStatusChangeEvent(url, false);
                        continue;
                    }
                }
            }
        }
    }
}
