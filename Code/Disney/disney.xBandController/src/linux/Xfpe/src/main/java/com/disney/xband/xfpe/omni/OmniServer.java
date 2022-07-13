package com.disney.xband.xfpe.omni;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/*
 * Simple, single threaded socket listener. Handles only one connection at a time.
 */
public class OmniServer {
	private static Logger logger = Logger.getLogger(OmniServer.class);
	private OmniSimulator simulator;
	private ServerSocket socket = null;	
	private boolean run = true;
	private List<OmniClientThread> clients;

	public void start(int port) throws IOException {

		simulator = new OmniSimulator();
		
		synchronized(this){
			socket = new ServerSocket(port);
			clients = new ArrayList<OmniClientThread>();
		}
		
		while (run) {
			Socket s;			
			try {
				s = socket.accept();
				handleConnection(s);
			} catch (IOException e) {
				logger.warn("Caught exception in OmniServer:start", e);
				run = false;
			}
		}
	}
	
	public void stop() {
		synchronized (this) {
			run = false;
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
			socket = null;
			
			if (clients != null) {
				for (OmniClientThread oct : clients) {
					oct.stopThread();
				}
			}
			
			clients = null;
		}
	}

	public void handleConnection(Socket s) {
		OmniClientThread oct = new OmniClientThread(s, simulator, this);
		synchronized(this) {
			clients.add(oct);
		}
		oct.start();
	}
	
	public void onOmniClientThreadExit(OmniClientThread oct)
	{
		logger.info("Reader with DeviceID " + oct.getOmniDeviceId() + " disconnected from Omni simulator");
		
		synchronized(this) {
			if (clients != null)
				clients.remove(oct);
		}
	}
	
	public boolean getRun() {
		return run;
	}

	public OmniSimulator getSimulator()
	{
		return simulator;
	}
}
