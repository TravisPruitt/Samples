package com.disney.xband.xbrc.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.disney.xband.common.lib.security.InputValidator;
import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.net.NaiveHostnameVerifier;
import com.disney.xband.xbrc.lib.net.NaiveX509TrustManager;


public abstract class UISSLUtil {
	
	private static Logger logger = Logger.getLogger(UISSLUtil.class);
	private static int connectionTimeout = 2000;
	
	protected static String DEFAULT_CONFIG_DIR = "/usr/share/disney.xband";
	protected static String configDir;
	
	protected static String clientKeystore;
	protected static char[] clientKeystorePass;
	
	private static X509Certificate xbrcCert;
	private static HostnameVerifier xbrmsHostnameVerifier;
	private static X509TrustManager[] xbrmsTrustManagers;
	
	static
	{
		
		/*
		 * ssl configuration
		 */
		String sslClientKeystore = UIProperties.getInstance().getProperty("nge.xconnect.xbrc.ssl.keystore.client");
		String sslClientKeystorePassword = UIProperties.getInstance().getProperty("nge.xconnect.xbrc.ssl.keystore.client.pwd");
		
		if (sslClientKeystore != null && !sslClientKeystore.trim().isEmpty() 
				&& sslClientKeystorePassword != null && !sslClientKeystorePassword.trim().isEmpty())
		{
			System.setProperty("javax.net.ssl.keyStore", sslClientKeystore);
			System.setProperty("javax.net.ssl.keyStorePassword", sslClientKeystorePassword);
			
			clientKeystore = sslClientKeystore;
			clientKeystorePass = sslClientKeystorePassword.toCharArray();
			
			if (logger.isInfoEnabled())
				logger.info("Using SSL client keystore: " + sslClientKeystore);
		}
		else
		{
			if (System.getProperty("javax.net.ssl.keyStore") == null ||
					System.getProperty("javax.net.ssl.keyStorePassword") == null)
			{
				logger.warn("SSL client authentication not configured. To configure client" +
					"authentication for this client, provide javax.net.ssl.keyStore and " +
					"javax.net.ssl.keyStorePassword java arguments. These can either be" +
					"initialized as VM arguments or provided in the config.properties file.");
			}
			else
			{
				clientKeystore = System.getProperty("javax.net.ssl.keyStore");
				clientKeystorePass = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
			}
		}
		
		/*
		 * setup default host name verifier
		 */
//		if(xbrmsHostnameVerifier == null) {
//			xbrmsHostnameVerifier = new XbrmsHostnameVerifier();
//		}
//
//		HttpsURLConnection.setDefaultHostnameVerifier(xbrmsHostnameVerifier);
//		
//		/*
//		 * setup default trust managers
//		 */
//		SSLContext context = null;
//
//		try 
//		{
//			// Create a trust manager that does not validate certificate chains
//			if(xbrmsTrustManagers == null) 
//			{
//				xbrmsTrustManagers = new X509TrustManager[] { new XbrmsX509TrustManager() };
//			}
//			
//			context = SSLContext.getInstance("SSL");
//			context.init(null, xbrmsTrustManagers, new SecureRandom());
//			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
//		} 
//		catch (Exception e)
//		{
//			logger.error("Failed to correctly initialize SSLUtilities", e);
//		}
	}
	
	public static HttpURLConnection getConnection(URL url) throws IOException, IllegalArgumentException
	{
		if (url == null)
			throw new IllegalArgumentException("Non null url must be provided.");
		
		if (url.getProtocol().equals("https"))
			return getHttpsConnection(url);
		else
			return getHttpConnection(url);
	}
	
	public static HttpURLConnection getHttpConnection(URL url) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(connectionTimeout);

