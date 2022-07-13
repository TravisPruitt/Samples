package com.disney.xband.xfpe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.XmlUtil;

public class RestfulRequest extends HttpServletRequestWrapper {

	private String requestBody;
	private Logger logger = Logger.getLogger(RestfulRequest.class);
	private ServletInputStream is;
	
	class MyInputStream extends ServletInputStream {
		private String buffer;
		private InputStream is;
		
		public MyInputStream(String buffer) {
			this.buffer = buffer;
			is = new ByteArrayInputStream(this.buffer.getBytes());
		}
		
		@Override
		public int read() throws IOException {
			return is.read(); 
		}
		
		@Override
		public int readLine(byte[] b, int off, int len) throws IOException {
			if (off > buffer.length())
				throw new IOException();
			
			int j = 0;
			byte[] bytes = buffer.getBytes();
			for (int i = off; i < buffer.length() && j < len && bytes[i] != '\n'; i++) {
				b[j++] = bytes[i];
			}
				
			return j;
		}
	}
	
	public RestfulRequest(HttpServletRequest request) {
		super(request);
		
		try {
			InputStream in = request.getInputStream();
			StringBuffer xmlStr = new StringBuffer();
		    int d;
		    while((d=in.read()) != -1){
		    	xmlStr.append((char)d);
		    }
			requestBody = xmlStr.toString();
			if (requestBody == null)
				requestBody = "";
			is = new MyInputStream(requestBody);
		} catch (IOException e) {
			logger.error("Failed to read the request body", e);
		}
	}

	public String getRequestBody() {
		return requestBody;
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		return is;
	}
}
