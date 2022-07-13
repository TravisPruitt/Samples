package com.disney.xband.jmslistener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.disney.xband.common.lib.health.ListenerStatus;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.jmslistener.configuration.ConnectionPoolConfiguration;
import com.disney.xband.jmslistener.gff.GffService;
import com.disney.xband.jmslistener.xi.XiService;

public class StatusInfo 
{
	public static StatusInfo INSTANCE = new StatusInfo();
	
	private ListenerStatus listenerStatus;
    private Map<String, Integer> healthMonitor;

	private static String CURRENT_GFF_VERSION = "1.7.0";
	private static String CURRENT_XI_VERSION = "1.7.0";
	
	private StatusInfo()
	{
		this.listenerStatus = new ListenerStatus();
        this.healthMonitor = new HashMap<String, Integer>();
	}
	
	public ListenerStatus getListenerStatus()
	{
		return listenerStatus;
	}

    public void registerHealthItem(Listener hi) {
        if(hi == null) {
            return;
        }

        healthMonitor.put(hi.getClass().getName(), new Integer(0));
    }

    public void resetHealthItemCounters(Listener hi) {
        if(hi == null) {
            return;
        }

        healthMonitor.put(hi.getClass().getName(), new Integer(0));
    }

    public void incHealthItemCounters(Listener hi) {
        if(hi == null) {
            return;
        }

        Integer restartsNum = healthMonitor.get(hi.getClass().getName());

        if(restartsNum != null) {
            healthMonitor.put(hi.getClass().getName(), new Integer(restartsNum + 1));
        }
    }

	public void Check()
	{
		Connection connection = null;
		
		//TODO: FIgure out cache data for status
		//listenerStatus.setCacheCapacity(MasterCache.INSTANCE.getCacheCapacity());
		//listenerStatus.setCacheSize(String.valueOf(cacheSize));
		
		listenerStatus.setStatus(StatusType.Green);

		StringBuilder statusMessage = new StringBuilder();
		String databaseVersion = "";

		try 
		{
			if(GffService.INSTANCE.getConnectionPool() != null)
			{
				connection = GffService.INSTANCE.getConnectionPool().createConnection();
				databaseVersion = GetDatabaseVersion(connection, GffService.INSTANCE.getGffListenerConfiguration(),
						statusMessage);
				
				this.listenerStatus.setGffDatabaseVersion(databaseVersion);

				if(!databaseVersion.startsWith(CURRENT_GFF_VERSION))
				{
					listenerStatus.setStatus(StatusType.Red);
					statusMessage.append("Incorrect database version. Expected version for GFF: " + 
					CURRENT_GFF_VERSION + ".xxxx. Actual version: " + databaseVersion);
                    statusMessage.append("\n");
				}
			}
			
		}
		catch( Exception e) 
		{
			listenerStatus.setStatus(StatusType.Red);
			statusMessage.append(e.getMessage());
		}
		finally
		{
			if(GffService.INSTANCE.getConnectionPool() != null)
			{
				GffService.INSTANCE.getConnectionPool().release(connection);
			}
		}

        connection = null;

		try 
		{
			if(XiService.INSTANCE.getConnectionPool() != null)
			{
				connection = XiService.INSTANCE.getConnectionPool().createConnection();
				databaseVersion = GetDatabaseVersion(connection, XiService.INSTANCE.getXiListenerConfiguration(),
						statusMessage);
				
				this.listenerStatus.setXiDatabaseVersion(databaseVersion);
				
				if(!databaseVersion.startsWith(CURRENT_XI_VERSION))
				{
					listenerStatus.setStatus(StatusType.Red);
					statusMessage.append("Incorrect database version. Expected version for xi: " + 
					CURRENT_XI_VERSION + ".xxxx. Actual version: " + databaseVersion);
                    statusMessage.append("\n");
				}
			}
		}
		catch( Exception e) 
		{
			listenerStatus.setStatus(StatusType.Red);
			statusMessage.append(e.getMessage());
		}
		finally
		{
			if(XiService.INSTANCE.getConnectionPool() != null)
			{
				XiService.INSTANCE.getConnectionPool().release(connection);
			}
		}

        // Check listeners status

        final Iterator<String> it = this.healthMonitor.keySet().iterator();
        StringBuilder statusMessage2 = new StringBuilder();

        while(it.hasNext()) {
            final String key = it.next();
            int val = this.healthMonitor.get(key);

            if(val > 2) {
                listenerStatus.setStatus(StatusType.Red);
                statusMessage2.append(getHealthItemName(key) + " ");
            }
            else {
                if(val > 0) {
                    if(listenerStatus.getStatus() == StatusType.Green) {
                        listenerStatus.setStatus(StatusType.Yellow);
                    }

                    statusMessage2.append(getHealthItemName(key) + " ");
                }
            }
        }

        if(statusMessage2.length() > 0) {
            if(statusMessage.length() > 0) {
                statusMessage.append(" ");
            }

            statusMessage.append("The following JMS listeners have not received any messages for more than ");
            statusMessage.append(JmsService.DEFAULT_NO_TRAFFIC_TOLERANCE_SECS);
            statusMessage.append(" seconds: ");
            statusMessage.append(statusMessage2);
        }

		listenerStatus.setStatusMessage(statusMessage.toString());
	}
	
    private static String getHealthItemName(String clazz) {
        if(clazz == null) {
            return "";
        }

        int ind = clazz.lastIndexOf(".");

        if(ind >= 0) {
            return clazz.substring(ind + 1);
        }

        return clazz;
    }

	private String GetDatabaseVersion(Connection connection,
			ConnectionPoolConfiguration configuration, StringBuilder statusMessage) 
			throws SQLException
	{
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String databaseVersion = "";

		try
		{
			if (connection == null)
			{
				listenerStatus.setStatus(StatusType.Red);
				statusMessage.append("Cannot connect to database " + 
						configuration.getDatabaseService() + ".");
			}
			else 
			{
				listenerStatus.setStatus(StatusType.Green);
				
				statement = connection.prepareStatement("SELECT version FROM schema_version WHERE schema_version_id = IDENT_CURRENT('schema_version');");
				resultSet = statement.executeQuery();
				
				if(resultSet.next())
				{
					databaseVersion = resultSet.getString("version");
				}				
			}
		}
		finally 
		{
            if(resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                }
            }
		}
		
		return databaseVersion;
	}
}
