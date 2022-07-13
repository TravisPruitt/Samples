package com.disney.xband.xbrc.lib.junit.bvt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class TestLocationInfo
{
	boolean isDebug = false;
	
	@Before
	public void prepare(){
		isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
			    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

	}

	@Test
	public void testSort_ByReaderHealth()
	{
		List<LocationInfo> before = new ArrayList<LocationInfo>();
		LocationInfo sampleLocationInfo = new LocationInfo();
		
		initialize(before);

		System.out.println("Before the sort:");
		print(before);
		
		Collections.sort(before, sampleLocationInfo.new ByReaderHealth());
		
		System.out.println("After the sort:");
		print(before);
	}
	
	private void initialize(List<LocationInfo> before){
		LocationInfo li1 = new LocationInfo();
		li1.setName("C Attraction");
		
		List<ReaderInfo> li1Readers = new ArrayList<ReaderInfo>();
		ReaderInfo li1r1 = new ReaderInfo();
		li1r1.setStatus(StatusType.Green);
		li1Readers.add(li1r1);
		ReaderInfo li1r2 = new ReaderInfo();
		li1r2.setStatus(StatusType.Green);
		li1Readers.add(li1r2);
		ReaderInfo li1r3 = new ReaderInfo();
		li1r3.setStatus(StatusType.Red);
		li1Readers.add(li1r3);
		ReaderInfo li1r4 = new ReaderInfo();
		li1r4.setStatus(StatusType.Yellow);
		li1Readers.add(li1r4);
		ReaderInfo li1r5 = new ReaderInfo();
		li1r5.setStatus(StatusType.Green);
		li1Readers.add(li1r5);
		ReaderInfo li1r6 = new ReaderInfo();
		li1r6.setStatus(StatusType.Red);
		li1Readers.add(li1r6);
		
		li1.setReaders(li1Readers);
		
		before.add(li1);
		
		before.add(new LocationInfo());
		
		LocationInfo li2 = new LocationInfo();
		li2.setName("A Attraction");
		
		List<ReaderInfo> li2Readers = new ArrayList<ReaderInfo>();
		ReaderInfo li2r1 = new ReaderInfo();
		li2r1.setStatus(StatusType.Green);
		li2Readers.add(li2r1);
		ReaderInfo li2r2 = new ReaderInfo();
		li2r2.setStatus(StatusType.Green);
		li2Readers.add(li2r2);
		
		li2.setReaders(li2Readers);
		
		before.add(li2);
		
		LocationInfo li3 = new LocationInfo();
		li3.setName("G Attraction");
		
		List<ReaderInfo> li3Readers = new ArrayList<ReaderInfo>();
		ReaderInfo li3r1 = new ReaderInfo();
		li3r1.setStatus(StatusType.Green);
		li3Readers.add(li3r1);
		ReaderInfo li3r2 = new ReaderInfo();
		li3r2.setStatus(StatusType.Yellow);
		li3Readers.add(li3r2);
		
		li3.setReaders(li3Readers);
		
		before.add(li3);
		
		
		LocationInfo li4 = new LocationInfo();
		li4.setName("B Attraction");
		
		List<ReaderInfo> li4Readers = new ArrayList<ReaderInfo>();
		ReaderInfo li4r1 = new ReaderInfo();
		li4r1.setStatus(StatusType.Green);
		li4Readers.add(li4r1);
		ReaderInfo li4r2 = new ReaderInfo();
		li4r2.setStatus(StatusType.Yellow);
		li4Readers.add(li4r2);
		
		li4.setReaders(li4Readers);
		
		before.add(li4);
	}
	
	private void print(List<LocationInfo> list){
		if (!isDebug)
			return;
		
		if (list == null || list.size() == 0)
			return;
		
		for (LocationInfo li : list){
			System.out.println(li.getName());
		}
	}

}
