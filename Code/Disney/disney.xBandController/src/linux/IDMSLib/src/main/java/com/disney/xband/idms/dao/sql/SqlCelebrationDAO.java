package com.disney.xband.idms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.disney.xband.idms.celebration.OneViewCelebrationGuestRowProcessor;
import com.disney.xband.idms.celebration.OneViewCelebrationRowProcessor;
import com.disney.xband.idms.dao.CelebrationDAO;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuest;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuestPost;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuestPut;
import com.disney.xband.idms.lib.model.oneview.CelebrationPost;
import com.disney.xband.idms.lib.model.oneview.CelebrationPut;

public class SqlCelebrationDAO extends SqlDAOBase implements CelebrationDAO
{
	private static String GET_CELEBRATION = "{call usp_celebration_retrieve(?)}";
	private static String CREATE_CELEBRATION = "{call usp_celebration_create(?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String UPDATE_CELEBRATION = "{call usp_celebration_update(?,?,?,?,?,?,?,?,?,?)}";
	private static String DELETE_CELEBRATION = "{call usp_celebration_delete(?)}";
	private static String ADD_CELEBRATION_GUEST = "{call usp_celebration_guest_add(?,?,?,?)}";
	private static String UPDATE_CELEBRATION_GUEST = "{call usp_celebration_guest_update(?,?,?)}";
	private static String DELETE_CELEBRATION_GUEST = "{call usp_celebration_guest_delete(?,?)}";

	public Celebration GetCelebration(String identifierType, String identifierValue) throws DAOException 
	{
		Celebration celebration = null;
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;
		ResultSet guests = null;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			
			statement = connection.prepareCall(GET_CELEBRATION);
			statement.setLong("@celebrationId", Long.parseLong(identifierValue));

			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				celebration = OneViewCelebrationRowProcessor.INSTANCE.toBean(resultSet, Celebration.class);
			
				if (statement.getMoreResults()) // Get celebration guests
				{
					guests = statement.getResultSet();
	
					List<CelebrationGuest> guestList = 
							OneViewCelebrationGuestRowProcessor.INSTANCE.toBeanList(guests, CelebrationGuest.class);
					
					for(CelebrationGuest guest : guestList )
					{
						celebration.Add(guest);
					}
					
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
			SqlServerDAOFactory.close(guests);
			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}
		
		return celebration;
	}

	@Override
	public long SaveCelebration(CelebrationPost celebration) throws DAOException 
	{
		long result = 0;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@xid", celebration.getXID());
			parameters.put("@name", StringEscapeUtils.escapeSql(celebration.getName()));
			parameters.put("@milestone", StringEscapeUtils.escapeSql(celebration.getMilestone()));
			parameters.put("@type",StringEscapeUtils.escapeSql(celebration.getType()));
			parameters.put("@role",StringEscapeUtils.escapeSql(celebration.getRole()));
			parameters.put("@date",StringEscapeUtils.escapeSql(celebration.getDate()));
			parameters.put("@startDate",celebration.getStartDate());
			parameters.put("@endDate",celebration.getEndDate());
			parameters.put("@recognitionDate",celebration.getRecognitionDate());
			parameters.put("@surpriseIndicator",celebration.hasSurpriseIndicator());
			parameters.put("@comment",celebration.getComment());
			
			result = super.ExecuteCreateProcedure(CREATE_CELEBRATION, parameters, "@celebrationId");
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		if(result == 0)
		{
			throw new DAOException("Save Celebration failed.");
		}
		
		return result;
	}

	@Override
	public boolean UpdateCelebration(CelebrationPut celebration)
			throws DAOException 
	{
		boolean result = false;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@celebrationId", celebration.getCelebrationId());
			parameters.put("@name", StringEscapeUtils.escapeSql(celebration.getName()));
			parameters.put("@milestone", StringEscapeUtils.escapeSql(celebration.getMilestone()));
			parameters.put("@type",StringEscapeUtils.escapeSql(celebration.getType()));
			parameters.put("@date",StringEscapeUtils.escapeSql(celebration.getDate()));
			parameters.put("@startDate",celebration.getStartDate());
			parameters.put("@endDate",celebration.getEndDate());
			parameters.put("@recognitionDate",celebration.getRecognitionDate());
			parameters.put("@surpriseIndicator",celebration.hasSurpriseIndicator());
			parameters.put("@comment",celebration.getComment());

			result = super.ExecuteUpdateProcedure(UPDATE_CELEBRATION, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean DeleteCelebration(long celebrationId)
			throws DAOException 
	{
		boolean result = false;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@celebrationId",celebrationId);

			result = super.ExecuteDeleteProcedure(DELETE_CELEBRATION, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean AddCelebrationGuest(CelebrationGuestPost celebrationGuest)
			throws DAOException 
	{
		boolean result = false;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@celebrationId",celebrationGuest.getCelebrationId());
			parameters.put("@xid",celebrationGuest.getXID());
			parameters.put("@role",celebrationGuest.getRole());
			parameters.put("@primaryGuest",false);

			result = super.ExecuteDeleteProcedure(ADD_CELEBRATION_GUEST, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean UpdateCelebrationGuest(CelebrationGuestPut celebrationGuest)
			throws DAOException 
	{
		boolean result = false;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@celebrationId",celebrationGuest.getCelebrationId());
			parameters.put("@xid",celebrationGuest.getXID());
			parameters.put("@role",celebrationGuest.getRole());

			result = super.ExecuteDeleteProcedure(UPDATE_CELEBRATION_GUEST, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

	@Override
	public boolean DeleteCelebrationGuest(CelebrationGuestPut celebrationGuest)
			throws DAOException 
	{
		boolean result = false;
		
		try
		{
			HashMap<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("@celebrationId",celebrationGuest.getCelebrationId());
			parameters.put("@xid",celebrationGuest.getXID());
	
			result = super.ExecuteDeleteProcedure(DELETE_CELEBRATION_GUEST, parameters);
		}
		catch(Exception ex)
		{
			throw new DAOException(ex);
		}
		
		return result;
	}

}
