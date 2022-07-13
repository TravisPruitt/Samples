package com.disney.xband.xbrms.server;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAudit;
import com.disney.xband.common.lib.audit.IAuditEventsProvider;
import com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.xbrms.common.model.*;
import com.disney.xband.xbrms.server.managed.*;
import com.disney.xband.xbrms.server.model.*;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.health.Status;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.server.messaging.JMSAgent;

public class SystemHealthConsumer {
	private static Logger logger = Logger.getLogger(SystemHealthConsumer.class);
	
	//private Map<String,HealthItemDto> inventory;
	private ConcurrentMap<String,Boolean> refreshing;
	private AtomicBoolean runningStatusThread = new AtomicBoolean(false);
	private Object statusThreadLock = new Object();
	private AtomicBoolean forceRefresh = new AtomicBoolean(false);
    private XbrcPerfService xbrcPerfService = new XbrcPerfService();
    
    // performance metric
    private Map<String, Long> metricLastRefresh;
    private static long metricMetaExpirationSec = 1200;	// in seconds
    
    // readers cache
    private ReaderLocationInfoCache readerLocationInfoCache;
    
    // threads that will execute the HTTP /status call
    private ThreadPoolExecutor executor;

    private static Pattern ipPattern;
    
    private AtomicBoolean master = new AtomicBoolean();
    
    private Timer timer = new Timer();
    
    private static class SingletonHolder {
		public static final SystemHealthConsumer instance = new SystemHealthConsumer();
	}
	
	public static SystemHealthConsumer getInstance() {
		return SingletonHolder.instance;
	}
	
	private SystemHealthConsumer() {
		//inventory = new HashMap<String,HealthItemDto>();
		refreshing = new ConcurrentHashMap<String,Boolean>();
		
		metricLastRefresh = new HashMap<String, Long>();
        ipPattern = Pattern.compile(PkConstants.IP_PATTERN);
        readerLocationInfoCache = ReaderLocationInfoCache.getInstance();
               
        // construct the thread pool
        executor = new ThreadPoolExecutor(
        		XbrmsConfigBo.getInstance().getDto().getStatusThreadPoolCoreSize(), 
        		XbrmsConfigBo.getInstance().getDto().getStatusThreadPoolMaximumSize(), 
        		XbrmsConfigBo.getInstance().getDto().getStatusThreadPoolKeepAliveTime(), 
        		TimeUnit.SECONDS, 
        		new  LinkedBlockingDeque<Runnable>()
        ) ;
        
        logger.info("Performance metrics meta data will be refreshed every " + metricMetaExpirationSec/60 + " minutes.");
        
        timer.schedule(new TimerTask() 
        {
            public void run() 
            {
                if (!Thread.currentThread().isInterrupted()) 
                {
                    try {
                    	if (!MetaDao.amIMaster(XbrmsConfigBo.getInstance().getDto().getMasterPronouncedDeadAfter_sec())) 
                    	{
    						if(logger.isTraceEnabled())
    						    logger.trace("I am not a master. Setting haStatus to empty."); 
    						
    						master.compareAndSet(true, false);
    						    		
                            XbrmsStatusBo.getInstance().getDto().setHaStatus("");
    					}
                        else 
                        {
                        	if(logger.isTraceEnabled())
    						    logger.trace("I am master. Setting haStatus to master.");
                        		
                        	master.compareAndSet(false, true);
                        	
                            XbrmsStatusBo.getInstance().getDto().setHaStatus(HAStatusEnum.master.toString());
                        }
                    }
                    catch (Exception ignore) 
                    {
                    	logger.error("Caught exception: ", ignore);
                    }
                }
                else 
                {
                	logger.fatal("!!!Task responsible for master/slave management has just been cancelled. Restart this application!!!");
                    timer.cancel();
                }
            }
        }, 5000, 5000);	// execute every 5 seconds starting 5 seconds after initialization
	}
	
