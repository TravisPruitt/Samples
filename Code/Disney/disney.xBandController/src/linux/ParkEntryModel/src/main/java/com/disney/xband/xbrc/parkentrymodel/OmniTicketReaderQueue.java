package com.disney.xband.xbrc.parkentrymodel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.ErrorCode;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXReader.State;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;

/*
 * OmniTicket message queue for a single reader. 
 */
public class OmniTicketReaderQueue {
	
	private static Logger logger = Logger.getLogger(OmniTicketReaderQueue.class);
	
	private LinkedList<RequestCommand> outqueue;
	private List<OmniServerItem> omniServers;
	private ReaderInfo reader;
	private Date lastWatchdogSent = null;
	private Integer watchdogTime = 60000;
	private CastMemberCreds creds = null;
	private Date lastConnectionStateChange = new Date();
	private boolean gotStatus = false;
	private OmniTicketReaderQueueThread thread;
	private OmniServerItem omniServer = null;
	private boolean lastAliveState = true;
	private ReaderType lastReaderType = ReaderType.xfpxbio;

	// Logical connection state. OmniTicket requires a connect message before any other message may be sent.
	public enum ConnectionState {
		Disconnected(State.shutdown),
		AwaitingConnectResponse(State.shutdown),
		ConnectedNeedStatus(State.shutdown),
		AwaitingStatusResponse(State.shutdown),
		Connected(State.closed),
		AwaitingLoginResponse(State.closed),
		LoggedIn(State.open),
		Logoff(State.open),
		AwaitingLogoffResponse(State.open),
		AccessDenied(State.shutdown);
		
		private PGXReader.State readerState;

		private ConnectionState(PGXReader.State readerState)
		{
			this.readerState = readerState;
		}
		
		public PGXReader.State getReaderState()
		{
			return readerState;
		}
	}
	
	private ConnectionState connectionState = ConnectionState.Disconnected;
	
	public OmniTicketReaderQueue(ReaderInfo reader) {
		outqueue = new LinkedList<RequestCommand>();
		this.reader = reader;
	}
	
	boolean isConnected() {
		switch(connectionState)
		{
			case Connected:
			case AwaitingStatusResponse:
			case AwaitingLoginResponse:
			case LoggedIn:
				return true;
		}
		return false;
	}
	
	public void send(RequestCommand cmd) {
		synchronized(outqueue) {
			outqueue.addLast(cmd);
		}
		synchronized(this)
		{
			if (thread != null)
				thread.notifyOfEvent();
		}
	}
	
	public void stop(boolean sendPendingMessages)
	{
		OmniTicketReaderQueueThread tmpThread = null;
		synchronized(this)
		{
			tmpThread = thread;
		}
		
		if (tmpThread == null)
			return;
		
		logger.trace("Stopping send queue for reader " + (reader != null ? reader.getName() : ""));
		
		// We don't want to do this in the synchronized(this) block or the thread
		// will not be able to stop.
		this.thread.stop(sendPendingMessages);
		
		synchronized(this)
		{
			if (this.thread != null)
			{
				try
				{
					this.thread.join(10000);
				}
				catch (InterruptedException e)
				{
					logger.warn("Timed out while waiting for the reader omni ticket queue thread to stop.");
				}
				this.thread = null;
				
			}
		}
		
		logger.trace("Finished stopping send queue for reader " + (reader != null ? reader.getName() : ""));
	}
	
	public void start()
	{
		synchronized(this)
		{
			if (this.thread != null)
				return;
			this.thread = new OmniTicketReaderQueueThread(this);
			this.thread.start();
		}
	}
	
	public void processTimeouts()
	{
		synchronized(this)
		{
			if (thread != null)
				thread.monitorStuckThread();
		}
	}

	public LinkedList<RequestCommand> getOutqueue() {
		return outqueue;
	}

	public void setOutqueue(LinkedList<RequestCommand> outqueue) {
		this.outqueue = outqueue;
	}

	public ConnectionState getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(ConnectionState connectionState) {
		ConnectionState newState;
		
		synchronized(this) {
			this.connectionState = connectionState;
			lastConnectionStateChange = new Date();
			newState = connectionState;
		}
				
		switch(newState)
		{
			case AccessDenied:
			case Connected:
			case Disconnected:
			case LoggedIn:
				OmniTicketQueue.getInstance().onReaderStateChange(this.getReader().getName());
				break;
		}
	}

	public Integer getDeviceId() {
		return reader.getDeviceId();
	}

	public Date getLastWatchdogSent() {
		return lastWatchdogSent;
	}

	public void setLastWatchdogSent(Date lastWatchdogSent) {
		this.lastWatchdogSent = lastWatchdogSent;
	}

	public Integer getWatchdogTime() {
		return watchdogTime;
	}

	public void setWatchdogTime(Integer watchdogTime) {
		this.watchdogTime = watchdogTime;
	}
	
	public boolean isTimeToSendWatchdog(Date now) {
		return getLastWatchdogSent() == null || getLastWatchdogSent().getTime() + watchdogTime < now.getTime();
	}
	
