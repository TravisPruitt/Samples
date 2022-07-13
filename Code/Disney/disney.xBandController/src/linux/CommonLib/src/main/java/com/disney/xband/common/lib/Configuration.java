package com.disney.xband.common.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Used to convert between the state of an object of type <CODE>Configuration</CODE> and meta data rich 
 * collection of {@link com.disney.xband.lib.controllerapi.ConfigInfo} objects;
 * provided their member variables are annotated with the {@link com.disney.xband.xbrc.lib.config.MetaData}.
 * 
 * Ignores <CODE>transient</CODE> member variables.
 * 
 * @author iwona
 */
public abstract class Configuration {
	
	private static Logger logger = Logger.getLogger(Configuration.class);
	private transient boolean initialized = false;
	
	public Configuration(){
		
		String defaultValue = null;
		MetaData md = null;
		@SuppressWarnings("rawtypes")
		Constructor constructor = null;
		
		for (Field f: this.getClass().getDeclaredFields())
		{
			//don't process compiler generated fields
			if (f.isSynthetic())
				continue;
			
			try {
				if (f.isAnnotationPresent(MetaData.class)){
					
					md = f.getAnnotation(MetaData.class);
					defaultValue = md.defaultValue();
					
					f.setAccessible(true);
					
					if (f.getType().equals(byte.class)){
						f.set(this, new Byte(defaultValue));
					} else if (f.getType().equals(char.class)){
						char[] chars = defaultValue.toCharArray();
						if (chars.length > 0)
							f.set(this, new Character(chars[0]));
					} else if (f.getType().equals(int.class)){
						f.set(this, new Integer(defaultValue));
					} else if (f.getType().equals(long.class)){
						f.set(this, new Long(defaultValue));
					} else if (f.getType().equals(double.class)){
						f.set(this, new Double(defaultValue));
					} else if (f.getType().equals(boolean.class)){
						f.set(this, new Boolean(defaultValue));
					} else if (f.getType().equals(List.class)) {
						f.set(this, parseList(defaultValue));
					} else if (Enum.class.isAssignableFrom(f.getType())) {
						f.set(this, Enum.valueOf((Class<Enum>)f.getType(), defaultValue));
					} else {
						try {
							/* 
							 * create an instance of an any complex type, provided that that
							 * type defines a constructor which takes a single parameter of type java.util.String
							 */
							constructor = f.getType().getConstructor(String.class);
							constructor.setAccessible(true);
							f.set(this, constructor.newInstance(defaultValue));
						} catch (Exception e){
							// nothing we can do, don't set
						}
					}
				}
			} catch (Exception e){}
		}
	}
	
