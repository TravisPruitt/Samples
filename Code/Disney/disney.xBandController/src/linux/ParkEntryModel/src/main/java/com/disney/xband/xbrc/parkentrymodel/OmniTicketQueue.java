package com.disney.xband.xbrc.parkentrymodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.lib.xbrapi.ReaderExecutor;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.ErrorCode;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.parkentrymodel.OmniTicketReaderQueue.ConnectionState;

/*
 * This class provides communication with the OmniTicket (AKA Tor) system.
 */
public class OmniTicketQueue { 
	
	private static Logger logger = Logger.getLogger(OmniTicketQueue.class);
	private static Logger plogger = Logger.getLogger("performance");
	
	private Map<String,CMST> CMT;
	
	private LinkedList<OmniResponse> inqueue;
	private HashMap<Integer,OmniTicketReaderQueue> queues;
	private HashMap<String, Collection<OmniTicketReaderQueue>> queuesByLocation;
	private HashMap<String, CastMemberCreds> credsByLocation;
	 

	private static class SingletonHolder { 
		public static final OmniTicketQueue instance = new OmniTicketQueue();
	}
	
	public static OmniTicketQueue getInstance() {
		return SingletonHolder.instance;
	}
	
	private OmniTicketQueue() {
		inqueue = new LinkedList<OmniResponse>();
		queues = new HashMap<Integer,OmniTicketReaderQueue>();
		queuesByLocation = new HashMap<String, Collection<OmniTicketReaderQueue>>();
		credsByLocation = new HashMap<String, CastMemberCreds>();
	}
	
