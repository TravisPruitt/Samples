package com.disney.xband.xbrc.Controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.disney.xband.ac.lib.*;
import com.disney.xband.ac.lib.client.AcManager;
import com.disney.xband.ac.lib.client.IAcManager;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.audit.*;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.common.lib.security.UserContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.SocketConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.JsonUtil;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.ResponseFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.lib.performance.PerfMetric;
import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.common.lib.performance.PerfMetricMetadataEnvelope;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;
import com.disney.xband.common.scheduler.ui.SchedulerServer;
import com.disney.xband.lib.controllerapi.LrrEventsByBand;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.lib.controllerapi.ReaderSequence;
import com.disney.xband.lib.controllerapi.ReaderSequenceInfo;
import com.disney.xband.lib.xbrapi.AVMSEvent;
import com.disney.xband.lib.xbrapi.BioOptionsMsg;
import com.disney.xband.lib.xbrapi.HelloMsg;
import com.disney.xband.lib.xbrapi.LightMsg;
import com.disney.xband.lib.xbrapi.LrrEvent;
import com.disney.xband.lib.xbrapi.RawAVMSEvent;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.lib.xbrapi.ReaderManifest;
import com.disney.xband.lib.xbrapi.ReaderRepo;
import com.disney.xband.lib.xbrapi.RfidOptionsMsg;
import com.disney.xband.lib.xbrapi.TapEvent;
import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrEventType;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_COMMAND;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.lib.xbrapi.XbrBandCommands;
import com.disney.xband.lib.xbrapi.XbrDiagEvent;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.lib.xbrapi.XbrEvents;
import com.disney.xband.lib.xbrapi.XbrJsonMapper;
import com.disney.xband.lib.xbrapi.XbrJsonMapperFast;
import com.disney.xband.lib.xbrapi.XfpDiagEvent;
import com.disney.xband.xbrc.Controller.PackageManager.ReaderVersion;
import com.disney.xband.xbrc.Controller.model.ReadersInfo;
import com.disney.xband.xbrc.Controller.model.UpdateStreamInfo;
import com.disney.xband.xbrc.Controller.model.WatchedBandInfo;
import com.disney.xband.xbrc.lib.config.EventConfig;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.config.UpdateConfig;
import com.disney.xband.xbrc.lib.db.ImageService;
import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.db.SchedulerItemService;
import com.disney.xband.xbrc.lib.db.StoredConfigService;
import com.disney.xband.xbrc.lib.db.XbandCommandService;
import com.disney.xband.xbrc.lib.entity.GuestInfo;
import com.disney.xband.xbrc.lib.entity.Image;
import com.disney.xband.xbrc.lib.entity.Images;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.TransmitReaderStatus;
import com.disney.xband.xbrc.lib.entity.XbrcHeartbeat;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.idms.IDMSResolver;
import com.disney.xband.xbrc.lib.model.ControllerInfo;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.MessageGenerator;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.TapEventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xbrc.lib.utils.ControllerLibUtils;
import com.disney.xband.xbrc.lib.utils.FileUtils;
import com.disney.xband.xbrc.lib.utils.ReaderPropertyCache;
import com.disney.xband.xbrc.lib.utils.XbrcDateFormatter;
import com.disney.xband.xbrc.scheduler.SchedulerSettings;
import com.disney.xband.xbrc.spacemodel.TapMessageArgs;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

public class WebServer implements Container
{
	// singleton
	public static WebServer INSTANCE = new WebServer();
	private static Properties prop = null;
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static DateTimeFormatter jodaDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static Logger logger = Logger.getLogger(WebServer.class);
    private static Logger configLogger = Logger.getLogger("controllerconfig");
	private static boolean initialized = false;

	// class data
	private final int UNKNOWN_LOCATION_ID = 0;
	private org.simpleframework.transport.connect.Connection sockConn = null;
	private org.simpleframework.transport.connect.Connection sockConnSSL = null;
	private boolean bCalibrate = false;
	private PackageManager reposManager = new PackageManager();
    private MediaManager mediaManager = new MediaManager();
	private PerfMetricMetadataEnvelope perfMetricMetadata;
    private ReaderPropertyCache readersLastEventId = new ReaderPropertyCache("lastIdReceived", "bigint(20)", 10000);    
    private ReaderPropertyCache readersLastHello = new ReaderPropertyCache("timeLastHello", "bigint(20)", 1000);
    private SchedulerServer schedulerServer = new SchedulerServer();

    // Access Control
    private IAcManager acManager;
    private IAuthService authManager;
    private boolean acDisabled;

    // Resources map
    private ResourcesMap rmap;

    private enum MessagePayloadFormat 
    { 
    	
    	json("application/json"), 
    	xml("application/xml");
    	
    	private String contentType;
    	
    	private MessagePayloadFormat(String contentType)
    	{
    		this.contentType = contentType;
    	}
    	
    	public String getContentType()
    	{
    		return this.contentType;
    	}
    };

    // helpers
	private PropertiesManager propertiesManager = new PropertiesManager();	

	/*
	 * Initialization and termination methods
	 */

	private WebServer()
	{
	}

	public static void setInitialized(boolean initialized)
	{
		WebServer.initialized = initialized;
	}
	
	public void Initialize(Properties prop) throws Exception
	{
		WebServer.prop = prop;
        Processor.prop = prop;

		// even if this xbrc is not configured to listen to https request,
		// it communicates with other parts of the system using ssl.
		setupSSLProperties();

        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        rmap = new ResourcesMap();
        initializeAccessControl();
        Processor.initializeAudit();
        
        schedulerServer.initialize(XconnectScheduler.getInstance());

		try
		{
			initializePackageManagers();
		}
		catch(Exception e)
		{
			// already logged
		}
	}

	public void Start() throws IOException
	{
		// start the Http connection
		int nHttpPort = ConfigOptions.INSTANCE.getControllerInfo()
				.getHttpPort();
		if (nHttpPort > 0)
		{
			sockConn = new SocketConnection(this, ConfigOptions.INSTANCE.getControllerInfo().getWebserverthreadcount());
			SocketAddress address = new InetSocketAddress(nHttpPort);
			sockConn.connect(address);
		}

		// start the Https connection
		int nHttpsPort = ConfigOptions.INSTANCE.getControllerInfo()
				.getHttpsPort();
		if (nHttpsPort > 0)
		{
			sockConnSSL = new SocketConnection(this, ConfigOptions.INSTANCE.getControllerInfo().getWebserverthreadcount());
			SocketAddress address = new InetSocketAddress(nHttpsPort);
			SSLContext sslc = createSSLContext();
			sockConnSSL.connect(address, sslc);
		}
	}

	public void Stop()
	{
		try
		{
			if (sockConn != null)
				sockConn.close();
		}
		catch (IOException e)
		{
			logger.error("Error closing insecure web server: " + e);
		}

		try
		{
			if (sockConnSSL != null)
				sockConnSSL.close();
		}
		catch (IOException e)
		{
			logger.error("Error closing secure web server: " + e);
		}

	}
	
	private void setupSSLProperties()
	{
		String sslServerStore = prop.getProperty("ssl.keyStore.server");
		String sslServerStorePassword = prop.getProperty("ssl.keyStorePassword.server");
		String sslClientKeystore = prop.getProperty("ssl.keyStore.client");
		String sslClientKeystorePassword = prop.getProperty("ssl.keyStorePassword.client");
		
		if (sslServerStore != null && ! sslServerStore.trim().isEmpty() 
				&& sslServerStorePassword != null && !sslServerStorePassword.trim().isEmpty())
		{
			/*
			 * Trust store is a keystore containing Certification Authority (CA)
			 * certificate(s) used to sign client and server certificates included
			 * in client and server keystores used by this xbrc and applications
			 * this xbrc communicates with over https.
			 */
			System.setProperty("javax.net.ssl.trustStore", sslServerStore);
			System.setProperty("javax.net.ssl.trustStorePassword", sslServerStorePassword);
			
			if (logger.isInfoEnabled())
				logger.info("Using SSL trustStore: " + sslServerStore);
		}
		else
		{
			if (logger.isInfoEnabled())
			{
				String trustStore = System.getProperty("javax.net.ssl.trustStore");
				if (trustStore == null)
					logger.warn("Server's keystore not provided. This will make requests over https fail.");
				else
					logger.info("Using SSL trustStore: " + trustStore);
			}
				
		}
		
		if (sslClientKeystore != null && !sslClientKeystore.trim().isEmpty() 
				&& sslClientKeystorePassword != null && !sslClientKeystorePassword.trim().isEmpty())
		{		
			System.setProperty("javax.net.ssl.keyStore", sslClientKeystore);
			System.setProperty("javax.net.ssl.keyStorePassword", sslClientKeystorePassword);
			
			if (logger.isInfoEnabled())
				logger.info("Using SSL client keystore: " + sslClientKeystore);
		}
		else
		{
			String keystore = System.getProperty("javax.net.ssl.keyStore");
			String password = System.getProperty("javax.net.ssl.keyStorePassword");
			
			if (keystore == null || password == null)
			{
				logger.warn("Client keystore not provided. Client SSL authentication will fail.");
			}
		}
	}

	private SSLContext createSSLContext()
	{
		try
		{
			SSLContext context = SSLContext.getInstance("SSL");

			// The reference implementation only supports X.509 keys
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			
			KeyStore ks = KeyStore.getInstance("JKS");
			
			String keystoreLocation = System.getProperty("javax.net.ssl.trustStore");
			String keystorePass = System.getProperty("javax.net.ssl.trustStorePassword");
			
			FileInputStream fis = null;
		    try {
		        fis = new java.io.FileInputStream(InputValidator.validateFilePath(keystoreLocation));
		        ks.load(fis, keystorePass.toCharArray());
		    } finally {
		        if (fis != null) {
                    try {
		                fis.close();
                    }
                    catch(Exception ignore) {
                    }
		        }
		    }
			
			kmf.init(ks, keystorePass.toCharArray());
				
			context.init(kmf.getKeyManagers(), null, null);
			return context;
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error initializing SSL context", e));
			return null;
		}
	}

	/*
	 * Web server callback method
	 */
	public void handle(Request request, Response response)
	{		
        final String sPath = request.getPath().getPath();
		
        if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
			logger.trace("Handle: Processing http request: " + request.getPath() + " from " + request.getClientAddress().getAddress().getHostAddress());
		
        final boolean isFastResponseRequired =
            (sPath.compareTo("/ControllerServer/stream") == 0) ||
		    (sPath.compareTo("/stream") == 0) ? true : false;

		try
		{
            if(!isFastResponseRequired) {
                if(!interceptRequest(request, response)) {
                    return;
                }
            }

			if (initialized)
			{
				if (request.getMethod().compareTo("GET") == 0)
					handleGet(request, response);
				else if (request.getMethod().compareTo("PUT") == 0)
					handlePut(request, response);
				else if (request.getMethod().compareTo("DELETE") == 0)
					handleDelete(request, response);
				else if (request.getMethod().compareTo("POST") == 0)
					handlePost(request, response);
			}
			else
			{
				if (request.getMethod().compareTo("GET") == 0)
					handleGetWithoutDatabase(request, response);
				else
		        	ResponseFormatter.return404(response);
			}

            if(!isFastResponseRequired) {
                interceptResponse(request, response, null);
            }
		}
		catch (Exception e)
		{
			logger.error("Error handling HTTP request: " + e);

            if(!isFastResponseRequired) {
                interceptResponse(request, response, e.toString());
            }
		}
	}

	/*
	 * Request parsers
	 */

	private void handleGet(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();
		
		// ekg is super frequent, it polutes the logs
		if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
			logger.trace("Processing http GET request: " + request.getPath() + " from " + request.getClientAddress().getAddress().getHostAddress());

		// redirect any UI stuff
		if (sPath.startsWith("/heartbeat"))
			handleHeartbeat(request, response, sPath);
		else if (sPath.startsWith("/UI"))
			handleGetUI(request, response, sPath);
		else if (sPath.startsWith("/messages"))
			handleGetMessages(request, response, sPath);
		else if (sPath.compareTo("/status") == 0)
			handleGetStatus(request, response);
		else if (sPath.startsWith("/properties"))
			handleGetProperties(request, response);
		else if (sPath.startsWith("/bandevents/band/"))
			handleGetBandEvents(request, response, sPath);
		else if (sPath.compareTo("/configurations") == 0)
			handleGetConfigurations(request, response);
		else if (sPath.startsWith("/configuration"))
			handleGetConfiguration(request, response, sPath);
		else if (sPath.compareTo("/currentconfiguration") == 0)
			handleGetCurrentConfiguration(request, response);
		else if (sPath.startsWith("/readerlocationinfo"))
			handleGetReaderLocationInfo(request, response, sPath);
		else if (sPath.startsWith("/gueststatus") || sPath.startsWith("/state"))
			handleGetState(request, response, sPath);
		else if (sPath.compareTo("/ekg") == 0)
			handleGetEKG(request, response);
		else if (sPath.compareTo("/ekgposition") == 0)
			handleGetEKGPosition(request, response);
		else if (sPath.equals("/www/metro/gueststatus"))
			handleGetState(request, response, sPath);
		else if (sPath.startsWith("/www") || sPath.compareTo("/") == 0)
			handleGetFile(request, response);
		else if (sPath.startsWith("/refreshpackages"))
			handleRefreshPackages(request, response);
		else if (sPath.compareTo("/perfmetricsmetadata") == 0)
			handlePerfMetricsMetadata(request, response);
        else if (sPath.compareTo("/sequences") == 0)
            handleGetSequences(request, response, true);
        else if (sPath.compareTo("/sequencesnocolors") == 0)
            handleGetSequences(request, response, false);
        else if (sPath.startsWith("/model"))
			handleModelRequest(request, response, sPath);
        else if (sPath.startsWith("/scheduleoverrideclear"))
        	handlePowerOverrideClear(request, response, sPath);
        else if (sPath.startsWith("/transmitreaderstatus"))
			handleGetTransmitReaderStatus(request, response);
        else if (sPath.startsWith("/getauditevents"))
			handleGetAuditEvents(request, response, sPath);
        else
        	ResponseFormatter.return404(response);
	}

	private void handleGetWithoutDatabase(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();
		
		// ekg is super frequent, it pollutes the logs
		if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
			logger.trace("Processing http GET request: " + request.getPath() + " from " + request.getClientAddress().getAddress().getHostAddress());

		if (sPath.compareTo("/status") == 0)
			handleGetStatusWithoutDatabase(request, response);
        else
        	ResponseFormatter.return404(response);
	}

	
	private void handlePut(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();
		
		logger.trace("Processing http PUT request: " + request.getPath() + " from " + request.getClientAddress().getAddress().getHostAddress());
		
		if (sPath.compareTo("/ControllerServer/hello") == 0)
			handleReaderPutHello(request, response);
		else if (sPath.compareTo("/hello") == 0)
			handleReaderPutHello(request, response);
		else if (sPath.compareTo("/ControllerServer/stream") == 0)
			handleReaderPutUpdateStream(request, response);
		else if (sPath.compareTo("/stream") == 0)
			handleReaderPutUpdateStream(request, response);
		else if (sPath.compareTo("/ControllerServer/guestpositions") == 0)
			handlePutGuestPositions(request, response);
		else if (sPath.compareTo("/guestpositions") == 0)
			handlePutGuestPositions(request, response);
		else if (sPath.compareTo("/updatestream") == 0)
			handleClientPutUpdateStream(request, response);
		else if (sPath.compareTo("/updateconfig") == 0)
			handlePutUpdateConfig(request, response);
		else if (sPath.startsWith("/properties"))
			handlePutProperties(request, response);
		else if (sPath.compareTo("/storeconfiguration") == 0)
			handlePutConfigurationStore(request, response);
		else if (sPath.startsWith("/selectconfiguration"))
			handlePutConfigurationSelect(request, response, sPath);
		else if (sPath.compareTo("/logcomment") == 0)
			handlePutLogComment(request, response);
		else if (sPath.compareTo("/configuration")==0)
			handlePutConfiguration(request, response);
		else if (sPath.startsWith("/calibrate"))
			handlePutCalibrate(request, response, sPath);
		else if (sPath.startsWith("/avmsevent") || sPath.startsWith("/videvent"))
			handlePutAvmsEvent(request, response, sPath);
		else if (sPath.startsWith("/avmshello") || sPath.startsWith("/vidhello"))
			handlePutAvmsHello(request, response, sPath);
        else if (sPath.startsWith("/mediapackage"))
            handlePutMediaPackage(request, response);
        else if (sPath.startsWith("/playsequence"))
            handlePutPlaySequence(request, response, sPath);
        else if (sPath.startsWith("/model"))
			handleModelRequest(request, response, sPath);
        else if (sPath.startsWith("/scheduleoverrideon"))
        	handlePowerOverrideOn(request, response, sPath);
        else if (sPath.startsWith("/replacereader"))
        	handlePutReplaceReader(request, response, sPath);
        else if (sPath.startsWith("/scheduler/message"))
        	handlePutSchedulerMessage(request, response);
        else if (sPath.startsWith("/updatereadersignalconfig"))
        	handlePutUpdateReaderSignalConfig(request, response);
        else
        	ResponseFormatter.return404(response);
	}

    private void handlePost(Request request, Response response)
	{
		String sPath = request.getPath().getPath();
		
		logger.trace("Processing http POST request: " + request.getPath() + " from " + request.getClientAddress().getAddress().getHostAddress());
		
		if (sPath.startsWith("/model"))
			handleModelRequest(request, response, sPath);
		else if (sPath.startsWith("/newconfiguration"))
			handlePostNewConfiguration(request, response);
		else if (sPath.startsWith("/configuration"))
			handlePostNewConfiguration(request, response);
		else if (sPath.startsWith("/avmsevent") || sPath.startsWith("/videvent"))
			handlePutAvmsEvent(request, response, sPath);
		else
			ResponseFormatter.return404(response);
	}

	private void handleDelete(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();
		
		logger.trace("Processing http DELETE request: " + request.getPath() + " from " + request.getClientAddress().getAddress().getHostAddress());
		
		if (sPath.compareTo("/updatestream") == 0)
			handleDeleteUpdateStream(request, response);
		else if (sPath.startsWith("/configuration"))
			handleDeleteConfiguration(request, response, sPath);
		else if (sPath.compareTo("/messages") == 0)
			handleDeleteMessages(request, response);
		else if (sPath.startsWith("/cache"))
			handleDeleteCache(request, response, sPath);
		else if (sPath.compareTo("/gueststatus")==0)
			handleDeleteGuestStatus(request, response);
        else if (sPath.startsWith("/mediapackage"))
            handleDeleteMediaPackage(request, response);
        else if (sPath.startsWith("/model"))
			handleModelRequest(request, response, sPath);
        else if (sPath.startsWith("/deleteauditevents"))
			handleDeleteAuditEvents(request, response, sPath);
        else
        	ResponseFormatter.return404(response);
	}
	
	private MessagePayloadFormat acceptedMessagePayloadFormat(Request request)
	{
		String accept = request.getValue("accept");
		
		// default to xml
		if (accept == null || accept.isEmpty())
			return MessagePayloadFormat.xml;
		
		if (accept.equals(MessagePayloadFormat.json.getContentType()))
			return MessagePayloadFormat.json;
		
		return MessagePayloadFormat.xml;
	}

	/*
	 * GET handlers
	 */
	