    public boolean isEqual(final Configuration conf) {
        if (!this.getClass().getName().equals(conf.getClass().getName())) {
            return false;
        }

        for (Field f : this.getClass().getDeclaredFields()) {
            if (f.isSynthetic()) {
                continue;
            }

            try {
                if (f.isAnnotationPresent(MetaData.class)) {
                    final Field cF = conf.getClass().getDeclaredField(f.getName());

                    f.setAccessible(true);
                    cF.setAccessible(true);

                    final Object fV = cF.get(conf);
                    final Object cV = f.get(this);

                    if(fV == null) {
                        if(cV == null) {
                            continue;
                        }
                        else {
                            return false;
                        }
                    }
                    else {
                        if(cV == null) {
                            return false;
                        }
                        else {
                            if(!fV.equals(cV)) {
                                return false;
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
            }
        }

        return true;
    }

	public synchronized void initialize(ConnectionPool cp){
		if (initialized)
			return;	//already initialized

		Connection conn = null;
		try {
			conn = cp.getConnection();
			
			Config.getInstance().read(conn, this);
			
			initHook(conn);
			
			initialized = true;
			
		} catch (Exception e) {
			logger.error("Failed to read SNMP configuration from the data base. Using class defaults.");
		} finally {
			cp.releaseConnection(conn);
		}	
	}

	public synchronized void initialize(Connection conn) throws Exception {
		if (initialized)
			return;	//already initialized
		
		Config.getInstance().read(conn, this);	
		
		initHook(conn);
		
		initialized = true;
	}
	
	/**
	 * A hook method for subtype specific initialization.
	 */
	protected abstract void initHook(Connection conn);
	
	/**
	 * Initializes the <CODE>instance</CODE> with default values as specified by
	 * the @link com.disney.xband.xbrc.lib.config.MetaData#defaultValue
	 * 
	 * @param instance
	 */
	@SuppressWarnings("unused")
	private static <T extends Configuration> void initDefaults(T instance){
		
		if (instance == null)
			return;
		
		String defaultValue = null;
		MetaData md = null;
		@SuppressWarnings("rawtypes")
		Constructor constructor = null;
		
		for (Field f: instance.getClass().getDeclaredFields())
		{
			//don't process compiler generated fields
			if (f.isSynthetic())
				continue;
			
			try {
				if (f.isAnnotationPresent(MetaData.class)){
					
					md = f.getAnnotation(MetaData.class);
					defaultValue = md.defaultValue();
					
					f.setAccessible(true);
					
					if (f.getType().equals(byte.class)){
						f.set(instance, new Byte(defaultValue));
					} else if (f.getType().equals(char.class)){
						char[] chars = defaultValue.toCharArray();
						if (chars.length > 0)
							f.set(instance, new Character(chars[0]));
					} else if (f.getType().equals(int.class)){
						f.set(instance, new Integer(defaultValue));
					} else if (f.getType().equals(long.class)){
						f.set(instance, new Long(defaultValue));
					} else if (f.getType().equals(double.class)){
						f.set(instance, new Double(defaultValue));
					} else if (f.getType().equals(boolean.class)){
						f.set(instance, new Boolean(defaultValue));
					} else if (f.getType().equals(List.class)) {
						f.set(instance, parseList(defaultValue)); 
					} else if (Enum.class.isAssignableFrom(f.getType())) {
						f.set(instance, Enum.valueOf((Class<Enum>)f.getType(), defaultValue));
					} else {
						constructor = f.getType().getConstructor(String.class);
						f.set(instance, constructor.newInstance(defaultValue));
					}
				}
			} catch (Exception e){}
		}
	}

	private static String determineClassName(Class<?> c){
		String persistName = c.getName();	//default
		if (c.isAnnotationPresent(PersistName.class))
			persistName = c.getAnnotation(PersistName.class).value();
		
		return persistName;
	}
	
	/**
	 * Usage example:
	 * 
	 * An object defined as follows:
	 * 
	 * 	public class ControllerInfo extends Configuration
	 * 	{
	 *		@PersistName("model") 
	 *		@MetaData(description = "Attraction model")
	 *		private String model = "com.disney.xband.xbrc.attractionmodel.CEP";
	 *		@PersistName("httpport") 
	 *		@MetaData(description = "HTTP port this controller is listening on", min = "0")
	 *		private int nHttpPort = 8080;
	 *
	 *		// setters and getters
	 *	}
	 * 
	 * Will be converted into a collection with the following elements:
	 * 
	 * ConfigInfo element_1 = new ConfigInfo();
	 * element_1.name = "model";
	 * element_1.value = "com.disney.xband.xbrc.attractionmodel.CEP";
	 * element_1.type = "java.lang.String";
	 * element_1.min = "N/A";
	 * element_1.max = "N/A";
	 * element_1.choices = "N/A"
	 * element_1.description = "Attraction model";
	 * 
	 * ConfigInfo element_2 = new ConfigInfo();
	 * element_2.name = "nHttpPort";
	 * element_2.value = "8080";
	 * element_2.type = "int";
	 * element_2.min = "0";
	 * element_2.max = "N/A";
	 * element_2.choices = "N/A"
	 * element_2.description = "HTTP port this controller is listening on";
	 * 
	 * <CODE>transient</CODE> member variables are ignored.
	 * 
	 * @param source a POJO of <CODE>this</CODE> supertype
	 * @return meta data rich xml representation of <CODE>source</CODE>
	 */
	public static <T extends Configuration> LinkedList<ConfigInfo> convert(T source){
		
		LinkedList<ConfigInfo> result = new LinkedList<ConfigInfo>();
		
		if (source == null)
			return result;
		
		String configClass = determineClassName(source.getClass());
		
		for (Field f: source.getClass().getDeclaredFields())
		{
			//don't process compiler generated fields
			if (f.isSynthetic())
				continue;
			//fields that should not get persisted
			if ( (f.getModifiers() & Modifier.TRANSIENT) != 0 )
				continue;
			
			try {
				// construct the ConfigInfo object and add it to the results
				ConfigInfo configInfo = construct(f, source, configClass);
				if (configInfo != null)
					result.add(configInfo);
			} catch (Exception e){}
		}
		
		return result;
	}
	
	public static <T extends Configuration> LinkedList<ConfigInfo> convert(T source, String propertyName)
			throws NoSuchFieldException
	{
		LinkedList<ConfigInfo> result = new LinkedList<ConfigInfo>();
		
		if (source == null)
			return result;
		
		String configClass = determineClassName(source.getClass());
		
		Field field = null;
		for (Field f: source.getClass().getDeclaredFields())
		{
			//don't process compiler generated fields
			if (f.isSynthetic())
				continue;
			/*
			 * Fields which should not get serialized/persisted should be declared as 'transient'!!!
			 */
			if ( (f.getModifiers() & Modifier.TRANSIENT) != 0)
				continue;
			
			if (f.getName().equals(propertyName))
			{
				field = f;
				break;
			}
		}
		
		if (field == null)
			throw new NoSuchFieldException();
		
		try {
			// construct the ConfigInfo object and add it to the results
			ConfigInfo configInfo = construct(field, source, configClass);
			if (configInfo != null)
				result.add(configInfo);
		} catch (Exception e){}
		
		return result;
	}
	
	private static HashMap<String,Field> getFieldsByMetaName(Object source)
	{
		HashMap<String,Field> map = new HashMap<String,Field>();
		
		for (Field f: source.getClass().getDeclaredFields())
		{
			//don't process compiler generated fields
			if (f.isSynthetic())
				continue;
			//fields that should not get persisted
			if ( (f.getModifiers() & Modifier.TRANSIENT) != 0 )
				continue;
			
			try {
				String name = f.getName();
				
				// construct the ConfigInfo object and add it to the results
				if (f.isAnnotationPresent(MetaData.class)) {					 					
					MetaData md = f.getAnnotation(MetaData.class);
					if (md.name() != null)
						name = md.name();
				}
				
				map.put(name, f);
				
			} catch (Exception e){}
		}
		
		return map;
	}
	
	/**
	 * Usage example:
	 * 
	 * A collection containing the following two elements:
	 * 
	 * ConfigInfo element_1 = new ConfigInfo();
	 * element_1.name = "model";
	 * element_1.value = "com.disney.xband.xbrc.attractionmodel.CEP";
	 * element_1.type = "java.lang.String";
	 * element_1.min = "";
	 * element_1.max = "";
	 * element_1.choices = ""
	 * element_1.description = "";
	 * 
	 * ConfigInfo element_2 = new ConfigInfo();
	 * element_2.name = "nHttpPort";
	 * element_2.value = "8080";
	 * element_2.type = "int";
	 * element_2.min = "";
	 * element_2.max = "";
	 * element_2.choices = ""
	 * element_2.description = "";
	 * 
	 * To the following object:
	 * 
	 * com.disney.xband.xbrc.attractionmodel.ConfigOptions$ControllerInfo config = 
	 * 	new com.disney.xband.xbrc.attractionmodel.ConfigOptions$ControllerInfo();
	 * config.model = "com.disney.xband.xbrc.attractionmodel.CEP";
	 * config.nHttpPort = 8080;
	 * 
	 * @return meta data rich xml representation of <CODE>source</CODE>
	 */
	public static <T extends Configuration> T convert(Collection<ConfigInfo> configInfo, T destination){
		if (configInfo == null || configInfo.size() == 0 || destination == null)
			return null;

		String fieldName = null;
		String fieldValue = null;
		String fieldConfigClass = null;
		@SuppressWarnings("rawtypes")
		Constructor constructor = null;
		HashMap<String,Field> fields = getFieldsByMetaName(destination);
		String className = determineClassName(destination.getClass());
		
		try {
			Field destField = null;
			for (ConfigInfo source : configInfo){
				
				fieldName = source.getName();
				fieldValue = source.getValue();
				fieldConfigClass = source.getConfigClass();
				
				if (fieldConfigClass == null)
					continue;
				
				if (!className.equals(fieldConfigClass))
					continue;
					
				try {
					destField = fields.get(fieldName);
					if (destField == null)
						continue;
				} catch (Exception e){
					continue;
				}
				
				destField.setAccessible(true);
				
				try {
					if (destField.getType().equals(byte.class)){
						destField.set(destination, fieldValue != null ? new Byte(fieldValue) : new Byte((byte)0));
					} else if (destField.getType().equals(char.class)){
						if (fieldValue != null){
							char[] chars = fieldValue.toCharArray();
							if (chars.length > 0)
								destField.set(destination, new Character(chars[0]));
						} else {
							destField.set(destination, new Character((char)0));
						}
					} else if (destField.getType().equals(int.class)){
						destField.set(destination, fieldValue != null ? new Integer(fieldValue) : new Integer(0));
					} else if (destField.getType().equals(long.class)){
						destField.set(destination, fieldValue != null ? new Long(fieldValue) : new Long(0L));
					} else if (destField.getType().equals(double.class)){
						destField.set(destination, fieldValue != null ? new Double(fieldValue) : new Double(0.0));
					} else if (destField.getType().equals(boolean.class)){
						destField.set(destination, fieldValue != null ? new Boolean(fieldValue) : Boolean.FALSE);
					} else if (Enum.class.isAssignableFrom(destField.getType())) {
						if (fieldValue != null){
							destField.set(destination, Enum.valueOf((Class<Enum>)destField.getType(), fieldValue));
						} else {
							destField.set(destination, Enum.class.getEnumConstants()[0]);
						}
					} else if (destField.getType().equals(java.util.List.class)) {
						// The list values should be in the following format: "value1|value2|value3"						
						destField.set(destination, Config.parseList(fieldValue));
					} else {
						if (fieldValue != null){
							constructor = destField.getType().getConstructor(String.class);
							destField.set(destination, constructor.newInstance(fieldValue));
						} else {
							destField.set(destination, null);
						}
					}
				} catch (Exception e){
					continue;
				}
			}
			
			return destination;
			
		} catch (Exception e){
			logger.error("Failed to update configuration information", e);
		}
		
		return null;
	}
	
	/**
	 * Ignores fields without the @link com.disney.xband.xbrc.lib.config.MetaData annotation
	 * present, since without it we wouldn't know how to process a ConfigInfo anyway.
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static ConfigInfo construct(Field f, Configuration conf, String configClass) throws Exception {
		
		f.setAccessible(true);
		
		if (f.isAnnotationPresent(MetaData.class)){
			
			ConfigInfo info = new ConfigInfo();
			info.setType(f.getType().getName());
			// special case lists as pipe separated values
			if (f.getType().equals(java.util.List.class)) {
				info.setValue(Config.listToString((List<String>)f.get(conf)));
			}
			else {
				info.setValue(f.get(conf).toString());
			}
			info.setClazz(f.getDeclaringClass().getName());
			info.setConfigClass(configClass);

			MetaData md = f.getAnnotation(MetaData.class);
			
			info.setName(md.name());
			info.setDescription(md.description());
			info.setMin(md.min());
			info.setMax(md.max());
			info.setChoices(md.choices());
			info.setUpdatable(String.valueOf(md.updatable()));
			
			return info;
		}
		
		return null;
	}
	
	private static List<String> parseList(String value) 
	{
		List<String> list = new LinkedList<String>();
		if (value == null || value.isEmpty())
			return list;
		String[] tokens = value.split("\\|");
		for (String token : tokens)
			list.add(token);
		return list;
	}
}
