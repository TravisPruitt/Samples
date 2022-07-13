package com.disney.xband.common.lib;

import java.util.Arrays;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"name", "value", "type", "min", "max", "choices", "description", "defaultValue", "updatable", "clazz", "configClass"})
public class ConfigInfo {
	// the name of the actual java object or class variable
	private String name;
	// the value of the variable as java.lang.String
	private String value;
	// variable's java type
	private String type;
	// a few words describing the purpose this variable serves; to be displayed as help in the UI
	private String description;
	/* 
	 * String representation of the minimum allowable value.
	 * - for numbers, it represents the minimum valid numeric value
	 * - for strings, it represents the minimum allowable number of characters
	 */
	private String min;
	/* 
	 * String representation of the maximum allowable value.
	 * - for numbers, it represents the maximum valid numeric value
	 * - for strings, it represents the maximum allowable number of characters
	 */
	private String max;
	// possible valid value choices
	private String[] choices;
	// default value, UI uses it for the 'restore default' option
	private String defaultValue;
	// should this property be exposed to the end user for updates
	private String updatable;
	// class defining this field
	private String clazz;
	// Config table class column name
	private String configClass;
	
	public ConfigInfo(){}
	
	public ConfigInfo(String name, String value, String type, String description, 
						String min, String max, String[] choices, String defaultValue,
						String clazz, String configClass){
		this.name = name;
		this.value = value;
		this.type = type;
		this.description = description;
		this.min = min;
		this.max = max;
		this.choices = choices;	
		this.defaultValue = defaultValue;
		this.setClazz(clazz);
		this.configClass = configClass;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the min
	 */
	public String getMin() {
		return min;
	}
	/**
	 * @return the max
	 */
	public String getMax() {
		return max;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param min the min to set
	 */
	public void setMin(String min) {
		this.min = min;
	}
	/**
	 * @param max the max to set
	 */
	public void setMax(String max) {
		this.max = max;
	}
	
	public String[] getChoices() {
		return choices;
	}

	public void setChoices(String[] choices) {
		this.choices = choices;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getUpdatable() {
		return updatable;
	}

	public void setUpdatable(String updatable) {
		this.updatable = updatable;
	}

	public String getClazz()
	{
		return clazz;
	}

	public void setClazz(String clazz)
	{
		this.clazz = clazz;
	}
	
	public String getConfigClass()
	{
		return configClass;
	}

	public void setConfigClass(String configClass)
	{
		this.configClass = configClass;
	}
	
	@XmlTransient
	public String getUniqueId()
	{
		StringBuffer uniqueId = new StringBuffer();		
		uniqueId.append(configClass.replace('.', '_')).append('-').append(name);	
		return uniqueId.toString();
	}
	
	@XmlTransient
	public String getSection()
	{
		return configClass != null ? configClass : clazz;
	}

	public String toString(){
		
		StringBuffer state = new StringBuffer("[");
		state.append("name:").append(name);
		state.append(",value:").append(value);
		state.append(",type:").append(type);
		state.append(",description:").append(description);
		state.append(",min:").append(min);
		state.append(",max:").append(max);
		state.append(",defaultValue:").append(defaultValue);
		state.append(",updatable:").append(updatable);
		state.append(",clazz:").append(clazz);
		state.append(",configClass:").append(configClass);
		
		if (choices != null){
			state.append(",choices:[");
			for (int i = 0; i < choices.length; i++){
				if (i == 0)
					state.append(choices[i]);
				else
					state.append(",").append(choices[i]);
			}
			state.append("]");
		}
	
		state.append("]");
		
		return state.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(choices);
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((updatable == null) ? 0 : updatable.hashCode());
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((configClass == null ) ? 0 : configClass.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigInfo other = (ConfigInfo) obj;
		if (!Arrays.equals(choices, other.choices))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (max == null) {
			if (other.max != null)
				return false;
		} else if (!max.equals(other.max))
			return false;
		if (min == null) {
			if (other.min != null)
				return false;
		} else if (!min.equals(other.min))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (updatable == null) {
			if (other.updatable != null)
				return false;
		} else if (!updatable.equals(other.updatable))
			return false;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (configClass == null) {
			if (other.configClass != null)
				return false;
		} else if (!configClass.equals(other.configClass))
			return false;
		
		return true;
	}
	
	/**
	 * Use to sort a collection of <code>ConfigInfo</code> objects by name
	 * in lexicographical order.
	 * 
	 * @author Iwona Glabek
	 */
	public class ClazzAndNameComparator implements Comparator<ConfigInfo>
	{
		public int compare(ConfigInfo c1, ConfigInfo c2)
		{
			String clazz1 = c1.getClazz();
			String clazz2 = c2.getClazz();
			
			int result = 0;
			
			if (clazz1 == null)
				result = -1;
			
			result = clazz1.compareTo(clazz2);
			
			if (result != 0)
				return result;
			
			String name1 = c1.getName();
			String name2 = c2.getName();
			
			if (name1 == null)
				return -1;
			
			return name1.compareTo(name2);
		}
	}
}
