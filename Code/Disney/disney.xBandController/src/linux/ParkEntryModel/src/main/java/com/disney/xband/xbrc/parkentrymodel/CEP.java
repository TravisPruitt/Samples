package com.disney.xband.xbrc.parkentrymodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Level;
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
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.CastMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.PECastMessage;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.lib.xbrapi.ReaderExecutor;
import com.disney.xband.lib.xbrapi.Sequence;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.lib.xbrapi.XbrJsonMapper;
import com.disney.xband.parkgreeter.lib.message.PGCast;
import com.disney.xband.parkgreeter.lib.message.PGGuest;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.ErrorCode;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXLocation;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXMessage;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader.Lights;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader.State;
import com.disney.xband.parkgreeter.lib.message.toxbrc.PGMessage;
import com.disney.xband.parkgreeter.lib.message.toxbrc.PGMessage.Type;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand.Entitlement.EntitlementInfo;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand.Entitlement.EntitlementInfo.SeasonPassInfo.DemographicData.Field;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.db.OmniServerService;
import com.disney.xband.xbrc.lib.db.PETransactionService;
import com.disney.xband.xbrc.lib.db.ReaderOmniServerService;
import com.disney.xband.xbrc.lib.db.XbioTemplateService;
import com.disney.xband.xbrc.lib.entity.CastMember;
import com.disney.xband.xbrc.lib.entity.CastMemberTapAction;
import com.disney.xband.xbrc.lib.entity.HAMessage;
import com.disney.xband.xbrc.lib.entity.HAStatusMessage;
import com.disney.xband.xbrc.lib.entity.OmniServer;
import com.disney.xband.xbrc.lib.entity.PETransaction;
import com.disney.xband.xbrc.lib.entity.ReaderOmniServer;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.XbioImage;
import com.disney.xband.xbrc.lib.entity.XbioTemplate;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.idms.IDMSResolver;
import com.disney.xband.xbrc.lib.model.BioEventAggregate;
import com.disney.xband.xbrc.lib.model.BioImageEventAggregate;
import com.disney.xband.xbrc.lib.model.BioScanErrorEventAggregate;
import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.GenericEventAggregate;
import com.disney.xband.xbrc.lib.model.IXBRCModel;
import com.disney.xband.xbrc.lib.model.LRREventAggregate;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.TapEventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.omni.OmniConst;
import com.disney.xband.xbrc.parkentrymodel.ConfigOptions.SaveBioImages;
import com.disney.xband.xbrc.parkentrymodel.OmniTicketReaderQueue.ConnectionState;
import com.disney.xband.xbrc.parkentrymodel.scheduler.ParkEntrySchedulerHelper;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.GuestIdentifier;
import com.disney.xband.xview.lib.model.Xband;

public class CEP implements IXBRCModel
{
	private static Logger logger = Logger.getLogger(CEP.class);
	private static Logger plogger = Logger.getLogger("performance");

	private ReaderLightEffects readerLightEffects = new ReaderLightEffects();

	// the guest status table is indexed by Guest ID
	private Hashtable<String, GuestStatus<GuestStatusState>> GST = new Hashtable<String, GuestStatus<GuestStatusState>>();
	
	// cast member status table
	private Map<String, CMST> CMT;
	// Omni servers mapped by reader id
	private Map<Long, List<OmniServerItem>> omniServers;
	// this flag is set to true whenever the GST becomes dirty and needs to be
	// saved
	private boolean saveGST = false;
	private boolean saveCMST = false;
	private long xbioImageCount = 0;
	private PGXMessageFactory pgxmsgfactory = null;
	private boolean active = false;
	// During failover from slave to master it takes a bit for location status to be sorted out.
	private AtomicInteger pendingLocationStatus = new AtomicInteger();
	private Date pendingLocationStatusStartTime = null;
	private Date oncePerSecondTimer = new Date(); 

	/*
	 * Allows to concurrently flash readers using different colors.
	 */
	private ReaderColorManager readerColorManager;

	@Override
	public void initialize()
	{

		GST = new Hashtable<String, GuestStatus<GuestStatusState>>();
		CastAppManager.getInstance().initialize();
		// Factory to create greeter response messages
		pgxmsgfactory = new PGXMessageFactory(this);
		readerLightEffects.initialize();		
	}

