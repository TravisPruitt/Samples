package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.XbrBandCommand;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_COMMAND;
import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.mysql.jdbc.Statement;

public class XbandCommandService {

	private static Logger logger = Logger.getLogger(XbandCommandService.class);
	
	public static XbrBandCommand find(Connection conn, long commandId) throws Exception 
	{
		if (conn == null)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
        PreparedStatement stmt2 = null;
        ResultSet rs2 = null;

		int transactionIsolation = -1;
		try
		{
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			
			// get the command
			String query = "SELECT * FROM TransmitCommand WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, commandId);
			stmt.execute();
			rs = stmt.getResultSet();
			
			XbrBandCommand command = null;
			
			if (rs.next()) {
				command = instantiateXbandCommand(rs);
			
			    // get its recipients
			    query = "SELECT recipientId FROM TransmitRecipients WHERE commandId = ?";
			    stmt2 = conn.prepareStatement(query);
			    stmt2.clearParameters();
			    stmt2.setLong(1, commandId);
			    stmt2.execute();
			    rs2 = stmt.getResultSet();
			
			    command.setRecipients(instantiateXbandCommandRecipients(rs2));
			
			    return command;
            }
            else {
                return null;
            }
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try
			{
				if (rs != null) try { rs.close(); } catch(Exception e){}
				if (stmt != null) try { stmt.close(); } catch(Exception e){}
                if (rs2 != null) try { rs2.close(); } catch(Exception e){}
                if (stmt2 != null) try { stmt2.close(); } catch(Exception e){}

				conn.setTransactionIsolation(transactionIsolation);
			} 
			catch (Exception e) {}
		}
	}
	
	public static List<XbrBandCommand> findByLocation(Connection conn, long locationId) throws Exception 
	{
		if (conn == null)
			return null;
		
		PreparedStatement stmt = null;
		PreparedStatement recStmt = null;
		ResultSet rs = null;
		ResultSet recRs = null;
		
		try
		{
			String query = "SELECT * from TransmitCommand WHERE locationId = ?";
			
			recStmt = conn.prepareStatement("SELECT recipientId FROM TransmitRecipients WHERE commandId = ?");
			
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.execute();
			rs = stmt.getResultSet();
			
			List<XbrBandCommand> commands = null;
			
			while (rs.next())
			{
				if (commands == null)
					commands = new LinkedList<XbrBandCommand>();
				
				XbrBandCommand command = instantiateXbandCommand(rs);

				if (command != null)
					commands.add(command);
				else
					continue;
				
				// get its recipients
				recStmt.clearParameters();
				recStmt.setLong(1, command.getId());
				recStmt.execute();
				recRs = recStmt.getResultSet();
				
				command.setRecipients(instantiateXbandCommandRecipients(recRs));
			}
			
			return commands;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try
			{
				if (rs != null) try { rs.close(); } catch(Exception e){}
				if (stmt != null) try { stmt.close(); } catch(Exception e){}
				if (recStmt != null) try { recStmt.close(); } catch(Exception e){}
				if (recRs != null) try { recRs.close(); } catch(Exception e){}
			} 
			catch (Exception e) {}
		}
	}
	
	public static boolean create(Connection conn, long locationId, XbrBandCommand command) throws Exception 
	{
		if (conn == null || command == null)
			return false;
		
		PreparedStatement stmtPayload = null;
		PreparedStatement stmtCommand = null;
		PreparedStatement stmtRecipients = null;
		ResultSet rs = null;
		boolean bOldAutoCommit = true;
		 
		int transactionIsolation = -1;
		
		// check if it is legal to add this command to this reader
		List<XbrBandCommand> existing = findByLocation(conn, locationId);
		if (existing != null && existing.size() > 0)
		{
			// there can only be one broadcast command configured per reader
			if (command.getMode() == XMIT_MODE.BROADCAST)
				throw new Exception("There can only be one broadcast command configured per location");

			// multiple reply commands are fine
			for (XbrBandCommand cmd : existing)
			{
				if (cmd.getMode() == XMIT_MODE.BROADCAST)
					throw new Exception("Your new command was not created because there is a broadcast command already configured for this location.");
				else
					break;
			}
		}

		try
		{		
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			bOldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			// create new command
			String commandQuery = "INSERT INTO TransmitCommand(locationId, command, `interval`, timeout, mode, threshold, enableThreshold)" +
								" VALUES(?,?,?,?,?,?,?)";
			stmtCommand = conn.prepareStatement(commandQuery, Statement.RETURN_GENERATED_KEYS);
			stmtCommand.clearParameters();
			stmtCommand.setLong(1, locationId);
			stmtCommand.setString(2, command.getCommand().name());
			stmtCommand.setInt(3, command.getInterval());
			stmtCommand.setInt(4, command.getTimeout());
			stmtCommand.setString(5, command.getMode().name());
			stmtCommand.setInt(6, command.getThreshold());
			stmtCommand.setBoolean(7, command.getEnableThreshold());
			stmtCommand.executeUpdate();

			rs = stmtCommand.getGeneratedKeys();
			
			Long commandId = null;
			if (rs.next()){
				commandId = rs.getLong(1);
			}
			
			if (commandId == null)
				return false;
			
			// BROADCAST command and REPLY to all within a specified signal strength will not have any recipients specified
			if (command.getRecipients() != null)
			{
				String recipientsQuery = "INSERT INTO TransmitRecipients(commandId, recipientId) VALUES(?,?)";
				stmtRecipients = conn.prepareStatement(recipientsQuery, Statement.RETURN_GENERATED_KEYS);
				
				for (Long recipientId : command.getRecipients())
				{
					stmtRecipients.clearParameters();
					stmtRecipients.setLong(1, commandId);
					stmtRecipients.setLong(2, recipientId);	
					stmtRecipients.executeUpdate();
				}
			}
			
			conn.commit();
		} 
		catch (Exception e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException sqle)
			{
				logger.error("Failed to persist an xband command " + command.getCommand().name() + " for a location id " + locationId);
			}
			
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try
			{
				if (rs != null) try { rs.close(); } catch(Exception e){}
				if (stmtCommand != null) try { stmtCommand.close(); } catch(Exception e){}
				if (stmtPayload != null) try { stmtPayload.close(); } catch(Exception e){}
				if (stmtRecipients != null) try { stmtRecipients.close(); } catch(Exception e){}
				
				conn.setAutoCommit(bOldAutoCommit);
				conn.setTransactionIsolation(transactionIsolation);
			} 
			catch (Exception e) {}
		}		
		
