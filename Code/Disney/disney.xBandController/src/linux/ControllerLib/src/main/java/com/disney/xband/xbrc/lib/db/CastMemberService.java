package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import com.disney.xband.xbrc.lib.entity.CastMember;
import com.disney.xband.xbrc.lib.entity.CastMemberTapAction;

public class CastMemberService {
	
	public static Collection<CastMember> findAll(Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "select * from CastMember order by name";
			stmt = conn.prepareStatement(query);
			stmt.execute();
			rs = stmt.getResultSet();
			Collection<CastMember> results = new ArrayList<CastMember>();
			
			while(rs.next())
				results.add(instantiateCastMember(rs));
			
			return results;
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
	}
	
	public static void insertAll(Connection conn, Collection<CastMember> castMembers, boolean replace) throws SQLException {
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			if (replace)
			{
				stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM CastMember");
			}
			
			if (castMembers == null || castMembers.size() == 0)
				return;
			
			String query = "insert into CastMember (name, bandId, externalId, omniUserName, omniPassword, tapAction, enabled) values (?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(query);
			
			for (CastMember cm : castMembers)
			{
				pstmt.setString(1, cm.getName());
				pstmt.setString(2, cm.getBandId());
				pstmt.setString(3, cm.getExternalId());
				pstmt.setString(4, cm.getOmniUsername());
				pstmt.setString(5, cm.getOmniPassword());
				pstmt.setString(6, cm.getTapAction().toString());
				pstmt.setBoolean(7, cm.isEnabled());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} 
		finally
		{
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
			} catch(Exception e) {}
		}
	}
	
	public static CastMember instantiateCastMember(ResultSet rs) throws SQLException {
		return new CastMember(
			rs.getLong("id"),
			rs.getString("name"),
			rs.getString("bandId"),
			rs.getString("externalId"),
			rs.getString("omniUsername"),
			rs.getString("omniPassword"),
			CastMemberTapAction.valueOf(rs.getString("tapAction")),
			rs.getBoolean("enabled")	
		);
	}
}
