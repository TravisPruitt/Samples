package com.disney.xband.xbrms.server.messaging.subscriber;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrms.server.SSConnectionPool;
import com.disney.xband.xbrms.server.messaging.FutureResult;
import com.disney.xband.xbrms.server.messaging.PublishEventType;
import com.disney.xband.xbrms.server.messaging.Subscriber;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import com.disney.xband.xbrms.server.model.XbrmsStatusBo;

public class XbrmsConfigurationUpdateSubscriber extends Subscriber
{
	public XbrmsConfigurationUpdateSubscriber(PublishEventType type) throws IllegalArgumentException
	{
		super(type);
		
		resultTimeout_msec = 10;
	}

	private static Logger logger = Logger.getLogger(XbrmsConfigurationUpdateSubscriber.class);
	
	private static Set<PublishEventType> acceptableEventTypes;
	
	static
	{
		acceptableEventTypes = new HashSet<PublishEventType>();
		acceptableEventTypes.add(PublishEventType.XBRMS_CONFIGURATION_UPDATE);
	}
	
	@Override
	public boolean isTypeSupported(PublishEventType messageType)
	{
		return acceptableEventTypes.contains(messageType);
	}

	@Override
	public FutureResult<?> call() throws Exception
	{
		if (logger.isTraceEnabled())
			logger.trace("Persisting changes to xbrms configuration...");
		
		// Try to persist the changes
		Connection conn = null;
		
		boolean configChangesPersisted = false;
		
		int transactionIsolation = 0;
		boolean autoCommit = false;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			// open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			// bump the date
			XbrmsConfigBo.getInstance().getDto().setLastModified((new Date()).getTime());
			
			// persist changes
			Config.getInstance().write(conn, XbrmsConfigBo.getInstance().getDto());
			Config.getInstance().write(conn, Auditor.getInstance().getConfig());
			
			conn.commit();
			configChangesPersisted = true;
			
			XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
		}
		catch(SQLException e)
		{
			// preserve data integrity
			if (conn != null && !conn.getAutoCommit()){
				try {
					conn.rollback();
				} catch (Exception ignore){}
			}
			
			configChangesPersisted = false;
			
			// report the error
			SSConnectionPool.handleSqlException(e, "Persisting xBRMS configuration changes");
		}
		catch (Exception e)
		{
			// preserve data integrity
			if (conn != null && !conn.getAutoCommit()){
				try {
					conn.rollback();
				} catch (Exception ignore){}
			}
			configChangesPersisted = false;
			
			// report the error
			logger.error("Failed to persist configuration changes!");
		}
		finally
		{
			if(conn != null) {
                try {
                	conn.setAutoCommit(autoCommit);
                    conn.setTransactionIsolation(transactionIsolation);
                }
                catch (Exception ignore) {}
            }
			
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
		
		if (!configChangesPersisted)
		{
			try
			{
				conn = SSConnectionPool.getInstance().getConnection();
				
				// update state with what's in the database
				Config.getInstance().read(conn, XbrmsConfigBo.getInstance().getDto());
				Config.getInstance().read(conn, Auditor.getInstance().getConfig());
				
				return new FutureResult<Boolean>(
						false, 
						"Failed to persist changes to xbrms configuration.", 
						Boolean.FALSE);
			}
			catch (Exception e)
			{
				logger.error("Failed to read xBRMS configuration from the database.",e);
			}
			finally
			{
				SSConnectionPool.getInstance().releaseConnection(conn);
                conn = null;
			}
		}
		
		// Update xbrms's status
        XbrmsStatusBo.getInstance().refreshStatusInfo();

		if (logger.isTraceEnabled())
			logger.trace("Success persisting changes to xbrms configuration.");
		
		return new FutureResult<Boolean>(
				true, 
				null, 
				Boolean.TRUE);
	}
}
