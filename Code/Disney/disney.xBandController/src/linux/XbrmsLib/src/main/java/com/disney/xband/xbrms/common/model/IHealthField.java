package com.disney.xband.xbrms.common.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IHealthField
{
	/* Field id to be used in HTML pages (no space, dots) */
	String id();
	/* The descriptive name for this field */
	String name();
	/* Detailed description for this field */
	String description();
	/* The field type */
	HealthFieldType type() default HealthFieldType.String;
	/* Is it mandatory that this field is included on the health monitoring page */
	boolean mandatory() default false;
}
