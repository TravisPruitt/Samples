package com.disney.xband.gxp.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.disney.xband.gxp.Gxp;
import com.disney.xband.gxp.TapClear;
import com.disney.xband.gxp.TapRequest;
import com.disney.xband.gxp.TapResponse;
import com.disney.xband.gxp.UIEvent;
import com.disney.xband.gxp.WebServer;

public class Test
{
	private static Logger logger = Logger.getLogger(Test.class);
	private static String sURLSelf = "http://localhost:" + Integer.toString(Gxp.nHttpPort); 

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			// start the web server
			WebServer.INSTANCE.Initialize(Gxp.nHttpPort, 0);
			
			// run the tests
			runTests();
			
			// stop the web server
			WebServer.INSTANCE.Stop();
			
		}
		catch(Exception ex)
		{
			logger.error("Failed to start web server: " + ex.getLocalizedMessage());
		}
		System.out.println("Done.");

	}

	private static void runTests()
	{
		runSuccessfulRequestTest();
		runUnsuccessfulRequestTest();
		System.out.println("Hit Enter...");
		try
		{
			System.in.read();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runUIFeedTest();
	}
	
	private static void runSuccessfulRequestTest()
	{
		
		if (runRequestTest(true))
			System.out.println("runSuccessfulRequestTest succeeded");
		else
			System.out.println("runSuccessfulRequestTest failed");
	}

	private static void runUnsuccessfulRequestTest()
	{
		if (runRequestTest(false))
			System.out.println("runUnsuccessfulRequestTest succeeded");
		else
			System.out.println("runUnsuccessfulRequestTest failed");
	}
	
	private static boolean runRequestTest(boolean bSucceed)
	{
		try
		{
			TapRequest treq = new TapRequest();
			treq.setBandId("0123456789abcdef");
			treq.setCallback("http://localhost:8080/light/entry1");
			treq.setEntertainmentId(20194);
			if (bSucceed)
				treq.setLocation(1);
			else
				treq.setLocation(0);
			
			treq.setUnitType("Entry");
			treq.setSide("Right");
			treq.setTimestamp(new Date().getTime());
			JSONObject joreq = JSONObject.fromObject(treq);
			JSONObject jo = new JSONObject();
			jo.put("tapRequest", joreq);
			
			HttpURLConnection conn = sendPost("/tap", jo.toString());
			
			if (conn==null)
			{
				System.out.println("Failed to send tap request");
				return false;
			}
			
			if (conn.getResponseCode()!=200)
			{
				System.out.println("Error with tap request: " + Integer.toString(conn.getResponseCode()));
				return false;
			}
		
			// deserialize the response
			InputStream is = conn.getInputStream();
			String sData = new Scanner(is).useDelimiter("\\A").next();
			
			// deserialize the request
			jo = JSONObject.fromObject(sData);
			
			// verify 
			if (jo==null)
			{
				System.out.println("Invalid JSON object received from POST /tap");
				return false;
			}
			
			JSONObject jor = jo.getJSONObject("tapResponse");
			if (jor==null)
			{
				System.out.println("Incorrect JSON object received from POST /tap: " + sData);
				return false;
			}
			
			TapResponse tres = (TapResponse) JSONObject.toBean(jor, TapResponse.class);
			if (tres==null)
			{
				System.out.println("Improper JSON object received from POST /tap: " + sData);
				return false;
			}
			
			if ( (bSucceed && !tres.getGreen()) ||
			     (!bSucceed && tres.getGreen()) )
			{
				System.out.println("Received unexpected entitlement response from POST /tap");
				return false;
			}
			
			return true;
		}
		catch (Exception e)
		{
			System.out.println("Error sending tap request: " + e.getLocalizedMessage());
			return false;
		}
	}

	private static void runUIFeedTest()
	{
        InputStream is = null;

		try
		{
			// get the UI feed
			HttpURLConnection conn = sendGet("/uifeed");
			
			if (conn==null)
			{
				System.out.println("Failed to send get uifeed");
				return;
			}
			
			if (conn.getResponseCode()!=200)
			{
				System.out.println("Error with uifeed request: " + Integer.toString(conn.getResponseCode()));
				return;
			}
			
			// deserialize the response
			is = conn.getInputStream();
			String sData = new Scanner(is).useDelimiter("\\A").next();
			
			JSONObject jo = JSONObject.fromObject(sData);
			JSONArray ja = jo.getJSONArray("uifeed");
			
			if (ja.size()!=1)
			{
				System.out.println("Unexpected count if UIEvents in uifeed");
				return;
			}
			
			JSONObject joui = ja.getJSONObject(0);
			UIEvent ui = (UIEvent) joui.toBean(joui, UIEvent.class);
			
			// send a message to the web server telling it to "override"
			TapClear clr = new TapClear();
			clr.setResolution("override");
			clr.setTreq(ui.getTreq());
			JSONObject joclr = JSONObject.fromObject(clr);
			JSONObject joclrEnv = new JSONObject();
			joclrEnv.put("tapClear", joclr);
			conn = sendPost("/cleartap", joclrEnv.toString());
			if (conn.getResponseCode()!=200)
			{
				System.out.println("Error sending /cleartap: " + conn.getResponseCode());
				return;
			}
			
			System.out.println("runUIFeedTest succeeded");
			
		}
		catch(Exception e)
		{
			System.out.println("Error getting uifeed: " + e.getLocalizedMessage());
		}
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private static HttpURLConnection sendGet(String sPath)
	{

		try
		{
			URL url = new URL(sURLSelf + sPath);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setRequestMethod("GET");
			return httpCon;

		}
		catch (Exception ex)
		{
			logger.error("Error when POSTing: " + ex.getLocalizedMessage());
			
			return null;
		}
	}
	
	private static HttpURLConnection sendPost(String sPath, String sData)
	{
        OutputStreamWriter wr = null;

		try
		{
			URL url = new URL(sURLSelf + sPath);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("POST");
			wr = new OutputStreamWriter(httpCon.getOutputStream());
			wr.write(sData);
			wr.flush();

			return httpCon;

		}
		catch (Exception ex)
		{
			logger.error("Error when POSTing: " + ex.getLocalizedMessage());
			
			return null;
		}
        finally {
            if(wr != null) {
                try {
                    wr.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
}
