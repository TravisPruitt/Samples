package com.disney.xband.jmslistener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.SocketConnection;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.ListenerStatus;
import com.disney.xband.common.lib.performance.PerfMetric;
import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.common.lib.performance.PerfMetricMetadataEnvelope;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;
import com.disney.xband.jmslistener.gff.GffService;
import com.disney.xband.jmslistener.idms.IdmsService;
import com.disney.xband.jmslistener.xi.XiService;

public class WebServer implements Container
{
	// singleton
	public static WebServer INSTANCE = new WebServer();

	private static Logger logger = Logger.getLogger(WebServer.class);

	private org.simpleframework.transport.connect.Connection sockConn = null;
	private org.simpleframework.transport.connect.Connection sockConnSSL = null;
	private PerfMetricMetadataEnvelope perfMetricMetadata;

	/*
	 * Initialization and termination methods
	 */

	private WebServer()
	{
	}

	public void Start() throws IOException
	{
		// even if this web server is not configured to listen to https request,
		// it communicates with other parts of the system using ssl.
		setupSSLProperties();

		if (ConfigurationProperties.INSTANCE.getUseSSL())
		{
			// start the Https connection
			int nHttpsPort = ConfigurationProperties.INSTANCE.getHttpsPort();
			if (nHttpsPort > 0)
			{
				sockConnSSL = new SocketConnection(this);
				SocketAddress address = new InetSocketAddress(nHttpsPort);
				SSLContext sslc = createSSLContext();
				sockConnSSL.connect(address, sslc);
			}
		}
		else
		{
			// start the Http connection
			int nHttpPort = ConfigurationProperties.INSTANCE.getHttpPort();
			if (nHttpPort > 0)
			{
				sockConn = new SocketConnection(this);
				SocketAddress address = new InetSocketAddress(nHttpPort);
				sockConn.connect(address);
			}

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
		String sslServerStore = ConfigurationProperties.INSTANCE
				.getSSLServerKeystore();
		String sslServerStorePassword = ConfigurationProperties.INSTANCE
				.getSSLServerKeystorePassword();
		String sslClientKeystore = ConfigurationProperties.INSTANCE
				.getSSLClientKeystore();
		String sslClientKeystorePassword = ConfigurationProperties.INSTANCE
				.getSSLClientKeystorePassword();

		if (sslServerStore != null && !sslServerStore.trim().isEmpty()
				&& sslServerStorePassword != null
				&& !sslServerStorePassword.trim().isEmpty())
		{
			/*
			 * Trust store is a keystore containing Certification Authority (CA)
			 * certificate(s) used to sign client and server certificates
			 * included in client and server keystores used by this web server
			 * and applications this web server communicates with over https.
			 */
			System.setProperty("javax.net.ssl.trustStore", sslServerStore);
			System.setProperty("javax.net.ssl.trustStorePassword",
					sslServerStorePassword);

			if (logger.isInfoEnabled())
				logger.info("Using SSL trustStore: " + sslServerStore);
		}
		else
		{
			if (logger.isInfoEnabled())
			{
				String trustStore = System
						.getProperty("javax.net.ssl.trustStore");
				if (trustStore == null)
					logger.warn("Server's keystore not provided. This will make requests over https fail.");
				else
					logger.info("Using SSL trustStore: " + trustStore);
			}

		}

		if (sslClientKeystore != null && !sslClientKeystore.trim().isEmpty()
				&& sslClientKeystorePassword != null
				&& !sslClientKeystorePassword.trim().isEmpty())
		{
			System.setProperty("javax.net.ssl.keyStore", sslClientKeystore);
			System.setProperty("javax.net.ssl.keyStorePassword",
					sslClientKeystorePassword);

			if (logger.isInfoEnabled())
				logger.info("Using SSL client keystore: " + sslClientKeystore);
		}
		else
		{
			String keystore = System.getProperty("javax.net.ssl.keyStore");
			String password = System
					.getProperty("javax.net.ssl.keyStorePassword");

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

			String keystoreLocation = System
					.getProperty("javax.net.ssl.trustStore");
			String keystorePass = System
					.getProperty("javax.net.ssl.trustStorePassword");

			FileInputStream fis = null;
			try
			{
				fis = new java.io.FileInputStream(InputValidator.validateFilePath(keystoreLocation));
				ks.load(fis, keystorePass.toCharArray());
			}
			finally
			{
				if (fis != null)
				{
					try
					{
						fis.close();
					}
					catch (Exception ignore)
					{
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
		try
		{
			logger.debug("Processing http request: " + request.getPath());

			if (request.getMethod().compareTo("GET") == 0)
			{
				handleGet(request, response);
			}
			else if (request.getMethod().compareTo("DELETE") == 0)
			{
				handleDelete(request, response);
			}
		}
		catch (IOException e)
		{
			logger.error("Error handling HTTP request: " + e);
		}
	}

	private void handleGet(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();

		if (sPath.compareTo("/status") == 0)
			handleGetStatus(request, response);
		else if (sPath.compareTo("/perfmetricsmetadata") == 0)
			handlePerfMetricsMetadata(request, response);
		else if (sPath.compareTo("/configuration") == 0)
			handleConfiguration(request, response);
		else
			return404(response);
	}

	private void handleDelete(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();

		if (sPath.compareTo("/cache") == 0)
		{
			handleDeleteCache(request, response);
		}
		else
		{
			return404(response);
		}
	}

	private void handleGetStatus(Request request, Response response)
	{
		if (!checkSecurity(request, response))
			return;

		PrintStream body = null;

		try
		{
			StatusInfo.INSTANCE.Check();

			ListenerStatus status = StatusInfo.INSTANCE.getListenerStatus();
			String xml = XmlUtil.convertToXml(status, ListenerStatus.class);

			body = response.getPrintStream();
			body.println(xml);
			setResponseHeader(response, "application/xml");

			return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format("Error handling GET status",
					e));
			return500(response,
					ExceptionFormatter.format("Error handling GET status", e));
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

	private void handlePerfMetricsMetadata(Request request, Response response)
	{
		if (!checkSecurity(request, response))
			return;

		String xml = null;

		PrintStream body = null;

		try
		{
			ListenerStatus status = StatusInfo.INSTANCE.getListenerStatus();

			if (perfMetricMetadata == null)
			{
				perfMetricMetadata = new PerfMetricMetadataEnvelope();

				PerfMetric metric = null;
				PerfMetricMetadata meta = null;
				for (Method m : status.getClass().getDeclaredMethods())
				{
					// don't process compiler generated methods
					if (m.isSynthetic())
						continue;

					if (m.getReturnType() != PerfMetric.class)
						continue;

					metric = (PerfMetric) m.invoke(status, new Object[]
					{});

					if (metric == null)
						continue;

					meta = metric.getMetadata();

					perfMetricMetadata.add(meta);
				}
			}

			// serialize
			xml = XmlUtil.convertToXml(perfMetricMetadata,
					PerfMetricMetadataEnvelope.class);

			body = response.getPrintStream();
			body.println(xml);
			setResponseHeader(response, "application/xml");
			return200(response);

		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET performance metrics metadata", e));
			return500(response, ExceptionFormatter.format(
					"Error handling GET performance metrics metadata", e));
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

	private void handleConfiguration(Request request, Response response)
	{
		if (!checkSecurity(request, response))
			return;

		String xml = null;

		PrintStream body = null;

		try
		{

			// serialize
			xml = XmlUtil.convertToXml(ConfigurationProperties.INSTANCE,
					ConfigurationProperties.class);

			body = response.getPrintStream();
			body.println(xml);
			setResponseHeader(response, "application/xml");
			return200(response);

		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET configuration", e));
			return500(response, ExceptionFormatter.format(
					"Error handling GET configuration", e));
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

	private void handleDeleteCache(Request request, Response response)
	{
		if (!checkSecurity(request, response))
			return;

		GffService.INSTANCE.clearCache();
		XiService.INSTANCE.clearCache();
		IdmsService.INSTANCE.clearCache();

		return200(response);
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

	private void return403(Response response)
	{
		PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(403);
			body = response.getPrintStream();
			body.println("Unauthorized");
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 403 response", e));
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
		response.set("Server", "JmsListener/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
	}

	private void return200(Response response)
	{
		PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(200);
			body = response.getPrintStream();
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 200 response", e));
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

	private void return500(Response response, String sError)
	{
		PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(500);
			body = response.getPrintStream();
			body.println(sError);
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 500 response", e));
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

	public boolean checkSecurity(Request request, Response response)
	{
		// security check
		if (ConfigurationProperties.INSTANCE.getUseSSL() && !request.isSecure())
		{
			return403(response);
			return false;
		}
		else
			return true;
	}
}
