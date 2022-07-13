package com.disney.xband.xbrms.client.model;

import com.disney.xband.xbrms.common.model.HealthField;
import com.disney.xband.xbrms.common.model.HealthItem;

import java.util.Collection;
import java.util.List;

public class MonitoredAppDpo implements Comparable<MonitoredAppDpo>, IPresentationObject
{
	private String type;
	private List<HealthField> defs;
	private Collection<HealthItem> values;
	
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public List<HealthField> getDefs()
	{
		return defs;
	}
	public void setDefs(List<HealthField> defs)
	{
		this.defs = defs;
	}
	public Collection<HealthItem> getValues()
	{
		return values;
	}
	public void setValues(Collection<HealthItem> values)
	{
		this.values = values;
	}

    public int compareTo(MonitoredAppDpo app){
        return this.getType().compareTo(app.getType());
    }
}
