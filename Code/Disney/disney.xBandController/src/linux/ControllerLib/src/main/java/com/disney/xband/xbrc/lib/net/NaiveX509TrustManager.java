package com.disney.xband.xbrc.lib.net;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * This class allows any X509 certificates to be used to authenticate the 
 * remote side of a secure socket, including self-signed certificates.
 * @author Iwona Glabek
 */
public class NaiveX509TrustManager implements X509TrustManager 
{
	private static Logger logger = Logger.getLogger(NaiveX509TrustManager.class);
	
	private static final X509Certificate[] acceptedIssuers = new X509Certificate[] {};

	public NaiveX509TrustManager(){}
	
	public void checkClientTrusted(X509Certificate[] chain, String authType) {
		logger.info("Using naive trust manager for xbrc ssl connections.");
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) {
		logger.info("Using naive trust manager for xbrc ssl connections.");
	}

	public X509Certificate[] getAcceptedIssuers() {
		return(acceptedIssuers);
	}
}
