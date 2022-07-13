package com.disney.xband.xbrms.junit.bvt;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.*;

import javax.xml.bind.JAXBException;

import com.disney.xband.xbrms.common.model.XbrcDto;
import org.junit.Test;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.xbrms.common.model.FacilityListDto;

/*
 * The primary purpose of these tests is to make sure that the objects serialized using JAXB
 * have all the valid @Xml.. tags required for JAXB serialization to JSON/XML and back into objects.
 */
public class TestJaxbSerialization
{
	@Test
	public void testRestFacilities() throws JAXBException
	{
		FacilityListDto xbrms = new FacilityListDto();
		
		XbrcDto xbrcDto = new XbrcDto();
		xbrcDto.setHostname("testhost");
		xbrcDto.setId(10l);
		xbrcDto.setIp("10.75.2.101");
		xbrcDto.setLastDiscovery(new Date());
		xbrcDto.setNextDiscovery(new Date());
		xbrcDto.setName("Test Name");
		xbrcDto.setPort(8080);
		xbrcDto.setName("test name");
		xbrcDto.setFacilityId("test");
		xbrcDto.setVersion("10.10.10");
		xbrcDto.setActive(Boolean.TRUE);
		
		List<XbrcDto> facilities = new LinkedList<XbrcDto>();
		xbrms.setFacility(facilities);
		facilities.add(xbrcDto);
		Calendar calendar = Calendar.getInstance();

		String xml = null;
		try
		{
			xml = XmlUtil.convertToXml(xbrms, FacilityListDto.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
		
		FacilityListDto xbrms2 = null;
		try
		{
			xbrms2 = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), FacilityListDto.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
