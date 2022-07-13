package com.disney.xband.common.scheduler;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SchedulerItemParameter {
	private String itemKey;
	private String name;
	private String value;
	private Integer sequence;
	private transient SchedulerItemParameterMetadata metadata;
	
	public String getItemKey() {
		return itemKey;
	}
	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	@XmlTransient
	public boolean isEnvPropValue() {
		return metadata == null ? false : metadata.getType() == SchedulerItemParameterType.ENVPROPVALUE; 
	}
	
	@XmlTransient
	@JsonIgnore
	public SchedulerItemParameterMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(SchedulerItemParameterMetadata metadata) {
		this.metadata = metadata;
	}
	
	@XmlTransient
	@JsonIgnore
	public String getHtmlSafeName() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (Character.isLetter(ch) || Character.isDigit(ch))
				sb.append(ch);
		}
		return sb.toString();
	}
	
	@Override
	protected Object clone() {
		SchedulerItemParameter ret = new SchedulerItemParameter();
		ret.itemKey = this.itemKey;
		ret.name = this.name;
		ret.value = this.value;
		ret.sequence = this.sequence;
		ret.metadata = this.metadata;
		return ret;
	}
	
	private boolean objectsMismatch(Object a, Object b) {
		if (a == null && b != null)
			return true;
		if (b == null && a != null)
			return true;
		if (b == null && a == null)
			return true;
		return a.equals(b);
	}
	
	@Override
	public boolean equals(Object obj) {
		SchedulerItemParameter other = (SchedulerItemParameter)obj;
		
		if (!objectsMismatch(itemKey, other.getItemKey()))
			return false;
		
		if (!objectsMismatch(name, other.getName()))
			return false;
		
		if (!objectsMismatch(value, other.getValue()))
			return false;
		
		if (!objectsMismatch(sequence, other.getSequence()))
			return false;
	
		return true;
	}
}
