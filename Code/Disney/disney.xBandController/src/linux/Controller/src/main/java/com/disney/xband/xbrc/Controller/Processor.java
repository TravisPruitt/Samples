package com.disney.xband.xbrc.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.SchedulerException;

import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.audit.AuditFactory;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAuditConnectionPool;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.health.DiscoveryInfo;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.lib.xbrapi.BioEvent;
import com.disney.xband.lib.xbrapi.LrrEvent;
import com.disney.xband.lib.xbrapi.RawAVMSEvent;
import com.disney.xband.lib.xbrapi.ReaderExecutor;
import com.disney.xband.lib.xbrapi.TapEvent;
import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.lib.xbrapi.XbrEventType;
import com.disney.xband.xbrc.Controller.model.ESBInfo;
import com.disney.xband.xbrc.Controller.model.WatchedBandInfo;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.config.UnlinkReader;
import com.disney.xband.xbrc.lib.config.UpdateConfig;
import com.disney.xband.xbrc.lib.config.UpdateConfigItem;
import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.entity.HAMessage;
import com.disney.xband.xbrc.lib.entity.HAStatusMessage;
import com.disney.xband.xbrc.lib.entity.LocationType;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.idms.IDMSResolver;
import com.disney.xband.xbrc.lib.model.ControllerInfo;
import com.disney.xband.xbrc.lib.model.IXBRCModel;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xbrc.scheduler.XbrcSchedulerHelper;
import com.disney.xband.xbrc.scheduler.XbrcSchedulerSerializer;

public class Processor implements Comparator<XbrEvent>
{
	// loggers
	private static Logger logger = Logger.getLogger(Processor.class);
	private static Logger plogger = Logger.getLogger("performance."
			+ Processor.class.toString());

	// singleton
	public static Processor INSTANCE = new Processor();

	// Double buffered list of events from all readers
	private List<XbrEvent> alEventsA = new ArrayList<XbrEvent>();
	private List<XbrEvent> alEventsB = new ArrayList<XbrEvent>();
	private List<XbrEvent> alEventsFromReaders = alEventsA;
	private List<XbrEvent> alEventsBeingProcessed = alEventsB;
	
	// Recent band activity list used to determine events to defer
	private HashSet<String> alRecentBandActivity = new HashSet<String>();

	// event processor
	private IXBRCModel model = null;

	// used to keep track of bands being watched
	private Map<String, WatchedBandInfo> mapWatchedBands = new Hashtable<String, WatchedBandInfo>();

	// termination flag
	private boolean bTerminate = false;

	// "reread" flag
	private boolean bReReadConfiguration = false;
	private UpdateConfig updateConfig = null;

	// performance and other data
	private XbrcStatus status = new XbrcStatus();
	
	// last time we dealt with transmitters
	private Date dtLastTransmit = null;
	
	// wait object allowing notifications from other threads
	private Object waitObj = new Object();
	
	// Mayhem database schema version
	private String dbSchemaVersion = null;
	private String dbSchemaModel = null;
	
	private boolean databaseConnected = true;
	private Date lastDatabaseConnectionCheck = null;
	
	private AtomicInteger mainThreadLoopCount = new AtomicInteger(0);
	private AtomicInteger lastProcessingDuration = new AtomicInteger(0);
	
	private String lastHAStatus = HAStatusEnum.unknown.name();
	private Date lastHAStatusChange = new Date();
	
	private static String defaultVenueName = "xBRC";
    public static Properties prop = null;
    
    private VidSystemStatus vidSystemStatus = null;

	static
	{
		try 
		{
			/*
			 * Figure out what the default xbrc's venue id is. Relies on existence of:
			 * 
			 * @MetaData(name = "venue", description = "Facility id", defaultValue = "xBRC")
			 * private String sVenueName;
			 */
			
			Field f = ConfigOptions.INSTANCE.getControllerInfo().getClass().getDeclaredField("sVenueName");
			
			f.setAccessible(true);
			
			if (f.isAnnotationPresent(MetaData.class))
				defaultVenueName = f.getAnnotation(MetaData.class).defaultValue();
		} 
		catch (Throwable t) 
		{
			logger.error("!!!High Availability might not work correctly! The sVenueName field on com.disney.xband.xbrc.Controller.model.ControllerInfo object " +
					"must have been renamed/removed. Update the static initializer in the Processor class!!!");
		}
	}
	
	private Processor()
	{
		status.setBootTime(new Date());
	}
	
	/***********************************************************************************************************
	 * Wake up the main thread loop because of events that need to be processed right away.
	 ***********************************************************************************************************/
	public void notifyOfHighPriorityEvent()
	{
		synchronized(waitObj)
		{
			waitObj.notify();
		}
	}

	/***********************************************************************************************************
	 * This is the main thread loop. KEEP IT FAST!
	 ***********************************************************************************************************/
	public int Process()
	{
		// Load the model
		try
		{
			INSTANCE.loadModel();
		}
		catch (Exception e)
		{
			logger.fatal(ExceptionFormatter.format("Failed to load model: "
					+ ConfigOptions.INSTANCE.getControllerInfo().getModel(), e));
			return 1;
		}
		
		// initialize the name resolver. Let the model figure out how many threads it needs.
		IDMSResolver.INSTANCE.initialize(model.getIDMSResolverThreads());

		// HA status starts out as unknown
		setHaStatus(HAStatusEnum.unknown.toString());
		
		// initialize with what's in the information that's included in the
		// status variable
		updateStatusObjectWithConfigValues();
		
		// restore any stored state (don't do this if HA is enabled)
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			if (!ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
			{
				model.restoreState(conn, ConfigOptions.INSTANCE.getStoredStatus().getLastStateStore());
				// Now synchronize the state that the model now has. 
				model.storeState(conn);
			}
			else
			{
				Statement stmt = conn.createStatement();
				stmt.execute("DELETE FROM GST");
			}
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Error clearing or reloading GST", ex));
		}
		finally
		{
			if (conn!=null)
				Controller.getInstance().releasePooledConnection(conn);
		}		
		
		initializeScheduler();

		// Inform the web server we are ready
		WebServer.setInitialized(true);

		// run the simulator forever
		for (;;)
		{
			// check the termination flag
			if (bTerminate)
				break;
			
			if (mainThreadLoopCount.incrementAndGet() == Integer.MAX_VALUE)
				mainThreadLoopCount.set(1);

			// outer try/catch in case anything fails here
			try
			{
				Date dtStart = new Date();

				// process some events
				INSTANCE.doProcess();

				// at this point, there shouldn't be any events!
				if (alEventsBeingProcessed.size() > 0)
				{
					logger.error("Events remain in queue! Clearing...");
					alEventsBeingProcessed.clear();
				}

				Date dtEnd = new Date();
				long cmsecDuration = dtEnd.getTime() - dtStart.getTime();
				lastProcessingDuration.set((int)cmsecDuration);

				// if the operation took less time than the processing period,
				// wait until the next window
				double pctUtilization = ((double) cmsecDuration * 100.0)
												/ ConfigOptions.INSTANCE.getControllerInfo().getEventProcessingPeriod();
				status.getPerfMainLoopUtilization().processValue(pctUtilization);
				plogger.trace("Main process loop took " + cmsecDuration
						+ " msec. Utilization: " + pctUtilization);
				if (cmsecDuration < ConfigOptions.INSTANCE.getControllerInfo().getEventProcessingPeriod())
				{
					long cmsecWait = ConfigOptions.INSTANCE.getControllerInfo().getEventProcessingPeriod() - cmsecDuration;
					plogger.trace("Main loop waiting for: " + cmsecWait + " milliseconds");
					try
					{
						synchronized(waitObj)
						{
							waitObj.wait(cmsecWait);
						}
					}
					catch (InterruptedException e)
					{
					}					
				}
			}			
			catch (Exception ex)
			{
				logger.error(ExceptionFormatter.format(
						"Exception in Process loop", ex));
			}

		}
		
		model.onShutdown();
		
		// Gracefully shutdown the reader executor.
		ReaderExecutor.getInstance().shutdown();
        // Wait for all reader executor threads to exit for up to 3 seconds.
		try 
		{
			if (!ReaderExecutor.getInstance().awaitTermination(3000l))
				logger.warn("ReaderExecutor - waited for over 3 seconds for all threads to finish. Forcing termination.");
		} 
		catch (InterruptedException e) 
		{
		}
		
		return 0;
	}
	
