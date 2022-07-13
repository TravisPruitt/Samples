package com.disney.xband.xbrc.parkentrymodel.junit.bvt;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.CastMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.GuestMessagePayload;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.PECastMessage;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.PEGuestMessage;
import com.disney.xband.xbrc.parkentrymodel.PEMessageGenerator;

public class JmsMsgTest
{
	@Test
	public void testPECastMessage() throws JAXBException
	{
		PECastMessage msg = new PECastMessage();		
		msg.setMessageType(PEMessageGenerator.TYPE_CAST_OPEN);
		msg.setTimestamp(PEMessageGenerator.formatTime(new Date()));
		msg.setLocationName("testLocation");
		msg.setPortalId("cast123");		
	
		String xml = XmlUtil.convertToPartialXml(msg, PECastMessage.class);
	
		PECastMessage msg2 = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), PECastMessage.class);
		
		assertEquals(msg.getMessageType(), msg2.getMessageType());
		assertEquals(msg.getLocationName(), msg2.getLocationName());
		assertEquals(msg.getPortalId(), msg2.getPortalId());
	}
	
	@Test
	public void testPEGuestMessage() throws JAXBException
	{
		PEGuestMessage msg = new PEGuestMessage();
		msg.setMessageType(PEMessageGenerator.TYPE_HASENTERED);
		msg.setTimestamp(PEMessageGenerator.formatTime(new Date()));
		
		String xml = XmlUtil.convertToPartialXml(msg, PEGuestMessage.class);
		
		PEGuestMessage msg2 = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), PEGuestMessage.class);
		
		assertEquals(msg.getMessageType(), msg2.getMessageType());
	}
	
	@Test
	public void testCastPayload() throws JAXBException, ClassNotFoundException
	{
		CastMessagePayload payload = new CastMessagePayload();
		
		PECastMessage msg = new PECastMessage();		
		msg.setMessageType(PEMessageGenerator.TYPE_CAST_OPEN);
		msg.setTimestamp(PEMessageGenerator.formatTime(new Date()));
		msg.setLocationName("testLocation");
		msg.setPortalId("cast123");		
	
		payload.setMessage(msg);
		
		String xml = XmlUtil.convertToXml(payload, CastMessagePayload.class);
		
		CastMessagePayload payload2 = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), MessagePayload.class, CastMessagePayload.class);
		
		assertNotNull(payload2.getMessage());
	}
	
	@Test
	public void testGuestPayload() throws JAXBException, ClassNotFoundException
	{
		GuestMessagePayload payload = new GuestMessagePayload();
		
		PEGuestMessage msg = new PEGuestMessage();
		msg.setMessageType(PEMessageGenerator.TYPE_HASENTERED);
		msg.setTimestamp(PEMessageGenerator.formatTime(new Date()));
	
		payload.setMessage(msg);
		
		String xml = XmlUtil.convertToXml(payload, GuestMessagePayload.class);
		
		GuestMessagePayload payload2 = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), MessagePayload.class, GuestMessagePayload.class);
		
		assertNotNull(payload2.getMessage());
	}
}
