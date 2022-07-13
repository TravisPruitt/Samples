package com.disney.xband.xbrms.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class XbrmsUtils
{
	private static Properties manifest;
	private static Logger logger;
    private static IRestCall restCaller;
    private static IRestCall localCaller;
    private static Pattern ipPattern;

    static {
        XbrmsUtils.logger = Logger.getLogger(XbrmsUtils.class);
        ipPattern = Pattern.compile(PkConstants.IP_PATTERN);

        try {
            XbrmsUtils.restCaller = RestCallFactory.createRestCall(false);
        }
        catch (Exception e) {
            XbrmsUtils.logger.info("Failed to initialize RESTful caller.");
        }

        try {
            XbrmsUtils.localCaller = RestCallFactory.createRestCall(true);
        }
        catch (Exception e) {
            XbrmsUtils.logger.info("Local caller not initialized.");
        }

        if((XbrmsUtils.localCaller == null) && (XbrmsUtils.restCaller == null)) {
            XbrmsUtils.logger.info("No caller available. Exiting ...");
            System.exit(-1);
        }
    }
	
	public static String getXbrmsVersion(final Logger logger, final ServletContext context){
		loadManifest(logger, context);
		
		String version =  null;
		if (manifest != null)
			version = manifest.getProperty("Implementation-Title");		
		if (version == null || version.isEmpty())
			version = "Development";
		
		return version;
	}
	
	private synchronized static void loadManifest(final Logger logger, final ServletContext context) {

		if (manifest == null) {
            InputStream is = null;

			try {
                is = context.getResourceAsStream("/META-INF/MANIFEST.MF");
				manifest = new Properties();
				manifest.load(is);
			}
			catch (IOException e) {
				logger.error("Failed to read th MANIFEST.MF file. Version will not be available;");			
			}
            finally {
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch (Exception e) {
                    	logger.warn("Input stream failed to close. Possible memory leak.", e);
                    }
                }
            }
		}
	}
	
	public static String readFileAsString(final String filePath) throws java.io.IOException{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
        FileInputStream fis = null;

	    try {
            fis = new FileInputStream(filePath);
	        f = new BufferedInputStream(fis);
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException e) { logger.warn("Input stream failed to close. Possible memory leak.", e); }
            if (fis != null) try { fis.close(); } catch (IOException e) { logger.warn("File input stream failed to close. Possible memory leak.", e); }
	    }
	    return new String(buffer);
	}
	
	public static String inputStreamToString(final InputStream is) throws IOException
	{
		BufferedReader br = null;
        InputStreamReader isr = null;

		try
		{
            isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			char[] buffer = new char[1024];
			int read = br.read(buffer);
			while(read > 0)
			{
				sb.append(buffer,0,read);
				read = br.read(buffer);
			}
			
			return sb.toString();
		}
		finally
		{
			if (br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
					logger.warn("Buffered reader failed to close. Possible memory leak.", e);
				}
			}

            if (isr != null)
            {
                try {
                    isr.close();
                } catch (IOException e) {
                	logger.warn("Input stream failed to close. Possible memory leak.", e);
                }
            }
		}
	}
	
	public static String escapeHTML(final String html)
	{
		if (html == null)
			return html;
		
		StringBuffer sb = new StringBuffer(html.length()*2);
		for (int i = 0; i < html.length(); i++) {
			char ch = html.charAt(i);
			switch(ch){
				case '\'':
					sb.append("&#39;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				default:
					sb.append(ch);
			}
		}
		
		return sb.toString();
	}

    public static boolean isEmpty(final String str) {
        if((str == null) || str.length() == 0) {
            return true;
        }

        return false;
    }

    public static IRestCall getRestCaller() {
        return XbrmsUtils.restCaller;
    }

    public static IRestCall getRestCaller(final String serverUri) {
        if(XbrmsUtils.isEmpty(serverUri)) {
            return XbrmsUtils.getRestCaller();
        }

        try {
            final IRestCall caller = RestCallFactory.createRestCall(false);
            caller.setServerUri(serverUri);
            return caller;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static IRestCall getLocalCaller() {
        return XbrmsUtils.localCaller;
    }

    public static String getFullRequestUrl(final HttpServletRequest request) {
        String url = request.getRequestURI();

        if (request.getQueryString() != null) {
           url += '?' + request.getQueryString();
        }

        try {
            final URL reconstructedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), url);
            return reconstructedURL.toString();
        }
        catch (Exception e) {
            return "";
        }
    }

    public static String getPartialRequestUrl(final HttpServletRequest request) {
        String url = request.getRequestURI();

        try {
            final URL reconstructedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), url);
            return reconstructedURL.toString();
        }
        catch (Exception e) {
            return "";
        }
    }

    public static String normalizeXbrmsServiceUrl(String s) {
        if(!s.contains(PkConstants.XBRMS_SERVER_APP_NAME)) {
            if(s.endsWith("/")) {
                s += (PkConstants.XBRMS_SERVER_APP_NAME + "/rest");
            }
            else {
                s += ("/" + PkConstants.XBRMS_SERVER_APP_NAME + "/rest");
            }
        }
        else {
            if(!s.contains("rest")) {
                if(s.endsWith("/")) {
                    s += "rest";
                }
                else {
                    s += "/rest";
                }
            }
        }

        if(!s.contains("://")) {
            s = "http://" + s;
        }
        else {
            final String sl = s.toLowerCase();

            if(!sl.startsWith("http://") && !sl.startsWith("https://")) {
                throw new RuntimeException("Invalid protocol. Must be http or https.");
            }
        }

        return s;
    }

    /**
     * Resolve host IP address.
     *
     * @param address Host name or IP address
     * @return Host name in the first element and IP address in the second element of the array.
     */
    public static String[] resolveIp(final String address, final Pattern ipPattern, final Logger logger) {
        if((address == null) || (address.trim().length() == 0)) {
            logger.warn("Failed to resolve an empty host address");
            return null;
        }

        final String[] nameIp = new String[] {null, null};
        final Matcher matcher = ipPattern.matcher(address);

        if (!matcher.matches()) 
        { // Without this check we can block here for a number of seconds if the reverse resolution does not work.             localhost) {
            // Assuming address is a host name
            try {
                // Resolving the host name
                java.net.InetAddress inetAddr = java.net.InetAddress.getByName(address);
                nameIp[0] = inetAddr.getHostName();
                nameIp[1] = inetAddr.getHostAddress();

                return nameIp;
            }
            catch (java.net.UnknownHostException itIsOk) {
            	logger.info("Failed to resolve a hostname " + address);
            }
        }

        nameIp[0] = address;
        nameIp[1] = address;

        return nameIp;
    }

    public static boolean isIpAddress(final String addr) {
        final Matcher matcher = XbrmsUtils.ipPattern.matcher(addr);

        return matcher.matches();
    }

    public static String getHostnamePart(final String fqdn) {
        if(XbrmsUtils.isEmpty(fqdn)) {
            return fqdn;
        }

        if(XbrmsUtils.isIpAddress(fqdn)) {
            return fqdn;
        }
        else {
            final int index = fqdn.indexOf(".");

            if(index <= 0) {
                return fqdn;
            }
            else {
                return fqdn.substring(0, index);
            }
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        final Map<K, V> result = new LinkedHashMap<K, V>();

        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
