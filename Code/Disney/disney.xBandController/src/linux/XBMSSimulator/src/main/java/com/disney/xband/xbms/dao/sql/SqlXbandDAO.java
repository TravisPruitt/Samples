package com.disney.xband.xbms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import com.disney.xband.jms.lib.entity.xbms.Xband;
import com.disney.xband.xbms.dao.DAOException;
import com.disney.xband.xbms.dao.XbandDAO;

public class SqlXbandDAO implements XbandDAO
{
	private static String GET_XBAND = "{call usp_xband_retrieve(?)}";

	@Override
	public Xband GetXband(String xbandId) throws DAOException
	{
		Xband xband = null;
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;

		try
		{
			connection = SqlDAOFactory.createConnection();
			statement = connection.prepareCall(GET_XBAND);
			statement.setString("@xbandId", xbandId);
			
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				xband = new Xband();
			
				xband.setXbandId(xbandId);
				xband.setOptions("/xband-options/" + xbandId);
				xband.setAssignmentDateTime(resultSet.getString("assignmentDateTime"));
				xband.setBandRole(resultSet.getString("bandRole"));
				xband.setExternalNumber(resultSet.getString("externalNumber"));
				xband.setGuestId(resultSet.getString("guestId"));
				xband.setGuestIdType(resultSet.getString("guestIdType"));
				xband.setProductId(resultSet.getString("productId"));
				xband.setPublicId(resultSet.getLong("publicId"));
				xband.setSecondaryState(resultSet.getString("secondaryState"));
				xband.setSecureId(resultSet.getLong("secureId"));
				xband.setShortRangeTag(resultSet.getString("shortRangeTag"));
				xband.setState(resultSet.getString("state"));
				xband.setXbandOwnerId(resultSet.getString("xbandOwnerId"));
				xband.setXbandRequest("/xband-requests/" + resultSet.getString("xbandRequestId"));
				xband.setHistory("/xband-history/" + xbandId);
				xband.setSelf("/xband/" + xbandId);
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlDAOFactory.close(resultSet);
			SqlDAOFactory.close(statement);
			SqlDAOFactory.release(connection);
		}
		
		return xband;
	}

}