	private void initializeScheduler()
	{
		try 
		{
			XconnectScheduler.getInstance().initialize(XBRCController.getInstance().getProperties(), new XbrcSchedulerSerializer());
		} 
		catch (SchedulerException e) 
		{
			logger.error("Failed to initialize the scheduler. No scheduled task will run.", e);
			return;
		}
		
		try 
		{
			XbrcSchedulerHelper.registerClasses(XconnectScheduler.getInstance());
		} 
		catch (Exception e) 
		{
			logger.error("Controller failed to register scheduler tasks metadata. Some scheduled task may not run.", e);
		}
		
		try 
		{
			model.registerSchedulerItemsMetadata(XconnectScheduler.getInstance());
		} 
		catch (Exception e) 
		{
			logger.error("Model failed to register scheduler tasks metadata. Some scheduled task may not run.", e);
		}
		
		try 
		{
			XconnectScheduler.getInstance().loadItems();
		} 
		catch (Exception e) 
		{
			logger.error("Failed to load scheduled items from the Mayhem database.", e);
		}
		
		try 
		{
			model.addDefaultSchedulerItems(XconnectScheduler.getInstance());
		} 
		catch (Exception e) 
		{
			logger.error("Model failed to add default scheduler items. Some scheduled task may not run.", e);			
		}
		
		try 
		{
			XconnectScheduler.getInstance().start();
		} 
		catch (Exception e) 
		{
			logger.error("Failed to start the scheduler.", e);
		}
	}

	/***********************************************************************************************************
	 * This is the body of the main thread "loop" - KEEP THESE FUNCTIONS FAST!
	 ***********************************************************************************************************/

	private void doProcess()
	{
		// if requested, re-read configuration information
		configurationChangeProcessing();

		// perform the double buffer swap and send events to the EKG file
		preProcessing();

		// let the model process the events
		performModelProcessing();

		// perform other periodic processing
		postProcessing();
	}

	/*
	 * If requested, re-read the configuration information
	 */
	private void configurationChangeProcessing()
	{
		// re-read the configuration file when requested to
		if (bReReadConfiguration)
		{
			try
			{
				boolean oldAuditIsEnabled = ConfigOptions.INSTANCE.getAuditConfig().isEnabled();
				
				// store the old ESB settings
				ESBInfo oldesb = ConfigOptions.INSTANCE.getESBInfo();
				String oldTopic = oldesb.getJMSTopic();
				int oldDiscoveryTime = oldesb.getJmsDiscoveryTimeSec();
				int oldJmsSendInterval = oldesb.getJmsSendIntervalMs();

				if (updateConfig != null)
				{
					List<UpdateConfigItem> updateConfigItems = updateConfig.getUpdateConfigItems();
					for (UpdateConfigItem item : updateConfigItems)
					{
						if (item instanceof UnlinkReader)
						{
							UnlinkReader unlinkReader = (UnlinkReader)item;
							Connection conn = null;

							try
							{
								conn = Controller.getInstance().getPooledConnection();

								ConfigOptions.INSTANCE.getReadersInfo().getReaders();
								Reader reader = ReaderService.find(conn, unlinkReader.getReaderId());
								if (reader != null)
								{
									synchronized (ConfigOptions.INSTANCE.getReadersInfo())
									{
										for (ReaderInfo ri : ConfigOptions.INSTANCE.getReadersInfo()
												.getReaders())
										{
											if (ri.getId() == reader.getId())
											{
												ri.setLocation(ConfigOptions.INSTANCE.getLocationInfo().get(0));
												break;
											}
										}
									}
									ReaderService.unlinkReader(conn, reader, Controller.getInstance().getXbrcAddresses());
								}
							}
							catch (Exception e)
							{
								logger.error(ExceptionFormatter.format(
										"Failed to unlink reader " + unlinkReader.getReaderId(), e));
							}
							finally
							{
								if (conn != null)
									Controller.getInstance().releasePooledConnection(conn);
							}
						}
					}
				}				
				
				ConfigOptions.INSTANCE.refreshConfigurationTable();
				
				Log.initialize(ConfigOptions.INSTANCE.getControllerInfo().isUselog4jforeventdump() ? 
								new EventdumpLog4jLog(ConfigOptions.INSTANCE.getControllerInfo().getEventdumpBufferSize()) : 
									new EventdumpFileLog());

				// let the model process anything it needs to
				model.readConfiguration();
					
				// now, update the information that's included in the status variable
				updateStatusObjectWithConfigValues();
				
				// initialize the tansmit manager since transmit commands may have changed
				TransmitManager.INSTANCE.reinitialize(ConfigOptions.INSTANCE.getLocationInfo());
				
				// notify the JMSAgent of a config change (if necessary)
				ESBInfo newesb = ConfigOptions.INSTANCE.getESBInfo();
				if (	!oldTopic.equals(newesb.getJMSTopic()) ||
						oldDiscoveryTime != newesb.getJmsDiscoveryTimeSec() ||
						oldJmsSendInterval != newesb.getJmsSendIntervalMs() )
				{
					JMSAgent.INSTANCE.setProcessConfigChange(true);
				}
				
				HTTPAgent.INSTANCE.setProcessConfigChange(true);

                //Reinitialize audit here
                Processor.initializeAudit();
                
                try 
        		{
        			XconnectScheduler.getInstance().reloadItems();
        		} 
        		catch (Exception e) 
        		{
        			logger.error("Failed to reload scheduled items from the Mayhem database.", e);
        		}
                
              
				try
				{
					ConfigOptions.INSTANCE.auditConfigurationChanges(oldAuditIsEnabled);
				}
				catch (Exception e)
				{
					logger.error("Failed to audit configuration changes.");
				}
			}
			catch (Exception e)
			{
				logger.error(ExceptionFormatter.format(
						"Failed to read configuration", e));
			}
			
			bReReadConfiguration = false;
		}
	}

	/*
	 * Perform double buffer swap and EKG output
	 */
	private void preProcessing()
	{
		Date dtStart = new Date();

		// swap the double buffers
		synchronized (alEventsFromReaders)
		{
			if (alEventsBeingProcessed == alEventsA)
			{
				alEventsBeingProcessed = alEventsB;
				alEventsFromReaders = alEventsA;
			}
			else
			{
				alEventsBeingProcessed = alEventsA;
				alEventsFromReaders = alEventsB;
			}
		}

		// sort and display the data
		try
		{
			// logger.debug("Sorting events");
			int cItems = alEventsBeingProcessed.size();
			if (cItems > 0)
			{
				status.getPerfEvents().processValue(cItems);
				Log.INSTANCE
						.logEKG(new Date().getTime() + ",PROCESS," + cItems);

				// sort the events by time
				Collections.sort(alEventsBeingProcessed, this);

				if (alEventsBeingProcessed.size() != cItems)
				{
					logger.error("Sorting event list changed its size!");
				}

				// if configured, remove events that are "too recent" in order
				// to avoid data slicing issues
				if (ConfigOptions.INSTANCE.getControllerInfo().getReduceDataSlicing())
					deferRecentLRREvents();

				// check size again as events might have all been culled by
				// deferRecentLrrEvents
				if (alEventsBeingProcessed.size() > 0)
				{
					// calculate oldest message date
					XbrEvent evFirst = alEventsBeingProcessed.get(0);
					if (evFirst == null)
					{
						logger.warn("Null event in list");
					}
					else
					{
						long cmsecAge = dtStart.getTime()
								- evFirst.getTime().getTime();
						status.getPerfEventAgeMsec().processValue(cmsecAge);
					}

					// Log to EKG
					logToEKGFile();
				}
			}
			else
			{
				// plogger.trace("No events to process");
			}
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Error sorting and/or logging to EKG", ex));
		}
	}
	
	/*
	 * Reset list of band id/packet number activity data
	 */
	private void clearRecentBandActivityList()
	{
		this.alRecentBandActivity.clear();
	}
	
	/*
	 * Add entry to band id/packet number activity data
	 */
	private void addToBandActivityList(
			String Xlrid,
			Long packetNumber
			)
	{
        StringBuilder BandPno = new StringBuilder();
        BandPno.append(Xlrid);
        BandPno.append("-");
        BandPno.append(packetNumber.toString());

        if ( !this.alRecentBandActivity.contains(BandPno.toString()))
		{
		    this.alRecentBandActivity.add(BandPno.toString());
		}
	}
	
	/*
	 * Check for entry in band id/packet number activity data
	 */
	private boolean isInBandActivityList(
			String Xlrid,
			Long packetNumber
		    )
	{
        StringBuilder BandPno = new StringBuilder();
        BandPno.append(Xlrid);
        BandPno.append("-");
        BandPno.append(packetNumber.toString());

        return (!this.alRecentBandActivity.contains(BandPno.toString()));
	}

