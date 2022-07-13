package com.swedishchef.esm.stas.latest.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ibm.icu.text.SimpleDateFormat;
import com.spacetimeinsight.db.model.util.SecurityDBUtils;
import com.spacetimeinsight.stas.config.GenericConfigurationManager;
public class DBAccessHelper 
{
	public static void updateAttractionConstants(){
		String query = "SELECT AttractionID, AttractionName, AttractionStatus, SBQueueCap, XPQueueCap, DisplayName FROM rdr.Attraction";
		try {
			Connection con = getConnection();
			Statement statement = con.createStatement();
			ResultSet attractions = statement.executeQuery(query);

			DataManager dm = DataManager.getInstance();

			int i = 0;
			while(attractions.next())
			{
				dm.addAttraction(i, attractions.getString(2), attractions.getString(3), attractions.getInt(4), attractions.getInt(5), attractions.getString(6));
				i++; 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static Connection getConnection(){
		Connection con = null;
		String login = GenericConfigurationManager.getInstance().getProperty("Synapse", "DBLogin");
		String password = GenericConfigurationManager.getInstance().getProperty("Synapse", "DBPassword");
		password = SecurityDBUtils.getDecreptedPassword(password);
		String database = GenericConfigurationManager.getInstance().getProperty("Synapse", "Database");
		String server = GenericConfigurationManager.getInstance().getProperty("Synapse", "DBServer");
		String dbInstance = GenericConfigurationManager.getInstance().getProperty("Synapse", "DBInstance");
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String dbURL = "";
			if(dbInstance!=null && !dbInstance.trim().equals(""))
				dbURL = "jdbc:sqlserver://"+server+";instanceName="+dbInstance+";database="+database;
			else
				dbURL = "jdbc:sqlserver://"+server+";database="+database;
			con = DriverManager.getConnection(dbURL, login, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public static Map<String, ArrayList<Object>> getSensorData()
	{
		Connection con = getConnection();
		Map<String,ArrayList<Object>> db_extract = new HashMap<String,ArrayList<Object>>();
		SimpleDateFormat sprocDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String startTimeStr = sprocDateFormat.format(new Date())+" 08:00:00";
		

		try 
		{			 
			if(con!=null)
			{
				DataManager dm = DataManager.getInstance();

				if(dm.hasAttractionConstants())
				{
					int i = 0;
					while(i < dm.getAttractionConstants().length)
					{
						Object[] attractionConstants = dm.getAttractionConstantsAt(i);
						String whichAttraction = (String)attractionConstants[0];
						ArrayList<Object> dataList= db_extract.get(whichAttraction);
						 
						 if(dataList==null)
						 {
							 dataList = new ArrayList<Object>();
							 db_extract.put(whichAttraction, dataList);
						 }
						
						dataList.add(runIntegerProcedure(con, "rdr.usp_getSBQueueCount", whichAttraction,null));
						dataList.add(runIntegerProcedure(con, "rdr.usp_getXPQueueCountPL", whichAttraction,null));
												
						dataList.add(runIntegerProcedure(con, "rdr.usp_getSBGuestsServedCount", whichAttraction,startTimeStr));
						dataList.add(runIntegerProcedure(con, "rdr.usp_getXPGuestsServedCount", whichAttraction,startTimeStr));
						
						dataList.add(runDoubleProcedure(con, "rdr.usp_getAvgSBQueueWait", whichAttraction,null));
						dataList.add(runDoubleProcedure(con, "rdr.usp_getAvgXPQueueWait", whichAttraction,null));
						
						dataList.add(runIntegerProcedure(con, "rdr.usp_getSBArrivalRate", whichAttraction,null));
						dataList.add(runIntegerProcedure(con, "rdr.usp_getXPArrivalRate", whichAttraction,null));
						
						dataList.add(runDoubleProcedure(con, "rdr.usp_getEstSBQueueWait", whichAttraction,null));
						dataList.add(runDoubleProcedure(con, "rdr.usp_getEstXPQueueWait", whichAttraction,null));

						 i++;
					 }
				}
				 
			 }else{
				 throw new Exception("connection is null");
			 }
		}
		catch (Exception ex) {
            ex.printStackTrace();
        }
		 finally {
			try {
				con.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return db_extract;
	}

	private static int runIntegerProcedure(Connection con,String procedureName,String attractionName,String startTime) throws SQLException {
		CallableStatement statement = null;
		ResultSet rs = null;
		try {
			if(startTime!=null){
				statement = con.prepareCall("{ call "+procedureName+"(?,?) }");
				statement.setString(1, attractionName);
				statement.setString(2, startTime);
			}else{
				statement = con.prepareCall("{ call "+procedureName+"(?) }");
				statement.setString(1, attractionName);
			}
			rs = statement.executeQuery();
			if (rs.next()) {
				 return rs.getInt(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally{
			rs.close();
			statement.close();
		}
		return 0;
	}
	private static double runDoubleProcedure(Connection con,String procedureName,String attractionName,String startTime) throws SQLException {
		CallableStatement statement = null;
		ResultSet rs = null;
		try {
			if(startTime!=null){
				statement = con.prepareCall("{ call "+procedureName+"(?,?) }");
				statement.setString(1, attractionName);
				statement.setString(2, startTime);
			}else{
				statement = con.prepareCall("{ call "+procedureName+"(?) }");
				statement.setString(1, attractionName);
			}
			rs = statement.executeQuery();
			if (rs.next()) {
				 return rs.getDouble(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally{
			rs.close();
			statement.close();
		}
		return 0;
	}
	public static void loadParks(){
		Connection con = getConnection();
		CallableStatement statement = null;
		ResultSet rs = null;
		DataManager dataManager = DataManager.getInstance();
		try {
			statement = con.prepareCall("{ call rdr.usp_getParkGuestCount() }");
			rs = statement.executeQuery();
			while (rs.next()) {
				dataManager.addPark(rs.getString(1), rs.getInt(2), rs.getInt(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				rs.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