	public void readOmniServersFromDatabase()
	{
		omniServers = new Hashtable<Long, List<OmniServerItem>>();
		Connection conn = null;
		try
		{
			conn = XBRCController.getInstance().getPooledConnection();
			Map<Long, OmniServer> servers = OmniServerService
					.getAllMapped(conn);
			Collection<ReaderOmniServer> pairs = ReaderOmniServerService
					.getAll(conn);

			// First, set the inherited port number on all OmniSever objects.
			Integer defaultOmniPort = ConfigOptions.INSTANCE.getSettings()
					.getOmniTicketPort();
			for (OmniServer omniServer : servers.values())
			{
				if (omniServer.getPort() == null || omniServer.getPort() == 0)
					omniServer.setPort(defaultOmniPort);
			}

			for (ReaderOmniServer pair : pairs)
			{
				OmniServer server = servers.get(pair.getOmniserverid());
				if (server == null)
				{
					logger.fatal("Failed to lookup OmniServer record using key: "
							+ pair.getOmniserverid()
							+ " for Reader with id: "
							+ pair.getReaderid());
					continue;
				}
				OmniServerItem item = new OmniServerItem(server,
						pair.getPriority());

				List<OmniServerItem> readerItems = omniServers.get(pair
						.getReaderid());
				if (readerItems == null)
				{
					readerItems = new LinkedList<OmniServerItem>();
					omniServers.put(pair.getReaderid(), readerItems);
				}
				readerItems.add(item);
			}
		}
		catch (Exception e)
		{
			logger.fatal(
					"Failed to read OmniServer or ReaderOmniServer table from database",
					e);
		}
		finally
		{
			XBRCController.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public void processEvents(List<XbrEvent> liEvents) throws Exception
	{
		List<EventAggregate> liAggregate = null;
		try
		{
			logger.debug("Calling first pass singulation");
			Date dtStart = new Date();
			
			if (ConfigOptions.INSTANCE.getSettings().isFastSingulation())
				liAggregate = new SimpleEventAggregator(XBRCController.getInstance(),
						this).Analyze(liEvents);
			else
				liAggregate = new EventAggregator(XBRCController.getInstance(),
						this).Analyze(liEvents);
			
			Date dtEnd = new Date();
			logger.debug("Returned from first pass singulation");

			long msec = dtEnd.getTime() - dtStart.getTime();
			double msecPerEvent = (double) msec / liEvents.size();
			XBRCController.getInstance().getStatus().getPerfSingulationMsec()
					.processValue(msecPerEvent);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Exception during 1st stage of singulation", ex));
		}
		plogger.trace("Ending 1st stage of singulation");

		if (plogger.getLevel() == Level.TRACE)
			plogger.trace("Processing " + liAggregate.size() + " events");

		// now process the events
		for (EventAggregate ev : liAggregate)
		{
			logger.trace("Received event " + ev.getClass().toString());

			Date now = new Date();

			if (Math.abs(now.getTime() - ev.getTimestamp().getTime()) > 10000)
			{
				logger.warn("Event time more than 10 seconds off from current time. Now: "
						+ now.toString()
						+ " Event Time: "
						+ ev.getTimestamp().toString());
				Long secOld = (now.getTime() - ev.getTimestamp().getTime()) / 1000;
				if (secOld > ConfigOptions.INSTANCE.getSettings()
						.getAbandonmentTimeSec())
				{
					logger.warn("Ignoring event because is too old to process. "
							+ secOld + " seconds old");
					continue;
				}
			}

			// Possible a cast member tap?
			if (ev instanceof TapEventAggregate)
			{
				// If the following function returns true then it was a Cast App
				// member tap.
				if (handleCastMemberTapEvent((TapEventAggregate) ev) == true)
					continue;
				
				if (ev.getReaderInfo().getType() != ReaderType.xfpxbio)
				{
					logger.warn("Ignoring tap on a reader " + ev.getReaderInfo().getName() + " because the biometric device is not working.");
					continue;
				}
				
				// For Guests we must have a secure ID in order to proceed.
				if (((TapEventAggregate) ev).getTapEvent().getSid() == null
						|| ((TapEventAggregate) ev).getTapEvent().getSid()
								.isEmpty())
				{
					logger.warn("Card read for UID " + ev.getID()
							+ " did not produce a secure ID. Ignoring tap.");
					continue;
				}
			}

			// Handle BioImageEvent here (we only save it to the database).
			if (ev instanceof BioImageEventAggregate)
			{
				BioImageEventAggregate bie = (BioImageEventAggregate) ev;
				logger.info("Bio image event for RFID "
						+ bie.getBioEvent().getUid());
				// Save the bio image information
				ReaderExecutor.getInstance().saveXbioImage(
						new XbioImage(null, bie.getBioEvent()
								.getTransactionId(), bie.getBioEvent()
								.getTemplateId(), bie.getBioEvent()
								.getXbioImages(), now));
				continue;
			}

			if (ev instanceof GenericEventAggregate)
			{
				GenericEventAggregate gea = (GenericEventAggregate) ev;
				if (gea.getEvent() != null)
					logger.trace("Ignoring event of type "
							+ gea.getEvent().getClass().toString());
				continue;
			}

			// Ignore tap events for locations that are not logged in unless
			// testMode is on.
			if (ev instanceof TapEventAggregate)
			{
				CMST cmst = getCastMemberStateForLoction(ev.getReaderInfo()
						.getLocation().getName());
				if ((cmst == null || cmst.getState() != CastMemberState.LOGGEDIN)
						&& !ConfigOptions.INSTANCE.getSettings().isTestMode())
				{
					logger.warn("Ignoring tap event uid: "
							+ ((TapEventAggregate) ev).getTapEvent().getUid()
							+ " for reader "
							+ ev.getReaderInfo().getName()
							+ " because cast member is not logged in and testMode configuration flag is set to false.");
					continue;
				}
			}

			GuestStatus<GuestStatusState> gs = null;
			if (ev instanceof BioEventAggregate
					|| ev instanceof BioScanErrorEventAggregate)
				gs = getGuestStatusFromLastReader(ev.getReaderInfo().getName());
			else
				gs = getGuestStatusFromId(ev);

			// We must have a GuestStatus to continue.
			if (gs == null)
			{
				logger.warn("No guest status found. Ignoring event of type: "
						+ ev.getClass().toString());
				continue;
			}
			
			if (ev instanceof TapEventAggregate)
			{
				if (processNonGuestBands(ev, ((TapEventAggregate)ev).getTapEvent().getXband(), gs.getGuest()))
					continue;
			}

			AnalyzeEvent(ev, gs);
		}

		if (plogger.getLevel() == Level.TRACE)
			plogger.trace("After processing " + liAggregate.size() + " events");

		saveGST = true;
	}

	private GuestStatus<GuestStatusState> getGuestStatusFromId(EventAggregate ev)
	{
		plogger.trace("performance: CEP::getGuestStatusFromId enter");

		// get RF or LR id
		String uid = ev.getID();

		if (uid == null)
		{
			logger.fatal("Event did not produce a valid ID. Cannot process guest."
					+ ev.toString());
			return null;
		}

		// map to a guest
		Guest guest = null;
		List<Celebration> celebrations = null;
		String sGuestID = null;
		Xband xb = null;

		if (ev instanceof LRREventAggregate)
		{
			xb = ((LRREventAggregate)ev).getXband();
			guest = ((LRREventAggregate)ev).getGuest();
		}
		else if (ev instanceof TapEventAggregate)
		{
			xb = ((TapEventAggregate)ev).getTapEvent().getXband();
			guest = ((TapEventAggregate)ev).getTapEvent().getGuest();
		}
	
		if (xb != null && !xb.getActive())
			return null;
	
		if (guest != null)
		{
			sGuestID = guest.getGuestId();
			// Asynchronous call
			celebrations = IDMSResolver.INSTANCE
					.getCelebrationListFromGuestId(sGuestID, true);
		}

		if (sGuestID == null)
		{
			if (ev instanceof LRREventAggregate)
				sGuestID = "?PID=" + ((LRREventAggregate)ev).getPidDecimal();
			else if (ev instanceof TapEventAggregate)
				sGuestID = "?PID=" + ((TapEventAggregate)ev).getPidDecimal();

			logger.info("Generating fake GuestId: " + sGuestID);
		}

		// do we have the guest in the table
		GuestStatus<GuestStatusState> gs = GST.get(sGuestID);
		if (gs == null)
		{
			plogger.trace("performance: CEP::getGuestStatusFromId creating new GST entry");
			// if it's an entry event, add the guest
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
			gs.setCelebrations(celebrations);
			gs.setBandRfid("");
			
			gs.setBandType(getGuestPrimaryBandType(xb));
			GST.put(sGuestID, gs);
			plogger.trace("performance: CEP::getGuestStatusFromId finished creating new GST entry");
		}
		
		if (ev instanceof TapEventAggregate && gs.getPeTransactionState() == null)
		{
			gs.setSid(((TapEventAggregate) ev).getTapEvent().getSid());
			gs.setPidDecimal(((TapEventAggregate) ev).getTapEvent().getPidDecimal());
			gs.setPeTransactionState(new PETransactionState());
			gs.getPeTransactionState().setStartTime(new Date());
			gs.getPeTransactionState().setReader(ev.getReaderInfo());
			PETransaction pet = gs.getPeTransactionState().getTransaction(ev.getID(), null, null);
			savePETransaction(pet);
			gs.getPeTransactionState().setTransactionId(pet.getId());
		}
		
		if (ev instanceof LRREventAggregate)
		{
			gs.setTimeLatestAtLrrReader(ev.getTimestamp());
			gs.setLastLrrReader(ev.getReaderInfo().getName());
			gs.setPidDecimal(ev.getPidDecimal());
		}

		plogger.trace("performance: CEP::getGuestStatusFromId exit");

		return gs;
	}

	// Attempts to find the band type of a given band for a guest.
	private String getGuestPrimaryBandType(Xband xb)
	{
		if (xb == null)
			return "Guest";

		return xb.getBandType();
	}

	public GuestStatus<GuestStatusState> getGuestStatusFromLastReader(
			String readerId)
	{

		for (GuestStatus<GuestStatusState> gs : GST.values())
		{
			if (gs.getLastReader() != null
					&& gs.getLastReader().equals(readerId) && gs.getState() != GuestStatusState.DELETABLE)
				return gs;
		}

		return null;
	}

	public CMST getCastMemberStateForLoction(String locationName)
	{
		synchronized (CMT)
		{
			return CMT.get(locationName);
		}
	}

	private void savePETransaction(PETransaction pet)
	{
		plogger.trace("performance: CEP::savePETransaction entry");
		Connection conn = null;
		try
		{
			conn = XBRCController.getInstance().getPooledConnection();
			plogger.trace("performance: CEP::savePETransaction before PETransactionService.save");
			PETransactionService.save(conn, pet);
			plogger.trace("performance: CEP::savePETransaction after PETransactionService.save");
		}
		catch (Exception e)
		{
			logger.error(
					"Failed to save PETransaction record for RFID "
							+ pet.getBandId() + " " + e.getLocalizedMessage(),
					e);
		}
		finally
		{
			XBRCController.getInstance().releasePooledConnection(conn);
			plogger.trace("performance: CEP::savePETransaction exit");
		}
	}

	private void updatePETransaction(PETransaction pet)
	{
		Connection conn = null;
		try
		{
			conn = XBRCController.getInstance().getPooledConnection();
			PETransactionService.update(conn, pet);
		}
		catch (Exception e)
		{
			logger.error(
					"Failed to update PETransaction record with id "
							+ pet.getId() + " for RFID " + pet.getBandId()
							+ " " + e.getLocalizedMessage(), e);
		}
		finally
		{
			XBRCController.getInstance().releasePooledConnection(conn);
		}
	}

	private void AnalyzeEvent(EventAggregate ev,
			GuestStatus<GuestStatusState> gs)
	{
		// peg some data
		LocationType locationType = getLocationType(ev);
		GuestStatusState stateStart = gs.getState();
		String sReaderName = ev.getReaderInfo().getName();

		boolean eventAccepted = true;

		// handle well known reader types
		switch (locationType)
		{
			case ENTRY:
				if (ev instanceof LRREventAggregate)
				{
					HandleEntryLrrEvent((LRREventAggregate)ev, gs);
					eventAccepted = false;
				}
				else if (ev instanceof TapEventAggregate)
					eventAccepted = HandleEntryTapEvent((TapEventAggregate) ev, gs);
				else if (ev instanceof BioEventAggregate)
				{
					gs.setFpScanCount(gs.getFpScanCount() + 1);
					HandleEntryBioEvent((BioEventAggregate) ev, gs);
				}
				else if (ev instanceof BioScanErrorEventAggregate)
				{
					HandleEntryBioScanErrorEvent(
							(BioScanErrorEventAggregate) ev, gs);
				}
				break;

			default:
				HandleOther(ev, gs);
				break;

		}

		if (eventAccepted)
		{
			gs.AddVisitedLocation(ev.getReaderInfo().getName());
			gs.setTimeLastEvent(ev.getTimestamp());
			gs.setLastReader(sReaderName);
			gs.setTimeLatestAtReader(ev.getTimestamp());
		}

		GuestStatusState stateEnd = gs.getState();

		if (stateStart != stateEnd)
		{
			if (XBRCController.getInstance().isVerbose())
				logger.debug("Guest: " + ev.getID() + " changed from state "
						+ stateStart + " to " + stateEnd
						+ " processing location " + getLocationType(ev));

			if (stateEnd == GuestStatusState.INDETERMINATE)
			{
				logger.warn("!! Guest: " + ev.getID()
						+ " is in INDETERMINATE state");
			}
		}
	}

	private void HandleOther(EventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		logger.warn("Guest event at unexpected location "
				+ getLocationType(ev).toString() + " was ignored");
	}
	
	private void HandleEntryLrrEvent(LRREventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		if (gs.isSentArrivedMessage())
			return;
		
		gs.setTimeArrived(ev.getTimestamp());		
		
		logger.info("Guest " + gs + " was detected by long range reader " + ev.getReaderInfo().getName() + " at park entrance");
		
		PEMessageGenerator.publishMessage(
				PEMessageGenerator.TYPE_PARKENTRYEVENT,
				null,
				gs, 
				ev.getReaderInfo(), 
				ev.getTimestamp());
		
		gs.setSentArrivedMessage(true);
		saveGST = true;
	}

	/*
	 * Reset the guest state as if he just arrived at the TAP reader.
	 */
	private void restartGuestState(TapEventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		// Update the previous transaction state and start a new one.
		gs.getPeTransactionState().setFinishTime(new Date());		
		gs.getPeTransactionState().setAbandoned(gs.getState() != GuestStatusState.DELETABLE);
		updatePETransaction(gs.getPeTransactionState().getTransaction(gs.getBandRfid(), gs.getOmniEntitlementResponse(), gs.getOmniBioResponse()));
		// Start the timers again as if the guest just arrived
		gs.setPeTransactionState(new PETransactionState());
		gs.getPeTransactionState().setStartTime(new Date());
		gs.getPeTransactionState().setReader(ev.getReaderInfo());
		PETransaction pet = gs.getPeTransactionState().getTransaction(ev.getID(),null,null);
		savePETransaction(pet);
		gs.getPeTransactionState().setTransactionId(pet.getId());
		gs.setState(GuestStatusState.INDETERMINATE);
	}
	
	/*
	 * Returns true if last reader and the time last at reader should be set.
	 */
	private boolean HandleEntryTapEvent(TapEventAggregate ev, GuestStatus<GuestStatusState> gs)
	{
		logger.info("Tap event for RFID " + ev.getID());
		
		String sReaderName = ev.getReaderInfo().getName();
		Date ts = ev.getTimestamp();
		
		// See if there is someone else using this reader.
		GuestStatus<GuestStatusState> othergs = 
				getGuestStatusFromLastReader(ev.getReaderInfo().getName());
		
		// Don't allow reader take-overs. If someone else is still at a reader then reject the tap.
		if (othergs != null && othergs != gs) {
			// If the other guest status is in HASENTERED state then we can now
			// safely delete it. We keep it in HASENTERED sate for some time to ignore
			// duplicate taps for a short time.
			if (othergs.getState() == GuestStatusState.HASENTERED) 
			{
				othergs.setState(GuestStatusState.DELETABLE);	
			}
			else
			{
				logger.info("Ignoring tap event RFID " + ev.getID() + " for reader " + 
							ev.getReaderInfo().getName() + " occupied by another guest with RFID " + othergs.getBandRfid());
				
				// Since we are ignoring this tap event make sure that the GST entry is removed.
				if (gs.getPeTransactionState().getReader().getName().equals(ev.getReaderInfo().getName())) {
					gs.setBandRfid(ev.getID());
					gs.setState(GuestStatusState.DELETABLE);
					gs.getPeTransactionState().setFinishTime(new Date());
					updatePETransaction(gs.getPeTransactionState().getTransaction(gs.getBandRfid(), 
							gs.getOmniEntitlementResponse(), 
							gs.getOmniBioResponse()));					
				}
				return false;
			}
		}
		
		// Handle special case of unexpected tap. Applies to all states except INDETERMINATE.
		if (gs.getState() != GuestStatusState.INDETERMINATE) {	
			
			// User tapped on a different reader than last time.
			// Reset the GST status for the guest to restart the entire process at a new reader.
			// Leave the old reader alone. If it has a blue light then Cast member will have to clear it.
			if (!gs.getLastReader().equals(ev.getReaderInfo().getName())) {
				// reset the reader.
				ReaderExecutor.getInstance().resetBioTapReader(gs.getPeTransactionState().getReader());
				
				if (gs.getState() == GuestStatusState.BLUELANE) {
					// Light change notification
					PGXMessage message = pgxmsgfactory.createReaderEventNotification(gs.getPeTransactionState().getReader());
					message.getReader().setLights(Lights.off);
					CastAppManager.getInstance().notify(message);
				}
				
				if (gs.getState() != GuestStatusState.DELETABLE)
				{
					PEMessageGenerator.publishMessage(
									PEMessageGenerator.TYPE_ABANDONED,
									PEMessageGenerator.REASON_SWITCHEDREADER,
									gs,
									ev.getReaderInfo(),
									new Date());
				}
				
				restartGuestState(ev, gs);
			}
		}

		switch(gs.getState())
		{	
			case DELETABLE:
			{ 
				if (gs.getLastReader().equals(ev.getReaderInfo().getName())) {
					logger.info("(DELETABLE) Guest " + gs + " tapped on the same reader. Reseting guest state." + ev.getReaderInfo().getName());
					restartGuestState(ev, gs);
					// no break here, we continue with the INDENTERMINATE state processing
				}
				else {
					break;
				}
			}
		
			case INDETERMINATE:
			{
				gs.setTimeEarliestAtReader(ts);
				gs.setTimeLatestAtReader(ts);			
				gs.setHasXpass(false);
				gs.setState(GuestStatusState.TAPPED);
				gs.setBandRfid(ev.getID());
				
				PEMessageGenerator.publishMessage(PEMessageGenerator.TYPE_TAPPED, PEMessageGenerator.REASON_NOTSET, gs, ev.getReaderInfo(), new Date());
				
				// Ask OMNI for entitlement.
				RequestCommand cmd = OmniTicket.makeEntitlementRequest(ev.getReaderInfo().getDeviceId(), gs.getGuestID(), 
						gs.nextOmniTransactionId().longValue(), ev.getReaderInfo().getBioDeviceType());
				cmd.getEntitlement().setRedeem(true);
				cmd.getEntitlement().setBioValidation(true);
				cmd.getEntitlement().getMediaInfo().getMediaSearchMode().setXBandID(ev.getTapEvent().getSid());
				OmniTicketQueue.getInstance().send(cmd);
				gs.getPeTransactionState().setOmniEntitlementStart(new Date());
				
				break;
			}			
			
			case TAPPED:
			{
				// If the same guest tapped again to the same reader the just ignore it. They are probably getting impatient.
				if (gs.getLastReader().equals(ev.getReaderInfo().getName())) {
					logger.info("(TAPPED, RFID=" + ev.getID() +") Ignoring tap on the same reader " + ev.getReaderInfo().getName());
					break;
				}
				break;
			}
			
			case WAITINGFORENROLL:
			{
				// If the same guest tapped again to the same reader the just ignore it. They are probably getting impatient.
				if (gs.getLastReader().equals(ev.getReaderInfo().getName())) {
					logger.info("(WAITINGFORENROLL, RFID=" + ev.getID() +") Ignoring tap on the same reader " + ev.getReaderInfo().getName());
					break;
				}
				break;
			}
			
			case WAITINGFORMATCH:
			{
				// If the same guest tapped again to the same reader the just ignore it. They are probably getting impatient.
				if (gs.getLastReader().equals(ev.getReaderInfo().getName())) {
					logger.info("(WAITINGFORMATCH, RFID=" + ev.getID() +") Ignoring tap on the same reader " + ev.getReaderInfo().getName());
					break;
				}
				break;
			}
			
			case BLUELANE:
			{
				// If the same guest tapped again to the same reader the just ignore it. They are probably getting impatient.
				if (gs.getLastReader().equals(ev.getReaderInfo().getName())) {
					logger.info("(BLUELANE, RFID=" + ev.getID() +") Ignoring tap on the same reader " + ev.getReaderInfo().getName());
					break;
				}
				break;
			}			
			
			case HASENTERED:
			{
				// If the same guest tapped again to the same reader the just ignore it. They are probably getting impatient.
				if (gs.getLastReader().equals(ev.getReaderInfo().getName())) {
					logger.info("(HASENTERED, RFID=" + ev.getID() +") Ignoring tap on the same reader " + ev.getReaderInfo().getName());
					break;
				}
				break;
			}
				
			default:
			{
				// guest backtracking? ignore?
				logger.info("From reader: " + sReaderName);
				logger.info("Guest:       " + ev.getID());
				logger.info("In state:    " + gs.getState());
				logger.info("Time:        " + ev.getTimestamp().getTime());
				logger.info("!! Guest unexpectedly near ENTRY reader: ignoring");
			}
		}
		
		return true;
	}

	private void HandleEntryBioEvent(BioEventAggregate ev,
			GuestStatus<GuestStatusState> gs)
	{
		String sReaderName = ev.getReaderInfo().getName();

		logger.info("Bio event for RFID " + gs.getBandRfid());

		// Scan is now complete
		gs.getPeTransactionState().setScanFinish(new Date());

		// Save the bio image information
		Connection conn = null;
		try
		{
			plogger.trace("Inserting new XbioTemplate record for RFID "
					+ gs.getBandRfid());
			conn = XBRCController.getInstance().getPooledConnection();
			XbioTemplate xbTemplate = new XbioTemplate(null, gs
					.getPeTransactionState().getTransactionId(), ev
					.getBioEvent().getXbioTemplate(), gs
					.getPeTransactionState().getTotalScanDuration(),
					ev.getTimestamp());
			XbioTemplateService.save(conn, xbTemplate);
			gs.setTemlateId(xbTemplate.getId());
			plogger.trace("After inserting XbioTemplate");
		}
		catch (Exception e)
		{
			logger.error("Failed to save XbioTemplate to database for reader "
					+ sReaderName + " and bandId " + gs.getBandRfid(), e);
		}
		finally
		{
			XBRCController.getInstance().releasePooledConnection(conn);
		}

		switch (gs.getState())
		{
			case INDETERMINATE:
			{
				logger.warn("(INDETERMINATE, RFID=" + ev.getID()
						+ ") Ignoring bio scan on reader "
						+ ev.getReaderInfo().getName());
				break;
			}

			case TAPPED:
			{
				logger.warn("(TAPPED, RFID=" + ev.getID()
						+ ") Ignoring bio scan on reader "
						+ ev.getReaderInfo().getName());
				break;
			}

			case WAITINGFORMATCH:
			case WAITINGFORENROLL:
			{
				// Deal with a special case of guest failing to scan. Turn blue
				// light on.
				if (ev.getBioEvent().getXbioTemplate() == null
						|| ev.getBioEvent().getXbioTemplate().isEmpty())
				{
					logger.info("bio scan failed:  BLUELANE for guest "
							+ getGuestName(gs));
					ReaderExecutor.getInstance().setReaderSequence(
							ev.getReaderInfo(), Sequence.failure);
					gs.setState(GuestStatusState.BLUELANE);
					gs.setTimeLatestAtReader(new Date());
					sendCastAppEntitlement(gs, ev.getReaderInfo(),
							"Guest bio scan did not produce a template",
							gs.getLastOmniEntitlementResponse());
					gs.getPeTransactionState().setBlue(true);
					gs.getPeTransactionState().setFinishTime(new Date());
					updatePETransaction(gs.getPeTransactionState()
							.getTransaction(gs.getBandRfid(),
									gs.getOmniEntitlementResponse(),
									gs.getOmniBioResponse()));

					PEMessageGenerator.publishMessage(
							PEMessageGenerator.TYPE_BLUELANE,
							PEMessageGenerator.REASON_SCANFAILED,
							gs, ev.getReaderInfo(), new Date());
					break;
				}

				// Ask OMNI for enrollment.
				RequestCommand cmd = OmniTicket.makeBioEntitlementRequest(ev
						.getReaderInfo().getDeviceId(), gs.getGuestID(), gs
						.nextOmniTransactionId().longValue(), ev
						.getReaderInfo().getBioDeviceType());
				cmd.getEntitlement().setRedeem(true);
				cmd.getEntitlement().setBioValidation(true);
				cmd.getEntitlement().getMediaInfo().getMediaSearchMode()
						.setXBandID(gs.getSid());
				cmd.getEntitlement()
						.getMediaInfo()
						.getBiometricInfo()
						.setBiometricTemplate(
								ev.getBioEvent().getXbioTemplate());
				cmd.getEntitlement().getMediaInfo().getBiometricInfo()
						.setItem(BigInteger.valueOf(1));
				cmd.getHeader()
						.setRetryCounter(gs.getFpScanCount().byteValue());
				OmniTicketQueue.getInstance().send(cmd);
				gs.getPeTransactionState().setOmniBioMatchStart(new Date());
				break;
			}

			case BLUELANE:
			{
				logger.warn("(BLUELANE, RFID=" + ev.getID()
						+ ") Ignoring bio scan on reader "
						+ ev.getReaderInfo().getName());
				break;
			}

			case DELETABLE:
			{
				logger.warn("(DELETABLE, RFID=" + ev.getID()
						+ ") Ignoring bio scan on reader "
						+ ev.getReaderInfo().getName());
				break;
			}

			case HASENTERED:
			{
				logger.warn("(HASENTERED, RFID=" + ev.getID()
						+ ") Ignoring bio scan on reader "
						+ ev.getReaderInfo().getName());
				break;
			}

			default:
			{
				// guest backtracking? ignore?
				logger.info("From reader: " + sReaderName);
				logger.info("Guest:       " + ev.getID());
				logger.info("In state:    " + gs.getState());
				logger.info("Time:        " + ev.getTimestamp().getTime());
				logger.info("!! Guest in unexpected state: "
						+ gs.getState().toString());
			}
		}
	}

	void HandleEntryBioScanErrorEvent(BioScanErrorEventAggregate ev,
			GuestStatus<GuestStatusState> gs)
	{
		gs.getPeTransactionState().setScanErrorCount(
				gs.getPeTransactionState().getScanErrorCount() + 1);
		if (gs.getPeTransactionState().getScanErrorReasons() == null)
			gs.getPeTransactionState().setScanErrorReasons(
					ev.getBioScanErrorEvent().getReason());
		else
			gs.getPeTransactionState().setScanErrorReasons(
					gs.getPeTransactionState().getScanErrorReasons() + ","
							+ ev.getBioScanErrorEvent().getReason());
	}

	private LocationType getLocationType(EventAggregate ev)
	{
		// gotta default to something or to throw an exception
		LocationType eLocationType = LocationType.ENTRY;
		switch (ev.getReaderInfo().getLocation().getLocationTypeID())
		{
			case 1:
				eLocationType = LocationType.ENTRY;
				break;
			case 0:
				eLocationType = LocationType.UNKNOWN;
			default:
				logger.error("Unexpected location type " + ev.getReaderInfo().getLocation().getLocationTypeID() + " for location " + 
								ev.getReaderInfo().getLocation().getName());
		}

		return eLocationType;
	}

	@Override
	public void readConfiguration()
	{
		ReaderExecutor.getInstance().initialize(XBRCController.getInstance().getReaders(null));

		readerColorManager = new ReaderColorManager(ConfigOptions.INSTANCE
				.getSettings().getFalshColors());

		ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();
		ReaderApi.setConnectionTimeout(ConfigOptions.INSTANCE.getSettings()
				.getReaderConnectTimeoutMs());

		if (CMT == null)
		{
			Connection conn = null;
			try
			{
				conn = XBRCController.getInstance().getPooledConnection();
				CMT = CMSTService.readCMST(conn);
			}
			catch (Exception e)
			{
				logger.fatal("Failed to read CMST table from database", e);
			}
			finally
			{
				XBRCController.getInstance().releasePooledConnection(conn);
			}
		}

		readOmniServersFromDatabase();		
		CastAppManager.getInstance().onConfigChanged();
		readerLightEffects.initialize();
		
		// If we are already active then we need to re-initialize ourselves
		if (active)
			onHAStatusBecomeActive();
	}
	
	private void onHAStatusBecomeActive()
	{	
		active = true;
		OmniTicketQueue.getInstance().initialize(XBRCController.getInstance().getReaders(null),
				CMT,omniServers);
	}
	
	private void onHAStatusBecomeInactive()
	{
		active = false;
		OmniTicketQueue.getInstance().stop(true);		
	}

	@Override
	public boolean isEventLogged(String bandId)
	{
		return true;
	}

	public void clear()
	{
		synchronized (GST)
		{
			GST.clear();
			saveGST = true;
		}

		// TODO: clear other status
	}

	public int SaveGST(Connection conn)
	{
		Statement stmt = null;
		PreparedStatement pstmt = null;

		boolean bOldAutoCommit = true;
		logger.trace("Saving GST");
		Date dtStart = new Date();
		try
		{
			bOldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			String sSQL = "DELETE FROM GST";
			stmt = conn.createStatement();
			stmt.execute(sSQL);

			sSQL = "INSERT INTO GST(GuestId, bandRfid, HasXPass, State, LastReader, TimeEarliestAtReader, TimeLatestAtReader, TimeArrived, TimeDeparted, fpScanCount) "
					+ "VALUES(?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sSQL);
			for (String sID : GST.keySet())
			{
				GuestStatus<GuestStatusState> gs = GST.get(sID);
				pstmt.clearParameters();
				pstmt.setString(1, sID);
				pstmt.setString(2, gs.getBandRfid());
				pstmt.setBoolean(3, gs.getHasXpass());
				pstmt.setString(4, gs.getState().toString());
				pstmt.setString(5, gs.getLastReader());
				pstmt.setLong(6, gs.getTimeEarliestAtReader().getTime());
				pstmt.setLong(7, gs.getTimeLatestAtReader().getTime());
				pstmt.setLong(8, gs.getTimeArrived().getTime());
				pstmt.setLong(9, gs.getTimeDeparted().getTime());
				pstmt.setInt(10, gs.getFpScanCount());
				pstmt.execute();
			}

			conn.commit();
			conn.setAutoCommit(bOldAutoCommit);

			Date dtEnd = new Date();
			long msec = dtEnd.getTime() - dtStart.getTime();
			XBRCController.getInstance().getStatus().getPerfSaveGSTMsec()
					.processValue((double) msec);

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

	private void cleanupGuestStatus(GuestStatus<GuestStatusState> gs, Iterator<String> it)
	{
		it.remove();
		saveGST = true;
	}

	@Override
	public void beforeProcessEvents() throws Exception
	{
		OmniResponse resp = OmniTicketQueue.getInstance().getNextMessage();
		while (resp != null)
		{
			if (resp.isReaderStateChange())
				onReaderStateChange(resp.getReaderName());
			else
			{
				saveGST = true;
				GuestStatus<GuestStatusState> gs = GST.get(resp.getAnswer()
						.getHeader().getReferenceNumber());
				handleOmniCommand(resp, gs);
			}
			resp = OmniTicketQueue.getInstance().getNextMessage();
		}

		CastAppMessage msg = CastAppManager.getInstance().getNextMessage();
		while (msg != null)
		{
			handleCastAppMessage(msg);
			msg = CastAppManager.getInstance().getNextMessage();
		}
	}

	@Override
	public void afterProcessEvents() throws Exception
	{
		Date dtNow = new Date();

		if (dtNow.getTime() - oncePerSecondTimer.getTime() < 1000)
			return;
				
		try
		{
			// now, walk through all entries in the GST and process age related
			// state changes
			for (Iterator<String> it = GST.keySet().iterator(); it.hasNext();)
			{
				String sID = it.next();
				GuestStatus<GuestStatusState> gs = GST.get(sID);
	
				// first, handle total abandonment
				Date dtLastAtReader = gs.getTimeLatestAtReader();
				long cmsMsDiff = (dtNow.getTime() - dtLastAtReader.getTime());
				long cmsecDiff = cmsMsDiff / 1000;
	
				switch (gs.getState())
				{
					case HASENTERED:
						if (cmsMsDiff >= ConfigOptions.INSTANCE.getSettings()
								.getGuestRetapTimeoutMs())
						{
							gs.setState(GuestStatusState.DELETABLE);
							saveGST = true;
						}
						break;
					case BLUELANE:
						if (cmsecDiff > ConfigOptions.INSTANCE.getSettings()
								.getAbandonmentTimeSec())
						{
							// Presumably the guest is still at the reader waiting
							// for Cast member to approach.
							// We reset the TimeLatestAtReader so we don't log this
							// message thousands of times.
							gs.setTimeLatestAtReader(new Date());
							logger.info("Guest "
									+ getGuestName(gs)
									+ " is in bluelane status. It must be cleared by Cast App member");
						}
						break;
						
					case DELETABLE:
						if (gs.getTimeLatestAtLrrReader() == null)
						{
							ReaderInfo ri = XBRCController.getInstance().getReader(gs.getLastReader());
							if (ri != null && ri.getLocation().getSendBandStatus())
							{
								logger.info("No long range reads were detected for guest " + gs);
								PEMessageGenerator.publishBandStatusMessage(gs);
							}
							cleanupGuestStatus(gs, it);
							break;
						}
						// no break here, fall through
					case INDETERMINATE:
					{
						if (gs.getTimeLatestAtLrrReader() != null)
						{					
							long cmsMsDiffLrr = (dtNow.getTime() - gs.getTimeLatestAtLrrReader().getTime());
							long cmSecDiffLrr = cmsMsDiffLrr / 1000;
							
							if (cmSecDiffLrr > ConfigOptions.INSTANCE.getSettings()
									.getLrrAbandonmentTimeSec())
							{
								logger.info("Guest " + gs + " is no longer detected by long range readers. Removing guest from GST.");
								
								PEMessageGenerator.publishMessage(
										PEMessageGenerator.TYPE_PARKENTRYABANDON,
										null,
										gs, 
										XBRCController.getInstance().getReader(gs.getLastLrrReader()), new Date());
								
								cleanupGuestStatus(gs, it);
							}
						}
						break;
					}

					default:
						if (cmsecDiff > ConfigOptions.INSTANCE.getSettings()
								.getAbandonmentTimeSec())
						{
							logger.info("Guest " + getGuestName(gs)
									+ " abandoned the reader.");
							gs.setState(GuestStatusState.BLUELANE);
							ReaderExecutor.getInstance().setReaderSequence(
									gs.getPeTransactionState().getReader(),
									Sequence.failure);
							sendCastAppEntitlement(gs, gs.getPeTransactionState()
									.getReader(), "Guest abandoned the reader.",
									gs.getLastOmniEntitlementResponse());
							gs.getPeTransactionState().setBlue(true);
							gs.getPeTransactionState().setAbandoned(true);
							if (gs.getPeTransactionState().getFinishTime() == null)
								gs.getPeTransactionState()
										.setFinishTime(new Date());
							saveGST = true;
	
							updatePETransaction(gs.getPeTransactionState().getTransaction(
									gs.getBandRfid(), gs.getOmniEntitlementResponse(),
									gs.getOmniBioResponse()));							
							
							PEMessageGenerator.publishMessage(
									PEMessageGenerator.TYPE_ABANDONED,
									PEMessageGenerator.REASON_TIMEOUT,
									gs, gs.getPeTransactionState().getReader(), 
									new Date());
						}
				}
			}
			
			CastAppManager.getInstance().timeoutRequests(pgxmsgfactory);
			readerColorManager.timeoutFlash();
			
			if (pendingLocationStatus.get() > 0 && pendingLocationStatusStartTime != null)
			{			
				// Give TOR some time to respond to all of our requests.
				if (dtNow.getTime() - pendingLocationStatusStartTime.getTime() > 20000)
				{
					pendingLocationStatus.set(0);
					pendingLocationStatusStartTime = null;
				}
			}
			
			if (active)
				OmniTicketQueue.getInstance().processTimeouts();
		}
		finally
		{
			oncePerSecondTimer = new Date();
		}
	}

	public void handleOmniCommand(OmniResponse resp,
			GuestStatus<GuestStatusState> gs)
	{
		// TODO: the following selection may need to be done on the
		// basis of cmd.getHeader().getRequestType() instead.

		AnswerCommand cmd = resp.getAnswer();

		if (cmd.getEntitlement() != null)
			handleOmniEntitlement(resp, gs);
		else if (cmd.getLogin() != null)
			handleOmniLogin(resp, gs);
		else if (cmd.getStatus() != null)
			handleOmniStatus(resp, gs);
		else if (cmd.getConnect() != null)
			handleOmniConnect(resp, gs);
		else if (cmd.getHeader().getRequestType()
				.equals(OmniConst.REQ_TYPE_LOGOUT))
			handleOmniLogout(resp, gs);
		else if (cmd.getHeader().getRequestType().equals(OmniConst.REQ_TYPE_ENTITLEMENT))
			handleEmptyOmniEntitlement(resp, gs);
		else
			logger.warn("Ignoring Omni answer of type: "
					+ cmd.getHeader().getRequestType());
	}

	private void handleOmniEntitlement(OmniResponse resp,
			GuestStatus<GuestStatusState> gs)
	{
		AnswerCommand cmd = resp.getAnswer();

		// We must have a guest match to continue.
		if (gs == null)
		{
			logger.error("Got Omni answer for XRFID="
					+ cmd.getHeader().getReferenceNumber()
					+ " but guest no longer in GST. Ignoring.");
			return;
		}

		// First we get the entitlement response and then the bio response...
		if (gs.getOmniEntitlementResponse() == null)
			gs.setOmniEntitlementResponse(cmd);
		else
			gs.setOmniBioResponse(cmd);

		ReaderInfo reader = XBRCController.getInstance().getReader(
				gs.getLastReader());
		if (reader == null)
		{
			logger.error("Failed to find reader: " + gs.getLastReader()
					+ ". Ignoring OMNI entitlement response.");
			return;
		}
		
		saveGST = true;

		boolean idCheckRequired = false;
		boolean askForBioImages = false;

		switch (gs.getState())
		{
			case INDETERMINATE:
			case TAPPED:
			case WAITINGFORENROLL:
			case WAITINGFORMATCH:
			case BLUELANE:
			case DELETABLE:
			case HASENTERED:
			{

				// Set this flag here because the statement is so long.
				idCheckRequired = (cmd.getEntitlement().getEntitlementInfo() != null && cmd
						.getEntitlement().getEntitlementInfo()
						.isIdCheckRequired());

				if (cmd.getEntitlement().getEntitlementInfo() != null &&
					cmd.getEntitlement().getEntitlementInfo().isDecremented())
				{
					logger.trace("handleOmniEntitlement:  HASENTERED "
							+ getGuestName(gs));
					ReaderExecutor.getInstance().setReaderSequence(reader,
							Sequence.success);

					gs.setState(GuestStatusState.HASENTERED);
					gs.setTimeLatestAtReader(new Date());
					sendCastAppEntitlement(gs, reader, "", cmd);
					askForBioImages = true;

					Date now = new Date();
					// We either received a response to BioMatch or the original
					// Entitlement
					if (gs.getPeTransactionState().getOmniEntitlementFinish() == null)
						gs.getPeTransactionState()
								.setOmniEntitlementFinish(now);
					else
						gs.getPeTransactionState().setOmniBioMatchFinish(now);

					gs.getPeTransactionState().setDecremented(true);
					gs.getPeTransactionState().setFinishTime(now);					
					
					updatePETransaction(gs.getPeTransactionState()
							.getTransaction(gs.getBandRfid(),
									gs.getOmniEntitlementResponse(),
									gs.getOmniBioResponse()));

					PEMessageGenerator.publishMessage(
							PEMessageGenerator.TYPE_HASENTERED,
							PEMessageGenerator.REASON_NOTSET,
							gs,reader,new Date());
				}
				// Error code of 9 or less means a positive response. Issue blue
				// light if isIdCheck of error code 10 or above.
				else if ((cmd.getError().getErrorCode() != null && cmd
						.getError().getErrorCode().longValue() > 9l)
						|| idCheckRequired)
				{
					logger.info("handleOmniEntitlement:  BLUELANE ("
							+ cmd.getError().getErrorCode() + ","
							+ cmd.getError().getErrorDescription() + ") "
							+ getGuestName(gs));
					ReaderExecutor.getInstance().setReaderSequence(reader,
							Sequence.failure);
					gs.setState(GuestStatusState.BLUELANE);
					gs.setTimeLatestAtReader(new Date());
					sendCastAppEntitlement(gs, reader, cmd.getError()
							.getErrorDescription(), cmd);
					askForBioImages = true;
					gs.getPeTransactionState().setBlue(true);

					Date now = new Date();
					// We either received a response to BioMatch or the original
					// Entitlement
					if (gs.getPeTransactionState().getOmniEntitlementFinish() == null)
						gs.getPeTransactionState()
								.setOmniEntitlementFinish(now);
					else
						gs.getPeTransactionState().setOmniBioMatchFinish(now);

					gs.getPeTransactionState().setFinishTime(now);
					updatePETransaction(gs.getPeTransactionState()
							.getTransaction(gs.getBandRfid(),
									gs.getOmniEntitlementResponse(),
									gs.getOmniBioResponse()));

					// For now just log the ID check requirement. TODO: start a
					// Cast App conversation here about the ID check...
					if (idCheckRequired)
					{
						logger.info("ID check is required for "
								+ getGuestName(gs));
						PEMessageGenerator.publishMessage(
								PEMessageGenerator.TYPE_BLUELANE,
								PEMessageGenerator.REASON_IDCHECKREQ,
								gs, reader, new Date());
					}
					else
					{
						PEMessageGenerator.publishMessage(
								PEMessageGenerator.TYPE_BLUELANE,
								PEMessageGenerator.REASON_ENTFAILED,
								gs, reader, new Date(), 
								cmd.getError().getErrorCode().intValue(),
								cmd.getError().getErrorDescription());
					}
				}
				else if (cmd.getEntitlement().getEntitlementInfo() != null &&
						 cmd.getEntitlement().getEntitlementInfo().isNewEnrollment())
				{
					logger.trace("handleOmniEntitlement:  WAITINGFORENROLL "
							+ getGuestName(gs));
					// Guest needs bio info to enroll. Ask reader for a scan.
					ReaderExecutor.getInstance().biometricEnroll(reader);
					readerLightEffects.startScan(reader);
					gs.getPeTransactionState().setScanStart(new Date());
					gs.setState(GuestStatusState.WAITINGFORENROLL);
					gs.getPeTransactionState().setOmniEntitlementFinish(
							new Date());
					gs.getPeTransactionState().setNewEnrollment(true);
				}
				else if (cmd.getEntitlement().getEntitlementInfo() != null &&
						 cmd.getEntitlement().getEntitlementInfo().isBioRequired())
				{
					logger.trace("handleOmniEntitlement:  WAITINGFORMATCH "
							+ getGuestName(gs));
					// Guest needs bio info to compare with one on file. Ask
					// reader for a scan.
					ReaderExecutor.getInstance().biometricEnroll(reader);
					readerLightEffects.startScan(reader);
					gs.getPeTransactionState().setScanStart(new Date());
					gs.setState(GuestStatusState.WAITINGFORMATCH);
					gs.getPeTransactionState().setOmniEntitlementFinish(
							new Date());
				}
				else if (cmd.getEntitlement().getEntitlementInfo() != null &&
						 cmd.getEntitlement().getEntitlementInfo().isSimulateBio())
				{
					// TODO: figure out what this is ???
					logger.fatal("Need to figure out what isSimulateBio() means. Ignoring Omni response. "
							+ getGuestName(gs));
				}
				else
				{
					logger.warn("Received unrecognized response from Omni ticket. Ignoring Omni response. "
							+ getGuestName(gs));
				}
				break;
			}
			default:
			{
				logger.info("(handleOmniEntitlement) Guest in unexpected state: "
						+ gs.getState().toString());
			}
		}

		if (askForBioImages && gs.getTemlateId() != null)
		{
			if ((ConfigOptions.INSTANCE.getSaveBioImages() == SaveBioImages.all && (xbioImageCount
					% ConfigOptions.INSTANCE.getSettings()
							.getSaveBioImagesFrequency() == 0))
					|| (ConfigOptions.INSTANCE.getSaveBioImages() == SaveBioImages.failed && gs
							.getState() == GuestStatusState.BLUELANE))
			{
				// ask for bio image
				if (reader != null)
				{
					ReaderExecutor.getInstance().askForBioImageEvent(reader,
							gs.getBandRfid(),
							gs.getPeTransactionState().getTransactionId(),
							gs.getTemlateId());
				}
			}
			// count all available images, not the ones that we insert
			xbioImageCount++;
		}
	}
	
	//
	// Empty entitlement means that even though the response type
	// is Entitlement the entitlement object is not set in the response.
	//
	private void handleEmptyOmniEntitlement(OmniResponse resp,
			GuestStatus<GuestStatusState> gs)
	{
		AnswerCommand cmd = resp.getAnswer();

		// We must have a guest match to continue.
		if (gs == null)
		{
			logger.error("Got Omni answer for XRFID="
					+ cmd.getHeader().getReferenceNumber()
					+ " but guest no longer in GST. Ignoring.");
			return;
		}

		ReaderInfo reader = XBRCController.getInstance().getReader(
				gs.getLastReader());
		if (reader == null)
		{
			logger.error("Failed to find reader: " + gs.getLastReader()
					+ ". Ignoring OMNI entitlement response.");
			return;
		}
		
		// First we get the entitlement response and then the bio response...
		if (gs.getOmniEntitlementResponse() == null)
			gs.setOmniEntitlementResponse(cmd);
		else
			gs.setOmniBioResponse(cmd);

		switch (gs.getState())
		{
			case INDETERMINATE:
			case TAPPED:
			case WAITINGFORENROLL:
			case WAITINGFORMATCH:
			case BLUELANE:
			case DELETABLE:
			case HASENTERED:
			{
				if ((cmd.getError().getErrorCode() != null && cmd.getError().getErrorDescription() != null &&
						cmd.getError().getErrorDescription().equalsIgnoreCase(OmniConst.ERROR_DESC_ACCESS_NOT_PERMITTED)))
				{
					logger.info("handleOmniEntitlement:  BLUELANE ("
							+ cmd.getError().getErrorCode() + ","
							+ cmd.getError().getErrorDescription() + ") "
							+ getGuestName(gs));
					ReaderExecutor.getInstance().setReaderSequence(reader,
							Sequence.failure);
					gs.setState(GuestStatusState.BLUELANE);
					gs.setTimeLatestAtReader(new Date());
					sendCastAppEntitlement(gs, reader, "Reader Closed on TOR", cmd);
					gs.getPeTransactionState().setBlue(true);

					Date now = new Date();
					// We either received a response to BioMatch or the original
					// Entitlement
					if (gs.getPeTransactionState().getOmniEntitlementFinish() == null)
						gs.getPeTransactionState()
								.setOmniEntitlementFinish(now);
					else
						gs.getPeTransactionState().setOmniBioMatchFinish(now);

					gs.getPeTransactionState().setFinishTime(now);
					updatePETransaction(gs.getPeTransactionState()
							.getTransaction(gs.getBandRfid(),
									gs.getOmniEntitlementResponse(),
									gs.getOmniBioResponse()));
					
					PEMessageGenerator.publishMessage(
							PEMessageGenerator.TYPE_BLUELANE,
							PEMessageGenerator.REASON_ENTFAILED,
							gs, reader, new Date(), 
							cmd.getError().getErrorCode().intValue(), 
							cmd.getError().getErrorDescription());
				}
				else
				{
					logger.warn("Received unrecognized response from Omni ticket. Ignoring Omni response. "
							+ getGuestName(gs));
				}
				break;
			}
			default:
			{
				logger.info("(handleOmniEntitlement) Guest in unexpected state: "
						+ gs.getState().toString());
			}
		}
	}

	private void handleOmniLogin(OmniResponse resp,
			GuestStatus<GuestStatusState> gs)
	{
		// This message is an answer to our Login request for a Cast Member.
		String keys[] = resp.getAnswer().getHeader().getReferenceNumber()
				.split("[\t]");
		String portalId = keys[0];
		String locationName = keys[1];
		CMST cms = getCastMemberStateByLocation(locationName);
		if (cms == null || !cms.getPortalId().equals(portalId))
		{
			logger.error("Ignoring OMNI login answer for cast no longer in Cast Member Status Table (CMST), portalId: "
					+ portalId);
			return;
		}		

		if (resp.isReceivedAll())
		{
			// Respond back to the greeter app. The greeter app will determine
			// the success or failure of the
			// login operation by examining the state of the invidual readers
			// included in the location message.			
						
			// Fix for bug 6382: Do not transition to LOGGEDIN state if no reader logged in to TOR.
			boolean someOpen = false;
			Collection<OmniTicketReaderQueue> locQueues = OmniTicketQueue.getInstance().getQueuesByLocation().get(locationName);
			if (locQueues != null && locQueues.size() > 0)
			{				
				for (OmniTicketReaderQueue queue : locQueues)
				{
					if (queue.getConnectionState().getReaderState() == State.open)
					{
						someOpen = true;
						break;
					}
				}				
			}
			
			if (pendingLocationStatus.get() > 0)
				pendingLocationStatus.decrementAndGet();
			
			CastAppMessage req = CastAppManager.getInstance().getLocationRequest(
					locationName, portalId);
			
			// Bail out of here is there are no open readers at this location.
			if (!someOpen)
			{
				logger.warn("(greeter) Failed to open/bump location for Cast "
						+ portalId + " and location " + locationName);
				
				synchronized (CMT)
				{
					CMT.remove(cms.getLocationName());
				}
				saveCMST = true;
				
				if (req == null)
				{
					// The request could be missing if the xBRC was restarted while it had locations opened.
					logger.warn("(greeter) Failed to find a matching open/bump location request for Cast "
							+ portalId + " and location " + locationName);
					return;
				}
								
				PGXMessage gresp;
				if (req.getMessage().getType() == Type.bumpLocation)
				{
					gresp = pgxmsgfactory.createBumpLocationResponse(req
							.getMessage());					
				}
				else
				{
					gresp = pgxmsgfactory.createOpenLocationResponse(req
							.getMessage());
				}				
				
				gresp.getLocation().setResponse(PGXLocation.Response.error);
				gresp.getLocation().setErrorCode(ErrorCode.noReadersConnectedToOmniAtLocation);
				String msg = (resp.getAnswer().getError() != null && resp.getAnswer().getError().getErrorDescription() != null) ? 
						resp.getAnswer().getError().getErrorDescription() : "Could not login any of the readers to TOR";
				gresp.getLocation().setErrorDescription(msg);
				CastAppManager.getInstance().respond(req, gresp);
				
				return;
			}
			
			cms.setState(CastMemberState.LOGGEDIN);
			cms.setOmniNumericId(resp.getAnswer().getLogin().getUserInfo()
					.getUser().getNumericId().toString());
			saveCMST = true;						

			if (req != null)
			{
				PGXMessage gresp;
				if (req.getMessage().getType() == Type.bumpLocation)
				{
					gresp = pgxmsgfactory.createBumpLocationResponse(req
							.getMessage());
					
					PEMessageGenerator.publishMessage(PEMessageGenerator.TYPE_CAST_BUMP, cms, new Date());
				}
				else
				{
					gresp = pgxmsgfactory.createOpenLocationResponse(req
							.getMessage());
									
					PEMessageGenerator.publishMessage(PEMessageGenerator.TYPE_CAST_OPEN, cms, new Date());
				}
				
				gresp.getLocation().setResponse(PGXLocation.Response.success);
				CastAppManager.getInstance().respond(req, gresp);
			}	
			else
			{
				// The request could be missing if the xBRC was restarted while it had locations opened.
				logger.warn("(greeter) Failed to find a matching open/bump location request for Cast "
						+ portalId + " and location " + locationName);
				
				PEMessageGenerator.publishMessage(PEMessageGenerator.TYPE_CAST_OPEN, cms, new Date());
			}

			PGXMessage notify = pgxmsgfactory
					.createLocationStateChangeNotification(locationName, null);
			CastAppManager.getInstance().broadcast(notify);
		}
	}
	
	private void closeLocation(CMST cms, PGCast byWhoom, boolean clearGuestStatus)
	{		
		synchronized (CMT)
		{
			CMT.remove(cms.getLocationName());
		}
		saveCMST = true;
		
		if (pendingLocationStatus.get() > 0)
			pendingLocationStatus.decrementAndGet();					
		
		PEMessageGenerator.publishMessage(PEMessageGenerator.TYPE_CAST_CLOSE, cms, new Date());
		
		PGXMessage notify = pgxmsgfactory
				.createLocationStateChangeNotification(cms.getLocationName(), byWhoom);
		CastAppManager.getInstance().broadcast(notify);		
		
		if (!clearGuestStatus)
			return;
		
		for (ReaderInfo ri : XBRCController.getInstance().getReaders(cms.getLocationName()))
		{
			if (!ReaderType.isTapReader(ri.getType()))
				continue;
			
			GuestStatus<GuestStatusState> gs = getGuestStatusFromLastReader(ri.getName());
			if (gs != null)
			{
				logger.warn("Clearing guest status for guest " + gs.getGuestID() + " because location " + cms.getLocationName() + " is being closed");
				resetReaderStatus(ri, byWhoom != null ? byWhoom.getPortalId() : "", "");
			}
		}
	}

	private void handleOmniLogout(OmniResponse resp,
			GuestStatus<GuestStatusState> gs)
	{
		ReaderInfo ri = getReaderByDeviceId(resp.getAnswer().getDeviceID());
		if (ri == null)
			return;

		String locationName = ri.getLocation().getName();

		if (resp.isReceivedAll())
		{
			boolean someLoggedIn = false;

			// We must check if all readers are actually logged off before we
			// remove the CMT entry.
			// It is possible for one reader to get logged off while others are
			// still logged in.
			Collection<OmniTicketReaderQueue> queues = OmniTicketQueue
					.getInstance().getQueuesByLocation().get(locationName);
			if (queues != null)
			{
				for (OmniTicketReaderQueue queue : queues)
				{
					if (queue.getConnectionState() == ConnectionState.LoggedIn)
					{
						someLoggedIn = true;
						break;
					}
				}
			}
			CastAppMessage req = CastAppManager.getInstance()
					.getLocationRequest(locationName, null);
			
			boolean bump = req != null && req.getMessage().getType() == Type.bumpLocation;
			
			if (someLoggedIn)
			{
				logger.warn("Received reader logout notification from Omni for reader "
						+ ri.getName()
						+ " but other readers are still connected to Omni. Leaving the location "
						+ locationName + " open");
			}
			else
			{
				CMST cms = getCastMemberStateByLocation(locationName);
				if (cms != null)
				{
					logger.warn("Omni logged out the cast member. Removing CMST entry.");
					closeLocation(cms, req != null ? req.getMessage().getCast() : null, !bump);					
				}
			}

			
			if (req != null)
			{
				// If the logout was a result of a bumpLocation request then we
				// now need to proceed
				// to open the location for the new cast member.
				if (bump)
				{
					handleCastAppOpenLocation(req);
					return;
				}

				PGXMessage gresp = pgxmsgfactory
						.createCloseLocationResponse(req.getMessage());
				gresp.getLocation().setResponse(PGXLocation.Response.success);
				CastAppManager.getInstance().respond(req, gresp);				
			}
		}
	}

	private void handleOmniStatus(OmniResponse resp,
			GuestStatus<GuestStatusState> gs)
	{
		if (resp.getAnswer().getStatus().getUserInfo() != null)
			handleOmniStatusLoggedIn(resp);
		else
			handleOmniStatusLoggedOut(resp);
	}

	private void handleOmniStatusLoggedIn(OmniResponse resp)
	{
		if (!resp.isReceivedAll())
			return;

		String locationName = resp.getAnswer().getHeader().getReferenceNumber();
		CMST cms = getCastMemberStateByLocation(locationName);
		boolean logout = false;

		if (cms != null)
		{
			if (cms.getState() != CastMemberState.LOGGEDIN)
			{
				logger.warn("Omni status does not match CMST cast member satus. Removing CMST entry.");
				synchronized (CMT)
				{
					CMT.remove(cms.getLocationName());
				}
				saveCMST = true;
				logout = true;
			}
			
			if (pendingLocationStatus.get() > 0)
				pendingLocationStatus.decrementAndGet();
		}
		else
		{
			logger.warn("Omni status says cast member logged in, but no entry in CMST table.");
			logout = true;
		}

		if (logout)
		{
			logger.warn("(greeter) Logging cast member out of TOR at location "
					+ locationName);
			OmniTicketQueue.getInstance().logoffCastMember(locationName);
		}
		else if (cms != null)
		{
			// Set the idle sequence for all logged in readers
			Collection<OmniTicketReaderQueue> queues = OmniTicketQueue
					.getInstance().getQueuesByLocation().get(locationName);
			
			if (queues != null)
			{
				for (OmniTicketReaderQueue queue : queues)
				{
					ReaderExecutor
							.getInstance()
							.setIdleSequence(
									queue.getReader(),
									queue.getConnectionState() == ConnectionState.LoggedIn);
				}
			}

			PGXMessage notify = pgxmsgfactory
					.createLocationStateChangeNotification(locationName, null);
			CastAppManager.getInstance().broadcast(notify);
		}
	}

	private void handleOmniStatusLoggedOut(OmniResponse resp)
	{
		if (!resp.isReceivedAll())
			return;

		// 
		// For high availability to work we re-logon cast member to Omni so that during a fail over to slave if we switch 
		// the TOR server then the cast member would not need to re-open the location. 
		//
		String locationName = resp.getAnswer().getHeader().getReferenceNumber();
		CMST cms = getCastMemberStateByLocation(locationName);
		if (cms != null)
		{
			logger.warn("(greeter) Omni status says cast member is not logged in, but we have an entry in CMST table. Trying to re-open location " 
						+ locationName + " for cast member " + cms.getPortalId());
			
			relogonCastMember(cms);
			return;
		}
		
		if (pendingLocationStatus.get() > 0)
			pendingLocationStatus.decrementAndGet();

		// Set the idle sequence for all logged out readers
		Collection<OmniTicketReaderQueue> queues = OmniTicketQueue
				.getInstance().getQueuesByLocation().get(locationName);
		
		if (queues != null)
		{
			for (OmniTicketReaderQueue queue : queues)
			{
				ReaderExecutor.getInstance().setIdleSequence(queue.getReader(),false);
			}
		}

		PGXMessage notify = pgxmsgfactory
				.createLocationStateChangeNotification(locationName, null);
		CastAppManager.getInstance().broadcast(notify);
	}

	private void handleOmniConnect(OmniResponse resp,
			GuestStatus<GuestStatusState> gs)
	{
		// we should not be getting this one
	}

	public void handleCastAppMessage(CastAppMessage msg)
	{

		switch (msg.getMessage().getType())
		{
			case bumpLocation:
				handleCastAppBumpLocation(msg);
				break;
			case closeLocation:
				handleCastAppCloseLocation(msg);
				break;
			case getLocationMap:
				break;
			case locationStatus:
				handleCastAppLocationStatus(msg);
				break;
			case openLocation:
				handleCastAppOpenLocation(msg);
				break;
			case readerRedirect:
				handleCastAppReaderRedirect(msg);
				break;
			case readerRetry:
				handleCastAppReaderRetry(msg);
				break;
			case scheduleBioMaintenance:
				handleCastAppScheduleBioMaintenance(msg);
				break;
			case scheduleMaintenance:
				handleCastAppScheduleMaintenance(msg);
				break;
			case flashLocation:
				handleCastAppFlashLocation(msg);
				break;
			case shutdownReader:
				handleCastAppShutdownReader(msg);
				break;
			case subscribeToLocation:
				handleCastAppSubscribeToLocation(msg);
				break;
			case unsubscribeFromLocation:
				handleCastAppUnsubscribeFromLocation(msg);
				break;
			default:
				logger.warn("(greeter) Ignoring unexpected cast app message type: "
						+ msg.getMessage().getType());
		}
	}

	private void onReaderStateChange(String readerName)
	{
		ReaderInfo ri = XBRCController.getInstance().getReader(readerName);
		if (ri == null)
			return;
		
		// Don't send reader state changes for readers in the UNKNOWN location
		if (ri.getLocation().getName().equals("UNKNOWN"))
			return;

		PGXMessage notify = pgxmsgfactory
				.createLocationStateChangeNotification(ri.getLocation()
						.getName(), null);
		CastAppManager.getInstance().broadcast(notify);
	}

	private void relogonCastMember(CMST cms)
	{
		try
		{
			// Initiate logon to the OmniTickete system.
			OmniTicketQueue.getInstance().logonCastMember(
					cms.getLocationName(),
					cms.getPortalId(),
					ConfigOptions.INSTANCE.getSettings().getOmniid(),
					ConfigOptions.INSTANCE.getSettings().getOmnipassword());

			cms.setState(CastMemberState.LOGGEDIN);

			saveCMST = true;
		}
		catch (CastAppException e)
		{
			// logonCastMember will throw an exception if the cast member cannot
			// log in at this time.
			logger.warn("(greeter) Cannot re-logon cast member " + cms.getPortalId() + " to location " + cms.getLocationName() + " " + e.getMessage());
			closeLocation(cms, null, true);
			
			PGXMessage notify = pgxmsgfactory
					.createLocationStateChangeNotification(cms.getLocationName(), null);
			CastAppManager.getInstance().broadcast(notify);
		}
	}
	
	private void logonCastMember(CastAppMessage req, CMST cms)
	{
		try
		{
			// Initiate logon to the OmniTickete system.
			OmniTicketQueue.getInstance().logonCastMember(
					req.getMessage().getLocationName(),
					req.getMessage().getCast().getPortalId(),
					ConfigOptions.INSTANCE.getSettings().getOmniid(),
					ConfigOptions.INSTANCE.getSettings().getOmnipassword());

			cms.setState(CastMemberState.WAITINGLOGONRESP);

			saveCMST = true;
		}
		catch (CastAppException e)
		{
			// logonCastMember will throw an exception if the cast member cannot
			// log in at this time.
			logger.warn("(greeter) " + e.getMessage());

			synchronized (CMT)
			{
				CMT.remove(req.getMessage().getLocationName());
			}
			saveCMST = true;

			PGXMessage resp = pgxmsgfactory.createOpenLocationResponse(req
					.getMessage());
			resp.getLocation().setResponse(PGXLocation.Response.error);
			resp.getLocation().setErrorCode(e.getErrorCode());
			resp.getLocation().setErrorDescription(e.getMessage());
			CastAppManager.getInstance().respond(req, resp);
		}
	}

	private boolean isLocationOperationInProgress(CastAppMessage req)
	{
		CastAppMessage pendreq = CastAppManager.getInstance()
				.getLocationRequest(req.getMessage().getLocationName(), null);
		if (pendreq == null || pendreq == req)
			return false;

		PGXMessage resp = pgxmsgfactory.createProcessingErrorResponse(req
				.getMessage());

		String error = "Currently processing " + pendreq.getMessage().getType()
				+ " request by cast "
				+ pendreq.getMessage().getCast().getPortalId()
				+ " for location " + pendreq.getMessage().getLocationName();

		logger.warn("(greeter) " + error);

		resp.setErrorDescription(error);

		CastAppManager.getInstance().respond(req, resp);

		return true;
	}
	
	private boolean isLocationOpenOnTOR(String locationName)
	{
		PGXMessage msg = pgxmsgfactory.createLocationStateChangeNotification(locationName, null);
		for (PGXReader r : msg.getLocation().getReaders())
			if (r.getState() == State.open)
				return true;
				
		return false;
	}

	private void handleCastAppOpenLocation(CastAppMessage req)
	{
		// Just fail if the request has timed out
		if (req.isRequestTimedOut())
		{
			logger.warn("Cast app openLocation request timed out for location "
					+ req.getMessage().getLocationName());

			CMST cms = getCastMemberStateByLocation(req.getMessage()
					.getLocationName());
			if (cms != null)
			{
				synchronized (CMT)
				{
					CMT.remove(cms.getLocationName());
				}
				saveCMST = true;
			}

			// Must cancel further automatic logon attempts.
			OmniTicketQueue.getInstance().cancelLogonCastMember(
					req.getMessage().getLocationName());

			sendCastAppServerErrorResponse(req);
			return;
		}

		// check for current location operations
		if (isLocationOperationInProgress(req))
			return;

		if (req.getMessage().getCast() == null
				|| req.getMessage().getCast().getPortalId() == null
				|| req.getMessage().getCast().getPortalId().isEmpty())
		{
			logger.error("Ignoring cast app logon requests for location "
					+ req.getMessage().getLocationName()
					+ " because the portalId is empty.");
			return;
		}

		CMST cms = getCastMemberStateByLocation(req.getMessage()
				.getLocationName());

		if (cms != null)
		{
			//
			// Same cast member trying to open the location again.
			//
			if (cms.getPortalId().equals(
					req.getMessage().getCast().getPortalId()))
			{
				logger.warn("(greeter) Cast "
						+ req.getMessage().getCast().getPortalId()
						+ " re-opened the same location: "
						+ req.getMessage().getLocationName());
				PGXMessage resp = pgxmsgfactory.createOpenLocationResponse(req
						.getMessage());
				resp.getLocation().setResponse(PGXLocation.Response.success);
				CastAppManager.getInstance().respond(req, resp);
				
				
				try
				{
					OmniTicketQueue.getInstance().logonCastMember(
							req.getMessage().getLocationName(),
							req.getMessage().getCast().getPortalId(),
							ConfigOptions.INSTANCE.getSettings().getOmniid(),
							ConfigOptions.INSTANCE.getSettings().getOmnipassword());
				}
				catch (CastAppException e)
				{
					logger.warn("(greeter) Failed to initiate cast member logon for location " + req.getMessage().getLocationName(), e);
				}
				
				return;
			}

			//
			// Different cast member is trying to open already opened location.
			//
			if (isLocationOpenOnTOR(req.getMessage().getLocationName()))
			{
				String errorDescription = "Rejecting cast "
						+ req.getMessage().getCast().getPortalId()
						+ " trying to open location: "
						+ req.getMessage().getLocationName()
						+ " already opened by: " + cms.getPortalId();
		
				logger.warn("(greeter) " + errorDescription);
				PGXMessage resp = pgxmsgfactory.createOpenLocationResponse(req
						.getMessage());
				resp.getLocation().setResponse(PGXLocation.Response.error);
				resp.getLocation().setErrorCode(ErrorCode.locationOccupied);
				resp.getLocation().setErrorDescription(errorDescription);
				CastAppManager.getInstance().respond(req, resp);
				return;
			}
			else
			{
				// This location is not really open. There are no readers that are logged into TOR.
				// It is possible that the xBRC got out of synch with TOR.
				synchronized (CMT)
				{
					CMT.remove(cms.getLocationName());
				}
				saveCMST = true;
			}
		}

		// Create a cast member state
		cms = new CMST();
		cms.setLocationName(req.getMessage().getLocationName());
		cms.setPortalId(req.getMessage().getCast().getPortalId());
		synchronized (CMT)
		{
			CMT.put(cms.getLocationName(), cms);
		}
		saveCMST = true;

		logonCastMember(req, cms);
	}

	private void handleCastAppLocationStatus(CastAppMessage req)
	{
		PGXMessage resp = pgxmsgfactory.createLocationStatusResponse(req
				.getMessage());
		CastAppManager.getInstance().respond(req, resp);
	}

	private void handleCastAppBumpLocation(CastAppMessage req)
	{
		// Just fail if the request has timed out
		if (req.isRequestTimedOut())
		{
			sendCastAppServerErrorResponse(req);
			return;
		}

		// check for current location operations
		if (isLocationOperationInProgress(req))
			return;

		CMST cms = getCastMemberStateByLocation(req.getMessage()
				.getLocationName());

		if (cms != null)
		{
			if (cms != null
					&& cms.getPortalId().equals(
							req.getMessage().getCast().getPortalId()))
			{
				logger.info("(greeter) Cast "
						+ req.getMessage().getCast().getPortalId()
						+ " tried to bump himself from location: "
						+ req.getMessage().getLocationName());
				PGXMessage resp = pgxmsgfactory.createOpenLocationResponse(req
						.getMessage());
				resp.getLocation().setResponse(PGXLocation.Response.success);
				CastAppManager.getInstance().respond(req, resp);
				return;
			}

			//
			// Bump the user
			//
			OmniTicketQueue.getInstance().logoffCastMember(
					cms.getLocationName());
			logger.info("(greeter) Cast member "
					+ req.getMessage().getCast().getPortalId()
					+ " bumped cast member " + cms.getPortalId()
					+ " out of location: " + cms.getLocationName());
		}
		else
			// No one at this location, try opening it then.
			handleCastAppOpenLocation(req);
	}

	private void handleCastAppCloseLocation(CastAppMessage req)
	{
		// Timeout means that omni failed to repond to our logoff requests. Just
		// close the location.
		if (req.isRequestTimedOut())
		{
			CMST cms = getCastMemberStateByLocation(req.getMessage()
					.getLocationName());
			if (cms != null)
			{
				logger.warn("(greeter) Close location request to Omni timed out. Removing CMST entry.");
				closeLocation(cms, req.getMessage().getCast(), true);
			}
			
			PGXMessage resp = pgxmsgfactory.createCloseLocationResponse(req
					.getMessage());
			resp.getLocation().setResponse(PGXLocation.Response.success);
			CastAppManager.getInstance().respond(req, resp);
			return;
		}

		// check for current location operations
		if (isLocationOperationInProgress(req))
			return;

		OmniTicketQueue.getInstance().logoffCastMember(
				req.getMessage().getLocationName());

		CMST cms = getCastMemberStateByLocation(req.getMessage()
				.getLocationName());
		if (cms == null)
		{
			logger.warn("(greeter) Processing close location request for already closed location: "
					+ req.getMessage().getLocationName());
			logger.info("(greeter) Logging cast unknown out of location: "
					+ req.getMessage().getLocationName());
		}
		else
			logger.info("(greeter) Logging cast " + cms.getPortalId()
					+ " out of location: " + req.getMessage().getLocationName());
	}

	private void handleCastAppFlashLocation(CastAppMessage req)
	{
		String color = readerColorManager.flashNextAvailable(req.getMessage()
				.getLocationName());
		PGXMessage resp = pgxmsgfactory.createFlashLocationResponse(req
				.getMessage());

		if (color == null)
		{
			resp.getLocation().setResponse(PGXLocation.Response.error);
			resp.getLocation().setErrorCode(ErrorCode.allFlashColorsAreBusy);
			resp.getLocation()
					.setErrorDescription(
							"All available flash colors are currently being used. Please try again later.");
		}
		else
		{
			logger.trace("(greeter) Flashing location "
					+ req.getMessage().getLocationName() + " " + color
					+ " color");
			resp.getLocation().setResponse(PGXLocation.Response.success);
			resp.getLocation().setFlashColor(color);
		}

		CastAppManager.getInstance().respond(req, resp);
	}

	private void handleCastAppReaderRedirect(CastAppMessage req)
	{
		ReaderInfo reader = XBRCController.getInstance().getReader(
				req.getMessage().getReader().getName());
		if (reader == null)
		{
			PGXMessage resp = pgxmsgfactory.createProcessingErrorResponse(req
					.getMessage());
			resp.setErrorDescription("Reader "
					+ req.getMessage().getReader().getName() + " not found");
			CastAppManager.getInstance().respond(req, resp);
			return;
		}

		resetReaderStatus(reader, "", "");

		PGXMessage resp = pgxmsgfactory.createReaderRedirectResponse(reader,
				req.getMessage());
		resp.getReader().setResponse(PGXReader.Response.success);
		CastAppManager.getInstance().respond(req, resp);
	}

	private void handleCastAppReaderRetry(CastAppMessage req)
	{
		ReaderInfo reader = XBRCController.getInstance().getReader(
				req.getMessage().getReader().getName());
		if (reader == null)
		{
			PGXMessage resp = pgxmsgfactory.createProcessingErrorResponse(req
					.getMessage());
			resp.setErrorDescription("Reader "
					+ req.getMessage().getReader().getName() + " not found");
			CastAppManager.getInstance().respond(req, resp);
			return;
		}

		resetReaderStatus(reader, "", "");

		PGXMessage resp = pgxmsgfactory.createReaderRetryResponse(reader,
				req.getMessage());
		resp.getReader().setResponse(PGXReader.Response.success);
		CastAppManager.getInstance().respond(req, resp);
	}

	private void handleCastAppSubscribeToLocation(CastAppMessage req)
	{
		try
		{
			CastAppManager.getInstance().subscribeToLocation(
					req.getMessage().getLocationName(),
					req.getMessage().getSessionId());
			PGXMessage resp = pgxmsgfactory
					.createSubscribeToLocationResponse(req.getMessage());
			CastAppManager.getInstance().respond(req, resp);
		}
		catch (CastAppException e)
		{
			PGXMessage resp = pgxmsgfactory.createProcessingErrorResponse(req
					.getMessage());
			resp.setErrorDescription(e.getMessage());
			CastAppManager.getInstance().respond(req, resp);
		}
	}

	private void handleCastAppUnsubscribeFromLocation(CastAppMessage req)
	{
		CastAppManager.getInstance().unsubscribeFromLocation(
				req.getMessage().getLocationName(),
				req.getMessage().getSessionId());
		PGXMessage resp = pgxmsgfactory
				.createUnsubscribeFromLocationResponse(req.getMessage());
		CastAppManager.getInstance().respond(req, resp);
	}

	private void sendReaderNotFoundResponse(CastAppMessage req,
			String readerName)
	{
		PGXMessage resp = pgxmsgfactory.createProcessingErrorResponse(req
				.getMessage());
		resp.setErrorDescription("Reader " + readerName + " not found");
		CastAppManager.getInstance().respond(req, resp);
	}

	private void handleCastAppScheduleBioMaintenance(CastAppMessage req)
	{
		ReaderInfo reader = XBRCController.getInstance().getReader(
				req.getMessage().getReader().getName());
		if (reader == null)
		{
			sendReaderNotFoundResponse(req, req.getMessage().getReader()
					.getName());
			return;
		}

		PGXMessage resp = pgxmsgfactory.createScheduleBioMaintenanceResponse(
				reader, req.getMessage());
		resp.getReader().setResponse(PGXReader.Response.success);
		logger.info("(greeter) Received ScheduleBioMaintenace request for reader "
				+ req.getMessage().getReader().getName());
		CastAppManager.getInstance().respond(req, resp);
	}

	private void handleCastAppScheduleMaintenance(CastAppMessage req)
	{
		ReaderInfo reader = XBRCController.getInstance().getReader(
				req.getMessage().getReader().getName());
		if (reader == null)
		{
			sendReaderNotFoundResponse(req, req.getMessage().getReader()
					.getName());
			return;
		}

		PGXMessage resp = pgxmsgfactory.createScheduleMaintenanceResponse(
				reader, req.getMessage());
		resp.getReader().setResponse(PGXReader.Response.success);
		logger.info("(greeter) Received ScheduleMaintenace request for reader "
				+ req.getMessage().getReader().getName());
		CastAppManager.getInstance().respond(req, resp);
	}

	private void handleCastAppShutdownReader(CastAppMessage req)
	{
		ReaderInfo reader = XBRCController.getInstance().getReader(
				req.getMessage().getReader().getName());
		if (reader == null)
		{
			sendReaderNotFoundResponse(req, req.getMessage().getReader()
					.getName());
			return;
		}

		XBRCController.getInstance().disableReader(reader,
				req.getMessage().getReader().getShutdownReason());
		OmniTicketQueue.getInstance().shutdownReader(
				req.getMessage().getReader().getName());

		PGXMessage resp = pgxmsgfactory.createShutdownReaderResponse(reader,
				req.getMessage());
		resp.getReader().setResponse(PGXReader.Response.success);
		logger.info("(greeter) Received ShutdownReader request for reader "
				+ req.getMessage().getReader().getName());
		CastAppManager.getInstance().respond(req, resp);

		PGXMessage notify = pgxmsgfactory
				.createLocationStateChangeNotification(reader.getLocation()
						.getName(), req.getMessage().getCast());
		CastAppManager.getInstance().broadcast(notify);
	}

	private void sendCastAppServerErrorResponse(CastAppMessage cam)
	{
		if (cam.isInternal())
			return;
		
		PGXMessage resp = pgxmsgfactory.createProcessingErrorResponse(cam
				.getMessage());
		logger.trace("(greeter) Sending Internal xBRC server error to session id "
				+ cam.getQueue().getSessionId());
		resp.setErrorDescription("Internal xBRC server error");
		cam.getQueue().send(resp);
	}

	/*
	 * Return true if it was a cast member tap, false otherwise.
	 */
	private boolean handleCastMemberTapEvent(TapEventAggregate ev)
	{
		CastMember cm = ConfigOptions.INSTANCE.getCastMembers().get(ev.getID());
		if (cm == null)
		{
			logger.trace("(greeter) BandId " + ev.getID()
					+ " is not in the CastMember table. Assuming guest tap.");
			return false;
		}
		
		logger.trace("(greeter) Received cast member tap RFID: " + ev.getID()
				+ " Name: " + cm.getName());
		
		if (cm.getTapAction() == CastMemberTapAction.RunReaderTest)
		{
			Xband xb = new Xband();
			xb.setActive(true);
			xb.setBandFriendlyName(cm.getName());
			xb.setBandType("ReaderTest");
			xb.setTapId(ev.getID());
			xb.setPublicId(ev.getPidDecimal());
			
			processReaderTestTap(ev, xb, null);
			return true;
		}

		CMST cms = getCastMemberStateByLocation(ev.getReaderInfo()
				.getLocation().getName());		

		if (cms != null)			
		{
			resetReaderStatus(ev.getReaderInfo(), "", ev.getID());
			return true;
		}
	
		logger.info("(greeter) Received cast app tap event to open location. RFID: " + ev.getID() + 
				" Location: " + ev.getReaderInfo().getLocation().getName());
		
		PGCast cast = new PGCast();
		cast.setPortalId(cm.getName());
		cast.setDeviceId("");
		cast.setOmniId("");
		
		PGMessage msg = new PGMessage();
		msg.setClientReference("Cast tap to open location: " + ev.getID());			
		msg.setCast(cast);
		msg.setLocationName(ev.getReaderInfo().getLocation().getName());
		msg.setType(Type.openLocation);
		CastAppMessage req = new CastAppMessage(null, msg);
		CastAppManager.getInstance().addInternalRequest(req);

		return true;
	}
	
	private void processReaderTestTap(EventAggregate ev, Xband xb, Guest guest)
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
			processReaderTestTap(ev, xb, guest);
			return true;
		}
		
		return false;
	}

	private void resetReaderStatus(ReaderInfo reader, String portalId,
			String castMemberRFID)
	{
		GuestStatus<GuestStatusState> gs = null;
		gs = getGuestStatusFromLastReader(reader.getName());
		if (gs != null)
		{
			logger.info("(greeter) Cast member " + portalId + " with RFID "
					+ castMemberRFID + " reset the guest status for guest id: "
					+ gs.getGuestID());
			gs.setState(GuestStatusState.DELETABLE);
			saveGST = true;
		}
		// reset the reader
		ReaderExecutor.getInstance().resetBioTapReader(reader);

		// Light change notification
		PGXMessage message = pgxmsgfactory
				.createReaderEventNotification(reader);
		message.getReader().setLights(Lights.off);
		CastAppManager.getInstance().notify(message);
	}

	private void sendCastAppEntitlement(GuestStatus<GuestStatusState> gs,
			ReaderInfo reader, String reason, AnswerCommand cmd)
	{

		// Light change notification
		PGXMessage msg = pgxmsgfactory.createReaderEventNotification(gs
				.getPeTransactionState().getReader());
		msg.getReader().setLights(
				gs.getState() == GuestStatusState.HASENTERED ? Lights.green
						: Lights.blue);

		if (msg.getReader().getLights() == Lights.green)
			msg.getReader().setLightDuration(
					ConfigOptions.INSTANCE.getSettings()
							.getGreenLightTimeoutMs());
		else
			msg.getReader().setLightDuration(0);

		PGGuest guest = new PGGuest();

		if (gs.getGuest() != null)
		{
			guest.setFirstName(gs.getGuest().getFirstName());
			guest.setLastName(gs.getGuest().getLastName());

			if (gs.getCelebrations() != null)
			{
				// Protect the asynchronous IDMSResolver lookup
				synchronized (gs.getCelebrations())
				{
					// Date now = new Date();

					LinkedList<Celebration> celebrations = new LinkedList<Celebration>();
					for (Celebration cel : gs.getCelebrations())
					{
						// TODO: figure out how to properly tell if this
						// celebration is celebrated today
						// if (cel.getMonth().equals(now.getDay()) &&
						// cel.getMonth().equals(now.getMonth()))
						// {
						celebrations.add(cel);
						// }
					}
					if (!celebrations.isEmpty())
						guest.setCelebrations(celebrations);
				}
			}
		}

		if ((guest.getLastName() == null || guest.getLastName().isEmpty())
				&& cmd != null && cmd.getEntitlement() != null
				&& cmd.getEntitlement().getEntitlementInfo() != null)
		{
			EntitlementInfo ei = cmd.getEntitlement().getEntitlementInfo();
			guest.setBioRequired(ei.isBioRequired());
			guest.setIdCheckRequired(ei.isIdCheckRequired());
			if (ei.getEntitlementConfiguration() != null)
				guest.setFlashAtTurnstile(ei.getEntitlementConfiguration()
						.isFlashAtTurnstile());

			// Get Last/First name of the guest
			if (ei.getSeasonPassInfo() != null
					&& ei.getSeasonPassInfo().getDemographicData() != null
					&& ei.getSeasonPassInfo().getDemographicData().getField() != null)
			{
				for (Field field : ei.getSeasonPassInfo().getDemographicData()
						.getField())
				{
					if (field.getFieldType() == 3
							&& field.getFieldValue() != null)
					{
						String keys[] = field.getFieldValue().split("[/]");
						if (keys.length == 2)
						{
							guest.setLastName(keys[0]);
							guest.setFirstName(keys[1]);
						}
						else
						{
							guest.setLastName(field.getFieldValue());
						}
					}
				}
			}
		}

		msg.getReader().setErrorDescription(reason);
		msg.setGuest(guest);

		CastAppManager.getInstance().notify(msg);

		/*
		 * CastAppManager.getInstance().notify(msg); Message msg = new
		 * Message(CastAppConst.MSG_TYPE_ENTITLEMENT_STATUS); msg.setRfid(new
		 * RFId()); msg.getRfid().setBandId(gs.getBandRfid());
		 * msg.getRfid().setReader(reader.getLocation().getName());
		 * msg.getRfid().setLane(reader.getLane()); msg.setGuest(new
		 * Message.Guest()); msg.getGuest().setCelebrations(new
		 * LinkedList<Celebration>()); msg.getGuest().setId(gs.getGuestID()); if
		 * (gs.getGuest() != null) {
		 * msg.getGuest().setFname(gs.getGuest().getFirstName());
		 * msg.getGuest().setLname(gs.getGuest().getLastName()); }
		 * msg.setStatus(new Status()); msg.getStatus().setValid(gs.getState()
		 * == GuestStatusState.HASENTERED ? CastAppConst.STATUS_OK :
		 * CastAppConst.STATUS_FAIL); msg.getStatus().setReason(reason); if (cmd
		 * != null) { msg.setError(cmd.getError());
		 * msg.setEntitlement(cmd.getEntitlement()); }
		 */
	}

	private CMST getCastMemberStateByLocation(String locationName)
	{
		// I don't think we need this synchronize here, but I saw a
		// concurrent modification exception in the log while removing an
		// expired
		// cast app member entry in afterProcessEvents()
		synchronized (CMT)
		{
			return CMT.get(locationName);
		}
	}

	private String getGuestName(GuestStatus<GuestStatusState> gs)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(gs.getGuestID());
		if (gs.getGuest() != null)
		{
			sb.append(" ");
			sb.append(gs.getGuest().getFirstName());
			sb.append(" ");
			sb.append(gs.getGuest().getLastName());
		}
		return sb.toString();
	}

	private ReaderInfo getReaderByDeviceId(int deviceId)
	{
		for (ReaderInfo ri : XBRCController.getInstance().getReaders(null))
		{
			if (!ReaderType.isTapReader(ri.getType()))
				continue;
			
			if (ri.getDeviceId() == deviceId)
				return ri;
		}
		return null;
	}

	@Override
	public String storeConfiguration(int cIndentLevel) throws Exception
	{
		return StoredConfiguration.storeConfiguration(cIndentLevel);
	}

	@Override
	public void restoreConfiguration(Connection conn, String sXML)
			throws Exception
	{
		StoredConfiguration.restoreConfiguration(conn, sXML);
	}

	@Override
	public void formatStatus(XbrcStatus status)
	{	
		// If we are not active (slave or unknown) then just bail out.
		if (!active)
			return;
	
		// Check readers for fatal problems. Do not report warnings here as they
		// are part of normal operating procedure.
		
		for (ReaderInfo ri : XBRCController.getInstance().getReaders(null))
		{
			if (!ReaderType.isTapReader(ri.getType()))
				continue;
			
			OmniReaderInfoStatus rs = OmniTicketQueue.getInstance()
					.getReaderStatus(ri);
			
			if (rs != null && rs.getStatus() == StatusType.Red)
			{
				status.setStatus(rs.getStatus());
				status.setStatusMessage(ri.getName() + ":"
						+ rs.getStatusMessage());
				return;
			}
		}
	}

	@Override
	public void formatReaderStatus(ReaderInfo ri)
	{
		// If we are not active (slave or unknown) then just bail out.
		if (!active)
			return;
		
		if (!ReaderType.isTapReader(ri.getType()))
			return;
				
		OmniReaderInfoStatus status = OmniTicketQueue.getInstance()
				.getReaderStatus(ri);
		if (status != null)
		{
			if (ri.getStatus().isParameterMoreSevere(status.getStatus()))
			{
				ri.setStatus(status.getStatus());
				ri.setStatusMessage(status.getStatusMessage());
			}
		}
	}

	@Override
	public void handlePropertiesRead(XbrcConfig config, Connection conn) throws Exception
	{

		com.disney.xband.xbrc.parkentrymodel.ConfigOptions.Settings settings = com.disney.xband.xbrc.parkentrymodel.ConfigOptions.INSTANCE
				.getSettings();

		// get fresh version from the database
		Config.getInstance().read(conn, settings);

		if (config.getConfiguration() != null)
			config.getConfiguration().addAll(Configuration.convert(settings));
		else
			config.setConfiguration(Configuration.convert(settings));
	}

	@Override
	public void handlePropertiesWrite(XbrcConfig config, Connection conn)
			throws Exception
	{

		com.disney.xband.xbrc.parkentrymodel.ConfigOptions.Settings peci = com.disney.xband.xbrc.parkentrymodel.ConfigOptions.INSTANCE
				.getSettings();

		Configuration.convert(config.getConfiguration(), peci);
		Config.getInstance().write(conn, peci);
	}

	@Override
	public void storeState(Connection conn)
	{

		if (saveGST)
		{
			// Now save the GST.
			SaveGST(conn);
			saveGST = false;
		}

		if (saveCMST)
		{
			// Now save the CMST.
			CMSTService.saveCMST(conn, CMT);
			saveCMST = false;
		}

	}

	@Override
	public void restoreState(Connection conn, Date dtLastStateStore)
	{
		// TODO Auto-generated method stub

	}

	private boolean canProcessRequest(Request request, Response response)
	{
		// Do not allow xGreeter connections if we are not active or waiting for location status responses during failover
		if (!active || pendingLocationStatus.get() > 0)
		{
			logger.warn("(greeter) Ignoring greeter subscription because the xBRC is still initializing or is a slave");
			
			if (pendingLocationStatus.get() > 0)
				logger.warn("Still waiting on " + pendingLocationStatus.get() + " location status resonse(s) from TOR");
			
			Date dtStart = new Date();
			do
			{
				try {
					// We can in a context of a Web Server request thread so we can sleep here without blocking the process thread.
					Thread.sleep(1000);
				}
				catch (InterruptedException e){}
			} 
			while((!active || pendingLocationStatus.get() > 0) && (new Date().getTime() - dtStart.getTime() < 60000));
			
			if (!active || pendingLocationStatus.get() > 0)
			{
				logger.warn("Timed out waiting for the xBRC to become active. Ignoring cast app request");
				returnCode(response, 500, "xBRC Server Not Active");
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void processExternal(Request request, Response response, String sPath)
	{
		String requestIp = request.getClientAddress().getAddress().toString()
				.substring(1);
		
		if (sPath.startsWith("/model/greeter/subscribe"))
		{				
			if (!canProcessRequest(request,response))
				return;
			
			if (request.isKeepAlive())
			{
				try
				{
					CastAppQueue newSubscription = CastAppManager.getInstance()
							.addSubscription(request, response);
					// If this is a new subscription then push location state
					// notification for all locations.
					// This is useful when the xBRC fails over to the slave
					// without the xGreeter app noticing the switch.
					if (newSubscription != null)
					{
						List<PGXMessage> messages = pgxmsgfactory
								.createLocationStateChangeNotificationList(null);
						CastAppManager.getInstance().unicast(messages,
								newSubscription);
					}
					return;
				}
				catch (CastAppException e)
				{
					returnCode(response, 500, e.getMessage());
				}
				catch (IOException e)
				{
					returnCode(response, 500, e.getMessage());
				}
				return;
			}

			logger.error("(greeter) Subscription failed " + sPath + " from "
					+ requestIp + " because keep alive was not set.");
		}

		if (sPath.startsWith("/model/greeter/message"))
		{
			try
			{
				if (!canProcessRequest(request,response))
					return;
				
				// Cannot process messages if we are not active or waiting for location status during failover.
				if (!active || pendingLocationStatus.get() > 0)
				{
					logger.warn("(greeter) Ignoring greeter message because HA status is " + XBRCController.getInstance().getHaStatus());
					if (pendingLocationStatus.get() > 0)
						logger.warn("Still waiting on " + pendingLocationStatus.get() + " location status resonse(s) from TOR");
					returnCode(response, 200, "");
					return;
				}
				
				CastAppManager.getInstance().addRequest(request, response);
				return;
			}
			catch (IOException e)
			{
				logger.error("(greeter) Failed to process request " + sPath
						+ " from " + requestIp, e);
			}
		}
		else
		{
			logger.warn("(greeter) Received unrecognized request " + sPath
					+ " from " + requestIp);
		}

		return404(response);
	}

	private void returnCode(Response response, int code, String error)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(code);
			PrintStream body = response.getPrintStream();
			body.println(error);
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format("Error sending response", e));
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
			logger.error(ExceptionFormatter.format(
					"Error sending 404 response", e));
		}
		finally
		{
			if (body != null)
			{
				try
				{
					body.close();
				}
				catch (Exception ignore)
				{
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
		return "1.0.0.34";
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
			ReaderInfo ri = XBRCController.getInstance().getReader(
					gs.getLastReader());
			if (ri == null)
				continue;

			if (locationIds.contains(Long.valueOf(ri.getLocation().getId())))
			{
				// TODO: review. We can just do g = gs.getGuest() no?
				Guest g = IDMSResolver.INSTANCE.getGuestFromGuestId(gs
						.getGuestID());
				
				if (g == null) {
					if (gs.getPidDecimal() != null && !gs.getPidDecimal().isEmpty())
						guests.add(XbrJsonMapper.publicIdToLongRangeId(Long.parseLong(gs.getPidDecimal())));
					continue;
				}

				for (Xband xb : g.getXbands())
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
		for (HAMessage ham : aHA)
		{
			try
			{
				if (ham instanceof HAStatusMessage)
				{
					processHAStatusMessage((HAStatusMessage) ham);					
				}
				else if (ham.getMessageType().startsWith("CAST"))
				{
					processHACastMessage(ham);
				}
			}
			catch (Exception ex)
			{
				logger.error(ExceptionFormatter.format(
						"Error process HA message of type " + ham.getMessageType(), ex));
			}
		}
	}
	
	private void processHAStatusMessage(HAStatusMessage haStatusMsg)
	{
		// If we just switched from slave to master then we need to
		// make sure
		// that the state of the physical readers reflects the state
		// of locations.
		
		if (haStatusMsg.getCurrentStatus() == HAStatusEnum.master
				&& haStatusMsg.getPreviousStatus() == HAStatusEnum.slave)
		{
			logger.info("Changed HA state from slave to master. Synchronizing reader state for all locations.");
						
			Collection<ReaderInfo> readers = XBRCController.getInstance().getReaders(null);			
			
			for (ReaderInfo ri : readers)
			{
				if (!ReaderType.isTapReader(ri.getType()))
					continue;
				
				CMST cms = getCastMemberStateByLocation(ri
						.getLocation().getName());
				
				if (cms == null)
					ReaderExecutor.getInstance().setIdleSequence(
							ri, false);
				else
					ReaderExecutor.getInstance().setIdleSequence(
							ri, true);
			}
									
			synchronized(CMT)
			{
				// Keep track of how many locations we need to get status for from TOR while
				// we reject all greeter communication.
				if (CMT.values().size() > 0)
				{
					pendingLocationStatus.set(CMT.values().size());
					pendingLocationStatusStartTime = new Date();
				}
			}
		}
		
		if (haStatusMsg.getCurrentStatus() == HAStatusEnum.master ||
			haStatusMsg.getCurrentStatus() == HAStatusEnum.solo)
			onHAStatusBecomeActive();
		else
			onHAStatusBecomeInactive();
	}
	
	private void processHACastMessage(HAMessage ham) throws JAXBException
	{
		CastMessagePayload payload = XmlUtil.convertToPojo(new ByteArrayInputStream(ham.getMessageText().getBytes()), MessagePayload.class, CastMessagePayload.class);
		PECastMessage cm = payload.getMessage();
	
		CMST remove = null;
		CMST add = null;
		CMST old = getCastMemberStateForLoction(cm.getLocationName());
		Date dtMessage = DateUtils.parseDate(cm.getTimestamp());
		
		if (old != null && old.getStateChangeTime().getTime() > dtMessage.getTime())
		{
			logger.warn("Ignoring HA message of type " + ham.getMessageType() + " because message time is older than current cast member state entry.");
			logger.warn(ham.getMessageText());
			return;
		}
			
		if (cm.getMessageType().equals(PEMessageGenerator.TYPE_CAST_OPEN))
		{				
			if (old != null)
			{
				if (!old.getPortalId().equals(cm.getPortalId()))
					logger.warn("Received HA message of type " + cm.getMessageType() + " for a location " + old.getLocationName() + 
								" opened by another cast member " + old.getPortalId());				
			}
						
			remove = old;
			add = new CMST();			
		}		
		else if (cm.getMessageType().equals(PEMessageGenerator.TYPE_CAST_BUMP))
		{
			remove = old;
			add = new CMST();			
		}
		else if (cm.getMessageType().equals(PEMessageGenerator.TYPE_CAST_CLOSE))
		{
			remove = old;
		}
		else
		{
			logger.error("Received HA message of unsupported type " + cm.getMessageType());			
		}
				
		if (add != null)
		{
			add.setLocationName(cm.getLocationName());
			add.setPortalId(cm.getPortalId());
			add.setState(CastMemberState.LOGGEDIN);
			add.setStateChangeTime(dtMessage);
		}
		
		if (remove != null)
		{
			synchronized(CMT)
			{
				logger.trace("HA: Removing cast member state for location " + remove.getLocationName() + " and cast member " + remove.getPortalId());
				CMT.remove(remove.getLocationName());
				saveCMST = true;
			}
		}
		
		if (add != null)
		{
			synchronized(CMT)
			{
				logger.trace("HA: Adding cast member state for location " + add.getLocationName() + " and cast member " + add.getPortalId());
				CMT.put(add.getLocationName(), add);
				saveCMST = true;
			}
		}
	}

	/*
	 * For now, this returns just an empty structure.
	 * 
	 * @see
	 * com.disney.xband.xbrc.lib.model.IXBRCModel#serializeStateToXML(java.lang
	 * .String)
	 */
	@Override
	public String serializeStateToXML(String sGuestID)
	{
		String sAll = "";

		try
		{
			GSTInfo gsti = new GSTInfo();
			gsti.setName(XBRCController.getInstance().getVenueName());
			gsti.setTime(DateUtils.format(new Date().getTime()));

			gsti.setGuests(new ArrayList<GuestStatus<GuestStatusState>>());

			for (String sID : GST.keySet())
			{
				if (sGuestID != null && !sID.equals(sGuestID))
					continue;
				GuestStatus<GuestStatusState> gs = GST.get(sID);
				gsti.getGuests().add(gs);
			}

			gsti.setCmstlist(new ArrayList<CMST>());
			synchronized (CMT)
			{
				for (CMST cmst : CMT.values())
					gsti.getCmstlist().add(cmst);
			}

			sAll = XmlUtil.convertToPartialXml(gsti, gsti.getClass());
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Error formatting response",
					ex));
		}
		return sAll;
	}

	@Override
	public void deserializeStateFromXML(String sXML)
	{
		try
		{
			GSTInfo gsti = XmlUtil.convertToPojo(
					new ByteArrayInputStream(sXML.getBytes()), GSTInfo.class);

			if (gsti.getCmstlist() != null)
			{
				// Synchronize the cast member state. Don't worry about the
				// guests.
				synchronized (CMT)
				{
					CMT.clear();
					for (CMST cms : gsti.getCmstlist())
						CMT.put(cms.getLocationName(), cms);
				}
				saveCMST = true;
			}
			else
			{
				logger.warn("Received state xml without the cast member state. Cannot synchronize the state to master.");
				logger.warn(sXML);
			}
		}
		catch (JAXBException e)
		{
			logger.error("Failed to deserialize state from xml. ", e);
			logger.error(sXML);
		}
	}

	@Override
	public int getIDMSResolverThreads()
	{
		if (XBRCController.getInstance().getReaders(null) == null)
			return 1;

		int tapCount = 0;
		// Count how many tap readers we have to process guest events
		for (ReaderInfo ri : XBRCController.getInstance().getReaders(null))
		{
			if (ReaderType.isTapReader(ri.getType()))
				tapCount++;
		}

		// One thread per 10 readers should be sufficient since it takes a while
		// for the guest to do the bio scan
		// after which the guest info is being checked.
		return Math.max(1, tapCount / 10);
	}

	@Override
	public boolean isTapSequenceEnabled(ReaderInfo ri)
	{
		if (getCastMemberStateByLocation(ri.getLocation().getName()) == null)
			return false;
		
		return ri.isEnabled() && ri.getType() == ReaderType.xfpxbio;
	}
	
	@Override
	public boolean isIdleSequenceEnabled(ReaderInfo ri)
	{
		// same as tap sequence
		return isTapSequenceEnabled(ri);
	}

	@Override
	public void beforeConfigurationDeleteFromTable(Connection conn,
			String tableName, Collection<Object> ids) throws Exception
	{
		Statement stmt = null;
		try
		{
			Statement stmt2 = null;
			try
			{
				stmt2 = conn.createStatement();
				if (tableName.equals("Reader"))
				{
					if (ids == null || ids.isEmpty())
					{
						stmt2.execute("DELETE FROM ReaderAntenna");
					}
					else
					{
						for (Object id : ids)
						{
							stmt2.execute("DELETE FROM ReaderAntenna where readerId = " + (Long) id);
						}
					}
				}
			}
			catch (Exception ex) { }
			finally
			{
				if (stmt2 != null)
					stmt2.close();
			}
			
			if (tableName.equals("Reader"))
			{
				stmt = conn.createStatement();
				if (ids == null || ids.isEmpty())
				{
					stmt.execute("DELETE FROM ReaderOmniServer");
				}
				else
				{
					for (Object id : ids)
					{
						stmt.execute("DELETE FROM ReaderOmniServer where readerid = "
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
		return false; // park entry does not have waypoint locations at this time
	}
	
	@Override
	/**
	 * Flattens the model section of the /currentconfiguration xml. Expects the xml
	 * to be formatted as follows:
	 * 
	 * <venue name="iwona" time="2013-05-01T22:22:16.298">
	 * 		<configuration name="current" type="full">
	 * 			<model>
	 * 				<parkEntryModelConfig>
	 * 					<castMember>
	 * 						<bandId>arek</bandId>
	 * 						<enabled>true</enabled>
	 * 						<externalId/>
	 * 						<id>13</id>
	 * 						<name>Arek</name>
	 * 						<omniPassword>11111</omniPassword>
	 * 						<omniUsername>800011</omniUsername>
	 * 					</castMember>
	 * 					...
	 * 					<readerOmniServers>
	 *						<omniServer>
	 *							<active>false</active>
	 *							<hostname>localhost</hostname>
	 *							<id>1</id>
	 *							<port>0</port>
	 *						</omniServer>
	 *						...
	 *						<readerOmniServer>
	 *							<omniserverid>1</omniserverid>
	 *							<priority>0</priority>
	 *							<readerid>10</readerid>
	 *						</readerOmniServer>
	 *						...
	 *					</readerOmniServers>
	 *				</parkEntryModelConfig>
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

		final String path = model.getParentElement().getName() + pathSeparator 
				+ model.getName() + pathSeparator
				+ "parkEntryModelConfig" + pathSeparator;
		
		Element modelConfig = model.getChild("parkEntryModelConfig");
		if (modelConfig == null)
			return;
		
		/*
		 * process cast members
		 */
		final List castMembers = modelConfig.getChildren("castMember");
		
		Element castMember = null;
		List castMemberElements = null;
		String castMemberId = null;
		StringBuffer castMemberPath = null;
		Element castMemberElement = null;

		for (int i = 0; i < castMembers.size(); i++) 
		{
			castMemberPath = new StringBuffer(path);

			castMember = (Element)castMembers.get(i);
			castMemberId = castMember.getChildText("bandId");
			castMemberElements = castMember.getChildren();

			castMemberPath.append(castMember.getName()).append(pathSeparator)
				.append("id").append(pathSeparator)
				.append(castMemberId).append(pathSeparator);

			for (int j = 0; j < castMemberElements.size(); j++)
			{
				castMemberElement = (Element)castMemberElements.get(j);

				if (exclude != null && exclude.contains(castMemberElement.getName()))
					continue;

				results.put(castMemberPath.toString() + castMemberElement.getName(), castMemberElement.getValue());
			}
		}
		
		/*
		 * process omni servers
		 */
		Element readerOmniServers = modelConfig.getChild("readerOmniServers");
		if (readerOmniServers == null)
			return;
		
		final List omniServers = readerOmniServers.getChildren("omniServer");
		
		Element omniServer = null;
		List omniServerElements = null;
		String omniServerId = null;
		StringBuffer omniServerPath = null;
		Element omniServerElement = null;

		for (int i = 0; i < omniServers.size(); i++) 
		{
			omniServerPath = new StringBuffer(path);

			omniServer = (Element)omniServers.get(i);
			omniServerId = omniServer.getChildText("id");
			omniServerElements = omniServer.getChildren();

			omniServerPath.append(omniServer.getName()).append(pathSeparator)
				.append("id").append(pathSeparator)
				.append(omniServerId).append(pathSeparator);

			for (int j = 0; j < omniServerElements.size(); j++)
			{
				omniServerElement = (Element)omniServerElements.get(j);

				if (exclude != null && exclude.contains(omniServerElement.getName()))
					continue;

				results.put(omniServerPath.toString() + omniServerElement.getName(), omniServerElement.getValue());
			}
		}
		
		/*
		 * process reader omni servers
		 */
		final List rOmniServers = readerOmniServers.getChildren("readerOmniServer");
		
		Element rOmniServer = null;
		List rOmniServerElements = null;
		String rOmniServerId = null;
		StringBuffer rOmniServerPath = null;
		Element rOmniServerElement = null;

		for (int i = 0; i < rOmniServers.size(); i++) 
		{
			rOmniServerPath = new StringBuffer(path);

			rOmniServer = (Element)rOmniServers.get(i);
			rOmniServerId = rOmniServer.getChildText("omniserverid");
			rOmniServerElements = rOmniServer.getChildren();

			rOmniServerPath.append(rOmniServer.getName()).append(pathSeparator)
				.append("omniserverid").append(pathSeparator)
				.append(rOmniServerId).append(pathSeparator);

			for (int j = 0; j < rOmniServerElements.size(); j++)
			{
				rOmniServerElement = (Element)rOmniServerElements.get(j);

				if (exclude != null && exclude.contains(rOmniServerElement.getName()))
					continue;

				results.put(rOmniServerPath.toString() + rOmniServerElement.getName(), rOmniServerElement.getValue());
			}
		}
	}

	@Override
	public void registerSchedulerItemsMetadata(XconnectScheduler scheduler) throws Exception {
		ParkEntrySchedulerHelper.registerClasses();
	}

	@Override
	public void addDefaultSchedulerItems(XconnectScheduler scheduler) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdown() {

		// If we are a slave then nothing to do.
		if (!active)
			return;
		
		// Turn the lights off on all the readers in currently opened locations
		
		Map<String, Collection<OmniTicketReaderQueue>> queuesByLocation = 
				OmniTicketQueue.getInstance().getQueuesByLocation();

        if (queuesByLocation == null || queuesByLocation.isEmpty())
			return;
        
        for (CMST cmst : CMT.values())
        {
        	if (cmst.getState() != CastMemberState.LOGGEDIN)
        		continue;
        	
        	Collection<OmniTicketReaderQueue> queues = queuesByLocation.get(cmst.getLocationName());
        	
        	if (queues == null)
        		continue;
        	
			for (OmniTicketReaderQueue queue : queues)
			{
				ReaderExecutor.getInstance().setIdleSequence(queue.getReader(),false);
			}
        }        
	}
}
