package com.disney.xband.xbrc.lib.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class MessageGenerator {
	
	private static Logger logger = Logger.getLogger(MessageGenerator.class);
	
	public static String formatTime(Date ts)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(ts);
	}
	
	public static String createGuestMessagePayload(String sType, MessageArgs ma)
	{	
		StringBuilder sb = new StringBuilder();
		sb.append("<message type=\"" + sType + "\" time=\""
				+ formatTime(ma.getTimestamp()) + "\">\n");
				
		sb.append("    <guestid>" + (ma.getGuestID() == null ? "" : ma.getGuestID()) + "</guestid>\n");
		sb.append("    <publicid>" + ma.getPidDecimal() + "</publicid>\n");
		sb.append("    <linkid>" + (ma.getLinkID() == null ? "" : ma.getLinkID()) + "</linkid>\n");
		sb.append("    <linkidtype>" + (ma.getLinkIDType() == null ? "" : ma.getLinkIDType()) + "</linkidtype>\n");
		sb.append("    <bandtype>" + ma.getBandType() + "</bandtype>\n");
		if (ma.getRFID() != null)
			sb.append("    <rfid>" + ma.getRFID() + "</rfid>\n");
		sb.append("    <xpass>" + (ma.HasxPass() ? "true" : "false") + "</xpass>\n");
		sb.append("    <locationid>" + ma.getLocationId() + "</locationid>\n");
		sb.append("    <readerlocation>" + ma.getReaderLocation() + "</readerlocation>\n");
		ReaderInfo ri = ma.getReader();
		sb.append("    <readername>" + (ri != null ? ri.getName() : "") + "</readername>\n");
		sb.append("    <readerdeviceid>" + (ri != null ? ri.getDeviceId() : "") + "</readerdeviceid>");

        // This currently is always assigned to true since reader events (in this current model) are only
        // generated by LRR's from WAYPOINT types (which are always assumed to be the primary band).
        // I'm surprised we aren't sending reader events for taps in the attraction model.
        if ( sType.compareTo(("READEREVENT")) == 0
                || sType.compareTo("LOCATIONEVENT") == 0 ) {
            sb.append("    <readerlocationid>" + ma.getReaderLocationId() + "</readerlocationid>\n");
        }

        if (ma instanceof ConfidenceMessageArgs)
        {
        	sb.append("    <confidence>");        
            sb.append(((ConfidenceMessageArgs)ma).getConfidence());
            sb.append("</confidence>\n");
        }        
        
        sb.append("</message>\n");
        return sb.toString();
	}
	
	public static String createBandStatusMessagePayload(String sType, MessageArgs ma)
	{	
		StringBuilder sb = new StringBuilder();
		sb.append("<message type=\"" + sType + "\" time=\""
				+ formatTime(ma.getTimestamp()) + "\">\n");
						
		sb.append("    <publicid>" + ma.getPidDecimal() + "</publicid>\n");		
		if (ma.getRFID() != null)
			sb.append("    <rfid>" + ma.getRFID() + "</rfid>\n");
		sb.append("    <locationid>" + ma.getLocationId() + "</locationid>\n");
		sb.append("    <readerlocation>" + ma.getReaderLocation() + "</readerlocation>\n");
		ReaderInfo ri = ma.getReader();
		sb.append("    <readername>" + (ri != null ? ri.getName() : "") + "</readername>\n");
		sb.append("    <readerdeviceid>" + (ri != null ? ri.getDeviceId() : "") + "</readerdeviceid>");       
        
        sb.append("</message>\n");
        return sb.toString();
	}
	
	public static String createAbandonMessagePayload(String sType, MessageArgs ma)
	{
		if (ma instanceof AbandonMessageArgs)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("<message type=\"" + sType + "\" time=\""
					+ formatTime(ma.getTimestamp()) + "\">\n");
			
			AbandonMessageArgs ama = (AbandonMessageArgs) ma;
			sb.append("    <guestid>" + (ma.getGuestID() == null ? "" : ma.getGuestID()) + "</guestid>\n");
			sb.append("    <publicid>" + ma.getPidDecimal() + "</publicid>\n");
			sb.append("    <linkid>" + (ma.getLinkID() == null ? "" : ma.getLinkID()) + "</linkid>\n");
			sb.append("    <linkidtype>" + (ma.getLinkIDType() == null ? "" : ma.getLinkIDType()) + "</linkidtype>\n");
			sb.append("    <bandtype>" + ma.getBandType() + "</bandtype>\n");
			if (ma.getRFID() != null)
				sb.append("    <rfid>" + ma.getRFID() + "</rfid>\n");
			sb.append("    <xpass>" + (ma.HasxPass() ? "true" : "false")
					+ "</xpass>\n");
			sb.append("    <locationid>" + ma.getLocationId() + "</locationid>\n");
			sb.append("    <readerlocation>" + ma.getReaderLocation()
					+ "</readerlocation>\n");
			sb.append("    <lastxmit>" + formatTime(ama.getLastXMit())
					+ "</lastxmit>\n");
            ReaderInfo ri = ma.getReader();
            sb.append("    <readername>" + (ri != null ? ri.getName() : "") + "</readername>\n");
            sb.append("    <readerdeviceid>" + (ri != null ? ri.getDeviceId() : "") + "</readerdeviceid>");
            
            sb.append("</message>\n");
            return sb.toString();
        }
			
		logger.warn("!! Error: arguments for ABANDON message are not of the correct type");
		return null;
	}
}