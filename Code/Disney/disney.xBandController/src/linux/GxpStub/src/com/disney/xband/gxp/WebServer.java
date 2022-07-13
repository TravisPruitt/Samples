package com.disney.xband.gxp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.disney.xband.common.lib.security.InputValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.SocketConnection;


public class WebServer implements Container
{
	private static final int cMaxUIEvents = 100;

	// singleton
	public static WebServer INSTANCE = new WebServer();

	private static Logger logger = Logger.getLogger(WebServer.class);

	// class data
	private org.simpleframework.transport.connect.Connection sockConn = null;
	private org.simpleframework.transport.connect.Connection sockConnSSL = null;
	
	// UI event feed
	private Queue<UIEvent> uie = new LinkedList<UIEvent>(); 
	
	/*
	 * Initialization and termination methods
	 */

	private WebServer()
	{
	}

	public void Initialize(int nHttpPort, int nHttpsPort) throws Exception
	{
		// start the Http connection
		if (nHttpPort>0)
		{
			sockConn = new SocketConnection(this);
			SocketAddress address = new InetSocketAddress(nHttpPort);
			sockConn.connect(address);
		}
		
		// start the Https connection
		if (nHttpsPort>0)
		{
			sockConnSSL = new SocketConnection(this);
			SocketAddress address = new InetSocketAddress(nHttpsPort);
			SSLContext sslc = createSSLContext();
			sockConnSSL.connect(address, sslc);
		}
		
	}

	public void Stop()
	{
		try
		{
			if (sockConn!=null)
				sockConn.close();
		}
		catch (IOException e)
		{
			logger.error("Error closing insecure web server: " + e);
		}
		
		try
		{
			if (sockConnSSL!=null)
				sockConnSSL.close();
		}
		catch (IOException e)
		{
			logger.error("Error closing secure web server: " + e);
		}
		
	}

	private SSLContext createSSLContext()
	{
        FileInputStream fis = null;

	    try
	    {
	        SSLContext context = SSLContext.getInstance("SSL");
	        
	        // The reference implementation only supports X.509 keys
	        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	        
	        // Sun's default kind of key store
	        KeyStore ks = KeyStore.getInstance("JKS");

	        // For security, every key store is encrypted with a 
	        // pass phrase that must be provided before we can load 
	        // it from disk. The pass phrase is stored as a char[] array
	        // so it can be wiped from memory quickly rather than
	        // waiting for a garbage collector. Of course using a string
	        // literal here completely defeats that purpose.
	        
	        char[] password = "gxpkeystore".toCharArray();
            fis = new FileInputStream("gxp.keys");
	        ks.load(fis, password);
	        kmf.init(ks, password);
	        
	        context.init(kmf.getKeyManagers(), null, null);
	        return context;
	    }
	    catch(Exception e)
	    {
	    	logger.error("Error initializing SSL context: " + e);
	    	return null;
	    }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}
	
	/*
	 * Web server callback method
	 */

	public void handle(Request request, Response response)
	{
		try
		{
			logger.trace("Processing http request: " + request.getPath());
			if (request.getMethod().compareTo("GET") == 0)
				handleGet(request, response);
			else if (request.getMethod().compareTo("PUT") == 0)
				handlePut(request, response);
			else if (request.getMethod().compareTo("DELETE") == 0)
				handleDelete(request, response);
			else if (request.getMethod().compareTo("POST")==0)
				handlePost(request, response);
		}
		catch (IOException e)
		{
			logger.error("Error handling HTTP request: " + e);
		}
	}
	
	/*
	 * Request parsers
	 */

	private void handleGet(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();
		System.out.println("Request for: " + sPath);

		// handle any "GET" paths here:
		if (sPath.compareTo("/uifeed")==0)
			handleGetUIFeed(request, response);
		else
			handleGetFile(request, response);
	}

	private void handlePut(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();
		
		// handle any "PUT" paths here:
		// if (sPath.compareTo("/WHATEVER")==0)
		//     handlePutWhatever(request, response);
		// else if (sPath.startsWith("/SOMEPATHPREFIX/"))
		//     handlePutSomethingElse(request, response);
		// else
		return404(response);
	}

	private void handlePost(Request request, Response response)
	{
		String sPath = request.getPath().getPath();
		
		// handle any "POST" paths here:
		if (sPath.compareTo("/tap")==0)
			handlePostTap(request, response);
		else if (sPath.compareTo("/cleartap")==0)
			handlePostClearTap(request, response);
		else
			return404(response);
	}