	public void refreshInventory() 
	{
		if (!master.get())
			return;
		
		try {
			refreshCashe();
		} catch (Exception e) {
			String message = "Failed to read health item list from the database.";
			if (logger.isDebugEnabled())
				logger.error(message, e);
			else
				logger.error(message);
		}
	}
	
	/*
	 * Returns the inventory grouped by class name. The template class T must have a public
	 * constructor that takes HealthItem as an argument.
	 */
	public Map<String, HealthItemListDto> getInventoryMap() throws Exception
	{
		LinkedHashMap<String,HealthItemListDto> ret = new LinkedHashMap<String, HealthItemListDto>();

		for (HealthItemDto item : HealthItemDao.getInstance().findAll(true))
		{
			HealthItemListDto dto = ret.get(item.getClass().getName());

			if (dto == null) {
				final ArrayList<HealthItemDto> list = new ArrayList<HealthItemDto>();
                dto = new HealthItemListDto();
                dto.setHealthItem(list);
				ret.put(item.getClass().getName(), dto);
			}

            dto.getHealthItem().add(item);
		}

		return ret;
	}

	/*
	 * Returns only the requested type of objects.
	 */
	public <T extends HealthItemDto> Collection<T> getInventory(String className) throws ClassNotFoundException {
        final Class clazz = Class.forName("com.disney.xband.xbrms.common.model." + className);
        return this.getInventory(clazz);
    }

	/*
	 * Returns only the requested type of objects.
	 */
	public <T extends HealthItemDto> List<T> getInventory(Class<T> c)
	{
		return HealthItemDao.getInstance().getHealthItems(c, true);
	}
	
    public HealthItemDto getHealthItem(String ip, int port) {
        final String[] addr = XbrmsUtils.resolveIp(ip, ipPattern, logger);

        if(addr == null) {
            return null;
        }

       return HealthItemDao.getInstance().findByIp(addr[1] == null ? ip : addr[1], port);
    }

	public HealthItemDto getHealthItemByAddr(String addr, int port)
	{
        if(addr == null) {
            return null;
        }
        
        String[] address = XbrmsUtils.resolveIp(addr, ipPattern, logger);

       return HealthItemDao.getInstance().findByIp(address[1], port, true);
	}
	
	public HealthItemDto getHealthItemById(Long id)
	{
		return HealthItemDao.getInstance().find(id);
	}

    private static final String LOCALHOST = "localhost";

    private boolean handleLocalhostCase(HealthItemDto item) throws IllegalArgumentException
    {
    	if (item.getIp() == null)
    		throw new IllegalArgumentException("Detected health item with null ip.");
    	
        final boolean isLocalhost =
                item.getIp().equals("127.0.0.1") ||
                item.getIp().equalsIgnoreCase(SystemHealthConsumer.LOCALHOST);

        if(isLocalhost) {
            item.setIp("127.0.0.1");
            item.setHostname(SystemHealthConsumer.LOCALHOST);
        }
        
        return isLocalhost;
    }

    public ReaderLocationInfoCache getReaderLocationInfoCache() {
        return readerLocationInfoCache;
    }

    public static String createHealthItemKey(String ip, int port) {
        if((ip == null) || (ip.trim().length() == 0)) {
            logger.warn("Failed to create a key for a health item with an empty address");
            return null;
        }

        final String[] nameIp = XbrmsUtils.resolveIp(ip, ipPattern, logger);
        if ((nameIp == null) || (nameIp[1] == null) || (nameIp[1].trim().length() == 0)) {
            return null;
        }

        return nameIp[1] + ":" + port;
    }

