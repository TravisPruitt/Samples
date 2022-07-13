package com.disney.xband.xfpe.simulate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.xfpe.XfpeProperties;

public class DB {
	private static Logger logger = Logger.getLogger(DB.class);
	private static Boolean parkentryModel = null;
	
	public static boolean isParkentryModel() {
		if (parkentryModel != null)
			return parkentryModel;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = XfpeProperties.getInstance().getConn();			
			if (conn==null)
				return false;
			
			/* NOTE: some classes rely on the order of the records returned by this query so don't change it */
			String sSQL = "SELECT value from Config where class = \"ControllerInfo\" and property = \"model\"";
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			
			rs = stmt.getResultSet();
			if (rs.next())
			{
				String model = rs.getString("value");
				parkentryModel = model.contains("com.disney.xband.xbrc.parkentrymodel.CEP");
			}
			
			rs.close();
			stmt.close();
		} 
		catch (Exception e)
		{
			logger.error("SQL error reading Config table: " + e.getLocalizedMessage(), e);
			return false;
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
		
		return parkentryModel;
	}
	
	public static PEGuestTestSuite getPEGuestTestSuite(Long id) throws Exception
	{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try {
			conn = XfpeProperties.getInstance().getConn();			
			if (conn == null)
				return null;
			
			String sSQL = "select * from PEGuestTestSuite where id = ?";
			stmt = conn.prepareStatement(sSQL);
			stmt.setLong(1, id);
			stmt.execute();			
			
			PEGuestTestSuite gts = null;
			
			rs = stmt.getResultSet();
			if (rs.next()) {
				gts = new PEGuestTestSuite(id, rs.getString("name"));
			}
			rs.close();
			
			return gts;
		}
		catch (Exception e)
		{
			logger.error("SQL error selecting PEGuestTestSuite: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static PEGuestTest getPEGuestTest(Long id) throws Exception
	{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try {
			conn = XfpeProperties.getInstance().getConn();			
			if (conn == null)
				return null;
			
			String sSQL = "select * from PEGuestTest where id = ?";
			stmt = conn.prepareStatement(sSQL);
			stmt.setLong(1, id);
			stmt.execute();			
			
			PEGuestTest test = null;
			
			rs = stmt.getResultSet();
			if (rs.next()) {
				test = instantiatePEGuestTest(rs);
			}
			rs.close();
			
			return test;
		}
		catch (Exception e)
		{
			logger.error("SQL error selecting PEGuestTest: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static void insertTestSuite(PEGuestTestSuite obj) throws Exception {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try {
			conn = XfpeProperties.getInstance().getConn();			
			if (conn == null)
				return;
			
			String sSQL = "insert into PEGuestTestSuite (name) values (?)";
			stmt = conn.prepareStatement(sSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, obj.getName());
			stmt.execute();
			
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				obj.setId(rs.getLong(1));
			}
			rs.close();
		}
		catch (Exception e)
		{
			logger.error("SQL error inserting PEGuestTestSuite: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static void deleteTestSuite(Long id) throws Exception
	{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try {
			conn = XfpeProperties.getInstance().getConn();			
			if (conn == null)
				return;
	
			// need a transaction..
			conn.setAutoCommit(false);			
			
			// First delete all PEGuestTestActions..
			String sSQL = "delete from PEGuestAction where guestId in (select id from PEGuestTest where suiteId = ?)";
			stmt = conn.prepareStatement(sSQL);
			stmt.setLong(1, id);
			stmt.execute();
			stmt.close();
			
			// Next delete all PEGuestTest ..
			sSQL = "delete from PEGuestTest where suiteId = ?";
			stmt = conn.prepareStatement(sSQL);
			stmt.setLong(1, id);
			stmt.execute();
			stmt.close();
						
			// Now delete the suite
			sSQL = "delete from PEGuestTestSuite where id = ?";
			stmt = conn.prepareStatement(sSQL);
			stmt.setLong(1, id);
			stmt.execute();
			
			conn.commit();
		}
		catch (Exception e)
		{
			logger.error("SQL error deleting PEGuestTestSuite: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
			
			conn.setAutoCommit(true);
		}
	}
	
	public static void insertTest(PEGuestTest obj) throws Exception {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = XfpeProperties.getInstance().getConn();			
			if (conn == null)
				return;
			
			String sSQL = "insert into PEGuestTest (suiteId,seq,name,bandId,child,validBand,reason,bioTemplate,omniLevel,finalResult,`desc`) " + 
						  " values (?,?,?,?,?,?,?,?,?,?,?)";
			
			stmt = conn.prepareStatement(sSQL,Statement.RETURN_GENERATED_KEYS);
			stmt.setLong(1, obj.getSuiteId());
			stmt.setInt(2, obj.getSeq());
			stmt.setString(3, obj.getName());
			stmt.setString(4, obj.getBandId());
			stmt.setBoolean(5, obj.getChild());
			stmt.setBoolean(6, obj.getValidBand());
			stmt.setString(7, obj.getReason());
			stmt.setString(8, obj.getBioTemplate());
			stmt.setInt(9, obj.getOmniLevel());
			stmt.setString(10, obj.getFinalResult().name());
			stmt.setString(11, obj.getDesc());
			stmt.execute();
			
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				obj.setId(rs.getInt(1));
			}
			rs.close();
		}
		catch (Exception e)
		{
			logger.error("SQL error inserting PEGuestTest: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static void insertGuestAction(PEGuestAction obj) throws Exception {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = XfpeProperties.getInstance().getConn();			
			if (conn == null)
				return;
			
			String sSQL = "insert into PEGuestAction (guestId,seq,`desc`,type,delaySec,fireAfterEvent,data) " + 
						  " values (?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sSQL,Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, obj.getGuestId());
			stmt.setInt(2, obj.getSeq());
			stmt.setString(3, obj.getDesc());
			stmt.setString(4, obj.getType().name());
			stmt.setInt(5, obj.getDelaySec());
			stmt.setBoolean(6, obj.getFireAfterEvent());
			stmt.setString(7, obj.getData());
			stmt.execute();
			
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				obj.setId(rs.getInt(1));
			}
			rs.close();
		}
		catch (Exception e)
		{
			XfpeProperties.getInstance().reconnect();
			logger.error("SQL error inserting PEGuestAction: " + e.getLocalizedMessage(), e);
			throw e;
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static List<PEGuestTestSuite> findAllTestSuites() {		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = XfpeProperties.getInstance().getConn();			
			if (conn==null)
				return null;
			
			/* NOTE: some classes rely on the order of the records returned by this query so don't change it */
			String sSQL = "SELECT * from PEGuestTestSuite order by name";
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			
			rs = stmt.getResultSet();
			LinkedList<PEGuestTestSuite> al = new LinkedList<PEGuestTestSuite>();
			while(rs.next())
			{
				PEGuestTestSuite gi = new PEGuestTestSuite(rs.getLong("id"),rs.getString("name"));
				al.add(gi);
			}
			
			rs.close();
			stmt.close();
			
			return al;
		}
		catch (Exception e)
		{
			logger.error("SQL error reading PEGuestTestSuite: " + e.getLocalizedMessage(), e);
			return new LinkedList<PEGuestTestSuite>();
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static List<PEGuestTest> findAllTests(Long suiteId) {					
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = XfpeProperties.getInstance().getConn();			
			if (conn==null)
				return null;
			
			/* NOTE: some classes rely on the order of the records returned by this query so don't change it */
			String sSQL = "SELECT * from PEGuestTest where suiteId = ? order by seq";
			stmt = conn.prepareStatement(sSQL);
			stmt.setLong(1, suiteId);
			stmt.execute();
			
			rs = stmt.getResultSet();
			LinkedList<PEGuestTest> al = new LinkedList<PEGuestTest>();
			while(rs.next())
			{
				PEGuestTest gi = instantiatePEGuestTest(rs);
				al.add(gi);
			}
			
			rs.close();
			stmt.close();
			
			for (PEGuestTest gat: al) {
				gat.setActions(findAllActions(gat.getId()));
			}
			
			return al;
		}
		catch (Exception e)
		{
			logger.error("SQL error reading PEGuestTest: " + e.getLocalizedMessage(), e);
			return new LinkedList<PEGuestTest>();
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static List<PEGuestAction> findAllActions(Integer guestId) {					
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = XfpeProperties.getInstance().getConn();			
			if (conn==null)
				return null;
			
			/* NOTE: some classes rely on the order of the records returned by this query so don't change it */
			String sSQL = "SELECT * from PEGuestAction where guestId = ? order by seq";
			stmt = conn.prepareStatement(sSQL);
			stmt.setInt(1, guestId);
			stmt.execute();
			
			rs = stmt.getResultSet();
			LinkedList<PEGuestAction> al = new LinkedList<PEGuestAction>();
			while(rs.next())
			{
				PEGuestAction gi = new PEGuestAction(
						rs.getInt("id"), 
						rs.getInt("guestId"),
						rs.getInt("seq"),
						rs.getString("desc"),
						PEGuestActionType.valueOf(rs.getString("type")), 
						rs.getInt("delaySec"), 
						rs.getBoolean("fireAfterEvent"),
						rs.getString("data"));
				al.add(gi);
			}
			
			rs.close();
			stmt.close();
			
			return al;
		}
		catch (Exception e)
		{
			logger.error("SQL error reading PEGuestAction: " + e.getLocalizedMessage(), e);
			return new LinkedList<PEGuestAction>();
		}
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch(Exception e){}
		}
	}
	
	public static PEGuestTest instantiatePEGuestTest(ResultSet rs) throws SQLException {
		PEGuestTest gi = new PEGuestTest(
				rs.getInt("id"),
				rs.getLong("suiteId"),
				rs.getInt("seq"),
				rs.getString("name"), 
				rs.getString("bandId"),
				rs.getBoolean("child"),
				rs.getBoolean("validBand"), 
				rs.getString("reason"),
				rs.getString("bioTemplate"),
				rs.getInt("omniLevel"), 
				PEGuestTestResult.valueOf(rs.getString("finalResult")),
				rs.getString("desc"));
		return gi;
	}
}
