package com.disney.xband.xview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/")
public class Utils {
	
	public static final String userName = "ursula";
	
				// This method is called if HTML is request
				@GET
				@Produces(MediaType.TEXT_HTML)
				public String sayHtmlHello() {
					
		
					
					
					return "<html> " + "<title>" + "Hello Xview" + "</title>"
							+ "<body><h1>" + "Hello Xview" + "</body></h1>" + "</html> ";
					
				}
				

				public static String getUserName()
				{
					return userName;				
				}
				
				public static String getDateTimeNow()
				{
					Date now = new Date();
					
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					return df.format(now);
				}
				

}
