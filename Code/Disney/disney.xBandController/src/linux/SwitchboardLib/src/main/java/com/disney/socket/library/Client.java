package com.disney.socket.library;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
	
	Socket socket;
	BufferedWriter out;
	BufferedReader in;
	boolean running = false;
	String dateTime;
	Thread clientThread;
	int timeout = -1;

	// Default terminator is a single zero
	int[] terminator = {0};

	String serverName;
	int portNumber = 0;

	public Client() throws IOException, InterruptedException {
		this.serverName = SwitchboardProperties.getPropertyValue("ServerName");
		this.portNumber = Integer.parseInt(SwitchboardProperties
				.getPropertyValue("Port"));
		initialize();
	}

	public Client(String serverName, int portNumber, int[] terminator) throws IOException,
			InterruptedException {
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.terminator =  terminator;
		initialize();
	}
	
	public Client(String serverName, int portNumber, int[] terminator, int timeout) throws IOException,
		InterruptedException {
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.terminator =  terminator;
		this.timeout = timeout;
		initialize();
	}
	
	public void cleanup() {
		try {
			in.close(); 
		} catch (IOException e) {}		
		try {
			out.close();
		} catch (IOException e) {}
		try {
			socket.close();
		} catch (IOException e) {}
	}

	protected void finalize() {
		running = false;

	}

	// protected java
	protected List<SwitchBoardMessageListener> listenerList = new ArrayList<SwitchBoardMessageListener>();

	// This methods allows classes to register for MyEvents
	public void addMyEventListener(SwitchBoardMessageListener listener) {
		listenerList.add(listener);
		//SendMessageEvent("Welcome Listener. Server:" + this.serverName + " Port:" + this.portNumber);
	}

	// This methods allows classes to unregister for MyEvents
	public void removeMyEventListener(SwitchBoardMessageListener listener) {
		listenerList.remove(listener);
	}

	// This private class is used to fire MyEvents
	void fireMyEvent(SwitchBoardMessageEvent evt) {
		for (SwitchBoardMessageListener el : listenerList) {
			el.switchBoardMessage(evt);
		}
	}

	// While we are running, check the input and evaluate for a connection.
	private void readInputSocket() throws IOException {
		clientThread = new Thread(r1);
		clientThread.start();
	}

	// Send any messages out to the listeners.
	private void SendMessageEvent(String message) {
		SwitchBoardMessageEvent evt = new SwitchBoardMessageEvent(this, message);
		fireMyEvent(evt);
	}

	private void SendMessageExceptionEvent(String message) {
		SwitchboardMessageExceptionEvent evt = new SwitchboardMessageExceptionEvent(
				this, message);
		fireMyEvent(evt);
	}

	public void Stop() throws IOException {
		running = false;

		this.shutdown();
	}

	// Write messages out to the connected port.
	public void Write(String message) throws IOException {

		out.write(message.toCharArray());
		out.append((char)0);
		out.flush();
	}

	private void initialize() throws IOException, InterruptedException {
		try {
			if (socket != null && socket.isConnected()) {
				shutdown();
			}

			if (timeout == -1)
			{
				InetAddress address = InetAddress.getByName(serverName);
				socket = new Socket(address, portNumber);
			}
			else
			{
				SocketAddress sockaddr = new InetSocketAddress(serverName, portNumber);
				socket = new Socket();
				socket.connect(sockaddr, timeout);
			}
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Setup running. and kick off the bufferedReader.

			running = true;
			readInputSocket();
			
			//this.Write("<administrator/>");
			//this.Write("Registering as Administrator");

			//SendMessageEvent("Switchboard Library initialize completed. Server:" + this.serverName + " Port:" + this.portNumber);

		} catch (UnknownHostException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	public BufferedReader getReader() {
		return this.in;
	}

	public BufferedWriter getWriter() {
		return this.out;
	}

	private void shutdown() throws IOException {
		running = false;

		out.close();
		in.close();
		socket.close();
	}

	public String getServerName() {
		return this.serverName;
	}

	public void setServerName(String serverName) throws IOException,
			InterruptedException {
		this.serverName = serverName;
		// If this changed, then we have to re-initialize.
		initialize();
	}

	public int getPortNumber() {
		return this.portNumber;
	}

	public void setPortNumber(int portNumber) throws IOException,
			InterruptedException {
		this.portNumber = portNumber;
		initialize();
	}

	public boolean getRunning() {
		return this.running;
	}

	Runnable r1 = new Runnable() {

		public void run() {
			int matchIdx = 0;
					
			while (running) {
				int a;
				StringBuilder instr = new StringBuilder();
				try {
					while (matchIdx < terminator.length) {
						a = in.read();
						if (a == -1) {
							// The socket was closed! Uh oh....!
							SendMessageExceptionEvent("Socket Closed!");
							running = false;							
							break;
						}
						else
						{
							// We expect strings, so don't add zero values to our receive buffer.
							if (a != 0)
								instr.append((char) a);
						}
						
						// Check if the received character is the next expected terminator character.
						if (terminator[matchIdx] == a) {
							matchIdx++;
						} else {
							matchIdx = 0;
						}
						
						Thread.yield();
					}
					
					// reset for the next match
					matchIdx = 0;
					
					if (instr.length() > 0)
					{
						SendMessageEvent(instr.toString());
					}
				} catch (IOException ex) {
					SendMessageExceptionEvent(ex.getMessage());
					running = false;
					break;
				} catch (Exception e) {
					SendMessageExceptionEvent(e.getMessage());
					running = false;
					break;
				}
				Thread.yield();
			}
		}
	};

}
