package com.disney.xband.xbrc.scheduler;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.common.scheduler.SchedulerItem;

@XmlRootElement
public class SchedulerSettings {
	
	private List<SchedulerItem> items;

	@XmlElementWrapper(name="schedulerItems")
	@XmlElement(name="schedulerItem")
	public List<SchedulerItem> getItems() {
		return items;
	}

	public void setItems(List<SchedulerItem> items) {
		this.items = items;
	}
}
