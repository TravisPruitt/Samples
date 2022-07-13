package com.disney.socket.ui.switchboard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class SwitchboardProperties {

	
	private static String fileName = "Switchboard.properties";
	
	public static String getPropertyValue(String keyName)
	{
		String retVal = "";
		
		Properties pi = new Properties();
        FileInputStream fis = null;

		try {
            fis = new FileInputStream(fileName);
			pi.load(fis);
			retVal = pi.getProperty(keyName);
			
		} catch (FileNotFoundException e) {
			retVal = "";
			e.printStackTrace();
		} catch (IOException e) {
			retVal = "";
			e.printStackTrace();
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

		return retVal;
	}
}