	/*
	 * Refresh the status of all xbrc object that are or are about to become non responsive. 
	 * This may take a while so we must do this is a separate thread.
	 * We do this mostly for XBRC objects that were added manually and are not receiving JMS messages.
	 */
    public void startStatusThread() 
    {		
    	if (!runningStatusThread.compareAndSet(false, true))
    		return;				

    	// Run the thread
    	Runnable r1 = new Runnable() {

    		public void run() {
    			
    			try {
    				refreshCashe();
    			} catch (Exception e) {
    				String message = "Failed to read xbrc list from the database.";
    				if (logger.isDebugEnabled())
    					logger.error(message, e);
    				else
    					logger.error(message);
    			}

    			synchronized (statusThreadLock) {
    				while (runningStatusThread.get()) 
    				{
    					if (!master.get())
    					{
    						try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								logger.warn("Sleep was interrupted. Probably no big deal.", e);
							}
    						continue;
    					}
    					
    					//
    					// Try to recover from JMS connection failures
    					//
    					
    					try
    	    			{
    						if (JMSAgent.INSTANCE.isEnabled() && !JMSAgent.INSTANCE.isInitializedCorrectly())
    							JMSAgent.INSTANCE.start();    							
    	    			}
    	    			catch(Exception e) {
    	    				logger.error("Failed to initialize the JMSAgent.", e);
    	    			}

    					//
    					// We are master so refresh health items.
    					//
    					
    					Collection<HealthItemDto> cache = HealthItemDao.getInstance().findAll(true);

    					if (cache != null && cache.size() > 0) {
    						Collection<IHealthSystem> inventory = new ArrayList<IHealthSystem>();
    						for (HealthItemDto hi : cache) {
    							inventory.add(HealthSystemFactory.makeHealthSystem(hi));
    						}

    						for (IHealthSystem item : inventory) {
    							Date now = new Date();

    							if (item == null) {
    								continue;
    							}

    							if (forceRefresh.get() || item.getDto().getNextDiscovery() == null || item.getDto().getNextDiscovery().getTime() - now.getTime() < 10000l) {
    								try {
    									refreshHealthItemStatus(item);
                                        AuditHelper.generateStatusEvent(item.getDto().getStatus(), item.getDto());
    								}
    								catch (Exception e) {
    									StringBuffer message = new StringBuffer("Failed to update ");
    									message.append(item.getDto().getType()).append(" status for health item ")
    									.append(item.getDto().getIp()).append(":").append(item.getDto().getPort());

    											if (logger.isDebugEnabled()) {
    												logger.info(message, e);
    											}
    											else {
    												logger.info(message);
    											}
    								}
    							}
    						}
    					}

    					try {
        					forceRefresh.set(false);
        					statusThreadLock.wait(1000);
        				}
        				catch (InterruptedException e) {
        				}
    				}    				
    			}

    			// We are done refreshing the status. This thread can be started again.
    			runningStatusThread.set(false);
    		}
    	};

    	Thread newThread = new Thread(r1);
    	newThread.start();
    	
