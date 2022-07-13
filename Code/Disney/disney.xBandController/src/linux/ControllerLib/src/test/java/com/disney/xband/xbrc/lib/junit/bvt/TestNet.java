package com.disney.xband.xbrc.lib.junit.bvt;

import java.io.IOException;
import java.net.DatagramPacket;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.xbrc.lib.net.BroadcastListener;

@SuppressWarnings("deprecation")
public class TestNet extends TestCase {
	private String data;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		
	}
		
	@Test
	public void testBroadcast() throws IOException {
		/*
		 * This test tests UDP broadcast which at the moment is not used anywhere.
		 *
		data = "test message";
		
		// Create the listener to listen for our broadcast
		TestBroadcastListener listener = new TestBroadcastListener(port);
		listener.listen();
		
		// Create the sender and broadcast a message
		BroadcastSender sender = new BroadcastSender(port);
		
		synchronized (data) {
			sender.sendToAll(data, "");
		}

		// Sleep for a while to allow the listener thread to receive the message.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			fail("Sleep was interrupted.");
		}
		
		listener.finish();
		
		assert(dataReceived);
		*/
	}
	
	class TestBroadcastListener extends BroadcastListener {

		public TestBroadcastListener(int port) throws IOException {
			super(port, 1024);
		}
		
		@Override
		public void onReceivePacket(DatagramPacket packet) {
			synchronized (data) {				
				if (data.equals(new String(packet.getData())))
				{
				}
			}
		}
	}
}