	public void initialize(Collection<ReaderInfo> readers, Map<String,CMST> CMT, Map<Long, List<OmniServerItem>> omniServers) {
		
		this.CMT = CMT;
		int tapReaderCount = 0;
		
		for (ReaderInfo ri : readers)
			if (ReaderType.isTapReader(ri.getType()) && !ri.getLocation().getName().equals("UNKNOWN"))
				tapReaderCount++;
		
		// Deal with re-initialization. Process deleted readers, new readers, and modified readers.
		if (!queues.isEmpty()) {
			
			LinkedList<OmniTicketReaderQueue> deleted = new LinkedList<OmniTicketReaderQueue>();
			LinkedList<OmniTicketReaderQueue> modified = new LinkedList<OmniTicketReaderQueue>();
			
			// Find deleted or modified readers and log them off if necessary.
			for (OmniTicketReaderQueue queue : queues.values()) {
				ReaderInfo match = null;
				for (ReaderInfo r : readers) {
					
					// skip unlinked readers
					if (r.getLocation().getName().equals("UNKNOWN"))
						continue;
					
					// skip readers other than tap
					if (!ReaderType.isTapReader(r.getType()))	
						continue;					
					
					if (r.getId() == queue.getReader().getId()) {
						match = r;
						// Check if any of the reader settings have changed
						if (r.getDeviceId() != queue.getDeviceId() || 
							r.getLocation().getId() != queue.getReader().getLocation().getId() ||
							r.getLane() != queue.getReader().getLane() ||
							r.isEnabled() != queue.getReader().isEnabled() ||
							r.getType() != queue.getReader().getType()) {
							modified.add(queue);
						}
						else {
							// Check if the list of OmniServers for that reader has changed
							boolean changed = false;
							List<OmniServerItem> oldList = queue.getOmniServers();
							List<OmniServerItem> newList = omniServers.get(new Long(r.getId()));
							if (oldList == null && newList != null)
								changed = true;
							else if (oldList != null && newList == null)
								changed = true;
							else if (oldList != null || newList != null)
							{
								Iterator<OmniServerItem> oldIt = oldList.iterator();
								Iterator<OmniServerItem> newIt = newList.iterator();
								
								while (oldIt.hasNext() && newIt.hasNext())
									if (!oldIt.next().getKey().equals(newIt.next().getKey()))
										changed = true;
								
								// both iterators should be empty now
								changed = changed || (oldIt.hasNext() || newIt.hasNext());
							}
							if (changed)
								modified.add(queue);
						}
						break;
					}
				}
				
				if (match == null) {
					deleted.add(queue);
				}
				else {
					// must replace the reader object with the new one
					queue.setReader(match);		
				}
			}
			
			// Check if there were any reader changes that we need to worry about.
			if (deleted.size() == 0 && modified.size() == 0 && tapReaderCount == queues.size()) {
				return;
			}
			
			logger.info("Readers have been modified. Re-initializing readers....");
						
			for (OmniTicketReaderQueue queue : deleted) {
				if (queue.getConnectionState() == ConnectionState.LoggedIn) {					
					logger.info("Reader " + queue.getReader().getName() + " was deleted or unlinked. Logging the reader off from TOR.");
					// Reset the login to stop automatic re-logon
					queue.shutdown();
				}
			}
			
			for (OmniTicketReaderQueue queue : modified) {
				if (queue.getConnectionState() == ConnectionState.LoggedIn) {
					logger.info("Reader " + queue.getReader().getName() + " was modified. Logging the reader off from TOR.");
					// Reset the login to stop automatic re-logon
					queue.shutdown();		
				}
			}			
			
			// Stop the send thread.
			stop(true);			
		}
		
		// In case of xBRC restart the CMT table is read from the database. We need to setup Omni login
		// creds based on this table for open locations.
		synchronized(credsByLocation)
		{
			credsByLocation.clear();
		
			synchronized(CMT) {
				for (CMST cmst : CMT.values()) {
					
					CastMemberCreds creds = new CastMemberCreds(ConfigOptions.INSTANCE.getSettings().getOmniid(), 
							ConfigOptions.INSTANCE.getSettings().getOmnipassword(), cmst.getPortalId());
					String locationName = cmst.getLocationName();
					
					credsByLocation.put(locationName, creds);
				}
			}						
		}
		
		for (ReaderInfo ri : readers) {
			if (!ReaderType.isTapReader(ri.getType()))
				continue;
			
			if (queues.containsKey(ri.getDeviceId())) {
				logger.error("Duplicate deviceId " + ri.getDeviceId() + " for reader. Ignoring reader " + ri.getName());
				continue;
			}
			
			// Don't try to use readers that are not assigned to a valid location.
			if (ri.getLocation().getName().equals("UNKNOWN")) {
				logger.warn("Reader location is UNKNOWN. Ignoring reader " + ri.getName());
				continue;
			}			
			
			// add to the map by device id
			OmniTicketReaderQueue queue = new OmniTicketReaderQueue(ri); 
			queues.put(ri.getDeviceId(), queue);
			
			// set the OmniServers for this queue
			queue.setOmniServers(omniServers.get(new Long(ri.getId())));
			
			// set the login creds if they were set previously
			CastMemberCreds creds = credsByLocation.get(ri.getLocation().getName());
			if (creds != null)
				queue.setCreds(creds);
			
			// add to the map by location name
			Collection<OmniTicketReaderQueue> locqueues = queuesByLocation.get(ri.getLocation().getName());
			if (locqueues == null) {
				locqueues = new LinkedList<OmniTicketReaderQueue>();
				queuesByLocation.put(ri.getLocation().getName(), locqueues);
			}
			locqueues.add(queue);
		}
		
		start();
	}
	
