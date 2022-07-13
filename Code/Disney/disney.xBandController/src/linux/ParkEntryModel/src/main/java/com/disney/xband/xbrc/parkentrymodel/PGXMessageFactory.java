package com.disney.xband.xbrc.parkentrymodel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.disney.xband.parkgreeter.lib.message.PGCast;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.ErrorCode;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXLocation;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXMessage;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader.Lights;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader.State;
import com.disney.xband.parkgreeter.lib.message.toxbrc.PGMessage;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;

/*
 * Park Greeter application message factory. 
 */
public class PGXMessageFactory
{	
	private CEP cep;
	
	public PGXMessageFactory(CEP cep)
	{
		this.cep = cep;
	}
		
	private PGXMessage createMessage(PGXMessage.Type type, String clientReference)
	{
		PGXMessage msg = new PGXMessage();
		msg.setVersion("1.0");
		msg.setType(type);
		msg.setClientReference(clientReference);
		return msg;
	}
	
	private PGXMessage createMessage(PGXMessage.Type type)
	{
		return createMessage(type, null);
	}
	
	
	private PGXLocation createPGXLocation(String locationName)
	{
		PGXLocation loc = new PGXLocation();
		loc.setName(locationName);
		CMST cmst = cep.getCastMemberStateForLoction(locationName);
		if (cmst != null)
		{
			PGCast cast = new PGCast();
			cast.setPortalId(cmst.getPortalId());
			cast.setOmniId(cmst.getOmniNumericId());
			loc.setCast(cast);
		}
		return loc;
	}
	
	private PGXReader createPGXReader(OmniTicketReaderQueue queue)
	{
		PGXReader reader = new PGXReader();
		reader.setName(queue.getReader().getName());
		
		OmniServerItem os = queue.getOmniServer();
		if (os != null)
		{
			reader.setTor(os.getOmniServer().getHostname() + ":" + os.getOmniServer().getPort());
			reader.setTorDescription(os.getOmniServer().getDescription());
		}
		
		if (!queue.getReader().isEnabled())
		{
			reader.setState(PGXReader.State.shutdown);
			reader.setErrorCode(ErrorCode.readerIsDisabled);
			
			if (queue.getReader().getDisabledReason() != null && !queue.getReader().getDisabledReason().isEmpty())
				reader.setErrorDescription(queue.getReader().getDisabledReason());
			else
				reader.setErrorDescription("Reader has been shutdown.");
		}
		else
		{
			reader.setState(queue.getConnectionState().getReaderState());
				
			if (reader.getState().equals(State.shutdown))
			{
				OmniReaderInfoStatus status = queue.getReaderStatus();
				reader.setErrorCode(status.getErrorCode());
				if (status.getStatusMessage() != null && !status.getStatusMessage().isEmpty())
					reader.setErrorDescription(status.getStatusMessage());
			}
			else if (!XBRCController.getInstance().isReaderAlive(queue.getReader()))
			{
				reader.setState(PGXReader.State.shutdown);
				reader.setErrorCode(ErrorCode.readerNotResponding);
				reader.setErrorDescription("Reader is not responding");
			}
			else if (queue.getReader().getType() != ReaderType.xfpxbio)
			{
				reader.setState(PGXReader.State.shutdown);
				reader.setErrorCode(ErrorCode.readerHardwareProblem);
				reader.setErrorDescription("Biometric device not working");
			}
		}
		
		return reader;
	}
	
	private void setReaderLights(PGXReader reader)
	{
		GuestStatus<GuestStatusState> gs = cep.getGuestStatusFromLastReader(reader.getName());
		if (gs != null && gs.getState() == GuestStatusState.BLUELANE)
		{
			reader.setLights(Lights.blue);
			return;
		}
		
		reader.setLights(Lights.off);
	}
	
	private void populateReadersForLocation(PGXLocation loc)
	{
		Map<String, Collection<OmniTicketReaderQueue>> queuesByLocation = 
				OmniTicketQueue.getInstance().getQueuesByLocation();
		
		if (queuesByLocation == null)
			return;
		
		Collection<OmniTicketReaderQueue> queues = queuesByLocation.get(loc.getName());
		
		if (queues == null || queues.isEmpty())
			return;
		
		loc.setReaders(new LinkedList<PGXReader>());
		
		for (OmniTicketReaderQueue queue : queues)
		{
			PGXReader reader = createPGXReader(queue);
			setReaderLights(reader);
			loc.getReaders().add(reader);
		}
	}
	
	private void populateAllLocations(PGXMessage msg)
	{
		Map<String, Collection<OmniTicketReaderQueue>> queuesByLocation = 
				OmniTicketQueue.getInstance().getQueuesByLocation();

        msg.setLocations(new LinkedList<PGXLocation>());

        if (queuesByLocation == null || queuesByLocation.isEmpty())
			return;
		
		for (Map.Entry<String, Collection<OmniTicketReaderQueue>> entry : queuesByLocation.entrySet())
		{
			String locationName = entry.getKey();
		    Collection<OmniTicketReaderQueue> queues = entry.getValue();
		
		    PGXLocation loc = createPGXLocation(locationName);		    
		    msg.getLocations().add(loc);
		    
			if (queues == null || queues.isEmpty())
				continue;
		
			loc.setReaders(new LinkedList<PGXReader>());
		
			for (OmniTicketReaderQueue queue : queues)
			{
				PGXReader reader = createPGXReader(queue);
				setReaderLights(reader);
				loc.getReaders().add(reader);
			}
		}
	}
	
