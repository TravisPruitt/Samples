package com.disney.xband.xbrc.Controller.junit.bvt;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.xbrc.Controller.PropertiesManager;

public class TestPropertiesManager extends PropertiesManager
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testHandleGetProperties()
	{
	}

	@Test
	public void testHandlePutProperties()
	{
	}

	@Test
	public void testParseRequest()
	{
		Properties properties = parseRequestUrl("/configuration");
		assertTrue(properties == null);
		
		properties = parseRequestUrl("/properties");
		assertTrue(properties == Properties.ALL);
		
		properties = parseRequestUrl("/properties/");
		assertTrue(properties == Properties.ALL);
		
		properties = parseRequestUrl("/properties/ESBInfo");
		assertTrue(properties == Properties.BY_CLASS);
		
		properties = parseRequestUrl("/properties/com.org.bla.bla.ESBInfo");
		assertTrue(properties == Properties.BY_CLASS);
		
		properties = parseRequestUrl("/properties/com.org.bla.bla.ESBInfo/");
		assertTrue(properties == Properties.BY_CLASS);
		
		properties = parseRequestUrl("/properties/com.org.bla.bla.ESBInfo/jmsBroker");
		assertTrue(properties == Properties.SINGLE);
		
		properties = parseRequestUrl("/properties/com.org.bla.bla.ESBInfo/jmsBroker/");
		assertTrue(properties == Properties.SINGLE);
	}

}