	/*
	 * The hartbeat is just a simple, super fast response that can be called frequently to determine 
	 * if the xBRC is still listening on the web server port.
	 */
	public void handleHeartbeat(Request request, Response response, String sPath)
	{
		MessagePayloadFormat format = acceptedMessagePayloadFormat(request);
		
		PrintStream body = null;

		try
		{			
			XbrcHeartbeat heartbeat = new XbrcHeartbeat();
			heartbeat.setMainThreadLoopCount(Processor.INSTANCE.getMainThreadLoopCount().get());
			heartbeat.setLastProcessingDuration(Processor.INSTANCE.getLastProcessingDuration().get());
			
			// serialize
			String strBody;
			if (format == MessagePayloadFormat.json)
				strBody = JsonUtil.convertToJson(heartbeat);
			else
				strBody = XmlUtil.convertToXml(heartbeat, XbrcHeartbeat.class);

	        body = response.getPrintStream();
			body.println(strBody);
			ResponseFormatter.setResponseHeader(response, format.getContentType());
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET hartbeat", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET hartbeat", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	/*
     * Pull audit events from the local cache.
	 */
	public void handleGetAuditEvents(Request request, Response response, String sPath)
	{
		MessagePayloadFormat format = acceptedMessagePayloadFormat(request);

		PrintStream body = null;

		try
		{
            String prefix = "/getauditevents";
            String id = sPath.substring(prefix.length());

            if(id.startsWith("/")) {
                id = id.substring(1).trim();
            }

            IAuditEventsProvider provider = Auditor.getInstance().getEventsProvider();
            AuditEventList list = new AuditEventList();

            if(provider.isAuditEnabled()) {
                List<AuditEvent> events = null;

                if(id.length() > 0) {
                    events = provider.getEvents(Long.parseLong(id));
                }
                else {
                    events = provider.getAllEvents();
                }

                list.setEvents(events);
            }

			// serialize
			String strBody;
			if (format == MessagePayloadFormat.json)
				strBody = JsonUtil.convertToJson(list);
			else
				strBody = XmlUtil.convertToXml(list, AuditEventList.class);

	        body = response.getPrintStream();
			body.println(strBody);
			ResponseFormatter.setResponseHeader(response, format.getContentType());
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET getauditevents", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET getauditevents", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	/*
	 * The hartbeat is just a simple, super fast response that can be called frequently to determine 
	 * if the xBRC is still listening on the web server port.
	 */
	public void handleGetTransmitReaderStatus(Request request, Response response)
	{
		MessagePayloadFormat format = acceptedMessagePayloadFormat(request);
		
		PrintStream body = null;

		try
		{			
			TransmitReaderStatus status = TransmitManager.INSTANCE.getTransmitReaderStatus();

			status.setName(ConfigOptions.INSTANCE.getControllerInfo().getName());
			status.setTime(XbrcDateFormatter.formatTime(new Date().getTime()));
			status.setFacilityId(ConfigOptions.INSTANCE.getControllerInfo().getVenue());	
			
			// serialize
			String strBody;
			if (format == MessagePayloadFormat.json)
				strBody = JsonUtil.convertToJson(status);
			else
				strBody = XmlUtil.convertToXml(status, TransmitReaderStatus.class);

	        body = response.getPrintStream();
			body.println(strBody);
			ResponseFormatter.setResponseHeader(response, format.getContentType());
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET transmitreaderstatus", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET transmitreaderstatus", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	public void handleGetUI(Request request, Response response, String sPath)
	{
		// if a vip address has been configured, use that, else try to determine our
		// own IP address
		
		String ownIP = null;
		
		String vipAddress = ConfigOptions.INSTANCE.getControllerInfo().getVipAddress();
		if (vipAddress!=null && vipAddress.length()>0 && !vipAddress.startsWith("#"))
		{
			ownIP = vipAddress;
		}
		else
		{
			// try to return an address on the same network as the requestor
			String sClientIP = request.getClientAddress().getAddress().getHostAddress();

			String ipPrefix = "";
			if (sClientIP.compareTo("127.0.0.1") != 0)
			{
				String[] ips = sClientIP.split("\\.");
				if (ips.length == 4)
					ipPrefix = ips[0] + "." + ips[1];
			}

			Collection<String> lIPs = null;
			try
			{
				lIPs = NetInterface.getOwnIpAddress(ipPrefix);
			}
			catch (SocketException e)
			{
				logger.error(ExceptionFormatter.format("Unable to get our own ip address", e));
			}
			
			if (lIPs!=null && lIPs.size()>0)
				ownIP = lIPs.iterator().next();
		}
		
		if (ownIP==null)
		{
			logger.error("Failed to get IP address with necessary prefix");
			ResponseFormatter.return404(response);
		}
		else
		{
			ResponseFormatter.return301(response, "http://" + ownIP + ":8090"
					+ sPath);
		}

	}

	private void handleGetCurrentConfiguration(Request request,
			Response response)
	{
        PrintStream body = null;

		try
		{
			body = response.getPrintStream();
			body.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
			body.println(serializeCurrentConfiguration("current"));
			ResponseFormatter.setResponseHeader(response, "application/xml");
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET currentconfiguration", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET currentconfiguration", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}
	
	private void handleGetConfiguration(Request request, Response response,
			String sPath)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        PrintStream body = null;

		try
		{
			String sNamePrefix = "/configuration/name/";
			String sIdPrefix = "/configuration/id/";
			String sName = null;
			int id = Integer.MIN_VALUE;
			
			if (sPath.startsWith(sNamePrefix))
			{
				sName = sPath.substring(sNamePrefix.length());
			}
			else if (sPath.startsWith(sIdPrefix))
			{
				String sIndex = sPath.substring(sIdPrefix.length());
				id = Integer.parseInt(sIndex);
				if (id<0)
				{
					ResponseFormatter.return404(response);
					return;
				}
			}
			else
			{
				ResponseFormatter.return404(response);
				return;
			}
			
			conn = Controller.getInstance().getPooledConnection();
			String storedConfigXml = null;
			
			// if asking for "current", then just return the current data
			if (sName!=null)
			{
				if (sName.equals("current"))
				{
					handleGetCurrentConfiguration(request, response);
					return;
				}
				storedConfigXml = StoredConfigService.getAsXml(conn, sName, false);
			}
			else
			{
				storedConfigXml = StoredConfigService.getAsXml(conn, id, false);
			}
			
			if (storedConfigXml == null)
			{
				ResponseFormatter.return404(response);
				return;
			}
			
			// format response
			body = response.getPrintStream();
			body.println(storedConfigXml);
			ResponseFormatter.setResponseHeader(response, "application/xml");
			ResponseFormatter.return200(response);
		}
		catch(Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET currentconfiguration", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET currentconfiguration", e));
		}
		finally
		{
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }

			if (rs != null) {
                try {
                    rs.close();
                }
                catch(Exception ignore) {
                }
            }

			if (pstmt != null) {
                try {
                    pstmt.close();
                }
                catch(Exception ignore) {
                }
            }

			if (conn != null) {
				Controller.getInstance().releasePooledConnection(conn);
            }
		}
	}

	private void handleGetReaderLocationInfo(Request request,
			Response response, String sPath)
	{
        PrintStream body = null;

		try
		{
			// all or just 1?
			String sNamePrefix = "/readerlocationinfo/name/";
			String sIndexPrefix = "/readerlocationinfo/index/";
			LocationInfo linfo = null;
			if (sPath.startsWith(sNamePrefix))
			{
				String sLocation = sPath.substring(sNamePrefix.length());
				for (LocationInfo li : ConfigOptions.INSTANCE.getLocationInfo()
						.values())
				{
					if (li.getName().compareTo(sLocation) == 0)
					{
						linfo = li;
						break;
					}
				}

				if (linfo == null)
				{
					ResponseFormatter.return404(response);
					return;
				}

			}
			else if (sPath.startsWith(sIndexPrefix))
			{
				String sIndex = sPath.substring(sIndexPrefix.length());
				int iLocation = -1;
				iLocation = Integer.parseInt(sIndex);
				if (iLocation >= 0)
					linfo = ConfigOptions.INSTANCE.getLocationInfo().get(
							iLocation);
				if (linfo == null)
				{
					ResponseFormatter.return404(response);
					return;
				}
			}
			else if (sPath.compareTo("/readerlocationinfo") != 0)
			{
				ResponseFormatter.return404(response);
				return;
			}
			
			MessagePayloadFormat format = acceptedMessagePayloadFormat(request);

			ReaderLocationInfo info = new ReaderLocationInfo();
			info.setName(ConfigOptions.INSTANCE.getControllerInfo().getVenue());
			info.setTime(XbrcDateFormatter.formatTime(new Date().getTime()));

			String xml = null;

			List<LocationInfo> locInfoList = new LinkedList<LocationInfo>();

			synchronized (ConfigOptions.INSTANCE.getReadersInfo())
			{
				if (linfo != null)
				{
					linfo.setReaders(ConfigOptions.INSTANCE.getReadersInfo().getReaders(linfo.getName()));
					Processor.INSTANCE.updateReaderStatus(linfo.getReaders());
					locInfoList.add(linfo);
				}
				else
				{
					for (LocationInfo lif : ConfigOptions.INSTANCE
							.getLocationInfo().values())
					{
						lif.setReaders(ConfigOptions.INSTANCE.getReadersInfo()
								.getReaders(lif.getName()));
						Processor.INSTANCE.updateReaderStatus(lif.getReaders());
						locInfoList.add(lif);
					}
				}

				info.setReaderlocationinfo(locInfoList);				
				
				// serialize
				if (format == MessagePayloadFormat.json)
					xml = JsonUtil.convertToJson(info);
				else
					xml = XmlUtil.convertToXml(info, ReaderLocationInfo.class);
			}

            body = response.getPrintStream();
			body.println(xml);
			ResponseFormatter.setResponseHeader(response, format.getContentType());
			ResponseFormatter.return200(response);
		}
		catch (JAXBException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET readerlocationinfo", e));
			ResponseFormatter.return404(response);
		}
		catch (Exception e2)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET readerlocationinfo", e2));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET readerlocationinfo", e2));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	

	private void handleGetMessages(Request request, Response response,
			String sPath)
	{
        PrintStream body = null;

		try
		{
			// get all or just for one guest?
			String sGuestId = null;
			String sPrefix = "/messages/guestid/";
			if (sPath.startsWith(sPrefix))
			{
				sGuestId = sPath.substring(sPrefix.length());
				if (sGuestId.isEmpty())
				{
					ResponseFormatter.return404(response);
					return;
				}
			}
			else if (sPath.compareTo("/messages") != 0)
			{
				ResponseFormatter.return404(response);
				return;
			}

			// parse additional parameters
			String sAfter = request.getParameter("after");
			String sMax = request.getParameter("max");
			long lAfter = -1;
			if (sAfter != null)
			{
				try
				{
					lAfter = Integer.parseInt(sAfter);
				}
				catch (Exception ex)
				{
				}
			}
			int nMax = -1;
			if (sMax != null)
			{
				try
				{
					nMax = Integer.parseInt(sMax);
				}
				catch (Exception ex)
				{
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
			sb.append(formatGuestMessages(sGuestId, lAfter, nMax));
			body = response.getPrintStream();
			body.println(sb.toString());
			ResponseFormatter.setResponseHeader(response, "application/xml");
			ResponseFormatter.return200(response);
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET messages", e));
			ResponseFormatter.return500(response,
					ExceptionFormatter.format("Error handling GET messages", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private void handleGetStatus(Request request, Response response)
	{
        PrintStream body = null;

		try
		{
			XbrcStatus status = Processor.INSTANCE.getStatus();
			String xml = XmlUtil.convertToXml(status, XbrcStatus.class);

			body = response.getPrintStream();
			body.println(xml);
			ResponseFormatter.setResponseHeader(response, "application/xml");
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format("Error handling GET status",
					e));
			ResponseFormatter.return500(response,
					ExceptionFormatter.format("Error handling GET status", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}
	
	private void handleGetStatusWithoutDatabase(Request request, Response response)
	{
        PrintStream body = null;

		try
		{
			XbrcStatus status = new XbrcStatus();
			status.setStatus(StatusType.Red);
			status.setStatusMessage("Not connected to database");
			
			String xml = XmlUtil.convertToXml(status, XbrcStatus.class);

			body = response.getPrintStream();
			body.println(xml);
			ResponseFormatter.setResponseHeader(response, "application/xml");
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format("Error handling GET status",
					e));
			ResponseFormatter.return500(response,
					ExceptionFormatter.format("Error handling GET status", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}
//	private void handleGetProperties(Request request, Response response)
//	{
//		String xml = null;
//		Connection conn = null;
//
//		try
//		{
//
//			conn = Controller.getInstance().getPooledConnection();
//
//			XbrcConfig config = new XbrcConfig();
//
//			// configuration common to all models
//			initControllerInfoConfig(config, conn);
//
//			// delegate model specific part of this request to the model
//			if (Processor.INSTANCE.getModel() != null)
//				Processor.INSTANCE.getModel().handleGetProperties(
//						request.getPath().toString(), "application/xml",
//						config, conn);
//
//			// serialize
//			xml = XmlUtil.convertToXml(config, XbrcConfig.class);
//
//			PrintStream body = response.getPrintStream();
//			body.println(xml);
//			body.close();
//			setResponseHeader(response, "application/xml");
//			return200(response);
//
//		}
//		catch (Exception e)
//		{
//			logger.error(ExceptionFormatter.format(
//					"Error handling GET properties", e));
//			return500(response, ExceptionFormatter.format(
//					"Error handling GET properties", e));
//		}
//		finally
//		{
//			Controller.getInstance().releasePooledConnection(conn);
//		}
//	}
	
	private void handleGetProperties(Request request, Response response)
	{
		propertiesManager.handleGetProperties(request, response);
	}

	

	private void handleGetState(Request request, Response response,
			String sPath)
	{
        PrintStream body = null;

		try
		{			
			// TODO: CHange this code to work through model
			
			// get all or just for one guest?
			String sGuestId = null;
			int iGuestId = sPath.indexOf("guestid/");
			if (iGuestId > 0)
			{
				sGuestId = sPath.substring(iGuestId + "guestid/".length());
				if (sGuestId.isEmpty())
				{
					ResponseFormatter.return404(response);
					return;
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
			String sXMLBody = formatGuestStatus(sGuestId);
			if (sXMLBody!=null)
			{
				sb.append(formatGuestStatus(sGuestId));
				body = response.getPrintStream();
				body.println(sb.toString());
				ResponseFormatter.setResponseHeader(response, "application/xml");
				ResponseFormatter.return200(response);
			}
			else
			{
				logger.error("Error formatting gueststatus");
				ResponseFormatter.return500(response, "Error formatting gueststatus");
			}
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET gueststatus", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET gueststatus", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private void handleGetBandEvents(Request request, Response response,
			String sPath)
	{
        PrintStream body = null;

		try
		{
			// parse out the bandId
			String sID = sPath.substring("/bandevents/band/".length());

			body = response.getPrintStream();

			sID = sID.toLowerCase();
			
			logger.debug("Getting events for band id: " + sID);

			// get the events for that band
			WatchedBandInfo wbi = Processor.INSTANCE.getWatchedBandInfo(sID);

			if (wbi != null)
			{
				// serialize events

				LrrEventsByBand events = new LrrEventsByBand();
				events.setVenue(ConfigOptions.INSTANCE.getControllerInfo().getVenue());
				events.setTimestamp(XbrcDateFormatter.formatTime(new Date().getTime()));

				synchronized (wbi)
				{
					events.setEvents(wbi.getEvents());
					logger.debug("Serializing " + events.getEvents().size()
							+ " events for band id: " + sID);
					body.println(XmlUtil.convertToXml(events,
							LrrEventsByBand.class));
				}
			}
			else
			{
				// add the band to the watched band list
				logger.debug("Adding to the watched band list - band id: "
						+ sID);

				Processor.INSTANCE.addWatchedBand(sID);
			}

			ResponseFormatter.setResponseHeader(response, "application/xml");
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET bandevents", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET bandevents", e));
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private void handleGetEKGPosition(Request request, Response response)
	{
        PrintStream body = null;

		try
		{
			long lPos = Log.INSTANCE.getEKGPosition();

			// write the data to the response
			response.setCode(200);
			ResponseFormatter.setResponseHeader(response, "text/plain");
			body = response.getPrintStream();
			body.println(Long.toString(lPos));
		}
		catch (Exception e)
		{
			logger.error("Error handling GET ekgposition: " + e);
			ResponseFormatter.return500(response, e.getStackTrace().toString());
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private void handleGetEKG(Request request, Response response)
	{
        PrintStream body = null;

		try
		{
			// is there a parameter
			int cLinesMax = 100;
			String sLinesMax = request.getParameter("max");
			if (sLinesMax != null)
				cLinesMax = Integer.parseInt(sLinesMax);
			long lPosition = 0;
			String sPosition = request.getParameter("position");
			if (sPosition != null)
				lPosition = Long.parseLong(sPosition);

			// get EGK data
			String sLines = Log.INSTANCE.readEKGFromPosition(lPosition,
					cLinesMax);

			// write the data to the response
			response.setCode(200);
			ResponseFormatter.setResponseHeader(response, "text/plain");
			body = response.getPrintStream();
			body.println(sLines);
		}
		catch (IOException e)
		{
			logger.error("Error handling GET ekg: " + e);
			ResponseFormatter.return500(response, e.getStackTrace().toString());
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private void initializePackageManagers()
	{
		// read installer packages from the file system
		String sReposDir = InputValidator.validateDirectoryName(ConfigOptions.INSTANCE.getControllerInfo()
				.getWwwdir()) + "/repos";

		reposManager.refereshPackages(sReposDir);		

        String sMediaDir = InputValidator.validateDirectoryName(ConfigOptions.INSTANCE.getControllerInfo()
                .getWwwdir()) + "/media";

        mediaManager.setPath(sMediaDir);
        mediaManager.refreshPackages();
    }

	private void handleRefreshPackages(Request request, Response response)
	{		
		initializePackageManagers();

        PrintStream body = null;

		try
		{
			response.setCode(200);
			body = response.getPrintStream();
			body.append("<html><body><h2>The packages have been refreshed.</h2></body></html>\n");
		}
		catch(IOException e)
		{
			logger.warn("Failed to produce HTML response to refreshpackages", e);
			ResponseFormatter.return500(response, e.getStackTrace().toString());
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private void handleGetFile(Request request, Response response)
	{
		String sPath = null;
        InputStream is = null;
        PrintStream body = null;

        try
		{
			String sReqPath = request.getPath().getPath();
			if (sReqPath.compareTo("/") == 0)
				sReqPath = "/www";

			File baseDir = new File(InputValidator.validateDirectoryName(ConfigOptions.INSTANCE.getControllerInfo().getWwwdir()));

			sPath = baseDir.getCanonicalPath() + File.separator
					+ sReqPath.substring("/www".length());
			File file = new File(sPath);

			if (!file.exists())
				ResponseFormatter.return404(response);

			// For security reasons all files must be under the wwwdir folder
			if (!file.getCanonicalPath().startsWith(baseDir.getCanonicalPath()))
				ResponseFormatter.return404(response);

			// This will remove any duplicate "//" and collapse any ".." in the
			// path
			sReqPath = "/www"
					+ file.getCanonicalPath().substring(
							baseDir.getCanonicalPath().length());

			body = response.getPrintStream();

			if (file.isDirectory())
			{
				response.setCode(200);
				ResponseFormatter.setResponseHeader(response, "text/html");

				// serve a directory listing..
				body.append("<html><body>\n");
				body.append("<h2>XBRC file store directory listing of "
						+ request.getPath().getPath() + "</h2>\n");
				body.append("<table border='0' style='margin: 10px;'>\n");
				if (file.getParent().startsWith(baseDir.getCanonicalPath()))
					body.append("<tr><td><a href='" + sReqPath
							+ "/..'>..</a></td><td></td></tr>\n");
				for (File f : file.listFiles())
				{
					body.append("<tr><td><a href='"
							+ sReqPath
							+ "/"
							+ f.getName()
							+ "'>"
							+ f.getName()
							+ "</a></td><td>"
							+ (f.isDirectory() ? "" : FileUtils
									.formatFileSize(f.length()))
							+ "</td></tr>\n");
				}
				body.append("</table>\n");
				body.append("</body></html>\n");

				return;
			}

			// read the file and write to the response
			response.setCode(200);
			
			if ( file.getName().endsWith(".html"))
				ResponseFormatter.setResponseHeader(response, "text/html");
			else
				ResponseFormatter.setResponseHeader(response, "application/octetstream");

			is = new FileInputStream(file);
			byte[] bytes = new byte[4096];
			int offset = 0;
			int numRead = is.read(bytes, 0, bytes.length);
			while (offset < file.length() && numRead >= 0)
			{
				body.write(bytes, 0, numRead);
				offset += numRead;
				numRead = is.read(bytes, 0, bytes.length);
			}
		}
		catch (IOException e)
		{
			logger.error("Error handling GET file " + sPath, e);
			ResponseFormatter.return500(response, e.getStackTrace().toString());
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(Exception ignore) {
                }
            }

            if(body != null) {
                try {
                   body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private void handleGetConfigurations(Request request, Response response)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
        PrintStream body = null;

		try
		{
			body = response.getPrintStream();
			StringBuilder sb = new StringBuilder();

			sb.append("{\n");
			sb.append("    \"configurations\" : \n");
			sb.append("    [\n");
			conn = Controller.getInstance().getPooledConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT id, name, description FROM StoredConfigurations");
			while (rs.next())
			{
				sb.append("        {\n");
				sb.append("            \"configurationId\" : "
						+ rs.getInt("id") + ",\n");
				sb.append("            \"name\" : \"" + rs.getString("name")
						+ "\",\n");
				sb.append("            \"description\" : \""
						+ rs.getString("description") + "\"\n");
				if (rs.isLast())
					sb.append("        }\n");
				else
					sb.append("        },\n");
			}
			sb.append("    ]\n");
			sb.append("}");

			// write the data to the response
			ResponseFormatter.setResponseHeader(response, "application/json");
			response.setCode(200);
			body.println(sb.toString());
		}
		catch (Exception e)
		{
			logger.error("Error handling GET configurations: " + e);
			ResponseFormatter.return500(response, e.getLocalizedMessage());
		}
		finally
		{

            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }

			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					Controller.getInstance().releasePooledConnection(conn);
			}
			catch (Exception ex)
			{
			}
		}
	}
	
	private void startProcessingEvents( String dipIpAddress )
	{
		// if getting messages then must be master
		HAStatusEnum haThis = HAStatusEnum.getStatus(Processor.INSTANCE.getHaStatus());
		if (haThis == HAStatusEnum.unknown || haThis == HAStatusEnum.slave)
		{
			if (ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
			{
				logger.info("Changing HA status to master. Event from reader (DIP): " + dipIpAddress);
				Processor.INSTANCE.setHaStatus(HAStatusEnum.master.toString());
			}
			else
			{
				logger.info("Changing HA status to solo");
				Processor.INSTANCE.setHaStatus(HAStatusEnum.solo.toString());
			}
			
			// Let the slaves know who the new master is.
			JMSAgent.INSTANCE.forceSendingHAChangeDiscoveryMessage();
		}
	}

	/*
	 * PUT handlers
	 */

	private void handleReaderPutHello(Request request, Response response)
	{
        InputStream is = null;        
        
        try
		{
        	Date now = new Date();
			is = request.getInputStream();
			String sPutData = new Scanner(is).useDelimiter("\\A").next();
			
			// if ha is enabled, reject any data that's missing the BigIP provided X-Forwarded-For header
			String sForwardedFor = request.getValue("X-Forwarded-For");
			if (sForwardedFor==null && ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
			{
				// eek!
				logger.error("HA is enabled but reader appears to be configured with DIP instead of VIP address. Hello message:\n" + sPutData);
				ResponseFormatter.return500(response, "HA is enabled but reader appears to be configured with DIP instead of VIP address.");
				return;
			}

			startProcessingEvents(request.getClientAddress().getAddress().getHostAddress());
			
			// if have header, use that address
			String ipAddress = null;
			if (sForwardedFor!=null)
			{
				// trim
				int iPos = sForwardedFor.indexOf(',');
				if (iPos>=0)
					sForwardedFor.substring(0, iPos);
				ipAddress = sForwardedFor;
				logger.debug("Got reader hello with X-Forwarded-For set to " + ipAddress);
			}
			else
			{
				// get and digest the incoming ip address
				ipAddress = request.getClientAddress().getAddress().getHostAddress();
			}

			logger.debug("got HELLO from: " + ipAddress);
			logger.trace(sPutData);

			HelloMsg hello = XbrJsonMapper.mapHello(sPutData);

			boolean useSecureId = ConfigOptions.INSTANCE.getControllerInfo().isSecureTapId();

			// Do we know this reader?
			logger.debug("Looking for reader in database");
			ReaderInfo r = findReader(hello.getMac(), hello.getPort(), hello.isSimulated(), hello.getReaderName());
			if (r != null)
			{
				logger.debug("Looking for reader in database: Found");

				// We must protect changes to the ReaderInfo object here.
				// Other threads read the ReaderInfo object. For example the
				// "/status" web call.
				synchronized (r)
				{
					// set the simulated flag
					r.setSimulated(hello.isSimulated());

					// if it has the wrong reader name, update it
					if (r.getName().compareTo(hello.getReaderName()) != 0 && hello.isListens())
					{
						sendSetReaderName(r);
					}

					// compare port
					if (!hello.getPort().equals(r.getPort()))
					{
						r.setPort(hello.getPort());
						updateReaderPort(r);
					}

					// compare ip addresses
					if (ipAddress.compareTo(r.getIpAddress()) != 0)
					{
						// update reader table
						r.setIpAddress(ipAddress);
						updateReaderIpAddress(r);
					}

					// compare the reader version
					if (r.getVersion() == null
							|| r.getMinXbrcVersion() == null
							|| hello.getReaderVersion().compareTo(r.getVersion()) != 0
							|| hello.getMinXbrcVersion().compareTo(r.getMinXbrcVersion()) != 0)
					{
						r.setVersion(hello.getReaderVersion());
						r.setMinXbrcVersion(hello.getMinXbrcVersion());
						updateReaderVersion(r);
					}

					if (r.getType() != hello.getType())
					{
						r.setType(hello.getType());
					}
					
					if (r.getHardwareType() == null || !r.getHardwareType().equals(hello.getHardwareType()))
					{
						r.setHardwareType(hello.getHardwareType());
						updateReaderHardwareType(r);
					}
					

                    if (r.getMediaPackageHash() == null || !r.getMediaPackageHash().equals(hello.getMediaHash()))
                    {
                        r.setMediaPackageHash(hello.getMediaHash());
                    }

					/*
					 * Global configuration setting is used to determine if a secure id
					 * should be used, unless overwritten by a location's setting.
					 */
					if (r.getLocation() != null)
					{
						if (r.getLocation().isUseSecureId() != null)
							useSecureId = r.getLocation().isUseSecureId();
						r.setUseSecureId(useSecureId);
					}
					
					r.setHardwareType(hello.getHardwareType());
					
					// patch the location name, if needed
					if (hello.getLocationId()!=-1)
					{
						if (hello.getLocationId()<0 || hello.getLocationId()>=ConfigOptions.INSTANCE.getLocationInfo().size())
						{
							logger.error("Invalid location id: " + hello.getLocationId() + " in reader HELLO message");
						}
						else
						{
							LocationInfo li = ConfigOptions.INSTANCE.getLocationInfo().get(hello.getLocationId());
							if (!r.getLocation().getName().equals(li.getName()))
								r.setLocation(li);
							
							/*
							 * Global configuration setting is used to determine if a secure id
							 * should be used, unless overwritten by a location's setting.
							 */
							if (li.isUseSecureId() != null)
								useSecureId = li.isUseSecureId();
							r.setUseSecureId(useSecureId);
						}
					}

					// update the hello time
					r.setTimeLastHello(new Date().getTime());
					updateReaderTimeLastHello(r);
				}
			}
			else
			{
				logger.debug("Looking for reader in database: Not found");
				
				// We must have a unique reader name because we use it as a key to a hash map.
				if (this.isReaderNameTaken(hello.getReaderName())) {
				    logger.debug("Setting added reader's name to mac address. mac=" + hello.getMac() + ";");
				    hello.setReaderName(hello.getMac());
                }

				// add entry to reader table
				r = new ReaderInfo(-1, hello.getReaderName(), hello.getType(), "", "", hello.getPort(), -90,
						1.1, hello.getMac(), ipAddress, -1, new Date().getTime(),
						hello.isSimulated(), 0, 0, 0, 0, hello.getReaderVersion(), hello.getMinXbrcVersion(), 
						false, "", useSecureId, hello.getHardwareType(), "", true, 3, 1, null, true, null);

				if (hello.isListens())
					sendSetReaderName(r);
				
				r.setMediaPackageHash(hello.getMediaHash());

				// patch the location name, if needed
				if (hello.getLocationId()!=-1)
				{
					// compare locations
					if (hello.getLocationId()<0 || !ConfigOptions.INSTANCE.getLocationInfo().containsKey(hello.getLocationId()))
					{
						logger.error("Invalid location id: " + hello.getLocationId() + " in reader HELLO message");
					}
					else
					{
						LocationInfo li = ConfigOptions.INSTANCE.getLocationInfo().get(hello.getLocationId());
						logger.debug("Adding reader and setting location. reader:" + r.getName() + ";locationId=" +
								hello.getLocationId() + ";locationName=" + li.getName() + ";");
                        r.setLocation(li);
						
						/*
						 * By default, the global configuration is used to determine if a secure id
						 * should be used, but locations can overwrite it.
						 */
						if (li.isUseSecureId() != null)
							useSecureId = li.isUseSecureId();
						r.setUseSecureId(useSecureId);
					}
				}
                else {
                    logger.warn("Reader to add has invalid location id. locationId=" + hello.getLocationId() + ";");
                }
				
				// store new reader in db
				addReader(r);
			}

			// Must return 200 before we do any posts otherwise we may block the
			// client who is
			// waiting on the PUT.
			ResponseFormatter.return200(response);

			/* 
			 * This code is now disabled since the hello message is getting completely discarded in this case.
			 *  
			 *
				// deal with readers that aren't communicating through the BigIP box
				// if ha is enabled, this is probably a bad thing!
				if (sForwardedFor==null && ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
				{
					logger.error("HA is enabled, but got reader hello without X-Forwarded-For header! Reader ip: " + ipAddress + ". Setting reader to inactive state.");
					r.setEnabled(false);
					r.setInvalidHeader(true);
				}
				else
				{
					// note that this also gets cleared if ha is not enabled
					r.setInvalidHeader(false);
				}
			*
			*/

			if (hello.isListens() && r.isEnabled())
			{				
				if (reposManager.hasUpgradeToVersion(r.getHardwareType()))
				{
					if (!checkReaderVersion(r, reposManager))
					{
						int retCode = upgradeReaderVersion(r);
						if (retCode == 200)
							return;	// bail out here, no sendTurnOnPostMode...
						
						if (retCode == 404)
							logger.warn("Received 404 from reader " + r.getName() + " while trying to upgrade reader.");
					}
				}

                // Determine if we need to upgrade the the media file on the reader.
                if ( !checkReaderMediaVersion(r, mediaManager))
                {
                    installMediaPackage(r);
                }
			}
			
			if (!r.isEnabled())
				logger.info("Received HELLO from reader " + r.getName() + " which is currently marked as disabled. Not sending update_stream to the reader.");
			
			boolean sendPowerSettings = !r.isSentHelloSettings() || hello.getUpdateStream() == null || hello.getUpdateStream().isEmpty();
			boolean firstTimeHello = hello.getUpdateStream() == null || hello.getUpdateStream().isEmpty();
			boolean sendHelloSettings = r.isEnabled() && 
					(!r.isSentHelloSettings() || firstTimeHello);
					
			// This flag will get set to false if any of the REST send commands fail
			r.setSentHelloSettings(true);
			
			// send a message to the reader to put it in post mode
			if (hello.isListens() && sendHelloSettings)
				sendTurnOnPostMode(r, hello);

			if (ReaderType.isTapReader(r.getType()) && hello.isListens() && sendHelloSettings)
			{
				// xfp specific options
				RfidOptionsMsg options = new RfidOptionsMsg();
				options.setTestLoop(ConfigOptions.INSTANCE.getControllerInfo()
						.isRFIDTestMode());
				options.setSecureId(r.isUseSecureId());
				if (!checkHttpRetCode(ReaderApi.setRfidOptions(r, options)))
					r.setSentHelloSettings(false);
				
				Controller.getInstance().enableTapSequence(r, Processor.INSTANCE.getModel().isTapSequenceEnabled(r));
				Controller.getInstance().setIdleSequence(r, Processor.INSTANCE.getModel().isIdleSequenceEnabled(r));

				if (r.getType() == ReaderType.xfpxbio)
				{
					// xfpxbio specific options
					BioOptionsMsg bioOptions = new BioOptionsMsg();
					bioOptions.setImageCapture(ConfigOptions.INSTANCE.getControllerInfo().isBioImageCapure());
					if (!checkHttpRetCode(ReaderApi.setBiometricOptions(r, bioOptions)))
						r.setSentHelloSettings(false);
				}
			} 
			else if (ReaderType.isLongRangeReader(r.getType()))
			{
				TransmitManager.INSTANCE.onReaderSaysHello(r, firstTimeHello);
			}

			// synchronize the reader time to ours
			if (ConfigOptions.INSTANCE.getControllerInfo().isSetReaderTime() && hello.isListens() && 
				(hello.getTime() == null || Math.abs(now.getTime() - hello.getTime().getTime()) > 
				ConfigOptions.INSTANCE.getControllerInfo().getReadertimeprecisionms()))
				if (!checkHttpRetCode(ReaderApi.setReaderTime(r)))
					r.setSentHelloSettings(false);


		    if ("xBR4".equals(r.getHardwareType()))
		    {
		    	if (sendPowerSettings)
				{
					try {
						ReaderApi.sendRadioPower(r);
					}
					catch (Exception ex) {
						r.setSentHelloSettings(false);
						logger.warn("Problem with sending radio power command to reader " + r.getName() + ":", ex);
					}
		    	}
		    

		    	if (r.isSentDiagnosticSettings())
		    	{
		    		long millisUntilOn = Controller.getInstance().shouldReaderBeOn();
		    		if (millisUntilOn > 60000 * 5) // Don't bother turning off for a short period of time
		    		{
			    		int minutesOff = ConfigOptions.INSTANCE.getControllerInfo().getReaderPowerOffPeriod();
			    		if (millisUntilOn / 1000 < minutesOff * 60) // If we need to start sooner than minutesOff
			    		{
			    			minutesOff = (int) (millisUntilOn / 60000);
			    		}
			    		
			    		try {
			    			ReaderApi.sendShutdownReader(r, minutesOff);
			    		}
			    		catch (Exception ex) {
			    			logger.warn("Problem with sending shutdown to reader " + r.getName() + ":", ex);
			    		}
		    		}
		    	}
			}
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling reader PUT hello", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling reader PUT hello", e));
		}
        finally 
        {
            try 
            {
                if ( is != null ) 
                {
                    is.close();
                }
            }
            catch (IOException e) 
            {
            }
        }
	}
	
	private void sendFastTapEvents(List<XbrEvent> events, boolean fastTapEvents, boolean fastTapUpdateStream)
	{
		String venue = ConfigOptions.INSTANCE.getControllerInfo()
				.getVenue();
		venue = venue.replaceFirst("[^\\d].*$", "");

		// escape name in case there are any weird characters in it
		venue = StringEscapeUtils.escapeXml(venue);
		
		// 99% of the time there will be only a single tap event in this list.
		for (XbrEvent ev : events)
		{
			String sEnv = "<venue name=\"" + venue + "\" " + "time=\""
					+ MessageGenerator.formatTime(new Date()) + "\">\n";

			StringBuilder sb = new StringBuilder();
			sb.append(sEnv);
			
			String guestId = null;
			TapEvent te = (TapEvent)ev;
			
			if (te.getSid() != null)
				guestId = "?SecureID=" + te.getSid();
			else 
				guestId = "?RFID=" + te.getID();
			
			TapEventAggregate tea = 
					new TapEventAggregate(ev.getID(), te.getPidDecimal(), ev.getReaderName(), ev.getTime(), te);
			
			String payload = MessageGenerator.createGuestMessagePayload("LOCATIONEVENT", 
					new TapMessageArgs(tea, false, guestId, te.getPidDecimal(), "", "", ""));
			
			sb.append(payload);
			
			sb.append("</venue>");
			
			if (fastTapEvents)
			{
				try 
				{
					HTTPAgent.INSTANCE.sendHTTPMessageAsync(ConfigOptions.INSTANCE.getControllerInfo().getFastTapUrl(), sb.toString());
				}
				catch (Exception e) 
				{
					logger.error("Failed to send tap message to " + ConfigOptions.INSTANCE.getControllerInfo().getFastTapUrl(), e);
				}
			}
			
			if (fastTapUpdateStream)
			{
				try 
				{
					HTTPAgent.INSTANCE.sendHTTPMessageAsync(ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL(), sb.toString());
				}
				catch (Exception e) 
				{
					logger.error("Failed to send tap message to " + ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL(), e);
				}
			}
		}
	}

    private void handleReaderPutUpdateStream(final Request request, final Response response)
	{
		String sPutData = "";
		InputStream is = null;
        
        try
		{
			is = request.getInputStream();
			sPutData = new Scanner(is).useDelimiter("\\A").next();
			
			 // if ha is enabled, reject any data that's missing the BigIP provided X-Forwarded-For header
			String sForwardedFor = request.getValue("X-Forwarded-For");
			if (sForwardedFor==null && ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
			{
				// eek!
				logger.error("HA is enabled but reader appears to be configured with DIP instead of VIP address. Event data:\n" + sPutData);
				ResponseFormatter.return500(response, "Receiving events. HA is enabled but reader appears to be configured with DIP instead of VIP address. Events ignored.");
				return;
			}

			startProcessingEvents(request.getClientAddress().getAddress().getHostAddress());

			final XbrEvents events = XbrJsonMapperFast.mapEvents(sPutData);
			
			if (events.getEvents() == null || events.getEvents().isEmpty())
			{
				ResponseFormatter.return200(response);
				return;
			}
			
			ReaderInfo ri = ConfigOptions.INSTANCE.getReadersInfo().getReader(events.getReaderName());
			
			if (ri==null)
			{
				logger.error("Received events from unknown reader: " + events.getReaderName() + ". Ignoring.");
				ResponseFormatter.return500(response, "Unknown reader");
				return;
			}
			
			if (ri.getLocation().getName().equals("UNKNOWN"))
			{
				logger.error("Received events from reader: " + events.getReaderName() + " at UNKNOWN location. Ignoring.");
				ResponseFormatter.return200(response);
				return;
			}
			
			if (ConfigOptions.INSTANCE.getControllerInfo().isAdjustEventTimes())
			{
				// adjust times
				final long lNow = new Date().getTime();

				// find the most recent time
				long lMax = Long.MIN_VALUE;
				for (XbrEvent ev : events.getEvents())
					if (ev.getTime().getTime() > lMax)
						lMax = ev.getTime().getTime();

				// adjust all the dates so that lMax is mapped to lNow but
				// relative times are still maintained
				for (XbrEvent ev : events.getEvents())
					ev.getTime()
							.setTime(lNow - (lMax - ev.getTime().getTime()));
			}
			
			long lastIdReceived = events.getEvents().getLast().getEno();
			
			List<XbrEvent> fastEventList = null;
			boolean fastTapEvents = false;
			boolean fastTapUpdateStream = false;
			boolean haveTaps = false;
			
			// See if we need to send fast taps from WAYPOINT locations.
			if ((ri.getType() == ReaderType.xfp || ri.getType() == ReaderType.xtpra) && 
					Processor.INSTANCE.getModel().isWaypointLocationType(ri.getLocation()))
			{
				fastTapEvents = !ConfigOptions.INSTANCE.getControllerInfo().getFastTapUrl().isEmpty() &&
								!ConfigOptions.INSTANCE.getControllerInfo().getFastTapUrl().startsWith("#");
				fastTapUpdateStream = ConfigOptions.INSTANCE.getControllerInfo().isUpdateStreamFastTaps() &&
										(!ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL().isEmpty() &&
										 !ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL().startsWith("#"));
				
				if (fastTapEvents || fastTapUpdateStream)
					fastEventList = new LinkedList<XbrEvent>();
			}
			
			// We must send all fast tap events before engaging in things like IDMS lookups
			if ((fastTapEvents || fastTapUpdateStream) && (fastEventList != null))
			{
				for (Iterator<XbrEvent> it = events.getEvents().iterator(); it.hasNext();)
				{				
					XbrEvent ev = it.next();
					
					if (ev instanceof TapEvent)
					{
						if ((fastTapEvents || fastTapUpdateStream) && (fastEventList != null))
						{
							fastEventList.add(ev);
							
							// cannot pass the tap event to the processor or a duplicate event will be sent.. 
							if (fastTapUpdateStream)
								it.remove();
						}
					}
				}
				
				if (fastEventList != null && !fastEventList.isEmpty())
					sendFastTapEvents(fastEventList, fastTapEvents, fastTapUpdateStream);
			}
			
			// Now analyze all events and do IDMS lookups and other processing 
			for (Iterator<XbrEvent> it = events.getEvents().iterator(); it.hasNext();)
			{				
				XbrEvent ev = it.next();
				
				if (ev instanceof XbrDiagEvent)
				{
					final XbrDiagEvent dev = (XbrDiagEvent)ev;
					
					if (ri != null)
					{
						synchronized(ri)
						{
							ri.setDeviceStatus(dev.getStatus());
							ri.setDeviceStatusMessage(dev.getStatusMsg());
							ri.setBatteryLevel(dev.getBatteryLevel());
							ri.setBatteryTime(dev.getBatteryTime());
							ri.setTemperature(dev.getTemperature());
							ri.setSentDiagnosticSettings(true);
						}
							
						if (!dev.getStatus().equals(StatusType.Green)) {
							logger.warn("Reader " + ev.getReaderName() + " ("+ ri.getIpAddress() + ") diagnostic message: " + dev.getStatusMsg());
	
						}
					}
				}
				if (ev instanceof XfpDiagEvent)
				{
					final XfpDiagEvent dev = (XfpDiagEvent)ev;
					
					if (ri != null)
					{
						synchronized (ri)
						{
							ri.setDeviceStatus(dev.getStatus());
							ri.setDeviceStatusMessage(dev.getStatusMsg());
							ri.setSentDiagnosticSettings(true);
						}
						
						if (!dev.getStatus().equals(StatusType.Green)) {
							logger.warn("Reader " + ev.getReaderName() + " ("+ ri.getIpAddress() + ") diagnostic message: " + dev.getStatusMsg());
						}
					}
				}
				
				if (ev instanceof TapEvent)
					haveTaps = true;
				
				if (ev instanceof LrrEvent && ri.getGain() != 0)
				{
					// Apply the gain and guard against out of range values
					((LrrEvent)ev).setSs(Math.max(EventConfig.getInstance().getSsaMin(), 
							Math.min(EventConfig.getInstance().getSsaMax(), (long)(((LrrEvent)ev).getSs() + ri.getGain()))));				
				}
				
				// get the guest associated with the tap id				

				if (IDMSResolver.INSTANCE.isEnabled() && (ev.getType() == XbrEventType.LRR || ev.getType() == XbrEventType.RFID))
				{
					Date dtStartIDMS = new Date();
					Date dtEndIDMS;
					Xband xb = null;					
					
					// ignore all but LRR and tap events
					if ( ev.getType() == XbrEventType.LRR)
					{
						LrrEvent lrre = (LrrEvent) ev;						
						xb = IDMSResolver.INSTANCE.getBandFromLRID(lrre.getID());
						lrre.setXband(xb);
					}
					else if (ev.getType() == XbrEventType.RFID)
					{
						TapEvent te = (TapEvent) ev;						
						// look the band up					
						xb = IDMSResolver.INSTANCE.getBandFromRFID(te.getID());
						if (xb==null)
							logger.warn("Failed to map lrid (" + te.getID() + " to band");
						te.setXband(xb);
					}					

					if (xb!=null && !IDMSResolver.INSTANCE.isPlaceholder(xb))
					{
						Guest g = IDMSResolver.INSTANCE.getGuestFromBandId(xb.getBandId());
						dtEndIDMS = new Date();						
						ev.setGuest(g);
					}
					else
						dtEndIDMS = new Date();

					// account for the time spent in idms
					Processor.INSTANCE.getStatusObject().getPerfIDMSQueryMsec().processValue(dtEndIDMS.getTime() - dtStartIDMS.getTime());
				}
			}
				
			ResponseFormatter.return200(response);
			
			if (ri.isEnabled())
			{	
				Processor.INSTANCE.pushEvents(events.getEvents());
				
				if (haveTaps)
					Processor.INSTANCE.notifyOfHighPriorityEvent();				
			}
			
			updateReaderLastIdReceived(ri, lastIdReceived);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling reader PUT stream from "
							+ request.getClientAddress().getAddress().getHostAddress(), e));
			logger.error(sPutData);
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling reader PUT stream from "
							+ request.getClientAddress().getAddress().getHostAddress(), e));
		}
        finally 
        {
            // Clean up our connection.
            try 
            {
                if ( is != null ) 
                {
                    is.close();
                }
            }
            catch (Exception ioException) 
            {
            }
        }

	}

	private void handlePutGuestPositions(Request request, Response response)
	{
        InputStream is = null;

		try
		{
			is = request.getInputStream();
			String sPutData = new Scanner(is).useDelimiter("\\A").next();

			JSONArray ja = null;

			try
			{
				ja = JSONArray.fromObject(sPutData);
			}
			catch (Exception ex)
			{
				logger.error(ExceptionFormatter
						.format("Error with PUT GuestPosition message: "
								+ sPutData, ex));
				ResponseFormatter.return500(response, ExceptionFormatter.format(
						"Error with PUT GuestPosition", ex));
				return;
			}

			int cEls = ja.size();
			ArrayList<GuestInfo> al = new ArrayList<GuestInfo>();
			for (int i = 0; i < cEls; i++)
			{
				JSONObject jo = (JSONObject) ja.get(i);

				// pluck out:
				// "XID": "bandid"
				// "x": double
				// "y": double
				// "hasxPass": "true" | "false"
				String sXID = jo.getString("XID");
				double x = jo.getDouble("x");
				double y = jo.getDouble("y");
				boolean bHasxPass = jo.getBoolean("hasxpass");

				al.add(new GuestInfo(sXID, x, y, bHasxPass));
			}

			// now, write these to the database
			Connection dbConn = null;
			try
			{
				dbConn = Controller.getInstance().getPooledConnection();

				dbConn.setAutoCommit(false);

				// first, delete any rows
				Statement stmt = dbConn.createStatement();
				String sCmd = "DELETE FROM GuestPosition";
				stmt.execute(sCmd);

				sCmd = "INSERT INTO GuestPosition(BandID, x, y, HasxPass) Values(?, ?, ?, ?)";
				PreparedStatement prep = dbConn.prepareStatement(sCmd);

				// now insert
				for (int i = 0; i < al.size(); i++)
				{
					GuestInfo gi = al.get(i);
					prep.clearParameters();
					prep.setString(1, gi.getsXID());
					prep.setDouble(2, gi.getX());
					prep.setDouble(3, gi.getY());
					prep.setBoolean(4, gi.getHasxPass());
					prep.execute();
				}
				prep.close();
				dbConn.commit();
			}
			catch (Exception e)
			{
				logger.error(ExceptionFormatter.format(
						"Error clearing or inserting guest positions", e));
				ResponseFormatter.return500(response, ExceptionFormatter.format(
						"Error clearing or inserting guest positions", e));

			}
			finally
			{
				if (dbConn != null)
					Controller.getInstance().releasePooledConnection(dbConn);
			}
			ResponseFormatter.return200(response);

		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling guest PUT guestpositions", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling guest PUT guestpositions", e));
		}
        finally 
        {
            // Clean up our connection.
            try 
            {
                if ( is != null ) 
                {
                    is.close();
                }
            }
            catch (Exception ioException) 
            {
            }
        }
	}

	private void handleClientPutUpdateStream(Request request, Response response)
	{
        InputStream is = null;

		try
		{
			is = request.getInputStream();
			UpdateStreamInfo usi = (UpdateStreamInfo) convertToPojo(is,
					UpdateStreamInfo.class);
			if (logger.isTraceEnabled())
			{
				String xml = XmlUtil.convertToXml(usi, UpdateStreamInfo.class);
				logger.trace(xml);
			}
			HTTPAgent.INSTANCE.setHTTPUpdateStream(usi.getUrl(), 
					usi.getMax(),
					usi.getInterval(), 
					usi.getAfter(), 
					usi.getPreferredGuestIdType(), 
					usi.getMessageTypes(), 
					usi.getBatchsize());
			
			ResponseFormatter.return200(response);
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling client PUT updatestream", e));
			ResponseFormatter.return500(response, e.toString());
		}
		catch (JAXBException e)
		{
			logger.error(ExceptionFormatter.format(
					"XML Error handling client PUT updatestream", e));
			ResponseFormatter.return500(response, e.toString());
		}
        finally 
        {
            // Clean up our connection.
            try 
            {
                if ( is != null ) 
                {
                    is.close();
                }
            }
            catch (Exception ioException) 
            {
            }
        }
	}

	private void handlePutUpdateConfig(Request request, Response response)
	{
		InputStream is = null;
		UpdateConfig updateConfig = null;
		String sPutData = null;

		try
		{
			is = request.getInputStream();
			try
			{
				if (is != null)
				{
					sPutData = new Scanner(is).useDelimiter("\\A").next();
				}
			}
			catch (Exception ignore)
			{
			}
			
			if (sPutData != null && !sPutData.isEmpty())
				updateConfig = XmlUtil.convertToPojo(new ByteArrayInputStream(sPutData.getBytes()), UpdateConfig.class);
			
			// note that re-read is deferred and performed in the main thread
			Processor.INSTANCE.reReadConfiguration(updateConfig);
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling PUT /updateconfig", e));
			ResponseFormatter.return500(response, e.toString());
		}
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }
	}

	private void handlePutProperties(Request request, Response response)
	{
		propertiesManager.handlePutProperties(request, response);

	}
	
	private void handlePutSchedulerMessage(Request request, Response response)
	{
		InputStream is = null;
		SchedulerRequest req = null;
		String sPutData = null;
		PrintStream body = null;
		
		MessagePayloadFormat format = acceptedMessagePayloadFormat(request);

		try
		{
			is = request.getInputStream();
			try
			{
				if (is != null)
				{
					sPutData = new Scanner(is).useDelimiter("\\A").next();
				}
			}
			catch (Exception ignore)
			{
			}
			
			if (format == MessagePayloadFormat.json)
				req = JsonUtil.convertToPojo(sPutData, SchedulerRequest.class);
			else
				req = XmlUtil.convertToPojo(new ByteArrayInputStream(sPutData.getBytes()), SchedulerRequest.class);
			
			SchedulerResponse resp = schedulerServer.processRequest(req);
			
			String strBody;
			if (format == MessagePayloadFormat.json)
				strBody = JsonUtil.convertToJson(resp);
			else
				strBody = XmlUtil.convertToXml(resp, SchedulerResponse.class);

	        body = response.getPrintStream();
			body.println(strBody);
			ResponseFormatter.setResponseHeader(response, format.getContentType());		
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling PUT /scheduler/message", e));
			ResponseFormatter.return500(response, e.toString());
		}
        finally 
        {
	    	 if(body != null) 
	         {
	             try 
	             {
	                 body.close();
	             }
	             catch(Exception ignore) 
	             {
	             }
	         }
        	 
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }
	}

	private void handlePutConfigurationStore(Request request, Response response)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        PrintStream body = null;

		try
		{
			body = response.getPrintStream();

			// pluck the parameters
			String sName = request.getParameter("name");
			if (sName == null)
			{
				ResponseFormatter.return404(response);
				return;
			}
			String sDescription = request.getParameter("description");
			if (sDescription == null)
				sDescription = "Created on: "
						+ XbrcDateFormatter.formatTime(new Date().getTime());

			// get the current configuration
			String sConfigCur = serializeCurrentConfiguration(sName);

			// now store this in the database
			conn = Controller.getInstance().getPooledConnection();
			pstmt = conn
					.prepareStatement(
							"INSERT INTO StoredConfigurations(name, description, model, xml) VALUES (?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, sName);
			pstmt.setString(2, sDescription);
			pstmt.setString(3, Processor.INSTANCE.getModel().getClass().getName()
					.toString());
			pstmt.setString(4, sConfigCur);
			int cRows = pstmt.executeUpdate();
			if (cRows != 1)
			{
				ResponseFormatter.return500(response, "Failed to store configuration");
				return;
			}

			// fetch the new id
			rs = pstmt.getGeneratedKeys();
			ResultSetMetaData rsmd = rs.getMetaData();
			int id = -1;
			if (rsmd.getColumnCount() == 1)
			{
				if (rs.next())
				{
					id = rs.getInt(1);
				}
			}

			// return it
			body.println(id);
			ResponseFormatter.setResponseHeader(response, "text/plain");
			response.setCode(200);
		}
		catch (Exception ex)
		{
			String sError = ExceptionFormatter.format(
					"Error storing configuration", ex);
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
		finally
		{
            if(body != null) 
            {
                try 
                {
                    body.close();
                }
                catch(Exception ignore) 
                {
                }
            }

			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex2)
			{
			}

			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	private void handlePutConfigurationSelect(Request request,
			Response response, String sPath)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			// pluck the parameters from the path
			String sName = null;
			int nId = -1;
			if (sPath.startsWith("/selectconfiguration/id/"))
			{
				String sId = sPath.substring("/selectconfiguration/id/"
						.length());
				try
				{
					nId = Integer.parseInt(sId);
				}
				catch (Exception ex)
				{
					ResponseFormatter.return500(response, ex.getLocalizedMessage());
					return;
				}
			}
			else if (sPath.startsWith("/selectconfiguration/name/"))
			{
				sName = sPath.substring("/selectconfiguration/name/".length());

			}
			else
			{
				ResponseFormatter.return404(response);
				return;
			}

			// fetch the configuration
			conn = Controller.getInstance().getPooledConnection();
			if (sName != null)
			{
				pstmt = conn
						.prepareStatement("SELECT model,xml from StoredConfigurations WHERE name=? AND internal=?");
				pstmt.setString(1, sName);
				pstmt.setBoolean(2, false);
			}
			else
			{
				pstmt = conn
						.prepareStatement("SELECT model,xml from StoredConfigurations WHERE id=? AND internal=?");
				pstmt.setInt(1, nId);
				pstmt.setBoolean(2, false);
			}

			rs = pstmt.executeQuery();
			if (rs.next())
			{
				String sModel = rs.getString("model");

				// verify we're not trying to do a model switch
				if (sModel.compareTo(Processor.INSTANCE.getModel().getClass().getName()
						.toString()) != 0)
				{
					ResponseFormatter.return500(response,
							"Cannot switch to a configuration that uses a different xBRC model");
					return;
				}

				String sXML = rs.getString("xml");
				switchToConfiguration(sName, sXML, request.getClientAddress().getAddress().getHostAddress());
				ResponseFormatter.return200(response);
			}
			else
				ResponseFormatter.return404(response);
		}
		catch (Exception ex)
		{
			String sError = ExceptionFormatter.format(
					"Error loading configuration", ex);
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
			}
			catch (Exception ex2)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	private void handlePutLogComment(Request request, Response response)
	{
		try
		{
			String sText = request.getParameter("text");
			if (sText == null)
			{
				ResponseFormatter.return500(response, "Missing text parameter");
				return;
			}

			String sComment = new Date().getTime() + ",COMMENT," + sText;
			ResponseFormatter.return200(response);

			Log.INSTANCE.logEKG(sComment);
		}
		catch (IOException e)
		{
			String sError = ExceptionFormatter.format(
					"Error handling PUT /logcomment", e);
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
	}
	
	private void handlePutConfiguration(Request request, Response response)
	{
        InputStream is = null;

		try
		{
			// get the payload
			is = request.getInputStream();
			String sPutData = new Scanner(is).useDelimiter("\\A").next();

			logger.trace("got PUT /configuration");
			String sName = request.getParameter("name");
			String sDescription = request.getParameter("description");
			storeConfiguration(sName, sDescription, false, sPutData, response,
                    request.getClientAddress().getAddress().getHostAddress());
			return;
		}
		catch (Exception e)
		{
			String sError = ExceptionFormatter.format(
					"Error PUT /configuration", e);
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }
	}

	private void handlePutCalibrate(Request request, Response response, String sPath)
	{
		boolean bStart = sPath.equals("/calibrate/start");
		if (!bStart)
		{
			if (!sPath.equals("/calibrate/end"))
			{
				ResponseFormatter.return404(response);
				return;
			}
		}
		
		for( ReaderInfo rdr : ConfigOptions.INSTANCE.getReadersInfo().getReaders())
		{
			// skip non xBRs
			if (!ReaderType.isLongRangeReader(rdr.getType()))
				continue;
			
			// if the min-ss was omitted, set it back to the configured value
			if (bStart)
				ReaderApi.sendSetSignalThreshold(rdr, -127);
			else
				ReaderApi.sendSetSignalThreshold(rdr, rdr.getSignalStrengthThreshold());
		}

		bCalibrate = bStart;
		ResponseFormatter.return200(response);

	}
	
	private void handlePutAvmsEvent(Request request, Response response, String sPath)
	{
		InputStream is = null;
		
		try
		{
			is = request.getInputStream();			
			String sPutData = new Scanner(is).useDelimiter("\\A").next();
						
			 // if ha is enabled, reject any data that's missing the BigIP provided X-Forwarded-For header
			String sForwardedFor = request.getValue("X-Forwarded-For");
			if (sForwardedFor==null && ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
			{
				logger.error("VEHICLE: HA is enabled but Vehicle ID system appears to be configured with DIP instead of VIP address. Vehicle ID data:\n" + sPutData);
				ResponseFormatter.return500(response, "Receving Vehicle ID data. HA is enabled but Vehicle ID system to be configured with DIP instead of VIP address. Data ignored");
				return;
			}

			startProcessingEvents(request.getClientAddress().getAddress().getHostAddress());
			
			if (logger.isTraceEnabled())
			{
				logger.trace("VEHICLE: Vehicle ID event: " + sPutData);
			}
			
			// Hack to deserialize utf-16 message.
			int i = 0;
			if ((i = sPutData.indexOf("utf-16")) > 0 || (i = sPutData.indexOf("UTF-16")) > 0)
			{
				sPutData = sPutData.substring(0, i) + "utf-8" + sPutData.substring(i + 6);
			}
			
			// deserialize the raw event and convert it into an XbrEvent
			RawAVMSEvent rawEvent = XmlUtil.convertToPojo(new ByteArrayInputStream(sPutData.getBytes()), RawAVMSEvent.class);
			AVMSEvent ev = AVMSEvent.fromRawAVMSEvent(rawEvent);
			
			// report clock skew error after allowing for some processing time
			if (Math.abs(ev.getReaderEventTime().getTime() - ev.getTime().getTime()) > 800)
			{
				logger.warn("VEHICLE: Vehicle ID event time is " + (ev.getReaderEventTime().getTime() - ev.getTime().getTime()) + " milliseconds off");
			}
			
			// Add the reader if missing as if hello has been called.
			ReaderInfo ri = ConfigOptions.INSTANCE.getReadersInfo().getReader(ev.getReaderName());
			if (ri == null)
			{
				LocationInfo li = ConfigOptions.INSTANCE.getLocationInfo().get(0);
				
				/*
				 * Global configuration setting is used to determine if a secure id
				 * should be used, unless overwritten by a location's setting.
				 */
				boolean useSecureId = ConfigOptions.INSTANCE.getControllerInfo().isSecureTapId();
				if (li.isUseSecureId() != null)
					useSecureId = li.isUseSecureId();
				
				// add entry to reader table
				ri = new ReaderInfo(-1, ev.getReaderName(), ReaderType.vid, "", "", 8080, 0,
						1.1, ControllerLibUtils.IPToMac(request.getClientAddress().getAddress(), ev.getReaderName()), request.getClientAddress().getAddress().getHostAddress(), 
						-1, new Date().getTime(), false, 0, 0, 0, 0, "1.0.0-0", "1.0.0-0", false, "", useSecureId, "", "", true, 3, 1, null, true, null);

				ri.setLocation(li);

				// store new reader in db
				addReader(ri);
			}
			
			// Fake the hello time so we can monitor if the reader is alive.
			ri.setTimeLastHello(new Date().getTime());
			updateReaderTimeLastHello(ri);
			
			LinkedList<XbrEvent> list = new LinkedList<XbrEvent>();
			list.add(ev);
			Processor.INSTANCE.pushEvents(list);

			updateReaderLastIdReceived(ri, ri.getLastIDReceived() + 1);
		}
		catch (Exception e)
		{
			String sError = ExceptionFormatter.format(
					"Error PUT /videvent", e);
			logger.error(sError);
		}
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }

		ResponseFormatter.return200(response);
	}
	
	private void handlePutAvmsHello(Request request, Response response, String sPath)
	{
		InputStream is = null;
		
		try
		{
			is = request.getInputStream();			
			String sPutData = new Scanner(is).useDelimiter("\\A").next();
			
			// if ha is enabled, reject any data that's missing the BigIP provided X-Forwarded-For header
			String sForwardedFor = request.getValue("X-Forwarded-For");
			if (sForwardedFor==null && ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
			{
				// eek
				logger.error("VEHICLE: HA is enabled but Vehicle ID system appears to be configured with DIP instead of VIP address. Vehicle ID data:\n" + sPutData);
				ResponseFormatter.return500(response, "Receving Vehicle ID data. HA is enabled but Vehicle ID system appears to be configured with DIP instead of VIP address. Data ignored");
				return;
			}

			startProcessingEvents(request.getClientAddress().getAddress().getHostAddress());
			
			if (logger.isTraceEnabled())
			{
				logger.trace("VEHICLE: Vehicle ID hello: " + sPutData);
			}
			
			// Hack to deserialize utf-16 message.
			int i = 0;
			if ((i = sPutData.indexOf("utf-16")) > 0 || (i = sPutData.indexOf("UTF-16")) > 0)
			{
				sPutData = sPutData.substring(0, i) + "utf-8" + sPutData.substring(i + 6);
			}
			
			// deserialize the raw event and convert it into an XbrEvent
			RawAVMSEvent rawEvent = XmlUtil.convertToPojo(new ByteArrayInputStream(sPutData.getBytes()), RawAVMSEvent.class);
			AVMSEvent ev = AVMSEvent.fromRawAVMSEvent(rawEvent);
			
			if (rawEvent.getType().equals(RawAVMSEvent.TYPE_SYSTEMHELLO)) 
			{
				Processor.INSTANCE.onVidSystemHello(rawEvent);
			}
			else
			{			
				// Add the reader if missing as if hello has been called.
				ReaderInfo ri = ConfigOptions.INSTANCE.getReadersInfo().getReader(ev.getReaderName());
				if (ri == null)
				{
					LocationInfo li = ConfigOptions.INSTANCE.getLocationInfo().get(0);
					
					// add entry to reader table
					ri = new ReaderInfo(-1, ev.getReaderName(), ReaderType.vid, "", "", 8080, 0,
							1.1, ControllerLibUtils.IPToMac(request.getClientAddress().getAddress(), ev.getReaderName()), request.getClientAddress().getAddress().getHostAddress().toString(), 
							-1, new Date().getTime(), false, 0, 0, 0, 0, "1.0.0-0", "1.0.0-0", false, "", false, "", "", true, 3, 1, null, true, null);
	
					ri.setLocation(li);
	
					// store new reader in db
					addReader(ri);
				}
				
				// Fake the hello time so we can monitor if the reader is alive.
				ri.setTimeLastHello(new Date().getTime());
				updateReaderTimeLastHello(ri);
				
				Processor.INSTANCE.onVidReaderHello(rawEvent, ri);
			}
		}
		catch (Exception e)
		{
			String sError = ExceptionFormatter.format(
					"Error PUT /vidhello", e);
			logger.error(sError);
		}
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }

		ResponseFormatter.return200(response);
	}

    private void handlePutPlaySequence(Request request, Response response, String sPath)
    {
        try
        {
            // get all or just for one reader?
            String sReader = null;
            String sSequence = null;
            Long lTimeout = null;
            ReaderInfo ri = null;

            // We should receive a reader name, sequence name, and timeout.
            // The format is /playsequence/readername?sequencename=test&timeout=1000
            String sPrefix = "/playsequence/";
            if (sPath.startsWith(sPrefix))
            {
                // Parse out the reader name.
                sReader = sPath.substring(sPrefix.length());
                int i = sReader.indexOf("?");
                if ( i >= 0 )
                {
                    sReader = sReader.substring(0, i);
                }

                if (sReader.isEmpty())
                {
                	ResponseFormatter.return404(response);
                    return;
                }

                // verify this reader exists
                ri = ConfigOptions.INSTANCE.getReadersInfo().getReader(sReader);
                if (ri == null)
                {
                	ResponseFormatter.return404(response);
                    return;
                }

                // Parse out the sequence name and timeout parameters.
                sSequence = request.getParameter("sequencename");
                if (sSequence == null || sSequence.isEmpty())
                {
                	ResponseFormatter.return404(response);
                    return;
                }

                // Timeout is optional.
                String sTimeout = request.getParameter("timeout");
                if ( sTimeout != null && !sTimeout.isEmpty())
                {
                    lTimeout = Long.parseLong(sTimeout);
                }

                // Send the reader the sequence request.
                int retCode = ReaderApi.setReaderSequence(ri, sSequence, lTimeout);
                if ( retCode == 404 ) {
                    logger.warn("Unable to find sequence. Reader=" + sReader + ";Sequence=" + sSequence);
                    ResponseFormatter.return404(response);
                }
            }
            else
            {
            	ResponseFormatter.return404(response);
                return;
            }

            ResponseFormatter.return200(response);
        }
        catch (Exception e)
        {
            logger.error(ExceptionFormatter.format(
                    "Error playing media sequence: ", e));
            ResponseFormatter.return500(response, ExceptionFormatter.format(
                    "Error playing media sequence: ", e));
        }
    }

    private void handlePutReplaceReader(Request request, Response response, String sPath)
    {
        InputStream is = null;
        Connection conn = null;

        try
        {
            is = request.getInputStream();
            String sPutData = new Scanner(is).useDelimiter("\\A").next();

            // get all or just for one reader?
            String oldReaderName = null;
            ReaderInfo oldRi = null;
            ReaderInfo newRi = null;

            String sPrefix = "/replacereader/";
            if (sPath.startsWith(sPrefix))
            {
                // Parse out the reader name.
                oldReaderName = sPath.substring(sPrefix.length());
                int i = oldReaderName.indexOf("?");

                if ( i >= 0 ) {
                    oldReaderName = oldReaderName.substring(0, i);
                }

                if ((oldReaderName == null) || oldReaderName.isEmpty())
                {
                	ResponseFormatter.return404(response);
                    return;
                }

                // verify this reader exists
                oldRi = ConfigOptions.INSTANCE.getReadersInfo().getReader(oldReaderName);

                if (oldRi == null) {
                	ResponseFormatter.return404(response);
                    return;
                }

                // Hack to deserialize utf-16 message.
                if ((i = sPutData.indexOf("utf-16")) > 0 || (i = sPutData.indexOf("UTF-16")) > 0) {
                    sPutData = sPutData.substring(0, i) + "utf-8" + sPutData.substring(i + 6);
                }

                newRi = XmlUtil.convertToPojo(new ByteArrayInputStream(sPutData.getBytes()), ReaderInfo.class);
                conn = Controller.getInstance().getPooledConnection();
                ReaderService.replaceReader(conn, oldRi, newRi, Controller.getInstance().getXbrcAddresses());
                
                // tell the reader to start saying HELLO to this xbrc 
                String ownIP = null;
        		String vipAddress = ConfigOptions.INSTANCE.getControllerInfo().getVipAddress();
        		if (vipAddress!=null && vipAddress.length()>0 && !vipAddress.startsWith("#"))
        		{
        			ownIP = vipAddress;
        		}
        		else
        		{
        			// try to return an address on the same network as the requestor
        			String sClientIP = request.getClientAddress().getAddress().getHostAddress();

        			String ipPrefix = "";
        			if (sClientIP.compareTo("127.0.0.1") != 0)
        			{
        				String[] ips = sClientIP.split("\\.");
        				if (ips.length == 4)
        					ipPrefix = ips[0] + "." + ips[1];
        			}

        			Collection<String> lIPs = null;
        			try
        			{
        				lIPs = NetInterface.getOwnIpAddress(ipPrefix);
        			}
        			catch (SocketException e)
        			{
        				logger.error(ExceptionFormatter.format("Unable to get our own ip address", e));
        			}
        			
        			if (lIPs!=null && lIPs.size()>0)
        				ownIP = lIPs.iterator().next();
        		}
                
                Processor.INSTANCE.reReadConfiguration(null);
                
                int statusCode = ReaderApi.sendPut(newRi.getURL() + "/xbrc?url=http://" + ownIP + ":" + ConfigOptions.INSTANCE.getControllerInfo().getPort(), "");
                if (statusCode > 200)
                {
                	// Failed to notify the reader about the new xBRC it should say hello to. Revert the reader replacement.
                	int newRiId = newRi.getId();
                	newRi.setId(oldRi.getId());
                	oldRi.setId(newRiId);
                	
                	// Return the reader that has just been replaced to its original state
                	ReaderService.replaceReader(conn, newRi, oldRi, Controller.getInstance().getXbrcAddresses());
                	
                	if (newRiId != -1)
                	{
                		// The replacement candidate wasn't a lost/found reader but an unlinked reader. Recreate.
                		Reader unlinkedReader = ReaderService.createReaderFromInfo(newRi);
                		unlinkedReader.setId(null);
                		ReaderService.save(conn, unlinkedReader);
                	}
                	
                	ResponseFormatter.return424(response, "Replacement aborted. Not able to communicate with reader " + newRi.getMacAddress());
                	Processor.INSTANCE.reReadConfiguration(null);
                	
                	return;
                }
                
                ResponseFormatter.return200(response);
                return;
            }
            else
            {
            	ResponseFormatter.return404(response);
                return;
            }
        }
        catch (Throwable e)
        {
            logger.error(ExceptionFormatter.format("Failed to replace reader: ", e));
            ResponseFormatter.return500(response, ExceptionFormatter.format("Failed to replace reader: ", e));
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(Exception ignore) {
                }

                if (conn != null) {
                    Controller.getInstance().releasePooledConnection(conn);
                }
            }
        }
    }

    private void handlePowerOverrideClear(Request request, Response response, String sPath)
    {
    	try
    	{
			ConfigOptions.INSTANCE.getControllerInfo().setReaderPowerOverrideDate(null);
			ConfigOptions.INSTANCE.getControllerInfo().setReaderPowerOverrideMinutes(0);
			propertiesManager.saveProperties(null);
            ResponseFormatter.return200(response);
    	}
    	catch (Exception e)
    	{
            logger.error(ExceptionFormatter.format(
                    "Error clearing schedule override: ", e));
            ResponseFormatter.return500(response, ExceptionFormatter.format(
                    "Error clearing schedule override: ", e));
    	}
    }
    
    private void handlePowerOverrideOn(Request request, Response response, String sPath)
    {
    	try
    	{
    		String minutes = request.getParameter("minutes");
    		if (minutes == null)
    		{
    			throw new Exception("Parameter 'minutes' is missing.");
    		}
    	
    		if (minutes != null)
    		{
    			int min = Integer.parseInt(minutes);
    			if (min < 1)
    			{
    				throw new Exception("Parameter 'minutes' needs to be greater than zero.");
    			}
    			if (min > 0)
    			{
    				DateTime now = new DateTime(DateTimeZone.UTC);
    				String time = jodaDateFormatter.print(now);
    				ConfigOptions.INSTANCE.getControllerInfo().setReaderPowerOverrideMinutes(min);
    				ConfigOptions.INSTANCE.getControllerInfo().setReaderPowerOverrideDate(time);
    				propertiesManager.saveProperties(null);    				
    			}
    		}
            ResponseFormatter.return200(response);
    	}
    	catch (Exception e)
    	{
            logger.error(ExceptionFormatter.format(
                    "Error overriding schedule: ", e));
            ResponseFormatter.return500(response, ExceptionFormatter.format(
                    "Error overriding schedule: ", e));
    	}
    }
    
    /*
      * POST handlers
      */

	private void handleModelRequest(Request request, Response response,
			String sPath)
	{
		// pass on to the model
		Processor.INSTANCE.getModel().processExternal(request, response, sPath);

	}

	private void handlePostNewConfiguration(Request request, Response response)
	{
        InputStream is = null;

		try
		{
			// get the payload
			is = request.getInputStream();
			String sPutData = new Scanner(is).useDelimiter("\\A").next();

			logger.trace("got POST  /newconfiguration");
			String sName = request.getParameter("name");
			String sDescription = request.getParameter("description");
			storeConfiguration(sName, sDescription, true, sPutData, response,
                    request.getClientAddress().getAddress().getHostAddress());
			return;
		}
		catch (Exception e)
		{
			String sError = ExceptionFormatter.format(
					"Error POST /newconfiguration", e);
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }
	}

	/*
	 * DELETE handlers
	 */

	private void handleDeleteUpdateStream(Request request, Response response)
	{
		HTTPAgent.INSTANCE.clearHTTPUpdateStream();
		ResponseFormatter.return200(response);
	}

	private void handleDeleteConfiguration(Request request, Response response,
			String sPath)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			// pluck the parameters from the path
			String sName = null;
			int nId = -1;
			if (sPath.startsWith("/configuration/id/"))
			{
				String sId = sPath.substring("/configuration/id/".length());
				try
				{
					nId = Integer.parseInt(sId);
				}
				catch (Exception ex)
				{
					ResponseFormatter.return500(response, ex.getLocalizedMessage());
					return;
				}
			}
			else if (sPath.startsWith("/configuration/name/"))
			{
				sName = sPath.substring("/configuration/name/".length());

			}
			else
			{
				ResponseFormatter.return404(response);
				return;
			}

			// fetch the configuration
			conn = Controller.getInstance().getPooledConnection();
			if (sName != null)
			{
				pstmt = conn
						.prepareStatement("DELETE FROM StoredConfigurations WHERE name=? AND internal=?");
				pstmt.setString(1, sName);
				pstmt.setBoolean(2, false);
			}
			else
			{
				pstmt = conn
						.prepareStatement("DELETE FROM StoredConfigurations WHERE id=? AND internal=?");
				pstmt.setInt(1, nId);
				pstmt.setBoolean(2, false);
			}

			if (pstmt.executeUpdate() == 1)
			{
				ResponseFormatter.return200(response);
			}
			else
			{
				ResponseFormatter.return404(response);
			}
		}
		catch (Exception ex)
		{
			String sError = ExceptionFormatter.format(
					"Error handling DELETE /configuration", ex);
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex2)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}

	}

	private void handleDeleteCache(Request request, Response response, String sPath)
	{
		if (sPath.equals("/cache"))
		{
			// delete everything
			IDMSResolver.INSTANCE.clearCache();
		}
		else if (sPath.startsWith("/cache/guest/id/"))
		{
			// fetch the guest id
			String sGuestId = sPath.substring("/cache/guest/id/".length());
			IDMSResolver.INSTANCE.clearCacheByGuestId(sGuestId);
		}
		else if (sPath.startsWith("/cache/band/secureid/"))
		{
			// fetch the id
			String sID = sPath.substring("/cache/band/secureid/".length());
			IDMSResolver.INSTANCE.clearCacheBySecureId(sID);
		}
		else if (sPath.startsWith("/cache/band/lrid/"))
		{
			// fetch the id
			String sID = sPath.substring("/cache/band/lrid/".length());
			IDMSResolver.INSTANCE.clearCacheByLRID(sID);
		}
		else if (sPath.startsWith("/cache/band/rfid/"))
		{
			// fetch the id
			String sID = sPath.substring("/cache/band/rfid/".length());
			IDMSResolver.INSTANCE.clearCacheByRFID(sID);
		}
		else
		{
			// invalid delete options
			ResponseFormatter.return404(response);
			return;
		}

		// it worked
		ResponseFormatter.return200(response);
	}
	
	private void handleDeleteGuestStatus(Request request, Response response)
	{
		// tell model to clear all of its state
		Processor.INSTANCE.getModel().clear();
		ResponseFormatter.return200(response);
	}

	private void handleDeleteMessages(Request request, Response response)
	{
		Connection conn = null;
		Statement stmt = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM Messages");
			ResponseFormatter.return200(response);
		}
		catch (Exception ex)
		{
			String sError = ExceptionFormatter.format(
					"Error handling DELETE /messages", ex);
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (Exception ex2)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}

	}
	
    private void handleDeleteMediaPackage(Request request, Response response)
    {
        //TODO-travis: Need to send a delete to the reader.
        mediaManager.deleteMediaPackage();
        
        ResponseFormatter.return200(response);
    }

    private void handleDeleteAuditEvents(Request request, Response response, String sPath)
    {
        try
        {
            String prefix = "/deleteauditevents";
            String id = sPath.substring(prefix.length());

            if(id.startsWith("/")) {
                id = id.substring(1).trim();
            }

            IAuditEventsProvider provider = Auditor.getInstance().getEventsProvider();

            if(provider.isAuditEnabled()) {
                if(id.length() > 0) {
                    provider.deleteEvents(Long.parseLong(id));
                }
                else {
                    provider.deleteAllEvents();
                }
            }
        }
        catch (Exception e)
        {
            logger.error(ExceptionFormatter.format(
                    "Error handling GET deleteauditevents", e));
            ResponseFormatter.return500(response, ExceptionFormatter.format(
                    "Error handling GET deleteauditevents", e));
        }

        ResponseFormatter.return200(response);
    }

	private void handlePerfMetricsMetadata(Request request, Response response)
	{
		String xml = null;
		Connection conn = null;

        PrintStream body = null;

		try
		{
			conn = Controller.getInstance().getPooledConnection();
			
			XbrcStatus status = Processor.INSTANCE.getStatusObject();
			
			if (perfMetricMetadata == null)
			{
				perfMetricMetadata = new PerfMetricMetadataEnvelope();
				
				PerfMetric metric = null;
				PerfMetricMetadata meta = null;
				for (Method m: status.getClass().getDeclaredMethods())
				{
					//don't process compiler generated methods
					if (m.isSynthetic())
						continue;
					
					if (m.getReturnType() != PerfMetric.class)
						continue;
					
					metric = (PerfMetric) m.invoke(status, new Object[]{});
					
					if (metric == null)
						continue;
					
					meta = metric.getMetadata();
					
					perfMetricMetadata.add(meta);
				}
			}
			
			// serialize
			xml = XmlUtil.convertToXml(perfMetricMetadata, PerfMetricMetadataEnvelope.class);

			body = response.getPrintStream();
			body.println(xml);
			ResponseFormatter.setResponseHeader(response, "application/xml");
			ResponseFormatter.return200(response);

		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET configuration", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET configuration", e));
		}
		finally
		{
            if(body != null) 
            {
                try 
                {
                    body.close();
                }
                catch(Exception ignore) 
                {
                }
            }

			Controller.getInstance().releasePooledConnection(conn);
		}
	}

    private void handleGetSequences(Request request, Response response, boolean includeColors)
    {
        String xml = null;
        PrintStream body = null;

        try
        {
            ReaderSequenceInfo sequenceInfo = new ReaderSequenceInfo();

            // Initialize the sequence info from the media manager.
            // Must be a better way to initialize a collection in Java,
            // but I don't know how at this moment. Crunch mode.
            String[] sequenceNames = mediaManager.getSequenceNames();
            sequenceInfo.setTimestamp(XbrcDateFormatter.formatTime(new Date().getTime()));

            TreeSet<ReaderSequence> sequenceCollection = new TreeSet<ReaderSequence>();

            // Add the media package sequences.
            for ( String sequenceName : sequenceNames)
                sequenceCollection.add( new ReaderSequence( sequenceName, false) );

            // Include the hardcoded default sequences always available in the reader.
            // Add them second so that any existing media package files essentially
            // override the default sequences.
            sequenceCollection.add( new ReaderSequence("success", true));
            sequenceCollection.add( new ReaderSequence("exception", true));
            sequenceCollection.add( new ReaderSequence("thinking", true));            
            sequenceCollection.add( new ReaderSequence("off", true));
            sequenceCollection.add( new ReaderSequence("idle", true));
            sequenceCollection.add( new ReaderSequence("identify", true));

            if (includeColors)
            {
	            // Add all colors defined in the light message
	            for (String color : LightMsg.getColors())
	            {
	            	sequenceCollection.add( new ReaderSequence(color, true));
	            }
            }

            sequenceInfo.setReaderSequences(sequenceCollection);

            // serialize
            xml = XmlUtil.convertToXml(sequenceInfo, ReaderSequenceInfo.class);

            body = response.getPrintStream();
            body.println(xml);
            ResponseFormatter.setResponseHeader(response, "application/xml");
            ResponseFormatter.return200(response);
        }
        catch (Exception e)
        {
            logger.error(ExceptionFormatter.format(
                    "Error handling GET configuration", e));
            ResponseFormatter.return500(response, ExceptionFormatter.format(
                    "Error handling GET configuration", e));
        }
        finally 
        {
            if(body != null) 
            {
                try 
                {
                    body.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }
    }

    private void handlePutMediaPackage(Request request, Response response)
    {
        InputStream is = null;

        try
        {
            is = request.getInputStream();
            if (!mediaManager.savePackage(is))
            	ResponseFormatter.return500(response, "Failed to process the media package file");
            ResponseFormatter.return200(response);
        }
        catch (Exception e)
        {
            logger.error(ExceptionFormatter.format(
                    "Error sending media package", e));
            ResponseFormatter.return500(response, ExceptionFormatter.format(
                    "Error sending media package", e));
        }
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }
    }
    
    
	private void handlePutUpdateReaderSignalConfig(Request request, Response response)
	{
		// This PUT call notifies the xBRC that the signal strength threshold or gain had changed for a reader.
		// The idea behind this call is to update the signal strength threshold and gain for a reader without
		// re-loading all of the configuration thus allowing for real-time adjustment of these reader settings.
		
		Connection conn = null;		
		try
		{						
			String readerId = request.getParameter("readerid");
			
			if (readerId == null || readerId.equals(""))
				return;		
			
			conn = Controller.getInstance().getPooledConnection();
			Reader r = ReaderService.find(conn, readerId);
			
			if (r == null)
				return;
			
			// now update the in-memory object for this reader
			ReaderInfo ri = ConfigOptions.INSTANCE.getReadersInfo().getReader(readerId);
			
			if (ri == null)
				return;
			
			synchronized(ri)
			{
				ri.setGain(r.getGain());				
				boolean notifyReader = ri.getSignalStrengthThreshold() != r.getSignalStrengthThreshold(); 
				ri.setSignalStrengthThreshold(r.getSignalStrengthThreshold());
				
				// If the signal strength threshold changed then we need to notify the reader.
				if (!notifyReader)
					return;
				
				if (!ri.isEnabled() || XBRCController.getInstance().getHaStatus().equals("unknown") 
						|| XBRCController.getInstance().getHaStatus().equals("slave"))
					return;
					
				logger.info("The ss threshold had changed. Notifying the reader " + ri.getName());
				sendTurnOnPostMode(ri, null);				
			}
		}
		catch (Exception e)
		{
			logger.error("Failed process /updatereadersignalconfig ", e);
		}
		finally
		{
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
			
			ResponseFormatter.return200(response);
		}
	}

	/*
	 * Send functions
	 */

	private void sendSetReaderName(ReaderInfo rdr)
	{
		String sURL = "http://" + rdr.getIpAddress() + ":" + rdr.getPort();
		sURL += "/reader/name?name=" + rdr.getName();
		logger.trace("Sending reader name: " + sURL);
		sendPost(sURL, "", ConfigOptions.INSTANCE.getControllerInfo()
				.getReaderHttpTimeout());
	}

	private String getXbrcIP(ReaderInfo rdr)
	{
		Collection<String> ownIP = null;
		try
		{
			String ipPrefix = ConfigOptions.INSTANCE.getControllerInfo()
					.getOwnIpPrefix();
			if (rdr.getIpAddress() != null
					&& !rdr.getIpAddress().startsWith("127"))
			{
				// Lets try to talk to the Reader on the same network as the
				// reader contacted us on.
				// We should be listening on all interfaces so this should work.
				String[] ips = rdr.getIpAddress().split("\\.");
				if (ips.length == 4)
					ipPrefix = ips[0] + "." + ips[1];
			}
			ownIP = NetInterface.getOwnIpAddress(ipPrefix);

			// if nothing with the desired prefix, go for anything at all!
			if (ownIP.isEmpty())
				ownIP = NetInterface.getOwnIpAddress(null);
		}
		catch (SocketException e)
		{
			logger.error("Exception while trying to get own IP address", e);
			return null;
		}

		if (ownIP.isEmpty())
		{
			logger.error("Failed to get own IP address with prefix "
					+ ConfigOptions.INSTANCE.getControllerInfo()
							.getOwnIpPrefix()
					+ " Not sending update_stream message to the reader.");
			return null;
		}

		return ownIP.iterator().next();
	}

	private void sendTurnOnPostMode(ReaderInfo rdr, HelloMsg hello)
	{
		// NOTE: the hello parameter can be null
		
		StringBuffer sURL = new StringBuffer(rdr.getURL());

		// delete any events
		// sendDelete(sURL + "/events");
		
		// if a vip address has been configured, use that, else try to determine our
		// own IP address
		
		String ownIP = null;
		
		String vipAddress = ConfigOptions.INSTANCE.getControllerInfo().getVipAddress();
		if (vipAddress!=null && vipAddress.length()>0 && !vipAddress.startsWith("#"))
		{
			ownIP = vipAddress;
		}
		else
		{
			ownIP = getXbrcIP(rdr);
		}

		// now send the POST to enter push mode
		int nHttpPort = ConfigOptions.INSTANCE.getControllerInfo().getHttpPort();
		int nHttpsPort = ConfigOptions.INSTANCE.getControllerInfo().getHttpsPort();
		if (nHttpPort!=0)
		{
			sURL.append("/update_stream?url=http://")
				.append(ownIP)
				.append(":")
				.append(Integer.toString(nHttpPort) + "/stream");
		}
		else if (nHttpsPort!=0)
		{
			sURL.append("/update_stream?url=https://")
				.append(ownIP)
				.append(":")
				.append(Integer.toString(nHttpsPort) + "/stream");
		}
		
		// tack on the min signal strength
		if (!bCalibrate)
			sURL.append("&min-ss=").append(rdr.getSignalStrengthThreshold());

		if (hello != null)
		{
			// if lNextId is not equal to 0 and if the reader's last id is set, tack
			// on the "after" parameter
			long lAfterArg = 0;
	        long readerEventBufferSize = ConfigOptions.INSTANCE.getControllerInfo().getReaderEventBufferSize();
			if (rdr.getLastIDReceived() != -1)
			{
				// typical, no wrap case
				 if (hello.getNextEno() > rdr.getLastIDReceived())
				 {
					// if there are too many queued messages, though, don't go back too
					// far. For now,
					// let the maximum be 1 second worth of 100 guests - about 200
					// events
					lAfterArg = rdr.getLastIDReceived();
					if ((hello.getNextEno() - lAfterArg) > readerEventBufferSize)
						lAfterArg = hello.getNextEno() - readerEventBufferSize;
				 }
				 else
				 {
					 // this can happen for two reasons. Either the eno has wrapped or
					 // for whatever reason, the reader has reset itself and started using small eno's
					 // Since we don't know what case we have, just don't do anything with the after argument
				 }
	
			}
			else
			{
				// if more than 200 events available, just get the last 200
				if (hello.getNextEno() > readerEventBufferSize)
					lAfterArg = hello.getNextEno() - readerEventBufferSize;
			}
			if (lAfterArg>0)
				sURL.append("&after=").append(lAfterArg);
		}

		// set interval
		sURL.append("&interval=").append(ConfigOptions.INSTANCE.getControllerInfo()
						.getReaderDataSendPeriod());
		
		if (ReaderType.supportsMultipleStreams(rdr.getType()))
		{
			if (!checkHttpRetCode(ReaderApi.sendDelete(rdr.getURL() + "/update_stream")))
				logger.error("Failed to send DELETE update_stream to the reader.");
		}		
		
		if (ReaderType.supportsMultipleStreams(rdr.getType()) && ConfigOptions.INSTANCE.getControllerInfo().getReaderUpdateStreams() != null)
		{
			int i = 2;
			for (String streamUrl : ConfigOptions.INSTANCE.getControllerInfo().getReaderUpdateStreams()) 
			{
				sURL.append("&url");
				sURL.append(i++);
				sURL.append("=");
				sURL.append(streamUrl);
			}
		}
		
		logger.debug("Sending updatestream message: " + sURL.toString());
		if (!checkHttpRetCode(sendPost(sURL.toString(), "", ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamHttpTimeout())))
			rdr.setSentHelloSettings(false);
	}

	private int sendPost(String sUrl, String sData, int msecTimeout)
	{
        OutputStreamWriter wr = null;
        OutputStream osw = null;

		try
		{
			URL url = new URL(sUrl);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("POST");
			httpCon.setConnectTimeout(msecTimeout);
			httpCon.setReadTimeout(msecTimeout);
            osw = httpCon.getOutputStream();
			wr = new OutputStreamWriter(osw);
			wr.write(sData);
			wr.flush();

			int code = httpCon.getResponseCode();
			if (!checkHttpRetCode(code))
				logger.warn("Received " + code + " return code in response to: " + sUrl);
			
			return code;
		} 
		catch (MalformedURLException e)
		{
			logger.error(ExceptionFormatter.format(
					"Bad URL when talking to reader. Url: " + sUrl, e));
			
			return 0;
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"IO error when talking to reader. Url: " + sUrl, e));
			
			return 0;
		}
        finally 
        {
            if(wr != null) 
            {
                try 
                {
                    wr.close();
                }
                catch(Exception ignore) 
                {
                }
            }

            if(osw != null) 
            {
                try 
                {
                    osw.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }
	}

	/*
	 * Formatters
	 */

	private String formatGuestStatus(String sGuestId)
	{

		try
		{
			String sStatus = Processor.INSTANCE.getModel().serializeStateToXML(sGuestId);
			return sStatus;
		}
		catch (Exception e1)
		{
			logger.error(ExceptionFormatter.format(
					"Error reading messages from database", e1));
			return "";
		}
	}
	
	private void Indent(StringBuilder sb, int cIndent)
	{
		for (int i = 0; i < cIndent; i++)
			sb.append("    ");
	}

	protected String serializeCurrentConfiguration(String sName) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			// escape names in case there are any weird characters in it
			String venue = ConfigOptions.INSTANCE.getControllerInfo().getVenue(); 
			venue = StringEscapeUtils.escapeXml(venue);
			sName = StringEscapeUtils.escapeXml(sName);

			sb.append("<venue name=\""
					+ venue
					+ "\" " + "time=\"" + XbrcDateFormatter.formatTime(new Date().getTime())
					+ "\">\n");

			// copy configuration information from database
			Indent(sb, 1);
			sb.append("<configuration name=\"" + sName + "\" type=\"full\">\n");
			Indent(sb, 2);
			sb.append("<description/>\n");
			Indent(sb, 2);
			sb.append("<properties>\n");
			conn = Controller.getInstance().getPooledConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT class, property, value from Config");
			while (rs.next())
			{
				Indent(sb, 3);
				sb.append("<property class=\"" + rs.getString("class")
						+ "\"  name=\"" + rs.getString("property") + "\">");
				sb.append(StringEscapeUtils.escapeXml(rs.getString("value")));
				sb.append("</property>\n");
			}
			Indent(sb, 2);
			sb.append("</properties>\n");

			// now include reader information
			Indent(sb, 2);
			sb.append("<readerlocationinfo>\n");
			for (LocationInfo lif : ConfigOptions.INSTANCE.getLocationInfo()
					.values())
			{
				sb.append(formatReaderLocation(3, lif));
			}

			Indent(sb, 2);
			sb.append("</readerlocationinfo>\n");

			// and facility designer info
			Indent(sb, 2);
			sb.append("<griditems>\n");
			sb.append(formatGridItems(3));
			Indent(sb, 2);
			sb.append("</griditems>\n");
			
			// Images
			sb.append(formatImages(3));
			
			// Scheduler Settings
			List<SchedulerItem> schedulerItems = SchedulerItemService.findAll(conn);
			SchedulerSettings schedulerSettings = new SchedulerSettings();
			schedulerSettings.setItems(schedulerItems);
			String xml = XmlUtil.convertToPartialXml(schedulerSettings, SchedulerSettings.class);
			sb.append(xml);

			// now allow the model to store any additional model specific
			// information
			Indent(sb, 2);
			sb.append("<model>\n");
			sb.append(Processor.INSTANCE.getModel().storeConfiguration(3));
			Indent(sb, 2);
			sb.append("</model>\n");
			Indent(sb, 1);
			sb.append("</configuration>\n");
			sb.append("</venue>\n");
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch (Exception ex)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}

		return sb.toString();
	}

	private String formatGridItems(int nTabLevel) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * from GridItem");
			while (rs.next())
			{
				// fetch data
				int id = rs.getInt("id");
				String sItemType = rs.getString("ItemType");
				int xGrid = rs.getInt("XGrid");
				int yGrid = rs.getInt("YGrid");
				String sState = rs.getString("State");
				String sLabel = rs.getString("Label");
				String sDescription = rs.getString("Description");
				String sImage = rs.getString("Image");
				int nSequence = rs.getInt("Sequence");
				int xPassOnly = rs.getInt("XPassOnly");
				Integer nLocationId = rs.getInt("LocationId"); // BOXED TYPE!

				Indent(sb, nTabLevel);
				sb.append("<griditem id=\"" + id + "\" type=\"" + sItemType
						+ "\" xgrid=\"" + xGrid + "\" ygrid=\"" + yGrid
						+ "\">\n");
				Indent(sb, nTabLevel + 1);
				sb.append("<state>" + sState + "</state>\n");
				Indent(sb, nTabLevel + 1);
				sb.append("<label>" + sLabel + "</label>\n");
				Indent(sb, nTabLevel + 1);
				sb.append("<description>"
						+ StringEscapeUtils.escapeXml(sDescription)
						+ "</description>\n");
				Indent(sb, nTabLevel + 1);
				sb.append("<image>" + (sImage == null ? "" : sImage)
						+ "</image>\n");
				Indent(sb, nTabLevel + 1);
				sb.append("<sequence>" + nSequence + "</sequence>\n");
				Indent(sb, nTabLevel + 1);
				sb.append("<gueststoshow>" + xPassOnly + "</gueststoshow>\n");
				Indent(sb, nTabLevel + 1);
				sb.append("<locationid>"
						+ (nLocationId == null ? "" : nLocationId)
						+ "</locationid>\n");
				Indent(sb, nTabLevel);
				sb.append("</griditem>\n");
			}
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch (Exception ex)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}

		return sb.toString();
	}
	
	private String formatImages(int cIndent)
	{	
		Connection conn = null;
		try 
		{
			conn = Controller.getInstance().getPooledConnection();
			Images images = new Images();
			images.setImages(ImageService.getAllImages(conn));
			String xml = XmlUtil.convertToPartialXml(images, Images.class);
			return xml;
		}
		catch(Exception e)
		{
			logger.error("Failed to read Image objects from the database.", e);
		}
		finally
		{
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
		
		return "";
	}

	private String formatReaderLocation(int cIndent, LocationInfo linfo)
	{
		StringBuilder sb = new StringBuilder();
		Indent(sb, cIndent);
		sb.append("<readerlocation>\n");
		Indent(sb, cIndent + 1);
		sb.append("<locationid>" + linfo.getLocationId() + "</locationid>\n");
		Indent(sb, cIndent + 1);
		sb.append("<name>" + linfo.getName() + "</name>\n");
		Indent(sb, cIndent + 1);
		sb.append("<id>" + linfo.getId() + "</id>\n");
		Indent(sb, cIndent + 1);
		sb.append("<type>" + linfo.getLocationTypeID() + "</type>\n");
		Indent(sb, cIndent + 1);
		sb.append("<x>" + linfo.getX() + "</x>\n");
		Indent(sb, cIndent + 1);
		sb.append("<y>" + linfo.getX() + "</y>\n");
		if (linfo.isUseSecureId() != null)
		{
			Indent(sb, cIndent + 1);
			sb.append("<usesecureid>" + linfo.isUseSecureId() + "</usesecureid>\n");
		}
		Indent(sb, cIndent + 1);
		sb.append("<successsequence>" + (linfo.getSuccessSequence() != null ? linfo.getSuccessSequence() : "") + "</successsequence>\n");
		Indent(sb, cIndent + 1);
		sb.append("<successtimeout>" + linfo.getSuccessTimeout() + "</successtimeout>\n");
		Indent(sb, cIndent + 1);
		sb.append("<failuresequence>" + (linfo.getFailureSequence() != null ? linfo.getFailureSequence() : "") + "</failuresequence>\n");
		Indent(sb, cIndent + 1);
		sb.append("<failuretimeout>" + linfo.getFailureTimeout() + "</failuretimeout>\n");
		Indent(sb, cIndent + 1);
		sb.append("<errorsequence>" + (linfo.getErrorSequence() != null ? linfo.getErrorSequence() : "") + "</errorsequence>\n");
		Indent(sb, cIndent + 1);
		sb.append("<errortimeout>" + linfo.getErrorTimeout() + "</errortimeout>\n");
		Indent(sb, cIndent + 1);
		sb.append("<idlesequence>" + (linfo.getIdleSequence() != null ? linfo.getIdleSequence() : "") + "</idlesequence>\n");
		Indent(sb, cIndent + 1);
		sb.append("<tapsequence>" + (linfo.getTapSequence() != null ? linfo.getTapSequence() : "") + "</tapsequence>\n");
		Indent(sb, cIndent + 1);
		sb.append("<taptimeout>" + linfo.getTapTimeout() + "</taptimeout>\n");
		Indent(sb, cIndent + 1);
		sb.append("<transmitzonegroup>" + (linfo.getTransmitZoneGroup() != null ? linfo.getTransmitZoneGroup() : "") + "</transmitzonegroup>\n");
		Indent(sb, cIndent + 1);
		sb.append("<sendbandstatus>" + (linfo.getSendBandStatus() != null ? linfo.getSendBandStatus() : "false") + "</sendbandstatus>\n");
		Indent(sb, cIndent + 1);
		
		if (linfo.getTransmitCommands()!=null)
		{
			try
			{
				XbrBandCommands commands = new XbrBandCommands();
				commands.setListCommands(linfo.getTransmitCommands());
				String xml = XmlUtil.convertToPartialXml(commands, XbrBandCommands.class);
				Indent(sb, cIndent + 1);
				sb.append(xml);
			}
			catch (Exception e)
			{
				logger.error("Failed to serialize xBand transmit commands to xml for location " + linfo.getName());
			}
		}
		
		formatReaders(sb, cIndent + 1, linfo);
		Indent(sb, cIndent);
		sb.append("</readerlocation>\n");
		return sb.toString();
	}

	private void formatReaders(StringBuilder sb, int cIndent, LocationInfo linfo)
	{
		Indent(sb, cIndent);
		sb.append("<readers>\n");
		synchronized (ConfigOptions.INSTANCE.getReadersInfo())
		{
			for (ReaderInfo ri : ConfigOptions.INSTANCE.getReadersInfo()
					.getReaders())
			{
				if (ri.getLocation() != linfo)
					continue;
	
				String sType = ri.getType().getType();
	
				Indent(sb, cIndent + 1);
				sb.append("<reader>\n");
				Indent(sb, cIndent + 2);
				sb.append("<name>" + ri.getName() + "</name>\n");
				Indent(sb, cIndent + 2);
				sb.append("<id>" + ri.getId() + "</id>\n");
				Indent(sb, cIndent + 2);
				sb.append("<type>" + sType + "</type>\n");
				Indent(sb, cIndent + 2);
				if (ri.getHardwareType() != null)
				{
					sb.append("<hardwareType>" + ri.getHardwareType() + "</hardwareType>\n");
					Indent(sb, cIndent + 2);
				}
				sb.append("<macaddress>" + ri.getMacAddress() + "</macaddress>\n");
				Indent(sb, cIndent + 2);
				sb.append("<ipaddress>" + ri.getIpAddress() + "</ipaddress>\n");
				Indent(sb, cIndent + 2);
				sb.append("<port>" + ri.getPort() + "</port>\n");
				Indent(sb, cIndent + 2);
				sb.append("<gain>" + ri.getGain() + "</gain>\n");
				Indent(sb, cIndent + 2);
				sb.append("<threshold>" + ri.getSignalStrengthThreshold()
						+ "</threshold>\n");
				Indent(sb, cIndent + 2);
				sb.append("<lane>" + ri.getLane() + "</lane>\n");
				Indent(sb, cIndent + 2);
				sb.append("<deviceid>" + ri.getDeviceId() + "</deviceid>\n");
				Indent(sb, cIndent + 2);
				sb.append("<x>" + ri.getX() + "</x>\n");
				Indent(sb, cIndent + 2);
				sb.append("<y>" + ri.getY() + "</y>\n");
				Indent(sb, cIndent + 2);
				sb.append("<timelasthello>" + ri.getTimeLastHello()
						+ "</timelasthello>\n");
				Indent(sb, cIndent + 2);
				sb.append("<version>" + ri.getVersion() + "</version>\n");
				Indent(sb, cIndent + 2);
				sb.append("<minxbrcversion>" + ri.getMinXbrcVersion()
						+ "</minxbrcversion>\n");
				Indent(sb, cIndent + 2);
				sb.append("<istransmitter>" + ri.isTransmitter() + "</istransmitter>\n");
				Indent(sb, cIndent + 2);
				sb.append("<enabled>" + ri.isEnabled() + "</enabled>\n");
				Indent(sb, cIndent + 2);
				sb.append("<disabledReason>" + (ri.getDisabledReason() == null ? "" : ri.getDisabledReason()) + "</disabledReason>\n");
				Indent(sb, cIndent + 2);
				sb.append("<transmitPayload>" + ri.getTransmitPayload() + "</transmitPayload>\n");
				Indent(sb, cIndent + 2);
				sb.append("<transmitterHaPriority>" + ri.getTransmitterHaPriority() + "</transmitterHaPriority>\n");
				Indent(sb, cIndent + 2);
				sb.append("<bioDeviceType>" + ri.getBioDeviceType() + "</bioDeviceType>\n");
	
				if (ri.getBatteryLevel() != null) {
					Indent(sb, cIndent + 2);
					sb.append("<batteryLevel>" + ri.getBatteryLevel() + "</batteryLevel>\n");
				}
	
				if (ri.getBatteryTime() != null) {
					Indent(sb, cIndent + 2);
					sb.append("<batteryTime>" + ri.getBatteryTime() + "</batteryTime>\n");
				}
				
				if (ri.getAntennas() != null)
				{
					List<Boolean> antennas = ri.getAntennas();
					if (antennas.size() > 0)
					{
						Indent(sb, cIndent + 2);
						sb.append("<antennas type=\"array\">\n");
						for (int i = 0; i < antennas.size(); i++)
						{
							Boolean power = antennas.get(i);
							String value = "null";
							if (Boolean.TRUE.equals(power))
								value = "true";
							else if (Boolean.FALSE.equals(power))
								value = "false";
							
							Indent(sb, cIndent + 3);
							sb.append("<value>" + value + "</value>\n");
						}
						Indent(sb, cIndent + 2);
						sb.append("</antennas>\n");
					}
				}
	
				Indent(sb, cIndent + 1);
				sb.append("</reader>\n");
			}
		}
		Indent(sb, cIndent);
		sb.append("</readers>\n");
	}

	private String formatGuestMessages(String sGuestId, long lAfter, int nMax)
	{
		// iterate through database looking for messages
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			dbConn = Controller.getInstance().getPooledConnection();

            // filter out?
            String sGuestFilter = null;
            if (sGuestId != null)
            {
                sGuestFilter =  "<guestid>" + sGuestId + "</guestid>";
            }

            // If -1 is specified as the start, just show the most
            // recent messages. If an offset is specified, use SQL
            // to filter out the messages.
            // TODO: Rewrite this with prepared statements. This is
            // a SQL Injection vulnerability
            String sSQL;
            if ( lAfter == -1 )
            {
                sSQL = "SELECT * FROM Messages";
                if ( sGuestFilter != null )
                    sSQL += " WHERE Payload LIKE '%" + sGuestFilter + "%'";
                sSQL += " ORDER BY id DESC";
                if ( nMax > 0 )
                    sSQL += " LIMIT " + nMax;
            }
            else
            {
                sSQL = "SELECT * FROM Messages WHERE id > " + lAfter;
                if ( sGuestFilter != null )
                    sSQL += " AND Payload LIKE '%" + sGuestFilter + "%' ORDER BY id DESC";
                if ( nMax > 0 )
                    sSQL += " LIMIT " + nMax;
            }

			stmt = dbConn.createStatement();
			stmt.execute(sSQL);
			rs = stmt.getResultSet();
			
			// escape name in case there are any weird characters in it
			String venue = ConfigOptions.INSTANCE.getControllerInfo().getVenue();
			venue = StringEscapeUtils.escapeXml(venue);

			StringBuilder sb = new StringBuilder();
			sb.append("<venue name=\""
					+ venue
					+ "\" " + "time=\"" + XbrcDateFormatter.formatTime(new Date().getTime())
					+ "\">\n");
			while (rs.next())
			{
				String sMessagePayload = rs.getString("Payload");
				long id = rs.getLong("id");

				// inject message sequence
				// TODO: consider a better way - this is pretty sucky.
				String sSeq = "    <sequence>" + id + "</sequence>\n    ";
				sMessagePayload = sMessagePayload.replaceFirst("</message>",
						sSeq + "</message>");
				sb.append(sMessagePayload);
			}
			sb.append("</venue>");

			return sb.toString();
		}
		catch (Exception e1)
		{
			logger.error(ExceptionFormatter.format(
					"Error reading messages from database", e1));
			return "";
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}

	/*
	 * Helper functions
	 */

	private void storeConfiguration(String sName, String sDescription, boolean bCreate, String sPutData,
                                    Response response, String updateBy)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        PrintStream body = null;

		try
		{
			// XML-ify the posted data to check for formatting and to grab key
			// fields
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(sPutData)));

			// walk through the tree
			Element el = doc.getDocumentElement();

			if (el.getNodeName().compareTo("venue") != 0)
			{
				ResponseFormatter.return500(response, "Configuration is missing venue element");
				return;
			}

			// find the configuration element
			NodeList nl = el.getElementsByTagName("configuration");
			if (nl == null || nl.getLength() != 1)
			{
				ResponseFormatter.return500(response, "XML must contain exactly one configuration element");
				return;
			}

			// get the name
			Node nodeConfig = nl.item(0);
			if (nodeConfig == null || nodeConfig.getNodeType() != Node.ELEMENT_NODE)
			{
				ResponseFormatter.return500(response,
						"XML must contain proper configuration element");
				return;
			}
			Node nodeAttr;
			if (sName==null)
			{
				nodeAttr = nodeConfig.getAttributes().getNamedItem("name");
				if (nodeAttr == null
						|| nodeAttr.getNodeType() != Node.ATTRIBUTE_NODE)
				{
					ResponseFormatter.return500(response,"Must specify a configuration name in the URL or XML");
					return;
				}
				sName = nodeAttr.getNodeValue();
			}
			
			nodeAttr = nodeConfig.getAttributes().getNamedItem("type");
			if (nodeAttr == null
					|| nodeAttr.getNodeType() != Node.ATTRIBUTE_NODE)
			{
				ResponseFormatter.return500(response,
						"Configuration element is missing type attribute");
				return;
			}
			String sType = nodeAttr.getNodeValue();
			if (sType == null
					|| ((sType.compareTo("full") != 0) && (sType.compareTo("incremental") != 0)))
			{
				ResponseFormatter.return500(response,
						"Invalid configuration type (must be \"full\" or \"incremental\")");
				return;
			}
			
			// now find the description element
			if (sDescription==null)
			{
				nl = ((Element) nodeConfig).getElementsByTagName("description");
				if (nl == null )
				{
					ResponseFormatter.return500(response,
							"Must specify a configuration description in the URL or XML");
					return;
				}
				sDescription = "";
				if (nl.getLength()>0)
				{
					Node nodeDesc = nl.item(0);
					if (nodeDesc == null || nodeDesc.getNodeType() != Node.ELEMENT_NODE)
					{
						ResponseFormatter.return500(response,
								"Must specify a configuration description in the URL or XML");
						return;
					}
					sDescription = nodeDesc.getTextContent();
				}
			}

			// if the name of the configuration is "current", then don't store it, just effect it!
			if (sName.equals("current"))
			{
				switchToConfiguration(sName, sPutData, updateBy);
				ResponseFormatter.return200(response);
				return;
			}

			// have what we need store in database
			conn = Controller.getInstance().getPooledConnection();
			
			if (bCreate)
			{
				pstmt = conn.prepareStatement(
								"INSERT INTO StoredConfigurations(name, description, model, xml) VALUES (?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, sName);
				pstmt.setString(2, sDescription);
				pstmt.setString(3, Processor.INSTANCE.getModel().getClass().getName()
						.toString());
				pstmt.setString(4, sPutData);
				if (pstmt.executeUpdate() != 1)
				{
					ResponseFormatter.return500(response, "Adding new configuration failed");
				}

                configLogger.trace("New Configuration Added. UpdateBy= " + updateBy + ", Name=" + sName + ", xml=" + sPutData);

				// fetch the new id
				rs = pstmt.getGeneratedKeys();
				ResultSetMetaData rsmd = rs.getMetaData();
				int id = -1;
				if (rsmd.getColumnCount() == 1)
				{
					if (rs.next())
					{
						id = rs.getInt(1);
					}
				}

				// return it
				body = response.getPrintStream();
				body.println(id);
				ResponseFormatter.setResponseHeader(response, "text/plain");
				response.setCode(200);
			}
			else
			{
				pstmt = conn.prepareStatement(
						"UPDATE StoredConfigurations set description=?,xml=? WHERE name=? AND internal=?");
				pstmt.setString(1, sDescription);
				pstmt.setString(2, sPutData);
				pstmt.setString(3, sName);
				pstmt.setBoolean(4, false);
				if (pstmt.executeUpdate() != 1)
				{
					ResponseFormatter.return500(response, "Updating configuration failed");
				}

                configLogger.trace("Configuration Updated. UpdateBy= " + updateBy + ", Name=" + sName + ", xml=" + sPutData);

				// return
				ResponseFormatter.return200(response);
			}
		}
		catch (Exception e)
		{
			String sError;
			if (bCreate)
				sError = ExceptionFormatter.format("Error POST /newconfiguration", e);
			else
				sError = ExceptionFormatter.format("Error PUT /configuration", e);
				
			logger.error(sError);
			ResponseFormatter.return500(response, sError);
		}
		finally
		{
            if(body != null) 
            {
                try 
                {
                    body.close();
                }
                catch(Exception ignore) 
                {
                }
            }

			try
			{
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
			}
			catch (Exception ex)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
		
	}
	
	public void switchToConfiguration(String sName, String sXML, String updateBy)
			throws Exception
	{
		// parse the xml so that we can process it more easily
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(sXML)));

		// walk through the tree
		Element el = doc.getDocumentElement();

		if (el.getNodeName().compareTo("venue") != 0)
			throw new Exception("invalid stored configuration: missing venue element");

		NodeList nl = el.getElementsByTagName("configuration");
		if (nl == null || nl.getLength() != 1 || nl.item(0)==null)
			throw new Exception("invalid stored configuration: must have exactly one configuration element");
		Node nodeConf = nl.item(0);
		Node nodeAttr = nodeConf.getAttributes().getNamedItem("type");
		if (nodeAttr == null || nodeAttr.getNodeType() != Node.ATTRIBUTE_NODE)
			throw new Exception("invalid stored configuration: improper configuration element");

		// zap the current configuration if the type is "full"
		boolean bFull = false;
		if (nodeAttr.getTextContent().compareTo("full") == 0)
			bFull = true;

		Connection conn = null;
		Statement stmt = null;
		int transactionIsolation = -1;
		Savepoint savepoint = null;
        try
        {
            // get a database connection
            conn = Controller.getInstance().getPooledConnection();            
        	
    		//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			savepoint = conn.setSavepoint();			
            
            stmt = conn.createStatement();

            // process child nodes
            nl = nodeConf.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++)
            {
                Node n2 = nl.item(i);

                // process elements
                if (n2.getNodeType() == Node.ELEMENT_NODE)
                {
                    // process the config table
                    if (n2.getNodeName().compareTo("properties") == 0)
                    {
                        if (bFull)
                        {
                        	Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "Config", null);
                            stmt.execute("DELETE FROM Config");                            
                        }
                        processProperties(conn, n2, !bFull, updateBy);
                    }
                    else if (n2.getNodeName().compareTo("readerlocationinfo") == 0)
                    {
                    	if (bFull)
                    	{
	                    	Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "TransmitCommand", null);
	            			stmt.execute("DELETE FROM TransmitCommand");   
	            			Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "TransmitRecipients", null);
	            			stmt.execute("DELETE FROM TransmitRecipients");
	            			Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "Location", null);
	            			stmt.execute("DELETE FROM Location");
	            			Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "ReaderAntenna", null);
	            			stmt.execute("DELETE FROM ReaderAntenna");
	            			Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "Reader", null);
	                		stmt.execute("DELETE FROM Reader");                       
                    	}
                    	
                        processReaderLocationInfo(conn, n2, !bFull, updateBy);
                    }
                    else if (n2.getNodeName().compareTo("griditems") == 0)
                    {
                        if (bFull)
                        {
                        	Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "GridItem", null);
                            stmt.execute("DELETE FROM GridItem");
                        }
                        processGridItems(conn, n2, !bFull);
                    }
                    else if (n2.getNodeName().compareTo("images") == 0)
                    {
                        if (bFull)
                        {
                        	Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "ImageBlob", null);
                        	stmt.execute("DELETE FROM ImageBlob");
                        	Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "Image", null);
                            stmt.execute("DELETE FROM Image");                                                
                        }
                        processImages(conn, n2, !bFull);
                    }    
                    else if (n2.getNodeName().compareTo("schedulerSettings") == 0)
                    {
                    	if (bFull)
                    	{
                    		Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "SchedulerItemParameter", null);
                    		stmt.execute("DELETE FROM SchedulerItemParameter");
                    		Processor.INSTANCE.getModel().beforeConfigurationDeleteFromTable(conn, "SchedulerItem", null);
                    		stmt.execute("DELETE FROM SchedulerItem");
                    	}
                    	processSchedulerSettings(conn, n2, !bFull, updateBy);
                    }
                    else if (n2.getNodeName().compareTo("model") == 0)
                    {
                        processModel(conn, n2);
                    }
                }
            }
            
            conn.commit();
		}
		catch (Exception ex)
		{	
			logger.error("Failed to switch to configuration " + sName, ex);
			if (conn != null && savepoint != null)
				conn.rollback(savepoint);
			throw ex;
		}
		finally
		{
			if (stmt != null)
				stmt.close();

			if (conn != null)	
			{	
				try
				{
					conn.setAutoCommit(true);
					if (transactionIsolation != -1)
						conn.setTransactionIsolation(transactionIsolation);
				}
				catch(Exception e)
				{
					logger.fatal("Failed to restore database connection auto commit functionality", e);
				}
				Controller.getInstance().releasePooledConnection(conn);
			}
		}
        
		// Now, tell the Processor to reload the configuration, but only if all the database stuff went well.
		Processor.INSTANCE.reReadConfiguration(null);
	}

	private void processProperties(Connection conn, Node node,
			boolean bPredelete, String updateBy) throws Exception
	{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtDelete = null;

		try
		{
			// prepare statement
			pstmt = conn
					.prepareStatement("INSERT INTO Config(class, property, value) VALUES (?,?,?)");
			if (bPredelete)
				pstmtDelete = conn
						.prepareStatement("DELETE FROM Config WHERE class=? AND property=?");

			NodeList nl = node.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node n2 = nl.item(i);

				// process elements
				if (n2.getNodeType() == Node.ELEMENT_NODE)
				{
					// process the config table
					if (n2.getNodeName().compareTo("property") == 0)
					{
						Node na = n2.getAttributes().getNamedItem("name");
						if (na == null
								|| na.getNodeType() != Node.ATTRIBUTE_NODE)
							throw new Exception(
									"malformed property element in stored configuration");

						String sName = na.getTextContent();

						na = n2.getAttributes().getNamedItem("class");
						if (na == null
								|| na.getNodeType() != Node.ATTRIBUTE_NODE)
							throw new Exception(
									"malformed property element in stored configuration");

						String sClass = na.getTextContent();
						String sValue = n2.getTextContent();
						
						if (sClass != null && sClass.equals("ControllerInfo"))
						{
							if (sName != null && sName.toLowerCase().equals("errortimeout_msec") && (sValue == null || sValue.trim().length() == 0))
							{
								// get the default
								Field f = ControllerInfo.class.getDeclaredField("errorTimeout");
								MetaData md = f.getAnnotation(MetaData.class);
								sValue = md.defaultValue();
							}
							else if (sName != null && sName.toLowerCase().equals("failuretimeout_msec") && (sValue == null || sValue.trim().length() == 0))
							{
								// get the default
								Field f = ControllerInfo.class.getDeclaredField("failureTimeout");
								MetaData md = f.getAnnotation(MetaData.class);
								sValue = md.defaultValue();
							}
							else if (sName != null && sName.toLowerCase().equals("successimeout_msec") && (sValue == null || sValue.trim().length() == 0))
							{
								// get the default
								Field f = ControllerInfo.class.getDeclaredField("successTimeout");
								MetaData md = f.getAnnotation(MetaData.class);
								sValue = md.defaultValue();
							}
						}

						// pre-delete if requested
						if ((pstmtDelete != null) && bPredelete)
						{
							pstmtDelete.clearParameters();
							pstmtDelete.setString(1, sClass);
							pstmtDelete.setString(2, sName);
							pstmtDelete.executeUpdate();
						}

						// poke these into the database
						pstmt.clearParameters();
						pstmt.setString(1, sClass);
						pstmt.setString(2, sName);
						pstmt.setString(3, sValue);
						pstmt.executeUpdate();

                        configLogger.trace("Configuration Updated. UpdateBy= " + updateBy +
                                ", Class=" + sClass +
                                ", Name=" + sName +
                                ", Value=" + sValue);
					}
					else
						throw new Exception(
								"Unexpected node type in stored configuration: "
										+ n2.getNodeName());
				}
			}

		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
				if (pstmtDelete != null)
					pstmtDelete.close();
			}
			catch (Exception ex2)
			{
			}
		}

	}

	private void processReaderLocationInfo(Connection conn, Node node,
			boolean bPreDelete, String updateBy) throws Exception
	{
		boolean foundUnknown = bPreDelete;
		
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n2 = nl.item(i);

			// process elements
			if (n2.getNodeType() == Node.ELEMENT_NODE)
			{
				if (n2.getNodeName().compareTo("readerlocation") == 0)
					foundUnknown |= processReaderLocation(conn, n2, bPreDelete, updateBy).equals("UNKNOWN");
				else
					throw new Exception(
							"Unexpected node type in stored configuration: "
									+ n2.getNodeName());
			}
		}
		
		if (!foundUnknown)
			throw new Exception("Invalid stored configuration. The UNKNOWN location is missing.");
	}

	private String processReaderLocation(Connection conn, Node node,
			boolean bPreDelete, String updateBy) throws Exception
	{
		Statement stmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtDelete = null;

		try
		{
			// prepare statements

			stmt = conn.createStatement();
			pstmt = conn
					.prepareStatement(
							"INSERT INTO Location(id, name, locationId, locationTypeId, x, y, singulationTypeId, eventGenerationTypeId," +
							"	useSecureId, successSeq, successTimeout, failureSeq, failureTimeout, errorSeq, errorTimeout, tapSeq, tapTimeout, idleSeq, transmitZoneGroup, sendBandStatus) " +
							"VALUES (?,?,?,?,?,?,0,0,?,?,?,?,?,?,?,?,?,?,?,?)",
							Statement.RETURN_GENERATED_KEYS);

			if (bPreDelete)
				pstmtDelete = conn.prepareStatement("DELETE FROM Location WHERE id=?");

			NodeList nl = node.getChildNodes();

			Node nodeTransmitCommands = null;
			int id = -1;
			String sLocationId = null;
			String sName = null;
			int nLocationTypeId = -1;
			Node nodeReaders = null;
			double x = 0.0;
			double y = 0.0;
			Boolean useSecureId = null;
			Boolean sendBandStatus = false;
			
			String successSequence = null;
			Field f = ControllerInfo.class.getDeclaredField("successTimeout");
			MetaData md = f.getAnnotation(MetaData.class);
			Long successTimeout = Long.valueOf(md.defaultValue());
		
			String failureSequence = null;
			f = ControllerInfo.class.getDeclaredField("failureTimeout");
			md = f.getAnnotation(MetaData.class);
			Long failureTimeout = Long.valueOf(md.defaultValue());
			
			String errorSequence = null;
			f = ControllerInfo.class.getDeclaredField("errorTimeout");
			md = f.getAnnotation(MetaData.class);
			Long errorTimeout = Long.valueOf(md.defaultValue());
			
			String tapSequence = null;
			f = ControllerInfo.class.getDeclaredField("tapTimeout");
			md = f.getAnnotation(MetaData.class);
			Long tapTimeout = Long.valueOf(md.defaultValue());
			
			String transmitZoneGroup = null;
			
			String idleSequence = null;
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node n2 = nl.item(i);

				// process elements
				if (n2.getNodeType() == Node.ELEMENT_NODE)
				{
					// process the config table
					if (n2.getNodeName().compareTo("section") == 0 || n2.getNodeName().compareTo("locationid") == 0)
						sLocationId = n2.getTextContent();
					else if (n2.getNodeName().compareTo("name") == 0)
						sName = n2.getTextContent();
					else if (n2.getNodeName().compareTo("type") == 0)
						nLocationTypeId = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("readers") == 0)
						nodeReaders = n2;
					else if (n2.getNodeName().compareTo("x") == 0)
						x = Double.parseDouble(n2.getTextContent());
					else if (n2.getNodeName().compareTo("y") == 0)
						y = Double.parseDouble(n2.getTextContent());
					else if (n2.getNodeName().compareTo("id") == 0)
						id = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().toLowerCase().compareTo("usesecureid") == 0)
					{
						if (n2.getTextContent() != null && n2.getTextContent().trim().length() > 0)
							if (!"null".equalsIgnoreCase(n2.getTextContent()))
								useSecureId = Boolean.parseBoolean(n2.getTextContent());
					}
					else if (n2.getNodeName().toLowerCase().compareTo("successsequence") == 0)
						successSequence = n2.getTextContent();
					else if (n2.getNodeName().toLowerCase().compareTo("successtimeout") == 0)
					{
						if (n2.getTextContent() != null && n2.getTextContent().trim().length() > 0)
							successTimeout = Long.parseLong(n2.getTextContent());
					}
					else if (n2.getNodeName().toLowerCase().compareTo("failuresequence") == 0)
						failureSequence = n2.getTextContent();
					else if (n2.getNodeName().toLowerCase().compareTo("failuretimeout") == 0)
					{
						if (n2.getTextContent() != null && n2.getTextContent().trim().length() > 0)
							failureTimeout = Long.parseLong(n2.getTextContent());
					}
					else if (n2.getNodeName().toLowerCase().compareTo("errorsequence") == 0)
						errorSequence = n2.getTextContent();
					else if (n2.getNodeName().toLowerCase().compareTo("errortimeout") == 0)
					{
						if (n2.getTextContent() != null && n2.getTextContent().trim().length() > 0)
							errorTimeout = Long.parseLong(n2.getTextContent());
					}
					else if (n2.getNodeName().toLowerCase().compareTo("tapsequence") == 0)
							tapSequence = n2.getTextContent();
					else if (n2.getNodeName().toLowerCase().compareTo("taptimeout") == 0)
					{
						if (n2.getTextContent() != null && n2.getTextContent().trim().length() > 0)
							tapTimeout = Long.parseLong(n2.getTextContent());
					}
					else if (n2.getNodeName().toLowerCase().compareTo("idlesequence") == 0)
						idleSequence = n2.getTextContent();
					else if (n2.getNodeName().toLowerCase().compareTo("transmitzonegroup") == 0)
						transmitZoneGroup = n2.getTextContent();
					else if (n2.getNodeName().compareTo("transmitCommands") == 0)
						nodeTransmitCommands = n2;
					else if (n2.getNodeName().compareTo("sendbandstatus") == 0)
					{
						if (n2.getTextContent() != null && n2.getTextContent().trim().length() > 0)
							if (!"null".equalsIgnoreCase(n2.getTextContent()))
								sendBandStatus = Boolean.parseBoolean(n2.getTextContent());
					}
					else
						throw new Exception(
								"Unexpected node type in stored configuration: "
										+ n2.getNodeName());
				}
			}

            // validatenodeReaders
			if (id < 0 || sName == null || sLocationId == null
					|| nLocationTypeId < 0
                    || !validateReaderLocationName(sName)
                    || !validateSequenceName(successSequence)
                    || !validateSequenceName(failureSequence)
                    || !validateSequenceName(errorSequence)
                    || !validateSequenceName(tapSequence)
                    || !validateTransmitZoneGroup(transmitZoneGroup)
                    )
				throw new Exception(
						"Improper readerlocationinfo in stored configuration");
			
			if (sName.equals("UNKNOWN") && id != 0)
				throw new Exception("Invalid stored configuration. The UNKNOWN location id must be 0.");

			// disable auto behavior
			stmt.execute("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'");

			if (bPreDelete && (pstmtDelete != null))
			{
				pstmtDelete.setInt(1, id);
				pstmtDelete.executeUpdate();
			}

			// create the location
			pstmt.setInt(1, id);
			pstmt.setString(2, Location.sanitizeName(sName));
			pstmt.setString(3, sLocationId);
			pstmt.setInt(4, nLocationTypeId);
			pstmt.setDouble(5, x);
			pstmt.setDouble(6, y);
			if (useSecureId == null)
				pstmt.setNull(7, Types.BOOLEAN);
			else
				pstmt.setBoolean(7, useSecureId);
			
			if (successSequence != null && successSequence.trim().isEmpty())
				successSequence = null;				
			if (failureSequence != null && failureSequence.trim().isEmpty())
				failureSequence = null;				 
			if (errorSequence != null && errorSequence.trim().isEmpty())
				errorSequence = null;
			if (idleSequence != null && idleSequence.trim().isEmpty())
				idleSequence = null;
			if (tapSequence != null && tapSequence.trim().isEmpty())
				tapSequence = null;
			
			pstmt.setString(8, successSequence);
			pstmt.setLong(9, successTimeout);
			pstmt.setString(10, failureSequence);
			pstmt.setLong(11, failureTimeout);
			pstmt.setString(12, errorSequence);
			pstmt.setLong(13, errorTimeout);
			pstmt.setString(14, tapSequence);
			pstmt.setLong(15, tapTimeout);
			pstmt.setString(16, idleSequence);
			pstmt.setString(17, transmitZoneGroup);
			pstmt.setBoolean(18, sendBandStatus);
			pstmt.executeUpdate();

			// re-enable
			stmt.execute("SET SQL_MODE=@OLD_SQL_MODE");

            configLogger.trace("Location Updated. UpdateBy= " + updateBy +
                    ", Id=" + id +
                    ", Name=" + Location.sanitizeName(sName) +
                    ", LocationId=" + sLocationId +
                    ", LocationTypeId=" + nLocationTypeId +
                    ", X=" + x +
                    ", Y=" + y +
                    ", UseSecureId=" + useSecureId +
                    ", SuccessSequence=" + successSequence +
                    ", FailureSequence=" + failureSequence +
                    ", ErrorSequence=" + errorSequence +
                    ", IdleSequence=" + idleSequence +
                    ", TapSequence=" + tapSequence +
                    ", transmitZoneGroup = " + transmitZoneGroup +
                    ", sendBandStatus = " + sendBandStatus
                );

            // now process band transmit commands
            processTransmitCommands(conn, id, nodeTransmitCommands, bPreDelete, updateBy);
         			
            // now process readers
			processReaders(conn, id, nodeReaders, bPreDelete, updateBy);
			
			return sName;
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
				if (pstmtDelete != null)
					pstmtDelete.close();
			}
			catch (Exception ex2)
			{
			}
		}

	}

	private void processReaders(Connection conn, int idLocation, Node node, boolean bPreDelete, String updateBy)
			throws Exception
	{
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n2 = nl.item(i);

			// process elements
			if (n2.getNodeType() == Node.ELEMENT_NODE)
			{
				// process the reader table
				if (n2.getNodeName().compareTo("reader") == 0)
					processReader(conn, idLocation, n2, bPreDelete, updateBy);
				else
					throw new Exception(
							"Unexpected node type in stored configuration: "
									+ n2.getNodeName());
			}
		}
	}

	private void processReaderAntenna(Connection conn, int readerId, int antenna, boolean power,
                                      String updateBy) throws Exception
	{
		Statement stmt = null;
		PreparedStatement pstmt = null;

		try
		{
			stmt = conn.createStatement();
			pstmt = conn.prepareStatement("INSERT INTO ReaderAntenna(readerId,antenna,power) VALUES (?,?,?)");

			// disable auto behavior
			stmt.execute("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'");

			pstmt.setLong(1, readerId);
			pstmt.setInt(2, antenna);
			pstmt.setBoolean(3, power);
			pstmt.executeUpdate();

            configLogger.trace("Reader Antenna Updated. UpdateBy= " + updateBy +
                    ", ReaderId=" + readerId +
                    ", Antenna=" + antenna +
                    ", Power=" + power);

            stmt.execute("SET SQL_MODE=@OLD_SQL_MODE");
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex2)
			{
			}
		}
		
	}
	
	private void processReaderAntennas(Connection conn, int readerId, Node antennasNode, boolean bPreDelete,
                                       String updateBy) throws Exception
	{
		List<Boolean> antennas = new ArrayList<Boolean>();
		NodeList antennaNodes = antennasNode.getChildNodes();
		for (int j = 0; j < antennaNodes.getLength(); j++)
		{
			Node antennaNode = antennaNodes.item(j);
			if (antennaNode.getNodeName().compareTo("value") == 0)
			{
				String text = antennaNode.getTextContent();
				antennas.add(Boolean.parseBoolean(text));
			}
		}

		Statement stmt = null;
		PreparedStatement pstmtDelete = null;
		if (bPreDelete) try
		{
			stmt = conn.createStatement();
			
			pstmtDelete = conn.prepareStatement("DELETE FROM ReaderAntenna WHERE readerId=?");
		
			// disable auto behavior
			stmt.execute("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'");

			pstmtDelete.setInt(1, readerId);
			pstmtDelete.executeUpdate();
			
			// re-enable
			stmt.execute("SET SQL_MODE=@OLD_SQL_MODE");
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
				if (pstmtDelete != null)
					pstmtDelete.close();
			}
			catch (Exception ex2)
			{
			}
		}
		
		for (int i = 0; i < antennas.size(); i++)
		{
			Boolean value = antennas.get(i);
			processReaderAntenna(conn, readerId, i, value, updateBy);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void processReader(Connection conn, int idLocation, Node node, boolean bPreDelete, String updateBy)
			throws Exception
	{
		Statement stmt = null;
		PreparedStatement pstmt = null;
		
		PreparedStatement pstmtDelete = null;
		
		ResultSet rs = null;

		try
		{
			// prepare statements
			stmt = conn.createStatement();
			pstmt = conn
					.prepareStatement("INSERT INTO Reader(id, readerId, type, hardwareType, signalStrengthThreshold, gain, macAddress, ipAddress, port, locationId, lane, deviceId, " + 
									  "timeLastHello, positionX, positionY, isTransmitter, transmitPayload, transmitterHaPriority, modelData, disabledReason, enabled, bioDeviceType, version, minXbrcVersion, " +
									  "lastReaderTestTime, lastReaderTestSuccess, lastReaderTestUser) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			if (bPreDelete)
				pstmtDelete = conn.prepareStatement("DELETE FROM Reader WHERE id=?");

			NodeList nl = node.getChildNodes();
						
			int id = -1;
			String sName = null;
			int nType = -1;
			String sMacAddress = null;
			String sIpAddress = null;
			int nPort = -1;
			int nThreshold = Integer.MIN_VALUE;
			double gain = 0.0;
			int nLane = 0;
			int nDeviceId = 0;
			Date dtNow = new Date();
			int x = 0, y = 0;
			boolean bIsTransmitter = false;
			String sTransmitPayload = "";
			int transmitterHaPriority = 1;
			String modelData = "";
			String disabledReason = "";
			boolean enabled = true;
			int bioDeviceType = 3;
			String sReaderVersion = "";
			String sMinXbrcVersion = "";
			Node antennasNode = null;
			String sHardwareType = null;
			String strLastReaderTestTime = "";
			Date lastReaderTestTime = null;
			boolean lastReaderTestSuccess = true;
			String lastReaderTestUser = null;
			
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node n2 = nl.item(i);

				// process elements
				if (n2.getNodeType() == Node.ELEMENT_NODE)
				{
					// process the reader table
					if (n2.getNodeName().compareTo("name") == 0)
						sName = n2.getTextContent();
					else if (n2.getNodeName().compareTo("type") == 0)
					{
						String sType = n2.getTextContent();
						ReaderType type = ReaderType.getByType(sType);
						if (type == ReaderType.undefined)
							type =  ReaderType.getByDescription(sType);
						if (type == ReaderType.undefined)
							type = ReaderType.getByOrdinal(Integer.parseInt(sType));
						if (type == ReaderType.undefined)
							throw new Exception("Unsupported reader type: " + sType);
						nType = type.ordinal();
					}
					else if (n2.getNodeName().compareTo("hardwareType") == 0)
						sHardwareType = n2.getTextContent();
					else if (n2.getNodeName().compareTo("macaddress") == 0)
						sMacAddress = n2.getTextContent();
					else if (n2.getNodeName().compareTo("ipaddress") == 0)
						sIpAddress = n2.getTextContent();
					else if (n2.getNodeName().compareTo("port") == 0)
						nPort = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("threshold") == 0)
						nThreshold = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("gain") == 0)
						gain = Double.parseDouble(n2.getTextContent());
					else if (n2.getNodeName().compareTo("lane") == 0)
						nLane = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("deviceid") == 0)
						nDeviceId = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("id") == 0)
						id = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("x") == 0)
						x = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("y") == 0)
						y = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("istransmitter")==0)
						bIsTransmitter = Boolean.parseBoolean(n2.getTextContent());
					else if (n2.getNodeName().toLowerCase().compareTo("transmitpayload")==0)
						sTransmitPayload = n2.getTextContent();
					else if (n2.getNodeName().compareTo("timelasthello")==0)			// ignore
						continue;
					else if (n2.getNodeName().compareTo("version")==0)
						sReaderVersion = n2.getTextContent();				
					else if (n2.getNodeName().compareTo("minxbrcversion")==0)
						sMinXbrcVersion = n2.getTextContent();
                    else if (n2.getNodeName().compareTo("minXbrcVersion")==0)
                    	sMinXbrcVersion = n2.getTextContent();                      
                    else if (n2.getNodeName().compareTo("status")==0)			// ignore
                        continue;
                    else if (n2.getNodeName().compareTo("statusMessage")==0)			// ignore
                        continue;                    
					else if (n2.getNodeName().compareTo("transmitterHaPriority") == 0)
						transmitterHaPriority = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("modelData") == 0)
						modelData = n2.getTextContent();
					else if (n2.getNodeName().compareTo("disabledReason") == 0)
						disabledReason = n2.getTextContent();
					else if (n2.getNodeName().compareTo("enabled") == 0)
						enabled = Boolean.parseBoolean(n2.getTextContent());
					else if (n2.getNodeName().compareTo("bioDeviceType") == 0)
						bioDeviceType = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("batteryLevel") == 0)
						continue;
					else if (n2.getNodeName().compareTo("batteryTime") == 0)
						continue;
					else if (n2.getNodeName().compareTo("temperature") == 0)
						continue;
					else if (n2.getNodeName().compareTo("antennas") == 0)
					{
						antennasNode = n2;
					}
					else if (n2.getNodeName().compareTo("lastReaderTestTime") == 0)
					{
						strLastReaderTestTime = n2.getTextContent();
						if (!strLastReaderTestTime.isEmpty())
							lastReaderTestTime = parseDate(strLastReaderTestTime);					
					}
					else if (n2.getNodeName().compareTo("lastReaderTestSuccess") == 0)
						lastReaderTestSuccess = Boolean.parseBoolean(n2.getTextContent());
					else if (n2.getNodeName().compareTo("lastReaderTestUser") == 0)
						lastReaderTestUser = n2.getTextContent();
					else if (n2.getNodeName().compareTo("transmitCommands") == 0)
						logger.warn("Ignoring deprecated stored configuration value under reader the reader section: transmitCommands");
					else if (n2.getNodeName().compareTo("signalStrengthTransitThreshold") == 0)
						logger.warn("Ignoring deprecated stored configuration value: signalStrengthTransitThreshold");
					else
						throw new Exception(
								"Unexpected node type in stored configuration: "
										+ n2.getNodeName());
				}
			}

            // validate
			if (id < 0 || sName == null || nType < 0 || sMacAddress == null
					|| sIpAddress == null || nPort < 0 || nThreshold == Integer.MIN_VALUE 
                    || !validateReaderName(sName) )
				throw new Exception("Improper reader information in stored configuration");

			// disable auto behavior
			stmt.execute("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'");

			if (bPreDelete && (pstmtDelete != null))
			{
				pstmtDelete.setInt(1, id);
				pstmtDelete.executeUpdate();
			}

			// add the reader to the database
			pstmt.setInt(1, id);
			pstmt.setString(2, sName);
			pstmt.setInt(3, nType);
			pstmt.setString(4,sHardwareType);
			pstmt.setInt(5, nThreshold);
			pstmt.setDouble(6, gain);
			pstmt.setString(7, sMacAddress);
			pstmt.setString(8, sIpAddress);
			pstmt.setInt(9, nPort);
			pstmt.setInt(10, idLocation);
			pstmt.setInt(11, nLane);
			pstmt.setInt(12, nDeviceId);			
			pstmt.setLong(13, dtNow.getTime());
			pstmt.setInt(14, x);
			pstmt.setInt(15, y);
			pstmt.setBoolean(16, bIsTransmitter);
			if (sTransmitPayload != null && sTransmitPayload.equals("null"))
				sTransmitPayload = null;
			pstmt.setString(17, sTransmitPayload);
			pstmt.setInt(18, transmitterHaPriority);
			pstmt.setString(19, modelData);
			pstmt.setString(20, disabledReason);
			pstmt.setBoolean(21, enabled);
			pstmt.setInt(22, bioDeviceType);
			pstmt.setString(23, sReaderVersion);
			pstmt.setString(24, sMinXbrcVersion);
			if (lastReaderTestTime == null)
				pstmt.setNull(25, Types.INTEGER);
			else
				pstmt.setLong(25, lastReaderTestTime.getTime());
			pstmt.setBoolean(26, lastReaderTestSuccess);
			pstmt.setString(27, lastReaderTestUser);
			pstmt.executeUpdate();

			// re-enable
			stmt.execute("SET SQL_MODE=@OLD_SQL_MODE");

            configLogger.trace("Reader Updated. UpdateBy= " + updateBy +
                    ", ReaderId=" + id +
                    ", Name=" + sName +
                    ", Type=" + nType +
                    ", hardwareType=" + sHardwareType +
                    ", Threshold=" + nThreshold +
                    ", Gain=" + gain +
                    ", MacAddress=" + sMacAddress +
                    ", IpAddress=" + sIpAddress +
                    ", Port=" + nPort +
                    ", LocationId=" + idLocation +
                    ", Lane=" + nLane +
                    ", DeviceId=" + nDeviceId +
                    ", X=" + x +
                    ", Y=" + y +
                    ", IsTransmitter=" + bIsTransmitter +
                    ", TransmitPayload=" + sTransmitPayload +
                    ", TransmitterHaPriority=" + transmitterHaPriority +
                    ", ModelData=" + modelData +
                    ", DisabledReason=" + disabledReason +
                    ", Enabled=" + enabled +
                    ", BioDeviceType=" + bioDeviceType +
                    ", ReaderVersion=" + sReaderVersion +
                    ", MinXbrcVersion=" + sMinXbrcVersion + 
                    ", lastReaderTestTime=" + strLastReaderTestTime + 
                    ", lastReaderTestSuccess=" + lastReaderTestSuccess + 
                    ", lastReaderTestUser=" + lastReaderTestUser
                );            
			
			if (antennasNode != null)
			{
				processReaderAntennas(conn, id, antennasNode, bPreDelete, updateBy);
			}

		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (pstmtDelete != null)
					pstmtDelete.close();
				if (rs != null)
					rs.close();
			}
			catch (Exception ex2)
			{
			}
		}

	}
	
	private boolean validateReaderName(String sName)
	{
		return Reader.validateName(sName);
	}
	
	private boolean validateReaderLocationName(String sName)
	{
		return ReaderInfo.validateName(sName);
	}
	
	private boolean validateTransmitZoneGroup(String transmitZoneGroup)
	{
		if (transmitZoneGroup == null ||
			transmitZoneGroup.equals("A") ||
			transmitZoneGroup.equals("B") ||
			transmitZoneGroup.isEmpty() )
			return true;
		
		logger.warn("Invalid transmitZoneGroup: " + transmitZoneGroup);
		return false;
	}

    private boolean validateSequenceName( String input ) 
    {    	
    	/* Disable any validate for now. Trust URL escaping later
    	    
	 		boolean invalidChars = false;
	
	        try 
	        {
	            if ( input != null && input.length()>0 ) 
	            {
	                String encodedInput = null;
	                    encodedInput = URLEncoder.encode(input, "UTF-8");
	                if ( encodedInput.compareTo(input) != 0 ) 
	                {
	                    invalidChars = true;
	                }
	            }
	        } 
	        catch (UnsupportedEncodingException e) 
	        {
	            logger.warn("Unable to check string for invalid characters.");
	        }
	        return !invalidChars;
	        
    	*/
    	
    	return true;
    }

	private void processTransmitCommands(Connection conn, int readerId, Node node, boolean bPreDelete,
                                         String updateBy)
			throws Exception
	{
		if (node == null)
			return;
		
		NodeList tcs = node.getChildNodes();
		for (int i = 0; i < tcs.getLength(); i++)
		{
			Node tc = tcs.item(i);

			// process elements
			if (tc.getNodeType() == Node.ELEMENT_NODE)
			{
				// process the reader table
				if (tc.getNodeName().compareTo("transmitCommand") == 0)
					processTransmitCommand(conn, readerId, tc, bPreDelete, updateBy);
				else
					throw new Exception(
							"Unexpected node type in stored configuration: "
									+ tc.getNodeName());
			}
		}
	}
	
	private void processTransmitCommand(Connection conn, long locationId, Node node, boolean bPreDelete,
                                        String updateBy)
			throws Exception
	{
		Statement stmt = null;
		
		ResultSet rs = null;
		
		try
		{
			// prepare statements
			stmt = conn.createStatement();
			
			NodeList nl = node.getChildNodes();
			
			// set some defaults
			String command = null;
			int interval = -1;
			String mode = null;
			long timeout = 0;
			int threshold = -127;
			boolean enableThreshold = false;
			
			NodeList recipients = null;
			
			// set the actual
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node n2 = nl.item(i);

				// process elements
				if (n2.getNodeType() == Node.ELEMENT_NODE)
				{
					// process the transmit commands
					if (n2.getNodeName().compareTo("command") == 0)
						command = n2.getTextContent();
					else if (n2.getNodeName().compareTo("interval") == 0)
						interval = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("mode") == 0)
						mode = n2.getTextContent();
					else if (n2.getNodeName().compareTo("timeout") == 0)
						timeout = Long.parseLong(n2.getTextContent());
					else if (n2.getNodeName().compareTo("threshold") == 0)
						threshold = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("enableThreshold") == 0)
						enableThreshold = Boolean.parseBoolean(n2.getTextContent());
					else if (n2.getNodeName().compareTo("recipients") == 0
							&& n2.getNodeType() == Node.ELEMENT_NODE)
					{
						recipients = n2.getChildNodes();
					}
					else if (n2.getNodeName().compareTo("locationId") == 0)
						continue;
					else
						throw new Exception(
								"Unexpected node type in stored configuration: "
										+ n2.getNodeName());
				}
			}
			
			Set<Long> recipientIds = null;
			if (recipients != null)
			{
				Long recipientLocationId = 0L;
				recipientIds = new HashSet<Long>();
				for (int i = 0; i < recipients.getLength(); i++)
				{
					Node n2 = recipients.item(i);
					
					if (n2.getNodeName().compareTo("recipientLocationId") == 0)
						recipientLocationId = Long.parseLong(n2.getTextContent());
					else
						throw new Exception(
								"Unexpected node type in stored configuration: "
										+ n2.getNodeName());
					
					recipientIds.add(recipientLocationId);
				}
			}

			if (command == null || command.isEmpty() || interval < 0 || mode == null || mode.isEmpty()
					|| timeout < 0)
				throw new Exception("Improper reader information in stored configuration");

			// disable auto behavior
			stmt.execute("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'");

			// add this transit command to the database
			XbrBandCommand cmd = new XbrBandCommand();
			cmd.setCommand(XMIT_COMMAND.valueOf(command));
			cmd.setInterval(interval);
			cmd.setLocationId(locationId);
			cmd.setThreshold(threshold);
			cmd.setEnableThreshold(enableThreshold);
			cmd.setMode(XMIT_MODE.valueOf(mode));
			cmd.setTimeout(new Long(timeout).intValue());
			cmd.setRecipients(recipientIds);
			
			if (!cmd.isValid())
				throw new Exception("Illegal command detected: " + cmd.toString());
						
			if (bPreDelete)
				XbandCommandService.deleteCommands(conn, locationId, false);

			boolean cmdCreated = XbandCommandService.create(conn, locationId, cmd);

			// re-enable
			stmt.execute("SET SQL_MODE=@OLD_SQL_MODE");
			
			if (!cmdCreated)
				throw new Exception("Command not created: " + cmd.toString());

            configLogger.trace("Transmit Command Updated. UpdateBy= " + updateBy +
                    ", Command=" + command +
                    ", Interval=" + interval +
                    ", Mode=" + mode +
                    ", LocatonId=" + locationId +
                    ", Threshold=" + threshold +
                    ", EnableThreshold=" + enableThreshold +
                    ", LocatonId=" + locationId +
                    ", Timeout=" + timeout +
                    ", Recipients=" + recipientIds);
        }
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch (Exception ex2)
			{
			}
		}

	}

	private void processModel(Connection conn, Node node) throws Exception
	{
		// get the xml text for the node and all below it
		if (!node.hasChildNodes())
			return;
		
		String sXML = "";
		NodeList nl = node.getChildNodes();
		// skip over empty nodes (whitespace)
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n2 = nl.item(i);
			int nodeType = n2.getNodeType();
			if (nodeType == Node.ELEMENT_NODE)
			{
				sXML = nodeToString(n2);
				break;
			}
		}
		
		Processor.INSTANCE.getModel().restoreConfiguration(conn, sXML);
	}

	private void processGridItems(Connection conn, Node node, boolean bPreDelete)
			throws Exception
	{
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n2 = nl.item(i);

			// process elements
			if (n2.getNodeType() == Node.ELEMENT_NODE)
			{
				// process the reader table
				if (n2.getNodeName().compareTo("griditem") == 0)
					processGridItem(conn, n2, bPreDelete);
				else
					throw new Exception(
							"Unexpected node type in stored configuration: "
									+ n2.getNodeName());
			}
		}
	}
	
	private void processImages(Connection conn, Node node, boolean bPreDelete)
			throws Exception
	{
		Images images = XmlUtil.convertToPojo(node, Images.class);
		if (images.getImages() == null)
			return;
		
		for (Image image : images.getImages())
		{
			ImageService.save(conn, image);
			if (image.getBlob() == null)
			{
				logger.error("Configuration contains image id=" + image.getId() + " name=" + image.getTitle() + " without a blob.");
				continue;
			}
			image.getBlob().setImageId(image.getId());
			ImageService.saveImageBlob(conn, image.getBlob());
		}
	}
	
	private void processSchedulerSettings(Connection conn, Node node, boolean bPreDelete, String updateBy)
			throws Exception
	{
		SchedulerSettings schedulerSettings = XmlUtil.convertToPojo(node, SchedulerSettings.class);
		if (schedulerSettings.getItems() == null)
			return;
		
		for (SchedulerItem item : schedulerSettings.getItems())
		{
			if (bPreDelete)
				SchedulerItemService.delete(conn, item.getItemKey());
			SchedulerItemService.insert(conn, item, updateBy);
		}
	}

	private void processGridItem(Connection conn, Node node, boolean bPreDelete)
			throws Exception
	{
		PreparedStatement pstmt = null;
		PreparedStatement pstmtDelete = null;
		ResultSet rs = null;

		try
		{
			// prepare statement
			pstmt = conn
					.prepareStatement("INSERT INTO GridItem(id, itemType, XGrid, YGrid, State, Label, Description, Image, Sequence, XPassOnly, LocationId ) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			if (bPreDelete)
				pstmtDelete = conn
						.prepareStatement("DELETE FROM GridItem WHERE id=?");

			NodeList nl = node.getChildNodes();

			int id = -1;
			String sItemType = null;
			int xGrid = -1, yGrid = -1;
			String sState = null;
			String sLabel = null;
			String sDescription = null;
			String sImage = null;
			int nSequence = -1;
			int nLocationId = -1;
			int nXpassOnly = -1;

			id = getIntAttribute(node, "id");
			sItemType = getStringAttribute(node, "type");
			xGrid = getIntAttribute(node, "xgrid");
			yGrid = getIntAttribute(node, "ygrid");

			for (int i = 0; i < nl.getLength(); i++)
			{
				Node n2 = nl.item(i);

				// process elements
				if (n2.getNodeType() == Node.ELEMENT_NODE)
				{
					// process the reader table
					if (n2.getNodeName().compareTo("state") == 0)
						sState = n2.getTextContent();
					else if (n2.getNodeName().compareTo("label") == 0)
						sLabel = n2.getTextContent();
					else if (n2.getNodeName().compareTo("description") == 0)
						sDescription = n2.getTextContent();
					else if (n2.getNodeName().compareTo("image") == 0)
						sImage = n2.getTextContent();
					else if (n2.getNodeName().compareTo("sequence") == 0)
						nSequence = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("locationid") == 0)
						nLocationId = Integer.parseInt(n2.getTextContent());
					else if (n2.getNodeName().compareTo("gueststoshow") == 0)
						nXpassOnly = Integer.parseInt(n2.getTextContent());
					else
						throw new Exception(
								"Unexpected node type in stored griditem configuration: "
										+ n2.getNodeName());
				}
			}

			// validate
			if (id < 0 || sItemType == null || xGrid < 0 || yGrid < 0
					|| sState == null || sLabel == null || sDescription == null
					|| sImage == null || nSequence < 0 || nLocationId < 0)
				throw new Exception("Improper griditem in stored configuration");

			if (bPreDelete)
			{
				pstmtDelete.setInt(1, id);
				pstmtDelete.executeUpdate();
			}

			// add the reader to the database
			pstmt.setInt(1, id);
			pstmt.setString(2, sItemType);
			pstmt.setInt(3, xGrid);
			pstmt.setInt(4, yGrid);
			pstmt.setString(5, sState);
			pstmt.setString(6, sLabel);
			pstmt.setString(7, sDescription);
			pstmt.setString(8, sImage);
			pstmt.setInt(9, nSequence);
			pstmt.setInt(10, nXpassOnly);
			pstmt.setInt(11, nLocationId);
			pstmt.executeUpdate();

		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
				if (pstmtDelete != null)
					pstmtDelete.close();
				if (rs != null)
					rs.close();
			}
			catch (Exception ex2)
			{
			}
		}

	}

	private int getIntAttribute(Node node, String sName) throws Exception
	{
		return Integer.parseInt(getStringAttribute(node, sName));
	}

	private String getStringAttribute(Node node, String sName) throws Exception
	{
		Node na = node.getAttributes().getNamedItem(sName);
		if (na == null || na.getNodeType() != Node.ATTRIBUTE_NODE)
			throw new Exception("missing " + sName
					+ " attribute in griditem element in stored configuration");

		String sVal = na.getTextContent();
		if (sVal == null)
			throw new Exception("invalid " + sName
					+ " attribute in griditem in stored configuration");

		return sVal;
	}

	private String nodeToString(Node node)
	{
		StringWriter sw = new StringWriter();
		try
		{
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		}
		catch (TransformerException te)
		{
			logger.error(ExceptionFormatter.format(
					"nodeToString Transformer Exception", te));
		}

		return sw.toString();
	}

	private void addReader(ReaderInfo rdr) throws Exception
	{
		// set the location object (if necessary)
		if (rdr.getLocation()==null) 
		{
			rdr.setLocation(ConfigOptions.INSTANCE.getLocationInfo().get(UNKNOWN_LOCATION_ID));
            logger.debug("NULL location for reader. Using unknown location when adding reader.");

            // check if still null
            if ( rdr.getLocation() == null ) 
            {
                logger.error("Could not lookup UNKNOWN location when adding new reader");
                
                // TODO: how to recover?
                throw new Exception("Could not lookup UNKNOWN location when adding new reader");
            }
        }
		
		Connection dbConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "INSERT INTO Reader(readerId, macAddress, ipAddress, port, locationId, type, " +
						  "timeLastHello, lane, deviceId, version, minXbrcVersion, modelData, disabledReason, enabled, bioDeviceType, hardwareType) VALUES(?, ?, ?, ?, ?, ?, ?, 0, 0, ?, ?, ?, ?, ?, ?, ?)";

			pstmt = dbConn.prepareStatement(sSQL,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, rdr.getName());
			pstmt.setString(2, rdr.getMacAddress());
			pstmt.setString(3, rdr.getIpAddress());
			pstmt.setInt(4, rdr.getPort());
			pstmt.setLong(5, rdr.getLocation().getId());
			pstmt.setInt(6, rdr.getType().ordinal());
			pstmt.setLong(7, rdr.getTimeLastHello());
			pstmt.setString(8, rdr.getVersion());
			pstmt.setString(9, rdr.getMinXbrcVersion());
			pstmt.setString(10, rdr.getModelData());
			pstmt.setString(11, rdr.getDisabledReason());
			pstmt.setBoolean(12, rdr.isEnabled());
			pstmt.setInt(13, rdr.getBioDeviceType());
			if (rdr.getHardwareType() != null)
				pstmt.setString(14, rdr.getHardwareType());
			else
				pstmt.setNull(14, Types.VARCHAR);
			pstmt.execute();

			rs = pstmt.getGeneratedKeys();
			ResultSetMetaData rsmd = rs.getMetaData();
			int id = -1;
			if (rsmd.getColumnCount() == 1)
			{
				if (rs.next())
				{
					id = rs.getInt(1);
				}
			}

			// patch the id
			rdr.setId(id);

			// add to the in-memory reader list
			synchronized (ConfigOptions.INSTANCE.getReadersInfo())
			{
				ConfigOptions.INSTANCE.getReadersInfo().AddReader(rdr);
				ConfigOptions.INSTANCE.setConfigurationChangedDate(new Date());
			}
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error adding reader to database", e));
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
			}
			catch (Exception e)
			{
			}

			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}

	private void updateReaderIpAddress(ReaderInfo rdr)
	{
		Connection dbConn = null;
		PreparedStatement pstmt = null;
		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "UPDATE Reader set ipAddress=? WHERE (macAddress=? AND port=?)";
			pstmt = dbConn.prepareStatement(sSQL);
			pstmt.setString(1, rdr.getIpAddress());
			pstmt.setString(2, rdr.getMacAddress());
			pstmt.setInt(3, rdr.getPort());
			pstmt.execute();
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error updating reader IP address", e));
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}
	
	private void updateReaderHardwareType(ReaderInfo rdr)
	{
		Connection dbConn = null;
		PreparedStatement pstmt = null;
		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "UPDATE Reader set hardwareType=? WHERE (macAddress=? AND port=?)";
			pstmt = dbConn.prepareStatement(sSQL);
			pstmt.setString(1, rdr.getHardwareType());		
			pstmt.setString(2, rdr.getMacAddress());
			pstmt.setInt(3, rdr.getPort());
			pstmt.execute();
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error updating reader hardwareType", e));
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}

	private void updateReaderVersion(ReaderInfo rdr)
	{
		Connection dbConn = null;
		PreparedStatement pstmt = null;
		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "UPDATE Reader set version=?, minXbrcVersion=? WHERE (macAddress=? AND port=?)";
			pstmt = dbConn.prepareStatement(sSQL);
			pstmt.setString(1, rdr.getVersion());
			pstmt.setString(2, rdr.getMinXbrcVersion());
			pstmt.setString(3, rdr.getMacAddress());
			pstmt.setInt(4, rdr.getPort());
			pstmt.execute();
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error updating reader version and minXbrcVersion", e));
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}

	private void updateReaderPort(ReaderInfo rdr)
	{
		Connection dbConn = null;
		PreparedStatement pstmt = null;
		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			String sSQL = "UPDATE Reader set port=? WHERE macAddress=?";
			pstmt = dbConn.prepareStatement(sSQL);
			pstmt.setInt(1, rdr.getPort());
			pstmt.setString(2, rdr.getMacAddress());
			pstmt.execute();
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error updating reader port", e));
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex)
			{
			}
			if (dbConn != null)
				Controller.getInstance().releasePooledConnection(dbConn);
		}
	}

	private void updateReaderTimeLastHello(ReaderInfo rdr)
	{
        long lTime = rdr.getTimeLastHello();

        synchronized ( readersLastHello ) {
            readersLastHello.updateProperty(rdr, lTime);
        }
    }

	private void updateReaderLastIdReceived(ReaderInfo rdr, long eno)
	{
        rdr.setLastIDReceived(eno);

        synchronized ( readersLastEventId ) {
            readersLastEventId.updateProperty(rdr, eno);
        }
	}

	public boolean checkSecurity(Request request, Response response)
	{
        // security check
		if (
            ConfigOptions.INSTANCE.getControllerInfo().isSLLEnabled() &&
		    !request.isSecure() &&
            rmap.getResourceEntry(request).isSslRequired()
        ) {
			ResponseFormatter.return403(response);
			return false;
		}
		else
			return true;
	}

	private ReaderInfo findReader(String sMacAddress, int nPort,
			boolean ignorePort, String readerId)
	{
		synchronized (ConfigOptions.INSTANCE.getReadersInfo())
		{
			ReadersInfo rdrs = ConfigOptions.INSTANCE.getReadersInfo();
			for (ReaderInfo rdr : rdrs.getReaders())
			{
				if (rdr.getMacAddress().compareTo(sMacAddress) == 0
						&& (ignorePort || rdr.getPort() == nPort))
					return rdr;

                // Handle simulated reader case
                if(ignorePort && (readerId != null) && !readerId.isEmpty() && readerId.equals(rdr.getName())) {
                    return rdr;
                }
			}
			return null;
		}
	}

    private boolean isReaderNameTaken(String readerId)
    {
        if((readerId == null) || readerId.isEmpty()) {
            return true;
        }

        synchronized (ConfigOptions.INSTANCE.getReadersInfo())
        {
            ReadersInfo rdrs = ConfigOptions.INSTANCE.getReadersInfo();
            for (ReaderInfo rdr : rdrs.getReaders())
            {
                if(readerId.equals(rdr.getName())) {
                    return true;
                }
            }

            return false;
        }
    }

	public static <T> Object convertToPojo(InputStream xml, Class<T> clazz)
			throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(clazz.getPackage().getName());
		Unmarshaller u = jc.createUnmarshaller();
		return u.unmarshal(xml);
	}

	public boolean checkReaderVersion(ReaderInfo r, PackageManager manager)
	{		
		// if it's a simulator, just return true
		if (r.getVersion()==null || r.getVersion().equals("0.0.0.0") || r.isSimulated())
			return true;
		
		PackageManager.VersionComparator comparator = new PackageManager.VersionComparator();
		String versionCheckMessage = null;
		int ret = 0;
		
		if (!manager.checkReaderVersion(r))
		{
			versionCheckMessage = "Reader version " + r.getVersion()
					+ " is smaller than required by XBRC";
			logger.warn("Reader " + r.getName() + " version " + r.getVersion()
					+ " is smaller than required by XBRC");
		}

		if (versionCheckMessage == null)
		{
			// Next check if we (XBRC) are at a version that the reader
			// requires.
			String ourVersion = this.getClass().getPackage()
					.getImplementationTitle();
			if (ourVersion == null || ourVersion.isEmpty())
				logger.warn("Skipping XBRC version check because we are running developement version of XBRC");
			else
			{
				ret = comparator.compare(ourVersion, r.getMinXbrcVersion());

				if (ret < 0)
				{
					versionCheckMessage = "XBRC version " + ourVersion
							+ " is smaller than required by reader "
							+ r.getMinXbrcVersion();
					logger.warn("XBRC version " + ourVersion
							+ " is smaller than required by reader "
							+ r.getName() + " " + r.getMinXbrcVersion());
				}
			}
		}

		synchronized (r)
		{
			r.setLastVersionCheckMessage(versionCheckMessage);
		}

		return versionCheckMessage == null;
	}
	
	public int upgradeReaderVersion(ReaderInfo r)
			throws IOException
	{
		int retCode = 0;
		// Convert the file path to the URL path that the reader would need to
		// call
		String ownIP = getXbrcIP(r);
		
		ReaderVersion upgradeToVersion = reposManager.getUpgradeToVersion(r.getHardwareType());
		
		if (upgradeToVersion == null || upgradeToVersion.getPath() == null)
		{
			logger.error("There is no manifest installer package for reader of type "
					+ r.getType().getType()
					+ ". Reader software cannot be upgraded.");
			return 0;
		}

		// Modify the manifest to use our URL for callbacks
		ReaderManifest rm = upgradeToVersion.getManifest().clone();
		for (ReaderRepo repo : rm.getRepos())
		{
			if (ConfigOptions.INSTANCE.getControllerInfo().isSLLEnabled())
				repo.setPath("https://" + ownIP + ":"
						+ ConfigOptions.INSTANCE.getControllerInfo().getHttpsPort() + "/www/repos/" + repo.getPath());
			else
				repo.setPath("http://" + ownIP + ":"
						+ ConfigOptions.INSTANCE.getControllerInfo().getHttpPort() + "/www/repos/" + repo.getPath());				
		}
		
		try
		{
			retCode = ReaderApi.upgrade(r, rm, 30000);
			logger.info("Asked reader " + r.getName()
					+ " to upgrade its software version from manifest file "
					+ upgradeToVersion.getPath() + " HTTP return code: " + retCode);
		}
		catch (Exception e)
		{
			logger.error("Failed to send upgrade request to the reader", e);
		}
		
		return retCode;
	}

    private int installMediaPackage(ReaderInfo r)
            throws IOException
    {
        int retCode = 0;

        InputStream is = null;

        try {
            is = mediaManager.readMediaPackage();
            retCode = ReaderApi.installMediaPackage(r, is, mediaManager.getHash());

            logger.info("Asked reader " + r.getName()
                + " to upgrade it's media package " + "HTTP ret code: " + retCode);

        }
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }

        return retCode;
    }

    private boolean checkReaderMediaVersion(ReaderInfo r, MediaManager mediaManager) {
        // if it's a simulator, just return true
        if ( r.isSimulated() || !ReaderType.isTapReader(r.getType()) || !r.supportsMediaPackages())
            return true;

        if (!mediaManager.checkReaderVersion(r))
        {
            logger.warn("Reader " + r.getName() + " media package hash " + r.getMediaPackageHash()
                    + " is different than XBRC media package hash " + mediaManager.getHash());
            return false;
        }

        return true;
    }
    
    
    private boolean checkHttpRetCode(int code)
    {
    	return code >= 200 && code < 300;
    }

    private void initializeAccessControl() {
        //try {Thread.sleep(8000);} catch (Exception ignore) {}

        final String appId = "XBRC";

        XbUtils.loadProperties(prop, "xbrc", logger);

        try {
            XbConnection.init(XbUtils.propsToHashMap(prop));
        }
        catch (Exception e) {
            this.logger.fatal("Failed to read load access control properties: " + e.getMessage());
            throw new RuntimeException(e);
        }

        InputStream is = null;

        try {
            if(prop.get(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE) != null) {
                is = new FileInputStream(
                    InputValidator.validateFilePath((String) prop.get(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE))
                );
            }
            else {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ac-model.xml");
            }
        }
        catch (Exception e) {
            this.logger.fatal("Failed to read roles map file " + prop.get(PkConstants.NAME_PROP_AC_ROLES_MAP_FILE), e);
            throw new RuntimeException(e);
        }

        if(XbUtils.isEmpty((String) prop.get(PkConstants.NAME_PROP_AC_XBRC_LOCAL_AUTH_SERVICE_CLASS))) {
            prop.put(
                PkConstants.NAME_PROP_AC_XBRC_LOCAL_AUTH_SERVICE_CLASS,
                PkConstants.DEFAULT_AC_XBRC_LOCAL_AUTH_SERVICE_CLASS
            );
        }

        if(
            (prop.get(PkConstants.NAME_PROP_AC_DISABLE_XBRC_AC) != null) &&
            ((String) prop.get(PkConstants.NAME_PROP_AC_DISABLE_XBRC_AC)).equalsIgnoreCase("true")
        ) {
            this.acDisabled = true;
        }

        try {
            this.acManager = new AcManager(is, appId);
            this.authManager = AuthServiceFactory.getLocalAuthService(prop, true);
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }

    private boolean checkAccess(Request request, Response response)
    {
        final String clientAddr = SfRequestWrapper.getClientAddr(request);
        UserContext.instance.set(new UserContext("", "", null, null, clientAddr));

        if (!this.acDisabled && this.rmap.getResourceEntry(request).isAuthRequired()) {
            // Authenticate the request
            XbSecureToken token = null;

            try {
                token = this.authManager.authenticate(new SfRequestWrapper(request));
            }
            catch (Exception e) {
            }

            if (token == null) {
                long time = System.currentTimeMillis();

                response.set("Content-Type", "text/plain");
                response.set("Server", "xBRC/1.0 (Simple 4.0)");
                response.setDate("Date", time);
                response.setDate("Last-Modified", time);
                response.set("WWW-Authenticate", "Basic realm=\"XBRC\"");
                response.setCode(401);

                try {
			        PrintStream body = response.getPrintStream();
			        body.println("Unauthorized");
			        body.close();
                }
                catch (Exception e) {
                }

                final IAudit auditor = Auditor.getInstance().getAuditor();
                auditor.audit(auditor.createAccessFailure(request.getMethod() + "@" + request.getPath()));

                return false;
            }

            UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, clientAddr));

            // Authorize the request
            boolean isAuthorized = false;

            try {
                isAuthorized = this.acManager.isAuthorized(token, new SfRequestWrapper(request));
            }
            catch (Exception e) {
            }

            Auditor.getInstance().getEventsProvider().cleanup(false);

            if (!isAuthorized) {
                ResponseFormatter.return403(response);
                return false;
            }
        }

        return true;
    }

    private void auditAccessBegin(final Request request, final Response response) {
    }

    private void auditAccessEnd(final Request request, final Response response, final String error) {
        final IAudit auditor = Auditor.getInstance().getAuditor();

        if(this.rmap.getResourceEntry(request).isAuditRequired()) {
            if(auditor.isAuditEnabled()) { // This check is only for performance reasons.
                final int code = response.getCode();

                if((error != null) && (error.length() != 0)) {
                    auditor.audit(auditor.createAccessFailure(request.getMethod() + "@" + request.getPath().getPath()));
                }
                else {
                    if((code >= 200) && (code < 400)) {
                        auditor.audit(auditor.createAccessSuccess(request.getMethod() + "@" + request.getPath().getPath()));
                    }
                    else {
                        auditor.audit(auditor.createAccessFailure(request.getMethod() + "@" + request.getPath().getPath()));
                    }
                }
            }
        }
    }

    private boolean interceptRequest(final Request request, final Response response)
    {
        if(!checkSecurity(request, response)) {
            return false;
        }

        if(!checkAccess(request, response)) {
            return false;
        }

        auditAccessBegin(request, response);

        return true;
    }

    private boolean interceptResponse(final Request request, final Response response, final String error)
    {
        auditAccessEnd(request, response, error);

        return true;
    }
    
    private static Date parseDate(String datetime)
    {
	    try
		{
			Date dt;
			synchronized(dateFormatter)
			{
				dt = dateFormatter.parse(datetime);
			}
			return dt;
		}
		catch (Exception ex)
		{
			logger.error("Invalid time: " + datetime);
			return null;
		}
    }
}
