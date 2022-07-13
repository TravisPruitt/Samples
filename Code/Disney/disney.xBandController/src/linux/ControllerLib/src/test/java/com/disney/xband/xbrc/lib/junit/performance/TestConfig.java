package com.disney.xband.xbrc.lib.junit.performance;

import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

	Properties config;
	
	@Before
	public void setUp() throws Exception {
		//read configuration
		config = new Properties();

        InputStream is = null;

        try {
            is = getClass().getResourceAsStream("test-performance-config.properties");
		    config.load(is);
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

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstance() {
	}

	@Test
	public void testRead() {
	}

	@Test
	public void testWrite() {
	}

}
