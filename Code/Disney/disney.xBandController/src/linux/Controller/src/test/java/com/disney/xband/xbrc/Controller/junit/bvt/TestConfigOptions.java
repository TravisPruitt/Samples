package com.disney.xband.xbrc.Controller.junit.bvt;

import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfigOptions
{

	Properties config;

	@Before
	public void setUp() throws Exception
	{
		// read configuration
		config = new Properties();
        InputStream is = getClass().getResourceAsStream("test-bvt-config.properties");
        try {
		    config.load(is);
        }
        finally {
            if(is != null) {
                is.close();
            }
        }
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetReadersInfo()
	{
	}

	@Test
	public void testGetControllerInfo()
	{
	}

	@Test
	public void testGetESBInfo()
	{
	}

	@Test
	public void testGetStatus()
	{
	}

	@Test
	public void testGetCarInfo()
	{
	}

	@Test
	public void testGetLocationInfo()
	{
	}

	@Test
	public void testReadConfigurationOptionsFromDatabase()
	{
	}

	@Test
	public void testRefreshConfigurationTable()
	{
	}

}
