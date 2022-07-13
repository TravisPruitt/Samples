package com.disney.xband.xbrc.Controller;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.ResponseFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.xbrc.Controller.model.ESBInfo;
import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.lib.model.ControllerInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xbrc.lib.utils.XbrcDateFormatter;

public class PropertiesManager
{
	private Logger logger = Logger.getLogger(PropertiesManager.class);
	
	private String className;
	private String propertyName;
	
	private Object monitor = new Object();
	
	public void handleGetProperties(Request request, Response response)
	{
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			
			synchronized(monitor)
			{
				switch (parseRequestUrl(request.getPath().getPath()))
				{
					case BY_CLASS: getClassOfProperties(conn, request, response, className); break;
					case SINGLE: getSingleProperty(conn, request, response, className, propertyName); break;
					default: getAllProperties(conn, request, response);
				}
			}
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling GET properties", e));
			ResponseFormatter.return500(response, ExceptionFormatter.format(
					"Error handling GET properties", e));
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}
	
	public void saveProperties(XbrcConfig config) throws Exception
	{
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			
			/*
			 * common configuration info
			 */
			ControllerInfo ci = ConfigOptions.INSTANCE.getControllerInfo();
			if (config != null)
				Configuration.convert(config.getConfiguration(), ci);
			Config.getInstance().write(conn, ci);
	
			// delegate updating of the model specific configuration to the
			// model
			if (Processor.INSTANCE.getModel() != null && config != null)
				Processor.INSTANCE.getModel().handlePropertiesWrite(config, conn);
			
			// JMS broker specific configuration
			ESBInfo isbInfoConfig = ConfigOptions.INSTANCE.getESBInfo();
			if (config != null)
				Configuration.convert(config.getConfiguration(), isbInfoConfig);
			Config.getInstance().write(conn, isbInfoConfig);
			
			// Reader configuration specific
			ReaderConfig readerConfig = ReaderConfig.getInstance();
			if (config != null)
				Configuration.convert(config.getConfiguration(), readerConfig);
			Config.getInstance().write(conn, readerConfig);
			
			// Audit specific properties
			AuditConfig auditConfig = new AuditConfig();
			if (config != null)
				Configuration.convert(config.getConfiguration(), auditConfig);
			Config.getInstance().write(conn, auditConfig);
	
			// note that re-read is deferred and performed in the main thread
			Processor.INSTANCE.reReadConfiguration(null);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error saving properties", e));
			throw e;
		}
		finally
		{
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}
	
	public void handlePutProperties(Request request, Response response)
	{
		InputStream is = null;
		try
		{
			is = request.getInputStream();
			
			// deserialize the payload
			XbrcConfig config = XmlUtil.convertToPojo(is, XbrcConfig.class);

			saveProperties(config);
			
			ResponseFormatter.return200(response);
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error handling PUT /model/configurationwrite", e));
			ResponseFormatter.return500(response, e.getLocalizedMessage());
		}
		finally
		{
			if (is != null)
				try
				{
					is.close();
				}
				catch (Exception e)
				{
				}
		}

	}
	
	protected Properties parseRequestUrl(String url)
	{
			String fullPath = url;
			className = null;
			propertyName = null;
			
			if (!fullPath.startsWith("/properties"))
				return null;
			
			if (fullPath.startsWith("/properties/"))
			{
				// requesting properties of a particular object
				String cName = fullPath.substring("/properties/".length());
				
				if (cName != null && !cName.trim().isEmpty())
				{
					if (cName.indexOf("/") >= 0)
					{
						// down to a property name
						className = cName.substring(0, cName.indexOf("/"));
						String propName = cName.substring(cName.indexOf("/") + 1);
						
						int trailingSlashIndex = propName.indexOf("/");
						if (trailingSlashIndex > 0)
						{
							propertyName = propName.substring(0, trailingSlashIndex);
						}
						else
						{
							propertyName = propName;
						}
					}
					else
					{
						className = cName;
						return Properties.BY_CLASS; 
					}
					
					if (propertyName != null && !propertyName.trim().isEmpty())
					{
						return Properties.SINGLE;
					}
					else
					{
						return Properties.BY_CLASS;
					}
					
				}
				else
				{
					return Properties.ALL;
				}
			}
			else
			{
				return Properties.ALL;
			}
	}
	
