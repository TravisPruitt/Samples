package com.disney.xband.idms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;

import com.disney.xband.idms.dao.DAOException;

public class SqlDAOBase 
{

	public <T> T ExecuteProcedure(String procedureName, 
			ResultSetHandler<T> resultSetHandler,
			Map<String,Object> parameters) throws DAOException
	{	
		Connection connection = null;
		CallableStatement statement = null;
		T result = null;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			statement = connection.prepareCall(procedureName);
			
			SetParameters(statement,parameters);
			
			result = resultSetHandler.handle(statement.executeQuery());
		}
		catch (Exception ex)
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
	
	public <T> long ExecuteCreateProcedure(String procedureName, 
			Map<String,Object> parameters, 
			String outputParameter) throws DAOException
	{	
		Connection connection = null;
		CallableStatement statement = null;
		long result = 0;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			statement = connection.prepareCall(procedureName);

			SetParameters(statement,parameters);
			
			statement.registerOutParameter(outputParameter, java.sql.Types.BIGINT);
			
			statement.execute();
			
			result = statement.getLong(outputParameter);
		}
		catch (Exception ex)
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

	public <T> boolean ExecuteUpdateProcedure(String procedureName, 
			Map<String,Object> parameters) throws DAOException
	{	
		Connection connection = null;
		CallableStatement statement = null;
		boolean result = false;

		try
		{
			connection = SqlServerDAOFactory.createConnection();
			statement = connection.prepareCall(procedureName);

			SetParameters(statement,parameters);
			
			statement.execute();
			
			result = true;
		}
		catch (Exception ex)
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

	public <T> boolean ExecuteDeleteProcedure(String procedureName, 
			Map<String,Object> parameters) throws DAOException
	{	
		return ExecuteUpdateProcedure(procedureName, parameters);
	}

	public <T> T ExecuteProcedure(String procedureName, 
			ResultSetHandler<T> resultSetHandler, String identifierType, String identifierValue) throws DAOException
	{	
		HashMap<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("@identifierType", identifierType);
		parameters.put("@identifierValue", identifierValue);
		
		return ExecuteProcedure(procedureName, resultSetHandler, parameters);
	}
	
	private void SetParameters(CallableStatement statement, Map<String,Object> parameters) throws SQLException
	{
		for(String key : parameters.keySet())
		{
			Object parameter = parameters.get(key);
			
			if (parameter == null)
			{
				statement.setObject(key, null);
			}
			else
			{
				Class<?> type = parameter.getClass();
			
				if(type == long.class || type == Long.class)
				{
					statement.setLong(key, ((Number)parameter).longValue()); 
				}
				else if(type == String.class)
				{
					statement.setString(key, parameter.toString()); 
				}
				else if(type == java.lang.Integer.class)
				{
					statement.setInt(key, ((Number)parameter).intValue()); 
				}
				else if(type == java.lang.Boolean.class)
				{
					statement.setBoolean(key, (Boolean) parameter); 
				}
				else if(type == Date.class)
				{
					java.util.Date utilDate = (java.util.Date)parameter;
					java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
					statement.setDate(key, sqlDate); 
				}
				else
				{
					throw new SQLException("Invalid parameter type");
				}
			}
		}
	}
}
