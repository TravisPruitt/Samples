package com.disney.xband.xview;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class UIProperties{
	/**
	 * 
	 */

	private Properties pi;

	public UIProperties()
	{
		pi = new Properties();
        InputStream is = null;
		try {
			pi.load(is);
			CheckRemoteSetting();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

		private static String fileName = "xview.properties";
		
		
		private void CheckRemoteSetting()
		{
            InputStream is = null;

			try
			{
			    String st = getPropertyValue("remoteSettings");

			    if (st != null && st.length() > 0)
			    {
                    is = new FileInputStream(st);

				    // We have a remove settings file, so use it now.
				    pi.load(is);
			    }
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally {
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch (Exception ignore) {
                    }
                }
            }
		}
		
		public String getPropertyValue(String keyName)
		{
			String retVal = "";
			
			retVal = pi.getProperty(keyName);
			
			return retVal;
			
		}
		
//		public static String getPropertyValue(String keyName)
//		{
//			String retVal = "";
//			
//			Properties pi = new Properties();
//			
//			String dir = UIProperties.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation().toString();
//			//URL pt = UIProperties.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation();
//
//			System.out.println("RunningDir = " + dir);
//			
//			try {
//				pi.load(new FileInputStream(fileName));
//				retVal = pi.getProperty(keyName);
//				
//			} catch (FileNotFoundException e) {
//				retVal = "";
//				e.printStackTrace();
//			} catch (IOException e) {
//				retVal = "";
//				e.printStackTrace();
//			}
//			return retVal;
//		}
		
		
		
	
	
}
