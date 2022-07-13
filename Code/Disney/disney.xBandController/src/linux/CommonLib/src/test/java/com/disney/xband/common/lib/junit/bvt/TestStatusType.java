package com.disney.xband.common.lib.junit.bvt;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.disney.xband.common.lib.health.StatusType;

public class TestStatusType
{

	@Test
	public void testCompare()
	{
		assertTrue(StatusType.compare(null, null) == 0);
		assertTrue(StatusType.compare(null, StatusType.Red) == 1);
		assertTrue(StatusType.compare(null, StatusType.Yellow) == 1);
		assertTrue(StatusType.compare(null, StatusType.Green) == 1);
		
		assertTrue(StatusType.compare(StatusType.Red, null) == -1);
		assertTrue(StatusType.compare(StatusType.Red, StatusType.Red) == 0);
		assertTrue(StatusType.compare(StatusType.Red, StatusType.Yellow) == -1);
		assertTrue(StatusType.compare(StatusType.Red, StatusType.Green) == -1);
		
		assertTrue(StatusType.compare(StatusType.Yellow, null) == -1);
		assertTrue(StatusType.compare(StatusType.Yellow, StatusType.Red) == 1);
		assertTrue(StatusType.compare(StatusType.Yellow, StatusType.Yellow) == 0);
		assertTrue(StatusType.compare(StatusType.Yellow, StatusType.Green) == -1);
		
		assertTrue(StatusType.compare(StatusType.Green, null) == -1);
		assertTrue(StatusType.compare(StatusType.Green, StatusType.Red) == 1);
		assertTrue(StatusType.compare(StatusType.Green, StatusType.Yellow) == 1);
		assertTrue(StatusType.compare(StatusType.Green, StatusType.Green) == 0);
	}

}
