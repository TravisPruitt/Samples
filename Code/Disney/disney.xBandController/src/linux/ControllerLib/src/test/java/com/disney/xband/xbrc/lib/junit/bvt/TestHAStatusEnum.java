package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.*;

import org.junit.Test;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;

public class TestHAStatusEnum
{
	@Test
	public void getStatus(){
		
		assertTrue(HAStatusEnum.getStatus(null) == HAStatusEnum.unknown);
		assertTrue(HAStatusEnum.getStatus("wrong-type") == HAStatusEnum.unknown);
		assertTrue(HAStatusEnum.getStatus("    ") == HAStatusEnum.unknown);
		assertTrue(HAStatusEnum.getStatus("unknown") == HAStatusEnum.unknown);
		assertTrue(HAStatusEnum.getStatus("master") == HAStatusEnum.master);
		assertTrue(HAStatusEnum.getStatus("slave") == HAStatusEnum.slave);
	}
	
	@Test
	public void testCompareTo()
	{
		HAStatusEnum unknown = HAStatusEnum.unknown;
		HAStatusEnum master = HAStatusEnum.master;
		HAStatusEnum slave = HAStatusEnum.slave;
		HAStatusEnum solo = HAStatusEnum.solo;
		
		assertTrue(HAStatusEnum.compare(null, null) == 0);
		assertTrue(HAStatusEnum.compare(null, unknown) == -1);
		assertTrue(HAStatusEnum.compare(null, solo) == -1);
		assertTrue(HAStatusEnum.compare(null, master) == -1);
		assertTrue(HAStatusEnum.compare(null, slave) == -1);
		
		assertTrue(HAStatusEnum.compare(unknown, null) == 1);
		assertTrue(HAStatusEnum.compare(unknown, unknown) == 0);
		assertTrue(HAStatusEnum.compare(unknown, solo) == -1);
		assertTrue(HAStatusEnum.compare(unknown, master) == -1);
		assertTrue(HAStatusEnum.compare(unknown, slave) == -1);
		
		assertTrue(HAStatusEnum.compare(solo, null) == 1);
		assertTrue(HAStatusEnum.compare(solo, unknown) == 1);
		assertTrue(HAStatusEnum.compare(solo, solo) == 0);
		assertTrue(HAStatusEnum.compare(solo, master) == -1);
		assertTrue(HAStatusEnum.compare(solo, slave) == -1);
		
		assertTrue(HAStatusEnum.compare(master, null) == 1);
		assertTrue(HAStatusEnum.compare(master, unknown) == 1);
		assertTrue(HAStatusEnum.compare(master, solo) == 1);
		assertTrue(HAStatusEnum.compare(master, master) == 0);
		assertTrue(HAStatusEnum.compare(master, slave) == -1);
		
		assertTrue(HAStatusEnum.compare(slave, null) == 1);
		assertTrue(HAStatusEnum.compare(slave, unknown) == 1);
		assertTrue(HAStatusEnum.compare(slave, solo) == 1);
		assertTrue(HAStatusEnum.compare(slave, master) == 1);
		assertTrue(HAStatusEnum.compare(slave, slave) == 0);
		
	}

}
