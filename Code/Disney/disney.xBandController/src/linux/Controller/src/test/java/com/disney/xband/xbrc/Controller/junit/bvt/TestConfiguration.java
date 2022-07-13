package com.disney.xband.xbrc.Controller.junit.bvt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import com.disney.xband.common.lib.ConfigInfo;
import com.disney.xband.xbrc.Controller.ConfigOptions;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.xbrc.lib.model.ControllerInfo;
import com.disney.xband.xbrc.lib.model.XbrcModel;

public class TestConfiguration
{

	@Test
	public void testConvertFrom()
	{
		ControllerInfo info = ConfigOptions.INSTANCE.getControllerInfo();

		Collection<ConfigInfo> configuration = Configuration.convert(info);

		assertTrue(configuration != null);
		assertTrue(configuration.size() > 0);

		for (ConfigInfo ci : configuration)
		{
			if (ci.getName().equals("sVenueName"))
				assertEquals(info.getVenue(), ci.getValue());
			else if (ci.getName().equals("cmsecJMSRetry"))
				assertEquals(info.getJMSRetryPeriod(),
						Integer.parseInt(ci.getValue()));
		}
	}

	@Test
	public void testConvertTo()
	{
		Collection<ConfigInfo> infoColl = new LinkedList<ConfigInfo>();

		ConfigInfo sVenueName = new ConfigInfo();
		sVenueName.setName("venue");
		sVenueName.setType("java.lang.String");
		sVenueName.setValue("Hunted Mansion");
		sVenueName.setDescription("Name of the venue");
		sVenueName.setMin("0");
		sVenueName.setMax("255");
		sVenueName.setChoices(new String[] { "N/A" });
		sVenueName.setConfigClass("ControllerInfo");
		infoColl.add(sVenueName);

		ConfigInfo verbose = new ConfigInfo();
		verbose.setName("verbose");
		verbose.setValue("true");
		verbose.setType("boolean");
		verbose.setDescription("N/A");
		verbose.setMin("N/A");
		verbose.setMax("N/A");
		verbose.setChoices(new String[] { "N/A" });
		verbose.setConfigClass("ControllerInfo");
		infoColl.add(verbose);

		ConfigInfo eventdumpfile = new ConfigInfo();
		eventdumpfile.setName("eventdumpfile");
		eventdumpfile.setValue("/var/logs/eventdumpfile.txt");
		eventdumpfile.setType("java.lang.String");
		eventdumpfile.setDescription("N/A");
		eventdumpfile.setMin("0");
		eventdumpfile.setMax("N/A");
		eventdumpfile.setChoices(new String[] { "N/A" });
		eventdumpfile.setConfigClass("ControllerInfo");
		infoColl.add(eventdumpfile);

		ConfigInfo vModel = new ConfigInfo();
		vModel.setName("model");
		vModel.setType("java.lang.String");
		vModel.setDescription("N/A");
		vModel.setMin("0");
		vModel.setMax("N/A");
		vModel.setChoices(new String[] { "N/A" });
		vModel.setValue(XbrcModel.ATTRACTION.getXbrcModelClass());
		vModel.setConfigClass("ControllerInfo");
		infoColl.add(vModel);

		ConfigInfo metricsperiod = new ConfigInfo();
		metricsperiod.setName("metricsperiod");
		metricsperiod.setValue("3000");
		metricsperiod.setType("int");
		metricsperiod.setDescription("N/A");
		metricsperiod.setMin("0");
		metricsperiod.setMax("24000");
		metricsperiod.setChoices(new String[] { "N/A" });
		metricsperiod.setConfigClass("AttractionModelConfig");
		infoColl.add(metricsperiod);

		ControllerInfo ci = ConfigOptions.INSTANCE.getControllerInfo();
		ci.setVenue(null);
		ci.setVerbose(false);
		ci.setEventDumpFile(null);
		ci.setModel(null);

		String discoveryNetPrefix = ci.getDiscoveryNetPrefix();
		Configuration.convert(infoColl, ci);

		// values that should have changed
		assertEquals(ci.getVenue(), "Hunted Mansion");
		assertTrue(ci.getVerbose());
		assertEquals(ci.getEventDumpFile(), "/var/logs/eventdumpfile.txt");
		assertEquals(ci.getModel(), XbrcModel.ATTRACTION.getXbrcModelClass());

		// values that should not have changed
		assertEquals(ci.getDiscoveryNetPrefix(), discoveryNetPrefix);

		com.disney.xband.xbrc.attractionmodel.ConfigOptions.ModelInfo aci = com.disney.xband.xbrc.attractionmodel.ConfigOptions.INSTANCE
				.getModelInfo();
		aci.setMetricsPeriod(-100);

		int abandonmentTimeout = aci.getAbandonmentTimeout();

		Configuration.convert(infoColl, aci);

		// values that should have changed
		assertEquals(aci.getMetricsPeriod(), 3000);

		// vlues that should not have changed
		assertTrue(aci.getAbandonmentTimeout() == abandonmentTimeout);
	}
}
