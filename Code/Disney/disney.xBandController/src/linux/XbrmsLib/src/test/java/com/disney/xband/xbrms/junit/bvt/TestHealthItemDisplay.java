package com.disney.xband.xbrms.junit.bvt;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.disney.xband.xbrms.common.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;

public class TestHealthItemDisplay
{
	boolean isDebug = false;
	
	@Before
	public void setUp() throws Exception
	{
		isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
			    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testXbrcByVenueComparator()
	{
		Map<String,Collection<HealthItem>> healthItemsMap = new HashMap<String, Collection<HealthItem>>();
		
		Collection<HealthItem> healthItems = new LinkedList<HealthItem>();
		healthItemsMap.put(XbrcDto.class.getName(), healthItems);
		healthItemsMap.put(IdmsDto.class.getName(), new LinkedList<HealthItem>());
		
		/*
		 * When adding new HealthItems to this test, make the first 
		 * number in their ip address equal to the index position
		 * this HealthItem will occupy after the sort is performed.
		 * This will save you having to modify the assertion part
		 * of this test.
		 */
		HealthItemDto hi1 = new XbrcDto();
		hi1.setActive(true);
		hi1.setIp("2.1.1.1");
		hi1.setPort(8080);
		((XbrcDto)hi1).setFacilityId("Princess Meet and Greet");
		((XbrcDto)hi1).setHaStatus(HAStatusEnum.slave.name());
		healthItems.add(new HealthItem(hi1));
		
		HealthItemDto hi2 = new XbrcDto();
		hi2.setActive(true);
		hi2.setIp("1.1.1.1");
		hi2.setPort(8080);
		((XbrcDto)hi2).setFacilityId("Princess Meet and Greet");
		((XbrcDto)hi2).setHaStatus(HAStatusEnum.master.name());
		healthItems.add(new HealthItem(hi2));
		
		HealthItemDto hi3 = new XbrcDto();
		hi3.setActive(true);
		hi3.setIp("0.1.1.1");
		hi3.setPort(8080);
		((XbrcDto)hi3).setFacilityId("Hunted Mansion");
		((XbrcDto)hi3).setHaStatus(HAStatusEnum.unknown.name());
		healthItems.add(new HealthItem(hi3));
		
		HealthItemDto hi4 = new XbrcDto();
		hi4.setActive(true);
		hi4.setIp("3.1.1.1");
		hi4.setPort(8080);
		((XbrcDto)hi4).setFacilityId("Tower of Terror");
		((XbrcDto)hi4).setHaStatus(null);
		healthItems.add(new HealthItem(hi4));
		
		if (isDebug)
			printCollection(healthItemsMap, "Before");
		
		List<HealthItem> current = null;
		for (String key : healthItemsMap.keySet()){
			if (key.equals(XbrcDto.class.getName())){
				// sort xBRC according to venue id and then master/slave ha indication
				current = (List<HealthItem>)healthItemsMap.get(key);
				
				if (current != null && current.size() > 0) {
					HealthItem display = current.iterator().next();
					
					Collections.sort(current, new Comparators.XbrcByVenueAndHAStatusComparator());
				}
			}
		}
		
		if (isDebug)
			printCollection(healthItemsMap, "After sorting by venue and ha status");
		
		verifySorted(healthItemsMap);
		
	}
	
	private void verifySorted(Map<String,Collection<HealthItem>> healthItemsMap){
		List<HealthItem> current = null;
		for (String key : healthItemsMap.keySet()){
			if (key.equals(XbrcDto.class.getName())){
				// sort xBRC according to venue id and then master/slave ha indication
				current = (List<HealthItem>)healthItemsMap.get(key);
				
				if (current != null){
					HealthItemDto hi = null;
					Iterator<HealthItem> iter = current.iterator();
					for (int i = 0; i < current.size(); i++){
						hi = iter.next().getItem();
						String ip = hi.getIp();
						
						if (ip == null)
							fail("HealthItem without an ip. Not able to test sort result.");
						
						assertTrue(Integer.parseInt(ip.substring(0, ip.indexOf("."))) == i);
					}
				}
			}
		}
	}

	private void printCollection(Map<String,Collection<HealthItem>> healthItemsMap, String when){
		StringBuffer collection = new StringBuffer(when + " Health items' order: ");
		List<HealthItem> current = null;
		for (String key : healthItemsMap.keySet()){
			if (key.equals(XbrcDto.class.getName())){
				// sort xBRC according to venue id and then master/slave ha indication
				current = (List<HealthItem>)healthItemsMap.get(key);
				
				if (current != null){
					HealthItemDto hi = null;
					for (HealthItem hid : current){
						hi = hid.getItem();
						if (hid != null){
							collection.append(((XbrcDto)hi).getFacilityId());
							collection.append(":");
							collection.append(((XbrcDto)hi).getHaStatus());
							collection.append(",");
						}
					}
				}
			}
		}
		
		System.out.println(collection);
	}
}
