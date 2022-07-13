package com.disney.xband.xbrms.junit.bvt;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TestRestful
{
	private WebResource service;

	@Before
	public void setUp() throws Exception
	{
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		service = client.resource(getBaseURI());
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
//		System.out.println(rest.path("rest").path("facilities").accept(
//				MediaType.TEXT_PLAIN).get(ClientResponse.class).toString());
		
//		System.out.println(rest.path("rest").path("facilities").accept(
//				MediaType.TEXT_XML).get(String.class).toString());
		
		System.out.println(service.path("rest").path("facilities").accept(
				MediaType.APPLICATION_XML).get(String.class).toString());
		
		System.out.println(service.path("rest").path("facilities").accept(
				MediaType.APPLICATION_JSON).get(String.class).toString());
		
	}
	
	private URI getBaseURI()
	{
		return UriBuilder.fromUri("http://localhost:18080/XBRMS").build();
	}

}
