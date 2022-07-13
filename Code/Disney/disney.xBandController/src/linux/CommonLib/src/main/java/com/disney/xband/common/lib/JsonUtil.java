package com.disney.xband.common.lib;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;

public class JsonUtil
{
	static SimpleDateFormat utcDateFormatter;
	
	static
	{
		utcDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		utcDateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public static String convertToJson(Object source) throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setDateFormat(utcDateFormatter); 
		return mapper.writeValueAsString(source);
	}
	
	public static <T> T convertToPojo(InputStream is, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setDateFormat(utcDateFormatter); 
		return mapper.readValue(is, clazz);
	}
	
	public static <T> T convertToPojo(String str, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setDateFormat(utcDateFormatter); 
		return mapper.readValue(str, clazz);
	}
}
