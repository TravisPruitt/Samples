package com.disney.xband.xview.controllers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import com.disney.xband.xview.UIProperties;
import com.disney.xband.xview.lib.model.baseModel;
import com.mchange.v2.c3p0.DataSources;
import org.apache.log4j.Logger;


public abstract class baseController implements IController {
    private static Logger logger = Logger.getLogger(baseController.class);
	public Connection connection;
	private static DataSource pooled;
	
	static {
		UIProperties uip = new UIProperties();
		String url = uip.getPropertyValue("databaseURL");
		String user = uip.getPropertyValue("databaseUser");
		String password = uip.getPropertyValue("databasePassword");
		
		try {
			Initialize(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void Initialize(String url, String user, String password ) throws SQLException, ClassNotFoundException
	{
		Class.forName("com.mysql.jdbc.Driver");
		DataSource unpooled = DataSources.unpooledDataSource(url, user, password);
		
		pooled = DataSources.pooledDataSource(unpooled);
	}
	
	public baseController()
	{
		
	}
	
	// Execute an SQL statement with only a bool return.
	public static boolean ExecuteSQL(String sql, String... params) throws Exception
	{
		Connection conn = null;
		boolean retVal = false;
		PreparedStatement stmt = null;
		
		try
		{
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            if(params != null) {
                for(int i = 0; i < params.length; ++i) {
                    stmt.setString(i + 1, params[i]);
                }
            }

            if(logger.isTraceEnabled()) {
                logger.trace("Executing SQL: " + stmt.toString());
            }

			stmt.execute(sql);
			retVal = true;
			
		}
		catch (Exception ex)
		{
			retVal = false;
			throw ex;
			
		}
		finally
		{
            releaseStatement(stmt);
			releaseConnection(conn);
		}
		
		return retVal;
	}
	
	public static List<baseModel> getResultSetAndClose(String sql, baseModel model, String... params) throws Exception
	{
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
		List<baseModel> result = new ArrayList<baseModel>();
		
		try
		{
            conn = getConnection();
			stmt = conn.prepareStatement(sql);

            if(params != null) {
                for(int i = 0; i < params.length; ++i) {
                    stmt.setString(i + 1, params[i]);
                }
            }

            if(logger.isTraceEnabled()) {
                logger.trace("Executing SQL: " + stmt.toString());
            }

			stmt.execute(sql);
			rs = stmt.getResultSet();
			
			while (rs.next())
			{
				baseModel c =  model.getClass().newInstance();
				// need to map the result set to a model.
				result.add( MapResultSetToModel(rs, c));
				c= null;
			}
		}
		catch (Exception ex) {
			throw ex;
		}
        finally {
            releaseResultSet(rs);
            releaseStatement(stmt);
            releaseConnection(conn);
        }

		return result;

	}
	
	public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1); //+ s.substring(1).toLowerCase();
    }

	private static Object getValueFromRs(ResultSet rs, Field field) throws SQLException
	{
		Object retVal = null;
		String fieldName = field.getName();
		
		//String typeName = field.getType().getName();

				if (field.getType().equals(String.class))
					retVal = new String(rs.getString(fieldName));
				
				if (field.getType().equals(BigInteger.class))
				{
					BigInteger val = BigInteger.valueOf(rs.getLong(fieldName));
					retVal = val;
				}
				
				if (field.getType().equals(Object.class))
				{
					Object val = rs.getObject(fieldName);
					retVal = val;
				}
				
				if (field.getType().equals(Date.class))
				{
					Date val = rs.getDate(fieldName);
					retVal = val;
				}
				
				if (field.getType().equals(boolean.class))
				{
					boolean val = rs.getBoolean(fieldName);
					retVal = val;
				}
				
				if (field.getType().equals(int.class))
				{
					int val = rs.getInt(fieldName);
					retVal = val;
				}
				
				
			
	
		return retVal;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static baseModel MapResultSetToModel(ResultSet rs, baseModel model) throws SQLException
	{
	
		Class c = model.getClass();
		Field[] fields = c.getDeclaredFields();
		
		for(Field field : fields)
		{
			try
			{
				
				if (field.getType().equals(int.class))
				{
					String methodName = "set"+ capitalize(field.getName());
					Method m;
					try {
							m = c.getMethod(methodName, field.getType() );
				
							//m.invoke(model, rs.getString(field.getName()));
							m.invoke(model, getValueFromRs(rs, field));
					} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
				}
				
				if (field.getType().equals(Date.class))
				{
					String methodName = "set"+ capitalize(field.getName());
					Method m;
					try {
							m = c.getMethod(methodName, field.getType() );
				
							//m.invoke(model, rs.getString(field.getName()));
							m.invoke(model, getValueFromRs(rs, field));
					} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
				}
				
				if (field.getType().equals(String.class))
				{
					String methodName = "set"+ capitalize(field.getName());
					Method m;
					try {
							m = c.getMethod(methodName, field.getType() );
				
							//m.invoke(model, rs.getString(field.getName()));
							m.invoke(model, getValueFromRs(rs, field));
					} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
			
			
				}
			
				if (field.getType().equals(BigInteger.class))
				{
					String methodName = "set"+ capitalize(field.getName());
					Method m;
					
					try {
						m = c.getMethod(methodName, BigInteger.class );
						m.invoke(model, BigInteger.valueOf(rs.getLong(field.getName())));
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (field.getType().equals(Object.class))
				{
					String methodName = "set"+ capitalize(field.getName());
					Method m;
					
					try {
						m = c.getMethod(methodName, Object.class );
						m.invoke(model, getValueFromRs(rs, field));
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (field.getType().equals(boolean.class))
				{
					String methodName = "set"+ capitalize(field.getName());
					Method m;
					
					try {
						m = c.getMethod(methodName, boolean.class );
						m.invoke(model, getValueFromRs(rs, field));
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
			catch (Exception ex)
			{
				// Don't care really.
			}
			
		}

		return  model;
	}
	
	public static Connection getConnection() throws SQLException
	{
		return pooled.getConnection();
	}
	
	public static void releaseConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
            }
		}
		catch (Exception e) {
		}
	}

    public static void releaseStatement(Statement s)
    {
        try {
            if (s != null) {
                s.close();
            }
        }
        catch (Exception e) {
        }
    }

    public static void releaseResultSet(ResultSet rs)
    {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (Exception e) {
        }
    }
}
