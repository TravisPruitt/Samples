package com.disney.xband.xbrc.lib.net;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * This class always returns true. It does not attempt to verify the hostname.
 * @author Iwona Glabek
 */
public class NaiveHostnameVerifier implements HostnameVerifier 
{
	public boolean verify(String hostname, SSLSession session) 
	{
		return true;
	}
}
