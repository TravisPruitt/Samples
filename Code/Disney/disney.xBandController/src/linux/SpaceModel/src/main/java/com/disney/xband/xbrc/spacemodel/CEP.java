package com.disney.xband.xbrc.spacemodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.jms.lib.entity.xbrc.AbandonMessage;
import com.disney.xband.jms.lib.entity.xbrc.AbandonMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.ReaderEventMessage;
import com.disney.xband.jms.lib.entity.xbrc.ReaderEventMessagePayload;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.lib.xbrapi.ReaderExecutor;
import com.disney.xband.lib.xbrapi.Sequence;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.lib.xbrapi.XbrJsonMapper;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.entity.HAMessage;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.idms.IDMSResolver;
import com.disney.xband.xbrc.lib.model.AbandonMessageArgs;
import com.disney.xband.xbrc.lib.model.ConfidenceMessageArgs;
import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.IXBRCModel;
import com.disney.xband.xbrc.lib.model.LRREventAggregate;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.MessageArgs;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.TapEventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.GuestIdentifier;
import com.disney.xband.xview.lib.model.Xband;

public class CEP implements IXBRCModel
{
	// logger
	private static Logger logger = Logger.getLogger(CEP.class);
	
	// the guest status table is indexed by Guest ID
	private Hashtable<String, GuestStatus<GuestStatusState>> GST = new Hashtable<String, GuestStatus<GuestStatusState>>();
	private boolean dirtyGST = false;

	public CEP()
	{
	}

	@Override
	public void initialize()
	{
	}
	
	public void clear()
	{
		synchronized(GST)
		{
			GST.clear();
			dirtyGST = true;
		}
	}

	public int SaveGST(Connection conn)
	{
		Statement stmt = null;
		PreparedStatement pstmt = null;

		boolean bOldAutoCommit = true;
		logger.trace("Saving GST");
		try
		{
			bOldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			String sSQL = "DELETE FROM GST";
			stmt = conn.createStatement();
			stmt.execute(sSQL);

			if (GST.size() == 0)
			{
				conn.commit();
				conn.setAutoCommit(bOldAutoCommit);
				dirtyGST = false;
				return 0;
			}

			sSQL = "INSERT INTO GST(GuestId, HasXPass, State, LastReader, TimeEarliestAtReader, TimeLatestAtReader, TimeEntered, TimeMerged, TimeLoaded, " + 
					"TimeExited, HasDeferredEntry, PidDecimal, LinkID, LinkIDType, BandType, BandTransmitting, CarID) "
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sSQL);
			for (String sID : GST.keySet())
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
				pstmt.setBoolean(11, gs.getDeferredEntryEvent() != null);
				pstmt.setString(12, gs.getPidDecimal());
				pstmt.setString(13, gs.getLinkID());
				pstmt.setString(14, gs.getLinkIDType());
				pstmt.setString(15, gs.getBandType());
				pstmt.setBoolean(16, gs.isBandTransmitting());
				pstmt.setString(17, "");
				pstmt.execute();
			}

			conn.commit();
			conn.setAutoCommit(bOldAutoCommit);
			dirtyGST = false;

