package com.disney.xband.xbrc.Controller.junit.bvt;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.disney.xband.xbrc.Controller.CurrentConfigDiff;
import com.disney.xband.xbrc.Controller.CurrentConfigDiffItem;
import com.disney.xband.xbrc.Controller.Processor;
import com.disney.xband.xbrc.lib.model.IXBRCModel;

@RunWith(value=Parameterized.class)
public class TestCurrentConfigDiff
{
	private String model;
	private static Map<String, String> expected;
	
	public TestCurrentConfigDiff(String model)
	{
		this.model = model;
	}
	
	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
			/*0*/ {"attraction"},
			/*1*/ {"parkentry"},
			/*2*/ {"space"}
		});
	}
	
	@Before
	public void loadModel()
	{
		try
		{
			String sModelClass = "com.disney.xband.xbrc." + model + "model.CEP";
			IXBRCModel model = (IXBRCModel) Class.forName(sModelClass).newInstance();
			model.readConfiguration();
			model.initialize();
	
			Field privateModel = Processor.class.getDeclaredField("model");
			privateModel.setAccessible(true);
			privateModel.set(Processor.INSTANCE, model);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("FAILED to load the model.");
		}
	}
	
	@Test
	public void testPreconfiguredData()
	{
		InputStream prevCurrConfig = getClass().getResourceAsStream(model + "_currentConfigPrev.xml");
		InputStream newCurrConfig = getClass().getResourceAsStream(model + "_currentConfigNew.xml");
		
		LinkedList<CurrentConfigDiffItem> result = CurrentConfigDiff.diff(prevCurrConfig, newCurrConfig);

		String actual = CurrentConfigDiff.prettyPrint(result, false);
		
		/*
		System.out.println("------------------- expected for " + model);
		System.out.println(expected.get(model).trim());
		System.out.println("------------------ actual ------------------------");
		System.out.println(actual.trim());
		*/
		
		assertTrue(expected.get(model).trim().equals(actual.trim()));
	}
	
	@BeforeClass
	public static void initExpected()
	{
		expected = new HashMap<String, String>();
		expected.put("attraction", "configuration/griditems/griditem/id/1/ygrid;5;55;MODIFIED"				
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/carsPerTrain;0;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/laserBreaksAfterVehicle;0;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/laserBreaksBeforeVehicle;0;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/maxAnalyzeGuestEvents;10000;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/maxAnalyzeGuestEventsPerVehicle;2;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/minReadsToAssociate;2;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/onrideTimeoutSec;2;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/requireVehicleLaserEvent;true;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/trainTimeoutSec;20;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/useVehicleEventTime;false;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/vaAlgorithm;closestpeakfallback;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/vehicleHoldTimeMs;1000;null;DELETED"
				+ "\nconfiguration/model/attractionModelConfig/vaLocationConfig/locationId/7/vehicleTimeOffsetMs;0;null;DELETED"
				+ "\nconfiguration/properties/AttractionModelConfig/abandonmenttimeout;3600;1111;MODIFIED"
				+ "\nconfiguration/properties/AttractionModelConfig/castmemberdetectdelay_msec;0;1;MODIFIED"
				+ "\nconfiguration/properties/AttractionModelConfig/chirprate_msec;1000;;MODIFIED"
				+ "\nconfiguration/properties/AttractionModelConfig/deletedProperty;;null;DELETED"
				+ "\nconfiguration/properties/AttractionModelConfig/newProperty;null;;ADDED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/7/name;DeletedLocation;NewLocation;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/7/readers;;"
				+ "\n                ;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/errortimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/failuretimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers;null;"
				+ "\n                ;ADDED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/bioDeviceType;3;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/deviceid;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/disabledReason;;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/enabled;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/gain;1.1;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/ipaddress;10.110.1.28;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/istransmitter;false;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/lane;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/macaddress;02:0D:20:01:01:3B;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/minxbrcversion;0.0.0.0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/name;vault-xTPra;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/port;8080;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/threshold;-90;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/transmitPayload;;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/type;xTPra;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/version;2.5.7-3097;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/x;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/1/y;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/0;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/1;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/2;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/3;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/4;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/5;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/6;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/antennas/value/7;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/bioDeviceType;3;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/deviceid;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/disabledReason;;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/enabled;true;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/gain;1.1;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/ipaddress;10.1.201.230;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/istransmitter;false;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/lane;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/macaddress;00:91:FA:00:00:95;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/minxbrcversion;1.0.0.0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/name;R2D2;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/port;8080;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/threshold;-90;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/transmitPayload;;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/type;Long Range;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/version;2.4.0-3135;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/x;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/readers/reader/id/3/y;0;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/successtimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/transmitCommands/transmitCommand/locationId/8/REPLY/FAST_PING/timeout/30000/interval/1000/locationIDs/1,3;;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/transmitCommands/transmitCommand/locationId/8/REPLY/FAST_PING/timeout/30000/interval/1000/threshold/-127;;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/transmitCommands/transmitCommand/locationId/8/REPLY/FAST_PING/timeout/30000/interval/1000/threshold/-127/locationIDs/7,8;;null;DELETED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/x;76.0;11.1;MODIFIED");
		expected.put("parkentry", "configuration/model/parkEntryModelConfig/castMember/id/008023C002AC9004/bandId;null;008023C002AC9004;ADDED" 
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/008023C002AC9004/enabled;null;true;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/008023C002AC9004/externalId;null;;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/008023C002AC9004/name;null;Cast Member Five;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/008023C002AC9004/omniPassword;null;11111;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/008023C002AC9004/omniUsername;null;800011;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/arek/bandId;null;arek;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/arek/enabled;null;true;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/arek/externalId;null;;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/arek/name;null;Arek;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/arek/omniPassword;null;11111;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/castMember/id/arek/omniUsername;null;800011;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/omniServer/id/1/active;null;false;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/omniServer/id/1/hostname;null;localhost;ADDED"
				+ "\nconfiguration/model/parkEntryModelConfig/omniServer/id/1/port;null;0;ADDED"
				+ "\nconfiguration/properties/ControllerInfo/readerdatasendperiod_msec;150;100;MODIFIED"
				+ "\nconfiguration/properties/ESBInfo/enablejms;true;null;DELETED"
				+ "\nconfiguration/properties/ESBInfo/jmsbroker;#;null;DELETED"
				+ "\nconfiguration/properties/ParkEntryModelConfig/abandonmenttimesec;null;25;ADDED"
				+ "\nconfiguration/properties/ParkEntryModelConfig/castappcorethreadpoolsize;null;10;ADDED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/7/name;DeletedLocation;NewLocation;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/errortimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/failuretimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/successtimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/x;76.0;11.1;MODIFIED");
		expected.put("space", "configuration/properties/SpaceModelConfig/abandonmenttimeout_msec;null;3000;ADDED"
				+ "\nconfiguration/properties/SpaceModelConfig/castmemberdetectdelay_msec;null;0;ADDED"
				+ "\nconfiguration/properties/SpaceModelConfig/chirprate_msec;null;1000;ADDED"
				+ "\nconfiguration/properties/SpaceModelConfig/confidencedelta;null;10;ADDED"
				+ "\nconfiguration/properties/SpaceModelConfig/guestdetectdelay_msec;null;0;ADDED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/7/name;DeletedLocation;NewLocation;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/errortimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/failuretimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/successtimeout;0;1;MODIFIED"
				+ "\nconfiguration/readerlocationinfo/readerlocation/id/8/x;76.0;11.1;MODIFIED");
	}
}