	public void start() {
		
		// Assign one Omni server to each reader.
		for (Collection<OmniTicketReaderQueue> locqueues : queuesByLocation.values())
		{
			if (locqueues == null)
				continue;
			
			CMST castMemberState = null;
			
			for (OmniTicketReaderQueue queue : locqueues)
			{
				OmniServerItem server = null;
				
				synchronized(CMT)
				{
					if (castMemberState == null)
						castMemberState = CMT.get(queue.getReader().getLocation().getName()); 
				}
				
				// If the reader was logged on when the system was stopped then
				// try to connect to the same reader.
				String lastOmniServerKey = queue.getReader().getModelData();
				if (lastOmniServerKey != null && !lastOmniServerKey.isEmpty() && 
					castMemberState != null && castMemberState.getState() == CastMemberState.LOGGEDIN &&
					queue.getOmniServerByKey(lastOmniServerKey) != null)
				{
					server = queue.getOmniServerByKey(lastOmniServerKey);
					logger.info("Connecting reader " + queue.getReader().getName() + " to the last logged on TOR server " + 
								server.getKey());
				}
				else
				{
					// Just pick the first Omni server from the list. It should have the least clients.
					server = queue.getFirstPriorityOmniServer();
				}
				
				queue.setOmniServer(server);
			}
		}
		
		// Start all the queues
		for (OmniTicketReaderQueue queue : queues.values())
		{
			if (queue.getOmniServer() == null)
				logger.warn("Not starting reader thread for reader " + queue.getReader().getName() + " because it does not have any TOR servers.");
			else if (!queue.getReader().isEnabled())
				logger.warn("Not starting reader thread for reader " + queue.getReader().getName() + " because it is disabled.");
			else
				queue.start();
		}
	}
	
	public void stop(boolean sendPendingMessages) {
		for (OmniTicketReaderQueue queue : queues.values())
			queue.stop(sendPendingMessages);
				
		queues.clear();
		queuesByLocation.clear();
	}
	
	public void processTimeouts()
	{
		for (OmniTicketReaderQueue queue : queues.values())
			queue.processTimeouts();
	}
	
	public boolean isConnected(int omniDeviceId) {
		OmniTicketReaderQueue queue = queues.get(omniDeviceId);
		if (queue == null)
			return false;
		return queue.isConnected();
	}
	
	public boolean isLocationConnected(String locationName) {
		Collection<OmniTicketReaderQueue> locqueues = queuesByLocation.get(locationName);
		for (OmniTicketReaderQueue q : locqueues) {
			if (!q.isConnected())
				return false;
		}
		return true;
	}
	
	public OmniReaderInfoStatus getReaderStatus(ReaderInfo ri) {
		plogger.trace("performance: OmniTicketQueue::getReaderStatus enter");
		
		OmniReaderInfoStatus status = null;
		
		// Ignore readers in the UNKNOWN location
		if (ri.getLocation().getName().equals("UNKNOWN"))
			return null;
		
		OmniTicketReaderQueue queue = queues.get(ri.getDeviceId());
		if (queue == null) {			
			
			status = new OmniReaderInfoStatus();
			if (ReaderType.isTapReader(ri.getType()))
			{				
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("No Omni thread created for this reader. Check if deviceid is unique.");
			}		
			else if (ri.getType() == ReaderType.xfp)
			{
				status.setStatus(StatusType.Red);
				status.setStatusMessage("Biometric device not present or not working.");
			}
			else
			{
				status.setStatus(StatusType.Green);
			}
		}
		else if (ri.getType() == ReaderType.xfp)
		{
			status = new OmniReaderInfoStatus();
			status.setStatus(StatusType.Red);
			status.setStatusMessage("Biometric device stopped working or not equipped.");
		}
		else
			status = queue.getReaderStatus();
		
		plogger.trace("performance: OmniTicketQueue::getReaderStatus exit");
		return status;		
	}

	
	public void send(RequestCommand cmd)
	{
		OmniTicketReaderQueue queue = queues.get(cmd.getDeviceID());
		if (queue == null) {
			logger.error("Cannot send OMNI request because there is no device with id = " + cmd.getDeviceID());
			return;
		}
		queue.send(cmd);
	}
	
