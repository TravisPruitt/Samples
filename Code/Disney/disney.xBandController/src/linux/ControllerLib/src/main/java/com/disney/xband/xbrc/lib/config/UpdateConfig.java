package com.disney.xband.xbrc.lib.config;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdateConfig {
		
	List<UpdateConfigItem> updateConfigItems = null;
	
	public UpdateConfig()
	{
	}
	
	public void addUpdateConfigItem(UpdateConfigItem item)
	{
		if (updateConfigItems == null)
			updateConfigItems = new LinkedList<UpdateConfigItem>();
		updateConfigItems.add(item);
	}
	
	@XmlElement
	public List<UpdateConfigItem> getUpdateConfigItems()
	{
		return updateConfigItems;
	}
	
	public void setUpdateConfigItems(List<UpdateConfigItem> updateConfigItems)
	{
		this.updateConfigItems = updateConfigItems;
	}
}
