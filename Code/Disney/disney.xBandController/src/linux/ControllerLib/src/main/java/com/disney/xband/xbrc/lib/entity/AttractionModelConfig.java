package com.disney.xband.xbrc.lib.entity;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AttractionModelConfig
{
	private Collection<VaLocationConfig> vaLocationConfigList;
    private Collection<EventsLocationConfig> eventsLocationConfigList;

	@XmlElement(name="vaLocationConfig")
	public Collection<VaLocationConfig> getVaLocationConfigList() {
		return vaLocationConfigList;
	}

	public void setVaLocationConfigList(
			Collection<VaLocationConfig> vaLocationConfigList) {
		this.vaLocationConfigList = vaLocationConfigList;
	}

    @XmlElement(name="eventsLocationConfig")
    public Collection<EventsLocationConfig> getEventsLocationConfigList() {
        return eventsLocationConfigList;
    }

    public void setEventsLocationConfigList(
            Collection<EventsLocationConfig> eventsLocationConfigList) {
        this.eventsLocationConfigList = eventsLocationConfigList;
    }

}
