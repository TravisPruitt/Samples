package com.disney.xband.common.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * Uses JAXB to serialize/deserialize between xml and pojo.
 * Each package containing JAXB annotated java classes must contain jaxb.index file
 * containing names off the JAXB annotated classes.
 * Each JAXB annotated class must define a public no argument constructor.
 * 
 * For a usage sample @see com.disney.xband.xbrc.lib.junit.bvt.xmlutil.TestXmlUtil
 */
public class XmlUtil {
	
	private static Logger logger = Logger.getLogger(XmlUtil.class);
	
	/*
	 * Performance note:
	 * 1) Creating JAXBContext and Marshaller/Unmarshaller is very expensive (10-20 times slower than just calling marshal/unmarshal).
	 * 3) JAXBContext once created for a specific class is tread safe.
	 * 2) Marshaller/Unmarshaller is not thread safe.
	 * 
	 * Solution:
	 * Maintain a global cache of JAXBContext indexed on the class type.
	 * Maintain a per thread cache of Marshaller/Unmarshaller.
	 */

	private static ConcurrentHashMap<String,JAXBContext> contextMap = new ConcurrentHashMap<String,JAXBContext>();
	
	private static ThreadLocal<HashMap<String,Marshaller>> marshallerMap = new ThreadLocal<HashMap<String,Marshaller>>() {
        protected synchronized HashMap<String,Marshaller> initialValue() {
        	logger.info("Executing initialValue() on marshallerMap for thread " + Thread.currentThread().getId());
            return new HashMap<String,Marshaller>();
        }
    };
    
    private static ThreadLocal<HashMap<String,Unmarshaller>> unmarshallerMap = new ThreadLocal<HashMap<String,Unmarshaller>>() {
        protected synchronized HashMap<String,Unmarshaller> initialValue() {
        	logger.info("Executing initialValue() on unmarshallerMap for thread " + Thread.currentThread().getId());
            return new HashMap<String,Unmarshaller>();
        }
    };
    
    public static HashMap<String,Unmarshaller> getUnmarshallerMap(){
    	if (unmarshallerMap == null)
    		return null;
    	
    	return unmarshallerMap.get();
    }
    
    public static int getUnmarshallerMapSize(){
    	if (unmarshallerMap == null)
    		return 0;
    	
    	return unmarshallerMap.get().size();
    }
    
    private static <T> JAXBContext getContext(Class<T> type) throws JAXBException {
    	JAXBContext ctx = contextMap.get(type.getName());
		if (ctx == null) {
			ctx = JAXBContext.newInstance(type);
			contextMap.put(type.getName(), ctx);
		}
		return ctx;
    }

    private static <T,B> JAXBContext getContext(Class<B> typeBase, Class<T> type) throws JAXBException {
    	String sKey = typeBase.getName() + ":" + type.getName();
    	JAXBContext ctx = contextMap.get(sKey);
		if (ctx == null) {
			ctx = JAXBContext.newInstance(typeBase, type);
			contextMap.put(sKey, ctx);
		}
		return ctx;
    }
	

    private static <T> Marshaller getMarshaller(Class<T> type) throws JAXBException {
    	HashMap<String,Marshaller> mmap = marshallerMap.get();
    	
    	Marshaller m = mmap.get(type.getName());
    	if (m == null) {
    		JAXBContext ctx = getContext(type);
    		m = ctx.createMarshaller();
    		mmap.put(type.getName(), m);
    	}
    	return m;
    }
    
    private static <T> Unmarshaller getUnmarshaller(Class<T> type) throws JAXBException {
    	HashMap<String,Unmarshaller> mmap = unmarshallerMap.get();
    	
    	Unmarshaller m = mmap.get(type.getName());
    	if (m == null) {
    		JAXBContext ctx = getContext(type);
    		m = ctx.createUnmarshaller();
    		mmap.put(type.getName(), m);
    	}
    	return m;
    }

    private static <T,B> Unmarshaller getUnmarshaller(Class<B> typeBase, Class<T> type) throws JAXBException {
    	HashMap<String,Unmarshaller> mmap = unmarshallerMap.get();
    	
    	String sKey = typeBase.getName() + ":" + type.getName();
    	Unmarshaller m = mmap.get(sKey);
    	if (m == null) {
    		JAXBContext ctx = getContext(typeBase, type);
    		m = ctx.createUnmarshaller();
    		mmap.put(sKey, m);
    	}
    	return m;
    }
    

	/**
	 * Serializes java classes annotated with JAXB annotations into xml
	 * @throws JAXBException 
	 */
	public static <T> String convertToXml(Object source, Class<T> type) throws JAXBException{
		String result = null;
		StringWriter sw = new StringWriter();
		Marshaller marshaller = getMarshaller(type);
		marshaller.marshal(source, sw);
		result = sw.toString();		
		return result;
	}
	
	public static <T> String convertToPartialXml(Object source, Class<T> type) throws JAXBException{
		String result = null;
		StringWriter sw = new StringWriter();
		Marshaller marshaller = getMarshaller(type);
		marshaller.marshal(source, sw);
		result = sw.toString();
		int idx = result.indexOf('>');
		if (idx > 0)
			return result.substring(idx+1);
		return result;
	}
	
	/**
	 * Does not close the InputStream after processing.
	 * The clazz type must be included in jaxb.index file; one class name per line.
	 * 
	 * Usage example:
	 * 		to deserialize an xml representing foo to com.boo.Foo.java, include a string Foo in 
	 * 		jaxb.index file located in com.boo package
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertToPojo(InputStream xml, Class<T> clazz) throws JAXBException {
		
		Unmarshaller u = getUnmarshaller(clazz);
		return (T)u.unmarshal(xml);
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertToPojo(InputStream xml, @SuppressWarnings("rawtypes") Class clazzBase, Class<T> clazz) throws JAXBException {
		
		Unmarshaller u = getUnmarshaller(clazzBase, clazz);
		return (T)u.unmarshal(xml);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T convertToPojo(Node node, Class<T> clazz) throws JAXBException {
		
		//deserilize to a POJO
		Unmarshaller u = getUnmarshaller(clazz);
		return (T)u.unmarshal(node);
	}
	
	public static String convertStreamToString(InputStream is){
		if (is == null)
			return null;
		
		int numBytes = 0;
		try {
			numBytes = is.available();
			if (numBytes == 0)
				return null;
		} catch (IOException e) {
			return null;
		}
		
		Writer writer = new StringWriter();
		char[] buffer = new char[numBytes];
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			int n = 0;
			while ((n = reader.read(buffer)) != -1){
				writer.write(buffer, 0, n);
			}
			
		} catch (Exception e){
			logger.debug("Failed to convert input stream to string", e);
		}
		
		return writer.toString();
	}
}
