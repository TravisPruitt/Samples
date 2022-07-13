package com.disney.xband.xbrms.common.model;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HealthSchema
{
	private Map<String,List<HealthField>> schema;
	
	public static HealthSchema getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	/*
	 * Find all fields annotated 
	 */
	private void processHealthItemClass(Class<? extends HealthItemDto> c) {
		List<HealthField> fields = new LinkedList<HealthField>();
		
		for (Method m : c.getDeclaredMethods()) 
		{
			//don't process compiler generated fields
			if (m.isSynthetic())
				continue;
			
			if (m.isAnnotationPresent(IHealthField.class)) 
			{
				HealthField fi = new HealthField();
				fi.setDesc(m.getAnnotation(IHealthField.class));
				fi.setMethod(m);
				fields.add(fi);
			}
		}
		
		schema.put(c.getName(), fields);
	}
	
	public List<HealthField> getFields(String className) {
		return schema.get(className);
	}
	
	private HealthSchema() {
		schema = new LinkedHashMap<String, List<HealthField>>();
		processHealthItemClass(XbrcDto.class);
		processHealthItemClass(IdmsDto.class);
		processHealthItemClass(JmsListenerDto.class);
	}
	
	private static class SingletonHolder
	{
		private final static HealthSchema INSTANCE = new HealthSchema();
	}
}
