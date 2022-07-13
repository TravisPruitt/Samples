package com.disney.xband.xbrc.attractionmodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.xml.bind.JAXBException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.lib.performance.PerfMetric;
import com.disney.xband.common.lib.performance.PerfMetricType;
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.jms.lib.entity.xbrc.AbandonMessage;
import com.disney.xband.jms.lib.entity.xbrc.AbandonMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.EventMessage;
import com.disney.xband.jms.lib.entity.xbrc.EventMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.ExitMessage;
import com.disney.xband.jms.lib.entity.xbrc.ExitMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.LoadMessage;
import com.disney.xband.jms.lib.entity.xbrc.LoadMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.lib.xbrapi.ReaderExecutor;
import com.disney.xband.lib.xbrapi.Sequence;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.lib.xbrapi.XbrJsonMapper;
import com.disney.xband.xbrc.attractionmodel.GxpCallback.Operation;
import com.disney.xband.xbrc.attractionmodel.GxpCallback.UnitType;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.db.EventsLocationConfigService;
import com.disney.xband.xbrc.lib.db.VaLocationConfigService;
import com.disney.xband.xbrc.lib.entity.AttractionModelConfig;
import com.disney.xband.xbrc.lib.entity.EventsLocationConfig;
import com.disney.xband.xbrc.lib.entity.HAMessage;
import com.disney.xband.xbrc.lib.entity.LocationType;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.VaLocationConfig;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.idms.IDMSResolver;
import com.disney.xband.xbrc.lib.model.AVMSEventAggregate;
import com.disney.xband.xbrc.lib.model.AbandonMessageArgs;
import com.disney.xband.xbrc.lib.model.BandMediaType;
import com.disney.xband.xbrc.lib.model.ConfidenceMessageArgs;
import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.IXBRCModel;
import com.disney.xband.xbrc.lib.model.LRREventAggregate;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.MessageArgs;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.TapEventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.model.XtpGpioEventAggregate;
import com.disney.xband.xbrc.lib.utils.FileUtils;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.GuestIdentifier;
import com.disney.xband.xview.lib.model.Xband;

public class CEP implements IXBRCModel
{
	// logger
	private static Logger logger = Logger.getLogger(CEP.class);
	private static Logger plogger = Logger.getLogger("performance." + CEP.class.toString());
	
	// the guest status table is indexed by Guest ID
	private Hashtable<String,GuestStatus<GuestStatusState>> GST = new Hashtable<String,GuestStatus<GuestStatusState>>();
	
	// gxp for entitlement checking
	private final int cThreadsGxp = 4;
	private GxpQueue gxp = new GxpQueue(cThreadsGxp);
	
	// for maintaining statistics
	private Date dtMetricsStart;
	private Metrics metStandby = new Metrics();
	private Metrics metXPass = new Metrics();
	
	// for saving the GST periodically
	private boolean bGSTDirty = false;
	
	// Vehicle association location
	private HashMap<String, VaLocation> vehicleAssociationLocations = new HashMap<String, VaLocation>();
    private HashMap<String, EventsLocationConfig> eventsLocationConfigurations = new HashMap<String, EventsLocationConfig>();
	
	// Keep the state of the readers to protect from being used when showing a blue light.
	HashMap<String,ReaderState> readerState = new HashMap<String,ReaderState>();	
	
	public CEP()
	{
		dtMetricsStart = new Date();		
	}
	
	@Override
	public void initialize() 
	{
		// initialize any model specific performance metrics
		PerfMetric perfModel1 = XBRCController.getInstance().getStatus().getPerfModel1();
		perfModel1.setDisplayName("GxpCallback");
		perfModel1.setDescription("Round trip to GxP");
		perfModel1.setType(PerfMetricType.milliseconds);
		
		PerfMetric perfModel2 = XBRCController.getInstance().getStatus().getPerfModel2();
		perfModel2.setDisplayName("MissedVIDEvents");
		perfModel2.setDescription("Missed VID Events");
		perfModel2.setType(PerfMetricType.numeric);
		
		PerfMetric perfModel3 = XBRCController.getInstance().getStatus().getPerfModel3();
		perfModel3.setDisplayName("MissedGPIOEvents");
		perfModel3.setDescription("Missed GPIO Events");
		perfModel3.setType(PerfMetricType.numeric);
		
		logger.info("VIP URL is: " + XBRCController.getInstance().getXbrcVipURL());
	}
	
	public void clear()
	{
		synchronized(GST)
		{
			GST.clear();
			bGSTDirty = true;
		}
	}
	