	public void logoff()
	{	
		synchronized(this)
		{
			setCreds(null);		
			
			if (thread != null)
			{
				switch(getConnectionState())
				{
				case AwaitingLoginResponse:
				case Connected:
				case LoggedIn:
					// Set the connection state to log off causing Logout omni message to be sent
					setConnectionState(OmniTicketReaderQueue.ConnectionState.Logoff);
					thread.notifyOfEvent();
					break;
				}
			}
		}
	}
	
	public void cancelLogon()
	{	
		synchronized(this)
		{
			// Prevent further logon
			setCreds(null);
		}
	}
	
	public void shutdown()
	{
		logoff();
		
		// Ask the thread the shutdown after the logoff sequence
		if (thread != null)
			thread.setShutdown(true);
	}
	
	public void login(CastMemberCreds creds)
	{
		synchronized(this)
		{
			setCreds(creds);
			
			if (thread != null)
				thread.notifyOfEvent();
		}
	}
	
	public void setCreds(CastMemberCreds creds) {
		this.creds = creds;
		
	}
	
	public CastMemberCreds getCreds() {
		return creds;
	}

	public Date getLastConnectionStateChange() {
		synchronized(this) {
			return lastConnectionStateChange;
		}
	}

	public boolean isGotStatus() {
		return gotStatus;
	}

	public void setGotStatus(boolean gotStatus) {
		this.gotStatus = gotStatus;
	}

	public ReaderInfo getReader()
	{
		return reader;
	}

	public void setReader(ReaderInfo reader)
	{
		this.reader = reader;
	}

	public OmniTicketReaderQueueThread getThread()
	{
		return thread;
	}
	
	public OmniReaderInfoStatus getReaderStatus() {
		
		OmniReaderInfoStatus status = new OmniReaderInfoStatus();				
		
		synchronized(this) {
			
			if (omniServers == null || omniServers.isEmpty() || omniServer == null)
			{
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("No TOR servers are configured for this reader");
				status.setErrorCode(ErrorCode.torConnectionFailure);
				return status;
			}
			
			int failed = 0;
			for (OmniServerItem os : omniServers)
				if (os.getLastConnectFailure() != null)
					failed++;
			
			if (failed == omniServers.size())
			{
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("Cannot connect to any of the " + failed + " configured TOR servers");
				status.setErrorCode(ErrorCode.torConnectionFailure);
				return status;
			}
			
			switch(getConnectionState()) {
			case Disconnected:
			case AwaitingConnectResponse:
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("Trying to connect to TOR");
				status.setErrorCode(ErrorCode.torConnectionFailure);
				break;
			case ConnectedNeedStatus:
			case AwaitingStatusResponse:
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("Waiting for login status response from TOR");
				break;			
			case Connected:
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("Cast member not logged in to TOR");
				break;
			case AwaitingLoginResponse:
				status.setStatus(StatusType.Yellow);
				status.setStatusMessage("Awaiting Cast member login response from TOR");
				break;
			case LoggedIn:
			case Logoff:
			case AwaitingLogoffResponse:
				status.setStatus(StatusType.Green);
				break;
			case AccessDenied:
				status.setStatus(StatusType.Yellow);
				status.setErrorCode(ErrorCode.badOmniCastCreds);
				if (getCreds() != null)
					status.setStatusMessage("TOR rejected login for Cast member with HUB ID " + getCreds().getPortalId() + " using TOR username " + getCreds().getUsername());
				else
					status.setStatusMessage("TOR rejected login for Cast member. No login creds provided");
				break;
			}
		}
		
		return status;
	}

	public OmniServerItem getOmniServer()
	{
		return omniServer;
	}

	public void setOmniServer(OmniServerItem omniServer)
	{
		if (this.omniServer != null)
			this.omniServer.removeClient();
		
		this.omniServer = omniServer;
		
		if (omniServer != null)
			omniServer.addClient();
		getReader().setModelData(omniServer != null ? omniServer.getKey() : null);
		XBRCController.getInstance().saveModelData(getReader());
	}

	public List<OmniServerItem> getOmniServers()
	{
		return omniServers;
	}

	public void setOmniServers(List<OmniServerItem> omniServers)
	{
		this.omniServers = omniServers;
	}
	
	public OmniServerItem getOmniServerByKey(String key)
	{
		if (omniServers == null)
			return null;
		
		for (OmniServerItem osi : omniServers)
		{
			if (osi.getKey().equals(key))
				return osi;
		}
		return null;
	}
	
	public OmniServerItem getFirstPriorityOmniServer()
	{
		// Servers are ordered according to priority
		if (omniServers == null || omniServers.isEmpty())
			return null;
		return omniServers.get(0);
	}
	
	public void setLastAliveState(boolean lastAliveState)
	{
		this.lastAliveState = lastAliveState;
	}
	
	public boolean getLastAliveState()
	{
		return this.lastAliveState;
	}

	public ReaderType getLastReaderType()
	{
		return lastReaderType;
	}

	public void setLastReaderType(ReaderType lastReaderType)
	{
		this.lastReaderType = lastReaderType;
	}
}
