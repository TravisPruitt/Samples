package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.disney.xband.lib.xbrapi.HelloMsg;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.lib.xbrapi.UpdateStream;
import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrJsonMapper;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_COMMAND;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestReaderApi extends ReaderApi
{
	@Test
	public void testSerializeBandCommands()
	{
		List<XbrBandCommand> cmds = new LinkedList<XbrBandCommand>();
		
		XbrBandCommand cmd1 = new XbrBandCommand();
		Set<Long> recipients = new HashSet<Long>();
		recipients.add(1L);
		cmd1.setRecipients(recipients);
		List<String> lisLRIDs = new LinkedList<String>();
		lisLRIDs.add("1212121212");
		lisLRIDs.add("2323232323");
		cmd1.setListLRIDs(lisLRIDs);
		cmd1.setThreshold(-66);
		cmd1.setCommand(XMIT_COMMAND.FAST_PING);
		
		XbrBandCommand cmd2 = new XbrBandCommand();
		cmd2.setThreshold(-55);
		cmd2.setEnableThreshold(true);
		cmd2.setCommand(XMIT_COMMAND.SLOW_PING);
		
		XbrBandCommand cmd3 = new XbrBandCommand();
		Set<Long> recipients3 = new HashSet<Long>();
		recipients3.add(1L);
		cmd3.setRecipients(recipients);
		cmd3.setListLRIDs(new LinkedList<String>());
		cmd3.setThreshold(null);
		cmd3.setCommand(XMIT_COMMAND.FAST_PING);
		
		cmds.add(cmd1);
		cmds.add(cmd2);
		cmds.add(cmd3);
		
		String jsonExpected = "{\"commands\":[{\"XLRID\":[\"1212121212\",\"2323232323\"],\"cmd\":" +
				"\"0300000A001E\"},{\"ss\":-55,\"cmd\":\"020001000708\"}]}";

		String jsonResult = ReaderApi.serializeBandCommands(cmds);
		
		assertTrue(jsonResult != null && jsonResult.trim().length() > 0);
		assertTrue(jsonResult.replace("\n", "").replace(" ", "").equals(jsonExpected));
	}
	
	@Test
	public void testSerializeHello() throws JsonGenerationException, JsonMappingException, IOException
	{
		HelloMsg hm = new HelloMsg();
		hm.setMac("00:00:00:00:00:01");
		hm.setLinuxVersion("liunux 1.3");	
		hm.setNextEno(12l);
		hm.setPort(8080);
		hm.setReaderName("test-reader");
		hm.setReaderType(ReaderType.xfp.toString());
		hm.setReaderVersion("1.0.0.0");
		hm.setMinXbrcVersion("1.0.0.1");
		hm.setHardwareType("xTP1");
		hm.setUpdateStream(new ArrayList<UpdateStream>());
		hm.setTime(new Date());
		
		UpdateStream us = new UpdateStream("http://someurl");
		hm.getUpdateStream().add(us);
		
		us = new UpdateStream("http://anotherurl");
		hm.getUpdateStream().add(us);

		String str = XbrJsonMapper.serializeHello(hm);
		assertNotNull(str);
	}
	
	@Test
	public void testBandIdConversion() {
		assertEquals("00001f5e89",XbrJsonMapper.publicIdToLongRangeId(2055817l));
	}
}