	public void logonCastMember(String location, String portalId, String username, String password) throws CastAppException {
		plogger.trace("performance: OmniTicketQueue::logonCastMember enter");
		
		Collection<OmniTicketReaderQueue> locqueues = queuesByLocation.get(location);
		if (locqueues == null) {
			throw new CastAppException(ErrorCode.noReadersAtLocation, 
					"Cannot login cast member " + username + " to location " + location + 
					" because there are no readers at that location.");
		}
		
		boolean someConnected = false;
		for (OmniTicketReaderQueue q : locqueues) {
			if (!q.getReader().isEnabled() || q.getReader().getType() != ReaderType.xfpxbio)
				continue;
			
			if (q.isConnected())
			{
				someConnected = true;
				break;
			}
		}
	
		if (!someConnected)
		{
			String error = "Cannot login cast member " + username + " to location " + location + 
					" because none of the readers are conencted to TOR.";
			
			logger.warn(error);
			
			throw new CastAppException(ErrorCode.noReadersConnectedToOmniAtLocation,  error);
		}
		
		CastMemberCreds creds = new CastMemberCreds(username, password, portalId);
		synchronized(credsByLocation) {
			credsByLocation.put(location, creds);
		}

		for (OmniTicketReaderQueue queue : locqueues) {
			if (!queue.getReader().isEnabled() || queue.getReader().getType() != ReaderType.xfpxbio)
				continue;
			
			queue.login(creds);
		}
		
		plogger.trace("performance: OmniTicketQueue::logonCastMember exit");
	}
	
	public void logoffCastMember(String location) {
		plogger.trace("performance: OmniTicketQueue::logoffCastMember enter");
		
		Collection<OmniTicketReaderQueue> locqueues = queuesByLocation.get(location);
		if (locqueues == null) {
			logger.fatal("Cannot logoff cast member from location " + location + " because there are no readers at that location.");
			return;
		}		
		
		for (OmniTicketReaderQueue queue : locqueues) {
			queue.logoff();
			ReaderExecutor.getInstance().setIdleSequence(queue.getReader(), false);
		}
		
		synchronized(credsByLocation) {
			credsByLocation.remove(location);
		}
		
		plogger.trace("performance: OmniTicketQueue::logoffCastMember exit");
	}
	
	public void cancelLogonCastMember(String location) {
		plogger.trace("performance: OmniTicketQueue::cancelLogonCastMember enter");
		
		Collection<OmniTicketReaderQueue> locqueues = queuesByLocation.get(location);
		if (locqueues == null) {
			return;
		}		
		
		for (OmniTicketReaderQueue queue : locqueues) {
			queue.cancelLogon();
		}
		
		synchronized(credsByLocation) {
			credsByLocation.remove(location);
		}
		
		plogger.trace("performance: OmniTicketQueue::cancelLogonCastMember exit");
	}
	
	public void shutdownReader(String readerName)
	{
		OmniTicketReaderQueue q = null;
		
		for (OmniTicketReaderQueue queue : queues.values()) {
			if (queue.getReader().getName().equals(readerName)) {
				q = queue;
				break;
			}
		}
		
		if (q != null)
		{
			ReaderExecutor.getInstance().setIdleSequence(q.getReader(), false);
			q.shutdown();
		}
	}
	
	public OmniResponse getNextMessage() {
		synchronized(inqueue) {
			OmniResponse cmd = inqueue.pollFirst();
			return cmd;
		}
	}
	
	public void omniMessage(OmniResponse cmd)
	{
		synchronized(inqueue) {
			inqueue.addLast(cmd);
		}
	}
	
	public void onLoginFailure(OmniTicketReaderQueue queue, AnswerCommand cmd)
	{
		boolean receivedAll = haveLoginResponseForAllReaders(queue.getReader().getLocation().getName());
		ReaderExecutor.getInstance().setIdleSequence(queue.getReader(), false);
		omniMessage(new OmniResponse(cmd, receivedAll));
	}
	
	public void onLoginSuccess(OmniTicketReaderQueue queue, AnswerCommand cmd)
	{	
		boolean receivedAll = haveLoginResponseForAllReaders(queue.getReader().getLocation().getName());
		ReaderExecutor.getInstance().setIdleSequence(queue.getReader(), true);
		omniMessage(new OmniResponse(cmd, receivedAll));
	}
	
