package com.disney.xband.xbrc.lib.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collection;

import org.apache.log4j.Logger;

/*
 * The BroadcastSender class allows to broadcast a UDP packet to one or more broadcast addresses.
 */
@Deprecated
public class BroadcastSender {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(BroadcastSender.class);
	
	private DatagramSocket socket;
	private int port;
	
	public BroadcastSender(int port) throws SocketException {
		this.port = port;
		socket = new DatagramSocket();		 
	}
	
	public void sendToAll(String data, String ipPrefix) throws IOException {	
		Collection<String> addresses = NetInterface.getBroadcastIpAddress(ipPrefix);
		for (String broadcastAddress : addresses ) {
			sendToOne(data,broadcastAddress);
		}		
	}
	
	public void sendToOne(String data, String broadcastAddress) throws IOException {
		InetAddress group = InetAddress.getByName(broadcastAddress);
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, group, port);
        socket.send(packet);
	}
}
