package com.disney.xband.xfpe;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.PersistName;
import com.disney.xband.xfpe.db.XfpeConnectionPool;

@PersistName("ServiceLocator")
public class ServiceLocator {
	
	private static String SERVICE_IMPLEMENTOR_SUFFIX = "Imp";
	
	private Logger logger = Logger.getLogger(ServiceLocator.class);
	
	public static ServiceLocator getInstance() {
		return ServiceLocatorHolder.instance;
	}
	
	public <T> T createService(Class<T> serviceInterface) {
		//figure out the name of the concrete implementation for serviceInterface
		String serviceName = serviceInterface.getName();
		serviceName += SERVICE_IMPLEMENTOR_SUFFIX;

		try {
			Class clazz = Class.forName(serviceName);
			Object newInstance = clazz.newInstance();
			return (T)newInstance;
		} catch (ClassNotFoundException e) {
			logger.error("Service implementation not found: " + serviceName, e);
		} catch (InstantiationException e) {
			logger.error("Could not instantiate service implementation: " + serviceName, e);
		} catch (IllegalAccessException e) {
			logger.error("Service implementation not accessable: " + serviceName, e);
		}
			
		return null;
	}
	
	
	private static class ServiceLocatorHolder {
		private static final ServiceLocator instance = new ServiceLocator();
	}
	
	private ServiceLocator(){

		Config config = Config.getInstance();

    	//read property/value pairs from the Config table
		Connection conn = null;
    	try {
    		conn = XfpeConnectionPool.getInstance().getConnection();
			config.read(conn, this);
		} catch (Exception e) {
			if (logger.isInfoEnabled())
				logger.info("Not able to read configuration from the database. Using defaults defined in the class.", e);
		} finally {
			XfpeConnectionPool.getInstance().releaseConnection(conn);
		}
	}
}
