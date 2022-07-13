package com.disney.xband.xbrc.attractionmodel.junit.bvt;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_COMMAND;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.lib.xbrapi.XbrBandCommands;

public class TestXbrCommand
{
	@Before
	public void setUp() throws Exception
	{
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void testXbrBandCommands() throws UnsupportedEncodingException
	{
		XbrBandCommands cmds = new XbrBandCommands();
		cmds.setListCommands(new ArrayList<XbrBandCommand>());

		XbrBandCommand cmd1 = new XbrBandCommand();
		Set<Long> recipients = new HashSet<Long>();
		recipients.add(1L);
		cmd1.setRecipients(recipients);
		List<String> liBands = new ArrayList<String>();
		liBands.add("12345");
		liBands.add("67890");
		cmd1.setListLRIDs(liBands);
		cmd1.setMode(XMIT_MODE.REPLY);
		cmd1.setThreshold(-80);
		cmd1.setCommand(XMIT_COMMAND.FAST_PING);
		cmds.getListCommands().add(cmd1);
		
		XbrBandCommand cmd2 = new XbrBandCommand();
		liBands = new ArrayList<String>();
		liBands.add("ALL");
		cmd2.setListLRIDs(liBands);
		cmd2.setMode(XMIT_MODE.BROADCAST);
		cmd2.setThreshold(-70);
		cmd2.setCommand(XMIT_COMMAND.FAST_RX_ONLY);
		cmds.getListCommands().add(cmd2);
		
		try
		{
			// convert to xml
			String xml = XmlUtil.convertToXml(cmds,  XbrBandCommands.class);

			// convert back
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			XbrBandCommands cmds2 = (XbrBandCommands) XmlUtil.convertToPojo(bais, XbrBandCommands.class);
			
			if (cmds2.getListCommands().size()!=2)
				fail();
			
			XbrBandCommand cmd1a = cmds.getListCommands().get(0);
			XbrBandCommand cmd2a = cmds.getListCommands().get(1);
			
			// compare
			if ( (cmd1.getListLRIDs().size() != cmd1a.getListLRIDs().size()) ||
				 !(cmd1.getListLRIDs().get(0).equals(cmd1a.getListLRIDs().get(0))) ||
				 !(cmd1.getListLRIDs().get(1).equals(cmd1a.getListLRIDs().get(1))) ||
				 (cmd1.getMode() != cmd1a.getMode()) ||
				 (cmd1.getThreshold() != cmd1a.getThreshold()))
			{
				System.err.println("XbrBandCommand Serialization/deserialization failure");
				fail();
			}
			
			// compare
			if ( (cmd2.getListLRIDs().size() != cmd2a.getListLRIDs().size()) ||
				 !(cmd2.getListLRIDs().get(0).equals(cmd2a.getListLRIDs().get(0))) ||
				 (cmd2.getMode() != cmd2a.getMode()) ||
				 (cmd2.getThreshold() != cmd2a.getThreshold()))
			{
				System.err.println("XbrBandCommand Serialization/deserialization failure");
				fail();
			}
			
			// now, serialize as JSON
			String s = ReaderApi.serializeBandCommands(cmds.getListCommands());
			s.length();
		}
		catch (JAXBException e)
		{
			System.err.println("Error: " + e);
			e.printStackTrace();
			fail();
		}
	}

}
