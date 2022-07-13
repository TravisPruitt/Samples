package com.disney.xband.common.scheduler;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SchedulerMetadataList")
public class SchedulerMetadataList {
	
	private Collection<SchedulerItemMetadata> metadata;

	@XmlElement(name="SchedulerItemMetadata")
	public Collection<SchedulerItemMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(Collection<SchedulerItemMetadata> metadata) {
		this.metadata = metadata;
	}
}
