package com.disney.xband.xbrc.lib.model;


public interface IXBRCConnector {
	public void publishMessage(XBRCMessage msg, boolean sendToJMS, boolean sendToHttp);
	public void sendMessage(Object client, XBRCMessage msg);
}
