package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_COMMAND;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.lib.xbrapi.XbrBandCommands;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

/*
 * The primary purpose of these tests is to make sure that the objects serialized using JAXB
 * have all the valid @Xml.. tags required for JAXB serialization to JSON/XML and back into objects.
 */
public class TestJaxbSerialization
{
	@Test
	public void testStatus() throws JAXBException
	{
		XbrcStatus status = new XbrcStatus();
		status.getPerfEKGWriteMsec().processValue(10.0);
		status.getPerfEKGWriteMsec().processValue(20.0);
		status.getPerfEKGWriteMsec().clear();
		
		assertEquals(status.getPerfEKGWriteMsec().getMin(),new Double(10.0));
		assertEquals(status.getPerfEKGWriteMsec().getMax(),new Double(20.0));
		assertEquals(status.getPerfEKGWriteMsec().getMean(),new Double(15.0));
		
		String xml = XmlUtil.convertToXml(status, XbrcStatus.class);
		
		XbrcStatus status2 = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), XbrcStatus.class);
		
		assertEquals(status.getPerfEKGWriteMsec().getMax(), status2.getPerfEKGWriteMsec().getMax());
		assertEquals(status.getPerfEKGWriteMsec().getMin(), status2.getPerfEKGWriteMsec().getMin());
		assertEquals(status.getPerfEKGWriteMsec().getMean(), status2.getPerfEKGWriteMsec().getMean());
		assertEquals(status.getPerfEKGWriteMsec().getVersion(), status2.getPerfEKGWriteMsec().getVersion());
	}
	
	@Test
	public void testReaderLocationInfo() throws JAXBException
	{
		ReaderLocationInfo info = new ReaderLocationInfo();
		info.setName("Test");
		info.setTime("2011-01-01 Z 10:00:00.000");

		List<LocationInfo> locInfoList = new LinkedList<LocationInfo>();
		info.setReaderlocationinfo(locInfoList);
		
		LocationInfo li = new LocationInfo();
		locInfoList.add(li);
		
		li.setId(1);
		li.setLocationTypeID(1);
		li.setLocationTypeName("Entry");
		li.setName("Test Location");
		li.setSingulationTypeID(0);
		li.setX(0);
		li.setY(0);
		li.setSuccessSequence("success");
		li.setFailureSequence("failure");
		li.setErrorSequence("failure");
		li.setIdleSequence("idle");
		
		List<ReaderInfo> readers = new LinkedList<ReaderInfo>();
		li.setReaders(readers);
		
		ReaderInfo ri = new ReaderInfo();
		readers.add(ri);
		
		ri.setId(1);
		ri.setDeviceId(1);
		ri.setGain(0);
		ri.setIpAddress("10.0.0.0");
		ri.setTransmitter(true);
		ri.setTransmitPayload("");
		ri.setLane(1);
		ri.setLastIDReceived(0);
		ri.setLocation(li);
		ri.setMacAddress("00:00:00:00:00:00");
		ri.setMinXbrcVersion("1.0.0.0");
		ri.setName("Test-reader");
		ri.setPort(8080);
		ri.setStatus(StatusType.Green);
		ri.setType(ReaderType.lrr);
		ri.setX(0);
		ri.setY(0);
		
		XbrBandCommand command = new XbrBandCommand();
		command.setCommand(XMIT_COMMAND.FAST_PING);
		command.setInterval(null);
		command.setTimeout(null);
		command.setMode(XMIT_MODE.REPLY);
		command.setThreshold(-65);
		command.setReader(23L);
		
		Set<Long> recipients = new HashSet<Long>();
		recipients.add(89L);
		command.setRecipients(recipients);
		
		List<String> rfids = new LinkedList<String>();
		rfids.add("FF40FF3C00");
		rfids.add("FF40FF3C01");
		rfids.add("FF40FF3C02");
		command.setListLRIDs(rfids);
		
		XbrBandCommand command1 = new XbrBandCommand();
		command1.setCommand(XMIT_COMMAND.FAST_RX_ONLY);
		command1.setInterval(null);
		command1.setTimeout(null);
		command1.setMode(XMIT_MODE.BROADCAST);
		command1.setThreshold(-65);
		command1.setReader(23L);
		Set<Long> recipients1 = new HashSet<Long>();
		recipients1.add(89L);
		recipients1.add(2L);
		command.setRecipients(recipients1);
	
		String xml = XmlUtil.convertToXml(info, ReaderLocationInfo.class);
		ReaderLocationInfo info2 = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), ReaderLocationInfo.class);
		String xml2 = XmlUtil.convertToXml(info2, ReaderLocationInfo.class);
		assertTrue(xml.equals(xml2));
		
	}
	
	@Test
	public void testXbrBandCommand() throws Exception
	{
		XbrBandCommand command = new XbrBandCommand();
		command.setCommand(XMIT_COMMAND.FAST_PING);
		command.setInterval(null);
		command.setTimeout(null);
		command.setMode(XMIT_MODE.REPLY);
		command.setThreshold(-65);
		command.setReader(23L);
		Set<Long> recipients = new HashSet<Long>();
		recipients.add(89L);
		command.setRecipients(recipients);
		
		List<String> rfids = new LinkedList<String>();
		rfids.add("FF40FF3C00");
		rfids.add("FF40FF3C01");
		rfids.add("FF40FF3C02");
		command.setListLRIDs(rfids);
		
		List<XbrBandCommand> cmdColl = new LinkedList<XbrBandCommand>();
		cmdColl.add(command);
		
		String jsonPayload = ReaderApi.serializeBandCommands(cmdColl);
		
		String expectedPayload = "{\"commands\":[{\"XLRID\":[\"FF40FF3C00\",\"FF40FF3C01\",\"FF40FF3C02\"],\"cmd\":\"0300000A001E\"}]}";

		String generatedPayload = jsonPayload.trim().replace("\n", "").replace(" ", ""); 
		//System.out.println("Expected:  " + expectedPayload);
		//System.out.println("Generated: " + generatedPayload);
		assertTrue(generatedPayload.equals(expectedPayload));
	}
	
	@Test
	public void testXbrBandCommandList() throws Exception
	{
		XbrBandCommand command = new XbrBandCommand();
		command.setCommand(XMIT_COMMAND.FAST_PING);
		command.setInterval(null);
		command.setTimeout(null);
		command.setMode(XMIT_MODE.REPLY);
		command.setThreshold(-65);
		command.setReader(23L);
		Set<Long> recipients = new HashSet<Long>();
		recipients.add(89L);
		command.setRecipients(recipients);
		
		List<String> rfids = new LinkedList<String>();
		rfids.add("FF40FF3C00");
		rfids.add("FF40FF3C01");
		rfids.add("FF40FF3C02");
		command.setListLRIDs(rfids);
		
		List<XbrBandCommand> cmdColl = new LinkedList<XbrBandCommand>();
		cmdColl.add(command);
		
		XbrBandCommands commands = new XbrBandCommands();
		commands.setListCommands(cmdColl);
		
		String xml = XmlUtil.convertToPartialXml(commands, XbrBandCommands.class);
		
		System.out.println(xml);
	}
}
