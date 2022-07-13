package com.disney.xband.xbrms.junit.bvt;

import java.util.Date;

import org.junit.Test;

import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.common.model.XbrcDto;

public class TestHealthTimeTime
{
	private void testFormatAgo(long ms) {
		XbrcDto dto = new XbrcDto();
		dto.setLastDiscovery(new Date(new Date().getTime() - ms));
		
		HealthItem hi = new HealthItem(dto);
		String last = hi.getLastDiscoveryAgo();
		System.out.println(last);
	}
	
	@Test
	public void testFormatAgo() {
		// 1 sec
		System.out.println("1 sec");
		testFormatAgo(1000l);
		// 5 sec
		System.out.println("5 sec");
		testFormatAgo(5000l);
		// 2 min
		System.out.println("2 min");
		testFormatAgo(125000l);
		// 1 hour
		System.out.println("1 hour");
		testFormatAgo(3600020l);
		// 2 days
		System.out.println("2 days");
		testFormatAgo(172800030);
		// 7 days
		System.out.println("7 days");
		testFormatAgo(604800000);
		// 2 weeks
		System.out.println("2 weeks");
		testFormatAgo(1209600020);
	}
}
