package com.disney.xband.xbrc.lib.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class FileUtils {
	public static String formatFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	public static String hideLeadingChars(String src, int leave)
	{
		try
		{
			if (src == null)
				return "";
			
			// leave 4 digits, strip the rest
			int stripSize = src.length() - leave;
			if (stripSize < 0)
				stripSize = 0;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < stripSize; i++)
				sb.append('*');
			sb.append(src.substring(stripSize));
			return sb.toString();
		} 
		catch (Throwable e) 
		{
			return "";
		}
	}
	
	public static byte[] fileToArray(File file) throws IOException {		
		int length = (int) file.length();  
	    byte[] data = new byte[length];
	    InputStream in = null;
	    try {		    
		    in = new FileInputStream(file);  
		    int offset = 0;  
		    while (offset < length) {  
		        offset += in.read(data, offset, (length - offset));  
		    }
	    }
	    finally {
	    	if (in != null) {
	    		in.close();
	    	}
	    }
	    
	    return data;
	}
	
	public static byte[] resourceToArray(String resource) throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = null;
		ByteArrayOutputStream os = null;

        try {
           is = loader.getResourceAsStream(resource);
           if (is == null)
        	   throw new Exception("Failed to load resource: " + resource);

           byte[] buff = new byte[1024];
           os = new ByteArrayOutputStream();
           int size = is.read(buff);
           while(size > 0) {
        	   os.write(buff, 0, size);
        	   size = is.read(buff);
           }
           
           return os.toByteArray();
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
            
            if(os != null) {
                try {
                    os.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
}