		return true;
	}
	
	public static boolean deleteCommand(Connection conn, long commandId, boolean commitTransaction) throws Exception 
	{
		if (conn == null)
			return false;
		
		PreparedStatement stmt = null;
		boolean bOldAutoCommit = true;
		 
		int transactionIsolation = -1;
		try
		{
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			bOldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			// remove the command from the database
			stmt = conn.prepareStatement("DELETE FROM TransmitRecipients WHERE commandId = ?");
			stmt.clearParameters();
			stmt.setLong(1, commandId);
			stmt.executeUpdate();
			stmt.close();
			
			stmt = conn.prepareStatement("DELETE FROM TransmitCommand WHERE id = ?");
			stmt.clearParameters();
			stmt.setLong(1, commandId);
			stmt.executeUpdate();
			
			if (commitTransaction)
				conn.commit();
			
			return true;
		} 
		catch (SQLException e)
		{
			if (commitTransaction)
				conn.rollback();
			
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try
			{			
				if (stmt != null) try { stmt.close(); } catch(Exception e){}

				conn.setAutoCommit(bOldAutoCommit);
				conn.setTransactionIsolation(transactionIsolation);
			} 
			catch (Exception e) {}
		}
	}
	
	public static boolean deleteCommands(Connection conn, long locationId, boolean commitTransaction) throws Exception 
	{
		if (conn == null)
			return false;
		
		PreparedStatement stmt = null;
		boolean bOldAutoCommit = true;
		 
		int transactionIsolation = -1;
		try
		{
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			bOldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			String queryRecipients = "DELETE FROM TransmitRecipients WHERE commandId in (select id from TransmitCommand where locationId = ?)";
			stmt = conn.prepareStatement(queryRecipients);
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.executeUpdate();	// not every command must have recipient
			stmt.close();
			
			String queryCommand = "DELETE FROM TransmitCommand WHERE locationId = ?";
			stmt = conn.prepareStatement(queryCommand);
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.executeUpdate();
			
			if (commitTransaction)
				conn.commit();
			
			return true;
		} 
		catch (SQLException e)
		{
			if (commitTransaction)
				conn.rollback();
			
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try
			{			
				if (stmt != null) try { stmt.close(); } catch(Exception e){}

				conn.setAutoCommit(bOldAutoCommit);
				conn.setTransactionIsolation(transactionIsolation);
			} 
			catch (Exception e) {}
		}
	}

	public static XbrBandCommand instantiateXbandCommand(ResultSet rs) throws Exception
	{
		if (rs == null)
			return null;
		
		XbrBandCommand command = null;
		try
		{
			command = new XbrBandCommand();
			
			command.setId(rs.getLong("id"));
			command.setLocationId(rs.getLong("locationId"));
			
			String commandType = rs.getString("command");
			try
			{
				command.setCommand(XMIT_COMMAND.valueOf(commandType));
			}
			catch (Exception e)
			{
				command.setCommand(XMIT_COMMAND.SLOW_PING);		// safe default
				logger.error("Found TransmitCommand with illegal command type. Using XMIT_COMMAND.SLOW_PING as default. TransmitCommand id " + command.getId());
			}
	
			String commandMode = rs.getString("mode");
			try
			{
				command.setMode(XMIT_MODE.valueOf(commandMode));
			}
			catch (Exception e)
			{
				command.setMode(XMIT_MODE.REPLY);		// safe default
				logger.error("Found TransmitCommand with illegal mode type. Using XMIT_COMMAND.REPLY as default. TransmitCommand id " + command.getId());
			}
			
			Integer interval = rs.getInt("interval");
			if (rs.wasNull())
				command.setInterval(command.getCommand().getFrequencyMin());
			else
				command.setInterval(interval);
			
			Integer timeout = rs.getInt("timeout");
			if (rs.wasNull())
				command.setTimeout(command.getCommand().getTimeoutMax());
			else
				command.setTimeout(timeout);
			
			command.setThreshold(rs.getInt("threshold"));
			
			command.setEnableThreshold(rs.getBoolean("enableThreshold"));			
		} 
		catch (Exception e)
		{
			logger.error("Failed to initialize xBand command object from command id " + 
					(command.getId() == null ? "NULL" : command.getId()));
			throw e;
		}
		
		return command;
	}
	
	public static Set<Long> instantiateXbandCommandRecipients(ResultSet rs) throws Exception 
	{
		if (rs == null)
			return null;
		
		Set<Long> recipients = null;
		while (rs.next())
		{
			if (recipients == null)
				recipients = new HashSet<Long>();
			
			recipients.add(rs.getLong("recipientId"));
		}
		
		return recipients;
	}
}