	private void getAllProperties(Connection conn, Request request, Response response) throws Exception
	{
		String xml = null;

		XbrcConfig config = new XbrcConfig();

		// configuration common to all models
		initControllerInfoConfig(config, conn);

		// delegate model specific part of this request to the model
		if (Processor.INSTANCE.getModel() != null)
			Processor.INSTANCE.getModel().handlePropertiesRead(config, conn);

		// serialize
		xml = XmlUtil.convertToXml(config, XbrcConfig.class);

		PrintStream body = response.getPrintStream();
		body.println(xml);
		body.close();
		ResponseFormatter.setResponseHeader(response, "application/xml");
		ResponseFormatter.return200(response);
	}
	
	private void getClassOfProperties(Connection conn, Request request, Response response, String className)
			throws Exception
	{
		String xml = null;

		// create an envelope
		XbrcConfig config = new XbrcConfig();

		Configuration configuration = null;
		try 
		{
			// create instance of class className
			@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(className);
			Class<Configuration> configurationClazz = Configuration.class;

			if (configurationClazz.isAssignableFrom(clazz)) 
			{
				ControllerInfo info = ConfigOptions.INSTANCE.getControllerInfo();
				info.initialize(conn);
				
				initEnvelope(conn, config, info);
				
				configuration = (Configuration)clazz.newInstance();
				
				// read its values from the database
				Config.getInstance().read(conn, configuration);
				
				// add its state to the envelope
				config.setConfiguration(Configuration.convert(configuration));
				
				// serialize the envelope
				xml = XmlUtil.convertToXml(config, XbrcConfig.class);

				PrintStream body = response.getPrintStream();
				body.println(xml);
				body.close();
				ResponseFormatter.setResponseHeader(response, "application/xml");
				ResponseFormatter.return200(response);
			} 
			else
			{
				StringBuffer errorMessage = new StringBuffer();
				errorMessage.append("Not able to retrieve properties of class ")
							.append(className)
							.append("Not able to retrieve properties of class ")
							.append(Configuration.class.getName());
				logger.warn(errorMessage.toString());
				
				ResponseFormatter.return400(response, errorMessage.toString());
			}
		} 
		catch (ClassNotFoundException e)
		{
			StringBuffer errorMessage = new StringBuffer();
			errorMessage.append("Instance of type ").append(className).append(" can not be created. Type doesn't exist.");
			logger.warn(errorMessage);
			
			ResponseFormatter.return400(response, errorMessage.toString());
		}
		catch (Exception e)
		{
			StringBuffer errorMessage = new StringBuffer();
			errorMessage.append("Failed to serialize object of type " + className);
			if (logger.isDebugEnabled())
				logger.warn(errorMessage, e);
			else
				logger.warn(errorMessage);
			
			ResponseFormatter.return500(response, errorMessage.toString());
		}
	}
	
