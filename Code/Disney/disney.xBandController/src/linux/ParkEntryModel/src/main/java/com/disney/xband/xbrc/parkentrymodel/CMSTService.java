package com.disney.xband.xbrc.parkentrymodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class CMSTService {
	private static Logger logger = Logger.getLogger(CMSTService.class);
	
	public static Map<String,CMST> readCMST(Connection conn) {
		Map<String,CMST> CMT = new HashMap<String, CMST>();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "select * from CMST";
			stmt = conn.prepareStatement(query);
			stmt.execute();
			rs = stmt.getResultSet();
			
			while(rs.next()) {
				CMST cms = new CMST();
				cms.setLocationName(rs.getString("locationName"));
				cms.setState(CastMemberState.valueOf(rs.getString("State")));
				cms.setBandId(rs.getString("bandId"));
				cms.setOmniNumericId(rs.getString("omniNumericId"));
				cms.setPortalId(rs.getString("portalId"));
				CMT.put(cms.getLocationName(), cms);
			}
		} catch (SQLException e) {
			logger.error("!! Error reading CMST to database: " + e.getLocalizedMessage(), e);
		} 
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch(Exception e) {}
		}
		
		return CMT;
	}
	
	public static void saveCMST(Connection conn, Map<String,CMST> CMT) {
		Statement stmt = null;
		PreparedStatement pstmt = null;		
		
		boolean bOldAutoCommit=true;
		logger.trace("Saving CMST");
		try
		{
			bOldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			String sSQL = "DELETE FROM CMST";
			stmt = conn.createStatement();
			stmt.execute(sSQL);
			
			sSQL = "INSERT INTO CMST(bandId,State,locationName,omniNumericId,portalId) VALUES(?,?,?,?,?)";
			pstmt = conn.prepareStatement(sSQL);
			for(CMST cms : CMT.values())
			{
				pstmt.clearParameters();
				// the band Id may be null so use setObject()
				pstmt.setObject(1, cms.getBandId(), Types.VARCHAR);
				pstmt.setString(2, cms.getState().name());
				pstmt.setString(3, cms.getLocationName());
				if (cms.getOmniNumericId() == null)
					pstmt.setObject(4,null);
				else
					pstmt.setString(4, cms.getOmniNumericId());
				if (cms.getPortalId() == null)
					pstmt.setObject(5,null);
				else
					pstmt.setString(5, cms.getPortalId());
				pstmt.execute();
			}
			
			conn.commit();
			conn.setAutoCommit(bOldAutoCommit);
		} 
		catch (SQLException e)
		{
			logger.error("!! Error writing CMST to database: " + e);
			
			try
			{
				conn.setAutoCommit(bOldAutoCommit);
			}
			catch(SQLException e2)
			{
			}
		}
		finally
		{			
			try
			{
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
			} 
			catch(Exception e) {}
		}
	}
}
