package com.disney.xband.xi.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class TestDAO extends DAO
{
	public TestDAO()
	{
		super();
		return;
	}

	public String dbTest()
	{
		ReturnMessage rmsg = new ReturnMessage();
		Connection connection = null;
		String query = "select getdate()";
		Statement s = null;
		ResultSet rs = null;

		try
		{
			connection = this.getConnection();
			s = connection.createStatement();
			rs = s.executeQuery(query);
			rs.next();
			Timestamp ts = rs.getTimestamp(1);
			rmsg.addData("timestamp", ts);
			return gson.toJson(rmsg);
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "testdao");	
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "testdao");	
		}
		finally
		{
            if(rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {
                }
            }

            if(s != null) {
                try {
                    s.close();
                }
                catch (Exception ignore) {
                }
            }

            connectionPool.release(connection);
		}
	}
}
