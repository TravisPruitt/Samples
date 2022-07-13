package com.disney.xband.xbrc.lib.utils;

import java.net.InetAddress;

public class ControllerLibUtils
{
	/*
	 * Map and IP address to a mac address. Padding last two mac address bytes with 00.
	 */
	public static String IPToMac(InetAddress src)
	{
		StringBuffer sb = new StringBuffer();
		for (byte b : src.getAddress())
		{
			sb.append(Integer.toHexString((b >> 4) & 0x0F));
			sb.append(Integer.toHexString(b & 0x0F));
			sb.append(":");
		}
		sb.append("00:00");
		
		return sb.toString();
	}
	
	public static String IPToMac(InetAddress src, String suffix)
	{
		StringBuffer sb = new StringBuffer();
		for (byte b : src.getAddress())
		{
			sb.append(Integer.toHexString((b >> 4) & 0x0F));
			sb.append(Integer.toHexString(b & 0x0F));
			sb.append(":");
		}
		
		// The suffix is any string. We use it to further make the 
		// MAC address unique based on the hash code of the string.
		String hash = new Integer(suffix.hashCode()).toString();
		sb.append(hash.substring(0,2));
		sb.append(":");
		sb.append(hash.substring(3,5));
		
		return sb.toString();
	}
}
