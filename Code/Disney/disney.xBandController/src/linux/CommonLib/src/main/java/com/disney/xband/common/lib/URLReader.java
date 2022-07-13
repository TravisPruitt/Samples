package com.disney.xband.common.lib;

import com.disney.xband.common.lib.security.InputValidator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class URLReader
{
	/*
	 * Returns an InputStream object for URL formatted paths in the following formats:
	 * 1) classpath:<recource name separated with "/" and no leading "/">
	 * 2) file://<full path to the file name including a leading "/">
	 * 3) <file name path>
	 * 4) http://<recource name>
	 */
	public static InputStream getInputStream(String path) throws IOException
	{
		if (path == null || path.isEmpty())
			throw new IllegalArgumentException("The resource name is not set.");
		
		String lpath = path.toLowerCase();
		
		// classpath resource?
		if (lpath.startsWith("classpath:"))
		{
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			String resourcepath = path.substring(10);
			InputStream in = loader.getResourceAsStream(resourcepath);
			
			if (in == null)
				throw new IOException("Resource does not exist: " + path);
			
	        return in;
		}
		
		// see if it looks like a URL
		if (lpath.contains("://"))
		{
			URL url = new URL(path);			
			InputStream is = url.openStream();
			return is;
		}
		
		// assume it is a file
		FileInputStream fis = new FileInputStream(InputValidator.validateFilePath(path));
		return fis;
	}
}
