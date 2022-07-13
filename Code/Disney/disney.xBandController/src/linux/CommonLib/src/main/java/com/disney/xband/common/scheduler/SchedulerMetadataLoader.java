package com.disney.xband.common.scheduler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.security.InputValidator;

public class SchedulerMetadataLoader {
	
	public static SchedulerMetadataList loadFromXmlFile(String path) throws Exception
	{
		 FileInputStream is = null;

         try {
            is = new FileInputStream(InputValidator.validateFilePath(path));
            SchedulerMetadataList metadata = XmlUtil.convertToPojo(is, SchedulerMetadataList.class);
            return metadata;
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
	
	public static void saveToXmlFile(SchedulerMetadataList metadata, String path) throws JAXBException, IOException
	{
		FileOutputStream os = null;

        try {
           os = new FileOutputStream(InputValidator.validateFilePath(path));
           String xml = XmlUtil.convertToXml(metadata, SchedulerMetadataList.class);
           os.write(xml.getBytes());
        }
        finally {
            if(os != null) {
                try {
                    os.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
	
	public static SchedulerMetadataList loadFromXmlResource(String resourcepath) throws JAXBException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = null;

        try {
           is = loader.getResourceAsStream(resourcepath);
           SchedulerMetadataList metadata = XmlUtil.convertToPojo(is, SchedulerMetadataList.class);
           return metadata;
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
}
