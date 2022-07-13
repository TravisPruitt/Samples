package com.disney.xband.xbrc.parkentrymodel;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.disney.socket.library.Client;
import com.disney.socket.library.SwitchBoardMessageEvent;
import com.disney.socket.library.SwitchBoardMessageListener;
import com.disney.socket.library.SwitchboardMessageExceptionEvent;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Watchdog;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.omni.OmniConst;
import com.disney.xband.xbrc.lib.utils.FileUtils;
import com.disney.xband.xbrc.parkentrymodel.OmniTicketReaderQueue.ConnectionState;

public class OmniTicketReaderQueueThread extends Thread implements
		SwitchBoardMessageListener
{
	private static Logger logger = Logger
			.getLogger(OmniTicketReaderQueueThread.class);
	private static Logger plogger = Logger.getLogger("performance");

	private OmniTicketReaderQueue queue;
	private Client client = null;
	private volatile boolean run = true;
	private Object queueEvent = new Object();
	private volatile boolean sendPendingMessages = false;
	private boolean reconnect = false;
	private OmniTicketStatus status = new OmniTicketStatus();
	private volatile boolean shutdown = false;
	private int torRetryCounter = 0;
	private int entitlementTimeoutCounter = 0;
	private Date entitlementRequestTime = null;
	private Date lastProcessTime = null;
	private volatile boolean sendingData = false;

	public OmniTicketReaderQueueThread(OmniTicketReaderQueue queue)
	{
		this.queue = queue;
	}

	@Override
	public void run()
	{
		// To keep track how long have we been trying to reconnect...
		Date reconnectStart = null;

		while (run)
		{
			synchronized (queueEvent)
			{
				// Wait one second or until a notify() is called signaling a new
				// message to send.
				try
				{
					queueEvent.wait(1000);
				}
				catch (InterruptedException e)
				{
				}
			}
			
			lastProcessTime = new Date();
			
			if (!run && !sendPendingMessages)
				break;

			if (reconnect && client != null)
			{
				client.cleanup();
				client = null;
			}

			if (client == null)
			{
				if (reconnectStart == null)
					reconnectStart = new Date();
				reconnect();
			}
			if (client == null)
			{	
				// If it has been longer than few seconds trying to re-connect,
				// bounce all OMNI traffic back to the CEP.
				if (reconnectStart != null
						&& (new Date().getTime() - reconnectStart.getTime() > ConfigOptions.INSTANCE
								.getSettings().getOmniConnectTimeoutMs()))
				{	
					if (onOmniServerConnectFailure(queue) == true)
					{
						logger.info("Reader " + queue.getReader().getName() + " trying to connect to another Omni server " + queue.getOmniServer().getKey());
						continue;
					}
					
					// no longer connected to Omni
					if (queue.getConnectionState() != ConnectionState.Disconnected)
					{
						queue.setConnectionState(ConnectionState.Disconnected);
						queue.setGotStatus(false);
						OmniTicketQueue.getInstance().onOmniConnectFailure(queue);
					}
					
					bounceAllOmniMessages(
							OmniConst.SHORTERR_LOST_TOR_CONNECTION,
							"Cannot process guest entitlement. Lost connection to TOR.");
				}
				continue;
			}

			reconnectStart = null;

			plogger.trace("performance: OmniTicketQueue::runSendThread checking for new messages to send");

			sendWatchdogCommand(queue);
			
			checkReaderHealth(queue);

			this.status = processReaderState();

			plogger.trace("performance: OmniTicketQueue::runSendThread finished checking for new messages to send");
		}
		
		logger.trace("Omni thread for reader " + queue.getReader().getName() + " is disconnecting from TOR");
		
		killClient();
		
		logger.info("Stopped Omni thread for reader " + queue.getReader().getName());
	}
	
	public void monitorStuckThread()
	{
		// We can only be stuck if we are actually sending data on the socket.
		if (!sendingData)
			return;

		Date now = new Date();
		
		// Copy the pointer for thread safety
		Date tempLastProcessTime = lastProcessTime;
		if (tempLastProcessTime == null || client == null)
			return;
		
		// If we have not looped for 10 seconds then we are definitely stuck
		if ((now.getTime() - tempLastProcessTime.getTime()) > ConfigOptions.INSTANCE.getSettings().getOmniConnectTimeoutMs())
		{
			logger.error("Reader thread became stuck for " + ConfigOptions.INSTANCE.getSettings().getOmniConnectTimeoutMs() / 1000 +  
						 " seconds while talking to Omni server at " + queue.getOmniServer().getOmniServer().getHostname() + ". Closing Omni connection.");
			killClient();
		}
	}

	private OmniTicketStatus processReaderState()
	{
		OmniTicketStatus status = new OmniTicketStatus();
		LinkedList<RequestCommand> tmpoutqueue = queue.getOutqueue();

		switch (queue.getConnectionState())
		{
			case LoggedIn:
				checkOmniEntitlementTimeout();
				break;
			case Disconnected:
			{	
				if (shutdown) {
					// Process shutdown in the Disconnected state
					run = false;
					break;
				}
				
				// Send Connect request.
				RequestCommand cmd = OmniTicket.makeConnectRequest(queue.getDeviceId());
				// Switch the tmpqueue to the local queue for the connect
				// message. We don't want to add it to the real queue.
				tmpoutqueue = new LinkedList<RequestCommand>();
				tmpoutqueue.add(cmd);
				queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.AwaitingConnectResponse);
				break;
			}
			case ConnectedNeedStatus:
			{
				// Ask Omni for the login status to see if a cast member is
				// already logged into this location.
				RequestCommand cmd = OmniTicket.makeStatusRequest(
						queue.getDeviceId(), queue.getReader().getLocation()
								.getName());
				tmpoutqueue = new LinkedList<RequestCommand>();
				tmpoutqueue.add(cmd);
				queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.AwaitingStatusResponse);
				break;
			}
			case Connected:
			{
				if (shutdown) {
					// Process shutdown in the Connected state
					run = false;
					break;
				}
				
				CastMemberCreds creds = null;

				synchronized (queue)
				{
					creds = queue.getCreds();
				}
				
				if (creds == null)
					checkOmniEntitlementTimeout();

				// If we have the creds then go ahead and send the login
				// request.
				if (creds != null)
				{
					RequestCommand cmd = OmniTicket
							.makeLogonRequest(queue.getDeviceId(),
									creds.getPortalId()
											+ "\t"
											+ queue.getReader().getLocation()
													.getName(),
									creds.getUsername(), creds.getPassword());
					tmpoutqueue = new LinkedList<RequestCommand>();
					tmpoutqueue.add(cmd);
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.AwaitingLoginResponse);
				}
				else if (status.getStatus().isParameterMoreSevere(StatusType.Yellow))
				{
					status.setStatus(StatusType.Yellow);
					status.setStatusMessage("Cast member not logged into Omni system at location "
							+ queue.getReader().getLocation().getName());
				}
				break;
			}
			case Logoff:
			{
				// To log off we  a logout message.
				RequestCommand cmd = OmniTicket.makeLogoutRequest(
						queue.getDeviceId(), "");
				tmpoutqueue = new LinkedList<RequestCommand>();
				tmpoutqueue.add(cmd);
				queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.AwaitingLogoffResponse);
				break;
			}
			case AwaitingStatusResponse:
			{
				if (status.getStatus().isParameterMoreSevere(StatusType.Yellow))
				{
					status.setStatus(StatusType.Yellow);
					status.setStatusMessage("Waiting for login Status response from Omni.");
				}

				if (isOmniResponseTimeout(OmniTicketReaderQueue.ConnectionState.Connected))
				{
					logger.warn("Timed out waiting for Omni response for status request for reader "
							+ queue.getReader().getName());
				}
				return status;
			}
			case AwaitingLogoffResponse:
			case AwaitingLoginResponse:
			{
				if (status.getStatus().isParameterMoreSevere(StatusType.Yellow))
				{
					status.setStatus(StatusType.Yellow);
					status.setStatusMessage("Waiting for Login/Logoff response from Omni.");
				}

				if (isOmniResponseTimeout(OmniTicketReaderQueue.ConnectionState.Connected))
				{
					logger.warn("Timed out waiting for Omni response for login/logoff request for reader "
							+ queue.getReader().getName());
					bounceAllOmniMessages(
							OmniConst.SHORTERR_TOR_RESPONSE_TIMEOUT,
							"TOR did not respond to logon or logoff request for reader "
									+ queue.getReader().getName());
				}
				return status;
			}
			case AwaitingConnectResponse:
			{
				if (status.getStatus().isParameterMoreSevere(StatusType.Yellow))
				{
					status.setStatus(StatusType.Yellow);
					status.setStatusMessage("Waiting for Connect response from Omni.");
				}

				if (isOmniResponseTimeout(OmniTicketReaderQueue.ConnectionState.Disconnected))
				{
					logger.warn("Timed out waiting for Omni response for connect request for reader "
							+ queue.getReader().getName());
					bounceAllOmniMessages(
							OmniConst.SHORTERR_TOR_RESPONSE_TIMEOUT,
							"TOR did not respond to connect request for reader "
									+ queue.getReader().getName());
				}
				return status;
			}
			case AccessDenied:
			{
				String bandId = "";
				String username = "";
				synchronized (queue)
				{
					if (queue.getCreds() != null)
					{
						bandId = queue.getCreds().getPortalId();
						username = queue.getCreds().getUsername();
					}
					// Stop further login attempts from this reader
					queue.setCreds(null);
				}

				if (status.getStatus().isParameterMoreSevere(StatusType.Yellow))
				{
					status.setStatus(StatusType.Yellow);
					status.setStatusMessage("Omni rejected user logon for user "
							+ username + " using RFID " + bandId);
				}

				Date now = new Date();
				if (now.getTime()
						- queue.getLastConnectionStateChange().getTime() > ConfigOptions.INSTANCE
						.getSettings().getOmniRequestTimeoutMs())
				{
					logger.warn("Omni rejected user logon for user " + username + " using RFID " + bandId);
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.Disconnected);
					queue.setGotStatus(false);
					bounceAllOmniMessages(
							OmniConst.SHORTERR_TOR_READER_DENIED,
							"TOR declined connect or logon request for reader "
									+ queue.getReader().getName());
				}
				return status;
			}
		}

		// Since this could be the actual reader queue we must synchronize it.
		plogger.trace("performance: OmniTicketQueue::runSendThread start synchronized(tmpoutqueue)");
		RequestCommand cmd = null;
		synchronized (tmpoutqueue)
		{
			cmd = tmpoutqueue.isEmpty() ? null : tmpoutqueue.getFirst();
		}
		plogger.trace("performance: OmniTicketQueue::runSendThread finished synchronized(tmpoutqueue)");

		while (cmd != null)
		{
			Date now = new Date();
			
			try
			{
				String xml = XmlUtil.convertToXml(cmd, RequestCommand.class);

				plogger.trace("performance: OmniTicketQueue::runSendThread sending message");
				
				sendingData = true;
				client.Write(xml);
				sendingData = false;
				
				if (cmd.getEntitlement() != null)
					entitlementRequestTime = now;

				plogger.trace("performance: OmniTicketQueue::runSendThread finished sending message");

				if (logger.isTraceEnabled())
				{
					// cannot log secure id (sid) into the log file...
					if (cmd.getEntitlement() != null
							&& cmd.getEntitlement().getMediaInfo() != null
							&& cmd.getEntitlement().getMediaInfo()
									.getMediaSearchMode() != null)
					{
						String tmp = cmd.getEntitlement().getMediaInfo()
								.getMediaSearchMode().getXBandID();
						cmd.getEntitlement().getMediaInfo()
								.getMediaSearchMode().setXBandID(FileUtils.hideLeadingChars(tmp,4));
						String tmpxml = XmlUtil.convertToXml(cmd,
								RequestCommand.class);
						logger.trace("TOR request: " + tmpxml);
						cmd.getEntitlement().getMediaInfo()
								.getMediaSearchMode().setXBandID(tmp);
					}
					else
						logger.trace("TOR request: " + xml);
				}
				queue.setLastWatchdogSent(now);
			}
			catch (IOException e)
			{
				logger.error("IOException while sending Omni message.", e);
				client.cleanup();
				client = null;
				break; // out of the inner while loop
			}
			catch (JAXBException e)
			{
				logger.error("JAXBException while sending Omni message.", e);
			}
			catch(Exception e)
			{
				logger.error("Caught exception while sending Omni message.", e);
			}
			finally
			{
				sendingData = false;
			}

			plogger.trace("performance: OmniTicketQueue::runSendThread start second synchronized(tmpoutqueue)");
			synchronized (tmpoutqueue)
			{
				tmpoutqueue.remove(cmd);
				cmd = tmpoutqueue.isEmpty() ? null : tmpoutqueue.getFirst();
			}
			plogger.trace("performance: OmniTicketQueue::runSendThread finished second synchronized(tmpoutqueue)");
		}

		return status;
	}
	
	private boolean isOmniResponseTimeout(OmniTicketReaderQueue.ConnectionState retryState)
	{
		Date now = new Date();
		// There are situations when Omni may not respond to our
		// request. For example if it is restarted. In this case we
		// must time out our request.
		if (now.getTime() - queue.getLastConnectionStateChange().getTime() < ConfigOptions.INSTANCE
				.getSettings().getOmniRequestTimeoutMs())
			return false;
		
		logger.warn("Omni server " + queue.getOmniServer().getOmniServer().getHostname() + " response timeout for reader " + 
						queue.getReader().getName());
		
		// Try the request two times, and then try to switch to another TOR.
		if (++torRetryCounter > 1)
		{
			torRetryCounter = 0;
			
			if (onOmniServerConnectFailure(queue) == true)			
				logger.info("Reader " + queue.getReader().getName() + " trying to connect to another Omni server " + queue.getOmniServer().getKey());				
			else
				logger.warn("Reader " + queue.getReader().getName() + " is closing connection to Omni server " + queue.getOmniServer().getKey() + 
						" because of Omni reponse timeouts. No other Omni servers are available for this reader at this time.");
			
			killClient();
			return true;
		}
		
		// Try again
		logger.info("Reseting reader " + queue.getReader().getName() + " state to " + retryState);
		
		queue.setConnectionState(retryState);
		
		return true;
	}
	
	private void checkOmniEntitlementTimeout()
	{
		Date lastRequest = entitlementRequestTime;
		if (lastRequest == null)
			return;
		
		Date now = new Date();
		
		if (now.getTime() - lastRequest.getTime() < ConfigOptions.INSTANCE
				.getSettings().getOmniRequestTimeoutMs())
			return;
		
		logger.warn("Omni server " + queue.getOmniServer().getOmniServer().getHostname() + 
				" did not respond to an entitlement request or cannot be contacted for reader " + queue.getReader().getName());
		
		if (++entitlementTimeoutCounter > 1)
		{
			entitlementTimeoutCounter = 0;
			
			if (onOmniServerConnectFailure(queue) == true)			
				logger.info("Reader " + queue.getReader().getName() + " trying to connect to another Omni server " + queue.getOmniServer().getKey());
			else
				logger.warn("Reader " + queue.getReader().getName() + " is closing connection to Omni server " + queue.getOmniServer().getKey() + 
						" because of Omni reponse timeouts. No other Omni servers are available for this reader at this time.");
			
			killClient();
		}
		else
		{
			// Allow for one missed entitlement response, possibly due to network outage...
			entitlementRequestTime = null;
		}
	}
	
	private void checkReaderHealth(OmniTicketReaderQueue queue)
	{
		boolean alive = XBRCController.getInstance().isReaderAlive(queue.getReader());
		if ( alive != queue.getLastAliveState())
		{
			if (alive)
				logger.info("Reader " + queue.getReader().getName() + " resumed communicating.");
			else				
				logger.warn("Reader " + queue.getReader().getName() + " stopped communicating");
			
			queue.setLastAliveState(alive);
			OmniTicketQueue.getInstance().onReaderStateChange(queue.getReader().getName());
			return;
		}
		
		ReaderType type = queue.getReader().getType();
		if (type != queue.getLastReaderType())
		{
			logger.info("Reader " + queue.getReader().getName() + " type changed to " + type);
			queue.setLastReaderType(type);
			OmniTicketQueue.getInstance().onReaderStateChange(queue.getReader().getName());
			return;
		}
	}

	private void sendWatchdogCommand(OmniTicketReaderQueue queue)
	{
		try
		{
			plogger.trace("performance: OmniTicketQueue::sendWatchdogCommand enter");

			if (queue.getConnectionState() == OmniTicketReaderQueue.ConnectionState.Disconnected)
				return;

			Date now = new Date();
			if (queue.isTimeToSendWatchdog(now))
			{
				plogger.trace("performance: OmniTicketQueue::sendWatchdogCommand one");
				if (!XBRCController.getInstance().isReaderAlive(
						queue.getReader()))
				{
					logger.warn("Reader "
							+ queue.getReader().getName()
							+ " omni device id "
							+ queue.getReader().getDeviceId()
							+ " is not alive. Not sending watchdog command to Omni.");
					// Set the last retry date to stop logging endless log
					// messages
					queue.setLastWatchdogSent(new Date());
					return;
				}
				plogger.trace("performance: OmniTicketQueue::sendWatchdogCommand two");
				RequestCommand cmd = OmniTicket.makeWatchdogRequest(queue
						.getDeviceId());
				cmd.setWatchdog(new Watchdog());
				cmd.getWatchdog();
				send(cmd);
				queue.setLastWatchdogSent(new Date());
				plogger.trace("performance: OmniTicketQueue::sendWatchdogCommand three");
			}
		}
		finally
		{
			plogger.trace("performance: OmniTicketQueue::sendWatchdogCommand exit");
		}
	}

	public void setStatus(StatusType s, String statusMessage)
	{
		synchronized (status)
		{
			status.setStatus(s);
			status.setStatusMessage(statusMessage);
		}
	}

	private void reconnect()
	{
		try
		{
			// The thread can be stopped asynchronously so check the flag.
			if (!run)
				return;
			
			torRetryCounter = 0;
			entitlementTimeoutCounter = 0;
			entitlementRequestTime = null;
			
			logger.info("Reader " + queue.getReader().getName() + " is trying to connect to OMNI server " + queue.getOmniServer().getOmniServer().getHostname());
			
			client = new Client(queue.getOmniServer().getOmniServer().getHostname(), 
								queue.getOmniServer().getOmniServer().getPort(),
								new int[] { 0 },
								12000);
			// new int[] {'<','/','C','o','m','m','a','n','d','>'});
			client.addMyEventListener(this);

			reconnect = false;

			queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.Disconnected);
			queue.getOmniServer().setLastConnectFailure(null);
			queue.setGotStatus(false);
			
			logger.info("Reader " + queue.getReader().getName() + " connected to OMNI server " + queue.getOmniServer().getOmniServer().getHostname());
		}
		catch (Exception e)
		{
			logger.error("Reader " + queue.getReader().getName() + " failed to connect to Omni server at " + 
					queue.getOmniServer().getOmniServer().getHostname() + ":" + queue.getOmniServer().getOmniServer().getPort(), e);
			if (client != null)
				client.cleanup();
			client = null;
		}
	}
	
	public boolean onOmniServerConnectFailure(OmniTicketReaderQueue queue)
	{
		queue.getOmniServer().setLastConnectFailure(new Date());
		
		// If we have only one server to choose from then just return false
		// indicating that we did not find a better server to connect to.
		if (queue.getOmniServers().size() <= 1)
			return false;
		
		// Try to connect to each of the servers other than the one that just failed
		// and pick the first one that works.
		for (OmniServerItem server : queue.getOmniServers())
		{	
			Socket socket = null;
			BufferedWriter out = null;
			
			// skip the one that just failed
			if (server == queue.getOmniServer())
				continue;
			
			if (server.getLastConnectFailure() == null)
			{
				queue.setOmniServer(server);
				return true;
			}
			
			logger.info("Checking if Omni server is alive " + server.getKey()); 

			try
			{
				SocketAddress sockaddr = new InetSocketAddress(server.getOmniServer().getHostname(), server.getOmniServer().getPort());
				socket = new Socket();
				socket.connect(sockaddr, 4000);
				socket.setSoTimeout(4000);
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				// BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//out.write(" ");
				out.close();
				socket.close();
				
				queue.setOmniServer(server);
				return true;
			}
			catch (Exception e)
			{
				logger.warn("Failed to connect to Omni at " + server.getKey());
			}
			finally
			{
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						logger.warn("Could not close Omni server BufferedWriter", e);
					}
				}
				
				if (socket != null && !socket.isClosed())
				{
					try
					{
						socket.close();
					}
					catch (IOException e)
					{
						logger.warn("Could not close Omni server socket", e);
					}
				}
			}
		}
			
		logger.warn("Cannot contact any of the Omni servers for reader " + queue.getReader().getName());
		
		return false;
	}

	@Override
	public void switchBoardMessage(SwitchBoardMessageEvent evnt)
	{
		// This function is called from the socket client thread.
		// All access to the client object are unsafe.

		try
		{
			plogger.trace("performance: OmniTicketQueue::SwitchBoardMessageEvent enter");

			if (evnt instanceof SwitchboardMessageExceptionEvent)
			{
				// Most likely lost connection with Omni Ticket. Reset it.
				logger.warn("Closing Omni connection because of exception: "
						+ evnt.getMessage());
				reconnect = true;
				return;
			}

			String input = evnt.getMessage().trim();
			AnswerCommand cmd = XmlUtil.convertToPojo(new ByteArrayInputStream(
					input.getBytes()), AnswerCommand.class);
			
			// Reset pending entitlement request time. We don't really care if there are other pending
			// entitlements. We just care that TOR is responding to us so we don't need to switch TOR servers.
			if (cmd != null && (cmd.getEntitlement() != null || cmd.getHeader().getRequestType().equalsIgnoreCase(OmniConst.REQ_TYPE_ENTITLEMENT)))
			{
				entitlementTimeoutCounter = 0;
				entitlementRequestTime = null;
			}

			if (logger.isTraceEnabled())
			{
				// cannot log secure id (sid) into the log file...
				if (cmd.getEntitlement() != null
						&& cmd.getEntitlement().getMediaInfo() != null
						&& cmd.getEntitlement().getMediaInfo()
								.getXBandID() != null)
				{
					String tmp = cmd.getEntitlement().getMediaInfo()
							.getXBandID();
					cmd.getEntitlement().getMediaInfo()
							.setXBandID(FileUtils.hideLeadingChars(tmp,4));
					String tmpxml = XmlUtil.convertToXml(cmd,
							AnswerCommand.class);
					logger.trace("TOR answer: " + tmpxml);
					cmd.getEntitlement().getMediaInfo()
							.setXBandID(tmp);
				}
				else
					logger.trace("TOR answer: " + evnt.getMessage());
			}

			processReaderQueueMessage(cmd);
		}
		catch (JAXBException e)
		{
			logger.error("Failed to deserialize OmniTicket response", e);
			logger.error(evnt.getMessage());
		}
		finally
		{
			plogger.trace("performance: OmniTicketQueue::SwitchBoardMessageEvent exit");
		}
	}

	private void processReaderQueueMessage(AnswerCommand cmd)
	{
		try
		{
			plogger.trace("performance: OmniTicketQueue::processReaderQueueMessage enter");
			
			// Make sure that the answer is for the correct device ID
			if (!queue.getDeviceId().equals(cmd.getDeviceID()))
			{
				logger.error("Cannot process Omni response command for reader with DeviceID = "
						+ cmd.getDeviceID()
						+ " because it does not match the DeviceID = "
						+ queue.getDeviceId()
						+ " of the reader that sent the request: ");
				return;
			}

			// Handle the answer to the connect message ourselves.
			if (cmd.getHeader().getRequestType()
					.equals(OmniConst.REQ_TYPE_CONNECT))
			{
				if (cmd.getError().getErrorCode().equals(BigInteger.ZERO))
				{
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.ConnectedNeedStatus);
					queue.setWatchdogTime(cmd.getConnect().getWatchDogTimeout()
							.intValue());
					logger.info("Omni connect message was accepted. Reader with DeviceID = "
							+ cmd.getDeviceID()
							+ " is now connected to Omni. Watchdog timeout is "
							+ cmd.getConnect().getWatchDogTimeout().intValue());
				}
				else
				{
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.AccessDenied);
					logger.warn("Omni refused our connect request for reader with DeviceId = "
							+ cmd.getDeviceID()
							+ " Error code: "
							+ cmd.getError().getErrorCode()
							+ " Error Message: "
							+ cmd.getError().getErrorShortDescription()
							+ ", "
							+ cmd.getError().getErrorDescription());

				}
				return;
			}

			// Handle the answer to the login message ourselves.
			if (cmd.getHeader().getRequestType().equals(OmniConst.REQ_TYPE_LOGIN))
			{
				if (cmd.getError().getErrorCode().equals(BigInteger.ZERO))
				{
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.LoggedIn);
					logger.info("Omni login message was accepted. Reader with DeviceID = "
							+ cmd.getDeviceID() + " is now logged in");
					
					OmniTicketQueue.getInstance().onLoginSuccess(queue, cmd);
				}
				else
				{
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.AccessDenied);
					logger.warn("Omni refused our login request for reader with DeviceId = "
							+ cmd.getDeviceID()
							+ " Error code: "
							+ cmd.getError().getErrorCode()
							+ " Error Message: "
							+ cmd.getError().getErrorShortDescription()
							+ ", "
							+ cmd.getError().getErrorDescription());

					OmniTicketQueue.getInstance().onLoginFailure(queue, cmd);
				}
				return;
			}

			if (cmd.getHeader().getRequestType().equals(OmniConst.REQ_TYPE_LOGOUT))
			{
				synchronized (queue)
				{
					// Reset the login to stop automatic re-logon
					queue.setCreds(null);
				}
				
				if (queue.getConnectionState() == OmniTicketReaderQueue.ConnectionState.LoggedIn ||
				    queue.getConnectionState() == OmniTicketReaderQueue.ConnectionState.AwaitingLogoffResponse)
				{
					// Cast member is now logged off from this reader.
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.Connected);
				}
				
				OmniTicketQueue.getInstance().onLogoutNotification(queue, cmd);
				return;
			}

			// Handle the answer to the connect message ourselves.
			if (cmd.getHeader().getRequestType().equals(OmniConst.REQ_TYPE_STATUS))
			{
				queue.setGotStatus(true);

				if (cmd.getError().getErrorCode().equals(BigInteger.ZERO))
				{
					if (cmd.getStatus().getUserInfo() != null)
					{
						// OK. Omni says that this reader is already logged in.
						queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.LoggedIn);

						logger.info("Omni status for reader with DeviceID " + cmd.getDeviceID() + " is logged in.");
					}
					else
					{
						// No one logged it. Need to move to Connected state.
						queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.Connected);						
					}
				}
				else
				{
					queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.Connected);
					logger.warn("Omni returned non zero error code for DeviceId = "
							+ cmd.getDeviceID()
							+ " Error code: "
							+ cmd.getError().getErrorCode()
							+ " Error Message: "
							+ cmd.getError().getErrorShortDescription()
							+ ", "
							+ cmd.getError().getErrorDescription());
				}
				
				OmniTicketQueue.getInstance().onStatusNotification(queue, cmd);
				return;
			}

			// Handle OPERATOR NOT LOGGED error
			if (cmd.getError().getErrorCode().equals(BigInteger.valueOf(76)))
			{
				if (queue.getConnectionState() == ConnectionState.LoggedIn)
				{
					logger.warn("Received error message from Omni "
							+ cmd.getError().getErrorDescription()
							+ ". Trying to re-connect reader "
							+ queue.getReader().getName());
					queue.setConnectionState(ConnectionState.Disconnected);
				}
				OmniTicketQueue.getInstance().onOperatorNotLoggedIn(queue, cmd);
			}

			OmniTicketQueue.getInstance().onOmniMessage(queue,cmd);

		}
		finally
		{
			plogger.trace("performance: OmniTicketQueue::processReaderQueueMessage exit");
		}
	}
	
	private void bounceAllOmniMessages(String shortErrorDescription, String errorDescription) {
		RequestCommand cmd = null;
		synchronized(queue.getOutqueue()) {					
			cmd = queue.getOutqueue().isEmpty() ? null : queue.getOutqueue().getFirst();
		}
		while(cmd != null) {
			
			// We are actually interested only in the Entitlement messages. The rest we just throw away.
			if (cmd.getEntitlement() != null) {
				AnswerCommand acmd = OmniTicket.makeEntitlementAnswer(cmd);
				acmd.getError().setErrorCode(BigInteger.valueOf(1000));
				acmd.getError().setErrorDescription(errorDescription);
				acmd.getError().setErrorShortDescription(shortErrorDescription);
				logger.warn("Rejecting entitlement request for GuestId " + cmd.getHeader().getReferenceNumber() + " because connection to TOR is lost.");
				OmniTicketQueue.getInstance().omniMessage(new OmniResponse(acmd,false));
			}
			
			synchronized(queue.getOutqueue()) {
				logger.warn("Discarding omni request of type " + cmd.getHeader().getRequestType() + " because connection to TOR is lost.");
				queue.getOutqueue().remove(cmd);
				cmd = queue.getOutqueue().isEmpty() ? null : queue.getOutqueue().getFirst();
			}
		}
	}

	public void stop(boolean sendPendingMessages)
	{
		// Stop our thread
		this.run = false;
		this.sendPendingMessages = sendPendingMessages;
		
		// wake up the send thread
		synchronized (queueEvent)
		{
			queueEvent.notify();
		}

		// wait for the send thread to finish
		try
		{
			this.join(6000l);
		}
		catch (InterruptedException e)
		{
			logger.warn("Timed out while trying to stop the send thread for Omni queue.");
		}
		
		killClient();
	}
	
	private void killClient()
	{
		// Close the client connection if open
		if (client != null)
		{
			try
			{
				client.Stop();
			}
			catch (IOException e)
			{
				logger.warn(
						"Caught exception while stopping omni connection client. Non fatal error.",
						e);
			}
			client = null;
		}
		
		queue.setConnectionState(OmniTicketReaderQueue.ConnectionState.Disconnected);
		queue.setGotStatus(false);
	}

	public void send(RequestCommand cmd)
	{
		plogger.trace("performance: OmniTicketQueue::send enter");

		// the send command on queue is thread safe so don't do this inside the
		// synchronized block below
		queue.send(cmd);

		plogger.trace("performance: OmniTicketQueue::send exit");
	}
	
	public void notifyOfEvent()
	{
		synchronized (queueEvent)
		{
			queueEvent.notify();
		}
	}

	public void setReconnect(boolean reconnect)
	{
		this.reconnect = reconnect;
	}

	public boolean isShutdown()
	{
		return shutdown;
	}

	public void setShutdown(boolean shutdown)
	{
		this.shutdown = shutdown;
	}
}