	public int SaveGST(Connection conn)
	{
		Statement stmt = null;
		PreparedStatement pstmt = null;		
		
		boolean bOldAutoCommit=true;
		plogger.trace("Saving GST");
		Date dtStart = new Date();
		try
		{
			bOldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			String sSQL = "DELETE FROM GST";
			stmt = conn.createStatement();
			stmt.execute(sSQL);
			
			if (GST.size()==0)
			{
				conn.commit();
				conn.setAutoCommit(bOldAutoCommit);
				plogger.trace("No GST state to save");
				return 0;
			}
			
			sSQL = "INSERT INTO GST(GuestId, HasXPass, State, LastReader, TimeEarliestAtReader, TimeLatestAtReader, TimeEntered, " + 
									"TimeMerged, TimeLoaded, TimeExited, CarID, HasDeferredEntry, BandMediaType, BandType, CarIDTime, " + 
									"PidDecimal, LinkID, LinkIDType, BandTransmitting) " +
			                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sSQL);
			
			synchronized(GST)
			{
				for(String sID: GST.keySet())
				{
					GuestStatus<GuestStatusState> gs = GST.get(sID);
					pstmt.clearParameters();
					pstmt.setString(1, sID);					
					pstmt.setBoolean(2, gs.getHasXpass());
					pstmt.setString(3, gs.getState().toString());
					pstmt.setString(4, gs.getLastReader());
					pstmt.setLong(5, gs.getTimeEarliestAtReader().getTime());
					pstmt.setLong(6, gs.getTimeLatestAtReader().getTime());
					pstmt.setLong(7, gs.getTimeEntered().getTime());
					pstmt.setLong(8, gs.getTimeMerged().getTime());
					pstmt.setLong(9, gs.getTimeLoaded().getTime());
					pstmt.setLong(10, gs.getTimeExited().getTime());
					pstmt.setString(11, gs.getCarId());
					pstmt.setBoolean(12, gs.getDeferredEntryEvent()!=null);
					pstmt.setString(13, gs.getBandMediaType().name());
					pstmt.setString(14, gs.getBandType());
					pstmt.setLong(15, gs.getCarIdTime() == null ? Long.MIN_VALUE : gs.getCarIdTime().getTime());
					pstmt.setString(16, gs.getPidDecimal());
					pstmt.setString(17, gs.getLinkID());
					pstmt.setString(18, gs.getLinkIDType());
					pstmt.setBoolean(19, gs.isBandTransmitting());
					
					pstmt.execute();
				}
			}
			
			conn.commit();
			conn.setAutoCommit(bOldAutoCommit);
			
			Date dtEnd = new Date();
			long msec = dtEnd.getTime() - dtStart.getTime();
			XBRCController.getInstance().getStatus().getPerfSaveGSTMsec().processValue((double) msec);
			
			plogger.trace("GST saved");
			return 0;
		} 
		catch (SQLException e)
		{
			logger.error("!! Error writing GST to database: " + e);
			
			try
			{
				conn.setAutoCommit(bOldAutoCommit);
			}
			catch(SQLException e2)
			{
			}
			return 1;
		}
		finally
		{
			logger.trace("Saved GST");
			
			try
			{
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
			} 
			catch(Exception e) {}
		}
	}
	
	@Override
	public void beforeProcessEvents() throws Exception 
	{	
		// prune any old entries in the GST
		synchronized(GST)
		{
			for( Iterator<String> it = GST.keySet().iterator(); it.hasNext(); )
			{
				String sID = it.next();
				
				GuestStatus<GuestStatusState> gs = GST.get(sID); 
				if (gs.getState()==GuestStatusState.DELETABLE)
				{
					// It is possible that the guest showed up at exit before his vehicle association process completed.
					if (gs.getEventsAtLocation() != null && !gs.getEventsAtLocation().isEmpty())
					{
						
						LocationInfo locationInfo = gs.getEventsAtLocation().get(0).getReaderInfo().getLocation();
						VaLocation vaLocation = vehicleAssociationLocations.get(locationInfo.getName());
						
						if (vaLocation == null)
						{
							// This should not ever happen, but better to find this in the logs than null pointer exception.
							logger.error("Could not find vehicle association configuration for location " + locationInfo.getName());
							continue;
						}
						
						logger.warn("Guest " + gs.getGuestID() + " is in DELETABLE state before the vehicle association process completed. Forcing vehicle association.");
						calculateVehicleAssociationScore(gs, vaLocation);
					}
					
					it.remove();
					bGSTDirty = true;
				}
			}
		}
		
		// handle any gxp callback messages
		while (true)
		{
			GxpCallback m = GxpCallbackQueue.INSTANCE.getNextMessage();
			if (m==null)
				break;
			
			handleGxpCallback(m);
		}
		
	}

	public void processEvents(List<XbrEvent> liEvents) throws Exception
	{
		EventAggregator ea = new EventAggregator(XBRCController.getInstance(), this);
	
		List<EventAggregate> li = null;
		try
		{
			logger.debug("Calling first pass singulation");
			Date dtStart = new Date();
			li = ea.Analyze(liEvents);
			Date dtEnd = new Date();
			logger.debug("Returned from first pass singulation");
			
			long msec = dtEnd.getTime() - dtStart.getTime();
			double msecPerEvent = (double)msec / liEvents.size();
			XBRCController.getInstance().getStatus().getPerfSingulationMsec().processValue(msecPerEvent);
		}
		catch(Exception ex)
		{
			logger.error("Exception during 1st stage of singulation", ex);
		}
		plogger.trace("Ending 1st stage of singulation");
		
		// now process the events
		for(EventAggregate ev: li)
		{
			// skip event types that we don't understand
			if ( !(ev instanceof TapEventAggregate) && 
				 !(ev instanceof LRREventAggregate) && 
				 !(ev instanceof AVMSEventAggregate) &&
				 !(ev instanceof XtpGpioEventAggregate))
				continue;
				
			// get RF or LR id
			String sID = ev.getID();
			if (sID==null)
			{
				logger.error("Null band id in model");
				continue;
			}
			
			// handle car events
			if (ConfigOptions.INSTANCE.getCarInfo().IsCar(sID))
			{
				AnalyzeCarEvent(ev);
				continue;
			}
			
			// handle vehicle assocation events
			if (ev instanceof AVMSEventAggregate)
			{
				AnalyzeAVMSEvent((AVMSEventAggregate)ev);
				continue;
			}
			
			// handle gpio events (typically, beam break)
			if (ev instanceof XtpGpioEventAggregate)
			{
				AnalyzeXtpGpioEvent((XtpGpioEventAggregate)ev);
				continue;
			}
			
			// verify we have a secure id, if it's required
			String sSID = null;
			if (ev.getReaderInfo().isUseSecureId() && (ev instanceof TapEventAggregate))
			{
				sSID = ((TapEventAggregate)ev).getTapEvent().getSid();
				if (sSID == null || sSID.isEmpty())
				{
					logger.warn("Band with UID " + ev.getID() + " did not produce a secure id. Ignoring tap.");
					continue;
				}
			}
			
			// get the public id (pid)
			String sPidDecimal = ev.getPidDecimal();
			if (sPidDecimal == null)
			{
				logger.debug("Band with UID " + sPidDecimal + " does not map to a tap or llr. Ignoring.");
				continue;
			}			
				
			
			// do not process events from readers in blue light state
			if (ev instanceof TapEventAggregate && !isReaderAvailable(ev.getReaderInfo().getName()))
			{
				logger.warn("Reader " + ev.getReaderInfo().getName() + " is in blue light state or waiting for GXP response. Ignoring tap");
				continue;
			}
			
			// map to a guest
			Guest guest = null;
			String sGuestID = null;
			String sBandID = null;
			boolean bIsTap = ReaderType.isTapReader(ev.getReaderInfo().getType());			
		
			Xband xb = null;
			
			if (IDMSResolver.INSTANCE.isEnabled()) 
			{	
				if (bIsTap)
				{
					xb = ((TapEventAggregate)ev).getTapEvent().getXband();		
				}
				else
				{
					xb = ((LRREventAggregate)ev).getXband();
				}
				
				if (xb!=null && !IDMSResolver.INSTANCE.isPlaceholder(xb))
				{
					sBandID = xb.getBandId();
					
					// if the band is not active, ignore the message.
					if (!xb.getActive())
					{					
						if (ev instanceof LRREventAggregate)
						{
							logger.info("Ignoring disabled band: " + sBandID);
							continue;
						}
						else
						{
							logger.info("Band is disabled: " + sBandID);
						}
					}
					
					if (ev instanceof TapEventAggregate)
						guest = ((TapEventAggregate)ev).getTapEvent().getGuest();
					else if (ev instanceof LRREventAggregate)
						guest = ((LRREventAggregate)ev).getGuest();
					
					if (guest == null)
						guest = IDMSResolver.INSTANCE.getGuestFromBandId(sBandID);
					
					if (guest!=null)
					{
						sGuestID = guest.getGuestId();											
					}
					else
					{						
						if (ev instanceof LRREventAggregate)
						{
							logger.info("Ignoring unassigned band: " + sBandID);
							continue;
						}
						else
						{
							logger.info("Band is unassigned: " + sBandID);
						}
					}
				}				
			}
			
			if (sGuestID == null)
			{
				sGuestID = "?PublicID=" + sPidDecimal;
			}
			
			if (processNonGuestBands(ev, xb, guest))
				continue;
			
			// do we have the guest in the table
			GuestStatus<GuestStatusState> gs = null;			
			gs = GST.get(sGuestID);
			if (gs==null)
			{
				// nope, got to add the guest
				gs = new GuestStatus<GuestStatusState>(GuestStatusState.INDETERMINATE);
				gs.setGuestID(sGuestID);				
				
				// Store the global guest identifier for the guest into the guest status entry
				GuestIdentifier gi = null;
				if (guest!=null)
					gi = Guest.getPreferredGuestIdentifier(guest, XBRCController.getInstance().getGuestIdentifierTypes());										
				
				if (gi != null)
				{
					logger.trace("IDMS returned " + gi.getType() + " " + gi.getValue() + " for guest " + gs);
					gs.setLinkID(gi.getValue());
 					gs.setLinkIDType(gi.getType());
				}
				else
				{
					gs.setLinkID("");				
					gs.setLinkIDType("xbms-link-id");
				}
				
				gs.setGuest(guest);
				
				// need to set some times here so that we can time out INDETERMINATE guests
				Date now = new Date();
				gs.setTimeEarliestAtReader(now);
				gs.setTimeLatestAtReader(now);

                gs.setBandType(getGuestPrimaryBandType(guest, sBandID));

                // Taps at WAYPOINT locations do not create a GST entry.
                if (!bIsTap || !isWaypointLocationType(ev.getReaderInfo().getLocation()))
                {
	                logger.debug("Adding guest to the GST: " + gs.getGuestID());
					synchronized(GST)
					{
						GST.put(sGuestID, gs);
						bGSTDirty = true;
					}
                }
            }			
			
			// TODO: consider lower-level synchronization
			synchronized(GST)
			{
				if (ev instanceof LRREventAggregate)
				{
					gs.setBandTransmitting(true);
					gs.setLRID(ev.getID());
				}
				
				if (sPidDecimal!=null)
				{
					gs.setPidDecimal(sPidDecimal);
					// The long range ID is the same as the hex representation of the public id.
					if (gs.getLRID() == null)
						gs.setLRID(XbrJsonMapper.publicIdToLongRangeId(Long.parseLong(sPidDecimal)));
				}
				
				if (sSID!=null)
					gs.setSecureId(sSID);
				
				// Figure out if a card or a band was tapped.
				if (gs.getBandMediaType() == BandMediaType.Unknown)
				{
					if (xb != null)
					{
						if (xb.getLRId() == null || xb.getLRId().isEmpty())
							gs.setBandMediaType(BandMediaType.Card);
						else
							gs.setBandMediaType(BandMediaType.Band);
					}
					else
					{
						// no band info, infer the band type from the event
						if (bIsTap)
							gs.setBandMediaType(BandMediaType.Card);
						else
							gs.setBandMediaType(BandMediaType.Band);
					}
				}
				
				AnalyzeEvent(ev, gs);
			}
		}
		
	}
	
	/**
	 * 
	 * @param ev
	 * @param xb
	 * @param guest
	 * @return true if the TAP was processed (non guest) or false if guest tap
	 */
	private boolean processNonGuestBands(EventAggregate ev, Xband xb, Guest guest)
	{
		if (xb == null)
			return false;
		if (xb.getBandType() == null)
			return false;
		
		if (xb.getBandType().equalsIgnoreCase(XBRCController.getInstance().getReaderTestBandType()))
		{
			try 
			{
				logger.info("A reader test tap was received for reader " + ev.getReaderInfo().getName() + " Band PID: " + ev.getPidDecimal());
				XBRCController.getInstance().processReaderTestTap(ev.getReaderInfo(), xb, guest);
			} 
			catch (Exception e) 
			{
				logger.error("Failed to process a reader test TAP on a reader " + ev.getReaderInfo().getName(), e);
			}
			return true;
		}
		
		return false;
	}

    //  Attempts to find the band type of a given band for a guest.
    private String getGuestPrimaryBandType(Guest guest, String sBandID)
    {
        String bandType = "Guest";

        // Determine what type of band this is. In the case where
        // there is multiple bandifs, find the band that we're currently
        // dealing with and get its role. If there's no role, assume
        // it's a guest band.
        if ( guest != null && guest.getXbands() != null )
        {
            for ( Xband band : guest.getXbands())
            {
                if ( band.getBandId().equals(sBandID))
                {
                    if ( band.getBandType() != null )
                        bandType = band.getBandType();
                }
            }
        }

        return bandType;
    }

    /*
	 * UE feedback on error conditions (not GXP negative responses)
	 * TODO: review configurable parameters
	 */
	public void showErrorFeedback(String sReaderName)
	{
		Date dtStart = new Date();
		
        ReaderInfo ri = XBRCController.getInstance().getReader(sReaderName);
        ReaderExecutor.getInstance().setReaderSequence(ri, Sequence.error);

		Date dtEnd = new Date();
		long msec = dtEnd.getTime() - dtStart.getTime();
		XBRCController.getInstance().getStatus().getPerfWriteToReaderMsec().processValue((double)msec);
	}

	
	/*
	 * UE feedback on Gxp negative response (not for error conditions!)
	 * TODO: review configurable parameters
	 */
	public void showFailureFeedback(String sReaderName, boolean lockReader)
	{
		Date dtStart = new Date();

        ReaderInfo ri = XBRCController.getInstance().getReader(sReaderName);
        ReaderExecutor.getInstance().setReaderSequence(ri, Sequence.failure);
        
        // Either lock or reset the reader
        if (lockReader)
        	setReaderState(ri.getName(), false, ConfigOptions.INSTANCE.getModelInfo().getCmsecReaderLockExpiry(), false);
        else 
        	setReaderState(ri.getName(),true,0, false);
        
		Date dtEnd = new Date();
		long msec = dtEnd.getTime() - dtStart.getTime();
		XBRCController.getInstance().getStatus().getPerfWriteToReaderMsec().processValue((double)msec);
	}

	/*
	 * UE feedback on Gxp positive response 
	 */
	public void showSuccessFeedback(String sReaderName)
	{
		Date dtStart = new Date();

		ReaderInfo ri = XBRCController.getInstance().getReader(sReaderName);
        ReaderExecutor.getInstance().setReaderSequence(ri, Sequence.success);
        setReaderState(ri.getName(), true, 0, false);
		Date dtEnd = new Date();
		long msec = dtEnd.getTime() - dtStart.getTime();
		XBRCController.getInstance().getStatus().getPerfWriteToReaderMsec().processValue((double)msec);
	}
	
	public void clearReaderLight(String sReaderName)
	{
		setReaderState(sReaderName,true,0, false);
		Date dtStart = new Date();
		ReaderInfo ri = XBRCController.getInstance().getReader(sReaderName);
		ReaderExecutor.getInstance().setReaderSequence(ri, Sequence.off);
		Date dtEnd = new Date();
		long msec = dtEnd.getTime() - dtStart.getTime();
		XBRCController.getInstance().getStatus().getPerfWriteToReaderMsec().processValue((double)msec);
	}

	private void adjustVehicleEventPlacement(GuestStatus<GuestStatusState> gs)
	{
		for (VehicleIndex vi :  gs.getVehicles())
		{
			Vehicle v = vi.getVehicle();
			long vehicleTime = v.getAdjustedEventTime().getTime();
			
			// Now make sure that we choose the correct slot for this vehicle event amongst the LRR
			// reads. The correct slot is the slot closest in time to the two surrounding LRR reads.
			
			boolean tryAgainAfterSorting = false;
			boolean alreadySorted = false;
			
			do
			{
				tryAgainAfterSorting = false;
				
				int idx = vi.getEventIndex();
				// This is just a precaution. Never want to get index out of bounds exception.
				if (idx >= gs.getEventsAtLocation().size())
				{
					logger.warn("Fixing incorrect event index " + idx + " for vehicle id " + v.getCompositeID() + " and guest " + gs.getGuestID());
					idx = gs.getEventsAtLocation().size() -1;
					vi.setEventIndex(idx);
				}
				
				boolean adjustedDownwards = false;
				int equals = 0;
				long prevEventTime = gs.getEventsAtLocation().get(idx).getTimestamp().getTime();
						
				// First adjust down if necessary
				while(!tryAgainAfterSorting && idx < gs.getEventsAtLocation().size() && 
					  gs.getEventsAtLocation().get(idx).getTimestamp().getTime() <= vehicleTime)
				{
					adjustedDownwards = true;
					
					// the code that does score calculation later on needs to have the idx set to the 
					// higest time just before if goes over
					if (idx + 1 < gs.getEventsAtLocation().size() && 
						gs.getEventsAtLocation().get(idx + 1).getTimestamp().getTime() > vehicleTime)
						break;
				
					idx++;
					logger.warn("Adjusted vehicle event index " + idx + " for vehicle id " + v.getCompositeID() + " and guest " + gs.getGuestID() + " downwards");
					
					if (idx < gs.getEventsAtLocation().size())
					{
						if (gs.getEventsAtLocation().get(idx).getTimestamp().getTime() == prevEventTime)
							equals++;
						else
							equals = 0;
						
						// see if the band events need to be resorted
						if (gs.getEventsAtLocation().get(idx).getTimestamp().getTime() < prevEventTime)
							tryAgainAfterSorting = true; 
						
						prevEventTime = gs.getEventsAtLocation().get(idx).getTimestamp().getTime();
					}
				}
				
				// If there were multiple events with the same time then move the index half way 
				// up to place it in the middle of these events rather than on the edge.
				if (equals > 0)
					idx -= equals/2;
				
				// Don't go past the array.
				if (idx >= gs.getEventsAtLocation().size())
					idx = gs.getEventsAtLocation().size() -1;
				
				if (!tryAgainAfterSorting && !adjustedDownwards)
				{
					prevEventTime = gs.getEventsAtLocation().get(idx).getTimestamp().getTime();
					
					// Adjust up if necessary
					while(!tryAgainAfterSorting && idx >= 0 && gs.getEventsAtLocation().get(idx).getTimestamp().getTime() >= vehicleTime)
					{	
						idx--;
						logger.warn("Adjusted vehicle event index " + idx + " for vehicle id " + v.getCompositeID() + " and guest " + gs.getGuestID() + " upwards");
						
						if (idx >= 0)
						{
							if (gs.getEventsAtLocation().get(idx).getTimestamp().getTime() == prevEventTime)
								equals++;
							else
								equals = 0;
							
							// see if the band events need to be resorted
							if (gs.getEventsAtLocation().get(idx).getTimestamp().getTime() > prevEventTime)
								tryAgainAfterSorting = true;
							
							prevEventTime = gs.getEventsAtLocation().get(idx).getTimestamp().getTime();
						}
					}
					
					if (equals > 0)
						idx += equals/2;
				}
					
				if (tryAgainAfterSorting)
				{
					if (alreadySorted)
						logger.warn("Detected lrr events not sorted propertly according to time. Aborting vehicle event adjustment.");
					else
					{
						logger.info("Detected lrr events not sorted propertly according to time. Sorting events.");
						Collections.sort(gs.getEventsAtLocation(), new LrrEventComparator());
						alreadySorted = true;
					}
				}
				
				if (idx < gs.getEventsAtLocation().size())
					vi.setEventIndex(idx);
			}
			while(tryAgainAfterSorting);
		}
	}
	
	private void abandonGuest(GuestStatus<GuestStatusState> gs, boolean bMasterOrSolo, Date dtNow)
	{
		// send the abandon message. Note that messages are sent for HASENTERED abandonments only
		// if entry messages are NOT being deferred (since we've already sent an ENTRY message, the ABANDON
		// message negates it).
		if( (gs.getState() == GuestStatusState.HASMERGED) ||
			(gs.getState() == GuestStatusState.LOADING) ||
			(gs.getState() == GuestStatusState.RIDING) ||
			(!ConfigOptions.INSTANCE.getModelInfo().getDeferEntryMessages() && gs.getState()==GuestStatusState.HASENTERED) )
		{
			if (bMasterOrSolo)
			{
				// generate a dummy event aggregate 
				EventAggregate ea = new EventAggregate(gs.getGuestID(), gs.getPidDecimal(), gs.getLastReader(), gs.getTimeLatestAtReader());
				
				SendJMSMessage("ABANDON",
                        new AbandonMessageArgs(ea,
                                gs.getHasXpass(),
                                dtNow,
                                gs.getGuestID(),
                                gs.getPidDecimal(),
                                gs.getLinkID(),
                                gs.getLinkIDType(),
                                gs.getBandType()), true, true);                            
			}			
			
			if (gs.getHasXpass())
				this.metXPass.AddAbandonment(gs.getSecondsWaitingToMerge(), gs.getSecondsWaitingToLoad());
			else
				this.metStandby.AddAbandonment(0, gs.getSecondsWaitingToLoad());
		}
		
		if (bMasterOrSolo && gs.getLastConfidenceState() != null)
		{
			sendLocationAbandon(gs);
            gs.setLastConfidenceState(null);
            gs.setLastSentConfidenceValue(null);
		}
		
		// mark the entry for deletion
		GuestStatusState stateStart = gs.getState();
		gs.setState(GuestStatusState.DELETABLE);
		bGSTDirty = true;
		if (XBRCController.getInstance().isVerbose())
			logger.debug("Guest: " + gs.getGuestID() + " changed from state " + stateStart + " to " + gs.getState() + " due to timeout");
	}
	
	private void sendLocationAbandon(GuestStatus<GuestStatusState> gs)
	{
		 ReaderInfo ri = gs.getLastConfidenceState().getReaderInfo();
    	 if (ri == null)
    		 return;    	 
    	 
    	 EventsLocationConfig eventConfig = eventsLocationConfigurations.get(ri.getLocation().getName());
         if ( eventConfig == null || !eventConfig.isEnableConfProcessing())
             return;
         
		 // generate a dummy event aggregate
        EventAggregate ea = new EventAggregate( gs.getGuestID(), gs.getLastConfidenceState().getPidDecimal(),
        		ri.getName(), new Date() );

        SendJMSMessage("LOCATIONABANDON",
              new AbandonMessageArgs(ea,
                      gs.getHasXpass(),
                      gs.getLastConfidenceState().getLastPingTime(),
                      gs.getGuestID(),
                      gs.getLastConfidenceState().getPidDecimal(),
                      gs.getLinkID(),
                      gs.getLinkIDType(),
                      gs.getBandType()), eventConfig.isSendConfToJMS(), true);

        XBRCController.getInstance().logEKG( new Date().getTime() +
             ",CONFABANDON," +
             gs + "," +
             gs.getBandType() + "," +
             ri.getLocation().getName()
        );        
	}
	
	private void doConfidenceTimeProcessing(GuestStatus<GuestStatusState> gs)
	{ 
         // Process events based on time.
         ConfidenceEventAggregate cev = ConfidenceEventAggregate.determineState( gs.getLastConfidenceState(), gs, eventsLocationConfigurations );
         
         if (cev == null)
         {
        	 if (gs.getLastConfidenceState() != null)
        	 {
                 sendLocationAbandon(gs);
                 gs.setLastSentConfidenceValue(null);
        	 }
         }
         else if (cev != gs.getLastConfidenceState())
         {
             HandleConfidence( cev, gs );
         }
         
         gs.setLastConfidenceState(cev);
	}
	
	private void processVehicleAssociation(GuestStatus<GuestStatusState> gs, Date dtNow, Long msSinceLastAtReader)
	{
		// Don't re-associate. Nothing to do if no events were collected.
		if (gs.getEventsAtLocation().isEmpty())
			return;
		
		LocationInfo locationInfo = gs.getEventsAtLocation().get(0).getReaderInfo().getLocation();
		VaLocation vaLocation = vehicleAssociationLocations.get(locationInfo.getName());
		
		if (vaLocation == null)
		{
			// This should not ever happen, but better to find this in the logs than null pointer exception.
			logger.error("Could not find vehicle association configuration for location " + locationInfo.getName());
			return;
		}
		
		boolean expireTrain = vaLocation.isExpireTrain(dtNow);
				
		if ((vaLocation.gotVehicleId() || msSinceLastAtReader > (vaLocation.getConfig().getTrainTimeoutSec()*1000)) &&
				 (msSinceLastAtReader > (vaLocation.getConfig().getOnrideTimeoutSec() * 1000) || expireTrain))
		{
			// Process vehicle association location events if the guest is no longer
			// at the vehicle association location.
			calculateVehicleAssociationScore(gs, vaLocation);
		}
	}
	
	@Override
	public void afterProcessEvents() throws Exception 
	{
		// if we're not an HA Master, perform a subset of the work. Test and set a flag
		HAStatusEnum haThis = HAStatusEnum.getStatus(XBRCController.getInstance().getHaStatus());

		boolean bMasterOrSolo = (haThis == HAStatusEnum.master || haThis == HAStatusEnum.solo); 
				
		// per guest processing
		synchronized(GST)
		{
			Date dtNow = new Date();
						
			// walk through all entries in the GST and process age-related state changes
			for(String sGID: GST.keySet())
			{	
				try
				{
					GuestStatus<GuestStatusState> gs = GST.get(sGID);                    

					// first, handle total abandonment
					Date dtFirstAtReader = gs.getTimeEarliestAtReader();
					Date dtLastAtReader = gs.getTimeLatestAtReader();
					long cmsecDiff = dtNow.getTime() - dtLastAtReader.getTime();
					long cmsecDiffFirst = dtNow.getTime() - dtFirstAtReader.getTime();
					if ( cmsecDiff > (ConfigOptions.INSTANCE.getModelInfo().getAbandonmentTimeout() * 1000) )
					{
						// abandoned
						abandonGuest(gs, bMasterOrSolo, dtNow);	
					}
					else if( gs.getState()==GuestStatusState.EXITED &&
							 cmsecDiffFirst > ConfigOptions.INSTANCE.getModelInfo().getExitStateTimeout()*1000 )
					{
						// after a guest goes to EXITED state, mark as DELETABLE after a suitable delay
						gs.setState(GuestStatusState.DELETABLE);
						bGSTDirty = true;
						if (XBRCController.getInstance().isVerbose())
							logger.debug("Guest: " + sGID + " changed from state EXITED to DELETABLE after timeout");
					}
					else if (bMasterOrSolo)
					{						
						// associate guest to a vehicle using Vehicle ID and GPIO events.
						processVehicleAssociation(gs, dtNow, cmsecDiff);					
						
						// Confidence stuff	                   
	                    doConfidenceTimeProcessing(gs);	                       
					}
				}
				catch (Exception e)
				{
					logger.error("Exception during afterProcessEvents", e);
				}
			}
		}
		
		Date dtNow = new Date();
		
		// expire trains					
		for (VaLocation vaLocation: vehicleAssociationLocations.values())
		{
			// Check to see if we received all expected GPIO events for this train.
			if (vaLocation.getConfig().getCarsPerTrain() > 0 && vaLocation.isExpireTrain(dtNow) && 
				vaLocation.getVehicleSequence() < vaLocation.getConfig().getCarsPerTrain())
				onMissedGpioEvent(vaLocation, vaLocation.getConfig().getCarsPerTrain() - vaLocation.getVehicleSequence());
			
			vaLocation.processTimerEvents(dtNow);
		}			
				
		// see if it's time to send out metrics (metrics sent out only if MetricsPeriod>0
		if (ConfigOptions.INSTANCE.getModelInfo().getMetricsPeriod()>0)
		{
			long secDiff = (dtNow.getTime() - dtMetricsStart.getTime()) / 1000;
			if (secDiff >= ConfigOptions.INSTANCE.getModelInfo().getMetricsPeriod())
			{
				if (bMasterOrSolo)
				{
					SendJMSMessage("METRICS", 
									new MetricsMessageArgs(	dtMetricsStart, 
															dtNow,
															metXPass.getGuestCount(),
															metXPass.getAbandonments(),
															metXPass.getWaitTime(),
															metXPass.getMergeTime(),
															metXPass.getTotalTime(),
															metStandby.getGuestCount(),
															metStandby.getAbandonments(),
															metStandby.getWaitTime(),
															metStandby.getTotalTime()), true, true);
				}
				
				// reset time and data
				dtMetricsStart = dtNow;
				metXPass = new Metrics();
				metStandby = new Metrics();
			}
		}
		
		// see if we need to unlock any readers in blue lane state
		if (bMasterOrSolo)
		{	
			for (Entry<String,ReaderState> rs : readerState.entrySet())
			{
				if (rs.getValue().getExpireTime() != null && dtNow.getTime() >= rs.getValue().getExpireTime().getTime())
				{
					if (rs.getValue().getWaitingForGxp())
					{
						logger.warn("GXP call expired. Performing the GXP timeout action \"" + 
								ConfigOptions.INSTANCE.getModelInfo().getGxpCallTimeoutAction() +
								"\" on reader " + rs.getKey());
						
						if (ConfigOptions.INSTANCE.getModelInfo().getGxpCallTimeoutAction().equals("success"))
							showSuccessFeedback(rs.getKey());
						else if (ConfigOptions.INSTANCE.getModelInfo().getGxpCallTimeoutAction().equals("failure"))
							showFailureFeedback(rs.getKey(), false);
						else if (ConfigOptions.INSTANCE.getModelInfo().getGxpCallTimeoutAction().equals("error"))
							showErrorFeedback(rs.getKey());
						else
							clearReaderLight(rs.getKey());
					}
					else					
						clearReaderLight(rs.getKey());
				}
			}
		}
	}

    private int HandleConfidence(ConfidenceEventAggregate ev,
                               GuestStatus<GuestStatusState> gs)
    {        	
    	EventsLocationConfig eventConfig = eventsLocationConfigurations.get(ev.getReaderInfo().getLocation().getName());
        if ( eventConfig == null || !eventConfig.isEnableConfProcessing())
            return 0;
        
        String lastConfidenceEventLocation = null;
    	if (gs.getLastConfidenceState() != null)
    	{
    		ReaderInfo ri = gs.getLastConfidenceState().getReaderInfo();
    		if (ri != null)
    			lastConfidenceEventLocation = ri.getLocation().getName();
    	}

        // Determine if the new confidence level is greater than
        // the delta of the last sent confidence. This needs to be
        // positive or negative.
    	Integer lastConfidence = gs.getLastSentConfidenceValue();
        Integer confidenceChange = null;
        logger.trace("Confidence last event: confidence=" + lastConfidence);
        if ( lastConfidence != null ) {
            logger.trace("Confidence current event: confidence=" + ((LRREventAggregate)ev).getConfidence());
            confidenceChange = Math.abs( ((LRREventAggregate)ev).getConfidence() - lastConfidence );
            logger.trace("Confidence change: confidenceDelta=" + confidenceChange);
        }        

        boolean locationChanged = lastConfidenceEventLocation != null && 
        		!ev.getReaderInfo().getLocation().getName().equals(lastConfidenceEventLocation);
        
        boolean confidenceDeltaChange = !locationChanged && confidenceChange != null && confidenceChange >= eventConfig.getConfidenceDelta();
        
        // Send out a message if we haven't sent one or if we have enough of a delta change.
        if (locationChanged || confidenceChange == null || confidenceDeltaChange) 
        {
        	//if (locationChanged)
        	//	sendLocationAbandon(gs);
        	
            logger.trace("Sending confidence event");

            // send the readerevent
            SendJMSMessage("LOCATIONEVENT",
                    new ConfidenceMessageArgs(ev,
                            false,
                            gs.getGuestID(),
                            ev.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType(),
                            ((LRREventAggregate)ev).getConfidence()),
                            confidenceDeltaChange ? eventConfig.isSendDeltaConfToJMS() : eventConfig.isSendConfToJMS(), true);
            
            gs.setLastSentConfidenceValue(((LRREventAggregate)ev).getConfidence());

            XBRCController.getInstance().logEKG(new Date().getTime() +
                    ",CONF," +
                    gs + "," +
                    gs.getBandType() + "," +
                    ev.getReaderInfo().getLocation().getName() + "," +
                    ((LRREventAggregate)ev).getConfidence()
            );
        }
        else
        {
            logger.trace("Confidence event not sent. Not big enough delta change in confidence: ConfidenceChange="
                    + confidenceChange +
                    "; confidenceLastEvent=" + (lastConfidence == null ? 0 : lastConfidence) +
                    "; currentConfidence=" + ((LRREventAggregate)ev).getConfidence() +
                    "; currentReaderLocation=" + ev.getReaderInfo().getLocation().getName() +
                    "; oldReaderLocation=" + lastConfidenceEventLocation);
        }

        return 0;
    }
    
    /*
     * The following algorithm examines band events that surround each GPIO/VEHICLE event.
     * For each GPIO/VEHICLE event a score is calculated by averaging the max signal strength.
     * The GPIO/VEHICLE event with the highest score wins.
     */
    private Vehicle calculateScoreUsingSourroundingEvents(GuestStatus<GuestStatusState> gs, VaLocation vaLocation)
    {
    	// The score calculation for each vehicle is simple. It is the average signal strength of 
    	// some maximum number of events immediately surrounding the VEHICLE event.		
		
		Vehicle vehicle = null;
		Double vehicleScore = null;
		int count = 0;
		
		for (VehicleIndex vi :  gs.getVehicles())
		{
			Vehicle v = vi.getVehicle();
			logger.trace("VEHICLE: Calculating score for vehicle " + v.getCompositeID() + " and guest " + gs.getGuestID());				
			
			// Examine up to max events surrounding the AVMSVehicleEvent. We do not want to 
			// analyze all of the events in case the ride had stopped.
			int iup = vi.getEventIndex();
			int idown = vi.getEventIndex() + 1;
			
			double score = 0.0;
			count = 0;
			boolean moveup = true;
			while(count < vaLocation.getConfig().getMaxAnalyzeGuestEventsPerVehicle() &&
				  (iup >= 0 || idown < gs.getEventsAtLocation().size()))
			{
				LRREventAggregate lre = null;
				
				if (moveup)
				{
					// Check if we can still go up
					if (iup < 0)
						break;
					
					lre = gs.getEventsAtLocation().get(iup);
					iup--;
					moveup = false;
				}
				else
				{
					// Check if we can still go down
					if (idown >= gs.getEventsAtLocation().size())
						break;
					
					lre = gs.getEventsAtLocation().get(idown);
					idown++;
					moveup = true;
				}
											
				long timeDelta = v.getAdjustedEventTime().getTime() - lre.getTimestamp().getTime();
					
				count++;
				// The signal strength must be positive for the formula to work correctly. So, lets just add a 90.
				score = score + (90 + lre.getMaxStrength());
				if (logger.isTraceEnabled())
					logger.trace("VEHICLE: event: count=" + count + " ss=" + lre.getMaxStrength() + " time delta=" + timeDelta + 
						     " eno = " + lre.getPacketSequence() + " new score=" + score);
			}
			
			if (count < 2)
			{
				logger.trace("VEHICLE: Ignoring vehicle " + v.getCompositeID() + " for guest " + gs + 
						" because at least 2 band events are required for score calculation.");
				continue;
			}
			
			// Calculate the average score.
			if (count > 0)
				score = score / count;
			
			logger.trace("VEHICLE: Vehicle score for guest " + gs + " and vehicle " + v.getCompositeID() + " is " + score);
			
			if (vehicleScore == null || score > vehicleScore)
			{
				vehicle = v;
				vehicleScore = score;
			}
		}
		
		return vehicle;
    }
    
    /*
     * The idea of the following algorithm is to find the time when the guest signals have peaked.
     * Then the closest in time GPIO/VEHICLE event is chosen.  
     * The algorithm finds the two adjacent band pings with the highest signal strength.
     * A time between these two events is calculated with a proportional shift toward the stronger of the two events.
     * This yields the approximate peak of all the pings for the guest.
     * NOTE: a better, but more calculation intensive approach may be to find a polynomial function that matches the
     * guest pings and then find time of the peak of the curve. 
     */
    private Vehicle calculateScoreUsingSignalPeak(GuestStatus<GuestStatusState> gs, VaLocation vaLocation)
    {
    	if (gs.getEventsAtLocation().isEmpty() || gs.getVehicles().isEmpty())
    		return null;
    	
    	LRREventAggregate left = null;
    	LRREventAggregate right = null;    	
    	
    	if (gs.getEventsAtLocation().size() == 1)
    		left = gs.getEventsAtLocation().get(0);
    	else if (gs.getEventsAtLocation().size() >= 2)
    	{
    		right = gs.getEventsAtLocation().get(1);
    		int maxIdx = 1;
    		
    		// Find the strongest event
	    	for (int i = 2; i < gs.getEventsAtLocation().size(); i++)
			{
	    		LRREventAggregate ev = gs.getEventsAtLocation().get(i);
	    		if (ev.getMaxStrength() >= right.getMaxStrength())
	    		{
	    			right = ev;
	    			maxIdx = i;
	    		}
			}
	    
	    	left = gs.getEventsAtLocation().get(maxIdx-1);
	    			
	    	// Find the stronger one of the surrounding events.
	    	if (maxIdx < (gs.getEventsAtLocation().size()-1))
	    	{
	    		if (gs.getEventsAtLocation().get(maxIdx+1).getMaxStrength() > left.getMaxStrength())
	    		{
	    			// The right one is stronger than the left one. Shift the events to the right.
	    			left = right;
	    			right = gs.getEventsAtLocation().get(maxIdx+1);
	    		}
	    	}
    	}
    	
    	long peakTime = 0;

        if(left != null) {
            left.getTimestamp().getTime();
        }
    	
    	if (right != null)
    	{
    		// delta time
    		double dt = right.getTimestamp().getTime() - left.getTimestamp().getTime();
    		// delta signal strength
    		double ds = right.getMaxStrength() - left.getMaxStrength();    		
    		// peak time shifted proportionally toward the stronger of the two signals
    		peakTime = Math.round(left.getTimestamp().getTime() + (dt * (ds/Math.sqrt(dt*dt + ds*ds) + 1.0))/2.0);
    	}
    	
    	if (logger.isTraceEnabled())
    	{
    		if (right != null)
    		{
	    		logger.trace("Signal peak time for guest " + gs + 
	    					" between events (time,eno,ss) (" + 
	    					left.getTimestamp().getTime() + "," + left.getPacketSequence() + "," + left.getMaxStrength() + ")" + 
	    					" and (" + 
	    					right.getTimestamp().getTime() + "," + right.getPacketSequence() + "," + right.getMaxStrength() + ")");
    		}
    		else
    		{
    			logger.trace("Signal peak time for guest " + gs + " is " + peakTime + 
    					" based on event (time,eno,ss) (" + 
    					left.getTimestamp().getTime() + "," + left.getPacketSequence() + "," + left.getMaxStrength() + ")");
    		}
    	}
    	
    	// Now find the closest GPIO/VEHILCE event to the peakTime.
    	Vehicle vehicle = null;
		
		for (VehicleIndex vi :  gs.getVehicles())
		{
			Vehicle v = vi.getVehicle();
			
			long dt1 = Math.abs(peakTime - v.getAdjustedEventTime().getTime());	
			logger.trace("VEHICLE: Time between vehicle " + v.getCompositeID() + " and peak signal strength is " + dt1);
			
			if (vehicle == null)
			{
				vehicle = v;
				continue;
			}
			
			long dt2 = Math.abs(peakTime - vehicle.getAdjustedEventTime().getTime());
			
			if (dt1 <= dt2)
				vehicle = v;
		}
		
		if (vehicle != null)
			logger.trace("VEHICLE: The closest vehicle to guest " + gs + 
						 " is " + vehicle.getCompositeID() + " (" + Math.abs(peakTime - vehicle.getAdjustedEventTime().getTime()) + " ms away)");
		
		return vehicle;
    }

	private void calculateVehicleAssociationScore(GuestStatus<GuestStatusState> gs, VaLocation vaLocation)
	{
		// First make some adjustments to the vehicle event, where it fits amongst the
		// list of received LRR events.
		adjustVehicleEventPlacement(gs);
			
		try
		{
			Vehicle vehicle1 = null;
			Vehicle vehicle2 = null;
			Vehicle vehicle = null;
			
			vehicle1 = calculateScoreUsingSourroundingEvents(gs, vaLocation);			
			vehicle2 = calculateScoreUsingSignalPeak(gs, vaLocation);
			
			switch(vaLocation.getConfig().getVaAlgorithmEnum())
			{
				case closestpeakfallback:
					vehicle = vehicle1;
					if (vehicle == null)
						vehicle = vehicle2;
					break;
				case nearevents:
					vehicle = vehicle1;
					break;
				case closestpeak:
					vehicle = vehicle2;
					break;
			}
			
			if (vehicle1 == vehicle2)
				logger.trace("VEHICLE: Both VA algorithms produced the same result for guest " + gs);
			else
			{
				logger.warn("VEHICLE: VA algorithms produced different results for guest " + gs);
				logger.warn("VEHICLE: The nearevents algorithm association was " + (vehicle1 == null ? " none " : vehicle1.getCompositeID()));
				logger.warn("VEHICLE: The closestpeak algorithm association was " + (vehicle2 == null ? " none " : vehicle2.getCompositeID()));
			}
			
			if (vehicle != null)
			{
				if (gs.getEventsAtLocation().size() < vaLocation.getConfig().getMinReadsToAssociate())
				{
					logger.trace("Guest " + gs
							+ " generated only " + gs.getEventsAtLocation().size() + " singulated band events at the vehicle association location. "
							+ " This is less than required " + vaLocation.getConfig().getMinReadsToAssociate() + " minreadstoassociate.");
					
					XBRCController.getInstance().logEKG(Long.toString(new Date().getTime()) + ",VBATFPINGS,"
							+ gs.getGuestID() + "," + vehicle.getCompositeID() + "," + gs.getEventsAtLocation().size() + "," + vaLocation.getConfig().getMinReadsToAssociate() + "," + gs);
					
					return;
				}
				
				assignGuestToVehicleFromAVMSEvent(vehicle,gs,vaLocation);
			}
			else
			{
				logger.warn("VEHICLE: Guest " + gs.getGuestID() + " was not associated with any vehicles after leaving the vehicle association location");
				
				if (gs.getVehicles().isEmpty())
				{
					XBRCController.getInstance().logEKG(Long.toString(new Date().getTime()) + ",VBANOCARS,"
							+ gs.getGuestID() + "," + gs.getEventsAtLocation().size() + "," + vaLocation.getConfig().getMinReadsToAssociate() + "," + gs);
				}
				else
				{
					XBRCController.getInstance().logEKG(Long.toString(new Date().getTime()) + ",VBATFPINGS,"
							+ gs.getGuestID() + ",0," + gs.getEventsAtLocation().size() + "," + vaLocation.getConfig().getMinReadsToAssociate() + "," + gs);
				}
			}
		}
		finally
		{
			gs.getEventsAtLocation().clear();
			gs.getVehicles().clear();
		}
	}	
	
	public void SetGuestState(String sGuestId, GuestStatusState gss)
	{
		synchronized(GST)
		{
			GuestStatus<GuestStatusState> gs = GST.get(sGuestId);
			if (gs!=null)
			{
				gs.setState(gss);
				bGSTDirty = true;
			}
		}
	}

	public GuestStatusState GetGuestState(String sGuestId)
	{
		synchronized(GST)
		{
			GuestStatus<GuestStatusState> gs = GST.get(sGuestId);
			if (gs!=null)
			{
				return gs.getState();
			}
			else
				return null;
		}
	}
	
	private String getGXPBandID(EventAggregate ev) 
	{
		if (ev instanceof LRREventAggregate)
			return ((LRREventAggregate) ev).getXband().getLRId();
		
		return ((TapEventAggregate)ev).getTapEvent().getSid();			
	}
	
	private void sendGXPTap(String sGuestId, EventAggregate ev, ReaderInfo ri, String readerName) throws Exception
	{		
		gxp.sendTap(sGuestId, getGXPBandID(ev), ri, ev.getTimestamp());
		
		// prevent double tapping until we get a gxp response
		setReaderState(readerName, false, ConfigOptions.INSTANCE.getModelInfo().getCmsecGxpCallTimeout(), true);
	}

	private void
	assignGuestToVehicleFromAVMSEvent(Vehicle vehicle, GuestStatus<GuestStatusState> gs, VaLocation vaLocation)
	{						 
		if (gs.getCarId() != null && !gs.getCarId().isEmpty() && gs.getCarIdTime() != null)
		{
			long elapsedTimeSec = (new Date().getTime() - gs.getCarIdTime().getTime()) / 1000;
			if (elapsedTimeSec < vaLocation.getConfig().getVaTimeoutSec())
			{
				logger.warn("VEHICLE: Guest " + gs + " was already associated to another vehicle " + gs.getCarId() + " " + elapsedTimeSec + " seconds ago " +
						"which is less than the " + vaLocation.getConfig().getVaTimeoutSec() + " VA Timeout configuration parameter. Not associating.");
				return;
			}
		}
			
		// When guest is assigned to a vehicle based on the VID event, we know that the guest is now RIDING.
		// Make sure the guest State is set properly.
		
		switch(gs.getState())
		{
			case HASENTERED:
			case HASMERGED:
			case LOADING:
			case INDETERMINATE:
			case RIDING:
			case DELETABLE:
			case EXITED:
			{	
				// Use the current train VEHICLE event in case of laser breaks..
				if (vehicle.getVehicleid() == null)
				{
					if (vaLocation.getCurrentTrain() == null || vaLocation.countLaserBreaks())
						logger.warn("VEHICLE: No VEHICLE event was received for guest " + gs.getGuestID());
					else
						vehicle.setTrainInfo(vaLocation.getCurrentTrain());
				}
				
				logger.debug("VEHICLE: ** Setting guest " + gs.getGuestID() + "'s car id to: " + vehicle.getCompositeID() + " based of Vehicle ID event");					
				gs.setCarId(vehicle.getCompositeID());
				gs.setCarIdTime(new Date());
				bGSTDirty = true;
				vehicle.setGuestCount(vehicle.getGuestCount() + 1);
				
				logger.trace("VEHICLE: Vehicle " + vehicle.getCompositeID() + " now has " + vehicle.getGuestCount() + " guest(s)");
				
				gs.setTimeLoaded(vehicle.getTimestamp());
				
				// normally, go to state RIDING, but if there are no readers in subsequent states, go to exit
				boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit,gs.getBandMediaType());
												
				if (bAnySubsequentReaders)
					gs.setState(GuestStatusState.RIDING);
				else
					gs.setState(GuestStatusState.EXITED);
				bGSTDirty = true;
				
				XBRCController.getInstance().logEKG(Long.toString(vehicle.getTimestamp().getTime()) + ",INVEHICLE,"
						+ gs.getGuestID() + "," + vehicle.getCompositeID() + "," 
						+ gs);
				
				// Until xBMS is fully functional always send the long range band ID rather than the GuestID in the INVEHICLE message
				this.SendJMSMessage("INVEHICLE",
                        new InVehicleMessageArgs(vehicle,
                                gs.getHasXpass(),
                                gs.getGuestID(),
                                gs.getPidDecimal(),
                                gs.getLinkID(),
                                gs.getLinkIDType(),
                                gs.getBandType()), true, true);
				
				XBRCController.getInstance().logEKG(Long.toString(vehicle.getTimestamp().getTime()) + ",LOAD,"
						+ gs.getGuestID() + "," + vehicle.getCompositeID() + "," 
						+ gs);

				this.SendJMSMessage("LOAD",
                        new LoadMessageArgs(vehicle.getEventAggregate(),
                                gs.getHasXpass(),
                                gs.getCarId(),
                                gs.getGuestID(),
                                gs.getPidDecimal(),
                                gs.getLinkID(),
                                gs.getLinkIDType(),
                                gs.getBandType()), true, true);
				
				break;				
			}
		}		
	}
	
	/*
	 * Adds this vehicle to the list of vehicle candidates for all guests present at the vehicle 
	 * association location. 
	 */
	private void
	AddVehicleCandidate(Vehicle vehicle, VaLocation vehicleVaLocation)
	{
		if (vehicleAssociationLocations.isEmpty())
		{	
			// this should never happen, but let's log it in case we have some bugs to fix
			logger.error("VEHICLE: Cannot associate guests with vehicles because there are no vehicle association locations.");
			return;
		}
		
		// Hold this vehicle for guests that may show up soon.
		if (vehicleVaLocation.getConfig().getVehicleHoldTimeMs() > 0)
			vehicleVaLocation.getVehiclesWaitingForGuests().add(vehicle);
		
		synchronized(GST)
		{
			Date dtNow = new Date();
			
			for (GuestStatus<GuestStatusState> gs : GST.values())
			{				
				// The guest must have produced at least one event at VA location so we know where he is
				if (gs.getEventsAtLocation().size() == 0)
					continue;
				
				VaLocation guestVaLocation = vehicleAssociationLocations.get(gs.getEventsAtLocation().get(0).getReaderInfo().getLocation().getName());
				// Both the guest and the vehicle must be at the same VA location
				if (guestVaLocation != vehicleVaLocation)
					continue;
												
				long cmsecDiff = dtNow.getTime() - gs.getTimeLatestAtReader().getTime();
				if (cmsecDiff > (vehicleVaLocation.getConfig().getOnrideTimeoutSec() * 1000))
				{
					logger.info("Ignoring vehicle " + vehicle.getCompositeID() + " event for guest " + gs.getGuestID()
							    + " because the guest stopped singulating to the vehicle association location for over " 
							    + vehicleVaLocation.getConfig().getOnrideTimeoutSec()
							    + " seconds");
					continue;
				}
				
				VehicleIndex vi = new VehicleIndex(vehicle);
				
				// We must remember the lrr index where the vehicle is so we can find it
				// when calculating the score for this vehicle.
				if (gs.getEventsAtLocation().size() == 0)
					vi.setEventIndex(0);
				else
					vi.setEventIndex(gs.getEventsAtLocation().size()-1);
				
				// Add this vehicle to vehicles that the guest could be associated with.
				gs.getVehicles().add(vi);
			}
		}
	}

	private void
	AnalyzeAVMSEvent(AVMSEventAggregate ev)
	{
		// The AVMS event tells us that a vehicle has reached a waypoint.
		// Add this vehicle to the list of possible vehicles that a guest
		// could be associated with. The actual decision as to which vehicle
		// the guests is really in will be deferred until we receive more
		// guest lrr band reads.
		
		Vehicle vehicle = new Vehicle(ev);
		
		VaLocation vaLocation = vehicleAssociationLocations.get(ev.getReaderInfo().getLocation().getName());
		
		if (vaLocation == null)
		{
			// This should not ever happen, but better to find this in the logs than null pointer exception.
			logger.error("Could not find vehicle association configuration for location " + ev.getReaderInfo().getLocation().getName());
			return;
		}
		
		// Adjust the vehicle event time if necessary. Only apply the offset if not using laser events.
		long offset = vaLocation.getConfig().getRequireVehicleLaserEvent() ? 0 : vaLocation.getConfig().getVehicleTimeOffsetMs(); 
		
		if (vaLocation.getConfig().getUseVehicleEventTime())
		{				
			vehicle.setAdjustedEventTime(
					new Date(vehicle.getReaderEventTime().getTime() + offset));
		}
		else
		{
			vehicle.setAdjustedEventTime(
					new Date(vehicle.getTimestamp().getTime() + offset));
		}
		
		// log event
		XBRCController.getInstance().logEKG(Long.toString(ev.getTimestamp().getTime()) + ",VEHICLE,"
				+ ev.getAvmsEvent().getVehicleid() + "," + ev.getAvmsEvent().getReaderName() + "," 
				+ ev.getAvmsEvent().getReaderEventTime().getTime());
		
		
		// If we do not require further laser events then just add this vehicle to the list of vehicles
		// to choose from. Otherwise, set this vehicles as the vehicle ID for the current train.
		if (vaLocation.getConfig().getRequireVehicleLaserEvent())
		{		
			// The VEHICLE events for a train should time out by themselves, we should not be getting duplicate
			// VEHICLE events within the timeout period, unless the VEHICLE ID is the same.
			
			if (vaLocation.getCurrentTrain() != null)
			{
				if (!vaLocation.getCurrentTrain().getVehicleid().equals(vehicle.getVehicleid()))
				{
					if (vaLocation.countLaserBreaks())
					{
						if (vaLocation.getCurrentTrain().getGpioEventCountBeforeVehicle() < vaLocation.getConfig().getLaserBreaksBeforeVehicle())
							logger.warn("VEHICLE: Received only " + vaLocation.getCurrentTrain().getGpioEventCountBeforeVehicle() + " GPIO events proceeding the vehicle " + 
									vehicle.getVehicleid() + " Expected " +  vaLocation.getConfig().getLaserBreaksBeforeVehicle()
									+ " GPIO event(s).");
						
						if (vaLocation.getCurrentTrain().getGpioEventCountAfterVehicle() <  vaLocation.getConfig().getLaserBreaksAfterVehicle())
							logger.warn("VEHICLE: Received only " + vaLocation.getCurrentTrain().getGpioEventCountAfterVehicle() + " GPIO events following the vehicle " + 
									vehicle.getVehicleid() + " Expected " +  vaLocation.getConfig().getLaserBreaksAfterVehicle()
									+ " GPIO event(s).");
					}
					else
					{
						logger.warn("VEHICLE: Received another VEHICLE event for the same train. Previous Vehicle ID: " + 
								vaLocation.getCurrentTrain().getVehicleid() + " New ID: " + vehicle.getVehicleid());
					}
				}
			}
			
			vaLocation.setCurrentTrain(vehicle);		
			
			if (vaLocation.countLaserBreaks())
			{
				// Associate this vehicle with previously received GPIO events.
				while (!vaLocation.getGpioWaitingForVehicle().isEmpty())
				{
					// Only associate the expected number of most recent gpio events.
					if (vaLocation.getCurrentTrain().getGpioEventCountBeforeVehicle() >= vaLocation.getConfig().getLaserBreaksBeforeVehicle())
						break;
					
					Vehicle gpio = vaLocation.getGpioWaitingForVehicle().pop();
					gpio.setTrainInfo(vaLocation.getCurrentTrain());
					vaLocation.getCurrentTrain().setGpioEventCountBeforeVehicle(vaLocation.getCurrentTrain().getGpioEventCountBeforeVehicle()+1);
					// We are popping the GPIO events in reverse order of reception.
					gpio.setVehicleSequence(vaLocation.getConfig().getLaserBreaksBeforeVehicle() - vaLocation.getCurrentTrain().getGpioEventCountBeforeVehicle() + 1);
				}
				
				vaLocation.getGpioWaitingForVehicle().clear();

				if (vaLocation.getCurrentTrain().getGpioEventCount() >= vaLocation.expectedLaserBreaks())
				{
					logger.trace("VEHICLE: Vehicle " + vaLocation.getCurrentTrain().getVehicleid() + " was associated with all expected GPIO events.");
					vaLocation.setCurrentTrain(null);
					vaLocation.setVehicleSequence(0);
				}
			}
		}
		else
		{
			AddVehicleCandidate(vehicle, vaLocation);
		}
	}
	
	private void 
	AnalyzeXtpGpioEvent(XtpGpioEventAggregate ev)
	{
		VaLocation vaLocation = vehicleAssociationLocations.get(ev.getReaderInfo().getLocation().getName());
		
		if (vaLocation == null)
		{
			// This should not ever happen, but better to find this in the logs than null pointer exception.
			logger.error("Could not find vehicle association configuration for location " + ev.getReaderInfo().getLocation().getName());
			return;
		}
		
		AvmsLaserEvent vle = new AvmsLaserEvent(ev);
		AnalyzeAvmsLaserEvent(vle, vaLocation);
	}
	
	private void
	AnalyzeAvmsLaserEvent(AvmsLaserEvent ev, VaLocation vaLocation)
	{
		Vehicle vehicle = new Vehicle(ev);
		
		Date dtNow = new Date();
		
		vehicle.setAdjustedEventTime(new Date(vehicle.getTimestamp().getTime() + 
				vaLocation.getConfig().getVehicleTimeOffsetMs()));
	
		if (!vaLocation.getConfig().getRequireVehicleLaserEvent())
		{
			XBRCController.getInstance().logEKG(Long.toString(ev.getTime().getTime()) + ",LASERBREAK," + Long.toString(vehicle.getAdjustedEventTime().getTime()) + ",1");
			logger.trace("Received GPIO event from reader " + ev.getEventAggregate().getReaderInfo().getName());
			
			// we should not be getting laser events if they are not required
			logger.warn("VEHICLE: Ignoring vehicle laser break event because requireVehicleLaserEvent Config option is not set.");
			return;
		}
		
		if (!vaLocation.countLaserBreaks())
		{
			// Reset vehicle sequence on timeout.
			if (vaLocation.getLastAvmsLaserEventTime() != null && vaLocation.getVehicleSequence() != 0)
			{				
				if ((dtNow.getTime() - vaLocation.getLastAvmsLaserEventTime().getTime()) > (vaLocation.getConfig().getTrainTimeoutSec()*1000))
				{					
					vaLocation.setVehicleSequence(0);
					// Normally, the vehicle sequence would have been reset on train timeout in afterprocessevents. If we got here then
					// this means that no vehicle event was ever received for this train. 
					onMissedVehicleEvent(vaLocation);
				}
			}
			vaLocation.setVehicleSequence(vaLocation.getVehicleSequence() + 1);
		}
		else
		{
			// Loose the current train if it has all the expected gpio events
			if (vaLocation.getCurrentTrain() != null)
			{								
				if (vaLocation.getCurrentTrain().getGpioEventCountAfterVehicle() >= vaLocation.getConfig().getLaserBreaksAfterVehicle())
				{							
					logger.trace("VEHICLE: Vehicle " + vaLocation.getCurrentTrain().getVehicleid() + " was associated with " + vaLocation.getCurrentTrain().getGpioEventCount() + " GPIO events. Discarding the VEHICLE event.");
					vaLocation.setCurrentTrain(null);
					vaLocation.setVehicleSequence(0);
				}
			}
						
			if (vaLocation.getCurrentTrain() != null)
			{
				// Associate the current train with the GPIO event and increment gpio event count.
				vehicle.setTrainInfo(vaLocation.getCurrentTrain());
				vaLocation.getCurrentTrain().setGpioEventCountAfterVehicle(vaLocation.getCurrentTrain().getGpioEventCountAfterVehicle()+1);
				vaLocation.setVehicleSequence(vaLocation.getCurrentTrain().getGpioEventCount());
			}
			else
			{
				vaLocation.setVehicleSequence(vaLocation.getVehicleSequence() + 1);
				
				// Need a VEHICLE event.
				vaLocation.getGpioWaitingForVehicle().push(vehicle);
				
				// There should never be more than the expected GPIO events per vehicle in this list.
				if (vaLocation.getGpioWaitingForVehicle().size() > vaLocation.getConfig().getLaserBreaksBeforeVehicle())
				{
					logger.warn("VEHICLE: GPIO event was not associated with a VEHICLE event " +
								" most likely due to a missing VEHICLE event or incorrectly set laserbreaksbeforevehicle parameter.");
					vaLocation.getGpioWaitingForVehicle().removeLast();
					onMissedVehicleEvent(vaLocation);
				}
			}
		}
		
		vaLocation.setLastAvmsLaserEventTime(dtNow);
		
		vehicle.setVehicleSequence(vaLocation.getVehicleSequence());
		
		XBRCController.getInstance().logEKG(Long.toString(ev.getTime().getTime()) + ",LASERBREAK," + 
				Long.toString(vehicle.getAdjustedEventTime().getTime()) + "," + vehicle.getVehicleSequence());
		
		logger.trace("Received GPIO event from reader " + ev.getEventAggregate().getReaderInfo().getName() + 
					 " Adjusted time: " + Long.toString(vehicle.getAdjustedEventTime().getTime()) + 
					 " Seq: " + vehicle.getVehicleSequence());
		
		if (vaLocation.countLaserBreaks())
		{
			// Loose the current train if after adding this GPIO event we don't need it anymore.
			if (vaLocation.getCurrentTrain() != null && vaLocation.getCurrentTrain().getGpioEventCountAfterVehicle() >= vaLocation.getConfig().getLaserBreaksAfterVehicle())
			{
				// ConfigOptions.INSTANCE.getModelInfo().getLaserBreaksAfterVehicle() could be set to 0
				if (vaLocation.getCurrentTrain().getGpioEventCount() == 0)
				{
					logger.trace("VEHICLE: Vehicle " + vaLocation.getCurrentTrain().getVehicleid() + " was not associated with any GPIO events most likely due to missing GPIO events.");
					onMissedGpioEvent(vaLocation, 1);
			    }
				else
					logger.trace("VEHICLE: Vehicle " + vaLocation.getCurrentTrain().getVehicleid() + " was associated with " + vaLocation.getCurrentTrain().getGpioEventCount() + " GPIO events. Discarding the VEHICLE event.");
				vaLocation.setCurrentTrain(null);
				vaLocation.setVehicleSequence(0);
			}
		}
		
		AddVehicleCandidate(vehicle, vaLocation);
	}
	
	private int
	AnalyzeCarEvent(EventAggregate ev)
	{
		// if the event is an exit type event, skip it
		LocationType lt = getLocationType(ev);
		if (lt == LocationType.Exit)
			return 0;
		
		String sCarID = ConfigOptions.INSTANCE.getCarInfo().GetCarID(ev.getID());
		String sCarLocationName = ev.getReaderInfo().getLocation().getName();
			
		if (XBRCController.getInstance().isVerbose())
			logger.debug("** Analyzing car event");
		
		// now, iterate through all guests at the same location and associate them with the car if they're LOADING
		boolean bAnyLoading = false;
		synchronized(GST)
		{
			for(String sGuestID: GST.keySet())
			{
				GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
				
				// only look at LOADING guests who are at the same location
				if (gs.getState() == GuestStatusState.LOADING)
				{
					bAnyLoading = true;
					
					String sLastReader = gs.getLastReader();
					ReaderInfo ri = XBRCController.getInstance().getReader(sLastReader);
					String sGuestLocationName = ri.getLocation().getName();
					
					if (XBRCController.getInstance().isVerbose())
						logger.debug("** Comparing car " + sCarID + "'s location, " + sCarLocationName + " to " + sGuestID + "'s: " + sGuestLocationName);
					
					if (sCarLocationName.compareTo(sGuestLocationName)==0)
					{
						// TODO: don't assign cars to stale guests (those who are in LOADING, but haven't been seen for a while
						if (XBRCController.getInstance().isVerbose())
							logger.debug("** Setting guest " + sGuestID + "'s car id to: " + sCarID);					
						gs.setCarId(sCarID);
						gs.setCarIdTime(new Date());
						bGSTDirty = true;
					}
					
				}
			}
		}
		
		if (!bAnyLoading && XBRCController.getInstance().isVerbose())
			logger.debug("** No guests in LOADING state");
		
		return 0;
		
	}
	
	private int
	AnalyzeEvent(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		int err = 0;
		
		// peg some data
		LocationType locationType = LocationType.getByOrdinal(ev.getReaderInfo().getLocation().getLocationTypeID());
		GuestStatusState stateStart = gs.getState();		

        // handle well known reader types
		switch(locationType)
		{
			case Entry:
				logger.debug("Processing entry event for: "+ gs.getGuestID());
				err = HandleEntryEvent(ev, gs);
				break;
				
			case xPassEntry:
				logger.debug("Processing xpass entry event for: "+ gs.getGuestID());
				err = HandleXPassEntry(ev, gs);
				break;
				
			case Merge:
				logger.debug("Processing xpass merge event for: "+ gs.getGuestID());
				err = HandleMerge(ev, gs);
				break;
				
			case Combo:
				logger.debug("Process combo event for: " + gs.getGuestID());
				err = HandleCombo(ev, gs);
				break;

			case Queue:
				logger.debug("Processing waypoint event for: "+ gs.getGuestID());
				err = HandleQueue(ev, gs);
				break;
				
			case Load:
				logger.debug("Processing load event for: "+ gs.getGuestID());				
				err = HandleLoad(ev, gs);				
				break;
				
			case Exit:
				logger.debug("Processing exit event for: "+ gs.getGuestID());
				err = HandleExit(ev, gs);
				break;
				
			case InCar:
				logger.debug("Processing incar event for: "+ gs.getGuestID());				
				err = HandleInCar(ev, gs);
				break;
				
			case Waypoint:
                logger.debug("Processing waypoint event for: "+ gs.getGuestID());
				err = HandleWaypoint(ev,gs);
				break;
				
			default:
				logger.debug("Processing other event for: "+ gs.getGuestID());
				err = HandleOther(ev, gs);
				break;
					
		}
		
		gs.AddVisitedLocation(ev.getReaderInfo().getName());

		GuestStatusState stateEnd = gs.getState();

		if (stateStart != stateEnd)
		{			
			if (XBRCController.getInstance().isVerbose())
				logger.debug("Guest: " + ev.getID() + " changed from state " + stateStart + " to " + stateEnd + " processing location " + getLocationType(ev));
			
			if (stateEnd==GuestStatusState.INDETERMINATE)
			{
				logger.warn("!! Guest: " + ev.getID() + " is in INDETERMINATE state");
			}			
		}
		
		if (ev instanceof TapEventAggregate && !gs.isBandTransmitting() && ev.getReaderInfo().getLocation().getSendBandStatus())
		{
			this.SendJMSMessage("BANDSTATUS",
                    new MessageArgs(ev,
                            false,
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
			logger.info("Guest band was not detected by any long range readers. Sending BANDSTATUS message for guest " + gs);
		}
		
		
		EventsLocationConfig eventConfig = eventsLocationConfigurations.get(ev.getReaderInfo().getLocation().getName());
        if (eventConfig != null && eventConfig.isEnableConfProcessing())
        {
	        // Add the event to the guest status for confidence tracking.
	        gs.addConfidenceEvent(ev);	       
        }
        
        // Send LOCATIONEVENT for TAP regardless of location type if enabled with eventConfig
        if (ev instanceof TapEventAggregate && eventConfig != null && (eventConfig.isSendTapToHTTP() || eventConfig.isSendTapToJMS()))
        	sendLocationTapEvent(eventConfig, ev, gs);

        return err;
	}
	
	private void setSignalStrengthAtReader(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		if (!(ev instanceof LRREventAggregate))
			return;
		
		LRREventAggregate lrrev = (LRREventAggregate)ev;
		gs.setMaxSsAtReader(lrrev.getMaxStrength());
		gs.setAvgSsAtReader(lrrev.getAverageStrength());
	}

	private int HandleEntryEvent(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();
		
		// This is for testing only. Normally location events should only be saved at load or waypoint locations.
		saveVaLocationEvents(ev,gs);
		
		// Validate
		switch(gs.getState())
		{
			case NEW:
			case INDETERMINATE:
			{
				gs.setTimeEarliestAtReader(ts);
				gs.setTimeLatestAtReader(ts);
				gs.setTimeEntered(ts);
				gs.setHasXpass(false);
				
				// if it's a tap event, this is a non-xpass situation - just light the green light
				if (ev.getReaderInfo().getType() != null && ev.getReaderInfo().getType().hasLight())
						showSuccessFeedback(sReaderName);
				
				// normally, go to state HASENTERED, but if there are no readers in subsequent states, go to exit
				boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Merge, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Queue, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Load, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.InCar, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit, gs.getBandMediaType());

				// bump the guest count
				XBRCController.getInstance().getStatus().incrementTotalGuestsSinceStart();
				
				// peg previous state
				GuestStatusState stateStart = gs.getState();

				if (bAnySubsequentReaders)
					gs.setState(GuestStatusState.HASENTERED);
				else
					gs.setState(GuestStatusState.EXITED);
				
				bGSTDirty = true;
				
				if (bAnySubsequentReaders && ConfigOptions.INSTANCE.getModelInfo().getDeferEntryMessages())
				{
					// Defer the sendjms message
					logger.debug("In HandleEntryEvent: storing deferred event");
					gs.setDeferredEntryEvent(ev);
				}
				else
				{
					this.SendJMSMessage("ENTRY",
                            new MessageArgs(ev,
                                    false,
                                    gs.getGuestID(),
                                    gs.getPidDecimal(),
                                    gs.getLinkID(),
                                    gs.getLinkIDType(),
                                    gs.getBandType()), true, true);
				}
				
				if (XBRCController.getInstance().isVerbose())
					logger.debug("Guest: " + gs.getGuestID() + " changed from state " + stateStart + " to " + gs.getState());								
				
				break;
			}
			
			case HASENTERED:
			{
				// duplicate
				gs.setTimeLatestAtReader(ts);
				break;
			}
				
			default:
			{
				// guest backtracking? ignore?
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly near ENTRY reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		return 0;
	}

	// This is for xPass entry only
	private int HandleXPassEntry(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Integer deviceId = ev.getReaderInfo().getDeviceId();
		boolean bMobileGxp = ev.getReaderInfo().getType() == ReaderType.mobileGxp;
		Date ts = ev.getTimestamp();
		
		logger.debug("In HandleXPassEntry");
		
		// sanity check
		if (!ReaderType.isTapReader(ev.getReaderInfo().getType()))
		{
			logger.error("Non-tap reader in XPassEntry location! Name: " + sReaderName +
                ", IP: " + ev.getReaderInfo().getIpAddress());
			return 0;
		}

		// Validate
		switch(gs.getState())
		{
			case NEW:
			case INDETERMINATE:
			case HASENTERED:
			case HASMERGED:
			case EXITED:
			{
				logger.debug("In HandleXPassEntry: handling new, indeterminate, hasentered, hasmerged or exited");

				if (gs.getHasXpass())
					logger.debug("In HandleXPassEntry: second or later tap for guest " + gs);
				
				gs.setDeferredEntryEvent(null);
				if (gs.getLastReader() == null || !gs.getLastReader().equals(sReaderName))
					gs.setTimeEarliestAtReader(ts);	
				gs.setTimeLatestAtReader(ts);
				
				// if it's a regular (not mobile-gxp), queue a gxp request. State processing happens on reply
				if (!bMobileGxp)
				{
					// send the GXP entitlement request
					try
					{
						logger.debug("In HandleXPassEntry: queuing tap for gxp  for guest " + gs);
						sendGXPTap(gs.getGuestID(), ev, ev.getReaderInfo(), sReaderName);
					}
					catch (Exception e)
					{
						logger.error("Error sending entry tap to GXP for guest " + gs +" : " + e.getLocalizedMessage() + "\n" + e.getStackTrace().toString());
					}
				}
				else
				{	// for a mobile gxp, queue a gxp callback just as if gxp had returned successfully
					GxpCallback m = new GxpCallback();
					m.setBandId(getGXPBandID(ev));
					long lEntertainmentId = 0;
					try
					{
						lEntertainmentId = Long.parseLong(XBRCController.getInstance().getVenueName()); 
					}
					catch(Exception ex) {}
					//m.setEntertainmentId(lEntertainmentId);
					m.setLocationId(lEntertainmentId);
					m.setOperation(Operation.success);
					m.setDeviceId(deviceId);
					m.setTime(new Date().getTime());
					m.setUnitType(UnitType.Entrance);
					m.setSynthesized(true);
					GxpCallbackQueue.INSTANCE.add(m);
				}
				
				break;
			}

			default:
			{
				// guest backtracking? ignore?
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly near XPASSENTRY reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		logger.debug("Exit HandleXPassEntry: storing deferred event");
		
		return 0;
	}

	private void processMergeTapEvent(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// if it's an regular (not mobile-gxp) reader  queue a gxp request. State processing happens on reply
		if (ev.getReaderInfo().getType() == ReaderType.mobileGxp)
		{
			// for a mobile gxp  queue a gxp callback just as if gxp had returned successfully
			GxpCallback m = new GxpCallback();
			m.setBandId(getGXPBandID(ev));
			long lEntertainmentId = 0;
			try
			{
				lEntertainmentId = Long.parseLong(XBRCController.getInstance().getVenueName()); 
			}
			catch(Exception ex) {}
			//m.setEntertainmentId(lEntertainmentId);
			m.setLocationId(lEntertainmentId);
			m.setOperation(Operation.success);
			m.setDeviceId(ev.getReaderInfo().getDeviceId());
			m.setTime(new Date().getTime());
			m.setUnitType(UnitType.Merge);
			m.setSynthesized(true);
			GxpCallbackQueue.INSTANCE.add(m);
			return;
		}
		
		// send the GXP request
		try
		{
			logger.debug("In HandleMerge: queuing a gxp request for guest " + gs);						
			sendGXPTap(gs.getGuestID(), ev, ev.getReaderInfo(), ev.getReaderInfo().getName());
		}
		catch (Exception e)
		{
			logger.error("Error sending merge tap to GXP for guest " + gs + " : " + e.getLocalizedMessage() + "\n" + e.getStackTrace().toString());
		}
	}
	
	/*
	 * Handle Merge locations. Note that these locations can be either xbr or xpfe locations. xbr should handle
	 * only standby guests; xpfe should handle only xpass guests
	 */
	private int HandleMerge(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// if this is an xBR, ignore xpass guests
		if (ReaderType.isLongRangeReader(ev.getReaderInfo().getType()) && gs.getHasXpass())
			return 0;
		
		logger.debug("In HandleMerge");
		
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();

		// Validate
		switch(gs.getState())
		{
			case INDETERMINATE:
			{
				// Not clear that this should happen, but fall through below
			}
			
			case HASENTERED:
			{
				// update reader times
				gs.setTimeEarliestAtReader(ts);
				gs.setTimeLatestAtReader(ts);
				
				// send deferred message (if any)
				if (ConfigOptions.INSTANCE.getModelInfo().getDeferEntryMessages() && gs.getDeferredEntryEvent()!=null)
				{
					logger.debug("In HandleMerge: sending deferred message");
					this.SendJMSMessage("ENTRY", new MessageArgs(gs.getDeferredEntryEvent(),
                            gs.getHasXpass(),
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
					gs.setDeferredEntryEvent(null);
				}
				
				if (ReaderType.isTapReader(ev.getReaderInfo().getType()))
					processMergeTapEvent(ev,gs);
				else if (ev instanceof LRREventAggregate && !gs.getHasXpass())
				{
					// set state
					gs.setTimeMerged(ev.getTimestamp());
					
					this.SendJMSMessage("MERGE", new MessageArgs(ev,
                            gs.getHasXpass(),
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
					
					// normally, go to state HASMERGED, but if there are no readers in subsequent states, go to exit
					boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Queue, gs.getBandMediaType()) ||
													XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Load, gs.getBandMediaType()) ||
													XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.InCar, gs.getBandMediaType()) ||
													XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit, gs.getBandMediaType());

					// peg previous state
					GuestStatusState stateStart = gs.getState();

					if (bAnySubsequentReaders)
						gs.setState(GuestStatusState.HASMERGED);
					else
						gs.setState(GuestStatusState.EXITED);
					bGSTDirty = true;
					
					if (XBRCController.getInstance().isVerbose())
						logger.debug("Guest: " + gs.getGuestID() + " changed from state " + stateStart + " to " + gs.getState());								
					
				}
				
				break;
			}
			
			case HASMERGED:
				// Also need to do this in exited state since when there are no long range readers
				// the guests never really enters the HASMERGED state.
			case EXITED:
				gs.setTimeLatestAtReader(ts);
				if (ReaderType.isTapReader(ev.getReaderInfo().getType()))
					processMergeTapEvent(ev,gs);
				break;
				
			default:
			{
				// guest backtracking? ignore?
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly at MERGE reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		return 0;
	}

	// This function handles arrival at a "COMBO" location. A combo location is an xFPE that
	// behaves as both an xpassentry and merge location
	private int HandleCombo(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Integer deviceId = ev.getReaderInfo().getDeviceId();
		boolean bIsMobileGxp = ev.getReaderInfo().getType()==ReaderType.mobileGxp;
		Date ts = ev.getTimestamp();
		
		// ignore LRR readers
		if (ReaderType.isLongRangeReader(ev.getReaderInfo().getType()))
		{
			logger.warn("Long-range reader (" + sReaderName + ") in COMBO type reader location is not allowed");
			return 0;
		}

		// Validate
		switch(gs.getState())
		{
			case NEW:
			case INDETERMINATE:
			case HASENTERED:
			case HASMERGED:
			case EXITED:
			{				
				logger.debug("In HandleCombo: handling new, indeterminate or hasentered");

				if (gs.getHasXpass())
					logger.info("HandleCombo: second or later tap on reader for guest " + gs);
				
				gs.setDeferredEntryEvent(null);	
				if (gs.getLastReader() == null || !gs.getLastReader().equals(sReaderName))
					gs.setTimeEarliestAtReader(ts);				
				gs.setTimeLatestAtReader(ts);
				
				// if it's a regular (not mobile-gxp), queue a gxp request. State processing happens on reply
				if (!bIsMobileGxp)
				{
					// send the GXP entitlement request
					try
					{
						logger.debug("In HandleCombo: queuing tap for gxp");
						sendGXPTap(gs.getGuestID(), ev, ev.getReaderInfo(), sReaderName);
					}
					catch (Exception e)
					{
						logger.error("Error sending combo tap to GXP: " + e.getLocalizedMessage() + "\n" + e.getStackTrace().toString());
					}
				}
				else
				{	// for a mobile gxp, queue a gxp callback just as if gxp had returned successfully
					GxpCallback m = new GxpCallback();
					m.setBandId(getGXPBandID(ev));
					long lEntertainmentId = 0;
					try
					{
						lEntertainmentId = Long.parseLong(XBRCController.getInstance().getVenueName()); 
					}
					catch(Exception ex) {}
					//m.setEntertainmentId(lEntertainmentId);
					m.setLocationId(lEntertainmentId);
					m.setOperation(Operation.success);
					m.setDeviceId(deviceId);
					m.setTime(new Date().getTime());
					m.setUnitType(UnitType.Combo);
					m.setSynthesized(true);
					GxpCallbackQueue.INSTANCE.add(m);
				}
				
				break;
				
			}
				
			default:
			{
				// guest backtracking? ignore?
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly at MERGE reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		return 0;
	}

	private int HandleQueue(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();

		// Validate
		switch(gs.getState())
		{
			case HASENTERED:
			case HASMERGED:
			{
				// normal case
				
				// send deferred message
				if (ConfigOptions.INSTANCE.getModelInfo().getDeferEntryMessages() && gs.getDeferredEntryEvent()!=null)
				{
					this.SendJMSMessage("ENTRY", new MessageArgs(gs.getDeferredEntryEvent(),
                            gs.getHasXpass(),
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
					gs.setDeferredEntryEvent(null);
				}
				
				// if it's a tap reader, flash green
				if (ev.getReaderInfo().getType() != null && ev.getReaderInfo().getType().hasLight())
					showSuccessFeedback(sReaderName);
				
				// send JMS message but only the first time
				if (!gs.BeenThere(sReaderName))
				{
					
					gs.setTimeEarliestAtReader(ts);
                    this.SendJMSMessage("INQUEUE", new MessageArgs(ev,
                            gs.getHasXpass(),
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
					bGSTDirty = true;
				}
				gs.setTimeLatestAtReader(ts);
				break;
			}
			
			case INDETERMINATE:
			{
				// ignore
				// TODO: review for production
				gs.setTimeLatestAtReader(ts);
				break;
			}
			
			default:
			{
				// no waypoints after LOAD
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly at QUEUE reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		return 0;
	}

	private int HandleLoad(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();
		
		saveVaLocationEvents(ev,gs);

		// Validate
		switch(gs.getState())
		{
			case HASENTERED:
			case HASMERGED:
			{
				// normal case
				
				// send deferred message
				if (ConfigOptions.INSTANCE.getModelInfo().getDeferEntryMessages() && gs.getDeferredEntryEvent()!=null)
				{
					this.SendJMSMessage("ENTRY", new MessageArgs(gs.getDeferredEntryEvent(),
                            gs.getHasXpass(),
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
					gs.setDeferredEntryEvent(null);
				}
				
				gs.setTimeEarliestAtReader(ts);
				gs.setTimeLatestAtReader(ts);
				gs.setTimeLoaded(ts);
				
				// if it's a tap reader, flash green
				if (ev.getReaderInfo().getType() != null && ev.getReaderInfo().getType().hasLight())
					showSuccessFeedback(sReaderName);
				
				// normally, go to state LOADING, but if there are no readers in subsequent states, go to exit
				boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.InCar, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit, gs.getBandMediaType());

				// peg previous state
				GuestStatusState stateStart = gs.getState();

				// change to new state
				if (bAnySubsequentReaders)
					gs.setState(GuestStatusState.LOADING);
				else
					gs.setState(GuestStatusState.EXITED);
				bGSTDirty = true;

				// generate a dummy event aggregate
				// REVIEW: why do we have to do this? Why not use ev?
				String sGID = gs.getGuestID();
				EventAggregate ea = new EventAggregate(sGID, gs.getPidDecimal(), sReaderName, ts);
				
				// send the load message
				SendJMSMessage("LOAD", new LoadMessageArgs(ea,
                        gs.getHasXpass(),
                        gs.getCarId(),
                        gs.getGuestID(),
                        gs.getPidDecimal(),
                        gs.getLinkID(),
                        gs.getLinkIDType(),
                        gs.getBandType()), true, true);

				// update status
				gs.setTimeLoaded(new Date());
				
				if (XBRCController.getInstance().isVerbose())
					logger.debug("Guest: " + sGID + " changed from state " + stateStart + " to " + gs.getState());								
				break;
			}
			
			case LOADING:
			case RIDING:
			{
				// duplicate
				gs.setTimeLatestAtReader(ts);
				break;
			}
			
			case INDETERMINATE:
			{
				// ignore
				// TODO: review for production
				gs.setTimeLatestAtReader(ts);
				break;
			}
				
			default:
			{
				// guest backtracking? ignore?
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly at LOAD reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		return 0;
	}

	private int HandleInCar(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();
		
		// ignore tap reads
		// TODO: is this right?
		if (ReaderType.isTapReader(ev.getReaderInfo().getType()))
		{
			logger.warn("Tap reader (" + sReaderName + ") in InCar location is not allowed");
			return 0;
		}

		// Validate
		switch(gs.getState())
		{
			case LOADING:
			{
				// normal case
				gs.setTimeEarliestAtReader(ts);
				gs.setTimeLatestAtReader(ts);
				gs.setTimeLoaded(ts);
				
				// normally, go to state RIDING, but if there are no readers in subsequent states, go to exit
				boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit, gs.getBandMediaType());
												
				// peg previous state
				GuestStatusState stateStart = gs.getState();
				
				if (bAnySubsequentReaders)
					gs.setState(GuestStatusState.RIDING);
				else
					gs.setState(GuestStatusState.EXITED);
				bGSTDirty = true;

				this.SendJMSMessage("LOAD", new LoadMessageArgs(ev,
                        gs.getHasXpass(),
                        gs.getCarId(),
                        gs.getGuestID(),
                        gs.getPidDecimal(),
                        gs.getLinkID(),
                        gs.getLinkIDType(),
                        gs.getBandType()), true, true);
				
				if (XBRCController.getInstance().isVerbose())
					logger.debug("Guest: " + gs.getGuestID() + " changed from state " + stateStart + " to " + gs.getState());								

				break;
				
			}
			
			case INDETERMINATE:
			{
				// ignore
				// TODO: review for production
				gs.setTimeLatestAtReader(ts);
				break;
			}
			
			case RIDING:
			{
				// duplicate
				gs.setTimeLatestAtReader(ts);
				break;
			}

			case DELETABLE:
			case EXITED:
			{
				// ignore
				gs.setTimeLatestAtReader(ts);
				break;
			}
				
			default:
			{
				// guest backtracking? ignore?
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly at EXIT reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		return 0;
	}
	
	private void sendLocationTapEvent(EventsLocationConfig eventConfig, EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		if (gs.getGuest() != null)
        {
			SendJMSMessage("LOCATIONEVENT", new MessageArgs(ev, false, gs.getGuestID(), ev.getPidDecimal(),
                    gs.getLinkID(), gs.getLinkIDType(), gs.getBandType()), 
                    eventConfig == null ? false : eventConfig.isSendTapToJMS(),
                    eventConfig == null ? false : eventConfig.isSendTapToHTTP());
        }
        else
        {
            // We don't have a registered band or guest id.
            // Send a reader event anyway.
        	SendJMSMessage("LOCATIONEVENT", new MessageArgs(ev,
                    false,
                    null,
                    ev.getPidDecimal(),
                    null,
                    null,
                    null), 
                    eventConfig == null ? false : eventConfig.isSendTapToJMS(), 
                    eventConfig == null ? false : eventConfig.isSendTapToHTTP());
        }
	}
	
	
	//
	// The Waypoint location type is a place holder for AVMS vehicle reader as well as long range readers.
	//
	private int HandleWaypoint(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		saveVaLocationEvents(ev,gs);
    
		//
		// Tap event
		//
		if (ev instanceof TapEventAggregate)
		{					
			// flash light appropriately
			if (ev.getReaderInfo().getType().hasLight())
			{
				if (gs.getGuest() != null)
	                ReaderExecutor.getInstance().setReaderSequence(ev.getReaderInfo(), Sequence.success);
				else
	                ReaderExecutor.getInstance().setReaderSequence(ev.getReaderInfo(), Sequence.failure);
			}
			
			// Tap events at WAYPOINT do not alter the state of the guest or his last reader.
			return 0;
		}

        Date now = new Date();

        if (gs.getTimeEarliestAtReader() == null)
            gs.setTimeEarliestAtReader(now);

        gs.setLastReader(ev.getReaderInfo().getName());
        gs.setTimeLatestAtReader(now);
        setSignalStrengthAtReader(ev,gs);

        return 0;
	}
	
	private int HandleExit(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();

		// Validate
		switch(gs.getState())
		{
			case LOADING:
			case RIDING:
			case HASMERGED:
			{
				// normal case
				gs.setTimeEarliestAtReader(ts);
				gs.setTimeLatestAtReader(ts);
				gs.setTimeExited(ts);
				
				// if it's a tap reader, flash green
				if (ev.getReaderInfo().getType() != null && ev.getReaderInfo().getType().hasLight())
					showSuccessFeedback(sReaderName);
				
				gs.setState(GuestStatusState.EXITED);
				bGSTDirty = true;
				
				this.SendJMSMessage("EXIT", new ExitMessageArgs(ev,
                        gs.getHasXpass(),
                        gs.getCarId(),
                        gs.getSecondsWaitingToLoad(),
                        gs.getSecondsWaitingToMerge(),
                        gs.getSecondsInAttraction(),
                        gs.getGuestID(),
                        gs.getPidDecimal(),
                        gs.getLinkID(),
                        gs.getLinkIDType(),
                        gs.getBandType()), true, true);
				if (gs.getHasXpass())
					this.metXPass.AddGuest(gs.getSecondsWaitingToMerge(), gs.getSecondsWaitingToLoad(), gs.getSecondsInAttraction());
				else
					this.metStandby.AddGuest(0, gs.getSecondsWaitingToLoad(), gs.getSecondsInAttraction());
				break;
			}

			case DELETABLE:
			case EXITED:
			{
				// if it's a tap reader, flash blue (double tap)
				if (ev.getReaderInfo().getType() != null && ev.getReaderInfo().getType().hasLight())
					showErrorFeedback(sReaderName);
				
				// duplicate
				gs.setTimeLatestAtReader(ts);
				break;
			}
				
			case INDETERMINATE:
			{
				// ignore
				// TODO: review for production
				gs.setTimeLatestAtReader(ts);
				break;
			}
			
			default:
			{
				// guest backtracking? ignore?
				logger.debug("From reader: " + sReaderName);
				logger.debug("Guest:       " + ev.getID());
				logger.debug("In state:    " + gs.getState());
				logger.debug("Time:        " + ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly at EXIT reader: ignoring");
				return 0;
			}
		}
		
		// set some stats
		gs.setLastReader(sReaderName);
		setSignalStrengthAtReader(ev,gs);
		
		return 0;
	}

	private void saveVaLocationEvents(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{	
		if (!(ev instanceof LRREventAggregate))
			return;
		
		VaLocation vaLocation = vehicleAssociationLocations.get(ev.getReaderInfo().getLocation().getName());
		
		if (vaLocation == null)
			return;
		
		// Protect ourselves from bad configuration.
		int max = vaLocation.getConfig().getMaxAnalyzeGuestEvents();
		if (max < 50)
		{
			logger.error("VEHICLE: Detected bad configuration value for MaxAnalyzeGuestEvents: " + 
					max + ". Using 50 instead.");
			max = 50;
		}
		
		// Protect ourselves from someone just standing at the location for a very long time.
		// In this case remove middle third of the events. We need to keep that leading and the trailing events.
		if (gs.getEventsAtLocation().size() > max)
		{
			int count = gs.getEventsAtLocation().size() / 3;
			
			gs.getEventsAtLocation().subList(count, count * 2).clear();
			
			logger.warn("VEHICLE: Discarding middle third " + count + " events for guest " + gs.getGuestID() + " at location " + ev.getReaderInfo().getLocation().getName() + 
					    " Reached max limit of " + gs.getEventsAtLocation().size() + " events");
		}
		
		// If we just started singulating to the vehicle association array then 
		// add any pending vehicles for this guest.
		if (gs.getEventsAtLocation().isEmpty())
		{
			for (Vehicle vehicle : vaLocation.getVehiclesWaitingForGuests())
			{
				// Add this vehicle to vehicles that the guest could be associated with.
				VehicleIndex vi = new VehicleIndex(vehicle);
				vi.setEventIndex(0);
				gs.getVehicles().add(vi);
				
				logger.trace("VEHICLE: Adding earlier vehicle " + vehicle.getCompositeID() + 
						" as a candidate for guest " +  gs);				
			}
		}
		
		gs.getEventsAtLocation().add((LRREventAggregate)ev);
	}

	private int HandleOther(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();

		// if it's a tap reader, flash green
		if (ev.getReaderInfo().getType() != null && ev.getReaderInfo().getType().hasLight())
			showSuccessFeedback(sReaderName);
		
		// set some stats
		if (gs.getLastReader().compareTo(sReaderName)!=0)
		{
			gs.setTimeEarliestAtReader(ts);
			gs.setLastReader(sReaderName);
		}
		else
			gs.setTimeLatestAtReader(ts);
		
		return 0;
	}
	
	private void handleGxpCallback(GxpCallback m)
	{
		Guest guest = null;
		
		// map the xBandId back into a guest
		if (XBRCController.getInstance().useSecureId())
		{
			Xband xb = null;
			
			if (m.getBandId() != null)
				xb = IDMSResolver.INSTANCE.getBandFromSecureId(m.getBandId());
			else
				logger.error("Failed: getBandId returned null while useSecureId == true. Verify use of secureId/publicId between systems.");
			
			if (xb!=null)
				guest = IDMSResolver.INSTANCE.getGuestFromBandId(xb.getBandId());
		}
		else
		{
			if (m.getBandId() != null)
				guest = IDMSResolver.INSTANCE.getGuestFromBandId(m.getBandId());
			else
				logger.error("Failed: getBandId returned null while useSecureId == false.  Verify use of secureId/publicId between systems.");
		}
		
		if (guest == null)
		{
			logger.error("Failed to resolve bandId " + FileUtils.hideLeadingChars(m.getBandId(),4) + " to a guest");
		}
		
		String sGuestId = null;
		
		// get or synthesize a guest id 
		if (guest!=null)
			sGuestId = guest.getGuestId();
			
		GuestStatus<GuestStatusState> gs = null;
		
		if (sGuestId!=null)
			gs = GST.get(sGuestId);
		else
		{
			// look through GST for guest with given secureid
			for(GuestStatus<GuestStatusState> gst : GST.values())
			{
				if (gst.getSecureId()!=null && gst.getSecureId().equals(m.getBandId()))
				{
					gs = gst;
					break;
				}
			}
		}
		
		if (gs==null)
		{
			String secretId = FileUtils.hideLeadingChars(m.getBandId(),4);
			logger.error("Unknown bandid in Gxp callback: " + secretId);
			
			// synthesize one
			gs = new GuestStatus<GuestStatusState>();
			gs.setGuestID("?SecureID=" + secretId);
		}
		
		// If the timeStart is not set then it is an internally generated gxp event. Don't time these.
		if (m.getTimeStart() != null)
		{
			// calculate metric
			long msec = new Date().getTime() - m.getTimeStart().getTime();
			XBRCController.getInstance().getStatus().getPerfModel1().processValue((double) msec);
		}
		
		// process status'es
		switch(m.getUnitType())
		{
			case Entrance:
				handleGxpEntranceCallback(m, gs);
				break;
				
			case Merge:
				handleGxpMergeCallback(m, gs);
				break;
				
			case Combo:
				handleGxpComboCallback(m, gs);
				break;
				
			default:
				logger.error("Unknown unit type!");
		}
		
	}

	// NOTE: only tap and mobile gxp readers have the device id set
	// do not try lookup long range readers based on this id.
	private String getReaderId(Integer deviceId)
	{
		ReaderInfo ri = XBRCController.getInstance().getReaderFromDeviceId(deviceId);
		if (ri == null)
		{
			logger.error("Failed to lookup a reader based on device id = " + deviceId);
			return "";
		}
		return ri.getName();
	}
	
	private boolean waitingForGxpCallback(GxpCallback m, String readerId)
	{
		if (m.isSynthesized())
			return true;
		
		ReaderState ret = readerState.get(readerId);
		return ret != null && !ret.getAvailable() && ret.getWaitingForGxp();
	}
	
	private void handleGxpEntranceCallback(GxpCallback m, GuestStatus<GuestStatusState> gs)
	{
		String sReaderId = getReaderId(m.getDeviceId());
		
		switch(m.getOperation())
		{
			case override:
			case success:
			{				
				// play the happy feedback
				if (waitingForGxpCallback(m, sReaderId))
					showSuccessFeedback(sReaderId);
				
				// xPass set to true since we have gotten a tap at GxP entry
				gs.setHasXpass(true);
				
				// guest successfully entered the fast pass queue
				gs.setTimeEntered(new Date(m.getTime()));

				if (gs.getState() == GuestStatusState.NEW ||
					gs.getState() == GuestStatusState.INDETERMINATE)
				{
					// synthesize an event and send a message
					EventAggregate ev = new EventAggregate(gs.getGuestID(), gs.getPidDecimal(), sReaderId, gs.getTimeEntered());
					this.SendJMSMessage("ENTRY", new MessageArgs(ev,
                            true,
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
				}
	
				// normally, go to state HASENTERED, but if there are no readers in subsequent states, go to exit
				boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Merge, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Queue, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Load, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.InCar, gs.getBandMediaType()) ||
												XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit, gs.getBandMediaType());				
				
				if (bAnySubsequentReaders)
					gs.setState(GuestStatusState.HASENTERED);
				else
					gs.setState(GuestStatusState.EXITED);
				
				bGSTDirty = true;

				break;
			}
				
			case fail:
				// play the unhappy feedback
				if (waitingForGxpCallback(m, sReaderId))
					showFailureFeedback(getReaderId(m.getDeviceId()), true);
				break;

			case nextGuest:
				// turn off the light
				clearReaderLight(sReaderId);
				break;
				
				
			default:
				logger.error("Unknown gxp callback operation");
				break;
		}
	}

	private void handleGxpMergeCallback(GxpCallback m, GuestStatus<GuestStatusState> gs)
	{
		String sReaderId = getReaderId(m.getDeviceId());
		
		switch(m.getOperation())
		{
			case override:
			case success:
			{	
				// play the happy feedback
				if (waitingForGxpCallback(m, sReaderId))
					showSuccessFeedback(sReaderId);
				
				// xPass will already typically be set to true, resetting in case of only tapping at merge
				gs.setHasXpass(true);

                if (gs.getState() == GuestStatusState.NEW ||
					gs.getState() == GuestStatusState.INDETERMINATE)
				{	
					// This appears to be a guest that bypassed the Entry and is first seen at Merge.
					// Update the guest information to record an entry along with the merge.

					// Set time entered in this case to the time first seen at merge
					gs.setTimeEntered(new Date(m.getTime()));
					
					// synthesize an event and send a message
					EventAggregate ev = new EventAggregate(gs.getGuestID(), gs.getPidDecimal(), sReaderId, gs.getTimeEntered());
					this.SendJMSMessage("ENTRY", new MessageArgs(ev,
                            true,
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
				}

                // normally, go to state HASMERGED, but if there are no readers in subsequent states, go to exit
                boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Queue, gs.getBandMediaType()) ||
                        XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Load, gs.getBandMediaType()) ||
                        XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.InCar, gs.getBandMediaType()) ||
                        XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit, gs.getBandMediaType());

                // Only do these if the guest isn't already in the merged state.
                if ( (bAnySubsequentReaders && gs.getState() != GuestStatusState.HASMERGED) ||
                      (!bAnySubsequentReaders && gs.getState() != GuestStatusState.EXITED) )
                {
                    // bump the guest count on the first Gxp response
                    XBRCController.getInstance().getStatus().incrementTotalxPassGuestsSinceStart();

                    // set state
                    gs.setTimeMerged(new Date(m.getTime()));

                    // synthesize an event and send the message
                    EventAggregate ev = new EventAggregate(gs.getGuestID(), gs.getPidDecimal(), getReaderId(m.getDeviceId()), gs.getTimeMerged());
                    this.SendJMSMessage("MERGE", new MessageArgs(ev,
                            gs.getHasXpass(),
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);

                    if (bAnySubsequentReaders)
                        gs.setState(GuestStatusState.HASMERGED);
                    else
                        gs.setState(GuestStatusState.EXITED);
                }
				
				bGSTDirty = true;

				break;
			}
				
			case fail:
				// play the unhappy feedback
				if (waitingForGxpCallback(m, sReaderId))
					showFailureFeedback(getReaderId(m.getDeviceId()), true);
				break;

			case nextGuest:
				// turn off the light			
				clearReaderLight(sReaderId);
				break;
				
				
			default:
				logger.error("Unknown gxp callback operation");
				break;
		}
	}

	private void handleGxpComboCallback(GxpCallback m, GuestStatus<GuestStatusState> gs)
	{
		String sReaderId = getReaderId(m.getDeviceId());
	
		switch(m.getOperation())
		{
			case success:
			case override:
			{
				gs.setTimeEntered(new Date(m.getTime()));
				gs.setTimeMerged(new Date(m.getTime()+1));		// to not mess up any math
				
				// play the happy feedback
				if (waitingForGxpCallback(m, sReaderId))
					showSuccessFeedback(sReaderId);

                // xPass will already typically be set to true, resetting in case of only tapping at merge
                gs.setHasXpass(true);

                // normally, go to state HASMERGED, but if there are no readers in subsequent states, go to exit
                boolean bAnySubsequentReaders = XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Queue, gs.getBandMediaType()) ||
                        XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Load, gs.getBandMediaType()) ||
                        XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.InCar, gs.getBandMediaType()) ||
                        XBRCController.getInstance().isAnyReaderAtLocationType(LocationType.Exit, gs.getBandMediaType());

                // Only do these if the guest isn't already in the merged state.
                if ( (bAnySubsequentReaders && gs.getState() != GuestStatusState.HASMERGED) ||
                        (!bAnySubsequentReaders && gs.getState() != GuestStatusState.EXITED) )
                {
                    // bump the guest count on the first Gxp response
                    XBRCController.getInstance().getStatus().incrementTotalxPassGuestsSinceStart();

                    // send both messages right now
                    EventAggregate evEntry = new EventAggregate(gs.getGuestID(), gs.getPidDecimal(), getReaderId(m.getDeviceId()), gs.getTimeEntered());
                    EventAggregate evMerge = new EventAggregate(gs.getGuestID(), gs.getPidDecimal(), getReaderId(m.getDeviceId()), gs.getTimeMerged());

                    this.SendJMSMessage("ENTRY", new MessageArgs(evEntry,
                            true,
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);
                    this.SendJMSMessage("MERGE", new MessageArgs(evMerge,
                            true,
                            gs.getGuestID(),
                            gs.getPidDecimal(),
                            gs.getLinkID(),
                            gs.getLinkIDType(),
                            gs.getBandType()), true, true);

                    if (bAnySubsequentReaders)
                        gs.setState(GuestStatusState.HASMERGED);
                    else
                        gs.setState(GuestStatusState.EXITED);
                }

				bGSTDirty = true;

				break;
			}
				
			case fail:
				// play the unhappy feedback
				if (waitingForGxpCallback(m, sReaderId))
					showFailureFeedback(sReaderId, true);
				break;

			case nextGuest:
				// turn off the light				
				clearReaderLight(sReaderId);
				break;
				
			default:
				logger.error("Unknown gxp callback operation");
				break;
		}
	}

	private int
	SendJMSMessage(String sType, MessageArgs ma, boolean sendToJMS, boolean snedToHTTP)
	{
		if (XBRCController.getInstance().isVerbose())
			logger.debug("{Message: " + sType + " Time: " + ma.getTimestamp() + ", ID: " + ma.getBandID() +" }");
		
		try 
		{
			// verify we're supposed to be sending!
			HAStatusEnum haThis = HAStatusEnum.getStatus(XBRCController.getInstance().getHaStatus());
			boolean bMasterOrSolo = (haThis == HAStatusEnum.master || haThis == HAStatusEnum.solo); 
			
			if (bMasterOrSolo)
				XBRMessageGenerator.publishMessage(sType, ma, sendToJMS, snedToHTTP);
			else
				logger.error("Non master or solo xBRC sending JMS message. Suppressed!");
			
		} 
		catch (Exception e) 
		{
			logger.error("Failed to publish JMS/HTTP message", e);
		}
		
		return 0;
	}
	
	private LocationType getLocationType(EventAggregate ev)
	{
		return LocationType.getByOrdinal(ev.getReaderInfo().getLocation().getLocationTypeID());
	}
	
	private Boolean isReaderAvailable(String readerId)
	{
		ReaderState ret = readerState.get(readerId);
		if (ret == null)
		{
			ret = new ReaderState(true,null,false);
			readerState.put(readerId, ret);
		}
		return ret.getAvailable();
	}
	
	private void setReaderState(String readerId, Boolean state, long expiryTimeMs, Boolean waitingForGxp)
	{
		Date expireTime = null;
		if (expiryTimeMs != 0)
			expireTime = new Date(new Date().getTime() + expiryTimeMs);
		
		ReaderState rs = readerState.get(readerId);
		if (rs != null)
			rs.setState(state, expireTime,waitingForGxp);
		else		
			readerState.put(readerId, new ReaderState(state,expireTime,waitingForGxp));
	}

	@Override
	public void readConfiguration() 
	{
		ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();
		
		ReaderExecutor.getInstance().initialize(XBRCController.getInstance().getReaders(null));
		
		// Set the vehicle association location based on the presence of the AVMS reader.
		vehicleAssociationLocations = new HashMap<String, VaLocation>();
		
		Collection<ReaderInfo> readers = XBRCController.getInstance().getReaders(null);
		if (readers != null)
		{
			Connection conn = null;
			
			try
			{
				conn = XBRCController.getInstance().getPooledConnection();
				
				for (ReaderInfo ri : readers)
				{
					if (vehicleAssociationLocations.containsKey(ri.getLocation().getName()))
						continue;
									
					if (ri.getType() == ReaderType.vid && !ri.getLocation().getName().equals("UNKNOWN"))
					{
						VaLocation vaLocation = new VaLocation();
						vaLocation.setLocation(ri.getLocation());
						VaLocationConfig config = ConfigOptions.INSTANCE.getVaLocations().get(Long.valueOf(ri.getLocation().getId()));
						
						// Insert a new VaLocationConfig if one is not there. We must do this so that the config settings
						// will remain constant between releases of the xBRC code where the defaults may change.
						
						if (config == null)
						{
							logger.warn("Found a location with VID reader. Creating new VaLocationConfig table entry for the vehicle association location " + 
									ri.getLocation().getName());
							config = new VaLocationConfig();
							config.setLocationId(Long.valueOf(vaLocation.getLocation().getId()));
							
					    	try 
					    	{					    	    
					    	    VaLocationConfigService.save(conn, config);
					    	    ConfigOptions.INSTANCE.getVaLocations().put(config.getLocationId(), config);
							} 
					    	catch (Exception e) 
					    	{
								logger.error("Failed to insert VaLocationConfig entry for " + ri.getLocation().getName(), e);
								continue;
							}					    	
						}
						
						vaLocation.setConfig(config);
						
						vehicleAssociationLocations.put(ri.getLocation().getName(), vaLocation);
					}
				}
			}
			catch(Exception e)
			{
				logger.error("Failed to insert VaLocationConfig entry", e);
			}
			finally
			{
				if (conn!=null)
	    			XBRCController.getInstance().releasePooledConnection(conn);
			}
		}

        //Need to read stored configuration.
        eventsLocationConfigurations = new HashMap<String, EventsLocationConfig>();

        Collection<LocationInfo> locations = XBRCController.getInstance().getReaderLocations();
        if (locations != null)
        {   
        	Connection conn = null;
        	
        	try
        	{
        		conn = XBRCController.getInstance().getPooledConnection();
        		
	            for (LocationInfo locationInfo : locations)
	            {	               
	            	if (locationInfo.getName().equals("UNKNOWN"))
	            		continue;
	            	
	                EventsLocationConfig config = ConfigOptions.INSTANCE.getEventLocationConfigs().get(Long.valueOf(locationInfo.getId()));
	                if ( config == null )
	                {
	                    config = new EventsLocationConfig();
	                    config.setLocationId(Long.valueOf(locationInfo.getId()));
	                    
	                    LocationType locationType = LocationType.getByOrdinal(locationInfo.getLocationTypeID());
	                    // Set WAYPOINT location type EventConfig settings appropriately
	                    if (locationType == LocationType.Waypoint)
	                    {
	                    	config.setSendConfToJMS(true);
	                    	config.setSendTapToHTTP(true);
	                    	config.setSendTapToJMS(true);	                    	
	                    }
	                    
	                    try
	                    {
	                        // now, open up the database and see if we have anything set there
	                        EventsLocationConfigService.save(conn, config);
	                        ConfigOptions.INSTANCE.getEventLocationConfigs().put(Long.valueOf(locationInfo.getId()), config);
	                    }
	                    catch (Exception e)
	                    {
	                        logger.error("Failed to insert EventsLocationConfig entry for " + locationInfo.getName(), e);
	                        continue;
	                    }
	                }
	                
	                eventsLocationConfigurations.put(locationInfo.getName(), config);
	            }
        	} 
        	catch (Exception e) 
        	{
				logger.error("Failed to insert EventsLocationConfig entry", e);
			}
        	finally
        	{
        		 if (conn!=null)
                     XBRCController.getInstance().releasePooledConnection(conn);
        	}
        }
	}

	@Override
	public boolean isEventLogged(String bandId) 
	{
		return !ConfigOptions.INSTANCE.getCarInfo().IsCar(bandId);
	}

	@Override
	public String storeConfiguration(int cIndentLevel) throws Exception
	{
		Connection conn = null;
		try 
		{
			conn = XBRCController.getInstance().getPooledConnection();
			AttractionModelConfig modelConfig = new AttractionModelConfig();
			Map<Long,VaLocationConfig> vaLocationConfigList = VaLocationConfigService.getAllMapped(conn);
			modelConfig.setVaLocationConfigList(vaLocationConfigList.isEmpty() ? null : vaLocationConfigList.values());
            Map<Long,EventsLocationConfig> eventsLocationConfigList = EventsLocationConfigService.getAllMapped(conn);
            modelConfig.setEventsLocationConfigList(eventsLocationConfigList.isEmpty() ? null : eventsLocationConfigList.values());
			String xml = XmlUtil.convertToPartialXml(modelConfig, AttractionModelConfig.class);
			return xml;
		} catch (Exception e) {
			logger.error("Failed serialize Attraction Model configuration to xml", e);
			throw e;
		}
		finally
		{
			if (conn != null)
				XBRCController.getInstance().releasePooledConnection(conn);
		}		
	}

	@Override
	public void restoreConfiguration(Connection conn, String sXML) throws Exception
	{
		if (sXML == null)
			return;
		
		try {
			AttractionModelConfig modelConfig = XmlUtil.convertToPojo(new ByteArrayInputStream(sXML.getBytes()), AttractionModelConfig.class);
			if (modelConfig == null)
				return;
			
			if (modelConfig.getVaLocationConfigList() != null)
			{
				VaLocationConfigService.deleteAll(conn);
				for (VaLocationConfig vaConfig : modelConfig.getVaLocationConfigList())
				{
					VaLocationConfigService.save(conn, vaConfig);
				}
			}

            if (modelConfig.getEventsLocationConfigList() != null)
            {
                EventsLocationConfigService.deleteAll(conn);
                for (EventsLocationConfig eventConfig : modelConfig.getEventsLocationConfigList())
                {
                    EventsLocationConfigService.save(conn, eventConfig);
                }
            }
		}
		catch (Exception e) 		
		{
			logger.error("Failed to deserialize the attraction model stored configuration.", e);
			throw e;
		} 
	}

	@Override
	public void formatStatus(XbrcStatus status) 
	{
		String message = "";
		
		int missedVehicle = XBRCController.getInstance().getStatus().getPerfModel2().getMax().intValue();
		int missedGPIO = XBRCController.getInstance().getStatus().getPerfModel3().getMax().intValue();
		
		// Check for missing GPIO or Vehicle events within last reporting period.
		if (missedVehicle > 0)
			message = "The xBRC detected "  + missedVehicle + " missing VEHICLE event(s)";
		
		if (missedGPIO > 0)
		{
			if (message.isEmpty())
				message = "The xBRC detected "  + missedGPIO + " missing GPIO event(s)";
			else
				message = " and " + missedGPIO + " GPIO event(s)";
		}

		if (status.getStatus() == StatusType.Green && !message.isEmpty())
		{
			status.setStatus(StatusType.Yellow);
			status.setStatusMessage(message);
		}
	}

	@Override
	public void formatReaderStatus(ReaderInfo readerInfo) 
	{
	}
	
	@Override
	public void handlePropertiesRead(XbrcConfig config, Connection conn) throws Exception 
	{
		
		com.disney.xband.xbrc.attractionmodel.ConfigOptions.ModelInfo attInfo = 
				com.disney.xband.xbrc.attractionmodel.ConfigOptions.INSTANCE
				.getModelInfo();
		
		// get fresh version from the database
		Config.getInstance().read(conn, attInfo);
		
		if (config.getConfiguration() != null)
			config.getConfiguration().addAll(Configuration.convert(attInfo));
		else
			config.setConfiguration(Configuration.convert(attInfo));

	}
	
	@Override
	public void handlePropertiesWrite(XbrcConfig config, Connection conn) throws Exception 
	{
		
		com.disney.xband.xbrc.attractionmodel.ConfigOptions.ModelInfo aci =  
				com.disney.xband.xbrc.attractionmodel.ConfigOptions.INSTANCE.getModelInfo();
		
		Configuration.convert(config.getConfiguration(), aci);
		Config.getInstance().write(conn, aci);
	}

	@Override
	public void storeState(Connection conn)
	{
		// Save the GST if dirty and if enough time has elapsed
		if (bGSTDirty)
		{
			bGSTDirty = false;
			SaveGST(conn);
		}
	}

	@Override
	public void restoreState(Connection conn, Date dtLastStateStore)
	{
		// if the last store is too old, don't do anything
		long msecSpan = new Date().getTime() - dtLastStateStore.getTime();
		if (msecSpan > 30000)
		{
			logger.debug("GST not reloaded -- too old");
			return;
		}
		
		// reload the GST
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = conn.createStatement();
			stmt.execute("SELECT * from GST");
			rs = stmt.getResultSet();
			synchronized(GST)
			{
				GST.clear();
				bGSTDirty = true;
				while (rs.next())
				{
					String sGuestId = rs.getString("GuestId");
					
					boolean bxPass = rs.getBoolean("HasXPass");
					String sState = rs.getString("State");
					String sLastReader = rs.getString("LastReader");
					long lTimeEarliestAtReader = rs.getLong("TimeEarliestAtReader");
					long lTimeLatestAtReader = rs.getLong("TimeLatestAtReader");
					long lTimeEntered = rs.getLong("TimeEntered");
					long lTimeMerged = rs.getLong("TimeMerged");
					long lTimeLoaded = rs.getLong("TimeLoaded");
					long lTimeExited = rs.getLong("TimeExited");
					long lCarIDTime = rs.getLong("CarIDTime");
					String sCarId = rs.getString("CarID");
					String bandMediaType = rs.getString("BandMediaType");
					
					GuestStatus<GuestStatusState> gs = new GuestStatus<GuestStatusState>(GuestStatusState.valueOf(sState));
					gs.setGuestID(sGuestId);
					
					gs.setHasXpass(bxPass);
					gs.setLastReader(sLastReader);
					gs.setTimeEarliestAtReader(new Date(lTimeEarliestAtReader));
					gs.setTimeLatestAtReader(new Date(lTimeLatestAtReader));
					gs.setTimeEntered(new Date(lTimeEntered));
					gs.setTimeMerged(new Date(lTimeMerged));
					gs.setTimeLoaded(new Date(lTimeLoaded));
					gs.setTimeExited(new Date(lTimeExited));
					gs.setBandType(rs.getString("BandType"));
					gs.setCarIdTime(lCarIDTime == Long.MIN_VALUE ? null : new Date(rs.getLong("CarIDTime")));
					gs.setPidDecimal(rs.getString("PidDecimal"));
					gs.setLinkID(rs.getString("LinkID"));
					gs.setLinkIDType(rs.getString("LinkIDType"));
					gs.setBandMediaType(bandMediaType == null || bandMediaType.isEmpty() ? BandMediaType.Unknown : BandMediaType.valueOf(bandMediaType));
					gs.setBandTransmitting(rs.getBoolean("BandTransmitting"));
					gs.setCarId(sCarId);
					
					// look up the Guest
					Guest g = IDMSResolver.INSTANCE.getGuestFromGuestId(sGuestId);
					gs.setGuest(g);
					
					GST.put(sGuestId, gs);
				}
			}
		}
		catch(Exception ex)
		{
			logger.error("Error restoring state from GST", ex);
		}
		finally
		{
			try
			{
				if (rs!=null)
					rs.close();
				if (stmt!=null)
					stmt.close();
			}
			catch(Exception ex){}
		}
		
	}

	@Override
	public void processExternal(Request request, Response response, String sPath)
	{
		if (!sPath.startsWith("/model/gxp/"))
				return404(response);

        InputStream is = null;
		try
		{
			// parse out the reader id
			String sDeviceId = sPath.substring("/model/gxp/".length());

			// get the payload
			is = request.getInputStream();
			String sPutData = new Scanner(is).useDelimiter("\\A").next();

			logger.trace("got POST  /model/gxp/" + sDeviceId);
			logger.trace(sPutData);

			// parse json object
			logger.trace("Deserializing payload");
			JSONObject jo = null;
			JSONObject joSC = null;
			try
			{
				jo = JSONObject.fromObject(sPutData);
				joSC = jo.getJSONObject("stateChange");

				// fetch fields
				long lTime = joSC.getLong("timestamp");
				long lBandId = joSC.getLong("xBandId");
				//long lEntertainmentId = joSC.getLong("entertainmentId");
				long lLocationId = joSC.getLong("location");
				String sUnitType = joSC.getString("unitType");
				String sGrantAccess = joSC.getString("grantAccess");
				String sReaderId = joSC.getString("readerId");

				// queue up a message
				boolean bSuccess = Boolean.parseBoolean(sGrantAccess);
				GxpCallback m = new GxpCallback();
				m.setBandId(Long.toString(lBandId));
				//m.setEntertainmentId(lEntertainmentId);
				m.setLocationId(lLocationId);
				m.setTime(lTime);
				m.setDeviceId(Integer.parseInt(sReaderId));
				m.setUnitType(GxpCallback.UnitType.valueOf(sUnitType));
				m.setOperation(bSuccess ? Operation.override : Operation.nextGuest);
				
				// m.setUnitType(unitType);
				// m.setOperation(operation);
				m.setSynthesized(true);
				GxpCallbackQueue.INSTANCE.add(m);
			}
			catch (Exception ex)
			{
				logger.error("Error deserializing POST /model/gxp message: " + sPutData,
						ex);
				return500(response, ExceptionFormatter.format(
						"Error deserializing POST /model/gxp message", ex));
				return;
			}

			return200(response);
		}
		catch (IOException e)
		{
			logger.error("Error handling POST /light", e);
			return500(response, e.getLocalizedMessage());
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
	
	private void return200(Response response)
	{
        PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(200);
			body = response.getPrintStream();
		}
		catch (IOException e)
		{
			logger.error("Error sending 200 response", e);
		}
        finally {
            if (body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private void return404(Response response)
	{
        PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(404);
			body = response.getPrintStream();
			body.println("Error 404");
			body.close();
		}
		catch (IOException e)
		{
			logger.error("Error sending 404 response", e);
		}
        finally {
            if (body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
	
	private void return500(Response response, String sError)
	{
        PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(500);
			body = response.getPrintStream();
			body.println(sError);
			body.close();
		}
		catch (IOException e)
		{
			logger.error("Error sending 500 response", e);
		}
        finally {
            if (body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private void setResponseHeader(Response response, String sContentType)
	{
		long time = System.currentTimeMillis();

		response.set("Content-Type", sContentType);
		response.set("Server", "xBRC/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
	}

	@Override
	public String getRequiredSchemaVersion()
	{
		return "1.0.0.30";
	}

	@Override
	public List<String> getBandsPresentAtLocations(Set<Long> locationIds)
	{
		if (locationIds == null || locationIds.size() == 0)
			return null;
		
		List<String> guests = new LinkedList<String>();
		
		for (GuestStatus<GuestStatusState> gs : GST.values())
		{
			if (gs.getLastReader() == null)
				continue;
			ReaderInfo ri = XBRCController.getInstance().getReader(gs.getLastReader());
			if (ri == null)
				continue;
			
			if (locationIds.contains(Long.valueOf(ri.getLocation().getId())))
			{
				Guest g = IDMSResolver.INSTANCE.getGuestFromGuestId(gs.getGuestID());
				if (g == null)
				{
					// No guest, but try a long range ID set on the GST object.
					if (gs.getLRID() != null && !gs.getLRID().isEmpty())
						guests.add(gs.getLRID());
					continue;
				}
				
				for(Xband xb: g.getXbands())
				{
					if (xb.getLRId() != null && !xb.getLRId().isEmpty())
						guests.add(xb.getLRId());
				}
			}
		}
		
		return guests;
	}

	@Override
	public void processHAMessages(HAMessage[] aHA)
	{
		for(HAMessage ham : aHA)
		{
			String messageType = null;
			String messageText = null;
			try
			{
				messageType = ham.getMessageType();
				messageText = ham.getMessageText();
				
				ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));
						
		    	if (messageType.equals("ENTRY"))
		    	{
		    		processEntryHAMessage(bais);
		    	}
		    	else if (messageType.equals("MERGE"))
	            {
		    		processMergeHAMessage(bais);
	 	        }
		        else if(messageType.equals("LOAD"))
		        {
		    		processLoadHAMessage(bais);
		        }
		        else if(messageType.equals("EXIT"))
		        {
		    		processExitHAMessage(bais);
		        }
		        else if(messageType.equals("ABANDON"))
		        {
		    		processAbandonHAMessage(bais);
		        }
		        else 
		        {
		        	// ignore other messages
		            continue;
		        }
			}
			catch(Exception ex)
			{
				logger.error("Error processing HA message", ex);
				if (messageType!=null)
					logger.error("HA message type: " + messageType);
				if (messageText!=null)
					logger.error("HA message text: " + messageText);
				
			}
		}
		
	}

	private void processEntryHAMessage(ByteArrayInputStream bais) throws JAXBException
	{
		EventMessagePayload emp = XmlUtil.convertToPojo(bais, MessagePayload.class, EventMessagePayload.class);
		EventMessage em = emp.getMessage();
		
		// if don't have a guestId, synthesize one
		String sGuestID;
		if (em.getGuestId()==null)
		{
			sGuestID = "?PublicID=" + em.getPidDecimal();
		}
		else
		{
			sGuestID = em.getGuestId().toString();
		}
		
		logger.info("Got HA ENTRY message for guest id: " + sGuestID);
		
		Date dtMessage = DateUtils.parseDate(em.getTimestamp());
		
		String sLinkID = em.getLinkId();
		String sLinkIDType = em.getLinkIdType();
		GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
		if (gs==null)
		{
			String sBandType = em.getBandtype();
			gs = addGuestToGST(sGuestID, sLinkID, sLinkIDType, sBandType);
		}
		
		// set status
		gs.setPidDecimal(em.getPidDecimal());
		gs.setTimeEarliestAtReader(dtMessage);
		gs.setTimeLatestAtReader(dtMessage);
		gs.setState(GuestStatusState.HASENTERED);
		gs.setTimeEntered(dtMessage);
		gs.setHasXpass(em.getxPass());
		bGSTDirty = true;
	}
	
	private void processAbandonHAMessage(ByteArrayInputStream bais) throws JAXBException
	{
		AbandonMessagePayload emp = XmlUtil.convertToPojo(bais, MessagePayload.class, AbandonMessagePayload.class);
		AbandonMessage em = emp.getMessage();
		
		// if don't have a guestId, synthesize one
		String sGuestID;
		if (em.getGuestId()==null)
		{
			sGuestID = "?PublicID=" + em.getPidDecimal();
		}
		else
		{
			sGuestID = em.getGuestId().toString();
		}
		
		logger.info("Got HA ABANDON message for guest id: " + sGuestID);
		
		GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
		if (gs!=null)
		{
			gs.setState(GuestStatusState.DELETABLE);
			bGSTDirty = true;
		}
		
	}

	private void processExitHAMessage(ByteArrayInputStream bais) throws JAXBException
	{
		ExitMessagePayload emp = XmlUtil.convertToPojo(bais, MessagePayload.class, ExitMessagePayload.class);
		ExitMessage em = emp.getMessage();
		
		// if don't have a guestId, synthesize one
		String sGuestID;
		if (em.getGuestId()==null)
		{
			sGuestID = "?PublicID=" + em.getPidDecimal();
		}
		else
		{
			sGuestID = em.getGuestId().toString();
		}
		
		logger.info("Got HA EXIT message for guest id: " + sGuestID);

		Date dtMessage = DateUtils.parseDate(em.getTimestamp());
		
		String sLinkID = em.getLinkId();
		String sLinkIDType = em.getLinkIdType();
		GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
		if (gs==null)
		{
			String sBandType = em.getBandtype();
			gs = addGuestToGST(sGuestID, sLinkID, sLinkIDType, sBandType);
			gs.setTimeEntered(dtMessage);
			gs.setHasXpass(em.getxPass());
		}
		
		// set status
        gs.setPidDecimal(em.getPidDecimal());
		gs.setTimeEarliestAtReader(dtMessage);
		gs.setTimeLatestAtReader(dtMessage);
		gs.setState(GuestStatusState.EXITED);
		gs.setTimeExited(dtMessage);
		gs.setCarId(em.getCarId());
		bGSTDirty = true;
		
	}

	private void processLoadHAMessage(ByteArrayInputStream bais) throws JAXBException
	{
		LoadMessagePayload emp = XmlUtil.convertToPojo(bais, MessagePayload.class, LoadMessagePayload.class);
		LoadMessage em = emp.getMessage();
		
		// if don't have a guestId, synthesize one
		String sGuestID;
		if (em.getGuestId()==null)
		{
			sGuestID = "?PublicID=" + em.getPidDecimal();
		}
		else
		{
			sGuestID = em.getGuestId().toString();
		}
		
		logger.info("Got HA LOAD message for guest id: " + sGuestID);

		Date dtMessage = DateUtils.parseDate(em.getTimestamp());
		
		String sLinkID = em.getLinkId();
		String sLinkIDType = em.getLinkIdType();
		GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
		if (gs==null)
		{
			String sBandType = em.getBandtype();
			gs = addGuestToGST(sGuestID, sLinkID, sLinkIDType, sBandType);
			gs.setTimeEntered(dtMessage);
			gs.setHasXpass(em.getxPass());
		}
		
		// set status
        gs.setPidDecimal(em.getPidDecimal());
		gs.setTimeEarliestAtReader(dtMessage);
		gs.setTimeLatestAtReader(dtMessage);
		gs.setState(GuestStatusState.LOADING);
		gs.setTimeLoaded(dtMessage);
		gs.setCarId(em.getCarId());
		bGSTDirty = true;
	}

	private void processMergeHAMessage(ByteArrayInputStream bais) throws JAXBException
	{
		EventMessagePayload emp = XmlUtil.convertToPojo(bais, MessagePayload.class, EventMessagePayload.class);
		EventMessage em = emp.getMessage();
		
		// if don't have a guestId, synthesize one
		String sGuestID;
		if (em.getGuestId()==null)
		{
			sGuestID = "?PublicID=" + em.getPidDecimal();
		}
		else
		{
			sGuestID = em.getGuestId().toString();
		}
		
		logger.info("Got HA MERGE message for guest id: " + sGuestID);
		
		Date dtMessage = DateUtils.parseDate(em.getTimestamp());
		
		String sLinkID = em.getLinkId();
		String sLinkIDType = em.getLinkIdType();
		GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
		if (gs==null)
		{
			String sBandType = em.getBandtype();
			gs = addGuestToGST(sGuestID, sLinkID, sLinkIDType, sBandType);
			gs.setTimeEntered(dtMessage);
			gs.setHasXpass(em.getxPass());
		}
		
		// set status
        gs.setPidDecimal(em.getPidDecimal());
		gs.setTimeEarliestAtReader(dtMessage);
		gs.setTimeLatestAtReader(dtMessage);
		gs.setState(GuestStatusState.HASMERGED);
		gs.setTimeMerged(dtMessage);
		bGSTDirty = true;
	}

	private GuestStatus<GuestStatusState> addGuestToGST(String sGuestID, String LinkID, String LinkIDType, String sBandType)
	{
		// nope, got to add the guest
		GuestStatus<GuestStatusState> gs = new GuestStatus<GuestStatusState>(GuestStatusState.INDETERMINATE);
		Guest guest = IDMSResolver.INSTANCE.getGuestFromGuestId(sGuestID);
		gs.setGuestID(sGuestID);
		gs.setLinkID(LinkID);
		gs.setLinkIDType(LinkIDType);
		gs.setGuest(guest);
		
		// need to set some times here so that we can time out INDETERMINATE guests
		Date now = new Date();
		gs.setTimeEarliestAtReader(now);
		gs.setTimeLatestAtReader(now);
        gs.setBandType(sBandType);
                
        logger.debug("HA: Adding guest to the GST: " + gs.getGuestID());
		synchronized(GST)
		{
			GST.put(sGuestID, gs);
			bGSTDirty = true;
		}
		
		return gs;
	}
	
	private void onMissedVehicleEvent(VaLocation vaLocation)
	{		
		XBRCController.getInstance().getStatus().getPerfModel2().processValue(
				XBRCController.getInstance().getStatus().getPerfModel2().getMax() + 1.0);
		vaLocation.setMissedVehicleEvents(vaLocation.getMissedVehicleEvents() + 1);
		logger.warn("VEHICLE: Missed VEHICLE event at location " + vaLocation.getLocation().getName() + 
					". Total VEHICLE events missing for this location since xBRC start time: " + vaLocation.getMissedVehicleEvents());
	}
	
	private void onMissedGpioEvent(VaLocation vaLocation, int howMany)
	{		
		XBRCController.getInstance().getStatus().getPerfModel3().processValue(
				XBRCController.getInstance().getStatus().getPerfModel3().getMax() + howMany);
		vaLocation.setMissedGPIOEvents(vaLocation.getMissedGPIOEvents() + howMany);
		logger.warn("VEHICLE: Missed " + howMany + " GPIO event(s) at location " + vaLocation.getLocation().getName() + 
				". Total GPIO events missing for this location since xBRC start time: " + vaLocation.getMissedGPIOEvents());
	}

	@Override
	public String serializeStateToXML(String sGuestID)
	{
		try
		{
			synchronized(GST)
			{
				
				if (sGuestID!=null)
				{
					GSTInfo gsti = new GSTInfo();
					gsti.name = XBRCController.getInstance().getVenueName();
					gsti.time = DateUtils.format(new Date().getTime());
					gsti.guests = new ArrayList<GuestStatus<GuestStatusState>>();
					GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
					
					// skip NEW or INDETERMINATE guests
					if (	gs != null &&
							gs.getState() != GuestStatusState.NEW &&
							gs.getState() != GuestStatusState.INDETERMINATE)
					{
						GSTLocationInfo gli = new GSTLocationInfo();
						ReaderInfo ri = XBRCController.getInstance().getReader(gs.getLastReader());
						gli.name = ri.getLocation().getName();
						gli.id = ri.getLocation().getId();
						gli.arrived = DateUtils.format(gs.getTimeEarliestAtReader().getTime());
						gli.latest = DateUtils.format(gs.getTimeLatestAtReader().getTime());
						gs.setGSTLocationInfo(gli);
						gsti.guests.add(gs);
					}
					
					String sOne = XmlUtil.convertToPartialXml(gsti, gsti.getClass());
					
					// clear so that there's no confusion later
					if (gs != null)
						gs.setGSTLocationInfo(null);
					
					return sOne;
				}
				else
				{
					GSTInfo gsti = new GSTInfo();
					gsti.name = XBRCController.getInstance().getVenueName();
					gsti.time = DateUtils.format(new Date().getTime());
					gsti.guests = new ArrayList<GuestStatus<GuestStatusState>>();
					
					for(String sID : GST.keySet())
					{
						GuestStatus<GuestStatusState> gs = GST.get(sID);
						
						// skip NEW or INDETERMINATE guests
						if (	gs.getState() == GuestStatusState.NEW ||
								gs.getState() == GuestStatusState.INDETERMINATE)
							continue;
						
						GSTLocationInfo gli = new GSTLocationInfo();
						ReaderInfo ri = null;
						if (gs.getLastReader()!=null)
							ri = XBRCController.getInstance().getReader(gs.getLastReader());
						if (ri!=null)
						{
							gli.name = ri.getLocation().getName();
							gli.id = ri.getLocation().getId();
							gli.arrived = DateUtils.format(gs.getTimeEarliestAtReader().getTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
							gli.latest = DateUtils.format(gs.getTimeLatestAtReader().getTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
						}
						else
						{
							// TODO: not quite sure what to do here. Can we just not set the location info?
							gli.name="";
							gli.id = 0;
							gli.arrived = gli.latest = "";
						}
						gs.setGSTLocationInfo(gli);
						gsti.guests.add(gs);
					}
					
					String sAll = XmlUtil.convertToPartialXml(gsti, gsti.getClass());
					
					// reset to avoid confusion
					for(String sID : GST.keySet())
					{
						GuestStatus<GuestStatusState> gs = GST.get(sID);
						gs.setGSTLocationInfo(null);
					}
					
					return sAll;
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error serializing state to XML", e);
			return null;
		}
	}

	@Override
	public void deserializeStateFromXML(String sXML)
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(sXML.getBytes());
		
		try
		{
			GSTInfo gsti = XmlUtil.convertToPojo(bais, GSTInfo.class);
			
			// now, process the data
			if (gsti.guests != null && gsti.guests.size()>0)
			{
				logger.warn("Synchronizing " + gsti.guests.size() + " guests from master");
				for(GuestStatus<GuestStatusState> gsMaster : gsti.guests)
				{
					// merge gs into the GST
					String sGuestID = gsMaster.getGuestID();
					GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
					if (gs == null)
					{
						String sBandType = gsMaster.getBandType();
						String sLinkID = gsMaster.getLinkID();
						String sLinkIDType = gsMaster.getLinkIDType();
						gs = addGuestToGST(sGuestID, sLinkID, sLinkIDType, sBandType);
					}
					
	                gs.setPidDecimal(gsMaster.getPidDecimal());
	                gs.setSecureId(gsMaster.getSecureId());
					gs.setHasXpass(gsMaster.getHasXpass());
					gs.setState(gsMaster.getState());
					gs.setBandType(gsMaster.getBandType());
					
					GSTLocationInfo gli = gsMaster.getGSTLocationInfo();
					gs.setLastReader(gli.name);
					gs.setTimeEarliestAtReader(DateUtils.parseDate(gli.arrived));
					gs.setTimeLatestAtReader(DateUtils.parseDate(gli.latest));
					
				}
				logger.warn("GST now contains: " + GST.size() + " guests");
			}
		}
		catch (Exception e)
		{
			logger.error("Error deserializing from XML", e);
			if (sXML!=null)
				logger.error("XML: " + sXML);
		}
			
	}

	@Override
	public int getIDMSResolverThreads()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTapSequenceEnabled(ReaderInfo ri)
	{
		return true;
	}
	
	@Override
	public boolean isIdleSequenceEnabled(ReaderInfo ri)
	{
		return true;
	}

	@Override
	public void beforeConfigurationDeleteFromTable(Connection conn,
			String tableName, Collection<Object> ids) throws Exception
	{
		Statement stmt = null;
		try
		{	
			if (tableName.equals("Location"))
			{
				stmt = conn.createStatement();
				if (ids == null || ids.isEmpty())
				{
					stmt.execute("DELETE FROM VaLocationConfig");
				}
				else
				{
					for (Object id : ids)
					{
						stmt.execute("DELETE FROM VaLocationConfig where locationId = "
								+ (Long) id);
					}
				}
			}
		}
		finally
		{
			if (stmt != null)
				stmt.close();
		}
		
	}

	@Override
	public boolean isWaypointLocationType(LocationInfo li) {
		return LocationType.getByOrdinal(li.getLocationTypeID()) == LocationType.Waypoint;
	}
	
	@Override
	/**
	 * Flattens the model section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <venue name="iwona" time="2013-05-01T22:22:16.298">
	 * 		<configuration name="current" type="full">
	 * 			<model>
	 * 				<attractionModelConfig>
	 * 					<vaLocationConfig>
	 * 						<carsPerTrain>0</carsPerTrain>
	 * 						<laserBreaksAfterVehicle>0</laserBreaksAfterVehicle>
	 * 						<laserBreaksBeforeVehicle>0</laserBreaksBeforeVehicle>
	 * 						<locationId>1</locationId>
	 * 						...
	 * 					</vaLocationConfig>
	 * 					...
	 * 				</attractionModelConfig>
	 *			</model>
	 * 		</configuration>
	 * </venue>
	 * 
	 * @param rootNode the <CODE>model</CODE> xml element.
	 * @param results a map of resulting key/value pairs
	 */
	public void flattenModel(org.jdom.Element model, Map<String, String> results, Set<String> exclude, char pathSeparator)
	{
		if (model == null)
		{
			logger.warn("Element 'model' not found in the xml provided.");
			return;
		}
		
		Element modelConfig = model.getChild("attractionModelConfig");
		if (modelConfig == null)
			return;

		final String path = model.getParentElement().getName() + pathSeparator 
				+ model.getName() + pathSeparator
				+ "attractionModelConfig" + pathSeparator;

		final List vaLocationConfigs = modelConfig.getChildren("vaLocationConfig");
		if (vaLocationConfigs == null)
			return;
		
		Element vaLocationConfig = null;
		String vaLocationConfigId = null;
		StringBuffer vaLocationConfigPath = null;
		Element vaLocationConfigElement = null;
		List vaLocationConfigChildElements = null;

		for (int i = 0; i < vaLocationConfigs.size(); i++) 
		{
			vaLocationConfigPath = new StringBuffer(path);

			vaLocationConfig = (Element)vaLocationConfigs.get(i);
			vaLocationConfigId = vaLocationConfig.getChildText("locationId");
			vaLocationConfigChildElements = vaLocationConfig.getChildren();

			vaLocationConfigPath.append(vaLocationConfig.getName()).append(pathSeparator)
				.append("locationId").append(pathSeparator)
				.append(vaLocationConfigId).append(pathSeparator);

			for (int j = 0; j < vaLocationConfigChildElements.size(); j++)
			{
				vaLocationConfigElement = (Element)vaLocationConfigChildElements.get(j);

				if (exclude != null && exclude.contains(vaLocationConfigElement.getName()))
					continue;

				results.put(vaLocationConfigPath.toString() + vaLocationConfigElement.getName(), vaLocationConfigElement.getValue());
			}
		}
	}

	@Override
	public void registerSchedulerItemsMetadata(XconnectScheduler scheduler)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDefaultSchedulerItems(XconnectScheduler scheduler)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdown() {		
	}
}
