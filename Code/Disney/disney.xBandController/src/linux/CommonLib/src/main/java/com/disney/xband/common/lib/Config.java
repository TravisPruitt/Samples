package com.disney.xband.common.lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Singleton which attempts to connect to a database using the provided <CODE>Connection</CODE> object
 * and updates the configuration object with property/value pairs stored in the Config database table.
 * 
 * The Config table has three columns: class, property, and value
 * - class - either the fully qualified class name (default), or an alternative name specified using a @PersistName annotation
 * - property - a field's name (default), or an alternative name specified using the @PersistName annotation
 * - value - a non-transient simple type variables of type: boolean, int, long, float, double, and java.util.String
 * 
 * Variable types not mentioned above and any transient variables will be ignored.
 * 
 * @throws SQLException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 */
public class Config {
	
	private transient static Logger logger = Logger.getLogger(Config.class);
	
	public static Config getInstance() {
		return ConfigHolder.instance;
	}
	
	public void read(Connection conn, Object obj) throws SQLException, IllegalArgumentException, IllegalAccessException 
	{	
		if (conn == null) 
			throw new IllegalArgumentException("Connection object is required.");
		
		if (obj == null) 
			throw new IllegalArgumentException("ConfigInfo object is required");
		
		/* get configuration values from the database */
		
		PreparedStatement stmt = null;
		PreparedStatement insertStmt = null;
        ResultSet rs = null;

		try 
		{
			//class or annotated name
			String persistName = determineClassName(obj.getClass());
			
			//query the database
			String query = "SELECT class, property, value from Config where class = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setString(1, persistName);
			stmt.execute();
			rs = stmt.getResultSet();
			
			//hash property/value pairs for O(1) lookups
			Map<String, String> properties = new HashMap<String, String>();
			while (rs.next())
				properties.put(rs.getString("property").toLowerCase(), rs.getString("value"));
			
			//prepare update statement for new fields
			insertStmt = prepareInsert(conn, obj);
			
			//match properties to fields
			String fieldDBName = null;
			for (Field f: obj.getClass().getDeclaredFields())
			{
				//don't process compiler generated fields
				if (f.isSynthetic())
					continue;
				/*
				 * Fields which should not get serialized/persisted should be declared as 'transient'!!!
				 */
				if ( (f.getModifiers() & Modifier.TRANSIENT) != 0)
						continue;
				
				//use either field's defined name or the annotated version, if provided
				fieldDBName = determineName(f);
				
				//new field, persist for next time
				if (!properties.containsKey(fieldDBName))
				{
					//null value means that Config does not handle this particular field's type
					String value = determineValue(f, obj);
					if (value == null)
						continue;
					
					//prepareInsert set the class name
					insertStmt.setString(2, fieldDBName);
					insertStmt.setString(3, value);
					insertStmt.executeUpdate();
					stmt.clearParameters();
					continue;
				};
				
				//update the configuration object with DB values
				setFieldValue(f, fieldDBName, obj, properties);
			}
		} 
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                }
            }

            if (insertStmt != null) {
                try {
                    insertStmt.close();
                }
                catch (Exception e) {
                }
            }
        }
	}
	
	/**
	 * Sets the value of the specified property on the specified object.
	 * @param conn
	 * @param obj a configuration object
	 * @param property name of a variable defined on the configuration object. NOT the persisted name.
	 * @return value of this property
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void read(Connection conn, Object obj, String property) 
			throws SQLException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException
	{	
		if (conn == null) 
			throw new IllegalArgumentException("Connection object is required.");
		
		if (obj == null) 
			throw new IllegalArgumentException("ConfigInfo object is required");
		
		if (property == null || property.trim().isEmpty())
			return;
		
		PreparedStatement query = null;
		PreparedStatement insert = null;
        ResultSet rs = null;

		try
		{
			//class or annotated name
			String persistClass = determineClassName(obj.getClass());
			
			//use either field's defined name or the annotated version, if provided
			Field field = null;
			for (Field f: obj.getClass().getDeclaredFields())
			{
				//don't process compiler generated fields
				if (f.isSynthetic())
					continue;
				/*
				 * Fields which should not get serialized/persisted should be declared as 'transient'!!!
				 */
				if ( (f.getModifiers() & Modifier.TRANSIENT) != 0)
					continue;
				
				if (f.getName().equals(property))
				{
					field = f;
					break;
				}
			}
			
			if (field == null)
				throw new NoSuchFieldException("Variable " + property + " is undefined on class " + obj.getClass().getName());
				
			String persistProperty = determineName(field);
			String persistValue = determineValue(field, obj);
			
			//query the database
			query = conn.prepareStatement("SELECT class, property, value from Config where class = ? and property = ?");
			query.clearParameters();
			query.setString(1, persistClass);
			query.setString(2, persistProperty);	
			query.execute();
			rs = query.getResultSet();
			
			String value = null;
			if (rs != null)
			{
				if (rs.next())
				{
					value = rs.getString("value");
					if (!rs.wasNull())
						persistValue = value;
					
					//update the configuration object with DB values
					setFieldValue(field, obj, persistValue);
					
					return;
				}
				else
				{
					// property not yet persisted, try persisting
					insert = conn.prepareStatement("INSERT INTO Config(value,class,property) VALUES(?,?,?)");
					insert.clearParameters();
					insert.setString(1, persistValue);
					insert.setString(2, persistClass);
					insert.setString(3, persistProperty);
					insert.executeUpdate();
				}
			} 
			else 
			{
				// property not yet persisted, try persisting
				insert = conn.prepareStatement("INSERT INTO Config(value,class,property) VALUES(?,?,?)");
				insert.clearParameters();
				insert.setString(1, persistValue);
				insert.setString(2, persistClass);
				insert.setString(3, persistProperty);
				insert.executeUpdate();
			}
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (query != null) {
                try {
                    query.close();
                }
                catch (Exception e) {
                }
            }

            if (insert != null) {
                try {
                    insert.close();
                }
                catch (Exception e) {
                }
            }
        }
	}
	
	public void write(Connection conn, Object obj) throws IllegalArgumentException, IllegalAccessException, SQLException 
	{
		if (conn == null) 
			throw new IllegalArgumentException("Connection object is required.");
		
		if (obj == null) 
			throw new IllegalArgumentException("ConfigInfo object is required");

		//match properties to fields
		String property = null;
		String value = null;
		for (Field f: obj.getClass().getDeclaredFields())
		{
			//don't process compiler generated fields
			if (f.isSynthetic())
				continue;
			/*
			 * Fields which should not get serialized/persisted should be declared as 'transient'!!!
			 */
			if ( (f.getModifiers() & Modifier.TRANSIENT) != 0)
				continue;

			property = f.getName();
			value = determineValue(f, obj);

			if (value == null)
				continue;

			write(conn, obj, property, value);
		}
	}
	
	public void write(Connection conn, Object obj, String property, String value) 
			throws IllegalArgumentException, IllegalAccessException, SQLException
	{
		if (conn == null) 
			throw new IllegalArgumentException("Connection object is required.");
		
		if (obj == null) 
			throw new IllegalArgumentException("ConfigInfo object is required.");
		
		if (property == null || property.trim().isEmpty())
			throw new IllegalArgumentException("Property name is required.");
		
		PreparedStatement select = null;
		PreparedStatement insert = null;
		PreparedStatement update = null;
        ResultSet rs = null;

		try
		{
			String persistClass = determineClassName(obj.getClass());

			Field field = null;
			for (Field f: obj.getClass().getDeclaredFields())
			{
				//don't process compiler generated fields
				if (f.isSynthetic())
					continue;
				/*
				 * Fields which should not get serialized/persisted should be declared as 'transient'!!!
				 */
				if ( (f.getModifiers() & Modifier.TRANSIENT) != 0)
					continue;
				
				if (f.getName().toLowerCase().equals(property.toLowerCase()))
				{
					field = f;
					break;
				}
			}
			
			if (field == null)
				logger.error("Variable " + property + " is undefined on class " + obj.getClass().getName());
			
			String persistProperty = determineName(field);
			
			// empty is a valid value, null means the value was not provided to this function
			// and that the default object's value should be used
			String persistValue = value;
			if (value == null)
				persistValue = determineValue(field, obj);
			
			// see whether to run an insert or an update
			select = conn.prepareStatement("SELECT class,property,value FROM Config WHERE class=? AND property=?");
			select.clearParameters();
			select.setString(1, persistClass);
			select.setString(2, persistProperty);
			select.execute();
			rs = select.getResultSet();
			if (rs != null && rs.next())
			{
				update = conn.prepareStatement("UPDATE Config SET value = ? WHERE class = ? AND property = ?");
				update.clearParameters();
				update.setString(1, persistValue);
				update.setString(2, persistClass);
				update.setString(3, persistProperty);
				update.execute();
			}
			else
			{			
				insert = conn.prepareStatement("INSERT INTO Config(class,property,value) VALUES(?,?,?)");
				insert.clearParameters();
				insert.setString(1, persistClass);
				insert.setString(2, persistProperty);
				insert.setString(3, persistValue);
				insert.executeUpdate();
			}
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (select != null) {
                try {
                    select.close();
                }
                catch (Exception e) {
                }
            }

            if (insert != null) {
                try {
                    insert.close();
                }
                catch (Exception e) {
                }
            }

            if (update != null) {
                try {
                    update.close();
                }
                catch (Exception e) {
                }
            }
        }
	}
	
	@SuppressWarnings("unchecked")
	private String determineValue(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		String value = null;
		
		f.setAccessible(true);
		
		if (f.getType() == boolean.class)
			value = String.valueOf(f.getBoolean(obj));
		else if (f.getType() == int.class)
			value = String.valueOf(f.getInt(obj));
		else if (f.getType() == long.class)
			value = String.valueOf(f.getLong(obj));
		else if (f.getType() == float.class)
			value = String.valueOf(f.getFloat(obj));
		else if (f.getType() == double.class)
			value = String.valueOf(f.getDouble(obj));
		else if (f.getType() == String.class)
			value = f.get(obj) == null ? "" : String.valueOf(f.get(obj));
		else if (f.getType() == List.class)
			value = f.get(obj) == null ? "" : listToString((List<String>)f.get(obj));
		else if (Enum.class.isAssignableFrom(f.getType()))
			value = f.get(obj) == null ? "" : ((Enum)f.get(obj)).name();
		else 
			logger.warn("Config does not handle type: " + f.getType() + ". Variable not persisted in " + obj.getClass().getName());
		
		return value;
	}
	
	private void setFieldValue(Field f, String fieldDBName, Object obj, Map<String, String> properties) throws IllegalArgumentException, IllegalAccessException
	{
		f.setAccessible(true);
		
		setFieldValue(f, obj, properties.get(fieldDBName));	
	}
	
	private void setFieldValue(Field f, Object obj, String value) throws IllegalArgumentException, IllegalAccessException
	{
		f.setAccessible(true);
		
		if (f.getType() == boolean.class)
			f.set(obj, Boolean.parseBoolean(value));
		else if (f.getType() == int.class)
			f.set(obj, Integer.parseInt(value));
		else if (f.getType() == long.class)
			f.set(obj, Long.parseLong(value));
		else if (f.getType() == float.class)
			f.set(obj, Float.parseFloat(value));
		else if (f.getType() == double.class)
			f.set(obj, Double.parseDouble(value));
		else if (f.getType() == String.class)
			f.set(obj, value);
		else if (f.getType() == List.class)
			f.set(obj, parseList(value));
		else if (Enum.class.isAssignableFrom(f.getType())) 
			f.set(obj, Enum.valueOf((Class<Enum>)f.getType(), value));
		else 
			logger.warn("Config does not handle type: " + f.getType() + ". Variable not persisted in " + obj.getClass().getName());
	}
	
	private String determineName(Field f) 
	{
		String fieldDBName = null;
		if (f.isAnnotationPresent(PersistName.class))
			//field's name and DB name don't match, using annotated name
			fieldDBName = f.getAnnotation(PersistName.class).value().toLowerCase();
		else 
			fieldDBName = f.getName().toLowerCase();
		
		return fieldDBName;
	}
	
	private String determineClassName(Class<?> c){
		String persistName = c.getName();	//default
		if (c.isAnnotationPresent(PersistName.class))
			persistName = c.getAnnotation(PersistName.class).value();
		
		return persistName;
	}
	
	private PreparedStatement prepareInsert(Connection conn, Object obj) throws SQLException
	{
		String persistName = determineClassName(obj.getClass());
		
		String query = "INSERT INTO Config (class, property, value) VALUES(?,?,?)";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.clearParameters();
		stmt.setString(1, persistName);
		
		return stmt;
	}
	
	public static List<String> parseList(String value) 
	{
		List<String> list = new LinkedList<String>();
		if (value == null || value.isEmpty())
			return list;
		String[] tokens = value.split("\\|");
		for (String token : tokens)
			list.add(token);
		return list;
	}
	
	public static String listToString(List<String> list)
	{
		if (list == null || list.isEmpty())
			return "";
		
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = list.iterator();
		sb.append(it.next());
		
		while(it.hasNext()) {
			sb.append("|");
			sb.append(it.next());
		}
			
		return sb.toString();
	}
	
	private Config() {}
	
	private static class ConfigHolder {
		public static final Config instance = new Config();
	}
}
