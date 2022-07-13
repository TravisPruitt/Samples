package com.disney.socket.library;



public class SocketRunner extends Thread {

	Client client;
	boolean running = false;
	
	
	public SocketRunner(Client client)
	{
		this.client = client;
		running = true;
	}
	
	public void run(){
		while (running)
		{
			
		}

	}
}
