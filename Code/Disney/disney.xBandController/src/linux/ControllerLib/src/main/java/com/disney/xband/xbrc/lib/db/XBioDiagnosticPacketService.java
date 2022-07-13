package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.disney.xband.xbrc.lib.model.XBioDiagnosticPacket;

public class XBioDiagnosticPacketService {

	
	public static boolean SaveXBioDiagnosticPacket(Connection conn, XBioDiagnosticPacket xBioDiagnosticPacket)
	{
		boolean retVal = false;
		PreparedStatement preparedStatement = null;
		try
		{
			preparedStatement = conn.prepareStatement("INSERT INTO xBioDiagnosticPacket (xFPEId, DateTimeStamp, v500DiagnosticPacket, xFPEDiagnosticPacket, xFP1DiagnosticPacket) VALUES(?, ?, ?, ?, ?)");
			
			preparedStatement.setString(1, xBioDiagnosticPacket.getxFPEId());
			preparedStatement.setTimestamp(2, new java.sql.Timestamp(xBioDiagnosticPacket.getDateTimeStamp().getTime()));
			preparedStatement.setString(3, xBioDiagnosticPacket.getV500DiagnosticPacket());
			preparedStatement.setString(4, xBioDiagnosticPacket.getxFPEDiagnosticPacket());
			preparedStatement.setString(5, xBioDiagnosticPacket.getxFP1DiagnosticPacket());
			
			preparedStatement.executeUpdate();
		
			retVal = true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			retVal = false;
		}
		finally
		{
			if (preparedStatement != null)
			{
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return retVal;
	}
	
	public static List<XBioDiagnosticPacket> GetXBioDiagnosticPacketsByRange(Connection conn, int startingId, int endingId)
	{
		PreparedStatement preparedStatement = null;
		List<XBioDiagnosticPacket> retVal = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = conn.prepareStatement("Select * from xBioDiagnosticPacket where xBioDiagnosticPacketId >= ? AND xBioDiagnosticPacketId <= ? ;" );
			preparedStatement.setInt(1, startingId);
			preparedStatement.setInt(2, endingId);
			resultSet = preparedStatement.executeQuery();
			retVal = WriteMeta(resultSet);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (resultSet != null)
			{
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			
			if (preparedStatement != null)
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return retVal;
		
	}
	
	public static List<XBioDiagnosticPacket> GetXBioDiagnosticPacketsByTimeRange(Connection conn, Date start, Date end)
	{
		PreparedStatement preparedStatement = null;
		List<XBioDiagnosticPacket> retVal = null;
		ResultSet resultSet = null;
		try
		{
			preparedStatement = conn.prepareStatement("Select * from xBioDiagnosticPacket where DateTimeStamp >= ? AND DateTimeStamp <= ? ;" );
			preparedStatement.setDate(1, new java.sql.Date(start.getTime()));
			preparedStatement.setDate(2, new java.sql.Date(end.getTime()));
			
			resultSet = preparedStatement.executeQuery();
			
			retVal = WriteMeta(resultSet);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (resultSet != null)
			{
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			
			if (preparedStatement != null)
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return retVal;
	}
	
	public static List<XBioDiagnosticPacket> GetXBioDiagnosticPackets(Connection conn)
	{
		Statement statement = null;
		List<XBioDiagnosticPacket> retVal = null;
		ResultSet resultSet = null;
		
		try {
			
			statement = conn.createStatement();
			
			resultSet = statement.executeQuery("Select * from xBioDiagnosticPacket");
			retVal = WriteMeta(resultSet);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			if (resultSet != null)
			{
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}			
		}
		
		return retVal;
	}
	
	public static List<XBioDiagnosticPacket> GetXBioDiagnosticPacketsByTop(Connection conn, int topCount)
	{
		PreparedStatement preparedStatement = null;
		List<XBioDiagnosticPacket> retVal = null;
		ResultSet resultSet = null;
		try
		{
			preparedStatement = conn.prepareStatement("SELECT * from xBioDiagnosticPacket LIMIT ?");
			preparedStatement.setInt(1, topCount);
			resultSet = preparedStatement.executeQuery();
			retVal = WriteMeta(resultSet);
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (resultSet != null)
			{
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return retVal;
	}
	
	private static List<XBioDiagnosticPacket> WriteMeta(ResultSet resultSet) throws SQLException
	{
		List<XBioDiagnosticPacket> retVal = new ArrayList<XBioDiagnosticPacket>();
		
			while(resultSet.next())
			{
				XBioDiagnosticPacket newResult = new XBioDiagnosticPacket();
				
				newResult.setxBioDiagnosticPacketId(resultSet.getInt(1));
				newResult.setxFPEId(resultSet.getString(2));
				newResult.setV500DiagnosticPacket(resultSet.getString(3));
				newResult.setxFPEDiagnosticPacket(resultSet.getString(4));
				newResult.setxFP1DiagnosticPacket(resultSet.getString(5));
				newResult.setDateTimeStamp(resultSet.getTimestamp(6));
				
				retVal.add(newResult);
			}
		return retVal;
	}
}