	private void getSingleProperty(Connection conn, Request request, Response response, String className, String propertyName)
			throws Exception
	{
		String xml = null;

		// create an envelope
		XbrcConfig config = new XbrcConfig();

		Configuration configuration = null;
		try 
		{
			// create instance of class className
			@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(className);
			Class<Configuration> configurationClazz = Configuration.class;

			if (configurationClazz.isAssignableFrom(clazz)) 
			{
				ControllerInfo info = ConfigOptions.INSTANCE.getControllerInfo();
				info.initialize(conn);
				
				initEnvelope(conn, config, info);
				
				configuration = (Configuration)clazz.newInstance();
				
				// read its values from the database
				Config.getInstance().read(conn, configuration, propertyName);
				
				// add its state to the envelope
				config.setConfiguration(Configuration.convert(configuration, propertyName));
				
				// serialize the envelope
				xml = XmlUtil.convertToXml(config, XbrcConfig.class);

				PrintStream body = response.getPrintStream();
				body.println(xml);
				body.close();
				ResponseFormatter.setResponseHeader(response, "application/xml");
				ResponseFormatter.return200(response);
			} 
			else
			{
				StringBuffer errorMessage = new StringBuffer();
				errorMessage.append("Not able to retrieve properties of class ")
							.append(className)
							.append("Not able to retrieve properties of class ")
							.append(Configuration.class.getName());
				logger.warn(errorMessage.toString());
				
				ResponseFormatter.return400(response, errorMessage.toString());
			}
		} 
		catch (ClassNotFoundException e)
		{
			StringBuffer errorMessage = new StringBuffer();
			errorMessage.append("Instance of type ").append(className).append(" can not be created. Type doesn't exist.");
			logger.warn(errorMessage);
			
			ResponseFormatter.return400(response, errorMessage.toString());
		}
		catch (NoSuchFieldException e)
		{
			StringBuffer errorMessage = new StringBuffer();
			errorMessage.append("Type ").append(className)
						.append(" does not define variable name: ").append(propertyName);
			logger.warn(errorMessage);
			
			ResponseFormatter.return400(response, errorMessage.toString());
		}
		catch (Exception e)
		{
			StringBuffer errorMessage = new StringBuffer();
			errorMessage.append("Failed to initialize object of type " + className);
			if (logger.isDebugEnabled())
				logger.warn(errorMessage, e);
			else
				logger.warn(errorMessage);
			
			ResponseFormatter.return500(response, errorMessage.toString());
		}
	}
	
	private void initControllerInfoConfig(XbrcConfig config, Connection conn)
			throws Exception
	{
		ControllerInfo info = ConfigOptions.INSTANCE.getControllerInfo();
		info.initialize(conn);
		
		initEnvelope(conn, config, info);
		
		initControllerConfig(conn, config, info);
		
		initJmsConfig(conn, config);
		
		initReaderConfig(conn, config);
		
		initAuditConfig(conn, config);
		
	}
	
	private void initEnvelope(Connection conn, XbrcConfig config, ControllerInfo info) throws Exception
	{
		config.setId(info.getVenue());
		config.setName(info.getName());
		config.setTime(XbrcDateFormatter.formatTime(new Date().getTime()));
		
		// determine port
		config.setPort("" + ConfigOptions.INSTANCE.getControllerInfo().getPort());
		
		// determine ip
		String ownIp = "";
		try
		{
			Collection<String> iplist = NetInterface
					.getOwnIpAddress(ConfigOptions.INSTANCE.getControllerInfo()
							.getDiscoveryNetPrefix());
			if (iplist.size() > 0)
				ownIp = iplist.iterator().next();
		}
		catch (SocketException e)
		{
			logger.error(
					"Failed to get our own IP address in response to a request for config parameters.",
					e);
		}
		config.setIp(ownIp);
		
		config.setHaStatus(XBRCController.getInstance().getStatus().getHaStatus());
	}
	
	private void initControllerConfig(Connection conn, XbrcConfig config, ControllerInfo info) throws Exception
	{
		config.setConfiguration(Configuration.convert(info));
	}
	
	private void initJmsConfig(Connection conn, XbrcConfig config) throws Exception
	{
		ESBInfo esbInfoConfig = new ESBInfo();
		esbInfoConfig.initialize(conn);

		if (config.getConfiguration() != null)
			config.getConfiguration().addAll(
					Configuration.convert(esbInfoConfig));
		else
			config.setConfiguration(Configuration.convert(esbInfoConfig));
	}
	
	private void initReaderConfig(Connection conn, XbrcConfig config) throws Exception
	{
		ReaderConfig readerConfig = ReaderConfig.getInstance();
		readerConfig.initialize(conn);
		
		if (config.getConfiguration() != null)
			config.getConfiguration().addAll(
					Configuration.convert(readerConfig));
		else
			config.setConfiguration(Configuration.convert(readerConfig));
	}
	
	private void initAuditConfig(Connection conn, XbrcConfig config) throws Exception
	{
		AuditConfig auditConfig = new AuditConfig();
		auditConfig.initialize(conn);
		
		if (config.getConfiguration() != null)
			config.getConfiguration().addAll(Configuration.convert(auditConfig));
		else
			config.setConfiguration(Configuration.convert(auditConfig));
	}
	
	protected enum Properties {
		ALL,
		BY_CLASS,
		SINGLE
	}
}