	private void handleDelete(Request request, Response response)
			throws IOException
	{
		String sPath = request.getPath().getPath();
		// handle any "DELETE" paths here:
		// if (sPath.compareTo("/WHATEVER")==0)
		//     handleDeleteWhatever(request, response);
		// else if (sPath.startsWith("/SOMEPATHPREFIX/"))
		//     handleDeleteSomethingElse(request, response);
		// else
		return404(response);
	}

	
	/*
	 * This is the main server function to handle /tap requests from xbrc's. It calls
	 * out to EntitlementChecker to decide how the request should be handled. If the result
	 * is an "invalid" response, it queues up a UIEvent that can be accessed via GET /uifeed.
	 * Note that this is not a real ATOM mechanism.
	 */
	private void handlePostTap(Request request, Response response)
	{
        InputStream is = null;
        PrintStream body = null;

		try
		{
			// read the input stream into a string
			is = request.getInputStream();
			body = response.getPrintStream();
			String sPutData = new Scanner(is).useDelimiter("\\A").next();
			
			// deserialize the request
			JSONObject jo = JSONObject.fromObject(sPutData);
			
			// verify 
			if (jo==null)
				throw new Exception("Invalid JSON object sent to POST /tap");
			
			JSONObject jor = jo.getJSONObject("tapRequest");
			if (jor==null)
				throw new Exception("Incorrect JSON object sent to POST /tap");
			
			TapRequest treq = (TapRequest) JSONObject.toBean(jor, TapRequest.class);
			if (treq==null)
				throw new Exception("Improper JSON object sent to POST /tap");
			
			logger.trace("Received tap request from: " + treq.getEntertainmentId() + "," + treq.getLocation());
			
			// validate the request
			TapResponse tres = EntitlementChecker.INSTANCE.checkEntitlement(treq);
			
			// if it's a failure, add it to the UI "feed"
			if (!tres.getGreen())
			{
				logger.trace("Returning invalid entitlement result");
				
				UIEvent ev = new UIEvent(treq, tres);
				synchronized(uie)
				{
					uie.add(ev);
					
					// only store a set number of these in the feed
					while(uie.size() > cMaxUIEvents)
						uie.remove();
				}
			}
			else
			{
				logger.trace("Returning valid entitlement result");
			}
			
			// serialize the response
			JSONObject jores = JSONObject.fromObject(tres);
			jo = new JSONObject();
			jo.put("tapResponse", jores);
			body.println(jo.toString());

			setResponseHeader(response, "application/json");
			return200(response);
		}
		catch(Exception ex)
		{
			String sError ="Error handling POST /tap request: " + ex.getLocalizedMessage(); 
			logger.error(sError);
			return500(response, sError);
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(Exception ignore) {
                }
            }

            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	/*
	 * This handler is an example of how a GXP DAP UI might clear a blue light condition. The
	 * payload of the POST /cleartap message is as follows:
	 * 
	 * {
	 *     "tapClear": {
	 *                    "resolution" : RESOLUTION,
	 *                    "treq": REQUEST
	 *                 }
	 * }
	 *
	 * Where RESOLUTION is "Override" or "NextGuest". REQUEST is the original request object.
	 * 
	 * The GxpStub proceeds by informing the EntitlementChecker and by sending a "/light" message to 
	 * the callback address in the enclosed REQUEST object.
	 */
	private void handlePostClearTap(Request request, Response response)
	{
        InputStream is = null;
        PrintStream body = null;

		try
		{
			// read the input stream into a string
			is = request.getInputStream();
			body = response.getPrintStream();
			String sPostData = new Scanner(is).useDelimiter("\\A").next();
			
			// deserialize the request
			JSONObject jo = JSONObject.fromObject(sPostData);
			
			// verify 
			if (jo==null)
				throw new Exception("Invalid JSON object sent to POST /cleartap");
			
			JSONObject jor = jo.getJSONObject("tapClear");
			if (jor==null)
				throw new Exception("Incorrect JSON object sent to POST /cleartap");
			
			TapClear tclr = (TapClear) JSONObject.toBean(jor, TapClear.class);
			if (tclr==null)
				throw new Exception("Improper JSON object sent to POST /cleartap");
			
			logger.trace("Received /tapclear for request from: " + tclr.getTreq().getLocation());
			logger.trace("  Cast member chose: " + tclr.getResolution());
			
			// handle the UI operation
			if (tclr.getResolution().compareToIgnoreCase("override")==0)
				EntitlementChecker.INSTANCE.createOverride(tclr.getTreq());
			else if (tclr.getResolution().compareToIgnoreCase("nextguest")==0)
				EntitlementChecker.INSTANCE.nextGuest(tclr.getTreq());
			
			// send a POST to the callback to tell it to turn it's light off
			sendClearLightMessage(tclr.getTreq().getCallback());
			
			// all done
			setResponseHeader(response, "text/plain");
			return200(response);
		}
		catch(Exception ex)
		{
			String sError ="Error handling PST /tap request: " + ex.getLocalizedMessage(); 
			logger.error(sError);
			return500(response, sError);
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }

            if(body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	/*
	 * Returns the list of queued UIEvents. This is a fake "feed" for UI handling. Note that the
	 * list looks like:
	 * 
	 * { "uifeed" : [ UIEVENT, UIEVENT ] }
	 * 
	 * Where each UIEVENT looks like this:
	 * 
	 * {  "id": 123,
	 *    "treq" : REQUEST,
	 *    "tres" : RESPONSE
	 * }
	 * 
	 * REQUEST and RESPONSE are the TapRequest and TapResponse objects (in JSON form) associated with the event. "id" is a monotonically
	 * increasing number that could be used by a UI device to request only new feed elements by including the last seen id as an
	 * "?after=####" parameter in the /uifeed request.
	 * 
	 */
	private void handleGetUIFeed(Request request, Response response)
	{
		// if security is enabled, require that this request come in through HTTPS
		if (!checkSecurity(request, response))
			return;
		
        PrintStream body = null;

		try
		{
			body = response.getPrintStream();
			StringBuilder sb = new StringBuilder();
			
			// parse out the "after" argument
			long lAfter = -1;
			String sParm = request.getParameter("after");
			if (sParm!=null)
				lAfter = Long.parseLong(sParm);
			
			// iterate through ui event list collecting the ones we need
			List<UIEvent> li = new ArrayList<UIEvent>();
			synchronized(uie)
			{
				for (UIEvent ev : uie)
				{
					if (lAfter>=0 && ev.getId()<=lAfter)
						continue;
					li.add(ev);
				}
			}
			
			JSONArray jores = JSONArray.fromObject(li);
			JSONObject jo = new JSONObject();
			jo.put("uifeed", jores);
			sb.append(jo.toString());
			body.println(sb.toString());
			
			setResponseHeader(response, "application/json");
			return200(response);
		}
		catch(Exception ex)
		{
			String sError = "Error handling GET /uifeed request: " + ex.getLocalizedMessage(); 
			logger.error(sError);
			return500(response, sError);
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private void handleGetFile(Request request, Response response)
	{
		// if security is enabled, require that this request come in through HTTPS
		if (!checkSecurity(request, response))
			return;
		
		String sPath = request.getPath().getPath();
		
		// strip the leading slash
		if (sPath.length()>0)
			sPath = sPath.substring(1);
		
		// is there a file with that name
		File fi = new File(InputValidator.validateFilePath(sPath));
		if (!fi.exists())
		{
			return404(response);
			return;
		}

        FileInputStream fis = null;
        OutputStream body = null;

		try
		{
			body = response.getOutputStream();
			fis = new FileInputStream(fi);
			byte[] buf =  new byte[4096];
			while(true)
			{
				int cb = fis.read(buf);
				if (cb<0)
					break;
				body.write(buf, 0, cb);
			}
			
			response.setCode(200);
		}
		catch(Exception e)
		{
			logger.error("Error sending file to http client: " + e.getLocalizedMessage());
			return500(response,e.getLocalizedMessage());
		}
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }

            if(body != null) {
                try {
                    body.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	
	/*
	 * Helper functions
	 */
	
	private void sendClearLightMessage(String callback)
	{
		// create LightMessage
		LightMessage lm = new LightMessage();
		lm.setStatus("reset");
		lm.setTimeout(0l);
		
		// create payload
		JSONObject jo = new JSONObject();
		JSONObject jolm = JSONObject.fromObject(lm);
		jo.put("stateChange", jolm);
		
		// serialize
		String sPayload = jo.toString();
		
		// post
		sendPost(callback, sPayload);
	}
	
	private static HttpURLConnection sendPost(String sURL, String sData)
	{
        OutputStream os = null;
        OutputStreamWriter wr = null;

		try
		{
			URL url = new URL(sURL);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("POST");
            os = httpCon.getOutputStream();
			wr = new OutputStreamWriter(os);
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
            if(os != null) {
                try {
                    os.close();
                }
                catch (Exception ignore) {
                }
            }

            if(wr != null) {
                try {
                    wr.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private boolean checkSecurity(Request request, Response response)
	{
		// security check
		if (this.sockConnSSL!=null && !request.isSecure())
		{
			return403(response);
			return false;
		}
		else
			return true;
	}

	/*
	 * Response formatters
	 */

	private void setResponseHeader(Response response, String sContentType)
	{
		long time = System.currentTimeMillis();

		response.set("Content-Type", sContentType);
		response.set("Server", "Gxp/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);

	}

	private void return404(Response response)
	{
        PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(404);
			body = response.getPrintStream();
			body.println("Error 404");
			body.close();
		}
		catch (IOException e)
		{
			logger.error("Error sending 404 response: " + e);
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private void return200(Response response)
	{
        PrintStream body = null;

		try
		{
			response.setCode(200);
			body = response.getPrintStream();
			body.close();
		}
		catch (IOException e)
		{
			logger.error("Error sending 200 response: " + e);
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private void return403(Response response)
	{
        PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(403);
			body = response.getPrintStream();
			body.println("Unauthorized");
			body.close();
		}
		catch (IOException e)
		{
			logger.error("Error sending 200 response: " + e);
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private void return500(Response response, String sError)
	{
        PrintStream body = null;

		try
		{
			setResponseHeader(response, "text/plain");
			response.setCode(500);
			body = response.getPrintStream();
			body.println(sError);
			body.close();
		}
		catch (IOException e)
		{
			logger.error("Error sending 200 response: " + e);
		}
        finally {
            if(body != null) {
                try {
                    body.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
}
