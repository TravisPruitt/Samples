package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.*;

import org.junit.Test;

import com.disney.xband.lib.xbrapi.XbrBandCommand;

public class TestXbrBandCommand
{

	@Test
	public void testFormatInHex()
	{
		// slow ping
		String slowPing = XbrBandCommand.XMIT_COMMAND.SLOW_PING.formatInHex(1000, 123123123);
		confim(XbrBandCommand.XMIT_COMMAND.SLOW_PING, 1000, 123123123, slowPing, "020001000708");
		
		slowPing = XbrBandCommand.XMIT_COMMAND.SLOW_PING.formatInHex(1, 1);
		confim(XbrBandCommand.XMIT_COMMAND.SLOW_PING, 1, 1, slowPing, "020001000708");
		
		slowPing = XbrBandCommand.XMIT_COMMAND.SLOW_PING.formatInHex(Integer.MAX_VALUE, Integer.MAX_VALUE);
		confim(XbrBandCommand.XMIT_COMMAND.SLOW_PING, Integer.MAX_VALUE, Integer.MAX_VALUE, slowPing, "020001000708");
		
		// fast receive
		String fastRx = XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY.formatInHex(1000, 10800000);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY, 1000, 10800000, fastRx, "010001002A30");
		
		fastRx = XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY.formatInHex(1000, 1800000);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY, 1000, 1800000, fastRx, "010001000708");
		
		fastRx = XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY.formatInHex(1000, 3600000);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY, 1000, 3600000, fastRx, "010001000E10");
		
		fastRx = XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY.formatInHex(1, 1);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY, 1, 1, fastRx, "010000000000");
		
		fastRx = XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY.formatInHex(Integer.MAX_VALUE, Integer.MAX_VALUE);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_RX_ONLY, Integer.MAX_VALUE, Integer.MAX_VALUE, fastRx, "01C49B40C49B");
		
		// fast ping
		String fastPing = XbrBandCommand.XMIT_COMMAND.FAST_PING.formatInHex(100, 30000);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_PING, 100, 30000, fastPing, "0300000A001E");
		
		fastPing = XbrBandCommand.XMIT_COMMAND.FAST_PING.formatInHex(690, 30000);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_PING, 690, 30000, fastPing, "03000045001E");
		
		fastPing = XbrBandCommand.XMIT_COMMAND.FAST_PING.formatInHex(1023, 30000);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_PING, 1023, 30000, fastPing, "03000102001E");
		
		fastPing = XbrBandCommand.XMIT_COMMAND.FAST_PING.formatInHex(1000, 1000);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_PING, 1000, 1000, fastPing, "030001000001");
		
		fastPing = XbrBandCommand.XMIT_COMMAND.FAST_PING.formatInHex(1, 1);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_PING, 1, 1, fastPing, "030000000000");
		
		fastPing = XbrBandCommand.XMIT_COMMAND.FAST_PING.formatInHex(Integer.MAX_VALUE, Integer.MAX_VALUE);
		confim(XbrBandCommand.XMIT_COMMAND.FAST_PING, Integer.MAX_VALUE, Integer.MAX_VALUE, fastPing, "03C49B40C49B");
	}
	
	private void confim(XbrBandCommand.XMIT_COMMAND command, Integer frequency, Integer timeout, String result, String expected){
		
		if (frequency == null || timeout == null || result == null || expected == null)
			fail("Null value(s) passed to confirm()");
		
		boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
			    
		if (isDebug)	    
			System.out.println(command.name() + " with frequency of " 
					+ frequency.toString() + ", timeout of " + timeout.toString() + " = " + result);
		
		assertTrue(result.equals(expected));
	}

}