	public void onLogoutNotification(OmniTicketReaderQueue queue, AnswerCommand cmd)
	{
		synchronized (credsByLocation)
		{
			credsByLocation.remove(queue.getReader().getLocation().getName());
		}
		
		boolean receivedAll = haveLogoutResponseForAllReaders(queue.getReader().getLocation().getName());
		ReaderExecutor.getInstance().setIdleSequence(queue.getReader(), false);
		omniMessage(new OmniResponse(cmd, receivedAll));
	}
	
	public void onStatusNotification(OmniTicketReaderQueue queue, AnswerCommand cmd)
	{
		boolean receivedAll = haveStatusResponseForAllReaders(queue.getReader().getLocation().getName());
		omniMessage(new OmniResponse(cmd, receivedAll));
	}
	
	public void onOperatorNotLoggedIn(OmniTicketReaderQueue queue, AnswerCommand cmd)
	{
		// Pass to CEP
		ReaderExecutor.getInstance().setIdleSequence(queue.getReader(), false);
		omniMessage(new OmniResponse(cmd, false));
	}
	
	public void onOmniMessage(OmniTicketReaderQueue queue, AnswerCommand cmd)
	{
		// Pass to CEP
		omniMessage(new OmniResponse(cmd, false));
	}
	
	public void onOmniConnectFailure(OmniTicketReaderQueue queue)
	{
		synchronized (credsByLocation)
		{
			credsByLocation.remove(queue.getReader().getLocation().getName());
		}
		
		ReaderExecutor.getInstance().setIdleSequence(queue.getReader(), false);
		omniMessage(new OmniResponse(queue.getReader().getName()));
	}
	
	public void onReaderStateChange(String readerName)
	{
		omniMessage(new OmniResponse(readerName));
	}
	
	private boolean haveStatusResponseForAllReaders(String loctionName)
	{
		// Check if we have received positive login response for all
		// readers.
		Collection<OmniTicketReaderQueue> queues = queuesByLocation.get(loctionName);
		for (OmniTicketReaderQueue q : queues)
		{
			// Skip disabled readers
			if (!q.getReader().isEnabled())
				continue;
			
			// Skip readers for which we do not expect a status response
			if (q.getReader().getType() != ReaderType.xfpxbio)
				continue;
			
			if (!q.isGotStatus())
				return false;
		}

		return true;
	}
	
	private boolean haveLoginResponseForAllReaders(String loctionName)
	{
		// Check if we have received positive login response for all
		// readers.
		Collection<OmniTicketReaderQueue> queues = queuesByLocation.get(loctionName);
		for (OmniTicketReaderQueue q : queues)
		{
			// Skip disabled readers
			if (!q.getReader().isEnabled() || q.getReader().getType() != ReaderType.xfpxbio)
				continue;
			
			// Skip readers for which we do not expect a status response
			if (q.getReader().getType() != ReaderType.xfpxbio)
				continue;
						
			if (q.getConnectionState() == OmniTicketReaderQueue.ConnectionState.AwaitingLoginResponse ||
				q.getConnectionState() == OmniTicketReaderQueue.ConnectionState.Connected)
				return false;
		}

		return true;
	}
	
	private boolean haveLogoutResponseForAllReaders(String loctionName)
	{
		// Check if we have received positive login response for all
		// readers.
		Collection<OmniTicketReaderQueue> queues = queuesByLocation.get(loctionName);
		for (OmniTicketReaderQueue q : queues)
		{
			// Skip disabled readers
			if (!q.getReader().isEnabled())
				continue;
			
			// Skip readers for which we do not expect a status response
			if (q.getReader().getType() != ReaderType.xfpxbio)
				continue;
			
			if (q.getConnectionState() == OmniTicketReaderQueue.ConnectionState.AwaitingLogoffResponse)
				return false;
		}

		return true;
	}

	public Map<String, Collection<OmniTicketReaderQueue>> getQueuesByLocation()
	{
		return queuesByLocation;
	}
	
	public OmniTicketReaderQueue getQueueByDeviceId(Integer deviceId)
	{
		return queues.get(deviceId);
	}
}
