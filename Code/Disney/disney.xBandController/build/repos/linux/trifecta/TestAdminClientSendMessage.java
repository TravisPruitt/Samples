package com.disney.nge.socket;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class TestAdminClientSendMessage {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if ( args.length != 3 ) {
				System.out.println("The following arguments are required: socketServer socketPort message");
				System.exit(0);
			}
			
			String socketServer = args[0];
			Integer socketPort = Integer.valueOf(args[1]);
			String message = args[2];
			
			System.out.println("Creating socket " + socketServer + ":" + socketPort + "...");
			Socket skt = new Socket(socketServer, socketPort);
			Writer out  = new OutputStreamWriter(skt.getOutputStream(), "UTF-8");
			
			System.out.println("Connecting as an administrator...");
			String handshakeMessage = "<administrator/>";
			out.write(handshakeMessage.toCharArray());
			out.append((char)0);
			out.flush();
			
			System.out.println("Sending the following message to the socket: " + message);
			out.write(message.toCharArray());
			out.append((char)0);
			out.flush();
			
			System.out.println("Finished socket test.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
