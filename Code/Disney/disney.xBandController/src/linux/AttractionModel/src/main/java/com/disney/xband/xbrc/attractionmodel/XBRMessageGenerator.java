package com.disney.xband.xbrc.attractionmodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.disney.xband.xbrc.lib.model.MessageArgs;
import com.disney.xband.xbrc.lib.model.MessageGenerator;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.model.XBRCMessage;

public class XBRMessageGenerator
{

	public static void publishMessage(String sType, MessageArgs ma,
                                      boolean sendToJMS,
                                      boolean sendToHttp)
			throws Exception
	{
		String payload = createPayload(sType, ma);
		XBRCMessage msg = new XBRCMessage(sType, ma.getTimestamp(), payload);
		XBRCController.getInstance().getConnector().publishMessage(msg, sendToJMS, sendToHttp);
	}

	private static String createPayload(String sType, MessageArgs ma)
	{
		if (sType.compareTo("BANDSTATUS") == 0)
		{
			return MessageGenerator.createBandStatusMessagePayload(sType, ma);
		}
		
		// type specific stuff
		if (sType.compareTo("ENTRY") == 0 || sType.compareTo("MERGE") == 0
				|| sType.compareTo("INQUEUE") == 0
				|| sType.compareTo("READEREVENT") == 0
                || sType.compareTo("LOCATIONEVENT") == 0)
		{			
			return MessageGenerator.createGuestMessagePayload(sType, ma);
        }
		
		if (sType.compareTo("ABANDON") == 0 || sType.compareTo("LOCATIONABANDON") == 0)
		{
			return MessageGenerator.createAbandonMessagePayload(sType, ma);
		}
	
		StringBuilder sb = new StringBuilder();
		sb.append("<message type=\"" + sType + "\" time=\""
				+ formatTime(ma.getTimestamp()) + "\">\n");
				
		if (sType.compareTo("INVEHICLE") == 0)
		{
			// downcast
			if (ma instanceof InVehicleMessageArgs)
			{
				InVehicleMessageArgs ivma = (InVehicleMessageArgs) ma;
				sb.append("    <guestid>" + ma.getGuestID() + "</guestid>\n");
				sb.append("    <publicid>" + ma.getPidDecimal() + "</publicid>\n");
                sb.append("    <linkid>" + ma.getLinkID() + "</linkid>\n");
                sb.append("    <linkidtype>" + ma.getLinkIDType() + "</linkidtype>\n");
				sb.append("    <bandtype>" + ma.getBandType() + "</bandtype>\n");
				sb.append("    <xpass>" + (ma.HasxPass() ? "true" : "false")
						+ "</xpass>\n");
				sb.append("    <locationid>" + ma.getLocationId() + "</locationid>\n");
				sb.append("    <readerlocation>" + ma.getReaderLocation()
						+ "</readerlocation>\n");
				sb.append("    <vehicle>" + ivma.getVehicle().getVehicleid() + "</vehicle>\n");
				if (ivma.getVehicle().getVehicleSequence() != null)
					sb.append("    <car>" + ivma.getVehicle().getVehicleSequence() + "</car>\n");
				sb.append("    <attractionid>" + ivma.getVehicle().getAttractionid() + "</attractionid>\n");
				sb.append("    <sceneid>" + ivma.getVehicle().getSceneid() + "</sceneid>\n");
				sb.append("    <vidlocationid>" + ivma.getVehicle().getLocationid() + "</vidlocationid>\n");
				sb.append("    <confidence>" + ivma.getVehicle().getConfidence() + "</confidence>\n");
                ReaderInfo ri = ma.getReader();
                sb.append("    <readername>" + (ri != null ? ri.getName() : "") + "</readername>\n");
                sb.append("    <readerid>" + (ri != null ? ri.getId() : "") + "</readerid>\n");
                sb.append("    <readerdeviceid>" + (ri != null ? ri.getDeviceId() : "") + "</readerdeviceid>");
            }
			else
				System.out
						.println("!! Error: arguments for INVEHICLE message are not of the correct type");
		}
		else if (sType.compareTo("LOAD") == 0)
		{
			// downcast
			if (ma instanceof LoadMessageArgs)
			{
				LoadMessageArgs lma = (LoadMessageArgs) ma;
				sb.append("    <guestid>" + ma.getGuestID() + "</guestid>\n");
				sb.append("    <publicid>" + ma.getPidDecimal() + "</publicid>\n");
                sb.append("    <linkid>" + ma.getLinkID() + "</linkid>\n");
                sb.append("    <linkidtype>" + ma.getLinkIDType() + "</linkidtype>\n");
				sb.append("    <bandtype>" + ma.getBandType() + "</bandtype>\n");
				sb.append("    <xpass>" + (ma.HasxPass() ? "true" : "false")
						+ "</xpass>\n");
				sb.append("    <locationid>" + ma.getLocationId() + "</locationid>\n");
				sb.append("    <readerlocation>" + ma.getReaderLocation()
						+ "</readerlocation>\n");
				sb.append("    <carid>" + lma.getCarID() + "</carid>\n");
                ReaderInfo ri = ma.getReader();
                sb.append("    <readername>" + (ri != null ? ri.getName() : "") + "</readername>\n");
                sb.append("    <readerid>" + (ri != null ? ri.getId() : "") + "</readerid>\n");
                sb.append("    <readerdeviceid>" + (ri != null ? ri.getDeviceId() : "") + "</readerdeviceid>");
            }
			else
				System.out
						.println("!! Error: arguments for LOAD message are not of the correct type");
		}
		else if (sType.compareTo("EXIT") == 0)
		{
			// downcast
			if (ma instanceof ExitMessageArgs)
			{
				ExitMessageArgs ema = (ExitMessageArgs) ma;
				sb.append("    <guestid>" + ma.getGuestID() + "</guestid>\n");
				sb.append("    <publicid>" + ma.getPidDecimal() + "</publicid>\n");
                sb.append("    <linkid>" + ma.getLinkID() + "</linkid>\n");
                sb.append("    <linkidtype>" + ma.getLinkIDType() + "</linkidtype>\n");
				sb.append("    <bandtype>" + ma.getBandType() + "</bandtype>\n");
				sb.append("    <xpass>" + (ma.HasxPass() ? "true" : "false")
						+ "</xpass>\n");
				sb.append("    <locationid>" + ma.getLocationId() + "</locationid>\n");
				sb.append("    <readerlocation>" + ma.getReaderLocation()
						+ "</readerlocation>\n");
				sb.append("    <carid>" + ema.getCarID() + "</carid>\n");
				sb.append("    <statistics>\n");
				sb.append("        <waittime>"
						+ Integer.toString(ema.getWaitTime()) + "</waittime>\n");
				sb.append("        <mergetime>"
						+ Integer.toString(ema.getMergeTime())
						+ "</mergetime>\n");
				sb.append("        <totaltime>"
						+ Integer.toString(ema.getTotalTime())
						+ "</totaltime>\n");
				sb.append("    </statistics>\n");
                ReaderInfo ri = ma.getReader();
                sb.append("    <readername>" + (ri != null ? ri.getName() : "") + "</readername>\n");
                sb.append("    <readerid>" + (ri != null ? ri.getId() : "") + "</readerid>\n");
                sb.append("    <readerdeviceid>" + (ri != null ? ri.getDeviceId() : "") + "</readerdeviceid>");
            }
			else
				System.out
						.println("!! Error: arguments for EXIT message are not of the correct type");
		}		
		else if (sType.compareTo("METRICS") == 0)
		{
			if (ma instanceof MetricsMessageArgs)
			{
				MetricsMessageArgs mma = (MetricsMessageArgs) ma;
				sb.append("    <starttime>" + formatTime(mma.getStartTime())
						+ "</starttime>\n");
				sb.append("    <endtime>" + formatTime(mma.getEndTime())
						+ "</endtime>\n");
				sb.append("    <standby>\n");
				sb.append("        <guests>" + mma.getSBGuestCount()
						+ "</guests>\n");
				sb.append("        <abandonments>"
						+ mma.getSBAbandonmentCount() + "</abandonments>\n");
				sb.append("        <waittime>" + mma.getSBMeanWaitTime()
						+ "</waittime>\n");
				sb.append("        <totaltime>" + mma.getSBMeanTotalTime()
						+ "</totaltime>\n");
				sb.append("    </standby>\n");
				sb.append("    <xpass>\n");
				sb.append("        <guests>" + mma.getXPGuestCount()
						+ "</guests>\n");
				sb.append("        <abandonments>"
						+ mma.getXPAbandonmentCount() + "</abandonments>\n");
				sb.append("        <waittime>" + mma.getXPMeanWaitTime()
						+ "</waittime>\n");
				sb.append("        <mergetime>" + mma.getXPMeanMergeTime()
						+ "</mergetime>\n");
				sb.append("        <totaltime>" + mma.getXPMeanTotalTime()
						+ "</totaltime>\n");
				sb.append("    </xpass>\n");
			}
			else
				System.out
						.println("!! Error: arguments for METRICS message are not of the correct type");
		}
		else
		{
			System.out.println("!! Error: Unexpected JMS message type: "
					+ sType);
		}

		sb.append("</message>\n");

		return sb.toString();
	}

	private static String formatTime(Date ts)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(ts);
	}
}
