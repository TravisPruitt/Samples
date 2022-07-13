package com.disney.xband.common.lib.junit.scheduler;

import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerItemParameterMetadata;
import com.disney.xband.common.scheduler.SchedulerItemParameterType;
import com.disney.xband.common.scheduler.SchedulerMetadataList;

public class TestMetadataSerialization {
	@Test
	public void testSerialize() throws JAXBException {
		SchedulerMetadataList metadata = new SchedulerMetadataList();
		metadata.setMetadata(new LinkedList<SchedulerItemMetadata>());
		
		SchedulerItemMetadata md = new SchedulerItemMetadata();
	
		metadata.getMetadata().add(md);
		
		md.setName("Database Cleanup Job");
		md.setShortDescription("Cleanup all database tables.");
		md.setShortHtmlDescription("Cleanup <b>all</b> tables.");
		md.setLongHtmlDescription("<h1>Description</h1>This item is used to clean up database.");
		md.setJobClassName("com.disney.xband.common.scheduler.ShellScriptJob");
		md.setDefaultSchedulingExpression("15 30 * * *");
		md.setCanRunConcurrently(false);
		md.setOnlyOnce(false);
		md.setSystemOnly(false);
		
		md.setParameters(new LinkedList<SchedulerItemParameterMetadata>());
		
		SchedulerItemParameterMetadata p1 = new SchedulerItemParameterMetadata();
		p1.setName("nge.xconnect.dbuser");
		p1.setDescription("environment.properties key for the database user to use");
		p1.setRequired(true);
		p1.setSequence(1);
		p1.setType(SchedulerItemParameterType.ENVPROPVALUE);
		md.getParameters().add(p1);
		
		SchedulerItemParameterMetadata p2 = new SchedulerItemParameterMetadata();
		p2.setName("nge.xconnect.dbpassword");
		p2.setDescription("environment.properties key for the database user password");
		p2.setRequired(true);
		p2.setSequence(2);
		p2.setType(SchedulerItemParameterType.ENVPROPVALUE);
		md.getParameters().add(p2);
		
		SchedulerItemParameterMetadata p3 = new SchedulerItemParameterMetadata();
		p3.setName("keepDays");
		p3.setDescription("How many days of data to keep.");
		p3.setRequired(true);
		p3.setDefaultValue("30");
		p3.setSequence(3);
		p3.setType(SchedulerItemParameterType.NUMBER);
		md.getParameters().add(p3);
		
		String xml = XmlUtil.convertToXml(metadata, SchedulerMetadataList.class);
	}
}