    	ReaderLocationInfoCache.getInstance().start();
    }
	
	public void stopStatusThread() {
		synchronized(statusThreadLock) {
			runningStatusThread.set(false);
            statusThreadLock.notify();
		}
		
		executor.shutdown();

        getReaderLocationInfoCache().stop();  
	}
	
	public void refreshStatus() {
		if (!master.get())
			return;
		
		synchronized(statusThreadLock) {
			forceRefresh.set(true);
			statusThreadLock.notify();
		}

        this.getReaderLocationInfoCache().refresh();
	}
	
	private class RefreshThread implements Runnable 
	{
		private IHealthSystem item;

		public void setItem(IHealthSystem item)
		{
			this.item = item;
		}

		@Override
		public void run()
		{
			if (item == null)
				return;
			
			String key = SystemHealthConsumer.createHealthItemKey(item.getDto().getIp(),item.getDto().getPort());
			
			logger.trace("Trying to connect to health item " + item.getDto().getName() + " at address " + key);

            Boolean bkey = refreshing.get(key);

			if ((bkey != null) && (bkey == false))
			{
				// this item has just been deleted
				refreshing.remove(key);
				return;
			}
			
			// performance metrics meta data cache update
			try 
			{
				Long lastUpdated = null;
				synchronized(metricLastRefresh){
					lastUpdated = metricLastRefresh.get(key);
				}

				// perf metrics meta data doesn't need to be refreshed very often
				if ((lastUpdated != null && (System.currentTimeMillis() - lastUpdated.longValue() > (metricMetaExpirationSec * 1000))) ||
						lastUpdated == null)
				{
					refreshPerfMetricMetaData(item.getDto());

					synchronized(metricLastRefresh){
						lastUpdated = metricLastRefresh.put(key, new Long(System.currentTimeMillis()));
					}
				}

			}
			catch (Exception e)
			{
				StringBuffer message = new StringBuffer("Exception while refreshing performnace metrics metadata.");
				
				if (logger.isDebugEnabled())
					logger.error(message, e);
				else
					logger.error(message);
			}
			
			if (refreshing.get(key) == false)
			{
				// this item has just been deleted
				refreshing.remove(key);
				return;
			}
			
			// health item status and perf metric update
			try
			{		
				Status curentStatus = null;
				
				String previousMeticPeriodStartTime = null;
				String currentMetricPeriodStartTime = null;

				// save some of the previous values
				final HealthItemDto prevItem = HealthItemDao.getInstance().findByIp(item.getDto().getIp(), item.getDto().getPort());

				// No sLock of any kind around this call..
				curentStatus = item.refreshStatus();

                if (curentStatus != null)
				{
                    if ((item.getDto() instanceof XbrcDto) && !XbrmsUtils.isEmpty(item.getDto().getName()))
					{
						if (prevItem != null && prevItem instanceof XbrcDto) {
							previousMeticPeriodStartTime = ((XbrcDto) prevItem).getStartPerfTime();
						}

						boolean newMetrics = previousMeticPeriodStartTime == null;

						if (previousMeticPeriodStartTime != null) 
						{
							currentMetricPeriodStartTime = ((XbrcDto) item.getDto()).getStartPerfTime();
							newMetrics = !previousMeticPeriodStartTime.equals(currentMetricPeriodStartTime);
						}

                        final XbrcStatus cStatus = (XbrcStatus)curentStatus;

						if (newMetrics && !XbrmsUtils.isEmpty(cStatus.getFacilityId())) {
							xbrcPerfService.insert(cStatus, item.getDto());
						}
					}
				}
				else {
				    // If we failed to contact/refresh the status then pause for 4 seconds to reduce number of
				    // connection attempts.
				    if (curentStatus == null) {
					    for (int i = 0; i < 4 && runningStatusThread.get(); i++) {
						    Thread.sleep(1000);
                        }
                    }
                }

                if(logger.isTraceEnabled()) {
                    logger.trace("Finished contacting health item " + item.getDto().getName() + " at address " + key);
                }
			}
			catch(Exception e)
			{
				StringBuffer message = new StringBuffer("Failed to refresh health item status for item ");
				message.append(item.getDto().getName()).append(" at address ").append(key);
                ProblemsReportBo.getInstance().setLastError(ProblemAreaType.UpdateHealthItem, message.toString(), e);

				if (logger.isDebugEnabled())
					logger.error(message.toString(), e);
				else
					logger.error(message.toString());
			}								
			finally
			{
				// Do this after SQL update/insert so that we don't end up trying to update a record we never inserted. 
				refreshing.remove(key);
			}
		}
	}
	
	private void refreshHealthItemStatus(IHealthSystem item) throws Exception 
	{
		if (item.getDto() == null)
			return;
		
		final String key = SystemHealthConsumer.createHealthItemKey(item.getDto().getIp(), item.getDto().getPort());

        if(key == null) {
            return;
        }
			
        // Check if we are already in the process of refreshing this item..
        if (refreshing.get(key) != null)
        	return;
       		
        // We need to refresh each health item in it's own thread.
        RefreshThread refreshThread = new RefreshThread();
        
        // Clone the item because we will not be locking while we call refreshStatus().
        refreshThread.setItem(HealthSystemFactory.makeHealthSystem(item.getDto().clone()));

        // Need to keep track which items we are refreshing
        refreshing.put(key, true);

        // refresh health item
        executor.execute(refreshThread);
	}
	
	public boolean addHealthItem(HealthItemDto item) throws Exception
	{
		boolean isLocalHost = handleLocalhostCase(item);
		
		// make sure we can resolve its ip address
        final String key = SystemHealthConsumer.createHealthItemKey(item.getIp(), item.getPort());
        
        if(key == null)
            return false;
        
        // see if already exists
        boolean alreadyExists = (HealthItemDao.getInstance().findByIp(item.getIp(), item.getPort()) != null);

        if (alreadyExists)
        	return false;
        
        boolean itemAdded = false;

        // resolve ip and hostname
        if (!isLocalHost && ((item.getHostname() == null) || (item.getHostname().length() == 0)))
        {
	        final String[] nameIp = XbrmsUtils.resolveIp(item.getIp(), ipPattern, logger);
	        if((nameIp != null) && (nameIp[1] != null)){
	        	item.setHostname(nameIp[0]);
	        	item.setIp(nameIp[1]);
	        }
        }

        // attempt to insert item to the cache
        itemAdded = HealthItemDao.getInstance().insert(item);

        if (!itemAdded)
        	return false;
        
        return true;
	}

	public void removeHealthItem(Long id)
	{
		logger.info("About to delete health item id: " + id);
		if (!master.get())
		{
			logger.info("Aborted delete of health item id: " + id + ". Call routed to slave, but only master xBRMS may delete health items.");
			return;
		}
		
		HealthItemDto hi = HealthItemDao.getInstance().find(id);
		if (hi == null)
		{
			logger.info("Aborted delete of health item id: " + id + ". Health item not found.");
			return;
		}
		
		final String key = SystemHealthConsumer.createHealthItemKey(hi.getIp(), hi.getPort());
		
		// If there is an entry int the refreshing table then there is currently a thread running 
		// trying to connect to the item we are deleting. In this case we set the boolean value in the
		// table to false to let the thread know that this item has been deleted.
		refreshing.replace(key, true, false);
			
		boolean deleted = false;
		try
		{
			deleted = HealthItemDao.getInstance().delete(hi);
		}
		catch (Exception e)
		{
			String message = "Failed to delete health item id: " + id;
			if (logger.isDebugEnabled())
				logger.error(message, e);
			else
				logger.error(message, e);
		}						
		
		if (!deleted)
		{
			logger.info("Failed to delete health item id: " + id);
			return;
		}

        if (hi instanceof XbrcDto) {
            try {
                this.getReaderLocationInfoCache().removeAssignedReaderLocationInfo((XbrcDto) hi);
            }
            catch(Exception ignore) {
                // Logging is done inside the cache.
            }
        }
	}
	
	public void deactivateHealthItem(Long id) 
	{
		if (!master.get())
			return;
		
		HealthItemDto hi = HealthItemDao.getInstance().find(id);
		if (hi == null)
			return;
		
        final String key = SystemHealthConsumer.createHealthItemKey(hi.getIp(), hi.getPort());
		
		// If there is an entry int the refreshing table then there is currently a thread running 
		// trying to connect to the item we are deleting. In this case we set the boolean value in the
		// table to false to let the thread know that this item has been deleted.
		refreshing.replace(key, true, false);
		
		boolean deactivated = false;
		try
		{
			deactivated = HealthItemDao.getInstance().toogleActiveFlag(id, false);
		}
		catch (Exception e)
		{
			String message = "Failed to inactivate a HealthItem";
			if (logger.isDebugEnabled())
				logger.error(message, e);
			else
				logger.error(message, e);
		}	
		
		if (!deactivated)
			return;

        if (hi instanceof XbrcDto) {
            try {
                this.getReaderLocationInfoCache().removeAssignedReaderLocationInfo((XbrcDto) hi);
            }
            catch(Exception ignore) {
                // Logging is done inside the cache.
            }
        }
	}
	
	public Map<String, PerfMetricMetadata> getMetricMetaByIp(String ip, String port)
	{
		if (ip == null || ip.trim().isEmpty() || port == null || port.trim().isEmpty())
			return new LinkedHashMap<String, PerfMetricMetadata>();
			
		try 
		{
			return xbrcPerfService.getPerfMetricsMetadataFromDB(ip, Integer.parseInt(port));
		} 
		catch (NumberFormatException e) 
		{
			logger.error("Non numeric port number.");
			return new LinkedHashMap<String, PerfMetricMetadata>();
		}
		catch (Exception e)
		{
			logger.error("Failed to get perf metric meta data for health item " + ip + ":" + port);
			return new LinkedHashMap<String, PerfMetricMetadata>();
		}
	}

	private void refreshCashe() throws Exception 
	{
		// retrieve all active health items
		Collection<HealthItemDto> list = HealthItemDao.getInstance().findAll(true);
		
		for (HealthItemDto item : list) {
            this.splitHostAddress(item);
			final String key = SystemHealthConsumer.createHealthItemKey(item.getIp(), item.getPort());

            if(key == null) {
                continue;
            }
			
			if (item instanceof XbrcDto) 
			{
				// grab fresh perf metric meta data
				refreshPerfMetricMetaData(item);
				
				// grab fresh reader data
				this.getReaderLocationInfoCache().updateReaderLocationInfo(
						item.getIp(), 
						item.getPort(), 
						item.getName()
				);
            }
		}

        this.getReaderLocationInfoCache().refresh();
	}

    private void splitHostAddress(HealthItemDto item) {
        String ip = item.getIp();
        final int ind = ip.indexOf("/");

        if(ind > 0) {
            final String hostName = ip.substring(ind + 1);
            ip = ip.substring(0, ind);
            item.setIp(ip);
            item.setHostname(hostName);
        }
    }

    public ReaderLocationInfo getReaderLocationInfo(XbrcDto item) throws Exception {
        final ReaderLocationInfo rlInfo = this.getReaderLocationInfoCache().getAssignedReaderLocationInfo((XbrcDto) item);

        return rlInfo;
    }
    
    /**
     * This method should only be used to create the object. Do not persist/
     * add to the cache in this method.
     *
     * @param xbrcIp
     * @param port
     * @param itemClassName
     * @return
     */
    public HealthItemDto createHealthItem(String xbrcIp, int port, String itemClassName) {
    	HealthItemDto item = null;

    	try {
    		final Class<?> c = Class.forName("com.disney.xband.xbrms.common.model." + itemClassName);
    		item = (HealthItemDto) c.newInstance();

    		final String[] nameIp = XbrmsUtils.resolveIp(xbrcIp, ipPattern, logger);

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

            if(hs != null) {
    		    item = hs.getDto();
            }
    	}
    	catch (Exception e) {
    		throw new RuntimeException("Failed to connect to " + item.getType() + " at " + xbrcIp + ":" + port);
    	}

    	return item;
    }
    
    private void refreshPerfMetricMetaData(HealthItemDto healthItem)
    {
    	if (healthItem == null)
    		return;
    	
    	try
    	{
	    	// grab fresh perf metric meta data from the health item
			Collection<PerfMetricMetadata> perfMetricMeta = xbrcPerfService.getPerfMetricsMetadataFromHealthItem(healthItem.getIp(), healthItem.getPort());
	    	
			// write it to the database
			if (perfMetricMeta != null && perfMetricMeta.size() > 0)
				xbrcPerfService.insert(perfMetricMeta, healthItem);
	    }
    	catch (Exception e)
    	{
    		logger.error("Failed to refresh performance metric meta data for health item " + healthItem.getHostname());
    	}
    }
}
