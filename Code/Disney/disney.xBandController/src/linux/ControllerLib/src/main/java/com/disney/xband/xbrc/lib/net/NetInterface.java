package com.disney.xband.xbrc.lib.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.log4j.Logger;

/*
 * Utility class for getting your own IP addresses.
 */
public class NetInterface {
	
	private static Logger logger = Logger.getLogger(NetInterface.class);

	public static Collection<String> getOwnIpAddress(String ipPrefix) throws SocketException
	{		
		if (ipPrefix == null)
			ipPrefix = "";
		
		LinkedList<String> result = new LinkedList<String>();
		
		// Build a list of all interfaces and sub-interfaces. 
		LinkedList<NetworkInterface> allInterfaces = new LinkedList<NetworkInterface>();
		for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			allInterfaces.add(iface);
			for (NetworkInterface siface : Collections.list(iface.getSubInterfaces())) {
				allInterfaces.add(siface);
			}
		}
		
		for (NetworkInterface iface : allInterfaces)
		{
			// loop through all the interfaces in each adapter
			Enumeration<InetAddress> raddrs = iface.getInetAddresses();
			for (InetAddress raddr : Collections.list(raddrs))
			{
				// skip any loopback or ipv6 addresses
				if (raddr.isLoopbackAddress() || raddr instanceof Inet6Address )
					continue;
				
				String sAddress = raddr.toString().substring(1);
			
				if (ipPrefix.isEmpty() || sAddress.startsWith(ipPrefix))
					result.add(sAddress);
			}
		}
		
		return result;			
	}
	
	public static Collection<String> getBroadcastIpAddress(String ipPrefix) throws SocketException
	{		
		if (ipPrefix == null)
			ipPrefix = "";
		
		LinkedList<String> result = new LinkedList<String>();
		
		// Build a list of all interfaces and sub-interfaces. 
		LinkedList<NetworkInterface> allInterfaces = new LinkedList<NetworkInterface>();
		for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			allInterfaces.add(iface);
			for (NetworkInterface siface : Collections.list(iface.getSubInterfaces())) {
				allInterfaces.add(siface);
			}
		}
		
		for (NetworkInterface iface : allInterfaces)
		{
			// loop through all the interfaces in each adapter
			for (InterfaceAddress interfaceAddress : iface.getInterfaceAddresses()) {
		
				InetAddress iaddr = interfaceAddress.getBroadcast();
				 
				// skip any loopback or ipv6 addresses
				if (iaddr == null || iaddr.isLoopbackAddress() || iaddr instanceof Inet6Address )
					continue;
					
				String sAddress = iaddr.toString().substring(1);
				
				if (ipPrefix.isEmpty() || sAddress.startsWith(ipPrefix))
					result.add(sAddress);
			}
		}
		
		return result;			
	}
	
	public static String formatHardwareAddress(byte[] mac) {
		StringBuffer sb = new StringBuffer();
		Formatter formatter = new Formatter(sb, Locale.US);
		for (int i = 0; i < mac.length; i++) {			
			formatter.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "");
		}
		return sb.toString();
	}
	
	public static Collection<String> getOwnMacAddress(String ipPrefix) throws SocketException
	{		
		if (ipPrefix == null)
			ipPrefix = "";
		
		LinkedList<String> result = new LinkedList<String>();
		
		for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces()))
		{
			// loop through all the interfaces in each adapter
			Enumeration<InetAddress> raddrs = iface.getInetAddresses();
			
			for (InetAddress raddr : Collections.list(raddrs))
			{
				// skip any loopback or ipv6 addresses
				if (raddr.isLoopbackAddress() || raddr instanceof Inet6Address )
					continue;
				
				String sAddress = raddr.toString().substring(1);
			
				if (ipPrefix.isEmpty() || sAddress.startsWith(ipPrefix)) {
					if (iface.getHardwareAddress() != null)
						result.add(formatHardwareAddress(iface.getHardwareAddress()));
					break;	// only one hardware address per interface
				}
			}
		}
		
		return result;			
	}
	
	
	public static String getHostname() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (UnknownHostException e) {
			logger.warn("Failed to get hostname", e);
		}
		return "";
	}
}
