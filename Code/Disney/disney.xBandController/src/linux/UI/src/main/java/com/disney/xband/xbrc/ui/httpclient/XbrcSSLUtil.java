package com.disney.xband.xbrc.ui.httpclient;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.security.InputValidator;
import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.net.NaiveHostnameVerifier;
import com.disney.xband.xbrc.lib.net.NaiveX509TrustManager;

public class XbrcSSLUtil {
	
	private static Logger logger = Logger.getLogger(XbrcSSLUtil.class);
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
		 * determine path to configuration file
		 */
		String configDir = System.getProperty("config.dir");
		if (configDir == null || configDir.trim().isEmpty())
			configDir = DEFAULT_CONFIG_DIR;
		configDir += "/UI";
		
		/*
		 * ssl configuration
		 */
		String sslClientKeystore =  InputValidator.validateFilePath(System.getProperty("ssl.keyStore.client"));
		String sslClientKeystorePassword = System.getProperty("ssl.keyStorePassword.client");
		
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
//			xbrmsHostnameVerifier = new XbrcHostnameVerifier();
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
//				xbrmsTrustManagers = new X509TrustManager[] { new XbrcX509TrustManager() };
//			}
//			
//			context = SSLContext.getInstance("SSL");
//			context.init(null, xbrmsTrustManagers, new SecureRandom());
//			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
//		} 
//		catch (Exception e)
//		{
//			logger.error("Failed to correcty initialize SSLUtilities", e);
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
        conn.setRequestProperty("Authorization", XbConnection.getAuthorizationString());

		return conn;
	}

	public static HttpsURLConnection getHttpsConnection(URL url) throws IOException
	{
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setConnectTimeout(connectionTimeout);
        conn.setRequestProperty("Authorization", XbConnection.getAuthorizationString());

		return conn;
	}

	/**
	 * Set the default Hostname Verifier to an instance of a naive class that 
	 * trust all host names.
	 */
	public static void trustAllHostnames() 
	{
		// Create a host name verifier that always returns true
		if(xbrmsHostnameVerifier == null || xbrmsHostnameVerifier instanceof XbrcHostnameVerifier) {
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
	private static class XbrcHostnameVerifier implements HostnameVerifier 
	{
		public XbrcHostnameVerifier()
		{
			BufferedInputStream in = null;
            FileInputStream fis = null;
			   try
			   {
                   fis = new FileInputStream(InputValidator.validateFilePath(clientKeystore));
				   in = new BufferedInputStream(fis);
				   CertificateFactory cf = CertificateFactory.getInstance("X.509");
				   xbrcCert = (X509Certificate)cf.generateCertificate(in);
			   }
			   catch (Exception e)
			   {
				   logger.warn("Failed to read certificate " + clientKeystore, e);
			   }
			   finally
			   {
				   if (in != null)
				   {
					   try{
						   in.close();
					   } catch (Exception e){}
				   }

                   if (fis != null)
                   {
                       try{
                           fis.close();
                       } catch (Exception e){}
                   }
			   }
		}
		
		public boolean verify(String hostname, SSLSession session) 
		{
			//TODO determine appropriate verification check. xbrcCert holds the certificate's text.
			return true;
		}
	}
	
	private static class XbrcX509TrustManager implements X509TrustManager {

		   /*
		    * The default X509TrustManager returned by SunX509.  We'll delegate
		    * decisions to it, and fall back to the logic in this class if the
		    * default X509TrustManager doesn't trust it.
		    */
		   X509TrustManager sunJSSEX509TrustManager;

		   XbrcX509TrustManager() throws Exception 
		   {
			   KeyStore ks = KeyStore.getInstance("JKS");
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
                       catch(Exception ignore) {
                       }
                   }
               }

			   ks.setCertificateEntry("publickey", xbrcCert);
			   
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