			return 0;
		}
		catch (SQLException e)
		{
			logger.error("!! Error writing GST to database: " + e);

			try
			{
				conn.setAutoCommit(bOldAutoCommit);
			}
			catch (SQLException e2)
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
			catch (Exception e)
			{
			}
		}
	}

	@Override
	public void beforeProcessEvents() throws Exception
	{
		// first, prune any old guests
		for (Iterator<String> it = GST.keySet().iterator(); it.hasNext();)
		{
			String sID = it.next();

			if (GST.get(sID).getState() == GuestStatusState.DELETABLE)
			{
				it.remove();
				dirtyGST = true;
			}
		}
	}

	@Override
	public void processEvents(List<XbrEvent> liEvents) throws Exception
	{
		EventAggregator ea = new EventAggregator(XBRCController.getInstance(), this);
		
		List<EventAggregate> li = null;
		try
		{
			logger.debug("Calling first pass singulation");
			Date dtStart = new Date();
			li = Reduce(ea.Analyze(liEvents));
			Date dtEnd = new Date();
			logger.debug("Returned from first pass singulation");
			
			long msec = dtEnd.getTime() - dtStart.getTime();
			double msecPerEvent = (double)msec / liEvents.size();
			XBRCController.getInstance().getStatus().getPerfSingulationMsec().processValue(msecPerEvent);
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Exception during 1st stage of singulation", ex));
		}
		logger.trace("Ending 1st stage of singulation");
		
		// now process the events
		for (EventAggregate ev : li)
		{
			// skip event types that we don't understand
			if (!(ev instanceof TapEventAggregate)
					&& !(ev instanceof LRREventAggregate))
				continue;

			// get RF or LR id
			String sID = ev.getID();
			boolean useSecureID = ev.getReaderInfo().isUseSecureId();
			String sSID = null;

			if (useSecureID && (ev instanceof TapEventAggregate))
			{
				sSID = ((TapEventAggregate)ev).getTapEvent().getSid();
				if (sSID == null || sSID.isEmpty())
				{
					logger.warn("Band with UID " + ev.getID() + " did not produce a secure id. Ignoring tap.");
					continue;
				}
			}
			
			if (ev instanceof TapEventAggregate)
			{
			}
			else if (ev instanceof LRREventAggregate)
			{
			}
			else
			{
				logger.debug("Band with UID " + ev.getID() + " does not map to a tap or llr. Igorning.");
				continue;
			}
			
			// map to a guest
			Guest guest = null;
			String sGuestID = null;
			String sLinkID = null;
			String sLinkIDType = null;
			String sBandID = null;
			String sBandType = null;
			boolean bIsTap = ReaderType.isTapReader(ev.getReaderInfo().getType());
			boolean bIsMobileGxp = ev.getReaderInfo().getType()==ReaderType.mobileGxp;
			Xband xb = null;
			if (bIsTap)
			{
				if(useSecureID)
				{
					xb = IDMSResolver.INSTANCE.getBandFromSecureId(sSID);
				}
				else
				{
					xb = IDMSResolver.INSTANCE.getBandFromRFID(sID);
				}
			}
			else
				xb = IDMSResolver.INSTANCE.getBandFromLRID(sID);
			if (xb!=null)
				sBandID = xb.getBandId();
			
			if (sBandID != null)
			{
				// if the band is not active, ignore the message. Disable this
				// for debug.
				if (!xb.getActive())
                {
                    logger.warn("Band has been disabled for: sBandID=" + sBandID);
					continue;
                }				

				// TODO: can probably get this from the event aggregate
				guest = IDMSResolver.INSTANCE.getGuestFromBandId(sBandID);
				
				if (bIsTap && processNonGuestBands(ev, xb, guest))
					continue;
				
				GuestIdentifier gi = null;
				if (guest != null)
                {
					sGuestID = guest.getGuestId();
					gi = Guest.getPreferredGuestIdentifier(guest, XBRCController.getInstance().getGuestIdentifierTypes());
                }
				else
				{
					// leave sGuestId null
                    logger.warn("No guest found for band from IDMS: sBandId=" + sBandID);
                }
				
				// Store the global guest identifier for the guest into the guest status entry
				if (gi != null)
				{
					sLinkID = gi.getValue();
					sLinkIDType = gi.getType();
				}
				else
				{
					sLinkID = "";				
					sLinkIDType = "xbms-link-id";
				}
				
				sBandType = getGuestPrimaryBandType(guest, sBandID);
			}
			else
			{
				// leave sBandId null
				
				// leave sBandType null
			}

			// if it's a tap event, just generate LOCATIONEVENT now
			if (bIsTap)
			{
				if (sBandID!=null)
                {
					SendJMSMessage("LOCATIONEVENT",
                            new TapMessageArgs(ev, false, sGuestID, ev.getPidDecimal(), sLinkID, sLinkIDType, sBandType));
                }
                else
                {
                    if ( ConfigOptions.INSTANCE.getControllerInfo().getSendUnregisteredBandEvents() )
                    {
                        // We don't have a registered band or guest id.
                        // Send a reader event anyway.
                    	SendJMSMessage("LOCATIONEVENT",
                                new TapMessageArgs(ev, false, null, ev.getPidDecimal(), null, null, null));
                    }
                }

				// flash light appropriately
				if (!bIsMobileGxp)
				{
					if (sBandID != null && sGuestID != null)
                        ReaderExecutor.getInstance().setReaderSequence(ev.getReaderInfo(), Sequence.success);
					else
                        ReaderExecutor.getInstance().setReaderSequence(ev.getReaderInfo(), Sequence.failure);
				}
			}
			else
			{
				// ignore unmapped bands
				if (sGuestID == null)
                    continue;

				// do we have the guest in the table
				GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
				if (gs == null)
				{
					// if it's an entry event, add the guest
					// nope, got to add the guest
					gs = new GuestStatus<GuestStatusState>(GuestStatusState.INDETERMINATE);
					gs.setGuestID(sGuestID);
					gs.setGuest(guest);
					gs.setLinkID(sLinkID);
					gs.setLinkIDType(sLinkIDType);
                                        gs.setBandType(sBandType);
					// need to set some times here so that we can time out INDETERMINATE guests
					Date now = new Date();
					gs.setTimeEarliestAtReader(now);
					gs.setTimeLatestAtReader(now);

                    // Set the guest state to use the queue model if configured.
                    // Hold only enough events to allow for the configured chirp rate
                    // and the guest type.
                    if ( ConfigOptions.INSTANCE.getControllerInfo().getUseQueue() ) {
                        gs.ToQueue( (int)( gs.getRoleDetectDelay() /
                                ConfigOptions.INSTANCE.getControllerInfo().getChirpRate() ) );
                    }
                    
                    gs.setPidDecimal(ev.getPidDecimal());

					GST.put(sGuestID, gs);
					dirtyGST = true;
				}

                // Add the event to the guest status.
                gs.addEAEvent(ev);
                
                // Always set a timestamp for abandonment events.
				gs.setTimeLatestAtReader(ev.getTimestamp());
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

    /**
     * Given a total list of event aggregates, this function will remove any very
     * old events once it gets behind. This is done by removing events that, for
     * all intensive purposes, are duplicates and can be safely pruned.
     * @param eventAggregates List of all event aggregates.
     * @return Reduced list of aggregates.
     */
    private List<EventAggregate> Reduce(List<EventAggregate> eventAggregates)
    {
        // Determine if we even need to reduce the list.
        long maxEventAge = ConfigOptions.INSTANCE.getControllerInfo().getMaxAgeEvents();
        if ( maxEventAge == 0 )
        {
            return eventAggregates;
        }

        List<EventAggregate> reducedList = new ArrayList<EventAggregate>();
        Date now = new Date();
        Date minimumDate = new Date(now.getTime() - maxEventAge);

        // Get the oldest event and see how old it is. Compare it against the oldest
        // configured event time we allow.
        for ( EventAggregate event : eventAggregates )
        {
            boolean valid = false;

            // Only include it if it's within our time window.
            // Always include taps.
            if ( event instanceof TapEventAggregate ||
                event.getTimestamp().compareTo(minimumDate) >= 0 )
            {
                valid = true;
            }

            // TODO: Need to try to reduce based on possible duplicates.

            if ( valid )
            {
                reducedList.add(event);
            }
        }

        return reducedList;
    }

    @Override
	public void afterProcessEvents() throws Exception
	{
		// if we're not an HA Master or solo, don't do anything here
		HAStatusEnum haThis = HAStatusEnum.getStatus(XBRCController.getInstance().getHaStatus());
		boolean bMasterOrSolo = (haThis == HAStatusEnum.master || haThis == HAStatusEnum.solo); 
		
		if (!bMasterOrSolo)
			return;
		
		synchronized(GST)
		{
			// now, walk through all entries in the GST and process age-related state changes
			for (String sGID : GST.keySet())
			{
				try
				{
					GuestStatus<GuestStatusState> gs = GST.get(sGID);
	
					// first, handle abandonment
					Date dtNow = new Date();
					Date dtLastAtReader = gs.getTimeLatestAtReader();
					long cmsecDiff = dtNow.getTime() - dtLastAtReader.getTime();
					if ( cmsecDiff > (Math.max( ConfigOptions.INSTANCE.getControllerInfo()
							.getAbandonmentTimeout(), gs.getRoleDetectDelay()) ) )
					{
						// abandoned
	
						// send the abandon message but only for guests that are in
						// proper state
						if (gs.getState() == GuestStatusState.HASENTERED)
						{
							// generate a dummy event aggregate
							EventAggregate ea = new EventAggregate( sGID, gs.getPidDecimal(),
									gs.getLastReader(),  new Date());
	
							SendJMSMessage("LOCATIONABANDON", 
									new AbandonMessageArgs(ea,
                                            gs.getHasXpass(),
                                            new Date(),
                                            gs.getGuestID(),
                                            gs.getPidDecimal(),
                                            gs.getLinkID(),
                                            gs.getLinkIDType(),
                                            gs.getBandType()));

							String sLocation;
							ReaderInfo ri = ea.getReaderInfo();
							if (ri!=null)
								sLocation = ri.getLocation().getName();
							else
								sLocation = "(Invalid reader: " + gs.getLastReader() + ")";
					    	XBRCController.getInstance().logEKG( new Date().getTime() +
					    			",CONFABANDON," +
					    			gs + "," +
					    			gs.getBandType() + "," +
					    			sLocation
					    			);
						}
	
						// mark the entry for deletion
						GuestStatusState stateStart = gs.getState();
						gs.setState(GuestStatusState.DELETABLE);
						if ( XBRCController.getInstance().isVerbose() )
							logger.trace("Guest: " + sGID + " changed from state "
									+ stateStart + " to " + gs.getState()
									+ " due to timeout");
	
					}
					else
					{
					    // Process events based on time.
					    ConfidenceEventAggregate cev = ConfidenceEventAggregate.determineState( gs );
					    if ( cev != null )
					    	AnalyzeEvent( cev, gs );
					}
				}
				catch (Exception e)
				{
					logger.error(ExceptionFormatter.format("Exception during afterProcessEvents", e));
				}
			}
		}
	}

    private String getGuestPrimaryBandType(Guest guest, String sBandID)
    {
        String bandType = "Guest";

        // Determine what type of band this is. In the case where
        // there is multiple bands, find the band that we're currently
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

 
	private int AnalyzeEvent(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		int err = 0;

		// peg some data/messages
		LocationType locationType = getLocationType(ev);
		GuestStatusState stateStart = gs.getState();

		// handle well known reader types
		switch (locationType)
		{
			case WAYPOINT:
				err = HandleWaypoint(ev, gs);
				break;

			default:
				logger.error("!! Non-WAYPOINT type location found in SPACE model venue");
				break;

		}

		gs.setTimeLastEvent(ev.getTimestamp());

		GuestStatusState stateEnd = gs.getState();

		if (stateStart != stateEnd)
		{
			if (XBRCController.getInstance().isVerbose())
				logger.trace("Guest: " + ev.getID() + " changed from state "
						+ stateStart + " to " + stateEnd
						+ " processing location " + getLocationType(ev));

			if (stateEnd == GuestStatusState.INDETERMINATE)
			{
				logger.warn("!! Guest: " + ev.getID()
						+ " is in INDETERMINATE state");
			}
		}

		return err;
	}

	private int HandleWaypoint(EventAggregate ev,
			GuestStatus<GuestStatusState> gs)
	{
		// peg
		String sReaderName = ev.getReaderInfo().getName();
		String sReaderLocation = ev.getReaderInfo().getLocation().getName();
		String sOldReader = gs.getLastReader();
		String sOldLocation = null;
		if (sOldReader != null)
		{
			ReaderInfo riOld = XBRCController.getInstance().getReader(sOldReader);
			if (riOld != null)
				sOldLocation = riOld.getLocation().getName();
		}
		Date ts = ev.getTimestamp();

		// Validate
		switch (gs.getState())
		{
			case NEW:
			case INDETERMINATE:
			{
				gs.setTimeEntered(ts);
				gs.setState(GuestStatusState.HASENTERED);

				// fall-through!
			}

			case HASENTERED:
			{
                // Determine if the new confidence level is greater than
                // the delta of the last sent confidence. This needs to be 
				// positive or negative.
				Integer confidenceChange = gs.getConfidenceLastEvent();
                logger.trace("Confidence last event: confidence=" + gs.getConfidenceLastEvent());
                if ( confidenceChange != null ) {
                    logger.trace("Confidence current event: confidence=" + ((LRREventAggregate)ev).getConfidence());
                    confidenceChange = Math.abs( ((LRREventAggregate)ev).getConfidence() -
                                gs.getConfidenceLastEvent() );
                    logger.trace("Confidence change: confidenceDelta=" + confidenceChange);
                }

                // Send out a message if we haven't sent one or if we have enough of a delta change.
                if ( confidenceChange == null ||
                        !sReaderLocation.equals(sOldLocation) ||
                        confidenceChange >= ConfigOptions.INSTANCE.getControllerInfo().getConfidenceDelta())
                {
                    logger.trace("Sending confidence event");

					gs.setTimeEarliestAtReader(ts);

					// send the readerevent
					SendJMSMessage("LOCATIONEVENT",
							new ConfidenceMessageArgs(ev,
                                    false,
                                    gs.getGuestID(),
                                    ev.getPidDecimal(),
                                    gs.getLinkID(),
                                    gs.getLinkIDType(),
                                    gs.getBandType(),
                                    ((LRREventAggregate)ev).getConfidence()));

	            	XBRCController.getInstance().logEKG(new Date().getTime() + 
	            			",CONF," +
	            			gs + "," +
	            			gs.getBandType() + "," +
	            			ev.getReaderInfo().getLocation().getName() + "," +
	            			((LRREventAggregate)ev).getConfidence()
	            			);

					gs.setConfidenceLastEvent(((LRREventAggregate)ev).getConfidence());
                }
                else
                {
                    logger.trace("Confidence event not sent. Not big enough delta change in confidence: ConfidenceChange="
                            + confidenceChange +
                            "; confidenceLastEvent=" + gs.getConfidenceLastEvent() +
                            "; currentConfidence=" + ((LRREventAggregate)ev).getConfidence() +
                            "; currentReaderLocation=" + sReaderLocation +
                            "; oldReaderLocation=" + sOldLocation);
                }

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
				logger.debug("Time:        "
						+ ev.getTimestamp().getTime());
				logger.debug("!! Guest unexpectedly near ENTRY reader: ignoring");
				return 0;
			}
		}

		// set some stats
		gs.setLastReader(sReaderName);

		return 0;
	}

	private LocationType getLocationType(LocationInfo li)
	{
		 // default to unknown and tolerate these. Others break the code
        LocationType eLocationType = LocationType.UNKNOWN;

        switch (li.getLocationTypeID())
        {
            case 2:
                eLocationType = LocationType.WAYPOINT;
                break;

            case 11:
                eLocationType = LocationType.UNKNOWN;
                break;

            default:
            	logger.debug("!! Unexpected LocationTypeID: ignoring");
        }

        return eLocationType;
	}
	
    private LocationType getLocationType(EventAggregate ev)
    {
       return getLocationType(ev.getReaderInfo().getLocation());
    }

    private int SendJMSMessage(String sType, MessageArgs ma)
	{
		if (XBRCController.getInstance().isVerbose())
			logger.trace("{Message: " + sType + " Time: " + ma.getTimestamp()
					+ ", ID: " + ma.getBandID() + " }");

		try
		{
			// make sure we are supposed to be sending something!
			HAStatusEnum haThis = HAStatusEnum.getStatus(XBRCController.getInstance().getHaStatus());
			boolean bMasterOrSolo = (haThis == HAStatusEnum.master || haThis == HAStatusEnum.solo); 

			if (bMasterOrSolo)
				XBRMessageGenerator.publishMessage(sType, ma, true, true);
			else
				logger.error("Non master or solo xBRC sending JMS message. Suppressed!");
		}
		catch (Exception e)
		{
			logger.error("Failed to publish JMS/HTTP message", e);
		}

		return 0;
	}

	@Override
	public void readConfiguration()
	{
		ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();
		ReaderExecutor.getInstance().initialize(XBRCController.getInstance().getReaders(null));
	}

	@Override
	public boolean isEventLogged(String bandId)
	{
		return true;
	}

	@Override
	public String storeConfiguration(int cIndentLevel)
	{
		return "";
	}

	@Override
	public void restoreConfiguration(Connection conn, String sXML)
	{
		// Nothing to do
	}

	@Override
	public void formatStatus(XbrcStatus status)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void formatReaderStatus(ReaderInfo readerInfo)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePropertiesRead(XbrcConfig config, Connection conn) throws Exception
	{

		com.disney.xband.xbrc.spacemodel.ConfigOptions.ControllerInfo spaceInfo = com.disney.xband.xbrc.spacemodel.ConfigOptions.INSTANCE
				.getControllerInfo();

		// get fresh version from the database
		Config.getInstance().read(conn, spaceInfo);

		if (config.getConfiguration() != null)
			config.getConfiguration().addAll(Configuration.convert(spaceInfo));
		else
			config.setConfiguration(Configuration.convert(spaceInfo));
	}

	public void handlePropertiesWrite(XbrcConfig config, Connection conn)
			throws Exception
	{

		com.disney.xband.xbrc.spacemodel.ConfigOptions.ControllerInfo spi = com.disney.xband.xbrc.spacemodel.ConfigOptions.INSTANCE
				.getControllerInfo();

		Configuration.convert(config.getConfiguration(), spi);
		Config.getInstance().write(conn, spi);
	}

	@Override
	public void storeState(Connection conn)
	{
		// Now save the GST.
		if (dirtyGST)
			SaveGST(conn);
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
				while (rs.next())
				{
                    // TODO: Need to store/retrieve confidence related params?
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
					gs.setPidDecimal(rs.getString("PidDecimal"));
					gs.setLinkID(rs.getString("LinkID"));
					gs.setLinkIDType(rs.getString("LinkIDType"));
					gs.setBandType(rs.getString("BandType"));
					gs.setBandTransmitting(rs.getBoolean("BandTransmitting"));
					
					// look up the Guest
					Guest g =  IDMSResolver.INSTANCE.getGuestFromGuestId(sGuestId);
					gs.setGuest(g);
					
					GST.put(sGuestId, gs);
				}
			}
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Error restoring state from GST", ex));
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

		return404(response);
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
			logger.error(ExceptionFormatter.format(
					"Error sending 404 response", e));
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
		return "1.0.0.26";
	}
	
	@Override
	public List<String> getBandsPresentAtLocations(Set<Long> locationIds)
	{
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
				// TODO: can get this from gs.getGuest() no?
				Guest g = IDMSResolver.INSTANCE.getGuestFromGuestId(gs.getGuestID());
				if (g == null)
				{
					if (gs.getPidDecimal() != null && !gs.getPidDecimal().isEmpty())
						guests.add(XbrJsonMapper.publicIdToLongRangeId(Long.parseLong(gs.getPidDecimal())));
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
						
		    	if (messageType.equals("LOCATIONEVENT"))
		    	{
		    		processReaderEventHAMessage(bais);
		    	}
		        else if (messageType.equals("LOCATIONABANDON"))
		    	{
		    		processAbandonEventHAMessage(bais);
		    	}
		        else
		        {
		        	// ignore other messages
		            continue;
		        }
			}
			catch(Exception ex)
			{
				logger.error(ExceptionFormatter.format("Error processing HA message", ex));
				if (messageType!=null)
					logger.error("HA message type: " + messageType);
				if (messageText!=null)
					logger.error("HA message text: " + messageText);
			}
		}
		
		
	}

	private void processReaderEventHAMessage(ByteArrayInputStream bais) throws JAXBException
	{
		ReaderEventMessagePayload emp = XmlUtil.convertToPojo(bais, MessagePayload.class, ReaderEventMessagePayload.class);
		ReaderEventMessage em = emp.getMessage();
		
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
		
		logger.info("Got HA LOCATIONEVENT message for guest id: " + sGuestID);
		
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
		gs.setTimeEarliestAtReader(dtMessage);
		gs.setTimeLatestAtReader(dtMessage);
		gs.setState(GuestStatusState.HASENTERED);
		gs.setTimeEntered(dtMessage);
		gs.setHasXpass(em.getxPass());

	}
		
	private void processAbandonEventHAMessage(ByteArrayInputStream bais) throws JAXBException
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
		
		logger.info("Got HA LOCATIONABANDON message for guest id: " + sGuestID);

		GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
		if (gs!=null)
		{
			gs.setState(GuestStatusState.DELETABLE);
		}
	}

	@Override
	public String serializeStateToXML(String sGuestID)
	{
		try
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
                if ( gs != null ) {
				    gs.setGSTLocationInfo(null);
                }
				
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
					String sLastReader = gs.getLastReader();
					if (sLastReader!=null)
					{
						ReaderInfo ri = XBRCController.getInstance().getReader(sLastReader);
						gli.name = ri.getLocation().getName();
						gli.id = ri.getLocation().getId();
						gli.arrived = DateUtils.format(gs.getTimeEarliestAtReader().getTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
						gli.latest = DateUtils.format(gs.getTimeLatestAtReader().getTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS");
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
		catch(Exception e)
		{
			logger.error(ExceptionFormatter.format("Error serializing state to XML", e));
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
					
					gs.setHasXpass(gsMaster.getHasXpass());
					gs.setState(gsMaster.getState());
					gs.setBandType(gsMaster.getBandType());
					
					GSTLocationInfo gli = gsMaster.getGSTLocationInfo();
					if (gli!=null)
					{
						gs.setLastReader(gli.name);
						gs.setTimeEarliestAtReader(DateUtils.parseDate(gli.arrived));
						gs.setTimeLatestAtReader(DateUtils.parseDate(gli.latest));
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format("Error deserializing from XML", e));
		}
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
		}
		
		return gs;
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
	public void beforeConfigurationDeleteFromTable(Connection conn,
			String tableName, Collection<Object> ids) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIdleSequenceEnabled(ReaderInfo ri)
	{
		return true;
	}

	@Override
	public boolean isWaypointLocationType(LocationInfo li) {
		return getLocationType(li) == LocationType.WAYPOINT;
	}

	@Override
	public void flattenModel(org.jdom.Element model, Map<String, String> results, Set<String> exclude, char pathSeparator)
	{
		if (model == null)
		{
			logger.warn("Element 'model' not found in the xml provided.");
			return;
		}
		
		Element modelConfig = model.getChild("spaceModelConfig");
		if (modelConfig == null)
			return;
		
		//TODO SpaceModel doesn't have any model specific config info at this time.
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
		// TODO Auto-generated method stub
		
	}
}
