package com.disney.xband.xbrc.lib.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

@Deprecated
public abstract class BroadcastListener extends Thread {
	private static Logger logger = Logger.getLogger(BroadcastListener.class);
	private DatagramSocket socket;
	private boolean bContinue = true;
	private int bufferLength = 1024;
	
	public BroadcastListener(int port, int bufferLength) throws IOException {
		socket = new DatagramSocket(port);
		socket.setSoTimeout(1000);
		this.bufferLength = bufferLength;
	}
	
	public void listen() throws IOException {
		this.start();
	}
	
	public void finish() {
		bContinue = false;
	}
	
	public void run() {

		while (bContinue) {
			byte[] buf = new byte[bufferLength];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
				onReceivePacket(packet);			
			} catch (SocketTimeoutException e) {
				// expected
			} catch (IOException e) {
				logger.error("BroadcastListener: failed to receive UDP packet.", e);
			}
		}

		socket.close();
	}
	
	/*
	 * Implement this method in a sub-class to process received packet.
	 */
	public abstract void onReceivePacket(DatagramPacket packet);
}
