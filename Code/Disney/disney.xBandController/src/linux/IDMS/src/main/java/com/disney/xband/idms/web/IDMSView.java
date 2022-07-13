package com.disney.xband.idms.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.disney.xband.idms.performance.Metrics;

import java.util.*;
import javax.management.*;


@Path("/")
public class IDMSView 
{

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {


		try
		{
			StatusInfo.INSTANCE.Check();
			
			return "<html> " + "<title>" + "Hello IDMS" + "</title>"
			+ "<body><h1>" + "Hello IDMS" + "</h1>" +
			"<ul>" +
			"<li> Host: " + StatusInfo.INSTANCE.getHostname() + 
			"<li> Version: " + StatusInfo.INSTANCE.getVersion() + 
			"<li> Database Version: " + StatusInfo.INSTANCE.getDatabaseVersion() + 
			"<li> Status: " + StatusInfo.INSTANCE.getStatus() + 
			"<li> Status Message: " + StatusInfo.INSTANCE.getStatusMessage() +
			"<li> Transaction Count: " + Metrics.INSTANCE.getTotalTransactions() +
			"<li> Average Time per Transaction in MS: " + Metrics.INSTANCE.getAverageTransactionTime() +
			"</ul></body>" +
			"" + "</html> ";
		}
		catch (Exception ex)
		{
			return "<html><title>" + "Hello IDMS" + "</title>" +"<body><h1>" + "Hello IDMS" + "</h1></br>Refresh this page for status.</body>" + "</html>";
		}
		

	}
	
	@GET
	@Path("/resetIDMS")
	@Produces(MediaType.TEXT_HTML)
	public String restartServier()
	{
		try
		{
			ObjectName objectNameQuery = new ObjectName("*:type=Connector,port=8090,*");
			MBeanServer mbeanServer = null;
			
			ObjectName objectName = null;
			
			for (final MBeanServer server : (List<MBeanServer>)MBeanServerFactory.findMBeanServer(null))
			{
				if (server.queryNames(objectNameQuery, null).size() > 0)
				{
					mbeanServer = server;
					objectName = (ObjectName) server.queryNames(objectNameQuery, null).toArray()[0];
					break;
				}
			}
		
			if (mbeanServer != null)
			{
				mbeanServer.invoke(objectName, "stop", null, null);
				
				Thread.sleep(20000);
				
				mbeanServer.invoke(objectName, "start", null, null);
			}
		}
		catch (Exception ex)
		{
			return "<html><title>IDMS Error</title><body>An error occurred resetting IDMS: </br>" + ex.getMessage() + "</br></body></html>";
		}
		
		
		String retVal = "<html><title>IDMS Error</title><body>IDMS Tomcat Server has been reset!</br></body></html>";
		
		return retVal;
	}

}
