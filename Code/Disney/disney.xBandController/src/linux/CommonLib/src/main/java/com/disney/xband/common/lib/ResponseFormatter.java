package com.disney.xband.common.lib;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.simpleframework.http.Response;

public class ResponseFormatter
{
	private static Logger logger = Logger.getLogger(ResponseFormatter.class);
	
	public static void setResponseHeader(Response response, String sContentType)
	{
		long time = System.currentTimeMillis();

		response.set("Content-Type", sContentType);
		response.set("Server", "xBRC/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
	}
	
	public static void returnCode(Response response, int code)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(code);
			PrintStream body = response.getPrintStream();
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format("Error sending " + code + " response", e));
		}
	}
	
	public static void return404(Response response)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(404);
			PrintStream body = response.getPrintStream();
			body.println("Error 404");
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 404 response", e));
		}
	}

	public static void return200(Response response)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(200);
			PrintStream body = response.getPrintStream();
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 200 response", e));
		}
	}

	public static void return403(Response response)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(403);
			PrintStream body = response.getPrintStream();
			body.println("Unauthorized");
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 403 response", e));
		}
	}

	public static void return500(Response response, String sError)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(500);
			PrintStream body = response.getPrintStream();
			body.println(sError);
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 500 response", e));
		}
	}

	public static void return301(Response response, String sURL)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(301);
			response.set("Location", sURL);
			PrintStream body = response.getPrintStream();
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 301 response", e));
		}

	}
	
	/**
	 * 
	 * @param response http status code 400 - incorrect syntax in client request
	 */
	public static void return400(Response response, String errorMessage)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(400);
			PrintStream body = response.getPrintStream();
			if (errorMessage == null || errorMessage.trim().isEmpty())
				body.println("Incorrect request syntax!");
			else
				body.println(errorMessage);
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 403 response", e));
		}
	}
	
	public static void return424(Response response, String message)
	{
		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(424);
			PrintStream body = response.getPrintStream();
			body.println(message);
			body.close();
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format(
					"Error sending 500 response", e));
		}
	}
}
