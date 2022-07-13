package com.disney.xband.xfpe;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class Utils {
	
	private static Logger logger = Logger.getLogger(Utils.class);
			
	public static Long parseLong(String str) {
		try {
			if (str == null)
				return null;
			Long n = Long.parseLong(str);
			return n;
		} catch(Exception e){}	
		
		return null;
	}
	
	/*
	 * Does a http PUT request and returns the return code from the server.
	 */
	public static int doPutHttpRequest(String url, String data) throws ClientProtocolException, IOException {
		
		HttpPut request = new HttpPut(url);
		request.setEntity(new StringEntity(data));
	    HttpResponse response = new DefaultHttpClient().execute(request);
	    InputStream is = null;

        try {
            is = response.getEntity().getContent();
    	    while( is.read() >= 0 );	// don't care about return data
	        int rCode = response.getStatusLine().getStatusCode();
	        return rCode;
        }
        finally
        {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
	}
}