	/*
	 * Heuristic algorithm to avoid data slicing issues. Note that this
	 * algorithm relies on clock synchronization between the xBRC and all of the
	 * long range reads.
	 * 
	 * The general approach to reduce data slicing issues is to not process any
	 * LRR events that are "too recent" in anticipation that some reader may yet
	 * report the same event (same pno). An event is too recent if it's
	 * timestamp is within T milliseconds of the current time where T is the
	 * reader data send period.
	 */
	private void deferRecentLRREvents()
	{
		// what's the time criterion
		long msecNow = new Date().getTime();
		long timeTooRecent = msecNow
				- ConfigOptions.INSTANCE.getControllerInfo()
						.getReaderDataSendPeriod();

		Log.INSTANCE.logEKG(Long.toString(msecNow) + ",TIME_CUT_OFF," + Long.toString(timeTooRecent));

		// reset our tracking information for packets being processed
		clearRecentBandActivityList();

		// walk through the sorted events
		List<XbrEvent> liTooNew = new ArrayList<XbrEvent>();
		for (Iterator<XbrEvent> it = alEventsBeingProcessed.iterator(); it.hasNext();)
		{
			XbrEvent ev = it.next();

			// skip non LRR events
			if (!(ev instanceof LrrEvent))
				continue;
			
			LrrEvent lev = (LrrEvent) ev;

			// if the event is too new, push it into the other buffer
			if (ev.getTime().getTime() > timeTooRecent)
			{
				// is event from a reader and packet number we have seen before? If not, defer it.
				if (!isInBandActivityList(lev.getXlrid(), lev.getPno()))
				{
					logEventToEKGFile(ev, "DEFERRED");
					liTooNew.add(ev);
					it.remove();					
				}
			}
			else
			{
				// add event for LRR identifier and packet number to recently seen list table.
				addToBandActivityList(lev.getXlrid(), lev.getPno());
			}
		}

		// if we have any culled messages, add them to the other list
		if (liTooNew.size() > 0)
		{
			Log.INSTANCE.logEKG(Long.toString(msecNow) + ",DEFER,"
					+ liTooNew.size());
			
			pushEvents(liTooNew);
		}

	}
	
	private void processBandTransmits()
	{
		XBRCController controller = XBRCController.getInstance();
		
		// see if we need to do any band transmits
		Date dtNow = new Date();
		long msecdiff = dtLastTransmit == null ? (controller.getXBrTransmitterPeriod() + 1) : (dtNow.getTime() - dtLastTransmit.getTime());
		
		if (msecdiff > controller.getXBrTransmitterPeriod()) // Default minimum period is 2 seconds
		{
			dtLastTransmit = dtNow;
			
			Collection<ReaderInfo> readers = null;
			for(LocationInfo li : controller.getReaderLocations())
			{
				if (li.getLocationTypeID() == LocationType.Undefined.ordinal())
					continue;
				
				// cache of LRIDs at this location
				List<String> liLRIDs = null;
				
				readers = XBRCController.getInstance().getReaders(li.getName());
				if (readers == null || readers.size() == 0)
					continue;
					
				if (li.getTransmitCommands() == null || li.getTransmitCommands().size() == 0)
					continue;

				try
				{
					/*
					 * Only REPLY to a list of bands commands are supposed to be re-sent.
					 * BROADCAST and REPLY to signal strength threshold commands may only be sent once.
					 */
					List<XbrBandCommand> replyToBand = new ArrayList<XbrBandCommand>();
					for (XbrBandCommand cmd : li.getTransmitCommands())
					{
						if (cmd.getMode() == XMIT_MODE.BROADCAST)
							continue;
						
						// must be a REPLY by signal strength threshold, these shouldn't be re-sent
						if (cmd.getRecipients() == null || cmd.getRecipients().size() == 0)
							continue;
						
						replyToBand.add(cmd);
					}
					
					if (replyToBand.size() > 0)
					{
						// populate recipient list for each command, if specified
						for( XbrBandCommand cmd : replyToBand)
						{
							
							// get a list of bands to apply this command to, if exists
							liLRIDs = model.getBandsPresentAtLocations(cmd.getRecipients());
							cmd.setListLRIDs(liLRIDs);

							if (cmd.getListLRIDs() != null)
							{
								for (String id : cmd.getListLRIDs())
								{
									if (logger.isTraceEnabled())
										logger.trace("Guest band LRID (" + id + ") is a candidate for the transmit list of command id " + cmd.getId());
								}
							}
							
						}

						TransmitManager.INSTANCE.sendCommands(replyToBand, li, XMIT_MODE.REPLY, null);						
					}
					
				} catch (Exception e) {
					logger.error("Caught unhandled exception while processing transmit payload for location (" + li.getName() + ")", e);
				}
			}
		}
	}

	/*
	 * Let the model handle the events
	 */
	private void performModelProcessing()
	{
		Date dtStart, dtEnd;
		try
		{
			// Allow to model to do any other processing per pool cycle.
			// This gets called even if there are not reader events to process.
			dtStart = new Date();
			model.beforeProcessEvents();
			dtEnd = new Date();

			long msec = dtEnd.getTime() - dtStart.getTime();
			status.getPerfPreModelingMsec().processValue((double) msec);

		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Model failed to execute beforeProcessEvents", e));
		}

		// Perform the 1st stage data analysis
		if (alEventsBeingProcessed.size() > 0)
		{
			plogger.trace("Beginning model event processing stage");
			try
			{
				dtStart = new Date();
				model.processEvents(alEventsBeingProcessed);
				dtEnd = new Date();
				
				long msec = dtEnd.getTime() - dtStart.getTime();
				double msecPerEvent = (double)msec / alEventsBeingProcessed.size();
				status.getPerfModelingMsec().processValue(msecPerEvent);
			}
			catch (Exception e)
			{
				logger.error(ExceptionFormatter.format("Model failed to process events", e));
			}
			plogger.trace("Ending second stage of singulation");

			// release the events
			alEventsBeingProcessed.clear();
		}
		else
		{
			// plogger.trace("No events to process in second stage of singulation");
		}

		try
		{
			// Allow to model to do any other processing per pool cycle.
			// This gets called even if there are not reader events to process.
			dtStart = new Date();
			model.afterProcessEvents();
			
			// handle band transmission stuff, but only if we're master or solo!
			HAStatusEnum haThis = HAStatusEnum.getStatus(getHaStatus());
			if (haThis == HAStatusEnum.master || haThis == HAStatusEnum.solo)
				processBandTransmits();
			
			dtEnd = new Date();

			long msec = dtEnd.getTime() - dtStart.getTime();
			status.getPerfPostModelingMsec().processValue((double) msec);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Model failed to execute afterProcessEvents", e));
		}
		
		// Notify the model of HA status change
		String haStatus = getHaStatus();
		
		if (!lastHAStatus.equals(haStatus))
		{
			logger.trace("HA status has changed from " + lastHAStatus + " to " + haStatus + " Notifying the model.");
			try
			{
				HAMessage[] messages = { new HAStatusMessage(HAStatusEnum.getStatus(lastHAStatus),
						HAStatusEnum.getStatus(haStatus))};
				model.processHAMessages(messages);
			}
			catch (Exception e)
			{
				logger.error(ExceptionFormatter.format(
						"Failed to process HA message", e));
			}
			finally
			{
				lastHAStatus = haStatus;
				lastHAStatusChange = new Date();
			}
		}
		
		// process any HA events
		if (ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
		{
			HAMessage[] aHA = JMSAgent.INSTANCE.getHAMessages();
			if (aHA!=null && aHA.length>0)
			{
				logger.trace("Received: " + aHA.length + " HA messages");
				try
				{
					processHAMessages(aHA);
				}
				catch (Exception e)
				{
					logger.error(ExceptionFormatter.format(
							"Failed to process HA message", e));
				}
			}			
		}
		
		// periodically store state
		Date dtNow = new Date();
		long msecSpan = dtNow.getTime() - ConfigOptions.INSTANCE.getStoredStatus().getLastStateStore().getTime();
		if (msecSpan > ConfigOptions.INSTANCE.getControllerInfo().getStateSavePeriod())
		{
			Connection conn = null;
			try
			{
				conn = Controller.getInstance().getPooledConnection();
				model.storeState(conn);
				dtEnd = new Date();
				msecSpan = dtEnd.getTime() - dtNow.getTime();
				status.getPerfSaveGSTMsec().processValue(msecSpan);
			}
			catch(Exception ex)
			{
				logger.error(ExceptionFormatter.format("Model failed to store state", ex));
			}
			finally
			{
				if (conn!=null)
					Controller.getInstance().releasePooledConnection(conn);
			}
			ConfigOptions.INSTANCE.getStoredStatus().setLastStateStore(dtNow);
		}
	}

	private void processHAMessages(HAMessage[] aHA) 
	{
		// first, perform any model-independent processing
		for(HAMessage ham : aHA)
		{
			if (ham.getMessageType().equals("DISCOVERY"))
	    		processDiscoveryMessage(ham.getMessageText());
		}
		
		// let the model process the messages
		model.processHAMessages(aHA);
	
	}
	
	private void processDiscoveryMessage(String messageText) 
	{
		try
		{
			ObjectMapper om = new ObjectMapper();
			DiscoveryInfo di = om.readValue(messageText, DiscoveryInfo.class);
			
			// switch to slave if someone asserts mastery
			HAStatusEnum haOther = HAStatusEnum.getStatus(di.getHaStatus());
			logger.info("Got DISCOVERY message from " + di.getIp() + " in status " + haOther);
			if (haOther == HAStatusEnum.master)
			{
				// if currently a master, just change status to slave
				// if currently unknown, go get status from master
				// if currently a slave, see if we have to reread the configuration info
				
				HAStatusEnum haThis = HAStatusEnum.getStatus(getHaStatus());
				if (haThis == HAStatusEnum.master)
				{
					// If we recently switched from slave to master then ignore 
					// this discovery message to prevent flip-flopping between slave and master. 
					if (lastHAStatus.equals(HAStatusEnum.slave.toString()))
					{
						Date now = new Date();
						int secondsSinceChange = (int)(now.getTime() - lastHAStatusChange.getTime()) / 1000;
						if (secondsSinceChange < ConfigOptions.INSTANCE.getESBInfo().getJmsDiscoveryTimeSec()) 
						{
							logger.warn("Ignoring Discovery message from " + di.getIp() + " in status " + haOther
										+ " because we bacame master only " + secondsSinceChange + " seconds ago");
							return;
						}
					}
					logger.info("Changing HA status from master to slave");
					setHaStatus(HAStatusEnum.slave.toString());
				}
				else if (haThis == HAStatusEnum.unknown)
				{
					logger.info("Changing HA status from unknown to slave");
					setHaStatus(HAStatusEnum.slave.toString());
					
					// synchronize with master
					synchronizeWithMaster(di);
				}
				else if (haThis == HAStatusEnum.slave)
				{
					Date dtConfigChangeMaster = DateUtils.parseDate(di.getConfigurationChangedTime());
					if (dtConfigChangeMaster.after(ConfigOptions.INSTANCE.getConfigurationChangedDate()))
					{
						logger.info("Resynchronizing configuration with master");
						synchronizeConfigWithMaster(di);
					}
				}
				else if (haThis == HAStatusEnum.solo)
				{
					logger.error("Solo xBRC got discover message from a Master xBRC on the same venue/topic. Ignored");
				}
			}
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format("Error processing DISCOVERY message", e));
		}
		
	}
	