	/***************************************************************************
	 * Response Creation
	 ***************************************************************************/
	
	public PGXMessage createOpenLocationResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.openLocationResponse, req.getClientReference());
		msg.setLocation(createPGXLocation(req.getLocationName()));
		populateReadersForLocation(msg.getLocation());
		
		return msg;
	}
	
	public PGXMessage createBumpLocationResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.bumpLocationResponse, req.getClientReference());
		msg.setLocation(createPGXLocation(req.getLocationName()));
		populateReadersForLocation(msg.getLocation());
		
		return msg;
	}
	
	public PGXMessage createCloseLocationResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.closeLocationResponse, req.getClientReference());
		msg.setLocation(createPGXLocation(req.getLocationName()));
		populateReadersForLocation(msg.getLocation());
		
		return msg;
	}
	
	public PGXMessage createFlashLocationResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.flashLocationResponse, req.getClientReference());
		msg.setLocation(createPGXLocation(req.getLocationName()));
		populateReadersForLocation(msg.getLocation());
		
		return msg;
	}
	
	public PGXMessage createProcessingErrorResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.processingError, req.getClientReference());
		return msg;
	}
	
	public PGXMessage createSubscribeToLocationResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.subscribeToLocationResponse, req.getClientReference());
		return msg;
	}
	
	public PGXMessage createUnsubscribeFromLocationResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.unsubscribeToLocationResponse, req.getClientReference());
		return msg;
	}
	
	public PGXMessage createScheduleBioMaintenanceResponse(ReaderInfo reader, PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.scheduleBioMaintenanceResponse, req.getClientReference());
		OmniTicketReaderQueue queue = OmniTicketQueue.getInstance().getQueueByDeviceId(reader.getDeviceId());		
		PGXReader pgxreader = createPGXReader(queue);
		msg.setReader(pgxreader);
		return msg;
	}
	
	public PGXMessage createScheduleMaintenanceResponse(ReaderInfo reader, PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.scheduleMaintenanceResponse, req.getClientReference());
		OmniTicketReaderQueue queue = OmniTicketQueue.getInstance().getQueueByDeviceId(reader.getDeviceId());		
		PGXReader pgxreader = createPGXReader(queue);
		msg.setReader(pgxreader);
		return msg;
	}
	
	public PGXMessage createShutdownReaderResponse(ReaderInfo reader, PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.shutdownReaderResponse, req.getClientReference());
		OmniTicketReaderQueue queue = OmniTicketQueue.getInstance().getQueueByDeviceId(reader.getDeviceId());		
		PGXReader pgxreader = createPGXReader(queue);
		msg.setReader(pgxreader);
		return msg;
	}
	
	public PGXMessage createLocationStatusResponse(PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.locationStatusResponse, req.getClientReference());		
		populateAllLocations(msg);
		return msg;
	}
	
	public PGXMessage createReaderRedirectResponse(ReaderInfo reader, PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.readerRedirectResponse, req.getClientReference());
		OmniTicketReaderQueue queue = OmniTicketQueue.getInstance().getQueueByDeviceId(reader.getDeviceId());		
		PGXReader pgxreader = createPGXReader(queue);
		msg.setReader(pgxreader);
		return msg;
	}
	
	public PGXMessage createReaderRetryResponse(ReaderInfo reader, PGMessage req)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.readerRetryResponse, req.getClientReference());
		OmniTicketReaderQueue queue = OmniTicketQueue.getInstance().getQueueByDeviceId(reader.getDeviceId());		
		PGXReader pgxreader = createPGXReader(queue);
		msg.setReader(pgxreader);
		return msg;
	}
	
	public PGXMessage createReaderEventNotification(ReaderInfo reader)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.readerEvent);
		OmniTicketReaderQueue queue = OmniTicketQueue.getInstance().getQueueByDeviceId(reader.getDeviceId());		
		PGXReader pgxreader = createPGXReader(queue);
		msg.setReader(pgxreader);
		return msg;
	}
	
	public PGXMessage createLocationStateChangeNotification(String locationName, PGCast byWhoom)
	{
		PGXMessage msg = createMessage(PGXMessage.Type.locationStateChange);
		msg.setLocation(createPGXLocation(locationName));
		msg.getLocation().setByWhom(byWhoom);
		populateReadersForLocation(msg.getLocation());
		
		return msg;
	}
	
	public List<PGXMessage> createLocationStateChangeNotificationList(PGCast byWhoom)
	{		
		List<PGXMessage> messages = new LinkedList<PGXMessage>();
		
		Map<String, Collection<OmniTicketReaderQueue>> queuesByLocation = 
				OmniTicketQueue.getInstance().getQueuesByLocation();
		
		if (queuesByLocation == null || queuesByLocation.isEmpty())
			return messages;
		
		for (String location : queuesByLocation.keySet()) 
		{			
			PGXMessage msg = createLocationStateChangeNotification(location, byWhoom);
			messages.add(msg);
		}
		
		return messages;
	}
}
