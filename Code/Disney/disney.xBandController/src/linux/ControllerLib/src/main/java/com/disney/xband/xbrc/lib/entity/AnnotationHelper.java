package com.disney.xband.xbrc.lib.entity;

import java.lang.reflect.Field;

public class AnnotationHelper {
	
	public AnnotationHelper getInstance(){
		return AnnotationHelperHolder.instance;
	}
	
	/**
	 * Returns @Column#length() if @Column specified on field fieldName, -1 otherwise.
	 * @param obj
	 * @param fieldName
	 * @return @Column#length() if @Column specified, -1 otherwise
	 */
	public static int getColumnLength(Object obj, String fieldName) throws IllegalArgumentException, NoSuchFieldException {
		if (obj == null)
			throw new IllegalArgumentException("obj may not be null");
		if (fieldName == null || fieldName.trim().isEmpty())
			throw new IllegalArgumentException("fieldName may not be null or empty");
		
		Field field = obj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		Column annotation = field.getAnnotation(Column.class);
		
		if (annotation == null)
			return -1; //annotation not specified for field fieldName
		
		return annotation.length();
	}
	
	private static class AnnotationHelperHolder {
		private static AnnotationHelper instance = new AnnotationHelper();
	}
	
	private AnnotationHelper(){}
}
