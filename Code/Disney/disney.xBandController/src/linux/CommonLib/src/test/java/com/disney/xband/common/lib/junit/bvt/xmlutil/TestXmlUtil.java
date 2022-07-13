package com.disney.xband.common.lib.junit.bvt.xmlutil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEventList;

public class TestXmlUtil {

	String xml;
	String color = "blue";
	String model = "toyota";
	int year = 1995;
	String manuf1 = "michelin";
	String manuf2 = "bridgestone";
	String xmlFileName;
	Date registration;
	String attribute;
	
	@Before
	public void setUp() throws Exception {
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><car attribute=\"attribute value\">" +
				"<model>toyota</model><year>1995</year><color>blue</color>" +
				"<registration>2011-09-20T13:48:15.147-07:00</registration>" +
				"<someSimpleType>0</someSimpleType><tires><tire>" +
				"<manufacture>michelin</manufacture></tire>" +
				"<tire><manufacture>michelin</manufacture>" +
				"</tire><tire><manufacture>bridgestone</manufacture></tire>" +
				"<tire><manufacture>bridgestone</manufacture></tire></tires></car>";
		
		xmlFileName = this.getClass().getResource("xmlPojo.xml").getPath();
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(xmlFileName));
			out.write(xml);
		} catch (IOException e1) {
			fail(e1.getLocalizedMessage());
		} finally {
			if (out != null) 
				try {
					out.close();
				} catch (Exception e){
					fail(e.getLocalizedMessage());
				}
		}
		
		color = "blue";
		model = "toyota";
		year = 1995;
		manuf1 = "michelin";
		manuf2 = "bridgestone";
		//Tue Sep 20 13:48:15 PDT 2011
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis(1316551695147L);
		registration = cal.getTime();
		attribute = "attribute value";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertToXml() throws JAXBException {

		try {
			//construct the pojo
			TirePojo tire1 = new TirePojo();
			tire1.setManufacture(manuf1);
			TirePojo tire2 = new TirePojo();
			tire2.setManufacture(manuf1);
			TirePojo tire3 = new TirePojo();
			tire3.setManufacture(manuf2);
			TirePojo tire4 = new TirePojo();
			tire4.setManufacture(manuf2);

			CarPojo car = new CarPojo();
			car.setColor(color);
			car.setModel(model);
			car.setYear(year);
			car.setRegistration(registration);
			car.getTires().add(tire1);
			car.getTires().add(tire2);
			car.getTires().add(tire3);
			car.getTires().add(tire4);
			car.setAttribute(attribute);
			car.setTransientField(new Date());

			//generate xml
			String pojoAsXml = XmlUtil.convertToXml(car, car.getClass());

			assertEquals(xml, pojoAsXml);
		} catch (Exception e){
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testConvertToPojo() {
		
		InputStream in = null;
		try {
			File file = new File(xmlFileName);
			in = new FileInputStream(file);
			CarPojo carPojo = XmlUtil.convertToPojo(in, CarPojo.class);
			assertTrue(carPojo != null);
			assertEquals(carPojo.getColor(), color);
			assertEquals(carPojo.getModel(), model);
			assertTrue(carPojo.getTires() != null);
			assertEquals(carPojo.getTires().size(), 4);
			assertEquals(carPojo.getRegistration(), registration);
			assertTrue(carPojo.getSomeObject() == null);
			assertEquals(carPojo.getSomeSimpleType(), 0);
		} catch (JAXBException e) {
			fail(e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			fail(e.getLocalizedMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {}
		}
	}
	
	@Test
	public void testConvertStreamToString() {
		InputStream in = null;
		try {
			//happy case
			File file = new File(xmlFileName);
			in = new FileInputStream(file);
			String result = XmlUtil.convertStreamToString(in);
			assertEquals(result, xml);
			
			//empty stream
			result = XmlUtil.convertStreamToString(in);
			assertTrue(result == null);
			in.close();
			
		} catch (FileNotFoundException e) {
			fail(e.getLocalizedMessage());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {}
		}
	}
	
	@Test
	public void testJaxbInheritance(){
		
		InputStream in = null;
		
		try {
			CatPojo cat = new CatPojo();
			cat.setName("Flufy");
			cat.setAge(2);
			cat.setWhiskers(true);
			cat.setTransientField(new Date());

			//generate xml
			String pojoAsXml = XmlUtil.convertToXml(cat, cat.getClass());

			in = new ByteArrayInputStream(pojoAsXml.getBytes());

			// xml to pojo
			CatPojo result = XmlUtil.convertToPojo(in, CatPojo.class);
			
			assertEquals(result.getName(), "Flufy");
			assertEquals(result.getAge(), 2);
			assertEquals(result.isWhiskers(), true);
			assertEquals(result.getTransientField(), null);
			
		} catch (Exception e){
			fail(e.toString());
		}
		finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {}
		}
	}
	
	@Test
	public void testJaxbInheritance1(){
		
		InputStream in = null;
		
		try {
			CagePojo cage = new CagePojo();
			
			CatPojo cat = new CatPojo();
			cat.setName("Flufy");
			cat.setAge(2);
			cat.setWhiskers(true);
			cat.setTransientField(new Date());
			
			CatPojo cat1 = new CatPojo();
			cat.setName("Bob");
			cat.setAge(5);
			cat.setWhiskers(true);
			cat.setTransientField(new Date());
			
			Collection<AnimalPojo> animals = new LinkedList<AnimalPojo>();
			animals.add(cat);
			animals.add(cat1);
			cage.setAnimals(animals);

			//generate xml
			String pojoAsXml = XmlUtil.convertToXml(cage, cage.getClass());
			System.out.println(pojoAsXml);
			
		} catch (Exception e){
			fail(e.toString());
		}
		finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {}
		}
	}
	
	@Test
	public void testJaxbXmlAdapterFunctionality(){
		
		InputStream in = null;
		
		double[][] temperatures = new double[][]{
				{68.2348768767777, 68.2349}, 
				{0, 0.0}, 
				{68.2348268767777, 68.2348}, 
				{-68.2348768767777, -68.2349}, 
				{-68.2348268767777, -68.2348}, 
				{45, 45.0}, 
				{3.4567, 3.4567}
		};
		
		try {
			CatPojo cat = new CatPojo();
			
			for (double[] temp : temperatures){
				cat.setBodyTemperature(temp[0]);

				//generate xml
				String pojoAsXml = XmlUtil.convertToXml(cat, cat.getClass());

				in = new ByteArrayInputStream(pojoAsXml.getBytes());

				// xml to pojo
				CatPojo result = XmlUtil.convertToPojo(in, CatPojo.class);
				
				assertTrue(result.getBodyTemperature() == temp[1]);
			}
			
		} catch (Exception e){
			fail(e.getLocalizedMessage());
		} finally {
			if (in != null){
				try {
					in.close();
				} catch (Exception e1){}
			}
		}
	}
	
	/**
	 * The main thread in this method sleaps a lot and will slow down BVTs. Please only run manually.
	 */
	//@Test
	public void testXmlUtilMemoryUsage() throws InterruptedException
	{
		// create the list of events
		List<AuditEvent> eventList = new ArrayList<AuditEvent>();
		for (int i = 0; i < 1000; i++)
		{
			AuditEvent ae = new AuditEvent();
			ae.setAid(10L);
			ae.setAppClass("XbrcDto.class");
			ae.setAppId("Xbrc.id");
			ae.setCategory("category");
			ae.setClient("xbrc.client");
			ae.setCollectorHost("xbrms.collectorHost");
			ae.setDateTime("2013-10-15T13:45:00+00:00");
			ae.setDesc("Some sort of a very very long description string");
			ae.setId(i);
			
			eventList.add(ae);
		}
		AuditEventList eventsList = new AuditEventList();
		eventsList.setEvents(eventList);
		
		// serialize to text
		String xml = null;
		try
		{
			xml = XmlUtil.convertToXml(eventsList, AuditEventList.class);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
			fail();
		}
		
		// create threads
		Thread[] threads = new Thread[100];
		for (int i = 0; i < 100; i++)
		{
			threads[i] = new Thread(new UnmarshallAuditEventList(new String(xml)));
		}
		
		// unmarshall the events list
		for (int i = 0; i < threads.length; i++)
			threads[i].start();
		
		//
		HashMap<String,Unmarshaller> unmarshallerMap = XmlUtil.getUnmarshallerMap();
		if (unmarshallerMap!= null)
		{
			System.out.println("Map size is " + unmarshallerMap.size());
		}
		
		Thread.sleep(10000);
	}
	
	class UnmarshallAuditEventList implements Runnable
	{
		AuditEventList element;
		String xml;
		
		public UnmarshallAuditEventList(String xml){
			this.xml = xml;
		}
		
		public void setXml(String xml){
			this.xml = xml;
		}
		
		public AuditEventList getElement(){
			return element;
		}

		@Override
		public void run()
		{
			while(!Thread.interrupted())
			{
				if (xml == null || xml.length() == 0)
					return;
				
				InputStream is = new ByteArrayInputStream(xml.getBytes());
				
				try
				{
					element = XmlUtil.convertToPojo(is, AuditEventList.class);
					
					System.out.println(
							"" + new Date() + " -- Element converted in thread " + Thread.currentThread().getId() + 
							" map size is " + XmlUtil.getUnmarshallerMapSize() + 
							" map id is " + XmlUtil.getUnmarshallerMap().toString());
					
				}
				catch (JAXBException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
