package com.disney.xband.common.lib.junit.bvt.xmlutil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.common.lib.JsonUtil;

public class TestJsonUtil {

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
		
		xml = "{\"color\":\"blue\",\"model\":\"toyota\",\"year\":1995,\"registration\":\"2011-09-20T20:48:15.147\",\"tires\":[{\"manufacture\":\"michelin\"},{\"manufacture\":\"michelin\"},{\"manufacture\":\"bridgestone\"},{\"manufacture\":\"bridgestone\"}],\"attribute\":\"attribute value\",\"someSimpleType\":0}";
		
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
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
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
			String pojoAsXml = JsonUtil.convertToJson(car);

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
			CarPojo carPojo = JsonUtil.convertToPojo(in, CarPojo.class);
			assertTrue(carPojo != null);
			assertEquals(carPojo.getColor(), color);
			assertEquals(carPojo.getModel(), model);
			assertTrue(carPojo.getTires() != null);
			assertEquals(carPojo.getTires().size(), 4);
			assertEquals(carPojo.getRegistration(), registration);
			assertTrue(carPojo.getSomeObject() == null);
			assertEquals(carPojo.getSomeSimpleType(), 0);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		finally {
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
			String pojoAsXml = JsonUtil.convertToJson(cat);

			in = new ByteArrayInputStream(pojoAsXml.getBytes());

			// xml to pojo
			CatPojo result = JsonUtil.convertToPojo(in, CatPojo.class);
			
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
			String pojoAsXml = JsonUtil.convertToJson(cage);
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
		
		Double[][] temperatures = new Double[][]{
				{68.2348768767777, 68.2349}, 
				{0.0, 0.0}, 
				{68.2348268767777, 68.2348}, 
				{-68.2348768767777, -68.2349}, 
				{-68.2348268767777, -68.2348}, 
				{45.0, 45.0}, 
				{3.4567, 3.4567}
		};
		
		try {
			CatPojo cat = new CatPojo();
			
			for (Double[] temp : temperatures){
				cat.setBodyTemperature(temp[0]);

				//generate xml
				String pojoAsXml = JsonUtil.convertToJson(cat);

				in = new ByteArrayInputStream(pojoAsXml.getBytes());

				// xml to pojo
				CatPojo result = JsonUtil.convertToPojo(in, CatPojo.class);
				
				assertEquals(result.getBodyTemperature(),temp[0]);
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
}
