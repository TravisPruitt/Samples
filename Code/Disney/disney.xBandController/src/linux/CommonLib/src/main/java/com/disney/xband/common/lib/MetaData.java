package com.disney.xband.common.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MetaData {
	/** What is this propertie's database name **/
	String name();
	/** What is this property for, what does it do **/
	String description() default "";
	/** Minimum allowed value for numeric values, or a minimum number of characters for string values **/
	String min() default "N/A";
	/** Maximum allowed value for numeric values, or a maximum number of characters for string values **/
	String max() default "N/A";
	/** Possible values choices **/
	String[] choices() default "N/A";
	/** What to restore the value of this property to when the user chooses to restore defaults **/
	String defaultValue() default "";
	/** Is null allowed **/
	boolean allowNull() default false;
	/**
	 * Should the UI expose this property for updates? Only properties which values can be changed on the fly 
	 * should be marked as updatable, i.e the xbrc does not have to be restarted for the change to take effect.
	 */
	boolean updatable() default true;
}
