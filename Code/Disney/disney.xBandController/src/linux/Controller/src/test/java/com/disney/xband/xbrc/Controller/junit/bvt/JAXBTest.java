package com.disney.xband.xbrc.Controller.junit.bvt;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.disney.xband.xbrc.Controller.model.UpdateStreamInfo;
import com.disney.xband.common.lib.XmlUtil;

public class JAXBTest
{
	@Test
	public void testUpdateStreamInfo() throws JAXBException
	{
		UpdateStreamInfo info = new UpdateStreamInfo();
		info.setAfter(5);
		info.setInterval(10);
		info.setMax(100);
		info.setPreferredGuestIdType("test");
		info.setUrl("http://test.com:8080");
		info.setMessageTypes(new LinkedList<String>());
		info.getMessageTypes().add("METRICS");
		info.getMessageTypes().add("LOAD");
		
		String xml = XmlUtil.convertToXml(info, info.getClass());
		assertTrue(xml != null);
		
		UpdateStreamInfo info2 = new UpdateStreamInfo();
		XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), info2.getClass());
	}
}
