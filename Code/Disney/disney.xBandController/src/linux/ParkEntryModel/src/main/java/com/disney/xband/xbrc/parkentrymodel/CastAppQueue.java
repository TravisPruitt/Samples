package com.disney.xband.xbrc.parkentrymodel;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.disney.xband.common.lib.JsonUtil;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXMessage;

public class CastAppQueue implements Runnable {
	private static Logger logger = Logger.getLogger(CastAppQueue.class);
	
	private String requestIp;
	private int requestPort;
	private Response response;
	private String tag;
	private String key;
	private String sessionId;
	private boolean persistent;
	private boolean running = false;
	private Date requestTime;
	private Date closedTime;
	private List<String> locations;
		
	private LinkedList<Object> outqueue;
	
	public CastAppQueue(Request request, Response response, boolean persistent, String sessionId) {		
		outqueue = new LinkedList<Object>();		
		key = Integer.toString(System.identityHashCode(this));
		this.persistent = persistent;
		this.sessionId = sessionId;	
		
		processRequest(request, response);		
	}
	
	public void processRequest(Request request, Response response)
	{
		synchronized(this)
		{	
			this.response = response;
			this.requestIp = request.getClientAddress().getAddress().getHostAddress();
			this.requestPort = request.getClientAddress().getPort();
			this.tag = "(greeter) " + requestIp + ":" + requestPort + " ";
			this.requestTime = new Date();
			
			if (response == null)
				logger.fatal(tag + "The response object is null. This should never happen.");
		}
	}
	
	public String getKey()
	{
		return key;
	}
	
	public void sendPendingMessages()
	{
		synchronized(outqueue) 
		{
			if (outqueue.isEmpty())
				return;
			
			if (!running)
			{
				running = true;
				CastAppManager.getInstance().execute(this);
			}
		}
	}

	public void send(PGXMessage msg) {
		synchronized(outqueue) {
			
			outqueue.addLast(msg);
			
			// safety precaution
			if (outqueue.size() > 500) {
				logger.warn(tag + outqueue.size() + " messages in cast app out queue. Expiring oldest message.");
				outqueue.removeFirst();
			}			
		}
		
		sendPendingMessages();
	}
	
	public void send(List<PGXMessage> messages)
	{
		synchronized(outqueue) {
			
			for (PGXMessage msg : messages) {
				
				outqueue.addLast(msg);
				
				// safety precaution
				if (outqueue.size() > 500) {
					logger.warn(tag + outqueue.size() + " messages in cast app out queue. Expiring oldest message.");
					outqueue.removeFirst();
				}			
			}
		}
		
		sendPendingMessages();
	}
	
	public void close()
	{	
		synchronized(this)
		{
			PrintStream body;
			try
			{			
				if (response != null)
				{
					closedTime = new Date();
					body = response.getPrintStream();
					body.close();
				}
			}
			catch (IOException e)
			{
				logger.error(tag + "Failed to close the cast app connection", e);
			}
			
			response = null;
		}
		
		if (!persistent)
			CastAppManager.getInstance().removeRequest(key);
	}
	
	@Override
	public void run()
	{	
		synchronized(this)
		{
			try
			{	
				if (response == null)
				{
					logger.trace(tag + "Queueing message to session " + sessionId + 
								" because there is no active subscription for this session");
					return;
				}
				
				Object msg;			
				
				synchronized(outqueue) {	
					msg = outqueue.getFirst();
				}
				
				List<Object> messages = null;
				
				// Persistent connections expect an array of messages, non-persistent only one message.
				if (persistent)
				{
					messages = new LinkedList<Object>();
					
					while(msg != null) {
						messages.add(msg);
						synchronized(outqueue) {
							outqueue.remove(msg);
							msg = outqueue.isEmpty() ? null : outqueue.getFirst();
						}
					}
				}
				else
				{
					synchronized(outqueue) {
						outqueue.remove(msg);
					}
				}			
				
				try
				{					
					CastAppManager.setResponseHeader(response, "application/json");
					response.setCode(200);
					PrintStream body = response.getPrintStream();
					String content;
					if (messages != null)
						content = JsonUtil.convertToJson(messages);
					else
						content = JsonUtil.convertToJson(msg);
					body.println(content);
					body.flush();
					logger.trace(tag + "To cast app: " + content);
					if (body.checkError())					
						logger.error(tag + "Failed to send Cast app message.");
				} catch (IOException e) {
					logger.error(tag + "IOException while sending Cast app message.", e);
				} catch (RuntimeException e) {
					logger.error(tag + "RuntimeException while sending Cast app message.", e);
				} catch (Exception e) {
					logger.error(tag + "Exception while sending Cast app message.", e);
				}
			}
			finally
			{					
				close();
				running = false;
			}
		}
	}

	public Date getRequestTime()
	{
		return requestTime;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public Date getClosedTime()
	{
		return closedTime;
	}
	
	public boolean isListening()
	{
		return response != null;
	}

	public List<String> getLocations()
	{
		return locations;
	}

	public void setLocations(List<String> locations)
	{
		this.locations = locations;
	}
}