	private void synchronizeWithMaster(DiscoveryInfo di)
	{
		try
		{
			// synch configuration information 
			synchronizeConfigWithMaster(di);
			
			// now, do GST
			String sURL;
			sURL = "http://" + di.getIp() + ":" + Integer.toString(di.getPort()) + "/state";
			String sGSTXML = sendGet(sURL, 10000);
			
			// TODO: review what to do if the GST synchonization fails. For now, the
			// code continues merrily.
			
			// process the XML
			if (sGSTXML!=null)
				model.deserializeStateFromXML(sGSTXML);
			else
				logger.error("Unable to synchronize state with master");
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format("Error synchronizing with master GXP", e));
		}
	}

	private void synchronizeConfigWithMaster(DiscoveryInfo di)
	{
		String sConfigurationXML = null;
		try
		{
			// request current configuration from master
			String sURL = "http://" + di.getIp() + ":" + Integer.toString(di.getPort()) + "/currentconfiguration";
			sConfigurationXML = sendGet(sURL, 10000);
	
			// TODO: review what to do if we can't synchronize with the master. For
			// now, the code continues merrily.
			
			// TODO: consider moving this code out of the web server
			if (sConfigurationXML!=null)
			{
				WebServer.INSTANCE.switchToConfiguration("current", sConfigurationXML, "xConnect:HA");
	
				// set the change date to be the same as the master's
				Date dtConfigChangeMaster = DateUtils.parseDate(di.getConfigurationChangedTime());
				ConfigOptions.INSTANCE.setConfigurationChangedDate(dtConfigChangeMaster);
			}
			else
				logger.error("Unable to synchronized configuration with master");
			
		}
		catch(Exception e)
		{
			logger.error(ExceptionFormatter.format("Error synchronizing configuration with master GXP", e));
			if (sConfigurationXML!=null && ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
				logger.error("Configuration XML: " + sConfigurationXML);
		}
	}

	/*
	 * Other periodic cleanup
	 */
	private void postProcessing()
	{
		// prune any watched bands that haven't had any activity for a while
		synchronized (mapWatchedBands)
		{
			for (Iterator<String> it = mapWatchedBands.keySet().iterator(); it
					.hasNext();)
			{
				String sID = it.next();

				WatchedBandInfo wbi = mapWatchedBands.get(sID);
				Date dtNow = new Date();
				if ((dtNow.getTime() - wbi.getLastAccessTime().getTime()) > (ConfigOptions.INSTANCE
						.getControllerInfo().getWatchedBandTimeout() * 1000))
					it.remove();
			}
		}
		
		// clear the metrics if it's been long enough
		Date dtNow = new Date();
		Date dtStart = status.getStartPerfDate();
		long sec = (dtNow.getTime() - dtStart.getTime()) / 1000;
		if (sec > ConfigOptions.INSTANCE.getControllerInfo()
				.getPerfMetricsPeriod())
			status.clearPerfValues();
	}

	/***********************************************************************************************************
	 * Public functions and interface implementations
	 ***********************************************************************************************************/

	public void Terminate()
	{
		bTerminate = true;
		
		try 
		{
			XconnectScheduler.getInstance().stop();
		} 
		catch (Exception e) {
			logger.warn("Caught an exception while trying to stop the scheduler", e);
		}
	}

	public void pushEvents(List<XbrEvent> liFromReader)
	{
		synchronized (alEventsFromReaders)
		{
			alEventsFromReaders.addAll(liFromReader);
		}
	}

	public void addWatchedBand(String sID)
	{
		synchronized (mapWatchedBands)
		{
			if (!mapWatchedBands.containsKey(sID))
				mapWatchedBands.put(sID, new WatchedBandInfo());
		}
	}

	public void removedWatchedBand(String sID)
	{
		mapWatchedBands.remove(sID);
	}

	public WatchedBandInfo getWatchedBandInfo(String sID)
	{
		return mapWatchedBands.get(sID);
	}

	public IXBRCModel getModel()
	{
		return model;
	}

	public synchronized void reReadConfiguration(UpdateConfig updateConfig)
	{
		this.updateConfig = updateConfig;
		// set flag to do this in an orderly fashion
		this.bReReadConfiguration = true;		
	}

	@Override
	public int compare(XbrEvent arg0, XbrEvent arg1)
	{
		// handle null cases
		if (arg0 == null && arg1 != null)
			return -1;
		else if (arg0 != null && arg1 == null)
			return 1;
		else if (arg0 == null && arg1 == null)
			return 0;

		// tap events have priority over other events
		if (arg0 instanceof TapEvent)
			if (!(arg1 instanceof TapEvent))
				return -1;
		
		if (arg1 instanceof TapEvent)
			if (!(arg0 instanceof TapEvent))
				return 1;
			
		// sort by timestamp
		return arg0.getTime().compareTo(arg1.getTime());
	}
	
	private void readDbSchemaVersion()
	{		
		Connection dbConn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "select * from SchemaVersion";
			pstmt = dbConn.prepareStatement(sSQL);
			pstmt.execute();
			rs = pstmt.getResultSet();
			if (rs.next())
			{
				dbSchemaVersion = rs.getString("version");
				dbSchemaModel = rs.getString("model");
			}
		}		
		catch (Exception ex)
		{
			logger.error("Failed to read SchemaVersion from the database: "
					+ ex.getLocalizedMessage());
		}
		finally
		{
			if (rs != null)
				try { rs.close(); } catch (Exception e){}
			if (pstmt != null)
				try { pstmt.close(); } catch (Exception e){}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}
	
	/*
	 * simple functions for getting and setting HA status in a synchronized fashion
	 */
	
	public String getHaStatus()
	{
		return status.getHaStatus();
	}
	
	public void setHaStatus(String haStatus)
	{
		status.setHaStatus(haStatus);
	}
	
	
	/*
	 * Returns the status object. Used for quick access.
	 */
	public XbrcStatus getStatusObject()
	{
		return status;
	}
	
	/*
	 * returns the status object after filling it in with more dynamic
	 * information
	 */
	public XbrcStatus getStatus()
	{
		// envelope data
		status.setName(ConfigOptions.INSTANCE.getControllerInfo().getName());
		status.setTime(formatTime(new Date().getTime()));
		status.setFacilityId(ConfigOptions.INSTANCE.getControllerInfo().getVenue());

		status.setJMSBroker(JMSAgent.INSTANCE.getBrokerUrl());
		status.setUpdateStreamURL(ConfigOptions.INSTANCE.getControllerInfo()
				.getUpdateStreamURL());
		long lLastMessageIdToJMS = ConfigOptions.INSTANCE.getStoredStatus().getLastMessageIdToJMS();
		status.setLastMessageToJMS(lLastMessageIdToJMS);
		long lLastMessageIdToUpdateStream = ConfigOptions.INSTANCE.getStoredStatus().getLastMessageIdToPostStream();
		status.setLastMessageToUpdateStream(lLastMessageIdToUpdateStream);
		
		// stow for later
		long lLastMessageSentSomewhere = Math.max(lLastMessageIdToJMS, lLastMessageIdToUpdateStream);

		String version = this.getClass().getPackage().getImplementationTitle();
		if (version == null || version.isEmpty())
			version = "Development";
		status.setVersion(version);

		status.setReaderLocationsCount(ConfigOptions.INSTANCE.getLocationInfo()
				.size());
		status.setModel(ConfigOptions.INSTANCE.getControllerInfo().getModel());
		status.setPerfMetricsPeriod(ConfigOptions.INSTANCE.getControllerInfo()
				.getPerfMetricsPeriod());

		// fill in the dynamic elements
		status.setTime(formatTime(new Date().getTime()));
		status.setMessageCount(getMessageCount());
		status.setHttpMessageCount(getHttpMessageCount());
		
		// get the max id seen in the database. NOTE: on older versions of MySQL this can be
		// zero if there are no rows in the table but will snap to the right value when a row is added
		long lLastMessageSeq = getMaxMessageSequence();
		
		// correct if wacky
		if (lLastMessageSeq < lLastMessageSentSomewhere)
			lLastMessageSeq = lLastMessageSentSomewhere;
		
		status.setLastMessageSeq(lLastMessageSeq);

		// assess our status
		StatusType statusOverall = StatusType.Green;
		String sReason = "";
		
		StatusType readerStatus = StatusType.Green;
		String sReaderReason = "";		
		
		List<ReaderInfo> readers = ConfigOptions.INSTANCE.getReadersInfo().getReadersClone();
		
		synchronized (ConfigOptions.INSTANCE.getReadersInfo())
		{			
			updateReaderStatus(readers);
		}
		
		HAStatusEnum haThis = HAStatusEnum.getStatus(getHaStatus());				
		if (haThis == HAStatusEnum.master || haThis == HAStatusEnum.solo)
		{
			for (ReaderInfo ri : readers)
			{
				if (ri.getLocation().getId() == 0 || !ri.isEnabled())
					continue;
				
				if (readerStatus.isParameterMoreSevere(ri.getStatus()))
				{
					readerStatus = ri.getStatus();
					sReaderReason = ri.getName() + ": " + ri.getStatusMessage();
				}
			}
		}
			
		if (statusOverall.isParameterMoreSevere(readerStatus))
		{
			statusOverall = readerStatus;			
			sReason = sReaderReason;
		}
		
		// TODO: consider other causes for yellow or red status
		status.setStatus(statusOverall);
		status.setStatusMessage(sReason);
		
		String httpAgentStatusMessage = HTTPAgent.INSTANCE.getStatusMessage();
		if (httpAgentStatusMessage != null)
		{
			status.setStatus(StatusType.Yellow);
			status.setStatusMessage(httpAgentStatusMessage);
		}
		
		String jmsAgentStatusMessage = JMSAgent.INSTANCE.getStatusMessage();
		if (jmsAgentStatusMessage != null)
		{
			status.setStatus(StatusType.Yellow);
			status.setStatusMessage(jmsAgentStatusMessage);
		}		

		// Allow the model to tweak status info
		if (Processor.INSTANCE.getModel() != null)
			Processor.INSTANCE.getModel().formatStatus(status);
		
		status.setHaenabled(ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled());
		
		// Monitor the VID system status
		if (vidSystemStatus != null && status.getStatus().isParameterMoreSevere(StatusType.Yellow))
		{
			if ((System.currentTimeMillis() - vidSystemStatus.getLastHelloTime().getTime()) > ConfigOptions.INSTANCE.getControllerInfo()
					.getReaderHelloTimeoutSec() * 1000)
			{
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("VID Software System is not responding");
			}
			else if (vidSystemStatus.getStatus() != StatusType.Green && 
					vidSystemStatus.getStatusMessage() != null && !vidSystemStatus.getStatusMessage().isEmpty())
			{
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("VID system error: " + vidSystemStatus.getStatusMessage());			
			}
		}
		
		if (XBRCController.getInstance().getProperties().getProperty("nge.xconnect.parkid") == null || 
			XBRCController.getInstance().getProperties().getProperty("nge.xconnect.parkid").isEmpty())
		{
			status.setStatusMessage("The nge.xconnect.parkid key is not set in environment.properties. This means that all JMS messages will be ignored.");
			status.setStatus(StatusType.Yellow);
		}
		else
		{
			status.setParkid(XBRCController.getInstance().getProperties().getProperty("nge.xconnect.parkid"));
		}
		
		status.setVip(ConfigOptions.INSTANCE.getControllerInfo().getVipAddress());
		
		// make sure high availability status is not undefined, when enableha is true
		if (ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
		{	
			// if HA is enabled and a vip address has been configured, set it
			String vipAddress = status.getVip();
			if (vipAddress == null || vipAddress.trim().length() == 0 || vipAddress.trim().startsWith("#"))
			{
				status.setStatusMessage("VIP not configured.");
				status.setStatus(StatusType.Yellow);
			}
						
			if (HAStatusEnum.getStatus(getHaStatus()) == HAStatusEnum.unknown)
			{				
				if (status.getStatus().isParameterMoreSevere(StatusType.Yellow))
				{			
					// need JMS to work for HA to work.
					if (jmsAgentStatusMessage == null)
						status.setStatusMessage("High availability status unknown.");
					else
						status.setStatusMessage("High availability status unknown. (" + jmsAgentStatusMessage + ")");
					status.setStatus(StatusType.Yellow);
				}
			}			
		}
		
		// warn about disk space
		try
		{
			File f = new File(ConfigOptions.INSTANCE.getControllerInfo().getDiskSpaceMonitorPath());
			
			long freeSpaceMb = (f.getUsableSpace() >> 20);
			
			if (freeSpaceMb < ConfigOptions.INSTANCE.getControllerInfo().getDiskSpaceFatalMb())
			{
				status.setStatus(StatusType.Red);
				status.setStatusMessage("Disk space on " + ConfigOptions.INSTANCE.getControllerInfo().getDiskSpaceMonitorPath()
						+ " is critically low (" + freeSpaceMb + " MB)");
			}
			else if (freeSpaceMb < ConfigOptions.INSTANCE.getControllerInfo().getDiskSpaceWarnMb())
			{
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("Disk space on " + ConfigOptions.INSTANCE.getControllerInfo().getDiskSpaceMonitorPath()
						+ " is low (" + freeSpaceMb + " MB)");
			}
		}
		catch (Exception e)
		{
			logger.warn( "Failed to check disk space for path " + 
						 ConfigOptions.INSTANCE.getControllerInfo().getDiskSpaceMonitorPath(), e);
		}
		
		// Check the Mayhem database schema. This will override any other errors.
		if (dbSchemaVersion == null)
			readDbSchemaVersion();
		
		status.setDatabaseVersion(dbSchemaVersion);
		
		if (dbSchemaModel == null || dbSchemaVersion == null)
		{
			status.setStatusMessage("The Mayhem database schema version cannot be determined.");
			status.setStatus(StatusType.Red);
		}
		else if (!ConfigOptions.INSTANCE.getControllerInfo().getModel().contains(dbSchemaModel))
		{
			status.setStatusMessage("The Mayhem database schema is for the " + dbSchemaModel + " model. It does not match the loaded model: " + 
					ConfigOptions.INSTANCE.getControllerInfo().getModelName());
			status.setStatus(StatusType.Red);
		}
		else
		{
			if (model!=null)
			{
				String requiredVersion = model.getRequiredSchemaVersion();
				if (!requiredVersion.equals(dbSchemaVersion))
				{
					status.setStatusMessage("The current Mayhem database schema version " + dbSchemaVersion + " is different from the required version " + requiredVersion);
					status.setStatus(StatusType.Red);
				}
			}
		}
		
		// check database connection
		Date now = new Date();
		if (lastDatabaseConnectionCheck == null || (now.getTime() - lastDatabaseConnectionCheck.getTime() > (databaseConnected ? 60000 : 20000)) )
		{			
			lastDatabaseConnectionCheck = now;
			databaseConnected = checkDbConnection();			
		}
		
		if (!databaseConnected)
		{
			status.setStatusMessage("Cannot connect to the MySQL server Mayhem database.");
			status.setStatus(StatusType.Red);
		}
		
		// last configuration change
		status.setConfigurationChangedDate(ConfigOptions.INSTANCE.getConfigurationChangedDate());
        status.setAuditEnabled(Auditor.getInstance().getConfig().isEnabled());

		return status;
	}
	
	public void updateReaderStatus(List<ReaderInfo> readers)
	{
		long timeout = ConfigOptions.INSTANCE.getControllerInfo().getReaderHelloTimeoutSec() * 1000;

		double readerTemperatureMaxYellow = ConfigOptions.INSTANCE.getControllerInfo().getReaderTemperatureMaxYellow();
		double readerTemperatureMinYellow = ConfigOptions.INSTANCE.getControllerInfo().getReaderTemperatureMinYellow();
		double readerTemperatureMaxRed = ConfigOptions.INSTANCE.getControllerInfo().getReaderTemperatureMaxRed();
		double readerTemperatureMinRed = ConfigOptions.INSTANCE.getControllerInfo().getReaderTemperatureMinRed();
		
		// Map for detecting duplicate device ID issues
		Map<Integer, ReaderInfo> deviceIdMap = new HashMap<Integer, ReaderInfo>();
		
		
		for (ReaderInfo ri : readers)
		{
			StatusType temperatureStatus = StatusType.Green;
			String temperatureStatusMsg = null;
			
			StatusType enabledStatus = StatusType.Green;
			String enabledStatusMsg = null;
			
			StatusType bigIPStatus = StatusType.Green;
			String bigIPStatusMsg = null;
			
			StatusType lastHelloStatus = StatusType.Green;
			String lastHelloStatusMsg = null;

			StatusType lastVersionCheckStatus = StatusType.Green;
			String lastVersionCheckStatusMsg = null;
			
			StatusType deviceStatus = StatusType.Green;
			String deviceStatusMsg = null;
			
			StatusType deviceIdStatus = StatusType.Green;
			String deviceIdStatusMsg = null;
			
			StatusType readerTestStatus = StatusType.Green;
			String readerTestStatusMsg = null;
			
			StatusType vidReaderStatus = StatusType.Green;
			String vidReaderStatusMsg = null;		
			
			// Need to determine if other devices have valid temperatures.
			if (ri.getType() == ReaderType.lrr && ri.getTemperature() != null)
			{
				double temperature = ri.getTemperature().doubleValue();
				if (temperature <= readerTemperatureMinRed || readerTemperatureMaxRed <= temperature)
				{
					temperatureStatus = StatusType.Red;
					temperatureStatusMsg = "Temperature is " + temperature + "C.";
				}
				else if (temperature <= readerTemperatureMinYellow || readerTemperatureMaxYellow <= temperature)
				{
					temperatureStatus = StatusType.Yellow;
					temperatureStatusMsg = "Temperature is " + temperature + "C.";
				}
			}
			
			if (ri.isEnabled())
			{
				enabledStatus = StatusType.Yellow;
				enabledStatusMsg = "Reader is disabled by administrator. All reader events are ignored.";
			}
			
			if (ri.isInvalidHeader())
			{
				bigIPStatus = StatusType.Yellow;
				bigIPStatusMsg = "Reader is bypassing BigIP.";
			}

			if ((System.currentTimeMillis() - ri.getTimeLastHello()) > timeout)
			{
				if (ri.getType() == ReaderType.lrr && "xBR4".equals(ri.getHardwareType()) && Controller.getInstance().shouldReaderBeOn() > 0)
				{
					; /* Nothing specified for sleeping xBR4 */
				}
				else
				{
					lastHelloStatus = StatusType.Red;
					lastHelloStatusMsg = "Reader not communicating.";
				}
			}
									
			if (ri.getLastVersionCheckMessage() != null)
			{
				lastVersionCheckStatus = StatusType.Yellow;
				lastVersionCheckStatusMsg = ri.getLastVersionCheckMessage();
			}
			
			if (ri.getType() == ReaderType.lrr ||
				ri.getType() == ReaderType.xfp ||
				ri.getType() == ReaderType.xfpxbio ||
				ri.getType() == ReaderType.xtpra)
			{
				if (ri.getDeviceStatus() != null)
				{
					deviceStatus = ri.getDeviceStatus();
					deviceStatusMsg = ri.getDeviceStatusMessage();
				}
			}
			
			// For VID readers, set the status to RED if the entire VID system is down.
			if (ri.getType() == ReaderType.vid)
			{
				VidSystemStatus vidSystemStatus = Processor.INSTANCE.getVidSystemStatus();
				
				if (vidSystemStatus != null)
				{
					if ((System.currentTimeMillis() - vidSystemStatus.getLastHelloTime().getTime()) > timeout)
					{
						vidReaderStatus = StatusType.Red;
						vidReaderStatusMsg = "VID Software System is not responding";
					}
					else if (vidSystemStatus.getStatus() != StatusType.Green && 
							vidSystemStatus.getStatusMessage() != null && !vidSystemStatus.getStatusMessage().isEmpty())
					{
						vidReaderStatus = vidSystemStatus.getStatus();
						vidReaderStatusMsg = vidSystemStatus.getStatusMessage();
					}
				}
			}
			
			// Check for duplicate device id
			if (ri.isTapReader())
			{
				ReaderInfo other = deviceIdMap.get(ri.getDeviceId());
				if (other != null)
				{
					deviceIdStatus = StatusType.Yellow;
					deviceIdStatusMsg = "Readers " + ri.getName() + " and " + other.getName() + " have the same device id: " + ri.getDeviceId();
				}
				else
					deviceIdMap.put(ri.getDeviceId(), ri);
			}
			
			// check for failed reader test
			if (ri.getLastReaderTestTime() != null && !ri.isLastReaderTestSuccess())
			{
				readerTestStatus = StatusType.Yellow;
				readerTestStatusMsg = "Functional test failed " + DateUtils.formatAgo(ri.getLastReaderTestTime().getTime()) + 
						" for reader " + ri.getName();
			}
			
			ri.setStatus(StatusType.Green);
			ri.setStatusMessage("");
			
			
			if (!ri.isEnabled())
			{
				if (StatusType.Green.isParameterMoreSevere(bigIPStatus) ||
					StatusType.Green.isParameterMoreSevere(enabledStatus))
				{
					StatusType highestStatus = StatusType.Yellow;
					String statusMsg = null;
					
					if (highestStatus.isParameterMoreSevere(bigIPStatus) ||
						highestStatus.isParameterMoreSevere(enabledStatus))
					{
						highestStatus = StatusType.Red;
					}
					
					if (bigIPStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + bigIPStatusMsg;
						else
							statusMsg = bigIPStatusMsg;
					}
					
					if (enabledStatus != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + enabledStatusMsg;
						else
							statusMsg = enabledStatusMsg;
					}
					
				}
					
				if (ri.isInvalidHeader())
				{
					ri.setStatus(StatusType.Yellow);
					ri.setStatusMessage("Reader is bypassing BigIP.");
				}
				else
				{
					ri.setStatus(StatusType.Yellow);
					ri.setStatusMessage("Reader is disabled by administrator. All reader events are ignored.");
				}
			}
			else
			{
				if (ri.getType() == ReaderType.mobileGxp)
				{
					ri.setStatus(StatusType.Green);
					ri.setStatusMessage("");
				}
				else if (StatusType.Green.isParameterMoreSevere(bigIPStatus) ||
						 StatusType.Green.isParameterMoreSevere(temperatureStatus) ||
						 StatusType.Green.isParameterMoreSevere(lastHelloStatus) ||
						 StatusType.Green.isParameterMoreSevere(lastVersionCheckStatus) ||
						 StatusType.Green.isParameterMoreSevere(deviceStatus) || 
						 StatusType.Green.isParameterMoreSevere(deviceIdStatus) ||
						 StatusType.Green.isParameterMoreSevere(readerTestStatus) || 
						 StatusType.Green.isParameterMoreSevere(vidReaderStatus))
				{
					StatusType highestStatus = StatusType.Yellow; // Must be at least Yellow if not Green.
					String statusMsg = null;
					
					if (highestStatus.isParameterMoreSevere(bigIPStatus) ||
						highestStatus.isParameterMoreSevere(temperatureStatus) ||
						highestStatus.isParameterMoreSevere(lastHelloStatus) ||
						highestStatus.isParameterMoreSevere(lastVersionCheckStatus) ||
						highestStatus.isParameterMoreSevere(deviceStatus) ||
						highestStatus.isParameterMoreSevere(deviceIdStatus) ||
						highestStatus.isParameterMoreSevere(readerTestStatus) || 
						highestStatus.isParameterMoreSevere(vidReaderStatus))
						highestStatus = StatusType.Red;
	
					if (bigIPStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + bigIPStatusMsg;
						else
							statusMsg = bigIPStatusMsg;
					}
										
					if (temperatureStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + temperatureStatusMsg;
						else
							statusMsg = temperatureStatusMsg;
					}
					
					if (lastHelloStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + lastHelloStatusMsg;
						else
							statusMsg = lastHelloStatusMsg;
					}
					
					if (lastVersionCheckStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + lastVersionCheckStatusMsg;
						else
							statusMsg = lastVersionCheckStatusMsg;
					}
					
					if (deviceStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + deviceStatusMsg;
						else
							statusMsg = deviceStatusMsg;
					}
					
					if (deviceIdStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + deviceIdStatusMsg;
						else
							statusMsg = deviceIdStatusMsg;
					}
					
					if (readerTestStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + readerTestStatusMsg;
						else
							statusMsg = readerTestStatusMsg;
					}				
					
					if (vidReaderStatusMsg != null)
					{
						if (statusMsg != null)
							statusMsg += "\n" + vidReaderStatusMsg;
						else
							statusMsg = vidReaderStatusMsg;
					}		
					
					ri.setStatus(highestStatus);
					ri.setStatusMessage(statusMsg);
				}
			}
			// Allow the model to set its own status per reader if it wishes to.
			Processor.INSTANCE.getModel().formatReaderStatus(ri);
		}
	}
	
	private boolean checkDbConnection()
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			conn = Controller.getInstance().getPooledConnection();
			String sSQL = "select CURRENT_TIME";
			pstmt = conn.prepareStatement(sSQL);
			pstmt.execute();
			rs = pstmt.getResultSet();
			rs.next();			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception e)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	/***********************************************************************************************************
	 * Helper functions
	 ***********************************************************************************************************/

	private void loadModel() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException
	{
		// don't load unless we have to
		String sModelClass = ConfigOptions.INSTANCE.getControllerInfo().getModel();
		logger.debug("Loading model " + sModelClass);
		model = (IXBRCModel) Class.forName(sModelClass).newInstance();
		model.readConfiguration();
		model.initialize();
	}

	//
	// Initializes the more "static" elements of the status object
	//
	private void updateStatusObjectWithConfigValues()
	{
		ControllerInfo info = ConfigOptions.INSTANCE.getControllerInfo();

		// envelope data
		status.setName(info.getVenue());
		status.setTime(formatTime(new Date().getTime()));

		if (JMSAgent.INSTANCE.getBrokerUrl() != null)
			status.setJMSBroker(XBRCController.getInstance().getProperties().
				getProperty(JMSAgent.INSTANCE.getBrokerUrl()));
		
		status.setUpdateStreamURL(ConfigOptions.INSTANCE.getControllerInfo()
				.getUpdateStreamURL());

		String version = this.getClass().getPackage().getImplementationTitle();
		if (version == null || version.isEmpty())
			version = "Development";
		status.setVersion(version);

		status.setReaderLocationsCount(ConfigOptions.INSTANCE.getLocationInfo()
				.size());
		status.setModel(ConfigOptions.INSTANCE.getControllerInfo().getModel());
		status.setPerfMetricsPeriod(ConfigOptions.INSTANCE.getControllerInfo()
				.getPerfMetricsPeriod());

		// clear the more dynamic settings
		status.setMessageCount(-1);
		status.setLastMessageSeq(-1);
		status.setStatus(StatusType.Green);
		status.setStatusMessage("");
		status.setConfigurationChangedDate(new Date());
		
		// disable HA if the venue name is still the default
		if (ConfigOptions.INSTANCE.getControllerInfo().getVenue().equals(defaultVenueName))
			ConfigOptions.INSTANCE.getControllerInfo().setHaEnabled(false);
		
		// force ha role to SOLO if not enabled or if the venue hasn't been renamed from its default
		if (!ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
		{
			setHaStatus(HAStatusEnum.solo.toString());
		}
		
	}

	private boolean isReaderAlive(ReaderInfo ri)
	{
		// short circuit mobileGxp 
		if (ri.getType() == ReaderType.mobileGxp || ri.getType() == ReaderType.vid)
			return true;		
		
		// base "aliveness" on time of last hello message
		Date now = new Date();
		Long msSinceHello = now.getTime() - ri.getTimeLastHello();
		return msSinceHello < ConfigOptions.INSTANCE.getControllerInfo()
				.getReaderHelloTimeoutSec() * 1000;
	}
	
	private long getMessageCount()
	{
		Connection dbConn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "SELECT COUNT(id) FROM Messages WHERE id>?";
			pstmt = dbConn.prepareStatement(sSQL);
			long lLast = ConfigOptions.INSTANCE.getStoredStatus()
						.getLastMessageIdToJMS();
			pstmt.setLong(1, lLast);
			pstmt.execute();
			rs = pstmt.getResultSet();
			rs.next();
			long l = rs.getLong(1);

			return l;
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"!! Error getting message count", e));
			return 0;
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception e)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}

	}
	
	private long getHttpMessageCount()
	{
		Connection dbConn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "SELECT COUNT(id) FROM Messages WHERE id>?";
			pstmt = dbConn.prepareStatement(sSQL);
			long lLast = ConfigOptions.INSTANCE.getStoredStatus()
						.getLastMessageIdToPostStream();
			pstmt.setLong(1, lLast);
			pstmt.execute();
			rs = pstmt.getResultSet();
			rs.next();
			long l = rs.getLong(1);

			return l;
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"!! Error getting message count", e));
			return 0;
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception e)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}

	private long getMaxMessageSequence()
	{
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "SELECT MAX(id) FROM Messages";
			stmt = dbConn.createStatement();
			stmt.execute(sSQL);
			rs = stmt.getResultSet();
			rs.next();
			long l = rs.getLong(1);

			return l;
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error getting max message sequence", e));
			return 0;
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}

	}

	/*
	 * Looks up band RFID/LRID then guest information and logs descriptive
	 * information to the raw "EKG" file
	 */

	private void logToEKGFile()
	{
		// short circuit if EKG file is off
		if (ConfigOptions.INSTANCE.getControllerInfo().getEventDumpFile()
				.startsWith("#")
				|| ConfigOptions.INSTANCE.getControllerInfo()
						.getEventDumpFile().length() == 0)
		{
			return;
		}

		// dump the events
		int cItems = alEventsBeingProcessed.size();
		plogger.trace("Writing " + cItems + " events to the EKG file");

		Date dtStart = new Date();

		for (XbrEvent ev : alEventsBeingProcessed)
		{
			// skip events that should not be logged in order to avoid junk in eventdump
			if (ev.getID()==null || !model.isEventLogged(ev.getID()))
			{
				continue;
			}

			logEventToEKGFile(ev, null);
		}

		Date dtEnd = new Date();
		long msecInEKG = (dtEnd.getTime() - dtStart.getTime());
		double msecEKGPerEvent = (double) msecInEKG
				/ alEventsBeingProcessed.size();
		status.getPerfEKGWriteMsec().processValue(msecEKGPerEvent);

		plogger.trace("Done writing to EKG");
	}	

	private void logEventToEKGFile(
		XbrEvent ev,
		String   sAction)
	{

		// short circuit if EKG file is off
		if (ConfigOptions.INSTANCE.getControllerInfo().getEventDumpFile().startsWith("#")
				|| ConfigOptions.INSTANCE.getControllerInfo().getEventDumpFile().length() == 0)
		{
			return;
		}

		// skip events that should not be logged in order to avoid junk in eventdump
		if (ev.getID()==null)
		{
			Log.INSTANCE.logEKG("Skipping ev with no ID value.");
			return;
		}
		
		if (!model.isEventLogged(ev.getID()))
		{
			Log.INSTANCE.logEKG("Skipping ev, logging disabled for this model.");
			return;
		}		

		String sLog = "";

		// get the guest associated with the tap id
		String sID = ev.getID();		
		String sGuestName = null;
		String sNameModifier = "";
		
		if (ev.getGuest() != null)
			sGuestName = ev.getGuest().getFirstName() + " " + ev.getGuest().getLastName();
				
		// Get the reader information for this event
		ReaderInfo ri = ConfigOptions.INSTANCE.getReadersInfo().getReader(ev.getReaderName());

		// Determine if the reader event is coming from a rogue source
		if (ri != null)
		{
			LocationInfo li = ri.getLocation();

			if (li != null)
			{				
			    if (li.getName() == null)
			    {
			        sNameModifier = "NO NAME.";
			    }
			    else
			    {
					sNameModifier = li.getName() + ".";
			    }
			}
			else
			{
				sNameModifier = "NO LOCATION SET.";
			}
		}
		else
		{
			sNameModifier = "UNCONFIGURED READER.";
		}

		if (sGuestName == null)
		{
			if (ev.getType() == XbrEventType.RFID)
				sGuestName = "?RFID=" + sID;
			else if (ev.getType() == XbrEventType.LRR)
				sGuestName = "?LRID=" + sID;
			else
				sGuestName = "?ID=" + sID;
		}

		if (ev.getType() == XbrEventType.RFID)
		{
			if (sAction == null)
			{
			    sLog = ev.getTime().getTime() + ",TAP," + sNameModifier + ev.getReaderName()
					+ "," + sGuestName + "," + ev.getID();
			}
			else
			{
			    sLog = ev.getTime().getTime() + "," + sAction + "(TAP)," + sNameModifier + ev.getReaderName()
					+ "," + sGuestName + "," + ev.getID();				
			}
		}
		else if (ev.getType() == XbrEventType.LRR)
		{
			LrrEvent le = (LrrEvent) ev;
			if (sAction == null)
			{					
				sLog = le.getTime().getTime() + ",LRR," + sNameModifier + le.getReaderName()
						+ "," + sGuestName + "," + le.getID() + ","
						+ le.getPno() + "," + le.getSs() + "," + le.getFreq()
						+ "," + le.getChan();
			}
			else
			{
				sLog = le.getTime().getTime() + "," + sAction + "(LRR)," + sNameModifier + le.getReaderName()
						+ "," + sGuestName + "," + le.getID() + ","
						+ le.getPno() + "," + le.getSs() + "," + le.getFreq()
						+ "," + le.getChan();				
			}

			// if this band id is being "watched", add the event to the
			// watch list
			WatchedBandInfo wbi = null;
			synchronized (mapWatchedBands)
			{
				wbi = mapWatchedBands.get(ev.getID());
			}

			// Obtain next lock outside of the previous synchronized block
			// to prevent deadlocks..
			if (wbi != null)
			{
				synchronized (wbi)
				{
					mapWatchedBands.get(ev.getID())
						.AddEvent(le, ConfigOptions.INSTANCE.getControllerInfo().getMaxReadsBeforePurge());
				}
			}
		}
		else if (ev.getType() == XbrEventType.BioEnroll
				|| ev.getType() == XbrEventType.BioMatch)
		{
			BioEvent be = (BioEvent) ev;
			if (sAction == null)
			{
				sLog = be.getTime().getTime() + ",BIO," + be.getReaderName()
						+ "," + sGuestName + "," + be.getID() + ","
						+ be.getXbioTemplate();
			}
			else
			{
				sLog = be.getTime().getTime() + "," + sAction + "(BIO)," + be.getReaderName()
						+ "," + sGuestName + "," + be.getID() + ","
						+ be.getXbioTemplate();				
			}
		}
		else if (ev.getType() == XbrEventType.XtpGpio)
		{
			if (sAction == null)
			{
			    sLog = ev.getTime().getTime() + ",GPIO," + sNameModifier + ev.getReaderName();
			}
			else
			{
			    sLog = ev.getTime().getTime() + "," + sAction + "(GPIO)," + sNameModifier + ev.getReaderName();				
			}
		}

		Log.INSTANCE.logEKG(sLog);
	}

	private static String formatTime(long lTime)
	{
		Date dt = new Date(lTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}

	private String sendGet(String sURL, int msecTimeout)
	{

        InputStreamReader is = null;
        BufferedReader br = null;

		try
		{
			URL url = new URL(sURL);

			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setConnectTimeout(msecTimeout);
			httpCon.setReadTimeout(msecTimeout);
            httpCon.setRequestProperty("Authorization", XbConnection.getAuthorizationString());
			int n = httpCon.getResponseCode();
			if (n!=200)
				return null;

            is = new InputStreamReader(httpCon.getInputStream());
			br = new BufferedReader(is);
			StringBuilder sb = new StringBuilder();
			while(true)
			{
				String sLine = br.readLine();
				if (sLine==null)
				{
					return sb.toString();
				}
				sb.append(sLine);
			}
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format("Error getting information from master xBRC " + sURL, e));
			return null;
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	public AtomicInteger getMainThreadLoopCount()
	{
		return mainThreadLoopCount;
	}

	public AtomicInteger getLastProcessingDuration()
	{
		return lastProcessingDuration;
	}
	
	public void onVidSystemHello(RawAVMSEvent rawEvent)
	{
		if (vidSystemStatus == null)
			vidSystemStatus = new VidSystemStatus();
		
		vidSystemStatus.setLastHelloTime(new Date());
		vidSystemStatus.setStatusMessage(rawEvent.getStatusmessage());
		if (rawEvent.getStatus() != null && !rawEvent.getStatus().isEmpty())
			vidSystemStatus.setStatus(StatusType.valueOf(rawEvent.getStatus()));
		else
			vidSystemStatus.setStatus(StatusType.Green);		
	}
	
	public void onVidReaderHello(RawAVMSEvent rawEvent, ReaderInfo ri)
	{
		// Don't create a VID system status object on the basis of a vehicle event only.
		if (vidSystemStatus == null)
			return;
		
		vidSystemStatus.setLastHelloTime(new Date());		
	}

    public static void initializeAudit() {
        final AuditConfig currentConfig = Auditor.getInstance().getConfig();
        final AuditConfig newConfig = ConfigOptions.INSTANCE.getAuditConfig();

        if(currentConfig.getImplStack().indexOf("AuditDbImpl") < 0) {
            Processor.initializeAudit(Processor.prop, newConfig);
        }
        else {
            // This attribute only makes sense for systems, which manage others, like xBRMS.
            currentConfig.setEnabledDips("");
            newConfig.setEnabledDips("");

            // In case the application ID has changed
            Connection conn = null;
            try
            {
            	conn = Controller.getInstance().getPooledConnection();
            	
            	// ConfigOptions hasn't been initialized yet, so we need to grab that one value
	            com.disney.xband.common.lib.Config.getInstance().read(
	            		conn, 
	            		ConfigOptions.INSTANCE.getControllerInfo(), 
	            		"sVenueName");
            }
            catch (Exception e){}
            finally
            {
            	if (conn != null)
            	{
            		try
            		{
            			Controller.getInstance().releasePooledConnection(conn);
            		}
            		catch (Exception ignore){}
            	}
            }
            
            newConfig.setAppId(ConfigOptions.INSTANCE.getControllerInfo().getVenue());
            if(!newConfig.isEqual(currentConfig)) {
                Processor.initializeAudit(Processor.prop, newConfig);
            }
        }
    }

    private static void initializeAudit(final Properties props, final AuditConfig config) {
        config.setImplStack("com.disney.xband.common.lib.audit.providers.AuditDbImpl");

        config.setConnectionPool(new IAuditConnectionPool() {
            @Override
            public Connection getConnection() throws Exception {
                return Controller.getInstance().getPooledConnection();
            }

            @Override
            public void releaseConnection(Connection conn) {
                Controller.getInstance().releasePooledConnection(conn);
            }
        });
        
        Connection conn = null;
        try
        {
        	conn = Controller.getInstance().getPooledConnection();
        	
        	// ConfigOptions hasn't been initialized yet, so we need to grab that one value
            com.disney.xband.common.lib.Config.getInstance().read(
            		conn, 
            		ConfigOptions.INSTANCE.getControllerInfo(), 
            		"vipAddress");
        }
        catch (Exception e){}
        finally
        {
        	if (conn != null)
        	{
        		try 
        		{
        			Controller.getInstance().releasePooledConnection(conn);
        		}
        		catch (Exception ignore){}
        	}
        }

        config.setAppClass(AuditEvent.AppClass.XBRC.toString());
        config.setHost(getOwnIp());
        config.setvHost(ConfigOptions.INSTANCE.getControllerInfo().getVipAddress());
        config.setBatchSizeMax(Integer.parseInt((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_BATCH_SIZE_MAX)));
        config.setInterceptors(AuditConfig.createAuditInterceptorConfigsFromProps(props, true));
        config.setDescTemplate(AuditConfig.createDescriptionTemplatesFromProps(props));

        final AuditFactory auditFactory = new AuditFactory(config);
        Auditor.getInstance().setAuditor(auditFactory.getAudit());
        Auditor.getInstance().setEventsProvider(auditFactory.getAuditControl().getEventsProvider("AuditDbImpl"));
        Auditor.getInstance().setConfig(config);
        Auditor.getInstance().setAuditFactory(auditFactory);
    }
    
    private static String getOwnIp()
	{
		String sOwnIp = "";
		try
		{
			Collection<String> iplist = NetInterface
					.getOwnIpAddress(ConfigOptions.INSTANCE.getControllerInfo()
							.getDiscoveryNetPrefix());
			
			// if got nothing, try without prefix
			if (iplist.size()==0)
				iplist = NetInterface.getOwnIpAddress(null);
			
			if (iplist.size() > 0)
				sOwnIp = iplist.iterator().next();
		}
		catch (SocketException e)
		{
			logger.error("Failed to get our own IP address",e);
		}
		return sOwnIp;
	}

	public VidSystemStatus getVidSystemStatus() {
		return vidSystemStatus;
	}
}
