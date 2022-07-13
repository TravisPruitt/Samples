package com.disney.xband.xbrms.server.messaging.subscriber;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.server.messaging.FutureResult;
import com.disney.xband.xbrms.server.messaging.JMSAgent;
import com.disney.xband.xbrms.server.messaging.PublishEventType;
import com.disney.xband.xbrms.server.messaging.Subscriber;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import com.disney.xband.xbrms.server.model.XbrmsStatusBo;

public class XbrmsStatusRefreshSubscriber extends Subscriber
{
    private long lastRefreshedMs;

	public XbrmsStatusRefreshSubscriber(PublishEventType type) throws IllegalArgumentException
	{
		super(type);
		
		resultTimeout_msec = 10;
	}

	private static Logger logger = Logger.getLogger(XbrmsStatusRefreshSubscriber.class);
	
	private static Set<PublishEventType> acceptableEventTypes;
	
	static
	{
		acceptableEventTypes = new HashSet<PublishEventType>();
		acceptableEventTypes.add(PublishEventType.XBRMS_CONFIGURATION_REFRESH);
	}
	
	@Override
	public boolean isTypeSupported(PublishEventType messageType)
	{
		return acceptableEventTypes.contains(messageType);
	}

	@Override
	public FutureResult<?> call() throws Exception
	{
        final long cTime = System.currentTimeMillis();

        if((cTime - this.lastRefreshedMs) > 1000) { // See bug #6643

            this.lastRefreshedMs = cTime;

            if (logger.isTraceEnabled()) {
                logger.trace("Refreshing xbrms status...");
            }

            // Update jms agent connection
            try {
                JMSAgent.getInstance().stop();
                JMSAgent.getInstance().start();

                logger.info("Restarted JMS Agent after a configuration change. Now using" +
                        " jms topic: " + XbrmsConfigBo.getInstance().getDto().getJmsXbrcDiscoveryTopic() +
                        ", jms message expiration: " + XbrmsConfigBo.getInstance().getDto().getJmsXbrcDiscoveryTopic() + " seconds.");
            }
            catch (JMSException e) {
                String errorMessage = "Failed to restart xBRMS JMS agent after configuration change! Health item discovery and high availability mechanisms will not work";

                logger.error(errorMessage);

                ProblemsReportBo.getInstance().setLastError(ProblemAreaType.JmsMessaging, errorMessage, e);
            }

            // Update xbrms's status
            XbrmsStatusBo.getInstance().refreshStatusInfo();

            if (logger.isTraceEnabled()) {
                logger.trace("Success refreshing xbrms status.");
            }

        }

        return new FutureResult<Boolean>(
				true, 
				null, 
				Boolean.TRUE);
	}
}
