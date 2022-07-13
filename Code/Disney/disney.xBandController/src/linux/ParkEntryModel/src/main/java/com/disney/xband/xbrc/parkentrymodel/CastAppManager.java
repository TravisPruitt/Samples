package com.disney.xband.xbrc.parkentrymodel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.disney.xband.common.lib.JsonUtil;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.ErrorCode;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.PGXMessage;
import com.disney.xband.parkgreeter.lib.message.toxbrc.PGMessage;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class CastAppManager {
	
	private static Logger logger = Logger.getLogger(CastAppManager.class);
	/*
	 * While all the send threads in the thread pool are busy, the request will start queuing up.
	 * This controls how many requests to queue up before rejecting new requests.
	 */
	private final int maximumSendRequestQueue = 1000;
	
	/*
	 * Persistent connections.
	 */
	private Map<String,CastAppQueue> connections;
	private Map<String,CastAppQueue> connetionsBySessionId;
	
	/*
	 * Persistent connections mapped by subscribed-to location. 
	 */
	private Map<String,Set<CastAppQueue>> locationMap;
	
	/*
	 * Single requests waiting to be serviced. 
	 */
	private Map<String,CastAppMessage> requests;
	
	private LinkedList<CastAppMessage> inqueue;
	
	private Date lastRequestTimeoutCheck = new Date();
	
	/*
	 * Thread pool used to execute all send operations.
	 */
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(
			ConfigOptions.INSTANCE.getSettings().getCastAppCoreThreadPoolSize(),
			ConfigOptions.INSTANCE.getSettings().getCastAppMaxThreadPoolSize(),
			ConfigOptions.INSTANCE.getSettings().getCastAppThreadKeepAliveSec(),
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(maximumSendRequestQueue)
			);
	
	private static class SingletonHolder {
		public static final CastAppManager instance = new CastAppManager();
	}
	
	public static CastAppManager getInstance() {
		return SingletonHolder.instance;
	}
	
	private CastAppManager() {
	}
	
	public void initialize() {
		connections = new HashMap<String,CastAppQueue>();
		connetionsBySessionId = new HashMap<String,CastAppQueue>();
		requests = new HashMap<String,CastAppMessage>();
		locationMap = new HashMap<String,Set<CastAppQueue>>();
		inqueue = new LinkedList<CastAppMessage>();
	}
	
	public void onConfigChanged() {
		
	}
	
	/*
	 * Runs using one of the threads from the thread pool.
	 */
	public void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}
	
	public static void setResponseHeader(Response response, String sContentType) {
		long time = System.currentTimeMillis();

		response.set("Content-Type", sContentType);
		response.set("Server", "xBRC/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
	}
	
	/****************************************************************************************
	 * Subscription Processing 
	 ****************************************************************************************/
	
	private void subscribeToLocation(String location, CastAppQueue queue)
	{
		synchronized(locationMap)
		{
			Set<CastAppQueue> queues = locationMap.get(location);
			if (queues == null)
			{
				queues = new HashSet<CastAppQueue>();
				locationMap.put(location, queues);
			}
			queues.add(queue);
		}
	}
	
	private void unsubscribeFromLocation(String location, CastAppQueue queue)
	{
		synchronized(locationMap)
		{
			Set<CastAppQueue> queues = locationMap.get(location);
			if (queues == null)
				return;
			queues.remove(queue);
		}
	}
	
	private CastAppQueue getQueueBySessionId(String sessionId)
	{
		synchronized(connections)
		{
			return connetionsBySessionId.get(sessionId);
		}
	}
	
	private boolean areListsEqual(List<String> one, List<String> two)
	{
		// check the pointers
		if (one == two)
			return true;
		
		// handle one null
		if (one == null && two != null ||
			one != null && two == null)
			return false;
		
		// handle elements different or order different
		Iterator<String> it1 = one.iterator();
		Iterator<String> it2 = two.iterator();
			
		while(it1.hasNext() && it2.hasNext())
		{
			if (!it1.next().equals(it2.next()))
				return false;
		}
		
		return it1.hasNext() == it2.hasNext();
	}
	
	public CastAppQueue addSubscription(Request request, Response response) throws CastAppException, IOException
	{			
        InputStream is = null;
		String data = null;
		CastAppQueue newSubscription = null;

        try {
             is = request.getInputStream();
             data = new Scanner(is).useDelimiter("\\A").next();
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

		logger.trace("(greeter) From cast app: " + data);
		PGMessage message = null;
		
		if (data != null && !data.isEmpty())
			message = JsonUtil.convertToPojo(data, PGMessage.class);
		
		if (message == null)
			throw new CastAppException(ErrorCode.invalidRequestParameters, "Invalid request. Json formatted message was not present.");
		if (message.getSessionId() == null || message.getSessionId().isEmpty())
			throw new CastAppException(ErrorCode.invalidRequestParameters, "Invalid request. Required field sessionId is missing.");
		
		// see if we already have a queue for this session ID
		CastAppQueue queue = connetionsBySessionId.get(message.getSessionId());
				
		boolean setLocations = false;
		
		if (queue != null)
		{	
			// Normally, the queue should not be listening since we just got another request for this session.
			// If we have a listening queue the we will ignore close that one and replace it with this one.
			if (queue.isListening())
			{
				logger.trace("(greeter) Received a second subscription requests for the same already listening session " + queue.getSessionId() + ". Closing prior connection.");
				queue.close();
			}
			
			queue.processRequest(request, response);
			
			if (!areListsEqual(queue.getLocations(), message.getLocations()))
			{
				setLocations = true;
				
				synchronized(locationMap)
				{
					for (Set<CastAppQueue> queues : locationMap.values())
						queues.remove(queue);
				}
			}
			
			queue.sendPendingMessages();
		}
		else
		{			
			queue = new CastAppQueue(request, response, true, message.getSessionId());
			setLocations = true;
			newSubscription = queue;
			
			synchronized(connections)
			{
				connections.put(queue.getKey(), queue);
				logger.trace("Added key " + queue.getKey() + " to connections. Size: " + connections.size());
				connetionsBySessionId.put(queue.getSessionId(), queue);
				logger.trace("Added session " + queue.getSessionId() + " to connetionsBySessionId. Size: " + connetionsBySessionId.size());
			}
		}
			
		if (setLocations)
		{
			if (message.getLocations() != null && !message.getLocations().isEmpty())
			{
				if (message.getLocations().get(0).equals("*"))
				{
					// all locations
					for (LocationInfo location : XBRCController.getInstance().getReaderLocations())
						subscribeToLocation(location.getName(), queue);
				}
				else
				{
					// specific location
					for (String location : message.getLocations())
						subscribeToLocation(location, queue);
				}
			}
		}
		
		return newSubscription;
	}

	/*
	 * Subscribe already connected session to receive location reader events.
	 */
	public void subscribeToLocation(String locationName, String sessionId) throws CastAppException
	{
		CastAppQueue queue = getQueueBySessionId(sessionId);
		
		if (queue == null)
			throw new CastAppException(ErrorCode.invalidRequestParameters, "Invalid request. Connection with sessionId " + sessionId + " was not found.");
		
		subscribeToLocation(locationName, queue);
	}
	
	/*
	 * Unsubscribe from location. 
	 */
	public void unsubscribeFromLocation(String locationName, String sessionId)
	{
		CastAppQueue queue = getQueueBySessionId(sessionId);
		
		if (queue == null)
		{
			logger.warn("(greeter) Cannot unsubscribe from location " + locationName + 
						" because connection with sessionId " + sessionId + " was not found");
			return;
		}
		
		unsubscribeFromLocation(locationName, queue);
	}
	
	public void removeSubscription(CastAppQueue queue)
	{
		synchronized(connections)
		{
			logger.trace("(greeter) Removing key " + queue.getKey() + " from connections. Size: " + connections.size());
			connections.remove(queue.getKey());
			logger.trace("(greeter) connections size: " + connections.size());
			logger.trace("(greeter) Removing session " + queue.getSessionId() + " from connectionsBySessionId. Size: " + connetionsBySessionId.size());
			connetionsBySessionId.remove(queue.getSessionId());
			logger.trace("(greeter) connetionsBySessionId size: " + connetionsBySessionId.size());
		}
		
		synchronized(locationMap)
		{
			for (Set<CastAppQueue> queues : locationMap.values())
				queues.remove(queue);
		}
	}
	
	/*
	 * Notify only the connections with matching locations.
	 */
	public void notify(PGXMessage msg) {
		
		Collection<CastAppQueue> queues = null;
		
		if (msg.getReader() != null)
		{
			ReaderInfo reader = 
					XBRCController.getInstance().getReader(msg.getReader().getName());
			
			synchronized(locationMap)
			{
				queues = locationMap.get(reader.getLocation().getName());
			}
		}
		else
		{
			logger.warn("(greeter) CastAppManager: Notify was called but the Reader was not specified. "
						+ "Broadcasting message to all connected cast app applications.");
			
			synchronized(connections)
			{
				queues = connections.values();
			}
		}
		
		if (queues == null)
			return;
		
		synchronized(locationMap)
		{
			for (CastAppQueue queue : queues)
				queue.send(msg);
		}
	}
	
	/*
	 * Notify everyone.
	 */
	public void broadcast(PGXMessage msg) {
		synchronized(connections)
		{
			for (CastAppQueue queue : connections.values())
				queue.send(msg);
		}
	}
	
	public void unicast(List<PGXMessage> messages, CastAppQueue queue)
	{
		queue.send(messages);
	}
	
	public CastAppMessage getNextMessage() {
		synchronized(inqueue) {
			return inqueue.pollFirst();
		}
	}
	
	/******************************************************************************
	 * Pending Request Processing
	 ******************************************************************************/
	
	public void addRequest(Request request, Response response) throws IOException 
	{		
		InputStream is = null;
        String data = null;

        try {
            is = request.getInputStream();
		    data = new Scanner(is).useDelimiter("\\A").next();
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

        if(data != null) {
            PGMessage message = JsonUtil.convertToPojo(data, PGMessage.class);

            logger.trace("(greeter) From cast app: " + data);

            CastAppQueue queue = new CastAppQueue(request, response, false, message.getSessionId());
            CastAppMessage cam = new CastAppMessage(queue, message);

            synchronized(requests)
            {
                requests.put(queue.getKey(), cam);
            }

            synchronized(inqueue) {
                inqueue.addLast(cam);
            }
        }
        else {
            logger.warn("Failed to get data from the request");
        }
	}
	
	/*
	 * Internal requests allow to perform CastApp operations without actually receiving 
	 * a cast app requests, for example opening a location in response to a TAP event. 
	 */
	public void addInternalRequest(CastAppMessage cam)
	{
		synchronized(requests)
        {
            requests.put(Integer.toString(cam.hashCode()), cam);
        }

        synchronized(inqueue) {
            inqueue.addLast(cam);
        }
	}
	
	public void removeRequest(String key)
	{
		synchronized(requests)
		{
			requests.remove(key);
		}
	}
	
	public CastAppMessage getLocationRequest(String locationName, String portalId)
	{
		synchronized(requests)
		{
			for (CastAppMessage cam : requests.values())
			{
				if (cam.getMessage().getType() == PGMessage.Type.openLocation ||
					cam.getMessage().getType() == PGMessage.Type.bumpLocation ||
					cam.getMessage().getType() == PGMessage.Type.closeLocation )
				{
					if (cam.getMessage().getLocationName().equals(locationName) &&
						(portalId == null || cam.getMessage().getCast().getPortalId().equals(portalId)))
						return cam;
				}
			}		
		}
		
		return null;
	}
	
	/*
	 * Respond to a request
	 */
	public void respond(CastAppMessage req, PGXMessage resp)
	{
		// No need to respond to internal requests, but the request needs to be removed.
		if (req.isInternal())
		{
			removeRequest(Integer.toString(req.hashCode()));
			return;
		}
		
		req.getQueue().send(resp);
	}
	
	/*
	 * Timeout requests that for some reason did not get answered. There should not be many, but sometimes unexpected
	 * exception in the main processor thread can leave these hanging.
	 */
	public void timeoutRequests(PGXMessageFactory pgxmsgfactory)
	{		
		
		// Let's be accurate to 5 seconds to save processing time.
		Date now = new Date();
		if (now.getTime() - lastRequestTimeoutCheck.getTime() < 5000)
			return;
		
		// timeout requests
		synchronized(requests)
		{
			long timeout = ConfigOptions.INSTANCE.getSettings().getCastAppResponseTimeoutSec() * 1000;
			
			for (CastAppMessage cam : requests.values())
			{
				if (!cam.isRequestTimedOut() && now.getTime() - cam.getReceivedTime().getTime() > timeout)
				{	
					logger.warn("(greeter) Timing out cast app request after " + ((now.getTime() - cam.getReceivedTime().getTime()) / 1000) + 
								 " seconds due to xBRC server error." + cam.getMessage().toLogString() );
					
					cam.setRequestTimedOut(true);
					
					// Allow the CEP to re-process the requests.
					synchronized(inqueue) {
						inqueue.addLast(cam);
					}		
				}
			}		
		}
		
		// time out connections
		synchronized(connections)
		{
			LinkedList<CastAppQueue> deleteList = new LinkedList<CastAppQueue>();
			
			long timeoutMs = ConfigOptions.INSTANCE.getSettings().getCastappNotifyTimeoutSec() * 1000;
			
			for (CastAppQueue queue : connections.values())
			{
				if (!queue.isListening() && (now.getTime() - queue.getClosedTime().getTime() > timeoutMs))
				{
					logger.info("(greeter) Timing out greeter app subscription session  " + queue.getSessionId());
					deleteList.add(queue);
				}
			}
			
			for (CastAppQueue queue : deleteList)
				removeSubscription(queue);
		}
	}
}
