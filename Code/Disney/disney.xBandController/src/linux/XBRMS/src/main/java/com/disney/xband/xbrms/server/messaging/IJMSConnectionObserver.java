package com.disney.xband.xbrms.server.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;

public interface IJMSConnectionObserver {
	public void onConnectionEstablished(Connection connection) throws JMSException;
	public void onConnectionLost();
}
