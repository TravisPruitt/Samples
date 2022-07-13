package com.disney.xband.xview.junit.performance;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Properties;

import com.sun.corba.se.spi.orbutil.fsm.Input;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGuestController {

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
                is.close();
            }
        }
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetGuestById() {
	}

	@Test
	public void testSearchGuestName() {
	}

	@Test
	public void testUpdateGuest() {
	}

	@Test
	public void testCreateGuest() {
	}

	@Test
	public void testDeleteGuest() {
	}

	@Test
	public void testGetAllGuests() {
	}

	@Test
	public void testGetGuestXBands() {
	}

	@Test
	public void testAddXBandToGuest() {
	}

	@Test
	public void testRemoveXBandFromGuest() {
	}

	@Test
	public void testCreateDemoGuest() {
	}

}
