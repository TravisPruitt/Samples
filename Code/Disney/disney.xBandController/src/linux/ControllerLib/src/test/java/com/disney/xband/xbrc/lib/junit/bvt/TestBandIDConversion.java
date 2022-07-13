package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.*;

import org.junit.Test;

import com.disney.xband.lib.xbrapi.LrrEvent;
import com.disney.xband.lib.xbrapi.TapEvent;

public class TestBandIDConversion
{
	@Test
	public void testLrr()
	{
		LrrEvent lrrEvent = new LrrEvent();
		lrrEvent.setXlrid("E6DA0BC595");
		assertEquals(lrrEvent.getPidDecimal(), "991500682645");
	}
	
	@Test
	public void testTap()
	{
		TapEvent tapEvent = new TapEvent();
		tapEvent.setPid("E6DA0BC595");
		assertEquals(tapEvent.getPidDecimal(), "991500682645");
		
		tapEvent.setUid("042BC232122080");
		assertEquals(tapEvent.getUidDecimal(), "36064059552049924");
	}
}
