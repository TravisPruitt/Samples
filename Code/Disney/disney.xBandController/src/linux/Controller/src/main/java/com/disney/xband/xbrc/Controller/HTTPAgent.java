package com.disney.xband.xbrc.Controller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.disney.xband.ac.lib.client.XbConnection;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class HTTPAgent {
	
	// singleton pattern
	public static HTTPAgent INSTANCE = new HTTPAgent();

	private static Logger logger = Logger.getLogger(HTTPAgent.class);
	private static Logger plogger = Logger.getLogger("performance." + HTTPAgent.class.toString());

	private Thread sendThread = null;

	private boolean run = true;

	// if using HTTP instead of JMS, this holds the destination parameters
	private int cMaxHTTPSend = -1;
	private Date dtNextHTTPSendTime = new Date();
	private HashMap<String, Boolean> postMessageTypes = null;

	private boolean processConfigChange = true;
	private String httpPublishingError = null;

	ThreadPoolExecutor executor;
	HttpBatchJob httpBatchJob = null;

	public void initialize() {
		executor = new ThreadPoolExecutor(ConfigOptions.INSTANCE
				.getControllerInfo().getUpdatestreamcorepoolsize(),
				ConfigOptions.INSTANCE.getControllerInfo()
						.getUpdatestreammaxpoolsize(), 60, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(ConfigOptions.INSTANCE
						.getControllerInfo().getUpdatestreamqueuesize()));

		httpBatchJob = new HttpBatchJob();

		sendThread = new Thread() {
			public void run() {
				runSendThread();
			}
		};
		sendThread.start();
	}

	public void Terminate() {
		run = false;

		if (sendThread == null)
			return;

		synchronized (sendThread) {
			// wake up our send thread to let it exit
			sendThread.notify();
		}

		sendThread = null;
	}

	public String getStatusMessage() {
		if (httpPublishingError != null)
			return httpPublishingError;
		return null;
	}

	private void shutdownExecutor(long secondsToWait)
	{
		if (executor == null)
			return;
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(secondsToWait, TimeUnit.SECONDS);
			if (!executor.isTerminated())
			{
				logger.warn("Killing remaining http send threads after waiting for " + secondsToWait + " seconds for them to stop");
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			logger.error("Thread was interrupted while waiting for http send threads to shutdown", e);
		}
	}
	
	/*
	 * The send thread
	 */
	private void runSendThread() {
		// run indefinitely
		while (true) {

			// time to quit?
			if (!run)
			{
				shutdownExecutor(5);
				break;
			}

			// any changes to configuration? If so, reread
			if (processConfigChange) {
				processConfigChange();
				processConfigChange = false;
			}

			Date dtNow = new Date();

			// is it time to send?
			if (dtNow.getTime() >= dtNextHTTPSendTime.getTime()) {
				if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
					plogger.trace("Start sending queued messages to HTTP.");

				// send any queued data (dtNextHTTPSendTime is set inside
				// sendQueuedMessagesViaHTTP())
				sendQueuedMessagesViaHTTP();

				if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
					plogger.trace("End sending queued messages to HTTP.");
			}

			// do the wait before the end of the loop
			try {
				synchronized (sendThread) {
					sendThread.wait(100);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	void processConfigChange() {
		try {
			setPostMessageTypes(ConfigOptions.INSTANCE.getControllerInfo()
					.getUpdateStreamMessageTypesAsList());
			dtNextHTTPSendTime = new Date(new Date().getTime()
					+ ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamIntervalMs());
			
			shutdownExecutor(10);
			
			executor = new ThreadPoolExecutor(ConfigOptions.INSTANCE
					.getControllerInfo().getUpdatestreamcorepoolsize(),
					ConfigOptions.INSTANCE.getControllerInfo()
							.getUpdatestreammaxpoolsize(), 60, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(ConfigOptions.INSTANCE
							.getControllerInfo().getUpdatestreamqueuesize()));
			
		} catch (Exception e) {
			logger.error("Failed to process config chagnge", e);
		}
	}
	
	private String setPostMessageTypes(Collection<String> messageTypes)
	{
		// If one of the message types is a "*" or the mssageTypes are null then
		// send all message types.
		boolean sendAllTypes = messageTypes == null || messageTypes.isEmpty();
		
		if (sendAllTypes)
		{
			postMessageTypes = null;
			return "*";		
		}
		
		StringBuffer sb = new StringBuffer();
		postMessageTypes = new HashMap<String, Boolean>();
		for (String messageType : messageTypes) {
			if (messageType.equals("*"))
				sendAllTypes = true;
			else
				postMessageTypes.put(messageType, true);

			if (sb.length() > 0)
				sb.append(" ");
			sb.append(messageType);
		}
		
		if (sendAllTypes)
		{
			postMessageTypes = null;
			return "*";		
		}
		
		return sb.toString();
	}

	public void setHTTPUpdateStream(String sURLPost, int cMaxHTTPSend,
			int cmsecPostInterval, long lAfter, String sPreferredGuestIdType,
			Collection<String> messageTypes, int updateStreamBatchSize) {
		
		if (sURLPost == null)
		{
			logger.error("Ignoring setHTTPUpdateStream because sURLPost parameter is null");
			return;
		}
		
		if (sPreferredGuestIdType == null)
		{
			logger.error("Ignoring setHTTPUpdateStream because sPreferredGuestIdType parameter is null");
			return;
		}
		
		this.cMaxHTTPSend = cMaxHTTPSend;

		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
		
			// Allows the after parameter to be optional.
			if (lAfter != -1)
				ConfigOptions.INSTANCE.getStoredStatus()
						.setLastMessageIdToPostStream(lAfter);
			
			if (!sURLPost.equals(ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL()))
				ConfigOptions.INSTANCE.getControllerInfo().setUpdateStreamURL(sURLPost, conn);
			
			if (!sPreferredGuestIdType.equals(ConfigOptions.INSTANCE.getControllerInfo().getPreferredGuestIdType()))
				ConfigOptions.INSTANCE.getControllerInfo().setPreferredGuestIdType(sPreferredGuestIdType, conn);
	
			ConfigOptions.INSTANCE.getControllerInfo()
						.setUpdateStreamMessageTypes(setPostMessageTypes(messageTypes), conn);
			
			if (updateStreamBatchSize != ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamBatchSize())
				ConfigOptions.INSTANCE.getControllerInfo().setUpdateStreamBatchSize(updateStreamBatchSize, conn);
			
			if (cmsecPostInterval != ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamIntervalMs())
			{
				ConfigOptions.INSTANCE.getControllerInfo().setUpdateStreamIntervalMs(cmsecPostInterval);
				dtNextHTTPSendTime = new Date(new Date().getTime()
						+ ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamIntervalMs());
			}
		} 
		catch (Exception e) 
		{
			logger.error("Failed to persist updatestream settings to the database", e);
		}
		finally
		{
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	public void clearHTTPUpdateStream() {
		Connection conn = null;
		try
		{
			this.cMaxHTTPSend = -1;
			conn = Controller.getInstance().getPooledConnection();
			ConfigOptions.INSTANCE.getControllerInfo().setUpdateStreamURL(null, conn);			
		}
		catch (Exception e) 
		{
			logger.error("Failed to persist updatestreamUrl to the database", e);
		}
		finally
		{
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	public String getHTTPUpdateStreamURL() {
		return ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL();
	}

	private static String preparePlaceHolders(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length;) {
			builder.append("?");
			if (++i < length) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	private static void setValues(PreparedStatement preparedStatement,
			Collection<String> values, int startIndex) throws SQLException {
		for (String value : values) {
			preparedStatement.setObject(startIndex++, value);
		}
	}

	private void sendQueuedMessagesViaHTTP() {
		boolean asyncEnabled = ConfigOptions.INSTANCE.getControllerInfo()
				.getUpdateStreamUseAsync();

		if (ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL().isEmpty()
				|| ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL().startsWith("#")) {
			httpPublishingError = null;
			return;
		}

		if (asyncEnabled) {
			if (httpBatchJob.getRunningJobs() > 0) {
				logger.trace("HTTP post jobs are still running. Will try sending again later.");
				return;
			}
		}

		// read events from the database
		long lLastMessageToPost = ConfigOptions.INSTANCE.getStoredStatus()
				.getLastMessageIdToPostStream();

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean startedAsyncJobs = false;

		try {			
			conn = Controller.getInstance().getPooledConnection();

			String sSQL = "SELECT * from Messages where Id > ? AND SendToHttp=1";

			if (postMessageTypes != null) {
				sSQL += " and MessageType in ("
						+ preparePlaceHolders(postMessageTypes.size()) + ")";
			}
			
			sSQL += " limit 1000";

			pstmt = conn.prepareStatement(sSQL);
			pstmt.setLong(1, lLastMessageToPost);
			if (postMessageTypes != null)
				setValues(pstmt, postMessageTypes.keySet(), 2);
			pstmt.execute();
			rs = pstmt.getResultSet();

			int cSentMessages = 0;
			long id = lLastMessageToPost;
			Date dtStart = new Date();

			// BUG#: 5850 - strip off any trailing, non-numeric data. This allow
			// non-numerics
			// (e.g. "-1", "-2" to be used to distinguish xBRCs while not
			// affecting GXP
			String venue = ConfigOptions.INSTANCE.getControllerInfo()
					.getVenue();
			venue = venue.replaceFirst("[^\\d].*$", "");

			// escape name in case there are any weird characters in it
			venue = StringEscapeUtils.escapeXml(venue);

			// add messages until reached maximum per batch
			boolean moreMessages = false;
			
			do
			{
				//
				// Build the batch surrounded by the envelope
				//
				
				String sEnv = "<venue name=\"" + venue + "\" " + "time=\""
						+ formatTime(new Date().getTime()) + "\">\n";

				StringBuilder sBatch = new StringBuilder();
				sBatch.append(sEnv);
				
				int cBatch = 0;
				int cBatchMax = ConfigOptions.INSTANCE.getControllerInfo()
						.getUpdateStreamBatchSize();
				
				while (moreMessages = rs.next() && cBatch < cBatchMax) {
					id = rs.getLong("id");
					String sMessagePayload = rs.getString("Payload");
	
					// TODO: consider a better way - this is pretty sucky.
					String sSeq = "    <sequence>" + id + "</sequence>\n    ";
					sMessagePayload = sMessagePayload.replaceFirst("</message>",
							sSeq + "</message>");
					sBatch.append(sMessagePayload);
	
					// is it time to send
					cBatch++;
				}
				
				sBatch.append("</venue>");
				
				if (cBatch == 0)
				{
					dtNextHTTPSendTime = new Date(new Date().getTime()
							+ ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamIntervalMs());
					break;
				}
				
				//
				// Send the whole batch
				//
				
				try {
					logger.debug("Sending " + cBatch + " message(s) via HTTP...");

					if (asyncEnabled)
					{
						if (!startedAsyncJobs)
						{
							// Add our own loop job. This is to prevent batch threads that managed to terminate before we were able to start
							// all threads updating the lastmessagetoupdatestream. We want lastmessagetoupdatestream to be updated when last 
							// thread finishes from this batch of threads.
							
							httpBatchJob.incNumJobs();
							startedAsyncJobs = true;
						}
						
						sendHTTPMessageAsync(ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL(), sBatch.toString(), httpBatchJob, id);
						httpBatchJob.incNumJobs();
					}
					else
					{
						sendHTTPMessage(ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL(), sBatch.toString());
					}

					if (logger.isTraceEnabled())
						logger.trace(sBatch.toString());
					cSentMessages += cBatch;
					logger.debug("...sent " + cSentMessages + " messages");

					dtNextHTTPSendTime = new Date(new Date().getTime()
							+ ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamIntervalMs());

				} catch (Exception e) {
					logger.error("!! Error: communicating with HTTP URL: ",
							e);

					logger.error("!! pausing out of push mode for "
							+ ConfigOptions.INSTANCE.getControllerInfo()
									.getUpdateStreamPostRetry_sec()
							+ " seconds");

					dtNextHTTPSendTime = new Date(new Date().getTime()
							+ ConfigOptions.INSTANCE.getControllerInfo()
									.getUpdateStreamPostRetry_sec() * 1000);

					httpPublishingError = "Cannot post messages to "
							+ ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL() + ": "
							+ e.getLocalizedMessage();

					break;
				}
			}
			while (moreMessages && 
				   (cMaxHTTPSend <= 0 || cSentMessages < cMaxHTTPSend) &&
				   (!asyncEnabled || (cSentMessages < ConfigOptions.INSTANCE
					.getControllerInfo().getUpdatestreammaxpoolsize())));

			Date dtEnd = new Date();

			long msec = dtEnd.getTime() - dtStart.getTime();
			double msecPerMessage = 0.0;
			if (cSentMessages > 0)
				msecPerMessage = (double) msec / cSentMessages;
			Processor.INSTANCE.getStatusObject().getPerfUpstreamMsec()
					.processValue(msecPerMessage);

			if (!asyncEnabled) {
				// update status
				ConfigOptions.INSTANCE.getStoredStatus()
						.setLastMessageIdToPostStream(id);
			}

			// clear all errors
			httpPublishingError = null;
		} catch (Exception e1) {
			logger.error("!! Error reading messages from database: "
					+ e1.getLocalizedMessage());

			httpPublishingError = "Error reading messages from database: "
					+ e1.getLocalizedMessage();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);

			// finish our own loop job
			if (asyncEnabled && startedAsyncJobs)
				httpBatchJob.onSendFinished(-1);
		}
	}
	
	/*
	 * This method uses the trhead pool to send a message, but it does it outside of any batch jobs.
	 */
	public void sendHTTPMessageAsync(String url, String data) {
		executor.execute(new SendMessage(url, data, null, 0));
	}

	public boolean sendHTTPMessage(String sURL, String sData) throws Exception {
		boolean success = false;
		BufferedWriter bw = null;

		try {
			logger.trace("Calling " + sURL);

			URL url = new URL(sURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-type", "application/xml");
			conn.setRequestProperty("Content-length",
					Integer.toString(sData.length()));
            conn.setRequestProperty("Authorization", XbConnection.getAuthorizationString());
			bw = new BufferedWriter(new OutputStreamWriter(
					conn.getOutputStream()));
			bw.write(sData);
			bw.close();
			if (conn.getResponseCode() >= 400) {
				logger.error("!! Error: " + conn.getResponseCode()
						+ " received from " + sURL);
			} else {
				success = true;
			}
		} finally {
			if (bw != null)
				bw.close();

			logger.trace("Finished calling " + sURL);
		}

		return success;
	}

	/*
	 * Unused (unusable)
	 * 
	 * private String getClientId() { String sOwnIp = getOwnIp(); return "xBRC-"
	 * + sOwnIp.replace('.', '-'); }
	 */

	private static String formatTime(long lTime) {
		Date dt = new Date(lTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}

	public void setProcessConfigChange(boolean processConfigChange) {
		this.processConfigChange = processConfigChange;
	}

	class SendMessage implements Runnable {
		private String url;
		private String data;
		private HttpBatchJob batchJob;
		private Long id;

		public SendMessage(String url, String data, HttpBatchJob batchJob,
				long id) {
			this.url = url;
			this.data = data;
			this.batchJob = batchJob;
			this.id = id;
		}

		@Override
		public void run() {
			try {
				sendHTTPMessage(url, data);
				if (batchJob != null)
					batchJob.onSendFinished(id);
			} catch (Exception e) {
				logger.error("Caught exception trying to send message to "
						+ url, e);
				if (batchJob != null)
					batchJob.onSendFinished(-1);
			}
		}
	}

	public void sendHTTPMessageAsync(String url, String data,
			HttpBatchJob batchJob, long id) {
		executor.execute(new SendMessage(url, data, batchJob, id));
	}

	class HttpBatchJob {
		int numJobs = 0;
		long lastIdSent = -1;

		int getRunningJobs() {
			synchronized (this) {
				return numJobs;
			}
		}

		void incNumJobs() {
			synchronized (this) {
				numJobs++;
				if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
					logger.trace("New http send thread job started: " + numJobs);
			}
		}

		void onSendFinished(long sentId) {
			synchronized (this) {
				if (sentId > lastIdSent)
					lastIdSent = sentId;

				numJobs--;

				if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
					logger.trace("Http send thread job finished: " + numJobs);

				if (numJobs <= 0) {
					synchronized (INSTANCE) {
						if (lastIdSent != -1) {
							if (ConfigOptions.INSTANCE.getControllerInfo()
									.getVerbose())
								logger.trace("Updating lastmessagetoupdatestream to "
										+ lastIdSent);

							ConfigOptions.INSTANCE.getStoredStatus()
									.setLastMessageIdToPostStream(lastIdSent);
						}
					}
				}
			}
		}
	}
}
