package com.disney.xband.xbrc.spacemodel;


import com.disney.xband.xbrc.lib.model.MessageArgs;
import com.disney.xband.xbrc.lib.model.MessageGenerator;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.model.XBRCMessage;

public class XBRMessageGenerator {
	
	public static void 
	publishMessage(String sType, MessageArgs ma, boolean sendToJMS, boolean sendToHttp) throws Exception
	{
		String payload = null;
		
		if (sType.equals("BANDSTATUS"))
			payload = MessageGenerator.createBandStatusMessagePayload(sType, ma);
		else if (sType.equals("LOCATIONEVENT") || sType.equals("READEREVENT"))
			payload = MessageGenerator.createGuestMessagePayload(sType, ma);
		else if (sType.equals("ABANDON") || sType.equals("LOCATIONABANDON"))
			payload = MessageGenerator.createAbandonMessagePayload(sType, ma);
		if (payload == null)
			return;
		XBRCMessage msg = new XBRCMessage(sType,ma.getTimestamp(),payload);
		XBRCController.getInstance().getConnector().publishMessage(msg, sendToJMS, sendToHttp);
	}
}
