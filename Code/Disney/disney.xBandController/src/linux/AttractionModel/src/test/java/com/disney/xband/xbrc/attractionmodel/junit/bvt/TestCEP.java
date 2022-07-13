package com.disney.xband.xbrc.attractionmodel.junit.bvt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.disney.xband.xbrc.attractionmodel.CEP;

public class TestCEP
{
	
	@Test
	public void testBandsPresentAtLocactions()
	{
		CEP cep = new CEP();
		
		Set<Long> locationIds = new HashSet<Long>();
		locationIds.add(14L);
		
		List<String> recipients = cep.getBandsPresentAtLocations(locationIds);
		
		if (recipients != null)
		{
			for (String s: recipients)
				System.out.println("band: " + s);
		}
	}

}