		return conn;
	}

	public static HttpsURLConnection getHttpsConnection(URL url) throws IOException
	{
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setConnectTimeout(connectionTimeout);

		return conn;
	}
	
	/**
	 * Returns an http connection if the port argument converted to <CODE>String</CODE> 
	 * has "80" in it, otherwise this method returns an https connection.
	 * 
	 * TODO this is a temporary solution and this method should be removed
	 * once communication with xbrcs is possible only over ssl.
	 * 
	 * @param ip
	 * @param port
	 * @param resourcePath
	 * @return either http or https connection
	 * @throws IOException
	 * @Deprecated, use either {@link #getHttpConnection(url)} or {@link #getHttpsConnection(url)}
	 */
	@Deprecated
	public static HttpURLConnection getConnection(String ip, int port, String resourcePath) throws IOException
	{
		if (ip == null)
			return null;
		
		boolean useHttp = false;
		
		/*
		 * Not all XBRCs are SSL enabled.
		 */
		StringBuffer address = new StringBuffer();
		if (String.valueOf(port).contains("80") || String.valueOf(port).contains("90"))
			useHttp = true;

		if (useHttp)
			address.append("http://");
		else
			address.append("https://");
		
		address.append(ip).append(":").append(port);
		if (resourcePath != null)
		{
			if (resourcePath.indexOf('/') != 0)
				address.append("/");
			
			address.append(resourcePath);
		}
		
		URL url = new URL(address.toString());
		
		if (useHttp)
			return getHttpConnection(url);
		else
			return getHttpsConnection(url);
		
	}

	/**
	 * Set the default Hostname Verifier to an instance of a naive class that 
	 * trust all host names.
	 */
	public static void trustAllHostnames() 
	{
		// Create a trust manager that does not validate certificate chains
		if(xbrmsHostnameVerifier == null || xbrmsHostnameVerifier instanceof XbrmsHostnameVerifier) {
			xbrmsHostnameVerifier = new NaiveHostnameVerifier();
		}

		HttpsURLConnection.setDefaultHostnameVerifier(xbrmsHostnameVerifier);
	}

	/**
	 * Set the default X509 Trust Manager to an instance of a naive class that 
	 * trust all certificates, even the self-signed ones.
	 */
	public static void trustAllHttpsCertificates() throws Exception
	{
		// Create a trust manager that does not validate certificate chains
		if(xbrmsTrustManagers == null || xbrmsTrustManagers instanceof X509TrustManager[]) 
		{
			xbrmsTrustManagers = new X509TrustManager[] {new NaiveX509TrustManager()};
		}

		SSLContext context = SSLContext.getInstance("SSL");
		context.init(null, xbrmsTrustManagers, new SecureRandom());

		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}
	
	/**
	 * This class always returns true. It does not attempt to verify the hostname.
	 * @author Iwona Glabek
	 */
	private static class XbrmsHostnameVerifier implements HostnameVerifier 
	{
		public XbrmsHostnameVerifier() {}
		
		public boolean verify(String hostname, SSLSession session) 
		{
			//TODO determine appropriate verification check. xbrcCert holds the certificate's text.
			return true;
		}
	}
	
	private static class XbrmsX509TrustManager implements X509TrustManager {

		   /*
		    * The default X509TrustManager returned by SunX509.  We'll delegate
		    * decisions to it, and fall back to the logic in this class if the
		    * default X509TrustManager doesn't trust it.
		    */
		   X509TrustManager sunJSSEX509TrustManager;

		   XbrmsX509TrustManager() throws Exception 
		   {
			   KeyStore ks = KeyStore.getInstance("JKS");
			   
			   /*
			    * Assumes that the keystore contains all the necessary certificates.
			    */

               FileInputStream fis = null;

               try {
                   fis = new FileInputStream(InputValidator.validateFilePath(clientKeystore));
		            ks.load(fis, clientKeystorePass);
               }
               finally {
                   if(fis != null) {
                       try {
                            fis.close();
                       }
                       catch(Exception e) {
                       }
                   }
               }
 
			   TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			   tmf.init(ks);

			   TrustManager tms [] = tmf.getTrustManagers();

			   /*
			    * Use the first instance of X509TrustManager found.
			    */
			   for (int i = 0; i < tms.length; i++) {
				   if (tms[i] instanceof X509TrustManager) {
					   sunJSSEX509TrustManager = (X509TrustManager) tms[i];
					   return;
				   }
			   }

			   /*
			    * Find some other way to initialize, or else we have to fail the
			    * constructor.
			    */
			   throw new Exception("Couldn't initialize");
		     }

		     /*
		      * Delegate to the default trust manager.
		      */
		     public void checkClientTrusted(X509Certificate[] chain, String authType)
		                 throws CertificateException {
		         try {
		             sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
		         } catch (CertificateException excep) {
		        	 //TODO add special handling?
		        	 throw excep;
		         }
		     }

		     /*
		      * Delegate to the default trust manager.
		      */
		     public void checkServerTrusted(X509Certificate[] chain, String authType)
		                 throws CertificateException {
		         try {
		             sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
		         } catch (CertificateException excep) {
		        	 //TODO add special handling?
		        	 throw excep;
		         }
		     }

		     public X509Certificate[] getAcceptedIssuers() {
		         return sunJSSEX509TrustManager.getAcceptedIssuers();
		     }
		}
}
