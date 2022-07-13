package com.disney.xband.xbrc.parkentrymodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.xbrc.BandStatusMessage;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.PECastMessage;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.PEGuestMessage;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.model.XBRCMessage;

public class PEMessageGenerator
{
	private static Logger logger = Logger.getLogger(PEMessageGenerator.class);
	
	// message types
	// guest messages
	public final static String TYPE_TAPPED 			= "TAPPED";
	public final static String TYPE_ABANDONED 		= "ABANDONED";
	public final static String TYPE_BLUELANE 		= "BLUELANE";
	public final static String TYPE_HASENTERED 		= "HASENTERED";
	public final static String TYPE_PARKENTRYEVENT	= "PARKENTRYEVENT";
	public final static String TYPE_PARKENTRYABANDON = "PARKENTRYABANDON";
	public final static String TYPE_BANDSTATUS		= "BANDSTATUS";
	
	// cast messages
	public final static String TYPE_CAST_OPEN 		= "CASTOPENLOCATION";
	public final static String TYPE_CAST_BUMP 		= "CASTBUMPLOCATION";
	public final static String TYPE_CAST_CLOSE		= "CASTCLOSELOCATION";
	
	// reason codes
	public final static String REASON_NOTSET 		= "";
	public final static String REASON_TIMEOUT 		= "TIMEOUT";
	public final static String REASON_SWITCHEDREADER = "SWITCHEDREADER";
	public final static String REASON_SCANFAILED 	= "SCANFAILED";
	public final static String REASON_ENTFAILED 	= "ENTITLEMENTFAILED";
	public final static String REASON_IDCHECKREQ 	= "IDCHECKREQ";
	
	public static <T> void publishBandStatusMessage(GuestStatus<GuestStatusState> gs) {
		try {
			BandStatusMessage msg = new BandStatusMessage();
			msg.setTimestamp(formatTime(gs.getPeTransactionState().getStartTime()));
			msg.setMessageType(TYPE_BANDSTATUS);		

			msg.setPidDecimal(gs.getPidDecimal());

			ReaderInfo ri = gs.getPeTransactionState().getReader();
			
			if (ri != null && ri.getLocation() != null) {
				msg.setReaderLocation(ri.getLocation().getName());
				msg.setLocationId(ri.getLocation().getLocationId());
				msg.setReaderName(ri.getName());
				msg.setReaderDeviceId(ri.getDeviceId());
			}
			
			msg.setRfid(gs.getBandRfid());

			String payload = XmlUtil.convertToPartialXml(msg,
					BandStatusMessage.class);
			XBRCMessage m = new XBRCMessage(msg.getMessageType(), gs.getPeTransactionState().getStartTime(),
					payload);
			XBRCController.getInstance().getConnector().publishMessage(m, true, true);
		} catch (JAXBException e) {
			logger.error("Failed to publish a JMS message of type " + TYPE_BANDSTATUS, e);
		}
	}
	
	public static <T> void publishMessage(String type, String reason, GuestStatus<GuestStatusState> gs, 
			  ReaderInfo ri, Date timestamp)
	{
		publishMessage(type, reason, gs, ri, timestamp, null, null);
	}
	
	public static <T> void publishMessage(String type, String reason, GuestStatus<GuestStatusState> gs, 
										  ReaderInfo ri, Date timestamp, Integer entErrorCode, String entErrorDescription)
	{
		try
		{				
			PEGuestMessage msg = new PEGuestMessage();
			msg.setTimestamp(formatTime(timestamp));
			msg.setMessageType(type);
			msg.setReason(reason);			
			msg.setXbrcReferenceNo(gs.getGuestID());
			msg.setPidDecimal(gs.getPidDecimal());
			msg.setEntErrorCode(entErrorCode);
			msg.setEntErrorDescription(entErrorDescription);
			
			if (ri != null && ri.getLocation() != null) {
				msg.setReaderLocation(ri.getLocation().getName());
				msg.setLocationId(ri.getLocation().getLocationId());
				msg.setReaderName(ri.getName());
				msg.setReaderDeviceId(ri.getDeviceId());
			}
			
			if (type.equals(TYPE_PARKENTRYABANDON))
				msg.setLastxmit(formatTime(gs.getTimeLatestAtLrrReader()));
			
			String payload = XmlUtil.convertToPartialXml(msg, PEGuestMessage.class);
			XBRCMessage m = new XBRCMessage(msg.getMessageType(), timestamp, payload);
			XBRCController.getInstance().getConnector().publishMessage(m, true, true);
		}
		catch (JAXBException e)
		{
			logger.error("Failed to publish a JMS message of type " + type, e);
		}		
	}
	
	public static <T> void publishMessage(String type, CMST cms, Date timestamp)
	{
		try
		{
			PECastMessage msg = new PECastMessage();
			msg.setTimestamp(formatTime(timestamp));
			msg.setMessageType(type);
			msg.setLocationName(cms.getLocationName());
			msg.setPortalId(cms.getPortalId());
						
			String payload = XmlUtil.convertToPartialXml(msg, PECastMessage.class);
			XBRCMessage m = new XBRCMessage(msg.getMessageType(), timestamp, payload);
			XBRCController.getInstance().getConnector().publishMessage(m, true, true);
		}
		catch (JAXBException e)
		{
			logger.error("Failed to publish a JMS message of type " + type, e);
		}				
	}

	public static String formatTime(Date ts)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(ts);
	}
}
