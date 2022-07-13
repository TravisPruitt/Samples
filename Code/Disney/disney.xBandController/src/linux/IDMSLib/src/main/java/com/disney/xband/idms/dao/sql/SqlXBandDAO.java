package com.disney.xband.idms.dao.sql;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.disney.xband.idms.dao.BandLookupType;
import com.disney.xband.idms.dao.XBandDAO;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.model.XBand;
import com.disney.xband.idms.lib.model.XBandCollection;
import com.disney.xband.idms.lib.model.XBandPut;
import com.disney.xband.idms.lib.model.XbandAssociation;

public class SqlXBandDAO extends SqlDAOBase implements XBandDAO
{
	private static String GET_XBAND = "{call usp_xband_retrieve(?,?)}";
	private static String GET_XBANDS = "{call usp_xbands_retrieve(?,?)}";
	private static String CREATE_XBAND = "{call usp_xband_create(?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CREATE_XBAND_ASSOCIATION = "{call usp_xband_association_create(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String ASSIGN_XBAND = "{call usp_xband_assign(?,?)}";
	private static String ASSIGN_XBAND_BY_BIGINT = "{call usp_xband_assign_by_bigint(?,?)}";
	private static String ASSIGN_XBAND_BY_IDENTIFIER = "{call usp_xband_assign_by_identifier(?,?,?)}";
	private static String UNASSIGN_XBAND = "{call usp_xband_unassign(?,?)}";
	private static String REASSIGN_XBAND = "{call usp_xband_reassign(?,?)}";
	private static String UPDATE_XBAND = "{call usp_xband_update(?,?,?)}";

	static public ResultSetHandler<BandData> resultSetHandler = new BeanHandler<BandData>(BandData.class, new BasicRowProcessor());
	static public ResultSetHandler<List<BandData>> listResultSetHandler = new BeanListHandler<BandData>(BandData.class, new BasicRowProcessor());
	
	@Override
	public BandData GetBand(BandLookupType bandLookupType, String id) throws DAOException
	{
		BandData bandData = null;
		GuestData guestData = null;
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;
		ResultSet guestResultSet = null;
		
		try
		{
			connection = SqlServerDAOFactory.createConnection();
			statement = connection.prepareCall(GET_XBAND);
			statement.setLong("@bandLookupType", bandLookupType.getValue());
			statement.setString("@id", id);
			
			resultSet = statement.executeQuery();
			bandData = resultSetHandler.handle(resultSet);
			
			if (bandData != null)
			{
				if (statement.getMoreResults()) // Get bands
				{
					guestResultSet = statement.getResultSet();
					guestData = SqlGuestDAO.resultSetHandler.handle(guestResultSet);
				
					bandData.setGuest(guestData);
				}
			}
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(resultSet);
			SqlServerDAOFactory.close(guestResultSet);
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}
			
		return bandData;
	}
	

	@Override
	public XBandCollection GetXBands(String identifierType,
			String identifierValue) throws DAOException 
	{
		XBandCollection xBands = null;

		try
		{
			List<BandData> bandList = super.ExecuteProcedure(GET_XBANDS, listResultSetHandler, identifierType, identifierValue);
			
			if (bandList.size() > 0)
			{
				List<XBand> xbandList = new ArrayList<XBand>(bandList.size());

				for (BandData bandData : bandList) {
					xbandList.add(bandData.getXBand());
				}
				
				xBands = new XBandCollection();
				xBands.setXbands(xbandList);
				xBands.setItemCount(xbandList.size());
				xBands.setStartItem(0);
				xBands.setItemLimit(0);
			}
		}
		catch (Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return xBands;
	}

	@Override
	public boolean AssignXbandToGuest(long guestId, long xbandId)
			throws DAOException 
	{
		boolean result = false;

		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@guestId", guestId);
			parameters.put("@xbandId", xbandId);
	
			result = super.ExecuteUpdateProcedure(ASSIGN_XBAND_BY_BIGINT, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean AssignXbandToGuest(String identifierType, String identifierValue, long xbandId)
			throws DAOException 
	{
		boolean result = false;

		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@identifierType", identifierType);
			parameters.put("@identifierValue", identifierValue);
			parameters.put("@xbandId", xbandId);
	
			result = super.ExecuteUpdateProcedure(
					ASSIGN_XBAND_BY_IDENTIFIER, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean UnassignXBand(long guestId, long xbandId)
			throws DAOException 
	{
		boolean result = false;

		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@guestId", guestId);
			parameters.put("@xbandId", xbandId);
	
			result = super.ExecuteUpdateProcedure(
					UNASSIGN_XBAND, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean ReassignXBand(long guestId, String xbmsId)
			throws DAOException 
	{
		boolean result = false;

		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@guestId", guestId);
			parameters.put("@xbmsId", xbmsId);
	
			result = super.ExecuteUpdateProcedure(
					REASSIGN_XBAND, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean UpdateType(long xbandId, String newTypeName)
			throws DAOException 
	{
		boolean result = false;

		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			
			parameters.put("@xbandId", xbandId);
			parameters.put("@bandType",newTypeName);
			parameters.put("@active",true);
	
			result = super.ExecuteUpdateProcedure(UPDATE_XBAND, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean Update(long xbandId, boolean active, String typeName)
			throws DAOException 
	{
		boolean result = false;

		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			
			parameters.put("@xbandId", xbandId);
			parameters.put("@active",active);
			parameters.put("@bandType", typeName);
	
			result = super.ExecuteUpdateProcedure(UPDATE_XBAND, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public long Create(XBandPut xband)
			throws DAOException 
	{
		long result = 0;
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(CREATE_XBAND);

			statement.setString("@bandId", xband.getBandId());
			statement.setString("@longRangeId", xband.getLongRangeTag());
			statement.setString("@tapId", xband.getShortRangeTag());
			statement.setString("@secureId", xband.getSecureId());
			statement.setString("@UID", xband.getUID());
			statement.setString("@publicId", xband.getPublicId());
			statement.setString("@printedName", xband.getPrintedName());
			
			if(xband.getXbmsId() == null)
			{
				statement.setNull("@xbmsId", java.sql.Types.VARCHAR);
			}
			else
			{
				statement.setString("@xbmsId", xband.getXbmsId());
				
			}
			
			if(xband.getBandType() == null)
			{
				statement.setNull("@bandType", java.sql.Types.VARCHAR);
			}
			else
			{
				statement.setString("@bandType", xband.getBandType());
			}

			statement.setNull("@xbandRowId", java.sql.Types.CHAR);
			statement.registerOutParameter("@xbandId",
					java.sql.Types.BIGINT);
			
			statement.execute();
	
			result = statement.getLong("@xbandId");
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}
		return result;
	}


	@Override
	public long Create(XbandAssociation xbandAssociation) throws DAOException
	{
		long result = 0;
		Connection connection = null;
		CallableStatement statement = null;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(CREATE_XBAND_ASSOCIATION);
			
			statement.setString("@externalNumber", xbandAssociation.getExternalNumber());
			statement.setString("@longRangeTag", xbandAssociation.getLongRangeTag());
			statement.setString("@shortRangeTag", xbandAssociation.getShortRangeTag());
			statement.setString("@secureId", xbandAssociation.getSecureId());
			statement.setString("@uid", xbandAssociation.getUID());
			statement.setString("@publicId", xbandAssociation.getPublicId());
			statement.setString("@printedName", xbandAssociation.getPrintedName());
			statement.setString("@xbmsId", xbandAssociation.getXbmsId());
			statement.setString("@primaryState", xbandAssociation.getPrimaryState());
			statement.setString("@secondaryState", xbandAssociation.getSecondaryState());
			statement.setString("@guestIdType", xbandAssociation.getGuestIdType());
			statement.setString("@guestIdValue", xbandAssociation.getGuestIdValue());
			statement.setString("@xbandOwnerId", xbandAssociation.getXbandOwnerId());
            final Date datetime = xbandAssociation.getAssignmentDateTime() == null ? new Date() : xbandAssociation.getAssignmentDateTime();
            statement.setTimestamp("@assignmentDateTime", new Timestamp(datetime.getTime()));
			
			if(xbandAssociation.getXbandRequestId() == null)
			{
				statement.setNull("@xbandRequestId", java.sql.Types.VARCHAR);

			}
			else
			{
				statement.setString("@xbandRequestId", xbandAssociation.getXbandRequestId());			
			}
			
			if(xbandAssociation.getBandType() == null)
			{
				statement.setNull("@bandType", java.sql.Types.VARCHAR);
			}
			else
			{
				statement.setString("@bandType", xbandAssociation.getBandType());
			}

			statement.registerOutParameter("@xbandId",
					java.sql.Types.BIGINT);
			
			statement.execute();
	
			result = statement.getLong("@xbandId");
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		finally
		{
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}
		return result;
	}
}
